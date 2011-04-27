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

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.cresques.cts.ICoordTrans;

import com.iver.cit.gvsig.fmap.core.v02.FConverter;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class FPolyline2D implements FShape {
	protected GeneralPathX gp;
	/**
	 * Crea un nuevo FPolyline2D.
	 *
	 * @param gpx GeneralPathX.
	 */
	public FPolyline2D(GeneralPathX gpx) {
		gp = gpx;
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#contains(double, double)
	 */
	public boolean contains(double x, double y) {
		return gp.contains(x, y);
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#contains(double, double, double, double)
	 */
	public boolean contains(double x, double y, double w, double h) {
		return gp.contains(x, y, w, h);
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#intersects(double, double, double, double)
	 */
	public boolean intersects(double x, double y, double w, double h) {
	    // Más rápido
		return gp.intersects(x, y, w, h);
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#getBounds()
	 */
	public Rectangle getBounds() {
		return gp.getBounds();
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#contains(java.awt.geom.Point2D)
	 */
	public boolean contains(Point2D p) {
		return gp.contains(p);
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#getBounds2D()
	 */
	public Rectangle2D getBounds2D() {
		return gp.getBounds2D();
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#contains(java.awt.geom.Rectangle2D)
	 */
	public boolean contains(Rectangle2D r) {
		return gp.contains(r);
	}

	/**
	 * El método intersects de java.awt.Shape que define la intersección entre
	 * una polilínea y un Rectangle2D considera la polilínea como un Shape
	 * genérico y se producen errores en la selección de polilíneas. Por este
	 * motivo se ha modificado este método intersect() de FPolyline2D para que
	 * realize la intersección estricta entre el Rectangle2D y la polilínea en
	 * cuestión. El precio es un incremento de tiempo máximo del 50%.
	 *
	 * @param r Rectángulo.
	 *
	 * @return True si intersecta con el rectangulo que se pasa como parámetro.
	 */
	public boolean intersects(Rectangle2D r) {
		//return gp.intersects(r);
	    // Más exacto
		boolean bool = false;
		   if (gp.intersects(r)) {
		           ArrayList arrayCoords;
		           int theType;
		           //Use this array to store segment coordinate data
		           double[] theData = new double[6];
		           PathIterator theIterator;

		       Point2D p1 = new Point2D.Double(r.getMinX(),r.getMinY());
		       Point2D p2 = new Point2D.Double(r.getMinX(),r.getMaxY());
		       Point2D p3 = new Point2D.Double(r.getMaxX(),r.getMaxY());
		       Point2D p4 = new Point2D.Double(r.getMaxX(),r.getMinY());
		       Line2D l1 = new Line2D.Double(p1,p2);
		       Line2D l2 = new Line2D.Double(p2,p3);
		       Line2D l3 = new Line2D.Double(p3,p4);
		       Line2D l4 = new Line2D.Double(p4,p1);

		           theIterator = this.getPathIterator(null,FConverter.FLATNESS);
		           arrayCoords = new ArrayList();
		           while(!theIterator.isDone()) {
		                    theType = theIterator.currentSegment(theData);
		           if (theType==PathIterator.SEG_MOVETO) {
		                    arrayCoords.add(new Point2D.Double(theData[0], theData[1]));
		           } else if (theType==PathIterator.SEG_LINETO) {
		                   arrayCoords.add(new Point2D.Double(theData[0], theData[1]));
		                   Point2D pAnt = (Point2D)arrayCoords.get(arrayCoords.size()-2);
		                   Line2D l = new Line2D.Double(pAnt.getX(),pAnt.getY(),theData[0],theData[1]);
		                   if (l.intersectsLine(l1.getX1(),l1.getY1(),l1.getX2(),l1.getY2())
		                                   || l.intersectsLine(l2.getX1(),l2.getY1(),l2.getX2(),l2.getY2())
		                                   || l.intersectsLine(l3.getX1(),l3.getY1(),l3.getX2(),l3.getY2())
		                                   || l.intersectsLine(l4.getX1(),l4.getY1(),l4.getX2(),l4.getY2())
		                                   || r.intersectsLine(l)) {
		                           bool = true;
		                   }
		           } else if(theType==PathIterator.SEG_CLOSE){
		        	   Point2D firstPoint=(Point2D)arrayCoords.get(0);
		        	   Point2D pAnt = (Point2D)arrayCoords.get(arrayCoords.size()-1);
	                   Line2D l = new Line2D.Double(pAnt.getX(),pAnt.getY(),firstPoint.getX(),firstPoint.getY());
	                   if (l.intersectsLine(l1.getX1(),l1.getY1(),l1.getX2(),l1.getY2())
	                                   || l.intersectsLine(l2.getX1(),l2.getY1(),l2.getX2(),l2.getY2())
	                                   || l.intersectsLine(l3.getX1(),l3.getY1(),l3.getX2(),l3.getY2())
	                                   || l.intersectsLine(l4.getX1(),l4.getY1(),l4.getX2(),l4.getY2())
	                                   || r.intersectsLine(l)) {
	                           bool = true;
	                   }
		           }else {
		                    System.out.println("Not supported here");
		           }
		                theIterator.next();
		            }
		   }
		   return bool;
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform)
	 */
	public PathIterator getPathIterator(AffineTransform at) {
		return gp.getPathIterator(at);
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform, double)
	 */
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return gp.getPathIterator(at, flatness);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param at DOCUMENT ME!
	 */
	public void transform(AffineTransform at) {

        // TODO: PRUEBA. BORRAR ESTA LINEA
        // gp = FConverter.transformToInts(gp, at);

		gp.transform(at);
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.core.FShape#getShapeType()
	 */
	public int getShapeType() {
		return FShape.LINE;
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShape#cloneFShape()
	 */
	public FShape cloneFShape() {
		return new FPolyline2D((GeneralPathX) gp.clone());
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShape#reProject(org.cresques.cts.ICoordTrans)
	 */
	public void reProject(ICoordTrans ct) {
		gp.reProject(ct);
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShape#getStretchingHandlers()
	 */
	public Handler[] getStretchingHandlers() {
		ArrayList handlers = new ArrayList();
		GeneralPathXIterator gpi = null;
		gpi = (GeneralPathXIterator) getPathIterator(null);

		double[] theData = new double[6];
		int i=0;
		while (!gpi.isDone()) {
			int theType = gpi.currentSegment(theData);
			//g.fillRect((int)(theData[0]-3),(int)(theData[1]-3),6,6);
			handlers.add(new PointHandler(i,theData[0], theData[1]));
			i++;
			gpi.next();
		}

		return (Handler[]) handlers.toArray(new Handler[0]);
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShape#getSelectHandlers()
	 */
	public Handler[] getSelectHandlers() {
		ArrayList handlers = new ArrayList();
		GeneralPathXIterator gpi = null;
		gpi = (GeneralPathXIterator) getPathIterator(null);

		double[] theData = new double[6];
		int i=0;
		boolean isFirst=true;
		while (!gpi.isDone()) {
			int theType = gpi.currentSegment(theData);
			//g.fillRect((int)(theData[0]-3),(int)(theData[1]-3),6,6);
			/* if (!(this instanceof FPolygon2D && isFirst)){
				handlers.add(new PointSelHandler(i,theData[0], theData[1]));
				i++;
			}
			isFirst=false; */
			switch (theType)
			{
			case GeneralPathXIterator.SEG_MOVETO:
				handlers.add(new PointSelHandler(i++,theData[0], theData[1]));
				break;
			case GeneralPathXIterator.SEG_LINETO:
				handlers.add(new PointSelHandler(i++,theData[0], theData[1]));
				break;
			case GeneralPathXIterator.SEG_CLOSE:
				break;
			case GeneralPathXIterator.SEG_QUADTO:
				handlers.add(new PointSelHandler(i++,theData[0], theData[1]));
				handlers.add(new PointSelHandler(i++,theData[2], theData[3]));
				break;
			case GeneralPathXIterator.SEG_CUBICTO:
				handlers.add(new PointSelHandler(i++,theData[0], theData[1]));
				handlers.add(new PointSelHandler(i++,theData[2], theData[3]));
				handlers.add(new PointSelHandler(i++,theData[4], theData[5]));
				break;

			}
			gpi.next();


		}

		return (Handler[]) handlers.toArray(new Handler[0]);
	}
	/**
	 * DOCUMENT ME!
	 *
	 * @author Vicente Caballero Navarro
	 */
	class PointHandler extends AbstractHandler implements IFinalHandler{
		/**
		 * Crea un nuevo PointHandler.
		 *
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 */
		public PointHandler(int i,double x, double y) {
			point = new Point2D.Double(x, y);
			index=i;
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
			gp.pointCoords[index*2]+=x;
			gp.pointCoords[index*2+1]+=y;
		}

		/**
		 * @see com.iver.cit.gvsig.fmap.core.Handler#set(double, double)
		 */
		public void set(double x, double y) {
			gp.pointCoords[index*2]=x;
			gp.pointCoords[index*2+1]=y;
		}
	}
	/**
	 * DOCUMENT ME!
	 *
	 * @author Vicente Caballero Navarro
	 */
	class PointSelHandler extends AbstractHandler implements IFinalHandler{
		/**
		 * Crea un nuevo PointHandler.
		 *
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 */
		public PointSelHandler(int i,double x, double y) {
			point = new Point2D.Double(x, y);
			index=i;
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
			gp.pointCoords[index*2]+=x;
			gp.pointCoords[index*2+1]+=y;
		}

		/**
		 * @see com.iver.cit.gvsig.fmap.core.Handler#set(double, double)
		 */
		public void set(double x, double y) {
			gp.pointCoords[index*2]=x;
			gp.pointCoords[index*2+1]=y;
		}
	}
}
