package org.gvsig.geotools.test;

import java.sql.Types;

import junit.framework.TestCase;

import org.geotools.data.memory.MemoryDataStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.StringValue;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.gt2.FLiteShape;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class GTFLyrVectTest extends TestCase {
	private static final GeometryFactory gf = new GeometryFactory();

	private static final String ATTRIBUTE_GEOM = "geom";
	private static final String ATTRIBUTE_NAME = "name";
	private static final String ATTRIBUTE_VALUE = "value";

	private static final int ATTRIBUTE_INDEX_NAME = 0;
	private static final int ATTRIBUTE_INDEX_VALUE = 1;

	private static final String CRS = "EPSG:23030";

	public void testShapeType() throws Exception {
		GTFLyrVect layer = mockPointLayer();
		assertEquals(FShape.POINT, layer.getShapeType());
	}

	public void testShapeCount() throws Exception {
		GTFLyrVect layer = mockPointLayer();
		assertEquals(3, layer.getShapeCount());
	}

	public void testGetFeature() throws Exception {
		GTFLyrVect layer = mockPointLayer();

		// First feature
		SimpleFeature feature = layer.getFeature(0);
		assertEquals(gf.createPoint(new Coordinate(0, 0)),
				feature.getAttribute(ATTRIBUTE_GEOM));
		assertEquals("name0", feature.getAttribute(ATTRIBUTE_NAME));
		assertEquals(0, feature.getAttribute(ATTRIBUTE_VALUE));

		// Last feature
		feature = layer.getFeature(2);
		assertEquals(gf.createPoint(new Coordinate(-10, -10)),
				feature.getAttribute(ATTRIBUTE_GEOM));
		assertEquals("name2", feature.getAttribute(ATTRIBUTE_NAME));
		assertEquals(200, feature.getAttribute(ATTRIBUTE_VALUE));

		// Out of bounds feature
		assertNull(layer.getFeature(20));
		assertNull(layer.getFeature(-20));
	}

	public void testGetShape() throws Exception {
		GTFLyrVect layer = mockPointLayer();

		// First geom
		assertEquals(gf.createPoint(new Coordinate(0, 0)),
				toJTS(layer.getShape(0)));

		// Last geom
		assertEquals(gf.createPoint(new Coordinate(-10, -10)),
				toJTS(layer.getShape(2)));

		assertNull(layer.getFeature(20));
		assertNull(layer.getFeature(-20));
	}

	public void testGetFieldCount() throws Exception {
		GTFLyrVect layer = mockPointLayer();
		assertEquals(2, layer.getFieldCount());
	}

	public void testGetRowCount() throws Exception {
		GTFLyrVect layer = mockPointLayer();
		assertEquals(3, layer.getRowCount());
	}

	public void testGetFieldName() throws Exception {
		GTFLyrVect layer = mockPointLayer();
		assertEquals(ATTRIBUTE_NAME, layer.getFieldName(ATTRIBUTE_INDEX_NAME));
		assertEquals(ATTRIBUTE_VALUE, layer.getFieldName(ATTRIBUTE_INDEX_VALUE));

		try {
			assertNull(layer.getFieldName(-10));
			fail();
		} catch (IndexOutOfBoundsException e) {
		}

		try {
			assertNull(layer.getFieldName(20));
			fail();
		} catch (IndexOutOfBoundsException e) {
		}
	}

	public void testGetFieldType() throws Exception {
		GTFLyrVect layer = mockPointLayer();
		assertEquals(Types.VARCHAR, layer.getFieldType(ATTRIBUTE_INDEX_NAME));
		assertEquals(Types.INTEGER, layer.getFieldType(ATTRIBUTE_INDEX_VALUE));

		try {
			assertNull(layer.getFieldType(-10));
			fail();
		} catch (IndexOutOfBoundsException e) {
		}

		try {
			assertNull(layer.getFieldType(20));
			fail();
		} catch (IndexOutOfBoundsException e) {
		}
	}

	public void testGetFieldWidth() throws Exception {
		fail();
	}

	public void testGetFieldValue() throws Exception {
		GTFLyrVect layer = mockPointLayer();

		StringValue stringValue = (StringValue) layer.getFieldValue(1,
				ATTRIBUTE_INDEX_NAME);
		assertEquals("name1", stringValue.getValue());

		NumericValue number = (NumericValue) layer.getFieldValue(1,
				ATTRIBUTE_INDEX_VALUE);
		assertEquals(100, number.intValue());

		stringValue = (StringValue) layer
				.getFieldValue(2, ATTRIBUTE_INDEX_NAME);
		assertEquals("name2", stringValue.getValue());

		number = (NumericValue) layer.getFieldValue(0, ATTRIBUTE_INDEX_VALUE);
		assertEquals(0, number.intValue());
	}

	public void testFeatureIterator() throws Exception {
		GTFLyrVect layer = mockPointLayer();
		IFeatureIterator iterator = layer.getFeatureIterator(null, null, null);

		assertTrue(iterator.hasNext());
		IFeature next = iterator.next();
		NumericValue intValue = (NumericValue) next
				.getAttribute(ATTRIBUTE_INDEX_VALUE);
		assertEquals(0, intValue.intValue());
		assertEquals(gf.createPoint(new Coordinate(0, 0)),
				toJTS(next.getGeometry()));

		assertTrue(iterator.hasNext());
		next = iterator.next();
		intValue = (NumericValue) next.getAttribute(ATTRIBUTE_INDEX_VALUE);
		assertEquals(100, intValue.intValue());
		assertEquals(gf.createPoint(new Coordinate(10, 10)),
				toJTS(next.getGeometry()));

		assertTrue(iterator.hasNext());
		next = iterator.next();
		intValue = (NumericValue) next.getAttribute(ATTRIBUTE_INDEX_VALUE);
		assertEquals(200, intValue.intValue());
		assertEquals(gf.createPoint(new Coordinate(-10, -10)),
				toJTS(next.getGeometry()));

		assertFalse(iterator.hasNext());
	}

	private Geometry toJTS(IGeometry geometry) {
		FLiteShape shape = (FLiteShape) geometry.getInternalShape();
		return shape.getGeometry();
	}

	private GTFLyrVect mockPointLayer() throws Exception {
		Geometry[] geoms = new Geometry[3];
		geoms[0] = gf.createPoint(new Coordinate(0, 0));
		geoms[1] = gf.createPoint(new Coordinate(10, 10));
		geoms[2] = gf.createPoint(new Coordinate(-10, -10));

		String names[] = new String[] { "name0", "name1", "name2" };
		Integer values[] = new Integer[] { 0, 100, 200 };

		return mockLayer(geoms, names, values);
	}

	private GTFLyrVect mockLayer(Geometry[] geoms, String[] names,
			Integer[] ints) throws Exception {
		// Schema
		SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
		typeBuilder.setName("testType");
		typeBuilder.setNamespaceURI("http://www.geotools.org/");
		typeBuilder.setSRS(CRS);
		typeBuilder.add(ATTRIBUTE_GEOM, Geometry.class);
		typeBuilder.add(ATTRIBUTE_NAME, String.class);
		typeBuilder.add(ATTRIBUTE_VALUE, Integer.class);
		typeBuilder.setDefaultGeometry(ATTRIBUTE_GEOM);
		SimpleFeatureType schema = typeBuilder.buildFeatureType();

		// Data store
		MemoryDataStore source = new MemoryDataStore();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(schema);
		for (int i = 0; i < geoms.length; i++) {
			featureBuilder.add(geoms[i]);
			featureBuilder.add(names[i]);
			featureBuilder.add(ints[i]);
			SimpleFeature feature = featureBuilder.buildFeature("" + i);
			source.addFeature(feature);
		}

		// Layer
		return new GTFLyrVect(source.getFeatureSource(source.getTypeNames()[0]));
	}
}
