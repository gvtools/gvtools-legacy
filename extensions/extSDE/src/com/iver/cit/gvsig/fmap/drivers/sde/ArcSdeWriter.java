package com.iver.cit.gvsig.fmap.drivers.sde;

import java.io.IOException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

import com.esri.sde.sdk.client.SeColumnDefinition;
import com.esri.sde.sdk.client.SeConnection;
import com.esri.sde.sdk.client.SeCoordinateReference;
import com.esri.sde.sdk.client.SeDelete;
import com.esri.sde.sdk.client.SeException;
import com.esri.sde.sdk.client.SeInsert;
import com.esri.sde.sdk.client.SeLayer;
import com.esri.sde.sdk.client.SeObjectId;
import com.esri.sde.sdk.client.SeRow;
import com.esri.sde.sdk.client.SeShape;
import com.esri.sde.sdk.client.SeUpdate;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.IFieldManager;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.writers.AbstractWriter;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class ArcSdeWriter extends AbstractWriter implements ISpatialWriter,
    IFieldManager {
    private int numRows;
    private DBLayerDefinition lyrDef;
    private SeConnection conex;

    //private Statement st;
    private boolean bCreateTable;
    private IVectorialSDEDriver driver;
    private SeLayer selayer;

    //private ArcSde postGisSQL = new ArcSde();
    // private double flatness;
    //private JdbcFieldManager fieldManager;

    /**
     * Useful to create a layer from scratch Call setFile before using this
     * function
     *
     * @param lyrD
     * @throws InitializeWriterException
     *
     * @throws EditionException
     */
    public void initialize(ITableDefinition lyrD) throws InitializeWriterException{
        super.initialize(lyrD);
        this.lyrDef = (DBLayerDefinition)lyrD;
        conex = (SeConnection) lyrDef.getConnection();

        //
        //		try {
        //			//st = conex.createStatement();
        //
        //			if (bCreateTable) {
        //				try {
        //					st.execute("DROP TABLE " + lyrDef.getTableName() + ";");
        //				} catch (SQLException e1) {
        //					// Si no existe la tabla, no hay que borrarla.
        //				}
        //
        //				String sqlCreate = postGisSQL.getSqlCreateSpatialTable(lyrDef,
        //						lyrDef.getFieldsDesc(), true);
        //				System.out.println("sqlCreate =" + sqlCreate);
        //				st.execute(sqlCreate);
        //
        //				String sqlAlter = postGisSQL.getSqlAlterTable(lyrDef);
        //				System.out.println("sqlAlter =" + sqlAlter);
        //				st.execute(sqlAlter);
        //				// CREATE TABLE PARKS ( PARK_ID int4, PARK_NAME varchar(128),
        //				// PARK_DATE date, PARK_TYPE varchar(2) );
        //				// SELECT AddGeometryColumn('parks_db', 'parks', 'park_geom',
        //				// 128,
        //				// 'MULTIPOLYGON', 2 );
        //
        //				/*
        //				 * BEGIN; INSERT INTO ROADS_GEOM (ID,GEOM,NAME ) VALUES
        //				 * (1,GeometryFromText('LINESTRING(191232 243118,191108
        //				 * 243242)',-1),'Jeff Rd'); INSERT INTO ROADS_GEOM (ID,GEOM,NAME )
        //				 * VALUES (2,GeometryFromText('LINESTRING(189141 244158,189265
        //				 * 244817)',-1),'Geordie Rd'); COMMIT;
        //				 */
        //				conex.commit();
        //			}
        //			conex.setAutoCommit(false);
        //			fieldManager = new JdbcFieldManager(conex, lyrDef.getTableName());
        //
        //		} catch (SQLException e) {
        //			e.printStackTrace();
        //			throw new EditionException(e);
        //		}
    }

    /**
     * DOCUMENT ME!
     * @throws StartWriterVisitorException
     *
     * @throws EditionException DOCUMENT ME!
     */
    public void preProcess() throws StartWriterVisitorException {
        numRows = 0;

        // ATENTION: We will transform (in PostGIS class; doubleQuote())
        // to UTF-8 strings. Then, we tell the PostgreSQL server
        // that we will use UTF-8, and it can translate
        // to its charset
        // Note: we have to translate to UTF-8 because
        // the server cannot manage UTF-16
        //ResultSet rsAux;
        try {
            //conex.rollbackTransaction();
            conex.startTransaction();
            alterTable();

            //rsAux = conex.getRelease().st.executeQuery("SHOW server_encoding;");
            //rsAux.next();
            //String serverEncoding = rsAux.getString(1);
            //System.out.println("Server encoding = " + serverEncoding);
            // st.execute("SET CLIENT_ENCODING TO 'UNICODE';");
            // Intentamos convertir nuestras cadenas a ese encode.
            //	        postGisSQL.setEncoding(serverEncoding);
        } catch (SeException e) {
        	throw new StartWriterVisitorException(lyrDef.getName(),e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param _row DOCUMENT ME!
     * @throws VisitorException
     *
     * @throws EditionException DOCUMENT ME!
     */
    public void process(IRowEdited _row) throws VisitorException {
        int status = _row.getStatus();

        try {
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
        } catch (SeException e) {
        	NotificationManager.addError(e);
            throw new VisitorException(lyrDef.getName(),e);
        } catch (IOException e) {
        	NotificationManager.addError(e);
            throw new VisitorException(lyrDef.getName(),e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param _row DOCUMENT ME!
     */
    private void originalRow(IRowEdited _row) {
        // TODO Auto-generated method stub
    }

    /**
     * DOCUMENT ME!
     *
     * @param irow DOCUMENT ME!
     *
     * @throws SeException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    private void updateRow(IRowEdited irow) throws SeException, IOException {
        //		 the values associated with the given record.
        SeUpdate update = new SeUpdate(conex);
        String[] cols = lyrDef.getFieldNames();

        //			String featureId = feature.getID().substring(
        //					feature.getID().lastIndexOf('.') + 1,
        //					feature.getID().length());
        update.toTable(selayer.getQualifiedName(), cols,
            "SHAPE" + " = " + irow.getID());
        update.setWriteMode(true);

        SeRow row = update.getRowToSet();

        // Set values on rows here.....
        for (int i = 0; i < cols.length; i++) {
            if (cols[i].equals("SHAPE")) {
                setRowValue(row, i,
                    ((IFeature) irow.getLinkedRow()).getGeometry());
            } else {
                setRowValue(row, i, irow.getAttribute(i));
            }
        }

        update.execute();
        update.close();
    }

    /**
     * DOCUMENT ME!
     *
     * @param _row DOCUMENT ME!
     *
     * @throws SeException DOCUMENT ME!
     */
    private void deleteRow(IRowEdited _row) throws SeException {
        //				if ((this.features == null)
        //						|| (this.currentIndex >= this.features.size())) {
        //					throw new IOException("No current feature available.");
        //				}
        //				if (this.notInserted) {
        //					this.features.remove(this.currentIndex--);
        //					this.notInserted = false;
        //				} else {
        //					Feature feature = (Feature) this.features.get(this.currentIndex);
        //					PooledConnection connection = null;
        //						connection = getConnection();
        SeDelete seDelete = new SeDelete(conex);

        long featureId = Long.parseLong(_row.getID());
        SeObjectId objectID = new SeObjectId(featureId);
        seDelete.byId(selayer.getQualifiedName(), objectID);

        //this.dataStore.fireRemoved(feature);
        //					} catch (Exception e) {
        //						throw new SOException(e.getMessage());
        //					} finally {
        conex.close();

        //					}
        //				}
    }

    /**
     * DOCUMENT ME!
     *
     * @param irow DOCUMENT ME!
     *
     * @throws EditionException DOCUMENT ME!
     */
    private void addRow(IRowEdited irow){
        try {
            //Feature feature = (Feature) this.features.get(this.currentIndex);
            //FeatureType featureType = feature.getFeatureType();
            //AttributeType[] attributeTypes = featureType.getAttributeTypes();
            //connection = getConnection();
            //if (this.notInserted) {
            // We must insert the record into ArcSDE
            SeInsert insert = new SeInsert(conex);
            String[] cols = lyrDef.getFieldNames();
            insert.intoTable(selayer.getQualifiedName(), cols);
            insert.setWriteMode(true);

            SeRow row = insert.getRowToSet();

            // Now set the values for the new row here...
            for (int i = 0; i < cols.length; i++) {
                if (cols[i].equals("SHAPE")) {
                    setRowValue(row, i,
                        ((IFeature) irow.getLinkedRow()).getGeometry());
                } else {
                    setRowValue(row, i, irow.getAttribute(i));
                }
            }

            // Now "commit" the changes.
            insert.execute();
            insert.close();

            //this.dataStore.fireAdded(feature);
            //}
            //		 else {
            //					// The record is already inserted, so we will be updating
            //					// the values associated with the given record.
            //					SeUpdate update = new SeUpdate(connection);
            //					String[] cols = getColumns(attributeTypes, connection);
            //					String featureId = feature.getID().substring(
            //							feature.getID().lastIndexOf('.') + 1,
            //							feature.getID().length());
            //					update.toTable(this.layer.getQualifiedName(), cols,
            //							this.spatialColumnName + " = " + featureId);
            //					update.setWriteMode(true);
            //
            //					SeRow row = update.getRowToSet();
            //
            //					// Set values on rows here.....
            //					for (int i = 0; i < cols.length; i++) {
            //						Object value = feature
            //								.getAttribute(this.mutableAttributeIndexes[i]
            //										.intValue());
            //						setRowValue(row, i, value);
            //					}
            //
            //					update.execute();
            //					update.close();
            //
            //				}
        } catch (Exception e) {
            //				LOGGER.log(Level.WARNING, e.getMessage(), e);
            //				if (LOGGER.isLoggable(Level.FINE)) {
            //					e.printStackTrace();
            //				}
            //				throw new DataSourceException(e.getMessage(), e);
        } finally {
            try {
                conex.close();
            } catch (SeException e) {

            	NotificationManager.addError(e);
            }
        }
    }

    /**
     * Used to set a value on an SeRow object. The values is converted to the
     * appropriate type based on an inspection of the SeColumnDefintion
     * object.
     *
     * @param row
     * @param index
     * @param value
     *
     * @throws SeException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    private void setRowValue(SeRow row, int index, Object value)
        throws SeException{
        SeColumnDefinition seColumnDefinition = null;
        seColumnDefinition = row.getColumnDef(index);

        switch (seColumnDefinition.getType()) {
        case SeColumnDefinition.TYPE_INT32:
        case SeColumnDefinition.TYPE_INT64:
        {
            if (value != null) {
                row.setInteger(index, new Integer(value.toString()));
            } else {
                row.setInteger(index, null);
            }

            break;
        }

        case SeColumnDefinition.TYPE_INT16: {
            if (value != null) {
                row.setShort(index, new Short(value.toString()));
            } else {
                row.setShort(index, null);
            }

            break;
        }

        case SeColumnDefinition.TYPE_FLOAT32: {
            if (value != null) {
                row.setFloat(index, new Float(value.toString()));
            } else {
                row.setFloat(index, null);
            }

            break;
        }

        case SeColumnDefinition.TYPE_FLOAT64: {
            if (value != null) {
                row.setDouble(index, new Double(value.toString()));
            } else {
                row.setDouble(index, null);
            }

            break;
        }

        case SeColumnDefinition.TYPE_STRING:
        case SeColumnDefinition.TYPE_NSTRING:
            if (value != null) {
                row.setString(index, value.toString());
            } else {
                row.setString(index, null);
            }

            break;
        case SeColumnDefinition.TYPE_DATE: {
            if (value != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime((Date) value);
                row.setTime(index, calendar);
            } else {
                row.setTime(index, null);
            }

            break;
        }

        case SeColumnDefinition.TYPE_SHAPE: {
            if (value != null) {
                try {
                    SeCoordinateReference coordRef = selayer.getCoordRef();
                    IGeometry geom = (IGeometry) value;
                    SeShape shape = ArcSdeFeatureIterator.constructShape(geom,
                            coordRef);
                    row.setShape(index, shape);
                } catch (Exception e) {
                	NotificationManager.addError(e);
                }
            } else {
                row.setShape(index, null);
            }

            break;
        }
        }
    }

    /**
     * DOCUMENT ME!
     * @throws StopWriterVisitorException
     *
     * @throws EditionException DOCUMENT ME!
     */
    public void postProcess() throws StopWriterVisitorException {
        try {
            conex.commitTransaction();
        } catch (SeException e) {
        	NotificationManager.addError(e);
            throw new StopWriterVisitorException(lyrDef.getName(),e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getName() {
        return "gvSIG SDE Writer";
    }

    /**
     * DOCUMENT ME!
     *
     * @param gvSIGgeometryType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean canWriteGeometry(int gvSIGgeometryType) {
        switch (gvSIGgeometryType) {
        case FShape.POINT:
            return true;

        case FShape.LINE:
            return true;

        case FShape.POLYGON:
            return true;

        case FShape.ARC:
            return false;

        case FShape.ELLIPSE:
            return false;

        case FShape.MULTIPOINT:
            return true;

        case FShape.TEXT:
            return false;
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param sqlType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean canWriteAttribute(int sqlType) {
        switch (sqlType) {
        case Types.DOUBLE:
        case Types.FLOAT:
        case Types.INTEGER:
        case Types.BIGINT:
            return true;

        case Types.DATE:
            return true;

        case Types.BIT:
        case Types.BOOLEAN:
            return true;

        case Types.VARCHAR:
        case Types.CHAR:
        case Types.LONGVARCHAR:
            return true;
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the bCreateTable.
     */
    public boolean isCreateTable() {
        return bCreateTable;
    }

    /**
     * DOCUMENT ME!
     *
     * @param createTable The bCreateTable to set.
     */
    public void setCreateTable(boolean createTable) {
        bCreateTable = createTable;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public FieldDescription[] getOriginalFields() {
        return lyrDef.getFieldsDesc();
    }

    /**
     * DOCUMENT ME!
     *
     * @param fieldDesc DOCUMENT ME!
     */
    public void addField(FieldDescription fieldDesc) {
        //		fieldManager.addField(fieldDesc);
    }

    /**
     * DOCUMENT ME!
     *
     * @param fieldName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public FieldDescription removeField(String fieldName) {
        //		return fieldManager.removeField(fieldName);
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param antName DOCUMENT ME!
     * @param newName DOCUMENT ME!
     */
    public void renameField(String antName, String newName) {
        //		fieldManager.renameField(antName, newName);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws EditionException DOCUMENT ME!
     */
    public boolean alterTable() {
        //		return fieldManager.alterTable();
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public FieldDescription[] getFields() {
        //		return fieldManager.getFields();
        return lyrDef.getFieldsDesc();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean canAlterTable() {
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean canSaveEdits() {
        //		try {
        //			return !conex.isReadOnly();
        //		} catch (SQLException e) {
        //			// TODO Auto-generated catch block
        //			e.printStackTrace();
        //			return false;
        //		}
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param driver DOCUMENT ME!
     */
    public void setDriver(ArcSdeDriver driver) {
        this.driver = driver;
    }

    /**
     * DOCUMENT ME!
     *
     * @param layer DOCUMENT ME!
     */
    public void setSeLayer(SeLayer layer) {
        this.selayer = layer;
    }
}
