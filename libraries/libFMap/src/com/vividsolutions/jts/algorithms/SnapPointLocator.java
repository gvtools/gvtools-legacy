/*
 * Created on 06-oct-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
/* CVS MESSAGES:
 *
 * $Id: SnapPointLocator.java 13685 2007-09-13 18:02:35Z azabala $
 * $Log$
 * Revision 1.3  2007-09-13 17:59:04  azabala
 * changes to adapt to JTS 1.8
 *
 * Revision 1.2  2007/03/06 17:08:55  caballero
 * Exceptions
 *
 * Revision 1.1  2006/12/04 19:29:31  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/17 18:25:53  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/09 19:10:56  azabala
 * First version in CVS
 *
 *
 */
package com.vividsolutions.jts.algorithms;

import java.util.Iterator;

import com.vividsolutions.jts.algorithm.BoundaryNodeRule;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryCollectionIterator;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Location;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class SnapPointLocator extends
		com.vividsolutions.jts.algorithm.PointLocator {

	private BoundaryNodeRule boundaryRule = BoundaryNodeRule.ENDPOINT_BOUNDARY_RULE; // OGC_SFS_BOUNDARY_RULE;

	private boolean isIn; // true if the point lies in or on any Geometry

	// element

	private int numBoundaries; // the number of sub-elements whose boundaries

	// the point lies in

	public SnapPointLocator() {
	}

	public SnapPointLocator(BoundaryNodeRule boundaryRule) {
		if (boundaryRule == null)
			throw new IllegalArgumentException("Rule must be non-null");
		this.boundaryRule = boundaryRule;
	}

	/**
	 * Convenience method to test a point for intersection with a Geometry
	 * 
	 * @param p
	 *            the coordinate to test
	 * @param geom
	 *            the Geometry to test
	 * @return <code>true</code> if the point is in the interior or boundary of
	 *         the Geometry
	 */
	public boolean intersects(Coordinate p, Geometry geom, double snapTolerance) {
		return locate(p, geom, snapTolerance) != Location.EXTERIOR;
	}

	/**
	 * Computes the topological relationship ({@link Location}) of a single
	 * point to a Geometry. It handles both single-element and multi-element
	 * Geometries. The algorithm for multi-part Geometries takes into account
	 * the SFS Boundary Determination Rule.
	 * 
	 * @return the {@link Location} of the point relative to the input Geometry
	 */
	public int locate(Coordinate p, Geometry geom, double snapTolerance) {
		if (geom.isEmpty())
			return Location.EXTERIOR;

		if (geom instanceof LinearRing) {
			return locate(p, (LinearRing) geom, snapTolerance);
		}
		if (geom instanceof LineString) {
			return locate(p, (LineString) geom, snapTolerance);
		} else if (geom instanceof Polygon) {
			return locate(p, (Polygon) geom, snapTolerance);
		}
		isIn = false;
		numBoundaries = 0;
		computeLocation(p, geom, snapTolerance);

		// if (SnappingGeometryGraph.isInBoundary(numBoundaries))
		// return Location.BOUNDARY;

		if (boundaryRule.isInBoundary(numBoundaries))
			return Location.BOUNDARY;

		if (numBoundaries > 0 || isIn)
			return Location.INTERIOR;
		return Location.EXTERIOR;
	}

	private void computeLocation(Coordinate p, Geometry geom,
			double snapTolerance) {
		if (geom instanceof LinearRing) {
			updateLocationInfo(locate(p, (LinearRing) geom, snapTolerance));
		}
		if (geom instanceof LineString) {
			updateLocationInfo(locate(p, (LineString) geom, snapTolerance));
		} else if (geom instanceof Polygon) {
			updateLocationInfo(locate(p, (Polygon) geom, snapTolerance));
		} else if (geom instanceof MultiLineString) {
			MultiLineString ml = (MultiLineString) geom;
			for (int i = 0; i < ml.getNumGeometries(); i++) {
				LineString l = (LineString) ml.getGeometryN(i);
				updateLocationInfo(locate(p, l, snapTolerance));
			}
		} else if (geom instanceof MultiPolygon) {
			MultiPolygon mpoly = (MultiPolygon) geom;
			for (int i = 0; i < mpoly.getNumGeometries(); i++) {
				Polygon poly = (Polygon) mpoly.getGeometryN(i);
				updateLocationInfo(locate(p, poly, snapTolerance));
			}
		} else if (geom instanceof GeometryCollection) {
			Iterator geomi = new GeometryCollectionIterator(
					(GeometryCollection) geom);
			while (geomi.hasNext()) {
				Geometry g2 = (Geometry) geomi.next();
				if (g2 != geom)
					computeLocation(p, g2, snapTolerance);
			}
		}
	}

	private void updateLocationInfo(int loc) {
		if (loc == Location.INTERIOR)
			isIn = true;
		if (loc == Location.BOUNDARY)
			numBoundaries++;
	}

	private int locate(Coordinate p, LineString l, double snapTolerance) {
		Coordinate[] pt = l.getCoordinates();
		if (!l.isClosed()) {
			if (p.distance(pt[0]) <= snapTolerance
					|| p.distance(pt[pt.length - 1]) <= snapTolerance) {
				return Location.BOUNDARY;
			}
		}
		if (SnapCGAlgorithms.isOnLine(p, pt, snapTolerance))
			return Location.INTERIOR;
		return Location.EXTERIOR;
	}

	private int locate(Coordinate p, LinearRing ring, double snapTolerance) {
		// can this test be folded into isPointInRing ?
		if (SnapCGAlgorithms.isOnLine(p, ring.getCoordinates())) {
			return Location.BOUNDARY;
		}
		if (SnapCGAlgorithms.isPointInRing(p, ring.getCoordinates(),
				snapTolerance))
			return Location.INTERIOR;
		return Location.EXTERIOR;
	}

	private int locate(Coordinate p, Polygon poly, double snapTolerance) {
		if (poly.isEmpty())
			return Location.EXTERIOR;
		LinearRing shell = (LinearRing) poly.getExteriorRing();

		int shellLoc = locate(p, shell, snapTolerance);
		if (shellLoc == Location.EXTERIOR)
			return Location.EXTERIOR;
		if (shellLoc == Location.BOUNDARY)
			return Location.BOUNDARY;
		// now test if the point lies in or on the holes
		for (int i = 0; i < poly.getNumInteriorRing(); i++) {
			LinearRing hole = (LinearRing) poly.getInteriorRingN(i);
			int holeLoc = locate(p, hole, snapTolerance);
			if (holeLoc == Location.INTERIOR)
				return Location.EXTERIOR;
			if (holeLoc == Location.BOUNDARY)
				return Location.BOUNDARY;
		}
		return Location.INTERIOR;
	}
}
