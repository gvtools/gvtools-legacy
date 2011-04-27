package com.hardcode.gdbms.engine.data.indexes.hashMap;

/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public interface Index {
	/**
	 * Invocado cuando se va a comenzar una operación de escritura con la
	 * estructura de datos
	 *
	 * @throws IndexException DOCUMENT ME!
	 */
	public void start() throws IndexException;

	/**
	 * Invocado cuando se termina la operación de escritura con el índice
	 *
	 * @throws IndexException DOCUMENT ME!
	 */
	public void stop() throws IndexException;

	/**
	 * Añade la posición de un valor al índice. Posiblemente ya haya una o
	 * varias posiciones para dicho valor tomando como función de identidad el
	 * método equals de Value. En dicho caso se deberán mantener todas estas
	 *
	 * @param v Valor
	 * @param position posición del Valor dentro del DataSource
	 *
	 * @throws IndexException
	 */
	public void add(Object v, int position) throws IndexException;

	/**
	 * Obtiene un iterador para iterar sobre las posiciones sobre las que puede
	 * haber valores iguales al que se pasa como parámetro. No todas las
	 * posiciones se deben corresponder necesariamente con registros que
	 * contengan el valor buscado pero todas las posiciones de los registros
	 * que contengan value estarán en las posiciones que se retornen.
	 *
	 * @param v Value
	 *
	 * @return Objeto para iterar por las posiciones
	 *
	 * @throws IndexException
	 */
	public PositionIterator getPositions(Object v) throws IndexException;
}
