package com.iver.cit.gvsig.fmap.core;

/**
 * Punto 2D con coordenada M
 *
 * @author An�nimo ;-)
 */
public class FPoint2DM extends FPoint2D implements FShapeM {
	private double m;
	private static final String NAME = "POINTM";
	
	/**
	 * Crea un nuevo FPoint2DM.
	 *
	 * @param x Coordenada x.
	 * @param y Coordenada y.
	 * @param z Coordenada z.
	 */
	public FPoint2DM(double x, double y, double m) {
		super(x, y);
		this.m = m;
	}
	
	public double getM() {
		return m;
	}

	public void setM(double value) {
		m = value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShapeM#getMs()
	 */
	public double[] getMs() {
		// tODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShapeM#isDecreasing()
	 */
	public boolean isDecreasing() {
		// tODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShapeM#setMAt(int, double)
	 */
	public void setMAt(int i, double value) {
		// tODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShapeM#revertMs()
	 */
	public void revertMs(){
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FShapeM#toText()
	 */
	public String toText() {
		StringBuffer str = new StringBuffer();
		str.append(NAME);
		str.append(" ((");
		str.append(getX() + " " + getY() + " " + m);
		str.append("))");
		return str.toString();
	}
}
