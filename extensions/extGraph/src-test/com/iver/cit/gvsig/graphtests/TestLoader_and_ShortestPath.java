package com.iver.cit.gvsig.graphtests;


import java.io.File;

import junit.framework.TestCase;

import org.cresques.cts.IProjection;
import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.EdgeWeightLabeller;
import org.gvsig.graph.core.FNode;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.IGraph;
import org.gvsig.graph.core.JungGraph;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.loaders.NetworkLoader;
import org.gvsig.graph.core.loaders.NetworkRedLoader;
import org.gvsig.graph.solvers.Route;
import org.gvsig.graph.solvers.ShortestPathSolverAStar;
import org.gvsig.graph.solvers.ShortestPathSolverDijkstra;

import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Graph;

public class TestLoader_and_ShortestPath extends TestCase {
	DataSourceFactory dsf;

	FLyrVect lyr;

	public void testLoadRedNetwork() {
		NetworkRedLoader netLoader = new NetworkRedLoader();
		netLoader.setNetFile(new File("test_files/ejes.net"));
		IGraph g = netLoader.loadNetwork();

		// Probamos la algoritmia: distancia entre nodo 1 y nodo 1000
		Network net = new Network();
		ShortestPathSolverDijkstra solver = new ShortestPathSolverDijkstra();
		net.setLayer(lyr);
		net.setGraph(g);
		solver.setNetwork(net);
		
		ShortestPathSolverAStar solverAstar = new ShortestPathSolverAStar();
		solverAstar.setNetwork(net);
		
		try {

			// Primer punto
			net.addFlag(433647.09370, 4488029, 10);

			// Segundo punto
			net.addFlag(437290.96875, 4481547, 10);
			long t1 = System.currentTimeMillis();
			Route resul;
			solver.setFielStreetName("Nombre");
			resul = solver.calculateRoute();
			long t2 = System.currentTimeMillis();

			// assertEquals(dist.doubleValue(), 8887, 0);

			System.out.println("fjp dist =" + resul.getLength()
					+ " meters. msecs: " + (t2 - t1));

			t1 = System.currentTimeMillis();
			Route resul2;
			solverAstar.setFielStreetName("Nombre");
			resul2 = solverAstar.calculateRoute();
			t2 = System.currentTimeMillis();

			System.out.println("ASTAR dist =" + resul2.getLength()
					+ " meters. msecs: " + (t2 - t1));
			
			assertEquals(resul.getLength(), resul2.getLength(), 0.0);
			
			JungGraph jG = new JungGraph(g);
			// Probamos la algoritmia: distancia entre nodo 1 y nodo 1000
			DijkstraShortestPath distCalculator = new DijkstraShortestPath(jG,
					new EdgeWeightLabeller());
			FNode vOrig = jG.getVertexByID(1);
			FNode vEnd = jG.getVertexByID(1000);
			t1 = System.currentTimeMillis();
			Number dist = distCalculator.getDistance(vOrig, vEnd);
			t2 = System.currentTimeMillis();

			// assertEquals(dist.doubleValue(), 8887, 0);

			System.out
					.println("dist =" + dist + " meters. msecs: " + (t2 - t1));

			System.out.println(vOrig + " - " + vEnd);
			System.out.println(vOrig.getX() + ", " + vOrig.getY() + " - "
					+ vEnd.getX() + ", " + vEnd.getY());
			
			assertEquals(resul.getLength(), dist.doubleValue(), 0.4);
			
//			net.addTurnCost(idArcOrigin, idArcDestination, newCost);

		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * Test method for
	 * 'org.gvsig.graph.core.NetworkLoader.loadNetwork()'
	 */
	public void DONT_TEST_testLoadNetwork() {
		// if (true) return;
		NetworkLoader netLoader = new NetworkLoader(true);

		DataSource dsNodes;
		try {
			dsNodes = dsf.createRandomDataSource("nodes",
					DataSourceFactory.MANUAL_OPENING);

			DataSource dsEdges = dsf.createRandomDataSource("edges",
					DataSourceFactory.MANUAL_OPENING);

			SelectableDataSource sdsNodes = new SelectableDataSource(dsNodes);
			SelectableDataSource sdsEdges = new SelectableDataSource(dsEdges);

			netLoader.setNodeReader(sdsNodes);
			netLoader.setEdgeReader(sdsEdges);

			Graph g = netLoader.loadJungNetwork();

			System.out.println("Num nodos=" + g.numVertices() + " numEdges = "
					+ g.numEdges());

			// Probamos la algoritmia: distancia entre nodo 1 y nodo 1000
			DijkstraDistance distCalculator = new DijkstraDistance(g,
					new EdgeWeightLabeller());
			ArchetypeVertex vOrig = netLoader.getIndexer().getVertex(1);
			ArchetypeVertex vEnd = netLoader.getIndexer().getVertex(1000);
			long t1 = System.currentTimeMillis();
			Number dist = distCalculator.getDistance(vOrig, vEnd);
			long t2 = System.currentTimeMillis();

			// assertEquals(dist.doubleValue(), 8887, 0);

			System.out
					.println("dist =" + dist + " meters. msecs: " + (t2 - t1));
			System.out.println(vOrig + " - " + vEnd);
			System.out.println(vOrig.getUserDatum("X") + ", "
					+ vOrig.getUserDatum("Y") + " - " + vEnd.getUserDatum("X")
					+ ", " + vEnd.getUserDatum("Y"));

		} catch (BaseException e) {
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

		// Setup de las tablas
		dsf.addFileDataSource("gdbms dbf driver", "nodes", "c:/nodes.dbf");
		dsf.addFileDataSource("gdbms dbf driver", "edges", "c:/edges.dbf");

		IProjection prj = CRSFactory.getCRS("EPSG:23030");
		File shpFile = new File("test_files/ejes.shp");
		lyr = (FLyrVect) LayerFactory.createLayer("Ejes", "gvSIG shp driver",
				shpFile, prj);

	}

	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

}
