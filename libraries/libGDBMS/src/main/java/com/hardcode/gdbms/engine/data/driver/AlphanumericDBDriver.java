package com.hardcode.gdbms.engine.data.driver;

import java.sql.Connection;
import java.sql.SQLException;

import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;

/**
 *
 */
public interface AlphanumericDBDriver extends DBDriver {

	/**
	 * Ejecuta la instruccion que se pasa como parámetro en el gestor de base de
	 * datos en el que está conectado el driver. Los nombres de las tablas de la
	 * instrucción están preparados para el sistema de gestión donde se
	 * ejecutará la instrucción
	 * 
	 * @param con
	 *            Conexión con la cual se ha de obtener el ResultSet
	 * @param sql
	 *            Instrucción SQL a ejecutar
	 * @param props
	 *            Properties of the overlaying DataSource layer
	 * 
	 * @throws SQLException
	 *             Si se produce un error ejecutando la instrucción SQL public
	 *             void openTable(Connection con, String table, HasProperties
	 *             props) throws SQLException;
	 * @throws OpenDriverException
	 */
	public void open(Connection con, String sql) throws SQLException,
			OpenDriverException;

	/**
	 * Devuelve el "esquema" del origen de datos subyacente al driver.
	 * (azabala). Es necesario porque determinados drivers de escritura (los de
	 * bbdd que no soportan resultsets updatables) necesitan conocer el esquema
	 * de la tabla subyacente (nombre, p. ej) para poder construir al vuelo la
	 * consulta SQL final.
	 * 
	 * @throws ReadDriverException
	 *             TODO
	 * */
	public ITableDefinition getTableDefinition() throws ReadDriverException;
}
