/*
 * Created on 08-jul-2006
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
 * $Id: GeometryUtil.java 6487 2006-07-21 09:08:57Z azabala $
 * $Log$
 * Revision 1.1  2006-07-21 09:08:57  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.util;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class GeometryUtil {

	private static GeometryFactory fact = new GeometryFactory();

	public static Polygon removeDuplicates(Polygon polygon) {
		LineString shell = polygon.getExteriorRing();
		LineString newShell = removeDuplicates(shell);
		LinearRing[] holes = new LinearRing[polygon.getNumInteriorRing()];
		for (int i = 0; i < holes.length; i++) {
			holes[i] = (LinearRing) polygon.getInteriorRingN(i);
		}
		Polygon newPolygon = fact.createPolygon((LinearRing) newShell, holes);
		return newPolygon;
	}

	public static MultiPolygon removeDuplicates(MultiPolygon multiPolygon) {
		Polygon[] pols = new Polygon[multiPolygon.getNumGeometries()];
		for (int i = 0; i < pols.length; i++) {
			Polygon pol = (Polygon) multiPolygon.getGeometryN(i);
			Polygon newPol = removeDuplicates(pol);
			pols[i] = newPol;
		}
		return fact.createMultiPolygon(pols);
	}

	public static Geometry removeDuplicatesFrom(Geometry geometry) {
		Geometry solution = null;
		if (geometry instanceof LineString)
			solution = removeDuplicates((LineString) geometry);
		else if (geometry instanceof Polygon)
			solution = removeDuplicates((Polygon) geometry);
		else if (geometry instanceof MultiPolygon)
			solution = removeDuplicates((MultiPolygon) geometry);
		else
			solution = geometry;
		return solution;
	}

	public static LineString removeDuplicates(LineString line) {
		ArrayList coordinates = new ArrayList();
		Coordinate prevCoord = null;
		Coordinate actualCoord = null;
		int numPoints = line.getNumPoints();
		for (int i = 0; i < numPoints; i++) {
			actualCoord = line.getCoordinateN(i);
			if (prevCoord != null) {
				if (!prevCoord.equals2D(actualCoord)) {
					coordinates.add(actualCoord);
				}
			} else {
				coordinates.add(actualCoord);
			}
			prevCoord = actualCoord;
		}// for
			// llegados a este punto, construimos la nueva geometria
		Coordinate[] newCoord = new Coordinate[coordinates.size()];
		coordinates.toArray(newCoord);
		if (line instanceof LineString) {
			return fact.createLineString(newCoord);
		} else {
			return fact.createLinearRing(newCoord);
		}

	}

	/**
	 * Elimina los puntos colineales de una linea. Tiene en cuenta una
	 * tolerancia dada en radianes, de manera que si dos segmentos contiguos de
	 * la linea forman un angulo menor o igual que esa tolerancia, se
	 * consideraran colineales sus puntos. Por tanto, cuanto mayor sea la
	 * tolerancia, mas puntos se eliminaran.
	 * 
	 * @param linea
	 *            Linea original.
	 * @param toleranciaAngulo
	 *            Tolerancia en radianes.
	 * @return Una nueva linea filtrada sin los puntos colineales.
	 */
	// public static Linea eliminarColineales(Linea linea, double
	// toleranciaAngulo)
	// {
	// // linea de dos puntos: no hay nada que filtrar
	// if (linea.getNumPuntos() < 3)
	// {
	// try {
	// return (Linea) linea.clone();
	// }
	// catch (CloneNotSupportedException e) {
	// e.printStackTrace();
	// return null;
	// }
	// }
	//
	// // linea de mas de dos puntos
	// Linea lineaFiltrada = new Linea();
	// cloneAndAddPunto(lineaFiltrada, linea.getPunto(0));
	//
	// // angulo del primer segmento de la linea
	// Punto p1 = linea.getPunto(0);
	// Punto p2 = linea.getPunto(1);
	// double anguloAnt = Math.atan2(p2.y - p1.y, p2.x - p1.x);
	//
	// // para cada par de segmentos se van comparando los angulos
	// for (int i = 1, size = linea.getNumPuntos()-1; i < size; i++)
	// {
	// p1 = linea.getPunto(i);
	// p2 = linea.getPunto(i+1);
	//
	// double angulo = Math.atan2(p2.y - p1.y, p2.x - p1.x);
	//
	// if ( !((anguloAnt - toleranciaAngulo) <= angulo) || !((anguloAnt +
	// toleranciaAngulo) >= angulo) )
	// {
	// cloneAndAddPunto(lineaFiltrada, p1);
	// }
	// anguloAnt = angulo;
	// }
	//
	// // ultimo punto
	// cloneAndAddPunto(lineaFiltrada, linea.getPunto(linea.getNumPuntos()-1));
	// return lineaFiltrada;
	// }

	/**
	 * Dado un punto, lo clona y lo añade a una linea dada. Metodo para ahorrar
	 * lineas de codigo.
	 * 
	 * @param linea
	 *            Linea a la que se añade el punto.
	 * @param p
	 *            Punto que se clona y se añade a la linea.
	 */
	// private static void cloneAndAddPunto(Linea linea, Punto p)
	// {
	// try {
	// Punto pc = (Punto) p.clone();
	// linea.addPunto(pc);
	// }
	// catch (CloneNotSupportedException e) {
	// e.printStackTrace();
	// }
	// }

}
