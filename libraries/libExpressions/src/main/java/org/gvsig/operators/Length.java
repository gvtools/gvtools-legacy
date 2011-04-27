package org.gvsig.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.AbstractOperator;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.expresions.EvalOperatorsTask;

/**
 * @author Vicente Caballero Navarro
 */
public class Length extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+")";
	}
	public String toString() {
		return "length";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"int length(String value){return value.length();};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def length(value):\n" +
				"  return len(value)");
	}
	public boolean isEnable() {
		return (getType()==IOperator.STRING);
	}
	public String getDescription() {
	    return  "parameter" + ": " +
	     "string_value" + "\n" +
	     "returns" + ": " +
	     "numeric_value" + "\n" +
	     "description" + ": " +
	    "Returns the length of string parameter.";
	}
}
