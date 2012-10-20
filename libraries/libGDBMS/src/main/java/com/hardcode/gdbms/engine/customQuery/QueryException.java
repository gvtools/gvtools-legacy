package com.hardcode.gdbms.engine.customQuery;

/**
 * If there is an error in the Custom Query execution
 * 
 * @author Fernando González Cortés
 */
public class QueryException extends Exception {
	/**
     *
     */
	public QueryException() {
		super();

		// TODO Auto-generated constructor stub
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param message
	 */
	public QueryException(String message) {
		super(message);

		// TODO Auto-generated constructor stub
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param message
	 * @param cause
	 */
	public QueryException(String message, Throwable cause) {
		super(message, cause);

		// TODO Auto-generated constructor stub
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param cause
	 */
	public QueryException(Throwable cause) {
		super(cause);

		// TODO Auto-generated constructor stub
	}
}
