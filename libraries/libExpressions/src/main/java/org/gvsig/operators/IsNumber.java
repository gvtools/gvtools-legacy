package org.gvsig.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.AbstractOperator;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.expresions.EvalOperatorsTask;

/**
 * @author Vicente Caballero Navarro
 */
public class IsNumber extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+")";
	}
	public String toString() {
		return "isNumber";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"boolean isNumber (String value){try{java.lang.Double.parseDouble(value);}catch(java.lang.NumberFormatException e){return false;}return true;};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def isNumber(value):\n" +
				"  import java.lang.Double\n" +
				"  import java.lang.NumberFormatException\n" +
				"  try:\n" +
				"    java.lang.Double.parseDouble(value)\n" +
				"  except java.lang.NumberFormatException:\n" +
				"    return 0==1 #false\n" +
				"  return 1==1 #true\n");
	}
	public boolean isEnable() {
		return (getType()==IOperator.STRING);
	}
	public String getDescription() {
	    return  "parameter" + ": " +
	     "string_value" + "\n" +
	     "returns" + ": " +
	     "boolean_value" + "\n" +
	     "description" + ": " +
	    "Returns true if the string parameter is a number.";
	}
}
