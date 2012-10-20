/*
 * Created on 02-oct-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
/* CVS MESSAGES:
 *
 * $Id: SnapSimpleMCSweepLineIntersector.java 10627 2007-03-06 17:10:21Z caballero $
 * $Log$
 * Revision 1.4  2007-03-06 17:08:56  caballero
 * Exceptions
 *
 * Revision 1.3  2006/12/04 19:30:23  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/05 19:20:57  azabala
 * first version in cvs
 *
 * Revision 1.1  2006/10/02 19:06:26  azabala
 * First version in CVS
 *
 *
 */
package com.vividsolutions.jts.geomgraph.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.geomgraph.Edge;

public class SnapSimpleMCSweepLineIntersector extends
		SimpleMCSweepLineIntersector {

	List events = new ArrayList();
	// statistics information
	int nOverlaps;

	public SnapSimpleMCSweepLineIntersector() {
	}

	public void computeIntersections(List edges, SegmentIntersector si,
			boolean testAllSegments) {
		if (testAllSegments)
			add(edges, null);
		else
			add(edges);
		computeIntersections(si);
	}

	public void computeIntersections(List edges0, List edges1,
			SegmentIntersector si) {
		add(edges0, edges0);
		add(edges1, edges1);
		computeIntersections(si);
	}

	private void add(List edges) {
		for (Iterator i = edges.iterator(); i.hasNext();) {
			Edge edge = (Edge) i.next();
			// edge is its own group
			add(edge, edge);
		}
	}

	private void add(List edges, Object edgeSet) {
		for (Iterator i = edges.iterator(); i.hasNext();) {
			Edge edge = (Edge) i.next();
			add(edge, edgeSet);
		}
	}

	private void add(Edge edge, Object edgeSet) {
		MonotoneChainEdge mce = edge.getMonotoneChainEdge();
		int[] startIndex = mce.getStartIndexes();
		for (int i = 0; i < startIndex.length - 1; i++) {
			MonotoneChain mc = new MonotoneChain(mce, i);
			SweepLineEvent insertEvent = new SweepLineEvent(edgeSet,
					mce.getMinX(i), null, mc);
			events.add(insertEvent);
			events.add(new SweepLineEvent(edgeSet, mce.getMaxX(i), insertEvent,
					mc));
		}
	}

	/**
	 * Because Delete Events have a link to their corresponding Insert event, it
	 * is possible to compute exactly the range of events which must be compared
	 * to a given Insert event object.
	 */
	private void prepareEvents() {
		Collections.sort(events);
		for (int i = 0; i < events.size(); i++) {
			SweepLineEvent ev = (SweepLineEvent) events.get(i);
			if (ev.isDelete()) {
				ev.getInsertEvent().setDeleteEventIndex(i);
			}
		}
	}

	private void computeIntersections(SegmentIntersector si) {
		nOverlaps = 0;
		prepareEvents();

		for (int i = 0; i < events.size(); i++) {
			SweepLineEvent ev = (SweepLineEvent) events.get(i);
			if (ev.isInsert()) {
				processOverlaps(i, ev.getDeleteEventIndex(), ev, si);
			}
		}
	}

	private void processOverlaps(int start, int end, SweepLineEvent ev0,
			SegmentIntersector si) {
		MonotoneChain mc0 = (MonotoneChain) ev0.getObject();
		/**
		 * Since we might need to test for self-intersections, include current
		 * insert event object in list of event objects to test. Last index can
		 * be skipped, because it must be a Delete event.
		 */
		for (int i = start; i < end; i++) {
			SweepLineEvent ev1 = (SweepLineEvent) events.get(i);
			if (ev1.isInsert()) {
				MonotoneChain mc1 = (MonotoneChain) ev1.getObject();
				// don't compare edges in same group
				// null group indicates that edges should be compared
				if (ev0.edgeSet == null || (ev0.edgeSet != ev1.edgeSet)) {
					mc0.computeIntersections(mc1, si);
					nOverlaps++;
				}
			}
		}
	}

}
