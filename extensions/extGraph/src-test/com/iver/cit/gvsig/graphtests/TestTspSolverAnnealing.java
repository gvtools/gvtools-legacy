package com.iver.cit.gvsig.graphtests;

import java.io.File;

import junit.framework.TestCase;

import org.cresques.cts.ProjectionUtils;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.IGraph;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.loaders.NetworkRedLoader;
import org.gvsig.graph.solvers.OneToManySolver;
import org.gvsig.graph.solvers.TspSolverAnnealing;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

public class TestTspSolverAnnealing extends TestCase {

	FLyrVect lyr;
	Network net;
	IGraph g;

	protected void setUp() throws Exception {
		super.setUp();
		// Setup de los drivers
		LayerFactory
				.setDriversPath("../_fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers");

		// Setup del factory de DataSources

		CoordinateReferenceSystem crs = ProjectionUtils.getCRS("EPSG:23030");
		File shpFile = new File("test_files/ejes.shp");
		lyr = (FLyrVect) LayerFactory.createLayer("Ejes", "gvSIG shp driver",
				shpFile, crs);

		NetworkRedLoader netLoader = new NetworkRedLoader();
		netLoader.setNetFile(new File("test_files/ejes.net"));
		g = netLoader.loadNetwork();

		net = new Network();

	}

	public void testCalculate() throws GraphException {
		OneToManySolver solver = new OneToManySolver();
		net.setLayer(lyr);
		net.setGraph(g);
		solver.setNetwork(net);

		// // Source flag
		// GvFlag sourceFlag = net.createFlag(441901, 4475977, 10);
		// solver.setSourceFlag(sourceFlag);

		// Destination flags
		net.addFlag(441901, 4475977, 10);

		net.addFlag(442673, 4475125, 200);
		net.addFlag(442830, 4476201, 200);

		net.addFlag(442773, 4475225, 200);

		// net.addFlag(442730, 4476101, 200);
		//
		// net.addFlag(442700, 447650, 200);
		//
		// net.addFlag(442873, 4475225, 200);

		solver.putDestinationsOnNetwork(net.getFlags());

		GvFlag[] flags = net.getFlags();

		double[][] odMatrix = new double[flags.length][flags.length];

		for (int i = 0; i < flags.length; i++) {

			solver.setSourceFlag(flags[i]);
			long t1 = System.currentTimeMillis();

			solver.calculate();
			long t2 = System.currentTimeMillis();
			System.out.println("Punto " + i + " de " + flags.length + ". "
					+ (t2 - t1) + " msecs.");

			for (int j = 0; j < flags.length; j++) {
				long secs = Math.round(flags[j].getCost());
				// long meters = Math.round(flags[j].getAccumulatedLength());
				// String strAux = i + "\t" + j + "\t" + secs + "\t" + meters;
				odMatrix[i][j] = flags[j].getCost();
			}

		}

		solver.removeDestinationsFromNetwork(net.getFlags());

		for (int i = 0; i < flags.length; i++) {
			System.out.println("Flag " + i + " " + flags[i].getCost());
		}

		TspSolverAnnealing solverAnnealing = new TspSolverAnnealing();

		solverAnnealing.setStops(flags);
		solverAnnealing.setODMatrix(odMatrix);

		GvFlag[] orderedFlags = solverAnnealing.calculate();

	}

}
