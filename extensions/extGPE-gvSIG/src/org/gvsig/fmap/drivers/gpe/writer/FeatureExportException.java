package org.gvsig.fmap.drivers.gpe.writer;

import java.util.HashMap;
import java.util.Map;

import org.gvsig.fmap.drivers.gpe.exceptions.GPELayerException;

import com.iver.cit.gvsig.fmap.layers.FLayer;

/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * $Id$
 * $Log$
 *
 */
/**
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class FeatureExportException extends GPELayerException {
	private static final long serialVersionUID = -7643617950034530635L;
	private int index = 0;

	public FeatureExportException(FLayer layer, int index, Throwable exception) {
		super(layer, exception);
		this.index = index;
		initialize();
	}

	/**
	 * Initialize the properties
	 */
	private void initialize() {
		messageKey = "gpe_gvsig_export_feature_exception";
		formatString = "Impossible to export the feature %(index) "
				+ "for the layer %(layerName).";
		code = serialVersionUID;
	}

	protected Map values() {
		HashMap params = (HashMap) super.values();
		params.put("index", String.valueOf(index));
		return params;
	}

}
