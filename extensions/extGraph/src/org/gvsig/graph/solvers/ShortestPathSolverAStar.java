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
import java.util.PriorityQueue;

import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.GlobalCounter;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvConnector;
import org.gvsig.graph.core.GvEdge;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.GvNode;
import org.gvsig.graph.core.GvTurn;
import org.gvsig.graph.core.IGraph;
import org.gvsig.graph.solvers.pqueue.FibHeap;

/**
 * @author fjp Este es útil solo cuando podemos calcular la distancia estimada
 *         entre 2 nodos. (Es decir, para temas de cartografía). Para analizar
 *         relaciones creo que no serviría).
 */
public class ShortestPathSolverAStar extends AbstractShortestPathSolver {

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
		GvFlag fFrom = flags[0];
		fFrom.setCost(0);
		for (int i = 0; i < flags.length - 1; i++) {
			fFrom = flags[desde];
			GvFlag fTo = flags[hasta];

			if (fFrom != fTo) {
				int idStart = net.creaArcosVirtuales(fFrom);
				int idStop = net.creaArcosVirtuales(fTo);

				long tA1= System.currentTimeMillis();
				double newCost = AStar(idStart, idStop);
				long tA2= System.currentTimeMillis();
				System.out.println("T Astar = " + (tA2-tA1));
				
				elCoste1 += newCost;
				fTo.setCost(elCoste1);

				if (newCost != Double.MAX_VALUE) {
					try {
						long t1 = System.currentTimeMillis();
						populateRoute(fFrom, fTo, idStart, idStop);
						long t2 = System.currentTimeMillis();
						System.out.println("T populateRoute=" + (t2-t1));
					} catch (BaseException e) {
						e.printStackTrace();
						net.reconstruyeTramo(fFrom.getIdArc());
						net.reconstruyeTramo(fTo.getIdArc());

						throw new GraphException(e);
					}
				} else {
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

	private double AStar(int idStart, int idStop) {
		int nodeNum;
		int linkNum;
		double newCost;
		int idSiguienteNodo;
		GvNode node, toNode, finalNode, bestNode; // , *pNodoProv;
		GvEdge link;
		boolean bExit = false;

		boolean bGiroProhibido;
//		ArrayList candidatos = new ArrayList();

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
		node.calculateStimation(finalNode, 0);
		
        // Priority Queue
        PriorityQueue<GvNode> pq = new PriorityQueue<GvNode>();
        pq.add(node);


		// Mientras que la lista de candidatosSTL no esté vacía, procesamos
		// Nodos
		double bestStimation;

		while ((!bExit) && (!pq.isEmpty())) {
			// Buscamos el nodo con mínimo coste
//			node = (GvNode) candidatos.get(0);
//			bestNode = node;
//			bestStimation = node.getStimation();
//			int bestIndex = 0;
//			for (nodeNum = 1; nodeNum < candidatos.size(); nodeNum++) {
//				node = (GvNode) candidatos.get(nodeNum);
//				if (node.getStimation() < bestStimation) {
//					bestStimation = node.getStimation();
//					bestNode = node;
//					bestIndex = nodeNum;
//				}
//			} // for nodeNum candidatosSTL

            node = pq.poll(); // get the lowest-weightSum Vertex 'u',
//			node = bestNode;
			// Borramos el mejor nodo de la lista de candidatosSTL
			node.setStatus(GvNode.statWasInList);
//			candidatos.remove(bestIndex);
			// System.out.println("LINK " + link.getIdArc() + " from ");
			// System.out.println("from " + idStart + " to " +
			// finalNode.getIdNode() + ". node=" + node.getIdNode());
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
				// Pillamos el nodo vecino
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
//				int from_link = c.getFrom_link_c();
//
//				if (from_link != -1) {
//					if (c.getEdgeIn().getIdEdge() == from_link) continue; // No queremos entrar y salir
																// por el mismo conector
				// NUEVO: 26-7-2003: Comprobamos si está inicializado
				if (toNode.getNumSoluc() != GlobalCounter.getGlobalSolutionNumber()) {
					toNode.initialize();
				} else {
					// System.out.println("Nodo ya inicializado");
				}
				// Miramos a ver si podemos mejorar su best_cost
//				double costeGiro = 0;
//
//				// Miramos la lista de Giros de ese nodo
//				bGiroProhibido = false;
//				if (from_link != -1) {
//					GvEdge edgeFrom = graph.getEdgeByID(from_link);
//					for (int idGiro=0; idGiro < node.getTurnCosts().size(); idGiro++)
//					{
//						// Si está prohibido, a por otro
//						GvTurn elGiro = node.getTurnCosts().get(idGiro);
//						if ((elGiro.getIdArcFrom() == edgeFrom.getIdArc()) && 
//							(elGiro.getIdArcTo() == link.getIdArc()))
//						{
//							if (elGiro.getCost() < 0)
//							{
//								bGiroProhibido = true;
//							}
//							else
//								costeGiro = elGiro.getCost();
//	
//							// Para que pueda volver a entrar en los cálculos
//							node.setStatus(GvNode.statNotInList);
//							break; // Salimos del for porque ya hemos encontrado el giro
//						}
//					}
//				}
//				// Si está prohibido, vamos a por otro enlace
//				if (bGiroProhibido)
//				{
//					continue;					
//				}
				// TODO: REVISAR SI HAY QUE SUMAR EL COSTE DEL GIRO A NEWCOST
				// Y SI LO DE TURNCOSTS NO DEBE IR EN EXISTEMEJORA

				// Miramos a ver si podemos mejorar su best_cost
				newCost = c.getBestCostOut() + link.getWeight();

				// Change to take care of turn costs
				if (toNode.existeMejora(link, newCost)) {  // Es una mejora, así que actualizamos el vecino y
//					// lo añadimos a los candidatosSTL
//					toNode.setBestCost(newCost);
//					 
//					toNode.setFromLink(link.getIdEdge());
					double newLength = node.getAccumulatedLength() + link.getDistance();
					toNode.setAccumulatedLength(newLength);


					toNode.calculateStimation(finalNode, newCost);

					if (toNode.getStatus() != GvNode.statNowInList) {
						toNode.setStatus(GvNode.statNowInList);
						pq.add(toNode);
//						candidatos.add(toNode);
					}
				} // Si hay mejora

			} // for linkNum
		} // while candidatosSTL

		newCost = finalNode.getBestCost();

		return newCost;
	}

}
