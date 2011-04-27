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
 * 2008 {{Company}}   {{Task}}
 */

package org.gvsig.fmap.algorithm.triangulation;

import org.gvsig.fmap.algorithm.contouring.TriEdge;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * 
 */
public class DefaultTriangle implements Triangle {

	protected Vertex V1, V2, V3;
	protected boolean visited = false;

	static GeometryFactory geomFactory = new GeometryFactory();
	/*
	 * determinant
	 * 
	 * Computes the determinant of the three points. Returns whether the
	 * triangle is clockwise or counter-clockwise.
	 */

	static double determinant(Vertex v1, Vertex v2, Vertex v3) {
		double determ;

		determ = (v2.getX() - v1.getX()) * (v3.getY() - v1.getY())
				- (v3.getX() - v1.getX()) * (v2.getY() - v1.getY());
		return determ;
	} /* End of determinant */

	static boolean isCCW(Vertex v1, Vertex v2, Vertex v3) {
		double determ = determinant(v1, v2, v3);
		if (determ >= 0.0)
			return true;
		else
			return false;

	}

	public DefaultTriangle(Vertex v1, Vertex v2, Vertex v3) {

		// our triangle will be always CW.
		this.V1 = v1;
		if (isCCW(v1, v2, v3)) {
			this.V2 = v3;
			this.V3 = v2;
		} else {
			this.V3 = v3;
			this.V2 = v2;
		}
		
	}

	public Vertex getV1() {
		return V1;
	}

	public Vertex getV2() {
		return V2;
	}

	public Vertex getV3() {
		return V3;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public boolean containsZ(double z) throws Exception {
		double min, max;
		min = Math.min(V1.getZ(), V2.getZ());
		min = Math.min(min, V3.getZ());
		max = Math.max(V1.getZ(), V2.getZ());
		max = Math.max(max, V3.getZ());
		
		if ((min <= z) && (z <= max))
			return true;

		return false;
	}

	public TriEdge getEdgeContainingZ(double z) {
		if ((V1.getZ() <= z) && (V2.getZ() >= z))
			return new TriEdge(V1, V2);
		if ((V1.getZ() >= z) && (V2.getZ() <= z))
			return new TriEdge(V2, V1);
		if ((V2.getZ() <= z) && (V3.getZ() >= z))
			return new TriEdge(V2, V3);
		if ((V2.getZ() >= z) && (V3.getZ() <= z))
			return new TriEdge(V3, V2);
				
		return null;
	}

	public Coordinate[] getSegmentZ(double z) {
		Coordinate[] resul = new Coordinate[2];
		int i=0;
		TriEdge e1 = new TriEdge(V1, V2);
		if (e1.containsZ(z))
			resul[i++] = e1.getPointOnZ(z);

		TriEdge e2 = new TriEdge(V2, V3);
		if (e2.containsZ(z))
			resul[i++] = e2.getPointOnZ(z);

		// Si el triángulo es plano, fallará
		if (i==2)
			return resul;
		TriEdge e3 = new TriEdge(V3, V1);
		if (e3.containsZ(z))
			resul[i++] = e3.getPointOnZ(z);
				
		return resul;
	}

	
	/* (non-Javadoc)
	 * @see org.gvsig.fmap.algorithm.triangulation.Triangle#getNextPoint(com.vividsolutions.jts.geom.Coordinate, double)
	 */
	public Coordinate getNextPoint(Coordinate lastPoint, double z) {
		// Options:
		// 	1.- lastPoint on edge, and we exit by other edge.
		// 	2.- lastPoint on vertex, and we exit by other edge.
		// 	3.- lastPoint on vertex, and we exit by other vertex.
		// 	4.- lastPoint on edge, and we exit by a vertex.
		
		// FIXME: ONLY WHEN WE ARE DEBUGGING
		try {
			if (!containsZ(z))
				throw new RuntimeException("Esto no debería pasar nunca.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TriEdge s1 = new TriEdge(V1, V2);
		TriEdge s2 = new TriEdge(V2, V3);
		TriEdge s3 = new TriEdge(V3, V1);

		// First, we check vertex
		if (lastPoint.equals3D(V1) || lastPoint.equals3D(V2) || lastPoint.equals3D(V3)) {
			// options 2 or 3
			// ¿Wich vertex are we entering?
			if (lastPoint.equals3D(V1)) {
				if (V2.getZ() == z)
					return V2;
				if (V3.getZ() == z)
					return V3;
				if (s2.containsZ(z)) { 
					return s2.getPointOnZ(z);
				}
			}
			if (lastPoint.equals3D(V2)) {
				if (V1.getZ() == z)
					return V1;
				if (V3.getZ() == z)
					return V3;
				if (s3.containsZ(z)) { 
					return s3.getPointOnZ(z);
				}

			}
			if (lastPoint.equals3D(V3)) {
				if (V2.getZ() == z)
					return V2;
				if (V1.getZ() == z)
					return V1;
				if (s1.containsZ(z)) { 
					return s1.getPointOnZ(z);
				}

			}
		}

		//  Now, options 1 and 3.
		double d1, d2, d3;
		d1 = s1.distance(lastPoint);
		d2 = s2.distance(lastPoint);
		d3 = s3.distance(lastPoint);
		
		if (d1 <= 1E-8) // lastPoint ON s1
		{
			if (s2.containsZ(z)) { 
				return s2.getPointOnZ(z);
			}
			else if (s3.containsZ(z)) {
				return s3.getPointOnZ(z);
			}
			else
				throw new RuntimeException("d1=d2=d3=0");
		}

		if (d2 <= 1E-8) // lastPoint ON s2
		{
			if (s1.containsZ(z)) { 
				return s1.getPointOnZ(z);
			}
			else if (s3.containsZ(z)) {
				return s3.getPointOnZ(z);
			}
			else
				throw new RuntimeException("d1=d2=d3=0");
		}
		if (d3 <= 1E-8) // lastPoint ON s3
		{
			if (s2.containsZ(z)) { 
				return s2.getPointOnZ(z);
			}
			else if (s1.containsZ(z)) {
				return s1.getPointOnZ(z);
			}
			else
				throw new RuntimeException("d1=d2=d3=0");
		}
		
		
//		// First, we check vertex
//		if (lastPoint.equals3D(V1) || lastPoint.equals3D(V2) || lastPoint.equals3D(V3)) {
//			// options 2 or 3
//		}
//		else
//		{
//			// We search for origin edge, and then for exit edge.
//			TriEdge edge = getEdgeContainingZ(z);
//		}
//		double x1;
//		double y1;
//		double pctAlong = (z - edgeIn.getV1().getZ()) / (edgeIn.getV2().getZ() - edgeIn.getV1().getZ());
//
//		x1 = edgeIn.getV1().getX() + (edgeIn.getV2().getX() - edgeIn.getV1().getX()) * pctAlong;
//		y1 = edgeIn.getV1().getY() + (edgeIn.getV2().getY() - edgeIn.getV1().getY()) * pctAlong;
//
//		if (V1.getZ() )
		throw new RuntimeException("Bad point reached in DefaultTriangle.getNextPoint");
//		return null;
	}

	public boolean contains(Coordinate pointOut) {
		CoordinateList coords = new CoordinateList();
		coords.add(getV1(), false);
		coords.add(getV2(), false);
		coords.add(getV3(), false);
		coords.add(getV1(), false);
		Point pOut = geomFactory.createPoint(pointOut);
		LinearRing linRing = geomFactory.createLinearRing(coords.toCoordinateArray());
		System.out.println("[" + getV1() + " - " + getV2() + " - " + getV3() + "] -> " + pointOut);
		return linRing.covers(pOut);
	}

}
