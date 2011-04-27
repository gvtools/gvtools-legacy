package org.gvsig.tools.exception;

import java.util.HashMap;
import java.util.Map;

public class NotYetImplemented extends BaseRuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = -4448165879403648365L;
	private final static String MESSAGE_FORMAT = "Operation %(operation)s not yet implemented.";
	private final static String MESSAGE_KEY = "_NotYetImplemented";
	private String operation = "";

	public NotYetImplemented() {
		super(MESSAGE_FORMAT, MESSAGE_KEY, serialVersionUID);
	}

	public NotYetImplemented(String operation) {
		super(MESSAGE_FORMAT, MESSAGE_KEY, serialVersionUID);
		this.operation = "'" + operation + "'";
	}

	protected Map values() {
		Map values = new HashMap();
		values.put("operation", this.operation);
		return values;
	}

}