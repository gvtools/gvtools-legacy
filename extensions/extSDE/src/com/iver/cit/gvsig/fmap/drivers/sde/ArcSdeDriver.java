/*
 * Created on 13-may-2005
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
package com.iver.cit.gvsig.fmap.drivers.sde;

import java.awt.Component;
import java.awt.geom.Rectangle2D;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.esri.sde.sdk.client.SeColumnDefinition;
import com.esri.sde.sdk.client.SeConnection;
import com.esri.sde.sdk.client.SeDefs;
import com.esri.sde.sdk.client.SeException;
import com.esri.sde.sdk.client.SeExtent;
import com.esri.sde.sdk.client.SeFilter;
import com.esri.sde.sdk.client.SeLayer;
import com.esri.sde.sdk.client.SeObjectId;
import com.esri.sde.sdk.client.SeQuery;
import com.esri.sde.sdk.client.SeQueryInfo;
import com.esri.sde.sdk.client.SeRegisteredColumn;
import com.esri.sde.sdk.client.SeRow;
import com.esri.sde.sdk.client.SeShape;
import com.esri.sde.sdk.client.SeShapeFilter;
import com.esri.sde.sdk.client.SeSqlConstruct;
import com.esri.sde.sdk.client.SeTable;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.Messages;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.ICanReproject;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.db.utils.ConnectionWithParams;
import com.iver.cit.gvsig.fmap.drivers.db.utils.SingleVectorialDBConnectionManager;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.swing.JPasswordDlg;


/**
 * Driver of ArcSDE.
 * @author Vicente Caballero Navarro
 */
public class ArcSdeDriver implements ICanReproject, IWriteable, IVectorialSDEDriver, ObjectDriver{
    protected static Hashtable poolPassw = new Hashtable();
    private SeLayer layer;
    private SeQuery query=null;
    private SeQuery queryAux=null;
    private SeSqlConstruct sqlConstruct;
    private SeColumnDefinition[] colDefs;
    private SeRow row = null;
    private long numReg = -1;
    private Rectangle2D fullExtent = null;
    private Hashtable hashRelate = null;
    private Hashtable hashRelateInverted = null;
    private int idSpatialColumn = -1;
    private String[] fields;
    private int[] fieldTypes;
    private long lastRowServed = -1;
//    private SeConnection alphanumericConnection = null;
    private IConnection spatialConnection=null;
    private String strEPSG = "-1";
    private String originalEPSG = "-1";
    private String sqlTotal;
    private long posActual = -1;
    private SeRow cachedRow = null;
    protected DBLayerDefinition lyrDef = null;
    protected Rectangle2D workingArea;
    protected String driverClass;
    protected String className;
    protected String catalogName;
	private ArcSdeWriter writer;
	private String FIDfield;
	private String geometryField;
	private String whereClause;
	private String strSRID;
	private SeConnection alphanumericConnection;

    /**
     * Recorre el recordset creando una tabla Hash que usaremos para relacionar
     * el número de un registro con su identificador único. Debe ser llamado
     * en el setData justo después de crear el recorset principal
     */
    protected void doRelateID_FID() {
        hashRelate = new Hashtable();
        hashRelateInverted = new Hashtable();

        	if (row == null) {
                System.out.println(" No rows fetched");

                return;
            }

            long index = 0;
            int fid=getLyrDef().getIdFieldID();
            Value value=getFieldValue(index,fid);

            while (value != null) {
                //SeShape shpVal = row.getShape(idSpatialColumn);
                //SeObjectId objID = shpVal.getFeatureId();
            	String theKey = value.toString();
            	Long indexLong=new Long(index);
                hashRelate.put(theKey, indexLong);
                hashRelateInverted.put(indexLong, new Long(Long.valueOf(theKey).longValue()-1));
                index++;
                value = getFieldValue(index,fid);


            }

            numReg = index;
    }
    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getFullExtent()
     */
    public Rectangle2D getFullExtent() {
		return fullExtent;
	}
    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getShape(int)
     */
    public IGeometry getShape(int index) {
        SeRow row;

        try {
            row = obtainRow(index);
            if (row==null)
            	return null;
            SeShape spVal = row.getShape(idSpatialColumn);
            IGeometry geom = ArcSdeFeatureIterator.getGeometry(spVal);
            return geom;
        } catch (SeException e) {
        	NotificationManager.addError(e);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.sde.IVectorialSDEDriver#getFeatureIterator(java.lang.String)
     */
    public IFeatureIterator getFeatureIterator(String sql){
        return null;
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getFeatureIterator(java.awt.geom.Rectangle2D, java.lang.String)
     */
    public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG) {
        /*
         *   Generate a rectangular shape that will be used as a filter
         */
        try {
            SeShape shape = new SeShape(layer.getCoordRef());
            SeExtent extent=layer.getExtent();
            shape.generateRectangle(extent);

            SeShape[] shapes = new SeShape[1];
            shapes[0] = shape;

            /*
             *   Retrieve all the shapes that are contained within the
             *   rectangles envelope.
             */
//            SeShapeFilter[] filters = new SeShapeFilter[1];
//            SeShapeFilter filter = null;
//
//            filter = new SeShapeFilter(layer.getQualifiedName(),
//                    layer.getSpatialColumn(), shape, SeFilter.METHOD_ENVP);
//            filters[0] = filter;

            SeQuery spatialQuery = null;
            SeSqlConstruct sqlCons = new SeSqlConstruct(layer.getQualifiedName());

            spatialQuery = new SeQuery(alphanumericConnection, fields, sqlCons);

            /*
             *   Set spatial constraints
             */
//            spatialQuery.setSpatialConstraints(SeQuery.SE_OPTIMIZE, false,
//                filters);

            spatialQuery.prepareQuery();

            spatialQuery.execute();

            return new ArcSdeFeatureIterator(spatialQuery,getLyrDef().getFieldID());
        } catch (SeException e) {
        	NotificationManager.addError(e);
            return null;
        }
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#close()
     */
    public void close() {
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#open()
     */
    public void open() {
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getRowIndexByFID(com.iver.cit.gvsig.fmap.core.IFeature)
     */
    public int getRowIndexByFID(IFeature FID) {
        int resul;
        String strID = FID.getID();
        Long rowIndex = (Long) hashRelate.get(strID);

        if (rowIndex == null) {
            System.err.println("Error: No se ha encontrado " + strID +
                " dentro del Hash");
        }
        resul = rowIndex.intValue();
        return resul;
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getGeometryField(java.lang.String)
     */
    public String getGeometryField(String fieldName) {
        return layer.getSpatialColumn();
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getShapeCount()
     */
    public int getShapeCount() {
        return (int)numReg;
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
        return "gvSIG SDE driver";
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getFields()
     */
    public String[] getFields() {
		String[] attributes = new String[fields.length - 1];
		for (int i = 1; i < fields.length; i++)
			attributes[i - 1] = fields[i];
		return attributes;
	}

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getTableName()
     */
    public String getTableName() {
        try {
			return layer.getQualifiedName();
		} catch (SeException e) {
			e.printStackTrace();
		}
		return layer.getName();
    }

    private synchronized SeRow obtainRow(long rowIndex) throws SeException {
       if (rowIndex != posActual) {
            if (rowIndex == 0) {
                if (query != null) {
                    if (query.inProgress()) {
                        query.close();
                    }
                    query = new SeQuery(((ConnectionSDE)spatialConnection).getConnection(), fields, sqlConstruct);
                    query.prepareQuery();
                    query.execute();
                }
                lastRowServed = -1;
            }

            if (lastRowServed == (rowIndex - 1)) {
                row = query.fetch();
                lastRowServed++;
            } else {
                if (queryAux != null && queryAux.inProgress()) {
                	queryAux.close();
                }

                queryAux = new SeQuery(((ConnectionSDE)spatialConnection).getConnection(), fields, sqlConstruct);

                SeObjectId rowID = new SeObjectId(((Long)hashRelateInverted.get(new Long(rowIndex + 1))).longValue());
                try{
                	row = queryAux.fetchRow(layer.getQualifiedName(), rowID, fields);
                }catch (Exception e) {
					row = obtainRow(rowIndex+1);
				}
            }
            posActual = rowIndex;
            cachedRow = row;
        }

        return cachedRow;
    }

    /* (non-Javadoc)
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long, int)
     */
    public Value getFieldValue(long rowIndex, int fieldId) {
        SeRow row;
        Value val = null;

        try {
            int idFieldArcSDE = fieldId+1; // SIEMPRE CONTANDO CON
            // QUE NOS HAN PASADO EL PRIMER CAMPO EL DE SHAPE
            row = obtainRow(rowIndex);
            if (row==null)
            	return null;
            int dataType = colDefs[idFieldArcSDE].getType();

            switch (dataType) {
            case SeColumnDefinition.TYPE_INT16:
                val = ValueFactory.createValue(row.getShort(idFieldArcSDE)
                                                  .intValue());

                break;

            case SeColumnDefinition.TYPE_INT32:
            case SeColumnDefinition.TYPE_INT64:
                val = ValueFactory.createValue(row.getInteger(idFieldArcSDE)
                                                  .intValue());

                break;

            case SeColumnDefinition.TYPE_FLOAT32:
                val = ValueFactory.createValue(row.getFloat(idFieldArcSDE)
                                                  .floatValue());

                break;

            case SeColumnDefinition.TYPE_FLOAT64:
                val = ValueFactory.createValue(row.getDouble(idFieldArcSDE)
                                                  .doubleValue());

                break;

            case SeColumnDefinition.TYPE_DATE:
                val = ValueFactory.createValue(row.getTime(idFieldArcSDE)
                                                  .getTime());

                break;

            case SeColumnDefinition.TYPE_STRING:
                String strAux = row.getString(idFieldArcSDE);

                if (strAux == null) {
                    strAux = "";
                }

                val = ValueFactory.createValue(strAux);

                break;
            case SeColumnDefinition.TYPE_NSTRING:
                String nstrAux = row.getNString(idFieldArcSDE);
                if (nstrAux == null) {
                    nstrAux = "";
                }

                val = ValueFactory.createValue(nstrAux);

                break;
            }

            return val;

        } catch (SeException e) {
        	NotificationManager.addError(e);
        } catch (NullPointerException e) {
        	Logger.getLogger(this.getClass()).error("concurrent_access",e);
        }

        return ValueFactory.createNullValue();
    }

    /* (non-Javadoc)
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldCount()
     */
    public int getFieldCount(){
        return fields.length - 1;
    }

    /* (non-Javadoc)
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldName(int)
     */
    public String getFieldName(int fieldId) {
        return fields[fieldId + 1];
    }

    /* (non-Javadoc)
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getRowCount()
     */
    public long getRowCount() {
        return numReg;
    }

    /* (non-Javadoc)
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldType(int)
     */
    public int getFieldType(int i) {
        return fieldTypes[i+1];
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getPrimaryKeys()
     */
    public int[] getPrimaryKeys() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#write(com.hardcode.gdbms.engine.data.edition.DataWare)
     */
    public void write(DataWare arg0) {
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getFeatureIterator(java.awt.geom.Rectangle2D, java.lang.String, java.lang.String[])
     */
    public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG,
        String[] alphaNumericFieldsNeeded) {
        /*
        *   Generate a rectangular shape that will be used as a filter
        */
        try {
            SeShape shape = new SeShape(layer.getCoordRef());
            SeExtent extent=layer.getExtent();
            shape.generateRectangle(extent);

            SeShape[] shapes = new SeShape[1];
            shapes[0] = shape;

            /*
             *   Retrieve all the shapes that are contained within the
             *   rectangles envelope.
             */
//            SeShapeFilter[] filters = new SeShapeFilter[1];
//            SeShapeFilter filter = null;
//
//            filter = new SeShapeFilter(layer.getQualifiedName(),
//                    layer.getSpatialColumn(), shape, SeFilter.METHOD_ENVP);
//            filters[0] = filter;

            SeQuery spatialQuery = null;
            SeSqlConstruct sqlCons = new SeSqlConstruct(layer.getQualifiedName());

            spatialQuery = new SeQuery(alphanumericConnection, fields, sqlCons);

            /*
             *   Set spatial constraints
             */
//            spatialQuery.setSpatialConstraints(SeQuery.SE_OPTIMIZE, false,
//                filters);

            spatialQuery.prepareQuery();

            spatialQuery.execute();

            return new ArcSdeFeatureIterator(spatialQuery,getLyrDef().getFieldID());
        } catch (SeException e) {
        	NotificationManager.addError(e);

            return null;
        }
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#isWritable()
     */
    public boolean isWritable() {
        return false;
    }
    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#setData(com.iver.cit.gvsig.fmap.drivers.IConnection, com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition)
     */
    public void setData(IConnection connection, DBLayerDefinition lyrDef) throws DBException {
    	 String[] fieldsAux=null;
         FieldDescription[] fds=lyrDef.getFieldsDesc();
    	 if ((fds != null) && (fds.length != 0)) {
             if (!fds[0].getFieldName().equals(lyrDef.getFieldGeometry())) {
                 fieldsAux = new String[fds.length + 1];
                 fieldsAux[0] = lyrDef.getFieldGeometry();

                 for (int k = 1; k < fds.length+1; k++) {
                     fieldsAux[k] = fds[k - 1].getFieldName();
                 }
             }else{
            	 fieldsAux = new String[fds.length];
            	 for (int k = 0; k < fds.length; k++) {
                     fieldsAux[k] = fds[k].getFieldName();
                 }
             }
         }
        lyrDef.setFieldNames(fieldsAux);
        lyrDef.setIdFieldID(lyrDef.getIdField(lyrDef.getFieldID())-1);
        setLyrDef(lyrDef);
        // Conexión:
        String server = lyrDef.getHost();
        int instance = lyrDef.getPort();
        String database = lyrDef.getDataBase();
        String user = lyrDef.getUser();
        String[] cols = lyrDef.getFieldNames();
        String tableName = lyrDef.getComposedTableName();
        String password;
        if (lyrDef.getPassword()!=null){
        	password=lyrDef.getPassword();
        }else{
        	String keyPool = getName().toLowerCase() + "_" + lyrDef.getHost().toLowerCase()
        		+ "_" + lyrDef.getPort() + "_" + lyrDef.getDataBase().toLowerCase()
        		+ "_" + lyrDef.getUser().toLowerCase();
        	password=(String)poolPassw.get(keyPool);
        }
        try {
            alphanumericConnection =new SeConnection(server, instance, database, user, password);
//            alphanumericConnection.startTransaction();
            spatialConnection = connection;
//            ((ConnectionSDE)spatialConnection).getConnection().startTransaction();
        } catch (SeException e) {
        	NotificationManager.addError(e);
            return;
		}
        // Fetching data
        String layerName = tableName;
        SeObjectId layerID = null;
        String strSpatialColumn = "";

        try {
            Vector theLayers = ((ConnectionSDE)spatialConnection).getConnection().getLayers();

            for (int i = 0; i < theLayers.size(); i++) {
                SeLayer layer = (SeLayer) theLayers.elementAt(i);

                if (layer.getQualifiedName().compareToIgnoreCase(layerName) == 0) {
                	layerID = layer.getID();
                    strSpatialColumn = layer.getSpatialColumn();
                    this.layer=layer;
                    break;
                }


            }

            if (layerID == null) {
                System.err.println("Capa no encontrada");

                return;
            }

            // layerName = layer.getName();
//            layer = new SeLayer(((ConnectionSDE)spatialConnection).getConnection(), layerName, strSpatialColumn);

            //TODO aquí se puede saber la proyección
            //layer.getCoordRef().getSrid();

//            if ((cols != null) && (cols.length != 0)) {
//                if (!cols[0].equals(lyrDef.getFieldGeometry())) {
//                    this.fields = new String[cols.length + 1];
//                    this.fields[0] = lyrDef.getFieldGeometry();
//
//                    for (int i = 1; i < fields.length; i++) {
//                        fields[i] = cols[i - 1];
//                    }
//                } else {
//                    fields = cols;
//                }
//            } else {
//                SeSqlConstruct sqlConstruct = new SeSqlConstruct(tableName);
//                sqlTotal = sqlConstruct.getWhere();
//
//                SeTable table1 = new SeTable(conn, tableName);
//
//                /*
//                 *   Get the table's column definition.
//                 */
//                SeColumnDefinition[] tableDef = table1.describe();
//                this.fields = new String[tableDef.length];
//
//                /*
//                 *   Store the names of all the table's columns in the
//                 *   String array cols. This array specifies the columns
//                 *   to be retrieved from the database.
//                 */
//                int idField = 1;
//
//                for (int i = 0; i < tableDef.length; i++) {
//                    if (tableDef[i].getType() == SeColumnDefinition.TYPE_SHAPE) {
//                        this.fields[0] = tableDef[i].getName();
//                    } else {
//                        this.fields[idField] = tableDef[i].getName();
//                        idField++;
//                    }
//                }
//
//                lyrDef.setFieldNames(fields);
//            }
            fields=cols;
            sqlConstruct = new SeSqlConstruct(layerName);

            //      Create a query stream between the client and server
            query = new SeQuery(alphanumericConnection, fields, sqlConstruct);
            query.prepareQuery();
            query.execute();
            row = query.fetch();
            if (row == null) {
                System.out.println("La tabla " + getName() +
                    " no tiene registros");

                return;
            }

            colDefs = row.getColumns();
            lyrDef.setShapeType(obtainShapeType(row.getShape(0).getType()));//ColumnDef(0).getType()));
            fieldTypes = new int[colDefs.length];

            for (int colNum = 0; colNum < colDefs.length; colNum++) {
                SeColumnDefinition colDef = colDefs[colNum];
                int dataType = colDef.getType();

                switch (dataType) {
                case SeColumnDefinition.TYPE_SHAPE:

                    if (colNum != 0) {
                        System.err.println(
                            "Por favor, el campo de shapes deber ser el primero");

                        return;
                    }

                    idSpatialColumn = colNum;

                    break;

                case SeColumnDefinition.TYPE_INT16:
                case SeColumnDefinition.TYPE_INT32:
                case SeColumnDefinition.TYPE_INT64:
                    fieldTypes[colNum] = Types.INTEGER;

                    break;

                case SeColumnDefinition.TYPE_FLOAT32:
                    fieldTypes[colNum] = Types.FLOAT;

                    break;

                case SeColumnDefinition.TYPE_FLOAT64:
                    fieldTypes[colNum] = Types.DOUBLE;

                    break;

                case SeColumnDefinition.TYPE_DATE:
                    fieldTypes[colNum] = Types.DATE;

                    break;

                default:
                    fieldTypes[colNum] = Types.VARCHAR;

                    break;
                }
            }
            fields=cols;
            SeQuery extentQuery = new SeQuery(((ConnectionSDE)spatialConnection).getConnection(), fields, sqlConstruct);
            SeQueryInfo queryInfo = new SeQueryInfo();
            queryInfo.setConstruct(sqlConstruct);

            SeExtent seExtent = extentQuery.calculateLayerExtent(queryInfo);
            fullExtent = new Rectangle2D.Double(seExtent.getMinX(),
                    seExtent.getMinY(),
                    seExtent.getMaxX() - seExtent.getMinX(),
                    seExtent.getMaxY() - seExtent.getMinY());
            extentQuery.close();

            doRelateID_FID();
        } catch (SeException e) {
        	throw new DBException(e);
        }
    }

    private int obtainShapeType(int type) {
        int shapeType = -1;

        switch (type) {
        case SeLayer.TYPE_POINT:
            shapeType = FShape.POINT;

            break;

        case SeLayer.TYPE_LINE:
        case SeLayer.TYPE_SIMPLE_LINE:
        case SeLayer.TYPE_MULTI_LINE:
        case SeLayer.TYPE_MULTI_SIMPLE_LINE:
            shapeType = FShape.LINE;

            break;

        case SeLayer.TYPE_POLYGON:
        case SeLayer.TYPE_MULTI_POLYGON:
            shapeType = FShape.POLYGON;

            break;

        default:
            shapeType = FShape.MULTI;
        }

        return shapeType;
    }

    public String getCompleteWhere() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getConnectionStringBeginning()
     */
    public String getConnectionStringBeginning() {
        return "sde";
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getDefaultPort()
     */
    public int getDefaultPort() {
        return 5151;
    }

    public String getSourceProjection(IConnection conn,DBLayerDefinition dbld) {
        return originalEPSG;
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.core.ICanReproject#getDestProjection()
     */
    public String getDestProjection() {
        return strEPSG;
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.core.ICanReproject#setDestProjection(java.lang.String)
     */
    public void setDestProjection(String toEPSG) {
        this.strEPSG = toEPSG;
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.core.ICanReproject#canReproject(java.lang.String)
     */
    public boolean canReproject(String toEPSGdestinyProjection) {
        //		 TODO POR AHORA, REPROYECTA SIEMPRE gvSIG.
        return false;
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.edition.IWriteable#getWriter()
     */
    public IWriter getWriter() {
    	 if (writer == null) {
             writer = new ArcSdeWriter();
             writer.setDriver(this);
             writer.setSeLayer(layer);
             try {
				writer.initialize(getLyrDef());
			} catch (InitializeWriterException e) {
				NotificationManager.addError(e);
			}
         }

         return writer;
    }

    public String getSqlTotal() {
		return sqlTotal;
	}

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getXMLEntity()
     */
    public XMLEntity getXMLEntity() {
        DBLayerDefinition lyrDef = getLyrDef();
        XMLEntity xml = new XMLEntity();
        xml.putProperty("className", this.getClass().getName());
        xml.putProperty("driverclass", this.getClass().getName());
        xml.putProperty("catalog", lyrDef.getCatalogName());
        ConnectionWithParams cwp =
        	SingleVectorialDBConnectionManager.instance().findConnection(getConnection());

        // TODO: NO DEBEMOS GUARDAR EL NOMBRE DE USUARIO Y CONTRASEÑA
        // AQUI. Hay que utilizar un pool de conexiones
        // y pedir al usuario que conecte a la base de datos
        // en la primera capa. En el resto, usar la conexión
        // creada con anterioridad.
        String userName = lyrDef.getUser();
        if (userName != null) {
            int aux = userName.indexOf("@");

            if (aux != -1) {
                userName = userName.substring(0, aux);
            }
        }

        xml.putProperty("username", userName);

        xml.putProperty("tablename", getTableName());
        xml.putProperty("fields", fields);
        xml.putProperty("ID", lyrDef.getFieldID());
        xml.putProperty("idfield",lyrDef.getIdFieldID());
        xml.putProperty("THE_GEOM", lyrDef.getFieldGeometry());
        xml.putProperty("whereclause", getWhereClause());
        xml.putProperty("SRID", lyrDef.getSRID_EPSG());

        xml.putProperty("host", lyrDef.getHost());
        xml.putProperty("port", lyrDef.getPort());
        xml.putProperty("dbName", lyrDef.getDataBase());
        xml.putProperty("connName", cwp.getName());

        xml.putProperty("typeShape",lyrDef.getShapeType());

        if (getWorkingArea() != null) {
            xml.putProperty("minXworkArea", getWorkingArea().getMinX());
            xml.putProperty("minYworkArea", getWorkingArea().getMinY());
            xml.putProperty("HworkArea", getWorkingArea().getHeight());
            xml.putProperty("WworkArea", getWorkingArea().getWidth());
        }

        return xml;
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#setXMLEntity(com.iver.utiles.XMLEntity)
     */
    public void setXMLEntity(XMLEntity xml) throws XMLException {
        className = xml.getStringProperty("className");
        catalogName = xml.getStringProperty("catalog");
        lyrDef = new DBLayerDefinition();

        if (xml.contains("username")) {
            lyrDef.setUser(xml.getStringProperty("username"));
        }

        driverClass = xml.getStringProperty("driverclass");
        lyrDef.setTableName(xml.getStringProperty("tablename"));
        fields = xml.getStringArrayProperty("fields");
        lyrDef.setFieldNames(fields);
        lyrDef.setFieldID(xml.getStringProperty("ID"));
        if (xml.contains("idfield"))
        	lyrDef.setIdFieldID(xml.getIntProperty("idfield"));
        lyrDef.setFieldGeometry(xml.getStringProperty("THE_GEOM"));
        lyrDef.setWhereClause(xml.getStringProperty("whereclause"));
        lyrDef.setSRID_EPSG(xml.getStringProperty("SRID"));

        if (xml.contains("host")) {
            lyrDef.setHost(xml.getStringProperty("host"));
            lyrDef.setPort(Integer.parseInt(xml.getStringProperty("port")));
            lyrDef.setDataBase(xml.getStringProperty("dbName"));
            lyrDef.setConnectionName(xml.getStringProperty("connName"));
        }
        lyrDef.setShapeType(xml.getIntProperty("typeShape"));
        if (xml.contains("minXworkArea")) {
            double x = xml.getDoubleProperty("minXworkArea");
            double y = xml.getDoubleProperty("minYworkArea");
            double H = xml.getDoubleProperty("HworkArea");
            double W = xml.getDoubleProperty("WworkArea");
            workingArea = new Rectangle2D.Double(x, y, W, H);
        }

        lyrDef.setCatalogName(catalogName);

        if (workingArea != null) {
            lyrDef.setWorkingArea(workingArea);
        }

        setLyrDef(lyrDef);
    }

    /* (non-Javadoc)
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldWidth(int)
     */
    public int getFieldWidth(int fieldId) {
        return colDefs[fieldId+1].getSize();
    }

    public void setLyrDef(DBLayerDefinition lyrDef) {
        this.lyrDef =lyrDef;
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#setWorkingArea(java.awt.geom.Rectangle2D)
     */
    public void setWorkingArea(Rectangle2D rect) {
		this.workingArea = rect;
	}

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getWorkingArea()
     */
    public Rectangle2D getWorkingArea() {
		return workingArea;
	}

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getShapeType()
     */
    public int getShapeType() {
        return lyrDef.getShapeType();
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.VectorialDatabaseDriver#getWhereClause()
     */
    public String getWhereClause() {
        if (lyrDef.getWhereClause() == null) {
            return "";
        }

        return lyrDef.getWhereClause().toUpperCase();
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getLyrDef()
     */
    public DBLayerDefinition getLyrDef() {
    	// Esto hace saltar un error.
    	//    	if (this.spatialConnection != null){
    	//    		lyrDef.setSeConnection();
    	//    	}
        return lyrDef;
    }

    /**
     * Empty method called when the layer is going to be removed from the view.
     * Subclasses can overwrite it if needed.
     */
    public void remove() {
    }

    /* (non-Javadoc)
    * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#reLoad()
    */
    public void reload() throws ReloadDriverException{
    	try {
			load();
		} catch (ReadDriverException e) {
			throw new ReloadDriverException(this.className,e);
		}
	}

    /* (non-Javadoc)
     * @see com.hardcode.gdbms.engine.data.driver.GDBMSDriver#setDataSourceFactory(com.hardcode.gdbms.engine.data.DataSourceFactory)
     */
    public void setDataSourceFactory(DataSourceFactory dsf) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#load()
     */
    public void load() throws ReadDriverException{
    	try {
            if (driverClass != null)
            	Class.forName(driverClass);

            String _drvName = getName();

            String keyPool = _drvName.toLowerCase() + "_" + lyrDef.getHost().toLowerCase()
            + "_" + lyrDef.getPort() + "_" + lyrDef.getDataBase().toLowerCase()
            + "_" + lyrDef.getUser().toLowerCase();

            IConnection newConn = null;
            String clave = null;
            ConnectionWithParams cwp = null;

            if (poolPassw.containsKey(keyPool)) {

                clave = (String) poolPassw.get(keyPool);
                cwp = SingleVectorialDBConnectionManager.instance().getConnection(
     					_drvName, lyrDef.getUser(), clave, lyrDef.getConnectionName(),
     					lyrDef.getHost(), String.valueOf(lyrDef.getPort()), lyrDef.getDataBase(), true);

            } else {

            	 cwp = SingleVectorialDBConnectionManager.instance().getConnection(
      					_drvName, lyrDef.getUser(), null, lyrDef.getConnectionName(),
      					lyrDef.getHost(), String.valueOf(lyrDef.getPort()), lyrDef.getDataBase(), false);

                if (cwp.isConnected()) {

                	poolPassw.put(keyPool, cwp.getPw());

                } else {

                    JPasswordDlg dlg = new JPasswordDlg();
                    dlg.setLocationRelativeTo((Component)PluginServices.getMainFrame());
                    String strMessage = Messages.getString("conectar_jdbc");
                    String strPassword = Messages.getString("password");
                    dlg.setMessage(strMessage
                    		+ " ["
                    		+ _drvName + ", "
                    		+ lyrDef.getHost() + ", "
                    		+ String.valueOf(lyrDef.getPort()) + ", "
                    		+ lyrDef.getDataBase() + ", "
                    		+ lyrDef.getUser() + "]. "
                    		+ strPassword
                    		+ "?");
                    dlg.setVisible(true);
                    clave = dlg.getPassword();
                    if (clave == null)
                        return;
                    poolPassw.put(keyPool, clave);

                    cwp.connect(clave);
                }
            }

            newConn = cwp.getConnection();
//            newConn.setAutoCommit(false);

            DBLayerDefinition lyrDef = new DBLayerDefinition();
            if (getLyrDef() == null) {
	            lyrDef.setCatalogName(catalogName);
	            lyrDef.setTableName(getTableName());
	            lyrDef.setFieldNames(fields);
	            lyrDef.setFieldID(FIDfield);
	            lyrDef.setFieldGeometry(geometryField);
	            lyrDef.setWhereClause(whereClause);
	            // lyrDef.setClassToInstantiate(driverClass);
	            if (workingArea != null)
	                lyrDef.setWorkingArea(workingArea);

	            lyrDef.setSRID_EPSG(strSRID);
            } else {
            	lyrDef = getLyrDef();
            }

            setData(newConn, lyrDef);
        } catch (ClassNotFoundException e) {
        	throw new ReadDriverException(this.className,e);
        } catch (DBException e) {
        	throw new ReadDriverException(this.className,e);

		}
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getConnection()
     */
    public IConnection getConnection() {
        return spatialConnection;
    }
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getConnectionString(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getConnectionString(
    		String host,
    		String port,
    		String dbname,
    		String user,
    		String pw) {

		String resp = getConnectionStringBeginning()+ ":" + host.toLowerCase();

		if (dbname.trim().length() > 0) {
			resp += ":" + port;
		} else {
			resp += ":" + getDefaultPort();
		}

		resp += ":" + dbname.toLowerCase();
		return resp;
    }
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getAllFields(com.iver.cit.gvsig.fmap.drivers.IConnection, java.lang.String)
	 */
	public String[] getAllFields(IConnection conex, String tableName) throws DBException {
		try {
			SeTable table = null;
			Vector tables=((ConnectionSDE)conex).getConnection().getTables(SeDefs.SE_SELECT_PRIVILEGE);
			for (int i=0;i<tables.size();i++) {
				if (tableName.equals(((SeTable)tables.get(i)).getQualifiedName())) {
					table=(SeTable)tables.get(i);
					break;
				}
			}
			SeRegisteredColumn[] columns=table.getColumnList();
			String[] fieldNames=new String[columns.length];
			for (int i=0; i < columns.length; i++){
				fieldNames[i]=columns[i].getName();
			}
			return fieldNames;
		} catch (SeException e) {
			throw new DBException(e);
		}
	}
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getAllFieldTypeNames(com.iver.cit.gvsig.fmap.drivers.IConnection, java.lang.String)
	 */
	public String[] getAllFieldTypeNames(IConnection conex, String tableName) throws DBException {
		try {
			SeTable table = null;
			Vector tables=((ConnectionSDE)conex).getConnection().getTables(SeDefs.SE_SELECT_PRIVILEGE);
			for (int i=0;i<tables.size();i++) {
				if (tableName.equals(((SeTable)tables.get(i)).getQualifiedName())) {
					table=(SeTable)tables.get(i);
					break;
				}
			}
			SeRegisteredColumn[] columns=table.getColumnList();
			String[] fieldNames=new String[columns.length];
			for (int i=0; i < columns.length; i++){
				fieldNames[i]=String.valueOf(columns[i].getType());
			}
			return fieldNames;
		} catch (SeException e) {
			throw new DBException(e);
		}
	}
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getIdFieldsCandidates(com.iver.cit.gvsig.fmap.drivers.IConnection, java.lang.String)
	 */
	public String[] getIdFieldsCandidates(IConnection conn2, String tableName) throws DBException {
		return getAllFields(conn2,tableName);
	}
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getGeometryFieldsCandidates(com.iver.cit.gvsig.fmap.drivers.IConnection, java.lang.String)
	 */
	public String[] getGeometryFieldsCandidates(IConnection conn2, String tableName) {
		return new String[]{"SHAPE"};//getAllFields(conn2,tableName);
	}
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getTableNames(com.iver.cit.gvsig.fmap.drivers.IConnection, java.lang.String)
	 */
	public String[] getTableNames(IConnection conex, String dbName) throws DBException {
		 try {
			Vector theLayers = ((ConnectionSDE)conex).getConnection().getLayers();
			TreeMap ret = new TreeMap();
			for (int i = 0; i < theLayers.size(); i++) {
				SeLayer layer = (SeLayer) theLayers.elementAt(i);
				ret.put(layer.getQualifiedName(), layer.getQualifiedName());
			}
			return (String[]) ret.keySet().toArray(new String[0]);
			} catch (SeException e) {
				throw new DBException(e);
			}
		}
	 public String[] getTableFields(IConnection conex,String tableName) throws DBException{
			try {
				SeTable table = new SeTable(((ConnectionSDE)conex).getConnection(),tableName);
				SeRegisteredColumn[] columns=table.getColumnList();
				String[] fieldNames=new String[columns.length];
				for (int i=0; i < columns.length; i++){
					fieldNames[i]=columns[i].getName();
				}
				return fieldNames;
			} catch (SeException e) {
				throw new DBException(e);
			}
		}
	 
	public boolean canRead(IConnection iconn, String tablename)
			throws SQLException {
		return true;
	}

}
