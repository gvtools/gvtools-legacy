package org.gvsig.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.AbstractOperator;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.expresions.EvalOperatorsTask;

/**
 * @author Vicente Caballero Navarro
 */
public class Cos extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+")";
	}
	public String toString() {
		return "cos";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"double cos(double value){return java.lang.Math.cos(value);};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def cos(value):\n" +
				"  import java.lang.Math\n" +
				"  return java.lang.Math.cos(value)");
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
        "Returns the trigonometric cosine of an angle. Special cases:\n" +
        "* If the argument is NaN or an infinity, then the result is NaN.";
    }
}
