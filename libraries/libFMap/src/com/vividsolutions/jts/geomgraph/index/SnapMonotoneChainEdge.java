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
 * $Id: SnapMonotoneChainEdge.java 10627 2007-03-06 17:10:21Z caballero $
 * $Log$
 * Revision 1.2  2007-03-06 17:08:57  caballero
 * Exceptions
 *
 * Revision 1.1  2006/12/04 19:30:23  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/17 18:25:53  azabala
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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geomgraph.SnappingEdge;

public class SnapMonotoneChainEdge extends MonotoneChainEdge {

	SnappingEdge e;
	int[] startIndex;
	double snapTolerance;

	public SnapMonotoneChainEdge(SnappingEdge e, double snapTolerance) {
		super(e);
		this.e = e;
		startIndex = super.getStartIndexes();
		this.snapTolerance = snapTolerance;
	}

	public SnappingEdge getEdge() {
		return e;
	}

	public void computeIntersects(MonotoneChainEdge mce, SegmentIntersector si) {
		if (!(mce instanceof SnapMonotoneChainEdge))
			throw new IllegalArgumentException("Requiere MCE de snap");
		for (int i = 0; i < startIndex.length - 1; i++) {
			for (int j = 0; j < mce.getStartIndexes().length - 1; j++) {
				computeIntersectsForChain(i, mce, j, si);
			}// for
		}// for
	}

	public void computeIntersectsForChain(int chainIndex0,
			MonotoneChainEdge mce, int chainIndex1, SegmentIntersector si) {
		if (!(mce instanceof SnapMonotoneChainEdge))
			throw new IllegalArgumentException("Requiere MCE de snap");
		computeIntersectsForChain(startIndex[chainIndex0],
				startIndex[chainIndex0 + 1], mce,
				mce.getStartIndexes()[chainIndex1],
				mce.getStartIndexes()[chainIndex1 + 1], si);
	}

	private void computeIntersectsForChain(int start0, int end0,
			MonotoneChainEdge mce, int start1, int end1, SegmentIntersector ei) {

		if (!(mce instanceof SnapMonotoneChainEdge))
			throw new IllegalArgumentException("Requiere MCE de snap");

		SnapMonotoneChainEdge snapMce = (SnapMonotoneChainEdge) mce;
		Coordinate p00 = super.getCoordinates()[start0];
		Coordinate p01 = super.getCoordinates()[end0];

		Coordinate p10 = mce.getCoordinates()[start1];
		Coordinate p11 = mce.getCoordinates()[end1];

		// terminating condition for the recursion
		if (end0 - start0 == 1 && end1 - start1 == 1) {
			ei.addIntersections(e, start0, snapMce.getEdge(), start1);
			return;
		}

		// nothing to do if the envelopes of these chains don't overlap
		Envelope env1 = new Envelope(p00, p01);
		double newMinX = env1.getMinX() - snapTolerance;
		double newMaxX = env1.getMaxX() + snapTolerance;
		double newMinY = env1.getMinY() - snapTolerance;
		double newMaxY = env1.getMaxY() + snapTolerance;
		env1 = new Envelope(newMinX, newMaxX, newMinY, newMaxY);

		Envelope env2 = new Envelope(p10, p11);
		newMinX = env1.getMinX() - snapTolerance;
		newMaxX = env1.getMaxX() + snapTolerance;
		newMinY = env1.getMinY() - snapTolerance;
		newMaxY = env1.getMaxY() + snapTolerance;
		env2 = new Envelope(newMinX, newMaxX, newMinY, newMaxY);

		if (!env1.intersects(env2))
			return;

		// the chains overlap,
		// so split each in half and iterate (binary search)
		int mid0 = (start0 + end0) / 2;
		int mid1 = (start1 + end1) / 2;

		if (start0 < mid0) {
			if (start1 < mid1)
				computeIntersectsForChain(start0, mid0, mce, start1, mid1, ei);
			if (mid1 < end1)
				computeIntersectsForChain(start0, mid0, mce, mid1, end1, ei);
		}

		if (mid0 < end0) {
			if (start1 < mid1)
				computeIntersectsForChain(mid0, end0, mce, start1, mid1, ei);
			if (mid1 < end1)
				computeIntersectsForChain(mid0, end0, mce, mid1, end1, ei);
		}
	}
}
