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
import org.gvsig.graph.solvers.ServiceAreaExtractor2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

public class TestServiceArea2 extends TestCase {

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
		
		ServiceAreaExtractor2 extractor = new ServiceAreaExtractor2(net);
		
		//		 Source flag
		GvFlag sourceFlag = net.createFlag(441901, 4475977, 10);
		net.addFlag(sourceFlag);
		solver.setSourceFlag(sourceFlag);
		solver.addListener(extractor);

		//		 Destination flags
		// NONE: We will use dijkstra algorithm to label network
		// and extract (after) the visited arcs.
//		net.addFlag(441901, 4475977, 10);
//		net.addFlag(442830, 4476201, 200);
//		net.addFlag(442673, 4475125, 200);
		extractor.setIdFlag(0);
		extractor.setDoCompactArea(true);
		long t1 = System.currentTimeMillis();
		solver.putDestinationsOnNetwork(net.getFlags());
		solver.setExploreAllNetwork(true);
		solver.setMaxDistance(4000.0);
		double[] costs = {1000.0, 2000.0}; 		
		extractor.setCosts(costs);
		
		solver.calculate();
		extractor.writeServiceArea();
		
		// En este punto tenemos la red "etiquetada" con los pesos
		// y distancias. Hay 2 opciones: recorrer toda la capa
		// y copiar los registros que nos interesan (opción fácil)
		// o implementar un listener que vaya vigilando los
		// arcos que se están evaluando dentro del algoritmo de
		// dijkstra.
		// Primero opción fácil
		// Recorremos la capa, vemos en qué intervalo cae cada
		// entidad y escribimos un shape.
		extractor.closeFiles(); // 2000 en unidades de coste. Normalmente serán segundos
		solver.removeDestinationsFromNetwork(net.getFlags());
		// TODO: También puede ser interesante fijar un límite en distancia, no
		// en tiempo
		
		extractor.getBorderPoints();
		
		long t2 = System.currentTimeMillis();
		System.out.println("tiempo:" + (t2-t1));
		
		// assertEquals(dist.doubleValue(), 8887, 0);

		
		


	}

}
