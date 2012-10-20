package com.hardcode.gdbms.engine.data.object;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.data.driver.ReadAccess;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.data.file.AbstractFileDataSource;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;

/**
 *
 */
class ObjectDriverDataSourceAdapter extends AbstractFileDataSource implements
		ObjectDataSource {
	protected ObjectDriver objectDriver;

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
	 * @see com.hardcode.gdbms.engine.data.DataSource#getDriver()
	 */
	public ReadAccess getReadDriver() {
		return objectDriver;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#setDriver(ObjectDriver)
	 */
	public void setDriver(ObjectDriver readDriver) {
		this.objectDriver = readDriver;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getName()
	 */
	public String getName() {
		return sourceInfo.name;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getPrimaryKeys()
	 */
	public int[] getPrimaryKeys() throws ReadDriverException {
		return objectDriver.getPrimaryKeys();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long,
	 *      int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws ReadDriverException {
		Value v = objectDriver.getFieldValue(rowIndex, fieldId);
		return (v == null) ? ValueFactory.createNullValue() : v;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldCount()
	 */
	public int getFieldCount() throws ReadDriverException {
		return objectDriver.getFieldCount();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws ReadDriverException {
		return objectDriver.getFieldName(fieldId);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldType(int)
	 */
	public int getFieldType(int i) throws ReadDriverException {
		return objectDriver.getFieldType(i);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getDataWare(int)
	 */
	public DataWare getDataWare(int mode) throws ReadDriverException {
		ObjectDataWare dw = ObjectDataSourceFactory.newDataWareInstance();
		dw.setDriver(objectDriver);
		dw.setDataSourceFactory(dsf);
		dw.setSourceInfo(getSourceInfo());
		return dw;

	}

	public int getFieldWidth(int i) throws ReadDriverException {
		return objectDriver.getFieldWidth(i);
	}

	public Driver getDriver() {
		return objectDriver;
	}

	public void reload() throws ReloadDriverException {

		objectDriver.reload();
		this.raiseEventReloaded();
	}
}
