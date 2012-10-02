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
package org.cresques.geo;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import org.cresques.px.Extent;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;


/**
 * Datos de vista sobre las capas.
 *
 * Mantiene un conjunto de datos necesarios, que describen el modo de
 * ver las capas actual.
 *
 * @author "Luis W. Sevilla" <sevilla_lui@gva.es>
 */
public class ViewPortData implements Georeferenced {
    /**
     * Tipo de proyección de la vista.
     */
	CoordinateReferenceSystem crs = null;

    /**
     * Sistema de coordenadas de la vista.
     */
	CoordinateReferenceSystem cs = null;

    /**
     * Amplitud de la vista, en coordenadas proyectadas.
     */
    Extent extent = null;

    /**
     * Tamaño de la vista, en coordenadas de dispositivo.
     */
    Dimension2D size = null;

    /**
     * Transformación afín usada en la vista actual.
     */
    public AffineTransform mat = null;

    /**
     * Resolución (Puntos por pulgada) de la vista actual.
     * Se necesita para los cálculos de escala geográfica.
     */
    int dpi = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();

    public ViewPortData() {
    }

    public ViewPortData(CoordinateReferenceSystem crs, Extent extent, Dimension2D size) {
        this.crs = crs;
        this.extent = extent;
        this.size = size;
        mat = new AffineTransform();
        mat.scale(1.0, -1.0);
    }

    public CoordinateReferenceSystem getCrs() {
        return crs;
    }

    public void setCrs(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    public void reProject(MathTransform rp, CoordinateReferenceSystem target) {
        // TODO metodo reProject pendiente de implementar
    }

    public void setCoordSys(CoordinateReferenceSystem cs) {
        this.cs = cs;
    }

    //public void setCoordTrans(ICoordTrans ct) { this.ct = ct; }
    public AffineTransform getMat() {
        return mat;
    }

    public void setMat(AffineTransform mat) {
        this.mat = mat;
    }

    public Object clone() {
        ViewPortData vp = new ViewPortData();

        if (mat != null) {
            vp.mat = new AffineTransform(mat);
        }

        if (extent != null) {
            vp.extent = new Extent(extent);
        }

        vp.crs = crs;
        vp.size = size;
        vp.dpi = dpi;

        return vp;
    }

    public double getWidth() {
        return size.getWidth();
    }

    public double getHeight() {
        return size.getHeight();
    }

    /**
     *
     */
    public Dimension2D getSize() {
        return size;
    }

    public void setSize(double w, double h) {
        setSize(new Dimension((int) w, (int) h));
    }

    public void setSize(Dimension2D sz) {
        size = sz;
        reExtent();
    }

    public Extent getExtent() {
        return extent;
    }

    public void setExtent(Dimension2D sz) {
        Point2D.Double pt0 = new Point2D.Double(0, 0);
        Point2D.Double ptSz = new Point2D.Double(sz.getWidth(), sz.getHeight());

        try {
            mat.inverseTransform(pt0, pt0);
            mat.inverseTransform(ptSz, ptSz);
        } catch (Exception e) {
            e.printStackTrace();
        }

        extent = new Extent(pt0, ptSz);
    }

    public void reExtent() {
        setExtent(size);
    }

    public void setDPI(int dpi) {
        this.dpi = dpi;
    }

    public int getDPI() {
        return this.dpi;
    }

    /**
     * zoom a un marco.
     *
     * @param extent
     */
    public void zoom(Extent extent) {
        double[] scale = extent.getScale(getWidth(), getHeight());
        double escala = Math.min(scale[0], scale[1]);

        mat.setToIdentity();
        mat.scale(escala, -escala);
        mat.translate(-extent.minX(), -extent.maxY());
        this.extent = extent;
        reExtent();
    }

    /**
     * zoom centrado en un punto.
     *
     * @param zoom
     * @param pt
     */
    public void zoom(double zoom, Point2D pt) {
        zoom(zoom, zoom, pt);
    }

    public void zoom(double zx, double zy, Point2D pt) {
        centerAt(pt);
        mat.scale(zx, zy);
        centerAt(pt);
        reExtent();
    }

    /**
     * Zoom a una escala (geográfica);
     *
     * @param scale
     */
    public void zoomToCenter(double f) {
        Point2D.Double ptCenter = new Point2D.Double(getWidth() / 2.0,
                                                     getHeight() / 2.0);

        try {
            mat.inverseTransform(ptCenter, ptCenter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        zoom(f, ptCenter);
    }

    /**
     * Centrar en un punto.
     *
     * @param pt
     */
    public void centerAt(Point2D pt) {
        Point2D.Double ptCenter = new Point2D.Double(getWidth() / 2.0,
                                                     getHeight() / 2.0);

        try {
            mat.inverseTransform(ptCenter, ptCenter);
            mat.translate(ptCenter.x - pt.getX(), ptCenter.y - pt.getY());
        } catch (Exception e) {
            e.printStackTrace();
        }

        reExtent();
    }

    /**
     * Desplaza la vista actual.
     *
     * @param pt
     */
    public void pan(Point2D ptIni, Point2D ptFin) {
        mat.translate(ptFin.getX() - ptIni.getX(), ptFin.getY() - ptIni.getY());
        reExtent();
    }

    public Point2D getCenter() {
        Point2D.Double ptCenter = new Point2D.Double(getWidth() / 2.0,
                                                     getHeight() / 2.0);

        try {
            mat.inverseTransform(ptCenter, ptCenter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ptCenter;
    }
}
