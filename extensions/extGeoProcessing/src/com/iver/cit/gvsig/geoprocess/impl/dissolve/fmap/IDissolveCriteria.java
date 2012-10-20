/*
 * Created on 12-may-2006
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
 * $Id: IDissolveCriteria.java 10626 2007-03-06 16:55:54Z caballero $
 * $Log$
 * Revision 1.2  2007-03-06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.1  2006/06/20 18:20:45  azabala
 * first version in cvs
 *
 * Revision 1.1  2006/05/24 21:11:14  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.dissolve.fmap;

import java.util.List;
import java.util.Map;

import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;

/**
 * Implementations has the responsability of decide if Dissolver must dissolve
 * two features. <br>
 * Because this decision could depend of one (or many) attribute values of
 * compared features, and resulting features of dissolve must have all
 * dissolving fields, implementations of this interface must have the
 * responsability of creating new resulting features of a dissolve.
 * 
 * @author azabala
 * 
 */
public interface IDissolveCriteria {
	/**
	 * Returns if we must dissolve feature of index "featureindex1" with feature
	 * of index "featureindex2"
	 * 
	 * @param featureIndex1
	 * @param featureIndex2
	 * @return
	 */
	public boolean verifyIfDissolve(int featureIndex1, int featureIndex2);

	/**
	 * Builds features from a dissolve operation.
	 * 
	 * @author azabala
	 * 
	 */
	public interface IDissolvedFeatureBuilder {
		/**
		 * Create a new feature from geometry g, and the index of the 'seed'
		 * (first feature of the dissolve group) feature (useful for fetching
		 * dissolve fields value, etc.)
		 * 
		 * @param g
		 * @param index
		 * @param fid
		 * @return
		 */
		public IFeature createFeature(IGeometry g, int index, int fid);

		/**
		 * 
		 * @param newGeometry
		 * @param sumarizedValues
		 * @param newFid
		 * @param index
		 * @return
		 */
		public IFeature createFeature(IGeometry newGeometry,
				List sumarizedValues, int newFid, int index);
	}

	public IDissolvedFeatureBuilder getFeatureBuilder();

	/**
	 * Clear all state information about dissolving (usually a criteria could
	 * cache dissolve information to avoid reading dissolve fields value always)
	 * 
	 */
	public void clear();

	/**
	 * Returns a LayerDefinition for the result layer of those dissolve
	 * geoprocesses where this criteria applies.
	 * 
	 * @return
	 */
	public ILayerDefinition createLayerDefinition(Map numFields_SumFunc);
}
