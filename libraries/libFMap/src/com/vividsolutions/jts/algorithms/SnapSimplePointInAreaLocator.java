/*
 * Created on 06-oct-2006
 *
 * gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
 * $Id: SnapSimplePointInAreaLocator.java 24730 2008-11-04 12:07:00Z vcaballero $
 * $Log$
 * Revision 1.2  2007-03-06 17:08:55  caballero
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

import com.vividsolutions.jts.algorithm.locate.SimplePointInAreaLocator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryCollectionIterator;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Location;
import com.vividsolutions.jts.geom.Polygon;

public class SnapSimplePointInAreaLocator extends SimplePointInAreaLocator {
	public SnapSimplePointInAreaLocator(Geometry geom) {
		super(geom);
		// TODO Auto-generated constructor stub
	}

	public static int locate(Coordinate p, Geometry geom, double snapTolerance) {
		if (geom.isEmpty())
			return Location.EXTERIOR;

		if (containsPoint(p, geom, snapTolerance))
			return Location.INTERIOR;
		return Location.EXTERIOR;
	}

	private static boolean containsPoint(Coordinate p, Geometry geom,
			double snapTolerance) {
		if (geom instanceof Polygon) {
			return containsPointInPolygon(p, (Polygon) geom);
		} else if (geom instanceof GeometryCollection) {
			Iterator geomi = new GeometryCollectionIterator(
					(GeometryCollection) geom);
			while (geomi.hasNext()) {
				Geometry g2 = (Geometry) geomi.next();
				if (g2 != geom)
					if (containsPoint(p, g2, snapTolerance))
						return true;
			}
		}
		return false;
	}

	public static boolean containsPointInPolygon(Coordinate p, Polygon poly,
			double snapTolerance) {
		if (poly.isEmpty())
			return false;
		LinearRing shell = (LinearRing) poly.getExteriorRing();
		if (!SnapCGAlgorithms.isPointInRing(p, shell.getCoordinates(),
				snapTolerance))
			return false;
		// now test if the point lies in or on the holes
		for (int i = 0; i < poly.getNumInteriorRing(); i++) {
			LinearRing hole = (LinearRing) poly.getInteriorRingN(i);
			if (SnapCGAlgorithms.isPointInRing(p, hole.getCoordinates(),
					snapTolerance))
				return false;
		}
		return true;
	}

}
