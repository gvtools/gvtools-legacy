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
import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.gvsig.graph.core.AbstractNetSolver;
import org.gvsig.graph.core.FEdge;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvEdge;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.solvers.OneToManySolver;
import org.gvsig.graph.solvers.ShortestPathSolverDijkstra;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.GraphicLayer;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.fmap.layers.VectorialAdapter;
import com.iver.cit.gvsig.fmap.rendering.FGraphic;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class TestExtension extends Extension {

	public static AbstractNetSolver solver = new ShortestPathSolverDijkstra();
	private int idSymbolLine = -1;

	public void initialize() {
	}

	public void execute(String actionCommand) {

		View v = (View) PluginServices.getMDIManager().getActiveWindow();
		MapContext map = v.getMapControl().getMapContext();
		SingleLayerIterator it = new SingleLayerIterator(map.getLayers());
		Network net = null;
		while (it.hasNext()) {
			FLayer aux = it.next();
			if (!aux.isActive())
				continue;
			net = (Network) aux.getProperty("network");

			if (net != null) {
				break;
			}
		}

		if (actionCommand.equals("TEST_TURNCOSTS")) {
			GvFlag[] flags = net.getFlags();
			if (flags.length == 0) {
				JOptionPane.showMessageDialog(null,
						"Primero carga las paradas.");
				return;
			}
			setVelocities(net);
			// PluginServices.getMDIManager().addWindow(new
			// RouteControlPanel(net));
			JFileChooser dlg = new JFileChooser();
			if (dlg.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
				// RandomAccessFile file = null;
				// try {
				// file = new RandomAccessFile(dlg.getSelectedFile(), "rw");
				// } catch (FileNotFoundException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }
				BufferedWriter output;
				try {
					calculateOdMatrix(net, flags, dlg.getSelectedFile());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (GraphException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} // if

			return;
		}

		if (actionCommand.equals("ODMATRIX")) {
			GvFlag[] flags = net.getFlags();
			if (flags.length == 0) {
				JOptionPane.showMessageDialog(null,
						"Primero carga las paradas.");
				return;
			}
			setVelocities(net);
			// PluginServices.getMDIManager().addWindow(new
			// RouteControlPanel(net));
			JFileChooser dlg = new JFileChooser();
			if (dlg.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
				// RandomAccessFile file = null;
				// try {
				// file = new RandomAccessFile(dlg.getSelectedFile(), "rw");
				// } catch (FileNotFoundException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }
				BufferedWriter output;
				try {
					calculateOdMatrix(net, flags, dlg.getSelectedFile());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (GraphException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} // if

			return;
		}

	}

	private void calculateOdMatrix(Network net, GvFlag[] flags, File file)
			throws IOException, GraphException {
		BufferedWriter output;
		output = new BufferedWriter(new FileWriter(file));
		// output.setByteOrder(ByteOrder.LITTLE_ENDIAN);

		OneToManySolver solver = new OneToManySolver();
		solver.setNetwork(net);
		solver.putDestinationsOnNetwork(net.getFlags());
		for (int i = 0; i < flags.length; i++) {

			solver.setSourceFlag(flags[i]);
			long t1 = System.currentTimeMillis();

			solver.calculate();
			long t2 = System.currentTimeMillis();
			System.out.println("Punto " + i + " de " + flags.length + ". "
					+ (t2 - t1) + " msecs.");
			// Escribe el resultado
			// idNodo1 idNodo2 tiempo distancia

			for (int j = 0; j < flags.length; j++) {
				long secs = Math.round(flags[j].getCost());
				long meters = Math.round(flags[j].getAccumulatedLength());
				String strAux = i + "\t" + j + "\t" + secs + "\t" + meters;
				output.write(strAux);
				output.newLine();

			}
			long t3 = System.currentTimeMillis();
			System.out.println("T. de escritura: " + (t3 - t2) + " msecs.");

		}
		solver.removeDestinationsFromNetwork(net.getFlags());
		output.flush();
		output.close();
	}

	/**
	 * Asignamos el coste en función de un array de velocidades y el tipo de
	 * via. Las velocidades, en metros/seg
	 * 
	 * @param net
	 */
	private void setVelocities(Network net) {
		double[] veloKm = { 120, 110, 90, 80, 70, 60, 50, 40 };
		ArrayList veloMeters = new ArrayList();
		for (int i = 0; i < veloKm.length; i++) {
			veloMeters.add(new Double(veloKm[i] / 3.6));
		}

		for (int i = 0; i < net.getGraph().numEdges(); i++) {
			GvEdge edge = net.getGraph().getEdgeByID(i);
			Double vel = (Double) veloMeters.get(edge.getType());
			edge.setWeight(edge.getDistance() / vel.doubleValue()); // segundos
		}

	}

	private void createGraphicsFrom(Collection featureList,
			MapControl mapControl) {
		Iterator it = featureList.iterator();
		GraphicLayer graphicLayer = mapControl.getMapContext()
				.getGraphicsLayer();
		if (idSymbolLine == -1) {
			Color color = new Color(0.9f, 0.0f, 0.0f, 0.3f);
			ILineSymbol lineSymbol = SymbologyFactory.createDefaultLineSymbol();
			lineSymbol.setLineColor(color);
			lineSymbol.setLineWidth(3.0f);
			idSymbolLine = graphicLayer.addSymbol(lineSymbol);
		}
		while (it.hasNext()) {
			IFeature feat = (IFeature) it.next();
			IGeometry gAux = feat.getGeometry();
			FGraphic graphic = new FGraphic(gAux, idSymbolLine);
			graphicLayer.insertGraphic(0, graphic);
		}
		mapControl.drawGraphics();

	}

	private void createGraphicsFrom(ListIterator it, MapControl mapControl)
			throws ReadDriverException {
		GraphicLayer graphicLayer = mapControl.getMapContext()
				.getGraphicsLayer();
		Color color = new Color(0.5f, 0.8f, 0.0f, 0.5f);
		ILineSymbol lineSymbol = SymbologyFactory.createDefaultLineSymbol();
		lineSymbol.setLineColor(color);
		lineSymbol.setLineWidth(7.0f);
		int idSymbol = graphicLayer.addSymbol(lineSymbol);
		VectorialAdapter va = (VectorialAdapter) solver.getNetwork().getLayer()
				.getSource();
		while (it.hasNext()) {
			FEdge edge = (FEdge) it.next();
			IFeature feat = va.getFeature(edge.getArcID());
			IGeometry gAux = feat.getGeometry();
			FGraphic graphic = new FGraphic(gAux, idSymbol);
			graphicLayer.insertGraphic(0, graphic);
		}
		mapControl.drawGraphics();

	}

	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isVisible() {
		// TODO Auto-generated method stub
		return true;
	}

}
