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
import org.gvsig.remoteClient.sld.SLDMark;
import org.gvsig.remoteClient.sld.SLDTags;
import org.gvsig.remoteClient.sld.filterEncoding.FExpression;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;
import com.iver.cit.gvsig.fmap.rendering.XmlBuilder;

/**
 * Implements the Mark element of an SLD implementation specification (version 
 * 1.0.0).<p>
 * The Mark element of a Graphic defines a �shape� which has coloring applied to it.<p>
 * The WellKnownName element gives the well-known name of the shape of the mark.
 * Allowed values include at least �square�, �circle�, �triangle�, �star�, �cross�,
 * and �x�, though map servers may draw a different symbol instead if they don't
 * have a shape for all of these. The default WellKnownName is �square�. Renderings 
 * of these marks may be made solid or hollow depending on Fill and Stroke elements.
 * <p> 
 * The Mark element serves two purposes. It allows the selection of simple shapes,
 * and, in combination with the capability to select and mix multiple external-URL 
 * graphics and marks, it allows a style to be specified that can produce a usable 
 * result in a best-effort rendering environment, provided that a simple Mark is 
 * included at the bottom of the list of sources for every Graphic.<p>
 * 
 * @see SLDFill1_0_0
 * @see SLDStroke1_0_0
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class SLDMark1_0_0 extends SLDMark {


	
	public void parse(XMLSchemaParser parser, int cuTag, String expressionType)throws IOException, XmlPullParserException, LegendDriverException  {

		int currentTag;
		boolean end = false;

		parser.require(XMLSchemaParser.START_TAG, null, SLDTags.MARK);
		currentTag = parser.next();

		while (!end)
		{
			switch(currentTag)
			{
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(SLDTags.WELLKNOWNNAME)==0) {
					parser.next();
					String s = parser.getText().trim();
					FExpression wellKnownName = new FExpression();
					
					if (s==null || "".equals(s)) {
						wellKnownName.parse(parser, parser.nextTag(),parser.getName());
					} else {
						wellKnownName.setLiteral(s);
					}				
					setWellKnownName(wellKnownName);
				}
				
				else if (parser.getName().compareTo(SLDTags.FILL)==0) {
					SLDFill1_0_0 fill = new SLDFill1_0_0();
					fill.parse(parser,currentTag,null);
					setFill(fill);
				}
				else if (parser.getName().compareTo(SLDTags.STROKE)==0) {
					SLDStroke1_0_0 stroke = new SLDStroke1_0_0();
					stroke.parse(parser,currentTag,null);
					setStroke(stroke);
				}
				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(SLDTags.MARK) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		parser.require(XMLSchemaParser.END_TAG, null, SLDTags.MARK);

	}

	
	public String toXML() {
		XmlBuilder xmlBuilder = new XmlBuilder();
		xmlBuilder.openTag(SLDTags.MARK);

		if (getWellKnownName().getLiteral() != null)
			xmlBuilder.writeTag(SLDTags.WELLKNOWNNAME, getWellKnownName().getLiteral().toString());
		if (getFill() != null)
			xmlBuilder.writeRaw(getFill().toXML());
		if (getStroke() != null)
			xmlBuilder.writeRaw(getStroke().toXML());

		xmlBuilder.closeTag();
		return xmlBuilder.getXML();
	}
}
