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

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

public class FEdge extends DirectedSparseEdge {

	private int arcID;
	private int type;
	private int direction;
	private double cost1;
	private double cost2;
	private int idNodeOrig;
	private int idNodeEnd;
	private int numSoluc;

	public FEdge(Vertex from, Vertex to) {
		super(from, to);
	}

	public double getWeight() {
		return cost1;
	}

	public int getArcID() {
		return arcID;
	}

	public void setArcID(int arcID) {
		this.arcID = arcID;
	}

	public double getCost1() {
		return cost1;
	}

	public void setWeight(double cost1) {
		this.cost1 = cost1;
	}

	public double getCost2() {
		return cost2;
	}

	public void setCost2(double d) {
		this.cost2 = d;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
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

	public int getNumSoluc() {
		return numSoluc;
	}

	public void setNumSoluc(int numSoluc) {
		this.numSoluc = numSoluc;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
