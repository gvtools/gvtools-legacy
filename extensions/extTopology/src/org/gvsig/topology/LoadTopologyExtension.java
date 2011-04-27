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
package org.gvsig.topology;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gvsig.topology.ui.preferencespage.TopologyPreferences;
import org.gvsig.topology.ui.util.GUIUtil;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.preferences.IPreference;
import com.iver.andami.preferences.IPreferenceExtension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * ANDAMI's extension to load a topology from an XML file.
 * 
 * @author Alvaro Zabala
 *
 */
public class LoadTopologyExtension extends Extension implements IPreferenceExtension{

	TopologyPreferences topologyPreferences = new TopologyPreferences();
	
	public void execute(String actionCommand) {
		if(actionCommand.equalsIgnoreCase("LOAD_TOPOLOGY")){
			com.iver.andami.ui.mdiManager.IWindow f = 
				PluginServices.getMDIManager().getActiveWindow();
			View vista = (View) f;
			IProjectView model = vista.getModel();
			MapContext mapContext = model.getMapContext();
			String selectedFile = GUIUtil.getInstance().selectFile("xml", 
					PluginServices.getText(this, "Ficheros_XML"), true);
			if(selectedFile == null || selectedFile.equals("")){
				GUIUtil.getInstance().
				messageBox("Message_not_selected_topology_file", "Message_error_loading_topology");
				return;
			}
			Map<String, Object> storageParams = new HashMap<String, Object>();
			storageParams.put(TopologyPersister.FILE_PARAM_NAME, selectedFile);
			//TODO Hacer la carga de la topologia en un ITask para no dejar la GUI
			//congelada. Además, chequear que las capas que se referencian existen,
			//y si no están en el TOC se cargan
			Topology newTopology = TopologyPersister.load(mapContext, storageParams);
			List newTopoLyrs = newTopology.getLayers();
			GUIUtil.getInstance().addTopologyToTOC(mapContext, newTopoLyrs, newTopology);
		}
	}

	public void initialize() {
		PluginServices.getIconTheme().registerDefault(
				"load-topology",
				this.getClass().getClassLoader().getResource("images/load-topology.png")
		);
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		IWindow f = PluginServices.getMDIManager().getActiveWindow();
		if (f == null) {
		    return false;
		}
		if (f instanceof View) {
		    return true;
		}
		return false;
	}

	public IPreference[] getPreferencesPages() {
		IPreference[] preferences=new IPreference[1];
		preferences[0] = topologyPreferences;
		return preferences;
	}

}
