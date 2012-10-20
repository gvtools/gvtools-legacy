/*
 * Created on 23-oct-2004
 */
package com.hardcode.gdbms.engine.data.indexes;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando González Cortés
 */
public abstract class MemoryIndexSet {
	protected long[] indexes;

	/**
	 * @see com.hardcode.gdbms.engine.data.indexes.VariableIndexSet#getIndex(long)
	 */
	public long getIndex(long nth) {
		return indexes[(int) nth];
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.indexes.VariableIndexSet#close()
	 */
	public void close() {
	}
}
