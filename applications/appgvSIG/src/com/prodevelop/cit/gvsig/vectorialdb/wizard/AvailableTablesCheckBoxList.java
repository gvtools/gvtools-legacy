/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package com.prodevelop.cit.gvsig.vectorialdb.wizard;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

/**
 * Utility class to keep the list of available tables.
 * 
 * @author jldominguez
 * 
 */
public class AvailableTablesCheckBoxList extends JList {
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	private static Logger logger = Logger
			.getLogger(AvailableTablesCheckBoxList.class.getName());
	private WizardVectorialDB parent = null;
	private TablesListItem actingTable = null;

	public AvailableTablesCheckBoxList(WizardVectorialDB p) {
		parent = p;

		setCellRenderer(new CellRenderer());

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int index = locationToIndex(e.getPoint());

				if (index == -1) {
					return;
				}

				actingTable = (TablesListItem) getModel().getElementAt(index);

				try {
					parent.setSettingsPanels(actingTable);
					if ((e.getClickCount() == 2) || (e.getX() < 15)) {
						if (!actingTable.isActivated()) {
							actingTable.activate();
						}
						actingTable.setSelected(!actingTable.isSelected());
						enableFieldsAndSettingsPanels();
					}
					parent.checkFinishable();
					repaint();
				} catch (Exception e1) {
					logger.error(
							"While setting selected table: " + e1.getMessage(),
							e1);
					// showConnectionErrorMessage(e1.getMessage());
				}
			}

		});

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	private void enableFieldsAndSettingsPanels() {
		if (actingTable.isSelected()) {
			actingTable.setEnabledPanels(true);
		} else {
			actingTable.setEnabledPanels(false);
		}
	}

	protected class CellRenderer implements ListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			TablesListItem checkbox = (TablesListItem) value;
			checkbox.setBackground(isSelected ? getSelectionBackground()
					: getBackground());
			checkbox.setForeground(isSelected ? getSelectionForeground()
					: getForeground());
			checkbox.setEnabled(isEnabled());
			checkbox.setFont(getFont());
			checkbox.setFocusPainted(false);
			checkbox.setBorderPainted(true);
			checkbox.setBorder(isSelected ? UIManager
					.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

			return checkbox;
		}
	}
}

// [eiel-gestion-conexiones]