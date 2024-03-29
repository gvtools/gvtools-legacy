/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
/* CVS MESSAGES:
 *
 * $Id: 
 * $Log: 
 */
package com.vividsolutions.jts.operation.polygonize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateArrays;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.planargraph.DirectedEdge;
import com.vividsolutions.jts.planargraph.DirectedEdgeStar;
import com.vividsolutions.jts.planargraph.Edge;
import com.vividsolutions.jts.planargraph.Node;
import com.vividsolutions.jts.planargraph.PlanarGraph;
import com.vividsolutions.jts.util.Assert;

/**
 * Polygonizer graph which stores its edges in a adapter based collection.
 * 
 * 
 * @author Alvaro Zabala
 * 
 */

/*
 * FIXME La idea es sustituir el Node y los Edges para que no mantengan entradas
 * de LineString en memoria.
 * 
 * As�, tendremos un AdapterPolygonizeEdge, AdapterPolygonizeDirectedEdge y
 * AdapterEdgeRing que lo que tendr�n es mantener "punteros" de los
 * correspondientes DriverBasedCollection.
 */
public class AdapterBasedPolygonizeGraph extends PlanarGraph {
	/*
	 * This class is an adaptation of JTS's PolygonizeGraph.
	 */

	private static int getDegreeNonDeleted(Node node) {
		List edges = node.getOutEdges().getEdges();
		int degree = 0;
		for (Iterator i = edges.iterator(); i.hasNext();) {
			PolygonizeDirectedEdge de = (PolygonizeDirectedEdge) i.next();
			if (!de.isMarked())
				degree++;
		}
		return degree;
	}

	private static int getDegree(Node node, long label) {
		List edges = node.getOutEdges().getEdges();
		int degree = 0;
		for (Iterator i = edges.iterator(); i.hasNext();) {
			PolygonizeDirectedEdge de = (PolygonizeDirectedEdge) i.next();
			if (de.getLabel() == label)
				degree++;
		}
		return degree;
	}

	/**
	 * Deletes all edges at a node
	 */
	public static void deleteAllEdges(Node node) {
		List edges = node.getOutEdges().getEdges();
		for (Iterator i = edges.iterator(); i.hasNext();) {
			PolygonizeDirectedEdge de = (PolygonizeDirectedEdge) i.next();
			de.setMarked(true);
			PolygonizeDirectedEdge sym = (PolygonizeDirectedEdge) de.getSym();
			if (sym != null)
				sym.setMarked(true);
		}
	}

	private GeometryFactory factory;

	/**
	 * Create a new polygonization graph.
	 */
	public AdapterBasedPolygonizeGraph(GeometryFactory factory) {
		this.factory = factory;
	}

	/**
	 * Add a {@link LineString} forming an edge of the polygon graph.
	 * 
	 * @param line
	 *            the line to add
	 */
	public void addEdge(LineString line) {
		if (line.isEmpty()) {
			return;
		}
		Coordinate[] linePts = CoordinateArrays.removeRepeatedPoints(line
				.getCoordinates());

		if (linePts.length < 2) {
			return;
		}

		Coordinate startPt = linePts[0];
		Coordinate endPt = linePts[linePts.length - 1];

		Node nStart = getNode(startPt);
		Node nEnd = getNode(endPt);

		DirectedEdge de0 = new PolygonizeDirectedEdge(nStart, nEnd, linePts[1],
				true);
		DirectedEdge de1 = new PolygonizeDirectedEdge(nEnd, nStart,
				linePts[linePts.length - 2], false);
		Edge edge = new PolygonizeEdge(line);
		edge.setDirectedEdges(de0, de1);
		add(edge);
	}

	private Node getNode(Coordinate pt) {
		Node node = findNode(pt);
		if (node == null) {
			node = new Node(pt);
			// ensure node is only added once to graph
			add(node);
		}
		return node;
	}

	private void computeNextCWEdges() {
		// set the next pointers for the edges around each node
		for (Iterator iNode = nodeIterator(); iNode.hasNext();) {
			Node node = (Node) iNode.next();
			computeNextCWEdges(node);
		}
	}

	/**
	 * Convert the maximal edge rings found by the initial graph traversal into
	 * the minimal edge rings required by JTS polygon topology rules.
	 * 
	 * @param ringEdges
	 *            the list of start edges for the edgeRings to convert.
	 */
	private void convertMaximalToMinimalEdgeRings(List ringEdges) {
		for (Iterator i = ringEdges.iterator(); i.hasNext();) {
			PolygonizeDirectedEdge de = (PolygonizeDirectedEdge) i.next();
			long label = de.getLabel();
			List intNodes = findIntersectionNodes(de, label);

			if (intNodes == null)
				continue;
			// flip the next pointers on the intersection nodes to create
			// minimal edge rings
			for (Iterator iNode = intNodes.iterator(); iNode.hasNext();) {
				Node node = (Node) iNode.next();
				computeNextCCWEdges(node, label);
			}
		}
	}

	/**
	 * Finds all nodes in a maximal edgering which are self-intersection nodes
	 * 
	 * @param startDE
	 * @param label
	 * @return the list of intersection nodes found, or <code>null</code> if no
	 *         intersection nodes were found
	 */
	private static List findIntersectionNodes(PolygonizeDirectedEdge startDE,
			long label) {
		PolygonizeDirectedEdge de = startDE;
		List intNodes = null;
		do {
			Node node = de.getFromNode();
			if (getDegree(node, label) > 1) {
				if (intNodes == null)
					intNodes = new ArrayList();
				intNodes.add(node);
			}

			de = de.getNext();
			Assert.isTrue(de != null, "found null DE in ring");
			Assert.isTrue(de == startDE || !de.isInRing(),
					"found DE already in ring");
		} while (de != startDE);

		return intNodes;
	}

	/**
	 * Computes the EdgeRings formed by the edges in this graph.
	 * 
	 * @return a list of the {@link EdgeRing}s found by the polygonization
	 *         process.
	 */
	public List getEdgeRings() {
		// maybe could optimize this, since most of these pointers should be set
		// correctly already
		// by deleteCutEdges()
		computeNextCWEdges();
		// clear labels of all edges in graph
		label(dirEdges, -1);
		List maximalRings = findLabeledEdgeRings(dirEdges);
		convertMaximalToMinimalEdgeRings(maximalRings);

		// find all edgerings
		List edgeRingList = new ArrayList();
		for (Iterator i = dirEdges.iterator(); i.hasNext();) {
			PolygonizeDirectedEdge de = (PolygonizeDirectedEdge) i.next();
			if (de.isMarked())
				continue;
			if (de.isInRing())
				continue;

			EdgeRing er = findEdgeRing(de);
			edgeRingList.add(er);
		}
		return edgeRingList;
	}

	/**
	 * 
	 * @param dirEdges
	 *            a List of the DirectedEdges in the graph
	 * @return a List of DirectedEdges, one for each edge ring found
	 */
	private static List findLabeledEdgeRings(Collection dirEdges) {
		List edgeRingStarts = new ArrayList();
		// label the edge rings formed
		long currLabel = 1;
		for (Iterator i = dirEdges.iterator(); i.hasNext();) {
			PolygonizeDirectedEdge de = (PolygonizeDirectedEdge) i.next();
			if (de.isMarked())
				continue;
			if (de.getLabel() >= 0)
				continue;

			edgeRingStarts.add(de);
			List edges = findDirEdgesInRing(de);

			label(edges, currLabel);
			currLabel++;
		}
		return edgeRingStarts;
	}

	/**
	 * Finds and removes all cut edges from the graph.
	 * 
	 * @return a list of the {@link LineString}s forming the removed cut edges
	 */
	public List deleteCutEdges() {
		computeNextCWEdges();
		// label the current set of edgerings
		findLabeledEdgeRings(dirEdges);

		/**
		 * Cut Edges are edges where both dirEdges have the same label. Delete
		 * them, and record them
		 */
		List cutLines = new ArrayList();
		for (Iterator i = dirEdges.iterator(); i.hasNext();) {
			PolygonizeDirectedEdge de = (PolygonizeDirectedEdge) i.next();
			if (de.isMarked())
				continue;

			PolygonizeDirectedEdge sym = (PolygonizeDirectedEdge) de.getSym();

			if (de.getLabel() == sym.getLabel()) {
				de.setMarked(true);
				sym.setMarked(true);

				// save the line as a cut edge
				PolygonizeEdge e = (PolygonizeEdge) de.getEdge();
				cutLines.add(e.getLine());
			}
		}
		return cutLines;
	}

	private static void label(Collection dirEdges, long label) {
		for (Iterator i = dirEdges.iterator(); i.hasNext();) {
			PolygonizeDirectedEdge de = (PolygonizeDirectedEdge) i.next();
			de.setLabel(label);
		}
	}

	private static void computeNextCWEdges(Node node) {
		DirectedEdgeStar deStar = node.getOutEdges();
		PolygonizeDirectedEdge startDE = null;
		PolygonizeDirectedEdge prevDE = null;

		// the edges are stored in CCW order around the star
		for (Iterator i = deStar.getEdges().iterator(); i.hasNext();) {
			PolygonizeDirectedEdge outDE = (PolygonizeDirectedEdge) i.next();
			if (outDE.isMarked())
				continue;

			if (startDE == null)
				startDE = outDE;
			if (prevDE != null) {
				PolygonizeDirectedEdge sym = (PolygonizeDirectedEdge) prevDE
						.getSym();
				sym.setNext(outDE);
			}
			prevDE = outDE;
		}
		if (prevDE != null) {
			PolygonizeDirectedEdge sym = (PolygonizeDirectedEdge) prevDE
					.getSym();
			sym.setNext(startDE);
		}
	}

	/**
	 * Computes the next edge pointers going CCW around the given node, for the
	 * given edgering label. This algorithm has the effect of converting maximal
	 * edgerings into minimal edgerings
	 */
	private static void computeNextCCWEdges(Node node, long label) {
		DirectedEdgeStar deStar = node.getOutEdges();
		// PolyDirectedEdge lastInDE = null;
		PolygonizeDirectedEdge firstOutDE = null;
		PolygonizeDirectedEdge prevInDE = null;

		// the edges are stored in CCW order around the star
		List edges = deStar.getEdges();
		// for (Iterator i = deStar.getEdges().iterator(); i.hasNext(); ) {
		for (int i = edges.size() - 1; i >= 0; i--) {
			PolygonizeDirectedEdge de = (PolygonizeDirectedEdge) edges.get(i);
			PolygonizeDirectedEdge sym = (PolygonizeDirectedEdge) de.getSym();

			PolygonizeDirectedEdge outDE = null;
			if (de.getLabel() == label)
				outDE = de;
			PolygonizeDirectedEdge inDE = null;
			if (sym.getLabel() == label)
				inDE = sym;

			if (outDE == null && inDE == null)
				continue; // this edge is not in edgering

			if (inDE != null) {
				prevInDE = inDE;
			}

			if (outDE != null) {
				if (prevInDE != null) {
					prevInDE.setNext(outDE);
					prevInDE = null;
				}
				if (firstOutDE == null)
					firstOutDE = outDE;
			}
		}
		if (prevInDE != null) {
			Assert.isTrue(firstOutDE != null);
			prevInDE.setNext(firstOutDE);
		}
	}

	/**
	 * Traverse a ring of DirectedEdges, accumulating them into a list. This
	 * assumes that all dangling directed edges have been removed from the
	 * graph, so that there is always a next dirEdge.
	 * 
	 * @param startDE
	 *            the DirectedEdge to start traversing at
	 * @return a List of DirectedEdges that form a ring
	 */
	private static List findDirEdgesInRing(PolygonizeDirectedEdge startDE) {
		PolygonizeDirectedEdge de = startDE;
		List edges = new ArrayList();
		do {
			edges.add(de);
			de = de.getNext();
			Assert.isTrue(de != null, "found null DE in ring");
			Assert.isTrue(de == startDE || !de.isInRing(),
					"found DE already in ring");
		} while (de != startDE);

		return edges;
	}

	private EdgeRing findEdgeRing(PolygonizeDirectedEdge startDE) {
		PolygonizeDirectedEdge de = startDE;
		EdgeRing er = new EdgeRing(factory);
		do {
			er.add(de);
			de.setRing(er);
			de = de.getNext();
			Assert.isTrue(de != null, "found null DE in ring");
			Assert.isTrue(de == startDE || !de.isInRing(),
					"found DE already in ring");
		} while (de != startDE);

		return er;
	}

	/**
	 * Marks all edges from the graph which are "dangles". Dangles are which are
	 * incident on a node with degree 1. This process is recursive, since
	 * removing a dangling edge may result in another edge becoming a dangle. In
	 * order to handle large recursion depths efficiently, an explicit recursion
	 * stack is used
	 * 
	 * @return a List containing the {@link LineStrings} that formed dangles
	 */
	public Collection deleteDangles() {
		List nodesToRemove = findNodesOfDegree(1);
		Set dangleLines = new HashSet();

		Stack nodeStack = new Stack();
		for (Iterator i = nodesToRemove.iterator(); i.hasNext();) {
			nodeStack.push(i.next());
		}

		while (!nodeStack.isEmpty()) {
			Node node = (Node) nodeStack.pop();

			deleteAllEdges(node);
			List nodeOutEdges = node.getOutEdges().getEdges();
			for (Iterator i = nodeOutEdges.iterator(); i.hasNext();) {
				PolygonizeDirectedEdge de = (PolygonizeDirectedEdge) i.next();
				// delete this edge and its sym
				de.setMarked(true);
				PolygonizeDirectedEdge sym = (PolygonizeDirectedEdge) de
						.getSym();
				if (sym != null)
					sym.setMarked(true);

				// save the line as a dangle
				PolygonizeEdge e = (PolygonizeEdge) de.getEdge();
				dangleLines.add(e.getLine());

				Node toNode = de.getToNode();
				// add the toNode to the list to be processed, if it is now a
				// dangle
				if (getDegreeNonDeleted(toNode) == 1)
					nodeStack.push(toNode);
			}
		}
		return dangleLines;
	}
}
