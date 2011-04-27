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
package org.gvsig.graph.core.writers;

import org.gvsig.graph.core.INetworkWriter;

import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

public abstract class AbstractNetworkWriter implements INetworkWriter {

	protected FLyrVect lyr;
	protected String fieldType;
	protected String fieldDist;
	protected String fieldCost;
	protected String fieldSense;
	protected FieldDescription[] nodeFields;
	protected FieldDescription[] edgeFields;

	public FLyrVect getLayer() {
		return lyr;
	}

	public void setLayer(FLyrVect lyr) {
		this.lyr = lyr;
	}

	public String getFieldCost() {
		return fieldCost;
	}

	public void setFieldCost(String fieldCost) {
		this.fieldCost = fieldCost;
	}

	public String getFieldDist() {
		return fieldDist;
	}

	public void setFieldDist(String fieldDist) {
		this.fieldDist = fieldDist;
	}

	public String getFieldSense() {
		return fieldSense;
	}

	public void setFieldSense(String fieldSense) {
		this.fieldSense = fieldSense;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

}


