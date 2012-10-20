package com.hardcode.gdbms.engine.values;

import java.sql.Types;

/**
 * Wrapper sobre el tipo long
 * 
 * @author Fernando González Cortés
 */
public class LongValue extends NumericValue {
	private long value;

	/**
	 * Creates a new LongValue object.
	 * 
	 * @param value
	 *            DOCUMENT ME!
	 */
	LongValue(long value) {
		this.value = value;
	}

	/**
	 * Creates a new LongValue object.
	 */
	LongValue() {
	}

	/**
	 * Establece el valor de este objeto
	 * 
	 * @param value
	 */
	public void setValue(long value) {
		this.value = value;
	}

	/**
	 * Obtiene el valor de este objeto
	 * 
	 * @return
	 */
	public long getValue() {
		return value;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "" + value;
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.NumericValue#intValue()
	 */
	public int intValue() {
		return (int) value;
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.NumericValue#longValue()
	 */
	public long longValue() {
		return (long) value;
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.NumericValue#floatValue()
	 */
	public float floatValue() {
		return (float) value;
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.NumericValue#doubleValue()
	 */
	public double doubleValue() {
		return (double) value;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public int getType() {
		return ValueFactory.LONG;
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.NumericValue#byteValue()
	 */
	public byte byteValue() {
		return (byte) value;
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.NumericValue#shortValue()
	 */
	public short shortValue() {
		return (short) value;
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Value#getStringValue(com.hardcode.gdbms.engine.data.driver.ValueWriter)
	 */
	public String getStringValue(ValueWriter writer) {
		return writer.getStatementString(value);
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Value#getSQLType()
	 */
	public int getSQLType() {
		return Types.BIGINT;
	}

	public int getWidth() {
		return 8;
	}
}
