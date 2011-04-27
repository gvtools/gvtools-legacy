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
package org.gvsig.remoteClient.sld.sld1_0_0.layers;

import java.io.IOException;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.gvsig.remoteClient.sld.SLDTags;
import org.gvsig.remoteClient.sld.layers.SLDNamedLayer;
import org.gvsig.remoteClient.sld.sld1_0_0.SLDLayerFeatureConstraints1_0_0;
import org.gvsig.remoteClient.sld.sld1_0_0.styles.SLDNamedStyle1_0_0;
import org.gvsig.remoteClient.sld.sld1_0_0.styles.SLDUserStyle1_0_0;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;

/**
 * Implements the NamedLayer element of an SLD implementation specification (version 
 * 1.0.0).<p>
 * 
 * A named layer is a layer that can be accessed from an OpenGIS Web
 * Server using a well-known name.<p>
 * 
 * @see SLDLayer
 * @see SLDUserLayer1_0_0
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class SLDNamedLayer1_0_0 extends SLDNamedLayer {


	/**
	 * Parses the xml data retrieved from the SLD, it will parse the NamedLayer 
	 * element</p>
	 * @throws LegendDriverException 
	 */
	public void parse(XMLSchemaParser parser)throws IOException, XmlPullParserException, LegendDriverException {
		int currentTag;
		boolean end = false;

		parser.require(XMLSchemaParser.START_TAG, null, SLDTags.NAMEDLAYER);
		currentTag = parser.next();

		while (!end)
		{
			switch(currentTag)
			{
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(SLDTags.NAME)==0) {
					setName(parser.nextText());
				}
				else if (parser.getName().compareTo(SLDTags.LAYER_FEATURE_CONST)==0) {
					SLDLayerFeatureConstraints1_0_0 sldLayerFeatCons = new SLDLayerFeatureConstraints1_0_0();
					sldLayerFeatCons.parse(parser,currentTag,null);
					addLayerFeatureConstraint(sldLayerFeatCons);

				}
				else if (parser.getName().compareTo(SLDTags.NAMEDSTYLE)==0) {
					SLDNamedStyle1_0_0 namedStyle = new SLDNamedStyle1_0_0();
					namedStyle.parse(parser);
					addLayerStyle(namedStyle);
				}
				else if (parser.getName().compareTo(SLDTags.USERSTYLE)==0) {
					SLDUserStyle1_0_0 userStyle = new SLDUserStyle1_0_0();
					userStyle.parse(parser);
					addLayerStyle(userStyle);
				}
				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(SLDTags.NAMEDLAYER) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		parser.require(XMLSchemaParser.END_TAG, null, SLDTags.NAMEDLAYER);

	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		throw new Error ("Not yet implemented");
	}


}
