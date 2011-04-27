package com.hardcode.gdbms.engine.data;


/**
 * Interfaz que define el Listener de un DataSource
 *
 * @author Jose Manuel Vivó
 */
public interface IDataSourceListener {
	
	/**
	 * Metodo llamado cuando se hace un reload del DataSource
	*/
	void reloaded(DataSource dataSource);

}
