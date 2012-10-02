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
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.cresques.cts.ProjectionUtils;
import org.opengis.referencing.operation.MathTransform;

import com.iver.utiles.XMLEntity;


/**
 * Punto 2D.
 *
 * @author Vicente Caballero Navarro
 */
public class FPoint2D implements FShape {
	protected Point2D p;

	/**
	 * Crea un nuevo Point2D.
	 *
	 * @param x Coordenada x del punto.
	 * @param y Coordenada y del punto.
	 */
	public FPoint2D(double x, double y) {
		p = new Point2D.Double(x, y);
	}
	public FPoint2D(){

	}
    public FPoint2D(Point2D p) {
        this.p = p;
    }

	private void setPoint(double x, double y){
		p = new Point2D.Double(x, y);
	}
	/**
	 * Aplica la transformación de la matriz de transformación que se pasa como
	 * parámetro.
	 *
	 * @param at Matriz de transformación.
	 */
	public void transform(AffineTransform at) {
		at.transform(p, p);
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#contains(double, double)
	 */
	public boolean contains(double x, double y) {
		if ((x == p.getX()) || (y == p.getY())) {
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#contains(double, double, double, double)
	 */
	public boolean contains(double x, double y, double w, double h) {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#intersects(double, double, double, double)
	 */
	public boolean intersects(double x, double y, double w, double h) {
		Rectangle2D.Double rAux = new Rectangle2D.Double(x, y, w, h);

		return rAux.contains(p.getX(), p.getY());
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#getBounds()
	 */
	public Rectangle getBounds() {
		return new Rectangle((int) p.getX(), (int) p.getY(), 0, 0);
	}

	/**
	 * Devuelve la coordenada x del punto.
	 *
	 * @return Coordenada x.
	 */
	public double getX() {
		return p.getX();
	}

	/**
	 * Devuelve la coordenada y del punto.
	 *
	 * @return Coordenada y.
	 */
	public double getY() {
		return p.getY();
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#contains(java.awt.geom.Point2D)
	 */
	public boolean contains(Point2D p) {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#getBounds2D()
	 */
	public Rectangle2D getBounds2D() {
		return new Rectangle2D.Double(p.getX()- 0.01, p.getY() - 0.01, 0.02, 0.02);
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#contains(java.awt.geom.Rectangle2D)
	 */
	public boolean contains(Rectangle2D r) {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#intersects(java.awt.geom.Rectangle2D)
	 */
	public boolean intersects(Rectangle2D r) {
		return r.contains(this.p);
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform)
	 */
	public PathIterator getPathIterator(AffineTransform at) {
		return new FPointIterator(p, at);
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform, double)
	 */
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new FPointIterator(p, at);
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.core.FShape#getShapeType()
	 */
	public int getShapeType() {
		return FShape.POINT;
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShape#cloneFShape()
	 */
	public FShape cloneFShape() {
		return new FPoint2D(p.getX(), p.getY());
	}

	@Override
	public void reProject(MathTransform trans) {
		p = ProjectionUtils.transform(p, trans);
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.core.FShape#getXMLEntity()
	 */
	public XMLEntity getXMLEntity() {
		XMLEntity xml=new XMLEntity();
		xml.putProperty("x",p.getX());
		xml.putProperty("y",p.getY());
		return xml;
	}
	public void setXMLEntity(XMLEntity xml){
		this.setPoint(xml.getDoubleProperty("x"),xml.getDoubleProperty("y"));//p=new FPoint2D(xml.getDoubleProperty("x"),xml.getDoubleProperty("y"));
	}
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShape#getStretchingHandlers()
	 */
	public Handler[] getStretchingHandlers() {
		ArrayList handlers = new ArrayList();
		handlers.add(new PointHandler(0,p.getX(),p.getY()));
		return (Handler[]) handlers.toArray(new Handler[0]);
	}
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShape#getSelectHandlers()
	 */
	public Handler[] getSelectHandlers() {
		ArrayList handlers = new ArrayList();
		handlers.add(new PointHandler(0,p.getX(),p.getY()));
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
			p.setLocation(p.getX()+x,p.getY()+y);
		}

		/**
		 * @see com.iver.cit.gvsig.fmap.core.Handler#set(double, double)
		 */
		public void set(double x, double y) {
			p.setLocation(x, y);
		}
	}
}
