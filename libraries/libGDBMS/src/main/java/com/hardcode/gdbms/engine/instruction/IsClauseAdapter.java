/*
 * Created on 12-oct-2004
 */
package com.hardcode.gdbms.engine.instruction;

import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;

/**
 * Adaptador
 *
 * @author Fernando González Cortés
 */
public class IsClauseAdapter extends AbstractExpression implements Expression {

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
     * @see com.hardcode.gdbms.engine.instruction.Expression#evaluate(long)
     */
    public Value evaluate(long row) throws EvaluationException {
        Value value = ((Expression)getChilds()[0]).evaluate(row);
        boolean b = value instanceof NullValue;
        if (getEntity().first_token.next.next.image.toLowerCase().equals("not")) b = !b;
        return ValueFactory.createValue(b);
    }

    /**
     * @see com.hardcode.gdbms.engine.instruction.Expression#isLiteral()
     */
    public boolean isLiteral() {
        return false;
    }
}
