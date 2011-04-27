package org.gvsig.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.AbstractOperator;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.expresions.EvalOperatorsTask;

/**
 * @author Vicente Caballero Navarro
 */
public class Before extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+",\"\")";
	}
	public String toString() {
		return "before";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"boolean before(java.lang.Object value1,java.lang.Object value2){return value1.before(value2);};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def before(value1, value2):\n" +
				"  return value1.before(value2)");
	}
	public boolean isEnable() {
		return (getType()==IOperator.DATE);
	}
	public String getTooltip(){
		return "operator"+":  "+toString()+"("+"parameter"+"1,"+"parameter"+"2"+")"+"\n"+getDescription();
	}
	public String getDescription() {
        return "parameter" + "1"+": " +
        "date_value" + "\n"+
        "parameter" + "2"+": " +
        "date_value" + "\n"+
        "returns" + ": " +
        "boolean_value" + "\n" +
        "description" + ": " +
        "Tests if parameter1 date is before the parameter2 date.";
    }

}
