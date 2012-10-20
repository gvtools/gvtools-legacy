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
 * $Id: ConvexHullVisitor.java 5918 2006-06-20 18:20:45Z azabala $
 * $Log$
 * Revision 1.1  2006-06-20 18:20:45  azabala
 * first version in cvs
 *
 * Revision 1.1  2006/05/24 21:13:31  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.1  2006/03/06 19:48:39  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/17 16:32:50  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.convexhull.fmap;

import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor;
import com.vividsolutions.jts.geom.Geometry;

public interface ConvexHullVisitor extends FeatureVisitor {
	public IGeometry getConvexHull();

	public Geometry getJtsConvexHull();
}
