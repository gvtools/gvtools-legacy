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
package org.gvsig.remoteClient.sld.sld1_0_0.layers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.gvsig.remoteClient.sld.SLDTags;
import org.gvsig.remoteClient.sld.filterEncoding.Filter;
import org.gvsig.remoteClient.sld.layers.SLDUserLayer;
import org.gvsig.remoteClient.sld.sld1_0_0.SLDFeatureTypeStyle1_0_0;
import org.gvsig.remoteClient.sld.sld1_0_0.SLDLayerFeatureConstraints1_0_0;
import org.gvsig.remoteClient.sld.sld1_0_0.SLDRule1_0_0;
import org.gvsig.remoteClient.sld.sld1_0_0.styles.SLDUserStyle1_0_0;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;

/**
 * Implements the User-Defined Layer element of an SLD implementation
 * specification (version 1.0.0).
 * <p>
 * 
 * In addition to using named layers, it is also useful to be able to define
 * custom user-defined layers for rendering.
 * <p>
 * Since a layer is defined as a collection of potentially mixed-type features,
 * the UserLayer element must provide the means to identify the features to be
 * used. All features to be rendered are assumed to be fetched from a Web
 * Feature Server (WFS) or a Web Coverage Service (WCS, in which case the term
 * �features� is used loosely).
 * 
 * The remote server to be used is identified by RemoteOWS (OGC Web Service)
 * element.
 * 
 * @see SLDLayer
 * @see SLDNamedLayer1_0_0
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class SLDUserLayer1_0_0 extends SLDUserLayer {

	/**
	 * Parses the xml data retrieved from the SLD, it will parse the
	 * User-Defined Layer element</p>
	 * 
	 * @throws LegendDriverException
	 */
	public void parse(XMLSchemaParser parser) throws IOException,
			XmlPullParserException, LegendDriverException {

		int currentTag;
		boolean end = false;

		parser.require(XMLSchemaParser.START_TAG, null,
				SLDTags.USERDEFINEDLAYER);
		currentTag = parser.next();

		while (!end) {
			switch (currentTag) {
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(SLDTags.NAME) == 0) {
					setName(parser.nextText());
				} else if (parser.getName().compareTo(SLDTags.REMOTE_OWS) == 0) {
					parseRemoteOWS(parser);
				} else if (parser.getName().compareTo(
						SLDTags.LAYER_FEATURE_CONST) == 0) {
					SLDLayerFeatureConstraints1_0_0 sldLayerFeatCons = new SLDLayerFeatureConstraints1_0_0();
					sldLayerFeatCons.parse(parser, currentTag, null);
					addLayerFeatureConstraint(sldLayerFeatCons);
				} else if (parser.getName().compareTo(SLDTags.USERSTYLE) == 0) {
					SLDUserStyle1_0_0 userStyle = new SLDUserStyle1_0_0();
					userStyle.parse(parser);
					addLayerStyle(userStyle);
				}
				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(SLDTags.USERDEFINEDLAYER) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		parser.require(XMLSchemaParser.END_TAG, null, SLDTags.USERDEFINEDLAYER);

	}

	/**
	 * Parses the xml data retrieved from the SLD, it will parse the RemoteOWS
	 * element which identifies the remote server to be used</p>
	 * 
	 * @param parser
	 * @throws IOException
	 * @throws XmlPullParserException
	 */

	private void parseRemoteOWS(XMLSchemaParser parser) throws IOException,
			XmlPullParserException {

		int currentTag;
		boolean end = false;

		parser.require(XMLSchemaParser.START_TAG, null, SLDTags.REMOTE_OWS);
		currentTag = parser.next();

		while (!end) {
			switch (currentTag) {
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(SLDTags.SERVICE) == 0) {
					setRemoteOWSService(parser.nextText());
				} else if (parser.getName().compareTo(SLDTags.ONLINE_RESOURCE) == 0) {
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						if (parser.getAttributeName(i).compareTo(
								SLDTags.XLINK_HREF) == 0) {
							URL myURL = new URL(parser.getAttributeValue(i));
							addRemoteOWSOnlineResource(myURL);
						}
					}
				}
				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(SLDTags.REMOTE_OWS) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		parser.require(XMLSchemaParser.END_TAG, null, SLDTags.REMOTE_OWS);

	}

	public ArrayList<Filter> getLayerFilters() {
		ArrayList<Filter> layerFilter = new ArrayList<Filter>();
		for (int i = 0; i < this.getLayerStyles().size(); i++) {
			SLDUserStyle1_0_0 userStyle = (SLDUserStyle1_0_0) this
					.getLayerStyles().get(i);
			for (int j = 0; j < userStyle.getFeatureTypeStyle().size(); j++) {
				SLDFeatureTypeStyle1_0_0 featTypeStyle = (SLDFeatureTypeStyle1_0_0) userStyle
						.getFeatureTypeStyle().get(j);
				for (int k = 0; k < featTypeStyle.getRules().size(); k++) {
					SLDRule1_0_0 rule = (SLDRule1_0_0) featTypeStyle.getRules()
							.get(k);
					layerFilter.add(rule.getFilter());
				}
			}
		}
		return layerFilter;
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		throw new Error("Not yet implemented");
	}

}
