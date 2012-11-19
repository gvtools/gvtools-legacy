package org.gvsig.layer;

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
	 * specified id.
	 * 
	 * @param id
	 * 
	 * @param source
	 * @throws IllegalArgumentException
	 *             If the source or the name is null or it is already added to a
	 *             {@link SourceManager} instance
	 */
	void register(String id, Source source) throws IllegalArgumentException;

}
