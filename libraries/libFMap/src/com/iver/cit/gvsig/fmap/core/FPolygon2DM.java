/* gvSIG. Geographic Information System of the Valencian Government
*
* Copyright (C) 2007-2008 Infrastructures and Transports Department
* of the Valencian Government (CIT)
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
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
* MA  02110-1301, USA.
* 
*/

/*
* AUTHORS (In addition to CIT):
* 2010 Software Colaborativo (www.scolab.es)   development
*/
 
package com.iver.cit.gvsig.fmap.core;

import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import com.iver.cit.gvsig.fmap.core.v02.FConverter;

public class FPolygon2DM extends FPolyline2DM implements FShapeM {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3376505816513737862L;

	public FPolygon2DM(GeneralPathX gpx, double[] pm) {
		super(gpx, pm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public FShape cloneFShape() {
		return new FPolygon2DM((GeneralPathX) gp.clone(), (double[]) pM);
	}

	@Override
	public int getShapeType() {
		return FShape.POLYGON | FShape.M;
	}

	@Override
	public String toText() {
		StringBuffer str = new StringBuffer();
		str.append("MULTIPOLYGONM");
		str.append(" ((");
		int theType;		
		double[] theData = new double[6];		

		PathIterator theIterator = getPathIterator(null, FConverter.FLATNESS);
		int i = 0;

		while (!theIterator.isDone()) {
			//while not done
			theType = theIterator.currentSegment(theData);

			double m = 0.0;
			if (i < pM.length){
				m = pM[i]; 
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

	@Override
	public boolean intersects(Rectangle2D r) {
		return gp.intersects(r);
	}

}

