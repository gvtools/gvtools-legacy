/*
 * Created on 28-sep-2006
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
 * $Id: NonRobustSnapLineIntersector.java 9178 2006-12-04 19:30:23Z azabala $
 * $Log$
 * Revision 1.1  2006-12-04 19:30:23  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/19 16:06:48  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/17 18:25:53  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/05 19:20:57  azabala
 * first version in cvs
 *
 * Revision 1.1  2006/10/02 19:06:56  azabala
 * *** empty log message ***
 *
 *
 */
package com.vividsolutions.jts.operation.overlay;

import com.vividsolutions.jts.algorithm.NonRobustLineIntersector;

public class NonRobustSnapLineIntersector extends NonRobustLineIntersector {

	/**
	 * @return true if both numbers are positive or if both numbers are
	 *         negative. Returns false if both numbers are zero.
	 */
	public static boolean isSameSignAndNonZero(double a, double b) {
		if (a == 0 || b == 0) {
			return false;
		}
		return (a < 0 && b < 0) || (a > 0 && b > 0);
	}

	public NonRobustSnapLineIntersector() {
	}

}
