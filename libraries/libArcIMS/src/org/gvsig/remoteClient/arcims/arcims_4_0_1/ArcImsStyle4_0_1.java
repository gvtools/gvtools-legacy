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

package org.gvsig.remoteClient.arcims.arcims_4_0_1;

import java.io.IOException;

import org.gvsig.remoteClient.utils.CapabilitiesTags;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * <p>
 * Represents the layer style defined by the Specifications for ArcIms 4.0.1
 * </p>
 * 
 */
public class ArcImsStyle4_0_1 extends org.gvsig.remoteClient.arcims.ArcImsStyle {
	/**
	 * <p>
	 * URL pointing to the legend for a layer with this style
	 * </p>
	 */

	/**
	 * <p>
	 * Parses the STYLE TAG according with the OGC Specifications for the ArcIms
	 * 1.1.1
	 * </p>
	 */
	public void parse(KXmlParser parser) throws IOException,
			XmlPullParserException {
		int currentTag;
		boolean end = false;

		parser.require(KXmlParser.START_TAG, null, CapabilitiesTags.STYLE);
		currentTag = parser.nextTag();

		while (!end) {
			switch (currentTag) {
			case KXmlParser.START_TAG:

				if (parser.getName().compareTo(CapabilitiesTags.NAME) == 0) {
					setName(parser.nextText());
				} else if (parser.getName().compareTo(CapabilitiesTags.TITLE) == 0) {
					setTitle(parser.nextText());
				} else if (parser.getName()
						.compareTo(CapabilitiesTags.ABSTRACT) == 0) {
					setAbstract(parser.nextText());
				} else if (parser.getName().compareTo(
						CapabilitiesTags.LEGENDURL) == 0) {
					break;
				}

			case KXmlParser.END_TAG:

				if (parser.getName().compareTo(CapabilitiesTags.STYLE) == 0) {
					end = true;
				}

				break;

			case KXmlParser.TEXT:
				break;
			}

			if (!end) {
				currentTag = parser.next();
			}
		}

		parser.require(KXmlParser.END_TAG, null, CapabilitiesTags.STYLE);
	}
}
