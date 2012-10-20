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
 * @author fjp To set barriers or modified cost to a graph. This objects are
 *         stored in arrayList modifiedCosts (inside Network class). Useful to
 *         remove barriers, for example (restore a cost). Be careful: You need
 *         to call applyModifiedCosts each time you call setVelocities
 *         (setVelocities recalculate cost (weight?) field in graph
 */
public class GvModifiedCost {
	private int idArc;
	private int idEdge;
	private int idInverseEdge;
	private int direction; // 1, 2 -> inverse, 3 -> both
	private double oldCost;
	private double newCost;
	private double oldInverseCost;

	private boolean applied;

	public GvModifiedCost(int idArc, double newCost, int direction) {
		this.idArc = idArc;
		this.newCost = newCost;
		this.direction = direction;
	}

	public boolean isApplied() {
		return applied;
	}

	public void setApplied(boolean applied) {
		this.applied = applied;
	}

	public int getIdArc() {
		return idArc;
	}

	public void setIdArc(int idArc) {
		this.idArc = idArc;
	}

	public int getIdEdge() {
		return idEdge;
	}

	public void setIdEdge(int idEdge) {
		this.idEdge = idEdge;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getIdInverseEdge() {
		return idInverseEdge;
	}

	public void setIdInverseEdge(int idInverseEdge) {
		this.idInverseEdge = idInverseEdge;
	}

	public double getNewCost() {
		return newCost;
	}

	public void setNewCost(double newCost) {
		this.newCost = newCost;
	}

	public double getOldCost() {
		return oldCost;
	}

	public void setOldCost(double oldCost) {
		this.oldCost = oldCost;
	}

	public double getOldInverseCost() {
		return oldInverseCost;
	}

	public void setOldInverseCost(double oldInverseCost) {
		this.oldInverseCost = oldInverseCost;
	}
}
