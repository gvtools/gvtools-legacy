package com.hardcode.gdbms.engine.function;

import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class ConcatenateFunction implements Function {
	/**
	 * @see com.hardcode.gdbms.engine.function.Function#evaluate(com.hardcode.gdbms.engine.values.Value[])
	 */
	public Value evaluate(Value[] args) throws FunctionException {
		String ret = "";

		for (int i = 0; i < args.length; i++) {
			ret = ret + args[i].toString();
		}

		return ValueFactory.createValue(ret);
	}

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#getName()
	 */
	public String getName() {
		return "concatenate";
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
        return new ConcatenateFunction();
    }
}
