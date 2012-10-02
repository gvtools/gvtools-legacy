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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.cresques.geo.ViewPortData;
import org.cresques.px.Extent;
import org.cresques.px.IObjList;
import org.cresques.px.dxf.DxfEntityList;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;


/**
 * FeatureCollection de .gml y .shp
 * @author "Luis W. Sevilla" <sevilla_lui@gva.es>
 * jmorell: Añadidas a FeatureCollection las capacidades de almacenamiento propias de
 * Feature mediante herencia.
 */
public class FeatureCollection extends Feature implements IObjList.vector {
    CoordinateReferenceSystem crs = null;
    public Vector data = null;
    
    /**
     * Constructor de FeatureCollection.
     * @param proj, Proyección cartográfica en la que se encuentra la FeatureCollection.
     */
    public FeatureCollection(CoordinateReferenceSystem proj) {
        extent = new Extent();
        data = new Vector();
        property = new Hashtable();
    }
    
    /**
     * Permite añadir un objeto gráfico a la lista.
     * @param obj, Objeto gráfico que implemente el interface Extent.Has
     */
    public void add(Extent.Has feature) {
        Extent ext = feature.getExtent();

        if (extent != null) {
            extent.add(ext);
            data.add(feature);
        }
    }
    
    /**
     * Devuelve los obhjetos gráficos de la lista cuyos extents contengan al
     * punto que se le pasa como argumento.
     * @param pt, punto para localizar los objetos gráficos.
     * @return IObjList, Conjunto de objetos gráficos que contienen a pt.
     */
    public IObjList getAt(Point2D pt) {
        IObjList oList = new DxfEntityList(crs);
        Iterator iter = iterator();

        while (iter.hasNext()) {
            Extent.Has o = (Extent.Has) iter.next();

            if (o.getExtent().isAt(pt)) {
                oList.add(o);
            }
        }

        return oList;
    }
    
    /**
     * Devuelve un iterador para recorrer los elementos de la lista de objetos gráficos.
     * @return Iterator, iterador.
     */
    public Iterator iterator() {
        return data.iterator();
    }
    
    /**
     * Devuelve la cantidad de elementos que contiene la lista de objetos gráficos.
     * @return int
     */
    public int size() {
        return data.size();
    }
    
    /**
     * Permite eliminar un elemento de la lista de objetos gráficos.
     * @param obj, Objeto que queremos eliminar.
     */
    public void remove(Object obj) {
        data.remove(obj);
    }
    
    /**
     * Permite vacíar la lista de objetos gráficos.
     */
    public void clear() {
        extent = new Extent();
        data.clear();
    }
    
    /**
     * Devuelve uno de los elementos de la lista de objetos gráficos.
     * @param i, Índice del elemento de la lista que queremos obtener.
     * @return Extent.Has, elemento gráfico que queremos obtener.
     */
    public Extent.Has get(int i) {
        return (Extent.Has) data.get(i);
    }
    
    /**
     * Devuelve la proyección cartográfica en la que se encuentra la FeatureCollection.
     * @return CoordinateReferenceSystem, proyección cartográfica.
     */
    public CoordinateReferenceSystem getCrs() {
        return crs;
    }
    
    /**
     * Establece la proyección cartográfica en la que se encuentra la FeatureCollection.
     * @param p, Proyección cartográfica.
     */
    public void setCrs(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }
    
    /**
     * Permite cambiar la proyección en la que se encuentra la FeatureCollection a
     * través de un conjunto de coordenadas de transformación.
     * @param rp, Coordenadas de transformación.
     */
	public void reProject(MathTransform trans, CoordinateReferenceSystem target) {
        extent = new Extent();

        Feature f;
        Geometry g;
        Iterator iter = iterator();

        while (iter.hasNext()) {
            f = (Feature) iter.next();
            g = f.getGeometry();
            g.reProject(trans, target);
            extent.add(g.getExtent());
        }

        setCrs(target);
    }
    
    /**
     * Permite dibujar las Features que conforman la FeatureCollection.
     */
    public void draw(Graphics2D g, ViewPortData vp) {
        Iterator iter = iterator();

        while (iter.hasNext()) {
            Feature f = (Feature) iter.next();
            f.draw(g, vp);
        }
    }
    
    /**
     * Permite obtener el extent de la FeatureCollection.
     * @return Extent, rectángulo en donde se ubican las Features que conforman la
     * FeatureCollection.
     */
    public Extent getExtent() {
        return extent;
    }
}
