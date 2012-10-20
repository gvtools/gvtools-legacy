/*
 * Created on 05.01.2005
 *
 * SVN header information:
 *  $Author: LBST-PF-3\orahn $
 *  $Rev: 2446 $
 *  $Date: 2006-09-12 14:57:25 +0200 (Di, 12 Sep 2006) $
 *  $Id: DelaunayLoopItem.java 2446 2006-09-12 12:57:25Z LBST-PF-3\orahn $
 */
package org.gvsig.fmap.algorithm.triangulation.pirol;

/**
 * Class tha describes the needed values to start an iteration in the
 * culculation of a delaunay diagramm.
 * 
 * @author Ole Rahn <br>
 * <br>
 *         FH Osnabr&uuml;ck - University of Applied Sciences Osnabr&uuml;ck, <br>
 *         Project: PIROL (2005), <br>
 *         Subproject: Daten- und Wissensmanagement
 * 
 * @version $Rev: 2446 $s
 */
public class DelaunayLoopItem {
	private int triangleCount;
	private DelaunayPunkt punkt1, punkt2, alterPunkt;

	public DelaunayLoopItem(DelaunayPunkt punkt1, DelaunayPunkt punkt2,
			DelaunayPunkt alterPunkt, int triangleCount) {
		super();
		this.punkt1 = punkt1;
		this.punkt2 = punkt2;
		this.alterPunkt = alterPunkt;
		this.triangleCount = triangleCount;
	}

	public DelaunayLoopItem(DelaunayPunkt punkt1, DelaunayPunkt punkt2,
			DelaunayPunkt alterPunkt) {
		super();
		this.punkt1 = punkt1;
		this.punkt2 = punkt2;
		this.alterPunkt = alterPunkt;
		this.triangleCount = -1;
	}

	public DelaunayPunkt getAlterPunkt() {
		return alterPunkt;
	}

	public DelaunayPunkt getPunkt1() {
		return punkt1;
	}

	public DelaunayPunkt getPunkt2() {
		return punkt2;
	}

	public int getTriangleCount() {
		return triangleCount;
	}
}
