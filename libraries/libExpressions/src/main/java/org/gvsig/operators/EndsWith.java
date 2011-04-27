package org.gvsig.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.AbstractOperator;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.expresions.EvalOperatorsTask;

/**
 * @author Vicente Caballero Navarro
 */
public class EndsWith extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+",\"\")";
	}
	public String toString() {
		return "endsWith";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"boolean endsWith(String value1,String value2){return value1.endsWith(value2);};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def endsWith(value1,value2):\n" +
				"  return value1.endswith(value2)");
	}
	public boolean isEnable() {
		return (getType()==IOperator.STRING);
	}
	public String getTooltip(){
		return "operator"+":  "+toString()+ "("+ "parameter"+"1,"+"parameter"+"2"+")\n"+getDescription();
	}
	public String getDescription() {
        return  "parameter"+ "1"+": " +
         "string_value" + "\n"+
         "parameter" + "2"+": " +
         "string_value" + "\n"+
         "returns" + ": " +
         "boolean_value" + "\n" +
         "description" + ": " +
        "Tests if this parameter1 ends with the specified parameter2.";
    }
}
