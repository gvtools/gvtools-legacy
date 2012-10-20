/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package com.iver.utiles.connections;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.iver.utiles.FileUtils;

/**
 * Class responsible of the operations on the database.
 * 
 * @author Vicente Caballero Navarro
 */
public class ConnectionDB {
	public static ConnectionDB instance = new ConnectionDB();
	private ConnectionTrans[] connTrans;

	/**
	 * Creates a new one ConnectionDB.
	 */
	private ConnectionDB() {
	}

	public void setConnTrans(ConnectionTrans[] ct) {
		connTrans = ct;
	}

	/**
	 * It returns a static instance of the class.
	 * 
	 * @return instance
	 */
	public static ConnectionDB getInstance() {
		return instance;
	}

	/**
	 * It verifies that the connection is correct
	 * 
	 * @param ct
	 *            Data of the connection
	 * @param driver
	 *            Driver
	 * 
	 * @throws ConnectionException
	 */
	public boolean testDB(ConnectionTrans ct) throws ConnectionException {
		/*
		 * try { Class.forName(driver.getDriverString()); } catch
		 * (java.lang.ClassNotFoundException e) { throw new
		 * ConnectionException("Driver no encontrado", e); }
		 */
		String url = ct.getConnBeginning() + "//" + ct.getHost() + ":"
				+ ct.getPort() + "/" + ct.getDb();
		String user = ct.getUser();
		String password = ct.getPassword();
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
				url, user, password);
		Connection connection = null;

		try {
			connection = connectionFactory.createConnection();
		} catch (SQLException e) {
			throw new ConnectionException(
					JDBCManager.getTranslation("fallo_crear_conexion"), e);
		} catch (Exception e) {
			throw new ConnectionException(
					JDBCManager.getTranslation("fallo_crear_conexion"), e);
		}

		try {
			connection.close();
		} catch (SQLException e) {
			throw new ConnectionException(
					JDBCManager.getTranslation("fallo_crear_conexion"), e);
		}
		return true;
	}

	/**
	 * Returns a connection by Name.
	 * 
	 * @param url_name
	 *            Name of connection
	 * 
	 * @return Connection
	 * 
	 * @throws ConnectionException
	 */
	public Connection getConnectionByName(String url_name)
			throws ConnectionException {
		Connection conn = null;

		try {
			conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:"
					+ url_name);
		} catch (SQLException e) {
			throw new ConnectionException(
					JDBCManager
							.getTranslation("fallo_obtener_conexion_existente"),
					e);
		}

		return conn;
	}

	/**
	 * Returns a connection from the data
	 * 
	 * @param ct
	 *            Data connection
	 * 
	 * @return Connection
	 * 
	 * @throws ConnectionException
	 */
	public Connection getConnection(ConnectionTrans ct)
			throws ConnectionException {
		// VectorialJDBCDriver driver = getDriver(ct.getDriver());

		String name = ct.getHost() + "_" + ct.getName();
		Connection conn = null;

		/*
		 * try { Class.forName(driver.getDriverString()); } catch
		 * (ClassNotFoundException e) { throw new
		 * ConnectionException("Driver no encontrado", e); }
		 */
		setupDriver(ct);
		conn = getConnectionByName(name);

		return conn;
	}

	/**
	 * Returns a Driver by name.
	 * 
	 * @param name
	 *            Driver´s name
	 * 
	 * @return Driver
	 * 
	 * @throws ConnectionException
	 */
	/*
	 * public VectorialJDBCDriver getDriver(String name) throws
	 * ConnectionException { VectorialJDBCDriver driver = null;
	 * 
	 * String[] drivers = LayerFactory.getDM().getDriverNames();
	 * 
	 * for (int i = 0; i < drivers.length; i++) { try { if
	 * (LayerFactory.getDM().getDriver(drivers[i]) instanceof
	 * VectorialJDBCDriver) { VectorialJDBCDriver d = (VectorialJDBCDriver)
	 * LayerFactory.getDM() .getDriver(drivers[i]);
	 * 
	 * if (d.getName().equals(name)) { driver = d; } } } catch
	 * (DriverLoadException e) { throw new
	 * ConnectionException("Driver no encontrado", e); } }
	 * 
	 * return driver; }
	 */
	/**
	 * Registers in the driverpool a new connection
	 * 
	 * @param ct
	 *            Data connection
	 * @param driver
	 *            Driver
	 * 
	 * @throws ConnectionException
	 */
	public void setupDriver(ConnectionTrans ct) throws ConnectionException {
		String url = ct.getConnBeginning() + "//" + ct.getHost() + ":"
				+ ct.getPort() + "/" + ct.getDb();
		String user = ct.getUser();
		String password = ct.getPassword();
		String name = ct.getHost() + "_" + ct.getName();
		ObjectPool connectionPool = new GenericObjectPool();
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
				url, user, password);

		try {
			PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
					connectionFactory, connectionPool, null, null, false, true);
		} catch (Exception e) {
			throw new ConnectionException(
					JDBCManager.getTranslation("fallo_crear_pool"), e);
		}

		try {
			Class.forName("org.apache.commons.dbcp.PoolingDriver");
		} catch (ClassNotFoundException e) {
			throw new ConnectionException("Clase : "
					+ "org.apache.commons.dbcp.PoolingDriver", e);
		}

		PoolingDriver driverPool;

		try {
			driverPool = (PoolingDriver) DriverManager
					.getDriver("jdbc:apache:commons:dbcp:");
		} catch (SQLException e) {
			throw new ConnectionException(
					JDBCManager.getTranslation("fallo_registrar_conexion"), e);
		}

		driverPool.registerPool(name, connectionPool);
		ct.setConnected(true);
	}

	/**
	 * Executes a query
	 * 
	 * @param SQL
	 *            query
	 * @param name
	 *            Driver´s name
	 * 
	 * @throws ConnectionException
	 */
	public void ejecutaSQLnors(String SQL, String name)
			throws ConnectionException {
		Connection conn = null;
		Statement s = null;

		try {
			try {
				conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:"
						+ name);

				s = conn.createStatement();
				s.execute(SQL);
			} catch (SQLException e) {
				throw new ConnectionException(
						JDBCManager.getTranslation("fallo_realizar_consulta"),
						e);
			}
		} finally {
			try {
				s.close();
			} catch (Exception e) {
			}

			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Returns the names of the tables
	 * 
	 * @param conn
	 *            Connection
	 * 
	 * @return Array of string with the names of the tables.
	 * 
	 * @throws ConnectionException
	 */
	public String[] getTableNames(Connection conn) throws ConnectionException {
		// Connection conn=getConnectionByName(name);
		ArrayList tableNames = new ArrayList();
		String nombreTablas = "%"; // Listamos todas las tablas
		String[] tipos = new String[1]; // Listamos sólo tablas
		tipos[0] = "TABLE";

		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet tablas = dbmd.getTables(null, null, nombreTablas, tipos);

			boolean seguir = tablas.next();

			while (seguir) {
				// Mostramos sólo el nombre de las tablas, guardado
				// en la columna "TABLE_NAME"
				System.out.println(tablas.getString(tablas
						.findColumn("TABLE_NAME")));
				tableNames
						.add(tablas.getString(tablas.findColumn("TABLE_NAME")));
				seguir = tablas.next();
			}
		} catch (SQLException e) {
			throw new ConnectionException(
					JDBCManager.getTranslation("fallo_obtener_tablas"), e);
		}

		return (String[]) tableNames.toArray(new String[0]);
	}

	/**
	 * Keeps in disk the data of the connection
	 * 
	 * @param ct
	 *            Data connection
	 * 
	 * @throws IOException
	 */
	public void setPersistence(ConnectionTrans ct) throws IOException {
		String name = ct.getHost() + "_" + ct.getName();
		Properties properties = new Properties();
		properties.put("jdbc.drivers", ct.getDriver());
		properties.put("jdbc.name", ct.getName());
		properties.put("jdbc.host", ct.getHost());
		properties.put("jdbc.port", ct.getPort());
		properties.put("jdbc.username", ct.getUser());
		properties
				.put("jdbc.savepassword", String.valueOf(ct.isSavePassword()));

		if (ct.isSavePassword()) {
			properties.put("jdbc.password", ct.getPassword());
		}

		properties.put("jdbc.database", ct.getDb());
		properties.put("jdbc.connBeginning", ct.getConnBeginning());
		boolean success = true;
		File file = null;

		String directory = FileUtils.getAppHomeDir() + "connections";

		if (!new File(directory).exists()) {
			file = new File(directory);
			success = file.mkdirs();
		}

		if (success) {
			File f = new File(directory + File.separator + name + ".properties");
			f.createNewFile();

			FileOutputStream out = new FileOutputStream(f);
			properties.store(out, name);
			out.close();
		}
	}

	public void delPersistence(String name) {
		String directory = FileUtils.getAppHomeDir() + "connections";
		File dir = new File(directory);
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName()
					.substring(0, files[i].getName().length() - 11)
					.equals(name)) {
				files[i].delete();
			}
		}
	}

	/**
	 * Returns the data of the connection
	 * 
	 * @return Array of data connections
	 * 
	 * @throws IOException
	 */
	public ConnectionTrans[] getPersistence() throws IOException {
		ArrayList conns = new ArrayList();
		String directory = FileUtils.getAppHomeDir() + "connections";
		File dir = new File(directory);
		File[] files = dir.listFiles();
		if (files == null)
			return null;
		for (int i = 0; i < files.length; i++) {
			Properties properties = new Properties();
			FileInputStream in = new FileInputStream(files[i]);
			properties.load(in);
			in.close();

			ConnectionTrans ct = new ConnectionTrans();
			ct.setDriver(properties.get("jdbc.drivers").toString());
			ct.setName(properties.get("jdbc.name").toString());
			ct.setHost(properties.get("jdbc.host").toString());
			ct.setPort(properties.get("jdbc.port").toString());
			ct.setUser(properties.get("jdbc.username").toString());

			boolean isSave = Boolean.valueOf(
					properties.get("jdbc.savepassword").toString())
					.booleanValue();
			ct.setSavePassword(isSave);

			if (isSave) {
				ct.setPassword(properties.get("jdbc.password").toString());
			}

			ct.setDb(properties.get("jdbc.database").toString());
			ct.setConnBegining(properties.get("jdbc.connBeginning").toString());
			conns.add(ct);
		}

		return (ConnectionTrans[]) conns.toArray(new ConnectionTrans[0]);
	}

	public ConnectionTrans[] getDefaultTrans() {
		return connTrans;
	}

}
