package org.gvsig.layer.impl;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.gvsig.layer.Source;

class SourceManagerImpl {

	private HashMap<Source, DataStore> urlSource = new HashMap<Source, DataStore>();

	public void addDataStore(Source source, DataStore store) {
		urlSource.put(source, store);
	}

	public DataStore getDataStore(Source source) throws IOException {
		DataStore dataStore = urlSource.get(source);
		if (dataStore == null) {
			URL url = source.getURL();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("url", url);
			dataStore = DataStoreFinder.getDataStore(map);
			urlSource.put(source, dataStore);
		}

		return dataStore;
	}

	public static void main(String[] args) throws Exception {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("url", new URL("file:///tmp/a.shp"));
		// map.put("url", new
		// URL("file:///home/fergonco/carto/Co_ALKGebaeude.shp"));
		DataStore dataStore = DataStoreFinder.getDataStore(map);
		System.out.println(dataStore.getInfo().getSource());

		System.out.println(dataStore.getSchema(dataStore.getTypeNames()[0])
				.getCoordinateReferenceSystem());
	}

}
