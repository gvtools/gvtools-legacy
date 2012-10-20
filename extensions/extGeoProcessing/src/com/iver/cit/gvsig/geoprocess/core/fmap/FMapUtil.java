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
package com.iver.cit.gvsig.geoprocess.core.fmap;

import java.util.ArrayList;
import java.util.Arrays;

import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

/**
 * Utility methods to work with FMap
 * 
 * @author Alvaro Zabala
 * 
 */
public class FMapUtil {
	/**
	 * Filters TOC layers to get only FLyrVect layers.
	 * 
	 * @param layers
	 * @return
	 */
	public static FLyrVect[] getVectorialLayers(FLayers layers) {
		FLyrVect[] solution = null;
		ArrayList<FLyrVect> list = new ArrayList<FLyrVect>();
		int numLayers = layers.getLayersCount();
		for (int i = 0; i < numLayers; i++) {
			FLayer layer = layers.getLayer(i);
			if (layer instanceof FLyrVect)
				list.add((FLyrVect) layer);
			else if (layer instanceof FLayers)
				list.addAll(Arrays.asList(getVectorialLayers((FLayers) layer)));
		}
		solution = new FLyrVect[list.size()];
		list.toArray(solution);
		return solution;

	}

	public static String[] getLayerNames(FLayers layers) {
		String[] solution = null;
		int numLayers = layers.getLayersCount();
		if (layers != null && numLayers > 0) {
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < numLayers; i++) {
				FLayer layer = layers.getLayer(i);
				if (layer instanceof FLyrVect)
					list.add(layer.getName());
				if (layer instanceof FLayers) {
					FLayers tempLayers = (FLayers) layer;
					FLyrVect[] vectorials = FMapUtil
							.getVectorialLayers(tempLayers);
					for (int j = 0; j < vectorials.length; j++) {
						list.add(vectorials[j].getName());
					}
				}
			}// for
			solution = new String[list.size()];
			list.toArray(solution);
		}
		return solution;
	}

}
