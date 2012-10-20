/*
 * Created on 12-oct-2004
 */
package com.hardcode.gdbms.engine.instruction;

/**
 * Adaptador
 * 
 * @author Fernando González Cortés
 */
public class CompareOpAdapter extends Adapter {
	/**
	 * Obtiene el operador
	 * 
	 * @return opertador
	 */
	public int getOperator() {
		return getEntity().first_token.kind;
	}
}
