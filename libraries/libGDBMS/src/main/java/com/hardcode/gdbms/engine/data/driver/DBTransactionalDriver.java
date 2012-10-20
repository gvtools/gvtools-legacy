package com.hardcode.gdbms.engine.data.driver;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface to be implemented by those db drivers whose management system
 * support transactions
 * 
 * @author Fernando González Cortés
 */
public interface DBTransactionalDriver {
	/**
	 * Begins a transaction
	 * 
	 * @param newParam
	 *            TODO
	 * 
	 * @throws SQLException
	 *             If the transaction could not be started
	 */
	public void beginTrans(Connection con) throws SQLException;

	/**
	 * Commits the changes made during the transaction
	 * 
	 * @param con
	 *            TODO
	 * 
	 * @throws SQLException
	 *             If the transaction could not be commited
	 */
	public void commitTrans(Connection con) throws SQLException;

	/**
	 * Cancels the changes made during the transaction
	 * 
	 * @param con
	 *            TODO
	 * 
	 * @throws SQLException
	 *             If the transaction could not be cancelled
	 */
	public void rollBackTrans(Connection con) throws SQLException;

}
