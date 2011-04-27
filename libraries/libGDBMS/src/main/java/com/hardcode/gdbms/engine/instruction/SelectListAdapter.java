/*
 * Created on 12-oct-2004
 */
package com.hardcode.gdbms.engine.instruction;

import java.util.ArrayList;


/**
 * Adaptador
 *
 * @author Fernando González Cortés
 */
public class SelectListAdapter extends Adapter {
	private Expression[] fieldExpressions;
	private String[] fieldAliases;

	/**
	 * Obtiene las expresiones de los campos y los alias
	 */
	private void selectList() {
		Adapter[] hijos = getChilds();

		ArrayList exprs = new ArrayList();

		for (int i = 0; i < hijos.length; i++) {
			exprs.add((Expression) hijos[i]);
		}

		ArrayList aliases = new ArrayList();
		String[] campos = Utilities.getText(getEntity()).split(",");

		for (int i = 0; i < campos.length; i++) {
			int indice = campos[i].indexOf("as");

			if (indice != -1) {
				aliases.add(campos[i].substring(indice + 3));
			} else {
				aliases.add(null);
			}
		}

		fieldAliases = (String[]) aliases.toArray(new String[0]);
		fieldExpressions = (Expression[]) exprs.toArray(new Expression[0]);
	}

	/**
	 * Obtiene las expresiones de los campos
	 *
	 * @return
	 */
	public Expression[] getFieldsExpression() {
		if (fieldExpressions == null) {
			selectList();
		}

		return fieldExpressions;
	}

	/**
	 * Obtiene los alias de los campos
	 *
	 * @return
	 */
	public String[] getFieldsAlias() {
		if (fieldAliases == null) {
			selectList();
		}

		return fieldAliases;
	}
}
