package org.gvsig.map;

import java.awt.Color;

import org.geotools.referencing.CRS;
import org.gvsig.GVSIGTestCase;
import org.gvsig.layer.Layer;
import org.gvsig.units.Unit;

import com.google.inject.Inject;

public class MapContextTest extends GVSIGTestCase {
	@Inject
	private MapContextFactory factory;

	private MapContext mapContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mapContext = factory.createMapContext(Unit.M, Unit.M, Unit.M,
				CRS.decode("EPSG:4326"));
	}

	public void testNotNullInContructor() throws Exception {
		try {
			factory.createMapContext(null, Unit.M, Unit.M,
					CRS.decode("EPSG:4326"));
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			factory.createMapContext(Unit.M, null, Unit.M,
					CRS.decode("EPSG:4326"));
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			factory.createMapContext(Unit.M, Unit.M, null,
					CRS.decode("EPSG:4326"));
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			factory.createMapContext(Unit.M, Unit.M, Unit.M, null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	public void testNonNullSetters() throws Exception {
		try {
			mapContext.setMapUnits(null);
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			mapContext.setAreaUnits(null);
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			mapContext.setDistanceUnits(null);
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			mapContext.setCRS(null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	public void testMapUnits() throws Exception {
		mapContext.setMapUnits(Unit.CM);
		assertEquals(Unit.CM, mapContext.getMapUnits());
		mapContext.setMapUnits(Unit.M);
		assertEquals(Unit.M, mapContext.getMapUnits());
	}

	public void testAreaUnits() throws Exception {
		mapContext.setAreaUnits(Unit.CM);
		assertEquals(Unit.CM, mapContext.getAreaUnits());
		mapContext.setAreaUnits(Unit.M);
		assertEquals(Unit.M, mapContext.getAreaUnits());
	}

	public void testDistanceUnits() throws Exception {
		mapContext.setDistanceUnits(Unit.CM);
		assertEquals(Unit.CM, mapContext.getDistanceUnits());
		mapContext.setDistanceUnits(Unit.M);
		assertEquals(Unit.M, mapContext.getDistanceUnits());
	}

	public void testRootLayer() throws Exception {
		Layer root = mapContext.getRootLayer();
		assertEquals(1, root.getAllLayers().length);
		assertEquals(root, root.getAllLayers()[0]);
	}

	public void testCRS() throws Exception {
		mapContext.setCRS(CRS.decode("EPSG:4326"));
		assertEquals(CRS.decode("EPSG:4326"), mapContext.getCRS());
		mapContext.setCRS(CRS.decode("EPSG:25830"));
		assertEquals(CRS.decode("EPSG:25830"), mapContext.getCRS());
	}

	public void testBGColor() throws Exception {
		mapContext.setBackgroundColor(Color.black);
		assertEquals(Color.black, mapContext.getBackgroundColor());
		mapContext.setBackgroundColor(Color.red);
		assertEquals(Color.red, mapContext.getBackgroundColor());
	}

	public void testDraw() throws Exception {
		fail();
	}

	public void testPersistence() throws Exception {
		fail();
	}

	public void testLayerErrors() throws Exception {
		fail();
	}
}
