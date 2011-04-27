/*
 * Created on 09-oct-2006
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
* $Id: SnapPolygonBuilder.java 10627 2007-03-06 17:10:21Z caballero $
* $Log$
* Revision 1.2  2007-03-06 17:08:59  caballero
* Exceptions
*
* Revision 1.1  2006/12/04 19:30:23  azabala
* *** empty log message ***
*
* Revision 1.2  2006/10/17 18:25:53  azabala
* *** empty log message ***
*
* Revision 1.1  2006/10/09 19:10:56  azabala
* First version in CVS
*
*
*/
package com.vividsolutions.jts.operation.overlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geomgraph.DirectedEdge;
import com.vividsolutions.jts.geomgraph.EdgeRing;
import com.vividsolutions.jts.geomgraph.SnappingPlanarGraph;
import com.vividsolutions.jts.util.Assert;

public class SnapPolygonBuilder extends PolygonBuilder {

	 private GeometryFactory geometryFactory;
	  private CGAlgorithms cga;
	  //private List dirEdgeList;
	  //private NodeMap nodes;
	  private List shellList        = new ArrayList();

	  public SnapPolygonBuilder(GeometryFactory geometryFactory, 
			  CGAlgorithms cga)
	  {
		super(geometryFactory, cga);
	    this.geometryFactory = geometryFactory;
	    this.cga = cga;
	  }

	  /**
	   * Add a complete graph.
	   * The graph is assumed to contain one or more polygons,
	   * possibly with holes.
	   */
	  public void add(SnappingPlanarGraph graph)
	  {
	    add(graph.getEdgeEnds(), graph.getNodes());
	  }

	  /**
	   * Add a set of edges and nodes, which form a graph.
	   * The graph is assumed to contain one or more polygons,
	   * possibly with holes.
	   */
	  public void add(Collection dirEdges, Collection nodes)
	  {
		SnappingPlanarGraph.linkResultDirectedEdges(nodes);
	    List maxEdgeRings = buildMaximalEdgeRings(dirEdges);
	    List freeHoleList = new ArrayList();
	    List edgeRings = buildMinimalEdgeRings(maxEdgeRings, shellList, freeHoleList);
	    sortShellsAndHoles(edgeRings, shellList, freeHoleList);
	    placeFreeHoles(shellList, freeHoleList);
	  }

	  public List getPolygons()
	  {
	    List resultPolyList = computePolygons(shellList);
	    return resultPolyList;
	  }


	  /**
	   * for all DirectedEdges in result, form them into MaximalEdgeRings
	   */
	  private List buildMaximalEdgeRings(Collection dirEdges)
	  {
	    List maxEdgeRings     = new ArrayList();
	    for (Iterator it = dirEdges.iterator(); it.hasNext(); ) {
	      DirectedEdge de = (DirectedEdge) it.next();
	      if (de.isInResult() && de.getLabel().isArea() ) {
	        // if this edge has not yet been processed
	        if (de.getEdgeRing() == null) {
	          MaximalEdgeRing er = new MaximalEdgeRing(de, geometryFactory, cga);
	          maxEdgeRings.add(er);
	          er.setInResult();
//	System.out.println("max node degree = " + er.getMaxDegree());
	        }
	      }
	    }
	    return maxEdgeRings;
	  }

	  private List buildMinimalEdgeRings(List maxEdgeRings, List shellList, List freeHoleList)
	  {
	    List edgeRings = new ArrayList();
	    for (Iterator it = maxEdgeRings.iterator(); it.hasNext(); ) {
	      MaximalEdgeRing er = (MaximalEdgeRing) it.next();
	      if (er.getMaxNodeDegree() > 2) {
	        er.linkDirectedEdgesForMinimalEdgeRings();
	        List minEdgeRings = er.buildMinimalRings();
	        // at this point we can go ahead and attempt to place holes, if this EdgeRing is a polygon
	        EdgeRing shell = findShell(minEdgeRings);
	        if (shell != null) {
	          placePolygonHoles(shell, minEdgeRings);
	          shellList.add(shell);
	        }
	        else {
	          freeHoleList.addAll(minEdgeRings);
	        }
	      }
	      else {
	        edgeRings.add(er);
	      }
	    }
	    return edgeRings;
	  }

	  /**
	   * This method takes a list of MinimalEdgeRings derived from a MaximalEdgeRing,
	   * and tests whether they form a Polygon.  This is the case if there is a single shell
	   * in the list.  In this case the shell is returned.
	   * The other possibility is that they are a series of connected holes, in which case
	   * no shell is returned.
	   *
	   * @return the shell EdgeRing, if there is one
	   * @return null, if all the rings are holes
	   */
	  private EdgeRing findShell(List minEdgeRings)
	  {
	    int shellCount = 0;
	    EdgeRing shell = null;
	    for (Iterator it = minEdgeRings.iterator(); it.hasNext(); ) {
	      EdgeRing er = (MinimalEdgeRing) it.next();
	      if (! er.isHole()) {
	        shell = er;
	        shellCount++;
	      }
	    }
	    Assert.isTrue(shellCount <= 1, "found two shells in MinimalEdgeRing list");
	    return shell;
	  }
	  /**
	   * This method assigns the holes for a Polygon (formed from a list of
	   * MinimalEdgeRings) to its shell.
	   * Determining the holes for a MinimalEdgeRing polygon serves two purposes:
	   * <ul>
	   * <li>it is faster than using a point-in-polygon check later on.
	   * <li>it ensures correctness, since if the PIP test was used the point
	   * chosen might lie on the shell, which might return an incorrect result from the
	   * PIP test
	   * </ul>
	   */
	  private void placePolygonHoles(EdgeRing shell, List minEdgeRings)
	  {
	    for (Iterator it = minEdgeRings.iterator(); it.hasNext(); ) {
	      MinimalEdgeRing er = (MinimalEdgeRing) it.next();
	      if (er.isHole()) {
	        er.setShell(shell);
	      }
	    }
	  }
	  /**
	   * For all rings in the input list,
	   * determine whether the ring is a shell or a hole
	   * and add it to the appropriate list.
	   * Due to the way the DirectedEdges were linked,
	   * a ring is a shell if it is oriented CW, a hole otherwise.
	   */
	  private void sortShellsAndHoles(List edgeRings, List shellList, List freeHoleList)
	  {
	    for (Iterator it = edgeRings.iterator(); it.hasNext(); ) {
	      EdgeRing er = (EdgeRing) it.next();
//	      er.setInResult();
	      if (er.isHole() ) {
	        freeHoleList.add(er);
	      }
	      else {
	        shellList.add(er);
	      }
	    }
	  }
	  /**
	   * This method determines finds a containing shell for all holes
	   * which have not yet been assigned to a shell.
	   * These "free" holes should
	   * all be <b>properly</b> contained in their parent shells, so it is safe to use the
	   * <code>findEdgeRingContaining</code> method.
	   * (This is the case because any holes which are NOT
	   * properly contained (i.e. are connected to their
	   * parent shell) would have formed part of a MaximalEdgeRing
	   * and been handled in a previous step).
	   */
	  private void placeFreeHoles(List shellList, List freeHoleList)
	  {
	    for (Iterator it = freeHoleList.iterator(); it.hasNext(); ) {
	      EdgeRing hole = (EdgeRing) it.next();
	      // only place this hole if it doesn't yet have a shell
	      if (hole.getShell() == null) {
	        EdgeRing shell = findEdgeRingContaining(hole, shellList);
	        Assert.isTrue(shell != null, "unable to assign hole to a shell");
	        hole.setShell(shell);
	      }
	    }
	  }
	  /**
	   * Find the innermost enclosing shell EdgeRing containing the argument EdgeRing, if any.
	   * The innermost enclosing ring is the <i>smallest</i> enclosing ring.
	   * The algorithm used depends on the fact that:
	   * <br>
	   *  ring A contains ring B iff envelope(ring A) contains envelope(ring B)
	   * <br>
	   * This routine is only safe to use if the chosen point of the hole
	   * is known to be properly contained in a shell
	   * (which is guaranteed to be the case if the hole does not touch its shell)
	   *
	   * @return containing EdgeRing, if there is one
	   * @return null if no containing EdgeRing is found
	   */
	  
	  
	  //TODO Estudiar como meter el snapping
	  private EdgeRing findEdgeRingContaining(EdgeRing testEr, List shellList)
	  {
	    LinearRing testRing = testEr.getLinearRing();
	    Envelope testEnv = testRing.getEnvelopeInternal();
	    Coordinate testPt = testRing.getCoordinateN(0);

	    EdgeRing minShell = null;
	    Envelope minEnv = null;
	    for (Iterator it = shellList.iterator(); it.hasNext(); ) {
	      EdgeRing tryShell = (EdgeRing) it.next();
	      LinearRing tryRing = tryShell.getLinearRing();
	      Envelope tryEnv = tryRing.getEnvelopeInternal();
	      if (minShell != null) minEnv = minShell.getLinearRing().getEnvelopeInternal();
	      boolean isContained = false;
	      if (tryEnv.contains(testEnv)
	          && CGAlgorithms.isPointInRing(testPt, tryRing.getCoordinates()) )
	        isContained = true;
	      // check if this new containing ring is smaller than the current minimum ring
	      if (isContained) {
	        if (minShell == null
	            || minEnv.contains(tryEnv)) {
	          minShell = tryShell;
	        }
	      }
	    }
	    return minShell;
	  }
	  private List computePolygons(List shellList)
	  {
	    List resultPolyList   = new ArrayList();
	    // add Polygons for all shells
	    for (Iterator it = shellList.iterator(); it.hasNext(); ) {
	      EdgeRing er = (EdgeRing) it.next();
	      Polygon poly = er.toPolygon(geometryFactory);
	      resultPolyList.add(poly);
	    }
	    return resultPolyList;
	  }

	  /**
	   * Checks the current set of shells (with their associated holes) to
	   * see if any of them contain the point.
	   */
	  
	  //TODO METER SNAPPING
	  public boolean containsPoint(Coordinate p)
	  {
	    for (Iterator it = shellList.iterator(); it.hasNext(); ) {
	      EdgeRing er = (EdgeRing) it.next();
	      if (er.containsPoint(p))
	       return true;
	    }
	    return false;
	  }



}

