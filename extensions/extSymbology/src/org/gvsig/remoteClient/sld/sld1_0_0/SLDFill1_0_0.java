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
import org.gvsig.remoteClient.sld.SLDFill;
import org.gvsig.remoteClient.sld.SLDTags;
import org.gvsig.remoteClient.sld.SLDUtils;
import org.gvsig.remoteClient.sld.filterEncoding.FExpression;
import org.gvsig.remoteClient.sld.filterEncoding.FilterTags;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;
import com.iver.cit.gvsig.fmap.rendering.XmlBuilder;
/**
 * Implements the Fill element of an SLD implementation specification (version 
 * 1.0.0).<p>
 * The Fill element specifies how the area of the geometry will be filled.
 * There are two types of fills, solid-color and repeated GraphicFill. The repeated 
 * graphic fill is selected only if the GraphicFill element is present. If the Fill
 * element is omitted from its parent element, then no fill will be rendered. The 
 * GraphicFill and CssParameter elements are discussed in conjunction with the 
 * Stroke element in Section 11.1.3. Here, the CssParameter names are �fill� 
 * instead of �stroke� and �fill-opacity� instead of �stroke-opacity�. None of the
 * other CssParameters in Stroke are available for filling and the default value
 * for the fill color in this context is 50% gray (value �#808080�).
 * 
 * @see SLDFill1_0_0
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class SLDFill1_0_0 extends SLDFill{



	public void parse(XMLSchemaParser parser,int cuTag, String expressionType)throws IOException, XmlPullParserException, LegendDriverException  {
		int currentTag;		
		boolean end = false;
		currentTag = parser.next();

		expressionColor.setLiteral("#808080");


		while (!end)
		{
			switch(currentTag)
			{
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(SLDTags.GRAPHICFILL)==0 ||
						parser.getName().compareTo(SLDTags.GRAPHICSTROKE)==0	) {
					SLDGraphic1_0_0 fillGraphic = new SLDGraphic1_0_0();
					fillGraphic.parse(parser,currentTag,parser.getName());
					setFillGraphic(fillGraphic);

				}
				else if (parser.getName().compareTo(SLDTags.CSSPARAMETER)==0) {
					if(parser.getAttributeValue("", SLDTags.NAME_ATTR).compareTo(SLDTags.FILL_ATTR)==0) {
						FExpression expressionColor = new FExpression();

						try {
							expressionColor.parse(parser, parser.nextTag(),parser.getName());
						}
						catch(XmlPullParserException e) {
							String s = parser.getText().trim();
							expressionColor.setLiteral(s);
						}

						if (!SLDUtils.isColor(expressionColor.getLiteral()))
							throw new LegendDriverException(LegendDriverException.PARSE_LEGEND_FILE_ERROR);
						setExpressionColor(expressionColor);

					}
					else if(parser.getAttributeValue("", SLDTags.NAME_ATTR).compareTo(SLDTags.FILLOPACITY_ATTR)==0) {
						
						FExpression expressionOpacity =  new FExpression();
						
						try {
							expressionOpacity.parse(parser,parser.nextTag(), parser.getName());
						}
						catch(XmlPullParserException e) {
							String s = parser.getText().trim();
							expressionOpacity.setLiteral(s);
						}

						if (!SLDUtils.isANumber(expressionOpacity.getLiteral()))
							throw new LegendDriverException(LegendDriverException.PARSE_LEGEND_FILE_ERROR);
						
						setExpressionOpacity(expressionOpacity);
					}
				}
				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(SLDTags.FILL) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		parser.require(XMLSchemaParser.END_TAG, null, SLDTags.FILL);

	}

	

	public String toXML() {

		XmlBuilder xmlBuilder = new XmlBuilder();

		xmlBuilder.openTag(SLDTags.FILL);
		if (getFillGraphic() != null)
			xmlBuilder.writeRaw(getFillGraphic().toXML());

		if(getExpressionColor().getLiteral() != null){
			xmlBuilder.openTag(SLDTags.CSSPARAMETER,SLDTags.NAME_ATTR,SLDTags.FILL_ATTR);
			xmlBuilder.writeTag(FilterTags.LITERAL, getExpressionColor().getLiteral());
			xmlBuilder.closeTag();

		}
		if(getExpressionOpacity().getLiteral() != null){
			xmlBuilder.openTag(SLDTags.CSSPARAMETER,SLDTags.NAME_ATTR,SLDTags.FILLOPACITY_ATTR);
			xmlBuilder.writeTag(FilterTags.LITERAL,getExpressionOpacity().getLiteral());
			xmlBuilder.closeTag();

		}

		xmlBuilder.closeTag();
		return xmlBuilder.getXML();
	}

}
