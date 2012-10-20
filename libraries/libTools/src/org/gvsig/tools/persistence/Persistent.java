package org.gvsig.tools.persistence;

public interface Persistent {

	/**
	 * Get the persistent state of the object and append to the passed state.
	 * 
	 * @param state
	 */
	public void getState(PersistentState state) throws PersistenceException;

	/**
	 * Set the state of the object from the state passed as parameter.
	 * 
	 * @param state
	 */
	public void setState(PersistentState state) throws PersistenceException;

}
