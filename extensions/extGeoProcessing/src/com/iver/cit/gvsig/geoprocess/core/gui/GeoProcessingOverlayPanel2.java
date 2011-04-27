/*
 * Created on 01-ago-2006
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
 * $Id: GeoProcessingOverlayPanel2.java 18949 2008-02-13 21:58:37Z azabala $
 * $Log$
 * Revision 1.2  2007-03-06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.1  2006/08/11 16:11:38  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.layerOperations.SingleLayer;
import com.iver.cit.gvsig.geoprocess.core.fmap.FMapUtil;

public class GeoProcessingOverlayPanel2 extends AbstractGeoprocessGridbagPanel
		implements OverlayPanelIF {

	private JComboBox secondLayerComboBox;

	private JCheckBox secondLayerSelectedCheckBox;

	private JLabel secondLayerNumSelectedLabel;

	/**
	 * Constructor.
	 *
	 */
	public GeoProcessingOverlayPanel2(FLayers layers, String titleText) {
		super(layers, titleText);
	}

	protected void addSpecificDesign() {
		Insets insets = new Insets(5, 5, 5, 5);
		String secondLayerText = PluginServices.getText(this,
				"Cobertura_de_recorte")
				+ ":";
		secondLayerComboBox = getSecondLayerComboBox();
		addComponent(secondLayerText,
					secondLayerComboBox,
					GridBagConstraints.BOTH,
						insets);

		secondLayerSelectedCheckBox = new JCheckBox();
		secondLayerSelectedCheckBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent arg0) {
				updateNumSelectedSecondLabel();
			}});
		secondLayerSelectedCheckBox.setText(PluginServices.
				getText(this, "Usar_solamente_los_elementos_seleccionados"));
		addComponent(secondLayerSelectedCheckBox,
				GridBagConstraints.BOTH,
				insets);

		String secondLayerNumSelectedText =
			PluginServices.getText(this,
				"Numero_de_elementos_seleccionados")
				+ ":";
		secondLayerNumSelectedLabel = new JLabel("00");
		addComponent(secondLayerNumSelectedText,
					secondLayerNumSelectedLabel,
						insets);

		initSelectedItemsJCheckBox();
		updateNumSelectedFeaturesLabel();
		initSelectedItems2JCheckBox();
		updateNumSelectedSecondLabel();
	}

	protected JComboBox getSecondLayerComboBox() {
		JComboBox solution = new JComboBox();
		DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(
				FMapUtil.getLayerNames(layers));
		solution.setModel(defaultModel);
		solution.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					initSelectedItems2JCheckBox();
					updateNumSelectedSecondLabel();
					processLayer2ComboBoxStateChange(e);
				}
			}// itemStateChange
		});
		return solution;
	}


	protected void initSelectedItems2JCheckBox(){
		String selectedLayer =
			(String) secondLayerComboBox.getSelectedItem();
		FLyrVect overlayLayer =
			(FLyrVect) layers.getLayer(selectedLayer);
		FBitSet fBitSet = null;
		try {
			fBitSet = overlayLayer.getRecordset().getSelection();
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (fBitSet.cardinality() == 0) {
			secondLayerSelectedCheckBox.setEnabled(false);
			secondLayerSelectedCheckBox.setSelected(false);
		} else {
			secondLayerSelectedCheckBox.setEnabled(true);
			secondLayerSelectedCheckBox.setSelected(true);
		}
		secondLayerSelectedCheckBox.setSelected(false);
	}

	/**
	 * updates the label's text with the number of features
	 * to operate with in the second layer
	 *
	 */
	protected void updateNumSelectedSecondLabel() {
		if (secondLayerSelectedCheckBox.isSelected()) {
			FLyrVect inputSelectable =
				(FLyrVect) (layers.getLayer(
						(String) secondLayerComboBox.
						getSelectedItem()));
			FBitSet fBitSet = null;
			try {
				fBitSet = inputSelectable.
							getRecordset().
							getSelection();
			} catch (ReadDriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			secondLayerNumSelectedLabel.
				setText(new Integer(fBitSet.
							cardinality()).
							toString());
		} else {
			ReadableVectorial va =
				((SingleLayer) (layers.getLayer(
						(String) secondLayerComboBox.
						getSelectedItem()))).getSource();
			try {
				secondLayerNumSelectedLabel.setText(new Integer(va
						.getShapeCount()).toString());
			} catch (ReadDriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//else
	}


	//cambiar esto para que lo implemente la clase padre,
	//y que las hijas simplemente sobreescriban y llamen a super()
	/**
	 * Processess events selection in first layer combo box
	 * (adding additional logic to abstract base class
	 */
	protected void processLayerComboBoxStateChange(ItemEvent e) {
	}

	/**
	 * Subclasses those want to overwrite logic of this component
	 * must overwrites this method
	 * @param e
	 */
	protected void processLayer2ComboBoxStateChange(ItemEvent e) {
	}


	public FLyrVect getSecondLayer() {
		String clippingLayer =
			(String) secondLayerComboBox.getSelectedItem();
		try {
			return (FLyrVect) layers.getLayer(clippingLayer);
		} catch (Exception e) {
			return null;
		}
	}

	public boolean onlyFirstLayerSelected() {
		return super.isFirstOnlySelected();
	}

	public boolean onlySecondLayerSelected() {
		return secondLayerSelectedCheckBox.isSelected();
	}

	public void openResultFileDialog() {
		super.openResultFile();
	}

	public void firstLayerSelectionChecked(boolean checked) {
		initSelectedItemsJCheckBox();
		updateNumSelectedFeaturesLabel();
	}

	public void secondLayerSelectionChecked(boolean checked) {
		initSelectedItems2JCheckBox();
		updateNumSelectedSecondLabel();
	}

}
