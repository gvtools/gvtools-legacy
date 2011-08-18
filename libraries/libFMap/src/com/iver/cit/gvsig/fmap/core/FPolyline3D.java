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
package com.iver.cit.gvsig.fmap.core;

import java.awt.geom.PathIterator;

import com.iver.cit.gvsig.fmap.core.v02.FConverter;

/**
 * Polilinea 3D.
 *
 * @author Vicente Caballero Navarro
 */
public class FPolyline3D extends FPolyline2D implements FShape3D {
	double[] pZ = null;

	/**
	 * Crea un nuevo Polyline3D.
	 *
	 * @param gpx GeneralPathX
	 * @param pZ Vector con la Z.
	 */
	public FPolyline3D(GeneralPathX gpx, double[] pZ) {
		super(gpx);
		this.pZ = pZ;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.core.FShape#getShapeType()
	 */
	public int getShapeType() {
		return FShape.LINE | FShape.Z;
	}

	/**
	 * Devuelve un Array con todos los valores de Z.
	 *
	 * @return Array de Zs.
	 */
	public double[] getZs() {
		return pZ;
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShape#cloneFShape()
	 */
	public FShape cloneFShape() {
		return new FPolyline3D((GeneralPathX) gp.clone(), (double[]) pZ);
	}

	
	//
	public String toText(){		
		StringBuffer str = new StringBuffer();
		//Ojo eldriver postgis no tiene en cuenta capas
		//de tipo no multi.Cambiar a static.
		str.append("MULTILINESTRING");
		str.append(" ((");
		int theType;		
		double[] theData = new double[6];		

		PathIterator theIterator = getPathIterator(null, FConverter.FLATNESS);
		int i = 0;

		while (!theIterator.isDone()) {
			//while not done
			theType = theIterator.currentSegment(theData);

			double m = 0.0;
			if (i < pZ.length){
				m = pZ[i]; 
			}
			
			switch (theType) {
			case PathIterator.SEG_MOVETO:					
				str.append(theData[0] + " " + theData[1] + " " + m + ",");
				break;

			case PathIterator.SEG_LINETO:
				str.append(theData[0] + " " + theData[1] + " " + m + ",");

				break;

			case PathIterator.SEG_QUADTO:
				System.out.println("Not supported here");

				break;

			case PathIterator.SEG_CUBICTO:
				System.out.println("Not supported here");

				break;

			case PathIterator.SEG_CLOSE:
				break;
			} //end switch

			theIterator.next();
			i++;
		} //end while loop		
		return str.delete(str.length()-1, str.length()) + "))";
	}
	
}
