/*
 * Created on 27-sep-2006
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
 * $Id: SnapLineIntersector.java 13685 2007-09-13 18:02:35Z azabala $
 * $Log$
 * Revision 1.2  2007-09-13 18:02:35  azabala
 * changes to adapt to JTS 1.8
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
 * Revision 1.1  2006/10/05 19:20:57  azabala
 * first version in cvs
 *
 * Revision 1.1  2006/10/02 19:06:56  azabala
 * *** empty log message ***
 *
 *
 */
package com.vividsolutions.jts.operation.overlay;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.algorithm.RobustLineIntersector;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineSegment;

public class SnapLineIntersector extends RobustLineIntersector {

	double snapTolerance;

	public SnapLineIntersector(double snapTolerance) {
		super();
		this.snapTolerance = snapTolerance;
	}

	
	public void computeIntersection(Coordinate p, Coordinate p1, Coordinate p2) {
		isProper = false;

		LineSegment segment = new LineSegment(p1, p2);
		/*
		 * TODO SERIA MUY INTERESANTE QUE, ADEMAS DE VER SI EL PUNTO MAS PROXIMO
		 * DEL SEGMENTO AL PUNTO DADO ENTRA EN SNAP, VER TAMBIÉN EL REGISTRO DE
		 * NODOS (NODEMAP)????
		 * 
		 */
		Coordinate intersectionCandidate = segment.closestPoint(p);
		if (intersectionCandidate.distance(p) <= snapTolerance) {
			isProper = true;
			// verify if it is an extreme point
			if (intersectionCandidate.distance(p1) <= snapTolerance) {
				isProper = false;
				result = DO_INTERSECT;
				intPt[0] = p1;// we snaps to the point
			}

			if (intersectionCandidate.distance(p2) <= snapTolerance) {
				isProper = false;
				result = DO_INTERSECT;
				intPt[0] = p2;// we snaps to the point
			}

			result = DO_INTERSECT;
			intPt[0] = intersectionCandidate;
			return;
		}// distance < snap tolerance
		result = DONT_INTERSECT;
	}

	public int computeIntersect(Coordinate p1, Coordinate p2, Coordinate q1,
			Coordinate q2) {
		isProper = false;
		Envelope env1 = new Envelope(p1, p2);
		double newMinX = env1.getMinX() - snapTolerance;
		double newMaxX = env1.getMaxX() + snapTolerance;
		double newMinY = env1.getMinY() - snapTolerance;
		double newMaxY = env1.getMaxY() + snapTolerance;
		env1 = new Envelope(newMinX, newMaxX, newMinY, newMaxY);

		Envelope env2 = new Envelope(q1, q2);
		newMinX = env2.getMinX() - snapTolerance;
		newMaxX = env2.getMaxX() + snapTolerance;
		newMinY = env2.getMinY() - snapTolerance;
		newMaxY = env2.getMaxY() + snapTolerance;
		env2 = new Envelope(newMinX, newMaxX, newMinY, newMaxY);

		if (!env1.intersects(env2))
			return DONT_INTERSECT;

		/*
		 * Algoritmo para calcular interseccion de dos segmentos aplicando
		 * snapping
		 */

		// 1-vemos si intersectan sin necesidad de snap
		int test = super.computeIntersect(p1, p2, q1, q2);
		if (test != DONT_INTERSECT)
			return test;

		// 2-Vemos si son paralelos, y la distancia que los separa es inferior
		// a la de snap (si fuesen coincidentes ya habría sido detectado

		// condiciones de paralelismo en función del producto escalar
		// ver http://www.faqs.org/faqs/graphics/algorithms-faq/
		// distancia de punto a recta y de recta a recta
		double r_bot = (p2.x - p1.x) * (q2.y - q1.y) - (p2.y - p1.y)
				* (q2.x - q1.x);

		/*
		 * Ahora mismo no recuerdo el significado matemático de r_bot (es (Dx1 *
		 * Dy2) - (Dy1 * Dx2) siendo (Dx1,Dy1) (Dx2, Dy2) los vectores que se
		 * está tratando de ver si son paralelos...
		 * 
		 * Como no tengo claro qué es, la condición de snap va a ser la
		 * distancia de snap
		 */

		// boolean parallels = (r_bot==0);
		boolean parallels = (Math.abs(r_bot) <= snapTolerance);

		if (parallels) {
			// Son paralelos
			double distance1 = CGAlgorithms.distancePointLine(p1, q1, q2);
			double distance2 = CGAlgorithms.distancePointLine(p2, q1, q2);
			double distance = Math.min(distance1, distance2);
			if (distance <= snapTolerance)
				return computeCollinearIntersection(p1, p2, q1, q2);
			else
				return DONT_INTERSECT;
		}

		// Otro intento. Probamos a intersectar cada uno de los vertices de la
		// linea de entrada
		computeIntersection(p1, q1, q2);
		if (this.hasIntersection()) {
			return result;
		}
		computeIntersection(p2, q1, q2);
		if (this.hasIntersection()) {
			return result;
		}

		// Ultimo intento. Obtenemos el punto mas cercano de un segmento
		// a los dos vertices del otro, y verificamos la distancia de snap

		LineSegment s1 = new LineSegment(p1, p2);
		Coordinate candidate1 = s1.closestPoint(q1);
		Coordinate candidate2 = s1.closestPoint(q2);
		double d1 = q1.distance(candidate1);
		double d2 = q2.distance(candidate2);
		if (d1 < snapTolerance) {
			isProper = false;
			intPt[0] = candidate1;// we snap to the point
			return DO_INTERSECT;
		}
		if (d2 < snapTolerance) {
			isProper = false;
			intPt[0] = candidate2;// we snap to the point
			return DO_INTERSECT;
		}
		return DONT_INTERSECT;
	}

	/**
	 * Returns t param for point P of vector AB in the parametric line equation
	 * P = t*AB
	 * 
	 * @param p
	 * @param A
	 * @param B
	 */
	private double getParametrizedLineFactor(Coordinate p, Coordinate A,
			Coordinate B) {
		double r = ((p.x - A.x) * (B.x - A.x) + (p.y - A.y) * (B.y - A.y))
				/ ((B.x - A.x) * (B.x - A.x) + (B.y - A.y) * (B.y - A.y));
		return r;
	}

	/*
	 * Computes collinear intersections, but instead of considering envelope
	 * intersections, it applies parametrized line equations (ideal to snap)
	 */
	private int computeCollinearIntersection(Coordinate p1, Coordinate p2,
			Coordinate q1, Coordinate q2) {

		/*
		 * TODO Estamos teniendo un problema: La clase SegmentIntersector hace
		 * el siguiente uso de computeCollinearIntersection:
		 * 
		 * li.computeIntersection(p00, p01, p10, p11); if (li.hasIntersection()) {
		 * numIntersections++; if (! isTrivialIntersection(e0, segIndex0, e1,
		 * segIndex1)) { hasIntersection = true; if (includeProper || !
		 * li.isProper() ) { e0.addIntersections(li, segIndex0, 0);
		 * e1.addIntersections(li, segIndex1, 1); } if (li.isProper()) {
		 * properIntersectionPoint = (Coordinate) li.getIntersection(0).clone();
		 * hasProper = true; if (! isBoundaryPoint(li, bdyNodes))
		 * hasProperInterior = true; }
		 * 
		 * Y esto aparece comentado: //if (li.isCollinear()) //hasCollinear =
		 * true;
		 * 
		 * En estas circunstancias no se está recuperando el segundo punto de la
		 * intersección colineal, ni se verifica que es una intersección
		 * colineal
		 * 
		 */

		/*
		 * TODO Otro problema: a la hora de calcular la intersección entre dos
		 * segmentos colineales, definir bien CUAL VA A SNAPEAR SOBRE CUAL.
		 * 
		 * Es decir, en grafoA.computeIntersect(grafoB), las coordenadas del
		 * GeometryGraph B se moverán a las líneas del GeometryGraph B
		 * 
		 * ESTO NO ES TAN IMPORTANTE COMO SNAPEAR EdgeIntersection y Coordinate
		 * de un Edge
		 * 
		 */

		/*
		 * We compute params of p1 and p2 point in q1q2 parametric line equation
		 */

		// REHACER ESTO
		// DADAS DOS LINEAS PARALELAS, SI ESTÁN A UNA DISTANCIA (LAS LINEAS)
		// INTERIOR A LA DE SNAP, HAY QUE CONSIDERARLAS COLINEALES..........
		// PERO ES MUY IMPORTANTE SNAPEAR P1 CON Q1-Q2 Y P2 CON Q1-Q2 PARA QUE
		// NO SALGAN COSAS RARAS (EDGEINTERSECTION NO PROPIAS)
		//
		// TAMBIEN HAY QUE DEFINIR UNA POLITICA DE SNAP:
		// QUE LINEA SE MANTIENE Y QUE LINEA SE MUEVE
		// (NO HACER ARBITRARIAMENTE)
		double rP1 = getParametrizedLineFactor(p1, q1, q2);
		double rP2 = getParametrizedLineFactor(p2, q1, q2);

		if (rP1 <= 0 && rP2 <= 0) {
			// p1---p2--q1----q2
			intPt[0] = q1;
			return DO_INTERSECT;
		}

		if (rP1 <= 0 && ((rP2 > 0 && rP2 <= 1))) {
			// p1---q1---p2----q2
			isProper = false;
			intPt[0] = q1;
			intPt[1] = p2;
			return COLLINEAR;
		}

		if ((rP1 > 0 && rP1 <= 1) && (rP2 > 0 && rP2 <= 1)) {
			// p1--q1---q2---p2
			intPt[0] = q1;
			intPt[1] = q2;
			return COLLINEAR;
		}

		if ((rP1 > 0 && rP1 <= 1) && (rP2 > 1)) {
			// q1--p1--q2--p2
			intPt[0] = p1;
			intPt[1] = q2;
			return COLLINEAR;

		}

		if ((rP1 < 0) && (rP2 > 1)) {
			// p1---q1---q2--p2
			intPt[0] = q1;
			intPt[1] = q2;
			return COLLINEAR;
		}

		if ((rP1 > 1) && (rP2 > 1)) {
			// q1--q2--p1--p2
			intPt[0] = q2;
			return DO_INTERSECT;
		}
		return DONT_INTERSECT;
	}

	/**
	 * Test whether a point lies in the envelopes of both input segments. A
	 * correctly computed intersection point should return <code>true</code>
	 * for this test. Since this test is for debugging purposes only, no attempt
	 * is made to optimize the envelope test.
	 * 
	 * @return <code>true</code> if the input point lies within both input
	 *         segment envelopes
	 */
	private boolean isInSegmentEnvelopes(Coordinate intPt) {
		Envelope env0 = new Envelope(inputLines[0][0], inputLines[0][1]);
		Envelope env1 = new Envelope(inputLines[1][0], inputLines[1][1]);

		Envelope snapEnvelope = new Envelope(intPt.x - snapTolerance, intPt.x
				+ snapTolerance, intPt.y - snapTolerance, intPt.y
				+ snapTolerance);
		// TODO Review if we must chech for intersections of containtment
		return env0.intersects(snapEnvelope) && env1.intersects(snapEnvelope);
		// return env0.contains(intPt) && env1.contains(intPt);
	}

	/***************************************************************************
	 * ********************************* Overwrited methods of LineIntersector
	 * ********************************* *********************************
	 */

	/**
	 * Test whether a point is a intersection point of two line segments. Note
	 * that if the intersection is a line segment, this method only tests for
	 * equality with the endpoints of the intersection segment. It does <b>not</b>
	 * return true if the input point is internal to the intersection segment.
	 * 
	 * @return true if the input point is one of the intersection points.
	 */
	public boolean isIntersection(Coordinate pt) {
		for (int i = 0; i < result; i++) {
			if (intPt[i].distance(pt) <= snapTolerance) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests whether either intersection point is an interior point of the
	 * specified input segment.
	 * 
	 * @return <code>true</code> if either intersection point is in the
	 *         interior of the input segment
	 */
	public boolean isInteriorIntersection(int inputLineIndex) {
		for (int i = 0; i < result; i++) {
			if (!(intPt[i].distance(inputLines[inputLineIndex][0]) <= snapTolerance || intPt[i]
					.distance(inputLines[inputLineIndex][1]) <= snapTolerance)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * TODO Estos dos metodos finales hacen uso de static getEdgeDistance, que
	 * se ha hecho sin tener en cuenta ningún tipo de snap o redondeo.
	 * 
	 * No se en que puede afectar (comprobar)
	 * 
	 * 
	 */
	protected void computeIntLineIndex(int segmentIndex) {
		double dist0 = getEdgeDistance(segmentIndex, 0);
		double dist1 = getEdgeDistance(segmentIndex, 1);
		if (dist0 > dist1) {
			intLineIndex[segmentIndex][0] = 0;
			intLineIndex[segmentIndex][1] = 1;
		} else {
			intLineIndex[segmentIndex][0] = 1;
			intLineIndex[segmentIndex][1] = 0;
		}
	}

	/**
	 * Computes the "edge distance" of an intersection point along the specified
	 * input line segment.
	 * 
	 * @param segmentIndex
	 *            is 0 or 1
	 * @param intIndex
	 *            is 0 or 1
	 * 
	 * @return the edge distance of the intersection point
	 */
	public double getEdgeDistance(int segmentIndex, int intIndex) {
		double dist = computeEdgeDistance(intPt[intIndex],
				inputLines[segmentIndex][0], inputLines[segmentIndex][1]);
		return dist;
	}

}
