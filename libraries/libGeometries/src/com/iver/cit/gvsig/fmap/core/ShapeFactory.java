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

import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;

import com.iver.cit.gvsig.fmap.edition.UtilFunctions;


/**
 * Clase que crea las geometrías, contendra un método create por cada tipo de
 * geometria que soporte gvSIG
 */
public class ShapeFactory {
	/**
	 * Crea una geometría que contiene como shape un punto 2D.
	 *
	 * @param x Coordenada x.
	 * @param y Coordenada y.
	 *
	 * @return Geometría.
	 */
	public static IGeometry createPoint2D(double x, double y) {
		return new FPoint2D(null, null, x, y);
	}
	
	public static IGeometry createPoint2D(FPoint2D p) {
		return p;
	}


	/**
	 * Crea una geometría que contiene como shape un Multipunto 2D.
	 *
	 * @param x Coordenada x.
	 * @param y Coordenada y.
	 *
	 * @return Geometría.
	 */
	public static IGeometry createMultipoint2D(double[] x, double[] y) {
		return new FMultiPoint2D(null, null, x, y);
	}

	/**
	 * Crea una geometría que contiene como shape un punto 3D.
	 *
	 * @param x Coordenada x.
	 * @param y Coordenada y.
	 * @param z Coordenada z.
	 *
	 * @return Geometría.
	 */
	public static IGeometry createPoint3D(double x, double y, double z) {
		return new FPoint3D(null, null, x, y, z);
	}

	/**
	 * Crea una geometría que contiene como shape un Multipunto 3D.
	 *
	 * @param x Coordenada x.
	 * @param y Coordenada y.
	 * @param z Coordenada z.
	 *
	 * @return Geometría.
	 */
	public static IGeometry createMultipoint3D(double[] x, double[] y,
		double[] z) {
		return new FMultipoint3D(null, null, x, y, z);
	}

	/**
	 * Crea una geometría que contiene como shape un Polilínea 2D.
	 *
	 * @param shape GeneralPathX.
	 *
	 * @return Geometría.
	 */
	public static IGeometry createPolyline2D(GeneralPathX shape) {
		return new FPolyline2D(null, null, shape);
	}

	/**
	 * Crea una geometría que contiene como shape un Polilínea 3D.
	 *
	 * @param shape GeneralPathX.
	 * @param pZ Vector de Z.
	 *
	 * @return Geometría.
	 */
	public static IGeometry createPolyline3D(GeneralPathX shape, double[] pZ) {
		return new FPolyline3D(null, null, shape, pZ);
	}
	
	/**
	 * Creates a multiline form a set of lines
	 * @param polylines
	 * The lines
	 * @return
	 * A multiline
	 */
	public static IGeometry createMultiPolyline(FPolyline2D[] polylines) {
		return new FMultiPolyline2D(null, null, polylines);
	}
	
	/**
	 * Creates a multiline form a set of lines
	 * @param polylines
	 * The lines
	 * @return
	 * A multiline
	 */
	public static IGeometry createMultiPolygon(FPolygon2D[] polygons) {
		return new FMultiPolygon2D(null, null, polygons);
	}
	
	/**
	 * Creates a multigeometry form a set of geometries
	 * @param geometries
	 * The geometries
	 * @return
	 * A multigeometry
	 */
	public static IGeometry createMultiGeometry(FGeometry[] geometries) {
		return new FGeometryCollection(null, null, geometries);
	}
	
	/**
	 * Crea una geometría que contiene como shape un Polígono 3D.
	 *
	 * @param shape GeneralPathX.
	 * @param pZ Vector de Z.
	 *
	 * @return Geometría.
	 */
	public static IGeometry createPolygon3D(GeneralPathX shape, double[] pZ) {
		return new FPolygon3D(null, null, shape, pZ);
	}

	/**
	 * Crea una geometría que contiene como shape un Polígono 2D.
	 *
	 * @param shape GeneralPathX.
	 *
	 * @return Geometría.
	 */
	public static IGeometry createPolygon2D(GeneralPathX shape) {
		return new FPolygon2D(null, null, shape);
	}

	/**
	 * Crea una geometría que contiene como shape un Polígono 2D.
	 *
	 * @param shape FPolyline2D closed (you must be sure it is really closed).
	 *
	 * @return Geometría.
	 */
	public static IGeometry createPolygon2D(FPolyline2D shape) {
		return new FPolygon2D(null, null, shape.gp);
	}

	/**
	 * Crea a partir de un FShape una geometría.
	 *
	 * @param shp FShape.
	 *
	 * @return Geometría.
	 */
	public static FGeometry createGeometry(FShape shp) {
		return (FGeometry)shp;
	}
   
    public static IGeometry createCircle(Point2D center, Point2D r){
		double radio = center.distance(r);
		return createCircle(center, radio);
	}

	public static IGeometry createCircle(Point2D center, double radio){
		Arc2D.Double arc = new Arc2D.Double(center.getX()-radio, center.getY() - radio,
				2 * radio, 2 * radio, 0, 360, Arc2D.OPEN);

		return new FCircle2D(null, null, new GeneralPathX(arc),center,radio);
	}
	public static IGeometry createCircle(Point2D p1, Point2D p2, Point2D p3){
		Point2D center = UtilFunctions.getCenter(p1, p2, p3);
		if (center!=null)
		return createCircle(center,p1);
		return null;
	}
	
	public static IGeometry createArc(Point2D p1, Point2D p2, Point2D p3){
		Arc2D arco = UtilFunctions.createArc(p1, p2, p3);
		if (arco == null) return null;
		return new FArc2D(null, null, new GeneralPathX(arco),p1,p2,p3);
	}
	
	public static IGeometry createEllipse(Point2D axis1Start, Point2D axis1End, double axis2Length){
		double xAxis = axis1Start.distance(axis1End);
		Arc2D.Double arc = new Arc2D.Double(axis1Start.getX(),
				axis1Start.getY() - axis2Length, xAxis, 2 * axis2Length, 0, 360, Arc2D.OPEN);
		Point2D rotationPoint = new Point2D.Double(axis1Start.getX() + xAxis /2, axis1Start.getY());
		double angle = UtilFunctions.getAngle(axis1Start, axis1End);
		AffineTransform mT = AffineTransform.getRotateInstance(angle, axis1Start.getX(), axis1Start.getY());
		GeneralPathX gp = new GeneralPathX(arc);
		gp.transform(mT);

		return new FEllipse2D(null, null, new GeneralPathX(gp),axis1Start,axis1End,axis2Length);
	}
	public static IGeometry createSpline2D(Point2D[] points) {
		return new FSpline2D(null, null, points);
	}
}
