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

import java.io.File;
import java.io.IOException;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.gvsig.remoteClient.sld.SLDProtocolHandler;
import org.gvsig.remoteClient.sld.SLDTags;
import org.gvsig.remoteClient.sld.sld1_0_0.layers.SLDNamedLayer1_0_0;
import org.gvsig.remoteClient.sld.sld1_0_0.layers.SLDUserLayer1_0_0;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;

/**
 * Implements the main class for the SLD implementation specification (version
 * 1.0.0) which starts the parsing of the styled layer descriptor document in
 * order to store all the information that it has inside and transform it into
 * objects.
 * <p>
 * 
 * An SLD document is defined as a sequence of styled layers.
 * <p>
 * The version attribute gives the SLD version an SLD document, to facilitate
 * backward compatibility with static documents stored in various different
 * versions of an SLD spec.
 * <p>
 * 
 * The Styled layers can correspond to either named layers or user-defined
 * layers, which are described in subsequent sections. There may be any number
 * of either type of styled layer, including zero, mixed in any order.
 * 
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class SLDProtocolHandler1_0_0 extends SLDProtocolHandler {

	public SLDProtocolHandler1_0_0() {
		setVersion("1.0.0");
	}

	public void parse(File f) throws XmlPullParserException, IOException,
			LegendDriverException {
		int tag;
		XMLSchemaParser xmlSchemaParser = null;
		xmlSchemaParser = new XMLSchemaParser();

		xmlSchemaParser.setInput(f);
		xmlSchemaParser.nextTag();
		if (xmlSchemaParser.getEventType() != XmlPullParser.END_DOCUMENT) {

			String value = xmlSchemaParser.getAttributeValue("",
					SLDTags.VERSION_ATTR);
			setVersion(value);

			xmlSchemaParser.require(KXmlParser.START_TAG, null,
					SLDTags.SLD_ROOT);
			tag = xmlSchemaParser.nextTag();

			while (tag != KXmlParser.END_DOCUMENT) {
				switch (tag) {
				case KXmlParser.START_TAG:
					if (xmlSchemaParser.getName().compareTo(
							SLDTags.USERDEFINEDLAYER) == 0) {
						SLDUserLayer1_0_0 lyr = new SLDUserLayer1_0_0();
						lyr.parse(xmlSchemaParser);
						if (lyr.getName() != null && lyr.getName() != "")
							layers.add(lyr);
						else
							throw new LegendDriverException(
									LegendDriverException.LAYER_NAME_NOT_SPECIFIED);
					} else if (xmlSchemaParser.getName().compareTo(
							SLDTags.NAMEDLAYER) == 0) {
						SLDNamedLayer1_0_0 lyr = new SLDNamedLayer1_0_0();
						lyr.parse(xmlSchemaParser);
						if (lyr.getName() != null && lyr.getName() != "")
							layers.add(lyr);
						else
							throw new LegendDriverException(
									LegendDriverException.LAYER_NAME_NOT_SPECIFIED);

					}
					break;
				case KXmlParser.END_TAG:
					break;
				case KXmlParser.TEXT:
					break;
				}
				tag = xmlSchemaParser.next();
			}
			xmlSchemaParser.require(KXmlParser.END_DOCUMENT, null, null);
		}

	}
}
