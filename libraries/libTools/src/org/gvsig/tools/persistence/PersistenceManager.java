package org.gvsig.tools.persistence;

public interface PersistenceManager {

	public PersistentState createState(Object obj) throws PersistenceException;

	public PersistentState createState(Object obj, boolean initialize)
			throws PersistenceException;

	public Object create(PersistentState state) throws PersistenceException;

	public void addAlias(String name, Class aClass);

	public void addAlias(String name, String className);

}
