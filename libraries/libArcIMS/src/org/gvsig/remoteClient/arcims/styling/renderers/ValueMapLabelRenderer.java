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

package org.gvsig.remoteClient.arcims.styling.renderers;

import java.util.Iterator;

import org.gvsig.remoteClient.arcims.styling.symbols.SymbolUtils;
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;

public class ValueMapLabelRenderer extends ValueMapRenderer implements
		ILabelRenderer {
	public static final String TAG = ServiceInfoTags.tVALUEMAPLABELRENDERER;
	private String rotationalangles;
	private String labelfield;

	/**
	 * @param labelField
	 */
	public ValueMapLabelRenderer(String labelField, String alookupField) {
		super(alookupField);
		this.labelfield = labelField;
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

		return "<" + ValueMapLabelRenderer.TAG + " lookupField=\""
				+ lookupField + "\"" + getParam() + "\">\r\n" + sb.toString()
				+ "</" + ValueMapLabelRenderer.TAG + ">\r\n";
	}

	private String getParam() {
		String param = "";

		if (SymbolUtils.isVoid(rotationalangles)) {
			param += (" rotationalangles=\"" + rotationalangles + "\"");
		}

		if (SymbolUtils.isVoid(labelfield)) {
			param += (" labelfield=\"" + labelfield + "\"");
		}

		return param;
	}

	/**
	 * @return Returns the labelField.
	 */
	public String getField() {
		return labelfield;
	}

	/**
	 * @param labelField
	 *            The labelField to set.
	 */
	public void setField(String labelField) {
		this.labelfield = labelField;
	}

	/**
	 * @return Returns the rotationalAngles.
	 */
	public String getRotationalangles() {
		return rotationalangles;
	}

	/**
	 * @param rotationalAngles
	 *            The rotationalAngles to set.
	 */
	public void setRotationalangles(String rotationalangles) {
		this.rotationalangles = rotationalangles;
	}
}
