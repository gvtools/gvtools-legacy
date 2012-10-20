package com.hardcode.gdbms.engine.strategies;

import java.util.Comparator;
import java.util.TreeSet;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.FieldNameAccessSupport;
import com.hardcode.gdbms.engine.data.persistence.Memento;
import com.hardcode.gdbms.engine.data.persistence.MementoException;
import com.hardcode.gdbms.engine.data.persistence.OperationLayerMemento;
import com.hardcode.gdbms.engine.instruction.EvaluationException;
import com.hardcode.gdbms.engine.instruction.Expression;
import com.hardcode.gdbms.engine.instruction.IncompatibleTypesException;
import com.hardcode.gdbms.engine.internalExceptions.InternalException;
import com.hardcode.gdbms.engine.internalExceptions.InternalExceptionCatcher;
import com.hardcode.gdbms.engine.internalExceptions.InternalExceptionEvent;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueCollection;
import com.hardcode.gdbms.engine.values.ValueFactory;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando González Cortés
 */
public class DistinctDataSource extends OperationDataSource {
	private DataSource dataSource;
	private int[] indexes;
	private FieldNameAccessSupport fnaSupport = new FieldNameAccessSupport(this);
	private Expression[] expressions;

	/**
	 * Crea un nuevo DistinctDataSource.
	 * 
	 * @param ds
	 *            DOCUMENT ME!
	 * @param expressions
	 *            DOCUMENT ME!
	 */
	public DistinctDataSource(DataSource ds, Expression[] expressions) {
		dataSource = ds;
		this.expressions = expressions;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#start()
	 */
	public void start() throws ReadDriverException {
		dataSource.start();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#stop()
	 */
	public void stop() throws ReadDriverException {
		dataSource.stop();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(),
				new Memento[] { dataSource.getMemento() }, getSQL());
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getFieldIndexByName(java.lang.String)
	 */
	public int getFieldIndexByName(String fieldName) throws ReadDriverException {
		return fnaSupport.getFieldIndexByName(fieldName);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long,
	 *      int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws ReadDriverException {
		return dataSource.getFieldValue(indexes[(int) rowIndex], fieldId);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldCount()
	 */
	public int getFieldCount() throws ReadDriverException {
		return dataSource.getFieldCount();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws ReadDriverException {
		return dataSource.getFieldName(fieldId);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws ReadDriverException {
		return indexes.length;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldType(int)
	 */
	public int getFieldType(int i) throws ReadDriverException {
		return dataSource.getFieldType(i);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws EvaluationException
	 *             DOCUMENT ME!
	 * @throws ReadDriverException
	 *             TODO
	 * @throws RuntimeException
	 *             DOCUMENT ME!
	 */
	public void filter() throws EvaluationException, ReadDriverException {
		int[] idx = new int[(int) dataSource.getRowCount()];
		TreeSet h = new TreeSet(new Comparator() {
			public int compare(Object o1, Object o2) {
				try {
					Value v1 = (Value) o1;
					Value v2 = (Value) o2;

					if (((BooleanValue) v1.equals(v2)).getValue()) {
						return 0;
					} else {
						return 1;
					}
				} catch (IncompatibleTypesException e) {
					InternalExceptionCatcher
							.callExceptionRaised(new InternalExceptionEvent(
									DistinctDataSource.this,
									new InternalException(
											"Internal error calculating distinct clause",
											e)));
					return 0;
				}
			}
		});
		int index = 0;

		for (int i = 0; i < dataSource.getRowCount(); i++) {
			Value[] values;
			if (expressions == null) {
				values = new Value[dataSource.getFieldCount()];
				for (int j = 0; j < values.length; j++) {
					values[j] = dataSource.getFieldValue(i, j);
				}
			} else {
				values = new Value[expressions.length];
				for (int j = 0; j < values.length; j++) {
					values[j] = expressions[j].evaluate(i);
				}
			}

			ValueCollection vc = ValueFactory.createValue(values);

			if (!h.contains(vc)) {
				idx[index] = i;
				index++;
				h.add(vc);
			}
		}

		indexes = new int[index];
		System.arraycopy(idx, 0, indexes, 0, index);
	}

	public int getFieldWidth(int i) throws ReadDriverException {
		return dataSource.getFieldType(i);
	}
}
