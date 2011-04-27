package org.gvsig.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.AbstractOperator;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.expresions.EvalOperatorsTask;

/**
 * @author Vicente Caballero Navarro
 */
public class E extends AbstractOperator{

	public String addText(String s) {
		return s.concat(toString()+"()");
	}
	public String toString() {
		return "e";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"double e(){return java.lang.Math.E;};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def e():\n" +
				"  import java.lang.Math\n" +
				"  return java.lang.Math.E");
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
        "The double value that is closer than any other to e, the base of the natural logarithms.";
    }
}
