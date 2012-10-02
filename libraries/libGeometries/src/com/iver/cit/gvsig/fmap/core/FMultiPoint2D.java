/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.gvsig.fmap.geometries.iso.aggregate.MultiPoint;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKBWriter;


/**
 * Multipunto 2D.
 *
 * @author Vicente Caballero Navarro
 */
public class FMultiPoint2D extends FGeometryCollection implements MultiPoint{
	private static final WKBWriter writer = new WKBWriter();
	
	public FMultiPoint2D(CoordinateReferenceSystem crs, FPoint2D[] points) {
		this(null, crs, points);		
	}
	
	public FMultiPoint2D(FPoint2D[] points) {
		this(null, null, points);		
	}

	public FMultiPoint2D(CoordinateReferenceSystem projection) {
		this(null, projection, null);		
	}

	public FMultiPoint2D(String id, CoordinateReferenceSystem crs,
			FPoint2D[] points) {
		super(id, crs, points);
	}

	public FMultiPoint2D(String id, CoordinateReferenceSystem crs) {
		this(id, crs, null);
	}

	public FMultiPoint2D(double[] x, double[] y) {
		this(null, null, x, y);		
	}
	
	/**
	 * Crea un nuevo MultiPoint2D.
	 *
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 */
	public FMultiPoint2D(String id, CoordinateReferenceSystem crs, double[] x,
			double[] y) {
		super(id, crs);
		geometries = new FPoint2D[x.length];
		for (int i=0;i<x.length;i++){
			geometries[i] = new FPoint2D(id, crs, x[i], y[i]);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#cloneGeometry()
	 */
	public IGeometry cloneGeometry() {
		FPoint2D[] aux = new FPoint2D[getNumgeometries()];
		for (int i=0; i < getNumgeometries(); i++){
			aux[i] = (FPoint2D) geometries[i].cloneGeometry().getInternalShape();
		}
		return new FMultiPoint2D(id, crs, aux);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FGeometryCollection#getBounds()
	 */
	public Rectangle getBounds() {
		Rectangle r=null;
		if (getNumgeometries()>0){
			r = geometries[0].getBounds();
		}
		for (int i=1;i<getNumgeometries();i++){
			Point2D p=geometries[i].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();
			r.add(p.getX(),p.getY());
		}
		return r;
	}
	
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getBounds2D()
	 */
	public Rectangle2D getBounds2D() {
		Rectangle2D r=null;
		if (getNumgeometries()>0){
			Point2D p=geometries[0].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();

			r=new Rectangle2D.Double(p.getX(),p.getY(),0.001,0.001);
		}
		for (int i=1;i<getNumgeometries();i++){
			Point2D p=geometries[i].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();
			r.add(p.getX(),p.getY());
		}
		return r;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getGeometryType()
	 */
	public int getGeometryType() {
		return FShape.MULTIPOINT;
	}	

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FGeometryCollection#toJTSGeometry()
	 */
	public Geometry toJTSGeometry() {
		Coordinate[] theGeoms = new Coordinate[geometries.length];
		for (int i = 0; i < theGeoms.length; i++){
			Point2D p=geometries[i].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();
			Coordinate c = new Coordinate(p.getX(), p.getY());
			theGeoms[i] = c;
		}
		com.vividsolutions.jts.geom.MultiPoint geomCol = new GeometryFactory().createMultiPoint(theGeoms);
		return geomCol;
	}	

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShape#getShapeType()
	 */
	public int getShapeType() {
		return FShape.MULTIPOINT;
	}



	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShape#cloneFShape()
	 */	
	public FShape cloneFShape() {
		FPoint2D[] aux = new FPoint2D[getNumgeometries()];
		for (int i=0; i < getNumgeometries(); i++)
		{
			aux[i] = (FPoint2D) geometries[i].cloneGeometry().getInternalShape();
		}
		return (FShape)new FMultiPoint2D(id, crs, aux);
	}

	/**
	 * @return the numbre of points
	 * @deprecated use getPrimitivesNumber
	 */
	public int getNumgeometries(){
		return getPrimitivesNumber();
	}

	/**
	 * @return the numbre of points
	 * @deprecated use getPrimitivesNumber
	 */
	public int getNumPoints(){
		return getPrimitivesNumber();
	}	

	public FPoint2D getPoint(int i){
		return (FPoint2D)geometries[i].getInternalShape();
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
			Point2D p=geometries[index].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();

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
		for (int i=0; i < getNumgeometries(); i++)
		{
			geometries[i].transform(at);
		}

	}

	public byte[] toWKB() throws IOException {
		return writer.write(toJTSGeometry());
	}
}
