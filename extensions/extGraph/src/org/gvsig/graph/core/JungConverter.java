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

import edu.uci.ics.jung.graph.ArchetypeGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.SparseGraph;

public class JungConverter {
	
	IGraph g;
	Graph gJ;
	Indexer indexer;
	
	
	public void setGraph(IGraph g) {
		this.g = g;
	}
	
	public void convert() {
		// TODO: Hacer y lanzar una excepción si algo ha ido mal
		if (g == null)
			throw new RuntimeException("Please, use setGraph() before convert()");
		gJ = new SparseGraph();
		long t1 = System.currentTimeMillis();
		
		for (int i=0; i < g.numVertices(); i++)
		{
			GvNode n = g.getNodeByID(i);
			FNode v = new FNode(i, n.getX(), n.getY());
			gJ.addVertex(v);				
		}
		indexer = Indexer.getIndexer(gJ);
		
		for (int i=0; i < g.numEdges(); i++)
		{
			GvEdge e = g.getEdgeByID(i);
			Vertex vFrom = (Vertex) indexer.getVertex(e.getIdNodeOrig());
			Vertex vTo = (Vertex) indexer.getVertex(e.getIdNodeEnd());
			
			FEdge edge = new FEdge(vFrom, vTo);
			edge.setArcID(e.getIdArc());
			edge.setDirection(e.getDirec());
			edge.setIdNodeOrig(e.getIdNodeOrig());
			edge.setIdNodeEnd(e.getIdNodeEnd());
			edge.setType(e.getType());
			edge.setWeight(e.getDistance());
			edge.setCost2(e.getWeight());
			
			gJ.addEdge(edge);
		}
		long t2 = System.currentTimeMillis();
		System.out.println("Tiempo de carga desde nodes.dbf y edges.dbf y generando JUNG network: " + (t2-t1) + " msecs");
	}
	
	public ArchetypeGraph getJungGraph() {
		if (gJ == null)
			throw new RuntimeException("You should call convert() before use this method.");
		return gJ;
	}
	
	public Indexer getIndexer() {
		return indexer;
	}

}


