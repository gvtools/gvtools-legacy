package com.iver.cit.gvsig.exceptions.table;

/**
 * @author Vicente Caballero Navarro
 */
public class StartEditingTableException extends TableEditingException {

	public StartEditingTableException(String table, Throwable exception) {
		super(table, exception);
		init();
		initCause(exception);
	}

	private void init() {
		messageKey = "error_start_editing_table";
		formatString = "Failed to start table editing: %(table). ";
	}

}
