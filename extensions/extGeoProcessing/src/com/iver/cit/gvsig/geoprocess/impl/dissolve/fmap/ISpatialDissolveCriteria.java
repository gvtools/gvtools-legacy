/*
 * Created on 22-may-2006
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
* $Id: ISpatialDissolveCriteria.java 5918 2006-06-20 18:20:45Z azabala $
* $Log$
* Revision 1.1  2006-06-20 18:20:45  azabala
* first version in cvs
*
* Revision 1.1  2006/05/24 21:11:14  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.dissolve.fmap;

import org.opengis.referencing.operation.MathTransform;

import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Forces to IDissolveCriteria to fetch geometries from 
 * ReadableVectorial (for performance reasons, once a criteria
 * has verified a spatial conditions, it caches IGeometry, to
 * avoid reading them many times)
 * @author azabala
 *
 */
public interface ISpatialDissolveCriteria {
	public IGeometry getFirstGeometry();
	public void setFirstGeometry(IGeometry g);
	public void setFirstJts(Geometry g);
	public void setSecondJts(Geometry g);
	public Geometry getSecondJts();
	
	public void setSecondGeometry(IGeometry g);
	public IGeometry getSecondGeometry();
	public void setCrsTransform(MathTransform crsTransform);
}

