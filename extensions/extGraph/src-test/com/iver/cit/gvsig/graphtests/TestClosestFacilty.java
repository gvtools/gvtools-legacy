package com.iver.cit.gvsig.graphtests;

import java.io.File;

import junit.framework.TestCase;

import org.cresques.cts.ProjectionUtils;
import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.IGraph;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.loaders.NetworkRedLoader;
import org.gvsig.graph.solvers.OneToManySolver;
import org.gvsig.graph.solvers.Route;
import org.gvsig.graph.solvers.ShortestPathSolverAStar;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

public class TestClosestFacilty extends TestCase {
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
	
	public void testCalculate() throws BaseException {
		OneToManySolver solver = new OneToManySolver();
		net.setLayer(lyr);
		net.setGraph(g);
		solver.setNetwork(net);
		
		//		 Source flag
		GvFlag sourceFlag = net.createFlag(442177.19, 4476236.12, 10);
		net.addFlag(sourceFlag);
		solver.setSourceFlag(sourceFlag);
		
		GvFlag[] facilitiesFlags={
				net.createFlag(442211.67, 4476093.92, 10),
				net.createFlag(441703.18, 4475266.55, 10),
				net.createFlag(441022.33, 4476843.72, 10),
				net.createFlag(442612.42, 4476744.61, 10)
		};
		
		//		 Destination flags
		// NONE: We will use dijkstra algorithm to label network
		// and extract (after) the visited arcs.
		for (int i = 0; i < facilitiesFlags.length; i++) {
			net.addFlag(facilitiesFlags[i]);
		}

		long t1 = System.currentTimeMillis();
		solver.putDestinationsOnNetwork(net.getFlags());
		solver.setExploreAllNetwork(false);
		solver.setMaxCost(4000.0);
		solver.calculate();
		solver.removeDestinationsFromNetwork(net.getFlags());
		
		// En este punto tenemos la red "etiquetada" con los pesos
		// y distancias. Hay 2 opciones: recorrer toda la capa
		// y copiar los registros que nos interesan (opción fácil)
		// o implementar un listener que vaya vigilando los
		// arcos que se están evaluando dentro del algoritmo de
		// dijkstra.
		// Primero opción fácil
		// Recorremos la capa, vemos en qué intervalo cae cada
		// entidad y escribimos un shape.
		
		GvFlag[] flags=net.getFlags();
		GvFlag closestFlag=null;
		if(flags.length>1){
			closestFlag=flags[1];
			
			for(int i=1;i<flags.length;i++){
				if(flags[i].getCost()<closestFlag.getCost()) closestFlag=flags[i];
			}
			
			System.out.println("Hacia el Proveedor:\nProveedor más cercano: "+closestFlag.getIdFlag()+" ("+closestFlag.getCost()+")");
		}
		else {
			System.out.println("Sin proveedores");
		}
		
		long t2 = System.currentTimeMillis();
		System.out.println("tiempo:" + (t2-t1));
		
		
		//Desde el proveedor
		try {
			this.setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ShortestPathSolverAStar solverFromFacility=new ShortestPathSolverAStar();
		net.setLayer(lyr);
		net.setGraph(g);
		solverFromFacility.setNetwork(net);
		
		GvFlag destinyFlag = sourceFlag; //Ahora el punto de origen pasa a ser el destino
		
		Route bestRoute=null;
		GvFlag closestFacility=null;
		long ini=System.currentTimeMillis();
		for (int i = 0; i < facilitiesFlags.length; i++) {
			net.removeFlags();
			net.addFlag(facilitiesFlags[i]);
			net.addFlag(destinyFlag);
			Route route=solverFromFacility.calculateRoute();
			if(bestRoute==null || route.getCost()<bestRoute.getCost()){
				bestRoute=route;
				closestFacility=facilitiesFlags[i];
			}
		}
		long time=System.currentTimeMillis()-ini;
		
		System.out.println("Desde el proveedor: ");
		System.out.println("El mejor proveedor es: "+closestFacility.getIdFlag()+" ("+bestRoute.getCost()+")");
		
		System.out.println("Coste: "+time);
		
		
		// assertEquals(dist.doubleValue(), 8887, 0);
	}
}
