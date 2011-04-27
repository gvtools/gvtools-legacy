/* gvSIG. Geographic Information System of the Valencian Government
*
* Copyright (C) 2007-2008 Infrastructures and Transports Department
* of the Valencian Government (CIT)
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
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
* MA  02110-1301, USA.
* 
*/

/*
* AUTHORS (In addition to CIT):
* 2008 Software Colaborativo (www.scolab.es)   development
*/
 
package org.gvsig.graph.core;
/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * 
 * Each node connects to GvEdge by GvConnectors. Their purpose is to model accurately turnCosts.
 *
 */
public class GvConnector {
		private GvEdge edgeIn = null;
		private GvEdge edgeOut = null;
		private int from_link_c;
		private double bestCostIn = Double.MAX_VALUE;  // Cost arriving to this connector
		private double bestCostOut = Double.MAX_VALUE;  // Cost exiting from this connector
		private boolean mustBeRevised = false;
		
		public int getFrom_link_c() {
			return from_link_c;
		}

		public void setFrom_link_c(int from_link_c) {
			this.from_link_c = from_link_c;
		}

		public double getBestCostIn() {
			return bestCostIn;
		}

		public void setBestCostIn(double bestCostIn) {
			this.bestCostIn = bestCostIn;
		}

		public double getBestCostOut() {
			return bestCostOut;
		}

		public void setBestCostOut(double bestCostOut) {
			this.bestCostOut = bestCostOut;
		}

		public boolean isMustBeRevised() {
			return mustBeRevised;
		}

		public void setMustBeRevised(boolean mustBeRevised) {
			this.mustBeRevised = mustBeRevised;
		}

		public GvEdge getEdgeIn() {
			return edgeIn;
		}

		public GvEdge getEdgeOut() {
			return edgeOut;
		}

		public GvConnector() {
		}

		public void setEdgeIn(GvEdge edgeIn) {
			this.edgeIn = edgeIn;
		}

		public void setEdgeOut(GvEdge edgeOut) {
			this.edgeOut = edgeOut;
		}
}

