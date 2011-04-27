package com.iver.cit.gvsig.fmap.core;

import java.awt.geom.PathIterator;
import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateArrays;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

//import com.iver.andami.PluginServices;


/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
 * $Id: FPolyline2DM.java,v 1.1 2007/10/19 10:03:45 jorpiell Exp $
 * $Log: FPolyline2DM.java,v $
 * Revision 1.1  2007/10/19 10:03:45  jorpiell
 * First commit
 *
 *
 */
/**
 * This object represents a polyline with the M coordinate.
 * @author Jorge Piera LLodr� (jorge.piera@iver.es)
 */
public class FPolyline2DM extends FPolyline2D implements FShapeM{
	private static final long serialVersionUID = -617233536274899782L;
	private static final String NAME = "MULTILINESTRINGM";
	double[] pM = null;

	FPolyline2DM(GeneralPathX gpx, double[] pM) {
		super(gpx);	
		this.pM = pM;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShapeDS#getMs()
	 */
	public double[] getMs() {
		return pM;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FPolyline3D#getShapeType()
	 */
	public int getShapeType() {
		return FShape.LINE | FShape.M;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FPolyline3D#cloneFShape()
	 */
	public FShape cloneFShape() {
		return new FPolyline2DM((GeneralPathX) gp.clone(), (double[]) pM);
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShapeM#setMAt(int, double)
	 */
	public void setMAt(int i, double value) {
		if (i < pM.length){
			pM[i] = value;		
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShapeM#isDecreasing()
	 */
	public boolean isDecreasing() {
		if (pM.length == 0){
			return false;
		}
		return (pM[0] > pM[pM.length-1]);
	}

	public void revertMs(){
		double totalDistance = Math.abs(pM[0] - pM[pM.length-1]);	
		double[] percentages = new double[pM.length];
		for (int i=1 ; i<percentages.length ; i++)
		{
			percentages [i] = (Math.abs(pM[i]-pM[i-1]))/totalDistance;
		}		
		//The first value
		double pm0 = pM[0];		
		if (!isDecreasing()){
			pM[0] = pM[pM.length-1];
			for (int i=1 ; i<pM.length-1 ; i++)
			{
				double increasing = percentages[i] * totalDistance;
				pM[i] = pM[i-1] - increasing;				
			}
		}else{
			pM[0] = pM[pM.length-1];
			for (int i=1 ; i<pM.length-1 ; i++)
			{
				double decreasing = percentages[i] * totalDistance;
				pM[i] = pM[i-1] + decreasing;				
			}
		}
		pM[pM.length-1] = pm0;	
	}

	/**
	 * This method is used instead of the JTS ToString method. The reason is because
	 * JTS doesn't support the M coordinate
	 */
	public String toText(){
		StringBuffer str = new StringBuffer();
		str.append(NAME);
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
}
