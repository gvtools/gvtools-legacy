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
public class Trim extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+")";
	}
	public String toString() {
		return "trim";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"String trim(String value){return value.trim();};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def trim(value):\n" +
				"  return value.strip()");
	}
	public boolean isEnable() {
		return (getType()==IOperator.STRING);
	}
	public String getDescription() {
	    return PluginServices.getText(this, "parameter") + ": " +
	    PluginServices.getText(this, "string_value") + "\n" +
	    PluginServices.getText(this, "returns") + ": " +
	    PluginServices.getText(this, "string_value") + "\n" +
	    PluginServices.getText(this, "description") + ": " +
	    "Returns a copy of the string, with leading and trailing whitespace omitted.";
	}
}
