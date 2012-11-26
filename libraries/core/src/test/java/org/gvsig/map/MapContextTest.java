package org.gvsig.map;

import java.awt.Color;

import org.geotools.referencing.CRS;
import org.gvsig.GVSIGTestCase;
import org.gvsig.layer.Layer;
import org.gvsig.layer.impl.CompositeLayer;
import org.gvsig.units.Unit;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.google.inject.Inject;

public class MapContextTest extends GVSIGTestCase {
	@Inject
	private MapContextFactory factory;

	private MapContext mapContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mapContext = factory.createMapContext();
	}

	public void testMapUnits() throws Exception {
		assertEquals(MapContext.DEFAULT_MAP_UNITS, mapContext.getMapUnits());

		mapContext.setMapUnits(Unit.CM);
		assertEquals(Unit.CM, mapContext.getMapUnits());

		mapContext.setMapUnits(null);
		assertEquals(MapContext.DEFAULT_MAP_UNITS, mapContext.getMapUnits());
	}

	public void testAreaUnits() throws Exception {
		assertEquals(MapContext.DEFAULT_AREA_UNITS, mapContext.getAreaUnits());

		mapContext.setAreaUnits(Unit.CM);
		assertEquals(Unit.CM, mapContext.getAreaUnits());

		mapContext.setAreaUnits(null);
		assertEquals(MapContext.DEFAULT_AREA_UNITS, mapContext.getAreaUnits());
	}

	public void testDistanceUnits() throws Exception {
		assertEquals(MapContext.DEFAULT_DISTANCE_UNITS,
				mapContext.getDistanceUnits());

		mapContext.setDistanceUnits(Unit.CM);
		assertEquals(Unit.CM, mapContext.getDistanceUnits());

		mapContext.setDistanceUnits(null);
		assertEquals(MapContext.DEFAULT_DISTANCE_UNITS,
				mapContext.getDistanceUnits());
	}

	public void testRootLayer() throws Exception {
		Layer root = mapContext.getRootLayer();
		assertTrue(root instanceof CompositeLayer);
		assertEquals(1, root.getAllLayers().length);
		assertEquals(root, root.getAllLayers()[0]);
	}

	public void testCRS() throws Exception {
		CoordinateReferenceSystem defaultCrs = CRS
				.decode(MapContext.DEFAULT_CRS_CODE);
		assertEquals(defaultCrs, mapContext.getCRS());

		mapContext.setCRS(CRS.decode("EPSG:4326"));
		assertEquals(CRS.decode("EPSG:4326"), mapContext.getCRS());

		mapContext.setCRS(null);
		assertEquals(defaultCrs, mapContext.getCRS());
	}

	public void testBGColor() throws Exception {
		assertEquals(MapContext.DEFAULT_BG_COLOR,
				mapContext.getBackgroundColor());

		mapContext.setBackgroundColor(Color.black);
		assertEquals(Color.black, mapContext.getBackgroundColor());

		mapContext.setBackgroundColor(null);
		assertEquals(MapContext.DEFAULT_BG_COLOR,
				mapContext.getBackgroundColor());
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
