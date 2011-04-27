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
package org.gvsig.graph.solvers;

import java.util.ArrayList;
import java.util.Stack;

import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.AbstractNetSolver;
import org.gvsig.graph.core.GlobalCounter;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvConnector;
import org.gvsig.graph.core.GvEdge;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.GvNode;
import org.gvsig.graph.core.IGraph;
import org.gvsig.graph.core.InfoShp;
import org.gvsig.graph.core.NetworkUtils;

import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.layers.VectorialAdapter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;


public class OneToManySolver extends AbstractNetSolver {

	protected int idStart = -1;
	protected ArrayList idStops = null;
	
	// Soporte listeners
	protected ArrayList<IDijkstraListener> listeners = new ArrayList<IDijkstraListener>();
	
	public void addListener(IDijkstraListener listener) {
		listeners.add(listener);
	}
	protected boolean callMinimumCostNodeSelectedListeners(GvNode node) {
		for (IDijkstraListener listener : listeners) {
			if (listener.minimumCostNodeSelected(node))
				return true;
		}
		return false;
	}
	protected boolean callAdjacenteEdgeVisitedListeners(GvNode fromNode, GvEdge edge) {
		for (IDijkstraListener listener : listeners) {
			if (listener.adjacentEdgeVisited(fromNode, edge))
				return true;
		}
		return false;
	}
	
	protected class StopAux {
		public StopAux(Integer idStop2) {
			idStop = idStop2;
			bFound = false;
		}
		private Integer idStop;
		private boolean bFound;
		public boolean isFound() {
			return bFound;
		}
		public void setFound(boolean found) {
			bFound = found;
		}
		public Integer getIdStop() {
			return idStop;
		}
	}
	
	protected GvFlag sourceFlag;
	protected boolean bExploreAll = false; // by default
	protected double maxCost = Double.MAX_VALUE;
	protected double maxDistance = Double.MAX_VALUE;
	protected GvFlag[] destinations;
	protected Route route = new Route();
	

	
	/**
	 * We have this method separated from calculate to speed up odmatrix calculations.
	 * The developer can position flags once, and call calculate only changing source
	 * (idStart). This way, destination flags are positionned only once.
	 * @throws GraphException
	 */
	public void putDestinationsOnNetwork(GvFlag[] flags) throws GraphException
	{
//		GvFlag[] flags = net.getFlags(); // Destinations
		
		if (flags.length == 0)
			throw new RuntimeException("Please, add flags before");
		
		idStops = new ArrayList();
		for (int i = 0; i < flags.length; i++) {
			GvFlag fTo = flags[i];

			int idStop = net.creaArcosVirtuales(fTo);
			idStops.add(new Integer(idStop));
		}
		destinations = flags;
		
	}
	public void removeDestinationsFromNetwork(GvFlag[] flags)
	{
//		GvFlag[] flags = net.getFlags(); // Destinations
		if (sourceFlag != null)
			net.reconstruyeTramo(sourceFlag.getIdArc());
		for (int i = 0; i < flags.length; i++)
		{
			GvFlag fTo = flags[i];
			net.reconstruyeTramo(fTo.getIdArc());			
		}
		
	}
	/**
	 * @throws GraphException 
	 */
	public void calculate() throws GraphException {
		if (idStops == null)
		{
			throw new RuntimeException("Please, call putDestinationsOnNetwork before calculate()");
		}
//		destinations = net.getFlags();
		idStart = net.creaArcosVirtuales(sourceFlag);
		dijkstra(idStart, idStops);
		
		IGraph graph = net.getGraph();
		for (int i = 0; i < destinations.length; i++)
		{
			GvFlag fTo = destinations[i];
			Integer auxId = (Integer) idStops.get(i);
			GvNode auxNode = graph.getNodeByID(auxId.intValue());
//			System.out.println("Asigno bestCost = " + auxNode.getBestCost());
			if (auxNode.getBestCost() == Double.MAX_VALUE)
			{
				fTo.setCost(-1);
				fTo.setAccumulatedLength(-1);
			}
			else
			{
				fTo.setCost(auxNode.getBestCost());
				fTo.setAccumulatedLength(auxNode.getAccumulatedLength());				
			}
		}
		
		// TODO: No podemos reconstruir el tramo porque perdemos la conectividad
		// con el resto de destinos.
//		net.reconstruyeTramo(sourceFlag.getIdArc());
	}


	private void dijkstra(int idStart, ArrayList stops) {
		int nodeNum;
		int linkNum;
		double newCost;
		int idSiguienteNodo;
		GvNode node, toNode, bestNode; // , *pNodoProv;
		GvEdge link;
		boolean bExit = false;
		double bestCost;

		boolean bGiroProhibido;
		// List clonedStops = Collections.synchronizedList(stops);
		ArrayList clonedStops = new ArrayList();
		for (int i=0; i < stops.size(); i++)
		{
			Integer idStop = (Integer) stops.get(i);
			clonedStops.add(new StopAux(idStop));
		}
		ArrayList candidatos = new ArrayList();

//		GvTurn elGiro;
		// char Mensaje[200];
		
		IGraph graph = net.getGraph();

		// NUEVO: 27-6-2003
		// Cada nodo y cada arco llevan un nuemero de solución. Se supone
		// que si lo del nodo y el arco no coincide con
		// este numero, todavía no ha sido inicializado y hay que hacerlo.
		// Para evitar coincidencias cuando de la vuelta el contador, cada
		// 65000 peticiones (por ejemplo), repasamos toda
		// la red y ponemos numSolucGlobal a -1
		if (GlobalCounter.increment())
		{
			for (nodeNum = 0; nodeNum < graph.numVertices(); nodeNum++) {
				node = graph.getNodeByID(nodeNum);
				node.initialize();
			} // for nodeNum */
		}

		candidatos.clear();
		// Añadimos el Start Node a la lista de candidatosSTL
		// Nodos finales
		for (int h=0; h < clonedStops.size(); h++)
		{
			StopAux auxStop = (StopAux) clonedStops.get(h);
			int idStop = auxStop.getIdStop().intValue();
		
			GvNode auxNode = graph.getNodeByID(idStop);
			auxNode.initialize();
		}
		node = graph.getNodeByID(idStart);
		node.initialize();
		bestNode = node;

		candidatos.add(node);
		node.setCostZero();
		node.setStatus(GvNode.statNowInList);
		bestCost = Double.MAX_VALUE;

		// Mientras que la lista de candidatosSTL no esté vacía, procesamos
		// Nodos
		int stopActual = 0;

		while ((!bExit) && (candidatos.size() > 0)) {
			// Buscamos el nodo con mínimo coste
			node = (GvNode) candidatos.get(0);
			bestNode = node;
			bestCost = node.getBestCost();
			for (nodeNum = 1; nodeNum < candidatos.size(); nodeNum++) {
				node = (GvNode) candidatos.get(nodeNum);
				if (node.getBestCost() < bestCost) {
					bestCost = node.getBestCost();
					bestNode = node;
				}
			} // for nodeNum candidatosSTL

			node = bestNode;
			// Borramos el mejor nodo de la lista de candidatosSTL
			node.setStatus(GvNode.statWasInList);
			// TODO: BORRAR POR INDEX, NO ASÍ. ES MÁS LENTO QUE SI BORRAMOS EL i-ésimo.
			candidatos.remove(node);
			
			if (callMinimumCostNodeSelectedListeners(node))
				bExit = true;
			
			// Si hemos fijado un máximo coste de exploración, lo
			// tenemos en cuenta para salir.
			if ((maxCost < bestNode.getBestCost()) ||
					maxDistance < bestNode.getAccumulatedLength())
			{
				bExit=true;
			}
			
			// System.out.println("LINK " + link.getIdArc() + " from ");
			// System.out.println("from " + idStart + " to " + finalNode.getIdNode() + ". node=" + node.getIdNode());
			if (!bExploreAll)
			{
				// Miramos si hemos llegado donde queríamos
				StopAux auxStop = (StopAux) clonedStops.get(stopActual);
				int idStop = auxStop.getIdStop().intValue();
				
				if (bestNode.getIdNode() == idStop) {
					// Hemos llegado a ese punto. Miramos el resto de puntos destino
					// a ver si ya hemos pasado por alguno de ellos.
					// Si con algun punto no pasamos por aquí, no habremos llegado a ese punto.
					// No importa, puede que al resto sí, y esos nodos a los que sí hemos llegado
					// tendrán bien rellenado el coste.
					auxStop.setFound(true);
					for (int i=stopActual; i < clonedStops.size(); i++)
					{
						auxStop = (StopAux) clonedStops.get(i);
						if (!auxStop.isFound())
						{
							Integer id = auxStop.getIdStop();
		
							GvNode auxNode = graph.getNodeByID(id.intValue());
							if (auxNode.getStatus() == GvNode.statWasInList)
							{
								auxStop.setFound(true);
							}
							else
							{
								stopActual = i;
								break;
							}
						}
					}						
					if (clonedStops.size() == 0)
					{
						bExit = true;
						break; // Ya hemos llegado a todos los nodos
					}				
				}
			} // if bExploreAll
			
			// sprintf(Mensaje,"Enlaces en el nodo %ld:
			// %ld.",pNodo->idNodo,pNodo->Enlaces.GetSize());
			// AfxMessageBox(Mensaje);

			// Añadimos a la lista de candidatosSTL los vecinos del nodo que
			// acabamos de borrar
			// HAY Arcos QUE SALEN Y Arcos QUE LLEGAN. SOLO MIRAMOS LOS QUE
			// SALEN.
//			for (linkNum = 0; linkNum < node.getOutputLinks().size(); linkNum++) {
			for (int iConec=0; iConec< node.getConnectors().size();  iConec++) {
				// Pillamos el nodo vecino
				GvConnector c = node.getConnectors().get(iConec);
				if (c.getEdgeOut() == null) continue;
				
				link = (GvEdge) c.getEdgeOut();
				idSiguienteNodo = link.getIdNodeEnd();
				
				// To avoid U-turn
				if (c.getEdgeIn() != null)
					if (c.getFrom_link_c() == c.getEdgeIn().getIdEdge())
						continue;


				
				toNode = graph.getNodeByID(idSiguienteNodo);

				// 27_5_2004
				// Si un arco tiene coste negativo, lo ignoramos
				if (link.getWeight() < 0)
					continue;

				// Fin arco con coste negativo

				// NUEVO: 26-7-2003: Comprobamos si está inicializado
				if (toNode.getNumSoluc() != GlobalCounter.getGlobalSolutionNumber()) {
					toNode.initialize();
				}
				else
				{
					// System.out.println("Nodo ya inicializado");
				}

				// Miramos si ese nodo ya ha estado antes en la lista de
				// candidatos
				if (toNode.getStatus() != GvNode.statWasInList) {
					// Miramos a ver si podemos mejorar su best_cost
					newCost = c.getBestCostOut() + link.getWeight();
//					newCost = node.getBestCost() + link.getWeight();
					// Change to take care of turn costs
					if (toNode.existeMejora(link, newCost)) {  // Es una mejora, así que actualizamos el vecino y
//						// lo añadimos a los candidatosSTL
//						toNode.setBestCost(newCost);
//						 
//						toNode.setFromLink(link.getIdEdge());
						// Es una mejora, así que actualizamos el vecino y
						// lo añadimos a los candidatosSTL
						double newLength = node.getAccumulatedLength() + link.getDistance();
						toNode.setAccumulatedLength(newLength);

						if (toNode.getStatus() != GvNode.statNowInList) {
							toNode.setStatus(GvNode.statNowInList);
							candidatos.add(toNode);
						}
					} // Si hay mejora
				} // if ese nodo no ha estado en la lista de candidatosSTL
				if (callAdjacenteEdgeVisitedListeners(bestNode, link))
					continue;				

			} // for linkNum
		} // while candidatosSTL

	}


	public GvFlag getSourceFlag() {
		return sourceFlag;
	}


	public void setSourceFlag(GvFlag sourceFlag) {
		this.sourceFlag = sourceFlag;
		
	}
	public void setExploreAllNetwork(boolean b) {
		bExploreAll  = b;
	}
	public double getMaxCost() {
		return maxCost;
	}
	public void setMaxCost(double maxCost) {
		this.maxCost = maxCost;
	}
	public double getMaxDistance() {
		return maxDistance;
	}
	public void setMaxDistance(double maxDistance) {
		this.maxDistance = maxDistance;
	}

}
