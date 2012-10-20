/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 *   Av. Blasco Ibáñez, 50
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
/* CVS MESSAGES:
 *
 * $Id: 
 * $Log: 
 */
package org.gvsig.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * Implementation of JComboBox that allows us to enable/disable individual items
 * of the JComboBox.
 * 
 * Based in the work of Nobuo Tamemasa, in
 * http://www.codeguru.com/java/articles/165.shtml
 * 
 * 
 * @author Alvaro Zabala
 * 
 */
public class JComboBoxWithDisabledItems extends JComboBox {

	private static final long serialVersionUID = -1375989245835223722L;

	public JComboBoxWithDisabledItems() {
		super();
		// initialize();
	}

	public JComboBoxWithDisabledItems(ComboBoxModel model) {
		super(model);
		initialize();
	}

	public JComboBoxWithDisabledItems(Object[] items) {
		super(items);
		initialize();
	}

	public JComboBoxWithDisabledItems(Vector<?> items) {
		super(items);
		initialize();
	}

	public void initialize() {
		setRenderer(new ComboRenderer());
		addActionListener(new ComboListener(this));
	}

	public void addItem(Object item) {
		if (item instanceof ComboItem) {
			super.addItem(item);
		} else {
			ComboItem newItem = new ComboItem(item);
			newItem.setEnabled(true);
			super.addItem(newItem);
		}
	}

	public Object getItemAt(int index) {
		ComboItem item = (ComboItem) super.getItemAt(index);
		return item.obj;
	}

	public Object getSelectedItem() {
		ComboItem item = (ComboItem) super.getSelectedItem();
		return item.obj;
	}

	public void setItemEnable(int index, boolean enabled) {
		ComboItem item = (ComboItem) super.getItemAt(index);
		item.isEnable = enabled;
	}

	public boolean isItemEnabled(int index) {
		if (index >= 0) {
			ComboItem item = (ComboItem) super.getItemAt(index);
			return item.isEnable;
		} else
			return false;
	}

	/**
	 * Combo box renderer.
	 * 
	 * @author Alvaro Zabala
	 * 
	 */
	class ComboRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 4216706963467674095L;

		public ComboRenderer() {
			setOpaque(true);
			setBorder(new EmptyBorder(1, 1, 1, 1));
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			// if (! ((ComboItem)value).isEnabled()) {
			if (!isItemEnabled(index)) {
				setBackground(list.getBackground());
				setForeground(UIManager.getColor("Label.disabledForeground"));
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setText((value == null) ? "" : value.toString());
			return this;
		}

	}

	/**
	 * Listens for combo box selection events.
	 * 
	 * It the new selected items are not enabled, the combo box will remain
	 * unchanged.
	 * 
	 * @author Alvaro Zabala
	 * 
	 */
	class ComboListener implements ActionListener {
		JComboBox combo;
		Object currentItem;

		ComboListener(JComboBox combo) {
			this.combo = combo;
			combo.setSelectedIndex(0);
			currentItem = combo.getSelectedItem();
		}

		public void actionPerformed(ActionEvent e) {
			Object tempItem = combo.getSelectedItem();
			int selectedIndex = combo.getSelectedIndex();
			// If the selected item in the combo box is enable,
			// the combo will change. If not, the selected item
			// will be the previous selected item
			// if (! ((ComboItem)tempItem).isEnabled()) {
			if (isItemEnabled(selectedIndex)) {
				combo.setSelectedItem(currentItem);
			} else {
				currentItem = tempItem;
			}
		}
	}// ComboListener

	/**
	 * Combo box item that allows to enabling / disabling combo box entries.
	 * 
	 * @author Alvaro Zabala
	 * 
	 */
	class ComboItem {
		Object obj;
		boolean isEnable;

		ComboItem(Object obj, boolean isEnable) {
			this.obj = obj;
			this.isEnable = isEnable;
		}

		ComboItem(Object obj) {
			this(obj, true);
		}

		public boolean isEnabled() {
			return isEnable;
		}

		public void setEnabled(boolean isEnable) {
			this.isEnable = isEnable;
		}

		public String toString() {
			return obj.toString();
		}
	}
}
