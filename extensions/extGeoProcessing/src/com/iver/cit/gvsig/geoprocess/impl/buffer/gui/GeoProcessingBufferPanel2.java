/*
 * Created on 28-jul-2006
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
 * $Id: GeoProcessingBufferPanel2.java 10626 2007-03-06 16:55:54Z caballero $
 * $Log$
 * Revision 1.2  2007-03-06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.1  2006/08/11 16:14:17  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.buffer.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessGridbagPanel;

public class GeoProcessingBufferPanel2 extends AbstractGeoprocessGridbagPanel
		implements BufferPanelIF {

	// first row->radio button and text field to entry dists
	JRadioButton distanceBufferRadioButton;
	JTextField bufferDistanceTextField;

	// second row: field values, combo with fields
	JRadioButton attributeBufferRadioButton;
	JComboBox layerFieldsComboBox;

	// third row: user selections
	JCheckBox dissolveEntitiesJCheckBox;
	JCheckBox endCapCheckBox;

	// more user selections
	JLabel typeBufferLabel;
	JComboBox typeBufferComboBox;
	JLabel radialBufferLabel;
	JSpinner radialBufferSpinner;

	/**
	 * Constructor.
	 * 
	 */
	public GeoProcessingBufferPanel2(FLayers layers) {
		super(layers, PluginServices.getText(null,
				"Areas_de_influencia._Introduccion_de_datos") + ":");
	}

	protected void addSpecificDesign() {
		Insets insets = new Insets(5, 5, 5, 5);

		// row of constant distance radio button and text field
		this.distanceBufferRadioButton = new JRadioButton();
		this.distanceBufferRadioButton.setText(PluginServices.getText(this,
				"Area_de_influencia_definida_por_una_distancia") + ":");
		this.distanceBufferRadioButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						constantDistanceSelected();
					}
				});
		this.bufferDistanceTextField = new JTextField();
		addComponent(distanceBufferRadioButton, bufferDistanceTextField,
				GridBagConstraints.BOTH, insets);

		// row of attribute based distance and fields combo box
		this.attributeBufferRadioButton = new JRadioButton();
		this.attributeBufferRadioButton.setText(PluginServices.getText(this,
				"Area_de_influencia_definida_por_un_campo") + ":");
		this.attributeBufferRadioButton.setBounds(new java.awt.Rectangle(2, 41,
				287, 21));
		this.attributeBufferRadioButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						attributeDistanceSelected();
					}
				});
		this.layerFieldsComboBox = new JComboBox();
		this.layerFieldsComboBox.setBounds(new java.awt.Rectangle(308, 41, 138,
				21));
		DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(
				getFieldNames());
		this.layerFieldsComboBox.setModel(defaultModel);
		addComponent(attributeBufferRadioButton, layerFieldsComboBox,
				GridBagConstraints.BOTH, insets);
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(distanceBufferRadioButton);
		buttonGroup.add(attributeBufferRadioButton);

		// row of options check boxes
		this.dissolveEntitiesJCheckBox = new JCheckBox();
		this.dissolveEntitiesJCheckBox.setText(PluginServices.getText(this,
				"Disolver_entidades"));

		this.endCapCheckBox = new JCheckBox();
		this.endCapCheckBox.setText(PluginServices.getText(this,
				"No_usar_buffer_redondeado"));
		addComponent(dissolveEntitiesJCheckBox, endCapCheckBox,
				GridBagConstraints.NONE, insets);

		this.typeBufferLabel = new JLabel();
		this.typeBufferLabel.setText(PluginServices.getText(this,
				"Crear_Buffer"));

		this.typeBufferComboBox = new JComboBox();
		this.typeBufferComboBox.addItem(BUFFER_INSIDE);
		this.typeBufferComboBox.addItem(BUFFER_OUTSIDE);
		this.typeBufferComboBox.addItem(BUFFER_INSIDE_OUTSIDE);
		this.typeBufferComboBox.setSelectedItem(BUFFER_OUTSIDE);
		addComponent(typeBufferLabel, typeBufferComboBox,
				GridBagConstraints.NONE, insets);

		this.radialBufferLabel = new JLabel();
		this.radialBufferLabel.setText(PluginServices.getText(this,
				"Numero_anillos_concentricos"));
		Integer one = new Integer(1);
		Integer two = new Integer(2);
		Integer three = new Integer(3);
		SpinnerListModel listModel = new SpinnerListModel(new Integer[] { one,
				two, three });
		this.radialBufferSpinner = new JSpinner(listModel);
		this.radialBufferSpinner.setBounds(new java.awt.Rectangle(298, 3, 137,
				19));
		// Disable keyboard edits in the spinner
		JFormattedTextField tf = ((JSpinner.DefaultEditor) radialBufferSpinner
				.getEditor()).getTextField();
		tf.setEditable(false);
		tf.setBackground(Color.white);
		addComponent(radialBufferLabel, radialBufferSpinner,
				GridBagConstraints.BOTH, insets);

		initSelectedItemsJCheckBox();
		updateNumSelectedFeaturesLabel();
		this.distanceBufferRadioButton.setSelected(true);
		this.layerFieldsComboBox.setEnabled(false);
		this.verifyTypeBufferComboEnabled();

	}

	/**
	 * Returns the number of radial buffers selected by user (by now, only a
	 * maximum of three radial buffers allowed)
	 * 
	 * @return
	 */
	public int getNumberOfRadialBuffers() {
		return ((Integer) this.radialBufferSpinner.getValue()).intValue();
	}

	private JTextField getBufferDistanceTextField() {
		return this.bufferDistanceTextField;
	}

	private JComboBox getLayerFieldsComboBox() {
		return this.layerFieldsComboBox;
	}

	public void constantDistanceSelected() {
		getBufferDistanceTextField().setEnabled(true);
		getLayerFieldsComboBox().setEnabled(false);

	}

	public void attributeDistanceSelected() {
		getBufferDistanceTextField().setEnabled(false);
		getLayerFieldsComboBox().setEnabled(true);

	}

	public String getTypePolygonBuffer() {
		return (String) this.typeBufferComboBox.getSelectedItem();
	}

	public boolean isSquareCap() {
		return this.endCapCheckBox.isSelected();
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

	protected void processLayerComboBoxStateChange(ItemEvent e) {
		// Cambiar el estado del CheckBox
		// lo comento pq esto se ha añadido a la clase padre
		// initSelectedItemsJCheckBox();

		// Cambiar el estado del layerFieldsComboBox
		DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(
				getFieldNames());
		layerFieldsComboBox.setModel(defaultModel);
		verifyTypeBufferComboEnabled();
	}

	private void verifyTypeBufferComboEnabled() {
		String layerName = (String) this.layersComboBox.getSelectedItem();
		FLyrVect layer = (FLyrVect) this.layers.getLayer(layerName);
		boolean enable = false;
		try {
			enable = layer.getShapeType() == XTypes.POLYGON;
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		enableTypePolygonBufferPanel(enable);
	}

	public void enableTypePolygonBufferPanel(boolean enable) {
		this.typeBufferComboBox.setEnabled(enable);
	}

	public boolean isConstantDistanceSelected() {
		return this.distanceBufferRadioButton.isSelected();
	}

	public boolean isAttributeDistanceSelected() {
		return this.attributeBufferRadioButton.isSelected();
	}

	public double getConstantDistance() throws GeoprocessException {
		try {
			String strDist = this.bufferDistanceTextField.getText();
			return Double.parseDouble(strDist);
		} catch (NumberFormatException ex) {
			throw new GeoprocessException(
					"Distancia de buffer introducida no numerica");
		}
	}

	public String getAttributeDistanceField() throws GeoprocessException {
		String attributeField = (String) this.layerFieldsComboBox
				.getSelectedItem();
		FLyrVect selectedLayer = getInputLayer();
		try {
			SelectableDataSource selectable = selectedLayer.getRecordset();

			int fieldIndex = selectable.getFieldIndexByName(attributeField);
			int fieldType = selectable.getFieldType(fieldIndex);

			if (!XTypes.isNumeric(fieldType))
				throw new GeoprocessException(
						"Atributo no numerico para distancia de buffer");

		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return attributeField;
	}

	public boolean isBufferOnlySelected() {
		return super.isFirstOnlySelected();
	}

	public boolean isDissolveBuffersSelected() {
		return this.dissolveEntitiesJCheckBox.isSelected();
	}

}
