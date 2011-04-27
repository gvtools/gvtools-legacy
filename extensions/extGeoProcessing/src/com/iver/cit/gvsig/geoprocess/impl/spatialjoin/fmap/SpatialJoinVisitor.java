/*
 * Created on 01-mar-2006
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
* $Id: SpatialJoinVisitor.java 5918 2006-06-20 18:20:45Z azabala $
* $Log$
* Revision 1.1  2006-06-20 18:20:45  azabala
* first version in cvs
*
* Revision 1.1  2006/05/24 21:09:47  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.4  2006/03/15 18:34:31  azabala
* *** empty log message ***
*
* Revision 1.3  2006/03/14 18:32:46  fjp
* Cambio con LayerDefinition para que sea compatible con la definición de tablas también.
*
* Revision 1.2  2006/03/07 21:01:33  azabala
* *** empty log message ***
*
* Revision 1.1  2006/03/06 19:48:39  azabala
* *** empty log message ***
*
* Revision 1.1  2006/03/05 19:59:47  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.spatialjoin.fmap;

import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor;
import com.iver.cit.gvsig.fmap.operations.strategies.Strategy;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureProcessor;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;

/**
 * Spatial Join is a geoprocess that tries to relate two layers
 * from its geometry field. To do that it could establish two strategies:
 * <ul>	
 *<li>
 *	Relate a geometry with the nearest geometry of the second layer
 *  (1-1 cardinality).
 * </li>
 * <li>
 *  Relate a geometry with all the geometries of second layer that verifies
 *  a spatial relationship: contained, intersects, etc (1-M cardinality).
 * </li>
 * </ul>
 * SpatialJoinVisitor is a base interface for Visitor that does the
 * process of relationship
 * @author azabala
 *
 */
public interface SpatialJoinVisitor extends FeatureVisitor {
	/**
	 * Responsability to create ILayerDefinition is of the visitor,
	 * because differents visitor will create differents layer definitions
	 * (1-1 join or 1-N join)
	 * @return
	 * @throws GeoprocessException
	 */
	public ILayerDefinition getResultLayerDefinition() throws GeoprocessException;
	public void setFeatureProcessor(FeatureProcessor processor);
	public void setCancelableStrategy(Strategy secondLyrStrategy);
	public void setOnlySecondLyrSelection(boolean onlySecondLayerSelection);
}

