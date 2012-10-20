package com.hardcode.gdbms.engine.strategies;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.customQuery.CustomQuery;
import com.hardcode.gdbms.engine.customQuery.QueryException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.instruction.Adapter;
import com.hardcode.gdbms.engine.instruction.Expression;
import com.hardcode.gdbms.engine.instruction.Utilities;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.Value;

/**
 * @author Fernando González Cortés
 */
public class SumQuery implements CustomQuery {

	/**
	 * @throws QueryException
	 * @see com.hardcode.gdbms.engine.customQuery.CustomQuery#evaluate(com.hardcode.gdbms.engine.data.DataSource[],
	 *      com.hardcode.gdbms.engine.instruction.Expression[])
	 */
	public OperationDataSource evaluate(DataSource[] tables, Expression[] values)
			throws QueryException {
		if (tables.length != 1)
			throw new QueryException("SUM only operates on one table");
		if (values.length != 1)
			throw new QueryException("SUM only operates with one value");

		((Adapter) values[0]).getInstructionContext().setFromTables(
				new DataSource[] { tables[0] });
		((Adapter) values[0]).getInstructionContext().setDs(tables[0]);

		String fieldName = values[0].getFieldName();
		if (fieldName == null)
			throw new QueryException("field not found "
					+ Utilities.getText(((Adapter) values[0]).getEntity()));

		double res = 0;
		try {

			tables[0].start();

			int fieldIndex = tables[0].getFieldIndexByName(fieldName);
			if (fieldIndex == -1)
				throw new RuntimeException(
						"we found the field name of the expression but could not find the field index?");

			for (int i = 0; i < tables[0].getRowCount(); i++) {
				Value v = tables[0].getFieldValue(i, fieldIndex);
				if (v instanceof NumericValue) {
					res += ((NumericValue) v).doubleValue();
				} else {
					throw new QueryException(
							"SUM only operates with numeric fields");
				}
			}

			tables[0].stop();
		} catch (ReadDriverException e) {
			throw new QueryException("Error reading data", e);
		}

		return new SumDataSource(res);
	}

	/**
	 * @see com.hardcode.gdbms.engine.customQuery.CustomQuery#getName()
	 */
	public String getName() {
		return "SUMQUERY";
	}
}
