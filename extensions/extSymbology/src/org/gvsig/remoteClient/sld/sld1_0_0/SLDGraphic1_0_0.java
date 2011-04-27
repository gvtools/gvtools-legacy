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
import org.gvsig.remoteClient.sld.SLDGraphic;
import org.gvsig.remoteClient.sld.SLDTags;
import org.gvsig.remoteClient.sld.SLDUtils;
import org.gvsig.remoteClient.sld.filterEncoding.FExpression;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;
import com.iver.cit.gvsig.fmap.rendering.XmlBuilder;

/**
 * Implements the Graphic element of an SLD implementation specification (version 
 * 1.0.0).<p>
 * A Graphic is a �graphic symbol� with an inherent shape, color(s), and possibly size. A
 * �graphic� can be very informally defined as �a little picture� and can be of either a raster
 * or vector-graphic source type. The term �graphic� is used since the term �symbol� is
 * similar to �symbolizer� which is used in a different context in SLD.<p>
 * If the Graphic element is omitted from the parent element, then nothing will be plotted.<p>
 * Graphics can either be referenced from an external URL in a common format (such as
 * GIF or SVG) or may be derived from a Mark. Multiple external URLs and marks may be
 * referenced with the semantic that they all provide the equivalent graphic in different
 * formats. The �hot spot� to use for positioning the rendering at a point must either be
 * inherent in the external format or is defined to be the �central point� of the graphic,
 * where the exact definition �central point� is system-dependent.<p>
 * The default if neither an ExternalGraphic nor a Mark is specified is to use the default
 * mark of a �square� with a 50%-gray fill and a black outline, with a size of 6 pixels,
 * 
 * @see SLDExternalGraphic1_0_0
 * @see SLDMark1_0_0
 * 
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class SLDGraphic1_0_0 extends SLDGraphic {


	public void parse(XMLSchemaParser parser,int cuTag,String tag)throws IOException, XmlPullParserException, LegendDriverException  {
		int currentTag;
		boolean end = false;

		parser.require(XMLSchemaParser.START_TAG, null, tag);
		currentTag = parser.next();

		while (!end)
		{
			switch(currentTag)
			{
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(SLDTags.GRAPHIC)==0) {
					this.parseGraphic(parser,SLDTags.GRAPHIC );
				}
				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(tag) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		parser.require(XMLSchemaParser.END_TAG, null, tag);
	}



	/**
	 * Parses the xml data retrieved from the SLD, it will parse the Graphic element</p>
	 * @throws LegendDriverException 
	 */
	public void parseGraphic(XMLSchemaParser parser,String tag)throws IOException, XmlPullParserException, LegendDriverException  {
		int currentTag;
		boolean end = false;

		parser.require(XMLSchemaParser.START_TAG, null, tag);
		currentTag = parser.next();

		while (!end)
		{
			switch(currentTag)
			{
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(SLDTags.EXTERNALGRAPHIC)==0) {
					SLDExternalGraphic1_0_0 externalGraphic = new SLDExternalGraphic1_0_0();
					externalGraphic.parse(parser,currentTag,null);
					addExternalGraphic(externalGraphic);

				}
				else if (parser.getName().compareTo(SLDTags.MARK)==0) {
					SLDMark1_0_0 mark = new SLDMark1_0_0();
					mark.parse(parser, currentTag, null);
					addMark(mark);
				}
				else if (parser.getName().compareTo(SLDTags.OPACITY)==0) {
					FExpression expressionOpacity = new FExpression();
					parser.next();
					String s = parser.getText().trim();

					if (s==null || "".equals(s)) {
						expressionOpacity.parse(parser, parser.nextTag(),parser.getName());
					} else {
						if(SLDUtils.isANumber(s))
							expressionOpacity.setLiteral(s);
						else throw new LegendDriverException(LegendDriverException.PARSE_LEGEND_FILE_ERROR);
					}
					setExpressionOpacity(expressionOpacity);
					
				}
				else if (parser.getName().compareTo(SLDTags.SIZE)==0) {
					FExpression expressionSize = new FExpression();
					parser.next();
					String s = parser.getText().trim();

					if (s==null || "".equals(s)) {
						expressionSize.parse(parser, parser.nextTag(),parser.getName());
					} else {
						if(SLDUtils.isANumber(s))
							expressionSize.setLiteral(s);
						else throw new LegendDriverException(LegendDriverException.PARSE_LEGEND_FILE_ERROR);
					}
					setExpressionSize(expressionSize);
				}
				else if (parser.getName().compareTo(SLDTags.ROTATION)==0) {
					FExpression expressionRotation = new FExpression();
					parser.next();
					String s = parser.getText().trim();

					if (s==null || "".equals(s)) {
						expressionRotation.parse(parser, parser.nextTag(),parser.getName());
					} else {
						if(SLDUtils.isANumber(s))
							expressionRotation.setLiteral(s);
						else throw new LegendDriverException(LegendDriverException.PARSE_LEGEND_FILE_ERROR);
				
					}
					setExpressionRotation(expressionRotation);
				}
				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(tag) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		parser.require(XMLSchemaParser.END_TAG, null, tag);

	}

	

	public String toXML() {
		XmlBuilder xmlBuilder = new XmlBuilder();


		xmlBuilder.openTag(SLDTags.GRAPHICFILL);
		xmlBuilder.openTag(SLDTags.GRAPHIC);
		//PictureFill
		for (int i = 0; i < getExternalGraphics().size(); i++)  {
			xmlBuilder.writeRaw(getExternalGraphics().get(i).toXML());
		}
		//MarkerFill
		for (int i = 0; i < getMarks().size(); i++)  {
			xmlBuilder.writeRaw(getMarks().get(i).toXML());
		}
		if (getExpressionOpacity().getLiteral() != null)
			xmlBuilder.writeTag(SLDTags.OPACITY
					,this.getExpressionOpacity().getLiteral());
		if (getExpressionSize().getLiteral()!= null)
			xmlBuilder.writeTag(SLDTags.SIZE
					,getExpressionSize().getLiteral());
		if (getExpressionRotation().getLiteral() != null)
			xmlBuilder.writeTag(SLDTags.ROTATION
					,getExpressionRotation().getLiteral());

		xmlBuilder.closeTag();
		xmlBuilder.closeTag();
		return xmlBuilder.getXML();
	}




}
