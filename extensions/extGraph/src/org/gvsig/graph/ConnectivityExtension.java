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

import org.gvsig.graph.core.Network;
import org.gvsig.graph.gui.ConnectivityControlPanel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * @author fjp
 * 
 *         Extension to perform Connectivity calculations. The user can put a
 *         flag and select a point associated layer, and set if he wants to use
 *         a max cost and/or max distance. The algorithm will explore the
 *         network in reverse order or in normal order and the arcs reached will
 *         be selected. If the user selects an associated layer, the points
 *         reached will be selected also. This may be useful in the following
 *         situations: 1.- Searching for connectivity. The unconnected parts of
 *         the network will be unselected. 2.- Selecting points connected to the
 *         network and affected by a cut in the graph 3.- Using reverse order,
 *         you may find the closest valve to close and avoid leaks.
 */
public class ConnectivityExtension extends Extension {

	public void initialize() {
		PluginServices.getIconTheme().registerDefault(
				"connectivity",
				this.getClass().getClassLoader()
						.getResource("images/connectivity.gif"));

	}

	public void execute(String actionCommand) {

		View v = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mapCtrl = v.getMapControl();
		MapContext map = mapCtrl.getMapContext();
		SingleLayerIterator it = new SingleLayerIterator(map.getLayers());
		while (it.hasNext()) {
			FLayer aux = it.next();
			if (!aux.isActive())
				continue;
			Network net = (Network) aux.getProperty("network");

			if (net != null) {
				if (actionCommand.equals("CONNECTIVITY")) {
					ConnectivityControlPanel w = new ConnectivityControlPanel();
					try {
						w.setMapControl(mapCtrl);
						PluginServices.getMDIManager().addWindow(w);
					} catch (ReadDriverException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				return;
			}
		}

	}

	public boolean isEnabled() {
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

}
