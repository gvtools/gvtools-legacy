package com.iver.cit.gvsig.project.documents.table.operators;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.ExpressionFieldExtension;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeometryUtilities;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.project.documents.table.GraphicOperator;
import com.iver.cit.gvsig.project.documents.table.IOperator;
import com.iver.cit.gvsig.project.documents.table.Index;
/**
 * @author Vicente Caballero Navarro
 */
public class Perimeter extends GraphicOperator{

	public String addText(String s) {
		return s.concat(toString()+"()");
	}
	public double process(Index index) throws DriverIOException {
		ReadableVectorial adapter = getLayer().getSource();
	   	IGeometry geom=null;
		try {
			geom = adapter.getShape(index.get());
		} catch (ExpansionFileReadException e) {
			throw new DriverIOException(e);
		} catch (ReadDriverException e) {
			throw new DriverIOException(e);
		}
		return GeometryUtilities.getLength(getLayer().getMapContext().getViewPort(), geom);
	}
	public void eval(BSFManager interpreter) throws BSFException {
		interpreter.declareBean("jperimeter",this,Perimeter.class);
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"double perimeter(){return jperimeter.process(indexRow);};");
		interpreter.exec(ExpressionFieldExtension.JYTHON,null,-1,-1,"def perimeter():\n" +
				"  return jperimeter.process(indexRow)");
	}
	public String toString() {
		return "perimeter";
	}
	public boolean isEnable() {
		if (getLayer()==null)
			return false;
		ReadableVectorial adapter = getLayer().getSource();
		int type=FShape.POINT;
		try {
			type=adapter.getShapeType();
		} catch (ReadDriverException e) {
			NotificationManager.addError(e);
		}
		return (getType()==IOperator.NUMBER && (type==FShape.POLYGON || type==FShape.LINE));
	}
	public String getTooltip(){
		return PluginServices.getText(this,"operator")+":  "+addText("")+"\n"+getDescription();
	}
	public String getDescription() {
        return PluginServices.getText(this, "returns") + ": " +
        PluginServices.getText(this, "numeric_value") + "\n" +
        PluginServices.getText(this, "description") + ": " +
        "Returns the perimeter of polygon or line geometry  of this row.";
    }
}
