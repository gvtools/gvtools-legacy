package com.hardcode.gdbms.engine.values;

import java.io.Serializable;
import java.sql.Timestamp;
import java.sql.Types;

import com.hardcode.gdbms.engine.instruction.IncompatibleTypesException;

/**
 * Wrapper sobre el tipo Date
 * 
 * @author Fernando González Cortés
 */
public class TimestampValue extends AbstractValue implements Serializable {
	private Timestamp value;

	/**
	 * Creates a new DateValue object.
	 * 
	 * @param d
	 *            DOCUMENT ME!
	 */
	TimestampValue(Timestamp d) {
		value = d;
	}

	/**
	 * Creates a new DateValue object.
	 */
	TimestampValue() {
	}

	/**
	 * Establece el valor
	 * 
	 * @param d
	 *            valor
	 */
	public void setValue(Timestamp d) {
		value = d;
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Operations#equals(com.hardcode.gdbms.engine.values.DateValue)
	 */
	public Value equals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof TimestampValue) {
			return new BooleanValue(
					this.value.equals(((TimestampValue) value).value));
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Operations#greater(com.hardcode.gdbms.engine.values.DateValue)
	 */
	public Value greater(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof TimestampValue) {
			return new BooleanValue(
					this.value.compareTo(((TimestampValue) value).value) > 0);
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Operations#greaterEqual(com.hardcode.gdbms.engine.values.DateValue)
	 */
	public Value greaterEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof TimestampValue) {
			return new BooleanValue(
					this.value.compareTo(((TimestampValue) value).value) >= 0);
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Operations#less(com.hardcode.gdbms.engine.values.DateValue)
	 */
	public Value less(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof TimestampValue) {
			return new BooleanValue(
					this.value.compareTo(((TimestampValue) value).value) < 0);
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Operations#lessEqual(com.hardcode.gdbms.engine.values.DateValue)
	 */
	public Value lessEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof TimestampValue) {
			return new BooleanValue(
					this.value.compareTo(((TimestampValue) value).value) <= 0);
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Operations#notEquals(com.hardcode.gdbms.engine.values.DateValue)
	 */
	public Value notEquals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof TimestampValue) {
			return new BooleanValue(
					!this.value.equals(((TimestampValue) value).value));
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String toString() {
		return value.toString();
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Value#doHashCode()
	 */
	public int doHashCode() {
		return value.hashCode();
	}

	/**
	 * @return
	 */
	public Timestamp getValue() {
		return value;
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
		return Types.TIMESTAMP;
	}

	public int getWidth() {
		return this.toString().length();
	}
}
