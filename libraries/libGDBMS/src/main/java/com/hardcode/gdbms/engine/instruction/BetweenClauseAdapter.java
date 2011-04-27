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
public class BetweenClauseAdapter extends Adapter {
 
    public Value getInfValue(long row) throws EvaluationException {
        return ((Expression)getChilds()[0]).evaluate(row);
    }

    public Value getSupValue(long row) throws EvaluationException {
        return ((Expression)getChilds()[1]).evaluate(row);
    }
    
    public boolean isNegated() {
        return (getEntity().first_token.image.toLowerCase().equals("not"));
    }

}
