package org.gvsig.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.AbstractOperator;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.expresions.EvalOperatorsTask;

/**
 * @author Vicente Caballero Navarro
 */
public class LastIndexOf extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+", \"\")";
	}
	public String toString() {
		return "lastIndexOf";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"int lastIndexOf(String value1,String value2){return value1.lastIndexOf(value2);};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def lastIndexOf(value1,value2):\n" +
				"  return value1.rfind(value2)");
	}
	public boolean isEnable() {
		return (getType()==IOperator.STRING);
	}
	public String getTooltip(){
		return "operator"+":  "+toString()+"("+"parameter"+"1,"+"parameter"+"2"+")"+"\n"+getDescription();
	}
	public String getDescription() {
        return  "parameter" + "1"+": " +
         "string_value" + "\n"+
         "parameter" + "2"+": " +
         "string_value" + "\n"+
         "returns" + ": " +
         "boolean_value" + "\n" +
         "description" + ": " +
        "Returns the index within parameter1 of the last occurrence of the parameter2 character.";
    }
}
