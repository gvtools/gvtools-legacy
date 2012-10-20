package com.hardcode.gdbms.engine.data.edition;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueCollection;

/**
 * Interface that provide transactional editing capabilities
 * 
 * @author Fernando González Cortés
 */
public interface DataWare extends DataSource {
	/**
	 * Deletes the ith row of the DataSource
	 * 
	 * @param rowId
	 *            index of the row to be deleted
	 * @throws WriteDriverException
	 *             TODO
	 * @throws ReadDriverException
	 *             TODO
	 */
	public void deleteRow(long rowId) throws WriteDriverException,
			ReadDriverException;

	/**
	 * Inserts a row at the end of the dataware with the specified values
	 * 
	 * @param values
	 *            Values of the inserted row fields in the field order
	 * @throws WriteDriverException
	 *             TODO
	 * @throws ReadDriverException
	 */
	public void insertFilledRow(Value[] values) throws WriteDriverException,
			ReadDriverException;

	/**
	 * Inserts a row at the end of the dataware
	 * 
	 * @param pk
	 *            primary key of the inserted row
	 * @throws WriteDriverException
	 *             TODO
	 */
	public void insertEmptyRow(ValueCollection pk) throws WriteDriverException;

	/**
	 * Begins a transaction
	 * 
	 * @throws ReadDriverException
	 *             TODO
	 */
	public void beginTrans() throws ReadDriverException;

	/**
	 * Commits the changes made during the transaction
	 * 
	 * @throws ReadDriverException
	 *             TODO
	 * @throws WriteDriverException
	 */
	public void commitTrans() throws ReadDriverException, WriteDriverException;

	/**
	 * Cancels the changes made during the transaction
	 * 
	 * @throws ReadDriverException
	 *             TODO
	 * @throws WriteDriverException
	 *             TODO
	 */
	public void rollBackTrans() throws ReadDriverException,
			WriteDriverException;

	/**
	 * Sets the value of a cell of the table. Cannot be called outside a
	 * beginTrans-commintTrans or beginTrans-rollBackTrans
	 * 
	 * @param row
	 *            row to update
	 * @param fieldId
	 *            field to update
	 * @param value
	 *            Value to update
	 * @throws WriteDriverException
	 *             TODO
	 * @throws ReadDriverException
	 *             TODO
	 */
	public void setFieldValue(long row, int fieldId, Value value)
			throws WriteDriverException, ReadDriverException;
}
