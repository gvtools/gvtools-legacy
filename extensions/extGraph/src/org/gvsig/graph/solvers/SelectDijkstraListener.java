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
 * 2009 Software Colaborativo (www.scolab.es)   development
 */

package org.gvsig.graph.solvers;

import org.gvsig.graph.core.GvEdge;
import org.gvsig.graph.core.GvNode;
import org.gvsig.graph.core.Network;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FBitSet;

public class SelectDijkstraListener implements IDijkstraListener {

	private Network net;
	private MapContext mapContext;

	public SelectDijkstraListener(Network net, MapContext mapContext) {
		this.net = net;
		this.mapContext = mapContext;
	}

	public void startSelection() {
		mapContext.beginAtomicEvent();
	}

	public void stopSelection() {
		mapContext.endAtomicEvent();
	}

	public boolean adjacentEdgeVisited(GvNode fromNode, GvEdge edge) {
		// TODO: Tener en cuenta maxDistance y maxCost
		try {
			FBitSet bs = net.getLayer().getRecordset().getSelection();
			bs.set(edge.getIdArc());
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public boolean minimumCostNodeSelected(GvNode node) {
		return false;
	}

}
