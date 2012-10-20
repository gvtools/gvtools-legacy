/*
 * Created on 28-jun-2006
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
 * $Id: GeoprocessingXYShiftPanel.java 13881 2007-09-19 16:22:04Z jaume $
 * $Log$
 * Revision 1.6  2007-09-19 16:09:14  jaume
 * removed unnecessary imports
 *
 * Revision 1.5  2007/03/06 16:48:14  caballero
 * Exceptions
 *
 * Revision 1.4  2006/08/11 17:17:55  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/07/03 20:29:08  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/06/29 17:58:31  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/06/28 18:17:21  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.xyshift.gui;

import java.awt.Rectangle;
import java.awt.event.ItemEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessPanel;

public class GeoprocessingXYShiftPanel extends AbstractGeoprocessPanel {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JCheckBox selectedOnlyCheckBox = null;
	private JPanel resultSelectionPanel = null;
	private JButton openResultButton = null;
	private JLabel jLabel2 = null;
	private JLabel xoffsetLabel = null;
	private JTextField xoffsetTextField = null;
	private JLabel jLabel3 = null;
	private JTextField yoffsetTextField = null;
	private JLabel offsetLabel = null;

	/**
	 * This method initializes
	 * 
	 */
	public GeoprocessingXYShiftPanel(FLayers layers) {
		super();
		this.layers = layers;
		initialize();
	}

	public double getXOffset() throws GeoprocessException {
		try {
			String strDist = xoffsetTextField.getText();
			return Double.parseDouble(strDist);
		} catch (NumberFormatException ex) {
			throw new GeoprocessException("Offset en x introducido no numerico");
		}
	}

	public double getYOffset() throws GeoprocessException {
		try {
			String strDist = yoffsetTextField.getText();
			return Double.parseDouble(strDist);
		} catch (NumberFormatException ex) {
			throw new GeoprocessException(
					"Distancia de buffer introducida no numerica");
		}
	}

	/**
	 * Tells if apply geoprocess only to selected features of the input layer or
	 * to all features
	 * 
	 * @return
	 */
	public boolean isOnlySelected() {
		return selectedOnlyCheckBox.isSelected();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		offsetLabel = new JLabel();
		offsetLabel.setBounds(new java.awt.Rectangle(29, 147, 211, 23));
		offsetLabel.setText(PluginServices.getText(this,
				"Introducir_valores_desplazamiento"));
		jLabel3 = new JLabel();
		jLabel3.setBounds(new java.awt.Rectangle(27, 215, 152, 27));
		jLabel3.setText(PluginServices.getText(this, "yOffset") + ":");
		xoffsetLabel = new JLabel();
		xoffsetLabel.setBounds(new java.awt.Rectangle(26, 182, 154, 26));
		xoffsetLabel.setText(PluginServices.getText(this, "xOffset") + ":");
		jLabel1 = new JLabel();
		jLabel1.setBounds(new java.awt.Rectangle(27, 60, 384, 23));
		jLabel1.setText(PluginServices.getText(this, "Cobertura_de_entrada")
				+ ":");
		jLabel = new JLabel();
		jLabel.setBounds(new java.awt.Rectangle(27, 17, 384, 27));
		jLabel.setText(PluginServices.getText(this,
				"XYShift._Introduccion_de_datos") + ":");
		this.setLayout(null);
		this.setBounds(new java.awt.Rectangle(0, 0, 500, 400));
		this.add(jLabel, null);
		this.add(jLabel1, null);
		this.add(getSelectedOnlyCheckBox(), null);
		this.add(getResultPanel(), null);
		this.add(xoffsetLabel, null);
		this.add(getXoffsetTextField(), null);
		this.add(jLabel3, null);
		this.add(getYoffsetjTextField1(), null);
		this.add(offsetLabel, null);
		this.add(getLayersComboBox(), null);
	}

	/**
	 * This method initializes selectedOnlyCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getSelectedOnlyCheckBox() {
		if (selectedOnlyCheckBox == null) {
			selectedOnlyCheckBox = new JCheckBox();
			selectedOnlyCheckBox.setBounds(new java.awt.Rectangle(27, 94, 383,
					23));
			selectedOnlyCheckBox.setText(PluginServices.getText(this,
					"Usar_solamente_los_elementos_seleccionados"));
		}
		return selectedOnlyCheckBox;
	}

	private JComboBox getLayersComboBox() {
		if (layersComboBox == null) {
			layersComboBox = new JComboBox();
			DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(
					getLayerNames());
			layersComboBox.setModel(defaultModel);
			layersComboBox.setBounds(142, 63, 260, 21);
			layersComboBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						// Cambiar el estado del CheckBox
						initSelectedItemsJCheckBox();
					}
				}// itemStateChange
			});
		}
		return layersComboBox;
	}

	private void initSelectedItemsJCheckBox() {
		String selectedLayer = (String) layersComboBox.getSelectedItem();
		FLyrVect inputLayer = (FLyrVect) layers.getLayer(selectedLayer);
		FBitSet fBitSet = null;
		try {
			fBitSet = inputLayer.getRecordset().getSelection();
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (fBitSet.cardinality() == 0) {
			selectedOnlyCheckBox.setEnabled(false);
		} else {
			selectedOnlyCheckBox.setEnabled(true);
		}
		selectedOnlyCheckBox.setSelected(false);
	}

	/**
	 * This method initializes resultSelectionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getResultPanel() {
		if (resultSelectionPanel == null) {
			jLabel2 = new JLabel();
			jLabel2.setBounds(new Rectangle(4, 13, 126, 17));
			jLabel2.setText(PluginServices.getText(this, "Cobertura_de_salida")
					+ ":");
			resultSelectionPanel = new JPanel();
			resultSelectionPanel.setLayout(null);
			resultSelectionPanel.setBounds(new java.awt.Rectangle(26, 259, 443,
					46));
			resultSelectionPanel.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.RAISED));
			resultSelectionPanel.add(getOpenResultButton(), null);
			resultSelectionPanel.add(getFileNameResultTextField(), null);
			resultSelectionPanel.add(jLabel2, null);
		}
		return resultSelectionPanel;
	}

	/**
	 * This method initializes openResultButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOpenResultButton() {
		if (openResultButton == null) {
			openResultButton = new JButton();
			openResultButton.setBounds(new Rectangle(311, 12, 101, 21));
			openResultButton.setText(PluginServices.getText(this, "Abrir"));
			openResultButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							openResultFile();
						}
					});
		}
		return openResultButton;
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getFileNameResultTextField() {
		if (fileNameResultTextField == null) {
			super.getFileNameResultTextField().setBounds(
					new Rectangle(135, 11, 169, 21));
		}
		return fileNameResultTextField;
	}

	/**
	 * This method initializes xoffsetTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getXoffsetTextField() {
		if (xoffsetTextField == null) {
			xoffsetTextField = new JTextField();
			xoffsetTextField
					.setBounds(new java.awt.Rectangle(193, 182, 216, 25));
		}
		return xoffsetTextField;
	}

	/**
	 * This method initializes yoffsetjTextField1
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getYoffsetjTextField1() {
		if (yoffsetTextField == null) {
			yoffsetTextField = new JTextField();
			yoffsetTextField
					.setBounds(new java.awt.Rectangle(193, 216, 216, 25));
		}
		return yoffsetTextField;
	}

} // @jve:decl-index=0:visual-constraint="13,15"

