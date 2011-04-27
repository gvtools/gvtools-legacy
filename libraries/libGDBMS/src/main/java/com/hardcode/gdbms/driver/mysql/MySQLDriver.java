/*
 * Created on 16-oct-2004
 */
package com.hardcode.gdbms.driver.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.db.DBDataSourceFactory;
import com.hardcode.gdbms.engine.data.db.DBDataWare;
import com.hardcode.gdbms.engine.data.db.DBTableSourceInfo;
import com.hardcode.gdbms.engine.data.db.JDBCSupport;
import com.hardcode.gdbms.engine.data.driver.AbstractJDBCDriver;
import com.hardcode.gdbms.engine.data.driver.DBTransactionalDriver;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.IWriter;


/**
 * MySQL driver
 *
 * @author Fernando González Cortés
 * @author azabala
 */
public class MySQLDriver extends AbstractJDBCDriver implements DBTransactionalDriver {
    private static Exception driverException;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            driverException = ex;
        }
    }

    /**
     * IWriter implementation for MySQL DB.
     * It does editing operations
     * */
    MySQLWriter writer = new MySQLWriter();


    public IWriter getWriter() {
		return writer;
	}

    public void open(Connection con, String sql) throws SQLException, OpenDriverException {
		jdbcSupport = JDBCSupport.newJDBCSupport(con, sql);
        writer.initialize(con);
		writer.setCreateTable(false);
	    writer.setWriteAll(false);
		DBDataWare dw = DBDataSourceFactory.newDataWareInstance(this, DataSourceFactory.DATA_WARE_DIRECT_MODE);
        dw.setDriver(this);
    	try {
			ITableDefinition schema = super.getTableDefinition();
		} catch (ReadDriverException e) {
			throw new OpenDriverException(getName(),e);
		}
		ResultSetMetaData metadata = jdbcSupport.getResultSet().
						getMetaData();

        DBTableSourceInfo sourceInfo = new DBTableSourceInfo();

        sourceInfo.connection = con;
        sourceInfo.dbName = con.getCatalog();
        sourceInfo.tableName = metadata.getTableName(1);

        dw.setSourceInfo(sourceInfo);

	    writer.setDirectDataWare(dw);

	}

    public void close() throws SQLException {
    	//commented to avoid problems with automatic datasource
//		jdbcSupport.close();
//		writer.close();
	}

    /**
     * DOCUMENT ME!
     *
     * @param host DOCUMENT ME!
     * @param port DOCUMENT ME!
     * @param dbName DOCUMENT ME!
     * @param user DOCUMENT ME!
     * @param password DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws SQLException
     * @throws RuntimeException DOCUMENT ME!
     *
     * @see com.hardcode.gdbms.engine.data.driver.DBDriver#connect(java.lang.String)
     */
    public Connection getConnection(String host, int port, String dbName,
        String user, String password) throws SQLException {
        if (driverException != null) {
            throw new RuntimeException(driverException);
        }

        String connectionString = "jdbc:mysql://" + host;

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
        return "mysql";
    }

    /*
     *Sobreescribo el metodo de AbstractJDBCDriver porque
     *si para la gran mayoria de bbdd no hay que escribir querys
     *(se hace con updatableresultset) para MySQL no funciona esto.
     *Como se necesita construir la query al vuelo, así se le proporciona
     *al ITableDefinition el nombre de la tabla
     * */
    public ITableDefinition getTableDefinition() throws ReadDriverException{
    	DBLayerDefinition solution = new DBLayerDefinition();
    	ITableDefinition schema = super.getTableDefinition();
    	solution.setFieldsDesc(schema.getFieldsDesc());
    	solution.setName(schema.getName());
    	try {
			ResultSetMetaData metadata = jdbcSupport.getResultSet().
						getMetaData();
			solution.setTableName(metadata.getTableName(1));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
    	return solution;


    }

	public void beginTrans(Connection con) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void commitTrans(Connection con) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void rollBackTrans(Connection con) throws SQLException {
		// TODO Auto-generated method stub

	}

}
