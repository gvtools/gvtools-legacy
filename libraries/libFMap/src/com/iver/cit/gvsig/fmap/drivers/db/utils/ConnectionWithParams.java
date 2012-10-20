package com.iver.cit.gvsig.fmap.drivers.db.utils;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.IConnection;

/**
 * Utility class to keep the connection parameters. It is used as a item in the
 * single connections manager tree and in the available connections combo box
 * (wizard db)
 * 
 * @author jldominguez
 * 
 */

public class ConnectionWithParams {

	private static Logger logger = Logger.getLogger(ConnectionWithParams.class
			.getName());

	private IConnection conn = null;
	private String connectionStr = "";
	private String drvName = "";
	private String user = "";
	private String pw = "";
	private String name = "";
	private boolean connected = false;

	private String host;
	private String port;
	private String db;

	private String schema = "";

	private boolean isNull = false;

	/**
	 * Utility constructor to indicate an empty item. It is used as the first
	 * item in the wizard's combo box so that when the wizard is loaded, no
	 * query is done to any database.
	 * 
	 */
	public ConnectionWithParams() {
		isNull = true;
	}

	/**
	 * Class Constructor.
	 * 
	 * @param _conn_str
	 *            connection string
	 * @param _c
	 *            connection object
	 * @param _drvName
	 *            driver name
	 * @param _user
	 *            user name
	 * @param _pw
	 *            password
	 * @param _name
	 *            connection name (freely chosen by user)
	 * @param _host
	 *            host's url
	 * @param _port
	 *            port number as a string
	 * @param _db
	 *            database name
	 * @param _isConn
	 *            whether the connection is open or not
	 */
	public ConnectionWithParams(String _conn_str, IConnection _c,
			String _drvName, String _user, String _pw, String _name,
			String _host, String _port, String _db, boolean _isConn) {

		connectionStr = _conn_str;
		connected = _isConn;
		conn = _c;
		drvName = _drvName;
		user = _user;
		pw = _pw;
		name = _name;

		host = _host;
		port = _port;
		db = _db;

		if (!connected) {
			pw = null;
			conn = null;
		}
	}

	/**
	 * Class Constructor.
	 * 
	 * @param _conn_str
	 *            connection string
	 * @param _c
	 *            connection object
	 * @param _drvName
	 *            driver name
	 * @param _user
	 *            user name
	 * @param _pw
	 *            password
	 * @param _name
	 *            connection name (freely chosen by user)
	 * @param _host
	 *            host's url
	 * @param _port
	 *            port number as a string
	 * @param _db
	 *            database name
	 * @param _schema
	 *            schema name or null if not used
	 * @param _isConn
	 *            whether the connection is open or not
	 */
	public ConnectionWithParams(String _conn_str, IConnection _c,
			String _drvName, String _user, String _pw, String _name,
			String _host, String _port, String _db, String _schema,
			boolean _isConn) {

		connectionStr = _conn_str;
		connected = _isConn;
		conn = _c;
		drvName = _drvName;
		user = _user;
		pw = _pw;
		name = _name;

		host = _host;
		port = _port;
		db = _db;
		schema = _schema;

		if (!connected) {
			pw = null;
			conn = null;
		}
	}

	public IConnection getConnection() {
		return conn;
	}

	public String getDrvName() {
		return drvName;
	}

	public String getPw() {
		return pw;
	}

	public String getUser() {
		return user;
	}

	/**
	 * Used to paint the object in lists and trees
	 */
	public String toString() {

		if (isNull) {
			return "";
		}

		if (connected) {
			return "[C] " + name + " (" + drvName + ")";
		} else {
			return name + " (" + drvName + ")";
		}
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean c) {
		connected = c;
	}

	/**
	 * Tries to connects the connection object with the given password.
	 * 
	 * @param _pw
	 *            password
	 * @throws SQLException
	 */
	public void connect(String _pw) throws DBException {
		throw new RuntimeException("Not implemented");
		//
		// try {
		// Driver _drv = LayerFactory.getDM().getDriver(drvName);
		//
		// if (_drv instanceof AlphanumericDBDriver) {
		// conn = SingleDBConnectionManager.getConnectionForDriver(
		// _drv, null, this.host, this.port, this.db, this.user, _pw);
		// } else {
		// if (_drv instanceof IVectorialDatabaseDriver) {
		// conn = ConnectionFactory.createConnection(this.connectionStr,
		// this.user, _pw);
		// } else {
		// logger.error("CONNECT: Unexpected driver type: " +
		// _drv.getClass().getName());
		// throw new DBException(new
		// Exception("CONNECT: Unexpected driver type: " +
		// _drv.getClass().getName()));
		// }
		// }
		// } catch (Exception e) {
		//
		// pw = null;
		// conn = null;
		// connected = false;
		// // e.printStackTrace();
		// throw new DBException(e);
		// }
		//
		// pw = _pw;
		// connected = true;
	}

	/**
	 * Disconnects the connection
	 * 
	 */
	public void disconnect() {

		try {
			conn.close();
		} catch (DBException e) {
			logger.error("While closing connection: " + e.getMessage(), e);
		}
		pw = null;
		conn = null;
		connected = false;
	}

	public String getConnectionStr() {
		return connectionStr;
	}

	public void setConnectionStr(String connectionStr) {
		this.connectionStr = connectionStr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * 
	 * @return whether or not this is the first item in the combo box.
	 */
	public boolean isNull() {
		return isNull;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String sch) {
		schema = sch;
	}

}

// [eiel-gestion-conexiones]