package org.gvsig.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.AbstractOperator;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.expresions.EvalOperatorsTask;

/**
 * @author Vicente Caballero Navarro
 */
public class Pi extends AbstractOperator{

	public String addText(String s) {
		return s.concat(toString()+"()");
	}
	public String toString() {
		return "pi";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"double pi(){return java.lang.Math.PI;};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def pi():\n" +
				"  import java.lang.Math\n" +
				"  return java.lang.Math.PI");
	}
	public boolean isEnable() {
		return (getType()==IOperator.NUMBER);
	}
	public String getTooltip(){
		return "operator"+":  "+addText("")+"\n"+getDescription();
	}
	public String getDescription() {
        return  "returns" + ": " +
         "numeric_value" + "\n" +
         "description" + ": " +
        "The double value that is closer than any other to pi, the ratio of the circumference of a circle to its diameter.";
    }
}
