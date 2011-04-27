package com.iver.cit.gvsig.fmap.core;

import java.awt.geom.PathIterator;

import com.iver.cit.gvsig.fmap.core.v02.FConverter;

/**
 * The Class FPolyline3DM that manages correctly (with Z and M) PolylineZs
 * 
 * @author Pompermaier Flavio
 */
public class FPolyline3DM extends FPolyline3D implements FShapeM {
	
	private static final long serialVersionUID = -4920745174292188836L;
	private static final String NAME = "MULTILINESTRING_3DM";
	double[] pM = null;
	
	/**
	 * Crea un nuevo Polyline3DM.
	 * 
	 * @param gpx
	 *            GeneralPathX
	 * @param pZ
	 *            Vector con la Z.
	 * @param pM
	 *            Vector con la M.
	 */
	public FPolyline3DM(GeneralPathX gpx, double[] pZ, double[] pM) {
		super(gpx, pZ);
		this.pM = pM;
	}
	
	/**
	 * @see com.iver.cit.gvsig.fmap.core.FShape#getShapeType()
	 */
	@Override
	public int getShapeType() {
		return FShape.LINE | FShape.Z;
	}
	
	/**
	 * Devuelve un Array con todos los valores de M.
	 * 
	 * @return Array de Ms.
	 */
	public double[] getMs() {
		return pM;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShape#cloneFShape()
	 */
	@Override
	public FShape cloneFShape() {
		return new FPolyline3DM((GeneralPathX) gp.clone(), pZ, pM);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShapeM#isDecreasing()
	 */
	public boolean isDecreasing() {
		if (pM.length == 0)
			return false;
		return pM[0] > pM[pM.length - 1];
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShapeM#revertMs()
	 */
	public void revertMs() {
		double totalDistance = Math.abs(pM[0] - pM[pM.length - 1]);
		double[] percentages = new double[pM.length];
		for (int i = 1; i < percentages.length; i++) {
			percentages[i] = Math.abs(pM[i] - pM[i - 1]) / totalDistance;
		}
		//The first value
		double pm0 = pM[0];
		if (!isDecreasing()) {
			pM[0] = pM[pM.length - 1];
			for (int i = 1; i < pM.length - 1; i++) {
				double increasing = percentages[i] * totalDistance;
				pM[i] = pM[i - 1] - increasing;
			}
		}
		else {
			pM[0] = pM[pM.length - 1];
			for (int i = 1; i < pM.length - 1; i++) {
				double decreasing = percentages[i] * totalDistance;
				pM[i] = pM[i - 1] + decreasing;
			}
		}
		pM[pM.length - 1] = pm0;
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShapeM#setMAt(int, double)
	 */
	public void setMAt(int i, double value) {
		if (i < pM.length) {
			pM[i] = value;
		}
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShapeM#toText()
	 */
	public String toText() {
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
			if (i < pM.length) {
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
		return str.delete(str.length() - 1, str.length()) + "))";
	}
}
