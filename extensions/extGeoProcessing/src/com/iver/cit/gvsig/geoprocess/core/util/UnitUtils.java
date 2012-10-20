/*
 * Created on 29-jun-2006
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
 * $Id: UnitUtils.java 20335 2008-04-24 09:04:23Z vcaballero $
 * $Log$
 * Revision 1.11  2007-09-19 16:02:53  jaume
 * removed unnecessary imports
 *
 * Revision 1.10  2007/08/07 15:07:39  azabala
 * removed dependencies from andami
 *
 * Revision 1.9  2007/03/06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.8  2007/02/28 13:31:37  azabala
 * solved bug with units (related with jcrs)
 *
 * Revision 1.7  2007/01/12 13:23:22  caballero
 * unidades de medida
 *
 * Revision 1.6  2006/12/22 09:02:03  caballero
 * Tener en cuenta tanto las unidades reales como las de medida
 *
 * Revision 1.5  2006/12/21 18:25:30  azabala
 * bug fixed (change getMapUnits with getDistanceUnits)
 *
 * Revision 1.4  2006/09/15 10:42:54  caballero
 * extensibilidad de documentos
 *
 * Revision 1.3  2006/08/29 07:56:30  cesar
 * Rename the *View* family of classes to *Window* (ie: SingletonView to SingletonWindow, ViewInfo to WindowInfo, etc)
 *
 * Revision 1.2  2006/08/29 07:21:09  cesar
 * Rename com.iver.cit.gvsig.fmap.Fmap class to com.iver.cit.gvsig.fmap.MapContext
 *
 * Revision 1.1  2006/06/29 17:30:36  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.util;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;

import com.iver.cit.gvsig.fmap.MapContext;

/**
 * Utility methods to manage distances (conversion from user entries to internal
 * units, verifying if projection is planar or we are working with geographic
 * coordinates, etc.)
 * 
 * @author azabala
 * 
 */
public class UnitUtils {

	public static final double EARTH_RADIUS = 6378137d;

	/**
	 * For computing with geodetic coordinates: returns the angular measure (in
	 * radians) for a distance over the earth along a meridiam. Because this
	 * consideration is an approximation, we consideer the eart like an sphere
	 * (not an ellipsoid)
	 * 
	 * @param dist
	 *            distance in meters
	 * @return arc of meridian whose length is the specified distance
	 */
	public static double toSexaAngularMeasure(double dist) {
		/*
		 * dist = 6378km(terrestrial radius) -> 2PI passed distance -> incognite
		 */
		return Math.toDegrees((2 * Math.PI * dist) / EARTH_RADIUS);
	}

	/**
	 * Converts a distance entered by user in the GUI in the same distance in
	 * internal units (measure units)
	 * 
	 * */
	public static double getInInternalUnits(double userEntryDistance,
			CoordinateReferenceSystem crs, int distanceUnits, int mapUnits) {
		// VCN he modificado esto a como creo que debería de estar,
		// así tiene en cuenta para calcular la distancia tanto las unidades
		// en las que se encuentra la cartografía como las unidades de medida
		// seleccionadas por el usuario.
		double[] trans2Meter = MapContext.getDistanceTrans2Meter();
		double distInInternalUnits = (userEntryDistance / trans2Meter[mapUnits])
				* trans2Meter[distanceUnits];

		/*
		 * if layer's projections is in geographics coords, pass distance to a
		 * angular measure
		 */

		if ((crs != null) && !(crs instanceof ProjectedCRS)) {
			distInInternalUnits = UnitUtils
					.toSexaAngularMeasure(distInInternalUnits);
		}
		return distInInternalUnits;
	}

}
