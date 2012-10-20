package com.hardcode.gdbms.engine.data.db;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.edition.EditionInfo;
import com.hardcode.gdbms.engine.data.edition.InternalBuffer;
import com.hardcode.gdbms.engine.data.edition.PKTable;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueCollection;
import com.hardcode.gdbms.engine.values.ValueFactory;

/**
 * @author Fernando González Cortés
 */
public class FakeTransactionSupport {

	private InternalBuffer internalBuffer;
	private PKTable pkTable;
	private boolean transaction = false;
	private DataSource ds;

	public FakeTransactionSupport(DataSource ds) {
		this.ds = ds;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.edition.DataWare#deleteRow(long)
	 */
	public void deleteRow(long rowId) {
		pkTable.deletePK((int) rowId);
	}

	/**
	 * @throws ReadDriverException
	 *             TODO
	 * @throws WriteDriverException
	 * @see com.hardcode.gdbms.engine.data.edition.DataWare#insertEmptyRow(ValueCollection)
	 */
	public void insertFilledRow(Value[] values) throws ReadDriverException,
			WriteDriverException {
		long index = internalBuffer.insertFilledRow(values);
		int[] pkIndices = ds.getPrimaryKeys();
		Value[] pks = new Value[pkIndices.length];

		for (int i = 0; i < pks.length; i++) {
			pks[i] = values[pkIndices[i]];
		}

		pkTable.addPK(ValueFactory.createValue(pks), (int) index);
	}

	/**
	 * @throws WriteDriverException
	 *             TODO
	 * @see com.hardcode.gdbms.engine.data.edition.DataWare#insertRow(Value)
	 */
	public void insertEmptyRow(ValueCollection pk) throws WriteDriverException {
		long index = internalBuffer.insertRow(pk);
		pkTable.addPK(pk, (int) index);
	}

	/**
	 * @throws ReadDriverException
	 *             TODO
	 * @see com.hardcode.gdbms.engine.data.edition.DataWare#beginTrans()
	 */
	public void beginTrans() throws ReadDriverException {
		// Get the table metadata to create a local buffer table
		int fc = ds.getFieldCount();
		String[] pkName = new String[ds.getPrimaryKeys().length];
		String[] names = new String[fc];
		int[] types = new int[fc];

		for (int i = 0; i < types.length; i++) {
			names[i] = ds.getFieldName(i);
			types[i] = ds.getFieldType(i);
		}

		for (int i = 0; i < pkName.length; i++) {
			pkName[i] = ds.getPKName(i);
		}

		// Create the DataSource of the local buffer table
		internalBuffer = new InternalBuffer(ds.getDataSourceFactory(), pkName,
				names, types);
		internalBuffer.start();

		// Initialize the internal data structure
		pkTable = new PKTable();

		for (int i = 0; i < ds.getRowCount(); i++) {
			ValueCollection pk = ds.getPKValue(i);
			pkTable.addPK(i, pk, i);
		}

		transaction = true;
	}

	/**
	 * @throws ReadDriverException
	 *             TODO
	 * @see com.hardcode.gdbms.engine.data.edition.DataWare#rollBackTrans()
	 */
	public void rollBackTrans() throws ReadDriverException {
		// free resources
		transaction = false;
		internalBuffer.stop();
		pkTable = null;
	}

	/**
	 * @throws WriteDriverException
	 *             TODO
	 * @see com.hardcode.gdbms.engine.data.edition.DataWare#setFieldValue(long,
	 *      int)
	 */
	public void setFieldValue(long row, int fieldId, Value value,
			ValueCollection originalPKValue, Value[] originalRow)
			throws WriteDriverException {
		// Get where's the pk
		EditionInfo loc = pkTable.getIndexLocation((int) row);

		if (loc.getFlag() == PKTable.ORIGINAL) {
			// It's in the original table. insert and modify
			long index = internalBuffer.insertRow(originalPKValue);
			loc.setFlag(PKTable.MODIFIED);
			loc.setIndex((int) index);

			Value[] rowValues = originalRow;
			rowValues[fieldId] = value;
			internalBuffer.setRow(index, rowValues);
		} else if ((loc.getFlag() == PKTable.ADDED)
				|| (loc.getFlag() == PKTable.MODIFIED)) {
			// It's in the internal buffer
			internalBuffer.setFieldValue(loc.getIndex(), fieldId, value);
		}
	}

	public InternalBuffer getInternalBuffer() {
		return internalBuffer;
	}

	public PKTable getPKTable() {
		return pkTable;
	}

	/**
	 * @throws ReadDriverException
	 *             TODO
	 * 
	 */
	public void commitTrans() throws ReadDriverException {
		// free resources
		transaction = false;
		internalBuffer.stop();
		pkTable = null;
	}

	public boolean isTransaction() {
		return transaction;
	}
}
