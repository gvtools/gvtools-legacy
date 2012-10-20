/*
 * Created on 10-abr-2006
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
 * $Id:
 * $Log:
 */
package org.gvsig.topology.ui.preferencespage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.gvsig.jts.JtsUtil;
import org.gvsig.topology.ui.util.GUIUtil;

import com.iver.andami.PluginServices;
import com.iver.andami.preferences.AbstractPreferencePage;
import com.iver.andami.preferences.StoreException;
import com.iver.utiles.XMLEntity;

/**
 * Preferences page of the topology extension.
 * 
 * @author Alvaro Zabala
 * 
 */
public class TopologyPreferences extends AbstractPreferencePage {

	private static final long serialVersionUID = 1L;

	private static String id = TopologyPreferences.class.getName();

	private JTextField topologyFilesPath;

	private JSpinner generalizationFactor;

	private ImageIcon icon = null;

	public static final String DEFAULT_TOPOLOGY_FILES_PATH = "TopologyFilesPath";
	public static final String SMOOTH_FACTOR = "GeneralizationFactor";

	public TopologyPreferences() {

		addComponent(PluginServices.getText(this, "topology_files_path"),
				topologyFilesPath = new JTextField(25));

		SpinnerNumberModel model = new SpinnerNumberModel(1d, 0d, 100d, 0.1);
		addComponent(PluginServices.getText(this, "generalization_factor"),
				generalizationFactor = new JSpinner(model));
	}

	public void setChangesApplied() {
		setChanged(false);
	}

	public void storeValues() throws StoreException {
		String filesDirectory = topologyFilesPath.getText();
		GUIUtil.getInstance().setFilesDirectory(filesDirectory);

		Double smoothFactor = (Double) generalizationFactor.getValue();
		JtsUtil.GENERALIZATION_FACTOR = smoothFactor;

		PluginServices ps = PluginServices.getPluginServices(this);
		XMLEntity xml = ps.getPersistentXML();
		xml.putProperty(DEFAULT_TOPOLOGY_FILES_PATH, filesDirectory);
		xml.putProperty(SMOOTH_FACTOR, smoothFactor);
	}

	public String getID() {
		return id;
	}

	public ImageIcon getIcon() {
		if (icon == null) {
			icon = PluginServices.getIconTheme().get("create-topology");
		}
		return icon;
	}

	public JPanel getPanel() {
		return this;
	}

	public String getTitle() {
		return PluginServices.getText(this, "TopologyPreferencesPage");
	}

	public void initializeDefaults() {
		topologyFilesPath.setText(GUIUtil.DEFAULT_TOPO_FILES_DIRECTORY);
		generalizationFactor.setValue(JtsUtil.GENERALIZATION_FACTOR);
	}

	public void initializeValues() {
		PluginServices ps = PluginServices.getPluginServices(this);
		XMLEntity xml = ps.getPersistentXML();
		if (xml.contains(DEFAULT_TOPOLOGY_FILES_PATH)) {
			String path = xml.getStringProperty(DEFAULT_TOPOLOGY_FILES_PATH);
			topologyFilesPath.setText(path);
			GUIUtil.getInstance().setFilesDirectory(path);
		} else {
			topologyFilesPath.setText(GUIUtil.DEFAULT_TOPO_FILES_DIRECTORY);
		}

		if (xml.contains(SMOOTH_FACTOR)) {
			Double factor = xml.getDoubleProperty(SMOOTH_FACTOR);
			JtsUtil.GENERALIZATION_FACTOR = factor;
			generalizationFactor.setValue(factor);
		} else {
			generalizationFactor.setValue(JtsUtil.GENERALIZATION_FACTOR);
		}
	}

	public boolean isValueChanged() {
		return super.hasChanged();
	}

}
