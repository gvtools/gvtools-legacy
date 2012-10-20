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
public class ExistsClauseAdapter extends AbstractExpression implements
		Expression {

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#simplify()
	 */
	public void simplify() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#evaluate(long)
	 */
	public Value evaluate(long row) throws EvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#isLiteral()
	 */
	public boolean isLiteral() {
		// TODO Auto-generated method stub
		return false;
	}
}
