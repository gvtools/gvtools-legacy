package com.hardcode.gdbms.engine.internalExceptions;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando González Cortés
 */
public class InternalExceptionListenerSupport {
	private ArrayList listeners = new ArrayList();

	/**
	 * DOCUMENT ME!
	 * 
	 * @param listener
	 *            DOCUMENT ME!
	 */
	public void addInternalExceptionListener(InternalExceptionListener listener) {
		listeners.add(listener);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param listener
	 *            DOCUMENT ME!
	 */
	public void removeInternalExceptionListener(
			InternalExceptionListener listener) {
		listeners.remove(listener);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param arg0
	 *            DOCUMENT ME!
	 */
	public void callExceptionRaised(
			com.hardcode.gdbms.engine.internalExceptions.InternalExceptionEvent arg0) {
		Iterator i = listeners.iterator();

		while (i.hasNext()) {
			((InternalExceptionListener) i.next()).exceptionRaised(arg0);
		}
	}
}
