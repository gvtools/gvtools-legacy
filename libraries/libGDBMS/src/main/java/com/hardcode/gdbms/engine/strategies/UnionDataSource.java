package com.hardcode.gdbms.engine.strategies;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.persistence.Memento;
import com.hardcode.gdbms.engine.data.persistence.MementoException;
import com.hardcode.gdbms.engine.data.persistence.OperationLayerMemento;
import com.hardcode.gdbms.engine.values.Value;


/**
 * DataSource que hace la union de dos datasources
 *
 * @author Fernando González Cortés
 */
public class UnionDataSource extends OperationDataSource {
	private DataSource dataSource1;
	private DataSource dataSource2;

	/**
	 * Creates a new UnionDataSource object.
	 *
	 * @param ds1 Primera tabla de la union
	 * @param ds2 Segunda tabla de la union
	 */
	public UnionDataSource(DataSource ds1, DataSource ds2) {
		dataSource1 = ds1;
		dataSource2 = ds2;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#open()
	 */
	public void start() throws ReadDriverException {
		dataSource1.start();

		try {
			dataSource2.start();
		} catch (ReadDriverException e) {
			dataSource1.stop();

			throw e;
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#close()
	 */
	public void stop() throws ReadDriverException {
		dataSource1.stop();
		dataSource2.stop();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.FieldNameAccess#getFieldIndexByName(java.lang.String)
	 */
	public int getFieldIndexByName(String fieldName) throws ReadDriverException {
		return dataSource1.getFieldIndexByName(fieldName);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long,
	 * 		int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
		throws ReadDriverException {
		long tamTabla1 = dataSource1.getRowCount();

		if (rowIndex < tamTabla1) {
			return dataSource1.getFieldValue(rowIndex, fieldId);
		} else {
			return dataSource2.getFieldValue(rowIndex - tamTabla1, fieldId);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldCount()
	 */
	public int getFieldCount() throws ReadDriverException {
		return dataSource1.getFieldCount();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws ReadDriverException {
		return dataSource1.getFieldName(fieldId);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws ReadDriverException {
		return dataSource1.getRowCount() + dataSource2.getRowCount();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldType(int)
	 */
	public int getFieldType(int i) throws ReadDriverException {
		return dataSource1.getFieldType(i);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(),
			new Memento[] { dataSource1.getMemento(), dataSource2.getMemento() }, getSQL());
	}

	public int getFieldWidth(int i) throws ReadDriverException {
		return dataSource1.getFieldWidth(i);
	}
}
