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
package org.gvsig.graph.core;

public class GvEdge {

	private int idArc;
	private int idEdge;
	/**
	 * Sentido de digitalización.Un 1 indica que va en ese sentido, un cero al
	 * contrario.
	 */
	private int direc;
	private int idNodeOrig;
	private int idNodeEnd;
	private int type;
	private double distance;
	private double weight;

	private int numSoluc;

	/**
	 * Sentido de digitalización.Un 1 indica que va en ese sentido, un cero al
	 * contrario.
	 * 
	 * @return digitalization direction. 0=>inversed 1=> Same as geometry
	 */
	public int getDirec() {
		return direc;
	}

	public void setDirec(int direc) {
		this.direc = direc;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getIdArc() {
		return idArc;
	}

	public void setIdArc(int idArc) {
		this.idArc = idArc;
	}

	public int getIdNodeEnd() {
		return idNodeEnd;
	}

	public void setIdNodeEnd(int idNodeEnd) {
		this.idNodeEnd = idNodeEnd;
	}

	public int getIdNodeOrig() {
		return idNodeOrig;
	}

	public void setIdNodeOrig(int idNodeOrig) {
		this.idNodeOrig = idNodeOrig;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public int getNumSoluc() {
		return numSoluc;
	}

	public void setNumSoluc(int numSoluc) {
		this.numSoluc = numSoluc;
	}

	public int getIdEdge() {
		return idEdge;
	}

	public void setIdEdge(int idEdge) {
		this.idEdge = idEdge;
	}

}
