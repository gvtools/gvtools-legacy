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
public class SetTimeDate extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+",\"\")";
	}
	public String toString() {
		return "setTimeDate";
	}
	public void eval(BSFManager interpreter) throws BSFException {
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"java.util.Date setTimeDate(java.lang.Object value1,double value2){value1.setTime((long)value2);return value1;};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def setTimeDate(value1,value2):\n" +
				"  value1.setTime(value2)\n" +
				"  return value1");
	}
	public boolean isEnable() {
		return (getType()==IOperator.DATE);
	}
	public String getTooltip(){
		return PluginServices.getText(this,"operator")+":  "+toString()+"("+PluginServices.getText(this,"parameter")+"1,"+PluginServices.getText(this,"parameter")+"2"+")"+"\n"+getDescription();
	}
	public String getDescription() {
       return PluginServices.getText(this, "parameter") + "1"+": " +
        PluginServices.getText(this, "date_value") + "\n"+
        PluginServices.getText(this, "parameter") + "2"+": " +
        PluginServices.getText(this, "numeric_value") + "\n"+
        PluginServices.getText(this, "returns") + ": " +
        PluginServices.getText(this, "date_value") + "\n" +
        PluginServices.getText(this, "description") + ": " +
        "Returns the parameter1 date object to represent a point in time that is time milliseconds after January 1, 1970 00:00:00 GMT.";
    }
}
