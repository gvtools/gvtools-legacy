/*
 * Created on 09.12.2004
 *
 * SVN header information:
 *  $Author: LBST-PF-3\orahn $
 *  $Rev: 2446 $
 *  $Date: 2006-09-12 14:57:25 +0200 (Di, 12 Sep 2006) $
 *  $Id: clusterPoint.java 2446 2006-09-12 12:57:25Z LBST-PF-3\orahn $
 */
package org.gvsig.fmap.algorithm.triangulation.pirol;

/**
 * 
 * Point object, specialized to be used in cluster center calculations: Can hold
 * information on the ratio (weight) that defines how strong this point to a
 * cluster center.
 * 
 * @author Ole Rahn <br>
 * <br>
 *         FH Osnabr&uuml;ck - University of Applied Sciences Osnabr&uuml;ck, <br>
 *         Project: PIROL (2005), <br>
 *         Subproject: Daten- und Wissensmanagement
 * 
 * @version $Rev: 2446 $
 * 
 */
public class clusterPoint extends PirolPoint {

	protected double[] dependencies;
	protected int numCenteres = 0;

	protected int indexOfMaxDependency = 0;
	protected boolean iomdValid = false;

	public clusterPoint(double[] coords, int index) {
		super(coords, index);
	}

	public void setupDependencies(int num) {
		// System.out.println( this.getIndex() +
		// " -> setupDependencies("+num+")");
		this.dependencies = new double[num];
		this.numCenteres = num;

		for (int i = 0; i < num; i++) {
			this.dependencies[i] = 0;
		}

		iomdValid = false;
	}

	public double getDependency(int nr) throws Exception {
		if (nr < this.dependencies.length) {
			return dependencies[nr];
		}
		throw new Exception("invalid dependency: " + nr);
	}

	public void setDependency(double dependency, int nr) throws Exception {
		if (nr < this.dependencies.length) {
			this.dependencies[nr] = dependency;
			iomdValid = false;
		} else {
			throw new Exception("invalid dependency: " + nr);
		}
	}

	public int getIndexOfMaxDependency() throws Exception {
		double maxDep = -1.0;

		if (!this.iomdValid) {
			for (int i = 0; i < this.numCenteres; i++) {
				if (this.getDependency(i) > maxDep) {
					this.indexOfMaxDependency = i;
					maxDep = this.getDependency(i);
				}
			}
			iomdValid = true;
		}

		return this.indexOfMaxDependency;
	}

	public int getNumCenteres() {
		return numCenteres;
	}

	public void setNumCenteres(int numCenteres) {
		this.setupDependencies(numCenteres);
	}

	public clusterPoint(double[] coords, int index, ScaleChanger scaler) {
		super(coords, index, scaler);
	}

}
