package com.hardcode.gdbms.engine.function;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.hardcode.gdbms.engine.values.StringValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando González Cortés
 */
public class DateFunction implements Function {
	/**
	 * @see com.hardcode.gdbms.engine.function.Function#evaluate(com.hardcode.gdbms.engine.values.Value[])
	 */
	public Value evaluate(Value[] args) throws FunctionException {
		if ((args.length < 1) || (args.length > 2)) {
			throw new FunctionException(
					"use: date('date_literal'[ , date_format])");
		}

		if (!(args[0] instanceof StringValue)) {
			throw new FunctionException("date parameters must be strings");
		}

		DateFormat df;

		if (args.length == 2) {
			if ((args[1] instanceof StringValue)) {
				df = new SimpleDateFormat(((StringValue) args[1]).getValue());
			} else {
				throw new FunctionException("date parameters must be strings");
			}
		} else {
			df = DateFormat.getDateInstance();
		}

		try {
			return ValueFactory.createValue(df.parse(((StringValue) args[0])
					.getValue()));
		} catch (ParseException e) {
			throw new FunctionException(
					"date format must match DateFormat java class requirements",
					e);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#getName()
	 */
	public String getName() {
		return "date";
	}

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#isAggregate()
	 */
	public boolean isAggregate() {
		return false;
	}

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#cloneFunction()
	 */
	public Function cloneFunction() {
		return new DateFunction();
	}
}
