/*
 * Created on 17-oct-2004
 */
package com.hardcode.gdbms.engine.data.driver;

/**
 * Excepción lanzada cuando un driver no pudo resolver la petición que se le
 * realizó. En un driver de fichero tendrá como causa una IOException, en un
 * driver de DB tendrá una SQLException, ...
 *
 * @author Fernando González Cortés
 */
public class DriverException extends Exception {
	/**
	 * Creates a new StartException object.
	 */
	public DriverException() {
		super();
	}

	/**
	 * Creates a new DriverException object.
	 *
	 * @param arg0
	 */
	public DriverException(String arg0) {
		super(arg0);
	}

	/**
	 * Creates a new DriverException object.
	 *
	 * @param arg0
	 * @param arg1
	 */
	public DriverException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Creates a new DriverException object.
	 *
	 * @param arg0
	 */
	public DriverException(Throwable arg0) {
		super(arg0);
	}
}
