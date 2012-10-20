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

import org.gvsig.fmap.layers.LayerListenerAdapter;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.NetworkUtils;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionListener;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class ClearFlagsExtension extends Extension {

	private LayerCollectionListener clearFlagGraphicsListener = null;
	private MapControl mapCtrl;

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
					if (actionCommand.equalsIgnoreCase("CLEAR_FLAGS")) {
						if (net.getFlags().length > 0) {
							net.removeFlags();
							NetworkUtils.clearFlagsFromGraphics(mapCtrl);
							mapCtrl.drawMap(false);
						}
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
			MapContext map = mapCtrl.getMapContext();

			SingleLayerIterator it = new SingleLayerIterator(map.getLayers());
			while (it.hasNext()) {
				FLayer aux = it.next();
				if (!aux.isActive())
					continue;
				Network net = (Network) aux.getProperty("network");

				if (net != null) {
					if (net.getFlags().length > 0) {
						return true;
					}
				}
			}
		}
		return false;

	}

	public boolean isVisible() {
		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof View) {
			View v = (View) window;
			mapCtrl = v.getMapControl();
			MapContext map = mapCtrl.getMapContext();

			SingleLayerIterator it = new SingleLayerIterator(map.getLayers());
			while (it.hasNext()) {
				FLayer aux = it.next();
				if (!aux.isActive())
					continue;
				Network net = (Network) aux.getProperty("network");

				if (net != null) {
					if (clearFlagGraphicsListener == null) {
						clearFlagGraphicsListener = new LayerListenerAdapter() {
							public void layerRemoved(LayerCollectionEvent e) {
								Network net = (Network) e.getAffectedLayer()
										.getProperty("network");
								if (net != null) {
									for (int i = 0; i < net.getFlagsCount(); i++) {
										NetworkUtils.clearFlagFromGraphics(
												mapCtrl,
												(GvFlag) net.getOriginaFlags()
														.get(i));
									}
									for (int i = 0; i < net.getModifiedCosts()
											.size(); i++) {
										NetworkUtils.clearGraphicByObjectTag(
												mapCtrl, net.getModifiedCosts()
														.get(i));
									}
									for (int i = 0; i < net.getTurnCosts()
											.size(); i++) {
										NetworkUtils.clearGraphicByObjectTag(
												mapCtrl, net.getTurnCosts()
														.get(i));
									}
									NetworkUtils
											.clearRouteFromGraphics(mapCtrl);

								}
							}
						};
						mapCtrl.getMapContext()
								.getLayers()
								.addLayerCollectionListener(
										clearFlagGraphicsListener);
					}
					return true;
				}
			}
		}
		return false;

	}

}
