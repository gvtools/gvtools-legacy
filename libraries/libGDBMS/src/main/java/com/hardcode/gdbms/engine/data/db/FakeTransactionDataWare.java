package com.hardcode.gdbms.engine.data.db;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.InnerDBUtils;
import com.hardcode.gdbms.engine.data.driver.DBDriver;
import com.hardcode.gdbms.engine.data.edition.DeletionInfo;
import com.hardcode.gdbms.engine.data.edition.EditionInfo;
import com.hardcode.gdbms.engine.data.edition.PKTable;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueCollection;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.hardcode.gdbms.engine.values.ValueWriter;

/**
 * DataWare for database data sources
 * 
 * @author Fernando González Cortés
 */
public class FakeTransactionDataWare extends DBDataSourceAdapter implements
		DBDataWare {

	private FakeTransactionSupport ftSupport = new FakeTransactionSupport(this);

	public void start() throws ReadDriverException {
		throw new RuntimeException("Invoke beginTrans in a DataWare");
	}

	public void stop() throws ReadDriverException {
		throw new RuntimeException(
				"Invoke commitTrans/rollBackTrans in a DataWare");
	}

	/**
	 * @throws WriteDriverException
	 * @see com.hardcode.gdbms.engine.data.edition.DataWare#commitTrans()
	 */
	public void commitTrans() throws ReadDriverException, WriteDriverException {
		// Deleted records
		for (int i = 0; i < ftSupport.getPKTable().getDeletedPKCount(); i++) {
			/*
			 * Check if the deleted row is an original row. If not it's not
			 * needed to execute any query
			 */
			DeletionInfo di = ftSupport.getPKTable().getDeletedPK(i);

			if (di.getOriginalIndex() == -1) {
				continue;
			}

			/*
			 * Get the primary key names and the string represetation of th
			 * primary key values in a SQL statement
			 */
			ValueCollection av = di.getPk();
			String[] names = getPKNames();

			// Create the delete statement
			String sql = InnerDBUtils.createDeleteStatement(av.getValues(),
					names, ((DBTableSourceInfo) getSourceInfo()).tableName,
					ValueWriter.internalValueWriter);

			// execute the delete statement
			execute(sql);
		}

		// Updated and added records
		for (int i = 0; i < ftSupport.getPKTable().getPKCount(); i++) {
			EditionInfo loc = ftSupport.getPKTable().getIndexLocation(i);

			if (loc.getFlag() == PKTable.ADDED) {
				// get the index in the internal buffer table
				int actualIndex = loc.getIndex();

				/*
				 * get the field names and the field values of the added row
				 */
				String[] fieldNames = getFieldNames();
				Value[] row = ftSupport.getInternalBuffer().getRow(actualIndex);
				Value[] rowWithoutGDBMSIndex = new Value[row.length - 1];
				System.arraycopy(row, 0, rowWithoutGDBMSIndex, 0,
						rowWithoutGDBMSIndex.length);

				// create and execute the insert statement
				String sql = InnerDBUtils.createInsertStatement(
						((DBTableSourceInfo) getSourceInfo()).tableName,
						rowWithoutGDBMSIndex, fieldNames,
						((DBDriver) getDriver()));

				execute(sql);
			} else if (loc.getFlag() == PKTable.MODIFIED) {
				// get the index in the internal buffer table
				int actualIndex = loc.getIndex();

				/*
				 * Get the primary key values. field values, fieldnames, etc
				 */
				ValueCollection av = getOriginalPKValue(loc.getOriginalIndex());
				Value[] pks = av.getValues();
				String[] pkNames = getPKNames();
				String[] fieldNames = getFieldNames();
				Value[] row = ftSupport.getInternalBuffer().getRow(actualIndex);

				/*
				 * Create and execute the update statement
				 */
				String sql = InnerDBUtils.createUpdateStatement(
						((DBTableSourceInfo) getSourceInfo()).tableName, pks,
						pkNames, fieldNames, row, ((DBDriver) getDriver()));

				execute(sql);
			}
		}

		ftSupport.commitTrans();
		super.stop();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long,
	 *      int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws ReadDriverException {
		// If there is no transaction
		if (!ftSupport.isTransaction()) {
			return super.getFieldValue(rowIndex, fieldId);
		}

		// get where's the row
		EditionInfo fip = ftSupport.getPKTable().getIndexLocation(
				(int) rowIndex);
		int flag = fip.getFlag();

		// get the value
		if ((flag == PKTable.ADDED) || (flag == PKTable.MODIFIED)) {
			return ftSupport.getInternalBuffer().getFieldValue(fip.getIndex(),
					fieldId);
		} else {
			return super.getFieldValue(fip.getIndex(), fieldId);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws ReadDriverException {
		if (ftSupport.isTransaction()) {
			// If there is a transaction
			return ftSupport.getPKTable().getPKCount();
		} else {
			// If there's not
			return super.getRowCount();
		}
	}

	/**
	 * Gets the value of the original row
	 * 
	 * @param rowIndex
	 *            index of the row to be retrieved
	 * 
	 * @return Row values
	 * @throws ReadDriverException
	 *             TODO
	 */
	private Value[] getOriginalRow(long rowIndex) throws ReadDriverException {
		Value[] ret = new Value[getFieldCount()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = super.getFieldValue(rowIndex, i);
		}

		return ret;
	}

	/**
	 * Gets the primary key value in the original DataSource
	 * 
	 * @param rowIndex
	 *            index of the row to be read
	 * 
	 * @return PK
	 * @throws ReadDriverException
	 *             TODO
	 */
	private ValueCollection getOriginalPKValue(long rowIndex)
			throws ReadDriverException {
		int[] fieldsId = getPrimaryKeys();
		Value[] pks = new Value[fieldsId.length];

		for (int i = 0; i < pks.length; i++) {
			pks[i] = super.getFieldValue(rowIndex, fieldsId[i]);
		}

		return ValueFactory.createValue(pks);
	}

	public void beginTrans() throws ReadDriverException {
		super.start();
		ftSupport.beginTrans();
	}

	public void deleteRow(long rowId) throws WriteDriverException,
			ReadDriverException {
		ftSupport.deleteRow(rowId);
	}

	public void insertEmptyRow(ValueCollection pk) throws WriteDriverException {
		ftSupport.insertEmptyRow(pk);
	}

	public void insertFilledRow(Value[] values) throws WriteDriverException,
			ReadDriverException {
		ftSupport.insertFilledRow(values);
	}

	public void rollBackTrans() throws ReadDriverException,
			WriteDriverException {
		ftSupport.rollBackTrans();
		super.stop();
	}

	public void setFieldValue(long row, int fieldId, Value value)
			throws WriteDriverException, ReadDriverException {
		ValueCollection pkValue = null;
		Value[] originalRow = null;
		if (row < super.getRowCount()) {
			pkValue = getOriginalPKValue(row);
			originalRow = getOriginalRow(row);
		}
		ftSupport.setFieldValue(row, fieldId, value, pkValue, originalRow);
	}
}
