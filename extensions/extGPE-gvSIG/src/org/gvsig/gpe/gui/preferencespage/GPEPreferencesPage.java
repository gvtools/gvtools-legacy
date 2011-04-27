package org.gvsig.gpe.gui.preferencespage;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.iver.andami.PluginServices;
import com.iver.andami.preferences.AbstractPreferencePage;
import com.iver.andami.preferences.StoreException;

/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * $Id$
 * $Log$
 *
 */
/**
 * Preferences page for the GPE layers
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class GPEPreferencesPage extends AbstractPreferencePage {
	private static final long serialVersionUID = -4472846749161169021L;
	private ArrayList defaults = null;
	private ImageIcon icon = null;
	
	public GPEPreferencesPage() {
		super();
		initialize();
	}

	/**
	 * Creates and initializes the panel
	 */
	private void initialize() {
		icon = new ImageIcon(this.getClass().getClassLoader().getResource("images/gpe.png"));
		setLayout(new java.awt.GridBagLayout());
		//The XML panel
		GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(getXMLPanel(),gridBagConstraints);
		//The XML SCHEMA panel
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(getXMLSchemaPanel(),gridBagConstraints);
		//The Coordinates panel
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(getCoordinatesPanel(),gridBagConstraints);
		//Others panel
		Component othersPanel = getOthersPanel();
		if (othersPanel != null){
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			add(getOthersPanel(),gridBagConstraints);
		}
	}

	/**
	 * @return the default properties that have their own panel
	 * to set them. 
	 */
	private ArrayList getDefaults(){
		if (defaults == null){
			defaults = new ArrayList();
//			defaults.add(GPEDefaults.DECIMAL);
//			defaults.add(GPEDefaults.COORDINATES_SEPARATOR);
//			defaults.add(GPEDefaults.TUPLES_SEPARATOR);
//			defaults.add(GPEDefaults.NAMESPACE_PREFIX);
//			defaults.add(GPEDefaults.NAMESPACE_URI);
//			defaults.add(GPEDefaults.XML_VERSION);
//			defaults.add(GPEDefaults.XML_ENCODING);
//			defaults.add(GPEDefaults.DEFAULT_FILE_NAME);
//			defaults.add(GPEDefaults.XSD_SCHEMA_FILE);		
//			defaults.add(GPEDefaults.DECIMAL_DIGITS);
//			defaults.add(GPEDefaults.DEFAULT_BLANC_SPACE);			
		}
		return defaults;	
	}

	/**
	 * @return a panel that contains all the properties related
	 * with the coordinates
	 */
	private Component getCoordinatesPanel(){
		//PropertiesComponent component = new PropertiesComponent();
		Component component = new JPanel();
//		component.addValue(PluginServices.getText(this, "gpe_" + GPEDefaults.DECIMAL),
//				GPEDefaults.DECIMAL,
//				GPEDefaults.getProperty(GPEDefaults.DECIMAL), 
//				null);
//		component.addValue(PluginServices.getText(this, "gpe_" + GPEDefaults.COORDINATES_SEPARATOR),
//				GPEDefaults.COORDINATES_SEPARATOR,
//				GPEDefaults.getProperty(GPEDefaults.COORDINATES_SEPARATOR),
//				null);
//		component.addValue(PluginServices.getText(this, "gpe_" + GPEDefaults.TUPLES_SEPARATOR), 
//				GPEDefaults.TUPLES_SEPARATOR,
//				GPEDefaults.getProperty(GPEDefaults.TUPLES_SEPARATOR),
//				null);
//		component.addValue(PluginServices.getText(this, "gpe_" + GPEDefaults.DECIMAL_DIGITS),
//				GPEDefaults.DECIMAL_DIGITS,
//				GPEDefaults.getProperty(GPEDefaults.DECIMAL_DIGITS), 
//				null);
//		component.setBorder(createBorderPanel(PluginServices.getText(this, "gpe_preferences_window_coordinates")));
		return component;
	}

	/**
	 * @return a panel that contains all the properties related
	 * with XML
	 */
	private Component getXMLPanel(){
//		PropertiesComponent component = new PropertiesComponent();
//		component.addValue(PluginServices.getText(this, "gpe_" + GPEDefaults.XML_VERSION),
//				GPEDefaults.XML_VERSION,
//				GPEDefaults.getProperty(GPEDefaults.XML_VERSION),
//				null);
//		component.addValue(PluginServices.getText(this, "gpe_" + GPEDefaults.XML_ENCODING),
//				GPEDefaults.XML_ENCODING,
//				GPEDefaults.getProperty(GPEDefaults.XML_ENCODING), 
//				null);
//		component.addValue(PluginServices.getText(this, "gpe_" + GPEDefaults.DEFAULT_BLANC_SPACE),
//				GPEDefaults.DEFAULT_BLANC_SPACE,
//				GPEDefaults.getProperty(GPEDefaults.DEFAULT_BLANC_SPACE), 
//				null);
//		component.setBorder(createBorderPanel(PluginServices.getText(this, "gpe_preferences_window_xml")));
		Component component = new JPanel();
		return component;
	}

	/**
	 * @return a panel that contains all the properties related
	 * with the XML Schema
	 */
	private Component getXMLSchemaPanel(){
//		PropertiesComponent component = new PropertiesComponent();
//		component.addValue(PluginServices.getText(this, "gpe_" + GPEDefaults.NAMESPACE_PREFIX),
//				GPEDefaults.NAMESPACE_PREFIX,
//				GPEDefaults.getProperty(GPEDefaults.NAMESPACE_PREFIX),
//				null);
//		component.addValue(PluginServices.getText(this, "gpe_" + GPEDefaults.NAMESPACE_URI),
//				GPEDefaults.NAMESPACE_URI,
//				GPEDefaults.getProperty(GPEDefaults.NAMESPACE_URI),
//				null);
//		component.addValue(PluginServices.getText(this, "gpe_" + GPEDefaults.XSD_SCHEMA_FILE),
//				GPEDefaults.XSD_SCHEMA_FILE,
//				GPEDefaults.getProperty(GPEDefaults.XSD_SCHEMA_FILE),
//				null);
//		component.setBorder(createBorderPanel(PluginServices.getText(this, "gpe_preferences_window_xmlschema")));
		Component component = new JPanel();
		return component;
	}

	/**
	 * @return a panel that contains all the properties that
	 * have not been join in the other groups
	 */
	private Component getOthersPanel(){
//		PropertiesComponent component = new PropertiesComponent();
//		Iterator it = GPEDefaults.getKeys();
//		int i=0;
//		while (it.hasNext()){
//			String key = (String)it.next();
//			if (!(getDefaults().contains(key))){
//				Object value = GPEDefaults.getProperty(key);
//				component.addValue(PluginServices.getText(this, key), key, value, null);
//				i++;
//			}
//		}
//		component.setBorder(createBorderPanel(PluginServices.getText(this, "gpe_preferences_window_other")));
//		if (i==0){
//			return null;
//		}
		Component component = new JPanel();
		return component;		
	}

	/**
	 * Cretaes a border for a JPanel
	 * @param title
	 * The title for the panel
	 */
	private Border createBorderPanel(String title){
		TitledBorder border = new TitledBorder(title);
		return border;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.preferences.AbstractPreferencePage#setChangesApplied()
	 */
	public void setChangesApplied() {
		// TODO Auto-generated method stub		
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.preferences.AbstractPreferencePage#storeValues()
	 */
	public void storeValues() throws StoreException {
		// TODO Auto-generated method stub		
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.preferences.IPreference#getID()
	 */
	public String getID() {
		return getClass().getName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.preferences.IPreference#getIcon()
	 */
	public ImageIcon getIcon() {
		return icon;		
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.preferences.IPreference#getPanel()
	 */
	public JPanel getPanel() {
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.preferences.IPreference#getTitle()
	 */
	public String getTitle() {
		return PluginServices.getText(this, "gpe_preferences_window");
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.preferences.IPreference#initializeDefaults()
	 */
	public void initializeDefaults() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.preferences.IPreference#initializeValues()
	 */
	public void initializeValues() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.preferences.IPreference#isValueChanged()
	 */
	public boolean isValueChanged() {
		// TODO Auto-generated method stub
		return false;
	}

}
