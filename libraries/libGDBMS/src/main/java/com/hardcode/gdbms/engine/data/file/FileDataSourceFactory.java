package com.hardcode.gdbms.engine.data.file;

/**
 * Factory to create FileDataSources
 */
public class FileDataSourceFactory {
	/**
	 * Creates a new FileDataSource
	 * 
	 * @return FileDataSource
	 */
	public static FileDataSource newInstance() {
		return new FileDataSourceAdapter();
	}

	/**
	 * Creates a new FileDataWare
	 * 
	 * @param driver
	 * 
	 * @return
	 * 
	 */
	public static FileDataWare newDataWareInstance() {
		return new FileDataWareImpl();
	}

	/**
	 * Creates a new FileSpatialDataSource
	 * 
	 * @return FileSpatialDataSource
	 */
	public static FileDataSource newSpatialInstance() {
		return new FileSpatialDataSourceAdapter();
	}
}
