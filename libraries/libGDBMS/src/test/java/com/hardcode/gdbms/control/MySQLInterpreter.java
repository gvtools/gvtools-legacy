package com.hardcode.gdbms.control;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class MySQLInterpreter {
	/**
	 * DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 *
	 * @throws InstantiationException DOCUMENT ME!
	 * @throws IllegalAccessException DOCUMENT ME!
	 * @throws ClassNotFoundException DOCUMENT ME!
	 * @throws SQLException DOCUMENT ME!
	 */
	public static void main(String[] args)
		throws InstantiationException, IllegalAccessException, 
			ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();

		Connection con = DriverManager.getConnection(args[0]);

		while (true) {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
							System.in));
				String sql = "";
				System.out.print("> ");
				sql = in.readLine();

				Statement st = con.createStatement();
				st.execute(sql.trim());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
