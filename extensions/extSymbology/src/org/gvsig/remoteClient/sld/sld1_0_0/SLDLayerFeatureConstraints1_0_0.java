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
import org.gvsig.remoteClient.sld.SLDLayerFeatureConstraints;
import org.gvsig.remoteClient.sld.SLDTags;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;

/**
 * Implements the LayerFeatureConstraints element of an SLD implementation
 * specification (version 1.0.0).
 * <p>
 * The LayerFeatureConstraints element is used to specify what features of what
 * feature types are to be rendered in a layer.
 * <p>
 * 
 * @see SLDFeatureTypeConstraint1_0_0
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class SLDLayerFeatureConstraints1_0_0 extends SLDLayerFeatureConstraints {

	public void parse(XMLSchemaParser parser, int cuTag, String expressionType)
			throws IOException, XmlPullParserException, LegendDriverException {
		int currentTag;
		boolean end = false;

		parser.require(XMLSchemaParser.START_TAG, null,
				SLDTags.LAYER_FEATURE_CONST);
		currentTag = parser.next();

		while (!end) {
			switch (currentTag) {
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(SLDTags.FEATURETYPECONSTRAINT) == 0) {
					SLDFeatureTypeConstraint1_0_0 featTypeCons = new SLDFeatureTypeConstraint1_0_0();
					featTypeCons.parse(parser, currentTag, null);
					if (featTypeCons.getFilter() == null)
						throw new LegendDriverException(
								LegendDriverException.PARSE_LEGEND_FILE_ERROR);
					addFeatureTypeConstraint(featTypeCons);
				}
				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(SLDTags.LAYER_FEATURE_CONST) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		parser.require(XMLSchemaParser.END_TAG, null,
				SLDTags.LAYER_FEATURE_CONST);
	}

	public String toXML() {
		// TODO Auto-generated method stub
		throw new Error("Not yet implemented");
	}

}
