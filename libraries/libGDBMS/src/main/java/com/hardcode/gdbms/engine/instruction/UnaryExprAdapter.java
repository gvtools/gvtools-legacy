package com.hardcode.gdbms.engine.instruction;

import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.hardcode.gdbms.parser.Node;


/**
 * Wrapper sobre las expresiones unarias en el arbol sintáctico de entrada
 *
 * @author Fernando González Cortés
 */
public class UnaryExprAdapter extends AbstractExpression implements Expression {
	private boolean signChange = false;

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

		Adapter[] terms = getChilds();

		if (terms.length > 0) {
			ret = ((Expression) terms[0]).evaluateExpression(row);

			for (int i = 1; i < terms.length; i++) {
				try {
                    ret = ret.suma(((Expression) terms[i]).evaluateExpression(row));
                } catch (IncompatibleTypesException e) {
                    throw new EvaluationException();
                }
			}
		}

		if (signChange) {
			Value menosUno = ValueFactory.createValue(-1);
			try {
                ret = ret.producto(menosUno);
            } catch (IncompatibleTypesException e) {
                throw new EvaluationException();
            }
		}

		return ret;
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		Adapter[] expr = (Adapter[]) getChilds();

		if (expr.length != 1) {
			return null;
		} else {
			return ((Expression) expr[0]).getFieldName();
		}
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
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Adapter#setEntity(com.hardcode.gdbms.parser.Node)
	 */
	public void setEntity(Node o) {
		super.setEntity(o);

		String text = Utilities.getText(getEntity());

		if (text.startsWith("-")) {
			signChange = true;
		}
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
