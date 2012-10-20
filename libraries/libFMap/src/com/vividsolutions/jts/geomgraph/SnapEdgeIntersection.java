/*
 * Created on 02-oct-2006
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
 * $Id: SnapEdgeIntersection.java 10627 2007-03-06 17:10:21Z caballero $
 * $Log$
 * Revision 1.2  2007-03-06 17:08:55  caballero
 * Exceptions
 *
 * Revision 1.1  2006/12/04 19:30:23  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/17 18:25:53  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/05 19:20:57  azabala
 * first version in cvs
 *
 * Revision 1.1  2006/10/02 19:06:39  azabala
 * *** empty log message ***
 *
 *
 */
package com.vividsolutions.jts.geomgraph;

import com.vividsolutions.jts.geom.Coordinate;

public class SnapEdgeIntersection extends EdgeIntersection {

	public SnapEdgeIntersection(Coordinate coord, int segmentIndex, double dist) {
		super(coord, segmentIndex, dist);
	}

	public int compareTo(Object obj) {
		SnapEdgeIntersection other = (SnapEdgeIntersection) obj;
		return compare(other.segmentIndex, other.dist);
	}

	/**
	 * @return -1 this EdgeIntersection is located before the argument location
	 * @return 0 this EdgeIntersection is at the argument location
	 * @return 1 this EdgeIntersection is located after the argument location
	 */
	public int compare(int segmentIndex, double dist) {
		// TODO VER SI METER DISTANCIA DE SNAP
		if (this.segmentIndex < segmentIndex)
			return -1;
		if (this.segmentIndex > segmentIndex)
			return 1;
		if (this.dist < dist)
			return -1;
		if (this.dist > dist)
			return 1;
		return 0;
	}

	public boolean isEndPoint(int maxSegmentIndex) {
		// TODO VER SI METER DISTANCIA DE SNAP
		if (segmentIndex == 0 && dist == 0.0)
			return true;
		if (segmentIndex == maxSegmentIndex)
			return true;
		return false;
	}
}
