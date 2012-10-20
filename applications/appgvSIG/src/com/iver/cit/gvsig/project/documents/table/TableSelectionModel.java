package com.iver.cit.gvsig.project.documents.table;

import com.iver.utiles.swing.objectSelection.ObjectSelectionModel;

/**
 * @author Fernando González Cortés
 */
public class TableSelectionModel implements ObjectSelectionModel {

	private ProjectTable[] tables;
	private String msg;

	public TableSelectionModel(ProjectTable[] tables, String msg) {
		this.tables = tables;
		this.msg = msg;
	}

	/**
	 * @see com.iver.cit.gvsig.project.documents.table.TableSelectionModel#getTables()
	 */
	public Object[] getObjects() {
		return tables;
	}

	/**
	 * @see com.iver.utiles.swing.objectSelection.ObjectSelectionModel#getMsg()
	 */
	public String getMsg() {
		return msg;
	}

}
