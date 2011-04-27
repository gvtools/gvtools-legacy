/*
 * Created on 23-oct-2004
 */
package com.hardcode.gdbms.engine.data.indexes;

import java.io.File;
import java.io.IOException;


/**
 * Los índices fijos se establecen sobre los campos de los DataSource para
 * acelerar el acceso a un determinado valor
 *
 * @author Fernando González Cortés
 */
public interface FixedIndexSet extends BaseIndexSet {
	/**
	 * establece el índice 'index'-ésimo para que apunte a la fila 'value'
	 *
	 * @param index índice que se quiere cambiar
	 * @param value índice de la fila a la que apunta este índice
	 *
	 * @throws IOException Si se produce un fallo al escribir el índice
	 */
	public void setIndex(long index, long value) throws IOException;

	/**
	 * Abre el almacenamiento del índice para la escritura de los índices
	 *
	 * @param f fichero en el que se guardará el índice
	 *
	 * @throws IOException Si se produce un fallo al abrir
	 */
	public void open(File f) throws IOException;
}
