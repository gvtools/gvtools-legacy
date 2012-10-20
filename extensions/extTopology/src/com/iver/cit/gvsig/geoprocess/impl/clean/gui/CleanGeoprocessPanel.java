package com.iver.cit.gvsig.geoprocess.impl.clean.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessGridbagPanel;
import com.iver.cit.gvsig.geoprocess.impl.clean.ICleanGeoprocessUserEntries;

public class CleanGeoprocessPanel extends AbstractGeoprocessGridbagPanel
		implements ICleanGeoprocessUserEntries {

	private static final long serialVersionUID = -4903369751810022010L;

	private JTextField txtFuzzy;

	private JTextField txtDangle;

	// private JComboBox cbbOutputLayer;

	private JCheckBox addGroupOfLyrsCb;

	public CleanGeoprocessPanel(FLayers layers) {
		super(layers, PluginServices.getText(null, "LineClean"));
	}

	@Override
	protected void addSpecificDesign() {
		txtFuzzy = new JTextField();
		addComponent(PluginServices.getText(null, "Tolerancia_fuzzy"),
				txtFuzzy, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
						100));

		txtDangle = new JTextField();
		addComponent(PluginServices.getText(null, "Max_lenght_dangle_nodes"),
				txtDangle, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
						100));

		// cbbOutputLayer = getComboOutputLayer();
		// addComponent(PluginServices.getText(null,
		// "Output_layer"),cbbOutputLayer,GridBagConstraints.HORIZONTAL,new
		// Insets(5,5,5,100));

		addGroupOfLyrsCb = new JCheckBox();
		this.addGroupOfLyrsCb.setText(PluginServices.getText(this,
				"Añadir_al_TOC_geometrias_erroneas"));
		addComponent(addGroupOfLyrsCb, GridBagConstraints.NONE, new Insets(5,
				5, 5, 5));

		initSelectedItemsJCheckBox();
		updateNumSelectedFeaturesLabel();
	}

	// private JComboBox getComboOutputLayer() {
	// if(cbbOutputLayer == null) {
	// cbbOutputLayer = new JComboBox();
	// String[] layerTypes = new String[] {"líneas","polígonos"};
	// DefaultComboBoxModel model = new DefaultComboBoxModel(layerTypes);
	// cbbOutputLayer.setModel(model);
	// }
	//
	// return cbbOutputLayer;
	// }

	@Override
	protected void processLayerComboBoxStateChange(ItemEvent e) {

	}

	public boolean onlyFirstLayerSelected() {
		return isFirstOnlySelected();
	}

	public boolean createLyrsWithErrorGeometries() {
		return addGroupOfLyrsCb.isSelected();
	}

	public double getFuzzyTolerance() throws GeoprocessException {
		double fuzzy = 0;
		try {
			fuzzy = Double.valueOf(txtFuzzy.getText());
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeoprocessException(PluginServices.getText(null,
					"Wrong_specified_fuzzy_tolerance"));
		}
		return fuzzy;
	}

	public double getDangleTolerance() throws GeoprocessException {
		double fuzzy = 0;
		try {
			fuzzy = Double.valueOf(txtDangle.getText());
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeoprocessException(PluginServices.getText(null,
					"Wrong_specified_dangle_tolerance"));
		}
		return fuzzy;
	}

	public boolean cleanOnlySelection() {
		return this.onlyFirstLayerSelected();
	}

	// public String getOutputLayerType() {
	// return (String)cbbOutputLayer.getSelectedItem();
	// }

}
