package com.hardcode.gdbms.engine.instruction;

import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.parser.SimpleNode;

/**
 * Adapter node of the CUSTOM syntax node
 * 
 * @author Fernando González Cortés
 */
public class CustomAdapter extends Adapter {
	private String queryName;
	private DataSource[] tables;
	private Expression[] values;

	/**
	 * Gets the DataSource's of the 'tables' clause of the custom query
	 * 
	 * @return DataSource array
	 * 
	 * @throws SemanticException
	 *             If there is any semantic error in the tables clause
	 */
	public DataSource[] getTables() throws SemanticException {
		return ((TableListAdapter) getChilds()[0]).getTables();
	}

	/**
	 * gets the values of the values clause
	 * 
	 * @return Expression array
	 */
	public Expression[] getValues() {
		if (values == null) {
			FunctionArgsAdapter fArgs = ((FunctionArgsAdapter) getChilds()[1]);
			Adapter[] exprs = fArgs.getChilds();
			values = new Expression[exprs.length];

			for (int i = 0; i < exprs.length; i++) {
				values[i] = (Expression) exprs[i];
			}
		}

		return values;
	}

	/**
	 * gets the name of the custom query
	 * 
	 * @return Returns the queryName.
	 */
	public String getQueryName() {
		if (queryName == null) {
			queryName = ((SimpleNode) getEntity()).first_token.next.image;
		}

		return queryName;
	}
}
