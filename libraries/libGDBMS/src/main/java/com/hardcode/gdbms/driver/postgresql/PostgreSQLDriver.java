package com.hardcode.gdbms.driver.postgresql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.hardcode.gdbms.engine.data.driver.AbstractJDBCDriver;

/**
 *
 */
public class PostgreSQLDriver extends AbstractJDBCDriver {
	private static Exception driverException;

	static {
		try {
			Class.forName("org.postgresql.Driver").newInstance();
		} catch (Exception ex) {
			driverException = ex;
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param host
	 *            DOCUMENT ME!
	 * @param port
	 *            DOCUMENT ME!
	 * @param dbName
	 *            DOCUMENT ME!
	 * @param user
	 *            DOCUMENT ME!
	 * @param password
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws SQLException
	 * @throws RuntimeException
	 *             DOCUMENT ME!
	 * 
	 * @see com.hardcode.gdbms.engine.data.driver.DBDriver#connect(java.lang.String)
	 */
	public Connection getConnection(String host, int port, String dbName,
			String user, String password) throws SQLException {
		if (driverException != null) {
			throw new RuntimeException(driverException);
		}

		String connectionString = "jdbc:postgresql://" + host;

		if (port != -1) {
			connectionString += (":" + port);
		}

		connectionString += ("/" + dbName);

		if (user != null) {
			connectionString += ("?user=" + user + "&password=" + password);
		}

		return DriverManager.getConnection(connectionString);
	}

	/**
	 * @see com.hardcode.driverManager.Driver#getName()
	 */
	public String getName() {
		return "PostgreSQL Alphanumeric";
	}

	public String getDefaultPort() {
		return "5432";
	}

	// public String[] getAvailableTables(
	// Connection co,
	// String schema) throws SQLException {
	//
	// String sql = "select c.relname FROM pg_catalog.pg_class c " +
	// "LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace " +
	// "WHERE c.relkind IN ('r','v','') AND n.nspname = '" + schema + "' " +
	// "AND pg_catalog.pg_table_is_visible(c.oid);";
	//
	// Statement st = co.createStatement(ResultSet.TYPE_FORWARD_ONLY,
	// ResultSet.CONCUR_READ_ONLY);
	// ResultSet res = st.executeQuery(sql);
	//
	// ArrayList str_list = new ArrayList();
	// while (res.next()) {
	// str_list.add(res.getString(1));
	// }
	// res.close();
	// st.close();
	// return (String[]) str_list.toArray(new String[0]) ;
	// }

}

// [eiel-gestion-conexiones]