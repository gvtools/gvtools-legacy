package com.hardcode.gdbms.engine.function;

import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando González Cortés
 */
public class BooleanFunction implements Function {
	/**
	 * @see com.hardcode.gdbms.engine.function.Function#evaluate(com.hardcode.gdbms.engine.values.Value[])
	 */
	public Value evaluate(Value[] args) throws FunctionException {
		if (args.length != 1) {
			throw new FunctionException("Use: boolean(true|false)");
		}

		return ValueFactory.createValue(Boolean.valueOf(args[0].toString())
				.booleanValue());
	}

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#getName()
	 */
	public String getName() {
		return "boolean";
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
		return new BooleanFunction();
	}
}
