/*
 * Created on 03-feb-2006
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
 * $Id: SpatialCache.java 5412 2006-05-24 21:15:07Z azabala $
 * $Log$
 * Revision 1.1  2006-05-24 21:12:36  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.2  2006/03/21 19:30:56  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/09 15:59:24  azabala
 * First version in CVS
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.util;

import java.io.Serializable;
import java.util.List;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Base interface to model Spatial Caches: a cache which allows to retrieve
 * objets by alphanumeric criteria or by spatial criteria. All cached objets
 * must be serializable, so it could be saved to disk if they havent been used
 * recently.
 * 
 * @author azabala
 * 
 */
public interface SpatialCache {
	public Serializable getElement(long idx);

	public List getCandidatesIndexes(Envelope rect);

	public void remove(Envelope rect, Serializable object);

	public void put(long idx, Envelope env, Serializable object);

	public List getKeys();
}
