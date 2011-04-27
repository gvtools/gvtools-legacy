package org.gvsig.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.AbstractOperator;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.expresions.EvalOperatorsTask;

/**
 * @author Vicente Caballero Navarro
 */
public class Acos extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+")";
	}
	public String toString() {
		return "acos";
	}
	public void eval(BSFManager interpreter) throws BSFException {
		interpreter.exec(EvalOperatorsTask.JYTHON, null, -1, -1,
	            "def acos(value):\n" +
	            "  import java.lang.Math\n"+
	            "  return java.lang.Math.acos(value)\n");
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"double acos(double value){return java.lang.Math.acos(value);};");
	}
	public boolean isEnable() {
		return (getType()==IOperator.NUMBER);
	}
	public String getDescription() {
        return  "parameter" + ": " +
        "numeric_value" + "\n" +
        "returns" + ": " +
        "numeric_value" + "\n" +
        "description" + ": " +
        "Returns the arc cosine of an angle, in the range of 0.0 through pi. Special case:\n" +
        "* If the argument is NaN or its absolute value is greater than 1, then the result is NaN.";
    }
}
