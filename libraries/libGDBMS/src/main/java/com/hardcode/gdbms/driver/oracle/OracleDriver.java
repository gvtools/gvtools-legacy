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
package com.hardcode.gdbms.driver.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.hardcode.gdbms.engine.data.db.JDBCSupport;
import com.hardcode.gdbms.engine.data.driver.AbstractJDBCDriver;

public class OracleDriver extends AbstractJDBCDriver {

    private static Exception driverException;
    
    static {
        try {
            // Class.forName("com.mysql.jdbc.Driver").newInstance();
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            
        } catch (Exception ex) {
            driverException = ex;
        }
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

        // String connectionString = "jdbc:mysql://" + host;
        // String connString="jdbc:oracle:thin:@prodHost:1521:ORCL";
        String connectionString="jdbc:oracle:thin:@" + host;

        if (port != -1) {
            connectionString += (":" + port);
        }

        connectionString += (":" + dbName);

//        if (user != null) {
//            connectionString += ("?user=" + user + "&password=" + password);
//        }

        return DriverManager.getConnection(connectionString, user, password);
    }

    /**
     * @see com.hardcode.driverManager.Driver#getName()
     */
    public String getName() {
        return "Oracle Alphanumeric";
    }

	public void open(Connection con, String sql) throws SQLException {
        jdbcWriter.setCreateTable(false);
        jdbcWriter.setWriteAll(false);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        ResultSetMetaData metaData = rs.getMetaData();
        
		String fields = "";
		for (int i=0; i<metaData.getColumnCount(); i++)
		{
			if (i==0)
				fields =metaData.getColumnName(i+1);
			else
				fields =fields + ", " + metaData.getColumnName(i+1);
		}
		rs.close();
		String sqlAux = sql.replaceFirst(" [*] ", " " + fields + " ");
		System.out.println(sqlAux);
		
        st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        ResultSet res = st.executeQuery(sqlAux);
        if (res.getConcurrency() != ResultSet.CONCUR_UPDATABLE)
        {
        	System.err.println("Error: No se puede editar la tabla " + sql);
        	jdbcWriter = null;
        }
        else
        	jdbcWriter.initialize(con, res);		

		
        jdbcSupport = JDBCSupport.newJDBCSupport(con, sql);

	}
	
	public String getDefaultPort() {
		return "1521";
	}

//	public String[] getAvailableTables(Connection co, String schema) throws SQLException {
//		
//		ArrayList str_list = new ArrayList();
//
//        Statement st = co.createStatement();
//		String sql = "select TABLE_NAME from ALL_TABLES where OWNER = '" + schema + "' and TABLE_NAME not like '%$' order by TABLE_NAME";
//        ResultSet rs = st.executeQuery(sql);
//        
//        while (rs.next()) {
//        	str_list.add(rs.getString(1));
//        }
//        
//        rs.close();
//
//		sql = "select VIEW_NAME from ALL_VIEWS where OWNER = '" + schema + "' and VIEW_NAME not like '%$' order by VIEW_NAME";
//        rs = st.executeQuery(sql);
//        
//        while (rs.next()) {
//        	str_list.add(rs.getString(1));
//        }
//        
//        rs.close();
//        st.close();
//        
//		return (String[]) str_list.toArray(new String[0]);
//	}



}

// [eiel-gestion-conexiones]

