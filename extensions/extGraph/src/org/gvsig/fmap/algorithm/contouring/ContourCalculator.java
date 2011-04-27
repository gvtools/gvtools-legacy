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

/*
 * AUTHORS (In addition to CIT):
 * 2008 Software Colaborativo (www.scolab.es)   development
 */

package org.gvsig.fmap.algorithm.contouring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gvsig.fmap.algorithm.triangulation.TIN;
import org.gvsig.fmap.algorithm.triangulation.Triangle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.operation.linemerge.LineSequencer;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;

public class ContourCalculator {
	private TIN tin;
	private Coordinate firstPoint;
	private Coordinate lastPoint;
	LineSegment lastSegment;
	private GeometryFactory geomFactory = new GeometryFactory();

	public ContourCalculator(TIN tin) {
		this.tin = tin;
	}

	public Collection<LineString> getContour(double height) {
		CoordinateList gp = null;
		ArrayList<LineString> segmentList = new ArrayList<LineString>();
		int iTri = 0;
		for (Triangle tri : tin.getTriangles()) {
			try {
				if (tri.containsZ(height)) {
					Coordinate[] segment = tri.getSegmentZ(height);
					// Checkeamos coordenadas
					if (segment[0] == segment[1]) {
						if (tri.containsZ(height - 0.1))
						{
							segment = tri.getSegmentZ(height - 0.1);
						}
						else
							continue;
					}
					LineString lineString = geomFactory.createLineString(segment);
					segmentList.add(lineString);

//					TriEdge edgeIn = tri.getEdgeContainingZ(height);
//					if (edgeIn != null) {
//						gp = new CoordinateList();
//						firstPoint = edgeIn.getPointOnZ(height);
//						lastPoint = tri.getNextPoint(firstPoint, height);
//						gp.add(firstPoint, true);
//						gp.add(lastPoint, false);
//					}
//					if (gp.size() > 1) {
//						LineString lineString = geomFactory.createLineString(gp.toCoordinateArray());
//						segmentList.add(lineString);
//					}
//					else {
//						System.out.println("stop!!");
//					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("ContourCalculator.getContour(): " + e.getMessage());
			}
			iTri++;
		}
		return segmentList;

	}
	
	// TODO:
	public Collection<LineString> getContour_Complex_BAD(double height) {
		CoordinateList gp = null;
		for (Triangle tri : tin.getTriangles()) {
			tri.setVisited(false);
		}
		firstPoint = null;
		ArrayList<LineString> contourList = new ArrayList<LineString>();
		for (Triangle tri : tin.getTriangles()) {
			if (tri.isVisited() == false) {
				try {
					if (tri.containsZ(height)) {
						TriEdge edgeIn = tri.getEdgeContainingZ(height);
						if (edgeIn != null) {
							if (firstPoint == null) { // initialize contour line
								gp = new CoordinateList();
								firstPoint = edgeIn.getPointOnZ(height);
								lastPoint = tri.getNextPoint(firstPoint, height);
								lastSegment = new LineSegment(firstPoint, lastPoint);
								gp.add(firstPoint, true);
								gp.add(lastPoint, false);
							}
							boolean bExit = false;
							do {
								Triangle nextTri = findNeighbourg(tri, lastSegment);
								if (nextTri != null) {
									Coordinate auxC = new Coordinate(lastPoint);
									lastPoint = nextTri.getNextPoint(lastPoint, height);
									lastSegment = new LineSegment(auxC, lastPoint);
									gp.add(lastPoint, true);
									double dAux = firstPoint.distance(lastPoint);
									if (dAux < 1e-8) {
										gp.closeRing();
										bExit = true;
									}
									nextTri.setVisited(true);
								} else
									bExit = true;

							} while (bExit == false);
							if (gp.size() > 2) {
								LineString lineString = geomFactory.createLineString(gp.toCoordinateArray());
								contourList.add(lineString);
							}
							firstPoint = null;
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tri.setVisited(true);
			}
		}
		return contourList;

	}

	/**
	 * Find the triangle side by side with edgeIn 
	 * Options: 
	 * 1.- lastPoint on edge, and we exit by other edge.
	 * 2.- lastPoint on vertex, and we exit by other edge.
	 * 3.- lastPoint on vertex, and we exit by other vertex.
	 * 4.- lastPoint on edge, and we exit by a vertex.
	 * 
	 * @param tri
	 * @param lastSegment
	 * @return
	 */
	private Triangle findNeighbourg(Triangle tri, LineSegment lastSegment) {
		// All neighbourgs:
		Coordinate lastPoint = lastSegment.p1;
		TriEdge s1 = new TriEdge(tri.getV1(), tri.getV2());
		TriEdge s2 = new TriEdge(tri.getV2(), tri.getV3());
		TriEdge s3 = new TriEdge(tri.getV3(), tri.getV1());
		double d1, d2, d3;
		d1 = s1.distance(lastPoint);
		d2 = s2.distance(lastPoint);
		d3 = s3.distance(lastPoint);
		ArrayList<Triangle> aux = new ArrayList<Triangle>();
		if (d1 < 1E-8) {
			getEdgeNeighbours(s1, aux);
		}
		else if (d2 < 1E-8) {
			getEdgeNeighbours(s2, aux);
		}
		else if (d3 < 1E-8) {
			getEdgeNeighbours(s3, aux);
		}
		
		// We choose a point "a little further on lastSegment"
		// and try if it is inside neighbourg triangles
		Coordinate pointOut = lastSegment.pointAlong(1.001);
		for (Triangle theNextTriangle: aux) {
			if (theNextTriangle == tri) continue; // We avoid to test the original triangle.
			if (theNextTriangle.isVisited()) continue;
			if (theNextTriangle.contains(pointOut))
				return theNextTriangle;
		}
		return null;
	}
	
	private void getEdgeNeighbours(TriEdge s1, ArrayList<Triangle> aux) {
		List<Triangle> l1 = tin.getTrianglesRelatedTo(s1.getV1());
		List<Triangle> l2 = tin.getTrianglesRelatedTo(s1.getV2());
		aux.addAll(l1);
		aux.addAll(l2);
	}

}
