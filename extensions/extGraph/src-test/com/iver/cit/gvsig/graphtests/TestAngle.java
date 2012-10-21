/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
package com.iver.cit.gvsig.graphtests;

import junit.framework.TestCase;

import org.gvsig.graph.core.TurnUtil;

import com.vividsolutions.jts.geom.Coordinate;

public class TestAngle extends TestCase {

	public void testAngle() {
		Coordinate c1, c2, c3;
		double grados;

		// 0 grados (no hay giro
		c1 = new Coordinate(-1, 0);
		c2 = new Coordinate(0, 0);
		c3 = new Coordinate(1, 0);
		grados = TurnUtil.angle(c1, c2, c3);
		assertEquals(0.0, grados, 0.0);

		// 90 grados (giro a la izquierda)
		c1 = new Coordinate(0, -1);
		c2 = new Coordinate(0, 0);
		c3 = new Coordinate(-1, 0);
		grados = TurnUtil.angle(c1, c2, c3);
		assertEquals(90.0, grados, 0.0);

		// 180 grados (360 grados) giro completo
		c1 = new Coordinate(-1, 0);
		c2 = new Coordinate(0, 0);
		c3 = new Coordinate(-1, 0);
		grados = TurnUtil.angle(c1, c2, c3);
		assertEquals(180.0, grados, 0.0);

		// 270 grados (giro a la derecha)
		c1 = new Coordinate(-1, 0);
		c2 = new Coordinate(0, 0);
		c3 = new Coordinate(0, 1);
		grados = TurnUtil.angle(c1, c2, c3);
		assertEquals(270.0, grados, 0.0);

	}
}
