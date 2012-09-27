package com.iver.cit.gvsig.fmap.core;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.cresques.cts.ICoordTrans;
import org.cresques.cts.IProjection;
import org.gvsig.fmap.geometries.iso.aggregate.MultiGeometry;
import org.gvsig.fmap.geometries.iso.primitive.AbstractGeometryPrimitive;
import org.gvsig.fmap.geometries.iso.primitive.Box;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.WKBWriter;


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
/* CVS MESSAGES:
 *
 * $Id: FGeometryCollection.java,v 1.2 2008/03/25 08:47:41 cvs Exp $
 * $Log: FGeometryCollection.java,v $
 * Revision 1.2  2008/03/25 08:47:41  cvs
 * Visitors removed
 *
 * Revision 1.1  2008/03/12 08:46:20  cvs
 * *** empty log message ***
 *
 *
 */
/**
 * @author Jorge Piera Llodr� (jorge.piera@iver.es)
 */
public class FGeometryCollection extends AbstractGeometry implements MultiGeometry{
	private static final WKBWriter writer = new WKBWriter();
	
	protected IGeometry[] geometries = null;
		
	public FGeometryCollection(String id, IProjection projection, IGeometry[] geometries) {
		super(id, projection);	
		this.geometries = geometries;	
	}
	
	public FGeometryCollection(IProjection projection) {
		this(null, projection, null);
	}
	
	public FGeometryCollection(IProjection projection, IGeometry[] geometries) {
		this(null, projection, geometries);
	}
	
	public FGeometryCollection(IGeometry[] geometries) {
		this(null, null, geometries);
	}

	public FGeometryCollection(String id, IProjection projection) {
		this(id, projection, null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#cloneGeometry()
	 */
	public IGeometry cloneGeometry() {
		FGeometry[] aux = new FGeometry[getPrimitivesNumber()];
		for (int i=0; i < getPrimitivesNumber(); i++){
			aux[i] = (FGeometry) geometries[i].cloneGeometry().getInternalShape();
		}
		return new FGeometryCollection(id, projection, aux);
	}	

	/*
	 * (non-Javadoc)
	 * @see java.awt.Shape#contains(double, double)
	 */
	public boolean contains(double x, double y) {
		boolean bResul;
		for (int i=0; i < getPrimitivesNumber(); i++){
			bResul = geometries[i].contains(x,y);
			if (bResul) return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.Shape#contains(double, double, double, double)
	 */
	public boolean contains(double x, double y, double w, double h) {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.Shape#contains(java.awt.geom.Point2D)
	 */
	public boolean contains(Point2D p) {
		boolean bResul;
		for (int i=0; i < getPrimitivesNumber(); i++){
			bResul = geometries[i].contains(p);
			if (bResul) return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.Shape#contains(java.awt.geom.Rectangle2D)
	 */
	public boolean contains(Rectangle2D r) {
		boolean bResul;
		for (int i=0; i < getPrimitivesNumber(); i++){
			bResul = geometries[i].contains(r);
			if (bResul) return true;
		}
		return false;
	}	

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.geometries.iso.GM_Object#coordinateDimension()
	 */
	public int getCoordinateDimension() {
		return 2;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#fastIntersects(double, double, double, double)
	 */
	public boolean fastIntersects(double x, double y, double w, double h) {
		for (int i=0; i < getPrimitivesNumber(); i++){
			if (geometries[i].intersects(x,y,w,h))
				return true;
		}
        return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.Shape#getBounds()
	 */
	public Rectangle getBounds() {
		Rectangle r = null;
		if (getPrimitivesNumber()>0){
			r = geometries[0].getBounds();
		}
		for (int i=1 ; i<getPrimitivesNumber() ; i++){
			Rectangle r2 = geometries[i].getBounds();
			r.add(r2);
		}
		return r;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getBounds2D()
	 */
	public Rectangle2D getBounds2D() {
		return null;
	}	

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getGeometryType()
	 */
	public int getGeometryType() {
		return FShape.MULTI;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getHandlers(int)
	 */
	public Handler[] getHandlers(int type) {
		int numPrimitives = getPrimitivesNumber();
		Handler[] handlers = new Handler[numPrimitives];
		for (int i=0; i < numPrimitives; i++){
			handlers[i] = geometries[i].getHandlers(type)[0];
		}
		return handlers;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getInternalShape()
	 */
	public Shape getInternalShape() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getPathIterator(java.awt.geom.AffineTransform)
	 */
	public PathIterator getPathIterator(AffineTransform at) {
		GeneralPathX gpx=new GeneralPathX();
		if (getPrimitivesNumber()>0){
			Point2D p = geometries[0].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();
			gpx.moveTo(p.getX(), p.getY());
		}
		for (int i=1;i<getPrimitivesNumber();i++){
			Point2D p = geometries[i].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();
			gpx.lineTo(p.getX(), p.getY());
		}
		return (GeneralPathXIterator)gpx.getPathIterator(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getPathIterator(java.awt.geom.AffineTransform, double)
	 */
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		GeneralPathX gpx=new GeneralPathX();
		if (getPrimitivesNumber()>0){
			Point2D p=geometries[0].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();
			gpx.moveTo(p.getX(), p.getY());
		}
		for (int i=1;i<getPrimitivesNumber();i++){
			Point2D p=geometries[i].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();
			gpx.lineTo(p.getX(), p.getY());
		}
		return gpx.getPathIterator(at, flatness);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gvsig.geometries.iso.aggregate.GM_Aggregate#getPrimitiveAt(int)
	 */
	public AbstractGeometryPrimitive getPrimitiveAt(int i) {
		if (i<getPrimitivesNumber()){
			return (AbstractGeometryPrimitive)geometries[i];
		}
		return null;
	}	

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.geometries.iso.aggregate.GM_Aggregate#getPrimitivesNumber()
	 */
	public int getPrimitivesNumber() {
		if (geometries == null){
			return 0;
		}
		return geometries.length;
	}	
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.Shape#intersects(double, double, double, double)
	 */
	public boolean intersects(double x, double y, double w, double h) {
		boolean bResul;
		for (int i=0; i < getPrimitivesNumber(); i++){
			bResul = geometries[i].contains(x,y,w,h);
			if (bResul) return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#intersects(java.awt.geom.Rectangle2D)
	 */
	public boolean intersects(Rectangle2D r) {
		for (int i=0;i<getPrimitivesNumber();i++){
			Point2D p=geometries[i].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();
			if (r.contains(p.getX(),p.getY()))
				return true;
		}
		return false;
	}	

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#isSimple()
	 */
	public boolean isSimple() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#reProject(org.cresques.cts.ICoordTrans)
	 */
	public void reProject(ICoordTrans ct) {
		for (int i=0; i < getPrimitivesNumber(); i++){
			geometries[i].reProject(ct);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#toJTSGeometry()
	 */
	public Geometry toJTSGeometry() {
		Geometry[] geometriesAux = new LineString[geometries.length];
        for (int i = 0; i < geometriesAux.length; i++){
        	geometriesAux[i] = ((AbstractGeometry)geometries[i]).toJTSGeometry();
        }
        return new GeometryFactory().createGeometryCollection(geometriesAux);		
	}
	

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#toWKB()
	 */
	public byte[] toWKB() throws IOException {
		return writer.write(toJTSGeometry());		
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#transform(java.awt.geom.AffineTransform)
	 */
	public void transform(AffineTransform at) {
		for (int i=0; i < getPrimitivesNumber(); i++){
			geometries[i].transform(at);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.geometries.iso.GM_Object#getBoundary()
	 */
	public Box getBoundary() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @return the geometries
	 * @deprecated
	 */
	public IGeometry[] getGeometries(){
		return geometries;
	}	
}

