package org.gvsig.layer;

import java.net.URL;
import java.util.Map;

/**
 * Abstraction of a data source that is accessed through layers. Any
 * implementation must implement the {@link #equals(Object)} method (and
 * therefore, implement {@link #hashCode()} accordingly)
 * 
 * @author fergonco
 */
public interface Source {

	URL getURL();

	/**
	 * Returns a {@link DefaultLegend} instance if this source provides one.
	 * Null if the layers created from this source should have a default legend
	 * initially
	 * 
	 * @return
	 */
	DefaultLegend getDefaultLegend();

	/**
	 * Returns the unique id this source this source has in the manager it is
	 * registered
	 * 
	 * @return The id or null if this instance has not been registered in a
	 *         {@link SourceManager} yet
	 */
	String getId();

	/**
	 * Returns a map of properties that can be used to rebuild this instance in
	 * the future by calling the
	 * {@link SourceFactory#createSource(java.util.HashMap)} method
	 * 
	 * @return
	 */
	Map<String, Object> getPersistentProperties();

}
