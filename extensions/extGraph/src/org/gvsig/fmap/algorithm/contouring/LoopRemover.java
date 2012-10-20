/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

/*
 * AUTHORS (In addition to CIT):
 * 2008 Software Colaborativo (www.scolab.es)   development
 */

package org.gvsig.fmap.algorithm.contouring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryComponentFilter;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.operation.linemerge.LineMergeEdge;
import com.vividsolutions.jts.operation.linemerge.LineMergeGraph;
import com.vividsolutions.jts.planargraph.DirectedEdge;
import com.vividsolutions.jts.planargraph.GraphComponent;
import com.vividsolutions.jts.planargraph.Node;
import com.vividsolutions.jts.planargraph.Subgraph;
import com.vividsolutions.jts.planargraph.algorithm.ConnectedSubgraphFinder;
import com.vividsolutions.jts.util.Assert;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es) Based in LineSequence from
 *         Java Topology Suite. The idea is to avoid loops. Later, we will check
 *         this loops to see if the CompactArea contains all good edges and
 *         disjoint bad edges. First, we need a well oriented graph. Then, we
 *         iterate for nodes, and check if we arrive to any already visited. If
 *         yes, then we will set the end point of this edge to the next non
 *         visited node.
 */
public class LoopRemover {

	public LoopRemover() {
	}

	/**
	 * Builds a sequence from a set of LineStrings so that they are ordered end
	 * to end. A sequence is a complete non-repeating list of the linear
	 * components of the input. Each linestring is oriented so that identical
	 * endpoints are adjacent in the list.
	 * 
	 * The input linestrings may form one or more connected sets. The input
	 * linestrings should be correctly noded, or the results may not be what is
	 * expected. The output of this method is a single MultiLineString
	 * containing the ordered linestrings in the sequence.
	 * <p>
	 * The sequencing employs the classic <b>Eulerian path</b> graph algorithm.
	 * Since Eulerian paths are not uniquely determined, further rules are used
	 * to make the computed sequence preserve as much as possible of the input
	 * ordering. Within a connected subset of lines, the ordering rules are:
	 * <ul>
	 * <li>If there is degree-1 node which is the start node of an linestring,
	 * use that node as the start of the sequence
	 * <li>If there is a degree-1 node which is the end node of an linestring,
	 * use that node as the end of the sequence
	 * <li>If the sequence has no degree-1 nodes, use any node as the start
	 * </ul>
	 * 
	 * <p>
	 * Not all arrangements of lines can be sequenced. For a connected set of
	 * edges in a graph, Euler's Theorem states that there is a sequence
	 * containing each edge once if and only if there are no more than 2 nodes
	 * of odd degree. If it is not possible to find a sequence, the
	 * {@link #isSequenceable} method will return <code>false</code>.
	 * 
	 * @version 1.7
	 */
	/**
	 * Tests whether a {@link Geometry} is sequenced correctly.
	 * {@link LineString}s are trivially sequenced. {@link MultiLineString}s are
	 * checked for correct sequencing. Otherwise, <code>isSequenced</code> is
	 * defined to be <code>true</code> for geometries that are not lineal.
	 * 
	 * @param geom
	 *            the geometry to test
	 * @return <code>true</code> if the geometry is sequenced or is not lineal
	 */
	public static boolean isSequenced(Geometry geom) {
		if (!(geom instanceof MultiLineString)) {
			return true;
		}

		MultiLineString mls = (MultiLineString) geom;
		// the nodes in all subgraphs which have been completely scanned
		Set prevSubgraphNodes = new TreeSet();

		Coordinate lastNode = null;
		List currNodes = new ArrayList();
		for (int i = 0; i < mls.getNumGeometries(); i++) {
			LineString line = (LineString) mls.getGeometryN(i);
			Coordinate startNode = line.getCoordinateN(0);
			Coordinate endNode = line.getCoordinateN(line.getNumPoints() - 1);

			/**
			 * If this linestring is connected to a previous subgraph, geom is
			 * not sequenced
			 */
			if (prevSubgraphNodes.contains(startNode))
				return false;
			if (prevSubgraphNodes.contains(endNode))
				return false;

			if (lastNode != null) {
				if (!startNode.equals(lastNode)) {
					// start new connected sequence
					prevSubgraphNodes.addAll(currNodes);
					currNodes.clear();
				}
			}
			currNodes.add(startNode);
			currNodes.add(endNode);
			lastNode = endNode;
		}
		return true;
	}

	private LineMergeGraph graph = new LineMergeGraph();
	// initialize with default, in case no lines are input
	private GeometryFactory factory = new GeometryFactory();
	private int lineCount = 0;

	private boolean isRun = false;
	private Geometry sequencedGeometry = null;
	private boolean isSequenceable = false;

	/**
	 * Adds a {@link Collection} of {@link Geometry}s to be sequenced. May be
	 * called multiple times. Any dimension of Geometry may be added; the
	 * constituent linework will be extracted.
	 * 
	 * @param geometries
	 *            a Collection of geometries to add
	 */
	public void add(Collection geometries) {
		for (Iterator i = geometries.iterator(); i.hasNext();) {
			Geometry geometry = (Geometry) i.next();
			add(geometry);
		}
	}

	/**
	 * Adds a {@link Geometry} to be sequenced. May be called multiple times.
	 * Any dimension of Geometry may be added; the constituent linework will be
	 * extracted.
	 * 
	 * @param geometry
	 *            the geometry to add
	 */
	public void add(Geometry geometry) {
		geometry.apply(new GeometryComponentFilter() {
			public void filter(Geometry component) {
				if (component instanceof LineString) {
					addLine((LineString) component);
				}
			}
		});
	}

	private void addLine(LineString lineString) {
		if (factory == null) {
			this.factory = lineString.getFactory();
		}
		graph.addEdge(lineString);
		lineCount++;
	}

	/**
	 * Tests whether the arrangement of linestrings has a valid sequence.
	 * 
	 * @return <code>true</code> if a valid sequence exists.
	 */
	public boolean isSequenceable() {
		computeSequence();
		return isSequenceable;
	}

	/**
	 * Returns the {@link LineString} or {@link MultiLineString} built by the
	 * sequencing process, if one exists.
	 * 
	 * @return the sequenced linestrings, or <code>null</code> if a valid
	 *         sequence does not exist
	 */
	public Geometry getSequencedLineStrings() {
		computeSequence();
		return sequencedGeometry;
	}

	private void computeSequence() {
		if (isRun) {
			return;
		}
		isRun = true;

		List sequences = findSequences();
		if (sequences == null)
			return;

		sequencedGeometry = buildSequencedGeometry(sequences);
		isSequenceable = true;

		int finalLineCount = sequencedGeometry.getNumGeometries();
		// Assert.isTrue(lineCount == finalLineCount,
		// "Lines were missing from result");
		Assert.isTrue(sequencedGeometry instanceof LineString
				|| sequencedGeometry instanceof MultiLineString,
				"Result is not lineal");
	}

	private List findSequences() {
		List sequences = new ArrayList();
		ConnectedSubgraphFinder csFinder = new ConnectedSubgraphFinder(graph);
		List subgraphs = csFinder.getConnectedSubgraphs();
		for (Iterator i = subgraphs.iterator(); i.hasNext();) {
			Subgraph subgraph = (Subgraph) i.next();
			if (hasSequence(subgraph)) {
				List seq = findSequence(subgraph);
				sequences.add(seq);
			} else {
				// if any subgraph cannot be sequenced, abort
				return null;
			}
		}
		return sequences;
	}

	/**
	 * Tests whether a complete unique path exists in a graph using Euler's
	 * Theorem.
	 * 
	 * @param graph
	 *            the subgraph containing the edges
	 * @return <code>true</code> if a sequence exists
	 */
	private boolean hasSequence(Subgraph graph) {
		int oddDegreeCount = 0;
		for (Iterator i = graph.nodeIterator(); i.hasNext();) {
			Node node = (Node) i.next();
			if (node.getDegree() % 2 == 1)
				oddDegreeCount++;
		}
		return oddDegreeCount <= 2;
	}

	private List findSequence(Subgraph graph) {
		GraphComponent.setVisited(graph.edgeIterator(), false);

		Node startNode = findLowestDegreeNode(graph);
		DirectedEdge startDE = (DirectedEdge) startNode.getOutEdges()
				.iterator().next();
		DirectedEdge startDESym = startDE.getSym();

		List seq = new LinkedList();
		ListIterator lit = seq.listIterator();
		addReverseSubpath(startDESym, lit, false);
		while (lit.hasPrevious()) {
			DirectedEdge prev = (DirectedEdge) lit.previous();
			DirectedEdge unvisitedOutDE = findUnvisitedBestOrientedDE(prev
					.getFromNode());
			if (unvisitedOutDE != null)
				addReverseSubpath(unvisitedOutDE.getSym(), lit, true);
		}

		/**
		 * At this point, we have a valid sequence of graph DirectedEdges, but
		 * it is not necessarily appropriately oriented relative to the
		 * underlying geometry.
		 */
		List orientedSeq = orient(seq);
		return orientedSeq;
	}

	/**
	 * Finds an {@link DirectedEdge} for an unvisited edge (if any), choosing
	 * the dirEdge which preserves orientation, if possible.
	 * 
	 * @param node
	 *            the node to examine
	 * @return the dirEdge found, or <code>null</code> if none were unvisited
	 */
	private static DirectedEdge findUnvisitedBestOrientedDE(Node node) {
		DirectedEdge wellOrientedDE = null;
		DirectedEdge unvisitedDE = null;
		for (Iterator i = node.getOutEdges().iterator(); i.hasNext();) {
			DirectedEdge de = (DirectedEdge) i.next();
			if (!de.getEdge().isVisited()) {
				unvisitedDE = de;
				if (de.getEdgeDirection())
					wellOrientedDE = de;
			}
		}
		if (wellOrientedDE != null)
			return wellOrientedDE;
		return unvisitedDE;
	}

	private void addReverseSubpath(DirectedEdge de, ListIterator lit,
			boolean expectedClosed) {
		// trace an unvisited path *backwards* from this de
		Node endNode = de.getToNode();

		Node fromNode = null;
		while (true) {
			lit.add(de.getSym());
			de.getEdge().setVisited(true);
			fromNode = de.getFromNode();
			DirectedEdge unvisitedOutDE = findUnvisitedBestOrientedDE(fromNode);
			// this must terminate, since we are continually marking edges as
			// visited
			if (unvisitedOutDE == null)
				break;
			de = unvisitedOutDE.getSym();
		}
		if (expectedClosed) {
			// the path should end at the toNode of this de, otherwise we have
			// an error
			Assert.isTrue(fromNode == endNode, "path not contiguous");
		}
	}

	private static Node findLowestDegreeNode(Subgraph graph) {
		int minDegree = Integer.MAX_VALUE;
		Node minDegreeNode = null;
		for (Iterator i = graph.nodeIterator(); i.hasNext();) {
			Node node = (Node) i.next();
			if (minDegreeNode == null || node.getDegree() < minDegree) {
				minDegree = node.getDegree();
				minDegreeNode = node;
			}
		}
		return minDegreeNode;
	}

	/**
	 * Computes a version of the sequence which is optimally oriented relative
	 * to the underlying geometry.
	 * <p>
	 * Heuristics used are:
	 * <ul>
	 * <li>If the path has a degree-1 node which is the start node of an
	 * linestring, use that node as the start of the sequence
	 * <li>If the path has a degree-1 node which is the end node of an
	 * linestring, use that node as the end of the sequence
	 * <li>If the sequence has no degree-1 nodes, use any node as the start
	 * (NOTE: in this case could orient the sequence according to the majority
	 * of the linestring orientations)
	 * </ul>
	 * 
	 * @param seq
	 *            a List of DirectedEdges
	 * @return a List of DirectedEdges oriented appropriately
	 */
	private List orient(List seq) {
		DirectedEdge startEdge = (DirectedEdge) seq.get(0);
		DirectedEdge endEdge = (DirectedEdge) seq.get(seq.size() - 1);
		Node startNode = startEdge.getFromNode();
		Node endNode = endEdge.getToNode();

		boolean flipSeq = false;
		boolean hasDegree1Node = startNode.getDegree() == 1
				|| endNode.getDegree() == 1;

		if (hasDegree1Node) {
			boolean hasObviousStartNode = false;

			// test end edge before start edge, to make result stable
			// (ie. if both are good starts, pick the actual start
			if (endEdge.getToNode().getDegree() == 1
					&& endEdge.getEdgeDirection() == false) {
				hasObviousStartNode = true;
				flipSeq = true;
			}
			if (startEdge.getFromNode().getDegree() == 1
					&& startEdge.getEdgeDirection() == true) {
				hasObviousStartNode = true;
				flipSeq = false;
			}

			// since there is no obvious start node, use any node of degree 1
			if (!hasObviousStartNode) {
				// check if the start node should actually be the end node
				if (startEdge.getFromNode().getDegree() == 1)
					flipSeq = true;
				// if the end node is of degree 1, it is properly the end node
			}

		}

		// if there is no degree 1 node, just use the sequence as is
		// (Could insert heuristic of taking direction of majority of lines as
		// overall direction)

		if (flipSeq)
			return reverse(seq);
		return seq;
	}

	/**
	 * Reverse the sequence. This requires reversing the order of the dirEdges,
	 * and flipping each dirEdge as well
	 * 
	 * @param seq
	 *            a List of DirectedEdges, in sequential order
	 * @return the reversed sequence
	 */
	private List reverse(List seq) {
		LinkedList newSeq = new LinkedList();
		for (Iterator i = seq.iterator(); i.hasNext();) {
			DirectedEdge de = (DirectedEdge) i.next();
			newSeq.addFirst(de.getSym());
		}
		return newSeq;
	}

	private boolean isNodeVisited(Node n, int idSeq) {
		Integer c = (Integer) n.getContext();
		return c.intValue() == idSeq;
	}

	private void setNodeVisited(Node n, int idSeq) {
		n.setContext(new Integer(idSeq));
	}

	/**
	 * Builds a geometry ({@link LineString} or {@link MultiLineString} )
	 * representing the sequence.
	 * 
	 * @param sequences
	 *            a List of Lists of DirectedEdges with LineMergeEdges as their
	 *            parent edges.
	 * @return the sequenced geometry, or <code>null</code> if no sequence
	 *         exists
	 */
	private Geometry buildSequencedGeometry(List sequences) {
		List lines = new ArrayList();

		for (Iterator i1 = sequences.iterator(); i1.hasNext();) {
			List seq = (List) i1.next();
			for (Iterator i2 = seq.iterator(); i2.hasNext();) {
				DirectedEdge de = (DirectedEdge) i2.next();
				LineMergeEdge e = (LineMergeEdge) de.getEdge();
				LineString line = e.getLine();

				LineString lineToAdd = line;
				if (!de.getEdgeDirection() && !line.isClosed())
					lineToAdd = reverse(line);

				lines.add(lineToAdd);
			}
		}
		if (lines.size() == 0)
			return factory.createMultiLineString(new LineString[0]);
		return factory.buildGeometry(lines);
	}

	/**
	 * @param sequences
	 * @return
	 */
	private Geometry buildModifiedSequencedGeometry(List sequences) {
		List lines = new ArrayList();

		for (Iterator itNodes = graph.getNodes().iterator(); itNodes.hasNext();) {
			Node n = (Node) itNodes.next();
			n.setContext(new Integer(-1));
			// n.setVisited(false);
		}

		int numSeq = -1;

		for (Iterator i1 = sequences.iterator(); i1.hasNext();) {
			List seq = (List) i1.next();
			Node node1 = null;
			Node node2 = null;
			numSeq++;
			for (Iterator i2 = seq.iterator(); i2.hasNext();) {
				DirectedEdge de = (DirectedEdge) i2.next();
				// /////////// FJP
				if (node1 == null)
					node1 = de.getFromNode();
				setNodeVisited(node1, numSeq);
				if (!isNodeVisited(de.getToNode(), numSeq)) {
					if (node1 == de.getFromNode()) {
						LineMergeEdge e = (LineMergeEdge) de.getEdge();
						LineString line = e.getLine();

						LineString lineToAdd = line;
						if (!de.getEdgeDirection() && !line.isClosed())
							lineToAdd = reverse(line);

						lines.add(lineToAdd);
					} else // Hemos saltado uno o varios nodos, así que no hay
							// lineString debajo
					{
						Coordinate[] coords = new Coordinate[2];
						coords[0] = node1.getCoordinate();
						coords[1] = de.getToNode().getCoordinate();
						LineString lineToAdd = factory.createLineString(coords);
						lines.add(lineToAdd);
					}
					node1 = de.getToNode();
					setNodeVisited(node1, numSeq);
				} // IF
			}
		}
		if (lines.size() == 0)
			return factory.createMultiLineString(new LineString[0]);
		return factory.buildGeometry(lines);
	}

	private static LineString reverse(LineString line) {
		Coordinate[] pts = line.getCoordinates();
		Coordinate[] revPts = new Coordinate[pts.length];
		int len = pts.length;
		for (int i = 0; i < len; i++) {
			revPts[len - 1 - i] = new Coordinate(pts[i]);
		}
		return line.getFactory().createLineString(revPts);
	}

}
