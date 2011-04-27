/*
 * Created on 16-feb-2006
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
* $Id: MemoryConvexHullVisitor.java 10626 2007-03-06 16:55:54Z caballero $
* $Log$
* Revision 1.2  2007-03-06 16:47:58  caballero
* Exceptions
*
* Revision 1.1  2006/06/20 18:20:45  azabala
* first version in cvs
*
* Revision 1.2  2006/06/02 18:21:28  azabala
* *** empty log message ***
*
* Revision 1.1  2006/05/24 21:13:31  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.2  2006/03/07 21:01:33  azabala
* *** empty log message ***
*
* Revision 1.1  2006/03/06 19:48:39  azabala
* *** empty log message ***
*
* Revision 1.3  2006/03/05 19:57:48  azabala
* *** empty log message ***
*
* Revision 1.2  2006/02/26 20:52:55  azabala
* *** empty log message ***
*
* Revision 1.1  2006/02/17 16:32:50  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.convexhull.fmap;

import java.util.ArrayList;
import java.util.List;

import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.layerOperations.VectorialData;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;



public class MemoryConvexHullVisitor implements ConvexHullVisitor {

	List geometries;
	GeometryFactory geomFact;
	
	public MemoryConvexHullVisitor(){
		geometries = new ArrayList();
		geomFact = new GeometryFactory();
		
	}
	public void visit(IGeometry g, int index) throws VisitorException, ProcessVisitorException {
		if(g == null)
			return;
		Geometry jtsgeo = g.toJTSGeometry();
		geometries.add(jtsgeo);
	}
	
	/**
	 * Returns FMap convex hull geometry.
	 * @return
	 */
	public IGeometry getConvexHull(){
		GeometryCollection gc = getGeometryCollection();
		return FConverter.jts_to_igeometry(gc.convexHull());
	}
	
	 GeometryCollection getGeometryCollection(){
		Geometry[] geoms = new Geometry[geometries.size()];
		geometries.toArray(geoms);
		GeometryCollection gc =
			geomFact.createGeometryCollection(geoms);
		return gc;
	}
	
	public Geometry getJtsConvexHull(){
		return getGeometryCollection().convexHull();
	}

	public void stop(FLayer layer) throws VisitorException {
	}

	public boolean start(FLayer layer) throws StartVisitorException {
		return layer instanceof VectorialData;
	}
	
	public String getProcessDescription() {
		return "Computing convex hull in memory";
	}

	

}

