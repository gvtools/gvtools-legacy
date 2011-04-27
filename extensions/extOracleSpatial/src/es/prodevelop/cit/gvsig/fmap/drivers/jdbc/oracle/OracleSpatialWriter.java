/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integraci�n de Tecnolog�as SL
 *   Conde Salvatierra de �lava , 34-10
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
import java.sql.SQLException;

import oracle.sql.STRUCT;

import org.apache.log4j.Logger;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultRow;
import com.iver.cit.gvsig.fmap.core.FPolygon2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.ConnectionJDBC;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IFieldManager;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.writers.AbstractWriter;


/**
 * Oracle Spatial geometry writer. Used during editing and when a vectorial layer has to be
 * exported to Oracle.
 *
 * @author jldominguez
 *
 */
public class OracleSpatialWriter extends AbstractWriter
    implements ISpatialWriter, IFieldManager {
	
    public static final String NAME = "Oracle Spatial Writer";
    private static Logger logger = Logger.getLogger(OracleSpatialWriter.class.getName());
    private Rectangle2D bbox = null;
    private OracleSpatialDriver driver;
    private int rowIndex = 0;
    private String oracleSRID = "";
    private int lyrShapeType = FShape.NULL;
    private int dimensions = 2;
    private boolean aguBien = true;
    private boolean storeWithSrid = false;
    private boolean tableCreation = false;
    private boolean isGeoCS = false;
    private String geoColName = OracleSpatialDriver.DEFAULT_GEO_FIELD;
    
    private OracleFieldManager fieldManager;

    /**
     * Constructor used when a whole layer is going to be exported.
     *
     */
    public OracleSpatialWriter() {
        tableCreation = true;
    }

    /**
     * Constructor used when a table is being edited.
     * @param rowcount the table's current row countt
     */
    public OracleSpatialWriter(long rowcount) {
        rowIndex = (int) rowcount;
        tableCreation = false;
    }
    
	public void initialize(ITableDefinition lyrD) throws InitializeWriterException {
		super.initialize(lyrD);
		DBLayerDefinition dbdef = (DBLayerDefinition) lyrD;
		
		String[] forbidden = {
				dbdef.getFieldID(),
				dbdef.getFieldGeometry(),
				OracleSpatialDriver.ORACLE_ID_FIELD };
		
		ConnectionJDBC _conn = (ConnectionJDBC) dbdef.getConnection();
		fieldManager = new OracleFieldManager(
				_conn.getConnection(),
				getTableDefinition().getName(), forbidden);
		
	}

    public boolean canWriteAttribute(int sqlType) {
        return true;
    }

    public boolean canWriteGeometry(int type) {
    	
    	if (!isGeoCS) return true; 
    	
    	if ((type == FShape.ARC) || (type == FShape.CIRCLE)) {
    		return false;
    	} else {
    		return true;
    	}
    }
    
    public static boolean createEmptyTable(
    		DBLayerDefinition lyr_def,
    		IConnection the_conn,
    		Rectangle2D rect) throws SQLException {
    	
            String srid_epsg = lyr_def.getSRID_EPSG();
            boolean with_srid = true;
            String _ora_srid = "NULL";
            
            try {
				_ora_srid = OracleSpatialDriver.epsgSridToOracleSrid(srid_epsg);
			} catch (Exception e1) {
				logger.error("Unknown EPSG code: " + srid_epsg);
				with_srid = false;
			}

			boolean table_exists = true;
			
            String _sql_table_exists = OracleSpatialDriver.getTableExistsSql(lyr_def);
            
            try {
                java.sql.PreparedStatement ps =
                	((ConnectionJDBC) the_conn).getConnection().prepareStatement(_sql_table_exists);
                ps.execute();
                ps.close();
            }
            catch (SQLException ex) {
                logger.info("Table did not exist: " + lyr_def.getTableName());
                table_exists = false;
            }
            
            if (table_exists) {
            	throw new SQLException("Table " + lyr_def.getTableName() + " already exists. Creation cancelled.");
            }

            String _sql_rem_meta = OracleSpatialDriver.getRemoveMetadataSql(lyr_def);
            String _sql_drop = OracleSpatialDriver.getDropTableSql(lyr_def);
            String _sql_creation = OracleSpatialDriver.getTableCreationSql(lyr_def);
            String _sql_index = OracleSpatialDriver.getIndexCreationSql(lyr_def);

            String _sql_meta = OracleSpatialDriver.getMetadataUpdateSql(
            		lyr_def.getUser(),
            		lyr_def.getTableName(),
            		_ora_srid, rect, 2, with_srid);

            boolean removed = true;

            try {
                java.sql.PreparedStatement ps =
                	((ConnectionJDBC) the_conn).getConnection().prepareStatement(_sql_drop);
                ps.execute();
                ps.close();
            }
            catch (SQLException ex) {
                logger.info("Table did not exist: " + lyr_def.getTableName());
                removed = false;
            }

            if (removed) {
                logger.info("Table existed and was deleted: " + lyr_def.getTableName());
            }
            
            try {
                java.sql.PreparedStatement ps = ((ConnectionJDBC) the_conn).getConnection().prepareStatement(_sql_creation);
                ps.execute();
                ps.close();
            }
            catch (SQLException ex) {
                logger.error("Error while executing SQL for table creation: " +
                    ex.getMessage(), ex);
                return false;
            }

            try {
                java.sql.PreparedStatement ps = ((ConnectionJDBC)the_conn).getConnection().prepareStatement(_sql_rem_meta);
                ps.execute();
                ps.close();
            }
            catch (SQLException ex) {
                logger.error("Error while executing SQL for metadata removal: " +
                    ex.getMessage(), ex);
            }

            try {
                java.sql.PreparedStatement ps = ((ConnectionJDBC)the_conn).getConnection().prepareStatement(_sql_meta);
                ps.execute();
                ps.close();
            }
            catch (SQLException ex) {
                logger.error(
                    "Error while executing SQL for metadata insertion: " +
                    ex.getMessage(), ex);
                return false;
            }

            try {
                java.sql.PreparedStatement ps = ((ConnectionJDBC)the_conn).getConnection().prepareStatement(_sql_index);
                ps.execute();
                ps.close();
            }
            catch (SQLException ex) {
                logger.error("Error while executing SQL for index creation: " +
                    ex.getMessage(), ex);
                return false;
            }
            
            return true;
    }

    public void preProcess() throws StartWriterVisitorException {
    	
    	IConnection conn = ((DBLayerDefinition) tableDef).getConnection();
    	
        if (tableCreation) {
            String srid_epsg = ((DBLayerDefinition) tableDef).getSRID_EPSG();


            try {
				oracleSRID = OracleSpatialDriver.epsgSridToOracleSrid(srid_epsg);
			} catch (Exception e1) {
				// not found
				logger.error("unknown EPSG code: " + srid_epsg);
				storeWithSrid = false;
			}

            String _sql_rem_meta =
            	OracleSpatialDriver.getRemoveMetadataSql((DBLayerDefinition) tableDef);
            String _sql_drop =
            	OracleSpatialDriver.getDropTableSql((DBLayerDefinition) tableDef);
            String _sql_creation =
            	OracleSpatialDriver.getTableCreationSql((DBLayerDefinition) tableDef);
            String _sql_index =
            	OracleSpatialDriver.getIndexCreationSql((DBLayerDefinition) tableDef);

            int dim_aux = dimensions;

            String _sql_meta = OracleSpatialDriver.getMetadataUpdateSql(
            		driver.getUserName(),
            		((DBLayerDefinition) tableDef).getTableName(),
                    oracleSRID, bbox, dim_aux, storeWithSrid);

            //dimensions);
            boolean removed = true;

            try {
                java.sql.PreparedStatement ps = ((ConnectionJDBC)conn).getConnection().prepareStatement(_sql_drop);
                ps.execute();
                ps.close();
            }
            catch (SQLException ex) {
                logger.info("Table did not exist: " +
                    ((DBLayerDefinition) tableDef).getTableName());
                removed = false;
            }

            if (removed) {
                logger.info("Table existed and was deleted: " +
                    ((DBLayerDefinition) tableDef).getTableName());
            }
            
            try {
                java.sql.PreparedStatement ps = ((ConnectionJDBC)conn).getConnection().prepareStatement(_sql_creation);
                ps.execute();
                ps.close();
            }
            catch (SQLException ex) {
                logger.error("Error while executing SQL for table creation: " +
                    ex.getMessage(), ex);
				throw new StartWriterVisitorException("Oracle layer for table: " + tableDef.getName(), ex);
            }

            try {
                java.sql.PreparedStatement ps = ((ConnectionJDBC)conn).getConnection().prepareStatement(_sql_rem_meta);
                ps.execute();
                ps.close();
            }
            catch (SQLException ex) {
                logger.error("Error while executing SQL for metadata removal: " +
                    ex.getMessage(), ex);
				throw new StartWriterVisitorException("Oracle layer for table: " + tableDef.getName(), ex);
            }

            try {
                java.sql.PreparedStatement ps = ((ConnectionJDBC)conn).getConnection().prepareStatement(_sql_meta);
                ps.execute();
                ps.close();
            }
            catch (SQLException ex) {
                logger.error(
                    "Error while executing SQL for metadata insertion: " +
                    ex.getMessage(), ex);
				throw new StartWriterVisitorException("Oracle layer for table: " + tableDef.getName(), ex);
            }

            try {
                java.sql.PreparedStatement ps = ((ConnectionJDBC)conn).getConnection().prepareStatement(_sql_index);
                ps.execute();
                ps.close();
            }
            catch (SQLException ex) {
                logger.error("Error while executing SQL for index creation: " +
                    ex.getMessage(), ex);
				throw new StartWriterVisitorException("Oracle layer for table: " + tableDef.getName(), ex);
            }

            rowIndex = 0;

        }
        else {
        	try {
				alterTable();
			} catch (WriteDriverException e) {
				logger.error("While altering table: " + getTableDefinition().getName());
				throw new StartWriterVisitorException("TABLE: " + getTableDefinition().getName(), e);
			}
        }
        
        try {
        	((ConnectionJDBC)conn).getConnection().setAutoCommit(false);
        	((ConnectionJDBC)conn).getConnection().commit();
        }
        catch (SQLException e) {
            logger.error("Error while setting auto commit FALSE: " +
                e.getMessage());
			throw new StartWriterVisitorException("Oracle layer for table: " + tableDef.getName(), e);
        }
        
    }

    public void process(IRowEdited _row) throws VisitorException {
        int status = _row.getStatus();

        switch (status) {
        case IRowEdited.STATUS_ADDED:
            addRow(_row);

            /*
             // TODO when addRowInCreation() is implemented:
            if (tableCreation) {
                    addRowInCreation(_row);
            } else {
                    addRow(_row);
            }
            */
            break;

        case IRowEdited.STATUS_DELETED:
            deleteRow(_row);

            break;

        case IRowEdited.STATUS_MODIFIED:
            updateRow(_row);

            break;

        case IRowEdited.STATUS_ORIGINAL:
            originalRow(_row);

            break;
        }
    }

    private void addRow(IRowEdited irow) throws VisitorException {
        DefaultRowEdited row = (DefaultRowEdited) irow;
        IFeature ifeat = (IFeature) row.getLinkedRow();
        String _sql_insert = "";

        IConnection conn = ((DBLayerDefinition) tableDef).getConnection();
        try {
            java.sql.PreparedStatement ps = null; 
            // Shape shp = ifeat.getGeometry().getInternalShape();
            IGeometry _ig = ifeat.getGeometry();
            
            Shape shp = _ig.getInternalShape();
            int simple_lyr_type = lyrShapeType % FShape.Z;
            if ((simple_lyr_type == FShape.LINE) && (shp instanceof FPolygon2D)) {
            	_ig = OracleSpatialUtils.makeLinear((FPolygon2D) shp);
            }
            
            
            // STRUCT st = driver.shapeToStruct(shp, lyrShapeType, conn, oracleSRID, false);
            STRUCT st;

            if ((oracleSRID == null) || (driver.getDestProjectionOracleCode().compareToIgnoreCase(oracleSRID) == 0)) {

                st = OracleSpatialDriver.iGeometryToSTRUCT(_ig,
                        lyrShapeType, conn, oracleSRID, storeWithSrid, aguBien,
                        isGeoCS);
            } else {
            	String viewSrid = driver.getDestProjectionOracleCode();
            	boolean isViewSridGedetic = driver.getIsDestProjectionGeog();
                st = OracleSpatialDriver.iGeometryToSTRUCT(_ig,
                        lyrShapeType, conn, viewSrid, storeWithSrid, aguBien,
                        isViewSridGedetic);
                st = OracleSpatialUtils.reprojectGeometry(conn, st, oracleSRID);
            }

            if (st == null) {
                _sql_insert = OracleSpatialDriver.getRowInsertSql(ifeat,
                        (DBLayerDefinition) tableDef, rowIndex, geoColName, "NULL");            
            	// ps.setObject(1, Types.NULL);
            	ps = ((ConnectionJDBC)conn).getConnection().prepareStatement(_sql_insert);
            } else {
                _sql_insert = OracleSpatialDriver.getRowInsertSql(ifeat,
                        (DBLayerDefinition) tableDef, rowIndex, geoColName, "?");   
                ps = ((ConnectionJDBC)conn).getConnection().prepareStatement(_sql_insert);
            	ps.setObject(1, st);
            }
            
            ps.execute();
            ps.close();
            rowIndex++;
        }
        catch (Exception ex) {
            logger.error("Error while executing SQL for row insertion: " +
                ex.getMessage() + " SQL: " + _sql_insert, ex);
			throw new VisitorException("Oracle layer for table: " + tableDef.getName(), ex);
        }
    }

    private void deleteRow(IRowEdited irow) throws VisitorException {
        DefaultRowEdited _row = (DefaultRowEdited) irow;
        DefaultRow row = (DefaultRow) _row.getLinkedRow();

        String id = row.getAttribute(0).toString();

        String _sql_delete = OracleSpatialDriver.getRowDeleteSql((DBLayerDefinition) tableDef,
                id);

        IConnection conn = ((DBLayerDefinition) tableDef).getConnection();

        try {
            java.sql.PreparedStatement ps = ((ConnectionJDBC)conn).getConnection().prepareStatement(_sql_delete);
            ps.execute();
            ps.close();

            // rowIndex--;

            // getDriver().deleteRow(irow.getID());
        }
        catch (SQLException ex) {
            logger.error("Error while executing SQL for row deletion: " +
                ex.getMessage(), ex);
			throw new VisitorException("Oracle layer for table: " + tableDef.getName(), ex);
        }
    }

    private void updateRow(IRowEdited irow) throws VisitorException {
        DefaultRowEdited row = (DefaultRowEdited) irow;
        IFeature ifeat = (IFeature) row.getLinkedRow();

        // ---------------------------------------------------------------
        // --------- DETECTS INSERTS THAT LOOK LIKE UPDATES    -----------
        // ---------------------------------------------------------------
        String aux_id = ifeat.getID();
        boolean is_actually_update = false;

        try {
            Integer.parseInt(aux_id);
        }
        catch (NumberFormatException nfe) {
            is_actually_update = true;
        }

        if (!is_actually_update) {
            addRow(irow);

            return;
        }

        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        String _sql_update = OracleSpatialDriver.getRowUpdateSql(ifeat,
                (DBLayerDefinition) tableDef, rowIndex, geoColName);
        
        logger.debug("############ SQL UPDATE = " + _sql_update);

        IConnection conn = ((DBLayerDefinition) tableDef).getConnection();

        try {
            java.sql.PreparedStatement ps = ((ConnectionJDBC)conn).getConnection().prepareStatement(_sql_update);

            // Shape shp = ifeat.getGeometry().getInternalShape();
            IGeometry _ig = ifeat.getGeometry();
            STRUCT st;
            
            logger.debug("############ driver.getDestProjectionOracleCode() = "
            		+ driver.getDestProjectionOracleCode());
            logger.debug("############ oracleSRID = " + oracleSRID);

            if ((oracleSRID == null) || (driver.getDestProjectionOracleCode().compareToIgnoreCase(oracleSRID) == 0)) {

                st = OracleSpatialDriver.iGeometryToSTRUCT(_ig,
                        lyrShapeType, conn, oracleSRID, storeWithSrid, aguBien,
                        isGeoCS);

            } else {
            	
            	String viewSrid = driver.getDestProjectionOracleCode();
            	boolean isViewSridGedetic = driver.getIsDestProjectionGeog();
                st = OracleSpatialDriver.iGeometryToSTRUCT(_ig,
                        lyrShapeType, conn, viewSrid, storeWithSrid, aguBien,
                        isViewSridGedetic);
                st = OracleSpatialUtils.reprojectGeometry(conn, st, oracleSRID);
            }

            ps.setObject(1, st);
            ps.execute();
            ps.close();

            // rowIndex++;
        }
        catch (Exception ex) {
            logger.error("Error while executing SQL for row update: " +
                ex.getMessage(), ex);
			throw new VisitorException("Oracle layer for table: " + tableDef.getName(), ex);
        }
    }

    private void originalRow(IRowEdited irow) {
        logger.error("Original row called!");
    }

    public void postProcess() throws StopWriterVisitorException {
        if (tableCreation) {
            IConnection conn = ((DBLayerDefinition) tableDef).getConnection();

            try {
            	((ConnectionJDBC)conn).getConnection().commit();
            } catch (SQLException e) {
                logger.error(
                    "!!! While performing table creation MAIN COMMIT: " +
                    e.getMessage());
                throw new StopWriterVisitorException("Oracle layer for table: " + tableDef.getName(), e);                
            }
        }
    }

    public boolean canAlterTable() {
        return true;
    }

    public boolean canSaveEdits() {
        return true;
    }

    public String getName() {
        return NAME;
    }

    public FieldDescription[] getOriginalFields() {
        return tableDef.getFieldsDesc();
    }

    public void addField(FieldDescription fieldDesc) {
    	fieldDesc.setFieldName(fieldDesc.getFieldName().toUpperCase());
    	fieldManager.addField(fieldDesc);
    }

    public FieldDescription removeField(String fieldName) {
        return fieldManager.removeField(fieldName);
    }

    public void renameField(String antName, String newName) {
    	fieldManager.renameField(antName, newName);
    }

    public boolean alterTable() throws WriteDriverException {
    	return fieldManager.alterTable();
    }

    public FieldDescription[] getFields() {
        return fieldManager.getFields();
    }

    public void setBbox(Rectangle2D b) {
        bbox = b;
    }

    public void setDriver(OracleSpatialDriver d) {
        driver = d;
    }

    public void setLyrShapeType(int shptype) {
        lyrShapeType = shptype;
    }

    public void setDimensions(int dims) {
        dimensions = dims;
    }

    public void setAguBien(boolean agu_bien) {
        aguBien = agu_bien;
    }

    public void setStoreWithSrid(boolean with) {
        storeWithSrid = with;
    }

    public void setGeoCS(boolean g) {
        isGeoCS = g;
    }

    public void setGeoColName(String g) {
        geoColName = g;
    }

    public OracleSpatialDriver getDriver() {
        return driver;
    }

    public void setSRID(String s) {
        oracleSRID = s;
    }
}
