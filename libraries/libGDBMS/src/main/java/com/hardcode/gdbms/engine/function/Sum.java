package com.hardcode.gdbms.engine.function;

import com.hardcode.gdbms.engine.instruction.IncompatibleTypesException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;

/**
 * @author Fernando González Cortés
 */
public class Sum implements Function {

	private Value acum = ValueFactory.createValue(0);

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#evaluate(com.hardcode.gdbms.engine.values.Value[])
	 */
	public Value evaluate(Value[] args) throws FunctionException {
		try {
			acum = acum.suma(args[0]);
		} catch (IncompatibleTypesException e) {
			throw new FunctionException(e);
		}

		return acum;
	}

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#getName()
	 */
	public String getName() {
		return "sum";
	}

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#isAggregate()
	 */
	public boolean isAggregate() {
		return true;
	}

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#cloneFunction()
	 */
	public Function cloneFunction() {
		return new Sum();
	}

}
