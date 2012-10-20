package com.iver.cit.gvsig.project.documents.table.gui.tablemodel;

import java.util.ArrayList;

import com.iver.utiles.XMLEntity;

/**
 * DOCUMENT ME!
 * 
 * @author Vicente Caballero Navarro
 */
public class Columns extends ArrayList {
	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();

		for (int i = 0; i < size(); i++) {
			Column column = (Column) get(i);
			xml.addChild(column.getXMLEntity());
		}

		return xml;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param xml
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public static Columns createColumns(XMLEntity xml) {
		Columns columns = new Columns();

		for (int i = 0; i < xml.getChildrenCount(); i++) {
			columns.add(Column.createColumn(xml.getChild(i)));
		}

		return columns;
	}
}
