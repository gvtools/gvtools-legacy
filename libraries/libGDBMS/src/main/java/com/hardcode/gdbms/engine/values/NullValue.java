package com.hardcode.gdbms.engine.values;

import java.io.Serializable;
import java.sql.Types;

import com.hardcode.gdbms.engine.instruction.IncompatibleTypesException;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando González Cortés
 */
public class NullValue extends AbstractValue implements Serializable {
	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String toString() {
		return "";
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Value#doHashCode()
	 */
	public int doHashCode() {
		return 0;
	}

	public Value and(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	public Value equals(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	public Value greater(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	public Value greaterEqual(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	public Value less(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	public Value lessEqual(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	public Value like(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	public Value notEquals(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	public Value or(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Value#getStringValue(com.hardcode.gdbms.engine.data.driver.ValueWriter)
	 */
	public String getStringValue(ValueWriter writer) {
		return writer.getNullStatementString();
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Value#getSQLType()
	 */
	public int getSQLType() {
		return Types.NULL;
	}

	public int getWidth() {
		return 0;
	}
}
