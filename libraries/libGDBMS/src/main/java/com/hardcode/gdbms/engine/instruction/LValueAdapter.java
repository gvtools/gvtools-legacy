/*
 * Created on 12-oct-2004
 */
package com.hardcode.gdbms.engine.instruction;

import com.hardcode.gdbms.engine.values.Value;


/**
 * Adaptador
 *
 * @author Fernando González Cortés
 */
public class LValueAdapter extends AbstractExpression {
	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		return ((Expression) getChilds()[0]).getFieldName();
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#simplify()
	 */
	public void simplify() {
		getParent().replaceChild(this, getChilds()[0]);
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.CachedExpression#evaluate(long)
	 */
	public Value evaluate(long row) throws EvaluationException {
		return ((Expression) getChilds()[0]).evaluateExpression(row);
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.CachedExpression#isLiteral()
	 */
	public boolean isLiteral() {
		return ((Expression) getChilds()[0]).isLiteral();
	}
}
