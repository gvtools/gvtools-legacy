package org.gvsig.tools.persistence;

public class PersistenceValueNotFoundException extends PersistenceException {

	/**
	 *
	 */
	private static final long serialVersionUID = -8365980563346330001L;
	private final static String MESSAGE_FORMAT = "Value '%(name)s' not found in persistent state.";
	private final static String MESSAGE_KEY = "_PersistenceValueNotFoundException";

	public PersistenceValueNotFoundException(String name, Throwable cause) {
		super(MESSAGE_FORMAT, cause, MESSAGE_KEY, serialVersionUID);
		setValue("name", name);
	}

	public PersistenceValueNotFoundException(String name) {
		super(MESSAGE_FORMAT, MESSAGE_KEY, serialVersionUID);
		setValue("name", name);
	}


}
