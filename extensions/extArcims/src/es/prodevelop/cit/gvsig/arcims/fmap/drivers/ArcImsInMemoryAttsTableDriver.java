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

import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;
import org.gvsig.remoteClient.arcims.utils.FieldInformation;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.IntValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.fmap.layers.FBitSet;


public class ArcImsInMemoryAttsTableDriver  {
    private static Logger logger = Logger.getLogger(FMapFeatureArcImsDriver.class.getName());
    private String tableName = "";

    // private String dataBName = "";
    private String idOriginalFieldName = "";
    private String idSqlFieldName = "";

    // private String rowIndexFieldName = "INMEMORYROWNUMBER";
    private int idFieldInd = -1;
    private FBitSet requested;
    private ArrayList sqlColumnNames = new ArrayList();
    private ArrayList originalColumnNames = new ArrayList();

    // private ArrayList originalColumnNames = new ArrayList();
    private ArrayList columnTypes = new ArrayList();

    // private int theRowCount = 0;
    private int theFieldCount = 0;
    private HashMap idToIndex;
    private Dimension lastRequest = new Dimension(-1, -1);

    // ------------------------------------------------
    private ArrayList data = new ArrayList();
    private ArrayList id = new ArrayList();

    // ------------------------------------------------
    private FMapFeatureArcImsDriver theParentDrv;
    private ArcImsAttributesDataSourceAdapter theParentAdapter;
	private DataSourceFactory dsFactory;
    
    // private ArcImsAttributesTableCachee attsCache;
    // private static Logger logger = Logger.getLogger(FMapFeatureArcImsDriver.class.getName());
    public ArcImsInMemoryAttsTableDriver(String tb_name, ArrayList colnames,
        ArrayList coltypes, FMapFeatureArcImsDriver p, ArcImsAttributesDataSourceAdapter pad) {
    	
    	
        // super();
    	
    	theParentDrv = p;
    	theParentAdapter = pad;
        tableName = tb_name;

        // dataBName = dbName;
        idFieldInd = getIdColumnInd(coltypes);

        idOriginalFieldName = (String) colnames.get(idFieldInd);
        idSqlFieldName = replaceUnwantedCharacters((String) colnames.get(
                    idFieldInd));

        requested = new FBitSet();

        for (int i = 0; i < colnames.size(); i++) {
            sqlColumnNames.add(replaceUnwantedCharacters(
                    (String) colnames.get(i)));
            originalColumnNames.add(colnames.get(i));
            columnTypes.add(coltypes.get(i));
            theFieldCount++;
        }

        idToIndex = new HashMap();
    }

    /**
     * user = "as", password = "", dbName = ?
     *
     * @see com.hardcode.gdbms.engine.data.driver.DBDriver#getConnection(java.lang.String,
     *      int, java.lang.String, java.lang.String, java.lang.String)
     */

    //    public Connection getConnection(String host, int port, String _dbName,
    //            String _user, String _password) throws SQLException {
    //        	
    ////            if (driverException != null) {
    ////                throw new RuntimeException(driverException);
    ////            }
    //            String connectionString = "jdbc:hsqldb:mem:" + dataBName;
    //            Connection c = DriverManager.getConnection(connectionString, "sa", "");
    //            return c;
    //        }

    //    public Connection getConnection() throws SQLException {
    //    	return getConnection("", 0, "", "", "");
    //        }

    /**
     * @see com.hardcode.driverManager.Driver#getName()
     */
    public String getName() {
        return "ArcIMS in-memory attributes data driver";
    }

    public void open(String sql) throws SQLException {
        //    	open(getConnection(), sql);
    }

    public void execute(String sql) throws SQLException {
        //    	execute(getConnection(), sql);
    }

    public String getDbName() {
        return "No database";
    }

    public int getIdFieldInd() {
        return idFieldInd;
    }

    public String getIdFieldName() {
        return idSqlFieldName;
    }

    // public void addRow(DataSourceFactory dsf, Value[] values) throws DriverException {
    public void addRow(Value[] values, boolean filled)
        throws DriverException {
        if (values.length < theFieldCount) {
            logger.error("Cannot add row with length < field count. ");

            return;
        }

        if (values[idFieldInd].toString().compareTo("") != 0) {
            // not null value
            int theid = ((IntValue) values[idFieldInd]).intValue();
            idToIndex.put(new Integer(theid), new Integer(data.size()));
        }

        requested.set(data.size(), filled);
        data.add(values);

        //		String fieldNames = "";
        //		for (int i=0; i < (values.length + 1); i++) {
        //			fieldNames = fieldNames + ((String) columnNames.get(i)) + ", "; 
        //		}
        //		fieldNames = fieldNames.substring(0, fieldNames.length() - 2);
        //		// + ArcImsSqlUtils.rowIndexFieldName;
        //		
        //		String fieldValues = "";
        //		for (int i=0; i<values.length; i++) {
        //			fieldValues = fieldValues + getValueInSqlFormat(values[i]) + ", "; 
        //		}
        //		fieldValues = fieldValues + getRowCount();
        //		
        //		String sqlStr = "insert into " + tableName + " (";
        //		sqlStr = sqlStr + fieldNames + ") values (";
        //		sqlStr = sqlStr + fieldValues + ")";
        //		/*
        //		* INSERT INTO Table1 (Column1, Column2, Column3?)
        //		* VALUES (Value1, Value2, Value3?)
        //		*/
        //		try {
        //			// dsf.executeSQL(sqlStr, DataSourceFactory.AUTOMATIC_OPENING);
        //			execute(sqlStr);
        //			theRowCount++;
        //		} catch (Exception e) {
        //			DriverException de = new DriverException(e.getMessage());
        //			throw de;
        //		}
    }

    // public void deleteRow(DataSourceFactory dsf, int i) throws DriverException {
    public void deleteRow(int i) throws DriverException {
        /*
         DELETE FROM Table1 WHERE Some_Column = Some_Value
         */
        logger.warn("deleteRow was invoqued, i = " + i);

        int theid = getRowId(i);
        idToIndex.remove(new Integer(theid));

        requested.set(i, false);
        data.set(i, null);

        //		String sqlStr = "delete from " + tableName + " where ";
        //		sqlStr = sqlStr + rowIndexFieldName + " = ";
        //		sqlStr = sqlStr + i;
        //		/*
        //		* INSERT INTO Table1 (Column1, Column2, Column3?)
        //		* VALUES (Value1, Value2, Value3?)
        //		*/
        //		try {
        //			// dsf.executeSQL(sqlStr, DataSourceFactory.AUTOMATIC_OPENING);
        //			execute(sqlStr);
        //			theRowCount--;
        //		} catch (Exception e) {
        //			DriverException de = new DriverException(e.getMessage());
        //			throw de;
        //		}
    }

    //	private String getValueInSqlFormat(Value v) {
    //		String tmp = v.getStringValue(internalValueWriter);
    ////		if (v instanceof StringValue) {
    ////			tmp = "'" + tmp + "'";
    ////		}
    //		return tmp;
    //	}
    private boolean isEmptyRow(int n) {
        return (data.get(n) == null);
    }

    public int getRowId(int row) throws DriverException {
        Integer rowid = (Integer) id.get(row);

        return rowid.intValue();

        //		Value[] therow = (Value []) data.get(row);
        //		return ((IntValue) therow[idFieldInd]).intValue();
        // return ((Integer) indexToId.get(new Integer(row))).intValue();
    }

    public int getRowIndex(int id) throws DriverException {
        // logger.debug("Searching for id: " + id);
        return ((Integer) idToIndex.get(new Integer(id))).intValue();
    }


    public boolean isNonRequestedRow(int rowind) {
        // TODO Auto-generated method stub
        return (!requested.get(rowind));
    }

    // TODO so far, this method is called with
    // a full field query (except rowindex)
    public void updateRow(Value[] upd_row, String[] fld_q, int rowind)
        throws DriverException {
        if (!isEmptyRow(rowind)) {
            int newid = 0;
            int oldid = getRowId(rowind);
            boolean done = false;

            for (int i = 0; i < fld_q.length; i++) {
                if (fld_q[i].compareTo(idSqlFieldName) == 0) {
                    newid = ((IntValue) upd_row[i]).intValue();
                    done = true;
                }
            }

            if (done) {
                idToIndex.remove(new Integer(oldid));
                idToIndex.put(new Integer(newid), new Integer(rowind));
            }
        }

        Value[] newrow = new Value[theFieldCount];

        if (data.get(rowind) == null) {
            data.set(rowind, newrow);
        }
        else {
            newrow = (Value[]) data.get(rowind);
        }

        if (fld_q[0].compareTo("#ALL#") == 0) {
            for (int i = 0; i < upd_row.length; i++) {
                newrow[i] = upd_row[i];
            }
        }
        else {
            int fldind = 0;

            for (int i = 0; i < fld_q.length; i++) {
                String sqlname = replaceUnwantedCharacters(fld_q[i]);
                fldind = getFieldIndexByName(sqlname);
                newrow[fldind] = upd_row[i];
            }
        }

    }

    public int getFieldIndexByName(String arg0) throws DriverException {
        //		String arg00 = ArcImsSqlUtils.getSqlCompliantFieldName(arg0);
        if (arg0 == null) {
            logger.error("Somebody asked for the index of a null field ");

            return -1;
        }

        for (int i = 0; i < sqlColumnNames.size(); i++) {
            if (((String) sqlColumnNames.get(i)).compareTo(arg0) == 0) {
                return i;
            }
        }

        logger.error("Field not found ");

        return -1;
    }

    public Value[] getRow(DataSourceFactory dsf, int n)
        throws DriverException {
        return (Value[]) data.get(n);

    }

    public void setRequested(int n, boolean req) {
        requested.set(n, req);
    }

    public void addTheseAsRequested(FBitSet fbs) {
        requested.or(fbs);
    }

    public long getRowCount() {
        return (long) data.size();
    }

    public int getFieldCount() {
        return theFieldCount;
    }

    public String getFieldName(int i) {
        return (String) sqlColumnNames.get(i);
    }

    public int getFieldType(int i) {
        int resp = ((Integer) columnTypes.get(i)).intValue();

        // This is done because gvSIG doesnt know about
        // FieldInformation.ID and FieldInformation.SHAPE
        if (resp == FieldInformation.ID) {
            resp = FieldInformation.INTEGER;
        }

        if (resp == FieldInformation.SHAPE) {
            resp = FieldInformation.STRING;
        }

        // stop();
        return resp;
    }

    public void addInIdToIndexHashMap(int i, int rwcount) {
        idToIndex.put(new Integer(i), new Integer(rwcount));
    }

    public void addAsRequested(FBitSet fbs) {
        requested.or(fbs);
    }

    public FBitSet getThisAndNonRequestedUpTo(int _thisone, int total) {
        int thisone = _thisone;
        int rowcount = (int) getRowCount();

        if (_thisone >= rowcount) {
            thisone = _thisone % rowcount;
        }

        FBitSet fbs = new FBitSet();
        fbs.set(thisone);

        if (total < 2) {
            return fbs;
        }

        int othersMustBeSet = total - 1;
        int checkind;

        for (int j = 1; j < rowcount; j++) {
            // checkind = (i + j) % noOfRows;
            checkind = (thisone + j) % rowcount;

            if (isNonRequestedRow(checkind)) {
                fbs.set(checkind);
                othersMustBeSet--;
            }

            if (othersMustBeSet == 0) {
                break;
            }
        }

        return fbs;
    }

    public void addEmptyRowWithId(Value idval, String idcolname) {
        data.add(null);
        id.add(new Integer(((IntValue) idval).intValue()));
    }

    public Value doGetFieldValue(long row, int colind) {
        // printLastAndUpdate(row, colind);
        if ((row >= getRowCount()) || (row < 0)) {
            logger.error("Row index out of limits ");

            return null;
        }

        Value[] therow = (Value[]) data.get((int) row);

        if (therow == null) {
            logger.error("Tried to get non available value (?) ");

            return null;
        }

        if ((colind >= therow.length) || (colind < 0)) {
            logger.error("Field index out of limits ");

            return null;
        }

        if (therow[colind] == null) {
            logger.warn("In order to avoid returning element [ " + row + ", " +
                colind +
                " ] (which is null), returned ValueFactory.createNullValue()");

            return ValueFactory.createNullValue();
        }
        else {
            return therow[colind];
        }
    }
    
    
    
    public Value getFieldValue(long row, int colind) throws ReadDriverException {
        if (isNonRequestedRow((int) row)) {
            int[] lim = theParentAdapter.getBlockLimits((int) row);

            System.err.println("Se ha pedido fila no disponible: " + row);
            System.err.println("Limites: [ " + lim[0] + " , " + lim[1] + " ]");

            // FBitSet fbs = dataDriver.getThisAndNonRequestedUpTo((int) row, rowsPerRequest);
            try {
            	theParentDrv.requestBlockWithoutChecking(lim);
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
        Value resp = doGetFieldValue(row, colind);

        //		stop();
        //		dataDriver.setCachedValue((int) row, colind, resp);
        return resp;
    }    

    private void printLastAndUpdate(long row, int colind) {
        logger.debug("Last request: [ " + lastRequest.height + " , " +
            lastRequest.width + " ]");
        lastRequest.height = (int) row;
        lastRequest.width = colind;
        logger.debug("Current request: [ " + lastRequest.height + " , " +
            lastRequest.width + " ]");
    }

    public int getIdColumnInd(ArrayList col_Types) {
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

        return idindex;
    }

    public String getTableName() {
        return tableName;
    }

    public static String replaceUnwantedCharacters(String str) {
        String resp = str;
        resp = resp.replace('.', '_');
        resp = resp.replace('#', 'z');

        return resp;
    }

    public String getOriginalFieldName(int idcolindex) {
        return (String) originalColumnNames.get(idcolindex);
    }

    public String getSqlFieldName(int idcolindex) {
        return (String) sqlColumnNames.get(idcolindex);
    }

	public int[] getPrimaryKeys() throws ReadDriverException {
        int[] resp = new int[1];
        resp[0] = getIdFieldInd();

        return resp;
        
	}

	public void reload() throws ReloadDriverException {
		// TODO Auto-generated method stub
		
	}

	public void write(DataWare dataWare) throws WriteDriverException,
			ReadDriverException {
		// TODO Auto-generated method stub
		
	}

    public void setDataSourceFactory(DataSourceFactory arg0) {
        dsFactory = arg0;
    }

	public int getFieldWidth(int i) throws ReadDriverException {
		return 40;
	}
	
	public DataSourceFactory getDataSourceFactory() {
		return dsFactory;
	}
    
    
    // ================== Object driver
	
	
	
	/**
	 * Utility methods to avoid issue when server declares attribute names which contain dot (.)
	 * Driver keeps two versions: name used with server (original) and name used by user
	 * (because you cannot use points because they are not valid in logical expressions)
	 */   
	public String[] gvSigNamesToServerNames(String[] gvsig_flds) {
		int len = gvsig_flds.length;
		String[] resp = new String[len];
		for (int i = 0; i < len; ++i) {
			resp[i] = sqlNameToOrigName(gvsig_flds[i]);
		}
		return resp;
	}

	private String sqlNameToOrigName(String fld) {
		int inde = indexIn(fld, this.sqlColumnNames);
		if (inde == -1) {
			logger.error("Did not find field: " + fld);
			return fld;
		}
		return ((String) this.originalColumnNames.get(inde));
	}

	private int indexIn(String str, ArrayList list) {
		int sz = list.size();

		String item = null;
		for (int i = 0; i < sz; ++i) {
			if (list.get(i) instanceof String) {
				if (str.compareTo((String) list.get(i)) == 0)
					return i;
			} else {
				return -1;
			}
		}
		return -1;
	}

}
