package com.iver.cit.gvsig.fmap.spatialindex;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.cresques.cts.IProjection;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

public class SpatialIndexTest extends TestCase {

	// TODO MOVER TODO LO ESTATICO A UNA CLASE AUXILIAR QUE NO SEA JUNIT

	String fwAndamiDriverPath = "../_fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers";

	File baseDataPath;

	File baseDriversPath;

	String SHP_DRIVER_NAME = "gvSIG shp driver";

	IProjection PROJECTION_DEFAULT = CRSFactory.getCRS("EPSG:23030");
	
	FLyrVect cantabria;
	FLyrVect edificaciones;

	public SpatialIndexTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		URL url = SpatialIndexTest.class.getResource("testdata");
		if (url == null)
			throw new Exception(
					"No se encuentra el directorio con datos de prueba");

		baseDataPath = new File(url.getFile());
		if (!baseDataPath.exists())
			throw new Exception(
					"No se encuentra el directorio con datos de prueba");

		baseDriversPath = new File(fwAndamiDriverPath);
		if (!baseDriversPath.exists())
			throw new Exception("Can't find drivers path: "
					+ fwAndamiDriverPath);

		LayerFactory.setDriversPath(baseDriversPath.getAbsolutePath());
		if (LayerFactory.getDM().getDriverNames().length < 1)
			throw new Exception("Can't find drivers in path: "
					+ fwAndamiDriverPath);
		
		cantabria = (FLyrVect) newLayer("Cantabria.shp", SHP_DRIVER_NAME);
		edificaciones = (FLyrVect) newLayer("Edificaciones.shp", SHP_DRIVER_NAME);
		
	}

	public FLayer newLayer(String fileName, String driverName)
			throws LoadLayerException {
		File file = new File(baseDataPath, fileName);
		return LayerFactory.createLayer(fileName, driverName, file,
				PROJECTION_DEFAULT);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		PROJECTION_DEFAULT = null;
	}

	public void testSpatialIndexFullExtent() throws ReadDriverException,
			ExpansionFileReadException, VisitorException, LoadLayerException {
		
		Rectangle2D rect2D = cantabria.getFullExtent();
		FBitSet bitset = cantabria.queryByRect(rect2D);
		assertTrue(bitset.cardinality() != 0);

		double x = rect2D.getCenterX();
		double y = rect2D.getCenterY();
		rect2D = new Rectangle2D.Double(x, y, 100d, 100d);
		bitset = cantabria.queryByRect(rect2D);
		assertTrue(bitset.cardinality() != 0);
	}

	public void testPersistentRtreeJsi() throws LoadLayerException,
			ReadDriverException, ExpansionFileReadException,
			SpatialIndexException {
		try {
			PersistentRTreeJsi rtree = new PersistentRTreeJsi(baseDataPath
					.getAbsolutePath()
					+ "/Cantabria", true);
			

			int numShapes = cantabria.getSource().getShapeCount();
			for (int i = 0; i < numShapes; i++) {
				IGeometry g = cantabria.getSource().getShape(i);
				if (g == null)
					System.out.println("geometria a null");
				rtree.insert(g.getBounds2D(), i);
			}
			rtree.flush();

			rtree = null;

			rtree = new PersistentRTreeJsi(baseDataPath.getAbsolutePath()
					+ "/Cantabria", false);

			Rectangle2D rect2D = cantabria.getFullExtent();
			List results = rtree.query(rect2D);
			assertTrue(results.size() != 0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testPerformanceRtreeJsiQuadtreeGt2() throws LoadLayerException, ExpansionFileReadException, SpatialIndexException, ReadDriverException {
		
		
		String fileName = baseDataPath.getAbsolutePath()
							+ "/Edificaciones.shp";
		long t0 = System.currentTimeMillis();
		QuadtreeGt2	spatialIndex = new QuadtreeGt2(fileName,
														"NM", 
										edificaciones.getSource().getFullExtent(),
										edificaciones.getSource().getShapeCount(), 
										true);
		int numShapes = edificaciones.getSource().getShapeCount();
		for (int i = 0; i < numShapes; i++) {
			IGeometry g = edificaciones.getSource().getShape(i);
			spatialIndex.insert(g.getBounds2D(), i);
		}
		spatialIndex.flush();
		long t1 = System.currentTimeMillis();
		System.out.println((t1-t0)+ " en generar Edificaciones.qix");
		
		t0 = System.currentTimeMillis();
		PersistentRTreeJsi rtree = new PersistentRTreeJsi(baseDataPath
				.getAbsolutePath()
				+ "/Edificaciones", true);
		numShapes = edificaciones.getSource().getShapeCount();
		for (int i = 0; i < numShapes; i++) {
			IGeometry g = edificaciones.getSource().getShape(i);
			rtree.insert(g.getBounds2D(), i);
		}
		t1 = System.currentTimeMillis();
		System.out.println((t1-t0)+" en generar Edificaciones.rix");
		rtree.flush();
		
		t0 = System.currentTimeMillis();
		spatialIndex = new QuadtreeGt2(fileName,
				"NM", 
				edificaciones.getSource().getFullExtent(),
				edificaciones.getSource().getShapeCount(), 
				false);
		t1 = System.currentTimeMillis();
		System.out.println((t1-t0)+" en cargar Edificaciones.qix");

		t0 = System.currentTimeMillis();
		rtree = new PersistentRTreeJsi(baseDataPath
				.getAbsolutePath()
				+ "/Edificaciones", false);
		t1 = System.currentTimeMillis();
		System.out.println((t1-t0)+" en cargar Edificaciones.rix");
		
		Rectangle2D rect2D = edificaciones.getFullExtent();
		
		t0 = System.currentTimeMillis();
		List results = rtree.query(rect2D);
		int nb1 = results.size();
		t1 = System.currentTimeMillis();
		System.out.println((t1-t0)+" en resolver query con RIX");
		System.out.println("encontrados "+nb1);
		t0 = System.currentTimeMillis();
		results = spatialIndex.query(rect2D);
		int nb2 = results.size();
		t1 = System.currentTimeMillis();
		System.out.println((t1-t0)+" en resolver query con QIX");
		System.out.println("encontrados "+nb2);
		
	}
	
	public void testNearestRectanglePoint() throws SpatialIndexException, ExpansionFileReadException, ReadDriverException{
		long t0 = System.currentTimeMillis();
		PersistentRTreeJsi rtree = new PersistentRTreeJsi(baseDataPath
				.getAbsolutePath()
				+ "/Edificaciones", false);
		long t1 = System.currentTimeMillis();
		
		System.out.println((t1-t0)+" en cargar Edificaciones.rix");
		
		Rectangle2D edifFullExtent = edificaciones.getFullExtent();
		double xc = edifFullExtent.getCenterX();
		double yc = edifFullExtent.getCenterY();
		double width = 200;
		int numberOfNearest = 20;
		Point2D p = new Point2D.Double(xc, yc);
		Rectangle2D query = new Rectangle2D.Double(xc, yc, width, width);
		
		t0 = System.currentTimeMillis();
		List solution = rtree.findNNearest(numberOfNearest, p);
		t1 = System.currentTimeMillis();
		
		System.out.println((t1-t0)+" en obtener los 20 mas cercanos al punto");
		
		assert(solution.size() > 0);
		
		t0 = System.currentTimeMillis();
		solution = rtree.findNNearest(numberOfNearest, query);
		t1 = System.currentTimeMillis();
		System.out.println((t1-t0)+" en obtener los 20 mas cercanos al rectangulo");
		assert(solution.size() > 0);
		
	}

}
