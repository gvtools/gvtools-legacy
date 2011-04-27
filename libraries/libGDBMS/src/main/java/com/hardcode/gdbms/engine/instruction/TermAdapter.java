package com.hardcode.gdbms.engine.instruction;

import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.parser.ASTSQLColRef;
import com.hardcode.gdbms.parser.ASTSQLFunction;
import com.hardcode.gdbms.parser.ASTSQLLiteral;
import com.hardcode.gdbms.parser.ASTSQLOrExpr;
import com.hardcode.gdbms.parser.SimpleNode;


/**
 * Wrapper sobre el nodo Term del arbol sintáctico
 *
 * @author Fernando González Cortés
 */
public class TermAdapter extends AbstractExpression implements Expression {
	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#evaluate(long)
	 */
	public Value evaluate(long row) throws EvaluationException {
		Adapter[] hijos = getChilds();

		if (hijos[0] instanceof Expression) {
			return ((Expression) hijos[0]).evaluateExpression(row);
		} else {
			return null;
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		SimpleNode child = (SimpleNode) getEntity().jjtGetChild(0);

		if (child.first_token.image.equals("(")) {
			child = (SimpleNode) getEntity().jjtGetChild(0);
		}

		if (child.getClass() == ASTSQLColRef.class) {
			return Utilities.getText(child);
		} else {
			return null;
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#isLiteral()
	 */
	public boolean isLiteral() {
		SimpleNode child = (SimpleNode) getEntity().jjtGetChild(0);

		if (child.first_token.image.equals("(")) {
			child = (SimpleNode) getEntity().jjtGetChild(0);
		}

		if (child.getClass() == ASTSQLColRef.class) {
			return false;
		} else if (child.getClass() == ASTSQLFunction.class) {
			return false;
		} else if (child.getClass() == ASTSQLLiteral.class) {
			return true;
		} else if (child.getClass() == ASTSQLOrExpr.class) {
			return ((Expression) getChilds()[0]).isLiteral();
		} else {
			throw new RuntimeException("really passed the parse???");
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#simplify()
	 */
	public void simplify() {
		getParent().replaceChild(this, getChilds()[0]);
	}

    /**
     * @see com.hardcode.gdbms.engine.instruction.Expression#isAggregated()
     */
    public boolean isAggregated() {
		Adapter[] expr = (Adapter[]) getChilds();

		if (expr.length != 1) {
			return false;
		} else {
			return ((Expression) expr[0]).isAggregated();
		}
    }
}
