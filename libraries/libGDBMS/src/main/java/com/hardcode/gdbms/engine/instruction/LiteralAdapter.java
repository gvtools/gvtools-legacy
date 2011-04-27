/*
 * Created on 12-oct-2004
 */
package com.hardcode.gdbms.engine.instruction;

import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.hardcode.gdbms.parser.SimpleNode;


/**
 * Adaptador
 *
 * @author Fernando González Cortés
 */
public class LiteralAdapter extends AbstractExpression {
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
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.CachedExpression#evaluate(long)
	 */
	public Value evaluate(long row) throws EvaluationException {
		SimpleNode n = getEntity();

		try {
			String text = Utilities.getText(n);
			if (!text.equals("''")) {
				// Single quotes are escaped in GDBMs by doubling them, but we need to un-escape them to process the real string
				return ValueFactory.createValue(Utilities.getText(n).replaceAll("''", "'"),
						Utilities.getType(n));
			}
			else {
				// We should not un-escape the '' literal 
				return ValueFactory.createValue(Utilities.getText(n),
						Utilities.getType(n));
			}
        } catch (SemanticException e) {
            throw new EvaluationException();
        }
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.CachedExpression#isLiteral()
	 */
	public boolean isLiteral() {
		return true;
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#calculateLiteralCondition()
	 */
	public void calculateLiteralCondition() {
	}
}
