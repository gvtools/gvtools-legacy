package org.gvsig.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.AbstractOperator;
import org.gvsig.baseclasses.IOperator;

/**
 * @author Vicente Caballero Navarro
 */
public class Division extends AbstractOperator{

	public String addText(String s) {
		return s.concat(toString());
	}

	public void eval(BSFManager interpreter) throws BSFException {
	}
	public String toString() {
		return "/";
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
        "Returns the result of division.";
    }
}
