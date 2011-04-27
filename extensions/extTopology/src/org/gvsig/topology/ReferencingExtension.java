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

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.referencing.VectorialReferencingPanel;

/**
 * Extension to reference vectorial layers.
 * 
 * @author Alvaro Zabala
 * 
 */
public class ReferencingExtension extends Extension {

	public void execute(String actionCommand) {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices
				.getMDIManager().getActiveWindow();
		View vista = (View) f;
		if (f != null) {
			MapControl mapControl = vista.getMapControl();
			if(mapControl != null){
				VectorialReferencingPanel refPanel = new VectorialReferencingPanel(mapControl.getMapContext().getLayers());
				PluginServices.getMDIManager().addWindow(refPanel);
			}// if
		}
	}

	public void initialize() {
		PluginServices.getIconTheme().registerDefault(
				"vectorial-adjust",
				this.getClass().getClassLoader().getResource(
						"images/vectorial-adjust.gif"));

		PluginServices.getIconTheme().registerDefault(
				"vectorial-adjust",
				this.getClass().getClassLoader().getResource(
						"images/vectorial-adjust.gif"));
	}

	public boolean isEnabled() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices
				.getMDIManager().getActiveWindow();
		if (f == null) {
			return false;
		}
		if (f instanceof View) {
			View vista = (View) f;
			MapContext mapContext = vista.getModel().getMapContext();
			FLayers layers = mapContext.getLayers();
			int numLayers = layers.getLayersCount();
			for (int i = 0; i < numLayers; i++) {
				FLayer layer = layers.getLayer(i);
				if (layer instanceof FLyrVect && layer.isAvailable()
						&& layer.isActive()) {
					return true;

				}// if
			}// for
		}// if
		return false;
	}

	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices
				.getMDIManager().getActiveWindow();
		if (f == null) {
			return false;
		}
		if (f instanceof View) {
			View vista = (View) f;
			IProjectView model = vista.getModel();
			FLayers layers = model.getMapContext().getLayers();
			int numLayers = layers.getLayersCount();
			for (int i = 0; i < numLayers; i++) {
				FLayer layer = layers.getLayer(i);
				if (layer instanceof FLyrVect)
					return true;
			}
			return false;
		}
		return false;
	}

}
