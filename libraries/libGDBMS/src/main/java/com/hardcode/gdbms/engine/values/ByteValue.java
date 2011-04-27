package com.hardcode.gdbms.engine.values;

import java.sql.Types;




/**
 *
 */
public class ByteValue extends NumericValue {
	private byte value;

	/**
	 * Crea un nuevo ByteValue.
	 *
	 * @param value DOCUMENT ME!
	 */
	ByteValue(byte value) {
		this.value = value;
	}

	/**
	 * Crea un nuevo ByteValue.
	 */
	ByteValue() {
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.NumericValue#byteValue()
	 */
	public byte byteValue() {
		return value;
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.NumericValue#shortValue()
	 */
	public short shortValue() {
		return (short) value;
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
		return ValueFactory.BYTE;
	}

    /**
     * @see com.hardcode.gdbms.engine.values.Value#getStringValue(com.hardcode.gdbms.engine.data.driver.ValueWriter)
     */
    public String getStringValue(ValueWriter writer) {
        return writer.getStatementString(value, Types.TINYINT);
    }

    /**
     * @see com.hardcode.gdbms.engine.values.Value#getSQLType()
     */
    public int getSQLType() {
        return Types.TINYINT;
    }

	public int getWidth() {
		return 1;
	}
}
