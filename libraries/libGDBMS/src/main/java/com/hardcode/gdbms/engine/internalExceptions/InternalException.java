package com.hardcode.gdbms.engine.internalExceptions;

/**
 * Exception produced in internal threads of the system or in code that cannot
 * be resolved by throwing up the exception. The message contains the
 * explanation of the problem
 * 
 * @author Fernando González Cortés
 */
public class InternalException extends Exception {

	/**
	 * DOCUMENT ME!
	 * 
	 * @param message
	 */
	public InternalException(String message) {
		super(message);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param message
	 * @param cause
	 */
	public InternalException(String message, Throwable cause) {
		super(message, cause);
	}
}
