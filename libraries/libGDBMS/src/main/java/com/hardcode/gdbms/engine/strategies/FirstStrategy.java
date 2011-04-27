package com.hardcode.gdbms.engine.strategies;


import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.customQuery.CustomQuery;
import com.hardcode.gdbms.engine.customQuery.QueryException;
import com.hardcode.gdbms.engine.customQuery.QueryManager;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.instruction.CustomAdapter;
import com.hardcode.gdbms.engine.instruction.EvaluationException;
import com.hardcode.gdbms.engine.instruction.Expression;
import com.hardcode.gdbms.engine.instruction.SelectAdapter;
import com.hardcode.gdbms.engine.instruction.SemanticException;
import com.hardcode.gdbms.engine.instruction.UnionAdapter;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.parser.ParseException;


/**
 * Strategy de pruebas, en la que los metodos tienen la característica de que
 * son los más fáciles de implementar en el momento en que fueron necesarios
 *
 * @author Fernando González Cortés
 */
public class FirstStrategy extends Strategy {
	/**
	 * @see com.hardcode.gdbms.engine.strategies.Strategy#select(com.hardcode.gdbms.parser.ASTSQLSelectCols,
	 * 		com.hardcode.gdbms.parser.ASTSQLTableList,
	 * 		com.hardcode.gdbms.parser.ASTSQLWhere)
	 */
	public OperationDataSource select(SelectAdapter instr)
		throws SemanticException, EvaluationException, ReadDriverException {
		OperationDataSource ret = null;

		DataSource[] fromTables = instr.getTables();
		OperationDataSource prod = new PDataSource(fromTables);

		ret = prod;

		/*
		 * Se establece como origen de datos el DataSource producto de las tablas
		 * de la cláusula from para que el acceso desde el objeto field a los
		 * valores del dataSource sea correcto
		 */
		//		Utilities.setTablesAndSource((SelectAdapter) instr, fromTables, prod);
		((SelectAdapter) instr).getInstructionContext().setDs(prod);
		((SelectAdapter) instr).getInstructionContext().setFromTables(fromTables);

		Expression[] fields = instr.getFieldsExpression();

		if (fields != null) {
			if (fields[0].isAggregated()){
			    return executeAggregatedSelect(fields, instr.getWhereExpression(), prod);
			}

			ret.start();

			OperationDataSource res = new ProjectionDataSource(prod, fields,
					instr.getFieldsAlias());
			ret.stop();

			ret = res;
		} else {
			ret = prod;
		}

		Expression whereExpression = instr.getWhereExpression();

		if (whereExpression != null) {
			ret.start();

			FilteredDataSource dataSource = new FilteredDataSource(ret,
					whereExpression);
			dataSource.filtrar();
			ret.stop();

			ret = dataSource;
		}

		if (instr.isDistinct()){
			ret.start();

			DistinctDataSource dataSource = new DistinctDataSource(ret,
					instr.getFieldsExpression());
			dataSource.filter();
			ret.stop();

			ret = dataSource;
		}

		int orderFieldCount = instr.getOrderCriterionCount();
		if (orderFieldCount > 0){
		    ret.start();
		    String[] fieldNames = new String[orderFieldCount];
		    int[] types = new int[orderFieldCount];
		    for (int i = 0; i < types.length; i++) {
                fieldNames[i] = instr.getFieldName(i);
                types[i] = instr.getOrder(i);
            }
		    OrderedDataSource dataSource = new OrderedDataSource(ret, fieldNames, types);
		    dataSource.order();
		    ret.stop();

		    ret = dataSource;
		}

		return ret;
	}

	/**
	 * @param expression
	 * @param fields
	 * @throws SemanticException
	 * @throws EvaluationException
	 * @throws ReadDriverException TODO
     *
     */
    private OperationDataSource executeAggregatedSelect(Expression[] fields, Expression whereExpression, DataSource ds) throws SemanticException, EvaluationException, ReadDriverException {
        Value[] aggregateds = new Value[fields.length];
		if (whereExpression != null) {
			ds.start();

			FilteredDataSource dataSource = new FilteredDataSource(ds,
					whereExpression);
			aggregateds = dataSource.aggregatedFilter(fields);
			ds.stop();

		} else {
		    ds.start();
		    for (int i = 0; i < fields.length; i++) {
                for (int j = 0; j < ds.getRowCount(); j++) {
                    aggregateds[i] = fields[i].evaluate(j);
                }
            }
		    ds.stop();
		}

		return new AggregateDataSource(aggregateds);
    }

    /**
	 * @see com.hardcode.gdbms.engine.strategies.Strategy#union(com.hardcode.gdbms.engine.instruction.UnionInstruction)
	 */
	public OperationDataSource union(UnionAdapter instr)
		throws DriverLoadException, ParseException, SemanticException, EvaluationException, ReadDriverException {
		return new UnionDataSource(instr.getFirstTable(), instr.getSecondTable());
	}

	/**
	 * @see com.hardcode.gdbms.engine.strategies.Strategy#custom(com.hardcode.gdbms.engine.instruction.CustomAdapter)
	 */
	public OperationDataSource custom(CustomAdapter instr)
		throws SemanticException {
		CustomQuery query = QueryManager.getQuery(instr.getQueryName());

		if (query == null) {
			throw new SemanticException("No such custom query");
		}

		try {
			return query.evaluate(instr.getTables(), instr.getValues());
		} catch (QueryException e) {
			throw new SemanticException(e.getMessage());
		}
	}
}
