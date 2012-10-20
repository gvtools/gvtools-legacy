package com.iver.cit.gvsig.project.documents.table.operators;

import java.util.ArrayList;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.ExpressionFieldExtension;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.project.documents.table.GraphicOperator;
import com.iver.cit.gvsig.project.documents.table.IOperator;
import com.iver.cit.gvsig.project.documents.table.Index;

/**
 * @author Vicente Caballero Navarro
 */
public class PointY extends GraphicOperator {

	public String addText(String s) {
		return s.concat(toString() + "()");
	}

	public double process(Index index) throws DriverIOException {
		ReadableVectorial adapter = getLayer().getSource();
		IGeometry geom = null;
		try {
			geom = adapter.getShape(index.get());
		} catch (ExpansionFileReadException e) {
			throw new DriverIOException(e);
		} catch (ReadDriverException e) {
			throw new DriverIOException(e);
		}
		ArrayList parts = getXY(geom);
		Double[][] xsys = (Double[][]) parts.get(0);// getXY(geom);
		return xsys[1][0].doubleValue();
	}

	public void eval(BSFManager interpreter) throws BSFException {
		interpreter.declareBean("pointY", this, PointY.class);
		// interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"double y(){return pointY.process(indexRow);};");
		interpreter.exec(ExpressionFieldExtension.JYTHON, null, -1, -1,
				"def y():\n" + "  return pointY.process(indexRow)");
	}

	public String toString() {
		return "y";
	}

	public boolean isEnable() {
		if (getLayer() == null)
			return false;
		ReadableVectorial adapter = getLayer().getSource();
		int type = FShape.POINT;
		try {
			type = adapter.getShapeType();
		} catch (ReadDriverException e) {
			NotificationManager.addError(e);
		}
		return (getType() == IOperator.NUMBER && type == FShape.POINT);
	}

	public String getTooltip() {
		return PluginServices.getText(this, "operator") + ":  " + addText("")
				+ "\n" + getDescription();
	}

	public String getDescription() {
		return PluginServices.getText(this, "returns") + ": "
				+ PluginServices.getText(this, "numeric_value") + "\n"
				+ PluginServices.getText(this, "description") + ": "
				+ "Returns the Y coordenate of point geometry of this row.";
	}
}
