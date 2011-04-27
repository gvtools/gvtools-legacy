package org.gvsig.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.AbstractOperator;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.expresions.EvalOperatorsTask;

/**
 * @author Vicente Caballero Navarro
 */
public class GetTimeDate extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+")";
	}
	public String toString() {
		return "getTimeDate";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"double getTimeDate(java.lang.Object value){return value.getTime();};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def getTimeDate(value):\n" +
				"  return value.getTime()");
	}
	public boolean isEnable() {
		return (getType()==IOperator.DATE);
	}
	public String getTooltip(){
		return "operator"+":  "+toString()+"("+"parameter"+"1,"+"parameter"+"2"+")"+"\n"+getDescription();
	}
	public String getDescription() {
        return  "parameter" +": " +
         "date_value" + "\n"+
         "returns" + ": " +
         "numeric_value" + "\n" +
         "description" + ": " +
        "Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this Date object.";
    }
}
