package com.hardcode.gdbms.engine.data.driver;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;

/**
 * Interfaz que define los métodos de acceso de lectura
 * 
 * @author Fernando González Cortés
 */
public interface ReadAccess {
	public final static int FIELD_TYPE_BOOLEAN = 0;
	public final static int FIELD_TYPE_STRING = 5;
	public final static int FIELD_TYPE_DOUBLE = 4;
	public final static int FIELD_TYPE_FLOAT = 3;
	public final static int FIELD_TYPE_LONGINT = 2;
	public final static int FIELD_TYPE_INT = 1;

	/**
	 * Obtiene el valor que se encuentra en la fila y columna indicada
	 * 
	 * @param rowIndex
	 *            fila
	 * @param fieldId
	 *            columna
	 * 
	 * @return subclase de Value con el valor del origen de datos. Never null
	 *         (use ValueFactory.createNullValue() instead)
	 * @throws ReadDriverException
	 *             TODO
	 */
	public abstract Value getFieldValue(long rowIndex, int fieldId)
			throws ReadDriverException;

	/**
	 * Obtiene el número de campos del DataSource
	 * 
	 * @return
	 * @throws ReadDriverException
	 *             TODO
	 */
	public abstract int getFieldCount() throws ReadDriverException;

	/**
	 * Devuelve el nombre del campo fieldId-ésimo
	 * 
	 * @param fieldId
	 *            índice del campo cuyo nombre se quiere obtener
	 * 
	 * @return
	 * @throws ReadDriverException
	 *             TODO
	 */
	public abstract String getFieldName(int fieldId) throws ReadDriverException;

	/**
	 * Obtiene el número de registros del DataSource
	 * 
	 * @return
	 * @throws ReadDriverException
	 *             TODO
	 */
	public abstract long getRowCount() throws ReadDriverException;

	/**
	 * Devuelve el tipo del campo iésimo. Devuelve una constante de la clase
	 * java.sql.Types
	 * 
	 * @param i
	 *            índice del campo cuyo tipo se quiere conocer
	 * 
	 * @return Class
	 * @throws ReadDriverException
	 *             TODO
	 */
	public abstract int getFieldType(int i) throws ReadDriverException;

	/**
	 * Devuelve el ancho del campo iésimo.
	 * 
	 * @param i
	 *            índice del campo cuyo ancho se quiere conocer
	 * 
	 * @return int
	 * @throws ReadDriverException
	 *             TODO
	 */
	public abstract int getFieldWidth(int i) throws ReadDriverException;
}
