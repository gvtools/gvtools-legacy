/*
 * Created on 25-oct-2004
 */
package com.hardcode.gdbms.engine.data.indexes;

import com.hardcode.gdbms.engine.data.driver.DriverException;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class CannotCreateIndexException extends DriverException {
	/**
	 *
	 */
	public CannotCreateIndexException() {
		super();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param arg0
	 */
	public CannotCreateIndexException(String arg0) {
		super(arg0);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param arg0
	 * @param arg1
	 */
	public CannotCreateIndexException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param arg0
	 */
	public CannotCreateIndexException(Throwable arg0) {
		super(arg0);
	}
}
