package com.hardcode.gdbms.engine.strategies;

import java.io.IOException;
import java.text.Collator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.persistence.Memento;
import com.hardcode.gdbms.engine.data.persistence.MementoException;
import com.hardcode.gdbms.engine.data.persistence.OperationLayerMemento;
import com.hardcode.gdbms.engine.instruction.IncompatibleTypesException;
import com.hardcode.gdbms.engine.instruction.SelectAdapter;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.StringValue;
import com.hardcode.gdbms.engine.values.Value;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando González Cortés
 */
public class OrderedDataSource extends OperationDataSource implements
		DataSource {
	private OperationDataSource dataSource;
	private int[] fieldIndexes;
	private int[] orders;
	private long[] orderIndexes;
	private Value[][] columnCache;

	/**
	 * DOCUMENT ME!
	 * 
	 * @param ret
	 * @param fieldNames
	 * @param types
	 * @throws ReadDriverException
	 *             TODO
	 */
	public OrderedDataSource(OperationDataSource ret, String[] fieldNames,
			int[] types) throws ReadDriverException {
		this.dataSource = ret;

		fieldIndexes = new int[fieldNames.length];
		for (int i = 0; i < fieldNames.length; i++) {
			fieldIndexes[i] = dataSource.getFieldIndexByName(fieldNames[i]);
		}

		orders = new int[types.length];
		for (int i = 0; i < types.length; i++) {
			orders[i] = (types[i] == SelectAdapter.ORDER_ASC) ? 1 : -1;
		}

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
		return dataSource.getFieldIndexByName(fieldName);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long,
	 *      int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws ReadDriverException {
		return dataSource.getFieldValue(orderIndexes[(int) rowIndex], fieldId);
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
		return dataSource.getRowCount();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldType(int)
	 */
	public int getFieldType(int i) throws ReadDriverException {
		return dataSource.getFieldType(i);
	}

	/**
	 * @throws ReadDriverException
	 *             TODO
	 * 
	 */
	public void order() throws ReadDriverException {
		int rowCount = (int) dataSource.getRowCount();
		columnCache = new Value[rowCount][fieldIndexes.length];
		for (int field = 0; field < fieldIndexes.length; field++) {
			for (int i = 0; i < rowCount; i++) {
				columnCache[i][field] = dataSource.getFieldValue(i,
						fieldIndexes[field]);
			}
		}

		TreeSet set = new TreeSet(new SortComparator());

		for (int i = 0; i < dataSource.getRowCount(); i++) {
			set.add(new Integer(i));
		}

		orderIndexes = new long[(int) dataSource.getRowCount()];
		int index = 0;
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Integer integer = (Integer) it.next();

			orderIndexes[index] = integer.intValue();
			index++;
		}
	}

	public long[] getWhereFilter() throws IOException {
		return orderIndexes;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @author Fernando González Cortés
	 */
	public class SortComparator implements Comparator {
		private Collator collator = Collator.getInstance();

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			try {
				int i1 = ((Integer) o1).intValue();
				int i2 = ((Integer) o2).intValue();

				for (int i = 0; i < orders.length; i++) {
					Value v1 = columnCache[i1][i];
					Value v2 = columnCache[i2][i];

					if (v1 instanceof NullValue)
						return -1 * orders[i];
					if (v2 instanceof NullValue)
						return 1 * orders[i];

					if (v1 instanceof StringValue && v2 instanceof StringValue) {
						String s1 = ((StringValue) v1).getValue();
						String s2 = ((StringValue) v2).getValue();
						if (collator.equals(s1.toLowerCase(),
								(s2.toLowerCase()))) {
							if (collator.compare(s1, s2) < 0) {
								return -1 * orders[i];
							} else {
								return 1 * orders[i];
							}
						}
						if (collator
								.compare(s1.toLowerCase(), s2.toLowerCase()) < 0) {
							return -1 * orders[i];
						} else {
							return 1 * orders[i];
						}
					}

					if (((BooleanValue) v1.less(v2)).getValue()) {
						return -1 * orders[i];
					} else if (((BooleanValue) v2.less(v1)).getValue()) {
						return 1 * orders[i];
					}
				}
				/*
				 * Because none of the orders criteria defined an order. The
				 * first value will be less than the second
				 */
				return -1;
			} catch (IncompatibleTypesException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public int getFieldWidth(int i) throws ReadDriverException {
		return dataSource.getFieldWidth(i);
	}
}
