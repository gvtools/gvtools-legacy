package com.hardcode.gdbms.engine.data.indexes;

/**
 * Implementación de un conjunto de índices en memoria, aunque puede haber un
 * conjunto de Long.MAXVALUE índices, en memoria, el tamaño máximo es de
 * Integer.MAXVALUE. Otras implementaciones de VariableIndexSet en memoria
 * pueden no tener esta restricción
 * 
 * @author Fernando González Cortés
 */
public class VariableMemoryIndexSet extends MemoryIndexSet implements
		VariableIndexSet {
	private int count = 0;

	/**
	 * Creates a new MemoryIndexSet object.
	 * 
	 * @param initialCapacity
	 *            Capacidad inicial del conjunto de índices. Deberá de ser la
	 *            capacidad máxima que pueda llegar a tener el conjunto
	 */
	public VariableMemoryIndexSet(int initialCapacity) {
		indexes = new long[initialCapacity];
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.indexes.VariableIndexSet#open(java.io.File)
	 */
	public void open() {
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.indexes.VariableIndexSet#indexSetComplete
	 */
	public void indexSetComplete() {
		long[] aux = new long[count];
		System.arraycopy(indexes, 0, aux, 0, count);
		indexes = aux;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.indexes.VariableIndexSet#addIndex(long)
	 */
	public void addIndex(long index) {
		indexes[count] = index;
		count++;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.indexes.VariableIndexSet#getIndexCount(long)
	 */
	public long getIndexCount() {
		return count;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.indexes.VariableIndexSet#getIndexes()
	 */
	public long[] getIndexes() {
		return this.indexes;
	}
}
