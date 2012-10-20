package com.hardcode.gdbms.engine.values;

import java.io.Serializable;
import java.sql.Types;

import com.hardcode.gdbms.engine.instruction.IncompatibleTypesException;

/**
 * Wrapper sobre el tipo de datos String
 * 
 * @author Fernando González Cortés
 */
public class StringValue extends AbstractValue implements Serializable {
	private String value;

	/**
	 * Construye un objeto StringValue con el texto que se pasa como parametro
	 * 
	 * @param text
	 */
	StringValue(String text) {
		this.value = text;
	}

	/**
	 * Creates a new StringValue object.
	 */
	StringValue() {
	}

	/**
	 * Establece el valor de este objeto
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Obtiene el valor de este objeto
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.expression.Operations#suma(com.hardcode.gdbms.engine.instruction.expression.Value)
	 */
	public Value suma(Value v) throws IncompatibleTypesException {
		if (v instanceof IntValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						+ ((IntValue) v).getValue());

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else if (v instanceof LongValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						+ ((LongValue) v).getValue());

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else if (v instanceof FloatValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						+ ((FloatValue) v).getValue());

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else if (v instanceof DoubleValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						+ ((DoubleValue) v).getValue());

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else if (v instanceof StringValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						+ Double.parseDouble(((StringValue) v).getValue()));

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.expression.Operations#producto(com.hardcode.gdbms.engine.instruction.expression.Value)
	 */
	public Value producto(Value v) throws IncompatibleTypesException {
		if (v instanceof IntValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						* ((IntValue) v).getValue());

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else if (v instanceof LongValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						* ((LongValue) v).getValue());

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else if (v instanceof FloatValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						* ((FloatValue) v).getValue());

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else if (v instanceof DoubleValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						* ((DoubleValue) v).getValue());

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else if (v instanceof StringValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						* Double.parseDouble(((StringValue) v).getValue()));

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return value;
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#equals(com.hardcode.gdbms.engine.values.Value)
	 */
	public Value equals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return ValueFactory.createValue(this.value.equals(value.toString()));
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#greater(com.hardcode.gdbms.engine.values.BooleanValue)
	 */
	public Value greater(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return new BooleanValue(this.value.compareTo(value.toString()) > 0);
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#greaterEqual(com.hardcode.gdbms.engine.values.BooleanValue)
	 */
	public Value greaterEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return new BooleanValue(this.value.compareTo(value.toString()) >= 0);
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#less(com.hardcode.gdbms.engine.values.BooleanValue)
	 */
	public Value less(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return new BooleanValue(this.value.compareTo(value.toString()) < 0);
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#lessEqual(com.hardcode.gdbms.engine.values.BooleanValue)
	 */
	public Value lessEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return new BooleanValue(this.value.compareTo(value.toString()) <= 0);
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#notEquals(com.hardcode.gdbms.engine.values.BooleanValue)
	 */
	public Value notEquals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return new BooleanValue(!this.value.equals(value.toString()));
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Operations#like(com.hardcode.gdbms.engine.values.Value)
	 */
	public Value like(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof StringValue) {
			String pattern = ((StringValue) value).getValue().replaceAll("%",
					".*");
			pattern = pattern.replaceAll("\\?", ".");

			return new BooleanValue(this.value.matches(pattern));
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Value#doHashCode()
	 */
	public int doHashCode() {
		return value.hashCode();
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Value#getStringValue(com.hardcode.gdbms.engine.data.driver.ValueWriter)
	 */
	public String getStringValue(ValueWriter writer) {
		return writer.getStatementString(value, Types.VARCHAR);
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.Value#getSQLType()
	 */
	public int getSQLType() {
		return Types.LONGVARCHAR;
	}

	public int getWidth() {
		return getValue().length();
	}
}
