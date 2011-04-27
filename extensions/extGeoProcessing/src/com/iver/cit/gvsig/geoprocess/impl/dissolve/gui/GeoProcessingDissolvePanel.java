/*
 * Created on 14-jul-2005
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
package com.iver.cit.gvsig.geoprocess.impl.dissolve.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
import com.iver.cit.gvsig.geoprocess.core.fmap.SummarizationFunction;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessPanel;
import com.iver.cit.gvsig.geoprocess.core.gui.NumericFieldListModel;
import com.iver.cit.gvsig.geoprocess.core.gui.SumFuncListModel;
import com.iver.cit.gvsig.geoprocess.core.gui.SumarizeFunctionsDialog;
import com.iver.cit.gvsig.geoprocess.core.gui.NumericFieldListModel.NumericFieldListEntry;
import com.iver.utiles.GenericFileFilter;

/**
 * Panel to work with DissolveGeoprocess in Geoprocessing Wizard.
 *
 * Precondition: all inputs layer of constructor must be FLyrVect
 *
 * @author azabala
 *
 */
public class GeoProcessingDissolvePanel extends AbstractGeoprocessPanel implements
		DissolvePanelIF, IWindow {

	private static final long serialVersionUID = 1L;

	private JLabel titleJLabel = null;

	private JLabel inputCoverJLabel = null;
	/**
	 * Allows user to specify if a dissolve precondition
	 * in two polygons with the same dissolve field value must
	 * be spatial adjacency
	 */
	private JCheckBox onlySelectedItemsJCheckBox = null;


	private JLabel dissolveFieldJLabel = null;
	/**
	 * Allows user to specify dissolve field
	 */
	private JComboBox dissolveFieldJComboBox = null;

	private JScrollPane additionalOperationsJScrollPane = null;

	private JList numericFieldJList = null;

	private JButton outputCoverJButton = null;

	private File outputFile = null;

	private JScrollPane sumarizationFunctionScrollPane = null;

	private JList sumarizeFunctionsJList;

	private JButton sumFunctionSelectionJButton = null;

	private SumarizeFunctionsDialog sumarizeFuncDialog = null;

	/**
	 * Relates a numeric field with its sumarize functions
	 */
	private Map nField_sumFuntionList = null;

	private WindowInfo viewInfo;

	private JPanel resultLayerPanel = null;

	private JLabel jLabel = null;

	private JPanel sumarizeAttributesPanel = null;

	private JLabel jLabel1 = null;

	private JLabel jLabel2 = null;

	private JCheckBox adjacencyCheckbox = null;
	/**
	 * This constructor initializes the set of layers
	 *
	 * TODO PRECONDITION: check for only FLyrVect layers
	 *
	 *
	 */
	public GeoProcessingDissolvePanel(FLayers layers) {
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
		dissolveFieldJLabel = new JLabel();
		dissolveFieldJLabel.setBounds(5, 93, 180, 22);
		dissolveFieldJLabel.setText(PluginServices.getText(this,
				"Campo_para_disolver")
				+ ":");
		inputCoverJLabel = new JLabel();
		inputCoverJLabel.setBounds(5, 36, 191, 22);
		inputCoverJLabel.setText(PluginServices.getText(this,
				"Cobertura_de_entrada")
				+ ":");
		titleJLabel = new JLabel();
		titleJLabel.setBounds(5, 5, 208, 22);
		titleJLabel.setText(PluginServices.getText(this,
				"Disolver._Introduccion_de_datos")
				+ ":");
		this.setLayout(null);
		this.setBounds(new java.awt.Rectangle(0,0,486,377));
		this.add(titleJLabel, null);
		this.add(inputCoverJLabel, null);
		this.add(getInputCoverJComboBox(), null);
		this.add(getOnlySelectedItemsJCheckBox(), null);
		this.add(dissolveFieldJLabel, null);
		this.add(getDissolveFieldJComboBox(), null);
		this.add(getResultLayerPanel(), null);
		this.add(getSumarizeAttributesPanel(), null);
		this.add(getAdjacencyCheckbox(), null);
		this.nField_sumFuntionList = new HashMap();
		layersComboBox.setSelectedIndex(0);
		inputLayerSelectedChange();
		// initSelectedItemsJCheckBox();
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

	/**
	 * Says if adjacency checkbox has been checked by
	 * user
	 * @return
	 */
	public boolean onlyAdjacentSelected(){
		return adjacencyCheckbox.isSelected();
	}

	private void initSelectedItemsJCheckBox() {
		FLyrVect inputSelectable = (FLyrVect) getInputLayer();
		FBitSet fBitSet = null;
		try {
			fBitSet = inputSelectable.getRecordset().getSelection();
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (fBitSet.cardinality() == 0) {
			onlySelectedItemsJCheckBox.setEnabled(false);
		} else {
			onlySelectedItemsJCheckBox.setEnabled(true);
		}
		onlySelectedItemsJCheckBox.setSelected(false);
	}

	/**
	 * This method initializes inputCoverJComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getInputCoverJComboBox() {
		if (layersComboBox == null) {
			layersComboBox = new JComboBox();
			DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(
					getLayerNames());
			layersComboBox.setModel(defaultModel);
			layersComboBox.setBounds(144, 36, 241, 22);
			layersComboBox
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							inputLayerSelectedChange();
						}
					});
		}
		return layersComboBox;
	}

	/**
	 * This method initializes onlySelectedItemsJCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getOnlySelectedItemsJCheckBox() {
		if (onlySelectedItemsJCheckBox == null) {
			onlySelectedItemsJCheckBox = new JCheckBox();
			onlySelectedItemsJCheckBox.setBounds(5, 64, 351, 22);
			onlySelectedItemsJCheckBox.setText(PluginServices.getText(this,
					"Usar_solamente_los_elementos_seleccionados"));
		}
		return onlySelectedItemsJCheckBox;
	}

	/**
	 * This method initializes dissolveFieldJComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getDissolveFieldJComboBox() {
		if (dissolveFieldJComboBox == null) {
			dissolveFieldJComboBox = new JComboBox();
			DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(
					getFieldNames());
			dissolveFieldJComboBox.setModel(defaultModel);
			dissolveFieldJComboBox.setBounds(227, 93, 241, 22);
		}
		return dissolveFieldJComboBox;
	}

	/**
	 * This method initializes additionalOperationsJScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getAdditionalOperationsJScrollPane() {
		if (additionalOperationsJScrollPane == null) {
			additionalOperationsJScrollPane = new JScrollPane();
			additionalOperationsJScrollPane
					.setViewportView(getNumericFieldJList());
			additionalOperationsJScrollPane.setEnabled(false);
			additionalOperationsJScrollPane.setBounds(new java.awt.Rectangle(14,36,139,112));
		}
		return additionalOperationsJScrollPane;
	}

	/**
	 * This method initializes numericFieldJList
	 *
	 * @return javax.swing.JList
	 */
	private JList getNumericFieldJList() {
		if (numericFieldJList == null) {
			numericFieldJList = new JList();
		}
		return numericFieldJList;
	}


	/**
	 * This method initializes outputCoverJButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getOutputCoverJButton() {
		if (outputCoverJButton == null) {
			outputCoverJButton = new JButton();
			outputCoverJButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							openResultFile();
						}
					});
			outputCoverJButton.setText(PluginServices.getText(this, "Abrir"));
		}
		return outputCoverJButton;
	}

	public void openResultFile() {
		// FIXME This code is similar in all GeoProcessingXXXPanels.
		// Create an utility class
		JFileChooser jfc = new JFileChooser();
		// FIXME Internationalize "Ficheros SHP" String
		jfc
				.addChoosableFileFilter(new GenericFileFilter("shp",
						"Ficheros SHP"));
		if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			if (!(file.getPath().endsWith(".shp") || file.getPath().endsWith(
					".SHP"))) {
				file = new File(file.getPath() + ".shp");
			}
			outputFile = file;
		}
		if (jfc.getSelectedFile() != null) {
			getFileNameResultTextField().setText(
					jfc.getSelectedFile().getAbsolutePath());
		}

	}

	public boolean isDissolveOnlySelected() {
		return onlySelectedItemsJCheckBox.isSelected();
	}

	public String getDissolveFieldName() {
		return (String) dissolveFieldJComboBox.getSelectedItem();
	}

	/**
	 * Returns numeric fields' names of the selected input layer.
	 * Needed to say user where he could apply sumarization functions.
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

	/**
	 * This method initializes sumarizationFunctionScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getSumarizationFunctionScrollPane() {
		if (sumarizationFunctionScrollPane == null) {
			sumarizationFunctionScrollPane = new JScrollPane();
			sumarizationFunctionScrollPane.setBounds(new java.awt.Rectangle(314,36,139,112));
			sumarizationFunctionScrollPane
					.setViewportView(getSumarizeFunctionsJList());

		}
		return sumarizationFunctionScrollPane;
	}

	/**
	 * Returns a list with SumarizationFunctions for each numerical field of
	 * field lists (in the same order).
	 *
	 * @return
	 */
	private JList getSumarizeFunctionsJList() {
		if (sumarizeFunctionsJList == null) {
			sumarizeFunctionsJList = new JList();
		}
		return sumarizeFunctionsJList;
	}

	/**
	 * This method initializes sumFunctionSelectionJButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getSumFunctionSelectionJButton() {
		if (sumFunctionSelectionJButton == null) {
			sumFunctionSelectionJButton = new JButton();
			sumFunctionSelectionJButton.setText(PluginServices.getText(this,
					"Escoger_Fun_Resumen"));
			sumFunctionSelectionJButton.setBounds(new java.awt.Rectangle(171,69,116,22));
			sumFunctionSelectionJButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					openSumarizeFunction();
				}

			});
		}
		return sumFunctionSelectionJButton;
	}

	/**
	 * Process change selection events of input layer combo box
	 */
	public void inputLayerSelectedChange() {
		// Cambiar el estado del CheckBox
		initSelectedItemsJCheckBox();
		// Cambiar el estado del jComboBox1
		DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(
				getFieldNames());
		dissolveFieldJComboBox.setModel(defaultModel);
		String[] numericFields = getInputLayerNumericFields();
		NumericFieldListModel numericListModel = new NumericFieldListModel(
				numericFields);
		this.numericFieldJList.setModel(numericListModel);

		this.nField_sumFuntionList = new HashMap();
		SumFuncListModel functionListModel = new SumFuncListModel(
				this.nField_sumFuntionList, numericFields);
		this.sumarizeFunctionsJList.setModel(functionListModel);
		this.sumarizeFunctionsJList.setSelectedIndex(0);

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

		NumericFieldListEntry fieldName = (NumericFieldListEntry) this.numericFieldJList.getSelectedValue();
		if(fieldName.getKey() != null  ){
			if(!fieldName.getKey().equals(""))
					nField_sumFuntionList.put(fieldName.getKey(), functions);
	    }
		this.nField_sumFuntionList.put(fieldName.getKey(), functions);

		// Hay que refrescar la lista y el listModel
		String[] numericFields = getInputLayerNumericFields();
		SumFuncListModel functionListModel = new SumFuncListModel(
				this.nField_sumFuntionList, numericFields);
		this.sumarizeFunctionsJList.setModel(functionListModel);
		this.sumarizeFunctionsJList.setSelectedIndex(0);

	}

	public String[] getFieldsToSummarize() {
		String[] solution = null;
		Set keySet = nField_sumFuntionList.keySet();
		solution = new String[keySet.size()];
		keySet.toArray(solution);
		return solution;

	}

	public Map getFieldFunctionMap() {
		return this.nField_sumFuntionList;
	}

	public SummarizationFunction[] getSumarizationFunctinFor(
			String numericFieldName) {
		return (SummarizationFunction[]) nField_sumFuntionList
				.get(numericFieldName);
	}

	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.MODALDIALOG);
			viewInfo.setTitle(PluginServices.getText(this,
					"Disolver"));
		}
		return viewInfo;
	}
	public Object getWindowProfile(){
		return WindowInfo.DIALOG_PROFILE;
	}

	/**
	 * This method initializes resultLayerPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getResultLayerPanel() {
		if (resultLayerPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new java.awt.Insets(14,3,9,4);
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.ipadx = 47;
			gridBagConstraints3.ipady = 6;
			gridBagConstraints3.gridx = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.ipadx = 173;
			gridBagConstraints2.ipady = 2;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.insets = new java.awt.Insets(14,4,9,7);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new java.awt.Insets(14,7,9,21);
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.ipadx = 11;
			gridBagConstraints1.ipady = -4;
			gridBagConstraints1.gridx = 2;
			jLabel = new JLabel();
			jLabel.setText(PluginServices.getText(this, "Cobertura_de_salida") + ":");
			resultLayerPanel = new JPanel();
			resultLayerPanel.setLayout(new GridBagLayout());
			resultLayerPanel.setBounds(new java.awt.Rectangle(4,296,465,45));
			resultLayerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
			resultLayerPanel.add(getOutputCoverJButton(), gridBagConstraints1);
			resultLayerPanel.add(getFileNameResultTextField(), gridBagConstraints2);
			resultLayerPanel.add(jLabel, gridBagConstraints3);
		}
		return resultLayerPanel;
	}

	/**
	 * This method initializes sumarizeAttributesPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getSumarizeAttributesPanel() {
		if (sumarizeAttributesPanel == null) {
			jLabel2 = new JLabel();
			jLabel2.setText(PluginServices.getText(this, "Funciones_Sumarizacion"));
			jLabel2.setBounds(new java.awt.Rectangle(294,9,159,18));
			jLabel1 = new JLabel();
			jLabel1.setText(PluginServices.getText(this, "Atributos_Numericos"));
			jLabel1.setBounds(new java.awt.Rectangle(13,9,171,18));
			sumarizeAttributesPanel = new JPanel();
			sumarizeAttributesPanel.setLayout(null);
			sumarizeAttributesPanel.setBounds(new java.awt.Rectangle(5,142,465,151));
			sumarizeAttributesPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
			sumarizeAttributesPanel.add(getAdditionalOperationsJScrollPane(), null);
			sumarizeAttributesPanel.add(getSumFunctionSelectionJButton(), null);
			sumarizeAttributesPanel.add(getSumarizationFunctionScrollPane(), null);
			sumarizeAttributesPanel.add(jLabel1, null);
			sumarizeAttributesPanel.add(jLabel2, null);
		}
		return sumarizeAttributesPanel;
	}

	/**
	 * This method initializes adjacencyCheckbox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getAdjacencyCheckbox() {
		if (adjacencyCheckbox == null) {
			adjacencyCheckbox = new JCheckBox();
			adjacencyCheckbox.setBounds(new java.awt.Rectangle(7,121,263,18));
			adjacencyCheckbox.setText(PluginServices.getText(this, "Solo_disolver_adyacentes"));
		}
		return adjacencyCheckbox;
	}

}  //  @jve:decl-index=0:visual-constraint="13,12"
