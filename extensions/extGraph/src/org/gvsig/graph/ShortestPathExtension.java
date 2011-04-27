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

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.NetworkUtils;
import org.gvsig.graph.gui.RouteControlPanel;
import org.gvsig.graph.gui.RouteReportPanel;
import org.gvsig.graph.solvers.OneToManySolver;
import org.gvsig.graph.solvers.Route;
import org.gvsig.graph.solvers.ShortestPathSolverAStar;
import org.gvsig.graph.solvers.TspSolverAnnealing;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.styles.ArrowDecoratorStyle;
import com.iver.cit.gvsig.fmap.core.styles.ILineStyle;
import com.iver.cit.gvsig.fmap.core.styles.SimpleLineStyle;
import com.iver.cit.gvsig.fmap.core.symbols.ArrowMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleLineSymbol;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.GraphicLayer;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.fmap.rendering.FGraphic;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.util.GvSession;
import com.iver.utiles.Utils;

public class ShortestPathExtension extends Extension {

//	public static ShortestPathSolverAStar solver = new ShortestPathSolverAStar();
	private int idSymbolLine = -1;

	public void initialize() {
		PluginServices.getIconTheme().registerDefault(
				"shortest_path",
				this.getClass().getClassLoader().getResource("images/shortest_path.png")
			);		
	}

	public void execute(String actionCommand) {
		View v = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mapCtrl = v.getMapControl();
		MapContext map = mapCtrl.getMapContext();
		SingleLayerIterator it = new SingleLayerIterator(map.getLayers());
		while (it.hasNext())
		{
			FLayer aux = it.next();
			if (!aux.isActive())
				continue;
			Network net = (Network) aux.getProperty("network");

			if ( net != null)
			{
				Route route;
				try {
					RouteControlPanel controlPanel = (RouteControlPanel) GvSession.getInstance().get(mapCtrl, "RouteControlPanel");
					if (controlPanel != null) {
						boolean returnToOrigin = controlPanel.isReturnToOriginSelected(); 
						if (returnToOrigin) {
							net.addFlag(net.getFlags()[0]);
						}
						if (controlPanel.isTspSelected()) {
							OneToManySolver odMatrixSolver = new OneToManySolver();
							odMatrixSolver.setNetwork(net);
							odMatrixSolver.putDestinationsOnNetwork(net.getFlags());
							
							GvFlag[] flags = net.getFlags();
							
							double[][] odMatrix = new double[flags.length][flags.length];
							
							for (int i=0; i < flags.length; i++)
							{
								
								odMatrixSolver.setSourceFlag(flags[i]);
								long t1 = System.currentTimeMillis();
								
								odMatrixSolver.calculate();
								long t2 = System.currentTimeMillis();
								System.out.println("Punto " + i + " de " + flags.length + ". " + (t2-t1) + " msecs.");
								
								for (int j=0; j < flags.length; j++)
								{
									long secs = Math.round(flags[j].getCost());
	//								long meters = Math.round(flags[j].getAccumulatedLength());
	//								String strAux = i + "\t" + j + "\t" + secs + "\t" + meters;
									odMatrix[i][j] = flags[j].getCost();
								}
								
							}
							
							odMatrixSolver.removeDestinationsFromNetwork(net.getFlags());
	
							
							TspSolverAnnealing solverTsp = new TspSolverAnnealing();
							solverTsp.setReturnToOrigin(returnToOrigin);
							solverTsp.setStops(net.getFlags());
							solverTsp.setODMatrix(odMatrix);
							GvFlag[] orderedFlags = solverTsp.calculate();
							ArrayList<GvFlag> orderedArray = new ArrayList<GvFlag>(orderedFlags.length);
							Collections.addAll(orderedArray, orderedFlags);
							net.setFlags(orderedArray);
							// TODO: Si ya existe el flag0, no añadirlo al final.
							
						}
					}
					route = calculateRoute(net);
					if (route == null)
						return;
					
					List routes = (List) GvSession.getInstance().get(mapCtrl, "Route");
					if(routes == null){
						routes = new ArrayList();
						GvSession.getInstance().put(mapCtrl, "Route", routes);
					}
					
					if (route.getFeatureList().size() == 0) 
						return;
					
					routes.add(route);
					
					
					createGraphicsFrom(route.getFeatureList(), v.getMapControl());
					RouteReportPanel routeReport = new RouteReportPanel(route, v.getMapControl());
					PluginServices.getMDIManager().addWindow(routeReport);
					List reportsPanels = (List) GvSession.getInstance().get(mapCtrl, "RouteReport");
					if(reportsPanels == null){
						reportsPanels = new ArrayList();
						GvSession.getInstance().put(mapCtrl, "RouteReport", reportsPanels);
					}	
					reportsPanels.add(routeReport);
					
					if (controlPanel != null)
						controlPanel.refresh();
					
				} catch (GraphException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}



	}

	private Route calculateRoute(Network net) throws GraphException {
		Route route = null;
		ShortestPathSolverAStar solver = new ShortestPathSolverAStar();
		solver.setNetwork(net);
//					solver.setFielStreetName("STREET_NAM");
		String fieldStreetName = (String) net.getLayer().getProperty("network_fieldStreetName");
		solver.setFielStreetName(fieldStreetName);
		route = solver.calculateRoute();
		if (route.getFeatureList().size() == 0)
		{
			JOptionPane.showMessageDialog((JComponent) PluginServices.getMDIManager().getActiveWindow(),
					PluginServices.getText(this, "shortest_path_not_found"));
		}
		return route;
	}

	private void createGraphicsFrom(Collection featureList, MapControl mapControl) {
		Iterator it = featureList.iterator();
		GraphicLayer graphicLayer = mapControl.getMapContext().getGraphicsLayer();
//		if (idSymbolLine == -1)
		{
			SimpleLineSymbol arrowSymbol = new SimpleLineSymbol();
			// FSymbol arrowSymbol = new FSymbol(FConstant.SYMBOL_TYPE_LINE);
			arrowSymbol.setLineWidth(3.0f);
			ILineStyle lineStyle = new SimpleLineStyle();
			
			ArrowDecoratorStyle arrowDecoratorStyle = new ArrowDecoratorStyle();
			ArrowMarkerSymbol marker = (ArrowMarkerSymbol) arrowDecoratorStyle.getMarker(); 
			marker.setSize(16);
			marker.setColor(Color.RED);
			arrowDecoratorStyle.setArrowMarkerCount(1);
			lineStyle.setArrowDecorator(arrowDecoratorStyle );
			lineStyle.setLineWidth(3.0f);
			arrowSymbol.setLineColor(Color.RED);
			arrowSymbol.setAlpha(120);
			arrowSymbol.setLineStyle(lineStyle);
			idSymbolLine = graphicLayer.addSymbol(arrowSymbol);
			
		}
		// Para evitar hacer reallocate de los elementos de la
		// graphicList cada vez, creamos primero la lista
		// y la insertamos toda de una vez.
		ArrayList graphicsRoute = new ArrayList();
		while (it.hasNext()) {
			IFeature feat = (IFeature) it.next();
			IGeometry gAux = feat.getGeometry();
			FGraphic graphic = new FGraphic(gAux, idSymbolLine);
			graphic.setTag("ROUTE");
			graphicsRoute.add(graphic);
//			graphicLayer.insertGraphic(0, graphic);
		}
		// Lo insertamos al principio de la lista para que los
		// pushpins se dibujen después.

		graphicLayer.inserGraphics(0, graphicsRoute);
		mapControl.drawGraphics();

	}

	public boolean isEnabled() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
		 .getActiveWindow();
		if (f == null) {
		    return false;
		}
		if (f instanceof View) {
		    View v = (View) f;
			MapContext map = v.getMapControl().getMapContext();
			SingleLayerIterator it = new SingleLayerIterator(map.getLayers());
			while (it.hasNext())
			{
				FLayer aux = it.next();
				if (!aux.isActive())
					continue;
				Network net = (Network) aux.getProperty("network");

				if ( net != null)
				{
					int count = 0;
					for (int i=0; i < net.getOriginaFlags().size(); i++)
					{
						GvFlag flag = (GvFlag) net.getOriginaFlags().get(i);
						if (flag.isEnabled()) count++;
					}
					if (count > 1) return true;
				}
			}
			return false;

		}
		return false;
	}

	public boolean isVisible() {
		IWindow f = PluginServices.getMDIManager()
		 .getActiveWindow();
		if (f == null) {
		    return false;
		}
		if (f instanceof View) {
			return true;
		}
		return false;

	}

}


