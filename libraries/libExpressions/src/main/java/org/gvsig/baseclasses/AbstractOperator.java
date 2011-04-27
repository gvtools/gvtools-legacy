package org.gvsig.baseclasses;


/**
 * @author Vicente Caballero Navarro
 */
public abstract class AbstractOperator implements IOperator{
	private int type;

	public void setType(int type) {
		this.type=type;
	}
	public int getType() {
		return type;
	}
	public abstract String toString();

	public String getTooltip(){
		return "Operator :  "+addText("parameter")+"\n"+getDescription();
	}
	public abstract String getDescription();
}
