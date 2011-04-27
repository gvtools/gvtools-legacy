package com.hardcode.gdbms.engine.instruction;

import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;


/**
 * Adaptador sobre las expresiones or del arbol sintáctico
 *
 * @author Fernando González Cortés
 */
public class OrExprAdapter extends AbstractExpression implements Expression {
	/**
	 * Evalua expresión invocando el método adecuado en función del tipo de
	 * expresion (suma, producto, ...) de los objetos Value de la expresion,
	 * de las subexpresiones y de los objetos Field
	 *
	 * @param row Fila en la que se evalúa la expresión, en este caso no es
	 * 		  necesario, pero las subexpresiones sobre las que se opera pueden
	 * 		  ser campos de una tabla, en cuyo caso si es necesario
	 *
	 * @return Objeto Value resultado de la operación propia de la expresión
	 * 		   representada por el nodo sobre el cual éste objeto es adaptador
	 *
	 * @throws SemanticException Si se produce un error semántico
	 * @throws DriverException Si se produce un error de I/O
	 */
	public Value evaluate(long row) throws EvaluationException {
		Value ret = null;

		Adapter[] expr = getChilds();

		if (expr.length > 0) {
			ret = ((Expression) expr[0]).evaluateExpression(row);

			for (int i = 1; i < expr.length; i++) {
				try {
                    ret = ret.or(((Expression) expr[i]).evaluateExpression(row));
                } catch (IncompatibleTypesException e) {
                    throw new EvaluationException();
                }
			}
		}

		return ret;
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		return null;
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#isLiteral()
	 */
	public boolean isLiteral() {
		return Utilities.checkExpressions(getChilds());
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#simplify()
	 */
	public void simplify() {
		Adapter[] childs = getChilds();

		if (childs.length == 1) {
			getParent().replaceChild(this, childs[0]);
		}
	}
}
