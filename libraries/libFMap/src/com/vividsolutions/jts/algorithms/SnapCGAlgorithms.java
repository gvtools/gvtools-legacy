/*
 * Created on 06-oct-2006
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
* $Id: SnapCGAlgorithms.java 13683 2007-09-13 17:58:32Z azabala $
* $Log$
* Revision 1.2  2007-09-13 17:58:32  azabala
* added snapEquals2D method
*
* Revision 1.1  2006/12/04 19:29:31  azabala
* *** empty log message ***
*
* Revision 1.2  2006/10/19 16:06:48  azabala
* *** empty log message ***
*
* Revision 1.1  2006/10/17 18:25:53  azabala
* *** empty log message ***
*
* Revision 1.1  2006/10/09 19:10:56  azabala
* First version in CVS
*
*
*/
package com.vividsolutions.jts.algorithms;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.algorithm.RobustDeterminant;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.operation.overlay.SnapLineIntersector;

public class SnapCGAlgorithms extends CGAlgorithms {
	/**
	   * Test whether a point lies on the line segments defined by a
	   * list of coordinates.
	   *
	   * @return true true if
	   * the point is a vertex of the line or lies in the interior of a line
	   * segment in the linestring
	   */
	  public static boolean isOnLine(Coordinate p, Coordinate[] pt, double snapTol) {
	    SnapLineIntersector lineIntersector = new SnapLineIntersector(snapTol);
	    for (int i = 1; i < pt.length; i++) {
	      Coordinate p0 = pt[i - 1];
	      Coordinate p1 = pt[i];
	      lineIntersector.computeIntersection(p, p0, p1);
	      if (lineIntersector.hasIntersection()) {
	        return true;
	      }
	    }
	    return false;
	  }
	  public static boolean isPointInRing(Coordinate p, Coordinate[] ring, double snapTolerance) {
		    /*
		     *  For each segment l = (i-1, i), see if it crosses ray from test point in positive x direction.
		     */
		    int crossings = 0;  // number of segment/ray crossings
		    
		    SnapLineIntersector lineIntersector = new SnapLineIntersector(snapTolerance);
		    
		    for (int i = 1; i < ring.length; i++) {
		      int i1 = i - 1;
		      Coordinate p1 = ring[i];
		      Coordinate p2 = ring[i1];
		      
		      lineIntersector.computeIntersection(p, p1, p2);
		      if (lineIntersector.hasIntersection()) {
		        crossings ++;
		      }

//		      if (((p1.y > (p.y - snapTolerance)) && (p2.y <= (p.y + snapTolerance))) ||
//		          ((p2.y > (p.y - snapTolerance)) && (p1.y <= (p.y) + snapTolerance))) {//si no se cumple, no pueden intersectar
//		        
//		    	double x1 = p1.x - p.x;
//		        double y1 = p1.y - p.y;
//		        double x2 = p2.x - p.x;
//		        double y2 = p2.y - p.y;
//		        /*
//		        *  segment straddles x axis, so compute intersection with x-axis.
//		         */
//		        double xInt = RobustDeterminant.signOfDet2x2(x1, y1, x2, y2) / (y2 - y1);
//		        //xsave = xInt;
//		        /*
//		        *  crosses ray if strictly positive intersection.
//		         */
//		        if (xInt > 0.0) {
//		          crossings++;
//		        }
//		      }
		    }
		    /*
		     *  p is inside if number of crossings is odd.
		     */
		    if ((crossings % 2) == 1) {
		      return true;
		    }
		    else {
		      return false;
		    }
		  }
	  
	  /**
	   * Computes the orientation of a point q to the directed line segment p1-p2.
	   * The orientation of a point relative to a directed line segment indicates
	   * which way you turn to get to q after travelling from p1 to p2.
	   *
	   * @return 1 if q is counter-clockwise from p1-p2
	   * @return -1 if q is clockwise from p1-p2
	   * @return 0 if q is collinear with p1-p2
	   */
	  public static int computeOrientation(Coordinate p1, Coordinate p2, Coordinate q) {
	    return orientationIndex(p1, p2, q);
	  }
	  /**
	   * Returns the index of the direction of the point <code>q</code>
	   * relative to a
	   * vector specified by <code>p1-p2</code>.
	   *
	   * @param p1 the origin point of the vector
	   * @param p2 the final point of the vector
	   * @param q the point to compute the direction to
	   *
	   * @return 1 if q is counter-clockwise (left) from p1-p2
	   * @return -1 if q is clockwise (right) from p1-p2
	   * @return 0 if q is collinear with p1-p2
	   */
	  public static int orientationIndex(Coordinate p1, Coordinate p2, Coordinate q) {
	    // travelling along p1->p2, turn counter clockwise to get to q return 1,
	    // travelling along p1->p2, turn clockwise to get to q return -1,
	    // p1, p2 and q are colinear return 0.
	    double dx1 = p2.x - p1.x;
	    double dy1 = p2.y - p1.y;
	    double dx2 = q.x - p2.x;
	    double dy2 = q.y - p2.y;
	    return RobustDeterminant.signOfDet2x2(dx1, dy1, dx2, dy2);
	  }
	  
	  
	  public static boolean snapEquals2D(Coordinate a, Coordinate b, double snapTolerance){
		  return a.distance(b) <= snapTolerance;
	  }
	  
	  
}

