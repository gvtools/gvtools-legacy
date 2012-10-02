/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
*
* Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
*
* For more information, contact:
*
*  Generalitat Valenciana
*   Conselleria d'Infraestructures i Transport
*   Av. Blasco Ibáñez, 50
*   46010 VALENCIA
*   SPAIN
*
*      +34 963862235
*   gvsig@gva.es
*      www.gvsig.gva.es
*
*    or
*
*   IVER T.I. S.A
*   Salamanca 50
*   46005 Valencia
*   Spain
*
*   +34 963163400
*   dac@iver.es
*/
package com.iver.cit.gvsig.fmap.core;

import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.gvsig.fmap.geometries.iso.primitive.Surface;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.cit.gvsig.fmap.edition.UtilFunctions;


/**
* DOCUMENT ME!
*
* @author Vicente Caballero Navarro
*/
public class FEllipse2D extends FPolygon2D implements Surface {
	private Point2D init;
	private Point2D end;
	private double ydist;
	/**
	 * DOCUMENT ME!
	 *
	 * @param gpx
	 */
	public FEllipse2D(String id, CoordinateReferenceSystem crs,
			GeneralPathX gpx, Point2D i, Point2D e, double d) {
		super(id, crs, gpx);
		init=i;
		end=e;
		ydist=d;
	}

	public Point2D getInit(){
		return init;
	}
	public Point2D getEnd(){
		return end;
	}
	public double getDist(){
		return ydist;
	}
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShape#cloneFShape()
	 */
	public FShape cloneFShape() {
		return new FEllipse2D(id, crs, (GeneralPathX) gp.clone(), init, end,
				ydist);
	}
	/**
	 * @see com.iver.cit.gvsig.fmap.core.FShape#getShapeType()
	 */
	public int getShapeType() {
		return FShape.ELLIPSE;
	}
	/**
	 * DOCUMENT ME!
	 *
	 * @param at DOCUMENT ME!
	 */
	public void transform(AffineTransform at) {
		Point2D center=new Point2D.Double((init.getX() + end.getX()) / 2,
				(init.getY() + end.getY()) / 2);
		Point2D pdist=UtilFunctions.getPerpendicularPoint(init,end,center,ydist);
		Point2D aux1=new Point2D.Double();
		at.transform(init,aux1);
		init=aux1;
		Point2D aux2=new Point2D.Double();
		at.transform(end,aux2);
		end=aux2;

		center=new Point2D.Double((init.getX() + end.getX()) / 2,
				(init.getY() + end.getY()) / 2);

		Point2D aux3=new Point2D.Double();
		at.transform(pdist,aux3);
		ydist=center.distance(aux3);
		gp.transform(at);
	}
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Handler[] getStretchingHandlers() {
		ArrayList handlers = new ArrayList();
		Rectangle2D rect = this.getBounds2D();
		handlers.add(new CenterHandler(0, rect.getCenterX(), rect.getCenterY()));
		return (Handler[]) handlers.toArray(new Handler[0]);
	}
	public Handler[] getSelectHandlers() {
		ArrayList handlers = new ArrayList();
		Rectangle2D rect = this.getBounds2D();
		handlers.add(new CenterSelHandler(0, rect.getCenterX(), rect.getCenterY()));
		handlers.add(new InitSelHandler(1, init.getX(), init.getY()));
		handlers.add(new EndSelHandler(2, end.getX(), end.getY()));
		Point2D mediop = new Point2D.Double((end.getX() + init.getX()) / 2,
				(end.getY() + init.getY()) / 2);
		Point2D[] p=UtilFunctions.getPerpendicular(init,end,mediop);
		Point2D u=UtilFunctions.getPoint(mediop,p[1],ydist);
		Point2D d=UtilFunctions.getPoint(mediop,p[1],-ydist);

		handlers.add(new RadioSelYHandler(3, u.getX(), u.getY()));
		handlers.add(new RadioSelYHandler(4, d.getX(), d.getY()));

		return (Handler[]) handlers.toArray(new Handler[0]);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @author Vicente Caballero Navarro
	 */
	class CenterHandler extends AbstractHandler implements ICenterHandler {
		/**
		 * Crea un nuevo PointHandler.
		 *
		 * @param i DOCUMENT ME!
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 */
		public CenterHandler(int i, double x, double y) {
			point = new Point2D.Double(x, y);
			index = i;
		}

		/**
		 * DOCUMENT ME!
		 *
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 *
		 * @return DOCUMENT ME!
		 */
		public void move(double x, double y) {
			for (int i=0;i<gp.numCoords/2;i++){
				gp.pointCoords[i*2]+=x;
				gp.pointCoords[i*2+1]+=y;
			}
			init = new Point2D.Double(init.getX() + x, init.getY() + y);
			end = new Point2D.Double(end.getX() + x, end.getY() + y);
		}

		/**
		 * @see com.iver.cit.gvsig.fmap.core.Handler#set(double, double)
		 */
		public void set(double x, double y) {
		}
	}
	/**
	 * DOCUMENT ME!
	 *
	 * @author Vicente Caballero Navarro
	 */
	class CenterSelHandler extends AbstractHandler implements ICenterHandler{
		/**
		 * Crea un nuevo PointHandler.
		 *
		 * @param i DOCUMENT ME!
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 */
		public CenterSelHandler(int i, double x, double y) {
			point = new Point2D.Double(x, y);
			index = i;
		}

		/**
		 * DOCUMENT ME!
		 *
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 *
		 * @return DOCUMENT ME!
		 */
		public void move(double x, double y) {
			for (int i=0;i<gp.numCoords/2;i++){
				gp.pointCoords[i*2]+=x;
				gp.pointCoords[i*2+1]+=y;
			}
		}

		/**
		 * @see com.iver.cit.gvsig.fmap.core.Handler#set(double, double)
		 */
		public void set(double x, double y) {
			Point2D center=new Point2D.Double((init.getX() + end.getX()) / 2,
					(init.getY() + end.getY()) / 2);
			double dx=x-center.getX();
			double dy=y-center.getY();
			for (int i=0;i<gp.numCoords/2;i++){
				gp.pointCoords[i*2]+=dx;
				gp.pointCoords[i*2+1]+=dy;
			}
			init=new Point2D.Double(init.getX()+dx,init.getY()+dy);
			end=new Point2D.Double(end.getX()+dx,end.getY()+dy);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @author Vicente Caballero Navarro
	 */
	class InitSelHandler extends AbstractHandler implements ICuadrantHandler{
		/**
		 * Crea un nuevo PointHandler.
		 *
		 * @param i DOCUMENT ME!
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 */
		public InitSelHandler(int i, double x, double y) {
			point = new Point2D.Double(x, y);
			index = i;
		}

		/**
		 * DOCUMENT ME!
		 *
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 *
		 * @return DOCUMENT ME!
		 */
		public void move(double x, double y) {

		}

		/**
		 * @see com.iver.cit.gvsig.fmap.core.Handler#set(double, double)
		 */
		public void set(double x, double y) {
			double dx=x-init.getX();
			double dy=y-init.getY();
			Point2D center=new Point2D.Double((init.getX() + end.getX()) / 2,
					(init.getY() + end.getY()) / 2);
//			Point2D[] p1=TrigonometricalFunctions.getPerpendicular(init,end,center);
//			Point2D[] p2=TrigonometricalFunctions.getPerpendicular(p1[0],p1[1],new Point2D.Double(x,y));

//			Point2D pl=TrigonometricalFunctions.getIntersection(p2[0],p2[1],p1[0],p1[1]);
//			double xdist=2*pl.distance(x,y);
			double xdist=2*center.distance(x,y);
			//init=new Point2D.Double(init.getX()+dx,init.getY()+dy);
			init=UtilFunctions.getPoint(center,init,center.distance(x,y));
			end=UtilFunctions.getPoint(init,center,xdist);
			Arc2D.Double arc = new Arc2D.Double(init.getX(),
					init.getY() - ydist, xdist, 2 * ydist, 0, 360, Arc2D.OPEN);
			Point2D rotationPoint = new Point2D.Double(init.getX() + xdist /2, init.getY());

			double angle = UtilFunctions.getAngle(init, end);
			AffineTransform mT = AffineTransform.getRotateInstance(angle, init.getX(), init.getY());
			gp = new GeneralPathX(arc);
			gp.transform(mT);

		}
	}
	/**
	 * DOCUMENT ME!
	 *
	 * @author Vicente Caballero Navarro
	 */
	class EndSelHandler extends AbstractHandler implements ICuadrantHandler{
		/**
		 * Crea un nuevo PointHandler.
		 *
		 * @param i DOCUMENT ME!
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 */
		public EndSelHandler(int i, double x, double y) {
			point = new Point2D.Double(x, y);
			index = i;
		}

		/**
		 * DOCUMENT ME!
		 *
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 *
		 * @return DOCUMENT ME!
		 */
		public void move(double x, double y) {

		}

		/**
		 * @see com.iver.cit.gvsig.fmap.core.Handler#set(double, double)
		 */
		public void set(double x, double y) {
			double dx=x-getPoint().getX();
			double dy=y-getPoint().getY();
			Point2D center=new Point2D.Double((init.getX() + end.getX()) / 2,
					(init.getY() + end.getY()) / 2);
//			Point2D[] p1=TrigonometricalFunctions.getPerpendicular(init,end,center);
//			Point2D[] p2=TrigonometricalFunctions.getPerpendicular(p1[0],p1[1],new Point2D.Double(x,y));

//			Point2D pl=TrigonometricalFunctions.getIntersection(p2[0],p2[1],p1[0],p1[1]);
//			double xdist=2*pl.distance(x,y);
			double xdist=2*center.distance(x,y);
			end=UtilFunctions.getPoint(center,end,center.distance(x,y));
			//end=new Point2D.Double(end.getX()+dx,end.getY()+dy);
			init=UtilFunctions.getPoint(end,center,xdist);
			Arc2D.Double arc = new Arc2D.Double(init.getX(),
					init.getY() - ydist, xdist, 2 * ydist, 0, 360, Arc2D.OPEN);
			Point2D rotationPoint = new Point2D.Double(init.getX() + xdist /2, init.getY());

			double angle = UtilFunctions.getAngle(init, end);
			AffineTransform mT = AffineTransform.getRotateInstance(angle, init.getX(), init.getY());
			gp = new GeneralPathX(arc);
			gp.transform(mT);

		}
	}
	/**
	 * DOCUMENT ME!
	 *
	 * @author Vicente Caballero Navarro
	 */
	class RadioSelYHandler extends AbstractHandler implements ICuadrantHandler{
		/**
		 * Crea un nuevo PointHandler.
		 *
		 * @param i DOCUMENT ME!
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 */
		public RadioSelYHandler(int i, double x, double y) {
			point = new Point2D.Double(x, y);
			index = i;
		}

		/**
		 * DOCUMENT ME!
		 *
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 *
		 * @return DOCUMENT ME!
		 */
		public void move(double x, double y) {

		}

		/**
		 * @see com.iver.cit.gvsig.fmap.core.Handler#set(double, double)
		 */
		public void set(double x, double y) {
			ydist=new Point2D.Double((init.getX() + end.getX()) / 2,
					(init.getY() + end.getY()) / 2).distance(x,y);
			//ydist=getSelectHandlers()[1].getPoint().distance(x,y);
//			Point2D center=new Point2D.Double((init.getX() + end.getX()) / 2,
//					(init.getY() + end.getY()) / 2);
//			Point2D[] p=TrigonometricalFunctions.getPerpendicular(init,end,new Point2D.Double(x,y));
//			Point2D pl=TrigonometricalFunctions.getIntersection(p[0],p[1],init,end);
//			double xdist=2*pl.distance(x,y);
			double xdist = init.distance(end);
			Arc2D.Double arc = new Arc2D.Double(init.getX(),
					init.getY() - ydist, xdist, 2 * ydist, 0, 360, Arc2D.OPEN);
			Point2D rotationPoint = new Point2D.Double(init.getX() + xdist /2, init.getY());

			double angle = UtilFunctions.getAngle(init, end);
			AffineTransform mT = AffineTransform.getRotateInstance(angle, init.getX(), init.getY());
			gp = new GeneralPathX(arc);
			gp.transform(mT);
		}
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FPolyline2D#intersects(java.awt.geom.Rectangle2D)
	 */
	public boolean intersects(Rectangle2D r) {
		return gp.intersects(r);
	}

}