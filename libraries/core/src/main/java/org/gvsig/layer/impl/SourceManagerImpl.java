package org.gvsig.layer.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureSource;
import org.gvsig.layer.Source;
import org.gvsig.layer.SourceManager;
import org.gvsig.persistence.generated.DataSourceType;

public class SourceManagerImpl implements SourceManager {

	private HashSet<Source> sources = new HashSet<Source>();

	@Override
	public Source[] getSources() {
		return sources.toArray(new Source[sources.size()]);
	}

	@Override
	public void register(Source source) throws IllegalArgumentException {
		sources.add(source);
	}

	@Override
	public SimpleFeatureSource getFeatureSource(Source source)
			throws IOException {
		return source.createFeatureSource();
	}

	@Override
	public DataSourceType[] getPersistence() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPersistence(List<DataSourceType> dataSources) {
		for (Source source : sources) {
			source.dispose();
		}

		sources.clear();
	}

}
