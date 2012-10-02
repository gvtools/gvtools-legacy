package com.iver.cit.gvsig.fmap.spatialindex;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.cresques.cts.ProjectionUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
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

	CoordinateReferenceSystem DEFAULT_CRS = ProjectionUtils
			.getCRS("EPSG:23030");
	
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
				DEFAULT_CRS);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		DEFAULT_CRS = null;
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
}
