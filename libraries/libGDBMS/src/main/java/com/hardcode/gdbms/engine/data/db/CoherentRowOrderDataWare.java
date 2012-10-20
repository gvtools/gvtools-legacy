package com.hardcode.gdbms.engine.data.db;

import java.sql.SQLException;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.driver.DBTransactionalDriver;

/**
 * DataWare for the DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER mode that
 * keeps the row order during edition with the drawback of being more slow that
 * the one that implements the DataSourceFactory.DATA_WARE_DIRECT_MODE mode
 * 
 * @author Fernando González Cortés
 */
public class CoherentRowOrderDataWare extends FakeTransactionDataWare {
	/**
	 * Gets the underlaying driver
	 * 
	 * @return DBTransactionalDriver
	 */
	private DBTransactionalDriver getTransacctionalDriver() {
		return (DBTransactionalDriver) driver;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.edition.DataWare#beginTrans()
	 */
	public void beginTrans() throws ReadDriverException {
		super.beginTrans();

		try {
			getTransacctionalDriver().beginTrans(con);
		} catch (SQLException e) {
			throw new ReadDriverException(getName(), e);
		}
	}

	/**
	 * @throws WriteDriverException
	 * @see com.hardcode.gdbms.engine.data.edition.DataWare#commitTrans()
	 */
	public void commitTrans() throws ReadDriverException, WriteDriverException {

		try {
			getTransacctionalDriver().commitTrans(con);
			super.commitTrans();
		} catch (SQLException e) {
			throw new WriteDriverException(getName(), e);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.edition.DataWare#rollBackTrans()
	 */
	public void rollBackTrans() throws ReadDriverException,
			WriteDriverException {

		try {
			getTransacctionalDriver().rollBackTrans(con);
			super.rollBackTrans();
		} catch (SQLException e) {
			throw new WriteDriverException(getName(), e);
		}
	}
}
