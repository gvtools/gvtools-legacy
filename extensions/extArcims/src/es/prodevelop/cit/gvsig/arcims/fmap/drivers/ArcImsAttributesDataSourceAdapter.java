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
package es.prodevelop.cit.gvsig.arcims.fmap.drivers;

import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.BitSet;

import org.apache.log4j.Logger;
import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;
import org.gvsig.remoteClient.arcims.utils.FieldInformation;
import org.gvsig.remoteClient.arcims.utils.GetFeaturesTags;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.IDataSourceListener;
import com.hardcode.gdbms.engine.data.SourceInfo;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.data.object.ObjectSourceInfo;
import com.hardcode.gdbms.engine.data.persistence.DataSourceLayerMemento;
import com.hardcode.gdbms.engine.data.persistence.Memento;
import com.hardcode.gdbms.engine.data.persistence.MementoException;
import com.hardcode.gdbms.engine.values.IntValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueCollection;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.fmap.layers.FBitSet;


public class ArcImsAttributesDataSourceAdapter implements ArcImsAlphanumericDataSource {
    private static Logger logger = Logger.getLogger(ArcImsAttributesDataSourceAdapter.class.getName());
    // private ArcImsInMemoryAttsTableDriver dataDriver;

    // private File csvFile;
    private String tableName;
    private DataSourceFactory dsFactory = null;
    private ObjectSourceInfo sourceInfo;
    private FMapFeatureArcImsDriver parentDriver;
    private int rowsPerRequest = 10;

    // private int privateKeyIndex = 0;

    // array of Value[]
    // private AttributesDataa attsData; 
    public ArcImsAttributesDataSourceAdapter(ArrayList col_Names,
        ArrayList col_Types, Value[] ids, DataSourceFactory dsf,
        FMapFeatureArcImsDriver drv, String forcedName) {
        // attsData = new AttributesDataa(col_Names, col_Types, ids, drv);
        parentDriver = drv;
        dsFactory = dsf;

        if (forcedName == null) {
            long timeid = System.currentTimeMillis();
            tableName = "table_" + timeid;
        }
        else {
            tableName = forcedName;
        }

        ArcImsInMemoryAttsTableDriver dataDriver = new ArcImsInMemoryAttsTableDriver(tableName, col_Names,
                col_Types, parentDriver, this);
        parentDriver.setDataTable(dataDriver);

        sourceInfo = new ObjectSourceInfo();
        sourceInfo.driver = parentDriver;
        sourceInfo.name = tableName;
        sourceInfo.driverName = dataDriver.getName();

        //		dsFactory = new DataSourceFactory();
        //		dsFactory.initialize();
        //		dsFactory.addDataSource(this, tableName);
        try {
            // dataDriver.open(dataDriver.getConnection(), tableName);

            //			String sqlStr = ArcImsSqlUtils.getSqlSentenceCreateTableWithFieldsAndTypes(tableName, col_Names, col_Types, dataDriver.getRowIndexFieldName());
            //			dataDriver.open(sqlStr);
            String idcolname = getIdColumnName(col_Names, col_Types);

            for (int i = 0; i < ids.length; i++) {
                Value id = ids[i];
                int rwcount = (int) dataDriver.getRowCount();

                //				sqlStr = ArcImsSqlUtils.getSqlSentenceSetEmptyRowWithId(
                //						tableName, id, idcolname,
                //						rwcount,
                //						dataDriver.getRowIndexFieldName());
                dataDriver.addEmptyRowWithId(id, idcolname);

                // dataDriver.addInIndexToIdHashMap(rwcount, ((IntValue) id).intValue());
                // logger.debug("Adding id = " + ((IntValue) id).intValue() + " in row = " + rwcount);
                dataDriver.addInIdToIndexHashMap(((IntValue) id).intValue(),
                    rwcount);

                // dsFactory.executeSQL(sqlStr, DataSourceFactory.DATA_WARE_DIRECT_MODE);
                //				dataDriver.execute(sqlStr);
                //				dataDriver.increaseRowCount(false);
            }

            logger.info("Stored " + ids.length + " IDs");
            setRowsPerRequest();
        }
        catch (Exception e) {
            logger.error("While creating in-memory database ", e);
        }
    }

    public void deleteRow(long rowInd) throws DriverException {
        // DriverException de = new DriverException("cannot_delete");
        logger.warn("So far, you cannot delete ");

    }

    public void insertFilledRowAt(long index, Value[] values)
        throws DriverException {
        insertFilledRowAt(index, values, true);
    }

    public void insertFilledRowAt(long index, Value[] values, boolean req)
        throws DriverException {
        // dataDriver.setRequested((int) dataDriver.getRowCount(), req);
        // start();
        parentDriver.getDataTable().addRow(values, req);

        // stop();
        logger.warn("Row was added at the end (not inserted at position " +
            index + ")");

        // this.
        // dataDriver.addRow(dsFactory, values);
        // dataDriver.insertRow((int) index, values);
    }

    public void insertEmptyRowAt(long index) throws DriverException {
        // start();
        int count = parentDriver.getDataTable().getFieldCount();

        // stop();
        Value[] empty = new Value[count];

        for (int i = 0; i < count; i++) {
            empty[i] = ValueFactory.createNullValue();
        }

        insertFilledRowAt(index, empty, false);
    }

    public void start() throws ReadDriverException {
    }

    public void stop() throws ReadDriverException {
    }

    public String getName() {
        return tableName;
    }

    public String getAlias() {
        return tableName;

        // return tableAlias;
    }

    public long[] getWhereFilter() throws IOException {
        // dataDriver.
        return null;

        //		String message = "'Where' filter not used in method: ArcImsAttributesDataSourceAdapter.getWhereFilter()";
        //		IOException ioe = new IOException(message);
        //		throw ioe;
    }

    public DataSourceFactory getDataSourceFactory() {
        return dsFactory;
    }

    public Memento getMemento() throws MementoException {
        DataSourceLayerMemento m = new DataSourceLayerMemento(tableName,
                tableName);

        return m;
    }

    public void setDataSourceFactory(DataSourceFactory arg0) {
        dsFactory = arg0;
    }

    public void setSourceInfo(SourceInfo arg0) {
        sourceInfo = (ObjectSourceInfo) arg0;
    }

    public SourceInfo getSourceInfo() {
        return sourceInfo;
    }

    public String getAsString() throws ReadDriverException {
        //		return attsData.getAsString();
        return parentDriver.getDataTable().toString();
    }

    public void remove() throws WriteDriverException {
        if (dsFactory == null) {
            logger.error(
                "dsFactory is null (?) and cannot receive the remove method ");

            return;
        }
        dsFactory.remove(this);
    }

    public int[] getPrimaryKeys() throws ReadDriverException {
        int[] resp = new int[1];
        resp[0] = parentDriver.getDataTable().getIdFieldInd();

        return resp;
    }

    public ValueCollection getPKValue(long arg0) throws ReadDriverException {
        ValueCollection vc = new ValueCollection();
        try {
			vc.add(ValueFactory.createValue(getRowId((int) arg0)));
		} catch (DriverException e) {
			ReadDriverException ex =
				new ReadDriverException("While doing getPKValue: " + e.getMessage(), e);
			throw ex;
		}

        return vc;
    }

    public String getPKName(int arg0) throws ReadDriverException {
        if (arg0 == 0) {
            return parentDriver.getDataTable().getIdFieldName();
        }
        else {
        	ReadDriverException de =
        		new ReadDriverException("pk_error", new Exception("In get PK name"));
            logger.error("Multiple primary key are never used.");
            throw de;
        }
    }

    public String[] getPKNames() throws ReadDriverException {
        String[] resp = new String[1];
        resp[0] = parentDriver.getDataTable().getIdFieldName();

        return resp;
    }

    public int getPKType(int arg0) throws ReadDriverException {
        if (arg0 == 0) {
            //			return attsData.getFieldType(attsData.getIdIndex());
            return Types.BIGINT;
        }
        else {
        	ReadDriverException de = new ReadDriverException("pk_error", new Exception());
            logger.error("Multiple primary key are never used. ", de);
            throw de;
        }
    }

    public int getPKCardinality() throws ReadDriverException {
        // Multiple primary key are never used.
        return 1;
    }

    public Value[] getRowWithoutDownloading(long row) throws DriverException {
        // start();
        Value[] resp = parentDriver.getDataTable().getRow(dsFactory, (int) row);

        // stop();
        return resp;
    }

    public Value[] getRow(long arg0) throws ReadDriverException {
    	
    	if (parentDriver.getDataTable().isNonRequestedRow((int) arg0)) {
    		
    		// ------------------- if we dont have it, get that and more:
            FBitSet fbs = parentDriver.getDataTable().getThisAndNonRequestedUpTo((int) arg0,
                    rowsPerRequest);

            try {
                parentDriver.requestFeatureAttributesWithoutChecking(fbs);
            }
            catch (ArcImsException e) {
            	ReadDriverException de = new ReadDriverException(e.getMessage(), new Exception());
                logger.error("While getting row ", de);
                throw de;
            }
    		// ----------------------------------------------------------
    	}


        // start();
        Value[] resp = null;
		try {
			resp = parentDriver.getDataTable().getRow(dsFactory, (int) arg0);
		} catch (DriverException e) {
        	ReadDriverException de = new ReadDriverException(e.getMessage(), new Exception());
            logger.error("While getting row ", de);
            throw de;
		}

        // stop();
        return resp;
    }

    public String[] getFieldNames() throws ReadDriverException {
        //		String[] resp = attsData.getFieldNames();
        //		return resp;
        // return null;
        // start();
        int count = parentDriver.getDataTable().getFieldCount();
        String[] resp = new String[count];

        for (int i = 0; i < count; i++) {
            // resp[i] = dataDriver.getFieldName(i);
            resp[i] = parentDriver.getDataTable().getSqlFieldName(i);
        }

        // stop();
        return resp;

        // dataDriver.ge
    }

    public int getFieldIndexByName(String arg0) throws ReadDriverException {
        //		int resp = attsData.getFieldIndexByName(arg0);
        //		return resp;
        // start();
        int resp = -1;
		try {
			resp = parentDriver.getDataTable().getFieldIndexByName(arg0);
		} catch (DriverException e) {
        	ReadDriverException de = new ReadDriverException(e.getMessage(), new Exception());
            logger.error("While getting row ", de);
            throw de;
		}

        // stop();
        return resp;

        // return 0;
    }

    public DataWare getDataWare(int arg0) {
        //		DataWare resp = attsData.getDataWare(arg0);
        //		return resp;
        return null;
    }

    public Value getFieldValue(long row, int colind) throws ReadDriverException {
        if (parentDriver.getDataTable().isNonRequestedRow((int) row)) {
            int[] lim = getBlockLimits((int) row);

            System.err.println("Se ha pedido fila no disponible: " + row);
            System.err.println("Limites: [ " + lim[0] + " , " + lim[1] + " ]");

            // FBitSet fbs = dataDriver.getThisAndNonRequestedUpTo((int) row, rowsPerRequest);
            try {
                parentDriver.requestBlockWithoutChecking(lim);
            }
            catch (ArcImsException e) {
            	ReadDriverException de = new ReadDriverException("In getFieldValue.", e);
                logger.error("While getting row: " + e.getMessage());
                throw de;
            }
        }

        //		Value v = dataDriver.getCachedValue((int) row, colind);
        //		if (v != null) {
        //			return v;
        //		}
        Value resp = parentDriver.getDataTable().getFieldValue(row, colind);

        //		stop();
        //		dataDriver.setCachedValue((int) row, colind, resp);
        return resp;
    }

    public int getFieldCount() throws ReadDriverException {
        //		int resp = attsData.getFieldCount();
        //		return resp;
        // start();
        int resp = parentDriver.getDataTable().getFieldCount();

        // stop();
        return resp;

        // return 0;
    }

    public String getFieldName(int arg0) throws ReadDriverException {
        //		String resp = attsData.getFieldName(arg0);
        //		return resp;
        // start();
        String resp = parentDriver.getDataTable().getSqlFieldName(arg0);

        // stop();
        return resp;

        // return "";
    }

    public long getRowCount() throws ReadDriverException {
        //		long resp = (int) attsData.getRowCount();
        //		return resp;
        // start();
        long resp = (long) parentDriver.getDataTable().getRowCount();

        // stop();
        return resp;

        // return 0;
    }

    public int getFieldType(int arg0) throws ReadDriverException {
        int resp = parentDriver.getDataTable().getFieldType(arg0);
        return resp;
    }

    // **************************
    // new_row[0] must contain ID
    // **************************
    // returns added row index 
    public void updateRow(Value[] new_row, String[] fld_query, int rowind)
        throws DriverException {
        // dataDriver.
        //		start();
        // int resp =
    	parentDriver.getDataTable().updateRow(new_row, fld_query, rowind);

        //		if (! ((new_row.length == fld_query.length)
        //				|| ((new_row.length == getFieldCount()) && (fld_query[0].compareTo("#ALL#") == 0)))) {
        //			DriverException de = new DriverException("array_error");
        //			logger.error("Wrong array sizes while updating row ", de);
        //			throw de;
        //		}
        //		
        //		if (new_row.length == 0) {
        //			DriverException de = new DriverException("array_error");
        //			logger.error("Wrong array sizes while updating row ", de);
        //			throw de;
        //		}
        //		
        //		if (fld_query[0].compareTo("#ALL#") == 0) {
        //			int length = getFieldCount();
        //			String newfldquery[] = new String[length];
        //			for (int i=0; i<length; i++) {
        //				newfldquery[i] = getFieldName(i);
        //			}
        //			return attsData.updateRow(new_row, newfldquery);
        //		}
        //		
        //		return attsData.updateRow(new_row, fld_query);
    }

    public String getIdColName() throws DriverException {
        String resp = parentDriver.getDataTable().getIdFieldName();

        return resp;

        //		return attsData.getIdColName();
        // return "";
    }

    public int getIdIndex() throws DriverException {
        int resp = parentDriver.getDataTable().getIdFieldInd();

        return resp;

        //		return attsData.getIdIndex();
        // return 0;
    }

    public int getRowId(int row) throws DriverException {
        //		start();
        //		int resp = dataDriver.getRowId(dsFactory, row);
        //		stop();
        int resp = parentDriver.getDataTable().getRowId(row);

        return resp;

        // System.out.println("dataDriver.getRowId(dsFactory, " + row + ") = " + dataDriver.getRowId(dsFactory, row));
        // return 0;
        // return dataDriver.getRowId(row);
        //		int resp = -1;
        //		try {
        //			resp =  attsData.getRowId(row);
        //		} catch (DriverException e) {
        //			logger.error("While getting row ID ", e);
        //		}
        //		return resp;
    }

    public int getRowIndex(int id) throws DriverException {
        //		start();
        //		int resp = dataDriver.getRowIndex(dsFactory, id);
        //		stop();
        int resp = parentDriver.getDataTable().getRowIndex(id);

        return resp;
    }

    public boolean isNonRequestedRow(int rowind) {
        //		return attsData.isNonRequestedRow(rowind);
        return parentDriver.getDataTable().isNonRequestedRow(rowind);
    }

    public void write(DataWare arg0) throws ReadDriverException {
        // TODO Auto-generated method stub
    }

    public String getTableName() {
        return tableName;
    }

    //	public String getOriginalColumnName(int idcolindex) {
    //		return dataDriver.getOriginalColumnName(idcolindex);
    //	}
    public void addAsRequested(FBitSet fbs) {
    	parentDriver.getDataTable().addAsRequested(fbs);
    }

    public BitSet getNonRequestedFromHere(int start, int total) {
        return parentDriver.getDataTable().getThisAndNonRequestedUpTo(start, total);
    }

    public String getIdColumnName(ArrayList col_Names, ArrayList col_Types) {
        int coltype;
        int idindex = -1;

        for (int i = 0; i < col_Types.size(); i++) {
            coltype = ((Integer) col_Types.get(i)).intValue();

            if (coltype == FieldInformation.ID) {
                idindex = i;

                break;
            }
        }

        if (idindex == -1) {
            logger.error("ID not found ");
        }

        return (String) col_Names.get(idindex);
    }

    public int getRowsPerRequest() {
        return rowsPerRequest;
    }

    private void setRowsPerRequest() {
        int rowcount = (int) parentDriver.getDataTable().getRowCount();
        int sqroot = Math.round((float) Math.sqrt(1.0 * rowcount));

        rowsPerRequest = sqroot;

        if (rowsPerRequest > GetFeaturesTags.MAX_ROWS_PER_REQUEST) {
            rowsPerRequest = GetFeaturesTags.MAX_ROWS_PER_REQUEST;
        }

        if (rowsPerRequest < GetFeaturesTags.MIN_ROWS_PER_REQUEST) {
            rowsPerRequest = GetFeaturesTags.MIN_ROWS_PER_REQUEST;
        }

        if (rowsPerRequest > rowcount) {
            rowsPerRequest = rowcount;
        }

        logger.info("Rows per request was set to " + rowsPerRequest +
            " (rowcount = " + rowcount + ")");
    }

    public String getOriginalFieldName(int idcolindex) {
        return parentDriver.getDataTable().getOriginalFieldName(idcolindex);
    }

    public boolean isVirtualField(int fieldId) throws ReadDriverException {
        // TODO Auto-generated method stub
        return false;
    }

    public int getFieldWidth(int i) throws ReadDriverException {
        // TODO Auto-generated method stub
        return parentDriver.getDataTable().getFieldWidth(i);
    }

    public int[] getBlockLimits(int row) throws ReadDriverException {
        int[] resp = new int[2];

        int rcount = 0;


        rcount = (int) getRowCount();

        int rperreq = getRowsPerRequest();

        int bloque = (row / rperreq);
        int inicio = rperreq * bloque;

        int fin = (inicio + rperreq) - 1;

        if (fin > (rcount - 1)) {
            fin = (rcount - 1);
        }

        resp[0] = inicio;
        resp[1] = fin;

        return resp;
    }

    public Driver getDriver() {
        // TODO Auto-generated method stub
        return null;
    }

    public void reload() throws ReloadDriverException {
        // TODO Auto-generated method stub
    }

    public void addDataSourceListener(IDataSourceListener arg0) {
        // TODO Auto-generated method stub
    }

    public void removeDataSourceListener(IDataSourceListener arg0) {
        // TODO Auto-generated method stub
    }
}
