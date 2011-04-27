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
import java.util.Map;

import org.gvsig.topology.project.documents.view.toc.actions.TopologyPropertiesTocMenuEntry;
import org.gvsig.topology.ui.util.GUIUtil;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.LayersIterator;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;
import com.iver.utiles.swing.threads.TopologyValidationTask;

/**
 * Commands for a topology selected in the TOC.
 * 
 * @author Alvaro Zabala
 *
 */
public class TopologyCommandsExtension extends Extension {

	public void execute(String actionCommand) {
		
		com.iver.andami.ui.mdiManager.IWindow f = 
			PluginServices.getMDIManager().getActiveWindow();
		View vista = (View) f;
		IProjectView model = vista.getModel();
		final MapContext mapContext = model.getMapContext();
		
		if(actionCommand.equalsIgnoreCase("SAVE_TOPOLOGY")){
			LayersIterator it = new LayersIterator(mapContext.getLayers());
			while (it.hasNext())
			{
				FLayer aux = (FLayer) it.next();
				if (!aux.isActive())
					continue;
				if(aux instanceof Topology)
				{
					Topology topology = (Topology) aux;
					String selectedFile = GUIUtil.getInstance().selectFile("xml",
							PluginServices.getText(this, "Ficheros_XML"), false);
					if(! selectedFile.endsWith(".xml"))
						selectedFile += ".xml";
					Map<String, Object> storageParams = new HashMap<String, Object>();
					storageParams.put(TopologyPersister.FILE_PARAM_NAME, selectedFile);
					//TODO Hacer la carga de la topologia en un ITask para no dejar la GUI
					//congelada. Además, chequear que las capas que se referencian existen,
					//y si no están en el TOC se cargan
					TopologyPersister.persist(topology, storageParams);
					return;
				}
			}//while
		}else if(actionCommand.equalsIgnoreCase("EVALUATE_TOPOLOGY")){
			
			LayersIterator it = new LayersIterator(mapContext.getLayers());
			while (it.hasNext())
			{
				FLayer aux = (FLayer) it.next();
				if (!aux.isActive())
					continue;

				if(aux instanceof Topology)
				{
					Topology topology = (Topology) aux;
					PluginServices.cancelableBackgroundExecution(new TopologyValidationTask(topology, mapContext));
				}
			}//while
		}

	}
	
	public void initialize() {
		PluginServices.getIconTheme().registerDefault(
				"save-topology",
				this.getClass().getClassLoader().getResource("images/save-topology.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
				"evaluate-topology",
				this.getClass().getClassLoader().getResource("images/evaluate-topology2.png")
		);
		
		ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
    	extensionPoints.add("View_TocActions","TopologyProperties",new TopologyPropertiesTocMenuEntry());
	}

	public boolean isEnabled() {
		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof View)
		{
			View v = (View) window;
	        MapControl mapCtrl = v.getMapControl();
			MapContext map = mapCtrl.getMapContext();
			
			LayersIterator it = new LayersIterator(map.getLayers());
			while (it.hasNext())
			{
				FLayer aux = (FLayer) it.next();
				if(! aux.isActive())
					continue;
				if(aux instanceof Topology)
					return true;
			}//while
		}//if
		return false;
	}

	public boolean isVisible() {
		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof View)
		{
			View v = (View) window;
	        MapControl mapCtrl = v.getMapControl();
			MapContext map = mapCtrl.getMapContext();
			
			LayersIterator it = new LayersIterator(map.getLayers());
			while (it.hasNext())
			{
				FLayer aux = (FLayer) it.next();
				if(aux instanceof Topology)
					return true;
			}//while
		}//if
		return false;
	}

}
