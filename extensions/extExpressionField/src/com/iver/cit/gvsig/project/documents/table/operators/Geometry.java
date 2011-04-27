package com.iver.cit.gvsig.project.documents.table.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.ExpressionFieldExtension;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.project.documents.table.GraphicOperator;
import com.iver.cit.gvsig.project.documents.table.Index;
/**
 * @author Vicente Caballero Navarro
 */
public class Geometry extends GraphicOperator{

	public String addText(String s) {
		return s.concat(toString()+"()");
	}
	public double process(Index index) throws DriverIOException {
		return 0;
	}
	public IGeometry getGeometry(Index index) throws ExpansionFileReadException, ReadDriverException {
		ReadableVectorial adapter = getLayer().getSource();
	   	IGeometry geom=adapter.getShape(index.get());
	   	return geom;
	}
	public void eval(BSFManager interpreter) throws BSFException {
		interpreter.declareBean("jgeometry",this,Geometry.class);
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"java.lang.Object geometry(){return geometry.getGeometry(indexRow);};");
		interpreter.exec(ExpressionFieldExtension.JYTHON,null,-1,-1,"def geometry():\n" +
				"  return jgeometry.getGeometry(indexRow)");
	}
	public String toString() {
		return "geometry";
	}
	public boolean isEnable() {
		return false;
	}
	public String getTooltip(){
		return PluginServices.getText(this,"operator")+":  "+addText("")+"\n"+getDescription();
	}
	public String getDescription() {
        return PluginServices.getText(this, "returns") + ": " +
        PluginServices.getText(this, "numeric_value") + "\n" +
        PluginServices.getText(this, "description") + ": " +
        "Returns the geometry of this row.";
    }
}
