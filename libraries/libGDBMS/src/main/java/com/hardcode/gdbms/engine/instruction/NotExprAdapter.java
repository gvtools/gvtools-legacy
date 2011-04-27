package com.hardcode.gdbms.engine.instruction;

import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.parser.Node;


/**
 * Adapter de las Expresiones Not del arbol sintáctico
 *
 * @author Fernando González Cortés
 */
public class NotExprAdapter extends AbstractExpression implements Expression {
	private boolean not = false;

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
	 * @throws IncompatibleTypesException Si la expresión es una negación y la
	 * 		   expresión que se niega no es booleana
	 */
	public Value evaluate(long row) throws EvaluationException {
		Value ret = null;

		Expression c = (Expression) getChilds()[0];

		try {
			Value value = c.evaluateExpression(row);

			if (not) {
				((BooleanValue) value).setValue(!((BooleanValue) value).getValue());
			}

			return value;
		} catch (ClassCastException e) {
			throw new EvaluationException();
		}
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
		return ((Expression) getChilds()[0]).isLiteral();
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#simplify()
	 */
	public void simplify() {
		if (!not) {
			getParent().replaceChild(this, getChilds()[0]);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Adapter#setEntity(com.hardcode.gdbms.parser.Node)
	 */
	public void setEntity(Node o) {
		super.setEntity(o);

		String text = Utilities.getText(getEntity()).trim();

		if (text.startsWith("not")) {
			not = true;
		}
	}
}
