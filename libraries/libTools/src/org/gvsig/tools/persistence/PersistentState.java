package org.gvsig.tools.persistence;

import java.util.Iterator;

public interface PersistentState {

	public void setTheClass(Object obj);

	public void setTheClass(Class theClass);

	public void setTheClass(String name);

	public String getTheClassName();

	public PersistentState createState(Object obj) throws PersistenceException;

	public PersistentState createState(Object obj, boolean initialize)
			throws PersistenceException;

	public int getInt(String name) throws PersistenceValueNotFoundException;

	public long getLong(String name) throws PersistenceValueNotFoundException;

	public double getDouble(String name)
			throws PersistenceValueNotFoundException;

	public float getFloat(String name) throws PersistenceValueNotFoundException;

	public boolean getBoolean(String name)
			throws PersistenceValueNotFoundException;

	public PersistentState getState(String name)
			throws PersistenceValueNotFoundException, PersistenceException;

	public Object get(String name) throws PersistenceValueNotFoundException,
			PersistenceException;

	public Iterator getIterator(String name) throws PersistenceException;

	public PersistentState set(String name, String value);

	public PersistentState set(String name, int value);

	public PersistentState set(String name, long value);

	public PersistentState set(String name, double value);

	public PersistentState set(String name, float value);

	public PersistentState set(String name, boolean value);

	public PersistentState set(String name, PersistentState state);

	public PersistentState set(String name, Persistent obj)
			throws PersistenceException;

	public PersistentState set(String name, Iterator it)
			throws PersistenceException;
}