package com.hardcode.gdbms.engine.strategies;

import java.sql.Types;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.persistence.Memento;
import com.hardcode.gdbms.engine.data.persistence.MementoException;
import com.hardcode.gdbms.engine.data.persistence.OperationLayerMemento;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;

class SumDataSource extends OperationDataSource {

	private double sum;

	public SumDataSource(double n) {
		sum = n;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.FieldNameAccess#getFieldIndexByName(java.lang.String)
	 */
	public int getFieldIndexByName(String fieldName) throws ReadDriverException {
		if (fieldName.equals("sum"))
			return 0;
		else
			return -1;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldValue(long,
	 *      int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws ReadDriverException {
		return ValueFactory.createValue(sum);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldCount()
	 */
	public int getFieldCount() throws ReadDriverException {
		return 1;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws ReadDriverException {
		return "sum";
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getRowCount()
	 */
	public long getRowCount() throws ReadDriverException {
		return 1;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldType(int)
	 */
	public int getFieldType(int i) throws ReadDriverException {
		return Types.INTEGER;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#start()
	 */
	public void start() throws ReadDriverException {
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#stop()
	 */
	public void stop() throws ReadDriverException {
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(), new Memento[0], getSQL());
	}

	public int getFieldWidth(int i) throws ReadDriverException {
		return ValueFactory.createValue(sum).getWidth();
	}

}