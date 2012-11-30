package org.gvsig.layer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.geotools.data.simple.SimpleFeatureSource;
import org.gvsig.GVSIGTestCase;
import org.gvsig.persistence.generated.DataSourceType;
import org.gvsig.persistence.generated.StringPropertyType;

public class SourceTest extends GVSIGTestCase {

	private static final String SHP = SourceTest.class.getResource(
			"/sample.shp").toExternalForm();

	@Inject
	private SourceFactory sourceFactory;

	public void testSourceGetDefaultTypeName() throws Exception {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("url", SHP);
		Source source = sourceFactory.createSource(properties);

		SimpleFeatureSource featureSource = source.createFeatureSource();

		assertTrue(featureSource != null);
	}

	public void testGetXML() throws Exception {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("url", SHP);
		properties.put("foo", "bar");
		Source source = sourceFactory.createSource(properties);

		DataSourceType xml = source.getXML();
		List<StringPropertyType> xmlProps = xml.getProperties();
		assertEquals(2, xmlProps.size());
		for (StringPropertyType prop : xmlProps) {
			String name = prop.getPropertyName();
			assertTrue(properties.containsKey(name));
			assertEquals(properties.get(name), prop.getPropertyValue());
		}
	}

	public void testSetXML() throws Exception {
		DataSourceType xml = new DataSourceType();
		List<StringPropertyType> xmlProps = xml.getProperties();
		StringPropertyType prop = new StringPropertyType();
		prop.setPropertyName("url");
		prop.setPropertyName(SHP);
		xmlProps.add(prop);

		Source source = sourceFactory.createSource(xml);

		Map<String, String> props = source.getPersistentProperties();
		String name = prop.getPropertyName();
		assertTrue(props.containsKey(name));
		assertEquals(props.get(name), prop.getPropertyValue());
	}

	public void testPersistence() throws Exception {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("url", SHP);
		properties.put("foo", "bar");
		Source s1 = sourceFactory.createSource(properties);
		Source s2 = sourceFactory.createSource(s1.getXML());

		Map<String, String> props1 = s1.getPersistentProperties();
		Map<String, String> props2 = s2.getPersistentProperties();

		assertEquals(props1.size(), props2.size());
		for (String key : props1.keySet()) {
			assertTrue(props2.containsKey(key));
			assertEquals(props1.get(key), props2.get(key));
		}
	}
}
