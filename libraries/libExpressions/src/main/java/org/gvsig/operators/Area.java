package org.gvsig.operators;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.cresques.cts.IProjection;
import org.gvsig.baseclasses.GraphicOperator;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.baseclasses.Index;
import org.gvsig.expresions.EvalOperatorsTask;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.tools.geo.Geo;
/**
 * @author Vicente Caballero Navarro
 */
public class Area extends GraphicOperator{

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
	   	int distanceUnits=getLayer().getMapContext().getViewPort().getDistanceUnits();
		return returnArea(geom)/Math.pow(MapContext.CHANGEM[distanceUnits],2);
	}
	public void eval(BSFManager interpreter) throws BSFException {
		interpreter.declareBean("jarea",this,Area.class);
//		interpreter.eval(ExpressionFieldExtension.BEANSHELL,null,-1,-1,"double area(){return area.process(indexRow);};");
		interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def area():\n" +
				"  return jarea.process(indexRow)");
	}
	public String toString() {
		return "area";
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
		return (getType()==IOperator.NUMBER && type==FShape.POLYGON);
	}

	private double returnArea(IGeometry geom) {
		ArrayList parts=getXY(geom);
		double area=0;
		for (int i=0;i<parts.size();i++){
		Double[][] xsys=(Double[][])parts.get(i);//getXY(geom);
		Double[] xs=xsys[0];
		Double[] ys=xsys[1];
		IProjection proj=getLayer().getMapContext().getProjection();
		if (proj.isProjected()) {
			area+= returnCoordsArea(xs,ys,new Point2D.Double(xs[xs.length-1].doubleValue(),ys[ys.length-1].doubleValue()));
		}else{
			area+= returnGeoCArea(xs,ys);
		}
		}
		return area;
	}
	private double returnGeoCArea(Double[] xs,Double[] ys) {
		double[] lat=new double[xs.length];
		double[] lon=new double[xs.length];
		for (int K= 0; K < xs.length; K++){
			lon[K]= xs[K].doubleValue()/Geo.Degree;
			lat[K]= ys[K].doubleValue()/Geo.Degree;
		}
		return (Geo.sphericalPolyArea(lat,lon,xs.length-1)*Geo.SqM);
	}
	/**
	 * Calcula el área.
	 *
	 * @param aux último punto.
	 *
	 * @return Área.
	 */
	public double returnCoordsArea(Double[] xs,Double[] ys, Point2D point) {
		Point2D aux=point;
		double elArea = 0.0;
		Point2D pPixel;
		Point2D p = new Point2D.Double();
		Point2D.Double pAnt = new Point2D.Double();
		ViewPort vp = getLayer().getMapContext().getViewPort();
		for (int pos = 0; pos < xs.length-1; pos++) {
			pPixel = new Point2D.Double(xs[pos].doubleValue(),
					ys[pos].doubleValue());
			p = pPixel;
			if (pos == 0) {
				pAnt.x = aux.getX();
				pAnt.y = aux.getY();
			}
			elArea = elArea + ((pAnt.x - p.getX()) * (pAnt.y + p.getY()));
			pAnt.setLocation(p);
		}

		elArea = elArea + ((pAnt.x - aux.getX()) * (pAnt.y + aux.getY()));
		elArea = Math.abs(elArea / 2.0);
		return (elArea*(Math.pow(MapContext.CHANGEM[vp.getMapUnits()],2)));
	}
	public String getTooltip(){
		return PluginServices.getText(this,"operator")+":  "+addText("")+"\n"+getDescription();
	}
	public String getDescription() {
        return PluginServices.getText(this, "returns") + ": " +
        PluginServices.getText(this, "numeric_value") + "\n" +
        PluginServices.getText(this, "description") + ": " +
        "Returns the area of polygon geometry of this row.";
    }
}
