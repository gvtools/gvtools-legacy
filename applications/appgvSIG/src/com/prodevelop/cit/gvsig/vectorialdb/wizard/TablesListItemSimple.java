package com.prodevelop.cit.gvsig.vectorialdb.wizard;

import javax.swing.JCheckBox;

import com.iver.cit.gvsig.fmap.drivers.IConnection;

public class TablesListItemSimple extends JCheckBox {

	protected String tableName = "";
	protected IConnection conn = null;
	protected String objType = "-";

	public TablesListItemSimple(String tn, String type, IConnection c) {
		tableName = tn;
		conn = c;
		objType = type;
		setText(toString());
	}

	public String toString() {
		return (objType != null) ? (tableName + " [" + objType + "]")
				: tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public IConnection getConnection() {
		return conn;
	}

}

// [eiel-gestion-conexiones]