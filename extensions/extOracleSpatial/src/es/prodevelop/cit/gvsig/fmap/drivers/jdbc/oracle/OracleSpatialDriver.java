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

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
import java.util.TreeMap;

import oracle.sql.ARRAY;
import oracle.sql.Datum;
import oracle.sql.NUMBER;
import oracle.sql.ROWID;
import oracle.sql.STRUCT;
import oracle.sql.TIMESTAMP;

import org.apache.log4j.Logger;
import org.cresques.cts.ICoordTrans;
import org.cresques.cts.IProjection;

import com.hardcode.driverManager.IDelayedDriver;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.DoubleValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FGeometry;
import com.iver.cit.gvsig.fmap.core.FGeometryCollection;
import com.iver.cit.gvsig.fmap.core.FNullGeometry;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.ICanReproject;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.drivers.ConnectionJDBC;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.DefaultJDBCDriver;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.db.utils.ConnectionWithParams;
import com.iver.cit.gvsig.fmap.drivers.db.utils.SingleVectorialDBConnectionManager;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.utiles.NumberUtilities;
import com.iver.utiles.XMLEntity;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;


/**
 * Vectorial driver to access Oracle databases geometries
 * Should work on Oracle Locator.
 *
 * It contains switches to test different modules to perform the
 * translation oracle structs --> gvsig geometries:
 *
 * - Parsing the structs directly.
 * - Using Oracle's JGeometry static methods
 * - Using Geotools utilities
 *
 *  (currently, the driver parses the structs directly)
 *
 * @author jldominguez
 *
 */
public class OracleSpatialDriver extends DefaultJDBCDriver
    implements IDelayedDriver, ICanReproject, IWriteable {
    private static Logger logger = Logger.getLogger(OracleSpatialDriver.class.getName());

    // constants
    public static final int GEODETIC_FULLEXTENT_SAMPLE_SIZE = 50;
    public static final String GEODETIC_SRID = "8307";
    // public static final String ASSUMED_ORACLE_SRID = "8307";
    
    // -----------------------------------------------
    public static final String NAME = "Oracle Spatial Database Driver";
    public static final int ID_COLUMN_INDEX = 1;
    
    public static final String ALL_ORACLE_GEOMETADATA_VIEW = "ALL_SDO_GEOM_METADATA";
    public static final String USER_ORACLE_GEOMETADATA_VIEW = "USER_SDO_GEOM_METADATA";
    
    public static final String ORACLE_EPSG_TABLE_NAME = "ORA_EPSG";
    public static final String ORACLE_EPSG_FILE_NAME = "ORA_EPSG.DBF";
    public static final String DEFAULT_GEO_FIELD = "GEOMETRY";
    // public static final String DEFAULT_GEO_FIELD = "MERGEDGEOMETRY";

    public static final String ORACLE_ID_FIELD = "ROWID";
    public static final String DEFAULT_ID_FIELD_CASE_SENSITIVE = "GID";
    public static final String ORACLE_GEO_SCHEMA = "MDSYS";
    public static final String CONN_STR_BEGIN = "jdbc:oracle:thin:";
    
    // public static final int VARCHAR2_DEFAULT_SIZE = 2000; //  512;  
    public static final int VARCHAR2_MAX_SIZE = 4000;
    
    public static final int MAX_ID_LENGTH = 30;
    private final static GeometryFactory geomFactory = new GeometryFactory();
    public static final double IRRELEVANT_DISTANCE = 0.00000001;
	private static final long ID_MIN_DELAY = 1000;

	public static final String ORACLE_JAR_FILE_NAME = "oracle.jdbc.driver.OracleDriver";

	
    static {
        try {
            Class.forName(ORACLE_JAR_FILE_NAME);
    		logger.info("*-----------------------------------------------------------------------*");
    		logger.info("* Found the Oracle JDBC library! :)                                     *");
    		logger.info("*-----------------------------------------------------------------------*");
            
        } catch (ClassNotFoundException e) {
       		logger.warn("*-----------------------------------------------------------------------*");
       		logger.warn("* Oracle JDBC library (ojdbc*.jar) not found. You need to copy that");
       		logger.warn("* jar file to gvSIG's main lib folder if you intend to access");
       		logger.warn("* Oracle Spatial/Locator databases.");
       		logger.warn("* Read gvSIG manual (Oracle driver section) for details.");
       		logger.warn("*-----------------------------------------------------------------------*");
        }
    }

    private OracleSpatialWriter writer = null;

    // switch variable
    private boolean use_geotools = false;
    private boolean tableHasSrid = true;

    // ------------------------------------------------
    private boolean isNotAvailableYet = true;
    private Value nullVal = ValueFactory.createNullValue();
    private IdLoaderThread idLoader;
    private DriverAttributes drvAtts;
    private int[] pkOneBasedIndexes;
    private String[] fieldNames;
    private String not_restricted_sql = "";

    private Rectangle2D workingAreaInViewsCS = null;
    private Rectangle2D workingAreaInTablesCS = null;
    private STRUCT workingAreaInTablesCSStruct = null;

    private String idFieldNames;
    private int oneBasedGeoColInd = 0;
    private int shapeType = -1;
    private boolean needsCollectionLayer = true;

    // ----------------------------------------------
    // one feature is cached to avoid querying for each attribute request:
    private IFeature singleCachedFeature = null;
    private long singleCachedFeatureRowNum = -1;

    // ----------------------------------------------
    private boolean cancelIDLoad = false;

    // ----------------------------------------------
    private String fullTableName = "";
    private String geoColName = "";
    private String oracleSRID;
    private String epsgSRID;
    private String destProj = "";
    private Rectangle2D full_Extent = null;
    private boolean emptyWhereClause = true;
    private boolean isGeogCS = false;
    private boolean hasRealiableExtent = true;

    // new hash map to perform queries by row number:
    private HashMap rowToId = new HashMap();
	private String destProjOracle;
	private boolean isDestGeogCS = false;

	private int adaptedFetchSize = 100;
    // private static int FETCH_SIZE = 15000;


    public OracleSpatialDriver() {
        drvAtts = new DriverAttributes();
        drvAtts.setLoadedInMemory(false);
    }

	public String getWhereClause() {
	    return lyrDef.getWhereClause();
	}


    /**
     * This method is called when the user creates a new oracle
     * table from a vectorial layer
     *
     * @param params this array simply contains the parameters <tt>Connection</tt> and
     * <tt>DBLayerDefinition</tt>
     */
    public void setData(Object[] params) {
        setData((IConnection) params[0], (DBLayerDefinition) params[1]);
    }

    private void adjustLyrDef() throws SQLException {
        DBLayerDefinition ldef = getLyrDef();
        int cnt = metaData.getColumnCount();

        FieldDescription[] _new = new FieldDescription[cnt];

        for (int i = 0; i < cnt; i++) {
            _new[i] = new FieldDescription();
            _new[i].setFieldName(metaData.getColumnName(i + 1));
            _new[i].setDefaultValue(ValueFactory.createNullValue());
            _new[i].setFieldAlias(_new[i].getFieldName());
            _new[i].setFieldLength(getFieldWidth(i));

            int _type = metaData.getColumnType(i + 1);
            _new[i].setFieldType(_type);

            if ((_type == Types.FLOAT) || (_type == Types.DOUBLE) ||
                    (_type == Types.DECIMAL) || (_type == Types.REAL)) {
                _new[i].setFieldDecimalCount(6);
            }
            else {
                _new[i].setFieldDecimalCount(0);
            }
        }

        ldef.setFieldsDesc(_new);
        setLyrDef(ldef);
    }

    /**
     * Standard initializing method.
     */
    public void setData(IConnection _conn, DBLayerDefinition lyrDef) {
        conn = _conn;
        
        // This metadata is required to store layer in GVP
        // without problems:
        ConnectionWithParams _cwp =
        	SingleVectorialDBConnectionManager.instance().findConnection(conn);
		host = _cwp.getHost();
		port = _cwp.getPort();
		dbName = _cwp.getDb();
		connName = _cwp.getName();

		try {
			if (conn.isClosed()) {
				SingleVectorialDBConnectionManager.instance().closeAndRemove(_cwp);
				_cwp = SingleVectorialDBConnectionManager.instance().getConnection(
						_cwp.getDrvName(),
						_cwp.getUser(),
						_cwp.getPw(),
						_cwp.getName(),
						_cwp.getHost(),
						_cwp.getPort(),
						_cwp.getDb(), true);
			}
		} catch (DBException e1) {
			logger.error("While trying to reconnect: " + e1.getMessage());
			logger.error("Layer will not be reloaded!");
		}
		
        // ------------------

		OracleSpatialUtils.setUpperCase(lyrDef);
        lyrDef.setConnection(conn);

        String geo_can[];
		try {
			geo_can = getGeometryFieldsCandidates(conn, lyrDef.getTableName());
			OracleSpatialUtils.removeStructFields(lyrDef, geo_can);
		} catch (DBException e) {
			logger.error("While removing STRUCT fields: " + e.getMessage());
		}
        
        setLyrDef(lyrDef);

        geoColName = lyrDef.getFieldGeometry();
        
        String tn = lyrDef.getTableName();
        
        if (tn.indexOf(".") == -1) {
        	
        	if (lyrDef.getSchema() == null) {
        		fullTableName = _cwp.getUser().toUpperCase() + "." + tn;
        	} else {
        		fullTableName = lyrDef.getSchema() + "." + tn;
        	}
        	
        	
        } else {
        	fullTableName = tn;
        }
        
        not_restricted_sql = "select " + getStandardSelectExpression() +
            " from " + getTableName() + " c ";

        // various metadata settings
        // getMetaDataInThisThread();
        cleanWhereClause();
        loadSdoMetadata();
        oneRowMetadata();

        setDestProjection(lyrDef.getSRID_EPSG());
        workingAreaInViewsCS = lyrDef.getWorkingArea();
        
        if ((workingAreaInViewsCS != null) && (epsgSRID != null)) {
            IProjection viewProj = CRSFactory.getCRS("EPSG:" + destProj);
            IProjection tableProj = CRSFactory.getCRS("EPSG:" + epsgSRID);
            ICoordTrans reprojecter = viewProj.getCT(tableProj);
        	workingAreaInTablesCS = reprojecter.convert(workingAreaInViewsCS);
            workingAreaInTablesCSStruct = shapeToStruct(workingAreaInTablesCS,
                    FShape.NULL, tableHasSrid, false, true);
        }

        cancelIDLoad = false;
        idLoader = new IdLoaderThread(this);
        idLoader.start();
    }




	/**
     * Utility method to load IDs in a different thred, so that gvsig's gui
     * does not get blocked.
     *
     */
    public void getMetaDataInThisThread() {
        getMetadata();
    }

    private void getMetadata() {

    	long id_load_start = System.currentTimeMillis();
        setIdRowTable();
        long id_load_end = System.currentTimeMillis();

        long delay = id_load_end - id_load_start;
        if (delay < ID_MIN_DELAY) {
        	logger.info("Ids thread delayed by: " + (ID_MIN_DELAY - delay) + " ms.");
        	try {
				Thread.sleep(ID_MIN_DELAY - delay);
			} catch (InterruptedException e) {
				logger.error("While delaying ids thread: " + e.getMessage());
			}
        }

        if (!hasRealiableExtent) {
        	full_Extent = getEstimatedExtent(
        			getTableName(), geoColName, conn, 20, 1.5, isGeogCS);
        }

        if (cancelIDLoad) {
            return;
        }
    }

    private String getOracleSridFromCurrentRecord(ResultSet _rs)
        throws SQLException {
        Object obj = _rs.getObject("SRID");

        if (obj == null) {
            logger.warn("No SRID found for this table.");
            tableHasSrid = false;

            return null;
        }

        return obj.toString();
    }

    private Rectangle2D getFullExtentFromCurrentRecord(ResultSet _rs)
        throws SQLException {
        ARRAY dim_info_array = (ARRAY) _rs.getObject("DIMINFO");

        if (dim_info_array == null) {
            // no full extent found:
            return null;
        }
        else {
            Datum[] da = dim_info_array.getOracleArray();

            STRUCT sx = (STRUCT) da[0];
            STRUCT sy = (STRUCT) da[1];

            try {
                double minx = Double.parseDouble(sx.getAttributes()[1].toString());
                double maxx = Double.parseDouble(sx.getAttributes()[2].toString());
                double miny = Double.parseDouble(sy.getAttributes()[1].toString());
                double maxy = Double.parseDouble(sy.getAttributes()[2].toString());

                if (minx > maxx) {
                    double aux = minx;
                    minx = maxx;
                    maxx = aux;
                }

                if (miny > maxy) {
                    double aux = miny;
                    miny = maxy;
                    maxy = aux;
                }

                return getRectangle(minx, maxx, miny, maxy);

                // fullExtentJTS = shapeToGeometry(fullExtent);
            }
            catch (Exception ex) {
            	logger.error("While getting full extent from metadata table.");
                return null;
            }
        }
    }

    private void loadSdoMetadata() {
        try {
            Statement _st = ((ConnectionJDBC)conn).getConnection().createStatement();
            String[] tokens = getTableName().split("\\u002E", 2);
            String qry;
            if (tokens.length > 1)
            {
            	qry = "select * from " + ALL_ORACLE_GEOMETADATA_VIEW +
                " where OWNER = '" + tokens[0] + "' AND TABLE_NAME = '" +
                tokens[1] + "' AND COLUMN_NAME = '" + geoColName + "'";
            }
            else
            {
            	qry = "select * from " + ALL_ORACLE_GEOMETADATA_VIEW +
                " where TABLE_NAME = " + "'" + getTableName()
                + "' AND COLUMN_NAME = '"
                + geoColName + "'";

            }

            ResultSet _rs = _st.executeQuery(qry);

            if (_rs.next()) {
                oracleSRID = getOracleSridFromCurrentRecord(_rs);

                isGeogCS = OracleSpatialUtils.getIsGCS(oracleSRID, tableHasSrid);

                try {
					epsgSRID = oracleSridToEpsgSrid(oracleSRID);
				} catch (Exception e) {
					logger.error("Unknown oracle SRID: " + oracleSRID);
					tableHasSrid = false;
				}
                full_Extent = getFullExtentFromCurrentRecord(_rs);

                hasRealiableExtent = realiableExtent(full_Extent, isGeogCS);

                if (!hasRealiableExtent) {
                	full_Extent = getFastEstimatedExtent(
                    			getTableName(), geoColName, conn, 20, 10, isGeogCS);
                }

                _rs.close();
                _st.close();
            }
            else {
                throw new SQLException("Empty resultset from this query: " +
                    qry);
            }
        }
        catch (SQLException se) {
        	logger.error("While getting SDO metadata: " + se.getMessage());
        }
    }

    /**
     * Utility method to get a geometry from a struct.
     *
     * @param theStruct the struct to be converted
     * @param use_gtools switch to use geotools classes or not
     * @return the geometry
     * @throws SQLException
     */
    public IGeometry getGeometryUsing(STRUCT theStruct, boolean use_gtools)
        throws SQLException {
        IGeometry _igeom = null;

        if (theStruct == null) {
            return OracleSpatialUtils.NULL_GEOM;
        }

        if (use_gtools) { // geotools
//            _igeom = getGeotoolsIGeometry(theStruct);
        }
        else { // jgeometry
        	// lastGeom = printStruct(theStruct.getOracleAttributes());
            _igeom = getFMapGeometry(theStruct, false, 0);
        }

        return _igeom;
    }
    
    // private static String lastGeom = "";
    
    /**
     * Utility method to transform a struct into a IGeometry.
     *
     * @param st the struct to be converted
     * @param complex comes from a complex sdo geometry
     * @return the IGeometry
     */
    private IGeometry getFMapGeometry(STRUCT st, boolean complex, int rec_iter) {

    	if (st == null) {
    		return new FNullGeometry();
    	}
    	
        Datum[] the_data = null;

        
        
        try {
            the_data = st.getOracleAttributes();
            
            /*
        	if (rec_iter > 4) {
        		logger.error(lastGeom);
        		return new FNullGeometry();
        	}
        	*/

            int full_gtype = ((NUMBER) the_data[0]).intValue();
            // int jgtype = ((NUMBER) the_data[0]).intValue() % 1000;
            int fshape_type = OracleSpatialUtils.oracleGTypeToFShapeType(full_gtype, complex);

            int dim = ((NUMBER) the_data[0]).intValue() / 1000;

            if (dim < 2) {
                dim = 2;
            }

            IGeometry ig = null;
            // ========================== collection issue with rects
            int collec_val = OracleSpatialUtils.isCollection(the_data);
            switch (collec_val) {
            case OracleSpatialUtils.COLLECTION_VALUE_YES_COLLECTION:
            	fshape_type = FShape.MULTI;
            	break;
            case OracleSpatialUtils.COLLECTION_VALUE_MERGE_COLLECTION_IN_POLYGON:
            	fshape_type = FShape.MULTI;
            	break;
            case OracleSpatialUtils.COLLECTION_VALUE_NOT_COLLECTION:
            	break;
            }
            // ========================================================

            switch (fshape_type) {
            case FShape.MULTI:
                ig = getFMapGeometryCollection(the_data, dim, rec_iter);
            	if (collec_val == OracleSpatialUtils.COLLECTION_VALUE_MERGE_COLLECTION_IN_POLYGON) {
            		ig = OracleSpatialUtils.mergePolygons((FGeometryCollection) ig);
            	}
                break;

            case FShape.POINT:
                ig = OracleSpatialUtils.getFMapGeometryPoint(the_data, dim);

                break;

            case FShape.LINE:
                ig = OracleSpatialUtils.getFMapGeometryMultiLineString(the_data, dim);

                break;

            case FShape.POLYGON:
                ig = OracleSpatialUtils.getFMapGeometryMultipolygon(the_data, dim);

                break;
            }

            return ig;
        }
        catch (Exception e) {
            logger.error(e);
        }

        return new FNullGeometry();
    }


	private static String printStruct(Datum[] datt) {
    	
    	String resp = "\n";
    	resp = resp + "============= START === PRINTING STRUCT:\n";
    	int sz = datt.length;
    	for (int i=0; i<sz; i++) {
    		resp = resp + printDatum(i, datt[i]);
    	}
    	resp = resp + "============= END === PRINTING STRUCT\n";
    	return resp;
	}
    
    

	private static String printDatum(int n, Datum d) {
		
		String resp = "";
		if (d == null) {
			resp = "============= DATUM: " + n + " ; VALUE = NULL\n";
			return resp;
		}
		
		if (d instanceof ARRAY) {
			ARRAY arr = (ARRAY) d;
			double[] darr = null;
			int[] iarr = null;
			try {
				
				resp = "============= DATUM: " + n + " ; VALUE:\n";
				darr = arr.getDoubleArray();
				// String valstr = "";
				int sz = darr.length;
				for (int i=0; i<sz; i++) {
					resp = resp + darr[i] + " , "; 
				}
				resp = resp + "\n"; 
				
			} catch (SQLException e) {
				try {
					resp = "============= DATUM: " + n + " ; VALUE:\n";
					iarr = arr.getIntArray();
					// String valstr = "";
					int sz = iarr.length;
					for (int i=0; i<sz; i++) {
						resp = resp + iarr[i] + " , "; 
					}
					resp = resp + "\n"; 
				} catch (SQLException e2) {
					resp = "============= DATUM: " + n + " ; VALUE = " + d.toString() + " (CAST FAILED)\n";
				}
			}
		} else {
			if (d instanceof NUMBER) {
				int v = -10;
				try {
					v = ((NUMBER) d).intValue();
					resp = "============= DATUM: " + n + " ; VALUE = " + v + "\n";
				} catch (SQLException e) {
					resp = "============= DATUM: " + n + " ; VALUE = " + d.toString() + " (CAST TO INT FAILED)\n";
				}
			} else {
				resp = "============= DATUM: " + n + " ; VALUE = " + d.toString() + " (NOT NUMBER)\n";
			}
		}
		return resp;
		
	}

	private Rectangle2D getRectangle(double minx, double maxx, double miny,
        double maxy) {
        Rectangle2D resp = new Rectangle2D.Double(minx, miny, maxx - minx,
                maxy - miny);
        return resp;
    }

    private void oneRowMetadata() {
        try {

            Statement st = ((ConnectionJDBC)conn).getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            
            ResultSet _rs = null;
            shapeType = guessShapeType();
            // -----------------------
            _rs = st.executeQuery(not_restricted_sql + " where (rownum = 1)");
            metaData = _rs.getMetaData(); 
            
            this.setAdaptedFetchSize(OracleSpatialUtils.estimateGoodFetchSize(metaData));
            
            userName = ((ConnectionJDBC)conn).getConnection().getMetaData().getUserName();

            // geoColInd = _rs.findColumn(geoColName);
            oneBasedGeoColInd = metaData.getColumnCount() + 1;

            DatabaseMetaData dbmd = ((ConnectionJDBC)conn).getConnection().getMetaData();
            pkOneBasedIndexes = getPKIndexes(dbmd, _rs);

            int cnt = metaData.getColumnCount();
            fieldNames = new String[cnt];

            for (int i = 0; i < cnt; i++) {
                fieldNames[i] = metaData.getColumnName(i + 1);
            }

            getIdFieldNames();
            adjustLyrDef();
            _rs.close(); // st no debe cerrarse ya que las llamadas a metadata lo encesitan
        }
        catch (SQLException se) {
            logger.error("While getting metadata. " + se.getMessage());
        }
    }

    private int guessShapeType() {

    	int resp = FShape.MULTI;

        String _sql = "select " + getStandardSelectExpression() + ", c." +
        geoColName + " from " + getTableName() + " c ";

        ResultSet _rs = null;
        STRUCT sample_geo = null;
        
        try {
        	_sql = _sql + " where c." + geoColName + " is not NULL AND "
    		+ OracleSpatialUtils.EXPONENTIAL_INDICES_CONDITION;
        	
            Statement st = ((ConnectionJDBC)conn).getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

            _rs = st.executeQuery(_sql);

            int aux = 0;
            ArrayList shptypes = new ArrayList();
            while (_rs.next()) {
                sample_geo = (STRUCT) _rs.getObject(geoColName);
                aux = OracleSpatialUtils.getShapeTypeOfStruct(sample_geo);
                shptypes.add(new Integer(aux));
            }
            if (shptypes.size() > 0) {
            	resp = getShapeTypeFromArray(shptypes);
            }
        } catch (Exception ex) {
        	logger.error("While guessing shape type: " + ex.getMessage());
        	logger.warn("Assumed MULTI");
        }
        
        return resp;
	}

	private int getShapeTypeFromArray(ArrayList arrlist) {
		
		int resp = ((Integer) arrlist.get(0)).intValue();
		
		int sz = arrlist.size();
		int aux = 0;
		for (int i=1; i<sz; i++) {
			aux = ((Integer) arrlist.get(i)).intValue();
			if (aux != resp) return FShape.MULTI;
		}
		return resp;
	}

	private String getIdFieldNames() {
        try {
            idFieldNames = "";

            for (int i = 0; i < pkOneBasedIndexes.length; i++) {
                idFieldNames = idFieldNames +
                    metaData.getColumnName(pkOneBasedIndexes[i]) + ", ";
            }
        }
        catch (SQLException se) {
        }

        idFieldNames = idFieldNames.substring(0, idFieldNames.length() - 2);

        return idFieldNames;
    }

    private int[] getPKIndexes(DatabaseMetaData metaData, ResultSet table_rs) {
        int[] _res = new int[1];
        _res[0] = 1;

        return _res;
    }

    public String getSqlTotal() {
        // TODO Auto-generated method stub
        return "";
    }

    public String getCompleteWhere() {
        // TODO Auto-generated method stub
        return "";
    }

    /**
     * Gets the feature iterator for a given SQL sentence (ignores previous filters
     * and uses directly that sentence to query the table).
     */
    public IFeatureIterator getFeatureIterator(String sql)
        throws ReadDriverException {
        if (isNotAvailableYet) {
        	return new AnEmptyFeatureIterator();
            // return null;
        }

        singleCachedFeatureRowNum = -1;

        Object[] rs_st = getViewResultSet(null, sql, tableHasSrid);

        ResultSet localrs = (ResultSet) rs_st[0];
        Statement _st = (Statement) rs_st[1];

        return new OracleSpatialFeatureIterator(this, localrs, _st,
            oneBasedGeoColInd, use_geotools, false, null);
    }

    /**
     * Gets Oracle particular connection string beginning: "jdbc:oracle:thin:"
     */
    public String getConnectionStringBeginning() {
        // oracle
        return CONN_STR_BEGIN;
    }

    public void open() { // throws DriverException {
    }

    /**
     * Gets Oracle's default port: 1521
     */
    public int getDefaultPort() {
        // oracle port
        return 1521;
    }

    /**
     * Gets the feature iterator for a given rectangle (the view's bounding box)
     * and a SRS.
     */
    public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG)
        throws ReadDriverException {
        if (isNotAvailableYet) {
        	return new AnEmptyFeatureIterator();
            // return null;
        }

        singleCachedFeatureRowNum = -1;
        
        STRUCT local_st = shapeToStruct(r, FShape.NULL, tableHasSrid, false, true);
        Object[] rs_st = getViewResultSet(local_st, null, tableHasSrid);
        ResultSet localrs = (ResultSet) rs_st[0];
        Statement _st = (Statement) rs_st[1];

        return new OracleSpatialFeatureIterator(this, localrs, _st,
            oneBasedGeoColInd, use_geotools, false, null);
    }

    private Rectangle2D intersectWithWorkingArea(Rectangle2D r) {
        if (workingAreaInTablesCS == null) return r;
        return OracleSpatialUtils.doIntersect(r, workingAreaInTablesCS);
    }

    /**
     * This method reverts to the one without the fields specification.
     * The fields have been selected from the start.
     */
    public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG,
        String[] alphaNumericFieldsNeeded) throws ReadDriverException {
    	
        if (isNotAvailableYet) {
        	return new AnEmptyFeatureIterator();
            // return null;
        }
        singleCachedFeatureRowNum = -1;

        if ((alphaNumericFieldsNeeded == null) || (alphaNumericFieldsNeeded.length == 0)) {
            return getFeatureIterator(r, strEPSG);
        } else {

            STRUCT local_st = shapeToStruct(r, FShape.NULL, tableHasSrid, false, true);
            Object[] rs_st = getViewResultSet(local_st, null, tableHasSrid);
            ResultSet localrs = (ResultSet) rs_st[0];
            Statement _st = (Statement) rs_st[1];

            return new OracleSpatialFeatureIterator(this, localrs, _st,
                oneBasedGeoColInd, use_geotools, true, alphaNumericFieldsNeeded);
        }
    }

    private String getSqlFor(String[] alphaNumericFieldsNeeded, String sdo_inter) {
    	
    	String idswhere = getIdsQueryWhereClause(false);
        String custom_sel = getCustomSelect(alphaNumericFieldsNeeded, sdo_inter, idswhere);
		return custom_sel;
	}

	public String getGeometryField(String fieldName) {
        return fieldName;
    }

    public DriverAttributes getDriverAttributes() {
        return drvAtts;
    }

    /**
     * Gets the requested geometry. Always performs a new query in this case.
     * This should be a rare way to get the geometries. The standard way is by using
     * the iterators.
     */
    public IGeometry getShape(int _ind) throws ReadDriverException {
        if (isNotAvailableYet) {
            return OracleSpatialUtils.NULL_GEOM;
        }

        ROWID r_id = (ROWID) rowToId.get(new Integer(_ind));

        String _sql = "select " + geoColName + " from " + getTableName() +
            " where rowid = ?";

        try {
            java.sql.PreparedStatement ps = ((ConnectionJDBC)conn).getConnection().prepareStatement(_sql);
            ps.setObject(1, r_id);

            // Statement stmnt = conn.createStatement();
            ps.execute();

            ResultSet _res = ps.getResultSet();

            if (_res.next()) {
                STRUCT _st = (oracle.sql.STRUCT) _res.getObject(1); // geocolind);
                IGeometry theGeom = getGeometryUsing(_st, use_geotools);
                _res.close();
                ps.close();

                return theGeom;
            }
            else {
                logger.error("Unable to get shape: " + _ind +
                    " (probably due to edition)");
                

                return OracleSpatialUtils.NULL_GEOM;
            }
        }
        catch (SQLException se) {
            throw new ReadDriverException(getName(), se);
        }
    }

    public boolean isWritable() {
        return true;
    }

    public String getName() {
        return NAME;
    }

    public int[] getPrimaryKeys() throws ReadDriverException {
        return pkOneBasedIndexes;
    }




    private void setIdRowTable() {
        hashRelate = new Hashtable();

        java.sql.PreparedStatement ps = null;

        try {
            String _sql = getIdAndElemInfoFullResulltSetQuery();

            logger.debug("SQL para leer ids: " + _sql);
            Statement st = null;


            st = ((ConnectionJDBC)conn).getConnection().createStatement(
            		ResultSet.TYPE_FORWARD_ONLY,
            		ResultSet.CONCUR_READ_ONLY);
            
            

            // st = ((ConnectionJDBC)conn).getConnection().createStatement();

             st.setFetchSize(getAdaptedFetchSize());
             logger.info("FETCH_SIZE = " + getAdaptedFetchSize());

            ResultSet _r = null;
            _r = st.executeQuery(_sql);

            ROWID ri = null;

            int row = 0;
            String gid;
            Value aux = null;

            // ----------------------------------- types init
            ArrayList types = new ArrayList();
            int types_aux = 0;

            ARRAY info_aux;
            int[] info_aux_int;
            int size;

            // ----------------------------------- types init
            logger.debug("Beginning of result set:");

            while (_r.next()) {
                // ---------------------------------------
                ri = (ROWID) _r.getObject(1);
                gid = ri.stringValue();
                aux = ValueFactory.createValue(gid);

                Integer intobj = new Integer(row);
                hashRelate.put(aux, intobj);
                rowToId.put(intobj, ri);

                if ((row % 5000) == 0) {
                    // ------------------------------------------- cancel load
                    if (cancelIDLoad) {
                        hashRelate.clear();
                        rowToId.clear();

                        return;
                    }

                    // -------------------------------------------
                    String fmt = OracleSpatialUtils.getFormattedInteger(row);
                    logger.info("IDs read: " + fmt);
                }

                row++;

                // --------------------------------------- types
                info_aux = (ARRAY) _r.getObject(2);

                if (info_aux == null) {
                    // logger.debug("NULL info array found in record: " + row);
                }
                else {
                    info_aux_int = info_aux.getIntArray();
                    size = info_aux_int.length / 3;

                    for (int i = 0; i < size; i++) {
                        types_aux = info_aux_int[(3 * i) + 1];
                        types.add(new Integer(types_aux % 1000));
                    }
                }

                // --------------------------------------- types end
            }

            _r.close();
//            ps.close();
            st.close();
            numReg = row;

            needsCollectionLayer = OracleSpatialUtils.hasSeveralGeometryTypes(types, false);

            if (needsCollectionLayer) {
                shapeType = FShape.MULTI;
            }
        }
        catch (SQLException e) {
        	logger.error("While setting id-row hashmap: " +
                e.getMessage());
        }
    }

    public int getFieldCount() throws ReadDriverException {
        try {
            return metaData.getColumnCount();
        }
        catch (SQLException e) {
        	logger.error("While getting field count: " + e.getMessage());
            throw new ReadDriverException(getName(), e);
        }
    }

    public String[] getFieldNames() {
        return fieldNames;
    }

    public String getTotalFields() {
        String strAux = "";

        for (int i = 0; i < fieldNames.length; i++) {
            if (i == 0) {
                strAux = fieldNames[i];
            }
            else {
                strAux = strAux + ", " + fieldNames[i];
            }
        }

        return strAux;
    }

    public int getFieldType(int idField) throws ReadDriverException {
        int i = 0;

        try {
            i = idField + 1; // idField viene basado en 0

            int __type = metaData.getColumnType(i);
            
            int size = 1;
            // avoid exception
            try { size = metaData.getPrecision(i); } catch (Exception ex) { }
            
            int dec_pos = metaData.getScale(i);

            // we must add this entry because we did not remove the 'geometry' column
            if (__type == Types.STRUCT) {
                return Types.VARCHAR; // .STRUCT;
                // ----------------------------------------------------------------------
            }

            if (__type == Types.VARCHAR) {
                return Types.VARCHAR;
            }

            if (__type == Types.FLOAT) {
                return Types.FLOAT;
            }

            if (__type == Types.DOUBLE) {
                return Types.DOUBLE;
            }

            if ((__type == Types.INTEGER)
            		|| (__type == Types.SMALLINT)
            		|| (__type == Types.TINYINT)
            		|| (__type == Types.BIGINT)
            		|| ((__type == Types.NUMERIC) && (dec_pos == 0))
            		) {
            	
            	if (size > 10) {
            		return Types.BIGINT;
            	} else {
            		return Types.INTEGER;
            	}
            }

            if (__type == Types.BIT) {
                return Types.BIT;
            }

            if (__type == Types.DATE) {
                return Types.DATE;
            }

            if (__type == Types.DECIMAL) {
                return Types.DOUBLE;
            }

            if (__type == Types.NUMERIC) {
            	return Types.DOUBLE;
            }

            if (__type == Types.DATE) {
                return Types.DATE;
            }

            if (__type == Types.TIME) {
                return Types.TIME;
            }

            if (__type == Types.TIMESTAMP) {
                return Types.TIMESTAMP;
            }
        }
        catch (SQLException e) {
            logger.error("Unknown field type of : " + i);
            throw new ReadDriverException(getName(), e);
        }

        return -1;
    }


    public String getFieldName(int fieldId) throws ReadDriverException {
        return fieldNames[fieldId];
    }

    public int getFieldWidth(int fieldId) {
        int i = -1;

        try {
            int aux = fieldId + 1; // fieldId viene basado en 0
            int _type = metaData.getColumnType(aux);
            if (NumberUtilities.isNumeric(_type)) {
                i = metaData.getPrecision(aux);
            } else {
                i = metaData.getColumnDisplaySize(aux);
            }
        }
        catch (SQLException e) {
            logger.error("While getting field width: " + e.getMessage());
        }

        if (i < 0) {
            i = 255;
        }

        return i;
    }

    public Value getFieldValue(long rowIndex, int field_Id) throws ReadDriverException {
        if (isNotAvailableYet) {
            return nullVal;
        }

        if ((singleCachedFeature != null) &&
                (rowIndex == singleCachedFeatureRowNum)) {
            return singleCachedFeature.getAttributes()[field_Id];
        }

        // return ValueFactory.createNullValue();
        ResultSet _r = null;
        java.sql.PreparedStatement ps = null;

        try {
            String rnq = getSearchId();
            ROWID _id = (ROWID) rowToId.get(new Integer((int) rowIndex));

            ps = ((ConnectionJDBC)conn).getConnection().prepareStatement(rnq);
            ps.setObject(1, _id);

            ps.execute();
            _r = ps.getResultSet();
            
            if (!_r.next()) {
                _r.close();
                ps.close();
            	throw new SQLException("No row for ROWID: " + _id.toString() + ". Possibly deleted from another app.");
            }
            
        } catch (SQLException se) {
        	logger.error("While getting row " + rowIndex + " : " + se.getMessage());
        	return ValueFactory.createNullValue();
        }

        IFeature ife = null;
        Value[] atts = null;

        try {
            ROWID ri = (ROWID) _r.getObject(1);
            atts = getAttributes(_r, true);

            String gid = ri.stringValue();
            STRUCT _st = (oracle.sql.STRUCT) _r.getObject(oneBasedGeoColInd);
            IGeometry theGeom = getGeometryUsing(_st, use_geotools);
            ife = new DefaultFeature(theGeom, atts, gid);
            _r.close();
            ps.close();
        } catch (SQLException se) {
            logger.error("Error while doing next(): " + se.getMessage(), se);
            return ValueFactory.createNullValue();
        }

        // -------------------------------
        singleCachedFeature = ife;
        singleCachedFeatureRowNum = rowIndex;

        // -------------------------------
        if (atts == null) {
            return ValueFactory.createNullValue();
        } else {
            return atts[field_Id];
        }
    }



    public Rectangle2D getFullExtent() {
    	return full_Extent;
    }



    private IGeometry getFMapGeometryCollection(Datum[] the_data, int dim, int reciter) {
        // int __srid) {

    	NUMBER _srid = new NUMBER(0);
        NUMBER main_type = new NUMBER((dim * 1000) +
                OracleSpatialUtils.getStructType(the_data));

        Datum[] all_info_array = null;
        Object[] elems_info_aray = null;
        Datum[] all_ords = null;

        Object[] ords_of_groups = null;
        Object[] _elems_info_aray = null;
        try {
            all_info_array = ((ARRAY) the_data[3]).getOracleArray();
            elems_info_aray = OracleSpatialUtils.groupByElement(all_info_array);
            all_ords = ((ARRAY) the_data[4]).getOracleArray();

            ords_of_groups = OracleSpatialUtils.getOrdOfGroups(all_ords, elems_info_aray);
            _elems_info_aray = new Object[elems_info_aray.length];
        }
        catch (SQLException e) { 
            logger.error("Unexpected error: " + e.getMessage());
        }


        for (int i = 0; i < elems_info_aray.length; i++) {
            _elems_info_aray[i] = OracleSpatialUtils.updateIndexes((Datum[]) elems_info_aray[i]);
        }

        // _elems_info_aray, ords_of_groups
        int no_of_elems = ords_of_groups.length;
        IGeometry[] geoms = new IGeometry[no_of_elems];

        for (int i = 0; i < no_of_elems; i++) {
            Datum[] item_info_array = null;
            Datum[] item_ords = null;
            NUMBER gtype = null;

            try {
                item_info_array = (Datum[]) _elems_info_aray[i];
                item_ords = (Datum[]) ords_of_groups[i];

                gtype = new NUMBER((dim * 1000) +
                        (item_info_array[1].intValue() % 1000));

                if (tableHasSrid) {
                	_srid = new NUMBER(Integer.parseInt(oracleSRID));
                }
            }
            catch (SQLException se) {
                logger.error("Unexpected error: " + se.getMessage());
            }

            // if it's the first geometry, the type is the collection's main type (no?) - no
            // if (i == 0) gtype = main_type;

            STRUCT itemst = null;

            if (tableHasSrid) {

                itemst = OracleSpatialUtils.createStruct(gtype, _srid,
                        item_info_array, item_ords, ((ConnectionJDBC)conn).getConnection());
            }
            else {
                itemst = OracleSpatialUtils.createStruct(gtype, null,
                        item_info_array, item_ords, ((ConnectionJDBC)conn).getConnection());
            }

            geoms[i] = getFMapGeometry(itemst, true, reciter + 1);
        }

        return new FGeometryCollection(geoms);
    }








    private void cleanWhereClause() {
        emptyWhereClause = false;

        String aux = getWhereClauseWithoutWhere();

        for (int i = 0; i < aux.length(); i++)
            if (aux.substring(i, i + 1).compareTo(" ") != 0) {
                return;
            }

        getLyrDef().setWhereClause("");
        emptyWhereClause = true;
    }



    private String getMainSelect(String viewsdo, String idsLoadWhere) {
        String resp = "";

        if (isGeogCS) {
            String vport = "sdo_filter(" + geoColName +
                ", SDO_CS.VIEWPORT_TRANSFORM(" + viewsdo + ", " + oracleSRID +
                "), 'querytype=window') = 'TRUE'";

                resp = "select " + getStandardSelectExpression() + ", c." +
                    geoColName + " from " + getTableName() + " c where ";
                if (idsLoadWhere.length() > 0) {
                	resp = resp + " (" + idsLoadWhere + ") AND ";
                }
                resp = resp + "(" + vport + ")";
        }
        else {
                resp = "select " + getStandardSelectExpression() + ", c." +
                    geoColName + " from " + getTableName() + " c where ";
                if (idsLoadWhere.length() > 0) {
                	resp = resp + " (" + idsLoadWhere + ") AND ";
                }
                resp = resp + "(" + "sdo_relate(" + geoColName + ", " + viewsdo +
                ", 'mask=anyinteract querytype=window') = 'TRUE')";
        }

//        return "select " + getStandardSelectExpression() + ", c." +
//        geoColName + " from " + getTableName() + " c";
        return resp;
    }
    
    private String getCustomSelect(String[] atts, String viewsdo, String idsLoadWhere) {
        String resp = "";
        
        String atts_enum = "";
        if ((atts == null) || (atts.length == 0)) {
        	
        } else {
        	atts_enum = " c.\"" + atts[0] + "\" ";
            for (int i=1; i<atts.length; i++) {
            	atts_enum = atts_enum + ", " + atts[1]; 
            }
        }

        if (isGeogCS) {
            String vport = "sdo_filter(" + geoColName +
                ", SDO_CS.VIEWPORT_TRANSFORM(" + viewsdo + ", " + oracleSRID +
                "), 'querytype=window') = 'TRUE'";

                resp = "select " + atts_enum + " from " + getTableName() + " c where ";
                if (idsLoadWhere.length() > 0) {
                	resp = resp + " (" + idsLoadWhere + ") AND ";
                }
                resp = resp + "(" + vport + ")";
        } else {
                resp = "select " + atts_enum + " from " + getTableName() + " c where ";
                if (idsLoadWhere.length() > 0) {
                	resp = resp + " (" + idsLoadWhere + ") AND ";
                }
                resp = resp + "(" + "sdo_relate(" + geoColName + ", " + viewsdo +
                ", 'mask=anyinteract querytype=window') = 'TRUE')";
        }

//        return "select " + getStandardSelectExpression() + ", c." +
//        geoColName + " from " + getTableName() + " c";
        return resp;
    }    

    public void setWorkingArea(Rectangle2D rect) {
    }

    private void setWAStructt() {
    }



    private int oracleTypeToFShapeTypeExceptPointTypes(int type) {
        /*
         * Tipos en Oracle Spatial usando JGeometry
         *
         * GTYPE_COLLECTION collection geometry type
         * GTYPE_CURVE curve geoemtry type
         * GTYPE_MULTICURVE multi-curve geometry type
         * GTYPE_MULTIPOINT multi-point geometry type
         * GTYPE_MULTIPOLYGON multi-polygon geometry type
         * GTYPE_POINT point geometry type
         * GTYPE_POLYGON  polygon geometry type
         *
         * Tipos gvSIG FShape
         *
         * NULL = 0;
         * POINT = 1;
         * LINE = 2;
         * POLYGON = 4;
         * TEXT = 8;
         * MULTI = 16;
         * MULTIPOINT = 32;
         * CIRCLE = 64;
         * ARC = 128;
         * ELLIPSE=256;
         * Z=512
         */
        switch (type) {
        case JGeometry_GTYPE_POLYGON:
        case JGeometry_GTYPE_MULTIPOLYGON:
            return FShape.POLYGON;

        case JGeometry_GTYPE_CURVE:
        case JGeometry_GTYPE_MULTICURVE:
            return FShape.LINE;
        }

        logger.error("Unhandled Oracle Spatial geometry type: " + type +
            " (conversion returned FShape.NULL)");

        return FShape.NULL;
    }
    
    private String getValidViewConstructor(
    		STRUCT _st,
    		String ora_srid,
    		boolean _hassrid,
    		boolean _isgeocs) {

    	String sdo = getSdoConstructor(_st, _hassrid, _isgeocs);
    	String resp = "";
    	if ((_hassrid) && (_isgeocs)) {
    		resp = "SDO_CS.VIEWPORT_TRANSFORM( " + sdo + " , " + ora_srid + ")";
    	} else {
    		resp = sdo;
    	}

    	return resp;
    }



    private Object[] getViewResultSet(STRUCT geoStruct, String fixsql,
        boolean hasSrid) throws ReadDriverException {
        String sdo_intersect = getSdoConstructor(geoStruct, hasSrid, isGeogCS);
        String main_sel = "";

        if (fixsql == null) {
            // main_sel = getMainSelect3(var_name);
        	String idswhere = getIdsQueryWhereClause(false);
            main_sel = getMainSelect(sdo_intersect, idswhere);
        }
        else {
            main_sel = fixsql;
        }

        logger.debug("MAIN SEL = " + main_sel);

        ResultSet _rs = null;
        Statement _stmnt = null;
        Object[] _resp = new Object[2];

        try {
            _stmnt = ((ConnectionJDBC)conn).getConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
            _stmnt.setFetchDirection(ResultSet.FETCH_FORWARD);
            _stmnt.setFetchSize(getAdaptedFetchSize());

            _rs = _stmnt.executeQuery(main_sel);

            // stmnt.close();
        }
        catch (SQLException se) {
            logger.error("Tablename: " + getTableName() + ". Error while getting main cursor: " + se.getMessage(),
                se);
            throw new ReadDriverException(getName(), se);
        }

        // this method returns the statement too, so that it can be closed afterwards
        _resp[0] = _rs;
        _resp[1] = _stmnt;

        return _resp;
    }

    private String getSdoConstructor(STRUCT geoStruct, boolean hasSrid, boolean _isGeogCS) {
        String resp = "";

        try {
            String mdsys_sdo_elem_info_array = "mdsys.sdo_elem_info_array(1, 1003, 3)";
            String mdsys_sdo_ordinate_array = "";
            Datum[] vertices = ((ARRAY) geoStruct.getOracleAttributes()[4]).getOracleArray();

            for (int i = 0; i < vertices.length; i++) {
                mdsys_sdo_ordinate_array = mdsys_sdo_ordinate_array +
                    vertices[i].doubleValue() + ", ";
            }

            mdsys_sdo_ordinate_array = mdsys_sdo_ordinate_array.substring(0,
                    mdsys_sdo_ordinate_array.length() - 2);
            mdsys_sdo_ordinate_array = "mdsys.sdo_ordinate_array(" +
                mdsys_sdo_ordinate_array + ")";

            String aux = "";

            if (hasSrid) {
                aux = oracleSRID;

                if (_isGeogCS) {
                    aux = "0";
                }
            }
            else {
                aux = "null";
            }

            resp = "mdsys.sdo_geometry(2003, " + aux + ", null, " +
                mdsys_sdo_elem_info_array + ", " + mdsys_sdo_ordinate_array +
                ")";
        }
        catch (Exception ex) {
        	logger.error("While getting sdo contructor: " +
                ex.getMessage());
        }

        return resp;
    }

    private String getIdsQueryWhereClause(boolean with_where) {
		String resp = "";

		String _where = "";
		if (with_where) _where = " where ";

		if (workingAreaInTablesCSStruct == null) {
			if (emptyWhereClause) {
				// return "select rowid from " + getTableName();
			} else {
				resp = resp + _where + "(" + getWhereClauseWithoutWhere()
						+ ")";
			}
		} else {
			String waqry = getValidViewConstructor(workingAreaInTablesCSStruct,
					oracleSRID, tableHasSrid, isGeogCS);

			if (emptyWhereClause) {
				resp = resp + _where + "(sdo_relate(" + geoColName + ", " + waqry
						+ ", 'mask=anyinteract querytype=window') = 'TRUE')";
			} else {
				resp = resp + _where + "((" + getWhereClauseWithoutWhere()
						+ ") AND (" + "sdo_relate(" + geoColName + ", " + waqry
						+ ", 'mask=anyinteract querytype=window') = 'TRUE'))";
			}
		}

		// resp = resp + " order by rowid";
		return resp;
	}

    public String getIdAndElemInfoFullResulltSetQuery() {
        String resp = "select rowid, c." + geoColName + ".SDO_ELEM_INFO from " +
            getTableName() + " c";

        resp = resp + getIdsQueryWhereClause(true);
        return resp;
    }

    private String getSearchId() {
        if (emptyWhereClause) {
            return "select " + getStandardSelectExpression() + ", c." +
            geoColName + " from " + getTableName() + " c where rowid = ?";
        }
        else {
            return "select " + getStandardSelectExpression() + ", c." +
            geoColName + " from " + getTableName() + " c " + " where " + "((" +
            getWhereClauseWithoutWhere() + ") and (rowid = ?))";
        }
    }

    public int getShapeType() {
        return shapeType;
    }

    public void setShapeType(int t) {
        shapeType = t;
    }
    

    private String getWhereClauseWithoutWhere() {
        String resp = "";
        String old = getLyrDef().getWhereClause();
        resp = old;

        if (old.length() <= 6) {
            return old;
        }

        if (old.substring(0, 6).compareToIgnoreCase("where ") == 0) {
            resp = resp.substring(6, resp.length());
        }

        return resp;
    }



    public static String fieldTypeToSqlStringType(FieldDescription fd) {
    	
        String aux = "VARCHAR2(" + VARCHAR2_MAX_SIZE + ")"; // Por defecto.
        int the_type = fd.getFieldType();
        
        int _w = fd.getFieldLength();
        int _dec = 0;
        
        if (NumberUtilities.isNumeric(the_type)) {
        	_dec = fd.getFieldDecimalCount(); 
        }

        switch (the_type) {
        case Types.SMALLINT:
            aux = "NUMBER(" + _w + ", 0)";
            break;

        case Types.INTEGER:
            aux = "NUMBER(" + _w + ", 0)";
            break;

        case Types.BIGINT:
            aux = "NUMBER(" + _w + ", 0)";
            break;

        case Types.BOOLEAN:
            aux = "NUMBER(1, 0)";
            break;

        case Types.DECIMAL:
            aux = "NUMBER(" + _w + ", " + _dec + ")";
            break;

        case Types.NUMERIC:
            aux = "NUMBER(" + _w + ", " + _dec + ")";
            break;

        case Types.DOUBLE:
            aux = "NUMBER(" + _w + ", " + _dec + ")";
            break;

        case Types.FLOAT:
            aux = "NUMBER(" + _w + ", " + _dec + ")";
            break;

        case Types.CHAR:
            aux = "CHAR(1 BYTE)";
            break;

        case Types.VARCHAR:
            aux = "VARCHAR2(" + _w + ")";
            break;

        case Types.LONGVARCHAR:
            aux = "VARCHAR2(" + _w + ")";
            break;
            
        case Types.DATE:
            aux = "DATE";
            break;

        }

        return aux;
    }

    // -----------------------------------------------------------
    // -----------------------------------------------------------
    public static String getDropTableSql(DBLayerDefinition dbLayerDef) {
        return "DROP TABLE " + dbLayerDef.getTableName() +
        " CASCADE CONSTRAINTS";
    }

    public static String getTableCreationSql(DBLayerDefinition dbLayerDef) {
        FieldDescription[] flds = dbLayerDef.getFieldsDesc();

        String type = "";
        String name = "";
        String table_name = dbLayerDef.getTableName().toUpperCase();

        String resp = "CREATE TABLE " + table_name + " ( ";

        for (int i = 0; i < flds.length; i++) {
            name = flds[i].getFieldName();

            // -------------- FORBIDDEN FIELD NAMES -----------------
            if (!isOracleAllowedFieldname(name)) {
                continue;
            }

            // ------------------------------------------------------
            if (name.compareToIgnoreCase(DEFAULT_GEO_FIELD) == 0) {
            }
            else {
                name = getValidOracleID(name, i, false);
                resp = resp + "\"" + name + "\" ";
                type = fieldTypeToSqlStringType(flds[i]);
                resp = resp + type + ", ";
            }
        }

        resp = resp + "\"" + DEFAULT_GEO_FIELD + "\" ";
        resp = resp + "\"MDSYS\".\"SDO_GEOMETRY\"";
        resp = resp + ", ";

        String pk = "CONSTRAINT " + getDerivedNAme(table_name, "PK") +
            " PRIMARY KEY (\"" + OracleSpatialDriver.DEFAULT_ID_FIELD_CASE_SENSITIVE +
            "\") ENABLE";

        resp = resp + pk + " )";

        return resp;
    }

    private static String getDerivedNAme(String tname, String suffix) {

    	int ind = tname.lastIndexOf(".");
    	if (ind == -1) {

    		int l = Math.min(28, tname.length());
    		return tname.substring(0, l) + "_" + suffix;

    	} else {

    		String pre = tname.substring(0, ind);
    		String post = tname.substring(ind + 1, tname.length());
    		int lpost = Math.min(24, post.length());
    		int lpre = Math.min(3, pre.length());
    		return pre.substring(0, lpre) + "_" + post.substring(0, lpost) + "_" + suffix;
    	}

    }

    public static String getIndexCreationSql(DBLayerDefinition dbLayerDef) {
        String resp = "CREATE INDEX " + getDerivedNAme(dbLayerDef.getTableName(), "SX") +
            " ON " + dbLayerDef.getTableName() + " (\"" +
            OracleSpatialDriver.DEFAULT_GEO_FIELD +
            "\") INDEXTYPE IS \"MDSYS\".\"SPATIAL_INDEX\" ";

        return resp;
    }

    public static String getRemoveMetadataSql(DBLayerDefinition dbLayerDef) {

    	String tname = dbLayerDef.getTableName();
    	int ind = tname.lastIndexOf(".");
    	if (ind != -1) {
    		String schema = tname.substring(0, ind);
    		tname = tname.substring(ind + 1, tname.length());
            return "DELETE FROM " + USER_ORACLE_GEOMETADATA_VIEW +
            " WHERE TABLE_NAME = '" + tname + "'";

    	} else{
            return "DELETE FROM " + USER_ORACLE_GEOMETADATA_VIEW +
            " WHERE TABLE_NAME = '" + tname + "'";
    	}
    }

    /**
     * UTility method to get the SQL sentence needed to update the geographic metadata table
     * with a new bounding box and SRS
     *
     * @param tName table name
     * @param ora_srid new SRS
     * @param bbox new bounding box
     * @param dim geometries dimension
     * @param withsrid False if the SRS is set to NULL. True otherwise.
     * @return the SQL sentence to perform the update
     */
    public static String getMetadataUpdateSql(String schema, String tName, String ora_srid,
        Rectangle2D bbox, int dim, boolean withsrid) {
    	
        String[] dim_name = new String[dim];

        
        String _ora_srid = ora_srid;
        if (_ora_srid == null) _ora_srid = "NULL";

        if (_ora_srid.compareTo(GEODETIC_SRID) == 0) {
            dim_name[0] = "LONGITUDE";
            dim_name[1] = "LATITUDE";
        }
        else {
            dim_name[0] = "X";
            dim_name[1] = "Y";

            if (dim > 2) {
                dim_name[2] = "Z";

                if (dim > 3) {
                    dim_name[3] = "T";
                }
            }
        }
        
        double minx = bbox.getMinX();
        double miny = bbox.getMinY();
        double maxx = bbox.getMaxX();
        double maxy = bbox.getMaxY();
        
        String resp = "INSERT INTO " + USER_ORACLE_GEOMETADATA_VIEW + " " +
            " ( TABLE_NAME, COLUMN_NAME, DIMINFO, SRID ) " + " VALUES ("
            + "'" + tName + "', "
            + "'" + DEFAULT_GEO_FIELD + "', " +
            "MDSYS.SDO_DIM_ARRAY( " + "MDSYS.SDO_DIM_ELEMENT ('" + dim_name[0] + "', " +
            minx + ", " + maxx + ", " + OracleSpatialUtils.ORACLE_GEOM_METADATA_TOLERANCE + " ), " +
            "MDSYS.SDO_DIM_ELEMENT ('" + dim_name[1] + "', " + miny + ", " +
            maxy + ", " + OracleSpatialUtils.ORACLE_GEOM_METADATA_TOLERANCE + " ))";

        if (dim > 2) {
            resp = resp.substring(0, resp.length() - 1) + ",";
            resp = resp + "MDSYS.SDO_DIM_ELEMENT ('" + dim_name[2] +
                "', 0.0, 100.0, " + OracleSpatialUtils.ORACLE_GEOM_METADATA_TOLERANCE + " ))";

            if (dim > 3) {
                resp = resp.substring(0, resp.length() - 1) + ",";
                resp = resp + "MDSYS.SDO_DIM_ELEMENT ('" + dim_name[3] +
                    "', 0.0, 100.0, " + OracleSpatialUtils.ORACLE_GEOM_METADATA_TOLERANCE + " ))";
            }
        }

        if (withsrid) {
            resp = resp + ", " + _ora_srid + " )";
        }
        else {
            resp = resp + ", NULL )";
        }

        return resp;
    }

    /**
     * Gets the SQL sentence to perform an insertion.
     *
     * @param feat feature to be added
     * @param dbLayerDef layer definition
     * @param rowInd row index
     * @param _geoColName geometry field name
     * @return the SQL sentence to perform the insertion
     */
    public static String getRowInsertSql(IFeature feat,
        DBLayerDefinition dbLayerDef, int rowInd,
        String _geoColName,
        String geo_val) {
    	
        String name = "";
        int ftype = -1;
        String aux_orig = "";
        String aux_limited = "";
        String aux_quotes_ok = "";

        FieldDescription[] fieldsDescr = dbLayerDef.getFieldsDesc();

        String resp = "INSERT INTO " + dbLayerDef.getTableName() + " ( ";

        for (int i = 0; i < fieldsDescr.length; i++) {
            name = fieldsDescr[i].getFieldName();
            ftype = fieldsDescr[i].getFieldType();

            // -------------- FORBIDDEN FIELD NAMES & TYPES ---------
            if (!isOracleAllowedFieldname(name)) continue;
            if (!isUserEditableType(ftype, name, _geoColName)) continue;
            // ------------------------------------------------------
            if (name.compareToIgnoreCase(_geoColName) == 0) {
            }
            else {
                name = getValidOracleID(name, i, false);
                resp = resp + "\"" + name + "\"" + " , ";
            }
        }

        resp = resp + _geoColName + " ) VALUES ( ";

        for (int i = 0; i < fieldsDescr.length; i++) {
            name = fieldsDescr[i].getFieldName();
            ftype = fieldsDescr[i].getFieldType();

            // -------------- FORBIDDEN FIELD NAMES/Types -----------
            if (!isOracleAllowedFieldname(name)) continue;
            if (!isUserEditableType(ftype, name, _geoColName)) continue;
            // ------------------------------------------------------
            String sur = getValueSurroundFromType(fieldsDescr[i]);

            if (name.compareToIgnoreCase(_geoColName) == 0) {
            }
            else {
                if (name.compareTo(OracleSpatialDriver.DEFAULT_ID_FIELD_CASE_SENSITIVE) == 0) {
                    resp = resp + rowInd + " , ";
                }
                else {
                    Value attValue = feat.getAttribute(i);

                    if (attValue.toString() == null) {
                        resp = resp + "NULL , ";
                    }
                    else {
                        if (sur.length() > 0) {
                            aux_orig = attValue.toString();
                            aux_limited = cropStringValue(aux_orig, i,
                                    fieldsDescr);
                            aux_quotes_ok = avoidQuoteProblem(aux_limited);

                            resp = resp + sur + aux_quotes_ok + sur + " , ";
                        }
                        else {
                            String _aux = attValue.toString();

                            if (_aux.length() == 0) {
                                _aux = "NULL";
                            }

                            resp = resp + _aux + " , ";
                        }
                    }
                }
            }
        }

        resp = resp + " " + geo_val + " )";
        /*
        String test = "SDO_UTIL.APPEND(SDO_GEOMETRY("
        		+ "2002, NULL, NULL,"
        		+ "SDO_ELEM_INFO_ARRAY(1, 2, 2),"
        		+ "SDO_ORDINATE_ARRAY(500000, 4000000, 1000000, 5000000, 500000, 5000000)"
        		+ "), ? )";

        resp = resp + " " + test + " )";
        */
        return resp;
    }

    private static boolean isUserEditableType(int ftype, String item_name, String geo_name) {

    	if (item_name.compareToIgnoreCase(geo_name) == 0) {
    		return true;
    	}

    	if ((ftype == Types.BINARY)
        	|| (ftype == Types.ARRAY)
        	|| (ftype == Types.BLOB)
        	|| (ftype == Types.CLOB)
        	|| (ftype == Types.STRUCT)
    	) {
    		return false;
    	}
		return true;
	}

	/**
     * Gets the SQL sentence to perform an update.
     *
     * @param feat feature to be updated
     * @param dbLayerDef layer definition
     * @param rowInd row index
     * @param geoFieldName geometry field name
     * @return the SQL sentence to perform the update
     */
    public static String getRowUpdateSql(IFeature feat,
        DBLayerDefinition dbLayerDef, int rowInd, String geoFieldName) {
        String name = "";
        String aux_orig = "";
        String aux_limited = "";
        String aux_quotes_ok = "";

        Value[] atts = feat.getAttributes();
        FieldDescription[] _fieldsDescr = dbLayerDef.getFieldsDesc();

        String resp = "UPDATE " + dbLayerDef.getTableName() + " SET ";

        for (int i = 0; i < _fieldsDescr.length; i++) {
            name = _fieldsDescr[i].getFieldName();

            // -------------- FORBIDDEN FIELD NAMES -----------------
            if (!isOracleAllowedFieldname(name)) {
                logger.info("Field: " + name + " will not be updated.");
                continue;
            }

            if (isStructAndNotGeoField(_fieldsDescr[i].getFieldType(), name,
                        geoFieldName)) {
                logger.info("Field: " + name + " will not be updated (it's a struct).");
                continue;
            }

            // ------------------------------------------------------
            if (name.compareToIgnoreCase(geoFieldName) == 0) {
                // resp = resp + "\"" + name + "\"" + " = ?, ";
            }
            else {
                String sur = getValueSurroundFromType(_fieldsDescr[i]);
                aux_orig = atts[i].toString();
                aux_limited = cropStringValue(aux_orig, i, _fieldsDescr);
                aux_quotes_ok = avoidQuoteProblem(aux_limited);
                resp = resp + "\"" + name + "\"" + " = " + sur + aux_quotes_ok +
                    sur + ", ";
            }
        }

        resp = resp + "\"" + geoFieldName + "\" = ?";
        resp = resp + " WHERE ROWID ='" + feat.getID() + "'";

        return resp;
    }

    private static boolean isStructAndNotGeoField(int ftype, String fldname,
        String geoname) {
        if (ftype == Types.STRUCT) {
            if (fldname.compareToIgnoreCase(geoname) != 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the SQL sentence to perform a deletion.
     *
     * @param dbLayerDef layer definition
     * @param id ROWID of the record to be deleted
     * @return the SQL sentence to perform the deletion
     */
    public static String getRowDeleteSql(DBLayerDefinition dbLayerDef, String id) {
        String resp = "DELETE FROM " + dbLayerDef.getTableName();
        resp = resp + " WHERE ROWID ='" + id + "'";

        return resp;
    }

    private static String cropStringValue(String orig_val, int i,
        FieldDescription[] _flds) {
    	
        if (orig_val == null) {
            return "NULL";
        }
        
        if (NumberUtilities.isNumeric(_flds[i].getFieldType())
            	&& (orig_val.length() == 0)) {
            	return "NULL";
        }

        int tpe = _flds[i].getFieldType();
        int max_size = OracleSpatialUtils.maxSizeForFieldType(tpe);

        if (max_size == -1) {
            return orig_val;
        }

        int or_size = orig_val.length();

        if (or_size <= max_size) {
            return orig_val;
        }

        return orig_val.substring(0, max_size);
    }

    private static String avoidQuoteProblem(String str) {
        return str.replaceAll("'", "''");
    }

    private static String getValueSurroundFromType(FieldDescription fieldDesc) {
        if (NumberUtilities.isNumeric(fieldDesc.getFieldType())) {
            return "";
        }

        return "'";
    }

    /**
     * Utility function to translate a SRS code from EPSG to Oracle.
     * Uses a datasource based on a DBF file.
     *
     * @param epsg the EPSG code
     * @return the Oracle code
     */
    public static String epsgSridToOracleSrid(String _epsg) throws Exception {
    	
    	String epsg = removePrefix(_epsg);
    	
        String resp = "8307";

        // --------------------------------------------
        String sql = "select ORACLE from " + ORACLE_EPSG_TABLE_NAME +
            " where EPSG = " + epsg + " and PRF_ORACLE = 1;";
        DataSource ds = null;

        try {
            ds = LayerFactory.getDataSourceFactory()
                             .executeSQL(sql,
                    DataSourceFactory.AUTOMATIC_OPENING); //.MANUAL_OPENING);

            if (ds.getRowCount() == 0) {
                logger.error("EPSG code not found in table: " + epsg);
                throw new Exception("Unknown EPSG: " + epsg);
            }

            if (ds.getRowCount() > 1) {
                logger.error("===============");
                logger.error(
                    "DBF file is wrong: More than one preferred Oracle Spatial code found for EPSG code: " +
                    epsg);

                for (int i = 0; i < ds.getRowCount(); i++) {
                    int aux = (int) Math.round(((DoubleValue) ds.getRow(i)[0]).doubleValue());

                    if (i == 0) {
                        resp = "" + aux;
                    }

                    logger.error("" + aux);
                }

                logger.error("===============");

                return resp;
            }

            resp = "" +
                Math.round(((DoubleValue) ds.getRow(0)[0]).doubleValue());
        }
        catch (Exception pe) {
            logger.error("Error with SQL statement. " + pe.getMessage());
        }

        return resp;
    }

    /**
     * Utility function to translate a SRS code from Oracle to EPSG.
     * Uses a datasource based on a DBF file.
     *
     * @param ora the Oracle code
     * @return the EPSG code
     */
    public static String oracleSridToEpsgSrid(String ora) throws Exception {
    	
    	if (ora == null) return null;
    	
        String resp = "4326";

        // --------------------------------------------
        String sql = "select EPSG from " + ORACLE_EPSG_TABLE_NAME +
            " where ORACLE = " + ora + ";";
        DataSource ds = null;

        try {
            ds = LayerFactory.getDataSourceFactory()
                             .executeSQL(sql,
                    DataSourceFactory.AUTOMATIC_OPENING);

            if (ds.getRowCount() == 0) {
                logger.error("Oracle Spatial code not found in table: " + ora);
                throw new Exception("Unknown Oracle code: " + ora);
            }

            if (ds.getRowCount() > 1) {
                logger.error("===============");
                logger.error(
                    "DBF file is wrong: More than one EPSG code found for Oracle Spatial code: " +
                    ora);

                for (int i = 0; i < ds.getRowCount(); i++) {
                    String aux = "" +
                        Math.round(((DoubleValue) ds.getRow(i)[0]).doubleValue());

                    if (i == 0) {
                        resp = aux;
                    }

                    logger.error("" + aux);
                }

                logger.error("===============");

                return resp;
            }

            resp = "" +
                Math.round(((DoubleValue) ds.getRow(0)[0]).doubleValue());
        }
        catch (Exception pe) {
            logger.error("Error with SQL statement. " + pe.getMessage());
        }

        return resp;
    }

    /**
     * This methos creates the datasource used to translate the SRS codes:
     * EPSG <--> Oracle.
     *
     * It's called from several places, so checks that the datasource does not exist.
     */


    /**
     * Utility method to get a valid Oracle identifier (in terms of length)
     *
     * @param str Proposed string
     * @param ind field index of the given field name (used by the method to
     * improve the renaming)
     * @return an acceptable oracle identifier.
     */
    public static String getValidOracleID(String _str, int ind, boolean force_uppercase) {
    	
    	String str = _str;
    	if (force_uppercase) str = _str.toUpperCase();
    	
        if (str.length() <= MAX_ID_LENGTH) {
            return str;
        }

        String resp = str.substring(0, MAX_ID_LENGTH - 7);
        resp = resp + str.substring(MAX_ID_LENGTH - 6, MAX_ID_LENGTH - 5);
        resp = resp + str.substring(MAX_ID_LENGTH - 4, MAX_ID_LENGTH - 3);
        resp = resp + str.substring(MAX_ID_LENGTH - 2, MAX_ID_LENGTH - 1);
        resp = resp + "_" + (ind % 1000);

        return resp;
    }

    private static ArrayList ensureSensibleShell(ArrayList cc) {
        if (sameCoordinate((Coordinate) cc.get(0),
                    (Coordinate) cc.get(cc.size() - 1))) {
            if (cc.size() == 2) {
                ArrayList resp = new ArrayList();
                resp.add(cc.get(0));

                Coordinate newcoo = new Coordinate((Coordinate) cc.get(0));
                newcoo.x = newcoo.x + IRRELEVANT_DISTANCE;
                resp.add(newcoo);

                newcoo = new Coordinate((Coordinate) cc.get(0));
                newcoo.x = newcoo.x + IRRELEVANT_DISTANCE;
                newcoo.y = newcoo.y - IRRELEVANT_DISTANCE;
                resp.add(newcoo);

                resp.add(cc.get(0));

                return resp;
            }

            if (cc.size() == 3) {
                cc.remove(1);

                return ensureSensibleShell(cc);
            }

            return cc;
        }
        else {
            cc.add(cc.get(0));

            return cc;
        }
    }

    private static ArrayList ensureSensibleHole(ArrayList cc) {
        if (sameCoordinate((Coordinate) cc.get(0),
                    (Coordinate) cc.get(cc.size() - 1))) {
            if (cc.size() == 2) {
                ArrayList resp = new ArrayList();
                resp.add(cc.get(0));

                Coordinate newcoo = new Coordinate((Coordinate) cc.get(0));
                newcoo.x = newcoo.x - IRRELEVANT_DISTANCE;
                resp.add(newcoo);

                newcoo = new Coordinate((Coordinate) cc.get(0));
                newcoo.x = newcoo.x - IRRELEVANT_DISTANCE;
                newcoo.y = newcoo.y + IRRELEVANT_DISTANCE;
                resp.add(newcoo);

                resp.add(cc.get(0));

                return resp;
            }

            if (cc.size() == 3) {
                cc.remove(1);

                return ensureSensibleHole(cc);
            }

            return cc;
        }
        else {
            cc.add(cc.get(0));

            return cc;
        }
    }

    private static ArrayList ensureSensibleLineString(ArrayList cc) {
        if (cc.size() == 2) {
            if (sameCoordinate((Coordinate) cc.get(0),
                        (Coordinate) cc.get(cc.size() - 1))) {
                ArrayList resp = new ArrayList();
                resp.add(cc.get(0));

                Coordinate newc = new Coordinate((Coordinate) cc.get(0));
                newc.x = newc.x + IRRELEVANT_DISTANCE;
                resp.add(newc);

                return resp;
            }
        }

        return cc;
    }

    private static boolean sameCoordinate(Coordinate c1, Coordinate c2) {
        if (c1.x != c2.x) {
            return false;
        }

        if (c1.y != c2.y) {
            return false;
        }

        return true;
    }

    private static ArrayList getClosedRelevantPolygon(ArrayList cc) {
        if (cc.size() == 2) {
            return null;
        }

        if (cc.size() == 3) {
            if (sameCoordinate((Coordinate) cc.get(0), (Coordinate) cc.get(1))) {
                return null;
            }

            if (sameCoordinate((Coordinate) cc.get(0), (Coordinate) cc.get(2))) {
                return null;
            }

            if (sameCoordinate((Coordinate) cc.get(1), (Coordinate) cc.get(2))) {
                return null;
            }

            cc.add(cc.get(0));

            return cc;
        }

        if (!sameCoordinate((Coordinate) cc.get(0),
                    (Coordinate) cc.get(cc.size() - 1))) {
            cc.add(cc.get(0));
        }

        return cc;
    }

    private static MultiPolygon getMinMultiPolygon(Coordinate c) {
        Coordinate[] p = new Coordinate[4];
        p[0] = c;

        Coordinate nc = new Coordinate(c);
        nc.x = nc.x + IRRELEVANT_DISTANCE;

        Coordinate nc2 = new Coordinate(nc);
        nc2.y = nc2.y - IRRELEVANT_DISTANCE;
        p[1] = nc;
        p[2] = nc2;
        p[3] = new Coordinate(c);

        CoordinateArraySequence cs = new CoordinateArraySequence(p);
        LinearRing ls = new LinearRing(cs, geomFactory);
        Polygon po = new Polygon(ls, null, geomFactory);
        Polygon[] pos = new Polygon[1];
        pos[0] = po;

        MultiPolygon mpo = new MultiPolygon(pos, geomFactory);

        return mpo;
    }

    public String getSourceProjection(IConnection conn, DBLayerDefinition lyrDef) {
    	
    	String resp = null;
        try {
            Statement _st = ((ConnectionJDBC) conn).getConnection().createStatement();
            String[] tokens = lyrDef.getName().split("\\u002E", 2);
            String qry;
            if (tokens.length > 1) {
            	qry = "select * from " + ALL_ORACLE_GEOMETADATA_VIEW +
                " where OWNER = '" + tokens[0] + "' AND TABLE_NAME = '" +
                tokens[1] + "'";
            } else {
            	qry = "select * from " + ALL_ORACLE_GEOMETADATA_VIEW +
                " where TABLE_NAME = " + "'" + lyrDef.getName() + "'";
            }
            ResultSet _rs = _st.executeQuery(qry);

            if (_rs.next()) {
                String aux = getOracleSridFromCurrentRecord(_rs);
                try {
					resp = oracleSridToEpsgSrid(aux);
				} catch (Exception e) {
					logger.error("Unknown oracle SRID: " + aux);
				}
            } else {
            	
            }
        } catch (Exception ex) {
        	logger.error("While getting Source Projection: " + ex.getMessage());
        }
        
        if (resp != null) {
        	return resp;
        } else {
        	return getDestProjection();
        }
        
    }

    public String getDestProjection() {
        return removePrefix(destProj);
    }

    public void setDestProjection(String toEPSG) {
        destProj = toEPSG;
        try {
			destProjOracle = epsgSridToOracleSrid(destProj);
			isDestGeogCS = OracleSpatialUtils.getIsGCS(destProjOracle, true);

		} catch (Exception e) {
			logger.error("Unknown EPSG code: " + destProj);
			destProjOracle = oracleSRID;
			isDestGeogCS = false;
		}

    }

    public String getDestProjectionOracleCode() {
    	return destProjOracle;
    }

    public boolean getIsDestProjectionGeog() {
    	return isDestGeogCS;
    }

    public String getTableProjectionOracleCode() {
    	return oracleSRID;
    }

    public boolean canReproject(String toEPSGdestinyProjection) {
        return false;
    }

    /**
     * Utility function. Says whether a given field name can be a user field name
     * or not (for example, "ROWID" is not a valid one because it's a system
     * reserved word).
     *
     * @param str proposed firld name
     * @return whether it is valid or not for Oracle databases
     */
    private static boolean isOracleAllowedFieldname(String str) {
        if (str.compareToIgnoreCase("rowid") == 0) {
            return false;
        }

        if (str.compareToIgnoreCase("rownum") == 0) {
            return false;
        }

        return true;
    }

    public Hashtable getHashRelate() {
        return hashRelate;
    }

    public void setHashRelate(Hashtable m) {
        hashRelate = m;
    }

    public void setNumReg(int n) {
        numReg = n;
    }

    private int[] getRandomSample(int maxn_one_based, int n) {
        int[] resp = new int[n];

        if (maxn_one_based <= n) {
            resp = new int[maxn_one_based];

            for (int i = 0; i < maxn_one_based; i++) {
                resp[i] = i;
            }
        }
        else {
            Random rnd = new Random();

            for (int i = 0; i < n; i++) {
                resp[i] = rnd.nextInt(maxn_one_based);
            }
        }

        return resp;
    }

    private String getRowIdRestrictionCondition(int nrecords) {
        int[] zero_based_rows = getRandomSample(nrecords,
                GEODETIC_FULLEXTENT_SAMPLE_SIZE);
        String resp = "(";
        Object aux = "";
        ROWID riaux = null;

        for (int i = 0; i < zero_based_rows.length; i++) {
            aux = rowToId.get(new Integer(zero_based_rows[i]));
            riaux = (ROWID) aux;
            resp = resp + "(ROWID = '" + riaux.stringValue() + "') OR ";
        }

        resp = resp.substring(0, resp.length() - 4);
        resp = resp + ")";

        return resp;
    }

    private Rectangle2D getBoundingFromSample(int n_max) {
        String _qry = "SELECT " + geoColName + " FROM " + getTableName() +
            " WHERE " + getRowIdRestrictionCondition(n_max);
        STRUCT auxstr = null;
        IGeometry theGeom = null;
        Rectangle2D resp = null;

        try {
            Statement st = ((ConnectionJDBC)conn).getConnection().createStatement();
            ResultSet rs = st.executeQuery(_qry);

            if (rs.next()) {
                auxstr = (STRUCT) rs.getObject(1);

                if (auxstr != null) {
                    theGeom = getGeometryUsing(auxstr, use_geotools);

                    if (resp == null) {
                        resp = theGeom.getBounds2D();
                    }
                    else {
                        resp.add(theGeom.getBounds2D());
                    }
                }

                while (rs.next()) {
                    auxstr = (STRUCT) rs.getObject(1);

                    if (auxstr != null) {
                        theGeom = getGeometryUsing(auxstr, use_geotools);

                        if (resp == null) {
                            resp = theGeom.getBounds2D();
                        }
                        else {
                            resp.add(theGeom.getBounds2D());
                        }
                    }
                }

                rs.close();
                st.close();
            }
            else {
                throw new SQLException("Empty resultset from this query: " +
                    _qry);
            }
        }
        catch (SQLException se) {
        	logger.error("While getting sample full extent: " +
                se.getMessage());
        }

        if (resp == null) {
            logger.warn(
                "Did not find a geometry to compute sample bbox. Returned geographic bbox for whole world.");

            return new Rectangle2D.Double(-180, -90, 360, 180);
        }

        return resp;
    }

    /**
     * Does what it says, puts the LinearRing in counter clock wise
     * order.
     * @param ls The ring to set.
     * @param gf a GeometryFactory object
     * @return A new ring in CCW order.
     *
     */
    public static LinearRing putInCCWOrderLR(LineString ls, GeometryFactory gf) {
        Coordinate[] cc = ls.getCoordinates();

        if (CGAlgorithms.isCCW(cc)) {
            return gf.createLinearRing(cc);
        }
        else {
            if (ls instanceof LinearRing) {
                return reverseRing((LinearRing) ls, gf);
            }
            else {
                return reverseLineString(ls, gf);
            }
        }
    }

    /**
     * Does what it says, reverses the order of the Coordinates in the ring.
     * @param lr The ring to reverse.
     * @return A new ring with the reversed Coordinates.
     *
     */
    public static LinearRing reverseRing(LinearRing lr, GeometryFactory gf) {
        int numPoints = lr.getNumPoints() - 1;
        Coordinate[] newCoords = new Coordinate[numPoints + 1];

        for (int t = numPoints; t >= 0; t--) {
            newCoords[t] = lr.getCoordinateN(numPoints - t);
        }

        return gf.createLinearRing(newCoords);
    }

    /**
     * Does what it says, reverses the order of the Coordinates in the linestring.
     * @param ls The ls to reverse.
     * @param gf a GeometryFactory object
     * @return A new ls with the reversed Coordinates.
     *
     */
    public static LinearRing reverseLineString(LineString ls, GeometryFactory gf) {
        int numPoints = ls.getNumPoints() - 1;
        Coordinate[] newCoords = new Coordinate[numPoints + 1];

        for (int t = numPoints; t >= 0; t--) {
            newCoords[t] = ls.getCoordinateN(numPoints - t);
        }

        return gf.createLinearRing(newCoords);
    }

    private static Geometry putInCCWOrder(Geometry ge, GeometryFactory gf) {
        if (ge instanceof MultiPolygon) {
            MultiPolygon mp = (MultiPolygon) ge;
            int size = ge.getNumGeometries();
            Polygon[] pols = new Polygon[size];

            for (int i = 0; i < size; i++)
                pols[i] = (Polygon) putInCCWOrder((Polygon) mp.getGeometryN(i),
                        gf);

            return new MultiPolygon(pols, gf);
        }
        else {
            if (ge instanceof Polygon) {
                Polygon p = (Polygon) ge;
                LinearRing exterior = putInCCWOrderLR(p.getExteriorRing(), gf);
                int nholes = p.getNumInteriorRing();

                if (nholes > 0) {
                    LinearRing[] holes = new LinearRing[nholes];

                    for (int i = 0; i < nholes; i++) {
                        holes[i] = putInCCWOrderLR(p.getInteriorRingN(i), gf);
                    }

                    return gf.createPolygon(exterior, holes);
                }
                else {
                    return gf.createPolygon(exterior, null);
                }
            }
            else {
                return ge;
            }
        }
    }

    /**
     * Converts from IGeometry to STRUCT
     *
     * @param ig the geometry to convert
     * @param _forced_type forced type to use
     * @param _conn connection
     * @param _o_srid  SRS (oracle code)
     * @param withSrid whether this STRUCT has a non-NULL SRS
     * @param agu_bien whether or not to check the correctness of the holes
     * @param _isGeoCS whether the SRS is geodetic or not
     * @return the generated STRUCT
     */
    public static STRUCT iGeometryToSTRUCT(IGeometry ig, int _forced_type,
        IConnection _conn, String _o_srid, boolean withSrid, boolean agu_bien,
        boolean _isGeoCS) {
        if (ig instanceof FGeometryCollection) {
            FGeometryCollection coll = (FGeometryCollection) ig;

            return OracleSpatialUtils.appendGeometriesInStruct(coll,
                _forced_type, _conn, _o_srid, withSrid, agu_bien, _isGeoCS);

            // logger.error("Collections no soportadas por ahora.");
            // return null;
        }
        else {
            Shape shp = ig.getInternalShape();

            return shapeToStruct(shp, _forced_type, _conn, _o_srid, withSrid,
                agu_bien, false, _isGeoCS);
        }
    }

    public STRUCT shapeToStruct(Shape shp, int force_type, boolean hasSrid,
        boolean agu_bien, boolean isView) {

    	if (shp == null) return null;
        return shapeToStruct(shp, force_type, conn, oracleSRID, hasSrid,
            agu_bien, isView, isGeogCS);
    }

    public static STRUCT shapeToStruct(Shape shp, int forced_type,
        IConnection _conn, String o_srid, boolean hasSrid, boolean agu_bien,
        boolean isView, boolean _isGeoCS) {
        int _srid = -1;

        if ((o_srid != null) && (o_srid.length() > 0)) {
            _srid = Integer.parseInt(o_srid);
        }

        if (shp == null) {
            logger.info("Shape is null. shapeToStruct(Shape) returned null.");

            return null;
        }

        if (shp instanceof Rectangle2D) {
            return OracleSpatialUtils.rectangleToStruct((Rectangle2D) shp, hasSrid, isView,
                _isGeoCS, o_srid, _conn);
        }

        try {
            STRUCT the_struct = OracleSpatialUtils.fShapeToSTRUCT(shp, _conn,
                    _srid, agu_bien, hasSrid, _isGeoCS);

            return the_struct;
        }
        catch (SQLException ex) {
            logger.error("While creating STRUCT: " + ex.getMessage());

            return null;
        }
    }

    // -------------------------- not ready yet ----------------
    public int getRowIndexByFID(IFeature _fid) {
        if (isNotAvailableYet) {
            return -1;
        }
        else {
            return super.getRowIndexByFID(_fid);
        }
    }

    public int getShapeCount() throws ReadDriverException { 
        if (isNotAvailableYet) {
            return 0;
        }
        else {
            return numReg;
        }
    }

    public void setNotAvailableYet(boolean nav) {
        isNotAvailableYet = nav;
    }

    // -------------------------------------------------------
    // -------------------------------------------------------
    public String[] getTableNames(IConnection conn, String catalog)
        throws DBException {
        try{
    	DatabaseMetaData dbmd = ((ConnectionJDBC)conn).getConnection().getMetaData();
        String[] types = { "TABLE", "VIEW" };
        // String[] types = { "VIEW" };

        ResultSet rs = null;
        rs = getTableNamesFromTable(dbmd.getTables(catalog, ORACLE_GEO_SCHEMA,
                    ALL_ORACLE_GEOMETADATA_VIEW, types), ((ConnectionJDBC)conn).getConnection());
//        rs = dbmd.getTables(catalog, ORACLE_GEO_SCHEMA,
//                          ORACLE_GEOMETADATA_VIEW, types);
        TreeMap ret = new TreeMap();

        while (rs.next()) {
        	String nomCompleto = rs.getString("OWNER") + "." +rs.getString("TABLE_NAME");
            ret.put(nomCompleto, nomCompleto);
        }

        return (String[]) ret.keySet().toArray(new String[0]);
        }catch (SQLException e) {
			throw new DBException(e);
		}
    }

    private ResultSet getTableNamesFromTable(ResultSet res, Connection con)
        throws SQLException {
        String tablename = "";

        if (res.next()) {
            tablename = res.getString("TABLE_NAME");

            // debug
            writeMetaTableToLog(con, tablename);

            Statement __st = con.createStatement();

            String sql = "(" + "(select TABLE_NAME from USER_TABLES) " +
                "union (select VIEW_NAME from USER_VIEWS)) " +
                "intersect (select TABLE_NAME from " + tablename + ")";
            sql = "SELECT TABLE_NAME, OWNER FROM " + tablename + "";
            ResultSet rs = __st.executeQuery(sql);

            return rs;
        }
        else {
            logger.error("Error while getting geometry tables.");

            return null;
        }
    }

    private void writeMetaTableToLog(Connection con, String tname) {

    	logger.debug("======================================================");
    	logger.debug("=     " + ALL_ORACLE_GEOMETADATA_VIEW + "  (1 EVERY 10 TABLES) ========");
    	logger.debug("======================================================");

    	try {
            Statement _stmt = con.createStatement();
            String sql = "SELECT * FROM " + tname;
            ResultSet res = _stmt.executeQuery(sql);
            
            int count = 0;
            while (res.next()) {
            	
            	if ((count % 10) == 0) {
                	logger.debug(
                			"OWNER: " + res.getString("OWNER")
                			+ ", TABLE_NAME: " + res.getString("TABLE_NAME")
                			+ ", COLUMN_NAME: " + res.getString("COLUMN_NAME")
                			+ ", SRID: " + res.getString("SRID"));
                	ARRAY _aux = (ARRAY) res.getObject("DIMINFO");
                	String dinfo = OracleSpatialUtils.getDimInfoAsString(_aux);
                	logger.debug("DIMINFO: " + dinfo);
                	logger.debug("=========");
            	}
            	count++;
            	
            }
    	} catch (Throwable th) {

    	}
	}

	/**
     * Gets the field names that can act as row id (always ROWID)
     */
    public String[] getIdFieldsCandidates(IConnection conn, String table_name)
        throws DBException {
    	try{
    	String rowid_avail_test = "SELECT ROWID FROM " + table_name + " WHERE ROWNUM = 1";
    	Statement _st = ((ConnectionJDBC)conn).getConnection().createStatement();
    	ResultSet _rs = _st.executeQuery(rowid_avail_test);
    	_rs.close();
    	_st.close();

    	String[] resp = { "ROWID" };
        return resp;
    	}catch (SQLException e) {
			throw new DBException(e);
		}
    }

    /**
     * Gets the field names that can act as geometry fields
     * (queries the user's geographic metadata).
     */
    public String[] getGeometryFieldsCandidates(IConnection conn,
        String table_name) throws DBException {
    	try{
        Statement _st = ((ConnectionJDBC)conn).getConnection().createStatement();
        String[] tokens = table_name.split("\\u002E", 2);
        String qry;
        if (tokens.length > 1)
        {
        	qry = "select * from " + ALL_ORACLE_GEOMETADATA_VIEW +
            " where OWNER = '" + tokens[0] + "' AND TABLE_NAME = '" +
            tokens[1] + "'";
        }
        else
        {
        	qry = "select * from " + ALL_ORACLE_GEOMETADATA_VIEW +
            " where TABLE_NAME = " + "'" + table_name + "'";

        }
        ResultSet _rs = _st.executeQuery(qry);

        ArrayList aux = new ArrayList();

        while (_rs.next()) {
            String _geo = _rs.getString("COLUMN_NAME");
            aux.add(_geo);
        }

        _rs.close();
        _st.close();

        String[] resp = (String[]) aux.toArray(new String[0]);

        return checkIndexes(conn, resp, table_name);
    	}catch (SQLException e) {
			throw new DBException(e);
		}
    }

    private String[] checkIndexes(IConnection c, String[] all, String long_table_name) throws DBException {

    	ArrayList good_ones = new ArrayList();
    	try{
    	String t = long_table_name;
    	if (t.lastIndexOf(".") != -1) t = t.substring(t.lastIndexOf(".") + 1, t.length());

    	for (int i=0; i<all.length; i++) {

        	String qry = "SELECT SRID, DIMINFO FROM " + ALL_ORACLE_GEOMETADATA_VIEW +
            " WHERE TABLE_NAME = " + "'" + t.toUpperCase() +
            "' AND COLUMN_NAME = '" + all[i].toUpperCase() + "'";

        	Statement _st = ((ConnectionJDBC)c).getConnection().createStatement();
        	ResultSet _rs = _st.executeQuery(qry);
        	if (_rs.next()) {
        		String _srid = toString((BigDecimal) _rs.getObject(1));
        		ARRAY diminfo = (ARRAY) _rs.getObject(2);
        		int len = diminfo.getOracleArray().length;
        		if (allowsGeoQueries(((ConnectionJDBC)c).getConnection(), long_table_name, all[i], _srid, len)) {
        			good_ones.add(all[i]);
        		}
        	}
        	_rs.close();
        	_st.close();
    	}

    	if (good_ones.size() == 0) {
    		throw new SQLException("no_indexes_on_declared_geo_fields");
    	}
    	}catch (SQLException e) {
			throw new DBException(e);
		}
    	return (String[]) good_ones.toArray(new String[0]);
    }

    private String toString(BigDecimal number) {

    	if (number == null) return "NULL";
    	return "" + number.intValue();
	}

	private boolean allowsGeoQueries(Connection c, String _t, String gf, String _srid, int dims) {
    	String p = getPointConstructor(dims, _srid);
    	String qry = "";
    	qry = "SELECT * FROM " + _t.toUpperCase()
    	+ " WHERE SDO_RELATE(" + "\"" + gf + "\", " + p + ", 'mask=TOUCH') = 'TRUE'"
    	+ " AND ROWNUM = 1";

    	try {
			Statement _st = c.createStatement();
			ResultSet _rs = _st.executeQuery(qry);
			_rs.close();
			_st.close();
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	private String getPointConstructor(int dims, String _srid) {

		String coord = "";
		for (int i=0; i<dims; i++) coord = coord + "0, ";
		coord = coord.substring(0, coord.length() - 2);

		return "MDSYS.SDO_GEOMETRY(" + (dims * 1000 + 1) + ", " + _srid + ", NULL, " +
		"MDSYS.SDO_ELEM_INFO_ARRAY(1, 1, 1), MDSYS.SDO_ORDINATE_ARRAY(" + coord + "))";
	}

	private boolean stringInArrayListOfStrings(ArrayList l, String str) {

    	if (l == null) return false;
    	if (str == null) return false;

    	String item = "";
    	for (int i=0; i<l.size(); i++) {
    		if (l.get(i) instanceof String) {
    			item = (String) l.get(i);
    			if (item.compareToIgnoreCase(str) == 0) return true;
    		}
    	}
    	return false;
    }

	/**
     * Utility method to check if a given table is empty.
     */
    public boolean isEmptyTable(Connection conn, String tableName) {
        boolean res = true;

        try {
            Statement st = conn.createStatement();
            ResultSet rs = null;
            rs = st.executeQuery("select * from " + tableName +
                    " where rownum = 1");
            res = !rs.next();
            rs.close();
            st.close();
        }
        catch (Exception ex) {
            res = true;
        }

        return res;
    }

    /**
     * Gets all the fields from a table name.
     */
    public String[] getAllFields(IConnection conn, String table_name)
        throws DBException {
    	try{
        Statement st = ((ConnectionJDBC)conn).getConnection().createStatement();
        ResultSet rs = st.executeQuery("select * from " + table_name +
                " where rownum = 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] ret = new String[rsmd.getColumnCount()];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = rsmd.getColumnName(i + 1);
        }

        rs.close();
        st.close();

        return ret;
    	}catch (SQLException e) {
			throw new DBException(e);
		}
    }

    /**
     * Gets all field type names from a table.
     */
    public String[] getAllFieldTypeNames(IConnection conn, String table_name)
        throws DBException {
    	try{
        Statement st = ((ConnectionJDBC)conn).getConnection().createStatement();
        ResultSet rs = st.executeQuery("select * from " + table_name +
                " where rownum = 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] ret = new String[rsmd.getColumnCount()];

        for (int i = 0; i < ret.length; i++) {
            if (rsmd.getColumnType(i + 1) == Types.NUMERIC) {
            	int scale = rsmd.getScale(i+1);
            	if (scale >= 0) {
                	String prec_dec = " (" + rsmd.getPrecision(i+1) + ", " + scale + ")";  
                	ret[i] = rsmd.getColumnTypeName(i + 1) + prec_dec;
            	} else {
            		ret[i] = rsmd.getColumnTypeName(i + 1);
            	}
            } else {
                ret[i] = rsmd.getColumnTypeName(i + 1);
            }
        }

        rs.close();
        st.close();

        close();

        return ret;
    	}catch (SQLException e) {
			throw new DBException(e);
		}
    }

    /**
     * Gets Oracle's specific connection string for the given parameters.
     */
    public String getConnectionString(String host, String port, String dbname,
        String user, String pw) {
        String _pw = pw;

        if (_pw == null) {
            _pw = "null";
        }

        String fullstr = CONN_STR_BEGIN;
        fullstr = fullstr + user.toLowerCase() + "/" + _pw;
        fullstr = fullstr + "@" + host.toLowerCase();
        fullstr = fullstr + ":" + port;
        fullstr = fullstr + ":" + dbname.toLowerCase();

        return fullstr;
    }

    /**
     * Gets the Pracle geometries writer associated with this driver.
     */
    public IWriter getWriter() {
        // on(VectorialEditableDBAdapter.java:290)
        if (writer == null) {

        	long count = 0;
			try {
				count = getRowCount();
			} catch (ReadDriverException e1) {
				logger.error("While getting row count: " + e1.getMessage());
			}
        	
            writer = new OracleSpatialWriter(count);
            writer.setDriver(this);
            writer.setLyrShapeType(getShapeType());
            writer.setGeoCS(isGeogCS());
            writer.setGeoColName(geoColName);
            writer.setSRID(oracleSRID);

            try {
            	DBLayerDefinition db_lyr_def = getLyrDef();
            	if (db_lyr_def == null) {
            		logger.warn("Found a null DB layer definition, method initialize of OracleWriter not called.");
            	} else {
            		writer.initialize(getLyrDef());
            	}
                
            } catch (InitializeWriterException e) {
                logger.error("While initializing OS Writer: " + e.getMessage(), e);
            }

            writer.setStoreWithSrid(tableHasSrid);
        }

        return writer;
    }

    /**
     * Tells whether the SRS is geodetic or not-
     * @return whether the SRS is geodetic or not
     */
    public boolean isGeogCS() {
        return isGeogCS;
    }

    /**
     * Adds a row id to the inner set od IDs.
     * @param id
     */
    public void addRow(String id) {
        Value aux = ValueFactory.createValue(id);
        Integer intobj = new Integer(numReg);
        hashRelate.put(aux, intobj);
        rowToId.put(intobj, id);

        numReg++;
    }

    /**
     * Removes a row id to the inner set od IDs.
     * @param id
     */
    public void deleteRow(String id) {
        Value aux = ValueFactory.createValue(id);
        Integer intobj = (Integer) hashRelate.get(aux);
        hashRelate.remove(aux);
        rowToId.remove(intobj);

        numReg--;
    }

    private String getStandardSelectExpression() {

		String resp = "";

		String[] flds = getLyrDef().getFieldNames();
		int size = flds.length;

		for (int i = 0; i < size; i++) {
			if (i > 0) {
				resp = resp + "c.\"" + flds[i] + "\", ";
			} else {
				resp = resp + flds[i] + ", ";
			}
		}

		resp = resp.substring(0, resp.length() - 2);
		return resp;
	}

    /**
	 * Allows the method to decide what to do with the geometry field name
	 * (remove/add it from the user selected fields).
	 * 
	 * @param flds
	 * @param geof
	 * @return the possibly modified field names
	 */
    public String[] manageGeometryField(String[] flds, String geof) {
        return addEndIfNotContained(flds, geof);
    }

    /**
     * Allows the method to decide what to do with the ID field name
     * (remove/add it from the user selected fields).
     *
     * @param flds
     * @param idf
     * @return the possibly modified field names
     */
    public String[] manageIdField(String[] flds, String idf) {
        return addStartIfNotContained(flds, idf);
    }

    private String[] addEndIfNotContained(String[] arr, String item) {
        if (contains(arr, item)) {
            return arr;
        }
        else {
            int size = arr.length;
            String[] resp = new String[size + 1];

            for (int i = 0; i < size; i++) {
                resp[i] = arr[i];
            }

            resp[size] = item;

            return resp;
        }
    }

    private String[] addStartIfNotContained(String[] arr, String item) {
        if (contains(arr, item)) {
            return arr;
        }
        else {
            int size = arr.length;
            String[] resp = new String[size + 1];

            for (int i = 1; i <= size; i++) {
                resp[i] = arr[i];
            }

            resp[0] = item;

            return resp;
        }
    }

    private boolean contains(String[] arr, String item) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].compareTo(item) == 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method is called when the user removes the layer from the view.
     * If the IDs were being loaded, the driver will check this field and will
     * let the thread 'die' quietly.
     *
     */
    public void remove() {
        cancelIDLoad = true;
    }

    private boolean realiableExtent(Rectangle2D ext, boolean isgeodetic) {
    	// if (!isgeodetic) return true;
    	if ((ext.getMinX() > -179.9) || (ext.getMinX() < -180.1)) return true;
    	if ((ext.getMinY() > -89.9) || (ext.getMinY() < -90.1)) return true;
    	if ((ext.getWidth() < 359.9) || (ext.getWidth() > 360.1)) return true;
    	if ((ext.getHeight() < 179.9) || (ext.getHeight() > 180.1)) return true;
    	return false;
    }

    private Rectangle2D getFastEstimatedExtent(
    		String tname,
    		String gfield,
    		IConnection c,
    		int sample_size,
    		double enlargement,
    		boolean is_geo) {

    	Rectangle2D world = new Rectangle2D.Double(-180, -90, 360, 180);
    	Rectangle2D resp_aux = null;
    	String qry = "SELECT " + gfield + " FROM " + tname + " WHERE ROWNUM <= " + sample_size;
    	ResultSet _rs = null;

    	try {
			PreparedStatement _st = ((ConnectionJDBC)c).getConnection().prepareStatement(qry);
			_rs = _st.executeQuery();
			while (_rs.next()) {
				STRUCT aux = (STRUCT) _rs.getObject(1);
				IGeometry ig = getGeometryUsing(aux, false);

				if (ig == null) continue;

				if (resp_aux == null) {
					resp_aux = ig.getBounds2D();
				} else {
					resp_aux.add(ig.getBounds2D());
				}

			}
		} catch (Exception ex) {
			logger.error("While getting random sample: " + ex.getMessage());
		}

		if (resp_aux == null) {
			logger.warn("ERROR, ESTIMATED BB = WORLD");
			return world;
		}
		
		double w = resp_aux.getWidth();
		double h = resp_aux.getHeight();
		double x = resp_aux.getMinX();
		double y = resp_aux.getMinY();

		// enlarge n times:
		double newx = x - (0.5 * (enlargement - 1)) * w;
		double newy = y - (0.5 * (enlargement - 1)) * h;
		Rectangle2D resp_aux_large = new Rectangle2D.Double(newx, newy,
				enlargement * w,
				enlargement * h);

		if (is_geo) {
			Rectangle2D.intersect(world, resp_aux_large, resp_aux);
			logger.debug("FAST BB: " + resp_aux.toString());
			return resp_aux;
		} else {
			logger.debug("FAST BB: " + resp_aux_large.toString());
			return resp_aux_large;
		}

    }

    private Rectangle2D getEstimatedExtent(
    		String tname,
    		String gfield,
    		IConnection c,
    		int sample_size,
    		double enlargement,
    		boolean is_geo) {

    	Rectangle2D world = new Rectangle2D.Double(-180, -90, 360, 180);

    	ArrayList ids = new ArrayList();
    	int _rnd_index = 0;
    	ROWID _id = null;
    	Random rnd = new Random(System.currentTimeMillis());

    	for (int i=0; i<sample_size; i++) {
    		_rnd_index = rnd.nextInt(numReg);
    		_id = (ROWID) rowToId.get(new Integer((int) _rnd_index));
    		ids.add(_id.stringValue());
    	}

    	String qry = "SELECT " + gfield + " FROM " + tname + " WHERE (";
    	for (int i=0; i<ids.size(); i++) {
    		qry = qry + "(ROWID = '" + ((String) ids.get(i)) + "') OR ";
    	}
    	qry = qry.substring(0, qry.length() - 4) + ")";

    	Rectangle2D resp_aux = null;
    	ResultSet _rs = null;

    	try {
			PreparedStatement _st = ((ConnectionJDBC)c).getConnection().prepareStatement(qry);
			_rs = _st.executeQuery();
			while (_rs.next()) {
				STRUCT aux = (STRUCT) _rs.getObject(1);
				IGeometry ig = getGeometryUsing(aux, false);

				if (ig == null) continue;

				if (resp_aux == null) {
					resp_aux = ig.getBounds2D();
				} else {
					resp_aux.add(ig.getBounds2D());
				}

			}
		} catch (Exception ex) {
			logger.error("While getting random sample: " + ex.getMessage());
		}

		if (resp_aux == null) {
			logger.warn("ERROR, ESTIMATED BB = WORLD");
			return world;
		}
		
		double w = resp_aux.getWidth();
		double h = resp_aux.getHeight();
		double x = resp_aux.getMinX();
		double y = resp_aux.getMinY();

		// enlarge 10 times:
		double newx = x - (0.5 * (enlargement - 1)) * w;
		double newy = y - (0.5 * (enlargement - 1)) * h;
		Rectangle2D resp_aux_large = new Rectangle2D.Double(newx, newy,
				enlargement * w,
				enlargement * h);

		if (is_geo) {
			Rectangle2D.intersect(world, resp_aux_large, resp_aux);
			logger.debug("ESTIMATED BB: " + resp_aux.toString());
			return resp_aux;
		} else {
			logger.debug("ESTIMATED BB: " + resp_aux_large.toString());
			return resp_aux_large;
		}

    }

    public void setUserName(String u) {
    	userName = u;
    }

    public String getUserName() {
    	return userName;
    }

    public static final int JGeometry_GTYPE_COLLECTION = 4;
    public static final int JGeometry_GTYPE_CURVE = 2;
    public static final int JGeometry_GTYPE_MULTICURVE = 6;
    public static final int JGeometry_GTYPE_MULTIPOINT = 5;
    public static final int JGeometry_GTYPE_MULTIPOLYGON = 7;
    public static final int JGeometry_GTYPE_POINT = 1;
    public static final int JGeometry_GTYPE_POLYGON = 3;
	

    // ------------------------------
    
    public void setXMLEntity(XMLEntity xml) throws XMLException {
    	
    	super.setXMLEntity(xml);
    	workingAreaInTablesCS = workingArea;
    	
    	try {
    		int[] ftypes = xml.getIntArrayProperty("fieldTypes");
    		setLyrDefFieldTypes(ftypes);
    	} catch (Exception ex) {
    		logger.warn("Apparently, an old GVP file has been opened," +
    				" field type values are not accurate after this point.");
    	}
    	
    }
    
    public XMLEntity getXMLEntity() {
		// ---------------------

		XMLEntity xml = new XMLEntity();
		xml.putProperty("className", getClass().getName());

		xml.putProperty("catalog", getLyrDef().getCatalogName());

		int aux = userName.indexOf("@");
		if (aux != -1)
			userName = userName.substring(0, aux);
		xml.putProperty("username", userName);

		xml.putProperty("driverclass", ORACLE_JAR_FILE_NAME);

		xml.putProperty("tablename", getTableName());
		xml.putProperty("fields", lyrDef.getFieldNames());
		xml.putProperty("fieldTypes", getLyrDefFieldTypes());
		xml.putProperty("FID", lyrDef.getFieldID());
		xml.putProperty("THE_GEOM", lyrDef.getFieldGeometry());
		xml.putProperty("whereclause", getWhereClause());
		xml.putProperty("SRID", lyrDef.getSRID_EPSG());

		xml.putProperty("host", host);
		xml.putProperty("port", port);
		xml.putProperty("dbName", dbName);
		xml.putProperty("connName", connName);

		if (workingAreaInTablesCS != null) {
			xml.putProperty("minXworkArea", workingAreaInTablesCS.getMinX());
			xml.putProperty("minYworkArea", workingAreaInTablesCS.getMinY());
			xml.putProperty("HworkArea", workingAreaInTablesCS.getHeight());
			xml.putProperty("WworkArea", workingAreaInTablesCS.getWidth());
		}

		return xml;
	}
    
    private int[] getLyrDefFieldTypes() {
    	FieldDescription[] fd = lyrDef.getFieldsDesc();
    	int sz = fd.length;
    	int[] resp = new int[sz];
    	for (int i=0; i<sz; i++) resp[i] = fd[i].getFieldType();
		return resp;
	}
    
    private void setLyrDefFieldTypes(int[] tt) {
    	FieldDescription[] fd = lyrDef.getFieldsDesc();
    	
    	int sz_fd = fd.length;
    	int sz_tt = tt.length;
    	int sz = sz_tt;
    	
    	if (sz_tt != sz_fd) {
    		logger.error("Field count does not match. lyrDef has " + sz_fd + " fields," +
    				" but this method was called with " + sz_tt + " items (?)");
    		sz = Math.min(sz_fd, sz_tt);
    	}
    	
    	for (int i=0; i<sz; i++) lyrDef.getFieldsDesc()[i].setFieldType(tt[i]);
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

    // Overwritten to keep old behavior: returns "schema.table_name" if
    // schema is not current user
	public String getTableName() {
	    return fullTableName; 
	}
	
    private Timestamp flexibleTimeStamp(String s) {
    	
    	String aux = s.replace('-', ' ');
    	aux = aux.replace(':', ' ');
    	aux = aux.replace('.', ' ');
    	// sample: 2007 12 31 23 59 59 9999
    	String[] parts = aux.trim().split(" ");
    	
    	int year;
    	int month;
    	int day;
    	int hour;
    	int minute;
    	int second;
    	int a_nanos;

    	if (parts.length == 7) {

    		try {

    			year = Integer.parseInt(parts[0]) - 1900;
    			month = Integer.parseInt(parts[1]) - 1;
    			day = Integer.parseInt(parts[2]);
    			hour = Integer.parseInt(parts[3]);
    			minute = Integer.parseInt(parts[4]);
    			second = Integer.parseInt(parts[5]);
    			a_nanos = Integer.parseInt(parts[6]);
    			
    		} catch (Exception ex) {
        		logger.debug("Bad time stamp: " + ex.getMessage());
        		return new Timestamp(1970, 1, 1, 0, 0, 0, 0);
    		}

    	} else {
    		
        	if (parts.length == 6) {
        		
        		try {
            		year = Integer.parseInt(parts[0]) - 1900;
            		month = Integer.parseInt(parts[1]) - 1;
            		day = Integer.parseInt(parts[2]);
            		hour = Integer.parseInt(parts[3]);
            		minute = Integer.parseInt(parts[4]);
            		second = Integer.parseInt(parts[5]);
            		a_nanos = 0;
            		
        		} catch (Exception ex) {
        			
            		logger.debug("Bad time stamp: " + ex.getMessage());
            		return new Timestamp(1970, 1, 1, 0, 0, 0, 0);
        		}

        	} else {
        		
        		logger.debug("Bad time stamp: " + s);
        		return new Timestamp(1970, 1, 1, 0, 0, 0, 0);
        		
        	}
    	}
    	
    	return new Timestamp(year, month, day, hour, minute, second, a_nanos);
    }

	public void write(DataWare arg0) throws WriteDriverException, ReadDriverException {
	}
	
	public static String removePrefix(String str) {
		
		int colon_ind = str.indexOf(":");
		if (colon_ind != -1) {
			return str.substring(colon_ind + 1);
		} else {
			return str;
		}
	}

    
    	
    	private class AnEmptyFeatureIterator implements IFeatureIterator {
		public boolean hasNext() throws ReadDriverException { return false; }
		public IFeature next() throws ReadDriverException { return null; }
		public void closeIterator() throws ReadDriverException { }		
	}
    	
        private Value objToValue(Object obj, int idFld) {
        	
            if (obj == null) {
            	return ValueFactory.createNullValue();
            } else {
            	
            	String objToString = obj.toString();

                if (obj instanceof String) {
                    objToString = (String) obj;
                    return ValueFactory.createValue(objToString);
                } else {
                    if (obj instanceof ROWID) {
                        objToString = ((ROWID) obj).stringValue();
                        return ValueFactory.createValue(objToString);
                    } else {
                        if (obj instanceof STRUCT) {
                            objToString = "STRUCT";
                            return ValueFactory.createValue(objToString);
                        } else {
                            if (obj instanceof TIMESTAMP) {
                            	TIMESTAMP aux = (TIMESTAMP) obj;
                                objToString = aux.stringValue();
                                Timestamp ts = flexibleTimeStamp(objToString);
                                return ValueFactory.createValue(ts);

                            } else {

                            	// last try
                            	int _type = -1;
    							try {
    								_type = getFieldType(idFld);
    	                            if (_type == Types.DATE) {
    	                            	objToString = objToString.replace('-', '/');
    	                            }
                                	return ValueFactory.createValueByType(objToString, _type);
                                } catch (Exception ex) {
                                	logger.debug("Failed to create Value: _type = "
                                			+ _type + ", objToString = " + objToString);
                                	return ValueFactory.createNullValue();
                                }
                                
                            }
                        }
                    }
                }
            }
        }

        public Value[] getAttributes(ResultSet rs, boolean use_main_metadata) {
            Value[] res = null;

            int fcount = 0;

            try {
            	if (use_main_metadata) {
            		fcount = metaData.getColumnCount();
            	} else {
            		fcount = rs.getMetaData().getColumnCount();
            	}
                
                res = new Value[fcount];

                for (int i = 0; i < fcount; i++) {
                    Object obj = rs.getObject(i + 1);
                    res[i] = objToValue(obj, i);
                }

            } catch (Exception se) {
            	logger.error("While getting resultset attribute values: " + se.getMessage());
            	res = new Value[fcount];
            	for (int i=0; i<fcount; i++) res[i] = ValueFactory.createNullValue();
            }

            return res;
        }    	
        
	public boolean canWriteGeometry(int gvSIGgeometryType) {
		if (writer == null) {
			return true;
		} else {
			return writer.canWriteGeometry(gvSIGgeometryType);
		}
	}

	public static String getTableExistsSql(DBLayerDefinition dbLayerDef) {
		
        return "SELECT * FROM " + dbLayerDef.getTableName();
	}

	public int getAdaptedFetchSize() {
		return adaptedFetchSize;
	}

	public void setAdaptedFetchSize(int v) {
		adaptedFetchSize = v;
	}
        
    

}
