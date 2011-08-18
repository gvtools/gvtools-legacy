/*
 * Created on 04-mar-2005
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
package com.iver.cit.gvsig.fmap.drivers.jdbc.hsqldb;

import java.awt.geom.Rectangle2D;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.ConnectionJDBC;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.DefaultJDBCDriver;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.WKBParser2;

/**
 * @author FJP
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class HSQLDBDriver extends DefaultJDBCDriver {
    private WKBParser2 parser = new WKBParser2();
    /* private int fetch_min=-1;
    private int fetch_max=-1; */
    private Statement st;
    private Rectangle2D fullExtent = null;
    private String sqlOrig;
    private String sqlTotal;

    private String strAux;
    /**
     *
     */
    public HSQLDBDriver() {
    }
    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getDriverAttributes()
     */
    public DriverAttributes getDriverAttributes() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.hardcode.driverManager.Driver#getName()
     */
    public String getName() {
        return "HSQL Spatial";
    }

	/**
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#getShape(int)
	 */
	public IGeometry getShape(int index) throws ReadDriverException {
	    IGeometry geom = null;
	    boolean resul;
	        try {
	            // EL ABSOLUTE NO HACE QUE SE VUELVAN A LEER LAS
	            // FILAS, ASI QUE MONTAMOS ESTA HISTORIA PARA QUE
	            // LO HAGA
	            // System.out.println("getShape " + index);
	            /* if (index < fetch_min)
	            {
	                rs.close();

	    	        rs = st.executeQuery(sqlOrig);
	                fetch_min = 0;
	                fetch_max = rs.getFetchSize();
	            }
	            while (index >= fetch_max)
	            {
	                rs.last();
	                // forzamos una carga
	                rs.next();
	                fetch_min = fetch_max;
	                fetch_max = fetch_max + rs.getFetchSize();
	                // System.out.println("fetchSize = " + rs.getFetchSize() + " " + fetch_min + "-" + fetch_max);
	            }
	            rs.absolute(index+1 - fetch_min); */
	            rs.absolute(index+1);
    	        // strAux = rs.getString(1);
    	        // geom = parser.read(strAux);
	            byte[] data = rs.getBytes(1);
	            geom = parser.parse(data);


            } catch (SQLException e) {
                throw new ReadDriverException(this.getName(),e);
            }

	    return geom;
	}
	/**
	 * @param conn
	 * @throws DBException
	 */
	public void setData(IConnection conn, DBLayerDefinition lyrDef) throws DBException
	{
	    this.conn = conn;
        setLyrDef(lyrDef);
	    try {
            sqlOrig = "SELECT " + getTotalFields() + " FROM " + getLyrDef().getTableName()
            + " WHERE " + getLyrDef().getWhereClause();
            // sqlOrig = getCompoundWhere(workingArea, lyrDef.getSRID_EPSG());
            sqlOrig = sqlOrig + " ORDER BY " + getLyrDef().getFieldID();

	        st = ((ConnectionJDBC)conn).getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	        rs = st.executeQuery(sqlOrig);
            metaData = rs.getMetaData();
            // Le pegamos un primera pasada para poder relacionar
            // un campo de identificador único (parecido al OID en
            // postgresql) con el índice dentro del recordset.
            // Esto cuando haya ediciones, no es válido, y hay
            // que refrescarlo.
            doRelateID_FID();

        } catch (SQLException e) {
        	  throw new DBException(e);
        }
	}

	/**
	 * @throws ExpansionFileReadException
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#getFullExtent()
	 */
	public Rectangle2D getFullExtent() throws ReadDriverException, ExpansionFileReadException{
	    if (fullExtent == null)
	    {
    	        IFeatureIterator itGeom = getFeatureIterator("SELECT the_geom AS the_geom FROM " + getLyrDef().getTableName());
    	        IGeometry geom;
    	        int cont = 0;
    	        while (itGeom.hasNext())
    	        {
    	            geom = itGeom.next().getGeometry();
    	            if (cont==0)
    	                fullExtent = geom.getBounds2D();
    	            else
    	                fullExtent.add(geom.getBounds2D());
    	            cont++;
    	        }
        }
	    return fullExtent;
	}
    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.VectorialDatabaseDriver#getGeometryIterator(java.lang.String)
     */
    public IFeatureIterator getFeatureIterator(String sql) throws ReadDriverException {
        Statement st;
        HSQLDBFeatureIterator geomIterator = null;
        try {
            st = ((ConnectionJDBC)conn).getConnection().createStatement();
            // st.setFetchSize(2000);
            ResultSet rs = st.executeQuery(sql);
            geomIterator = new HSQLDBFeatureIterator(rs);
        } catch (SQLException e) {
//            e.printStackTrace();
//            SqlDriveExceptionType type = new SqlDriveExceptionType();
//            type.setLayerName(this.getTableName());
//            type.setDriverName(this.getName());
//            type.setSchema(this.getLyrDef());
//            type.setSql(sql);
            throw new ReadDriverException(getName(),e);
        }

        return geomIterator;
    }
    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.VectorialDatabaseDriver#getGeometryIterator(java.awt.geom.Rectangle2D)
     */
    public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG) throws ReadDriverException {
        if (workingArea != null){
        r = r.createIntersection(workingArea);
        }
        double xMin = r.getMinX();
        double yMin = r.getMinY();
        double xMax = r.getMaxX();
        double yMax = r.getMaxY();

        String wktBox = "GeomFromText('LINESTRING(" + xMin + " " + yMin + ", "
		+ xMax + " " + yMin + ", "
		+ xMax + " " + yMax + ", "
		+ xMin + " " + yMax + ")', "
		+ strEPSG + ")";
        String sqlAux = sqlOrig;
        if (getWhereClause().startsWith("WHERE"))
            sqlAux += " 1=1" ;

        return getFeatureIterator(sqlAux);
    }
    public void open() {
        // TODO Auto-generated method stub

    }
	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getConnectionStringBeginning()
	 */
	public String getConnectionStringBeginning() {
		return "jdbc:mysql:";
	}

    static{
	    try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
    }

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getGeometryField(java.lang.String)
	 */
	public String getGeometryField(String fieldName) {
		return fieldName;
	}
    /**
     * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getPrimaryKeys()
     */
    public int[] getPrimaryKeys() throws ReadDriverException {
        return null;
    }
    /**
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialJDBCDriver#getDefaultPort()
     */
    public int getDefaultPort() {
        return 0;
    }
    /**
     * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#write(com.hardcode.gdbms.engine.data.edition.DataWare)
     */
    public void write(DataWare arg0) throws ReadDriverException {
        // TODO Auto-generated method stub

    }

    public String getSqlTotal()
    {
        return sqlTotal;
    }
    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.DefaultDBDriver#getCompleteWhere()
     */
    public String getCompleteWhere() {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.VectorialDatabaseDriver#getFeatureIterator(java.awt.geom.Rectangle2D, java.lang.String, java.lang.String[])
     */
    public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG, String[] alphaNumericFieldsNeeded) throws ReadDriverException {
        // TODO Auto-generated method stub
        return null;
    }
	public boolean isWritable() {
		// TODO Auto-generated method stub
		return true;
	}
	public String[] getTableFields(IConnection conex, String table) throws DBException {
		try{
		Statement st = ((ConnectionJDBC)conex).getConnection().createStatement();
        // ResultSet rs = dbmd.getTables(catalog, null, dbLayerDefinition.getTable(), null);
		ResultSet rs = st.executeQuery("select * from " + table + " LIMIT 1");
		ResultSetMetaData rsmd = rs.getMetaData();

		String[] ret = new String[rsmd.getColumnCount()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = rsmd.getColumnName(i+1);
		}

		return ret;
		}catch (SQLException e) {
			throw new DBException(e);
		}
	}
}

// [eiel-gestion-conexiones]