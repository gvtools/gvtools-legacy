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
package com.iver.cit.gvsig.fmap.drivers.jdbc.mysql;

import java.awt.geom.Rectangle2D;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.ConnectionJDBC;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.DefaultJDBCDriver;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.WKBParser2;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;

/**
 * @author FJP
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class MySQLDriver extends DefaultJDBCDriver
							implements IWriteable {

	private static Logger logger = Logger
			.getLogger(MySQLDriver.class.getName());

	private WKBParser2 parser = new WKBParser2();

	private MySQLSpatialWriter writer = new MySQLSpatialWriter();

	private static final String[] GEOM_SQL_TYPE_NAMES ={
	      "GEOMETRY",
	      "POINT",
	      "LINESTRING",
	      "POLYGON",
	      "MULTIPOINT",
	      "MULTILINESTRING",
	      "MULTIPOLYGON",
	      "GEOMETRYCOLLECTION",
	};

	/*
	 * private int fetch_min=-1; private int fetch_max=-1;
	 */
	private Statement st;

	private String strAux;

	private String strEPSG = "-1";

	private String originalEPSG;

	private String completeWhere;

	/**
	 * Don't have information about working area
	 */
	private String sqlOrig;

	/**
	 * Does have information about working area and order
	 */
	private String sqlTotal;

	/**
	 *
	 */
	public MySQLDriver() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getDriverAttributes()
	 */
	public DriverAttributes getDriverAttributes() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.hardcode.driverManager.Driver#getName()
	 */
	public String getName() {
		return "MySQL Spatial";
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#getShape(int)
	 */
	public IGeometry getShape(int index) {
		IGeometry geom = null;
		boolean resul;
		try {
			if (rs != null) {
				rs.absolute(index + 1);
				byte[] data = rs.getBytes(1);
				geom = parser.parse(data);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return geom;
	}

	/**
	 * @param conn
	 * @throws DBException
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialJDBCDriver#setData(java.sql.Connection,
	 *      java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public void setData(IConnection conn, DBLayerDefinition lyrDef) throws DBException {
		this.conn = conn;
		setLyrDef(lyrDef);
		try {

			// NO ESTA LISTO ESTO AUN EN mySQL, o no sé usuarlo getTableEPSG();

			sqlOrig = "SELECT " + getTotalFields() + " FROM "
					+ getLyrDef().getTableName() + " ";
					// + getLyrDef().getWhereClause();
			completeWhere = getCompoundWhere(workingArea, strEPSG);
			String sqlAux = sqlOrig + completeWhere + " ORDER BY "
					+ getLyrDef().getFieldID();
			logger.info("Cadena SQL:" + sqlAux);
			sqlTotal = sqlAux;

			st = ((ConnectionJDBC)conn).getConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);

			rs = st.executeQuery(sqlTotal);
			metaData = rs.getMetaData();
			// Le pegamos un primera pasada para poder relacionar
			// un campo de identificador único (parecido al OID en
			// postgresql) con el índice dentro del recordset.
			// Esto cuando haya ediciones, no es válido, y hay
			// que refrescarlo.
			doRelateID_FID();

			writer.setCreateTable(false);
			writer.setWriteAll(false);
			writer.initialize(lyrDef);


		} catch (SQLException e) {
			  throw new DBException(e);
		} catch (InitializeWriterException e) {
			  throw new DBException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDatabaseDriver#getGeometryIterator(java.lang.String)
	 */
	public IFeatureIterator getFeatureIterator(String sql)
			throws ReadDriverException {
		Statement st;
		MySqlFeatureIterator geomIterator = null;
		geomIterator = myGetFeatureIterator(sql);
		geomIterator.setLyrDef(getLyrDef());

		return geomIterator;
	}

	private MySqlFeatureIterator myGetFeatureIterator(String sql)
	throws ReadDriverException {
		Statement st;
		MySqlFeatureIterator geomIterator = null;
		try {
			logger.debug(sql);
			st = ((ConnectionJDBC)conn).getConnection().createStatement();
			// st.setFetchSize(2000);
			ResultSet rs = st.executeQuery(sql);
			geomIterator = new MySqlFeatureIterator(rs);
			geomIterator.setLyrDef(getLyrDef());
		} catch (SQLException e) {
//			e.printStackTrace();
//			SqlDriveExceptionType type = new SqlDriveExceptionType();
//			type.setDriverName("MySQL Driver");
//			type.setSql(sql);
//			type.setLayerName(getTableName());
//			type.setSchema(null);
			throw new ReadDriverException("MySQL Driver",e);
//			throw new com.iver.cit.gvsig.fmap.DriverException(e);
		}

		return geomIterator;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDatabaseDriver#getGeometryIterator(java.awt.geom.Rectangle2D)
	 */
	public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG)
			throws ReadDriverException {
		if (workingArea != null)
			r = r.createIntersection(workingArea);
		String sqlAux = sqlOrig + getCompoundWhere(r, strEPSG);

		return getFeatureIterator(sqlAux);
	}

	/**
	 * Le pasas el rectángulo que quieres pedir. La primera vez es el
	 * workingArea, y las siguientes una interseccion de este rectangulo con el
	 * workingArea
	 *
	 * @param r
	 * @param strEPSG
	 * @return
	 */
	private String getCompoundWhere(Rectangle2D r, String strEPSG) {
		if (r == null)
			return getWhereClause();

		double xMin = r.getMinX();
		double yMin = r.getMinY();
		double xMax = r.getMaxX();
		double yMax = r.getMaxY();

		String wktBox = "GeomFromText('LINESTRING(" + xMin + " " + yMin + ", "
				+ xMax + " " + yMin + ", " + xMax + " " + yMax + ", " + xMin
				+ " " + yMax + ")', '" + strEPSG + "')";
		String sqlAux;
		if (getWhereClause().startsWith("WHERE"))
			sqlAux = getWhereClause() + " AND " +"MBRIntersects(" + wktBox + ", " + getLyrDef().getFieldGeometry() + ")";
		else
			sqlAux = "WHERE MBRIntersects(" + wktBox + ", " + getLyrDef().getFieldGeometry() + ")";
		return sqlAux;
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

	static {
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
		return "ASBINARY(" + fieldName + ")";
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getPrimaryKeys()
	 */
	public int[] getPrimaryKeys()
			throws ReadDriverException {
		return new int[] { getLyrDef().getIdFieldID() - 2 };
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialJDBCDriver#getDefaultPort()
	 */
	public int getDefaultPort() {
		return 3306;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#write(com.hardcode.gdbms.engine.data.edition.DataWare)
	 */
	public void write(DataWare arg0){
		// TODO Auto-generated method stub

	}

	private void getTableEPSG() {
		try {
			Statement stAux = ((ConnectionJDBC)conn).getConnection().createStatement();

			String sql = "SELECT SRID(" + getLyrDef().getFieldGeometry() + ") FROM " + getTableName()
					+ " LIMIT 1;";
			ResultSet rs = stAux.executeQuery(sql);
			rs.next();
			originalEPSG = "" + rs.getInt(1);
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getSqlTotal() {
		return sqlTotal;
	}

	/**
	 * @return Returns the completeWhere.
	 */
	public String getCompleteWhere() {
		return completeWhere;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDatabaseDriver#getFeatureIterator(java.awt.geom.Rectangle2D,
	 *      java.lang.String, java.lang.String[])
	 */
	public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG,
			String[] alphaNumericFieldsNeeded) throws ReadDriverException {

		DBLayerDefinition lyrDef = getLyrDef();
		DBLayerDefinition clonedLyrDef = cloneLyrDef(lyrDef);
		ArrayList<FieldDescription> myFieldsDesc = new ArrayList<FieldDescription>(); // = new FieldDescription[alphaNumericFieldsNeeded.length+1];

		if (workingArea != null)
			r = r.createIntersection(workingArea);

		String strAux = getGeometryField(lyrDef.getFieldGeometry());
		boolean found = false;
		if (alphaNumericFieldsNeeded != null) {
			for (int i = 0; i < alphaNumericFieldsNeeded.length; i++) {
				strAux = strAux + ", " + alphaNumericFieldsNeeded[i];
				if (alphaNumericFieldsNeeded[i].
						equals(lyrDef.getFieldID())){
					found = true;
					clonedLyrDef.setIdFieldID(i);
				}


				FieldDescription[] fieldsDesc = lyrDef.getFieldsDesc();
				for (int j =0; j < fieldsDesc.length; j++){
					if (fieldsDesc[j].getFieldName().
							equals(alphaNumericFieldsNeeded[i])){
						myFieldsDesc.add(fieldsDesc[j]);
					}
				}


			}
		}
		// Nos aseguramos de pedir siempre el campo ID
		if (found == false) {
			strAux = strAux + ", " + lyrDef.getFieldID();
			myFieldsDesc.add(lyrDef.getFieldsDesc()[lyrDef.getIdField(
					lyrDef.getFieldID())]);
			clonedLyrDef.setIdFieldID(myFieldsDesc.size()-1);
		}
		clonedLyrDef.setFieldsDesc( (FieldDescription[])myFieldsDesc.toArray(new FieldDescription[]{}) );


		String sqlProv = "SELECT " + strAux + " FROM "
				+ lyrDef.getTableName() + " ";
				// + getLyrDef().getWhereClause();

		String sqlAux;
		sqlAux = sqlProv + getCompoundWhere(r, strEPSG);

		System.out.println("SqlAux getFeatureIterator = " + sqlAux);
		MySqlFeatureIterator geomIterator = null;
		geomIterator = myGetFeatureIterator(sqlAux);
		geomIterator.setLyrDef(clonedLyrDef);
		return geomIterator;
	}

	public boolean isWritable() {
		return true;
	}

	public IWriter getWriter() {
		return writer;
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

private DBLayerDefinition cloneLyrDef(DBLayerDefinition lyrDef){
	DBLayerDefinition clonedLyrDef = new DBLayerDefinition();

	clonedLyrDef.setName(lyrDef.getName());
	clonedLyrDef.setFieldsDesc(lyrDef.getFieldsDesc());

	clonedLyrDef.setShapeType(lyrDef.getShapeType());
	clonedLyrDef.setProjection(lyrDef.getProjection());

	clonedLyrDef.setConnection(lyrDef.getConnection());
	clonedLyrDef.setCatalogName(lyrDef.getCatalogName());
	clonedLyrDef.setSchema(lyrDef.getSchema());
	clonedLyrDef.setTableName(lyrDef.getTableName());

	clonedLyrDef.setFieldID(lyrDef.getFieldID());
	clonedLyrDef.setFieldGeometry(lyrDef.getFieldGeometry());
	clonedLyrDef.setWhereClause(lyrDef.getWhereClause());
	clonedLyrDef.setWorkingArea(lyrDef.getWorkingArea());
	clonedLyrDef.setSRID_EPSG(lyrDef.getSRID_EPSG());
	clonedLyrDef.setClassToInstantiate(lyrDef.getClassToInstantiate());

	clonedLyrDef.setIdFieldID(lyrDef.getIdFieldID());
	clonedLyrDef.setDimension(lyrDef.getDimension());
	clonedLyrDef.setHost(lyrDef.getHost());
	clonedLyrDef.setPort(lyrDef.getPort());
	clonedLyrDef.setDataBase(lyrDef.getDataBase());
	clonedLyrDef.setUser(lyrDef.getUser());
	clonedLyrDef.setPassword(lyrDef.getPassword());
	clonedLyrDef.setConnectionName(lyrDef.getConnectionName());
	return clonedLyrDef;
}


/**
 * Gets the table's possible geometry fields. By default, all fields can be geometry
 * fields. It should be overwritten by subclasses.
 *
 * @param conn conenction object
 * @param table_name table name
 * @return the table's possible geometry fields
 * @throws SQLException
 */
public String[] getGeometryFieldsCandidates(IConnection conn, String table_name) throws DBException {
	try {
		Statement st = ((ConnectionJDBC)conn).getConnection().createStatement();
		ResultSet rs = st.executeQuery("select * from " + table_name + " where false");
		ResultSetMetaData rsmd = rs.getMetaData();
		ArrayList names = new ArrayList();
		ResultSetMetaData rsMeta = rs.getMetaData();
		boolean isGeo;
		for (int i = 0; i < rsMeta.getColumnCount(); i++) {
			isGeo = false;
			System.out.println(rsMeta.getColumnName(i+1));
			for (int j = 0;j< GEOM_SQL_TYPE_NAMES.length;j++){
				rsMeta.getColumnType(i+1);
				System.out.println(rsMeta.getColumnTypeName(i+1));
				if (GEOM_SQL_TYPE_NAMES[j].equalsIgnoreCase(rsMeta.getColumnTypeName(i+1))){
					isGeo = true;
					break;
				}
			}
			if (isGeo || "UNKNOWN".equalsIgnoreCase(rsMeta.getColumnTypeName(i+1))){
				names.add(rsMeta.getColumnName(i+1));
			}
		}
		rsMeta = null;
		rs.close(); st.close();
		return (String[]) names.toArray(new String[names.size()]);
    	} catch (SQLException e) {
			throw new DBException(e);
		}
}

}

// [eiel-gestion-conexiones]
