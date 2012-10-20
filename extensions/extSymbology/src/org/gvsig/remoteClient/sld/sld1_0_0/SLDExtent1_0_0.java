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
package org.gvsig.remoteClient.sld.sld1_0_0;

import java.io.IOException;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.gvsig.remoteClient.sld.SLDExtent;
import org.gvsig.remoteClient.sld.SLDTags;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Implements the Extent element of an SLD implementation specification (version
 * 1.0.0).
 * <p>
 * When used in a UserLayer (<code>SLDUserDefinedLayer</code>), it defines what
 * features are to be included in the layer and when used in a NamedLayer (
 * <code>SLDNamedLayer</code>) , it filters the features that are part of the
 * named layer.
 * 
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class SLDExtent1_0_0 extends SLDExtent {

	public void parse(XMLSchemaParser parser, int cuTag, String expressionType)
			throws IOException, XmlPullParserException {
		int currentTag;
		boolean end = false;

		parser.require(XMLSchemaParser.START_TAG, null, SLDTags.EXTENT);
		currentTag = parser.next();

		while (!end) {
			switch (currentTag) {
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(SLDTags.NAME) == 0) {
					setName(parser.nextText());
				} else if (parser.getName().compareTo(SLDTags.VALUE) == 0) {
					setValue(parser.nextText());
				}
				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(SLDTags.EXTENT) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}
		parser.require(XMLSchemaParser.END_TAG, null, SLDTags.EXTENT);
	}

	public String toXML() {
		// TODO Auto-generated method stub
		throw new Error("Not yet implemented");
	}

}
