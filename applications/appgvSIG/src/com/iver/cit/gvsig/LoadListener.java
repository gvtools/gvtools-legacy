package com.iver.cit.gvsig;

/**
 * @author fergonco
 */
public interface LoadListener {

	/**
	 * Invoked after a project has been loaded. The loaded project can be
	 * accessed through {@link LoadEvent#getProject()}
	 * 
	 * @param e
	 */
	void afterLoading(LoadEvent e);

}
