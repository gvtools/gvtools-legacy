/*
 * Created on 17.08.2006
 *
 * SVN header information:
 *  $Author$
 *  $Rev$
 *  $Date$
 *  $Id$
 */
package org.gvsig.fmap.algorithm.triangulation.pirol;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Creates triangular polygons out of a set of points and lines in the Delaunay
 * diagramm.
 * 
 * <br>
 * <br>
 * <b>Last change by $Author$ on $Date$</b>
 * 
 * @author Ole Rahn
 * 
 * @version $Rev$
 * 
 */
public class PolygonCreator {

	protected final static GeometryFactory geometryFactory = new GeometryFactory();

	// protected static PersonalLogger logger = new
	// PersonalLogger(DebugUserIds.OLE);
	public static double AreaSum = 0; // added for 3 dimensional face
										// calculation

	public static final Polygon[] createPolygons(DelaunayCalculator dc) {
		ArrayList<Polygon> triangleList = new ArrayList<Polygon>();
		DelaunayPunkt pkt;
		DelaunayPunkt[] pointArray = dc.getPointsArray();
		AreaSum = 0;
		double s = 0, a = 0, b = 0, c = 0;

		Integer[] trianglesBuild;

		List<Integer> trianglesDone = new ArrayList<Integer>();
		DelaunayPunkt[] theOtherTwoPoints;

		Coordinate[] coords = new Coordinate[4];

		for (int l = 0; l < pointArray.length; l++) {
			pkt = pointArray[l];

			trianglesBuild = pkt.getDreieckTrianglesBelongingToArray();

			for (int numInd = 0; numInd < trianglesBuild.length; numInd++) {
				if (!trianglesDone.contains(trianglesBuild[numInd])) {
					trianglesDone.add(trianglesBuild[numInd]);
					s = a = b = c = 0;
					theOtherTwoPoints = pkt
							.getTheOther2PointsFromDreieckTriangle(trianglesBuild[numInd]
									.intValue());

					if (theOtherTwoPoints.length != 2) {
						System.err.println("invalid number of neighbor points");
						continue;
					}
					/*
					 * a=PirolPoint.distanceBetween(pkt,theOtherTwoPoints[0],3);
					 * b=PirolPoint.distanceBetween(pkt,theOtherTwoPoints[1],3);
					 * c=PirolPoint.distanceBetween(theOtherTwoPoints[0],
					 * theOtherTwoPoints[1],3); s=(a+b+c)/2;
					 */
					AreaSum += Math.sqrt(s * (s - a) * (s - b) * (s - c));

					try {
						coords = new Coordinate[4];
						coords[0] = PirolPoint.toCoordinate(pkt); // Data2LayerConnector.punkt2Coordinate(pkt);
						coords[1] = PirolPoint
								.toCoordinate(theOtherTwoPoints[0]); // Data2LayerConnector.punkt2Coordinate(theOtherTwoPoints[0]);
						coords[2] = PirolPoint
								.toCoordinate(theOtherTwoPoints[1]); // Data2LayerConnector.punkt2Coordinate(theOtherTwoPoints[1]);
						coords[3] = PirolPoint.toCoordinate(pkt); // Data2LayerConnector.punkt2Coordinate(pkt);
					} catch (Exception e) {
						System.err.println(e.getLocalizedMessage());
						continue;
					}

					triangleList.add(geometryFactory.createPolygon(
							geometryFactory.createLinearRing(coords), null));
				}
			}
		}

		return triangleList.toArray(new Polygon[0]);
	}

	public static final ArrayList<DelaunayPunkt[]> createTrianglesList(
			DelaunayCalculator dc) {
		ArrayList<DelaunayPunkt[]> triangleList = new ArrayList<DelaunayPunkt[]>();
		DelaunayPunkt pkt;
		DelaunayPunkt[] pointArray = dc.getPointsArray();
		AreaSum = 0;
		double s = 0, a = 0, b = 0, c = 0;

		Integer[] trianglesBuild;

		List<Integer> trianglesDone = new ArrayList<Integer>();
		DelaunayPunkt[] theOtherTwoPoints;

		DelaunayPunkt[] triangle = new DelaunayPunkt[3];

		for (int l = 0; l < pointArray.length; l++) {
			pkt = pointArray[l];

			trianglesBuild = pkt.getDreieckTrianglesBelongingToArray();

			for (int numInd = 0; numInd < trianglesBuild.length; numInd++) {
				if (!trianglesDone.contains(trianglesBuild[numInd])) {
					trianglesDone.add(trianglesBuild[numInd]);
					s = a = b = c = 0;
					theOtherTwoPoints = pkt
							.getTheOther2PointsFromDreieckTriangle(trianglesBuild[numInd]
									.intValue());

					if (theOtherTwoPoints.length != 2) {
						System.err.println("invalid number of neighbor points");
						continue;
					}
					/*
					 * a=PirolPoint.distanceBetween(pkt,theOtherTwoPoints[0],3);
					 * b=PirolPoint.distanceBetween(pkt,theOtherTwoPoints[1],3);
					 * c=PirolPoint.distanceBetween(theOtherTwoPoints[0],
					 * theOtherTwoPoints[1],3); s=(a+b+c)/2;
					 */
					AreaSum += Math.sqrt(s * (s - a) * (s - b) * (s - c));

					try {
						triangle = new DelaunayPunkt[3];
						triangle[0] = pkt; // Data2LayerConnector.punkt2Coordinate(pkt);
						triangle[1] = theOtherTwoPoints[0]; // Data2LayerConnector.punkt2Coordinate(theOtherTwoPoints[0]);
						triangle[2] = theOtherTwoPoints[1]; // Data2LayerConnector.punkt2Coordinate(theOtherTwoPoints[1]);
						// coords[3] = PirolPoint.toCoordinate(pkt);
						// //Data2LayerConnector.punkt2Coordinate(pkt);
					} catch (Exception e) {
						System.err.println(e.getLocalizedMessage());
						continue;
					}

					triangleList.add(triangle);
				}
			}
		}

		return triangleList;
	}

}
