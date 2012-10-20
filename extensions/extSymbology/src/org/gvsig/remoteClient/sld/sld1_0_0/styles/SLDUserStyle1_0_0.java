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
package org.gvsig.remoteClient.sld.sld1_0_0.styles;

import java.io.IOException;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.gvsig.remoteClient.sld.SLDTags;
import org.gvsig.remoteClient.sld.sld1_0_0.SLDFeatureTypeStyle1_0_0;
import org.gvsig.remoteClient.sld.styles.SLDUserStyle;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;

/**
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class SLDUserStyle1_0_0 extends SLDUserStyle {

	public void parse(XMLSchemaParser parser) throws IOException,
			XmlPullParserException, LegendDriverException {
		int currentTag;
		boolean end = false;

		parser.require(XMLSchemaParser.START_TAG, null, SLDTags.USERSTYLE);
		currentTag = parser.next();

		while (!end) {
			switch (currentTag) {
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(SLDTags.NAME) == 0) {
					setName(parser.nextText());
				} else if (parser.getName().compareTo(SLDTags.TITLE) == 0) {
					setTitle(parser.nextText());
				} else if (parser.getName().compareTo(SLDTags.ABSTRACT) == 0) {
					setUStyleAbstract(parser.nextText());
				} else if (parser.getName().compareTo(SLDTags.IS_DEFAULT) == 0) {
					String value = parser.nextText();
					if (value.compareTo("1") != 0 && value.compareTo("0") != 0)
						throw new LegendDriverException(
								LegendDriverException.PARSE_LEGEND_FILE_ERROR);
					else if (value.compareTo("1") == 0)
						setDefault(true);

				} else if (parser.getName().compareTo(SLDTags.FEATURETYPESTYLE) == 0) {
					SLDFeatureTypeStyle1_0_0 fTypeStyle = new SLDFeatureTypeStyle1_0_0();
					fTypeStyle.parse(parser, currentTag, null);
					addFeatureTypeStyle(fTypeStyle);
				}
				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(SLDTags.USERSTYLE) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		parser.require(XMLSchemaParser.END_TAG, null, SLDTags.USERSTYLE);

	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		throw new Error("Not yet implemented");
	}

}
