package com.hardcode.gdbms.engine.data.file;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.driver.FileDriver;

/**
 * Interface for file DataSources
 */
public interface FileDataSource extends DataSource {
	/**
	 * Sets the driver layer
	 * 
	 * @param driver
	 *            The driver to set.
	 */
	public void setDriver(FileDriver driver);

	/**
	 * Gets the DataSource driver
	 * 
	 * @return FileDriver
	 */
	public Driver getDriver();
}
