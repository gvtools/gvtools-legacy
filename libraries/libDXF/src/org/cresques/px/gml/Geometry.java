/*
 * Cresques Mapping Suite. Graphic Library for constructing mapping applications.
 *
 * Copyright (C) 2004-5.
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
 * cresques@gmail.com
 */
package org.cresques.px.gml;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Vector;

import org.cresques.geo.Georeferenced;
import org.cresques.geo.ViewPortData;
import org.cresques.px.Extent;
import org.cresques.px.PxObj;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;


/**
 * Clase base para geometrías.
 * @author "Luis W. Sevilla" <sevilla_lui@gva.es>
 */
abstract public class Geometry extends PxObj implements Georeferenced {
    protected CoordinateReferenceSystem crs;

    //	protected Extent extent = null;
    Vector data = null;
    
    /**
     * Constructor de Geometry.
     */
    public Geometry() {
        extent = new Extent();
        data = new Vector();
    }
    
    /**
     * Permite añadir un punto a la Geometry.
     * @param pt
     */
    public void add(Point2D pt) {
        extent.add(pt);
        data.add(pt);
    }
    
    /**
     * Devuelve un punto de la Geometry dado por su índice.
     * @param i, Índice.
     * @return Point2D.
     */
    public Point2D get(int i) {
        return (Point2D) data.get(i);
    }
    
    /**
     * Devuelve el número de puntos que componen la Geometry.
     * @return int
     */
    public int pointNr() {
        return data.size();
    }
    
    /**
     * Devuelve el conjunto de objetos que conforman la Geometry en forma de Vector.
     * @return Vector de objetos.
     */
    public Vector getData() {
        return data;
    }
    
    /**
     * Devuelve el extent de la Geometry.
     * @return Extent, el rectángulo que contiene la Geometry.
     */
    public Extent getExtent() {
        return extent;
    }
    
    /**
     * Devuelve la proyección cartográfica en la que se encuentra la Geometry.
     * @return IProjection, la proyección cartográfica.
     */
    abstract public CoordinateReferenceSystem getCrs();
    
    /**
     * Permite reproyectar la Geometry en función de unas coordenadas de transformación.
     * @param rp, Coordenadas de transformación.
     */
	abstract public void reProject(MathTransform trans,
			CoordinateReferenceSystem target);
    
    /**
     * Permite dibujar la Geometry.
     * @param g, Graphics2D.
     * @param vp, ViewPortData.
     */
    public void draw(Graphics2D g, ViewPortData vp) {
    }
}
