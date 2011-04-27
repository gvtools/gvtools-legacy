/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *   Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package es.prodevelop.cit.gvsig.fmap.drivers.jdbc.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.sql.ROWID;
import oracle.sql.STRUCT;

import org.apache.log4j.Logger;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;


/**
 * Oracle feature iterator. An instance of this class is returned whaen gvSIG
 * asks for a feature iterator for a new viewport.
 *
 * @author jldominguez
 *
 */
public class OracleSpatialFeatureIterator implements IFeatureIterator {
	
	private static Logger logger = Logger.getLogger(OracleSpatialFeatureIterator.class);	
	
    private OracleSpatialDriver driver;
    private ResultSet rs = null;
    private int oneBasedGeoColInd = 0;
    private boolean useGeotools = false;
    private Statement st;
    private String[] explicitAtts = null;
    private boolean useExplicitAtts = false;

    /**
     * Constructor.
     *
     * @param parent the driver that creates it
     * @param _rs the result set to be iterated (already computed by the driver)
     * @param _st the statement that generated the result set. The iterator will close it.
     * @param geoColIndex index of the geometry field
     * @param _useGeotools a switch to decide if the geotools classes
     * must be used to deal with geometris
     */
    public OracleSpatialFeatureIterator(
    		OracleSpatialDriver parent,
    		ResultSet _rs, Statement _st,
    		int geoColIndex, boolean _useGeotools,
    		boolean explicit_atts, String[] exp_atts) {
        driver = parent;
        rs = _rs;
        st = _st;
        useGeotools = _useGeotools;
        oneBasedGeoColInd = geoColIndex;
        explicitAtts = exp_atts;
        useExplicitAtts = explicit_atts;
    }

    public boolean hasNext() throws ReadDriverException {
        if (rs == null) {
            return false;
        }

        try {
            boolean _resp = rs.next();

            if (!_resp) {
                rs.close();
                st.close();
            }

            return _resp;
        } catch (SQLException se) {
        	throw new ReadDriverException(driver.getName(), se);
        }
    }

    public IFeature next() throws ReadDriverException {
        if (rs == null) {
            return null;
        }

        IFeature ife = null;

        try {
            ROWID ri = (ROWID) rs.getObject(1);
            Value[] atts = driver.getAttributes(rs, false);
            
            if (useExplicitAtts) {
            	atts = reorderAtts(atts, driver, explicitAtts);
            }
            
            String gid = ri.stringValue();
            STRUCT _st = (oracle.sql.STRUCT) rs.getObject(oneBasedGeoColInd);
            IGeometry theGeom = driver.getGeometryUsing(_st, useGeotools);
            ife = new DefaultFeature(theGeom, atts, gid);
        }
        catch (SQLException se) {
        	throw new ReadDriverException(driver.getName(), se);
        }

        // showGeometrySample(ife);
        
        return ife;
    }

    private Value[] reorderAtts(
    		Value[] atts,
    		OracleSpatialDriver drv,
			String[] ordered_names) {
    	
    	String[] fnames = drv.getFieldNames();
    	int len = ordered_names.length;
    	Value[] resp = new Value[len];
    	for (int i=0; i<len; i++) {
    		int index = getFiledIndexByName(fnames, ordered_names[i]);
    		resp[i] = atts[index];
    	}
		return resp;
	}
    
    private int getFiledIndexByName(String[] names, String item) {
    	for (int i=0; i<names.length; i++) {
    		if (names[i].compareToIgnoreCase(item) == 0) {
    			return i;
    		}
    	}
    	return -1;
    }

	private void showGeometrySample(IFeature ife) {
    	
    	IGeometry geom = ife.getGeometry();
    	
    	int size = 80;
    	
			String wkt_str = geom.toJTSGeometry().toText();
			if (wkt_str.length() <= size) {
				logger.debug("Oracle driver returns geometry:\n" + wkt_str);
			} else {
				logger.debug("Oracle driver returns geometry:\n" + wkt_str.substring(0, size));
			}
	}

	public void closeIterator() throws ReadDriverException {
        try {
            rs.close();
            st.close();
        }
        catch (SQLException se) {
            throw new ReadDriverException(driver.getName(), se);
        }
    }

    /**
     * Utility method to get the oracle geometry type as a human-readable String.
     *
     * @param type the oracle geometry type
     * @return a human-readable String describing it.
     */
    public static String getJGeometryTypeName(int type) {
        String resp = "Unknown JGeometry type (" + type + ")";

        switch (type) {
        case OracleSpatialDriver.JGeometry_GTYPE_COLLECTION:
            resp = "Collection";

            break;

        case OracleSpatialDriver.JGeometry_GTYPE_CURVE:
            resp = "Curve";

            break;

        case OracleSpatialDriver.JGeometry_GTYPE_MULTICURVE:
            resp = "Multi-curve";

            break;

        case OracleSpatialDriver.JGeometry_GTYPE_MULTIPOINT:
            resp = "Multi-point";

            break;

        case OracleSpatialDriver.JGeometry_GTYPE_MULTIPOLYGON:
            resp = "Multi-polygon";

            break;

        case OracleSpatialDriver.JGeometry_GTYPE_POINT:
            resp = "Point";

            break;

        case OracleSpatialDriver.JGeometry_GTYPE_POLYGON:
            resp = "Polygon";

            break;
        }

        return resp;
    }
}
