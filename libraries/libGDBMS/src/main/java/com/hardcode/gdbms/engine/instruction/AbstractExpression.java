package com.hardcode.gdbms.engine.instruction;

import com.hardcode.gdbms.engine.values.Value;


/**
 * Adaptador
 *
 * @author Fernando González Cortés
 */
public abstract class AbstractExpression extends Adapter implements Expression {
	private boolean literal;
	private boolean literalCalculated = false;
	private Value value;

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#evaluateExpression(long)
	 */
	public Value evaluateExpression(long row)
		throws EvaluationException {
		if (!getLiteralCondition()) {
			return evaluate(row);
		} else {
			if (value == null) {
				return (value = evaluate(row));
			} else {
				return value;
			}
		}
	}


    public boolean getLiteralCondition() {
        if (!literalCalculated){
            literal = isLiteral();
        }

        return literal;
    }

    /**
     * @see com.hardcode.gdbms.engine.instruction.Expression#isAggregated()
     */
    public boolean isAggregated() {
        return false;
    }

}
