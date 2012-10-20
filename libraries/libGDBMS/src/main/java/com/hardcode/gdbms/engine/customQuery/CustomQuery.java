package com.hardcode.gdbms.engine.customQuery;

import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.instruction.Expression;
import com.hardcode.gdbms.engine.strategies.OperationDataSource;

/**
 * Interface to implement of all the custom queries
 * 
 * @author Fernando González Cortés
 */
public interface CustomQuery {
	/**
	 * Executes the custom query
	 * 
	 * @param tables
	 *            tables involved in the query
	 * @param values
	 *            values passed to the query
	 * 
	 * @return DataSource result of the query
	 * 
	 * @throws QueryException
	 *             if the custom query execution fails
	 */
	public OperationDataSource evaluate(DataSource[] tables, Expression[] values)
			throws QueryException;

	/**
	 * Gets the query name. Must ve a valid SQL identifier (i.e.: '.' is not
	 * allowed)
	 * 
	 * @return query name
	 */
	public String getName();
}
