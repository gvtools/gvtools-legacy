package com.iver.cit.gvsig.gui.preferencespage;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.iver.andami.PluginServices;
import com.iver.andami.preferences.AbstractPreferencePage;
import com.iver.andami.preferences.StoreException;
import com.iver.cit.gvsig.fmap.core.CartographicSupport;
import com.iver.cit.gvsig.fmap.core.CartographicSupportToolkit;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.gui.JComboBoxUnits;
import com.iver.cit.gvsig.gui.styling.JComboBoxUnitsReferenceSystem;
import com.iver.utiles.StringUtilities;
import com.iver.utiles.XMLEntity;

/**
 *
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class CartographicSupportPage extends AbstractPreferencePage {
	private static final String DefaultMeasureUnitKey = "DefaultMeasureUnitKey";
	private static final String DefaultUnitReferenceSystemKey = "DefaultUnitReferenceSystemKey";
	private JComboBoxUnits cmbUnits;
	private JComboBoxUnitsReferenceSystem cmbReferenceSystem;

	public CartographicSupportPage() {
		super();
		initialize();
	}

	private void initialize() {
		addComponent(PluginServices.getText(this, "default_measure_units"),
				cmbUnits = new JComboBoxUnits(true));
		addComponent(PluginServices.getText(this, "default_measure_units_reference_system"),
				cmbReferenceSystem = new JComboBoxUnitsReferenceSystem());
		addComponent(new JSeparator(JSeparator.HORIZONTAL));
	}

	// pending of a proposed refactor, don't erase
	public void persistPreferences() throws StoreException {
		PluginServices ps = PluginServices.getPluginServices(this);
		XMLEntity xml = ps.getPersistentXML();
		xml.putProperty(DefaultMeasureUnitKey, cmbUnits.getSelectedUnitIndex());
		xml.putProperty(DefaultUnitReferenceSystemKey, cmbReferenceSystem.getSelectedIndex());
	}

	@Override
	public void setChangesApplied() {
		setChanged(false);
	}

	public void applyValuesFromPersistence() throws StoreException {
		PluginServices ps = PluginServices.getPluginServices(this);
		XMLEntity xml = ps.getPersistentXML();
		if (xml.contains(DefaultMeasureUnitKey))
			CartographicSupportToolkit.DefaultMeasureUnit = xml.getIntProperty(DefaultMeasureUnitKey);
		if (xml.contains(DefaultUnitReferenceSystemKey))
			CartographicSupportToolkit.DefaultReferenceSystem = xml.getIntProperty(DefaultUnitReferenceSystemKey);
	}

	public String getID() {
		return getClass().getName();
	}

	public ImageIcon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	public JPanel getPanel() {
		return this;
	}

	public String getTitle() {
		return PluginServices.getText(this, "cartographic_support");
	}

	// pending of a refactoring do not delete (swap commented lines)
//	public void initializeComponents() {
	public void initializeValues() {

		PluginServices ps = PluginServices.getPluginServices(this);
		XMLEntity xml = ps.getPersistentXML();

		if (xml.contains(DefaultMeasureUnitKey)) {
			cmbUnits.setSelectedUnitIndex(xml.getIntProperty(DefaultMeasureUnitKey));
			CartographicSupportToolkit.DefaultMeasureUnit = xml.getIntProperty(DefaultMeasureUnitKey);
		}else{
			CartographicSupportToolkit.DefaultMeasureUnit = -1; // pixel
		}

		if (xml.contains(DefaultUnitReferenceSystemKey)) {
			cmbReferenceSystem.setSelectedIndex(xml.getIntProperty(DefaultUnitReferenceSystemKey));
			CartographicSupportToolkit.DefaultReferenceSystem = xml.getIntProperty(DefaultUnitReferenceSystemKey);
		}else{
			CartographicSupportToolkit.DefaultReferenceSystem = CartographicSupport.WORLD;
		}

	}

	public void initializeDefaults() {
		CartographicSupportToolkit.DefaultMeasureUnit = -1; // pixel
		CartographicSupportToolkit.DefaultReferenceSystem = CartographicSupport.WORLD;
		initializeValues();
		// pending of a refactoring do not delete (swap commented lines)
//		initializeComponents();
	}

	// pending of a refactoring, following method would be removed
	@Override
	public void storeValues() throws StoreException {
		setPropertiesFromPanel();
		persistPreferences();
	}

	public boolean isValueChanged() {
		return super.hasChanged();
	}

	private void setPropertiesFromPanel(){

		if(cmbReferenceSystem.getSelectedItem()!=null)
			CartographicSupportToolkit.DefaultReferenceSystem = cmbReferenceSystem.getSelectedIndex();
		CartographicSupportToolkit.DefaultMeasureUnit = cmbUnits.getSelectedUnitIndex();
	}

}
