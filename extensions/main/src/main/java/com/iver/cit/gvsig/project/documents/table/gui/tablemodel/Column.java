package com.iver.cit.gvsig.project.documents.table.gui.tablemodel;

import com.iver.utiles.XMLEntity;

/**
 * This class save the values of the graphic interface of the columns.
 * 
 * @author Vicente Caballero Navarro
 * 
 */
public class Column {
	private int width = 75;

	public Column() {
	}

	public int getWidth() {
		return width;
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("width", width);

		return xml;
	}

	public static Column createColumn(XMLEntity xml) {
		Column column = new Column();
		column.setWidth(xml.getIntProperty("width"));

		return column;
	}

	public void setWidth(int minWidth) {
		width = minWidth;
	}
}
