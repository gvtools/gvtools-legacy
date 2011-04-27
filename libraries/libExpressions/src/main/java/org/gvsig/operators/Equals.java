package org.gvsig.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.AbstractOperator;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.expresions.EvalOperatorsTask;

/**
 * @author Vicente Caballero Navarro
 */
public class Equals extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+", \"\")";
	}
	public String toString() {
		return "equals";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"boolean equals(java.lang.Object value1,java.lang.Object value2){return value1.equals(value2);};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def equals(value1,value2):\n" +
				"  return value1 == value2");
	}
	public boolean isEnable() {
		return (getType()==IOperator.STRING || getType()==IOperator.DATE);
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
        "Compares the parameter1 to the parameter2. The result is true if and only if the arguments are not null and represents the same object.";
    }
}
