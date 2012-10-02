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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.opengis.referencing.operation.MathTransform;

import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.v02.FLabel;
import com.iver.utiles.swing.threads.Cancellable;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.io.WKBWriter;


/**
 * Multipunto 2D.
 *
 * @author Vicente Caballero Navarro
 */
public class FMultiPoint2D extends AbstractGeometry {
	private static final WKBWriter writer = new WKBWriter();
	
	FGeometry[] points = null;

	/**
	 * Crea un nuevo MultiPoint2D.
	 *
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 */
	public FMultiPoint2D(double[] x, double[] y) {
		points = new FGeometry[x.length];
		for (int i=0;i<x.length;i++){
			points[i] = new FGeometry(new FPoint2D(x[i], y[i]));
		}

	}
	public FMultiPoint2D(FPoint2D[] points) {
		this.points=new FGeometry[points.length];
		for (int i=0;i<points.length;i++){
			this.points[i] = new FGeometry(points[i]);
		}
   }
	/**
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#draw(java.awt.Graphics2D,
	 * 		ViewPort, ISymbol)
	 */
	public void draw(Graphics2D g, ViewPort vp, ISymbol symbol) {
		int size = 2;
		int hw = 4;

		for (int i = 0; i < points.length; i++) {
			Point2D p=points[i].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();
			//Point2D.Double p = new Point2D.Double(p.getX(), p.getY());
			vp.getAffineTransform().transform(p, p);
			g.setColor(Color.red);
			g.fillOval((int) p.getX() - size, (int) p.getY() - size, hw, hw);
			g.setColor(Color.black);
			g.drawOval((int) p.getX() - size, (int) p.getY() - size, hw, hw);
		}
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#toJTSGeometry()
	 */
	public Geometry toJTSGeometry() {
        Coordinate[] theGeoms = new Coordinate[points.length];
        for (int i = 0; i < theGeoms.length; i++)
        {
        	Point2D p=points[i].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();

        	Coordinate c = new Coordinate(p.getX(), p.getY());
            theGeoms[i] = c;
        }
        MultiPoint geomCol = new GeometryFactory().createMultiPoint(theGeoms);


		return geomCol;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#createLabels(int, boolean)
	 */
	public FLabel[] createLabels(int position, boolean duplicates) {
        FLabel[] aux = new FLabel[getNumPoints()];
        for (int i=0; i < getNumPoints(); i++)
        {
        	Shape p=points[i].getInternalShape();

            aux[i] = FLabel.createFLabel((FShape)p);
        }

		return aux;
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#intersects(java.awt.geom.Rectangle2D)
	 */
	public boolean intersects(Rectangle2D r) {
		for (int i=0;i<getNumPoints();i++){
			Point2D p=points[i].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();
			if (r.contains(p.getX(),p.getY()))
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getBounds2D()
	 */
	public Rectangle2D getBounds2D() {
		Rectangle2D r=null;
		if (getNumPoints()>0){
			Point2D p=points[0].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();

			r=new Rectangle2D.Double(p.getX(),p.getY(),0.001,0.001);
		}
		for (int i=1;i<getNumPoints();i++){
			Point2D p=points[i].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();
			r.add(p.getX(),p.getY());
		}
		return r;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getGeometryType()
	 */
	public int getGeometryType() {
		return FShape.MULTIPOINT;
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#draw(java.awt.Graphics2D, com.iver.cit.gvsig.fmap.ViewPort, com.iver.cit.gvsig.fmap.core.v02.FSymbol)
	 */
	public void draw(Graphics2D g, ViewPort vp, ISymbol symbol, Cancellable cancel) {
		//int size = 2;
		//int hw = 4;

		for (int i = 0; i < getNumPoints(); i++) {
			Point2D p=points[i].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();

			vp.getAffineTransform().transform(p, p);
			symbol.draw(g, vp.getAffineTransform(), new FPoint2D(p.getX(),p.getY()), cancel);
			// FGraphicUtilities.DrawShape(g, vp.getAffineTransform(), new FPoint2D(p.getX(),p.getY()), symbol);

		/*	java.awt.geom.Point2D.Double p = new java.awt.geom.Point2D.Double(x[i],
					y[i]);
			vp.getAffineTransform().transform(p, p);
			g.setColor(Color.red);
			g.fillOval((int) p.x - size, (int) p.y - size, (int) hw, (int) hw);
			g.setColor(Color.black);
			g.drawOval((int) p.x - size, (int) p.y - size, (int) hw, (int) hw);
		*/
		}
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#cloneGeometry()
	 */
	public IGeometry cloneGeometry() {
		FPoint2D[] aux = new FPoint2D[getNumPoints()];
		for (int i=0; i < getNumPoints(); i++)
		{
			aux[i] = (FPoint2D) points[i].cloneGeometry().getInternalShape();
		}
		return new FMultiPoint2D(aux);
	}

	@Override
	public void reProject(MathTransform trans) {
		for (int i=0; i < getNumPoints(); i++)
		{
			points[i].reProject(trans);
		}
	}
	
	public int getNumPoints(){
		return points.length;
	}
	public FPoint2D getPoint(int i){
		return (FPoint2D)points[i].getInternalShape();
	}
	/**
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getPathIterator(AffineTransform)
	 */
	public PathIterator getPathIterator(AffineTransform at) {
		GeneralPathX gpx=new GeneralPathX();
		if (getNumPoints()>0){
			Point2D p=points[0].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();
			gpx.moveTo(p.getX(), p.getY());
		}
		for (int i=1;i<getNumPoints();i++){
			Point2D p=points[i].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();
			gpx.lineTo(p.getX(), p.getY());
		}
		return (GeneralPathXIterator)gpx.getPathIterator(null);
	}
    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.core.IGeometry#fastIntersects(double, double, double, double)
     */
    public boolean fastIntersects(double x, double y, double w, double h) {
		for (int i=0; i < getNumPoints(); i++)
		{
			if (points[i].intersects(x,y,w,h))
				return true;
		}
        return false;
    }
    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.core.IGeometry#drawInts(java.awt.Graphics2D, com.iver.cit.gvsig.fmap.ViewPort, com.iver.cit.gvsig.fmap.core.v02.FSymbol)
     */
    public void drawInts(Graphics2D g, ViewPort vp, ISymbol symbol, Cancellable cancel) {
//        draw(g,vp, symbol, cancel);
    	for (int i = 0; i < points.length; i++) {
			points[i].drawInts(g,vp,symbol,cancel);
		}
    }
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getHandlers(int)
	 */
	public Handler[] getHandlers(int type) {
		int numPoints=getNumPoints();
		Handler[] handlers=new Handler[numPoints];
		for (int i = 0; i < numPoints; i++){
			handlers[i]=points[i].getHandlers(type)[0];
		}
		return handlers;
	}
	/**
	 * DOCUMENT ME!
	 *
	 * @author Vicente Caballero Navarro
	 */
	class PointHandler extends AbstractHandler {
		/**
		 * Crea un nuevo PointHandler.
		 *
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 */
		public PointHandler(int i,FPoint2D p) {
			point = new Point2D.Double(p.getX(), p.getY());
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
			Point2D p=points[index].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();

			point.setLocation(p.getX()+x,
					p.getY()+y);
		}

		/**
		 * @see com.iver.cit.gvsig.fmap.core.Handler#set(double, double)
		 */
		public void set(double x, double y) {
			point.setLocation(x, y);
		}

	}
	public void transform(AffineTransform at) {
		for (int i=0; i < getNumPoints(); i++)
		{
			points[i].transform(at);
		}

	}
	public byte[] toWKB() throws IOException {
		return writer.write(toJTSGeometry());
	}
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		GeneralPathX gpx=new GeneralPathX();
		if (getNumPoints()>0){
			Point2D p=points[0].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();

			gpx.moveTo(p.getX(), p.getY());
		}
		for (int i=1;i<getNumPoints();i++){
			Point2D p=points[i].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();

			gpx.lineTo(p.getX(), p.getY());
		}
		return gpx.getPathIterator(at, flatness);

	}
	public boolean contains(double x, double y) {
		boolean bResul;
		for (int i=0; i < getNumPoints(); i++)
		{
			bResul = points[i].contains(x,y);
			if (bResul) return true;
		}
		return false;
	}
	public boolean contains(double x, double y, double w, double h) {
		return false;
	}
	public boolean intersects(double x, double y, double w, double h) {
		boolean bResul;
		for (int i=0; i < getNumPoints(); i++)
		{
			bResul = points[i].contains(x,y,w,h);
			if (bResul) return true;
		}
		return false;
	}
	public Rectangle getBounds() {
		Rectangle r=null;
		if (getNumPoints()>0){
			r= points[0].getBounds();
		}
		for (int i=1;i<getNumPoints();i++){
			Point2D p=points[i].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();
			r.add(p.getX(),p.getY());
		}
		return r;
	}
	public boolean contains(Point2D p) {
		boolean bResul;
		for (int i=0; i < getNumPoints(); i++)
		{
			bResul = points[i].contains(p);
			if (bResul) return true;
		}
		return false;

	}
	public boolean contains(Rectangle2D r) {
		boolean bResul;
		for (int i=0; i < getNumPoints(); i++)
		{
			bResul = points[i].contains(r);
			if (bResul) return true;
		}
		return false;

	}
	public Shape getInternalShape() {
		return this;
	}
	public void drawInts(Graphics2D graphics2D, ViewPort viewPort, double dpi,
			CartographicSupport cartographicSymbol, Cancellable cancel) {
		for (int i = 0; i < points.length; i++) {
			points[i].drawInts(graphics2D,viewPort,dpi,cartographicSymbol,cancel);
		}

	}
}
