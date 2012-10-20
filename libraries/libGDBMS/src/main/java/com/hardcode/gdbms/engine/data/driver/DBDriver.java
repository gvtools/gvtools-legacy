package com.hardcode.gdbms.engine.data.driver;

import java.sql.Connection;
import java.sql.SQLException;

import com.hardcode.gdbms.engine.values.ValueWriter;

/**
 * Interfaz a implementar por los drivers que proporcionen interfaz JDBC
 * 
 * @author Fernando González Cortés
 */
public interface DBDriver extends ReadAccess, GDBMSDriver, ValueWriter {
	/**
	 * Método mediante el cual el driver surte de conexiones a la base de datos.
	 * 
	 * @param host
	 *            nombre o dirección IP del host al que se quiere conectar
	 * @param port
	 *            puerto al que se quiere conectar. -1 si es el puerto por
	 *            defecto
	 * @param dbName
	 *            Nombre de la base de datos a la que se quiere conectar
	 * @param user
	 *            Usuario de la conexión
	 * @param password
	 *            Password del usuario
	 * 
	 * @return Connection
	 * 
	 * @throws SQLException
	 *             Si se produce algún error
	 */
	Connection getConnection(String host, int port, String dbName, String user,
			String password) throws SQLException;

	/**
	 * Executes an instruction against the server
	 * 
	 * @param con
	 *            Connection used to execute the instruction
	 * @param sql
	 *            Instruction to execute
	 * @param props
	 *            Properties of the overlaying DataSource layer
	 * 
	 * @throws SQLException
	 *             If the execution fails
	 */
	public void execute(Connection con, String sql) throws SQLException;

	/**
	 * Free any resource reserved in the open method
	 * 
	 * @throws SQLException
	 *             If the free fails
	 */
	public void close() throws SQLException;

	/**
	 * Gets the name of the table as it's stored on the system tables. The
	 * result of this method is used in calls to methods as
	 * DatabaseMetadata.getPrimaryKeys
	 * 
	 * @param tablename
	 *            table name
	 * 
	 * @return String
	 */
	public String getInternalTableName(String tablename);

	/**
	 * @return default port used by thsese type of DB
	 */
	public String getDefaultPort();

	/**
	 * @return list available tables for connection and schema. null if it's not
	 *         possible
	 */
	// public String[] getAvailableTables(Connection co, String schema) throws
	// SQLException;
}

// [eiel-gestion-conexiones]