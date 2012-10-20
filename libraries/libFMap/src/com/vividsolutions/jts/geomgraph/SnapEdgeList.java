/*
 * Created on 04-oct-2006
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
 * $Id: SnapEdgeList.java 10627 2007-03-06 17:10:21Z caballero $
 * $Log$
 * Revision 1.2  2007-03-06 17:08:55  caballero
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
 *
 */
package com.vividsolutions.jts.geomgraph;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.iver.cit.gvsig.fmap.spatialindex.RTreeJsi;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Overwrites jts' EdgeList to allow spatial queries consideering snap.
 * 
 * (for example, (0 0, 5 0) and (0.01 0.01, 5.01 0.02) arent equivalent in
 * findEqualEdge, even thought with a snap distance of 0.1
 * 
 * 
 * 
 * */
public class SnapEdgeList extends EdgeList {
	private List edges = new ArrayList();

	// private SpatialIndex index = new Quadtree();

	// El quadtree no funciona bien para indexar dimensiones
	// lineales o lineas.

	private RTreeJsi spatialIndex = new RTreeJsi();
	private double snapTolerance;

	public SnapEdgeList(double snapTolerance) {
		this.snapTolerance = snapTolerance;
	}

	/**
	 * Insert an edge unless it is already in the list
	 */
	public void add(Edge e) {
		// int position = edges.size();
		edges.add(e);
		// TODO me peta el indice espacial
		// Envelope oldEnvelope = e.getEnvelope();
		// Rectangle2D newEnvelope = getEnvelopeForSnap(oldEnvelope);
		// spatialIndex.insert(newEnvelope, position);
	}

	private Rectangle2D getEnvelopeForSnap(Envelope oldEnvelope) {
		double xmin = oldEnvelope.getMinX() - snapTolerance;
		double ymin = oldEnvelope.getMinY() - snapTolerance;
		double width = oldEnvelope.getWidth() + snapTolerance;
		double height = oldEnvelope.getHeight() + snapTolerance;

		return new Rectangle2D.Double(xmin, ymin, width, height);
	}

	// <FIX> fast lookup for edges
	/**
	 * If there is an edge equal to e already in the list, return it. Otherwise
	 * return null.
	 * 
	 * @return equal edge, if there is one already in the list null otherwise
	 */
	public Edge findEqualEdge(Edge e) {
		// TODO NO ENCUENTRO UN INDICE ESPACIAL EN CONDICIONES
		// Collection testEdges = spatialIndex.
		// query(getEnvelopeForSnap(e.getEnvelope()));
		// PETA CON QUADTREE Y CON RTREE DE JSI

		Collection testEdges = edges;

		for (Iterator i = testEdges.iterator(); i.hasNext();) {
			// TODO me peta el indice espacial
			// int index = ((Integer)i.next()).intValue();
			Edge testEdge = (Edge) i.next();

			// Edge testEdge = (Edge) edges.get(index);
			if (testEdge.equals(e))
				return testEdge;
		}
		return null;
	}

	public void addAll(Collection edgeColl) {
		for (Iterator i = edgeColl.iterator(); i.hasNext();) {
			add((Edge) i.next());
		}
	}

	public Iterator iterator() {
		return edges.iterator();
	}

	public Edge get(int i) {
		return (Edge) edges.get(i);
	}

	/**
	 * If the edge e is already in the list, return its index.
	 * 
	 * @return index, if e is already in the list -1 otherwise
	 */
	public int findEdgeIndex(Edge e) {
		for (int i = 0; i < edges.size(); i++) {
			if (((Edge) edges.get(i)).equals(e))
				return i;
		}
		return -1;
	}

	public List getEdges() {
		return edges;
	}

}
