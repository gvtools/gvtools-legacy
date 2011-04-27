package org.gvsig.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.AbstractOperator;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.expresions.EvalOperatorsTask;

/**
 * @author Vicente Caballero Navarro
 */
public class Replace extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+" , \"\",\"\")";
	}
	public String toString() {
		return "replace";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"String replace(String value1,String value2,String value3){return value1.replaceAll(value2,value3);};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def replace(value1,value2,value3):\n" +
				"  return value1.replace(value2,value3)");
	}
	public boolean isEnable() {
		return (getType()==IOperator.STRING);
	}
	public String getTooltip(){
		return "operator"+":  "+toString()+"("+"parameter"+"1,"+"parameter"+"2, "+"parameter"+"3"+")"+"\n"+getDescription();
	}
	public String getDescription() {
        return  "parameter" + "1"+": " +
         "string_value" + "\n"+
         "parameter" + "2"+": " +
         "string_value" + "\n"+
         "parameter" + "3"+": " +
         "string_value" + "\n"+
         "returns" + ": " +
         "string_value" + "\n" +
         "description" + ": " +
        "Replaces each substring(parameter2) of parameter1 string that matches the given regular expression with the given replacement parameter3";
    }
}
