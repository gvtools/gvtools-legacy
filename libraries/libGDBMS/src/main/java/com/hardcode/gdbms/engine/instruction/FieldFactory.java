package com.hardcode.gdbms.engine.instruction;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;

/**
 * Clase que se encarga de crear los objetos Field de las instrucciones Select a
 * partir del nombre del campo
 * 
 * @author Fernando González Cortés
 */
public class FieldFactory {
	/**
	 * Dada una lista de tablas y el nombre de un campo, devuelve el objeto
	 * Field conteniendo la información del índice de la tabla a la que
	 * pertenece el campo y el índice del campo dentro de dicha tabla
	 * 
	 * @param tables
	 *            Array de tablas donde se buscará el campo
	 * @param fieldName
	 *            Nombre del campo que se está buscando
	 * @param source
	 *            Fuente de datos para el campo que se crea. El campo obtendrá
	 *            sus valores de dicha fuente.
	 * 
	 * @return Objeto Field
	 * @throws AmbiguousFieldNameException
	 *             Si hay dos tablas que pueden tener el campo
	 * @throws FieldNotFoundException
	 *             Si el campo no se encuentra en ninguna de las tablas
	 * @throws ReadDriverException
	 *             TODO
	 */
	public static Field createField(DataSource[] tables, String fieldName,
			DataSource source) throws AmbiguousFieldNameException,
			FieldNotFoundException, ReadDriverException {
		if (fieldName.indexOf(".") != -1) {
			return createWithTable(tables, fieldName, source);
		} else {
			return createWithoutTable(tables, fieldName, source);
		}
	}

	/**
	 * Crea un campo que viene especificado por el nombre de campo sin el nombre
	 * de la tabla a la que pertenece
	 * 
	 * @param tables
	 *            Array de tablas donde se buscará el campo
	 * @param fieldName
	 *            Nombre del campo que se está buscando
	 * @param source
	 *            Fuente de datos para el campo que se crea
	 * 
	 * @return Objeto Field
	 * @throws FieldNotFoundException
	 *             Si el campo no se encuentra en ninguna de las tablas
	 * @throws AmbiguousFieldNameException
	 *             Si hay dos tablas que pueden tener el campo
	 * @throws ReadDriverException
	 *             TODO
	 */
	private static Field createWithoutTable(DataSource[] tables,
			String fieldName, DataSource source) throws FieldNotFoundException,
			AmbiguousFieldNameException, ReadDriverException {
		int retIndex = -1;
		int dataSource = -1;

		for (int i = 0; i < tables.length; i++) {
			int index = tables[i].getFieldIndexByName(fieldName);

			if (index != -1) {
				// Si ya se había encontrado uno
				if (retIndex != -1) {
					throw new AmbiguousFieldNameException(fieldName);
				} else {
					retIndex = index;
					dataSource = i;
				}
			}
		}

		if (retIndex == -1) {
			throw new FieldNotFoundException(fieldName);
		}

		Field ret = new Field();
		ret.setDataSourceIndex(dataSource);
		ret.setFieldId(retIndex);
		ret.setTables(tables);
		ret.setDataSource(source);

		return ret;
	}

	/**
	 * Crea un campo que viene especificado por el nombre de la tabla seguido de
	 * "." y del nombre del campo de dicha tabla
	 * 
	 * @param tables
	 *            Array de tablas donde se buscará el campo
	 * @param fieldName
	 *            Nombre del campo que se está buscando
	 * @param source
	 *            Fuente de datos para el campo que se crea
	 * 
	 * @return Objeto Field
	 * @throws FieldNotFoundException
	 *             Si el campo no se encuentra en ninguna de las tablas
	 * @throws AmbiguousFieldNameException
	 *             Si hay dos tablas que pueden tener el campo
	 * @throws ReadDriverException
	 *             TODO
	 */
	private static Field createWithTable(DataSource[] tables, String fieldName,
			DataSource source) throws FieldNotFoundException,
			AmbiguousFieldNameException, ReadDriverException {
		int retIndex = -1;
		int dataSource = -1;

		// Se obtiene el nombre de la tabla y del campo
		String[] nombres = fieldName.split("[.]");
		String tableName = nombres[0].trim();
		fieldName = nombres[1].trim();

		for (int i = 0; i < tables.length; i++) {
			if (tables[i].getName().equals(tableName)) {
				retIndex = tables[i].getFieldIndexByName(fieldName);
				dataSource = i;

				break;
			}
		}

		if (retIndex == -1) {
			throw new FieldNotFoundException(fieldName);
		}

		Field ret = new Field();
		ret.setDataSourceIndex(dataSource);
		ret.setFieldId(retIndex);
		ret.setTables(tables);
		ret.setDataSource(source);

		return ret;
	}
}
