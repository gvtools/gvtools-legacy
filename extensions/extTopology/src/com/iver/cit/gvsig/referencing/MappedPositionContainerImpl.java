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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.geotools.referencing.operation.builder.MappedPosition;
import org.gvsig.referencing.MappedPositionContainer;
import org.gvsig.referencing.ReferencingUtil;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.drivers.VectorErrorMemoryDriver;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.fmap.core.ArrowLineSymbol;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.layers.FLayerGenericVectorial;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;
import com.iver.cit.gvsig.fmap.rendering.SingleSymbolLegend;

/**
 * MappedPositionContainer implementation for the spatial adjust tool
 * 
 * @author Alvaro Zabala
 * 
 */
public class MappedPositionContainerImpl implements MappedPositionContainer {

	/**
	 * Digitized vector errors
	 */
	private List<MappedPosition> mappedPositions;

	/**
	 * FLyrVect representation of the error vectors digitized by user
	 */
	private FLyrVect linksLyr;

	public MappedPositionContainerImpl() {
		this.mappedPositions = new ArrayList<MappedPosition>();
	}

	public void addMappedPosition(MappedPosition mappedPosition) {
		mappedPositions.add(mappedPosition);
	}

	public int getCount() {
		return mappedPositions.size();
	}

	public MappedPosition getMappedPosition(int idx) {
		return mappedPositions.get(idx);
	}

	public List<MappedPosition> getAsList() {
		return mappedPositions;
	}

	// FIXME CUANDO AÑADO ESTA CAPA AL TOC, NO SE MUESTRA EL SIMBOLO
	// HAY QUE AÑADIR ALGUNA EXTENSION AL TOC??
	public FLyrVect getLinkLyr(CoordinateReferenceSystem crs) {
		if (linksLyr == null) {
			ReferencingUtil ref = ReferencingUtil.getInstance();
			int numberOfSessions = ref.getNumberOfSpatialAdjustSessions();
			linksLyr = new FLayerGenericVectorial();
			String name = PluginServices.getText(this, "LINKS_SPATIAL_ADJUST")
					+ " " + numberOfSessions;
			ref.incrementAdjustSessions();
			linksLyr.setName(name);
			linksLyr.setCrs(crs);
			((FLayerGenericVectorial) linksLyr)
					.setDriver(new VectorErrorMemoryDriver(name, this));
			try {
				linksLyr.load();

				ILineSymbol defaultSymbol = new ArrowLineSymbol();
				defaultSymbol.setLineColor(Color.RED);

				SingleSymbolLegend defaultLegend = (SingleSymbolLegend) LegendFactory
						.createSingleSymbolLegend(FShape.LINE);
				defaultLegend.setDefaultSymbol(defaultSymbol);
				defaultLegend.setDefaultSymbol(defaultSymbol);

				linksLyr.setLegend(defaultLegend);

			} catch (LoadLayerException e) {
				e.printStackTrace();
			}
		}

		return linksLyr;
	}

	public boolean existsLinksLyr() {
		return linksLyr != null;
	}

	public void delete(int linkIdx) {
		this.mappedPositions.remove(linkIdx);
	}
}
