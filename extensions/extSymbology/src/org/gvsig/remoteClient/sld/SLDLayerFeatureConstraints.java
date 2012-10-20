/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
package org.gvsig.remoteClient.sld;

import java.util.ArrayList;

/**
 * Implements the LayerFeatureConstraints element of an SLD implementation
 * specification .
 * <p>
 * The LayerFeatureConstraints element is used to specify what features of what
 * feature types are to be rendered in a layer.
 * <p>
 * 
 * @see SLDFeatureTypeConstraint
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */

public abstract class SLDLayerFeatureConstraints implements ISLDFeatures {

	protected ArrayList<SLDFeatureTypeConstraint> featureTypeConstraint = new ArrayList<SLDFeatureTypeConstraint>();

	public ArrayList<SLDFeatureTypeConstraint> getFeatureTypeConstraint() {
		return featureTypeConstraint;
	}

	public void setFeatureTypeConstraint(
			ArrayList<SLDFeatureTypeConstraint> featureTypeConstraint) {
		this.featureTypeConstraint = featureTypeConstraint;
	}

	public void addFeatureTypeConstraint(SLDFeatureTypeConstraint feat) {
		this.featureTypeConstraint.add(feat);
	}

}
