package com.hardcode.gdbms.engine.data.indexes;

import java.io.IOException;

/**
 * Implementación del conjunto de índices que guarda en memoria hasta un límite
 * y a partir de ese límite pasa todos los índices a disco
 * 
 * @author Fernando González Cortés
 */
public class IndexSetImpl implements VariableIndexSet {
	private VariableIndexSet set;
	private boolean complete = false;

	/**
	 * Número de índices que se guardan en memoria, a partir de dicho número se
	 * guarda en disco
	 */
	private int limit;

	/**
	 * Creates a new IndexSetImpl object.
	 */
	public IndexSetImpl() {
		limit = IndexFactory.MEMORY_THRESHOLD;
		set = new VariableMemoryIndexSet(IndexFactory.MEMORY_THRESHOLD);
	}

	/**
	 * Creates a new IndexSetImpl object.
	 * 
	 * @param limit
	 *            límite a partir del cual se guardan todos los índices en disco
	 */
	public IndexSetImpl(int limit) {
		this.limit = limit;
		set = new VariableMemoryIndexSet(limit);
	}

	/**
	 * Añade un índice al conjunto
	 * 
	 * @param index
	 *            índice a añadir
	 * 
	 * @throws IOException
	 *             Si se produce un error al escribir en el disco
	 * @throws RuntimeException
	 */
	public synchronized void addIndex(long index) throws IOException {
		if (complete) {
			throw new RuntimeException(
					"Cannot add more indexes after indexSetComplete");
		}

		if (set.getIndexCount() == limit) {
			// Se sustituye el índice de memoria por el índice de disco
			VariableDiskIndexSet newSet = new VariableDiskIndexSet();
			newSet.open();
			newSet.addAll(set);
			set = newSet;
		}

		set.addIndex(index);
	}

	/**
	 * Devuelve el índice nth-ésimo si se invocó previamente a indexSetComplete
	 * y lanza una excepción en caso contrario
	 * 
	 * @param nth
	 *            índice de índice que se quiere obtener
	 * 
	 * @return indice nth-ésimo
	 * 
	 * @throws IOException
	 *             Si se produce un error accediendo a disco
	 * @throws RuntimeException
	 */
	public long getIndex(long nth) throws IOException {
		if (!complete) {
			throw new RuntimeException("Must call indexSetComplete First");
		}

		return set.getIndex(nth);
	}

	/**
	 * Devuelve el número de índices si se invocó previamente a indexSetComplete
	 * y lanza una excepción en caso contrario
	 * 
	 * @return número de índices
	 * 
	 * @throws RuntimeException
	 */
	public long getIndexCount() {
		if (!complete) {
			throw new RuntimeException("Must call indexSetComplete First");
		}

		return set.getIndexCount();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.indexes.VariableIndexSet#indexSetComplete()
	 */
	public void indexSetComplete() throws IOException {
		complete = true;
		set.indexSetComplete();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.indexes.VariableIndexSet#open(java.io.File)
	 */
	public void open() throws IOException {
		set.open();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.indexes.VariableIndexSet#close()
	 */
	public void close() throws IOException {
		set.close();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws IOException
	 * 
	 * @see com.hardcode.gdbms.engine.data.indexes.VariableIndexSet#getIndexes()
	 */
	public long[] getIndexes() throws IOException {
		return set.getIndexes();
	}
}
