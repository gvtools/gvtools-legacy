/*
 * Created on 09-feb-2006
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
* $Id: CacherJTSProcessor.java 5412 2006-05-24 21:15:07Z azabala $
* $Log$
* Revision 1.1  2006-05-24 21:12:16  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.2  2006/02/20 19:44:01  azabala
* *** empty log message ***
*
* Revision 1.1  2006/02/17 16:33:25  azabala
* *** empty log message ***
*
* Revision 1.2  2006/02/12 21:02:58  azabala
* *** empty log message ***
*
* Revision 1.1  2006/02/09 16:00:36  azabala
* First version in CVS
*
*
*/
package com.iver.cit.gvsig.geoprocess.core.fmap;

import java.io.IOException;

import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.geoprocess.core.util.SpatialCache;
import com.iver.cit.gvsig.geoprocess.core.util.SpatialCacheImpl;
import com.vividsolutions.jts.geom.Geometry;
/**
 * Processes individual geometries procedent from a geoprocessing
 * operation, by caching them in a multilevel memory/file cache.
 * 
 *  TODO Maybe, we could work with a VectorialEditableAdapter 
 *  (spatially indexed) and save results in a third layer
 * @author azabala
 * 
 * TODO DEPRECAR!!!
 * @deprecated
 *
 */
public class CacherJTSProcessor implements GeoprocessingResultsProcessor {
	/**
	 * It caches JTS geometries (to process them
	 * in a second pass)
	 */
	private SpatialCache spatialCache;
	
	public CacherJTSProcessor() throws GeoprocessException{
		try {
			this.spatialCache = SpatialCacheImpl.getInstance();
		} catch (IOException e) {
			throw new GeoprocessException("Problemas para guardar la capa de salida");
		}
	}
	
	/**
	 * Caches a geometry procedent of a buffer operation
	 */
	public void processJtsGeometry(Geometry g, int index) {
		spatialCache.put((long)index, 
				g.getEnvelopeInternal(), 
				g);

	}
	
	public SpatialCache getSpatialCache(){
		return spatialCache;
	}

	public void finish() {
	}

	//Se guarda la IGeometry
	public void processFeature(IFeature feature) {
	}

}

