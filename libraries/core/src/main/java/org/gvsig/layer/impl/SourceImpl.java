package org.gvsig.layer.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.gvsig.layer.DefaultLegend;
import org.gvsig.layer.Source;

public class SourceImpl implements Source {

	private Map<String, String> properties;
	private String typeName;
	private DataStore dataStore;

	public SourceImpl(String typeName, Map<String, String> properties) {
		this.typeName = typeName;
		this.properties = properties;
	}

	@Override
	public URL getURL() {
		assert false;
		return null;
	}

	@Override
	public DefaultLegend getDefaultLegend() {
		assert false;
		return null;
	}

	@Override
	public Map<String, String> getPersistentProperties() {
		return properties;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SourceImpl) {
			SourceImpl that = (SourceImpl) obj;
			return this.properties == that.properties;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return properties.hashCode();
	}

	@Override
	public SimpleFeatureSource createFeatureSource() throws IOException {
		return getDataStore().getFeatureSource(typeName);
	}

	private DataStore getDataStore() throws IOException {
		if (dataStore == null) {
			dataStore = DataStoreFinder.getDataStore(properties);
		}

		return dataStore;
	}

	@Override
	public void dispose() {
		if (dataStore == null) {
			dataStore.dispose();
		}
	}
}
