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
package com.iver.cit.gvsig.referencing;

import java.util.List;

import org.geotools.referencing.operation.builder.MappedPosition;
import org.gvsig.exceptions.BaseException;
import org.gvsig.referencing.LineLyrAdapter;
import org.gvsig.referencing.MappedPositionContainer;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.cit.gvsig.fmap.layers.FLyrVect;

/**
 * Adapter class to provides mapped position links from a vectorial line layer.
 * Caution! Its a read only MappedPositionContainer. It launchs runtime
 * exception if try to use writing operations.
 * 
 * If layer's linear geometries have more than two points, only will be
 * consideer the two first points.
 * 
 * @author Alvaro Zabala
 * 
 */
public class MappedPositionContainerLayerBased implements
		MappedPositionContainer {

	private FLyrVect lineLyr;
	private LineLyrAdapter listOfMapped;
	private boolean hasBeenAddedToToc = false;

	public MappedPositionContainerLayerBased(FLyrVect layer)
			throws BaseException {
		this.lineLyr = layer;
		this.listOfMapped = new LineLyrAdapter(layer);
	}

	public void addMappedPosition(MappedPosition mappedPosition) {
		throw new UnsupportedOperationException();
	}

	public void delete(int idx) {
		throw new UnsupportedOperationException();
	}

	public boolean existsLinksLyr() {
		return hasBeenAddedToToc;
	}

	public List<MappedPosition> getAsList() {
		return listOfMapped;
	}

	public int getCount() {
		return listOfMapped.size();
	}

	public FLyrVect getLinkLyr(CoordinateReferenceSystem crs) {
		FLyrVect solution = null;
		try {
			solution = (FLyrVect) lineLyr.cloneLayer();
			solution.setCrs(crs);
			hasBeenAddedToToc = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return solution;
	}

	public MappedPosition getMappedPosition(int idx) {
		return listOfMapped.get(idx);
	}

}
