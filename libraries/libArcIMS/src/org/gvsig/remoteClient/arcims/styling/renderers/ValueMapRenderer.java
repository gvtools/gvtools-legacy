/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */

/**
 *
 */
package org.gvsig.remoteClient.arcims.styling.renderers;

import java.util.ArrayList;
import java.util.Iterator;

import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;

/**
 * @author jsanz
 * 
 */
public class ValueMapRenderer extends Renderer {
	public static final String TAG = ServiceInfoTags.tVALUEMAPRENDERER;
	protected String lookupField;
	protected ArrayList values;

	/**
	 * @param lookupField
	 */
	public ValueMapRenderer(String lookupfield) {
		this.lookupField = lookupfield;
		this.values = new ArrayList();
	}

	/**
	 * @return Returns a Value
	 */
	public TypeValueMap getValue(int index) {
		return (TypeValueMap) values.get(index);
	}

	/**
	 * @return Returns a Value
	 */
	public ArrayList getValues() {
		return values;
	}

	/**
	 * @param value
	 *            Add a value into the Array
	 */
	public boolean addType(TypeValueMap value) {
		return values.add(value);
	}

	/**
	 * @return Number of types
	 */
	public int valuesCount() {
		return values.size();
	}

	/**
	 * Generates an XML representation of the Renderer
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator it = values.iterator();

		while (it.hasNext()) {
			sb.append(((TypeValueMap) it.next()).toString());
		}

		return "<" + ValueMapRenderer.TAG + " lookupfield=\"" + lookupField
				+ "\">\r\n" + sb.toString() + "</" + ValueMapRenderer.TAG
				+ ">\r\n";
	}

	/**
	 * @return Returns the lookupField.
	 */
	public String getLookupfield() {
		return lookupField;
	}

	/**
	 * @param lookupField
	 *            The lookupField to set.
	 */
	public void setLookupfield(String lookupfield) {
		this.lookupField = lookupfield;
	}

	/**
	 * @param values
	 *            The values to set.
	 */
	public void setValues(ArrayList values) {
		this.values = values;
	}
}
