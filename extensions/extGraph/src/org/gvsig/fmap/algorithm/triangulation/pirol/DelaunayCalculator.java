/*
 * Created on 04.01.2005
 *
 * SVN header information:
 *  $Author: LBST-PF-3\orahn $
 *  $Rev: 2509 $
 *  $Date: 2006-10-06 12:01:50 +0200 (Fr, 06 Okt 2006) $
 *  $Id: DelaunayCalculator.java 2509 2006-10-06 10:01:50Z LBST-PF-3\orahn $
 */
package org.gvsig.fmap.algorithm.triangulation.pirol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gvsig.fmap.algorithm.triangulation.pirol.apiTools.CollectionsTools;
import org.gvsig.fmap.algorithm.triangulation.pirol.comparisonAndSorting.CoordinateComparator;
import org.xml.sax.ErrorHandler;

/**
 * Class to calculate a Delaunay diagram from a given points (DelaunayPunkt)
 * array.
 * 
 * This class is from a time when we were not sure if we should use german or
 * english language in our classes - parts of this one are german as you see...
 * Sorry about that!
 * 
 * <pre>
 * german,         english:
 * -----------------------------
 * Punkt           point
 * Nachbar (von)   neighbor (of)
 * Kante           line
 * Dreieck         triangle
 * gehört zu       is part of
 * </pre>
 * 
 * @author Ole Rahn <br>
 * <br>
 *         FH Osnabr&uuml;ck - University of Applied Sciences Osnabr&uuml;ck, <br>
 *         Project: PIROL (2005), <br>
 *         Subproject: Daten- und Wissensmanagement
 * 
 * @version $Rev: 2509 $
 * 
 * @see DelaunayPunkt
 */
public final class DelaunayCalculator extends Thread {

	private int sortiertNach = -1;
	private boolean sorted = false;

	private double minimalDistance = Double.MAX_VALUE;

	private DelaunayPunkt[] pointsArray = null;
	private int numPoints = 0;
	private int kantenCreated = 0;
	private int estimatedLineNum = 0;

	private DelaunayPunkt point1, point2;

	private static final double pi = Math.PI;
	// private static final double umrechnungsFaktor =
	// 1.0/(2.0*DelaunayCalculator.pi)*360.0;
	private static final double standardGrenzWinkel = DelaunayCalculator.pi / 18.0;
	protected double limitingAngle = DelaunayCalculator.standardGrenzWinkel;

	private boolean triangulationDone = false;
	private boolean errorOccured = false;
	// private TaskMonitorDialog monitor = null;
	private ErrorHandler errHandler = null;

	// protected PersonalLogger logger = new PersonalLogger(DebugUserIds.OLE);

	int numTriangles = 0;

	public DelaunayCalculator(List<DelaunayPunkt> punktefeld) {
		super();
		this.pointsArray = (DelaunayPunkt[]) punktefeld
				.toArray(new DelaunayPunkt[0]);
	}

	public DelaunayCalculator(List<DelaunayPunkt> punktefeld,
			ErrorHandler errHandler) {
		super();
		this.pointsArray = (DelaunayPunkt[]) punktefeld
				.toArray(new DelaunayPunkt[0]);
		this.numPoints = this.pointsArray.length;
		// this.monitor = monitor;
		this.errHandler = errHandler;
	}

	public DelaunayCalculator(DelaunayPunkt[] punktefeld,
			ErrorHandler errHandler) {
		super();
		this.pointsArray = punktefeld;
		this.numPoints = this.pointsArray.length;
		// this.monitor = monitor;
		this.errHandler = errHandler;
	}

	public boolean hasErrorOccured() {
		return errorOccured;
	}

	public DelaunayPunkt getPunkt(int index) {
		DelaunayPunkt p;

		for (int i = 0; i < this.numPoints; i++) {
			p = this.pointsArray[i];
			if (p.getIndex() == index)
				return p;
		}
		return null;
	}

	public boolean isTriangulationDone() {
		return triangulationDone;
	}

	public boolean isSorted() {
		return sorted;
	}

	protected void setSorted(boolean sortiert, int sortFor) {
		this.sorted = sortiert;
		this.sortiertNach = sortFor;
	}

	protected PirolPoint[] findNextNeighbors() throws Exception {

		if (!this.isSorted() || this.sortiertNach < 0) {
			CoordinateComparator cComp = new CoordinateComparator(
					CoordinateComparator.SORTFOR_X);
			List<DelaunayPunkt> tmp = new ArrayList<DelaunayPunkt>();
			CollectionsTools.addArrayToList(tmp, this.pointsArray);
			Collections.sort(tmp, cComp);
			this.pointsArray = (DelaunayPunkt[]) tmp
					.toArray(new DelaunayPunkt[0]);
			this.setSorted(true, CoordinateComparator.SORTFOR_X);
		}
		this.findNextNeighborsRecursive(0, this.pointsArray.length - 1);

		return new PirolPoint[] { this.point1, point2, this.point2 };
	}

	protected void findNextNeighborsRecursive(int down, int up)
			throws Exception {
		if (down < up) {
			int dividingIndex = (int) (Math.floor((up + down) / 2));
			this.findNextNeighborsRecursive(down, dividingIndex);
			this.findNextNeighborsRecursive(dividingIndex + 1, up);

			double dividingXValue = ((PirolPoint) this.pointsArray[dividingIndex])
					.getX();

			int lowerLimitIndex = dividingIndex;
			int upperLimitIndex = dividingIndex;
			boolean found = false;

			while (!found && lowerLimitIndex > down) {
				if (Math.abs(dividingXValue
						- ((PirolPoint) this.pointsArray[lowerLimitIndex])
								.getX()) > this.minimalDistance) {
					found = true;
					break;
				}
				lowerLimitIndex--;
			}

			found = false;
			while (!found && upperLimitIndex < up) {
				if (Math.abs(dividingXValue
						- ((PirolPoint) this.pointsArray[upperLimitIndex])
								.getX()) > this.minimalDistance) {
					found = true;
					break;
				}
				upperLimitIndex++;
			}

			ArrayList<PirolPoint> borderLinePoints = new ArrayList<PirolPoint>();

			for (int i = lowerLimitIndex; i <= upperLimitIndex; i++) {
				borderLinePoints.add(this.pointsArray[i]);
			}

			CoordinateComparator cComp = new CoordinateComparator(
					CoordinateComparator.SORTFOR_Y);
			Collections.sort(borderLinePoints, cComp);

			DelaunayPunkt iPoint = null, jPoint;
			double tmpDistance;

			for (int i = 0; i < borderLinePoints.size(); i++) {
				try {
					iPoint = ((DelaunayPunkt) borderLinePoints.get(i));
				} catch (Exception e) {
					// this.logger.printError(e.getMessage() + ", " +
					// borderLinePoints.get(i).getClass().getName() +
					// " vs. DelaunayPunkt");
					e.printStackTrace();
				}
				for (int j = 0; j < borderLinePoints.size(); j++) {
					if (i == j)
						continue;
					jPoint = ((DelaunayPunkt) borderLinePoints.get(j));
					if (iPoint.isNeighborOf(jPoint))
						continue;

					if (Math.abs(iPoint.getY() - jPoint.getY()) < this.minimalDistance) {
						tmpDistance = iPoint.distanceTo(jPoint);
						if (tmpDistance < this.minimalDistance) {
							if (tmpDistance == 0) {
								this.eliminatePoint(jPoint);
								this.minimalDistance = Double.MAX_VALUE;
								this.point1 = null;
								this.point2 = null;
								throw new IllegalArgumentException(
										"two-points-with-identical-coordinates");
							}
							this.minimalDistance = tmpDistance;
							this.point1 = iPoint;
							this.point2 = jPoint;
						}
					} else if (j > i) {
						// die Abstaende sind bereits groesser, da der index j
						// im nach y-koordinaten
						// geordneten feld groesser ist...
						break;
					}

				}
			}
		}
	}

	protected void eliminatePoint(DelaunayPunkt pkt) {
		DelaunayPunkt[] newPointArray = new DelaunayPunkt[this.pointsArray.length - 1];

		for (int i = 0, j = 0; i < this.pointsArray.length; i++) {
			if (this.pointsArray[i] != pkt) {
				newPointArray[j] = this.pointsArray[i];
				j++;
			}
		}
		this.pointsArray = newPointArray;
		this.numPoints = newPointArray.length;
	}

	protected void calculateDiagramIterative(DelaunayPunkt p1,
			DelaunayPunkt p2, DelaunayPunkt alterPunkt, int triangleCounter)
			throws Exception {
		int number = triangleCounter;
		boolean continueQuery = true;

		List<DelaunayLoopItem> currentList = new ArrayList<DelaunayLoopItem>();
		currentList.add(new DelaunayLoopItem(p1, p2, alterPunkt, number));

		List<DelaunayLoopItem> newList, newResults;

		DelaunayLoopItem item;

		while (!currentList.isEmpty()) {
			newList = new ArrayList<DelaunayLoopItem>();
			continueQuery = true;

			while (continueQuery) {
				continueQuery = false;

				DelaunayLoopItem[] currentItems = currentList
						.toArray(new DelaunayLoopItem[0]);

				for (int itemIndex = 0; itemIndex < currentItems.length; itemIndex++) {
					number++;
					item = currentItems[itemIndex];
					newResults = this.buildTriangles(item.getPunkt1(),
							item.getPunkt2(), item.getAlterPunkt(), number);
					newList.addAll(newResults);
				}

				if (newList.isEmpty() && this.kantenCreated == 1
						&& this.limitingAngle > Double.MIN_VALUE) {
					this.limitingAngle -= 0.017;
					continueQuery = true;
				} else if (this.limitingAngle != DelaunayCalculator.standardGrenzWinkel) {
					this.limitingAngle = DelaunayCalculator.standardGrenzWinkel;
				}

			}

			// this.monitor.report(this.kantenCreated, this.estimatedLineNum,
			// PirolPlugInMessages.getString("done"));

			currentList.clear();
			currentList = newList;
		}

	}

	protected List<DelaunayLoopItem> buildTriangles(DelaunayPunkt p1,
			DelaunayPunkt p2, DelaunayPunkt alterPunkt, int nummerDreieck)
			throws Exception {
		List<DelaunayLoopItem> result = new ArrayList<DelaunayLoopItem>();
		PirolEdge k;

		DelaunayPunkt ap = null;

		PirolEdge k_tmp = new PirolEdge(p1, p2);

		if (p1.gehoertZuMehrAlsEinemDreieckMit(p2)) {
			return result;
		}

		double maximalerWinkel = 0;
		double aktuellerWinkel = 0;

		int index3 = -1;
		double normFaktor = 0;

		double c = p1.distanceTo(p2);
		double cSq = c * c;

		if (alterPunkt != null) {
			ap = alterPunkt;
			normFaktor = k_tmp.getNormalenFaktorZu(ap);
		}

		DelaunayPunkt aktItem, p3 = null;
		double a, b, cosGamma, aktuellerNormFaktor = 0;

		for (int j = 0; j < this.numPoints; j++) {
			aktItem = this.pointsArray[j];

			if (aktItem.getIndex() == p1.getIndex()
					|| aktItem.getIndex() == p2.getIndex()
					|| (ap != null && aktItem.getIndex() == ap.getIndex()))
				continue;

			aktuellerNormFaktor = k_tmp.getNormalenFaktorZu(aktItem);
			if (aktuellerNormFaktor == 0)
				continue;
			else if (normFaktor > 0 && aktuellerNormFaktor > 0)
				continue;
			else if (normFaktor < 0 && aktuellerNormFaktor < 0)
				continue;

			/*
			 * # (C) # / | \ # b/ | \a # / |h \ # (A)______|_________(B) # c
			 */
			aktuellerWinkel = 0;
			b = p1.distanceTo(aktItem);
			a = p2.distanceTo(aktItem);

			// a^2=b^2+c^2-2*b*c*cos( Gamma ) (Kosinussatz).
			// => gamma = acos( (a^2+b^2-c^2)/(2ab) )
			cosGamma = (a * a + b * b - cSq) / (2.0 * b * a);

			if (cosGamma > 1.0 && cosGamma - 1.0 < 0.000000000000001) {
				cosGamma = 1;
			}

			if (cosGamma >= -1.0 && cosGamma <= 1.0) {
				aktuellerWinkel = Math.acos(cosGamma);
			} else {
				System.out.println("schrott winkel: " + cosGamma + ", " + a
						+ ", " + b + ", " + c);
				continue;
			}

			if (aktuellerWinkel > maximalerWinkel) {
				maximalerWinkel = aktuellerWinkel;
				index3 = aktItem.getIndex();
				p3 = aktItem;
			}

		}

		if (index3 > -1 && maximalerWinkel >= this.limitingAngle) {

			boolean isNeighborOfP1 = p3.isNeighborOf(p1);
			boolean isNeighborOfP2 = p3.isNeighborOf(p2);

			if (!isNeighborOfP1 || !isNeighborOfP2) {

				p1.setAttendsTriangle(nummerDreieck);
				p2.setAttendsTriangle(nummerDreieck);
				p3.setAttendsTriangle(nummerDreieck);
				this.numTriangles++;

				if (!isNeighborOfP1) {
					// if p3 and p1 are not already connected, they are now
					p1.setNeighborOf(p3);
					p3.setNeighborOf(p1);
					k = new PirolEdge(p1, p3);
					p1.attendsEdge(k);
					p3.attendsEdge(k);
					this.kantenCreated++;
					result.add(new DelaunayLoopItem(p1, p3, p2));
				}

				if (!isNeighborOfP2) {
					// if p3 and p2 are not already connected, they are now
					p2.setNeighborOf(p3);
					p3.setNeighborOf(p2);
					k = new PirolEdge(p2, p3);
					p2.attendsEdge(k);
					p3.attendsEdge(k);
					this.kantenCreated++;
					result.add(new DelaunayLoopItem(p2, p3, p1));
				}

			} else {
				// Löcher im 3d modell weg!
				if (!p3.attendsTriangleTogetherWith(p1, p2)) {
					// this.logger.printDebug("stopfe loch!");
					p1.setAttendsTriangle(nummerDreieck);
					p2.setAttendsTriangle(nummerDreieck);
					p3.setAttendsTriangle(nummerDreieck);
					this.numTriangles++;
				}
			}
		}
		return result;
	}

	public void run() {
		super.run();
		this.estimatedLineNum = this.numPoints * 3;
		try {
			this.createDelaunayNet();
			this.compilePoints();
		} catch (Exception e) {
			errorOccured = true;
			if (this.errHandler != null) {
				// this.errHandler.handleThrowable(e);
				e.printStackTrace();
			} else {
				e.printStackTrace();
			}
		} finally {
			this.quitExecution();
		}
	}

	public void compilePoints() {
		int numPoints = this.pointsArray.length;

		for (int i = 0; i < numPoints; i++) {
			this.pointsArray[i].compile();
		}
	}

	protected void quitExecution() {
		// if (this.monitor!=null){
		// this.monitor.setVisible(false);
		// this.monitor.dispose();
		// }
		this.triangulationDone = true;
	}

	public int getNumPoints() {
		return numPoints;
	}

	public DelaunayPunkt[] getPointsArray() {
		return pointsArray;
	}

	public boolean createDelaunayNet() throws Exception {
		if (this.pointsArray.length == 0) {
			this.triangulationDone = true;
			return false;
		}

		boolean neighborsFound = false;
		int numPts = this.numPoints;
		while (!neighborsFound) {
			try {
				numPts = this.numPoints;
				this.findNextNeighbors();
				neighborsFound = true;
			} catch (IllegalArgumentException e) {
				neighborsFound = (numPts == this.numPoints);
				System.out.println((numPts - this.numPoints)
						+ " point(s) was/were kicked out");
			}
			numPts = this.numPoints;
		}

		// das punktefeld ist nun nach x koordinaten geordnet
		// rendern der ersten Basislinie

		if (this.point1 != null && this.point2 != null) {
			PirolEdge k = new PirolEdge(this.point1, this.point2);
			this.point1.attendsEdge(k);
			this.point2.attendsEdge(k);
			this.kantenCreated++;

			this.point1.setNeighborOf(this.point2);
			this.point2.setNeighborOf(this.point1);
		} else {
			throw new Exception("No next neighbours found!");
		}

		this.calculateDiagramIterative(this.point1, this.point2, null, 0);
		this.triangulationDone = true;
		return true;

	}

	public int getNumLines() {
		return this.kantenCreated;
	}

	public int getNumTriangles() {
		return numTriangles;
	}
}
