package com.iver.cit.gvsig.graphtests;


import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;

import org.cresques.cts.ProjectionUtils;
import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.writers.NetworkJdbcWriter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

public class TestNetworJdbcWriter extends TestCase {
	NetworkJdbcWriter netBuilder = new NetworkJdbcWriter();
	FLyrVect lyr;
	/*
	 * Test method for
	 * 'org.gvsig.graph.core.NetworkWriter.writeNetwork()'
	 */
	public void testWriteNetwork() {
		try {
			long t1 = System.currentTimeMillis();
			netBuilder.writeNetwork();
			long t2 = System.currentTimeMillis();
			System.out.println("Building MYSQL time:" + (t2-t1) + " msecs.");
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
		lyr = (FLyrVect) LayerFactory.createLayer("Ejes",
				"gvSIG shp driver", shpFile, crs);

		String fieldType = "tipored";
		String fieldDist = "length";
		String fieldCost = "cost";
		String fieldSense = "";

		netBuilder.setLayer(lyr);
		netBuilder.setFieldCost(fieldCost);
		netBuilder.setFieldType(fieldType);
		netBuilder.setFieldDist(fieldDist);
		netBuilder.setFieldSense(fieldSense);

        String dbURL = "jdbc:mysql://localhost:3306/test";  
        String user = "root";
        String pwd = "aquilina";
        Connection conn;
		conn = DriverManager.getConnection(dbURL, user, pwd);

		netBuilder.setConnection(conn);
		
		
		
	}

}
