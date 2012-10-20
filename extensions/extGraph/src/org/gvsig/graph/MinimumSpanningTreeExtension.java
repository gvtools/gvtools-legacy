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

import java.awt.Component;

import javax.swing.JOptionPane;

import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.solvers.MinimumSpanningTreeExtractor;
import org.gvsig.graph.solvers.OneToManySolver;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * @author fjp
 * 
 *         Extension to perform ServiceArea calculations. Here you will find
 *         code to: 1.- See the distances to every node on the network to one or
 *         many point sources. 2.- TODO: Calculate a polyline layer with costs
 *         and length calculated to nearest source point. 3.- TODO: Calculate
 *         polygons covering those service areas.
 */
public class MinimumSpanningTreeExtension extends Extension {

	private int idSymbolLine = -1;

	public void initialize() {
		PluginServices.getIconTheme().registerDefault("mst",
				this.getClass().getClassLoader().getResource("images/mst.png"));

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
				GvFlag[] flags = net.getFlags();
				if (flags.length == 0) {
					JOptionPane.showMessageDialog(null,
							"Primero carga las paradas.");
					return;
				}
				// setVelocities(net);
				try {
					OneToManySolver solver = new OneToManySolver();
					solver.setNetwork(net);
					solver.putDestinationsOnNetwork(net.getFlags());
					if (actionCommand.equals("MST")) {
						calculateMST(map, net, flags, solver);
					}
					solver.removeDestinationsFromNetwork(net.getFlags());
				} catch (BaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return;
			}
		}

	}

	/**
	 * @param mapCtrl
	 * @param map
	 * @param net
	 * @param flags
	 * @param solver
	 * @return
	 * @throws GraphException
	 */
	private void calculateMST(MapContext map, Network net, GvFlag[] flags,
			OneToManySolver solver) throws BaseException {
		MinimumSpanningTreeExtractor extractor = new MinimumSpanningTreeExtractor(
				net);
		String aux = JOptionPane.showInputDialog(PluginServices.getText(this,
				"Please_enter_max_cost_MST") + ":");
		if (aux == null)
			return;
		double cost = 0;
		try {
			cost = Double.parseDouble(aux);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog((Component) PluginServices
					.getMDIManager().getActiveWindow(), PluginServices.getText(
					null, "Please_enter_a_valid_number"));
			return;
		}
		solver.addListener(extractor);
		for (int i = 0; i < flags.length; i++) {
			solver.setSourceFlag(flags[i]);
			long t1 = System.currentTimeMillis();
			solver.setExploreAllNetwork(true);
			solver.setMaxCost(cost);
			extractor.setIdFlag(i);
			solver.calculate();
			long t2 = System.currentTimeMillis();
			System.out.println("Punto " + i + " de " + flags.length + ". "
					+ (t2 - t1) + " msecs.");
		}
		extractor.endExtraction();

		FLyrVect lyrLine = extractor.getLineLayer();
		lyrLine.setCrs(map.getCrs());
		map.beginAtomicEvent();
		map.getLayers().addLayer(lyrLine);
		map.endAtomicEvent();

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
