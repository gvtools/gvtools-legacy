/*
 * Created on 04-jul-2005
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
package com.iver.cit.gvsig.geoprocess.impl.buffer.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessPanel;
import com.iver.utiles.GenericFileFilter;

/**
 * Component that represents a Step of the GeoProcessingPanel Wizard. It allows
 * user to make selections to do buffers geoprocesses.
 * 
 * @author jmorell, azabala
 * 
 *         TODO copiar del ClipPanel el que se muestre el numero de elementos
 *         seleccionados en la capa
 */
public class GeoProcessingBufferPanel extends AbstractGeoprocessPanel implements
		BufferPanelIF, IWindow {
	/*
	 * Constants to indicate how to create buffers on polygonal geometries
	 */
	public final String BUFFER_INSIDE = PluginServices.getText(this, "Dentro");

	public final String BUFFER_INSIDE_OUTSIDE = PluginServices.getText(this,
			"Dentro_y_fuera");

	public final String BUFFER_OUTSIDE = PluginServices.getText(this, "Fuera");

	private static final long serialVersionUID = 1L;

	private JLabel jLabel = null;

	private JCheckBox selectedOnlyCheckbox = null;

	private JButton openFileButton = null;

	private JRadioButton distanceBufferRadioButton = null;

	private JTextField bufferDistanceTextField = null;

	private JRadioButton attributeBufferRadioButton = null;

	private JComboBox layerFieldsComboBox = null;

	private ButtonGroup buttonGroup = null;

	private JLabel jLabel3 = null;

	private JCheckBox dissolveEntitiesJCheckBox = null;

	private WindowInfo viewInfo;

	private JPanel methodSelectionPanel = null;

	private JPanel resultSelectionPanel = null;

	private JLabel jLabel1 = null;

	private JCheckBox endCapCheckBox = null;

	private JPanel extendedOptionsPanel = null;

	private JPanel typePolygonBufferPanel = null;

	private JLabel typeBufferLabel = null;

	private JComboBox typeBufferComboBox = null;

	private JPanel numBuffersPanel = null;

	private JLabel radialBufferLabel = null;

	private JSpinner radialBufferSpinner = null;

	/**
	 * This constructor initializes the set of layers
	 */
	public GeoProcessingBufferPanel(FLayers layers) {
		super();
		this.layers = layers;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		jLabel3 = new JLabel();
		jLabel = new JLabel();
		this.setLayout(null);
		this.setBounds(new java.awt.Rectangle(0, 0, 486, 377));
		jLabel.setText(PluginServices.getText(this,
				"Areas_de_influencia._Introduccion_de_datos") + ":");
		jLabel.setBounds(5, 20, 343, 21);
		jLabel3.setText(PluginServices.getText(this, "Cobertura_de_entrada")
				+ ":");
		jLabel3.setBounds(6, 63, 190, 21);
		this.add(jLabel, null);
		this.add(jLabel3, null);
		this.add(getLayersComboBox(), null);
		this.add(getSelectedOnlyCheckBox(), null);
		this.add(getMethodSelectionPanel(), null);
		this.add(getResultSelectionPanel(), null);
		this.add(getExtendedOptionsPanel(), null);
		confButtonGroup();
		layersComboBox.setSelectedIndex(0);
		initSelectedItemsJCheckBox();
		distanceBufferRadioButton.setSelected(true);
		layerFieldsComboBox.setEnabled(false);
		verifyTypeBufferComboEnabled();
	}

	private String[] getFieldNames() {
		AlphanumericData lyr = (AlphanumericData) (layers
				.getLayer((String) layersComboBox.getSelectedItem()));
		DataSource ds;
		String[] fieldNames = null;
		try {
			ds = lyr.getRecordset();
			fieldNames = new String[ds.getFieldCount()];
			for (int i = 0; i < ds.getFieldCount(); i++) {
				fieldNames[i] = ds.getFieldName(i);
			}
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fieldNames;
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
			selectedOnlyCheckbox.setEnabled(false);
		} else {
			selectedOnlyCheckbox.setEnabled(true);
		}
		selectedOnlyCheckbox.setSelected(false);
	}

	private JCheckBox getSelectedOnlyCheckBox() {
		if (selectedOnlyCheckbox == null) {
			selectedOnlyCheckbox = new JCheckBox();
			selectedOnlyCheckbox.setText(PluginServices.getText(this,
					"Usar_solamente_los_elementos_seleccionados"));
			selectedOnlyCheckbox.setBounds(8, 102, 339, 21);
		}
		return selectedOnlyCheckbox;
	}

	/**
	 * This method initializes layersComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
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

						// Cambiar el estado del layerFieldsComboBox
						DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(
								getFieldNames());
						layerFieldsComboBox.setModel(defaultModel);
						verifyTypeBufferComboEnabled();
					}
				}
			});
		}
		return layersComboBox;
	}

	private void verifyTypeBufferComboEnabled() {
		String layerName = (String) layersComboBox.getSelectedItem();
		FLyrVect layer = (FLyrVect) layers.getLayer(layerName);
		boolean enable = false;
		try {
			enable = layer.getShapeType() == XTypes.POLYGON;
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		enableTypePolygonBufferPanel(enable);
	}

	/**
	 * This method initializes fileNameResultTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getFileNameResultTextField() {
		if (fileNameResultTextField == null) {
			super.getFileNameResultTextField().setBounds(
					new java.awt.Rectangle(132, 11, 199, 21));
		}
		return fileNameResultTextField;
	}

	/**
	 * This method initializes openFileButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOpenFileButton() {
		if (openFileButton == null) {
			openFileButton = new JButton();
			openFileButton.setText(PluginServices.getText(this, "Abrir"));
			openFileButton.setBounds(new java.awt.Rectangle(335, 11, 129, 21));
			openFileButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							openResultFile();
						}
					});
		}
		return openFileButton;
	}

	private void confButtonGroup() {
		if (buttonGroup == null) {
			buttonGroup = new ButtonGroup();
			buttonGroup.add(getDistanceBufferRadioButton());
			buttonGroup.add(getAttributeBufferRadioButton());
		}
	}

	/**
	 * This method initializes distanceBufferRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getDistanceBufferRadioButton() {
		if (distanceBufferRadioButton == null) {
			distanceBufferRadioButton = new JRadioButton();
			distanceBufferRadioButton.setText(PluginServices.getText(this,
					"Area_de_influencia_definida_por_una_distancia") + ":");
			distanceBufferRadioButton.setBounds(new java.awt.Rectangle(2, 10,
					303, 24));
			distanceBufferRadioButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							constantDistanceSelected();
						}
					});
		}
		return distanceBufferRadioButton;
	}

	/**
	 * This method initializes bufferDistanceTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getBufferDistanceTextField() {
		if (bufferDistanceTextField == null) {
			bufferDistanceTextField = new JTextField();
			bufferDistanceTextField.setBounds(new java.awt.Rectangle(308, 11,
					134, 21));
		}
		return bufferDistanceTextField;
	}

	/**
	 * This method initializes attributeBufferRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getAttributeBufferRadioButton() {
		if (attributeBufferRadioButton == null) {
			attributeBufferRadioButton = new JRadioButton();
			attributeBufferRadioButton.setText(PluginServices.getText(this,
					"Area_de_influencia_definida_por_un_campo") + ":");
			attributeBufferRadioButton.setBounds(new java.awt.Rectangle(2, 41,
					287, 21));
			attributeBufferRadioButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							attributeDistanceSelected();
						}
					});
		}
		return attributeBufferRadioButton;
	}

	/**
	 * This method initializes layerFieldsComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getLayerFieldsComboBox() {
		if (layerFieldsComboBox == null) {
			layerFieldsComboBox = new JComboBox();
			layerFieldsComboBox.setBounds(new java.awt.Rectangle(308, 41, 138,
					21));
			DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(
					getFieldNames());
			layerFieldsComboBox.setModel(defaultModel);
		}
		return layerFieldsComboBox;
	}

	private JCheckBox getDissolveEntitiesJCheckBox() {
		if (dissolveEntitiesJCheckBox == null) {
			dissolveEntitiesJCheckBox = new JCheckBox();
			dissolveEntitiesJCheckBox.setText(PluginServices.getText(this,
					"Disolver_entidades"));
			dissolveEntitiesJCheckBox.setBounds(new java.awt.Rectangle(9, 70,
					190, 21));
		}
		return dissolveEntitiesJCheckBox;
	}

	public void openResultFile() {
		JFileChooser jfc = new JFileChooser();
		jfc.addChoosableFileFilter(new GenericFileFilter("shp", PluginServices
				.getText(this, "Ficheros_SHP")));
		if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			if (!(file.getPath().endsWith(".shp") || file.getPath().endsWith(
					".SHP"))) {
				file = new File(file.getPath() + ".shp");
			}
		}// if
		if (jfc.getSelectedFile() != null) {
			getFileNameResultTextField().setText(
					jfc.getSelectedFile().getAbsolutePath());
		}

	}

	public void constantDistanceSelected() {
		getBufferDistanceTextField().setEnabled(true);
		getLayerFieldsComboBox().setEnabled(false);

	}

	public void attributeDistanceSelected() {
		getBufferDistanceTextField().setEnabled(false);
		getLayerFieldsComboBox().setEnabled(true);

	}

	public boolean isConstantDistanceSelected() {
		return distanceBufferRadioButton.isSelected();
	}

	public boolean isAttributeDistanceSelected() {
		return attributeBufferRadioButton.isSelected();
	}

	public double getConstantDistance() throws GeoprocessException {
		try {
			String strDist = bufferDistanceTextField.getText();
			Double dist = Double.parseDouble(strDist);
			if (dist <= 0) {
				throw new GeoprocessException(
						"Distancia de buffer debe ser > 0");
			}
			return dist;
		} catch (NumberFormatException ex) {
			throw new GeoprocessException(
					"Distancia de buffer introducida no numerica");
		}
	}

	public String getAttributeDistanceField() throws GeoprocessException {
		String attributeField = (String) layerFieldsComboBox.getSelectedItem();
		FLyrVect selectedLayer = getInputLayer();
		try {
			SelectableDataSource selectable = selectedLayer.getRecordset();

			int fieldIndex = selectable.getFieldIndexByName(attributeField);
			int fieldType = selectable.getFieldType(fieldIndex);

			if (!XTypes.isNumeric(fieldType))
				throw new GeoprocessException(
						"Atributo no numerico para distancia de buffer");

		} catch (ReadDriverException e) {
			throw new GeoprocessException(
					"Problemas accediendo al campo que define la distancia de buffer");
		}
		return attributeField;
	}

	public boolean isBufferOnlySelected() {
		return selectedOnlyCheckbox.isSelected();
	}

	public boolean isDissolveBuffersSelected() {
		return dissolveEntitiesJCheckBox.isSelected();
	}

	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.MODALDIALOG);
			viewInfo.setTitle(PluginServices
					.getText(this, "Area_de_influencia"));
		}
		return viewInfo;
	}

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	/**
	 * This method initializes methodSelectionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMethodSelectionPanel() {
		if (methodSelectionPanel == null) {
			methodSelectionPanel = new JPanel();
			methodSelectionPanel.setLayout(null);
			methodSelectionPanel.setBounds(new java.awt.Rectangle(2, 127, 467,
					94));
			methodSelectionPanel
					.setBorder(javax.swing.BorderFactory
							.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
			methodSelectionPanel.add(getDistanceBufferRadioButton(), null);
			methodSelectionPanel.add(getBufferDistanceTextField(), null);
			methodSelectionPanel.add(getAttributeBufferRadioButton(), null);
			methodSelectionPanel.add(getLayerFieldsComboBox(), null);
			methodSelectionPanel.add(getDissolveEntitiesJCheckBox(), null);
			methodSelectionPanel.add(getEndCapCheckBox(), null);
		}
		return methodSelectionPanel;
	}

	/**
	 * This method initializes resultSelectionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getResultSelectionPanel() {
		if (resultSelectionPanel == null) {
			jLabel1 = new JLabel();
			jLabel1.setBounds(new java.awt.Rectangle(5, 9, 132, 24));
			jLabel1.setText(PluginServices.getText(this, "Cobertura_de_salida")
					+ ":");
			resultSelectionPanel = new JPanel();
			resultSelectionPanel.setLayout(null);
			resultSelectionPanel.setBounds(new java.awt.Rectangle(0, 291, 468,
					41));
			resultSelectionPanel
					.setBorder(javax.swing.BorderFactory
							.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
			resultSelectionPanel.add(getFileNameResultTextField(), null);
			resultSelectionPanel.add(getOpenFileButton(), null);
			resultSelectionPanel.add(jLabel1, null);
		}
		return resultSelectionPanel;
	}

	public boolean isSquareCap() {
		return getEndCapCheckBox().isSelected();
	}

	/**
	 * This method initializes endCapCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getEndCapCheckBox() {
		if (endCapCheckBox == null) {
			endCapCheckBox = new JCheckBox();
			endCapCheckBox.setBounds(new java.awt.Rectangle(209, 69, 235, 21));
			endCapCheckBox.setText(PluginServices.getText(this,
					"No_usar_buffer_redondeado"));
		}
		return endCapCheckBox;
	}

	/**
	 * This method initializes extendedOptionsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getExtendedOptionsPanel() {
		if (extendedOptionsPanel == null) {
			extendedOptionsPanel = new JPanel();
			extendedOptionsPanel.setLayout(null);
			extendedOptionsPanel.setBounds(new java.awt.Rectangle(2, 226, 465,
					62));
			extendedOptionsPanel
					.setBorder(javax.swing.BorderFactory
							.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
			extendedOptionsPanel.add(getTypePolygonBufferPanel(), null);
			extendedOptionsPanel.add(getNumBuffersPanel(), null);
		}
		return extendedOptionsPanel;
	}

	/**
	 * This method initializes typePolygonBufferPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTypePolygonBufferPanel() {
		if (typePolygonBufferPanel == null) {
			typeBufferLabel = new JLabel();
			typeBufferLabel.setBounds(new java.awt.Rectangle(10, 3, 143, 18));
			typeBufferLabel.setText(PluginServices
					.getText(this, "Crear_Buffer"));
			typePolygonBufferPanel = new JPanel();
			typePolygonBufferPanel.setLayout(null);
			typePolygonBufferPanel.setBounds(new java.awt.Rectangle(8, 6, 449,
					24));
			typePolygonBufferPanel.add(typeBufferLabel, null);
			typePolygonBufferPanel.add(getTypeBufferComboBox(), null);
		}
		return typePolygonBufferPanel;
	}

	/**
	 * This method initializes typeBufferComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getTypeBufferComboBox() {
		if (typeBufferComboBox == null) {
			typeBufferComboBox = new JComboBox();
			typeBufferComboBox
					.setBounds(new java.awt.Rectangle(167, 4, 270, 18));
			typeBufferComboBox.addItem(BUFFER_INSIDE);
			typeBufferComboBox.addItem(BUFFER_OUTSIDE);
			typeBufferComboBox.addItem(BUFFER_INSIDE_OUTSIDE);
			typeBufferComboBox.setSelectedItem(BUFFER_OUTSIDE);
		}
		return typeBufferComboBox;
	}

	/**
	 * Returns the type of polygon byffer (inside, outside, inside and outside)
	 * selected by user
	 * 
	 * @return
	 */
	public String getTypePolygonBuffer() {
		return (String) getTypeBufferComboBox().getSelectedItem();
	}

	/**
	 * Allow to enable/disable typePolygonBufferPanel (and all its components).
	 * The reason is that this customizations are only possible with polygon
	 * layers.
	 * 
	 * @param enable
	 */
	public void enableTypePolygonBufferPanel(boolean enable) {
		getTypePolygonBufferPanel().setEnabled(enable);
		getTypeBufferComboBox().setEnabled(enable);
	}

	/**
	 * This method initializes numBuffersPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getNumBuffersPanel() {
		if (numBuffersPanel == null) {
			radialBufferLabel = new JLabel();
			radialBufferLabel.setBounds(new java.awt.Rectangle(5, 3, 269, 19));
			radialBufferLabel.setText(PluginServices.getText(this,
					"Numero_anillos_concentricos"));
			numBuffersPanel = new JPanel();
			numBuffersPanel.setLayout(null);
			numBuffersPanel.setBounds(new java.awt.Rectangle(10, 35, 448, 24));
			numBuffersPanel.add(radialBufferLabel, null);
			numBuffersPanel.add(getRadialBufferSpinner(), null);
		}
		return numBuffersPanel;
	}

	/**
	 * This method initializes radialBufferTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JSpinner getRadialBufferSpinner() {
		if (radialBufferSpinner == null) {
			Integer one = new Integer(1);
			Integer two = new Integer(2);
			Integer three = new Integer(3);
			SpinnerListModel listModel = new SpinnerListModel(new Integer[] {
					one, two, three });
			radialBufferSpinner = new JSpinner(listModel);
			radialBufferSpinner.setBounds(new java.awt.Rectangle(298, 3, 137,
					19));

			// Disable keyboard edits in the spinner
			JFormattedTextField tf = ((JSpinner.DefaultEditor) radialBufferSpinner
					.getEditor()).getTextField();
			tf.setEditable(false);
			tf.setBackground(Color.white);
		}
		return radialBufferSpinner;
	}

	/**
	 * Returns the number of radial buffers selected by user (by now, only a
	 * maximum of three radial buffers allowed)
	 * 
	 * @return
	 */
	public int getNumberOfRadialBuffers() {
		return ((Integer) radialBufferSpinner.getValue()).intValue();
	}
} // @jve:decl-index=0:visual-constraint="25,13"
