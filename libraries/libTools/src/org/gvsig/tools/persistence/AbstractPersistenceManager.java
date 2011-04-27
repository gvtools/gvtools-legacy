package org.gvsig.tools.persistence;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPersistenceManager implements PersistenceManager {

	protected Map alias;

	protected AbstractPersistenceManager() {
		alias = new HashMap();
	}

	public void addAlias(String name, Class aClass) {
		alias.put(name, aClass);
	}

	public void addAlias(String name, String className) {
		alias.put(name, className);
	}

	public Object create(PersistentState state) throws PersistenceException {
		String className = state.getTheClassName();
		if (className == null) {
			throw new PersistenceException(null); // FIXME
		}
		try {
			Class theClass;

			Object x = alias.get(className);
			if (x instanceof Class) {
				theClass = (Class) x;
			} else if (x instanceof String) {
				theClass = Class.forName((String) x);
			} else { // x is null
				theClass = Class.forName(className);
			}
			Persistent obj = (Persistent) theClass.newInstance();
			obj.setState(state);
			return obj;
		} catch (ClassNotFoundException e) {
			throw new PersistenceException(e);
		} catch (InstantiationException e) {
			throw new PersistenceException(e);
		} catch (IllegalAccessException e) {
			throw new PersistenceException(e);
		}
	}

}
