/*
 * Created on 23-abr-2007
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
 * $Id: JTSFacade.java 12946 2007-08-07 15:08:11Z azabala $
 * $Log$
 * Revision 1.1  2007-08-07 15:08:11  azabala
 * new version in cvs. centralizes all jts stuff
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.util;

import com.iver.cit.gvsig.fmap.core.FShape;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.precision.EnhancedPrecisionOp;

/**
 * 
 * Instead of calling individual methods of JTS (JTS Topology Suite), this
 * singleton facade allows gvSIG users to centralize all JTS computational
 * geometry operations in one only class.
 * 
 * This is useful to ensure certain operations (for example, geoprocessing
 * operations could cause robustness problems, so JTS allows to use enhanced
 * precission operations, this facade forces to use these enhanced precision
 * operations)
 * 
 * @author alvaro zabala
 * 
 * 
 * */
public class JTSFacade {

	public static Geometry computeBuffer(Geometry originalGeometry,
			double distance) {
		return EnhancedPrecisionOp.buffer(originalGeometry, distance);
	}

	public static Geometry difference(Geometry geom1, Geometry geom2) {
		return EnhancedPrecisionOp.difference(geom1, geom2);
	}

	public static Geometry symDifference(Geometry geom1, Geometry geom2) {
		return EnhancedPrecisionOp.symDifference(geom1, geom2);
	}

	public static Geometry union(Geometry geom1, Geometry geom2) {
		return EnhancedPrecisionOp.union(geom1, geom2);
	}

	public static Geometry union(Geometry[] geomArray, int geometryType) {
		if (geomArray.length == 0)
			return null;
		if (geomArray.length == 1)
			return geomArray[0];
		if (geometryType == FShape.POLYGON || geometryType == FShape.CIRCLE
				|| geometryType == FShape.ELLIPSE) {
			GeometryFactory fact = geomArray[0].getFactory();
			Geometry geomCol = fact.createGeometryCollection(geomArray);
			return computeBuffer(geomCol, 0d);
		} else {
			Geometry unionResult = geomArray[0];
			for (int i = 1; i < geomArray.length; i++)
				unionResult = union(unionResult, geomArray[i]);
			return unionResult;
		}
	}

	public static Geometry intersection(Geometry geom1, Geometry geom2) {
		return EnhancedPrecisionOp.intersection(geom1, geom2);
	}

	/**
	 * Checks a JTS geometry to be null or NIL. NIL in jts is used to represent
	 * particular cases of geometries. (for example, an interior buffer that
	 * collapses a geometry). NIL is managed with a zero lenght
	 * GeometryCollection
	 * 
	 * @param geometry
	 * @return
	 */
	public static boolean checkNull(Geometry geometry) {
		if (geometry == null)
			return true;
		if (geometry instanceof GeometryCollection) {
			GeometryCollection col = (GeometryCollection) geometry;
			if (col.getNumGeometries() < 1)
				return true;
		}
		return false;
	}

}
