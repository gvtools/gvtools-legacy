package org.gvsig.tools.persistence;

import java.util.HashMap;
import java.util.Map;

import org.gvsig.tools.exception.BaseException;

public class PersistenceException extends BaseException {

	/**
	 *
	 */
	private static final long serialVersionUID = -3729654883985281840L;
	private final static String MESSAGE_FORMAT = "Error getting or setting the state of the object.";
	private final static String MESSAGE_KEY = "_PersistenceException";

	protected Map values = new HashMap();

	public PersistenceException(String messageFormat, Throwable cause,
			String messageKey, long code) {
		super(messageFormat, cause, messageKey, code);
	}

	public PersistenceException(String messageFormat, String messageKey,
			long code) {
		super(messageFormat, messageKey, code);
	}

	public PersistenceException(Throwable cause) {
		super(MESSAGE_FORMAT, cause, MESSAGE_KEY, serialVersionUID);
	}

	protected void setValue(String name, String value) {
		this.values.put(name, value);
	}

	protected Map values() {
		return this.values;
	}
}
