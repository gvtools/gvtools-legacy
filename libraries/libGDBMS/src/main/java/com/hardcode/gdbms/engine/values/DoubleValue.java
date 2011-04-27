package com.hardcode.gdbms.engine.values;

import java.sql.Types;



/**
 * Wrapper sobre el valor double
 *
 * @author Fernando González Cortés
 */
public class DoubleValue extends NumericValue {
	private double value;

	/**
	 * Creates a new DoubleValue object.
	 *
	 * @param val DOCUMENT ME!
	 */
	DoubleValue(double val) {
		value = val;
	}

	/**
	 * Creates a new DoubleValue object.
	 */
	DoubleValue() {
	}

	/**
	 * Establece el valor de este objeto
	 *
	 * @param value
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * Obtiene el valor de este objeto
	 *
	 * @return
	 */
	public double getValue() {
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
		return value;
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.NumericValue#getType()
	 */
	public int getType() {
		return ValueFactory.DOUBLE;
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
        return writer.getStatementString(value, Types.DOUBLE);
    }

    /**
     * @see com.hardcode.gdbms.engine.values.Value#getSQLType()
     */
    public int getSQLType() {
        return Types.DOUBLE;
    }

	public int getWidth() {
		return 8;
	}
}
