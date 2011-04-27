/*
 * Created on 03-mar-2006
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
* $Id: ScalableUnionVisitor.java 12949 2007-08-07 15:23:01Z azabala $
* $Log$
* Revision 1.3  2007-08-07 15:23:01  azabala
* centrilizing JTS in JTSFacade
*
* Revision 1.2  2007/03/06 16:47:58  caballero
* Exceptions
*
* Revision 1.1  2006/06/20 18:20:45  azabala
* first version in cvs
*
* Revision 1.3  2006/06/08 18:20:59  azabala
* modificaciones para admitir capas de shapeType MULTI
*
* Revision 1.2  2006/06/02 18:21:28  azabala
* *** empty log message ***
*
* Revision 1.1  2006/05/24 21:13:31  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.2  2006/05/01 19:16:10  azabala
* optimizada la union mediante buffer(0)
*
* Revision 1.1  2006/03/06 19:48:39  azabala
* *** empty log message ***
*
* Revision 1.1  2006/03/05 19:57:48  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.convexhull.fmap;

import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.geoprocess.core.util.JTSFacade;
import com.vividsolutions.jts.geom.Geometry;




//TODO Este Visitor no debería heredar del anterior, puesto que las
//llamadas getJtsConvexHull() no devuelven un convex hull, sino una
//unión.


public class ScalableUnionVisitor extends ScalableConvexHullVisitor {
	//geometry type of the layer whose features we are going to fussion
	//(polygon features are optimized in jts with buffer(0) trick, the
	//nor the rest
	int geometryType;
	
	
	public ScalableUnionVisitor(int geometryType){
		super();
		this.geometryType = geometryType;
	}
	
	
	public void visit(IGeometry g, int index) throws VisitorException, ProcessVisitorException {
		if(g == null)
			return;
		
		/*
		 * TODO Aquí estamos desechando aquellas geometrias cuyo tipo
		 * no es POLYGON (el tipo MULTI no aparece por ningún lado)
		 * 
		 * Se puede dar el caso de que una capa tenga polilineas cerradas,
		 * y el usuario visualmente piensa que son poligonos.
		 * 
		 * Podemos considerar aquí este caso.
		 * 
		 * */
		if(g.getGeometryType() != XTypes.POLYGON &&
				g.getGeometryType() != XTypes.MULTI)
			return;
		
		Geometry actualGeometry = g.toJTSGeometry();
		if(geometry == null){
			geometry = actualGeometry;
		}else{
			Geometry[] geoms = new Geometry[2];
			geoms[0] = geometry;
			geoms[1] = actualGeometry;
			
			geometry = JTSFacade.union(geoms, geometryType);
		}
	}
}

