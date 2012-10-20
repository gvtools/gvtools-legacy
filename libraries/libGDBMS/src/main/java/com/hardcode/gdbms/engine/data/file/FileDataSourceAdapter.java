package com.hardcode.gdbms.engine.data.file;

import java.io.File;
import java.sql.Types;

import org.apache.log4j.Logger;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.CloseDriverException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.engine.data.SourceInfo;
import com.hardcode.gdbms.engine.data.driver.FileDriver;
import com.hardcode.gdbms.engine.data.driver.GDBMSDriver;
import com.hardcode.gdbms.engine.data.driver.ReadAccess;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;

/**
 * Adapta la interfaz FileDriver a la interfaz DataSource
 * 
 * @author Fernando González Cortés
 */
class FileDataSourceAdapter extends AbstractFileDataSource implements
		FileDataSource {
	private File file;

	private FileDriver driver;

	private int sem = 0;

	private int fieldCount = -1;

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#start()
	 */
	public void start() throws ReadDriverException {
		try {
			if (sem == 0) {
				driver.open(file);
			}

			sem++;
		} catch (OpenDriverException e) {
			throw new ReadDriverException(getName(), e);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#stop()
	 */
	public void stop() throws ReadDriverException {
		try {
			sem--;

			if (sem == 0) {
				driver.close();
			} else if (sem < 0) {
				sem = 0;
				driver.close();
				Logger.getLogger(this.getClass()).debug(
						"DataSource closed too many times  =  "
								+ driver.getName());
				// throw new
				// RuntimeException("DataSource closed too many times");
			}
		} catch (CloseDriverException e) {
			throw new ReadDriverException(getName(), e);
		}
	}

	/**
	 * Asigna el driver al adaptador
	 * 
	 * @param driver
	 *            The driver to set.
	 */
	public void setDriver(FileDriver driver) {
		this.driver = driver;
	}

	/**
	 * Sets the source information of the DataSource
	 * 
	 * @param sourceInfo
	 *            The file to set.
	 */
	public void setSourceInfo(SourceInfo sourceInfo) {
		super.setSourceInfo(sourceInfo);
		file = new File(((FileSourceInfo) sourceInfo).file);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getDriver()
	 */
	public ReadAccess getReadDriver() {
		return driver;
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
		// The last field is the pk/row in FileDataSources
		return new int[] { getFieldCount() - 1 };
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldCount()
	 */
	public int getFieldCount() throws ReadDriverException {
		if (fieldCount == -1) {
			fieldCount = getReadDriver().getFieldCount() + 1;

		}

		return fieldCount;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws ReadDriverException {
		// last field is the virtual primary key
		if (fieldId == getFieldCount() - 1)
			return "PK";
		return getReadDriver().getFieldName(fieldId);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldType(int)
	 */
	public int getFieldType(int i) throws ReadDriverException {
		// last field is the virtual primary key
		if (i == getFieldCount() - 1)
			return Types.BIGINT;
		return getReadDriver().getFieldType(i);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldValue(long,
	 *      int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws ReadDriverException {
		// last field is the virtual primary key
		if (fieldId == getFieldCount() - 1)
			return ValueFactory.createValue(rowIndex);
		Value v = getReadDriver().getFieldValue(rowIndex, fieldId);
		return (v == null) ? ValueFactory.createNullValue() : v;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.file.FileDataSource#getDriver()
	 */
	public Driver getDriver() {
		return driver;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getDataWare(int)
	 */
	public DataWare getDataWare(int mode) throws ReadDriverException {
		FileDataWare dw = FileDataSourceFactory.newDataWareInstance();
		FileDriver driver;
		driver = null;
		((GDBMSDriver) driver).setDataSourceFactory(getDataSourceFactory());
		dw.setDriver(driver);
		dw.setDataSourceFactory(dsf);
		dw.setSourceInfo(getSourceInfo());
		return dw;

	}

	public int getFieldWidth(int i) throws ReadDriverException {
		return getReadDriver().getFieldWidth(i);
	}

	public boolean isVirtualField(int fieldId) throws ReadDriverException {
		// last field is the virtual primary key
		if (fieldId == this.getFieldCount() - 1)
			return true;
		return false;
	}

	public void reload() throws ReloadDriverException {
		try {
			sem = 0;
			driver.close();

			this.fieldCount = -1;

			this.start();

			this.raiseEventReloaded();
		} catch (CloseDriverException e) {
			throw new ReloadDriverException(getName(), e);
		} catch (ReadDriverException e) {
			throw new ReloadDriverException(getName(), e);
		}

	}

}
