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
public class InClauseAdapter extends Adapter {

	public boolean isNegated() {
		return getEntity().first_token.image.toLowerCase().equals("not");
	}

	public int getListLength() {
		return ((LValueListAdapter) getChilds()[0]).getListLength();
	}

	public Value getLValue(int index, long rowIndex) throws EvaluationException {
		return ((LValueListAdapter) getChilds()[0]).getLValue(index, rowIndex);
	}
}