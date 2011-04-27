package com.hardcode.gdbms.driver.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.hardcode.gdbms.engine.data.driver.AbstractJDBCDriver;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class HSQLDBDriver extends AbstractJDBCDriver {
    private static Exception driverException;
    private static DateFormat timestampFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
  
    static {
        try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
        } catch (Exception ex) {
            driverException = ex;
        }
    }

    private Connection con = null;
    
    /**
     * @see com.hardcode.gdbms.engine.data.driver.DBDriver#getConnection(java.lang.String,
     *      int, java.lang.String, java.lang.String, java.lang.String)
     */
    public Connection getConnection(String host, int port, String dbName,
        String user, String password) throws SQLException {
        if (driverException != null) {
            throw new RuntimeException(driverException);
        }

        String connectionString = "jdbc:hsqldb:file:" + dbName;
        if (con == null){
        	con = DriverManager.getConnection(connectionString, user, password);
        }
        return con;
    }


    /**
     * @see com.hardcode.driverManager.Driver#getName()
     */
    public String getName() {
        return "GDBMS HSQLDB driver";
    }

	
}
