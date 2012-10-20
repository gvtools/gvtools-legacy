package com.iver.cit.gvsig.project.documents.table.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JButton;

import com.hardcode.gdbms.engine.values.ComplexValue;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;

/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * $Id: ComplexValuePanel.java 24986 2008-11-12 12:02:30Z jcampos $
 * $Log$
 * Revision 1.4  2007-01-10 12:43:26  jorpiell
 * The panel for a complexFeature is not editable
 *
 * Revision 1.3  2006/11/01 17:26:56  jorpiell
 * La ventana se reajusta en tamaño dependiendo de la feature compleja
 *
 * Revision 1.2  2006/10/11 16:28:19  jorpiell
 * Añadido un botón para cerrar la ventana
 *
 * Revision 1.1  2006/10/11 08:30:09  jorpiell
 * Panel usado para representar features complejas
 *
 *
 */
/**
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 */
public class ComplexValuePanel extends JPanel implements IWindow {
	private ComplexValue value = null;
	private String name = null;
	private WindowInfo m_windowInfo = null;
	private JButton closeButton = null;
	private JPanel buttonsPanel = null;
	private JScrollPane scroll = null;
	private GridBagLayoutPanel complexPanel = null;

	public ComplexValuePanel(String name, ComplexValue value) {
		super();
		this.value = value;
		this.name = name;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(getScrollPane());
		this.add(getButtonPanel());
	}

	private JScrollPane getScrollPane() {
		if (scroll == null) {
			scroll = new JScrollPane();
			complexPanel = createLevelPanel(name, value);
			Dimension dim = complexPanel.getPreferredSize();
			int widht, heigth = 0;
			if (dim.getHeight() > 500) {
				heigth = 500;
			} else {
				heigth = new Double(dim.getHeight()).intValue() + 10;
			}
			if (dim.getWidth() > 500) {
				widht = 500;
			} else {
				widht = new Double(dim.getWidth()).intValue() + 10;
			}
			scroll.setPreferredSize(new Dimension(widht, heigth));
			scroll.setViewportView(complexPanel);
		}
		return scroll;
	}

	/**
	 * Create a new panel with a row (label + text field) by each geometry
	 * attribute. If one attribute is a complexValue it creates a new panel
	 * recursively
	 * 
	 * @param name
	 *            Param name
	 * @param map
	 *            Map with the attributes
	 * @return
	 */
	private GridBagLayoutPanel createLevelPanel(String name, Map map) {
		GridBagLayoutPanel panel = new GridBagLayoutPanel();
		panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				null, javax.swing.border.TitledBorder.LEFT,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		Iterator keys = map.keySet().iterator();
		while (keys.hasNext()) {
			String param = (String) keys.next();
			Value value = (Value) map.get(param);
			if (!(value instanceof ComplexValue)) {
				JTextField tf = new JTextField(value.toString(), 30);
				tf.setEditable(false);
				tf.setBackground(Color.WHITE);
				panel.addComponent(param, tf, new Insets(1, 0, 1, 0));
			} else {
				panel.addComponent(param,
						createLevelPanel(param, (ComplexValue) value),
						new Insets(1, 0, 1, 0));
			}
		}
		return panel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.ui.mdiManager.IWindow#getWindowInfo()
	 */
	public WindowInfo getWindowInfo() {
		if (m_windowInfo == null) {
			m_windowInfo = new WindowInfo(WindowInfo.MODALDIALOG);
			m_windowInfo.setTitle(name);
			Dimension dim = getComponent(0).getPreferredSize();
			int widht, heigth = 0;
			if (dim.getHeight() > 500) {
				heigth = 500 + 15;
			} else {
				heigth = new Double(dim.getHeight()).intValue() + 15;
			}
			if (dim.getWidth() > 500) {
				widht = 500 + 5;
			} else {
				widht = new Double(dim.getWidth()).intValue() + 5;
			}
			m_windowInfo.setWidth(widht);
			m_windowInfo.setHeight(heigth);
		}
		return m_windowInfo;
	}

	/**
	 * Gets the panel where are all the buttons
	 * 
	 * 
	 * @return JPanel
	 */
	public JPanel getButtonPanel() {
		if (buttonsPanel == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.RIGHT);
			buttonsPanel = new JPanel(flowLayout);
			buttonsPanel.add(getCloseButton(), null);
		}
		return buttonsPanel;
	}

	/**
	 * It gets the close button
	 * 
	 * @return
	 */
	public JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton(PluginServices.getText(this, "close"));
			closeButton.setPreferredSize(new Dimension(80, 23));
			closeButton.setActionCommand("cerrar");
			closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					closeJDialog();
				}
			});
		}
		return closeButton;
	}

	/**
	 * Closes the dialog
	 */
	private void closeJDialog() {
		PluginServices.getMDIManager().closeWindow(this);
	}

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
