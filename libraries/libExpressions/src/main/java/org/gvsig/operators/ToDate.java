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
public class ToDate extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+")";
	}
	public String toString() {
		return "toDate";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"java.util.Date toDate(String value){return new java.text.SimpleDateFormat().parse(value);};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def toDate(value):\n" +
				"  import java.text.SimpleDateFormat\n" +
				"  sdf=instance(java.text.SimpleDateFormat)\n" +
				"  return sdf.parse(value)");
	}
	public boolean isEnable() {
		return (getType()==IOperator.DATE);
	}
	public String getDescription() {
		return PluginServices.getText(this, "parameter") + ": " +
	    PluginServices.getText(this, "string_value") + "\n" +
	    PluginServices.getText(this, "returns") + ": " +
	    PluginServices.getText(this, "date_value") + "\n" +
	    PluginServices.getText(this, "description") + ": " +
	    "Returns a date object from string parameter.";
	}
}
