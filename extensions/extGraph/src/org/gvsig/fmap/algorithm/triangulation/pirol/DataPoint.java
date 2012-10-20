/*
 * Created on 09.03.2005 for Pirol
 *
 * SVN header information:
 * $Author: LBST-PF-3\orahn $
 * $Rev: 2446 $
 * $Date: 2006-09-12 14:57:25 +0200 (Di, 12 Sep 2006) $
 * $Id: DataPoint.java 2446 2006-09-12 12:57:25Z LBST-PF-3\orahn $
 */
package org.gvsig.fmap.algorithm.triangulation.pirol;

/**
 * 
 * Extends the class "punkt" and adds a value for the data which will be
 * interpolated.
 * 
 * @author Stefan Ostermann <br>
 * <br>
 *         FH Osnabr&uuml;ck - University of Applied Sciences Osnabr&uuml;ck, <br>
 *         Project: PIROL (2006), <br>
 *         Subproject: Daten- und Wissensmanagement
 * 
 * @version $Rev: 2446 $
 * 
 */
public class DataPoint extends PirolPoint {
	protected double value = 0;
	private boolean isValueSet = false;

	public DataPoint() {
		super();
	}

	public DataPoint(double[] coords) {
		super(coords);
	}

	public DataPoint(double[] coords, int index) {
		super(coords, index);
	}

	public DataPoint(double[] coords, int index, ScaleChanger scaler,
			boolean prescaled) {
		super(coords, index, scaler, prescaled);
	}

	public void setValue(double _value) {
		value = _value;
		isValueSet = true;
	}

	public double getValue() {
		return value;
	}

	public boolean valueSet() {
		return isValueSet;
	}

	public boolean equals(Object obj) {
		DataPoint p;
		try {
			p = (DataPoint) obj;
		} catch (ClassCastException e) {
			return false;
		}

		try {
			if (this == p) {
				return true;
			} else if (p.getIndex() == this.getIndex()
					&& !(this.getIndex() < 0)) {
				return true;
			} else if (p.value == this.value && p.getX() == this.getX()
					&& p.getY() == this.getY() && p.getZ() == this.getZ()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static DataPoint clone(DataPoint dataPoint) {
		DataPoint newDataPoint = new DataPoint(dataPoint.coordinates);
		newDataPoint.setValue(dataPoint.value);
		return newDataPoint;
	}

}
