package com.hardcode.gdbms.engine.data.indexes.hashMap;

import java.io.IOException;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public interface PositionIterator {
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean hasNext();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public int next() throws IOException;
}
