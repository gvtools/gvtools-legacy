package com.iver.cit.gvsig.project.documents.table.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import org.gvsig.gui.beans.swing.JButton;

import com.iver.andami.PluginServices;

/**
 * ComplexValue Renderer.
 * 
 * @author Vicente Caballero Navarro
 */
class ValueComplexRenderer extends JButton implements TableCellRenderer {
	private final Border grayBorder = BorderFactory
			.createLineBorder(Color.darkGray);

	/**
	 * DOCUMENT ME!
	 * 
	 * @param table
	 *            DOCUMENT ME!
	 * @param value
	 *            DOCUMENT ME!
	 * @param isSelected
	 *            DOCUMENT ME!
	 * @param hasFocus
	 *            DOCUMENT ME!
	 * @param row
	 *            DOCUMENT ME!
	 * @param col
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Component getTableCellRendererComponent(javax.swing.JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		setText(PluginServices.getText(this, "compleja"));
		setForeground(Color.darkGray);
		setOpaque(true);

		if (isSelected) {
			setBackground(Color.yellow);
		} else {
			setBackground(UIManager.getColor("Button.background"));
		}

		setBorder(grayBorder);

		return this;
	}
}
