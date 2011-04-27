package com.iver.cit.gvsig.geoprocess.impl.build.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessGridbagPanel;
import com.iver.cit.gvsig.geoprocess.impl.build.IBuildGeoprocessUserEntries;

public class BuildGeoprocessPanel extends AbstractGeoprocessGridbagPanel
		implements IBuildGeoprocessUserEntries {
	
	private JCheckBox addGroupOfLyrsCb;
	
	private JCheckBox cleanBeforeCb;
	
	private JTextField txtFuzzy;
	
	private JTextField txtDangle;

	public BuildGeoprocessPanel(FLayers layers) {
		super(layers,"Build");
	}
	
	@Override
	protected void addSpecificDesign() {
		selectedOnlyCheckBox.setEnabled(true);
		addGroupOfLyrsCb = new JCheckBox();
		this.addGroupOfLyrsCb.setText(PluginServices.getText(this,"Añadir_al_TOC_geometrias_erroneas"));
		addComponent(addGroupOfLyrsCb,GridBagConstraints.NONE,new Insets(5, 5, 5, 5));
		
		cleanBeforeCb = new JCheckBox();
		cleanBeforeCb.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if(((JCheckBox)e.getItem()).isSelected()) {
					txtFuzzy.setEnabled(true);
					txtDangle.setEnabled(true);
				}
				else {
					txtFuzzy.setEnabled(false);
					txtDangle.setEnabled(false);
				}
			}
			
		});
		this.cleanBeforeCb.setText(PluginServices.getText(this,"Limpiar_antes_geometrias"));
		addComponent(cleanBeforeCb,GridBagConstraints.NONE,new Insets(5, 5, 5, 5));
		
		txtFuzzy = new JTextField();
		txtFuzzy.setEnabled(false);
		addComponent(PluginServices.getText(null, "Tolerancia_fuzzy"),txtFuzzy,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,100));
		
		txtDangle = new JTextField();
		txtDangle.setEnabled(false);
		addComponent(PluginServices.getText(null, "Max_lenght_dangle_nodes"),txtDangle,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,100));
		
		initSelectedItemsJCheckBox();
		updateNumSelectedFeaturesLabel();
	}

	@Override
	protected void processLayerComboBoxStateChange(ItemEvent e) {

	}

	public boolean buildOnlySelection() {
		return isFirstOnlySelected();
	}

	public boolean createLyrsWithErrorGeometries() {
		if(addGroupOfLyrsCb != null)
			return addGroupOfLyrsCb.isSelected();
		return false;
	}

	public boolean cleanBefore() {
		return cleanBeforeCb.isSelected();
	}

	public double getFuzzyTolerance() throws GeoprocessException {
		double fuzzy = 0;
		try {
			fuzzy =  Double.valueOf(txtFuzzy.getText());
		}
		catch(Exception e){
			e.printStackTrace();
			throw new GeoprocessException(PluginServices.getText(null, "Wrong_specified_fuzzy_tolerance"));
		}
		return fuzzy;
	}
	
	public double getDangleTolerance()  throws GeoprocessException{
		double fuzzy = 0;
		try {
			fuzzy =  Double.valueOf(txtDangle.getText());
		}
		catch(Exception e){
			e.printStackTrace();
			throw new GeoprocessException(PluginServices.getText(null, "Wrong_specified_dangle_tolerance"));
		}
		return fuzzy;
	}

}
