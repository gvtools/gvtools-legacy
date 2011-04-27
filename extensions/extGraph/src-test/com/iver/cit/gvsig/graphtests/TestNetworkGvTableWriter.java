package com.iver.cit.gvsig.graphtests;


import java.io.File;

import junit.framework.TestCase;

import org.cresques.cts.IProjection;
import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.writers.NetworkGvTableWriter;

import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.edition.writers.dbf.DbfWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

public class TestNetworkGvTableWriter extends TestCase {
	NetworkGvTableWriter netBuilder = new NetworkGvTableWriter();
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
			System.out.println("Building DBF time:" + (t2-t1) + " msecs.");
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	protected void setUp() throws Exception {
		LayerFactory
				.setDriversPath("../_fwAndami/gvSIG/extensiones/org.gvsig/drivers");
		IProjection prj = CRSFactory.getCRS("EPSG:23030");
		File shpFile = new File("c:/ejes.shp");
		lyr = (FLyrVect) LayerFactory.createLayer("Ejes",
				"gvSIG shp driver", shpFile, prj);

		String fieldType = "tipored";
		String fieldDist = "length";
		String fieldCost = "cost";
		String fieldSense = "";

		netBuilder.setLayer(lyr);
		netBuilder.setFieldCost(fieldCost);
		netBuilder.setFieldType(fieldType);
		netBuilder.setFieldDist(fieldDist);
		netBuilder.setFieldSense(fieldSense);
		DbfWriter nodeWriter = new DbfWriter();
		File nodeFile = new File("c:/nodes.dbf");
		nodeFile.delete();
		nodeWriter.setFile(nodeFile);

		File edgeFile = new File("c:/edges.dbf");
		DbfWriter edgeWriter = new DbfWriter();
		edgeWriter.setFile(edgeFile);
		
		
		netBuilder.setEdgeWriter(edgeWriter);
		netBuilder.setNodeWriter(nodeWriter);
		
		
		
	}

}
