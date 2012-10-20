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

import java.awt.Color;
import java.text.NumberFormat;

import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.GvNode;
import org.gvsig.graph.core.IGraph;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.gui.ServiceAreaControlPanel;
import org.gvsig.graph.solvers.EdgesMemoryDriver;
import org.gvsig.graph.solvers.OneToManySolver;
import org.gvsig.graph.solvers.ServiceAreaExtractor2;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.core.v02.FLabel;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.GraphicLayer;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.fmap.rendering.FGraphic;
import com.iver.cit.gvsig.fmap.rendering.FGraphicLabel;
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
public class ServiceAreaExtension extends Extension {

	private int idSymbolLine = -1;

	public void initialize() {
		PluginServices.getIconTheme().registerDefault(
				"service_area",
				this.getClass().getClassLoader()
						.getResource("images/service_area.png"));

		PluginServices.getIconTheme().registerDefault(
				"service_area_wrong_costs",
				this.getClass().getClassLoader()
						.getResource("images/service_area_wrong_costs.png"));

		PluginServices.getIconTheme().registerDefault(
				"service_area_compact",
				this.getClass().getClassLoader()
						.getResource("images/service_area_compact.png"));

		PluginServices.getIconTheme().registerDefault(
				"service_area_convex",
				this.getClass().getClassLoader()
						.getResource("images/service_area_convex.png"));

		PluginServices.getIconTheme().registerDefault(
				"service_area_fusion",
				this.getClass().getClassLoader()
						.getResource("images/service_area_fusion.png"));

		PluginServices.getIconTheme().registerDefault(
				"service_area_non_fusion",
				this.getClass().getClassLoader()
						.getResource("images/service_area_non_fusion.png"));

		PluginServices.getIconTheme().registerDefault(
				"service_area_disks",
				this.getClass().getClassLoader()
						.getResource("images/service_area_disks.png"));

		PluginServices.getIconTheme().registerDefault(
				"service_area_rings",
				this.getClass().getClassLoader()
						.getResource("images/service_area_rings.png"));
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
				// if (flags.length == 0) {
				// JOptionPane.showMessageDialog(null,
				// "Primero carga las paradas.");
				// return;
				// }
				// setVelocities(net);
				try {
					// OneToManySolver solver = new OneToManySolver();
					// solver.setNetwork(net);
					// solver.putDestinationsOnNetwork(net.getFlags());
					// if (actionCommand.equals("LABEL_NODE_DISTANCES")) {
					// calculateLabels(mapCtrl, map, net, flags, solver);
					// }
					if (actionCommand.equals("SERVICE_AREA")) {
						calculateServiceArea(mapCtrl, net, flags);
					}
					// if(actionCommand.equals("PRUEBA_WIZARD_SERVICE_AREA")){
					// ImageIcon icon = new
					// ImageIcon(this.getClass().getClassLoader()
					// .getResource("images/service_area-wizard-logo.jpg"));
					// ServiceAreaWizard wiz=new ServiceAreaWizard(icon, null);
					// PluginServices.getMDIManager().addWindow(wiz);
					// }
					// solver.removeDestinationsFromNetwork(net.getFlags());
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
	private void calculateLabels(MapControl mapCtrl, MapContext map,
			Network net, GvFlag[] flags, OneToManySolver solver)
			throws GraphException {
		GraphicLayer graphicLayer = mapCtrl.getMapContext().getGraphicsLayer();
		removeOldLabels(graphicLayer);
		for (int i = 0; i < flags.length; i++) {

			solver.setSourceFlag(flags[i]);
			long t1 = System.currentTimeMillis();
			solver.setExploreAllNetwork(true);
			solver.calculate();
			long t2 = System.currentTimeMillis();
			System.out.println("Punto " + i + " de " + flags.length + ". "
					+ (t2 - t1) + " msecs.");
			// Después de esto, los nodos de la red están
			// etiquetados con los costes al nodo orígen
			EdgesMemoryDriver driver = new EdgesMemoryDriver(net);
			FLayer lyr = LayerFactory.createLayer("Edges", driver, null);
			map.getLayers().addLayer(lyr);
			// doLabeling(mapCtrl, net, flags[i]);

		}

	}

	private void calculateServiceArea(MapControl mapCtrl, Network net,
			GvFlag[] flags) throws BaseException {
		ServiceAreaExtractor2 extractor = new ServiceAreaExtractor2(net);

		ServiceAreaControlPanel controlPanel = new ServiceAreaControlPanel(net);
		controlPanel.setMapControl(mapCtrl, net);
		controlPanel = (ServiceAreaControlPanel) PluginServices.getMDIManager()
				.addWindow(controlPanel);

	}

	private FSymbol getTextSymbol() {
		FSymbol theSymbol = new FSymbol(FConstant.SYMBOL_TYPE_TEXT);
		theSymbol.setColor(Color.RED);
		theSymbol.setStyle(FConstant.SYMBOL_STYLE_MARKER_CIRCLE);
		theSymbol.setFontColor(Color.BLACK);
		theSymbol.setSizeInPixels(true);
		theSymbol.setSize(9);
		return theSymbol;
	}

	private void removeOldLabels(GraphicLayer gLyr) {
		for (int i = gLyr.getNumGraphics() - 1; i >= 0; i--) {
			FGraphic gr = gLyr.getGraphic(i);
			if (gr.equals("N"))
				gLyr.removeGraphic(i);

		}
	}

	private void doLabeling(MapControl mapControl, Network net, GvFlag flag) {
		GraphicLayer graphicLayer = mapControl.getMapContext()
				.getGraphicsLayer();
		IGraph g = net.getGraph();
		int idSymbol = graphicLayer.addSymbol(getTextSymbol());
		String tag = "N";
		for (int i = 0; i < g.numVertices(); i++) {
			GvNode node = g.getNodeByID(i);
			IGeometry geom = ShapeFactory.createPoint2D(node.getX(),
					node.getY());
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(1);
			String aux = "\u221E"; // infinito
			if (node.getBestCost() < Double.MAX_VALUE)
				aux = nf.format(node.getBestCost()) + " - "
						+ nf.format(node.getAccumulatedLength());
			FGraphicLabel theGLabel = new FGraphicLabel(geom, idSymbol, aux);
			theGLabel.setObjectTag(tag);
			theGLabel.getLabel().setJustification(FLabel.CENTER_TOP);
			graphicLayer.addGraphic(theGLabel);
		}
		mapControl.drawGraphics();

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
					// if (net.getFlags().length > 0)
					// {
					return true;
					// }
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
