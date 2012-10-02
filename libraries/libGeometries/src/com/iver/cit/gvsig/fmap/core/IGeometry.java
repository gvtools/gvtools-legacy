package com.iver.cit.gvsig.fmap.core;


import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Serializable;

import org.gvsig.fmap.geometries.iso.AbstractGeometry;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Geometry;

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

/**
 * Interfaz de Geometr�a.
 *
 * @author $author$
 */
public interface IGeometry extends Shape, Serializable, AbstractGeometry{
	public static int BEST = 0;
	public static int N = 1;
	public static int NE = 2;
	public static int E = 3;
	public static int SE = 4;
	public static int S = 5;
	public static int SW = 6;
	public static int W = 7;
	public static int NW = 8;

	public static int SELECTHANDLER=0;
	public static int STRETCHINGHANDLER=1;
	
	/**
	 * Transforma esta Shape en un Geometry de JTS
	 *
	 * @return Geometr�a.
	 */
	Geometry toJTSGeometry();

	/**
	 * Obtiene el tipo de la geometr�a
	 *
	 * @return una de las constantes de FShape: POINT, LINE, POLIGON
	 */
	int getGeometryType();

	/**
	 * Clona la Geometr�a.
	 *
	 * @return Geometr�a clonada.
	 */
	IGeometry cloneGeometry();

	/**
	 * Devuelve true si la geometr�a intersecta con el rect�ngulo que se pasa
	 * como par�metro.
	 *
	 * @param r Rect�ngulo.
	 *
	 * @return True, si intersecta.
	 */
	boolean intersects(Rectangle2D r);
	/**
	 * Devuelve true si la geometr�a contiene al rect�ngulo que se pasa
	 * como par�metro.
	 *
	 * @param r Rect�ngulo.
	 *
	 * @return True, si intersecta.
	 */
	//boolean contains(IGeometry g);

	/**
	 * Se usa en las strategies de dibujo para comprobar de manera r�pida
	 * si intersecta con el rect�ngulo visible
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public boolean fastIntersects(double x, double y, double w, double h);

	/**
	 * Devuelve el Rect�ngulo que ocupa la geometr�a.
	 *
	 * @return Rect�ngulo.
	 */
	Rectangle2D getBounds2D();

	/**
	 * Reproyecta la geometr�a a partir del transformador de coordenadas.
	 *
	 * @param trans Coordinate Transformer.
	 */
	void reProject(MathTransform trans);

	/**
	 * Devuelve el GeneralPathXIterator con la informaci�n relativa a la geometr�a.
	 * @param at TODO
	 *
	 * @return PathIterator.
	 */
	PathIterator getPathIterator(AffineTransform at);

    public byte[] toWKB() throws IOException;
    /**
	 * It returns the handlers of the geomety,
	 * these they can be of two types is straightening and of seleccion.
	 *
	 * @param type Type of handlers
	 *
	 * @return Handlers.
	 */
	public Handler[] getHandlers(int type);

	public void transform(AffineTransform at);

	PathIterator getPathIterator(AffineTransform at, double flatness);

	/**
	 * Useful to have the real shape behind the scenes.
	 * May be uses to edit it knowing it it is a Circle, Ellipse, etc
	 * @return
	 */
	Shape getInternalShape();	
	
}
