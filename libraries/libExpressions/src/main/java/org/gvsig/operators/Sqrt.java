package org.gvsig.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.AbstractOperator;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.expresions.EvalOperatorsTask;

import com.iver.andami.PluginServices;

/**
 * @author Vicente Caballero Navarro
 */
public class Sqrt extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+")";
	}
	public String toString() {
		return "sqrt";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"double sqrt(double value){return java.lang.Math.sqrt(value);};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def sqrt(value):\n" +
				"  import java.lang.Math\n" +
				"  return java.lang.Math.sqrt(value)");
	}
	public boolean isEnable() {
		return (getType()==IOperator.NUMBER);
	}
	public String getDescription() {
        return PluginServices.getText(this, "parameter") + ": " +
        PluginServices.getText(this, "numeric_value") + "\n" +
        PluginServices.getText(this, "returns") + ": " +
        PluginServices.getText(this, "numeric_value") + "\n" +
        PluginServices.getText(this, "description") + ": " +
        "Returns the correctly rounded positive square root of a double value. Special cases:\n" +
        "* If the argument is NaN or less than zero, then the result is NaN.\n" +
        "* If the argument is positive infinity, then the result is positive infinity.\n" +
        "* If the argument is positive zero or negative zero, then the result is the same as the argument.";
    }
}
