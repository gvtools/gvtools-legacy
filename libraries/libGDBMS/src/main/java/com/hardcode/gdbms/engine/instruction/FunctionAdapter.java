package com.hardcode.gdbms.engine.instruction;

import com.hardcode.gdbms.engine.function.Function;
import com.hardcode.gdbms.engine.function.FunctionException;
import com.hardcode.gdbms.engine.function.FunctionManager;
import com.hardcode.gdbms.engine.values.Value;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class FunctionAdapter extends AbstractExpression implements Expression {
	private Function function;

    /**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		return null;
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#simplify()
	 */
	public void simplify() {
		//Nunca se simplifica una función
	}

	public String getFunctionName(){
	    return getEntity().first_token.image;
	}

	public boolean isAggregated(){
	    return FunctionManager.getFunction(getFunctionName()).isAggregate();
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#evaluate(long)
	 */
	public Value evaluate(long row) throws EvaluationException {
		String functionName = getEntity().first_token.image;

		Function func = getFunction();

		if (func == null) {
			throw new EvaluationException("No function called " + functionName);
		}

		Adapter[] params = this.getChilds()[0].getChilds();
		Value[] paramValues = new Value[params.length];

		for (int i = 0; i < paramValues.length; i++) {
			paramValues[i] = ((Expression) params[i]).evaluate(row);
		}

		try {
			return func.evaluate(paramValues);
		} catch (FunctionException e) {
			throw new EvaluationException("Function error");
		}
	}

	/**
     * @return
     */
    private Function getFunction() {
        if (function == null) {
            function = FunctionManager.getFunction(getFunctionName());

        }

        return function;
    }

    /**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#isLiteral()
	 */
	public boolean isLiteral() {
		return Utilities.checkExpressions(this.getChilds()[0].getChilds());
	}
}
