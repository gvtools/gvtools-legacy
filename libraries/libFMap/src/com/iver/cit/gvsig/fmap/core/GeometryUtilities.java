package com.iver.cit.gvsig.fmap.core;

/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 */

import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.cresques.cts.IProjection;

import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.tools.geo.Geo;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;

/**
 * Misc utilities for calculations with geometries (area, length, close line,
 * get multipolygon or multiline parts, etc).
 *
 * @author Vicente Caballero Navarro (vicente.caballero@iver.es)
 * @author Jaume Domínguez Faus (jaume.dominguez@iver.es)
 * @author César Martínez Izquierdo (cesar.martinez@iver.es)
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class GeometryUtilities {
	/**
	 * Calculates the area of a polygon.
	 */
	public static double getArea(FLayer layer, IGeometry geom) {
		ArrayList parts=getParts(geom);
		double area=0;
		for (int i=0;i<parts.size();i++){
			Double[][] xsys=(Double[][])parts.get(i);//getXY(geom);
			Double[] xs=xsys[0];
			Double[] ys=xsys[1];
			IProjection proj=layer.getMapContext().getProjection();
			if (isCCW(xs, ys)){
				if (proj.isProjected()) {
					ViewPort vp =layer.getMapContext().getViewPort();
					area-= getCoordsArea(vp, xs,ys,new Point2D.Double(xs[xs.length-1].doubleValue(),ys[ys.length-1].doubleValue()));
				}else{
					area-= getGeoCArea(xs,ys);
				}
			}else{
				if (proj.isProjected()) {
					ViewPort vp =layer.getMapContext().getViewPort();
					area+= getCoordsArea(vp, xs,ys,new Point2D.Double(xs[xs.length-1].doubleValue(),ys[ys.length-1].doubleValue()));
				}else{
					area+= getGeoCArea(xs,ys);
				}
			}
		}
		return area;
	}

    /**
     * Calculates the length of a polygon (perimeter) or a line.
     *
     * @param vp
     * @param geom
     * @return
     */
    public static double getLength(ViewPort vp, IGeometry geom){
    	ArrayList parts=getParts(geom);
    	double perimeter=0;
    	for (int j=0;j<parts.size();j++){
    		Double[][] xsys=(Double[][])parts.get(j);
    		double dist = 0;
    		double distAll = 0;

    		for (int i = 0; i < (xsys[0].length - 1); i++) {
    			dist = 0;

    			Point2D p = new Point2D.Double(xsys[0][i].doubleValue(), xsys[1][i].doubleValue());
    			Point2D p2 = new Point2D.Double(xsys[0][i + 1].doubleValue(), xsys[1][i + 1].doubleValue());
    			dist = vp.distanceWorld(p,p2);
    			distAll += dist;
    		}
    		int distanceUnits=vp.getDistanceUnits();
    		perimeter+= distAll/MapContext.getDistanceTrans2Meter()[distanceUnits];
    	}
    	return perimeter;

    }

	/**
	 * <p>Returns an array list containing the parts that compose a
	 * multipolygon or multilyne. Each element of the array list is an array
	 * (Double[][]) containing the X and Y coordinates for each polygon
	 * part.</p>
	 *
	 * <p>I think it wll be clearer with an example:
	 *
	 * <pre>ArrayList parts = GeometryUtilities.getParts(geom);
	 * // xs will contain all the X coordinates from the first polygon part
	 * Double[] xs = parts.get(0)[0];
	 * // ys will contain all the Y coordinates from the first polygon part
	 * Double[] ys = parts.get(0)[1];
	 *
	 * ... do something with 'xs' and 'ys' ...
	 *
	 * // xs will now contain all the X coordinates from the second polygon part
	 * xs = parts.get(1)[0];
	 * // ys will now contain all the Y coordinates from the second polygon part
	 * ys = parts.get(1)[1];
	 *
	 * ... we will continue with the rest of the parts: parts.get(2),
	 * parts.get(3), etc... of course, we should check how many parts the polygon
	 * really has.
	 * </pre>
	 *
	 *
	 * @param geometry
	 * @return
	 */
	public static ArrayList getParts(IGeometry geometry) {
        ArrayList xs = new ArrayList();
        ArrayList ys = new ArrayList();
        ArrayList parts=new ArrayList();
        double[] theData = new double[6];

        //double[] aux = new double[6];
        PathIterator theIterator;
        int theType;
        int numParts = 0;
        theIterator = geometry.getPathIterator(null, FConverter.FLATNESS); //, flatness);
        boolean isClosed = false;
        double firstX=0;
        double firstY=0;
        while (!theIterator.isDone()) {
            theType = theIterator.currentSegment(theData);

            switch (theType) {
            case PathIterator.SEG_MOVETO:
            		if (numParts==0){
            			firstX=theData[0];
            			firstY=theData[1];
            			xs.add(new Double(theData[0]));
            			ys.add(new Double(theData[1]));
            		}else{
            			if (!isClosed){
            				Double[] x = (Double[]) xs.toArray(new Double[0]);
            				Double[] y = (Double[]) ys.toArray(new Double[0]);
            				parts.add(new Double[][] { x, y });
            				xs.clear();
            				ys.clear();
            			}
            			firstX=theData[0];
            			firstY=theData[1];
            			xs.add(new Double(theData[0]));
            			ys.add(new Double(theData[1]));
            		}
                numParts++;
                isClosed = false;
                break;
            case PathIterator.SEG_LINETO:
            	isClosed=false;
                xs.add(new Double(theData[0]));
                ys.add(new Double(theData[1]));
                break;
            case PathIterator.SEG_CLOSE:
            	isClosed=true;
                xs.add(new Double(theData[0]));
                ys.add(new Double(theData[1]));
                xs.add(new Double(firstX));
                ys.add(new Double(firstY));
                Double[] x = (Double[]) xs.toArray(new Double[0]);
                Double[] y = (Double[]) ys.toArray(new Double[0]);
                parts.add(new Double[][] { x, y });
                xs.clear();
                ys.clear();
                break;
            } //end switch

            theIterator.next();
        } //end while loop

        if (!isClosed){
        	isClosed=true;
        	xs.add(new Double(theData[0]));
            ys.add(new Double(theData[1]));
            Double[] x = (Double[]) xs.toArray(new Double[0]);
            Double[] y = (Double[]) ys.toArray(new Double[0]);
            parts.add(new Double[][] { x, y });
            xs.clear();
            ys.clear();
        }
        return parts;

    }

	public static boolean isCCW(Double[] xs, Double[] ys){
		CoordinateList coordList = new CoordinateList();
		for (int i = 0; i < ys.length; i++) {
    	   Coordinate coord=new Coordinate(xs[i].doubleValue(),ys[i].doubleValue());
    	   coordList.add(coord);
		}
		if (coordList.isEmpty())
			return true;
		return CGAlgorithms.isCCW(coordList.toCoordinateArray());
	}

	/**
	 * Returns the area of a polygon, whose coordinates are defined
	 * in a geographic (latitude/longitude) CRS.
	 *
	 * @param xs
	 * @param ys
	 * @return
	 */
	private static double getGeoCArea(Double[] xs,Double[] ys) {
		double[] lat=new double[xs.length];
		double[] lon=new double[xs.length];
		for (int K= 0; K < xs.length; K++){
			lon[K]= xs[K].doubleValue()/Geo.Degree;
			lat[K]= ys[K].doubleValue()/Geo.Degree;
		}
		return (Geo.sphericalPolyArea(lat,lon,xs.length-1)*Geo.SqM);
	}
	/**
	 * Calculates the area for a polygon whose coordinates are defined
	 * in a projected CRS.
	 *
	 * @param aux último punto.
	 *
	 * @return Área.
	 */
	private static double getCoordsArea(ViewPort vp, Double[] xs,Double[] ys, Point2D point) {
		Point2D aux=point;
		double elArea = 0.0;
		Point2D pPixel;
		Point2D p = new Point2D.Double();
		Point2D.Double pAnt = new Point2D.Double();

		for (int pos = 0; pos < xs.length-1; pos++) {
			pPixel = new Point2D.Double(xs[pos].doubleValue(),
					ys[pos].doubleValue());
			p = pPixel;
			if (pos == 0) {
				pAnt.x = aux.getX();
				pAnt.y = aux.getY();
			}
			elArea = elArea + ((pAnt.x - p.getX()) * (pAnt.y + p.getY()));
			pAnt.setLocation(p);
		}

		elArea = elArea + ((pAnt.x - aux.getX()) * (pAnt.y + aux.getY()));
		elArea = Math.abs(elArea / 2.0);
		return (elArea/(Math.pow(MapContext.getAreaTrans2Meter()[vp.getDistanceArea()],2)));
	}


	/**
	 * Creates a closed polygon from an open polyline.
	 */
    public static IGeometry closeLine(IGeometry line) {
    	PathIterator it = line.getPathIterator(null);
    	double[] newCoords = new double[6]; // Can receive as much 6 coordinates (3 points)
    	Point2D firstPoint = new Point2D.Double(), point = new Point2D.Double();
		GeneralPathX gP = new GeneralPathX();
    	int currentType;
    	boolean closed = false;
    	long numPoints = 0;

		while (! it.isDone()) {
			currentType = it.currentSegment(newCoords);

			switch (currentType) {
				case PathIterator.SEG_MOVETO:
					// SEG_MOVETO -> New polygon
					firstPoint.setLocation(newCoords[0], newCoords[1]);
				//	firstPoint = vP.toMapPoint(firstPoint);
					gP.moveTo(firstPoint.getX(), firstPoint.getY());
					numPoints ++;
					break;
                case PathIterator.SEG_LINETO:
					point.setLocation(newCoords[0], newCoords[1]);
					//point = vP.toMapPoint(point);
					gP.lineTo(point.getX(), point.getY());
					numPoints ++;
                	break;
                case PathIterator.SEG_QUADTO:
                	// Don't used
                    break;
                case PathIterator.SEG_CUBICTO:
                	// Don't used
                    break;
                case PathIterator.SEG_CLOSE:
                	// Closes the line with the first point
					//gP.lineTo(firstPoint.getX(), firstPoint.getY());
                	numPoints ++;
					gP.closePath();
					closed = true;
                    break;
            }

			it.next();
		}

		// Only can close (convert to polygons) the lines which are composed by more than 2 points
		if (numPoints < 3)
			return null;

		// Forces to close the multi-line
		if ((! closed) && (firstPoint != null)) {
			//gP.lineTo(firstPoint.getX(), firstPoint.getY());
			numPoints ++;
			gP.closePath();
		}

		return ShapeFactory.createPolygon2D(gP);
    }

    /**
     * Creates a polyline 2D from an array of points
     *
     * @param points
     * @return
     */
    public static IGeometry getPolyLine2D(FPoint2D[] points) {
    	if (points.length < 2)
    		return null;

    	// Creates the general path of the new polyline
    	GeneralPathX gPath = new GeneralPathX();

    	gPath.moveTo(points[0].getX(), points[0].getY());

    	for (int i = 1; i < points.length; i++) {
    		gPath.lineTo(points[i].getX(), points[i].getY());
		}

    	return ShapeFactory.createPolyline2D(gPath);
    }

    /**
     * Creates a polygon 2D from an array of points.
     *
     * @param points
     * @return
     */
    public static IGeometry getPolygon2D(FPoint2D[] points) {
    	if (points.length < 3)
    		return null;

    	// Creates the general path of the new polygon
    	GeneralPathX gPath = new GeneralPathX();

    	gPath.moveTo(points[0].getX(), points[0].getY());

    	for (int i = 1; i < points.length; i++) {
    		gPath.lineTo(points[i].getX(), points[i].getY());
		}

    	// Forces to close the polygon
    	gPath.closePath();

    	return ShapeFactory.createPolygon2D(gPath);
    }

}
