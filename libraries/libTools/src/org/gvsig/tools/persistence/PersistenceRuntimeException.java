package org.gvsig.tools.persistence;

import java.util.HashMap;
import java.util.Map;

import org.gvsig.tools.exception.BaseRuntimeException;

public class PersistenceRuntimeException extends BaseRuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -3729654883985281840L;
	private final static String MESSAGE_FORMAT = "Error getting or setting the state of the object.";
	private final static String MESSAGE_KEY = "_PersistenceRuntimeException";

	protected Map values = new HashMap();

	public PersistenceRuntimeException(String messageFormat, Throwable cause,
			String messageKey, long code) {
		super(messageFormat, cause, messageKey, code);
	}

	public PersistenceRuntimeException(String messageFormat, String messageKey,
			long code) {
		super(messageFormat, messageKey, code);
	}

	public PersistenceRuntimeException(Throwable cause) {
		super(MESSAGE_FORMAT, cause, MESSAGE_KEY, serialVersionUID);
	}

	protected void setValue(String name, String value) {
		this.values.put(name, value);
	}

	protected Map values() {
		return this.values;
	}
}
