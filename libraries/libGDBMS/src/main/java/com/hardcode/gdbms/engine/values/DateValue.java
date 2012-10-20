package com.hardcode.gdbms.engine.values;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Types;
import java.text.DateFormat;

import com.hardcode.gdbms.engine.instruction.IncompatibleTypesException;

/**
 * Wrapper sobre el tipo Date
 * 
 * @author Fernando González Cortés
 */
public class DateValue extends AbstractValue implements Serializable {
	private Date value;

	/**
	 * Creates a new DateValue object.
	 * 
	 * @param d
	 *            Data to set
	 */
	DateValue(Date d) {
		value = d;
	}

	/**
     * 
     */
	public DateValue() {
	}

	/**
	 * Establece el valor
	 * 
	 * @param d
	 *            valor
	 */
	public void setValue(java.util.Date d) {
		value = new Date(d.getTime());
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Operations#equals(com.hardcode.gdbms.engine.values.DateValue)
	 */
	public Value equals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof DateValue) {
			return new BooleanValue(
					this.value.equals(((DateValue) value).value));
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

		if (value instanceof DateValue) {
			return new BooleanValue(
					this.value.compareTo(((DateValue) value).value) > 0);
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

		if (value instanceof DateValue) {
			return new BooleanValue(
					this.value.compareTo(((DateValue) value).value) >= 0);
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

		if (value instanceof DateValue) {
			return new BooleanValue(
					this.value.compareTo(((DateValue) value).value) < 0);
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

		if (value instanceof DateValue) {
			return new BooleanValue(
					this.value.compareTo(((DateValue) value).value) <= 0);
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

		if (value instanceof DateValue) {
			return new BooleanValue(
					!this.value.equals(((DateValue) value).value));
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
		return DateFormat.getDateInstance().format(value);
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Value#doHashCode()
	 */
	public int doHashCode() {
		return value.hashCode();
	}

	public Date getValue() {
		return value;
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Value#getStringValue(com.hardcode.gdbms.engine.data.driver.ValueWriter)
	 */
	public String getStringValue(ValueWriter writer) {
		return writer.getStatementString(value);
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Value#getType()
	 */
	public int getSQLType() {
		return Types.DATE;
	}

	public int getWidth() {
		return DateFormat.getDateInstance().format(value).length();
	}
}
