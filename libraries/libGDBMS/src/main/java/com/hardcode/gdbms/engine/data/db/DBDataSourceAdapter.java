package com.hardcode.gdbms.engine.data.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.engine.data.AbstractDataSource;
import com.hardcode.gdbms.engine.data.SourceInfo;
import com.hardcode.gdbms.engine.data.driver.AlphanumericDBDriver;
import com.hardcode.gdbms.engine.data.driver.DBDriver;
import com.hardcode.gdbms.engine.data.driver.GDBMSDriver;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.Value;

/**
 * Adaptador de la interfaz DBDriver a la interfaz DataSource. Adapta las
 * interfaces de los drivers de base de datos a la interfaz DataSource.
 * 
 * @author Fernando González Cortés
 */
public class DBDataSourceAdapter extends AbstractDataSource implements
		DBDataSource {
	/*
	 * datos de la conexión
	 */
	protected String host;
	protected int port;
	protected String dbName;
	protected String user;
	protected String password;

	// driver de base de datos
	protected DBDriver driver;

	// objetos jdbc
	protected Connection con;

	// Instrucción sql completa que representa el dataSource
	protected String sql;

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getRowCount()
	 */
	public long getRowCount() throws ReadDriverException {
		return ((DBDriver) getDriver()).getRowCount();
	}

	/**
	 * Establece el driver
	 * 
	 * @param driver
	 *            The driver to set.
	 */
	public void setDriver(DBDriver driver) {
		this.driver = driver;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldCount()
	 */
	public int getFieldCount() throws ReadDriverException {
		return ((DBDriver) getDriver()).getFieldCount();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws ReadDriverException {
		return ((DBDriver) getDriver()).getFieldName(fieldId);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldType(int)
	 */
	public int getFieldType(int i) throws ReadDriverException {
		return ((DBDriver) getDriver()).getFieldType(i);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldValue(long,
	 *      int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws ReadDriverException {
		return ((DBDriver) getDriver()).getFieldValue(rowIndex, fieldId);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public Driver getDriver() {
		return driver;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#setSourceInfo(com.hardcode.gdbms.engine.data.driver.DriverInfo)
	 */
	public void setSourceInfo(SourceInfo sourceInfo) {
		super.setSourceInfo(sourceInfo);

		this.host = ((DBSourceInfo) sourceInfo).host;
		this.port = ((DBSourceInfo) sourceInfo).port;
		this.dbName = ((DBSourceInfo) sourceInfo).dbName;
		this.user = ((DBSourceInfo) sourceInfo).user;
		this.password = ((DBSourceInfo) sourceInfo).password;
		this.tableName = ((DBTableSourceInfo) sourceInfo).tableName;
		this.con = ((DBTableSourceInfo) sourceInfo).connection;

		this.sql = "SELECT * FROM " + tableName;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.db.DBDataSource#execute(java.lang.String)
	 */
	public void execute(String sql) throws ReadDriverException {
		try {
			((DBDriver) getDriver()).execute(this.getConnection(), sql);
		} catch (SQLException e) {
			throw new ReadDriverException(getName(), e);
		}
	}

	protected String tableName;
	protected int sem = 0;
	private int[] cachedPKIndices = null;

	/**
	 * Get's a connection to the driver
	 * 
	 * @return Connection
	 * 
	 * @throws SQLException
	 *             if the connection cannot be established
	 */
	public Connection getConnection() throws SQLException {
		if (this.con == null) {
			return driver.getConnection(host, port, dbName, user, password);
		} else {
			return this.con;
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#start()
	 */
	public void start() throws ReadDriverException {
		try {
			if (sem == 0) {
				con = getConnection();
				((AlphanumericDBDriver) driver).open(con, sql);
				// driver.setSourceInfo(this.getSourceInfo());

			}

			sem++;
		} catch (SQLException e) {
			throw new ReadDriverException(getName(), e);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#stop()
	 */
	public void stop() throws ReadDriverException {
		try {
			sem--;

			if (sem == 0) {
				driver.close();
				if (this.con == null) {
					con.close();
					con = null;
				}
			} else if (sem < 0) {
				throw new RuntimeException("DataSource closed too many times");
			}
		} catch (SQLException e) {
			throw new ReadDriverException(getName(), e);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getDBMS()
	 */
	public String getDBMS() {
		return ((DBSourceInfo) sourceInfo).dbms;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getName()
	 */
	public String getName() {
		return sourceInfo.name;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getPrimaryKeys()
	 */
	public int[] getPrimaryKeys() throws ReadDriverException {
		if (cachedPKIndices == null) {
			try {
				// Gets the pk column names
				Connection c = getConnection();
				ResultSet rs = c.getMetaData().getPrimaryKeys(null, null,
						driver.getInternalTableName(tableName));
				ArrayList pks = new ArrayList();

				while (rs.next()) {
					pks.add(rs.getString("COLUMN_NAME"));
				}

				// create the index array
				cachedPKIndices = new int[pks.size()];

				for (int i = 0; i < cachedPKIndices.length; i++) {
					cachedPKIndices[i] = getFieldIndexByName((String) pks
							.get(i));
				}
			} catch (SQLException e) {
				throw new ReadDriverException(getName(), e);
			}
		}

		return cachedPKIndices;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getDataWare(int)
	 */
	public DataWare getDataWare(int mode) throws ReadDriverException {
		DBDataWare dw = DBDataSourceFactory.newDataWareInstance(
				((DBDriver) getDriver()), mode);
		DBDriver driver;
		driver = (DBDriver) null;
		((GDBMSDriver) driver).setDataSourceFactory(getDataSourceFactory());
		dw.setDriver(driver);
		dw.setDataSourceFactory(dsf);
		dw.setSourceInfo(getSourceInfo());
		return dw;

	}

	public int getFieldWidth(int i) throws ReadDriverException {
		return ((DBDriver) getDriver()).getFieldWidth(i);
	}

	public void reload() throws ReloadDriverException {
		try {
			sem = 0;

			driver.close();
			if (this.con == null) {
				con.close();
				con = null;
			}

			this.start();
		} catch (SQLException e) {
			throw new ReloadDriverException(getName(), e);
		} catch (ReadDriverException e) {
			throw new ReloadDriverException(getName(), e);
		}
		this.raiseEventReloaded();

	}
}
