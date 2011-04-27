package com.iver.cit.gvsig.exceptions.table;

/**
 * @author Vicente Caballero Navarro
 */
public class StopEditingTableException extends TableEditingException {

	public StopEditingTableException(String table,Throwable exception) {
		super(table,exception);
		init();
		initCause(exception);
	}

	private void init() {
		messageKey = "error_stop_editing_table";
		formatString = "Failed to stop table editing: %(table). ";
	}

}
