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

import com.iver.cit.gvsig.fmap.edition.UtilFunctions;



/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class FCircle2D extends FPolygon2D {
	private Point2D center;
	private double radio;


	/**
	 * DOCUMENT ME!
	 *
	 * @param gpx
	 */
	public FCircle2D(GeneralPathX gpx,Point2D c,double r) {
		super(gpx);
		center=c;
		radio=r;
	}
	public Point2D getCenter(){
		return center;
	}
	public double getRadio(){
		return radio;
	}
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShape#cloneFShape()
	 */
	public FShape cloneFShape() {
		return new FCircle2D((GeneralPathX) gp.clone(),center,radio);
	}
	/**
	 * @see com.iver.cit.gvsig.fmap.core.FShape#getShapeType()
	 */
	public int getShapeType() {
		return FShape.CIRCLE;
	}
	/**
	 * DOCUMENT ME!
	 *
	 * @param at DOCUMENT ME!
	 */
	public void transform(AffineTransform at) {
		Point2D pdist=UtilFunctions.getPerpendicularPoint(new Point2D.Double(center.getX()+10,center.getY()),new Point2D.Double(center.getX()-10,center.getY()),center,radio);
		Point2D aux=new Point2D.Double();
		at.transform(center,aux);
		center=aux;
		Point2D aux3=new Point2D.Double();
		at.transform(pdist,aux3);
		radio=center.distance(aux3);
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
		//handlers.add(new RadioHandler(1, rect.getX(), rect.getCenterY()));
		//handlers.add(new RadioHandler(2, rect.getMaxX(), rect.getCenterY()));
		//handlers.add(new RadioHandler(3, rect.getCenterX(), rect.getY()));
		//handlers.add(new RadioHandler(3, rect.getCenterX(), rect.getMaxY()));

		return (Handler[]) handlers.toArray(new Handler[0]);
	}
	public Handler[] getSelectHandlers() {
		ArrayList handlers = new ArrayList();
		handlers.add(new CenterSelHandler(0,center.getX(), center.getY()));
		handlers.add(new RadioSelHandler(1, center.getX()-radio, center.getY()));
		handlers.add(new RadioSelHandler(2, center.getX()+radio, center.getY()));
		handlers.add(new RadioSelHandler(3, center.getX(), center.getY()-radio));
		handlers.add(new RadioSelHandler(3, center.getX(), center.getY()+radio));

		return (Handler[]) handlers.toArray(new Handler[0]);
	}
	/**
	 * DOCUMENT ME!
	 *
	 * @author Vicente Caballero Navarro
	 */
	class CenterHandler extends AbstractHandler implements ICenterHandler{
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
			center=new Point2D.Double(center.getX()+x,center.getY()+y);
			for (int i=0;i<gp.numCoords/2;i++){
				gp.pointCoords[i*2]+=x;
				gp.pointCoords[i*2+1]+=y;
			}
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
			center=new Point2D.Double(x,y);
			Arc2D.Double arc = new Arc2D.Double(center.getX()-radio, center.getY() - radio,
					2 * radio, 2 * radio, 0, 360, Arc2D.OPEN);
			gp=new GeneralPathX(arc);

		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @author Vicente Caballero Navarro
	 */
	class RadioSelHandler extends AbstractHandler implements ICuadrantHandler{

		/**
		 * Crea un nuevo PointHandler.
		 *
		 * @param i DOCUMENT ME!
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 */
		public RadioSelHandler(int i, double x, double y) {
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
			radio = center.distance(x,y);
			Arc2D.Double arc = new Arc2D.Double(center.getX()-radio, center.getY() - radio,
					2 * radio, 2 * radio, 0, 360, Arc2D.OPEN);
			gp=new GeneralPathX(arc);
		}
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FPolyline2D#intersects(java.awt.geom.Rectangle2D)
	 */
	public boolean intersects(Rectangle2D r) {
		return gp.intersects(r);
	}

}
