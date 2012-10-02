package com.iver.cit.gvsig.fmap.layers;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

import org.cresques.cts.ProjectionUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.driverManager.DriverLoadException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;

public class LayersIteratorTest extends TestCase {
	static final String fwAndamiDriverPath = "../_fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers";
	private File baseDataPath;
	private File baseDriversPath;
	private String shpDriverName = "gvSIG shp driver";
	private CoordinateReferenceSystem defaultCrs = ProjectionUtils
			.getCRS("EPSG:23030");


	protected void setUp() throws Exception {
		super.setUp();
		URL url = this.getClass().getResource("LayersIteratorTest_data");
		if (url == null)
			throw new Exception("Can't find 'LayersIteratorTest_data' dir");

		baseDataPath = new File(url.getFile());
		if (!baseDataPath.exists())
			throw new Exception("Can't find 'LayersIteratorTest_data' dir");

		baseDriversPath = new File(fwAndamiDriverPath);
		if (!baseDriversPath.exists())
			throw new Exception("Can't find drivers path: " + fwAndamiDriverPath);

		LayerFactory.setDriversPath(baseDriversPath.getAbsolutePath());
		if (LayerFactory.getDM().getDriverNames().length < 1)
			throw new Exception("Can't find drivers in path: " + fwAndamiDriverPath);
	}


	private MapContext newMapContext() {
		ViewPort vp = new ViewPort(defaultCrs);
		return new MapContext(vp);
	}

	private FLayer newShpLayerToLayers(String shpFileName) {
		FLayerFileVectorial layerResult = new FLayerFileVectorial();
		layerResult.setName(shpFileName);
		try {
			layerResult.setDriverByName(shpDriverName);
		} catch (DriverLoadException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			layerResult.setCrs(defaultCrs);
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		try {
			layerResult.setFileName(baseDataPath+File.separator+shpFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return layerResult;
	}

	private void testLayerIterator(LayersIterator iter,FLayer[] layers) {
		int i;
		FLayer aux;

		for (i=0;i<layers.length;i++) {
			if (!iter.hasNext()) {
				fail("iter count == "+i+", "+ (layers.length)+ "expected");
			}
			aux = iter.nextLayer();
			assertEquals("element "+ i,aux,layers[i]);
		}

		assertFalse("inter hasNext",iter.hasNext());
		try {
			iter.next();
			fail("No Exception throw");
		} catch (NoSuchElementException e){

		} catch (Exception e ) {
			fail("Exception throw is not a NoSuchElementException instance");
		}

		try {
			iter.remove();
			fail("No Exception throw");
		} catch (UnsupportedOperationException e){

		} catch (Exception e ) {
			fail("Exception throw is not a UnsupportedOperationException instance");
		}


	}

	public void test1() {
		MapContext mapContext = newMapContext();
		FLayers root = mapContext.getLayers();
		FLayer layerX = newShpLayerToLayers("x.shp");
		assertNotNull("x.shp",layerX);
		FLayer layerX1 = newShpLayerToLayers("x1.shp");
		assertNotNull("x1.shp",layerX1);
		FLayer layerX2 = newShpLayerToLayers("x2.shp");
		assertNotNull("x2.shp",layerX1);

		try {
			root.addLayer(layerX);
			root.addLayer(layerX1);
			root.addLayer(layerX2);
		} catch (CancelationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue("rootLayer.getLayersCount() == 3",root.getLayersCount() == 3);

		testLayerIterator(
				new LayersIterator(root),
				new FLayer[] {root,layerX,layerX1,layerX2}
		);
	}

	public void test2() {
		MapContext mapContext = newMapContext();
		FLayers root = mapContext.getLayers();

		FLayer layerX = newShpLayerToLayers("x.shp");
		assertNotNull("x.shp",layerX);
		FLayer layerX1 = newShpLayerToLayers("x1.shp");
		assertNotNull("x1.shp",layerX1);
		FLayer layerX2 = newShpLayerToLayers("x2.shp");
		assertNotNull("x2.shp",layerX1);

		FLayers group1 = new FLayers();//(mapContext,root);
		group1.setMapContext(mapContext);
		group1.setParentLayer(root);
		try {
			root.addLayer(group1);
			root.addLayer(layerX);
			group1.addLayer(layerX1);
			group1.addLayer(layerX2);
		} catch (CancelationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue("rootLayer.getLayersCount() == 2",root.getLayersCount() == 2);
		assertTrue("group1.getLayersCount() == 2",group1.getLayersCount() == 2);

		testLayerIterator(
				new LayersIterator(root),
				new FLayer[] {root,group1,layerX1,layerX2,layerX}
		);
	}

	public void test3() {
		MapContext mapContext = newMapContext();
		FLayers root = mapContext.getLayers();

		FLayer layerX = newShpLayerToLayers("x.shp");
		assertNotNull("x.shp",layerX);
		FLayer layerX1 = newShpLayerToLayers("x1.shp");
		assertNotNull("x1.shp",layerX1);
		FLayer layerX2 = newShpLayerToLayers("x2.shp");
		assertNotNull("x2.shp",layerX1);

		FLayers group1 = new FLayers();//(mapContext,root);
		group1.setMapContext(mapContext);
		group1.setParentLayer(root);
		try {
			root.addLayer(group1);
		} catch (CancelationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FLayers group1_1 = new FLayers();//(mapContext,group1);
		group1_1.setMapContext(mapContext);
		group1_1.setParentLayer(group1);
		try {
			group1.addLayer(group1_1);
		} catch (CancelationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FLayers group1_2 = new FLayers();//(mapContext,group1);
		group1_2.setMapContext(mapContext);
		group1_2.setParentLayer(group1);

		try {
			group1.addLayer(group1_2);
		} catch (CancelationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			root.addLayer(layerX);
			group1.addLayer(layerX1);
			group1_1.addLayer(layerX2);
		} catch (CancelationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue("rootLayer.getLayersCount() == 2",root.getLayersCount() == 2);
		assertTrue("group1.getLayersCount() == 3",group1.getLayersCount() == 3);
		assertTrue("group1_1.getLayersCount() == 1",group1_1.getLayersCount() == 1);
		assertTrue("group1_2.getLayersCount() == 0",group1_2.getLayersCount() == 0);

		testLayerIterator(
				new LayersIterator(root),
				new FLayer[] {root,group1,group1_1,layerX2,group1_2,layerX1,layerX}
		);

		testLayerIterator(
				new LayersIterator(group1),
				new FLayer[] {group1,group1_1,layerX2,group1_2,layerX1}
		);

	}

	public void test4() {
		MapContext mapContext = newMapContext();
		FLayers root = mapContext.getLayers();

		FLayer layerX = newShpLayerToLayers("x.shp");
		assertNotNull("x.shp",layerX);
		FLayer layerX1 = newShpLayerToLayers("x1.shp");
		assertNotNull("x1.shp",layerX1);
		FLayer layerX2 = newShpLayerToLayers("x2.shp");
		assertNotNull("x2.shp",layerX1);

		FLayers group1 = new FLayers();//(mapContext,root);
		group1.setMapContext(mapContext);
		group1.setParentLayer(root);
		try {
			root.addLayer(group1);
		} catch (CancelationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FLayers group1_1 = new FLayers();//(mapContext,root);
		group1_1.setMapContext(mapContext);
		group1_1.setParentLayer(group1);
		try {
			group1.addLayer(group1_1);
		} catch (CancelationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FLayers group1_2 = new FLayers();//(mapContext,root);
		group1_2.setMapContext(mapContext);
		group1_2.setParentLayer(group1);
		try {
			group1.addLayer(group1_2);
		} catch (CancelationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			root.addLayer(layerX);
			group1.addLayer(layerX1);
			group1_1.addLayer(layerX2);
		} catch (CancelationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue("rootLayer.getLayersCount() == 2",root.getLayersCount() == 2);
		assertTrue("group1.getLayersCount() == 3",group1.getLayersCount() == 3);
		assertTrue("group1_1.getLayersCount() == 1",group1_1.getLayersCount() == 1);
		assertTrue("group1_2.getLayersCount() == 0",group1_2.getLayersCount() == 0);

		testLayerIterator(
				new myLayerIteratorFLayers(root),
				new FLayer[] {root,group1,group1_1,group1_2}
		);

		LayersIterator iter;
		iter = new LayersIterator(root) {

			public boolean evaluate(FLayer layer) {
				return !(layer instanceof FLayers);
			}

		};

		testLayerIterator(
				iter,
				new FLayer[] {layerX2,layerX1,layerX}
		);


	}

}

class myLayerIteratorFLayers extends LayersIterator {
	public myLayerIteratorFLayers(FLayer layer) {
		super(layer);
	}

	public boolean evaluate(FLayer layer) {
		return layer instanceof FLayers;
	}

}
