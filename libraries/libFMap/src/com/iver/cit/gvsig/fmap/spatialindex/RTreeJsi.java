/*
 * Created on 15-may-2006
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
* $Id: RTreeJsi.java 28572 2009-05-11 13:48:23Z vcaballero $
* $Log$
* Revision 1.5  2007-09-19 16:25:39  jaume
* ReadExpansionFileException removed from this context and removed unnecessary imports
*
* Revision 1.4  2007/06/27 20:17:30  azabala
* new spatial index (rix)
*
* Revision 1.3  2007/03/06 17:08:59  caballero
* Exceptions
*
* Revision 1.2  2006/06/05 16:59:08  azabala
* implementada busqueda de vecino mas proximo a partir de rectangulos
*
* Revision 1.1  2006/05/24 21:58:04  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.fmap.spatialindex;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.infomatiq.jsi.IntProcedure;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;

/**
 * RTree spatial index implementation based in library
 * JSI (java spatial index).
 *
 * http://jsi.sourceforge.net/
 *
 * This RTree has better performance that Spatial Index Library
 * RTree, and that JTS'RTree, because
 * it uses the GNU's Trove Collections API.
 *
 * We are doing some probes with it, because it offers
 * a Nearest Neighbour algorithm implementation
 * (useful for Spatial Join geoprocess, for example).
 *
 * It isnt persistent, and We've found some problems
 * with delete operations.
 *
 *
 *
 *
 * @author azabala
 *
 */
public class RTreeJsi implements ISpatialIndex, INearestNeighbourFinder {
	private RTree rtree;

	public RTreeJsi(){
		rtree = new RTree();
	}

	public void create(){
		Properties props = new Properties();
//		props.setProperty("MaxNodeEntries", "500");
//		props.setProperty("MinNodeEntries", "200");
		rtree.init(props);
	}

	class ListIntProcedure implements IntProcedure{
		ArrayList solution = new ArrayList();

		public boolean execute(int arg0) {
			solution.add(new Integer(arg0));
			return true;
		}

		public List getSolution(){
			return solution;
		}
	}


	public List query(Rectangle2D rect) {
		ListIntProcedure solution = new ListIntProcedure();
		rtree.intersects(toJsiRect(rect), solution);
		return solution.getSolution();
	}

	private Rectangle toJsiRect(Rectangle2D rect){
		Rectangle jsiRect = new Rectangle((float)rect.getMinX(),
				(float)rect.getMinY(),
				(float)rect.getMaxX(),
				(float)rect.getMaxY());
		return jsiRect;
	}

	public void insert(Rectangle2D rect, int index) {
		rtree.add(toJsiRect(rect), index);
	}

	public void delete(Rectangle2D rect, int index) {
		rtree.delete(toJsiRect(rect), index);
	}

	public List findNNearest(int numberOfNearest, Point2D point) {
		ListIntProcedure solution = new ListIntProcedure();
		com.infomatiq.jsi.Point jsiPoint =
			new com.infomatiq.jsi.Point((float)point.getX(), (float)point.getY());
		//FIXME REVISAR
		rtree.nearest(jsiPoint, solution, Float.POSITIVE_INFINITY);
		return solution.getSolution();
	}

	public List findNNearest(int numberOfNearest, Rectangle2D rect) {
		ListIntProcedure solution = new ListIntProcedure();
		com.infomatiq.jsi.Rectangle jsiRect =
			toJsiRect(rect);

		rtree.nearest(jsiRect, solution, Float.POSITIVE_INFINITY);
		return solution.getSolution();
	}

//	public List findNNearest(int numberOfNearest, Rectangle2D rect){
//		return (List) rtree.nearest(toJsiRect(rect), numberOfNearest);
//	}

//	public List findNNearest(int numberOfNearest, Point2D point){
//		com.infomatiq.jsi.Point jsiPoint =
//			new com.infomatiq.jsi.Point((float)point.getX(), (float)point.getY());
//		return (List) rtree.nearest(jsiPoint, numberOfNearest);
//	}

	//FIXME Add this method to spatial index interface
	public Iterator iterator(){
		return rtree.iterator();
	}

	public static void main(String[] args){
		RTreeJsi q = new RTreeJsi();
		q.create();

			for(int i = 1; i <= 99; i++){
				q.insert(new Rectangle2D.Double(100+i, 200+i, 200+i, 500+i),i);
			}

			for(int i = 1; i <= 99; i++){
				q.delete(new Rectangle2D.Double(100+i, 200+i, 200+i, 500+i),i);
				System.out.println("Rect="+i);
			}
	}

}

