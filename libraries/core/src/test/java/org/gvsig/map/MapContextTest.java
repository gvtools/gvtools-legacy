package org.gvsig.map;

import static org.mockito.Mockito.mock;

import java.awt.Color;
import java.util.List;

import org.geotools.referencing.CRS;
import org.gvsig.GVSIGTestCase;
import org.gvsig.layer.Layer;
import org.gvsig.layer.LayerFactory;
import org.gvsig.layer.Source;
import org.gvsig.persistence.generated.LayerType;
import org.gvsig.persistence.generated.MapType;
import org.gvsig.units.Unit;

import com.google.inject.Inject;

public class MapContextTest extends GVSIGTestCase {
	private static final Unit DEFAULT_UNIT = Unit.M;

	@Inject
	private MapContextFactory factory;

	@Inject
	private LayerFactory layerFactory;

	private MapContext mapContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mapContext = factory.createMapContext(DEFAULT_UNIT, DEFAULT_UNIT,
				DEFAULT_UNIT, CRS.decode("EPSG:4326"));
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

	public void testGetXML() throws Exception {
		mapContext.setBackgroundColor(Color.red);
		String crs = "EPSG:23030";
		mapContext.setCRS(CRS.decode(crs));
		mapContext.getRootLayer().addLayer(
				layerFactory.createLayer(mock(Source.class)));

		MapType xml = mapContext.getXML();
		List<LayerType> xmlLayers = xml.getRootLayer().getLayers();

		assertEquals(DEFAULT_UNIT, Unit.values()[xml.getMapUnits()]);
		assertEquals(DEFAULT_UNIT, Unit.values()[xml.getDistanceUnits()]);
		assertEquals(DEFAULT_UNIT, Unit.values()[xml.getAreaUnits()]);
		assertEquals(Color.red, new Color(xml.getColor()));
		assertEquals(crs, xml.getCrs());
		assertEquals(1, xmlLayers.size());
		assertTrue(xmlLayers.get(0).isVectorial());
	}

	public void testSetXML() throws Exception {
		MapType xml = new MapType();
		xml.setMapUnits(Unit.KM.ordinal());
		xml.setAreaUnits(Unit.KM.ordinal());
		xml.setDistanceUnits(Unit.KM.ordinal());
		xml.setColor(Color.green.getRGB());
		xml.setCrs("EPSG:23030");
		LayerType xmlLayer = new LayerType();
		xmlLayer.setActive(false);
		xmlLayer.setEditing(false);
		xmlLayer.setVectorial(false);
		LayerType xmlChild = new LayerType();
		xmlChild.setActive(false);
		xmlChild.setEditing(false);
		xmlChild.setVectorial(true);
		xmlLayer.getLayers().add(xmlChild);
		xml.setRootLayer(xmlLayer);

		mapContext.setXML(xml);
		Layer root = mapContext.getRootLayer();
		assertEquals(Unit.KM, mapContext.getMapUnits());
		assertEquals(Unit.KM, mapContext.getDistanceUnits());
		assertEquals(Unit.KM, mapContext.getAreaUnits());
		assertEquals(Color.green, mapContext.getBackgroundColor());
		assertEquals(CRS.decode("EPSG:23030"), mapContext.getCRS());
		assertFalse(root.isVectorial());
		assertEquals(2, root.getAllLayers().length);
		assertTrue(root.getAllLayers()[1].isVectorial());
	}

	public void testPersistence() throws Exception {
		mapContext.setBackgroundColor(Color.red);
		String crs = "EPSG:23030";
		mapContext.setCRS(CRS.decode(crs));
		mapContext.getRootLayer().addLayer(
				layerFactory.createLayer(mock(Source.class)));

		MapType xml = mapContext.getXML();
		MapContext mapContext2 = factory.createMapContext(
				Unit.values()[xml.getMapUnits()],
				Unit.values()[xml.getDistanceUnits()],
				Unit.values()[xml.getAreaUnits()], CRS.decode(xml.getCrs()));
		mapContext2.setXML(xml);

		assertEquals(mapContext.getMapUnits(), mapContext2.getMapUnits());
		assertEquals(mapContext.getDistanceUnits(),
				mapContext2.getDistanceUnits());
		assertEquals(mapContext.getAreaUnits(), mapContext2.getAreaUnits());
		assertEquals(mapContext.getBackgroundColor(),
				mapContext2.getBackgroundColor());
		assertEquals(mapContext.getCRS(), mapContext2.getCRS());
		Layer[] layers1 = mapContext.getRootLayer().getAllLayers();
		Layer[] layers2 = mapContext2.getRootLayer().getAllLayers();
		assertEquals(layers1.length, layers2.length);
	}

	public void testDrawErrors() throws Exception {
		fail();
	}
}
