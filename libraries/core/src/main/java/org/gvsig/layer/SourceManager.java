package org.gvsig.layer;

import java.io.IOException;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureSource;
import org.gvsig.persistence.generated.DataSourceType;

public interface SourceManager {

	/**
	 * Gets all the sources registered by in this source manager
	 * 
	 * @return
	 */
	Source[] getSources();

	/**
	 * Registers the specified <code>source</code> in this manager. It there is
	 * already the same source in the manager this call does nothing.
	 * 
	 * @param source
	 * @throws IllegalArgumentException
	 *             If the source is null
	 */
	void register(Source source) throws IllegalArgumentException;

	SimpleFeatureSource getFeatureSource(Source source) throws IOException;

	DataSourceType[] getPersistence();

	void setPersistence(List<DataSourceType> dataSources);
}
