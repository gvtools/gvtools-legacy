package com.hardcode.gdbms.engine.values;

import java.io.Serializable;

import com.hardcode.gdbms.engine.instruction.IncompatibleTypesException;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public abstract class NumericValue extends AbstractValue implements Serializable {
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public abstract byte byteValue();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public abstract short shortValue();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public abstract int intValue();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public abstract long longValue();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public abstract float floatValue();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public abstract double doubleValue();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public abstract int getType();

	/**
	 * DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IncompatibleTypesException DOCUMENT ME!
	 */
	public Value producto(Value value) throws IncompatibleTypesException {
		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory.producto(this, (NumericValue) value);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IncompatibleTypesException DOCUMENT ME!
	 */
	public Value suma(Value value) throws IncompatibleTypesException {
		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory.suma(this, (NumericValue) value);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IncompatibleTypesException DOCUMENT ME!
	 */
	public Value inversa() throws IncompatibleTypesException {
		return ValueFactory.inversa(this);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IncompatibleTypesException DOCUMENT ME!
	 */
	public Value equals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory.createValue(this.doubleValue() == ((NumericValue) value).doubleValue());
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IncompatibleTypesException DOCUMENT ME!
	 */
	public Value greater(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory.createValue(this.doubleValue() > ((NumericValue) value).doubleValue());
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IncompatibleTypesException DOCUMENT ME!
	 */
	public Value greaterEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory.createValue(this.doubleValue() >= ((NumericValue) value).doubleValue());
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IncompatibleTypesException DOCUMENT ME!
	 */
	public Value less(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory.createValue(this.doubleValue() < ((NumericValue) value).doubleValue());
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IncompatibleTypesException DOCUMENT ME!
	 */
	public Value lessEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory.createValue(this.doubleValue() <= ((NumericValue) value).doubleValue());
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IncompatibleTypesException DOCUMENT ME!
	 */
	public Value notEquals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory.createValue(this.doubleValue() != ((NumericValue) value).doubleValue());
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Value#doHashCode()
	 */
	public int doHashCode() {
		return intValue();
	}
}
