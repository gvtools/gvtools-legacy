package com.hardcode.gdbms.engine.data.edition;

import java.sql.SQLException;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.InnerDBUtils;
import com.hardcode.gdbms.engine.data.db.DBDataSourceAdapter;
import com.hardcode.gdbms.engine.data.db.DBDataWare;
import com.hardcode.gdbms.engine.data.db.DBTableSourceInfo;
import com.hardcode.gdbms.engine.data.driver.DBTransactionalDriver;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueCollection;
import com.hardcode.gdbms.engine.values.ValueWriter;


/**
 * DataWare that delegates all transaction or write operations in the driver.
 * The driver must be a DBTransactionalDriver
 *
 * @author Fernando González Cortés
 */
public class DirectDataWare extends DBDataSourceAdapter implements DBDataWare {
    private boolean refreshNeeded;

    public void start() throws ReadDriverException {
        throw new RuntimeException("Invoke beginTrans in a DataWare");
    }

    public void stop() throws ReadDriverException {
        throw new RuntimeException("Invoke commitTrans/rollBackTrans in a DataWare");
    }

    private DBTransactionalDriver getTransacctionalDriver(){
        return (DBTransactionalDriver) driver;
    }

    /**
     * @see com.hardcode.gdbms.engine.data.edition.DataWare#deleteRow(long)
     */
    public void deleteRow(long rowId) throws WriteDriverException, ReadDriverException {
        Value[] pk;
			pk = getPKValue(rowId).getValues();

			String sql = InnerDBUtils.createDeleteStatement(pk, getPKNames(),
					((DBTableSourceInfo) getSourceInfo()).tableName,
					((ValueWriter) getDriver()));

			execute(sql);
			refreshNeeded = true;
    }

    /**
	 * @see com.hardcode.gdbms.engine.data.edition.DataWare#insertFilledRow(com.hardcode.gdbms.engine.values.Value[])
	 */
    public void insertFilledRow(Value[] values) throws WriteDriverException {
        String sql;
		try {
			sql = InnerDBUtils.createInsertStatement(
					((DBTableSourceInfo) getSourceInfo()).tableName, values,
					getFieldNames(), ((ValueWriter) getDriver()));

			execute(sql);
			refreshNeeded = true;
		} catch (ReadDriverException e) {
			throw new WriteDriverException(getName(),e);
		}
    }

    /**
     * @see com.hardcode.gdbms.engine.data.edition.DataWare#insertEmptyRow(ValueCollection)
     */
    public void insertEmptyRow(ValueCollection pk) throws WriteDriverException {
       try {
			String sql = InnerDBUtils.createInsertStatement(
					((DBTableSourceInfo) getSourceInfo()).tableName, pk
							.getValues(), getPKNames(),
					((ValueWriter) getDriver()));

			execute(sql);
			refreshNeeded = true;
		} catch (ReadDriverException e) {
			throw new WriteDriverException(getName(),e);
		}
    }

    /**
     * @see com.hardcode.gdbms.engine.data.edition.DataWare#beginTrans()
     */
    public void beginTrans() throws ReadDriverException {
        super.start();
        try {
            getTransacctionalDriver().beginTrans(con);
        } catch (SQLException e) {
            throw new ReadDriverException(getName(),e);
        }
    }

    /**
     * @see com.hardcode.gdbms.engine.data.edition.DataWare#commitTrans()
     */
    public void commitTrans() throws ReadDriverException {
        try {
            getTransacctionalDriver().commitTrans(con);
        } catch (SQLException e) {
            throw new ReadDriverException(getName(),e);
        }
        super.stop();
    }

    /**
     * @see com.hardcode.gdbms.engine.data.edition.DataWare#rollBackTrans()
     */
    public void rollBackTrans() throws ReadDriverException, WriteDriverException {
        try {
            getTransacctionalDriver().rollBackTrans(con);
        } catch (SQLException e) {
            throw new WriteDriverException(getName(),e);
        }
        super.stop();
    }

    /**
     * @see com.hardcode.gdbms.engine.data.edition.DataWare#setFieldValue(long,
     *      int, com.hardcode.gdbms.engine.values.Value)
     */
    public void setFieldValue(long row, int fieldId, Value value)
        throws WriteDriverException, ReadDriverException {
        Value[] rowValue = getRow(row);
        rowValue[fieldId] = value;

        String sql = InnerDBUtils.createUpdateStatement(((DBTableSourceInfo) getSourceInfo()).tableName,
                getPKValue(row).getValues(), getPKNames(),
                getFieldNames(), rowValue, ((ValueWriter)getDriver()));

        execute(sql);
        refreshNeeded = true;
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getRow(long)
     */
    public Value[] getRow(long rowIndex) throws ReadDriverException {
        refresh();

        return super.getRow(rowIndex);
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long,
     *      int)
     */
    public Value getFieldValue(long rowIndex, int fieldId)
        throws ReadDriverException {
        refresh();

        return super.getFieldValue(rowIndex, fieldId);
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getRowCount()
     */
    public long getRowCount() throws ReadDriverException {
        refresh();

        return super.getRowCount();
    }

    /**
     * Causes the underlaying DataSource to reread the data
     * @throws ReadDriverException TODO
     */
    private void refresh() throws ReadDriverException {
        if (refreshNeeded == true) {
            super.stop();
            super.start();
            refreshNeeded = false;
        }
    }
}
