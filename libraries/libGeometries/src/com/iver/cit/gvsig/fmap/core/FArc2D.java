/*
 * Created on 09-feb-2005
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
   USA.
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

import org.cresques.cts.IProjection;

import com.iver.cit.gvsig.fmap.edition.UtilFunctions;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class FArc2D extends FPolyline2D {
	private Point2D init;
	private Point2D center;
	private Point2D end;
	/**
	 * DOCUMENT ME!
	 *
	 * @param gpx
	 */
	public FArc2D(String id, IProjection projection, GeneralPathX gpx,Point2D i,Point2D c, Point2D e) {
		super(id, projection, gpx);
		init=i;
		center=c;
		end=e;
	}
	public Point2D getInit(){
		return init;
	}
	public Point2D getEnd(){
		return end;
	}
	public Point2D getCenter(){
		return UtilFunctions.getCenter(init, center,end);
	}
	public Point2D getMid(){
		return center;
	}
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShape#cloneFShape()
	 */
	public FShape cloneFShape() {
		FArc2D arc=new FArc2D(id, projection, (GeneralPathX) gp.clone(),init,center,end);
		return arc;
	}
	public void transform(AffineTransform at) {
		gp.transform(at);
		InitHandler inithandler=(InitHandler)getStretchingHandlers()[0];
		//CenterHandler centerhandler=(CenterHandler)getHandlers()[1];
		EndHandler endhandler=(EndHandler)getStretchingHandlers()[1];
		Point2D aux1=new Point2D.Double();
		Point2D aux2=new Point2D.Double();
		Point2D aux3=new Point2D.Double();
		at.transform(inithandler.getPoint(),aux1);
		inithandler.setPoint(aux1);
		//at.transform(centerhandler.getPoint(),aux2);
		//centerhandler.setPoint(aux2);
		at.transform(endhandler.getPoint(),aux3);
		endhandler.setPoint(aux3);
		CenterSelHandler centerhandler=(CenterSelHandler)getSelectHandlers()[1];
		at.transform(centerhandler.getPoint(),aux2);
		centerhandler.setPoint(aux2);

	}
	/**
	 * @see com.iver.cit.gvsig.fmap.core.FShape#getShapeType()
	 */
	public int getShapeType() {
		return FShape.ARC;
	}
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Handler[] getStretchingHandlers() {
		ArrayList handlers = new ArrayList();

		handlers.add(new InitHandler(0, init.getX(), init.getY()));
		//handlers.add(new CenterHandler(1, center.getX(), center.getY()));
		handlers.add(new EndHandler(1, end.getX(), end.getY()));

		return (Handler[]) handlers.toArray(new Handler[0]);
	}

	public Handler[] getSelectHandlers() {
		ArrayList handlers = new ArrayList();

		handlers.add(new InitSelHandler(0, init.getX(), init.getY()));
		handlers.add(new CenterSelHandler(1, center.getX(), center.getY()));
		handlers.add(new EndSelHandler(2, end.getX(), end.getY()));

		return (Handler[]) handlers.toArray(new Handler[0]);
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
			center = new Point2D.Double(x, y);
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
		public void setPoint(Point2D p){
			center=p;
		}
		public Point2D getPoint(){
			return center;
		}

		/**
		 * @see com.iver.cit.gvsig.fmap.core.Handler#set(double, double)
		 */
		public void set(double x, double y) {
			center=new Point2D.Double(x,y);
			Arc2D arco = UtilFunctions.createArc(init,center, end);
			gp=new GeneralPathX(arco);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @author Vicente Caballero Navarro
	 */
	class InitHandler extends AbstractHandler implements IFinalHandler{
		/**
		 * Crea un nuevo PointHandler.
		 *
		 * @param i DOCUMENT ME!
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 */
		public InitHandler(int i, double x, double y) {
			init = new Point2D.Double(x, y);
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
			Point2D mediop=new Point2D.Double((init.getX()+end.getX())/2,(init.getY()+end.getY())/2);
			double dist=mediop.distance(center);
			init=new Point2D.Double(init.getX()+x,init.getY()+y);

			mediop=new Point2D.Double((init.getX()+end.getX())/2,(init.getY()+end.getY())/2);
			Point2D[] perp=UtilFunctions.getPerpendicular(init,end,mediop);
			if (UtilFunctions.getAngle(end,init)<=Math.PI){
				dist=-dist;
			}
			center=UtilFunctions.getPoint(mediop,perp[1],dist);

			Arc2D arco = UtilFunctions.createArc(init,center, end);
			gp=new GeneralPathX(arco);
		}
		public void setPoint(Point2D p){
			init=p;
		}
		public Point2D getPoint(){
			return init;
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
	class EndHandler extends AbstractHandler implements IFinalHandler{
		/**
		 * Crea un nuevo PointHandler.
		 *
		 * @param i DOCUMENT ME!
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 */
		public EndHandler(int i, double x, double y) {
			end = new Point2D.Double(x, y);
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
			Point2D mediop=new Point2D.Double((init.getX()+end.getX())/2,(init.getY()+end.getY())/2);
			double dist=mediop.distance(center);
			end=new Point2D.Double(end.getX()+x,end.getY()+y);

			mediop=new Point2D.Double((init.getX()+end.getX())/2,(init.getY()+end.getY())/2);
			Point2D[] perp=UtilFunctions.getPerpendicular(init,end,mediop);
			if (UtilFunctions.getAngle(end,init)<=Math.PI){
				dist=-dist;
			}
			center=UtilFunctions.getPoint(mediop,perp[1],dist);

			Arc2D arco = UtilFunctions.createArc(init,center, end);
			gp=new GeneralPathX(arco);
		}
		public void setPoint(Point2D p){
			end=p;
		}
		public Point2D getPoint(){
			return end;
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
	class InitSelHandler extends AbstractHandler implements IFinalHandler{
		/**
		 * Crea un nuevo PointHandler.
		 *
		 * @param i DOCUMENT ME!
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 */
		public InitSelHandler(int i, double x, double y) {
			init = new Point2D.Double(x, y);
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
			Point2D mediop=new Point2D.Double((init.getX()+end.getX())/2,(init.getY()+end.getY())/2);
			double dist=mediop.distance(center);
			init=new Point2D.Double(init.getX()+x,init.getY()+y);

			mediop=new Point2D.Double((init.getX()+end.getX())/2,(init.getY()+end.getY())/2);
			Point2D[] perp=UtilFunctions.getPerpendicular(init,end,mediop);
			if (UtilFunctions.getAngle(end,init)<=Math.PI){
				dist=-dist;
			}
			center=UtilFunctions.getPoint(mediop,perp[1],dist);

			Arc2D arco = UtilFunctions.createArc(init,center, end);
			gp=new GeneralPathX(arco);
		}
		public void setPoint(Point2D p){
			init=p;
		}
		public Point2D getPoint(){
			return init;
		}

		/**
		 * @see com.iver.cit.gvsig.fmap.core.Handler#set(double, double)
		 */
		public void set(double x, double y) {
			Point2D mediop=new Point2D.Double((init.getX()+end.getX())/2,(init.getY()+end.getY())/2);
			double dist=mediop.distance(center);
			init=new Point2D.Double(x,y);

			mediop=new Point2D.Double((init.getX()+end.getX())/2,(init.getY()+end.getY())/2);
			Point2D[] perp=UtilFunctions.getPerpendicular(init,end,mediop);
			if (UtilFunctions.getAngle(end,init)<=Math.PI){
				dist=-dist;
			}
			///center=TrigonometricalFunctions.getPoint(mediop,perp[1],dist);
			Arc2D arco = UtilFunctions.createArc(init,center, end);
			gp=new GeneralPathX(arco);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @author Vicente Caballero Navarro
	 */
	class EndSelHandler extends AbstractHandler implements IFinalHandler{
		/**
		 * Crea un nuevo PointHandler.
		 *
		 * @param i DOCUMENT ME!
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 */
		public EndSelHandler(int i, double x, double y) {
			end = new Point2D.Double(x, y);
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
			Point2D mediop=new Point2D.Double((init.getX()+end.getX())/2,(init.getY()+end.getY())/2);
			double dist=mediop.distance(center);
			end=new Point2D.Double(end.getX()+x,end.getY()+y);

			mediop=new Point2D.Double((init.getX()+end.getX())/2,(init.getY()+end.getY())/2);
			Point2D[] perp=UtilFunctions.getPerpendicular(init,end,mediop);
			if (UtilFunctions.getAngle(end,init)<=Math.PI){
				dist=-dist;
			}
			center=UtilFunctions.getPoint(mediop,perp[1],dist);

			Arc2D arco = UtilFunctions.createArc(init,center, end);
			gp=new GeneralPathX(arco);
		}
		public void setPoint(Point2D p){
			end=p;
		}
		public Point2D getPoint(){
			return end;
		}

		/**
		 * @see com.iver.cit.gvsig.fmap.core.Handler#set(double, double)
		 */
		public void set(double x, double y) {
			Point2D mediop=new Point2D.Double((init.getX()+end.getX())/2,(init.getY()+end.getY())/2);
			double dist=mediop.distance(center);
			end=new Point2D.Double(x,y);

			mediop=new Point2D.Double((init.getX()+end.getX())/2,(init.getY()+end.getY())/2);
			Point2D[] perp=UtilFunctions.getPerpendicular(init,end,mediop);
			if (UtilFunctions.getAngle(end,init)<=Math.PI){
				dist=-dist;
			}
			///center=TrigonometricalFunctions.getPoint(mediop,perp[1],dist);
			Arc2D arco = UtilFunctions.createArc(init,center, end);
			gp=new GeneralPathX(arco);
		}
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FPolyline2D#intersects(java.awt.geom.Rectangle2D)
	 */
	public boolean intersects(Rectangle2D r) {
		return gp.intersects(r);
	}


}
