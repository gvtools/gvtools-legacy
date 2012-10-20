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
package org.gvsig.topology.ui;

import javax.swing.JComboBox;

import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayersIterator;

/**
 * JComboBox to show the layers of a FLayers instance.
 * 
 * It can filter layers by the specified criteria.
 * 
 * 
 * @author Alvaro Zabala
 * 
 *         TODO Si FLayer.toString() devuelve el resultado de FLayer.getName()
 *         la clase se puede simplificar muchisimo
 * 
 */
public class LayerJComboBox extends JComboBox {

	private static final long serialVersionUID = 4417161682529079817L;

	/**
	 * Interface to filter layers in the combo box.
	 * 
	 * @author Alvaro Zabala
	 * 
	 */
	public interface LayerFilter {
		public boolean filter(FLayer layer);
	}

	/**
	 * Filter that filters vectorial layers.
	 */
	public static final LayerFilter FLYRVECTFILTER = new LayerFilter() {
		public boolean filter(FLayer layer) {
			if (layer instanceof FLyrVect) {
				return true;
			} else {
				return false;
			}
		}
	};

	/**
	 * Filter for this combobox. If null doesnt filter anything.
	 */
	private LayerFilter layerFilter;

	private FLayers layers;

	/**
	 * Constructor.
	 * 
	 * @param layers
	 */
	public LayerJComboBox(FLayers layers) {
		this(layers, FLYRVECTFILTER);
	}

	public LayerJComboBox(FLayers layers, LayerFilter filter) {
		super();
		this.layers = layers;
		this.layerFilter = filter;
		initialize();
	}

	private void initialize() {
		LayersIterator lyrIterator = new LayersIterator(layers);
		while (lyrIterator.hasNext()) {
			FLayer layer = lyrIterator.nextLayer();
			if (layerFilter != null) {
				if (!layerFilter.filter(layer)) {
					continue;
				}
			}
			addItem(layer.getName());
		}// while
	}

	public FLayer getSelectedLayer() {
		return layers.getLayer((String) getSelectedItem());
	}

	public void setLayers(FLayers layers) {
		this.layers = layers;
		this.removeAllItems();
		initialize();
	}

	public FLayers getLayers() {
		return layers;
	}
}
