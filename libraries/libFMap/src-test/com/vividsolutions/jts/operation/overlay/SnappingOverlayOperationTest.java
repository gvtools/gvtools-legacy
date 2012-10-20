/*
 * Created on 03-oct-2006
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
 * $Id: SnappingOverlayOperationTest.java 13233 2007-08-22 07:54:39Z cesar $
 * $Log$
 * Revision 1.1  2007-08-22 07:54:39  cesar
 * Move tests to src-test
 *
 * Revision 1.2  2007/03/06 17:08:59  caballero
 * Exceptions
 *
 * Revision 1.1  2006/12/04 19:30:23  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/19 16:06:48  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/17 18:25:53  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/10/10 18:50:57  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/10/09 19:10:56  azabala
 * First version in CVS
 *
 * Revision 1.1  2006/10/05 19:20:57  azabala
 * first version in cvs
 *
 *
 */
package com.vividsolutions.jts.operation.overlay;

import junit.framework.TestCase;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class SnappingOverlayOperationTest extends TestCase {

	// test 1
	Geometry a, b;
	GeometryFactory factory;
	com.vividsolutions.jts.io.WKTReader reader;

	// test 2
	Geometry c, d;

	// test 4
	Geometry f;

	// test 5
	Geometry g, h;

	// test 6
	Geometry pol1, pol2;

	public static void main(String[] args) {
	}

	public SnappingOverlayOperationTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		factory = new GeometryFactory();
		reader = new com.vividsolutions.jts.io.WKTReader(factory);
		a = reader.read("LINESTRING(0.001 0.001, 5.001 5.001)");
		b = reader
				.read("LINESTRING(2.1 -3, 0.0 -0.001, -2.22 4.88, 10.0 10.0, 5.002 5.002)");

		c = reader.read("LINESTRING(0 0, 5 0, 10 0.001)");
		d = reader.read("LINESTRING(0 0.01, 5 0.002, 10 0.002)");

		f = reader.read("LINESTRING(0 0.11, 5 0.12, 10 0.14)");

		g = reader.read("LINESTRING(1 0, 3 2)");
		h = reader.read("LINESTRING(3.05 2.01, 5 1.25, 0.25 1.75)");

		pol1 = reader
				.read("POLYGON((0 0, -5 0, -10 5, 0 10,  10 5, 5 0, 0 0))");
		pol2 = reader.read("POLYGON((10.01 0, 5 5, 5 10, 10 10, 10.01 0))");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		factory = null;
		reader = null;
		a = null;
		b = null;
	}

	/*
	 * Test method for
	 * 'com.iver.cit.gvsig.fmap.topology.SnappingOverlayOperation.SnappingOverlayOperation(Geometry,
	 * Geometry, double)'
	 */
	public void testSnappingOverlayOperation() {
		// test 1: dos lineas que intersectan en dos puntos: uno Nodo y otro
		// vertice
		Geometry geom = SnappingOverlayOperation.overlayOp(a, b,
				OverlayOp.INTERSECTION, 0.01);
		assertTrue(geom.toString().equals(
				"MULTIPOINT (0.001 0.001, 5.001 5.001)"));

		// test 2: dos lineas paralelas separadas por una distancia inferior a
		// la
		// de snap
		geom = SnappingOverlayOperation.overlayOp(c, d, OverlayOp.INTERSECTION,
				0.1);

		assertTrue(geom.toString().equals(
				"MULTILINESTRING ((0 0, 5 0), (5 0, 10 0.001))"));

		// test 3: identicas líneas pero reducimos la distancia de snap
		geom = SnappingOverlayOperation.overlayOp(c, d, OverlayOp.INTERSECTION,
				0.0001);
		assertTrue(geom.toString().equals("GEOMETRYCOLLECTION EMPTY"));

		// test 4: identicas líneas, pero se separan un poco mas
		geom = SnappingOverlayOperation.overlayOp(c, f, OverlayOp.INTERSECTION,
				0.1);
		assertTrue(geom.toString().equals("GEOMETRYCOLLECTION EMPTY"));

		// test 5: interseccion de dos lineas, la solucion son dos puntos:
		// uno con snap y el otro normal. Presentan cambios de orientacion
		geom = SnappingOverlayOperation.overlayOp(g, h, OverlayOp.INTERSECTION,
				0.1);
		assertTrue(geom instanceof GeometryCollection);
		assertTrue(geom.toString().equals(
				"MULTIPOINT (2.511904761904762 1.5119047619047619, 3 2)"));

		geom = SnappingOverlayOperation.overlayOp(pol1, pol2,
				OverlayOp.INTERSECTION, 0.01);
		assertTrue(geom instanceof Polygon);
		assertTrue(geom
				.toString()
				.equals("POLYGON ((5 7.5, 10 5, 7.502497502497502 2.5024975024975022, 5 5, 5 7.5))"));

	}

}
