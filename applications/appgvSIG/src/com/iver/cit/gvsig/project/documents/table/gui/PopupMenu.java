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
package com.iver.cit.gvsig.project.documents.table.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;

/**
 * PopupMenu with options to operate on the table.
 * 
 * @author Vicente Caballero Navarro
 */
public class PopupMenu extends JPopupMenu {
	private static final ImageIcon editcopy = PluginServices.getIconTheme()
			.get("edit-copy");
	private static final ImageIcon editcut = PluginServices.getIconTheme().get(
			"edit-cut");
	private static final ImageIcon editpaste = PluginServices.getIconTheme()
			.get("edit-paste");
	private static final ImageIcon editdelete = PluginServices.getIconTheme()
			.get("edit-delete");

	private JMenuItem copy = null;
	private JMenuItem cut = null;
	private JMenuItem paste = null;
	// private JMenuItem insertRow = null;
	// private JMenuItem insertColumn = null;
	private JMenuItem removeRow = null;
	// private JMenuItem removeColumn = null;
	// private JMenuItem startEdition = null;
	// private JMenuItem stopEdition = null;
	private Table table = null;
	private Point point = null;

	/**
	 * Create a new PopupMenu.
	 * 
	 * @param p
	 *            Point to location.
	 */
	public PopupMenu(Point p) {
		point = p;
		initialize();
	}

	/**
	 * Initialize the components of Popupmenu.
	 */
	private void initialize() {
		copy = new JMenuItem(PluginServices.getText(this, "copiar"), editcopy);
		cut = new JMenuItem(PluginServices.getText(this, "cortar"), editcut);
		paste = new JMenuItem(PluginServices.getText(this, "pegar"), editpaste);
		// insertRow = new JMenuItem(PluginServices.getText(this,
		// "insertar_fila"));
		// insertColumn = new JMenuItem(PluginServices.getText(this,
		// "insertar_columna"));
		removeRow = new JMenuItem(
				PluginServices.getText(this, "eliminar_fila"), editdelete);
		// removeColumn = new JMenuItem(PluginServices.getText(this,
		// "eliminar_columna"));
		// startEdition = new JMenuItem(PluginServices.getText(this,
		// "comenzar_edicion"));
		// stopEdition = new JMenuItem(PluginServices.getText(this,
		// "terminar_edicion"));

		// add(startEdition);
		// add(stopEdition);
		// addSeparator();
		add(copy);
		add(cut);
		add(paste);
		addSeparator();
		// add(insertRow);
		add(removeRow);
		// add(insertColumn);
		// add(removeColumn);

		table = (Table) PluginServices.getMDIManager().getActiveWindow();
		// startEdition.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// try {
		// table.startEditing();
		// } catch (EditionException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// PluginServices.getMainFrame().enableControls();
		// }
		// });
		// stopEdition.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {

		// table.stopEditing();
		// PluginServices.getMainFrame().enableControls();

		// }
		// });
		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					table.copyRow();
				} catch (ReadDriverException e1) {
					e1.printStackTrace();
				}
				PluginServices.getMainFrame().enableControls();

			}
		});
		cut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					table.cutRow();
				} catch (ReadDriverException e1) {
					e1.printStackTrace();
				}
				PluginServices.getMainFrame().enableControls();

			}
		});
		paste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO este m�todo a�ade las filas seleccionadas
				// previamente al final de la tabla.
				// De momento no se puede a�adir filas en una posici�n en
				// concreto.

				try {
					table.pasteRow();
				} catch (ValidateRowException e1) {
					e1.printStackTrace();
				} catch (ReadDriverException e1) {
					e1.printStackTrace();
				}
				PluginServices.getMainFrame().enableControls();

			}
		});
		// insertRow.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// //TODO Este m�todo a�ade filas al final de la tabla y deber�a
		// a�adirlas justo antes de la fila que tengamos seleccionada.

		// try {
		// table.addRow(null);
		// } catch (DriverIOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// PluginServices.getMainFrame().enableControls();

		// }
		// });
		removeRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					table.removeRow();
				} catch (ReadDriverException e1) {
					e1.printStackTrace();
				}
				PluginServices.getMainFrame().enableControls();

			}
		});
		// insertColumn.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// //TODO Falta que implementar.

		// }
		// });
		// removeColumn.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// //TODO Falta que implementar.
		// }
		// });
		editingMenu(table.isEditing());
		setEnablePaste(table.isCopied());
		this.show(table, point.x, point.y);
	}

	/**
	 * Set the menu enabled if it�s possible.
	 * 
	 * @param b
	 *            boolean
	 */
	private void editingMenu(boolean b) {
		copy.setEnabled(false);
		cut.setEnabled(false);
		// startEdition.setEnabled(!b);
		// stopEdition.setEnabled(b);
		// insertRow.setEnabled(b);
		// insertColumn.setEnabled(b);
		removeRow.setEnabled(false);
		if (table.getSelectedRowIndices().length > 0) {
			copy.setEnabled(true);
			if (b) {
				cut.setEnabled(true);
				removeRow.setEnabled(true);
			}
		}

		// if ((table.getSelectedFieldIndices().length() > 0) && b) {
		// removeColumn.setEnabled(true);
		// } else {
		// removeColumn.setEnabled(false);
		// }
	}

	/**
	 * Set the menu of paste enabled if it�s possible
	 * 
	 * @param b
	 *            boolean
	 */
	private void setEnablePaste(boolean b) {
		paste.setEnabled(b);
	}
}
