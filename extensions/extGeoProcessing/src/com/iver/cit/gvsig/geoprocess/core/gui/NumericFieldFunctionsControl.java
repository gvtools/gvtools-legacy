/*
 * Created on 09-ago-2006
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
* $Id: NumericFieldFunctionsControl.java 13881 2007-09-19 16:22:04Z jaume $
* $Log$
* Revision 1.4  2007-09-19 16:02:53  jaume
* removed unnecessary imports
*
* Revision 1.3  2007/08/07 15:06:59  azabala
* bug solved when a layer vect hasnt numeric fields
*
* Revision 1.2  2007/03/06 16:47:58  caballero
* Exceptions
*
* Revision 1.1  2006/08/11 16:11:38  azabala
* first version in cvs
*
*
*/
package com.iver.cit.gvsig.geoprocess.core.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;

import org.gvsig.gui.beans.swing.JButton;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.fmap.SummarizationFunction;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.geoprocess.core.gui.NumericFieldListModel.NumericFieldListEntry;

/**
 * Control to select sumarization functions for a given
 * numeric field of a layer
 * @author azabala
 *
 */
public class NumericFieldFunctionsControl extends JPanel {

	private FLyrVect inputLayer;


	private JList numericFieldList;
	private JList sumarizeFunctionsList;
	private SumarizeFunctionsDialog sumarizeFuncDialog;
	private Map nField_sumFuntionList;
	private JButton sumFunctionSelectionJButton;



	public NumericFieldFunctionsControl(FLyrVect inputLayer){
		super();
		initialize();
		setLayer(inputLayer);
	}



	public void initialize(){
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.
				createEtchedBorder(EtchedBorder.RAISED));

		GridBagConstraints buttonConstraints =
			new GridBagConstraints();
		buttonConstraints.gridy = 1;
		buttonConstraints.gridx = 1;
		buttonConstraints.ipadx = 4;
		buttonConstraints.ipady = 2;
		buttonConstraints.weightx = 1f;
		buttonConstraints.fill = GridBagConstraints.NONE;

		GridBagConstraints attrsLabelConstraints = new GridBagConstraints();
		attrsLabelConstraints.insets = new java.awt.Insets(7, 11, 4, 106);
		attrsLabelConstraints.gridx = 0;
		attrsLabelConstraints.gridy = 0;
		attrsLabelConstraints.ipadx = 50;
		attrsLabelConstraints.ipady = 2;
		attrsLabelConstraints.gridwidth = 2;

		GridBagConstraints functionsLabelConstraints = new GridBagConstraints();
		functionsLabelConstraints.insets = new java.awt.Insets(7, 4, 4, 10);
		functionsLabelConstraints.gridy = 0;
		functionsLabelConstraints.ipadx = 50;
		functionsLabelConstraints.ipady = 2;
		functionsLabelConstraints.gridx = 2;

		GridBagConstraints functionScrollConstraints = new GridBagConstraints();
		functionScrollConstraints.fill = java.awt.GridBagConstraints.BOTH;
		functionScrollConstraints.gridx = 2;
		functionScrollConstraints.gridy = 1;
		functionScrollConstraints.ipadx = 120;
		functionScrollConstraints.ipady = 19;
		functionScrollConstraints.weightx = 0.75;
		functionScrollConstraints.weighty = 1.0;
		functionScrollConstraints.insets = new java.awt.Insets(5, 24, 1, 10);

		GridBagConstraints numericAttrScrollConstraints = new GridBagConstraints();
		numericAttrScrollConstraints.fill = java.awt.GridBagConstraints.BOTH;
		numericAttrScrollConstraints.gridx = 0;
		numericAttrScrollConstraints.gridy = 1;
		numericAttrScrollConstraints.ipadx = 120;
		numericAttrScrollConstraints.ipady = 19;
		numericAttrScrollConstraints.weightx = 0.75;
		numericAttrScrollConstraints.weighty = 1.0;
		numericAttrScrollConstraints.insets = new java.awt.Insets(5, 12, 1,
				9);

		JScrollPane numericAttrsScrollPane = new JScrollPane();
		numericFieldList = new JList();
		numericFieldList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		numericAttrsScrollPane.setViewportView(numericFieldList);
		numericAttrsScrollPane.setEnabled(false);
		numericAttrsScrollPane.setPreferredSize(new Dimension(150, 150));
		numericAttrsScrollPane.setMinimumSize(new Dimension(100, 100));
		add(numericAttrsScrollPane,
				numericAttrScrollConstraints);

		JScrollPane sumarizationFunctionScrollPane = new JScrollPane();
		sumarizeFunctionsList = new JList();
		sumarizeFunctionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		sumarizationFunctionScrollPane
				.setViewportView(sumarizeFunctionsList);
		sumarizationFunctionScrollPane.setPreferredSize(new Dimension(150,
				150));
		sumarizationFunctionScrollPane.setMinimumSize(new Dimension(100,
				100));
		add(sumarizationFunctionScrollPane,
				functionScrollConstraints);

		JLabel functionsLabel = new JLabel(PluginServices.getText(this,
				"Funciones_Sumarizacion"));
		add(functionsLabel,
				functionsLabelConstraints);

		JLabel attrsLabel = new JLabel(PluginServices.getText(this,
				"Atributos_Numericos"));

		add(attrsLabel, attrsLabelConstraints);

		sumFunctionSelectionJButton = new JButton();
		sumFunctionSelectionJButton.setText(PluginServices.getText(this,
				"Escoger_Fun_Resumen"));
		sumFunctionSelectionJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openSumarizeFunction();
			}
		});
		add(sumFunctionSelectionJButton,
				buttonConstraints);

	}

	public void openSumarizeFunction() {
		if (sumarizeFuncDialog == null) {
			sumarizeFuncDialog = new SumarizeFunctionsDialog();
			sumarizeFuncDialog.pack();
		}
		sumarizeFuncDialog.setSize(205, 181);
		sumarizeFuncDialog.resetCheckbox();
		sumarizeFuncDialog.setVisible(true);
		SummarizationFunction[] functions = sumarizeFuncDialog.getFunctions();

		NumericFieldListEntry fieldName = (NumericFieldListEntry) numericFieldList.getSelectedValue();
//		if it returns "", there arent any fields (disable button if list is empty??)
		if(fieldName.getKey() != null  ){
				if(!fieldName.getKey().equals(""))
						nField_sumFuntionList.put(fieldName.getKey(), functions);
		}
		// refresh list and listModel
		String[] numericFields = getInputLayerNumericFields();
		SumFuncListModel functionListModel = new SumFuncListModel(
				nField_sumFuntionList, numericFields);
		sumarizeFunctionsList.setModel(functionListModel);
		this.sumarizeFunctionsList.setSelectedIndex(0);
	}

	public Map getFieldFunctionMap() {
		return this.nField_sumFuntionList;
	}

	public String[] getFieldsToSummarize() {
		String[] solution = null;
		Set keySet = nField_sumFuntionList.keySet();
		solution = new String[keySet.size()];
		keySet.toArray(solution);
		return solution;
	}

	public SummarizationFunction[] getSumarizationFunctinFor(
			String numericFieldName) {
		return (SummarizationFunction[]) nField_sumFuntionList
				.get(numericFieldName);
	}

	public void setLayer(FLyrVect inputLayer){
		this.inputLayer = inputLayer;
		String[] numericFields = getInputLayerNumericFields();
		NumericFieldListModel numericListModel = new NumericFieldListModel(
				numericFields);
		this.numericFieldList.setModel(numericListModel);
		this.numericFieldList.setSelectedIndex(0);

		this.nField_sumFuntionList = new HashMap();
		SumFuncListModel functionListModel = new SumFuncListModel(
				this.nField_sumFuntionList, numericFields);
		sumarizeFunctionsList.setModel(functionListModel);
		sumarizeFunctionsList.setSelectedIndex(0);
		if(numericFields.length == 0){
			sumFunctionSelectionJButton.setEnabled(false);
		}
	}

	/**
	 * Returns numeric fields' names of the selected input layer. Needed to say
	 * user where he could apply sumarization functions.
	 */
	public String[] getInputLayerNumericFields() {
		return XTypes.getNumericFieldsNames(inputLayer);
	}

}

