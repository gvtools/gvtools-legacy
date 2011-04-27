/*
 * Created on 23-oct-2004
 */
package com.hardcode.gdbms.engine.data.indexes;

import java.io.IOException;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public interface BaseIndexSet {
	/**
	 * Cierra el fichero de índices
	 *
	 * @throws IOException Si se produce un fallo al cerrar
	 */
	public void close() throws IOException;

	/**
	 * Devuelve el índice nth-ésimo si se invocó previamente a indexSetComplete
	 * y lanza una excepción en caso contrario
	 *
	 * @param nth índice del índice que se quiere obtener
	 *
	 * @return indice nth-ésimo
	 *
	 * @throws IOException Si se produce un fallo al recuperar el índice
	 */
	public long getIndex(long nth) throws IOException;

	/**
	 * Devuelve el número de índices si se invocó previamente a
	 * indexSetComplete y lanza una excepción en caso contrario
	 *
	 * @return Si se produce un fallo al obtener el número de índices
	 */
	public long getIndexCount();
}
