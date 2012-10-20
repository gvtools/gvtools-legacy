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

import org.gvsig.fmap.algorithm.triangulation.Vertex;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

public class TriEdge extends LineSegment {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TriEdge(Vertex v1, Vertex v2) {
		super(v1, v2);
	}

	public Vertex getV1() {
		return (Vertex) p0;
	}

	public Vertex getV2() {
		return (Vertex) p1;
	}

	public boolean containsZ(double z) {
		if ((getV1().getZ() <= z) && (getV2().getZ() >= z))
			return true;
		if ((getV1().getZ() >= z) && (getV2().getZ() <= z))
			return true;
		return false;
	}

	/**
	 * Intersects plane z=Z0 with 3D segment. Returns coordinate of
	 * intersection. Returns null if there is not intersection
	 * 
	 * @param height
	 * @return
	 */
	public Coordinate getPointOnZ(double Z0) {
		double xI, yI;
		boolean bContainsZ = false;
		if (getV1().getZ() == Z0)
			return getV1();
		if (getV2().getZ() == Z0)
			return getV2();

		if ((getV1().getZ() <= Z0) && (Z0 <= getV2().getZ()))
			bContainsZ = true;
		if ((getV1().getZ() >= Z0) && (Z0 >= getV2().getZ()))
			bContainsZ = true;
		if (!bContainsZ)
			return null;

		double pctAlong = (Z0 - getV1().getZ())
				/ (getV2().getZ() - getV1().getZ());

		xI = getV1().getX() + (getV2().getX() - getV1().getX()) * pctAlong;
		yI = getV1().getY() + (getV2().getY() - getV1().getY()) * pctAlong;

		return new Coordinate(xI, yI, Z0);
	}

}
