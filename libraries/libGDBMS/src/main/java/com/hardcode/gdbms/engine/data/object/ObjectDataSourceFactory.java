package com.hardcode.gdbms.engine.data.object;

/**
 * Factory to create FileDataSources
 */
public class ObjectDataSourceFactory {
	/**
	 * Creates a new FileDataSource
	 * 
	 * @return FileDataSource
	 */
	public static ObjectDataSource newInstance() {
		return new ObjectDriverDataSourceAdapter();
	}

	/**
	 * Creates a new ObjectDataWare
	 * 
	 * @return
	 */
	public static ObjectDataWare newDataWareInstance() {
		return new ObjectDataWareImpl();
	}
}
