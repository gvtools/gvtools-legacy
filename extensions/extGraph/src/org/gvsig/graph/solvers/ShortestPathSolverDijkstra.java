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

import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.AbstractNetSolver;
import org.gvsig.graph.core.GlobalCounter;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvConnector;
import org.gvsig.graph.core.GvEdge;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.GvNode;
import org.gvsig.graph.core.GvTurn;
import org.gvsig.graph.core.IGraph;
import org.gvsig.graph.solvers.pqueue.FibHeap;

import com.hardcode.gdbms.engine.data.driver.DriverException;

public class ShortestPathSolverDijkstra extends AbstractShortestPathSolver {
	
	/**
	 * @return a list of features
	 * @throws GraphException 
	 */
	public Route calculateRoute() throws GraphException {
		GvFlag[] flags = net.getFlags();
		if (flags.length == 0)
			throw new RuntimeException("Please, add flags before");
		int desde = 0;
		int hasta = 1;
		double elCoste1 = 0;
		route = new Route();
		for (int i = 0; i < flags.length - 1; i++) {
			GvFlag fFrom = flags[desde];
			GvFlag fTo = flags[hasta];

			if (fFrom != fTo) {
				int idStart = net.creaArcosVirtuales(fFrom);
				int idStop = net.creaArcosVirtuales(fTo);

				double newCost = dijkstra(idStart, idStop);
				elCoste1 += newCost;

				if (newCost != Double.MAX_VALUE)
				{
					try {
						populateRoute(fFrom, fTo, idStart, idStop);
					} catch (BaseException e) {
						e.printStackTrace();
						throw new GraphException(e);
					}
				}
				else
				{
					// No way
				}

				net.reconstruyeTramo(fFrom.getIdArc());
				net.reconstruyeTramo(fTo.getIdArc());
				desde = hasta;
			} // if son puntos distintos
			hasta++;
		}

		return route;
	}

	private double dijkstra(int idStart, int idStop) {
		int nodeNum;
		int linkNum;
		double newCost;
		int idSiguienteNodo;
		GvNode node, toNode, finalNode;// , bestNode; // , *pNodoProv;
		GvEdge link;
		boolean bExit = false;
		double bestCost;

		boolean bGiroProhibido;
//		ArrayList candidatos = new ArrayList();

		GvTurn theTurn;
		// char Mensaje[200];
		
		IGraph graph = net.getGraph();

		// NUEVO: 27-6-2003
		// Cada nodo y cada arco llevan un numero de solución. Se supone
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

//		candidatos.clear();
		// Añadimos el Start Node a la lista de candidatosSTL
		// Nodo final
		finalNode = graph.getNodeByID(idStop);
		finalNode.initialize();

		node = graph.getNodeByID(idStart);
		node.initialize();
//		bestNode = node;

//		candidatos.add(node);
		node.setCostZero();
		node.setStatus(GvNode.statNowInList);
		bestCost = Double.MAX_VALUE;
        // Priority Queue
        FibHeap pq = new FibHeap(graph.numVertices());
        pq.insert(node, 0);

		// Mientras que la lista de candidatosSTL no esté vacía, procesamos
		// Nodos

		while ((!bExit) && (!pq.empty())) {
			// Buscamos el nodo con mínimo coste
//			node = (GvNode) candidatos.get(0);
//			bestNode = node;
//			bestCost = node.getBestCost();
//			for (nodeNum = 1; nodeNum < candidatos.size(); nodeNum++) {
//				node = (GvNode) candidatos.get(nodeNum);
//				if (node.getBestCost() < bestCost) {
//					bestCost = node.getBestCost();
//					bestNode = node;
//				}
//			} // for nodeNum candidatosSTL
//
//			
//			node = bestNode;
			node = (GvNode) pq.extract_min(); // get the lowest-weightSum Vertex 'u',
			// Borramos el mejor nodo de la lista de candidatosSTL
			node.setStatus(GvNode.statWasInList);
			// TODO: BORRAR POR INDEX, NO ASÍ. ES MÁS LENTO QUE SI BORRAMOS EL i-ésimo.
//			candidatos.remove(node);
			// System.out.println("LINK " + link.getIdArc() + " from ");
			// System.out.println("from " + idStart + " to " + finalNode.getIdNode() + ". node=" + node.getIdNode());
			// Miramos si hemos llegado donde queríamos
			if (node.getIdNode() == idStop) {
				bExit = true;
				break;
			}

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
//				link = (GvEdge) node.getOutputLinks().get(linkNum);
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

					// Change to take care of turn costs
					if (toNode.existeMejora(link, newCost)) {  // Es una mejora, así que actualizamos el vecino y
//						// lo añadimos a los candidatosSTL
//						toNode.setBestCost(newCost);
//						 
//						toNode.setFromLink(link.getIdEdge());
						double newLength = node.getAccumulatedLength() + link.getDistance();
						toNode.setAccumulatedLength(newLength);


						if (toNode.getStatus() != GvNode.statNowInList) {
							toNode.setStatus(GvNode.statNowInList);
							pq.insert_or_dec_key(toNode, newCost);
							//candidatos.add(toNode);
						}
					} // Si hay mejora
				} // if ese nodo no ha estado en la lista de candidatosSTL

			} // for linkNum
		} // while candidatosSTL

		newCost = finalNode.getBestCost();

		return newCost;
	}

}
