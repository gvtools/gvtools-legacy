/*
 * Created on 10-abr-2006
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
* $Id: 
* $Log: 
*/
package com.iver.cit.gvsig.geoprocess.core;

import com.iver.cit.gvsig.fmap.layers.FLyrVect;

public class PairOfFeatures {
	private FLyrVect firstFeatureLyr;
	private int firstFeatureIdx;
	
	private FLyrVect secondFeatureLyr;
	private int secondFeatureIdx;
	
	public PairOfFeatures(FLyrVect firstFeatureLyr,
			int firstFeatureIdx, FLyrVect secondFeatureLyr, int secondFeatureIdx) {
		this.firstFeatureLyr = firstFeatureLyr;
		this.firstFeatureIdx = firstFeatureIdx;
		this.secondFeatureLyr = secondFeatureLyr;
		this.secondFeatureIdx = secondFeatureIdx;
	}
	
	
	public boolean equals(PairOfFeatures other){
		if(other == null || !(other instanceof PairOfFeatures))
			return false;
			
		if(firstFeatureIdx != other.firstFeatureIdx && firstFeatureIdx != other.secondFeatureIdx)
			return false;
		if(secondFeatureIdx != other.secondFeatureIdx && secondFeatureIdx != other.firstFeatureIdx)
			return false;
		if(! firstFeatureLyr.equals(other.firstFeatureLyr)  && ! firstFeatureLyr.equals(other.secondFeatureLyr))
			return false;
		if(! secondFeatureLyr.equals(other.firstFeatureLyr)  && ! secondFeatureLyr.equals(other.secondFeatureLyr))
			return false;
		return true;
	}
	
	public int hashCode(){
//		int seed = HashCodeUtil.hashCode(HashCodeUtil.SEED, firstFeatureIdx);
//		seed = HashCodeUtil.hashCode(seed, secondFeatureIdx );
//		seed = HashCodeUtil.hashCode(seed, firstFeatureLyr);
//		seed = HashCodeUtil.hashCode(seed, secondFeatureLyr);
//		return seed;
		return 1;
		
	}
}
