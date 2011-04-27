package com.hardcode.gdbms.engine.strategies;

import java.io.IOException;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.indexes.IndexFactory;
import com.hardcode.gdbms.engine.data.indexes.VariableIndexSet;
import com.hardcode.gdbms.engine.data.persistence.Memento;
import com.hardcode.gdbms.engine.data.persistence.MementoException;
import com.hardcode.gdbms.engine.data.persistence.OperationLayerMemento;
import com.hardcode.gdbms.engine.instruction.EvaluationException;
import com.hardcode.gdbms.engine.instruction.Expression;
import com.hardcode.gdbms.engine.instruction.IncompatibleTypesException;
import com.hardcode.gdbms.engine.instruction.SemanticException;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.Value;


/**
 * Representa una fuente de datos que contiene una cláusula where mediante la
 * cual se filtran los campos
 *
 * @author Fernando González Cortés
 */
public class FilteredDataSource extends OperationDataSource {
	private DataSource source;
	private Expression whereExpression;
	private VariableIndexSet indexes;

	/**
	 * Creates a new FilteredDataSource object.
	 *
	 * @param source DataSource que se va a filtrar
	 * @param whereExpression Expresión de la cláusula where
	 */
	public FilteredDataSource(DataSource source, Expression whereExpression) {
		this.source = source;
		this.whereExpression = whereExpression;
	}

    public Value[] aggregatedFilter(Expression[] fields) throws IncompatibleTypesException, EvaluationException, ReadDriverException {
        Value[] aggregatedValues = new Value[fields.length];
		indexes = IndexFactory.createVariableIndex();
		try {
			indexes.open();
			for (long i = 0; i < source.getRowCount(); i++) {
				try {
					if (((BooleanValue) whereExpression.evaluateExpression(i))
							.getValue()) {
						indexes.addIndex(i);
						for (int j = 0; j < aggregatedValues.length; j++) {
							aggregatedValues[j] = fields[j].evaluate(i);
						}
					}
				} catch (ClassCastException e) {
					throw new IncompatibleTypesException(
							"where expression is not boolean");
				}
			}

			indexes.indexSetComplete();
		} catch (IOException e1) {
			throw new ReadDriverException(getName(),e1);
		}
		return aggregatedValues;
    }

    /**
	 * Método que construye el array de índices de las posiciones que las filas
	 * filtradas ocupan en el DataSource origen
     * @throws SemanticException Si se produce algún error semántico al evaluar
	 * 		   la expresión
     * @throws EvaluationException If the expression evaluation fails
     * @throws ReadDriverException TODO
     * @throws IncompatibleTypesException Si la expresión where no evalua a
	 * 		   booleano
	 */
	public void filtrar()
		throws SemanticException, EvaluationException, ReadDriverException {
		indexes = IndexFactory.createVariableIndex();
		try {
			indexes.open();
			for (long i = 0; i < source.getRowCount(); i++) {
				try {
					if (((BooleanValue) whereExpression.evaluateExpression(i))
							.getValue()) {
						indexes.addIndex(i);
					}
				} catch (ClassCastException e) {
					throw new IncompatibleTypesException(
							"where expression is not boolean");
				}
			}

			indexes.indexSetComplete();
		} catch (IOException e1) {
			throw new ReadDriverException(getName(),e1);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#open()
	 */
	public void start() throws ReadDriverException {
		source.start();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#close()
	 */
	public void stop() throws ReadDriverException {
		source.stop();

		try {
			indexes.close();
		} catch (IOException e) {
			throw new ReadDriverException(getName(),e);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.FieldNameAccess#getFieldIndexByName(java.lang.String)
	 */
	public int getFieldIndexByName(String fieldName) throws ReadDriverException {
		return source.getFieldIndexByName(fieldName);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long,
	 * 		int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
		throws ReadDriverException {
		try {
			return source.getFieldValue(indexes.getIndex(rowIndex), fieldId);
		} catch (IOException e) {
			throw new ReadDriverException(getName(),e);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldCount()
	 */
	public int getFieldCount() throws ReadDriverException {
		return source.getFieldCount();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws ReadDriverException {
		return source.getFieldName(fieldId);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws ReadDriverException {
		return indexes.getIndexCount();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IOException
	 *
	 * @see com.hardcode.gdbms.engine.data.DataSource#getWhereFilter()
	 */
	public long[] getWhereFilter() throws IOException {
		return indexes.getIndexes();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldType(int)
	 */
	public int getFieldType(int i) throws ReadDriverException {
		return source.getFieldType(i);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(),
			new Memento[] { source.getMemento() }, getSQL());
	}

	public int getFieldWidth(int i) throws ReadDriverException {
		return source.getFieldWidth(i);
	}
}
