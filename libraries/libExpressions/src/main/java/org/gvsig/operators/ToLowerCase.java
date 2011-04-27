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
public class ToLowerCase extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+")";
	}
	public String toString() {
		return "toLowerCase";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"String toLowerCase(String value){return value.toLowerCase();};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def toLowerCase(value):\n" +
				"  return value.lower()");
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
	    "Converts all of the characters in this String to lower case using the rules of the default locale.";
	}
}
