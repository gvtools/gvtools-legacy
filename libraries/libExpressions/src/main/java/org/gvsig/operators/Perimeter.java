package org.gvsig.operators;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.GraphicOperator;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.baseclasses.Index;
import org.gvsig.expresions.EvalOperatorsTask;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
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
		ArrayList parts=getXY(geom);
	   	double perimeter=0;
	   	for (int j=0;j<parts.size();j++){
	   	Double[][] xsys=(Double[][])parts.get(j);//getXY(geom);
	    double dist = 0;
        double distAll = 0;

        ViewPort vp = getLayer().getMapContext().getViewPort();
        for (int i = 0; i < (xsys[0].length - 1); i++) {
            dist = 0;

            Point2D p = new Point2D.Double(xsys[0][i].doubleValue(), xsys[1][i].doubleValue());//vp.toMapPoint(new Point(event.getXs()[i].intValue(), event.getYs()[i].intValue()));
            Point2D p2 = new Point2D.Double(xsys[0][i + 1].doubleValue(), xsys[1][i + 1].doubleValue());//vp.toMapPoint(new Point(event.getXs()[i + 1].intValue(), event.getYs()[i + 1].intValue()));
            dist = vp.distanceWorld(p,p2);
            //System.out.println("distancia parcial = "+dist);
            distAll += dist;
        }
        int distanceUnits=vp.getDistanceUnits();
		perimeter+= distAll/MapContext.CHANGEM[distanceUnits];
	   	}
	   	return perimeter;
	}
	public void eval(BSFManager interpreter) throws BSFException {
		interpreter.declareBean("jperimeter",this,Perimeter.class);
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"double perimeter(){return jperimeter.process(indexRow);};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def perimeter():\n" +
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
		return "operator"+":  "+addText("")+"\n"+getDescription();
	}
	public String getDescription() {
        return  "returns" + ": " +
         "numeric_value" + "\n" +
         "description" + ": " +
        "Returns the perimeter of polygon or line geometry  of this row.";
    }
}
