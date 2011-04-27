/*
 * Created on 12-oct-2004
 */
package com.hardcode.gdbms.engine.instruction;

/**
 * Adaptador
 *
 * @author Fernando González Cortés
 */
public class WhereAdapter extends Adapter {
	/**
	 * Obtiene la expresión del where
	 *
	 * @return Expression
	 */
	public Expression getExpression() {
		//Ha de ser un OrExprAdapter
		return (Expression) getChilds()[0];
	}
}
