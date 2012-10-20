/*
 * Created on 16-oct-2004
 */
package com.hardcode.gdbms.engine.data.indexes;

import java.io.IOException;

/**
 * Interfaz a implementar por los índices sobre las tablas. Esta interfaz se
 * utiliza al filtrar una tabla, en la que se añaden indices a la tabla
 * secuencialmente. Una vez se invoca el método indexSetComplete ya no se pueden
 * meter más índices
 * 
 * @author Fernando González Cortés
 */
public interface VariableIndexSet extends BaseIndexSet {
	/**
	 * Cierra el conjunto de índices para el almacenamiento, a partir de una
	 * llamada a este método no se puede escribir ningún índice más, sólamente
	 * se pueden leer
	 * 
	 * @throws IOException
	 *             Si se produce un fallo al cerrar los streams de salida
	 */
	public void indexSetComplete() throws IOException;

	/**
	 * Añade un índice al conjunto de índices
	 * 
	 * @param value
	 *            índice de la fila a la que apunta el índice que se quiere
	 *            añadir
	 * 
	 * @throws IOException
	 *             Si se produce un fallo al escribir el índice
	 */
	public void addIndex(long value) throws IOException;

	/**
	 * Abre el almacenamiento del índice para la escritura de los índices. En
	 * caso de un almacenamiento permanente se usará un fichero temporal
	 * 
	 * @throws IOException
	 *             Si se produce un fallo al abrir
	 */
	public void open() throws IOException;

	/**
	 * Obtiene los índices del conjunto de índices en un array
	 * 
	 * @return long[]
	 * 
	 * @throws IOException
	 */
	public long[] getIndexes() throws IOException;
}
