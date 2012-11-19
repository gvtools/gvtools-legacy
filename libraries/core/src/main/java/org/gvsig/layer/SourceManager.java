package org.gvsig.layer;

import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureSource;

public interface SourceManager {

	/**
	 * Gets all the sources registered by in this source manager
	 * 
	 * @return
	 */
	Source[] getSources();

	/**
	 * Return the Source with the specified <code>id</code> if any. Null if
	 * there is no Source in this manager with that id
	 * 
	 * @param id
	 * @return
	 */
	Source getSource(String id);

	/**
	 * Registers the specified <code>source</code> in this manager with the
	 * specified id. The <code>id</code> attribute of the Source is set after a
	 * successful register
	 * 
	 * @param id
	 * 
	 * @param source
	 * @throws IllegalArgumentException
	 *             If the source or the name is null or it is already added to a
	 *             {@link SourceManager} instance
	 */
	void register(String id, Source source) throws IllegalArgumentException;

	SimpleFeatureSource getFeatureSource(Source source);

}
