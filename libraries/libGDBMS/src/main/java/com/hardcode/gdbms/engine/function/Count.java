package com.hardcode.gdbms.engine.function;

import com.hardcode.gdbms.engine.values.IntValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;


/**
 * @author Fernando González Cortés
 */
public class Count implements Function {
    private IntValue v = ValueFactory.createValue(0);

    /**
     * @see com.hardcode.gdbms.engine.function.AggregateFunction#getName()
     */
    public String getName() {
        return "count";
    }

    /**
     * @see com.hardcode.gdbms.engine.function.Function#evaluate(com.hardcode.gdbms.engine.values.Value[])
     */
    public Value evaluate(Value[] args) throws FunctionException {
        v.setValue(v.getValue()+1);
        return v;
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
        return new Count();
    }

}
