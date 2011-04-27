package com.hardcode.gdbms.engine.values;

import java.sql.Types;




/**
 *
 */
public class ShortValue extends NumericValue {
	private short value;

	/**
	 * Crea un nuevo ShortValue.
	 *
	 * @param s DOCUMENT ME!
	 */
	ShortValue(short s) {
		value = s;
	}

	/**
	 * Crea un nuevo ShortValue.
	 */
	ShortValue() {
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
		return value;
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
	 * @see com.hardcode.gdbms.engine.values.NumericValue#getType()
	 */
	public int getType() {
		return ValueFactory.SHORT;
	}

    /**
     * @see com.hardcode.gdbms.engine.values.Value#getStringValue(com.hardcode.gdbms.engine.data.driver.ValueWriter)
     */
    public String getStringValue(ValueWriter writer) {
        return writer.getStatementString(value, Types.SMALLINT);
    }

    /**
     * @see com.hardcode.gdbms.engine.values.Value#getSQLType()
     */
    public int getSQLType() {
        return Types.SMALLINT;
    }
    
	public int getWidth() {
		return 2;
	}
}
