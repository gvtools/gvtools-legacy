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

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.AbstractSparseGraph;

public class JungGraph extends AbstractSparseGraph {
	// Estamos obligados a mantener una lista privada porque los nodos
	// y los edges en Jung se numeran incrementando una variable
	// estática (ver nextGlobalEdgeId, en AbstractSparseEdge)
	// TODO: NO heredar de AbstractSparseGraph, sino copiar y pegar código.
	// de esta forma quizás obtengamos una clase de jung, pero mucho más rápida y
	// eficiente en consumo de memoria. Y compatible por completo con IGraph
	private ArrayList myVertexList;
	private ArrayList myEdgeList;
	public JungGraph(IGraph g) {
		super();
		long t1 = System.currentTimeMillis();
		myVertexList = new ArrayList(g.numVertices());
		myEdgeList = new ArrayList(g.numEdges());
		for (int i=0; i < g.numVertices(); i++)
		{
			GvNode n = g.getNodeByID(i);
			FNode v = new FNode(i, n.getX(), n.getY());
			addVertex(v);
			myVertexList.add(v);
			
		}
		for (int i=0; i < g.numEdges(); i++)
		{
			GvEdge e = g.getEdgeByID(i);
			Vertex vFrom = getVertexByID(e.getIdNodeOrig()); // (Vertex) indexer.getVertex(e.getIdNodeOrig());
			Vertex vTo = getVertexByID(e.getIdNodeEnd()); //indexer.getVertex(e.getIdNodeEnd());
			
			FEdge edge = new FEdge(vFrom, vTo);
			edge.setArcID(e.getIdArc());
			edge.setDirection(e.getDirec());
			edge.setIdNodeOrig(e.getIdNodeOrig());
			edge.setIdNodeEnd(e.getIdNodeEnd());
			edge.setType(e.getType());
			edge.setWeight(e.getDistance());
			edge.setCost2(e.getWeight());
			
			addEdge(edge);
			myEdgeList.add(edge);
		}
		long t2 = System.currentTimeMillis();
		System.out.println("Tiempo de carga copiando desde IGraph a JUNG network: " + (t2-t1) + " msecs");
		
	}
	
	public FNode getVertexByID(int idNode) {
		
		return (FNode) myVertexList.get(idNode);
	}

	public FEdge getEdgeByID(int idEdge) {
		return (FEdge) myEdgeList.get(idEdge);
	}

//	public EdgePair getEdgesByIdArc(int idArc) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	

}


