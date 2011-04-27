/*
 * Created on 11-sep-2006 by azabala
 *
 */
package com.vividsolutions.jts.geomgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.algorithm.BoundaryNodeRule;
import com.vividsolutions.jts.algorithms.SnapSimplePointInAreaLocator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Location;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.util.Assert;

/**
 * Repository of nodes of a PlanarGraph that applies a snap tolerance.
 * 
 * If we ask for a node, and it founds a node in the tolerance distance it
 * returns the found node.
 * 
 * 
 * 
 * @author azabala
 */
public class SnappingNodeMap extends NodeMap {

	private double snapTolerance;

	public SnappingNodeMap(NodeFactory arg0) {
		super(arg0);
	}
	
	public SnappingNodeMap(NodeFactory nodeFactory, double snapTolerance) {
		super(nodeFactory);
		this.snapTolerance = snapTolerance;
		// spatialIndex = new Quadtree();
		nodeMap = new HashMap();
	}

	// Esto va a ir muy lento
	// buscar otros mecanismos (indice espacial, o hashmap)
	protected HashMap nodeMap;

	/**
	 * A DirectedEdgeStar whose nodes are of type SnapNode
	 */
	private class SnapDirectedEdgeStar extends DirectedEdgeStar {

		/**
		 * The location of the point for this star in Geometry i Areas
		 */
		private int[] ptInAreaLocation = { Location.NONE, Location.NONE };

		private Label label;

		List resultAreaEdgeList = null;

		List getResultAreaEdges() {
			if (resultAreaEdgeList != null)
				return resultAreaEdgeList;
			resultAreaEdgeList = new ArrayList();
			for (Iterator it = iterator(); it.hasNext();) {
				DirectedEdge de = (DirectedEdge) it.next();
				if (de.isInResult() || de.getSym().isInResult())
					resultAreaEdgeList.add(de);
			}
			return resultAreaEdgeList;
		}

		private final int SCANNING_FOR_INCOMING = 1;

		private final int LINKING_TO_OUTGOING = 2;

		private SnapDirectedEdgeStar() {
			super();

			edgeList = new ArrayList();

		}

		/**
		 * Traverse the star of DirectedEdges, linking the included edges
		 * together. To link two dirEdges, the <next> pointer for an incoming
		 * dirEdge is set to the next outgoing edge.
		 * <p>
		 * DirEdges are only linked if:
		 * <ul>
		 * <li>they belong to an area (i.e. they have sides)
		 * <li>they are marked as being in the result
		 * </ul>
		 * <p>
		 * Edges are linked in CCW order (the order they are stored). This means
		 * that rings have their face on the Right (in other words, the
		 * topological location of the face is given by the RHS label of the
		 * DirectedEdge)
		 * <p>
		 * PRECONDITION: No pair of dirEdges are both marked as being in the
		 * result
		 */
		public void linkResultDirectedEdges() {
			// make sure edges are copied to resultAreaEdges list
			List resultEdges = getResultAreaEdges();
			// find first area edge (if any) to start linking at
			DirectedEdge firstOut = null;
			DirectedEdge incoming = null;
			int state = SCANNING_FOR_INCOMING;
			// link edges in CCW order
			for (int i = 0; i < resultAreaEdgeList.size(); i++) {
				DirectedEdge nextOut = (DirectedEdge) resultEdges.get(i);
				DirectedEdge nextIn = nextOut.getSym();

				// skip de's that we're not interested in
				if (!nextOut.getLabel().isArea())
					continue;

				// record first outgoing edge, in order to link the last
				// incoming edge
				if (firstOut == null && nextOut.isInResult())
					firstOut = nextOut;
				// assert: sym.isInResult() == false, since pairs of dirEdges
				// should have been removed already

				switch (state) {
				case SCANNING_FOR_INCOMING:
					if (!nextIn.isInResult())
						continue;
					incoming = nextIn;
					state = LINKING_TO_OUTGOING;
					break;
				case LINKING_TO_OUTGOING:
					if (!nextOut.isInResult())
						continue;
					incoming.setNext(nextOut);
					state = SCANNING_FOR_INCOMING;
					break;
				}
			}// for
			if (state == LINKING_TO_OUTGOING) {
				// Debug.print(firstOut == null, this);
				if (firstOut == null)
					throw new TopologyException("no outgoing dirEdge found",
							getCoordinate());
				// Assert.isTrue(firstOut != null, "no outgoing dirEdge found
				// (at " + getCoordinate() );
				Assert.isTrue(firstOut.isInResult(),
						"unable to link last incoming dirEdge");
				incoming.setNext(firstOut);
			}
		}

		/**
		 * Insert an EdgeEnd into the map, and clear the edgeList cache, since
		 * the list of edges has now changed
		 */
		protected void insertEdgeEnd(EdgeEnd e, Object obj) {
			edgeMap.put(e, obj);
			if (edgeList == null)
				edgeList = new ArrayList();
			edgeList.add(e);

			// Necesitamos que la "estrella" de Ejes asociada a un Nodo conserve
			// siempre un orden antihorario. Por eso, cada vez que se añada un
			// nuevo
			// eje solo se inserta en el Map (que mantiene el orden)
			// y la cache se pone a null

			// edgeList = null;
		}

		public Iterator iterator() {
			return getEdges().iterator();
		}

		public List getEdges() {
			Collections.sort(edgeList, new Comparator() {
				public int compare(Object arg0, Object arg1) {
					EdgeEnd e0 = (EdgeEnd) arg0;
					EdgeEnd e1 = (EdgeEnd) arg1;
					return e0.compareTo(e1);
				}
			});
			// if (edgeList == null) {
			// edgeList = new ArrayList(edgeMap.values());
			// }
			return edgeList;
		}

		public Label getLabel() {
			return label;
		}

		void computeEdgeEndLabels(BoundaryNodeRule boundaryNodeRule)
		  {
		    // Compute edge label for each EdgeEnd
		    for (Iterator it = iterator(); it.hasNext(); ) {
		      EdgeEnd ee = (EdgeEnd) it.next();
		      ee.computeLabel(boundaryNodeRule);
		    }
		  }

		void propagateSideLabels(int geomIndex) {
			// Since edges are stored in CCW order around the node,
			// As we move around the ring we move from the right to the left
			// side of the edge
			int startLoc = Location.NONE;
			// initialize loc to location of last L side (if any)
			// System.out.println("finding start location");
			for (Iterator it = iterator(); it.hasNext();) {
				EdgeEnd e = (EdgeEnd) it.next();
				Label label = e.getLabel();
				if (label.isArea(geomIndex)
						&& label.getLocation(geomIndex, Position.LEFT) != Location.NONE)
					startLoc = label.getLocation(geomIndex, Position.LEFT);
			}
			// no labelled sides found, so no labels to propagate
			if (startLoc == Location.NONE)
				return;

			int currLoc = startLoc;
			for (Iterator it = iterator(); it.hasNext();) {
				EdgeEnd e = (EdgeEnd) it.next();
				Label label = e.getLabel();
				// set null ON values to be in current location
				if (label.getLocation(geomIndex, Position.ON) == Location.NONE)
					label.setLocation(geomIndex, Position.ON, currLoc);
				// set side labels (if any)
				// if (label.isArea()) { //ORIGINAL
				if (label.isArea(geomIndex)) {
					int leftLoc = label.getLocation(geomIndex, Position.LEFT);
					int rightLoc = label.getLocation(geomIndex, Position.RIGHT);
					// if there is a right location, that is the next location
					// to propagate
					if (rightLoc != Location.NONE) {
						// Debug.print(rightLoc != currLoc, this);
						if (rightLoc != currLoc)
							throw new TopologyException(
									"side location conflict", e.getCoordinate());
						if (leftLoc == Location.NONE) {
							Assert
									.shouldNeverReachHere("found single null side (at "
											+ e.getCoordinate() + ")");
						}
						currLoc = leftLoc;
					} else {
						/**
						 * RHS is null - LHS must be null too. This must be an
						 * edge from the other geometry, which has no location
						 * labelling for this geometry. This edge must lie
						 * wholly inside or outside the other geometry (which is
						 * determined by the current location). Assign both
						 * sides to be the current location.
						 */
						Assert.isTrue(label.getLocation(geomIndex,
								Position.LEFT) == Location.NONE,
								"found single null side");
						label.setLocation(geomIndex, Position.RIGHT, currLoc);
						label.setLocation(geomIndex, Position.LEFT, currLoc);
					}
				}
			}
		}

		int getLocation(int geomIndex, Coordinate p, GeometryGraph[] geom) {
			// compute location only on demand
			if (ptInAreaLocation[geomIndex] == Location.NONE) {
				ptInAreaLocation[geomIndex] = SnapSimplePointInAreaLocator
						.locate(p, geom[geomIndex].getGeometry(), snapTolerance);
			}
			return ptInAreaLocation[geomIndex];
		}

		public void mergeSymLabels() {
			for (Iterator it = iterator(); it.hasNext();) {
				DirectedEdge de = (DirectedEdge) it.next();
				Label label = de.getLabel();
				Label symLabel = de.getSym().getLabel();
				label.merge(symLabel);
			}
		}

		/**
		 * Update incomplete dirEdge labels from the labelling for the node
		 */
		public void updateLabelling(Label nodeLabel) {
			for (Iterator it = iterator(); it.hasNext();) {
				DirectedEdge de = (DirectedEdge) it.next();
				Label label = de.getLabel();
				label.setAllLocationsIfNull(0, nodeLabel.getLocation(0));
				label.setAllLocationsIfNull(1, nodeLabel.getLocation(1));
			}
		}
		
		 public void computeLabelling(GeometryGraph[] geom)
		  {
			 computeEdgeEndLabels(geom[0].getBoundaryNodeRule());
			 propagateSideLabels(0);
			 propagateSideLabels(1); 
			 boolean[] hasDimensionalCollapseEdge = { false, false };
			    for (Iterator it = iterator(); it.hasNext(); ) {
			      EdgeEnd e = (EdgeEnd) it.next();
			      Label label = e.getLabel();
			      for (int geomi = 0; geomi < 2; geomi++) {
			        if (label.isLine(geomi) && label.getLocation(geomi) == Location.BOUNDARY)
			          hasDimensionalCollapseEdge[geomi] = true;
			      }
			    }
			    for (Iterator it = iterator(); it.hasNext(); ) {
			      EdgeEnd e = (EdgeEnd) it.next();
			      Label label = e.getLabel();
			      for (int geomi = 0; geomi < 2; geomi++) {
			        if (label.isAnyNull(geomi)) {
			          int loc = Location.NONE;
			          if (hasDimensionalCollapseEdge[geomi]) {
			            loc = Location.EXTERIOR;
			          }
			          else {
			            Coordinate p = e.getCoordinate();
			            loc = getLocation(geomi, p, geom);
			          }
			          label.setAllLocationsIfNull(geomi, loc);
			        }
			      }
			    }
		    
		    // determine the overall labelling for this DirectedEdgeStar
		    // (i.e. for the node it is based at)
		    label = new Label(Location.NONE);
		    for (Iterator it = iterator(); it.hasNext(); ) {
		      EdgeEnd ee = (EdgeEnd) it.next();
		      Edge e = ee.getEdge();
		      Label eLabel = e.getLabel();
		      for (int i = 0; i < 2; i++) {
		        int eLoc = eLabel.getLocation(i);
		        if (eLoc == Location.INTERIOR || eLoc == Location.BOUNDARY)
		          label.setLocation(i, Location.INTERIOR);
		      }
		    }
// Debug.print(this);
		  }
	}

	/**
	 * A geometry graph node that consideer equals those nodes which are at a distance
	 * lower than the snap tolerance.
	 *
	 */
	class SnapNode extends Node {

		public SnapNode(Coordinate arg0, EdgeEndStar arg1) {
			super(arg0, arg1);
		}

		public boolean equals(Object obj) {
			SnapNode other = (SnapNode) obj;
			return other.coord.distance(this.coord) <= snapTolerance;
		}

		public int hashCode() {
			return 1; // esto no es eficiente
		}
	}

	

	/**
	 * Comparator that allows to sort Coordinates  from lower to higher distance
	 * to a given coordinate.
	 *
	 */

	class MinDistCoordComparator implements Comparator {
		Coordinate coord;

		MinDistCoordComparator(Coordinate coord) {
			this.coord = coord;
		}

		public int compare(Object arg0, Object arg1) {
			Coordinate c1 = ((Node) arg0).getCoordinate();
			Coordinate c2 = ((Node) arg1).getCoordinate();

			double d1 = c1.distance(coord);
			double d2 = c2.distance(coord);

			if (d1 < d2)
				return 1;
			if (d1 > d2)
				return -1;
			else
				return 0;
		}
	}

	public Node addNode(final Coordinate coord) {
		SnapNode candidate = null;
		DirectedEdgeStar edgeStar = new SnapDirectedEdgeStar();
		candidate = new SnapNode(coord, edgeStar);

		/*
		 * FIXME
		 * PRUEBA PARA VER POR QUÉ DA ERRORES CON POLIGONOS
		 * 
		 * 
		 * 
		 */

		Node stored = (Node) nodeMap.get(candidate);
		if (stored != null)
			return stored;
		else {
			nodeMap.put(candidate, candidate);
			return candidate;
		}
		// Envelope queryRect = getQueryRect(coord);
		// List nodes = spatialIndex.query(queryRect);
		// Spatial Index está devolviendo candidatos fuera del rectangulo
		// será problema del quadtree
		// if(nodes.size() != 0){
		// Collections.sort(nodes, new MinDistCoordComparator(coord));
		// Node candidate = (Node) nodes.get(0);
		// if(candidate.getCoordinate().distance(coord) < snapTolerance )
		// return candidate;
		// }else{
		// System.out.println("El nodo "+coord.x+","+coord.y+" no tiene entradas
		// analogas en el quadtree");
		// List all = this.spatialIndex.queryAll();
		// System.out.println("en el quadtree "+all.size());
		// for(int i = 0; i < all.size(); i++){
		// Node node = (Node) all.get(i);
		// Coordinate coord2 = node.getCoordinate();
		// System.out.println(coord2.x + "," + coord2.y);
		// }
		// }
		// Node solution = nodeFactory.createNode(coord);
		// spatialIndex.insert(queryRect, solution);
		// return solution;
	}

	// FIXME: REVISAR SI HABRÍA QUE HACER UN MERGE LABEL ARRIBA O AQUÍ
	public Node addNode(Node n) {

		Node node = addNode(n.getCoordinate());
		node.mergeLabel(n);
		return node;
	}

	/**
	 * Adds a node for the start point of this EdgeEnd (if one does not already
	 * exist in this map). Adds the EdgeEnd to the (possibly new) node.
	 */
	public void add(EdgeEnd e) {
		Coordinate p = e.getCoordinate();
		Node n = addNode(p);
		n.add(e);// Si el nodo ya existe, se le añade una arista
	}

	/**
	 * @return the node if found; null otherwise
	 */
	public Node find(Coordinate coord) {
		// Envelope queryRect = getQueryRect(coord);
		// List nodes = spatialIndex.query(getQueryRect(coord));
		// Collections.sort(nodes, new MinDistCoordComparator(coord));
		// Node candidate = (Node) nodes.get(0);
		return (Node) nodeMap.get(new SnapNode(coord, null));
		// return candidate;
	}

	public Iterator iterator() {
		return nodeMap.values().iterator();
		// return spatialIndex.queryAll().iterator();
	}

	public Collection values() {
		return nodeMap.values();
		// return spatialIndex.queryAll();
	}

	public Collection getBoundaryNodes(int geomIndex) {
		Collection bdyNodes = new ArrayList();
		for (Iterator i = iterator(); i.hasNext();) {
			Node node = (Node) i.next();
			if (node.getLabel().getLocation(geomIndex) == Location.BOUNDARY)
				bdyNodes.add(node);
		}
		return bdyNodes;
	}

	public void dump() {
		System.out.println(this.toString());
		Iterator it = iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			Coordinate coord = node.getCoordinate();
			System.out.println("x= " + coord.x + ", y= " + coord.y);
		}
	}

	public static void main(String[] args) {
		SnappingNodeMap nodeMap = new SnappingNodeMap(new NodeFactory(), 0.01);
		nodeMap.addNode(new Coordinate(0.001, 0.001));
		nodeMap.addNode(new Coordinate(0.002, 0.002));
		Iterator it = nodeMap.iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			Coordinate coord = node.getCoordinate();
			System.out.println("x= " + coord.x + ", y= " + coord.y);
		}
	}
}
