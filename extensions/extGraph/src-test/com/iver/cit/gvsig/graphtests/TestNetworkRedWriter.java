package com.iver.cit.gvsig.graphtests;

import java.io.File;

import junit.framework.TestCase;

import org.cresques.cts.ProjectionUtils;
import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.writers.NetworkFileRedWriter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

public class TestNetworkRedWriter extends TestCase {
	NetworkFileRedWriter netBuilder = new NetworkFileRedWriter();
	FLyrVect lyr;
	File redFile;

	/*
	 * Test method for 'org.gvsig.graph.core.NetworkWriter.writeNetwork()'
	 */
	public void testWriteNetwork() {
		try {
			long t1 = System.currentTimeMillis();
			netBuilder.writeNetwork();
			long t2 = System.currentTimeMillis();
			System.out.println("Building RED time:" + (t2 - t1) + " msecs.");
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void setUp() throws Exception {
		LayerFactory
				.setDriversPath("../_fwAndami/gvSIG/extensiones/org.gvsig/drivers");
		CoordinateReferenceSystem crs = ProjectionUtils.getCRS("EPSG:23030");
		File shpFile = new File("c:/ejes.shp");
		lyr = (FLyrVect) LayerFactory.createLayer("Ejes", "gvSIG shp driver",
				shpFile, crs);

		String redFilePath = lyr.getName().replaceFirst("\\Q.shp\\E", "");
		redFile = new File("c:/" + redFilePath + ".red");

		redFile.delete();

		String fieldType = "tipored";
		String fieldDist = "length";
		String fieldCost = "cost";
		String fieldSense = "";

		netBuilder.setLayer(lyr);
		netBuilder.setFieldCost(fieldCost);
		netBuilder.setFieldType(fieldType);
		netBuilder.setFieldDist(fieldDist);
		netBuilder.setFieldSense(fieldSense);

		netBuilder.setRedFile(redFile);

	}

}
