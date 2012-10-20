package com.iver.cit.gvsig.graphtests;

import java.io.File;
import java.util.HashMap;

import junit.framework.TestCase;

import org.cresques.cts.ProjectionUtils;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvEdge;
import org.gvsig.graph.core.GvNode;
import org.gvsig.graph.core.IGraph;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.loaders.NetworkRedLoader;
import org.gvsig.graph.solvers.Route;
import org.gvsig.graph.solvers.ShortestPathSolverDijkstra;
import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.AsWeightedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * 
 *         The results are not the same. It seems JGraphT may have some bugs, or
 *         I did something wrong. So, I will have a look and take some ideas,
 *         but I think we cannot use it. If I have time, I will ask for help in
 *         JGraphT lists.
 */
public class TestJGraphT_BAD_SHORTEST_PATH extends TestCase {

	DataSourceFactory dsf;

	FLyrVect lyr;

	public void testJGraphT() {
		NetworkRedLoader netLoader = new NetworkRedLoader();
		netLoader.setNetFile(new File("test_files/ejes.net"));
		IGraph g = netLoader.loadNetwork();

		// Probamos la algoritmia: distancia entre nodo 1 y nodo 1000
		Network net = new Network();
		ShortestPathSolverDijkstra solver = new ShortestPathSolverDijkstra();
		net.setLayer(lyr);
		net.setGraph(g);
		solver.setNetwork(net);

		try {
			GvNode vOrig = g.getNodeByID(1);
			GvNode vEnd = g.getNodeByID(1000);

			// Primer punto
			// net.addFlag(433647.09370, 4488029, 10);
			net.addFlagToNode(vOrig.getX(), vOrig.getY(), 10);

			// Segundo punto
			// net.addFlag(437290.96875, 4481547, 10);
			net.addFlagToNode(vEnd.getX(), vEnd.getY(), 10);
			long t1 = System.currentTimeMillis();
			Route resul;
			solver.setFielStreetName("Nombre");
			resul = solver.calculateRoute();
			long t2 = System.currentTimeMillis();

			// assertEquals(dist.doubleValue(), 8887, 0);

			System.out.println("fjp dist =" + resul.getLength()
					+ " meters. msecs: " + (t2 - t1));

			t1 = System.currentTimeMillis();

			DefaultDirectedGraph<GvNode, GvEdge> jG1 = new DefaultDirectedGraph<GvNode, GvEdge>(
					GvEdge.class);
			HashMap<GvEdge, Double> weights = new HashMap<GvEdge, Double>();
			AsWeightedGraph<GvNode, GvEdge> jG = new AsWeightedGraph<GvNode, GvEdge>(
					jG1, weights);

			for (int i = 0; i < g.numVertices(); i++) {
				GvNode n = g.getNodeByID(i);
				jG.addVertex(n);
			}

			for (int i = 0; i < g.numEdges(); i++) {
				GvEdge e = g.getEdgeByID(i);
				GvNode n1 = g.getNodeByID(e.getIdNodeOrig());
				GvNode n2 = g.getNodeByID(e.getIdNodeEnd());
				jG.addEdge(n1, n2, e);
				jG.setEdgeWeight(e, e.getDistance());
			}

			t1 = System.currentTimeMillis();
			// Probamos la algoritmia: distancia entre nodo 1 y nodo 1000
			DijkstraShortestPath<GvNode, GvEdge> distCalculator = new DijkstraShortestPath<GvNode, GvEdge>(
					jG, vOrig, vEnd);

			Number dist = distCalculator.getPathLength();
			t2 = System.currentTimeMillis();

			// assertEquals(dist.doubleValue(), 8887, 0);

			System.out.println("JGraph Dijkstra dist =" + dist
					+ " meters. msecs: " + (t2 - t1));

			BellmanFordShortestPath<GvNode, GvEdge> bellman = new BellmanFordShortestPath<GvNode, GvEdge>(
					jG, vOrig);

			dist = bellman.getCost(vEnd);

			System.out
					.println("JGraph BellmanFord dist =" + dist + " meters. ");

			System.out.println(vOrig + " - " + vEnd);
			System.out.println(vOrig.getX() + ", " + vOrig.getY() + " - "
					+ vEnd.getX() + ", " + vEnd.getY());

			assertEquals(resul.getLength(), dist.doubleValue(), 0.4);

		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void setUp() throws Exception {
		// Setup de los drivers
		LayerFactory
				.setDriversPath("../_fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers");

		// Setup del factory de DataSources
		dsf = LayerFactory.getDataSourceFactory();

		CoordinateReferenceSystem crs = ProjectionUtils.getCRS("EPSG:23030");
		File shpFile = new File("test_files/ejes.shp");
		lyr = (FLyrVect) LayerFactory.createLayer("Ejes", "gvSIG shp driver",
				shpFile, crs);

	}

	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

}
