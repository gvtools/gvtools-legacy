package com.iver.cit.gvsig.fmap.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;


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
/* CVS MESSAGES:
 *
 * $Id: ShapeMFactory.java,v 1.1 2007/10/19 10:03:45 jorpiell Exp $
 * $Log: ShapeMFactory.java,v $
 * Revision 1.1  2007/10/19 10:03:45  jorpiell
 * First commit
 *
 *
 */
/**
 * This factory is used to create geoemtries with the M 
 * coordinate
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class ShapeMFactory {

	public static IGeometry createPoint2DM(double x, double y, double m) {
		return new FGeometryM(new FPoint2DM(x, y, m));
	}


	/**
	 * Creates a Polyline in 2D with the M coordinate
	 * @param gp
	 * Coordinates to create the polyline
	 * @param ms
	 * Array with the M values
	 * @return
	 * A Geometry with Ms
	 */
	public static IGeometryM createPolyline2DM(GeneralPathX gp, double[] ms) {
		return new FGeometryM(new FPolyline2DM(gp, ms));
	}

	public static IGeometryM createPolyline2DM(FPolyline2DM polyline) {
		return new FGeometryM(polyline);
	}

	public static IGeometry createPolyline2DM(ByteBuffer data) {

		int count = data.getInt();
		GeneralPathX gp = new GeneralPathX();
		//double[] ms = new double[count - 1];
		//ArrayList alMs = new ArrayList();

		ArrayList<Double> ms = new ArrayList<Double>();
		//		        ArrayList<Double> ms_aux = null;
		//		        double[] ms = null;      //Intento de evitar el tener que encapsular las m's en
		//		        double[] ms_aux = null;  //objetos Double y de tener que recorrer un ArrayList
		int ms_lentgh = 0;

		for (int i=0; i < count; i++) {
			parseTypeAndSRID(data);
			FPoint2DM[] points = parsePointArray(data);
			//		            ms_aux = new double[ms_lentgh + points.length];

			gp.moveTo(points[0].getX(), points[0].getY());
			//alMs.add(new Double(points[0].getM()));
			//		            ms_aux[ms_lentgh + 0] = points[0].getM();
			ms.add(points[0].getM());

			for (int j = 1; j< points.length; j++) {
				ms.add(points[j].getM());
				//		             ms_aux[ms_lentgh + j] = points[j].getM();
				gp.lineTo(points[j].getX(), points[j].getY());
			} 

			//ms[i] = points[i].getM();
			//		            if (ms != null) {
			//		             System.arraycopy(ms, 0, ms_aux, ms.length, ms.length);
			//		            }
			//		            ms = ms_aux;
			//		            ms_lentgh = ms.length;
			//		            ms_aux = null;
		}//for


		// OJO: Para ahorrarme esto tendría que modificar la clase FPolyline2DM para
		//      que las ms se almacenaran como objetos Double en lugar de usar el tipo
		//      primitivo double.
		double[] aMs = new double[ms.size()];
		for (int i = 0; i < ms.size(); i++) {
			aMs[i] = ((Double)ms.get(i)).doubleValue();
		}

		return new FGeometryM(new FPolyline2DM(gp, aMs));
	}

	public static IGeometry createPolygon2DM(GeneralPathX gp, double[] pM) {
		return new FGeometryM(new FPolygon2DM(gp, pM));
	}


	public static IGeometry createMultipoint2DM(double[] x, double[] y,
			double[] m) {		
		throw new UnsupportedOperationException();
	}

	private static void parseTypeAndSRID(ByteBuffer data) {
		byte endian = data.get(); //skip and test endian flag
		/* if (endian != data.endian) {
            throw new IllegalArgumentException("Endian inconsistency!");
        } */
		int typeword = data.getInt();
		int realtype = typeword & 0x1FFFFFFF; //cut off high flag bits

		boolean gHaveZ = (typeword & 0x80000000) != 0;
		boolean gHaveM = (typeword & 0x40000000) != 0;
		boolean gHaveS = (typeword & 0x20000000) != 0;

		int srid = -1;

		if (gHaveS) {
			srid = data.getInt();
		}

	}

	/**
	 * Parse an Array of "slim" Points (without endianness and type, part of
	 * LinearRing and Linestring, but not MultiPoint!
	 * 
	 * @param haveZ
	 * @param haveM
	 */
	private static FPoint2DM[] parsePointArray(ByteBuffer data) {
		int count = data.getInt();
		FPoint2DM[] result = new FPoint2DM[count];
		for (int i = 0; i < count; i++) {
			result[i] = parsePoint(data);
		}
		return result;
	}

	private static FPoint2DM parsePoint(ByteBuffer data) {
		double X = data.getDouble();
		double Y = data.getDouble();
		double M = data.getDouble();

		return new FPoint2DM(X, Y, M);
	}   


}
