package org.gvsig.layer.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureSource;
import org.gvsig.layer.Source;
import org.gvsig.layer.SourceManager;
import org.gvsig.persistence.generated.DataSourceType;

public class SourceManagerImpl implements SourceManager {

	private HashMap<String, Source> idSource = new HashMap<String, Source>();

	// private HashMap<Source, DataStore> urlSource = new HashMap<Source,
	// DataStore>();
	//
	// public void addDataStore(Source source, DataStore store) {
	// urlSource.put(source, store);
	// }
	//
	// public DataStore getDataStore(Source source) throws IOException {
	// DataStore dataStore = urlSource.get(source);
	// if (dataStore == null) {
	// URL url = source.getURL();
	// HashMap<String, Object> map = new HashMap<String, Object>();
	// map.put("url", url);
	// dataStore = DataStoreFinder.getDataStore(map);
	// urlSource.put(source, dataStore);
	// }
	//
	// return dataStore;
	// }
	//
	// public static void main(String[] args) throws Exception {
	// HashMap<String, Object> map = new HashMap<String, Object>();
	// map.put("url", new URL("file:///tmp/a.shp"));
	// // map.put("url", new
	// // URL("file:///home/fergonco/carto/Co_ALKGebaeude.shp"));
	// DataStore dataStore = DataStoreFinder.getDataStore(map);
	// System.out.println(dataStore.getInfo().getSource());
	//
	// System.out.println(dataStore.getSchema(dataStore.getTypeNames()[0])
	// .getCoordinateReferenceSystem());
	// }

	@Override
	public Source[] getSources() {
		Collection<Source> ret = idSource.values();
		return ret.toArray(new Source[ret.size()]);
	}

	@Override
	public Source getSource(String id) {
		return idSource.get(id);
	}

	@Override
	public void register(String id, Source source)
			throws IllegalArgumentException {
		idSource.put(id, source);
	}

	@Override
	public SimpleFeatureSource getFeatureSource(Source source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataSourceType[] getPersistence() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPersistence(List<DataSourceType> dataSources) {
		// TODO Auto-generated method stub

	}

}
