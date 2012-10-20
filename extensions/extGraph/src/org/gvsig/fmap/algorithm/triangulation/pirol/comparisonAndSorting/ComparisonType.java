/*
 * Created on 21.02.2007 for PIROL
 * 
 * SVN header information:
 *  $Rev$
 *  $Author$
 *  $Date$
 *  $Id$
 */
/**
 * 
 */
package org.gvsig.fmap.algorithm.triangulation.pirol.comparisonAndSorting;

/**
 * TODO: comment class
 * 
 * <br>
 * <br>
 * <b>Last change by $Author$ on $Date$</b>
 * 
 * @author Michael Br&uuml;ning <br>
 * <br>
 *         FH Osnabr&uuml;ck - University of Applied Sciences Osnabr&uuml;ck, <br>
 *         Project: PIROL (2007), <br>
 *         Subproject: Daten- und Wissensmanagement
 * 
 * @version $Rev$
 * 
 */
public enum ComparisonType {
	LessThan(0), Equal(1), GreaterThan(2), Contains(3), Matches(4);

	int type = -1;

	ComparisonType(int type) {
		this.type = type;
	}

	public static int getNumOfTypes() {
		return ComparisonType.values().length;
	}

	public int getType() {
		return this.type;
	}
}
