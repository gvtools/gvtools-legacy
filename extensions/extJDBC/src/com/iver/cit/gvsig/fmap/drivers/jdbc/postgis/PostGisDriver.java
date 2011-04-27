/*
 * Created on 04-mar-2005
 *
 * gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
package com.iver.cit.gvsig.fmap.drivers.jdbc.postgis;

import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.postgis.PGbox2d;
import org.postgis.PGbox3d;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.ICanReproject;
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
import com.iver.cit.gvsig.fmap.drivers.XTypes;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;

/**
 * @author FJP
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class PostGisDriver extends DefaultJDBCDriver implements ICanReproject, IWriteable {
	private static Logger logger = Logger.getLogger(PostGisDriver.class
			.getName());

	private static int FETCH_SIZE = 5000;

	// To avoid problems when using wkb_cursor with same layer.
	// I mean, when you add twice or more the same layer using
	// the same connection

	private static int CURSOR_ID = 0;

	private int myCursorId;

	private PostGISWriter writer = new PostGISWriter();

	private WKBParser2 parser = new WKBParser2();

	private int fetch_min = -1;

	private int fetch_max = -1;

	private String sqlOrig;

	/**
	 * Used by setAbsolutePosition
	 */
	private String sqlTotal;

	private String strEPSG = null;

	private String originalEPSG = null;

	private Rectangle2D fullExtent = null;

	private String strAux;

	private String completeWhere;

	private String provCursorName = null;

	int numProvCursors = 0;

	boolean bShapeTypeRevised = false;

	private int actual_position;

	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 */
	public PostGisDriver() {
		// To avoid problems when using wkb_cursor with same layer.
		// I mean, when you add twice or more the same layer using
		// the same connection
		CURSOR_ID++;
		myCursorId = CURSOR_ID;
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
		return "PostGIS JDBC Driver";
	}

	/**
	 * @throws ReadDriverException
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#getShape(int)
	 */
	public IGeometry getShape(int index) throws ReadDriverException {
		IGeometry geom = null;
		boolean resul;
		try {
			setAbsolutePosition(index);
			// strAux = rs.getString(1);
			// geom = parser.read(strAux);
			if (rs != null) {
				byte[] data = rs.getBytes(1);
				if (data == null) // null geometry.
					return null;
				geom = parser.parse(data);
			}
		} catch (SQLException e) {
			throw new ReadDriverException(this.getName(),e);
		}

		return geom;
	}

	/**
	 * First, the geometry field. After, the rest of fields
	 *
	 * @return
	 */
	/*
	 * public String getTotalFields() { String strAux = "AsBinary(" +
	 * getLyrDef().getFieldGeometry() + ")"; String[] fieldNames =
	 * getLyrDef().getFieldNames(); for (int i=0; i< fieldNames.length; i++) {
	 * strAux = strAux + ", " + fieldNames[i]; } return strAux; }
	 */

	/**
	 * Antes de llamar a esta funci�n hay que haber fijado el workingArea si se
	 * quiere usar.
	 *
	 * @param conn
	 * @throws DBException
	 */
	public void setData(IConnection conn, DBLayerDefinition lyrDef) throws DBException {
		this.conn = conn;
		// TODO: Deber�amos poder quitar Conneciton de la llamada y meterlo
		// en lyrDef desde el principio.

		lyrDef.setConnection(conn);
		setLyrDef(lyrDef);

		getTableEPSG_and_shapeType(conn, lyrDef);

		getLyrDef().setSRID_EPSG(originalEPSG);

		try {
			((ConnectionJDBC)conn).getConnection().setAutoCommit(false);
			sqlOrig = "SELECT " + getTotalFields() + " FROM "
					+ getLyrDef().getComposedTableName() + " ";
					// + getLyrDef().getWhereClause();
			if (canReproject(strEPSG)) {
				completeWhere = getCompoundWhere(sqlOrig, workingArea, strEPSG);
			} else {
				completeWhere = getCompoundWhere(sqlOrig, workingArea,
						originalEPSG);
			}
			// completeWhere = getLyrDef().getWhereClause() + completeWhere;

			String sqlAux = sqlOrig + completeWhere + " ORDER BY "
			+ getLyrDef().getFieldID();

			sqlTotal = sqlAux;
			logger.info("Cadena SQL:" + sqlAux);
			st = ((ConnectionJDBC)conn).getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			// st.setFetchSize(FETCH_SIZE);
			myCursorId++;
			st.execute("declare " + getTableName() + myCursorId + "_wkb_cursor binary scroll cursor with hold for " + sqlAux);
			rs = st.executeQuery("fetch forward " + FETCH_SIZE
					+ " in " + getTableName() + myCursorId + "_wkb_cursor");
			fetch_min = 0;
			fetch_max = FETCH_SIZE - 1;
			actual_position = 0;
			metaData = rs.getMetaData();
			doRelateID_FID();

			writer.setCreateTable(false);
			writer.setWriteAll(false);
			writer.initialize(lyrDef);


		} catch (SQLException e) {
			try {
				if (rs != null){
					rs.close();
				}
			} catch (SQLException e1) {
				throw new DBException(e);
			}
			throw new DBException(e);
		} catch (InitializeWriterException e) {
			throw new DBException(e);
		}
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#getFullExtent()
	 */
	public Rectangle2D getFullExtent() throws ReadDriverException {
		if (fullExtent == null) {
			try {
				Statement s = ((ConnectionJDBC)conn).getConnection().createStatement();
				ResultSet r = s.executeQuery("SELECT extent("
						+ getLyrDef().getFieldGeometry()
						+ ") AS FullExtent FROM " + getLyrDef().getComposedTableName()
						+ " " + getCompleteWhere());
				r.next();
				String strAux = r.getString(1);
				System.out.println("fullExtent = " + strAux);
				if (strAux == null)
				{
					logger.debug("La capa " + getLyrDef().getName() + " no tiene FULLEXTENT");
					return null;
				}
				if (strAux.startsWith("BOX3D")) {
					PGbox3d regeom = new PGbox3d(strAux);
					double x = regeom.getLLB().x;
					double y = regeom.getLLB().y;
					double w = regeom.getURT().x - x;
					double h = regeom.getURT().y - y;
					fullExtent = new Rectangle2D.Double(x, y, w, h);
				} else {
					PGbox2d regeom = new PGbox2d(strAux);
					double x = regeom.getLLB().x;
					double y = regeom.getLLB().y;
					double w = regeom.getURT().x - x;
					double h = regeom.getURT().y - y;
					fullExtent = new Rectangle2D.Double(x, y, w, h);
				}
			} catch (SQLException e) {
				throw new ReadDriverException(this.getName(),e);
			}

		}
		return fullExtent;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDatabaseDriver#getGeometryIterator(java.lang.String)
	 */
	public IFeatureIterator getFeatureIterator(String sql) throws ReadDriverException {
		PostGisFeatureIterator geomIterator = null;
		geomIterator = myGetFeatureIterator(sql);
		geomIterator.setLyrDef(getLyrDef());

		return geomIterator;
	}

	private PostGisFeatureIterator myGetFeatureIterator(String sql) throws ReadDriverException {
		PostGisFeatureIterator geomIterator = null;
		try {
			// st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			// ResultSet.CONCUR_READ_ONLY);


			// st = conn.createStatement
			if (provCursorName != null) {
				/* st.execute("BEGIN");
				st.execute("CLOSE " + provCursorName);
				bCursorActivo = false;
				st.execute("COMMIT"); */
				numProvCursors++;
			}
			// st.execute("BEGIN");
			provCursorName = getTableName() + myCursorId + "wkb_cursor_prov_" + System.currentTimeMillis() + "" + numProvCursors;

			// st.execute("BEGIN");
			bCursorActivo = true;
			// ResultSet rs = st.executeQuery(sql);
			geomIterator = new PostGisFeatureIterator(((ConnectionJDBC)conn).getConnection(), provCursorName, sql);
		} catch (SQLException e) {
//			e.printStackTrace();
//			e.printStackTrace();
//			SqlDriveExceptionType type = new SqlDriveExceptionType();
//            type.setDriverName("PostGIS Driver");
//            type.setSql(sql);
//            type.setLayerName(getTableName());
//            type.setSchema(null);
            throw new ReadDriverException("PostGIS Driver",e);
//			throw new DriverException(e);
			// return null;
		}

		return geomIterator;
	}

	public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG)
			throws ReadDriverException {
		if (workingArea != null)
			r = r.createIntersection(workingArea);

		String sqlAux;
		if (canReproject(strEPSG)) {
			sqlAux = sqlOrig + getCompoundWhere(sqlOrig, r, strEPSG);
		} else {
			sqlAux = sqlOrig + getCompoundWhere(sqlOrig, r, originalEPSG);
		}

		System.out.println("SqlAux getFeatureIterator = " + sqlAux);

		return getFeatureIterator(sqlAux);
	}

	/**
	 * Le pasas el rect�ngulo que quieres pedir. La primera vez es el
	 * workingArea, y las siguientes una interseccion de este rectangulo con el
	 * workingArea
	 *
	 * @param r
	 * @param strEPSG
	 * @return
	 */
	private String getCompoundWhere(String sql, Rectangle2D r, String strEPSG) {
		if (r == null)
			return getWhereClause();

		double xMin = r.getMinX();
		double yMin = r.getMinY();
		double xMax = r.getMaxX();
		double yMax = r.getMaxY();
		String wktBox = "GeometryFromText('LINESTRING(" + xMin + " " + yMin
				+ ", " + xMax + " " + yMin + ", " + xMax + " " + yMax + ", "
				+ xMin + " " + yMax + ")', " + strEPSG + ")";
		String sqlAux;
		if (getWhereClause().toUpperCase().indexOf("WHERE") != -1)
			sqlAux = getWhereClause() + " AND " + getLyrDef().getFieldGeometry() + " && " + wktBox;
		else
			sqlAux = "WHERE " + getLyrDef().getFieldGeometry() + " && "
					+ wktBox;
		return sqlAux;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getConnectionStringBeginning()
	 */
	public String getConnectionStringBeginning() {
		return "jdbc:postgresql:";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.drivers.DefaultDBDriver#getFieldValue(long,
	 *      int)
	 */
	public Value getFieldValue(long rowIndex, int idField)
			throws ReadDriverException {
		boolean resul;
		// EL ABSOLUTE NO HACE QUE SE VUELVAN A LEER LAS
		// FILAS, ASI QUE MONTAMOS ESTA HISTORIA PARA QUE
		// LO HAGA
		// System.out.println("getShape " + index);
		int index = (int) (rowIndex);
		try {
			setAbsolutePosition(index);
			int fieldId = idField + 2;
			return getFieldValue(rs, fieldId);

		} catch (SQLException e) {
			throw new ReadDriverException("PostGIS Driver",e);
		}
	}

	static Value getFieldValue(ResultSet aRs, int fieldId)
	throws SQLException {
		ResultSetMetaData metaData = aRs.getMetaData();
		byte[] byteBuf = aRs.getBytes(fieldId);
		if (byteBuf == null)
			return ValueFactory.createNullValue();
		else {
			ByteBuffer buf = ByteBuffer.wrap(byteBuf);
			if (metaData.getColumnType(fieldId) == Types.VARCHAR)
				return ValueFactory.createValue(aRs.getString(fieldId));
			if (metaData.getColumnType(fieldId) == Types.CHAR){
				String character = aRs.getString(fieldId);
				if (character != null){
					return ValueFactory.createValue(character.trim());
				}else{
					return ValueFactory.createValue(character);
				}					
			}
			if (metaData.getColumnType(fieldId) == Types.FLOAT)
				return ValueFactory.createValue(buf.getFloat());
			if (metaData.getColumnType(fieldId) == Types.DOUBLE)
				return ValueFactory.createValue(buf.getDouble());
			if (metaData.getColumnType(fieldId) == Types.REAL)
				return ValueFactory.createValue(buf.getFloat());
			if (metaData.getColumnType(fieldId) == Types.INTEGER)
				return ValueFactory.createValue(buf.getInt());
			if (metaData.getColumnType(fieldId) == Types.BIGINT)
				return ValueFactory.createValue(buf.getLong());
			if (metaData.getColumnType(fieldId) == Types.BIT)
				return ValueFactory.createValue((byteBuf[0] == 1));
			if (metaData.getColumnType(fieldId) == Types.BOOLEAN)
				return ValueFactory.createValue(aRs.getBoolean(fieldId));
			if (metaData.getColumnType(fieldId) == Types.DATE)
			{
				long daysAfter2000 = buf.getInt() + 1;
				long msecs = daysAfter2000*24*60*60*1000;
				long real_msecs_date1 = (long) (XTypes.NUM_msSecs2000 + msecs);
				Date realDate1 = new Date(real_msecs_date1);
				return ValueFactory.createValue(realDate1);
			}
			if (metaData.getColumnType(fieldId) == Types.TIME)
			{
				// TODO:
					// throw new RuntimeException("TIME type not implemented yet");
					return ValueFactory.createValue("NOT IMPLEMENTED YET");
				}
				if (metaData.getColumnType(fieldId) == Types.TIMESTAMP)
				{
					double segsReferredTo2000 = buf.getDouble();
					long real_msecs = (long) (XTypes.NUM_msSecs2000 + segsReferredTo2000*1000);
					Timestamp valTimeStamp = new Timestamp(real_msecs);
					return ValueFactory.createValue(valTimeStamp);
				}

				if (metaData.getColumnType(fieldId) == Types.NUMERIC) {
					// System.out.println(metaData.getColumnName(fieldId) + " "
					// + metaData.getColumnClassName(fieldId));
					short ndigits = buf.getShort();
					short weight = buf.getShort();
					short sign = buf.getShort();
					short dscale = buf.getShort();
					String strAux;
					if (sign == 0)
						strAux = "+";
					else
						strAux = "-";

					for (int iDigit = 0; iDigit < ndigits; iDigit++) {
						short digit = buf.getShort();
						strAux = strAux + digit;
						if (iDigit == weight)
							strAux = strAux + ".";

					}
					strAux = strAux + "0";
					BigDecimal dec;
					dec = new BigDecimal(strAux);
					// System.out.println(ndigits + "_" + weight + "_" + dscale
					// + "_" + strAux);
					// System.out.println(strAux + " Big= " + dec);
					return ValueFactory.createValue(dec.doubleValue());
				}

			}

		return ValueFactory.createNullValue();

	}

	public void open() {
		/*
		 * try { st = conn.createStatement(); st.setFetchSize(2000); if
		 * (bCursorActivo) close(); st.execute("declare wkb_cursor binary cursor
		 * for " + sqlOrig); rs = st.executeQuery("fetch forward all in
		 * wkb_cursor"); // st.execute("BEGIN"); bCursorActivo = true; } catch
		 * (SQLException e) { e.printStackTrace(); throw new
		 * com.iver.cit.gvsig.fmap.DriverException(e); }
		 */

	}

	private void setAbsolutePosition(int index) throws SQLException {
		// TODO: USAR LIMIT Y ORDER BY, Y HACERLO TAMBI�N PARA
		// MYSQL

		// EL ABSOLUTE NO HACE QUE SE VUELVAN A LEER LAS
		// FILAS, ASI QUE MONTAMOS ESTA HISTORIA PARA QUE
		// LO HAGA
		// System.out.println("getShape " + index + " fetchMin=" + fetch_min + "
		// fetchMax=" + fetch_max);

		if ((index >= fetch_min) && (index <= fetch_max))
		{
			// Est� en el intervalo, as� que lo podemos posicionar

		}
		else
		{
			// calculamos el intervalo correcto
			fetch_min = (index / FETCH_SIZE) * FETCH_SIZE;
			fetch_max = fetch_min +  FETCH_SIZE - 1;
			// y cogemos ese cacho
			rs.close();
			myCursorId++;
			st.execute("declare " + getTableName() + myCursorId + "_wkb_cursorAbsolutePosition binary scroll cursor with hold for " + sqlTotal);
			st.executeQuery("fetch absolute " + fetch_min
			+ " in " + getTableName() + myCursorId + "_wkb_cursorAbsolutePosition");

			rs = st.executeQuery("fetch forward " + FETCH_SIZE
			+ " in " + getTableName() + myCursorId + "_wkb_cursorAbsolutePosition");

		}
		rs.absolute(index - fetch_min + 1);
		actual_position = index;

	}

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getGeometryField(java.lang.String)
	 */
	public String getGeometryField(String fieldName) {
		return "AsEWKB(" + fieldName + ", 'XDR')";
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getPrimaryKeys()
	 */
	public int[] getPrimaryKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialJDBCDriver#getDefaultPort()
	 */
	public int getDefaultPort() {
		return 5432;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#write(com.hardcode.gdbms.engine.data.edition.DataWare)
	 */
	public void write(DataWare arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.core.ICanReproject#getSourceProjection()
	 */
	public String getSourceProjection(IConnection conn,DBLayerDefinition dbld) {
		if (originalEPSG == null)
			getTableEPSG_and_shapeType(conn,dbld);
		return originalEPSG;
	}

	/**
	 * Las tablas con geometr�as est�n en la tabla GEOMETRY_COLUMNS y de
	 * ah� sacamos en qu� proyecci�n est�n.
	 * El problema es que si el usuario hace una vista de esa
	 * tabla, no estar� dada de alta aqu� y entonces gvSIG
	 * no se entera en qu� proyecci�n est� trabajando (y le
	 * ponemos un -1 como mal menor). El -1 implica que luego
	 * no podremos reproyectar al vuelo desde la base de datos.
	 * OJO: ES SENSIBLE A MAYUSCULAS / MINUSCULAS!!!
	 */
	private void getTableEPSG_and_shapeType(IConnection conn, DBLayerDefinition dbld) {
		try {
			Statement stAux = ((ConnectionJDBC)conn).getConnection().createStatement();

//			String sql = "SELECT * FROM GEOMETRY_COLUMNS WHERE F_TABLE_NAME = '"
//					+ getTableName() + "' AND F_GEOMETRY_COLUMN = '" + getLyrDef().getFieldGeometry() + "'";
			String sql;
			if (dbld.getSchema() == null || dbld.getSchema().equals("")){
				sql = "SELECT * FROM GEOMETRY_COLUMNS WHERE F_TABLE_SCHEMA = current_schema() AND F_TABLE_NAME = '"
					+ dbld.getTableName() + "' AND F_GEOMETRY_COLUMN = '" + dbld.getFieldGeometry() + "'";
			}else{
				sql = "SELECT * FROM GEOMETRY_COLUMNS WHERE F_TABLE_SCHEMA = '"+ dbld.getSchema() + "' AND F_TABLE_NAME = '"
						+ dbld.getTableName() + "' AND F_GEOMETRY_COLUMN = '" + dbld.getFieldGeometry() + "'";
			}

			ResultSet rs = stAux.executeQuery(sql);
			if (rs.next()){
				originalEPSG = "" + rs.getInt("SRID");
				String geometryType = rs.getString("TYPE");
				int shapeType = FShape.MULTI;
				if (geometryType.compareToIgnoreCase("POINT") == 0)
					shapeType = FShape.POINT;
				else if (geometryType.compareToIgnoreCase("LINESTRING") == 0)
					shapeType = FShape.LINE;
				else if (geometryType.compareToIgnoreCase("POLYGON") == 0)
					shapeType = FShape.POLYGON;
				else if (geometryType.compareToIgnoreCase("MULTIPOINT") == 0)
					shapeType = FShape.MULTIPOINT;
				else if (geometryType.compareToIgnoreCase("MULTILINESTRING") == 0)
					shapeType = FShape.LINE;
				else if (geometryType.compareToIgnoreCase("MULTILINESTRINGM") == 0) //MCoord
					shapeType = FShape.LINE | FShape.M;
				else if (geometryType.compareToIgnoreCase("MULTIPOLYGON") == 0)
					shapeType = FShape.POLYGON;
				dbld.setShapeType(shapeType);
			} else{
				originalEPSG = "-1";
			}


			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			originalEPSG = "-1";
			logger.error(e);
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.core.ICanReproject#getDestProjection()
	 */
	public String getDestProjection() {
		return strEPSG;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.core.ICanReproject#setDestProjection(java.lang.String)
	 */
	public void setDestProjection(String toEPSG) {
		this.strEPSG = toEPSG;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.core.ICanReproject#canReproject(java.lang.String)
	 */
	public boolean canReproject(String toEPSGdestinyProjection) {
		// TODO POR AHORA, REPROYECTA SIEMPRE gvSIG.
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.drivers.DefaultDBDriver#doRelateID_FID()
	 */
	protected void doRelateID_FID() throws DBException {
		hashRelate = new Hashtable();
		try {
			String strSQL = "SELECT " + getLyrDef().getFieldID() + " FROM "
					+ getLyrDef().getComposedTableName() + " ";
					// + getLyrDef().getWhereClause();
			if (canReproject(strEPSG)) {
				strSQL = strSQL
						+ getCompoundWhere(strSQL, workingArea, strEPSG);
			} else {
				strSQL = strSQL
						+ getCompoundWhere(strSQL, workingArea, originalEPSG);
			}
			strSQL = strSQL + " ORDER BY " + getLyrDef().getFieldID();
			Statement s = ((ConnectionJDBC)getConnection()).getConnection().createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int fetchSize = 5000;
			ResultSet r = s.executeQuery(strSQL);
			int id = 0;
			String gid;
			while (r.next()) {
				gid = r.getString(1);
				Value aux = ValueFactory.createValue(gid);
				hashRelate.put(aux, new Integer(id));
				// System.out.println("ASOCIANDO CLAVE " + aux + " CON VALOR " + id);
				id++;
				// System.out.println("Row " + id + ":" + strAux);
			}
			s.close();
			numReg = id;

			/*
			 * for (int index = 0; index < getShapeCount(); index++) { Value aux =
			 * getFieldValue(index, idFID_FieldName-2); hashRelate.put(aux, new
			 * Integer(index)); // System.out.println("Row " + index + " clave=" +
			 * aux); }
			 */
			/*
			 * int index = 0;
			 *
			 * while (rs.next()) { Value aux = getFieldValue(index,
			 * idFID_FieldName-2); hashRelate.put(aux, new Integer(index));
			 * index++; System.out.println("Row " + index + " clave=" + aux); }
			 * numReg = index;
			 */
			// rs.beforeFirst();
			/*
			 * } catch (com.hardcode.gdbms.engine.data.driver.DriverException e) { //
			 * TODO Auto-generated catch block e.printStackTrace();
			 */
		} catch (SQLException e) {
			throw new DBException(e);
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
	 * @see com.iver.cit.gvsig.fmap.drivers.DefaultDBDriver#close()
	 */
	public void close() {
		super.close();
		/*
		 * if (bCursorActivo) { try { // st =
		 * conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
		 * ResultSet.CONCUR_READ_ONLY); st.execute("CLOSE wkb_cursor_prov"); //
		 * st.close(); } catch (SQLException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); } bCursorActivo = false; }
		 */

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDatabaseDriver#getFeatureIterator(java.awt.geom.Rectangle2D,
	 *      java.lang.String, java.lang.String[])
	 */
	public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG,
			String[] alphaNumericFieldsNeeded) throws ReadDriverException {
		String sqlAux = null;
		DBLayerDefinition lyrDef = getLyrDef();
		DBLayerDefinition clonedLyrDef = cloneLyrDef(lyrDef);
		ArrayList<FieldDescription> myFieldsDesc = new ArrayList<FieldDescription>(); // = new FieldDescription[alphaNumericFieldsNeeded.length+1];
		try {
			if (workingArea != null)
				r = r.createIntersection(workingArea);
			// if (getLyrDef()==null){
			// load();
			// throw new DriverException("Fallo de la conexi�n");
			// }
			String strAux = getGeometryField(lyrDef.getFieldGeometry());

			boolean found = false;
			int fieldIndex = -1;
			if (alphaNumericFieldsNeeded != null) {
				FieldDescription[] fieldsDesc = lyrDef.getFieldsDesc();

				for (int i = 0; i < alphaNumericFieldsNeeded.length; i++) {
					fieldIndex =lyrDef.getFieldIdByName(alphaNumericFieldsNeeded[i]);
					if (fieldIndex < 0){
						throw new RuntimeException("No se ha encontrado el nombre de campo " + metaData.getColumnName(i));
					}
					strAux = strAux + ", " + PostGIS.escapeFieldName(lyrDef.getFieldNames()[fieldIndex]);
					if (alphaNumericFieldsNeeded[i].equalsIgnoreCase(lyrDef.getFieldID())){
						found = true;
						clonedLyrDef.setIdFieldID(i);
					}

					myFieldsDesc.add(fieldsDesc[fieldIndex]);
				}
			}
			// Nos aseguramos de pedir siempre el campo ID
			if (found == false){
				strAux = strAux + ", " + lyrDef.getFieldID();
				myFieldsDesc.add(lyrDef.getFieldsDesc()[lyrDef.getIdField(
						lyrDef.getFieldID())]);
				clonedLyrDef.setIdFieldID(myFieldsDesc.size()-1);
			}
			clonedLyrDef.setFieldsDesc( (FieldDescription[])myFieldsDesc.toArray(new FieldDescription[]{}) );

			String sqlProv = "SELECT " + strAux + " FROM "
					+ lyrDef.getComposedTableName() + " ";
					// + getLyrDef().getWhereClause();

			if (canReproject(strEPSG)) {
				sqlAux = sqlProv + getCompoundWhere(sqlProv, r, strEPSG);
			} else {
				sqlAux = sqlProv + getCompoundWhere(sqlProv, r, originalEPSG);
			}

			System.out.println("SqlAux getFeatureIterator = " + sqlAux);
			PostGisFeatureIterator geomIterator = null;
			geomIterator = myGetFeatureIterator(sqlAux);
			geomIterator.setLyrDef(clonedLyrDef);
			return geomIterator;
		} catch (Exception e) {
//			e.printStackTrace();
//			SqlDriveExceptionType type = new SqlDriveExceptionType();
//            type.setDriverName("PostGIS Driver");
//            type.setSql(sqlAux);
//            type.setLayerName(getTableName());
//            type.setSchema(null);
            throw new ReadDriverException("PostGIS Driver",e);

//			throw new DriverException(e);
		}
	}

	/* public void preProcess() throws EditionException {
		writer.preProcess();
	}

	public void process(IRowEdited row) throws EditionException {
		writer.process(row);
	}

	public void postProcess() throws EditionException {
		writer.postProcess();
	}

	public String getCapability(String capability) {
		return writer.getCapability(capability);
	}

	public void setCapabilities(Properties capabilities) {
		writer.setCapabilities(capabilities);
	}

	public boolean canWriteAttribute(int sqlType) {
		return writer.canWriteAttribute(sqlType);
	}

	public boolean canWriteGeometry(int gvSIGgeometryType) {
		return writer.canWriteGeometry(gvSIGgeometryType);
	}

	public void initialize(ITableDefinition layerDef) throws EditionException {
		writer.setCreateTable(false);
		writer.setWriteAll(false);
		// Obtenemos el DBLayerDefinition a partir del driver

		DBLayerDefinition dbLyrDef = getLyrDef();


		writer.initialize(dbLyrDef);
	}
*/
	public boolean isWritable() {
		// CHANGE FROM CARTOLAB
		// return true;
		return writer.canSaveEdits();
		// END CHANGE CARTOLAB
	}

	public IWriter getWriter() {
		return writer;
	}

	public String[] getGeometryFieldsCandidates(IConnection conn, String table_name) throws DBException {
		ArrayList list = new ArrayList();
		try{
		Statement stAux = ((ConnectionJDBC)conn).getConnection().createStatement();
 		String[] tokens = table_name.split("\\u002E", 2);
        String sql;
        if (tokens.length > 1)
        {
        	sql = "select * from GEOMETRY_COLUMNS WHERE F_TABLE_SCHEMA = '"
                 + tokens[0] + "' AND F_TABLE_NAME = '" +
            tokens[1] + "'";
        }
        else
        {
        	sql = "select * from GEOMETRY_COLUMNS" +
            " where F_TABLE_SCHEMA = current_schema() AND F_TABLE_NAME = '" + table_name + "'";

        }

//		String sql = "SELECT * FROM GEOMETRY_COLUMNS WHERE F_TABLE_NAME = '"
//				+ table_name + "'";

		ResultSet rs = stAux.executeQuery(sql);
		while (rs.next())
		{
			String geomCol = rs.getString("F_GEOMETRY_COLUMN");
			list.add(geomCol);
		}
		rs.close(); stAux.close();
		} catch (SQLException e) {
			throw new DBException(e);
		}
		return (String[]) list.toArray(new String[0]);
	}
//	public String[] getTableFields(IConnection conex, String table) throws DBException {
//		try{
//		Statement st = ((ConnectionJDBC)conex).getConnection().createStatement();
//        // ResultSet rs = dbmd.getTables(catalog, null, dbLayerDefinition.getTable(), null);
//		ResultSet rs = st.executeQuery("select * from " + table + " LIMIT 1");
//		ResultSetMetaData rsmd = rs.getMetaData();
//
//		String[] ret = new String[rsmd.getColumnCount()];
//
//		for (int i = 0; i < ret.length; i++) {
//			ret[i] = rsmd.getColumnName(i+1);
//		}
//
//		return ret;
//		}catch (SQLException e) {
//			throw new DBException(e);
//		}
//	}

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

	public String getTotalFields() {
		StringBuilder strAux = new StringBuilder();
        strAux.append(getGeometryField(getLyrDef().getFieldGeometry()));
        String[] fieldNames = getLyrDef().getFieldNames();
        for (int i=0; i< fieldNames.length; i++)
        {
            strAux.append(", " + PostGIS.escapeFieldName(fieldNames[i]));
        }
        return strAux.toString();
	}

}