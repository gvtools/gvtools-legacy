package com.iver.cit.gvsig.exceptions.table;

/**
 * @author Vicente Caballero Navarro
 */
public class CancelEditingTableException extends TableEditingException {

	public CancelEditingTableException(String table, Throwable exception) {
		super(table, exception);
		init();
		initCause(exception);
	}

	private void init() {
		messageKey = "error_cancel_editing_table";
		formatString = "Cannot cancel table editing: %(table). ";
	}

}
