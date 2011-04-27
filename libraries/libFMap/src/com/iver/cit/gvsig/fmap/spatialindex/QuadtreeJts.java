/*
 * Created on 28-abr-2006
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
* $Id: QuadtreeJts.java 11288 2007-04-19 17:32:50Z azabala $
* $Log$
* Revision 1.2  2007-04-19 17:32:50  azabala
* new constructor (fmap spatial index from an existing jts spatial index)
*
* Revision 1.1  2006/05/01 18:38:41  azabala
* primera version en cvs del api de indices espaciales
*
*
*/
package com.iver.cit.gvsig.fmap.spatialindex;

import java.awt.geom.Rectangle2D;
import java.util.List;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.quadtree.Quadtree;
/**
 * Adapter for ISPatialIndex gvSIG's interface to
 * JTS Quadtree.
 * 
 * 
 * @author azabala
 *
 */
public class QuadtreeJts implements ISpatialIndex {
	private Quadtree quadtree;
	
	public QuadtreeJts(){
		quadtree = new Quadtree();
	}
	
	public QuadtreeJts(Quadtree jtsidx){
		quadtree = jtsidx;
	}
		
	public List query(Rectangle2D rect) {
		return quadtree.query(fromRect(rect));
	}

	public void insert(Rectangle2D rect, int index) {
		quadtree.insert(fromRect(rect), new Integer(index));
	}

	public void delete(Rectangle2D rect, int index) {
		quadtree.remove(fromRect(rect), new Integer(index));
	}
	
	private Envelope fromRect(Rectangle2D rect){
		Envelope env = new Envelope(rect.getMinX(),
				rect.getMaxX(),
				rect.getMinY(),
				rect.getMaxY());
		return env;
	}

}

