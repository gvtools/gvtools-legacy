/*
 * Created on 12-sep-2006 by azabala
 *
 */
package com.vividsolutions.jts.operation.overlay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.algorithm.LineIntersector;
import com.vividsolutions.jts.algorithms.SnapPointLocator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Location;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geomgraph.Depth;
import com.vividsolutions.jts.geomgraph.DirectedEdge;
import com.vividsolutions.jts.geomgraph.DirectedEdgeStar;
import com.vividsolutions.jts.geomgraph.Edge;
import com.vividsolutions.jts.geomgraph.Label;
import com.vividsolutions.jts.geomgraph.Node;
import com.vividsolutions.jts.geomgraph.PlanarGraph;
import com.vividsolutions.jts.geomgraph.Position;
import com.vividsolutions.jts.geomgraph.SnapEdgeList;
import com.vividsolutions.jts.geomgraph.SnappingGeometryGraph;
import com.vividsolutions.jts.geomgraph.SnappingPlanarGraph;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.util.Assert;

/**
 * TODO El codigo esta lleno de coordinateA.distance(coordinateB) <=
 * snapTolerance;
 * 
 * CAMBIAR POR SnapCoordinate, de forma que equals haga esta comprobacin
 * 
 * 
 */
public class SnappingOverlayOperation extends OverlayOp {

	protected final SnapPointLocator ptLocator = new SnapPointLocator();
	protected GeometryFactory geomFact;
	protected Geometry resultGeom;

	protected LineIntersector li;

	protected double snapTolerance;

	/* Planar graph of the overlay operation */
	protected SnappingPlanarGraph graph;

	/* Geometry graph of each individual geometry */
	protected SnappingGeometryGraph[] arg;

	/*
	 * It saves all the new edges resulting from intersections of edges of
	 * geometries A and B. It is a temporal repository, before to save them in
	 * SnappingPlanarGraph
	 */
	protected SnapEdgeList edgeList = null;

	/*
	 * El resultado de una operacion de overlay puede contener puntos, lineas y
	 * poligonos.
	 */
	protected List resultPolyList = new ArrayList();

	protected List resultLineList = new ArrayList();

	protected List resultPointList = new ArrayList();

	public static Geometry overlayOp(Geometry geom0, Geometry geom1,
			int opCode, double tolerance) {
		SnappingOverlayOperation gov = new SnappingOverlayOperation(geom0,
				geom1, tolerance);
		Geometry geomOv = gov.getResultGeometry(opCode);
		return geomOv;
	}

	public SnappingOverlayOperation(Geometry g0, Geometry g1, double tolerance) {
		super(g0, g1);
		graph = new SnappingPlanarGraph(new OverlayNodeFactory(), tolerance);
		arg = new SnappingGeometryGraph[2];
		arg[0] = new SnappingGeometryGraph(tolerance, 0, g0);
		arg[1] = new SnappingGeometryGraph(tolerance, 1, g1);
		geomFact = g0.getFactory();
		li = new SnapLineIntersector(tolerance);
		edgeList = new SnapEdgeList(tolerance);
		this.snapTolerance = tolerance;
	}

	public Geometry getResultGeometry(int funcCode) {
		computeOverlay(funcCode);
		return resultGeom;
	}

	public PlanarGraph getGraph() {
		return graph;
	}

	private boolean isReused = false;

	/**
	 * Metodo de utilidad cuando vayamos a intersectar la geometria g1 con
	 * varias geometrias (g2, g3, g4, .... , etc.)
	 * 
	 * Los calculos basicos no se repiten para g1
	 * 
	 * */

	public void setSecondGeometry(Geometry geometry) {
		Geometry g0 = arg[0].getGeometry();
		if (g0.getPrecisionModel().compareTo(geometry.getPrecisionModel()) >= 0)
			setComputationPrecision(g0.getPrecisionModel());
		else
			setComputationPrecision(geometry.getPrecisionModel());
		graph = new SnappingPlanarGraph(new OverlayNodeFactory(), snapTolerance);

		/*
		 * TODODeberiamos borrar todos los nodos del GeometryGraph,o solo las
		 * intersecciones?????De todos modos, aunque borrasemos los nodos,
		 * getBoundaryNodesdevolveria nodos cacheados
		 * 
		 * TODO REVISAR
		 */
		arg[0].clearIntersections();

		arg[1] = new SnappingGeometryGraph(snapTolerance, 1, geometry);
		geomFact = g0.getFactory();
		edgeList = new SnapEdgeList(snapTolerance);

		resultPolyList.clear();
		resultLineList.clear();
		resultPointList.clear();

		resultGeom = null;

		isReused = true;
	}

	/*
	 * ************************************************************
	 * METODO PRINCIPAL
	 * ************************************************************
	 */
	private void computeOverlay(int opCode) {

		/*
		 * Se copian los NODOS de las dos geometrias. ESTO ES IMPORTANTE, PUES:
		 * a) un punto origina un nodo. b) una linea origina dos nodos c) un
		 * poligono origina un nodo.
		 */
		copyPoints(0);
		copyPoints(1);
		if (!isReused) // esto solo se hace si no se ha calculado ya
			arg[0].computeSelfNodes(li, false);
		arg[1].computeSelfNodes(li, false);

		/*
		 * Calcula las intersecciones. Se supone que daran lugar a Nodes, ¿NO?
		 * Como resultado, cada Edge guardará en sus EdgeIntersectionList las
		 * intersecciones que hay en sus segmentos (EdgeIntersection).
		 * 
		 * Estas intersecciones se representan por: -segmento del edge en que
		 * ocurren. -coordenada -distancia al primer vertice del segmento
		 * 
		 * ¡OJO¡ COMO RESULTADO DE ESTO NO SE GENERAN EJES NUEVOS. PARA HACER
		 * SNAP EN LAS INTERSECCIONES TENDRIAMOS QUE RETOCAR LA CLASE
		 * EDGEINTERSECTIONLIST
		 */
		arg[0].computeEdgeIntersections(arg[1], li, true);
		/*
		 * Ahora lo que se hace es: para cada Edge del grafo, se parte (en
		 * función de sus intersecciones) y se añaden los Edges fragmentados a
		 * la colección baseSplitEdges
		 */

		List baseSplitEdges = new ArrayList();
		arg[0].computeSplitEdges(baseSplitEdges);
		// TODO Quizas la clave esté tambien en la 2ª geometria
		arg[1].computeSplitEdges(baseSplitEdges);

		/*
		 * Edges resulting of A intersection B, that are in baseSplitEdges
		 * Collection, are saved in EdgeList. ¡OJO¡ Si aparecen ejes repetidos,
		 * no se duplican (pero si que se cambia su etiqueta)
		 */
		// Se copian los nuevos Edges generados en EdgeList
		insertUniqueEdges(baseSplitEdges);

		/* Se etiquetan */
		computeLabelsFromDepths();

		/*
		 * Quita los Edges que hayan sufrido colapso dimensional (en la
		 * documentacíon de JTS viene algo de esto)
		 */
		replaceCollapsedEdges();

		/*
		 * Finalmente, se añade al SnappingPlanarGraph resultado los Edges
		 * calculados como fruto de las intersecciones (contenidos en EdgeList).
		 * 
		 * Aquí se hace algo muy importante también: se añaden nuevos nodos al
		 * grafo (correspondientes con los extremos de los nuevos Edge que no
		 * estuvieran ya en el grafo)
		 */
		graph.addEdges(edgeList.getEdges());

		computeLabelling();
		labelIncompleteNodes();

		/**
		 * The ordering of building the result Geometries is important. Areas
		 * must be built before lines, which must be built before points. This
		 * is so that lines which are covered by areas are not included
		 * explicitly, and similarly for points.
		 */
		findResultAreaEdges(opCode);
		cancelDuplicateResultEdges();

		// TODO Todos los builders deberán usar los metodos snap de locator
		SnapPolygonBuilder polyBuilder = new SnapPolygonBuilder(geomFact);
		polyBuilder.add(graph);
		resultPolyList = polyBuilder.getPolygons();

		LineBuilder lineBuilder = new LineBuilder(this, geomFact, ptLocator);
		resultLineList = lineBuilder.build(opCode);

		PointBuilder pointBuilder = new PointBuilder(this, geomFact, ptLocator) {
			public List build(int opCode) {
				for (Iterator nodeit = getGraph().getNodes().iterator(); nodeit
						.hasNext();) {
					Node n = (Node) nodeit.next();

					// filter out nodes which are known to be in the result
					if (n.isInResult())
						continue;
					// if an incident edge is in the result, then the node
					// coordinate is included already
					if (n.isIncidentEdgeInResult())
						continue;
					if (n.getEdges().getDegree() == 0
							|| opCode == OverlayOp.INTERSECTION) {

						/**
						 * For nodes on edges, only INTERSECTION can result in
						 * edge nodes being included even if none of their
						 * incident edges are included
						 */
						Label label = n.getLabel();
						if (SnappingOverlayOperation.checkLabelLocation(
								label.getLocation(0), label.getLocation(1),
								opCode)) {
							filterCoveredNodeToPoint(n);
						}
					}
				}
				return resultPointList;
			}

			private void filterCoveredNodeToPoint(Node n) {
				Coordinate coord = n.getCoordinate();
				if (!isCoveredByLA(coord)) {
					Point pt = geomFact.createPoint(coord);
					resultPointList.add(pt);
				}
			}
		};
		resultPointList = pointBuilder.build(opCode);

		// gather the results from all calculations into a single Geometry for
		// the result set
		resultGeom = computeGeometry(resultPointList, resultLineList,
				resultPolyList);
	}

	/**
	 * This method will handle arguments of Location.NONE correctly
	 * 
	 * @return true if the locations correspond to the opCode
	 */
	public static boolean isResultOfOp(int loc0, int loc1, int opCode) {
		if (loc0 == Location.BOUNDARY)
			loc0 = Location.INTERIOR;
		if (loc1 == Location.BOUNDARY)
			loc1 = Location.INTERIOR;
		switch (opCode) {
		case INTERSECTION:
			return loc0 == Location.INTERIOR && loc1 == Location.INTERIOR;
		case UNION:
			return loc0 == Location.INTERIOR || loc1 == Location.INTERIOR;
		case DIFFERENCE:
			return loc0 == Location.INTERIOR && loc1 != Location.INTERIOR;
		case SYMDIFFERENCE:
			return (loc0 == Location.INTERIOR && loc1 != Location.INTERIOR)
					|| (loc0 != Location.INTERIOR && loc1 == Location.INTERIOR);
		}
		return false;
	}

	public static boolean isResultOfOp(Label label, int opCode) {
		int loc0 = label.getLocation(0);
		int loc1 = label.getLocation(1);
		return isResultOfOp(loc0, loc1, opCode);
	}

	// TODO Quitar esto de aqui

	public static boolean checkLabelLocation(int loc0, int loc1, int opCode) {
		if (loc0 == Location.BOUNDARY)
			loc0 = Location.INTERIOR;
		if (loc1 == Location.BOUNDARY)
			loc1 = Location.INTERIOR;
		switch (opCode) {
		case INTERSECTION:
			return loc0 == Location.INTERIOR && loc1 == Location.INTERIOR;
		case UNION:
			return loc0 == Location.INTERIOR || loc1 == Location.INTERIOR;
		case DIFFERENCE:
			return loc0 == Location.INTERIOR && loc1 != Location.INTERIOR;
		case SYMDIFFERENCE:
			return (loc0 == Location.INTERIOR && loc1 != Location.INTERIOR)
					|| (loc0 != Location.INTERIOR && loc1 == Location.INTERIOR);
		}
		return false;
	}

	private void insertUniqueEdges(List edges) {
		for (Iterator i = edges.iterator(); i.hasNext();) {
			Edge e = (Edge) i.next();
			insertUniqueEdge(e);
		}
	}

	/**
	 * Insert an edge from one of the noded input graphs. Checks edges that are
	 * inserted to see if an identical edge already exists. If so, the edge is
	 * not inserted, but its label is merged with the existing edge.
	 */
	protected void insertUniqueEdge(Edge e) {

		// TODO Crear una clase SnapEdge y SnapEdgeList puede ser necesario???
		// creo que si pq SnapEdgeList mantiene una cache que no considera snap
		Edge existingEdge = edgeList.findEqualEdge(e);
		// If an identical edge already exists, simply update its label
		if (existingEdge != null) {
			Label existingLabel = existingEdge.getLabel();
			Label labelToMerge = e.getLabel();

			// check if new edge is in reverse direction to existing edge
			// if so, must flip the label before merging it
			if (!existingEdge.isPointwiseEqual(e)) {
				labelToMerge = new Label(e.getLabel());
				labelToMerge.flip();
			}
			Depth depth = existingEdge.getDepth();
			// if this is the first duplicate found for this edge, initialize
			// the depths
			if (depth.isNull()) {
				depth.add(existingLabel);
			}
			depth.add(labelToMerge);
			existingLabel.merge(labelToMerge);
		} else { // no matching existing edge was found
			// add this new edge to the list of edges in this graph
			edgeList.add(e);
		}
	}

	/**
	 * Update the labels for edges according to their depths. For each edge, the
	 * depths are first normalized. Then, if the depths for the edge are equal,
	 * this edge must have collapsed into a line edge. If the depths are not
	 * equal, update the label with the locations corresponding to the depths
	 * (i.e. a depth of 0 corresponds to a Location of EXTERIOR, a depth of 1
	 * corresponds to INTERIOR)
	 */
	private void computeLabelsFromDepths() {
		for (Iterator it = edgeList.iterator(); it.hasNext();) {
			Edge e = (Edge) it.next();
			Label lbl = e.getLabel();
			Depth depth = e.getDepth();
			/*
			 * Only check edges for which there were duplicates, since these are
			 * the only ones which might be the result of dimensional collapses.
			 */
			if (!depth.isNull()) {
				depth.normalize();
				for (int i = 0; i < 2; i++) {
					if (!lbl.isNull(i) && lbl.isArea() && !depth.isNull(i)) {
						/**
						 * if the depths are equal, this edge is the result of
						 * the dimensional collapse of two or more edges. It has
						 * the same location on both sides of the edge, so it
						 * has collapsed to a line.
						 */
						if (depth.getDelta(i) == 0) {
							lbl.toLine(i);
						} else {
							/**
							 * This edge may be the result of a dimensional
							 * collapse, but it still has different locations on
							 * both sides. The label of the edge must be updated
							 * to reflect the resultant side locations indicated
							 * by the depth values.
							 */
							Assert.isTrue(!depth.isNull(i, Position.LEFT),
									"depth of LEFT side has not been initialized");
							lbl.setLocation(i, Position.LEFT,
									depth.getLocation(i, Position.LEFT));
							Assert.isTrue(!depth.isNull(i, Position.RIGHT),
									"depth of RIGHT side has not been initialized");
							lbl.setLocation(i, Position.RIGHT,
									depth.getLocation(i, Position.RIGHT));
						}
					}
				}
			}
		}
	}

	/**
	 * If edges which have undergone dimensional collapse are found, replace
	 * them with a new edge which is a L edge
	 */
	private void replaceCollapsedEdges() {
		List newEdges = new ArrayList();
		for (Iterator it = edgeList.iterator(); it.hasNext();) {
			Edge e = (Edge) it.next();
			if (e.isCollapsed()) {
				// Debug.print(e);
				it.remove();
				newEdges.add(e.getCollapsedEdge());
			}
		}
		edgeList.addAll(newEdges);
	}

	/**
	 * Copy all nodes from an arg geometry into this graph. The node label in
	 * the arg geometry overrides any previously computed label for that
	 * argIndex. (E.g. a node may be an intersection node with a previously
	 * computed label of BOUNDARY, but in the original arg Geometry it is
	 * actually in the interior due to the Boundary Determination Rule)
	 */
	private void copyPoints(int argIndex) {
		for (Iterator i = arg[argIndex].getNodeIterator(); i.hasNext();) {
			Node graphNode = (Node) i.next();
			Node newNode = graph.addNode(graphNode.getCoordinate());
			newNode.setLabel(argIndex,
					graphNode.getLabel().getLocation(argIndex));
		}
	}

	/**
	 * Compute initial labelling for all DirectedEdges at each node. In this
	 * step, DirectedEdges will acquire a complete labelling (i.e. one with
	 * labels for both Geometries) only if they are incident on a node which has
	 * edges for both Geometries
	 */
	private void computeLabelling() {
		for (Iterator nodeit = graph.getNodes().iterator(); nodeit.hasNext();) {
			Node node = (Node) nodeit.next();
			node.getEdges().computeLabelling(arg);
		}
		mergeSymLabels();
		updateNodeLabelling();

	}

	/**
	 * For nodes which have edges from only one Geometry incident on them, the
	 * previous step will have left their dirEdges with no labelling for the
	 * other Geometry. However, the sym dirEdge may have a labelling for the
	 * other Geometry, so merge the two labels.
	 */
	private void mergeSymLabels() {
		for (Iterator nodeit = graph.getNodes().iterator(); nodeit.hasNext();) {
			Node node = (Node) nodeit.next();
			((DirectedEdgeStar) node.getEdges()).mergeSymLabels();
		}
	}

	private void updateNodeLabelling() {
		// update the labels for nodes
		// The label for a node is updated from the edges incident on it
		// (Note that a node may have already been labelled
		// because it is a point in one of the input geometries)
		for (Iterator nodeit = graph.getNodes().iterator(); nodeit.hasNext();) {
			Node node = (Node) nodeit.next();
			Label lbl = ((DirectedEdgeStar) node.getEdges()).getLabel();
			Label otherLbl = node.getLabel();
			otherLbl.merge(lbl);
		}
	}

	/**
	 * Incomplete nodes are nodes whose labels are incomplete. (e.g. the
	 * location for one Geometry is null). These are either isolated nodes, or
	 * nodes which have edges from only a single Geometry incident on them.
	 * 
	 * Isolated nodes are found because nodes in one graph which don't intersect
	 * nodes in the other are not completely labelled by the initial process of
	 * adding nodes to the nodeList. To complete the labelling we need to check
	 * for nodes that lie in the interior of edges, and in the interior of
	 * areas.
	 * <p>
	 * When each node labelling is completed, the labelling of the incident
	 * edges is updated, to complete their labelling as well.
	 */
	private void labelIncompleteNodes() {
		for (Iterator ni = graph.getNodes().iterator(); ni.hasNext();) {
			Node n = (Node) ni.next();
			Label label = n.getLabel();
			if (n.isIsolated()) {
				if (label.isNull(0))
					labelIncompleteNode(n, 0);
				else
					labelIncompleteNode(n, 1);
			}
			// now update the labelling for the DirectedEdges incident on this
			// node
			((DirectedEdgeStar) n.getEdges()).updateLabelling(label);
		}
	}

	/**
	 * Label an isolated node with its relationship to the target geometry.
	 */
	private void labelIncompleteNode(Node n, int targetIndex) {
		// TODO Ver si el pointLocator deberia snapear
		Coordinate coord = n.getCoordinate();
		Geometry geom = arg[targetIndex].getGeometry();
		int loc = ptLocator.locate(coord, geom, snapTolerance);
		n.getLabel().setLocation(targetIndex, loc);
	}

	/**
	 * Find all edges whose label indicates that they are in the result area(s),
	 * according to the operation being performed. Since we want polygon shells
	 * to be oriented CW, choose dirEdges with the interior of the result on the
	 * RHS. Mark them as being in the result. Interior Area edges are the result
	 * of dimensional collapses. They do not form part of the result area
	 * boundary.
	 */
	private void findResultAreaEdges(int opCode) {
		for (Iterator it = graph.getEdgeEnds().iterator(); it.hasNext();) {
			DirectedEdge de = (DirectedEdge) it.next();
			// mark all dirEdges with the appropriate label
			Label label = de.getLabel();
			if (label.isArea()
					&& !de.isInteriorAreaEdge()
					&& isResultOfOp(label.getLocation(0, Position.RIGHT),
							label.getLocation(1, Position.RIGHT), opCode)) {
				de.setInResult(true);
				// Debug.print("in result "); Debug.println(de);
			}
		}
	}

	/**
	 * If both a dirEdge and its sym are marked as being in the result, cancel
	 * them out.
	 */
	private void cancelDuplicateResultEdges() {
		// remove any dirEdges whose sym is also included
		// (they "cancel each other out")
		for (Iterator it = graph.getEdgeEnds().iterator(); it.hasNext();) {
			DirectedEdge de = (DirectedEdge) it.next();
			DirectedEdge sym = de.getSym();
			if (de.isInResult() && sym.isInResult()) {
				de.setInResult(false);
				sym.setInResult(false);
				// Debug.print("cancelled "); Debug.println(de);
				// Debug.println(sym);
			}
		}
	}

	/**
	 * This method is used to decide if a point node should be included in the
	 * result or not.
	 * 
	 * @return true if the coord point is covered by a result Line or Area
	 *         geometry
	 */
	public boolean isCoveredByLA(Coordinate coord) {
		if (isCovered(coord, resultLineList))
			return true;
		if (isCovered(coord, resultPolyList))
			return true;
		return false;
	}

	/**
	 * This method is used to decide if an L edge should be included in the
	 * result or not.
	 * 
	 * @return true if the coord point is covered by a result Area geometry
	 */
	public boolean isCoveredByA(Coordinate coord) {
		if (isCovered(coord, resultPolyList))
			return true;
		return false;
	}

	/**
	 * @return true if the coord is located in the interior or boundary of a
	 *         geometry in the list.
	 */
	private boolean isCovered(Coordinate coord, List geomList) {
		for (Iterator it = geomList.iterator(); it.hasNext();) {
			Geometry geom = (Geometry) it.next();
			int loc = ptLocator.locate(coord, geom, snapTolerance);
			if (loc != Location.EXTERIOR)
				return true;
		}
		return false;
	}

	private Geometry computeGeometry(List resultPointList, List resultLineList,
			List resultPolyList) {
		List geomList = new ArrayList();
		// element geometries of the result are always in the order P,L,A
		geomList.addAll(resultPointList);
		geomList.addAll(resultLineList);
		geomList.addAll(resultPolyList);
		// build the most specific geometry possible
		return geomFact.buildGeometry(geomList);
	}

	public static void main(String[] args) {
		GeometryFactory factory = new GeometryFactory();
		com.vividsolutions.jts.io.WKTReader reader = new com.vividsolutions.jts.io.WKTReader(
				factory);
		Geometry a, b, c, d;
		try {

			// Snap en un extremo y en un vertice intermedio
			a = reader.read("LINESTRING(0.001 0.001, 5.001 5.001)");
			b = reader
					.read("LINESTRING(2.1 -3, 0.0 -0.001, -2.22 4.88, 10.0 10.0, 5.002 5.002)");
			System.out.println(SnappingOverlayOperation.overlayOp(a, b,
					OverlayOp.INTERSECTION, 0.01));
			// //snap mediante líneas paralelas
			c = reader.read("LINESTRING(0 0, 5 0, 10 0.001)");
			d = reader
					.read("LINESTRING(0.001 0.01, 5.001 0.002, 10.001 0.002)");
			long t0 = System.currentTimeMillis();
			System.out.println(SnappingOverlayOperation.overlayOp(c, d,
					OverlayOp.INTERSECTION, 0.1));
			long t1 = System.currentTimeMillis();
			System.out.println(OverlayOp
					.overlayOp(c, d, OverlayOp.INTERSECTION));
			long t2 = System.currentTimeMillis();
			System.out.println("Con snap: " + (t1 - t0) + " ms");
			System.out.println("Sin snap: " + (t2 - t1) + " ms");

			d = reader.read("LINESTRING(0 0, 5 0, 10 0.001)");
			System.out.println(OverlayOp
					.overlayOp(c, d, OverlayOp.INTERSECTION));

			// lineas paralelas a una distancia superior a la de snap
			// (para comprobar el criterio de paralelismo en LineIntersector
			c = reader.read("LINESTRING(0 0, 5 0, 10 0.001)");
			d = reader.read("LINESTRING(0 0.11, 5 0.12, 10 0.14)");
			System.out.println(SnappingOverlayOperation.overlayOp(c, d,
					OverlayOp.INTERSECTION, 0.001));
			//
			c = reader.read("LINESTRING(1 0, 3 2)");
			d = reader.read("LINESTRING(3.05 2.01, 5 1.25, 0.25 1.75)");
			System.out.println(OverlayOp
					.overlayOp(c, d, OverlayOp.INTERSECTION));
			System.out.println((SnappingOverlayOperation.overlayOp(c, d,
					OverlayOp.INTERSECTION, 0.1)));
			//
			//
			d = reader.read("LINESTRING(3 2, 5 1.25, 0.25 1.75)");
			System.out.println(OverlayOp
					.overlayOp(c, d, OverlayOp.INTERSECTION));
			System.out.println((SnappingOverlayOperation.overlayOp(c, d,
					OverlayOp.INTERSECTION, 0.1)));

			// Que un polígono esté cerrado o no no es snap, sino regla
			// topologica

			// TODO CON POLIGONOS ESTÁ DANDO PROBLEMAS. HABRA QUE REVISAR LINEAS
			// Y POLIGONOS
			// c = reader.read("POLYGON((0 0, 0 5, 5 5, 5 0,  0 0))");
			// d = reader.read("POLYGON((-0.01 0, 3 8, 6 6 ,  -0.01 0))");

			c = reader.read("POLYGON((5 0, 5 5, 10 5, 10 0,  5 0))");
			d = reader.read("POLYGON((4 3, 4.99 3.5, 10.01 3.5, 12 3,  4 3))");

			// REVISIÓN TOPOLOGICA
			/*
			 * Un aspecto esencial de la topologia en JTS es el etiquetado. Todo
			 * Eje del grafo asociado a un polígono tiene una etiqueta o Label,
			 * con tres valores posibles para la izquierda, derecha y encima del
			 * poligono (EXTERIOR, BOUNDARY, INTERIOR)
			 * 
			 * Por tanto, si la orientación no es la correcta, todo se va al
			 * traste (pues las cosas se invierten especularmente)
			 */
			if (CGAlgorithms.isCCW(c.getCoordinates())) {
				System.out
						.println("Anillo exterior de poligono en orden incorrecto");
				System.out.println(c.toText());
				System.exit(-2);
			}
			if (CGAlgorithms.isCCW(d.getCoordinates())) {
				System.out
						.println("Anillo exterior de poligono en orden incorrecto");
				System.out.println(d.toText());
				System.exit(-2);
			}

			System.out.println((SnappingOverlayOperation.overlayOp(c, d,
					OverlayOp.INTERSECTION, 0.1)));

			Geometry pol1 = reader
					.read("POLYGON((0 0, -5 0, -10 5, 0 10,  10 5, 5 0, 0 0))");
			Geometry pol2 = reader
					.read("POLYGON((10.01 0, 5 5, 5 10, 10 10, 10.01 0))");

			System.out.println((SnappingOverlayOperation.overlayOp(pol1, pol2,
					OverlayOp.INTERSECTION, 0.1)));
			System.out.println((OverlayOp.overlayOp(pol1, pol2,
					OverlayOp.INTERSECTION)));

		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

}