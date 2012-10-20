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
package org.gvsig.remoteClient.sld.sld1_0_0.symbolizers;

import java.io.IOException;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.gvsig.remoteClient.sld.SLDTags;
import org.gvsig.remoteClient.sld.sld1_0_0.SLDFill1_0_0;
import org.gvsig.remoteClient.sld.sld1_0_0.SLDStroke1_0_0;
import org.gvsig.remoteClient.sld.symbolizers.SLDPolygonSymbolizer;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;
import com.iver.cit.gvsig.fmap.rendering.XmlBuilder;

/**
 * Implements the PolygonSymbolizer element of an SLD implementation
 * specification (version 1.0.0).
 * <p>
 * 
 * PolygonSymbolizer is used draw a polygon (or other area-type geometries),
 * including filling its interior and stroking its border (outline).
 * <p>
 * 
 * @see SLDStroke1_0_0
 * @see SLDFill1_0_0
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class SLDPolygonSymbolizer1_0_0 extends SLDPolygonSymbolizer {

	/**
	 * Parses the xml data retrieved from the SLD, it will parse the
	 * PolygonSymbolizer element</p>
	 * 
	 * @throws LegendDriverException
	 */
	public void parse(XMLSchemaParser parser) throws IOException,
			XmlPullParserException, LegendDriverException {
		int currentTag;
		boolean end = false;

		parser.require(XMLSchemaParser.START_TAG, null,
				SLDTags.POLYGONSYMBOLIZER);
		currentTag = parser.next();

		while (!end) {
			switch (currentTag) {
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(SLDTags.GEOMETRY) == 0) {
					parseGeometry(parser);
				} else if (parser.getName().compareTo(SLDTags.FILL) == 0) {
					SLDFill1_0_0 fill = new SLDFill1_0_0();
					fill.parse(parser, currentTag, null);
					setFill(fill);
				} else if (parser.getName().compareTo(SLDTags.STROKE) == 0) {
					SLDStroke1_0_0 stroke = new SLDStroke1_0_0();
					stroke.parse(parser, currentTag, null);
					setStroke(stroke);
				}
				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(SLDTags.POLYGONSYMBOLIZER) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		parser.require(XMLSchemaParser.END_TAG, null, SLDTags.POLYGONSYMBOLIZER);

	}

	/**
	 * Parses the xml data retrieved from the SLD, it will parse the Geometry
	 * element</p> The Geometry element of a Symbolizer defines the geometry to
	 * be used for styling. The Geometry element is optional and if it is absent
	 * then the “default” geometry property of the feature type that is used in
	 * the containing FeatureStyleType is used. The precise meaning of “default”
	 * geometry property is system-dependent. Most frequently, feature types
	 * will have only a single geometry property.
	 * <p>
	 * The only method available for defining a geometry is to reference a
	 * geometry property using the ogc:PropertyName element (defined in the WFS
	 * Specification). The content of the element gives the property name in
	 * XPath syntax. In principle, a fixed geometry could be defined using GML
	 * or operators could be defined for computing the geometry from references
	 * or literals. However, using a feature property directly is by far the
	 * most commonly useful method.
	 * 
	 */
	private void parseGeometry(XMLSchemaParser parser) throws IOException,
			XmlPullParserException {
		int currentTag;
		boolean end = false;

		parser.require(XMLSchemaParser.START_TAG, null, SLDTags.GEOMETRY);
		currentTag = parser.next();

		while (!end) {
			switch (currentTag) {
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(SLDTags.PROPERTY_NAME) == 0) {
					setGeometry(parser.nextText());
				}

				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(SLDTags.GEOMETRY) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		parser.require(XMLSchemaParser.END_TAG, null, SLDTags.GEOMETRY);

	}

	public String toXML() {
		XmlBuilder xmlBuilder = new XmlBuilder();

		xmlBuilder.openTag(SLDTags.POLYGONSYMBOLIZER);
		if (fill != null) {
			xmlBuilder.writeRaw(getFill().toXML());
		}
		if (stroke != null) {
			xmlBuilder.writeRaw(getStroke().toXML());
		}
		xmlBuilder.closeTag();

		return xmlBuilder.getXML();
	}

}
