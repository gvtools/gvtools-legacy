package org.gvsig.layer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;

import javax.inject.Inject;

import org.geotools.data.simple.SimpleFeatureSource;
import org.gvsig.GVSIGTestCase;
import org.gvsig.persistence.generated.DataSourceType;
import org.junit.Test;

public class SourceManagerTest extends GVSIGTestCase {

	@Inject
	private SourceManager sourceManager;

	@Inject
	private SourceFactory sourceFactory;

	public void testAddSource() throws Exception {
		Source source = mock(Source.class);
		sourceManager.register(source);

		assertTrue(sourceManager.getSources().length == 1);
		assertTrue(sourceManager.getSources()[0] == source);
	}

	public void testAddSameSourceTwice() throws Exception {
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put("url", "file:///tmp/my.shp");
		Source s1 = sourceFactory.createSource(properties);
		Source s2 = sourceFactory.createSource(properties);

		sourceManager.register(s1);
		sourceManager.register(s2);

		assertTrue(sourceManager.getSources().length == 1);
		assertTrue(sourceManager.getSources()[0] == s1);

		properties.put("encoding", "utf-8");
		Source s3 = sourceFactory.createSource(properties);
		sourceManager.register(s3);

		assertTrue(sourceManager.getSources().length == 2);
		assertTrue(sourceManager.getSources()[1] == s3
				|| sourceManager.getSources()[0] == s3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNullSource() throws Exception {
		sourceManager.register(null);
	}

	public void testGetFeatureSource() throws Exception {
		Source source = mock(Source.class);
		SimpleFeatureSource featureSource = mock(SimpleFeatureSource.class);
		when(source.createFeatureSource()).thenReturn(featureSource);

		sourceManager.register(source);
		SimpleFeatureSource featureSourceResult = sourceManager
				.getFeatureSource(source);

		assertTrue(featureSource == featureSourceResult);
	}

	public void testSourceDataSourcesAreFreedAfterSourceManagerCleanUp()
			throws Exception {
		Source source = mock(Source.class);
		sourceManager.register(source);
		sourceManager.setPersistence(Collections.<DataSourceType> emptyList());

		verify(source).dispose();
		assertTrue(sourceManager.getSources().length == 0);
	}

}
