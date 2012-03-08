package com.iver.cit.gvsig;

/**
 * @author fergonco
 */
public interface NewProjectListener {

	/**
	 * Invoked after the user creates a new empty project
	 * 
	 * @param e
	 */
	void onCreate(CreateEvent e);

}
