package org.gvsig.layer;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.geotools.data.simple.SimpleFeatureSource;
import org.gvsig.GVSIGTestCase;

public class SourceTest extends GVSIGTestCase {

	@Inject
	private SourceFactory sourceFactory;

	public void testSourceGetDefaultTypeName() throws Exception {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("url", "file:///tmp/myshape.shp");
		Source source = sourceFactory.createSource(properties);

		SimpleFeatureSource featureSource = source.createFeatureSource();

		assertTrue(featureSource != null);

		fail("Should create a project with data to test");
	}

	public void testPersistence() throws Exception {
		fail();
	}

	public void testSourceManagerCleanFreesDataStore() throws Exception {
		fail();
	}

	public void testThinkMoreTests() throws Exception {
		fail();
	}

}
