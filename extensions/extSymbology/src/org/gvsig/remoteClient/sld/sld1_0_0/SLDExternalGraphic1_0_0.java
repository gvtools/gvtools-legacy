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
import java.net.URL;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.gvsig.remoteClient.sld.SLDExternalGraphic;
import org.gvsig.remoteClient.sld.SLDTags;
import org.gvsig.remoteClient.sld.filterEncoding.FilterTags;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.rendering.XmlBuilder;

/**
 * Implements the ExternalGraphic element of an SLD implementation specification
 * (version 1.0.0).<p>
 * The ExternalGraphic element allows a reference to be made to an external graphic
 * file with a Web URL. The onlineResource sub-element gives the URL and the format
 * sub-element identifies the expected document MIME type of a succesful fetch. Knowing
 * the MIME type in advance allows the styler to select the best-supported format
 * from the list of URLs with the equivalent content. Users should avoid referencing
 * external graphics that may change at arbitrary times. Graphic should be static
 * when al all possible.
 *
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 *
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
*/
public class SLDExternalGraphic1_0_0 extends SLDExternalGraphic {

	
	public void parse(XMLSchemaParser parser, int cuTag, String expressionType)throws IOException, XmlPullParserException  {
		int currentTag;
		boolean end = false;

		parser.require(XMLSchemaParser.START_TAG, null, SLDTags.EXTERNALGRAPHIC);
		currentTag = parser.next();

		while (!end)
		{
			switch(currentTag)
			{
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(SLDTags.ONLINE_RESOURCE)==0) {
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						if (parser.getAttributeName(i).compareTo(SLDTags.XLINK_HREF) == 0) {
							URL myURL = new URL(parser.getAttributeValue(i));
							addOnlineResource(myURL);
						}
					}
				}
				else if (parser.getName().compareTo(SLDTags.FORMAT)==0) {
					setFormat(parser.nextText());
				}
				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(SLDTags.EXTERNALGRAPHIC) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		parser.require(XMLSchemaParser.END_TAG, null, SLDTags.EXTERNALGRAPHIC);

	}

	public String toXML() {
		XmlBuilder xmlBuilder = new XmlBuilder();
		xmlBuilder.openTag(SLDTags.EXTERNALGRAPHIC);
		xmlBuilder.writeRaw("<"+SLDTags.ONLINE_RESOURCE+" "+SLDTags.XLINK_HREF+"= \""+
				getOnlineResource().get(0)+"\"/>");
		if(getFormat() != null) {
			xmlBuilder.writeTag(SLDTags.FORMAT, getFormat());
		}
		xmlBuilder.closeTag();
		return xmlBuilder.getXML();
	}
}
