package com.iver.cit.gvsig.fmap;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.geotools.data.DataStore;

import com.iver.cit.gvsig.fmap.layers.DefaultSource;

public class SourceManagerTest extends TestCase {

	public void testCachedDataStore() throws Exception {
		URL url = new File("file:///my-fancy-url.shp").toURI().toURL();
		DataStore dataStore = SourceManager.instance
				.getDataStore(new DefaultSource(url));
		assertTrue(dataStore != null);
		assertTrue(dataStore == SourceManager.instance
				.getDataStore(new DefaultSource(url)));
	}
}
