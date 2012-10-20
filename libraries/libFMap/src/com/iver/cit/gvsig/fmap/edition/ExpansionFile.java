package com.iver.cit.gvsig.fmap.edition;

import java.util.HashMap;

import com.iver.cit.gvsig.exceptions.expansionfile.CloseExpansionFileException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.expansionfile.OpenExpansionFileException;
import com.iver.cit.gvsig.fmap.core.IRow;

/**
 * Maneja el fichero de extensión en el que se almacenan las modificacionesy
 * adiciónes durante la edición. Los índices que devuelve esta clase en sus
 * métodos addRow y modifyRow son invariables, es decir, que si se invoca un
 * método addRow que retorna un 8, independientemente de las operaciones que se
 * realicen posteriormente, una llamada a getRow(8) retornará dicha fila. Si
 * esta geometría es eliminada posteriormente, se retornará un null. Esto último
 * se cumple mientras no se invoque el método compact, mediante el que se
 * reorganizan las geometrías dejando en el fichero sólo las que tienen validez
 * en el momento de realizar la invocación.
 */
public interface ExpansionFile {
	/**
	 * Añade una geometria al final del fichero y retorna el índice que ocupa
	 * esta geometria en el mismo
	 * 
	 * @param row
	 *            DOCUMENT ME!
	 * @param status
	 *            TODO
	 * @param indexInternalFields
	 *            fields that where valid when this row was added.
	 * 
	 * @return calculated index of the new row.
	 * @throws ExpansionFileWriteException
	 *             TODO
	 */
	int addRow(IRow row, int status, int indexInternalFields)
			throws ExpansionFileWriteException;

	/**
	 * Modifica la index-ésima geometría del fichero devolviendo la posición en
	 * la que se pone la geometria modificada.
	 * 
	 * @param calculated
	 *            index of row to be modified
	 * @param row
	 *            newRow that replaces the old row.
	 * 
	 * @return new calculated index of the modified row.
	 * @throws ExpansionFileWriteException
	 *             TODO
	 */
	int modifyRow(int index, IRow row, int indexInternalFields)
			throws ExpansionFileWriteException;

	/**
	 * Obtiene la geometria que hay en el índice 'index' o null si la geometría
	 * ha sido invalidada.
	 * 
	 * @param index
	 *            caculatedIndex of the row to be read.
	 * @return
	 * @throws ExpansionFileReadException
	 *             TODO
	 */
	IRowEdited getRow(int index) throws ExpansionFileReadException;

	/**
	 * Invalida una geometría, de cara a una futura compactación del fichero
	 * 
	 * @param index
	 *            DOCUMENT ME!
	 */
	// void invalidateRow(int index);

	/**
	 * Realiza una compactación del fichero que maneja esta clase
	 * 
	 * @param relations
	 *            DOCUMENT ME!
	 */
	void compact(HashMap relations);

	/**
	 * Devuelve el número de geometrías del fichero.
	 * 
	 * @return número de geometrías.
	 */
	// int getRowCount();

	/**
	 * Mueve el puntero de escritura de manera que las siguientes escrituras
	 * machacarán la última fila
	 */
	void deleteLastRow();

	/**
	 * Abre el fichero de expansión para comenzar la edición
	 * 
	 * @throws OpenExpansionFileException
	 *             TODO
	 */
	void open() throws OpenExpansionFileException;

	/**
	 * Cierra el fichero de expansión al terminar la edición
	 * 
	 * @throws CloseExpansionFileException
	 *             TODO
	 */
	void close() throws CloseExpansionFileException;

	/**
	 * @param previousExpansionFileIndex
	 */
	// void validateRow(int previousExpansionFileIndex);
	// BitSet getInvalidRows();

	int getSize();
}
