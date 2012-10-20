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
package org.gvsig.graph;

import java.util.List;

import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.NetworkUtils;
import org.gvsig.graph.gui.RouteReportPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.GraphicLayer;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.fmap.rendering.FGraphic;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.util.GvSession;

public class ClearRouteExtension extends Extension {

	public void initialize() {
		// TODO Auto-generated method stub

	}

	public void execute(String actionCommand) {
		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof View) {
			View v = (View) window;
			MapControl mapCtrl = v.getMapControl();
			MapContext map = mapCtrl.getMapContext();

			SingleLayerIterator it = new SingleLayerIterator(map.getLayers());
			while (it.hasNext()) {
				FLayer aux = it.next();
				if (!aux.isActive())
					continue;
				Network net = (Network) aux.getProperty("network");

				if (net != null) {
					if (actionCommand.equalsIgnoreCase("CLEAR_ROUTES")) {
						NetworkUtils.clearRouteFromGraphics(mapCtrl);
						GvSession.getInstance().delete(mapCtrl, "Route");
						List reportPanels = (List) GvSession.getInstance().get(
								mapCtrl, "RouteReport");
						if (reportPanels != null) {
							for (int i = 0; i < reportPanels.size(); i++) {
								PluginServices.getMDIManager().closeWindow(
										(RouteReportPanel) reportPanels.get(i));
							}
						}

						GvSession.getInstance().delete(mapCtrl, "RouteReport");
						mapCtrl.drawMap(false);
					}

				}
			}
		}

	}

	public boolean isEnabled() {
		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof View) {
			View v = (View) window;
			MapControl mapCtrl = v.getMapControl();
			GraphicLayer grafLayer = mapCtrl.getMapContext().getGraphicsLayer();
			for (int i = 0; i < grafLayer.getNumGraphics(); i++) {
				FGraphic graf = grafLayer.getGraphic(i);
				if (graf.getTag() != null)
					if (graf.getTag().compareTo("ROUTE") == 0)
						return true;
			}
		}
		return false;

	}

	public boolean isVisible() {
		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof View) {
			View v = (View) window;
			MapControl mapCtrl = v.getMapControl();
			MapContext map = mapCtrl.getMapContext();

			SingleLayerIterator it = new SingleLayerIterator(map.getLayers());
			while (it.hasNext()) {
				FLayer aux = it.next();
				if (!aux.isActive())
					continue;
				Network net = (Network) aux.getProperty("network");

				if (net != null) {
					return true;
				}
			}
		}
		return false;

	}

}
