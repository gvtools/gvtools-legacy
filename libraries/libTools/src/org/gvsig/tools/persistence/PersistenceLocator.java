package org.gvsig.tools.persistence;

import org.gvsig.tools.locator.AbstractLocator;
import org.gvsig.tools.locator.Locator;
import org.gvsig.tools.locator.LocatorException;

public class PersistenceLocator extends AbstractLocator {

	private static final String LOCATOR_NAME = "PersistenceLocator";

	public static final String DATA_MANAGER_NAME = "PersistenceManager";

	private static final String DATA_MANAGER_DESCRIPTION = "PersistenceManager of gvSIG";

	/**
	 * Unique instance.
	 */
	private static final PersistenceLocator instance = new PersistenceLocator();

	/**
	 * Return the singleton instance.
	 * 
	 * @return the singleton instance
	 */
	public static PersistenceLocator getInstance() {
		return instance;
	}

	public String getLocatorName() {
		return LOCATOR_NAME;
	}

	/**
	 * Return a reference to PersistenceManager.
	 * 
	 * @return a reference to PersistenceManager
	 * @throws LocatorException
	 *             if there is no access to the class or the class cannot be
	 *             instantiated
	 * @see Locator#get(String)
	 */
	public static PersistenceManager getPersistenceManager()
			throws LocatorException {
		return (PersistenceManager) getInstance().get(DATA_MANAGER_NAME);
	}

	/**
	 * Registers the Class implementing the PersistenceManager interface.
	 * 
	 * @param clazz
	 *            implementing the DataManager interface
	 */
	public static void registerPersistenceManager(Class clazz) {
		getInstance().register(DATA_MANAGER_NAME, DATA_MANAGER_DESCRIPTION,
				clazz);
	}
}
