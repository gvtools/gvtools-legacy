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

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * 
 *         If cost == -1 => prohibited turn
 */
public class GvTurn {
	private int idArcFrom;
	private int idArcTo;
	private double cost;
	private GvNode node;

	public GvTurn(int idArcOrigin, int idArcDestination, double newCost) {
		idArcFrom = idArcOrigin;
		idArcTo = idArcDestination;
		cost = newCost;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public int getIdArcFrom() {
		return idArcFrom;
	}

	public void setIdArcFrom(int idArcFrom) {
		this.idArcFrom = idArcFrom;
	}

	public int getIdArcTo() {
		return idArcTo;
	}

	public void setIdArcTo(int idArcTo) {
		this.idArcTo = idArcTo;
	}

	public GvNode getNode() {
		return node;
	}

	public void setNode(GvNode node) {
		this.node = node;
	}
}
