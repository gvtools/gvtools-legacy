/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ib��ez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package com.iver.cit.gvsig.project.documents.view.legend.gui;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

/**
 * Cell Editor for expressions. It controls the edition events in an expression
 * column of the table
 *
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class ExpressionFieldCellEditor extends JTextField implements TableCellEditor {

	private static final long serialVersionUID = 1L;

	public ExpressionFieldCellEditor() {

	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

		return null;
	}

	public void addCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub
		throw new Error("Not yet implemented");
	}

	public void cancelCellEditing() {
		// TODO Auto-generated method stub
		throw new Error("Not yet implemented");

	}

	public Object getCellEditorValue() {
		return null;
	}

	public boolean isCellEditable(EventObject anEvent) {
		return false;
	}

	public void removeCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub
		throw new Error("Not yet implemented");

	}

	public boolean shouldSelectCell(EventObject anEvent) {
		return false;
	}

	public boolean stopCellEditing() {
		return false;
	}

}
