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
public class ToUpperCase extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+")";
	}
	public String toString() {
		return "toUpperCase";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"String toUpperCase(String value){return value.toUpperCase();};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def toUpperCase(value):\n" +
				"  return value.upper()");
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
	    "Converts all of the characters in this String to upper case using the rules of the given Locale.\n" +
	    "Case mappings rely heavily on the Unicode specification's character data.\n" +
	    "Since case mappings are not always 1:1 char mappings, the resulting String may be a different length than the original String.";
	}
}
