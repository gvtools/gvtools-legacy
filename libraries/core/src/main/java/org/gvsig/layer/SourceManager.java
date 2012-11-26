package org.gvsig.layer;

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
	 * Registers the specified <code>source</code> in this manager
	 * 
	 * @param source
	 * @throws IllegalArgumentException
	 *             If the source or the name is null or it is already added to a
	 *             {@link SourceManager} instance
	 */
	void register(Source source) throws IllegalArgumentException;

	SimpleFeatureSource getFeatureSource(Source source);

	DataSourceType[] getPersistence();

	void setPersistence(List<DataSourceType> dataSources);
}
