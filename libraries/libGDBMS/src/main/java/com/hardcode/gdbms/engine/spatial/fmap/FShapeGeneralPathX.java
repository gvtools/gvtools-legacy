/*
 * Created on 31-ago-2005
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
package com.hardcode.gdbms.engine.spatial.fmap;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.opengis.referencing.operation.MathTransform;

import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.Handler;


public class FShapeGeneralPathX implements FShape{

	private GeneralPathX gpx;
	private int type;
	
	public FShapeGeneralPathX(GeneralPathX gpx, int type) {
		this.gpx = gpx;
		this.type = type;
	}
	
	/**
	 * @see com.iver.cit.gvsig.fmap.core.FShape#getShapeType()
	 */
	public int getShapeType() {
		return type;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.core.FShape#cloneFShape()
	 */
	public FShape cloneFShape() {
		throw new RuntimeException();
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.core.FShape#reProject(org.cresques.cts.ICoordTrans)
	 */
	public void reProject(MathTransform trans) {
		throw new RuntimeException();
	}

	public void append(PathIterator pi, boolean connect) {
		gpx.append(pi, connect);
	}
	public void append(Shape s, boolean connect) {
		gpx.append(s, connect);
	}
	public void closePath() {
		gpx.closePath();
	}
	public boolean contains(double x, double y) {
		return gpx.contains(x, y);
	}
	public boolean contains(double x, double y, double w, double h) {
		return gpx.contains(x, y, w, h);
	}
	public boolean contains(Point2D p) {
		return gpx.contains(p);
	}
	public boolean contains(Rectangle2D r) {
		return gpx.contains(r);
	}
	public Shape createTransformedShape(AffineTransform at) {
		return gpx.createTransformedShape(at);
	}
	public void curveTo(double x1, double y1, double x2, double y2, double x3,
			double y3) {
		gpx.curveTo(x1, y1, x2, y2, x3, y3);
	}
	public boolean equals(Object obj) {
		return gpx.equals(obj);
	}
	public void flip() {
		gpx.flip();
	}
	public Rectangle getBounds() {
		return gpx.getBounds();
	}
	public Rectangle2D getBounds2D() {
		return gpx.getBounds2D();
	}
	public Point2D getCurrentPoint() {
		return gpx.getCurrentPoint();
	}
	public PathIterator getPathIterator(AffineTransform at) {
		return gpx.getPathIterator(at);
	}
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return gpx.getPathIterator(at, flatness);
	}
	public int getWindingRule() {
		return gpx.getWindingRule();
	}
	public int hashCode() {
		return gpx.hashCode();
	}
	public boolean intersects(double x, double y, double w, double h) {
		return gpx.intersects(x, y, w, h);
	}
	public boolean intersects(Rectangle2D r) {
		return gpx.intersects(r);
	}
	public void lineTo(double x, double y) {
		gpx.lineTo(x, y);
	}
	public void moveTo(double x, double y) {
		gpx.moveTo(x, y);
	}
	public void quadTo(double x1, double y1, double x2, double y2) {
		gpx.quadTo(x1, y1, x2, y2);
	}
	public void reset() {
		gpx.reset();
	}
	public void setWindingRule(int rule) {
		gpx.setWindingRule(rule);
	}
	public String toString() {
		return gpx.toString();
	}
	public void transform(AffineTransform at) {
		gpx.transform(at);
	}

	public Handler[] getStretchingHandlers() {
		// TODO Auto-generated method stub
		return null;
	}

	public Handler[] getSelectHandlers() {
		// TODO Auto-generated method stub
		return null;
	}
}
