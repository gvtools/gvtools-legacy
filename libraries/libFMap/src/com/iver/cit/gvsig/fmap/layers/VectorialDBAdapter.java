/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package com.iver.cit.gvsig.fmap.layers;

import java.awt.geom.Rectangle2D;

import org.cresques.cts.ProjectionUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.NoSuchTableException;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.ICanReproject;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver;
import com.iver.cit.gvsig.fmap.drivers.featureiterators.ReprojectWrapperFeatureIterator;



/**
 * Adapta un driver de base de datos vectorial a la interfaz vectorial,
 * manteniendo además el estado necesario por una capa vectorial de base de
 * datos (parámetros de la conexión)
 */
public class VectorialDBAdapter extends VectorialAdapter implements ISpatialDB {
    private int numReg=-1;
    private SelectableDataSource ds = null;
	/**
	 * incrementa el contador de las veces que se ha abierto el fichero.
	 * Solamente cuando el contador está a cero pide al driver que conecte con
	 * la base de datos
	 */
	public void start() throws ReadDriverException {
        ((IVectorialDatabaseDriver)driver).open();
    }

	/**
	 * decrementa el contador de número de aperturas y cuando llega a cero pide
	 * al driver que cierre la conexion con el servidor de base de datos
	 */
	public void stop() throws ReadDriverException {
	    ((IVectorialDatabaseDriver)driver).close();
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.ISpatialDB#getFeatureIterator(java.awt.geom.Rectangle2D, java.lang.String)
	 */
	/* public Connection getConnection()
	{
	    return ((VectorialDatabaseDriver)driver).getConnection();
	}*/
	/* public IFeatureIterator getFeatureIterator(String sql) throws DriverException
	{
	    return ((VectorialDatabaseDriver)driver).getFeatureIterator(sql);
	}*/
	public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG) throws ReadDriverException
	{

		CoordinateReferenceSystem newCrs = ProjectionUtils.getCRS(strEPSG);
		IFeatureIterator featureIterator = getFeatureIterator(r, strEPSG, null);
		if(driver instanceof ICanReproject){
			ICanReproject reprojectDriver = (ICanReproject)driver;
			if(reprojectDriver.canReproject(strEPSG)){
				return featureIterator;
			}
		}

		return new ReprojectWrapperFeatureIterator(featureIterator,
									getCrs(), newCrs);
	}

    /* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.ISpatialDB#getFeatureIterator(java.awt.geom.Rectangle2D, java.lang.String, java.lang.String[])
	 */
    public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG, String[] alphaNumericFieldsNeeded) throws ReadDriverException
    {
    	CoordinateReferenceSystem newCrs = ProjectionUtils.getCRS(strEPSG);
		Rectangle2D newRect = reprojectRectIfNecessary(r, newCrs);
    	return ((IVectorialDatabaseDriver)driver).getFeatureIterator(newRect, strEPSG, alphaNumericFieldsNeeded);
    }


    /*
     * Overwrites the policy of VectorialAdapter because databases dont have external
     * spatial indices (spatial index is internal to the database) and because some databases
     * can reproject, and some not
     * */
	public IFeatureIterator getFeatureIterator(Rectangle2D rect,
			String[] fields, CoordinateReferenceSystem newCrs,
			boolean fastIterator) throws ReadDriverException{
		//TODO ver como incluir el concepto de fastIteration en bbdd
		CoordinateReferenceSystem newProj = null;
		if (newCrs != null) {
			newProj = newCrs;
		} else {
			newProj = getCrs();
		}

		String srs = ProjectionUtils.getAbrev(newProj);
		IFeatureIterator featureIterator = getFeatureIterator(rect, srs, fields);
		if(driver instanceof ICanReproject){
			ICanReproject reprojectDriver = (ICanReproject)driver;
			if(reprojectDriver.canReproject(srs)){
				return featureIterator;
			}
		}
		return new ReprojectWrapperFeatureIterator(featureIterator,
									getCrs(), newProj);



	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.ISpatialDB#getFields()
	 */
	public String[] getFields()
	{
	    return ((IVectorialDatabaseDriver)driver).getFields();
	}
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.ISpatialDB#getWhereClause()
	 */
	public String getWhereClause()
	{
	    return ((IVectorialDatabaseDriver)driver).getWhereClause();
	}
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.ISpatialDB#getTableName()
	 */
	public String getTableName()
	{
	    return ((IVectorialDatabaseDriver)driver).getTableName();
	}


	/**
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#getShape(int)
	 */
	public IGeometry getShape(int index) throws ReadDriverException {
	    IGeometry geom = null;
	    geom = ((IVectorialDatabaseDriver)driver).getShape(index);
        return geom;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#getShapeType()
	 */
	public int getShapeType() throws ReadDriverException {
		return ((IVectorialDatabaseDriver)driver).getShapeType();
	}

	/**
	 * @throws ReadDriverException
	 * @see com.iver.cit.gvsig.fmap.layers.VectorialAdapter#getRecordset()
	 */
	public SelectableDataSource getRecordset() throws ReadDriverException {
	    if (driver instanceof ObjectDriver)
	    {
            if (ds == null)
            {
    			String name = LayerFactory.getDataSourceFactory().addDataSource((ObjectDriver)driver);
    			try {
                    ds = new SelectableDataSource(LayerFactory.getDataSourceFactory().createRandomDataSource(name, DataSourceFactory.AUTOMATIC_OPENING));
                } catch (NoSuchTableException e) {
                    throw new RuntimeException(e);
    			} catch (DriverLoadException e) {
					throw new ReadDriverException(name,e);
				}
            }
	    }
		return ds;
	}

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.layers.VectorialAdapter#getFeature(int)
     */
    public IFeature getFeature(int numReg) throws ReadDriverException {
        IGeometry geom;
        IFeature feat = null;
        geom = getShape(numReg);
        DataSource rs = getRecordset();
        int idFieldID = getLyrDef().getIdFieldID();
        Value[] regAtt = new Value[rs.getFieldCount()];
        String theID = null;
        for (int fieldId=0; fieldId < rs.getFieldCount(); fieldId++ )
        {
            regAtt[fieldId] =  rs.getFieldValue(numReg, fieldId);
            if (fieldId == idFieldID)
               theID = regAtt[fieldId].toString();
        }
        feat = new DefaultFeature(geom, regAtt, theID);
        return feat;
    }

    /* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.ISpatialDB#getLyrDef()
	 */
    public DBLayerDefinition getLyrDef()
    {
        return ((IVectorialDatabaseDriver)driver).getLyrDef();
    }

	public int getRowIndexByFID(IFeature feat) {
		return ((IVectorialDatabaseDriver) driver).getRowIndexByFID(feat);
	}
	private Rectangle2D reprojectRectIfNecessary(Rectangle2D rect,
			CoordinateReferenceSystem targetCrs) {
		//by design, rect Rectangle2D must be in the target reprojection
		//if targetReprojection != sourceReprojection, we are going to reproject
		//rect to the source reprojection (is faster).
		//once spatial check is made, result features will be reprojected in the inverse direction
		if (targetCrs != null && getCrs() != null
				&& !targetCrs.getName().equals(getCrs().getName())) {
			MathTransform trans = ProjectionUtils.getCrsTransform(targetCrs,
					getCrs());
			rect = ProjectionUtils.transform(rect, trans);
		}
		return rect;
	}
}
