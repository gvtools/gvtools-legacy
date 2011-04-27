package com.hardcode.gdbms.engine.values;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Types;
import java.text.DateFormat;

import com.hardcode.gdbms.engine.instruction.IncompatibleTypesException;


/**
 * Wrapper sobre el tipo Date
 *
 * @author Fernando González Cortés
 */
public class TimeValue extends AbstractValue implements Serializable {
	private Time value;

	/**
	 * Creates a new DateValue object.
	 *
	 * @param d DOCUMENT ME!
	 */
	TimeValue(Time d) {
		value = d;
	}

	/**
	 * Creates a new DateValue object.
	 */
	TimeValue() {
	}

	/**
	 * Establece el valor
	 *
	 * @param d valor
	 */
	public void setValue(Time d) {
		value = d;
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Operations#equals(com.hardcode.gdbms.engine.values.DateValue)
	 */
	public Value equals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof TimeValue) {
			return new BooleanValue(this.value.equals(((TimeValue) value).value));
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

		if (value instanceof TimeValue) {
			return new BooleanValue(this.value.compareTo(
					((TimeValue) value).value) > 0);
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

		if (value instanceof TimeValue) {
			return new BooleanValue(this.value.compareTo(
					((TimeValue) value).value) >= 0);
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

		if (value instanceof TimeValue) {
			return new BooleanValue(this.value.compareTo(
					((TimeValue) value).value) < 0);
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

		if (value instanceof TimeValue) {
			return new BooleanValue(this.value.compareTo(
					((TimeValue) value).value) <= 0);
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

		if (value instanceof TimeValue) {
			return new BooleanValue(!this.value.equals(
					((TimeValue) value).value));
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
		return DateFormat.getTimeInstance().format(value);
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
    public Time getValue() {
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
        return Types.TIME;
    }
	public int getWidth() {
		return this.toString().length();
	}    
}
