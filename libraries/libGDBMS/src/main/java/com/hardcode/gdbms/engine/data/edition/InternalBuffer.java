package com.hardcode.gdbms.engine.data.edition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.InnerDBUtils;
import com.hardcode.gdbms.engine.data.db.DBDataSourceAdapter;
import com.hardcode.gdbms.engine.data.db.DBTableSourceInfo;
import com.hardcode.gdbms.engine.data.driver.AlphanumericDBDriver;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueCollection;
import com.hardcode.gdbms.engine.values.ValueWriter;


/**
 * DataSource with write capabilities over a internal database source.
 *
 * @author Fernando González Cortés
 */
public class InternalBuffer extends DBDataSourceAdapter {
    private Statement s;
    private ResultSet rs;
    private String[] pkNames;
    private String[] fieldNames;
    private int[] pkIndices;
    private int lastInserted = 0;
    private int rowCount;
    private boolean refreshNeeded = true;

    /**
     * Creates a new InternalBufferDataWare object.
     *
     * @param dsf DataSourceFactory
     * @param pkNames primary key field names
     * @param names field names
     * @param types field types
     * @throws ReadDriverException TODO
     */
    public InternalBuffer(DataSourceFactory dsf, String[] pkNames,
	        String[] names, int[] types) throws ReadDriverException {
    	try {

	        this.dsf = dsf;

	        //Add a secuential number
	        String databaseName = dsf.getTempFile();
	        String tableName = "gdbms";


	            InnerDBUtils.execute(databaseName,
	                InnerDBUtils.getCreateStatementWithAutonumeric(tableName,
	                    names, types));
	            InnerDBUtils.execute(databaseName,
	                InnerDBUtils.getPKIndexStatement(tableName, pkNames));
	        //Cache of metadata
	        this.pkNames = pkNames;
	        this.fieldNames = names;

	        //DataSource configuration
	        DBTableSourceInfo di = new DBTableSourceInfo();
	        di.name = tableName;
	        di.dbName = databaseName;
	        di.tableName = tableName;
	        di.driverName = "GDBMS HSQLDB driver";
	        setSourceInfo(di);
			setDriver((AlphanumericDBDriver) dsf.getDriverManager().getDriver(di.driverName));
			rowCount = 0;
	    } catch (SQLException e) {
	        throw new ReadDriverException(getName(),e);
		} catch (DriverLoadException e) {
	        throw new ReadDriverException(getName(),e);
	    }

    }

    /**
     * @throws ReadDriverException TODO
     * @throws WriteDriverException
     * @see com.hardcode.gdbms.engine.data.edition.DataWare#deleteRow(long)
     */
    public void deleteRow(long rowId) throws ReadDriverException, WriteDriverException {
        //Get the pk values
        ValueCollection pks = getPKValue(rowId);
        String[] names = getFieldNames();

        String sql = InnerDBUtils.createDeleteStatement(pks.getValues(), names, tableName, ValueWriter.internalValueWriter);

        try {
            s.execute(sql.toString());
            rowCount--;
            refreshNeeded = true;
        } catch (SQLException e) {
            throw new WriteDriverException(getName(),e);
        }
    }

    /**
     * Inserts a row with data
     *
     * @param values values of the data to be inserted
     *
     * @return index where the row is inserted
     * @throws ReadDriverException TODO
     * @throws WriteDriverException
     */
    public long insertFilledRow(Value[] values) throws ReadDriverException, WriteDriverException {
        /*
         * the last field is the automuneric gdbmsindex
         */
        String[] names = new String[getFieldCount() - 1];
        System.arraycopy(getFieldNames(), 0, names, 0, names.length);

        String sql = InnerDBUtils.createInsertStatement(tableName, values, names, ValueWriter.internalValueWriter);

        try {
            s.execute(sql);
            rowCount++;
            refreshNeeded = true;

            return rowCount - 1;
        } catch (SQLException e) {
            throw new WriteDriverException(getName(),e);
        }
    }

    /**
     * Inserts a new row with only the pk values
     *
     * @param pk value of the PK
     *
     * @return index where the row is inserted
     * @throws WriteDriverException TODO
     */
    public long insertRow(ValueCollection pk) throws WriteDriverException {
        Value[] pks = pk.getValues();

        String sql = InnerDBUtils.createInsertStatement(tableName, pks, pkNames, ValueWriter.internalValueWriter);

        try {
            s.execute(sql);
            rowCount++;
            refreshNeeded = true;

            return rowCount - 1;
        } catch (SQLException e1) {
            throw new WriteDriverException(getName(),e1);
        }
    }

    /**
     * Set the modified row at the specified index
     *
     * @param row index of the row to update
     * @param modifiedRow Value array with the update
     * @throws WriteDriverException TODO
     */
    public void setRow(long row, Value[] modifiedRow) throws WriteDriverException {
    	 try {
			Value[] pks = new Value[getPkIndices().length];

			for (int i = 0; i < pks.length; i++) {
				pks[i] = getFieldValue(row, getPkIndices()[i]);
			}

			String sql = InnerDBUtils.createUpdateStatement(tableName, pks,
					pkNames, fieldNames, modifiedRow,
					ValueWriter.internalValueWriter);

			s.execute(sql);
			refreshNeeded = true;
		} catch (SQLException e) {
			throw new WriteDriverException(getName(),e);
		} catch (ReadDriverException e) {
			throw new WriteDriverException(getName(),e);
		}
    }

    /**
	 * @throws WriteDriverException TODO
     * @see com.hardcode.gdbms.engine.data.edition.DataWare#setFieldValue(long,
	 *      int, com.hardcode.gdbms.engine.values.Value)
	 */
    public void setFieldValue(long row, int modifiedField, Value modifiedValue)
        throws WriteDriverException {
    	 try {
    	ValueCollection pk = getPKValue(row);
        Value[] pks = getPKValue(row).getValues();
        String[] names = new String[pk.getValueCount()];

        for (int i = 0; i < pk.getValueCount(); i++) {
            names[i] = getPKName(i);
        }

        String sql = InnerDBUtils.createUpdateStatement(tableName, pks, names,
                new String[] { getFieldName(modifiedField) },
                new Value[] { modifiedValue }, ValueWriter.internalValueWriter);


            s.execute(sql);
            refreshNeeded = true;
        } catch (SQLException e) {
            throw new WriteDriverException(getName(),e);
        } catch (ReadDriverException e) {
        	 throw new WriteDriverException(getName(),e);
		}
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#start()
     */
    public void start() throws ReadDriverException {
        try {
            con = this.getConnection();

            String localSql = sql + " ORDER BY GDBMSINDEX";
            ((AlphanumericDBDriver) driver).open(con, localSql);
            s = con.createStatement();
        } catch (SQLException e) {
            throw new ReadDriverException(getName(),e);
        } catch (OpenDriverException e) {
        	 throw new ReadDriverException(getName(),e);
		}
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#stop()
     */
    public void stop() throws ReadDriverException {
        try {
            s.close();
            con.close();
            con = null;
        } catch (SQLException e) {
            throw new ReadDriverException(getName(),e);
        }
    }

    /**
     * Gets a int array containing the field index of the primary key fields
     *
     * @return int[]
     * @throws ReadDriverException TODO
     */
    private int[] getPkIndices() throws ReadDriverException {
        if (pkIndices == null) {
            pkIndices = new int[pkNames.length];

            for (int i = 0; i < pkIndices.length; i++) {
                this.pkIndices[i] = getFieldIndexByName(pkNames[i]);
            }
        }

        return pkIndices;
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getPKValue(long)
     */
    public ValueCollection getPKValue(long rowIndex) throws ReadDriverException {
        refresh();

        return super.getPKValue(rowIndex);
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldValue(long,
     *      int)
     */
    public Value getFieldValue(long rowIndex, int fieldId)
        throws ReadDriverException {
        refresh();

        return super.getFieldValue(rowIndex, fieldId);
    }

    /**
     * Rereads the information if has been modified
     * since last refresh
     * @throws ReadDriverException TODO
     */
    private void refresh() throws ReadDriverException {
        if (refreshNeeded == true) {
            stop();
            start();
            refreshNeeded = false;
        }
    }
}
