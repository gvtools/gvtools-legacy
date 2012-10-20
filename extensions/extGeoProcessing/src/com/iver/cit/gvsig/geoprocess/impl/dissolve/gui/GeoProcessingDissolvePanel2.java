/*
 * Created on 03-ago-2006
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
 * $Id: GeoProcessingDissolvePanel2.java 10626 2007-03-06 16:55:54Z caballero $
 * $Log$
 * Revision 1.3  2007-03-06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.2  2006/09/21 18:21:09  azabala
 * fixed bug in sumarization function dialog
 *
 * Revision 1.1  2006/08/11 16:28:25  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.dissolve.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.geoprocess.core.fmap.SummarizationFunction;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessGridbagPanel;
import com.iver.cit.gvsig.geoprocess.core.gui.NumericFieldFunctionsControl;

public class GeoProcessingDissolvePanel2 extends AbstractGeoprocessGridbagPanel
		implements DissolvePanelIF {

	JComboBox dissolveFieldComboBox;

	JCheckBox dissolveAdjacentsCheck;

	JPanel sumarizationAttrsPanel;

	/**
	 * Constructor
	 * 
	 * @param layers
	 */
	public GeoProcessingDissolvePanel2(FLayers layers) {
		super(layers, PluginServices.getText(null,
				"Disolver._Introduccion_de_datos") + ":");
	}

	protected void addSpecificDesign() {
		// FIXME Si dejamos el insets a gusto del programador,
		// no saldra alineado con el inset de la clase abstracta
		// crear metodos especificos de geoprocessing que creen el Insets
		Insets insets = new Insets(5, 5, 5, 5);

		String dissolveFieldText = PluginServices.getText(this,
				"Campo_para_disolver") + ":";
		dissolveFieldComboBox = getDissolveFieldJComboBox();
		addComponent(dissolveFieldText, dissolveFieldComboBox,
				GridBagConstraints.BOTH, insets);

		dissolveAdjacentsCheck = new JCheckBox();
		dissolveAdjacentsCheck.setText(PluginServices.getText(this,
				"Solo_disolver_adyacentes"));
		addComponent(dissolveAdjacentsCheck, GridBagConstraints.BOTH, insets);

		sumarizationAttrsPanel = getSumarizeAttributesPanel();
		addComponent(sumarizationAttrsPanel, GridBagConstraints.HORIZONTAL,
				insets);

		initSelectedItemsJCheckBox();
		updateNumSelectedFeaturesLabel();

		inputLayerSelectedChange();

	}

	public boolean onlyAdjacentSelected() {
		return dissolveAdjacentsCheck.isSelected();
	}

	public Map getFieldFunctionMap() {
		return ((NumericFieldFunctionsControl) sumarizationAttrsPanel)
				.getFieldFunctionMap();
	}

	// TODO Llamar a NumericFieldFunctionControl
	public void openSumarizeFunction() {
		((NumericFieldFunctionsControl) sumarizationAttrsPanel)
				.openSumarizeFunction();

	}

	public boolean isDissolveOnlySelected() {
		return super.isFirstOnlySelected();
	}

	/**
	 * Processes layer selection event in input layer combo box
	 */
	public void inputLayerSelectedChange() {
		processLayerComboBoxStateChange(null);
	}

	protected void processLayerComboBoxStateChange(ItemEvent e) {
		// Cambiar el estado del jComboBox1
		DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(
				getFieldNames());
		dissolveFieldComboBox.setModel(defaultModel);
		((NumericFieldFunctionsControl) sumarizationAttrsPanel)
				.setLayer(getInputLayer());

	}

	public String getDissolveFieldName() {
		return (String) dissolveFieldComboBox.getSelectedItem();
	}

	/**
	 * Returns numeric fields' names of the selected input layer. Needed to say
	 * user where he could apply sumarization functions.
	 */
	public String[] getInputLayerNumericFields() {
		String[] solution;
		FLyrVect layer = getInputLayer();
		ArrayList list = new ArrayList();
		try {
			SelectableDataSource recordset = layer.getRecordset();
			int numFields = recordset.getFieldCount();
			for (int i = 0; i < numFields; i++) {
				if (XTypes.isNumeric(recordset.getFieldType(i))) {
					list.add(recordset.getFieldName(i));
				}
			}// for
		} catch (ReadDriverException e) {
			return null;
		}
		solution = new String[list.size()];
		list.toArray(solution);
		return solution;
	}

	public String[] getFieldsToSummarize() {
		return ((NumericFieldFunctionsControl) sumarizationAttrsPanel)
				.getFieldsToSummarize();

	}

	public SummarizationFunction[] getSumarizationFunctinFor(
			String numericFieldName) {
		return ((NumericFieldFunctionsControl) sumarizationAttrsPanel)
				.getSumarizationFunctinFor(numericFieldName);
	}

	private JComboBox getDissolveFieldJComboBox() {
		if (dissolveFieldComboBox == null) {
			dissolveFieldComboBox = new JComboBox();
			DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(
					getFieldNames());
			dissolveFieldComboBox.setModel(defaultModel);
		}
		return dissolveFieldComboBox;
	}

	private String[] getFieldNames() {
		AlphanumericData lyr = (AlphanumericData) getInputLayer();
		DataSource ds;
		String[] fieldNames = null;
		try {
			ds = lyr.getRecordset();
			fieldNames = new String[ds.getFieldCount()];
			for (int i = 0; i < ds.getFieldCount(); i++) {
				fieldNames[i] = ds.getFieldName(i);
			}
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}
		return fieldNames;
	}

	private JPanel getSumarizeAttributesPanel() {
		if (sumarizationAttrsPanel == null) {

			sumarizationAttrsPanel = new NumericFieldFunctionsControl(
					getInputLayer());
		}
		return sumarizationAttrsPanel;
	}

}
