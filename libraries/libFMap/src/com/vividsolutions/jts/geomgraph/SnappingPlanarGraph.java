/*
 * Created on 11-sep-2006 by azabala
 *
 */
package com.vividsolutions.jts.geomgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Location;

/**
 * @author alzabord
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class SnappingPlanarGraph extends PlanarGraph {

	public static final CGAlgorithms cga = new CGAlgorithms();

	protected List edges = new ArrayList();

	protected SnappingNodeMap nodes;

	protected List edgeEndList = new ArrayList();

	/**
	 * For nodes in the Collection, link the DirectedEdges at the node that are
	 * in the result. This allows clients to link only a subset of nodes in the
	 * graph, for efficiency (because they know that only a subset is of
	 * interest).
	 */
	public static void linkResultDirectedEdges(Collection nodes) {
		for (Iterator nodeit = nodes.iterator(); nodeit.hasNext();) {
			Node node = (Node) nodeit.next();
			DirectedEdgeStar edgeStar = (DirectedEdgeStar) node.getEdges();
			edgeStar.linkResultDirectedEdges();
		}
	}

	public SnappingPlanarGraph(NodeFactory nodeFact, double tolerance) {
		nodes = new SnappingNodeMap(nodeFact, tolerance);
	}

	public SnappingPlanarGraph(double tolerance) {
		nodes = new SnappingNodeMap(new NodeFactory(), tolerance);
	}

	public Iterator getEdgeIterator() {
		return edges.iterator();
	}

	public Collection getEdgeEnds() {
		return edgeEndList;
	}

	public boolean isBoundaryNode(int geomIndex, Coordinate coord) {
		Node node = nodes.find(coord);
		if (node == null)
			return false;
		Label label = node.getLabel();
		if (label != null && label.getLocation(geomIndex) == Location.BOUNDARY)
			return true;
		return false;
	}

	protected void insertEdge(Edge e) {
		edges.add(e);
	}

	public void add(EdgeEnd e) {
		nodes.add(e);
		edgeEndList.add(e);
	}

	public Iterator getNodeIterator() {
		return nodes.iterator();
	}

	public Collection getNodes() {
		return nodes.values();
	}

	public Node addNode(Node node) {
		return nodes.addNode(node);
	}

	public Node addNode(Coordinate coord) {
		return nodes.addNode(coord);
	}

	/**
	 * @return the node if found; null otherwise
	 */
	public Node find(Coordinate coord) {
		return nodes.find(coord);
	}

	/**
	 * Add a set of edges to the graph. For each edge two DirectedEdges will be
	 * created. DirectedEdges are NOT linked by this method.
	 */
	public void addEdges(List edgesToAdd) {
		// create all the nodes for the edges
		for (Iterator it = edgesToAdd.iterator(); it.hasNext();) {
			Edge e = (Edge) it.next();
			edges.add(e);
			SnapDirectedEdge de1 = new SnapDirectedEdge(e, true);
			SnapDirectedEdge de2 = new SnapDirectedEdge(e, false);
			de1.setSym(de2);
			de2.setSym(de1);
			// IMPORTAR VER QUE PASA AQUÍ: Para cada nuevo Edge, construye un
			// EdgeEnd que puede dar a lugar a un nodo y que origina un Edge
			add(de1);
			add(de2);
		}
	}

	/**
	 * Link the DirectedEdges at the nodes of the graph. This allows clients to
	 * link only a subset of nodes in the graph, for efficiency (because they
	 * know that only a subset is of interest).
	 */
	public void linkResultDirectedEdges() {
		for (Iterator nodeit = nodes.iterator(); nodeit.hasNext();) {
			Node node = (Node) nodeit.next();
			((DirectedEdgeStar) node.getEdges()).linkResultDirectedEdges();
		}
	}

	/**
	 * Link the DirectedEdges at the nodes of the graph. This allows clients to
	 * link only a subset of nodes in the graph, for efficiency (because they
	 * know that only a subset is of interest).
	 */
	public void linkAllDirectedEdges() {
		for (Iterator nodeit = nodes.iterator(); nodeit.hasNext();) {
			Node node = (Node) nodeit.next();
			((DirectedEdgeStar) node.getEdges()).linkAllDirectedEdges();
		}
	}

	/**
	 * Returns the EdgeEnd which has edge e as its base edge (MD 18 Feb 2002 -
	 * this should return a pair of edges)
	 * 
	 * @return the edge, if found <code>null</code> if the edge was not found
	 */
	public EdgeEnd findEdgeEnd(Edge e) {
		for (Iterator i = getEdgeEnds().iterator(); i.hasNext();) {
			EdgeEnd ee = (EdgeEnd) i.next();
			if (ee.getEdge() == e)
				return ee;
		}
		return null;
	}

	/**
	 * Returns the edge whose first two coordinates are p0 and p1
	 * 
	 * @return the edge, if found <code>null</code> if the edge was not found
	 */
	public Edge findEdge(Coordinate p0, Coordinate p1) {
		for (int i = 0; i < edges.size(); i++) {
			Edge e = (Edge) edges.get(i);
			Coordinate[] eCoord = e.getCoordinates();
			if (p0.equals(eCoord[0]) && p1.equals(eCoord[1]))
				return e;
		}
		return null;
	}

	/**
	 * Returns the edge which starts at p0 and whose first segment is parallel
	 * to p1
	 * 
	 * @return the edge, if found <code>null</code> if the edge was not found
	 */
	public Edge findEdgeInSameDirection(Coordinate p0, Coordinate p1) {
		for (int i = 0; i < edges.size(); i++) {
			Edge e = (Edge) edges.get(i);

			Coordinate[] eCoord = e.getCoordinates();
			if (matchInSameDirection(p0, p1, eCoord[0], eCoord[1]))
				return e;

			if (matchInSameDirection(p0, p1, eCoord[eCoord.length - 1],
					eCoord[eCoord.length - 2]))
				return e;
		}
		return null;
	}

	/**
	 * The coordinate pairs match if they define line segments lying in the same
	 * direction. E.g. the segments are parallel and in the same quadrant (as
	 * opposed to parallel and opposite!).
	 */
	private boolean matchInSameDirection(Coordinate p0, Coordinate p1,
			Coordinate ep0, Coordinate ep1) {
		if (!p0.equals(ep0))
			return false;

		if (CGAlgorithms.computeOrientation(p0, p1, ep1) == CGAlgorithms.COLLINEAR
				&& Quadrant.quadrant(p0, p1) == Quadrant.quadrant(ep0, ep1))
			return true;
		return false;
	}

	public void dump() {
		System.out.println("EDGES");
		Iterator it = this.getEdgeIterator();
		while (it.hasNext()) {
			Edge e = (Edge) it.next();
			e.print(System.out);
			System.out.println("");
		}
		System.out.println("NODES");
		it = this.getNodeIterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			System.out.println(node.getCoordinate());
			System.out.println(node.getLabel());
			List edges = node.getEdges().getEdges();
			for (int z = 0; z < edges.size(); z++) {
				EdgeEnd ee = (EdgeEnd) edges.get(z);
				Label eeL = ee.getLabel();
				System.out.println(ee.toString() + "," + eeL.toString());
			}
		}
		// nodes.dump();
	}

}