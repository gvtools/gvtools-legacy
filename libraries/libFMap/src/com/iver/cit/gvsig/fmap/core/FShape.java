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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

import org.opengis.referencing.operation.MathTransform;


/**
 * <p><code>FShape</code> extends <code>Shape</code> adding shape types, and allowing
 *  to work with it as a geometry.</p>
 */
public interface FShape extends Shape, Serializable {
	/**
	 * <p>Unknown or not defined type.</p>
	 */
	public final static int NULL = 0;

	/**
	 * <p>A geometric element that has zero dimensions and a location determinable by an ordered set
	 *  of coordinates.</p>
	 */
	public final static int POINT = 1;

	/**
	 * <p>A straight or curved geometric element that is generated by a moving point and that has extension
	 *  only along the path of the point.</p>
	 */
	public final static int LINE = 2;

	/**
	 * <p>A closed plane figure bounded by straight lines.</p>
	 */
	public final static int POLYGON = 4;

	/**
	 * <p>Words, symbols and form of a written or printed work.</p>
	 */
	public final static int TEXT = 8;

	/**
	 * <p>A set that can contain points, lines and polygons. This is usual in <i>CAD</i> layers <i>(dxf, dgn, dwg)</i>.</p>
	 */
	public final static int MULTI = 16;

	/**
	 * <p>A set of points.</p>
	 */
	public final static int MULTIPOINT = 32;

	/**
	 * <p>A closed plane curve every point of which is equidistant from a fixed point within the curve.</p>
	 */
	public final static int CIRCLE = 64;

	/**
	 * <p>A continuous portion (as of a circle or ellipse) of a curved line.</p>
	 */
	public final static int ARC = 128;

	/**
	 *  <p>A closed plane curve generated by a point moving in such a way that the sums of its distances
	 *   from two fixed points is a constant : a plane section of a right circular cone that is a closed
	 *   curve.</p>
	 */
	public final static int ELLIPSE=256;

	/**
	 * <p>Indicates third coordinate. And can be combined with other geometries via the bits enabled.</p>
	 */
	public final static int Z=512;
	
	/**
	 * <p>Indicates M coordinate. And can be combined with other geometries via the bits enabled.</p>
	 */	
	public final static int M=1024;

	/**
	 * <p>Gets the geometry type that identifies this shape.</p>
	 *
	 * @return int the geometry type that identifies this shape.
	 */
	public int getShapeType();

	/**
	 * <p>Creates and returns a shape equal and independent of this one.</p>
	 *
	 * @return the new shape.
	 */
	public FShape cloneFShape();

	/**
	 * <p>Re-projects this shape using <code>ct</code> as transformation coordinates.</p>
	 *
	 * @param crsTransform the transformation coordinates
	 */
	public void reProject(MathTransform crsTransform);

	/**
	 * <p>Returns the handlers they utilized to stretch the geometries.</p>
	 *
	 * @return Handlers the handlers used to stretch the geometries
	 */
	public Handler[] getStretchingHandlers();

	/**
	 * <p>Returns the handlers used to select the geometries.</p>
	 *
	 * @return Handlers the handlers used to select the geometries
	 */
	public Handler[] getSelectHandlers();
	
	/**
	 * <p>Executes a 2D transformation on this shape, using six parameters.</p>
	 * 
	 * @param at object that allows execute the affine transformation
	 * 
	 * @see AffineTransform
	 */
	public void transform(AffineTransform at);
}
