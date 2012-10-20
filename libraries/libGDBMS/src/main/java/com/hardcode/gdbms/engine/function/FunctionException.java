package com.hardcode.gdbms.engine.function;

/**
 * Excepción producida en el código de las funciones
 * 
 * @author Fernando González Cortés
 */
public class FunctionException extends Exception {
	/**
	 *
	 */
	public FunctionException() {
		super();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param message
	 */
	public FunctionException(String message) {
		super(message);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param message
	 * @param cause
	 */
	public FunctionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param cause
	 */
	public FunctionException(Throwable cause) {
		super(cause);
	}
}
