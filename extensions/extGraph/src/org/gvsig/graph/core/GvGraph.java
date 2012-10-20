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
package org.gvsig.graph.core;

import java.util.ArrayList;
import java.util.Hashtable;

public class GvGraph implements IGraph {
	private ArrayList nodes;
	private ArrayList edges;
	private Hashtable indexArcsEdges;

	public GvGraph() {
		nodes = new ArrayList();
		edges = new ArrayList();
		indexArcsEdges = new Hashtable();
	}

	public GvGraph(int numArcs, int numEdges, int numNodes) {
		nodes = new ArrayList<GvNode>(numNodes);
		edges = new ArrayList<GvEdge>(numEdges);
		indexArcsEdges = new Hashtable<Integer, EdgePair>(numArcs);
	}

	public GvNode getNodeByID(int idNode) {
		return (GvNode) nodes.get(idNode);
	}

	public GvEdge getEdgeByID(int idEdge) {
		return (GvEdge) edges.get(idEdge);
	}

	public int numVertices() {
		return nodes.size();
	}

	public int numEdges() {
		return edges.size();
	}

	public EdgePair getEdgesByIdArc(int idArc) {
		// if (idArc < indexArcsEdges.size())
		return (EdgePair) indexArcsEdges.get(new Integer(idArc));
		// return null;
	}

	public void addEdge(GvEdge edge) {
		edges.add(edge);
	}

	public void addNode(GvNode node) {
		nodes.add(node);

	}

	public void addEdgePair(int idArc, EdgePair edgePair) {
		// assert(indexArcsEdges.size() == edgePair.idArc);
		Object aux = indexArcsEdges.put(new Integer(idArc), edgePair);
		// assert(aux == null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.graph.core.IGraph#removeEdge(int)
	 */
	public void removeEdge(int idEdge) {
		// TODO: Solo se usa para edges añadidos debido a la incorporación
		// de flags. Si se usara para los otros arcos, habría que
		// revisar indexArcsEdges para ver si es necesario quitarlo
		// también de ahí.
		edges.remove(idEdge);
	}

	public void removeNode(int idNode) {
		nodes.remove(idNode);
	}

	// public void addEdge(int i, GvEdge edge) {
	// edges.set(i, edge);
	//
	// }
	// public void addNode(int i, GvNode node) {
	// nodes.set(i, node);
	//
	// }

}
