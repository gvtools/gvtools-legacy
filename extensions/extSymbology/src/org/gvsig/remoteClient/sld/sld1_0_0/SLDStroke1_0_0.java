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
import org.gvsig.remoteClient.sld.SLDStroke;
import org.gvsig.remoteClient.sld.SLDTags;
import org.gvsig.remoteClient.sld.SLDUtils;
import org.gvsig.remoteClient.sld.filterEncoding.FExpression;
import org.gvsig.remoteClient.sld.filterEncoding.FilterTags;
import org.gvsig.remoteClient.sld.filterEncoding.FilterUtils;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;
import com.iver.cit.gvsig.fmap.rendering.XmlBuilder;

/**
 * Implements the Stroke element of an SLD specification which
 * encapsulates the graphical-symbolization parameters for linear
 * geometries
 *
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 *
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class SLDStroke1_0_0 extends SLDStroke {

	String cad;

	public void parse(XMLSchemaParser parser, int cuTag, String expressionType) throws XmlPullParserException, IOException, LegendDriverException {

		int currentTag;
		boolean end = false;
		parser.require(XMLSchemaParser.START_TAG, null, SLDTags.STROKE);
		currentTag = parser.next();

		while (!end)
		{
			switch(currentTag)
			{
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(SLDTags.GRAPHICFILL)==0 ||
						parser.getName().compareTo(SLDTags.GRAPHICSTROKE)==0)  {
					String name = parser.getName();
					SLDGraphic1_0_0 graphic = new SLDGraphic1_0_0();
					graphic.parse(parser,currentTag,parser.getName());
					setGraphic(graphic);

					if (name.compareTo(SLDTags.GRAPHICFILL)== 0)
						setHasGraphicFill(true);
					if (name.compareTo(SLDTags.GRAPHICSTROKE)== 0)
						setHasGraphicStroke(true);
				}
				else if (parser.getName().compareTo(SLDTags.CSSPARAMETER)==0) {
					String attributeName = parser.getAttributeValue("", SLDTags.NAME_ATTR);
					/*
					 * Parche para compatibilizar leyendas generadas con la 1.1.2
					 * donde el atributo "name" se pone con la primera letra mayuscula "Name"
					 */
					if (attributeName == null){
						attributeName = parser.getAttributeValue("", SLDTags.NAME_ATTR_VERSAL);
					}
					/* Fin del parche */
					if(attributeName.compareTo(SLDTags.STROKE_WIDTH_ATTR)==0) {
						FExpression expressionWidth = new FExpression();

						try {
							expressionWidth.parse(parser,parser.nextTag(), parser.getName());
						}
						catch(XmlPullParserException e) {
							String s = parser.getText().trim();
							expressionWidth.setLiteral(s);
						}

						if (!SLDUtils.isANumber(expressionWidth.getLiteral()))
							throw new LegendDriverException(LegendDriverException.PARSE_LEGEND_FILE_ERROR);

						setExpressionWidth(expressionWidth);
					}
					else if(attributeName.compareTo(SLDTags.STROKE_ATTR)==0) {
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
					else if(attributeName.compareTo(SLDTags.STROKE_OPACITY_ATTR)==0) {
						FExpression expressionOpacity = new FExpression();



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
					else if(attributeName.compareTo(SLDTags.STROKE_LINEJOIN_ATTR)==0) {

						FExpression expressionLineJoin = new FExpression();

						try {
							expressionLineJoin.parse(parser,parser.nextTag(), parser.getName());
						}
						catch(XmlPullParserException e) {
							String s = parser.getText().trim();
							expressionLineJoin.setLiteral(s);
						}

						if (!SLDUtils.isLineJoin(expressionLineJoin.getLiteral()))
							throw new LegendDriverException(LegendDriverException.PARSE_LEGEND_FILE_ERROR);

						setExpressionLineJoin(expressionLineJoin);
					}
					else if(attributeName.compareTo(SLDTags.STROKE_LINECAP_ATTR)==0) {

						FExpression expressionLineCap = new FExpression();

						try {
							expressionLineCap.parse(parser,parser.nextTag(), parser.getName());
						}
						catch(XmlPullParserException e) {
							String s = parser.getText().trim();
							expressionLineCap.setLiteral(s);
						}

						if (!SLDUtils.isLineCap(expressionLineCap.getLiteral()))
							throw new LegendDriverException(LegendDriverException.PARSE_LEGEND_FILE_ERROR);

						setExpressionLineCap(expressionLineCap);
					}
					else if(attributeName.compareTo(SLDTags.STROKE_DASHARRAY_ATTR)==0) {


						FExpression expressionDashArray =  new FExpression();
						try {
							expressionDashArray.parse(parser,parser.nextTag(), parser.getName());
							cad = expressionDashArray.getLiteral();
							if (cad == null)
								cad = parser.getText().trim();
						}
						catch(XmlPullParserException e) {
							cad = parser.getText().trim();
						}

						if (cad==null /*|| "".equals(cad)*/) {
							throw new LegendDriverException (LegendDriverException.PARSE_LEGEND_FILE_ERROR);
						} else {
							String x="",y="";
							int cont = 0;

							for(int i=0;i < cad.length();i++) {
								x = String.valueOf(cad.charAt(i));
								if(x.compareTo(",") == 0) {
									if (SLDUtils.isANumber(y)) {
										dashArray.add(Float.valueOf(y));
										y = "";
										cont++;
									}
									else
										throw new LegendDriverException(LegendDriverException.PARSE_LEGEND_FILE_ERROR);
								}
								else {
									if(x.compareTo(" ") != 0)
										y += String.valueOf(cad.charAt(i));
									if ( i == cad.length()-1 ) {
										if (SLDUtils.isANumber(y)) {
											dashArray.add(Float.valueOf(y));
											cont++;
										}
										else
											throw new LegendDriverException(LegendDriverException.PARSE_LEGEND_FILE_ERROR);
									}

								}
							}
						}
					}

					else if(attributeName.compareTo(SLDTags.STROKE_DASHOFFSET_ATTR)==0) {
						FExpression expressionDashOffset = new FExpression();


						try {
							expressionDashOffset.parse(parser,parser.nextTag(), parser.getName());
						}
						catch(XmlPullParserException e) {
							String s = parser.getText().trim();
							expressionDashOffset.setLiteral(s);
						}

						if (!SLDUtils.isANumber(expressionDashOffset.getLiteral()))
							throw new LegendDriverException(LegendDriverException.PARSE_LEGEND_FILE_ERROR);

						setExpressionDashOffset(expressionDashOffset);
					}
				}

				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(SLDTags.STROKE) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		parser.require(XMLSchemaParser.END_TAG, null, SLDTags.STROKE);

	}


	private void parserDashArray(XMLSchemaParser parser, int i, String name) throws XmlPullParserException, IOException, LegendDriverException {

		int currentTag;
		boolean end = false;
		currentTag = i;

		while (!end)
		{
			switch(currentTag)
			{
			case XMLSchemaParser.START_TAG:

				if (parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.LITERAL))==0) {
					this.cad = parser.nextText();
					end = true;

				}
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(FilterUtils.remNameSpace(SLDTags.CSSPARAMETER)) == 0) {
					parser.next();
				}
			}
			break;
		}
		if (!end)
			currentTag = parser.next();

	}


	public String toXML() {
		XmlBuilder xmlBuilder = new XmlBuilder();

		xmlBuilder.openTag(SLDTags.STROKE);
		if (getGraphic() != null) {
			xmlBuilder.writeRaw(getGraphic().toXML());
		}
		if(getExpressionColor().getLiteral() != null){
			xmlBuilder.openTag(SLDTags.CSSPARAMETER,SLDTags.NAME_ATTR,SLDTags.STROKE_ATTR);
			xmlBuilder.writeTag(FilterTags.LITERAL, getExpressionColor().getLiteral());
			xmlBuilder.closeTag();
		}
		if(getExpressionOpacity().getLiteral() != null){
			xmlBuilder.openTag(SLDTags.CSSPARAMETER,SLDTags.NAME_ATTR,SLDTags.STROKE_OPACITY_ATTR);
			xmlBuilder.writeTag(FilterTags.LITERAL, getExpressionOpacity().getLiteral());
			xmlBuilder.closeTag();
		}
		if(getExpressionWidth().getLiteral() != null){
			xmlBuilder.openTag(SLDTags.CSSPARAMETER,SLDTags.NAME_ATTR,SLDTags.STROKE_WIDTH_ATTR);
			xmlBuilder.writeTag(FilterTags.LITERAL, getExpressionWidth().getLiteral());
			xmlBuilder.closeTag();
		}
		if(getExpressionLineCap().getLiteral() != null){
			xmlBuilder.openTag(SLDTags.CSSPARAMETER,SLDTags.NAME_ATTR,SLDTags.STROKE_LINECAP_ATTR);
			xmlBuilder.writeTag(FilterTags.LITERAL, getExpressionLineCap().getLiteral());
			xmlBuilder.closeTag();
		}
		if(getExpressionLineJoin().getLiteral() != null){
			xmlBuilder.openTag(SLDTags.CSSPARAMETER,SLDTags.NAME_ATTR,SLDTags.STROKE_LINEJOIN_ATTR);
			xmlBuilder.writeTag(FilterTags.LITERAL, getExpressionLineJoin().getLiteral());
			xmlBuilder.closeTag();
		}
		if(getDashArray() != null && getDashArray().size() > 0){
			String myDash = "";
			for (int i = 0; i <getDashArray().size(); i++) {
				myDash += getDashArray().get(i);
				if ((i+1) < getDashArray().size())
					myDash += ", ";
			}

			xmlBuilder.openTag(SLDTags.CSSPARAMETER,SLDTags.NAME_ATTR,SLDTags.STROKE_DASHARRAY_ATTR);
			xmlBuilder.writeTag(FilterTags.LITERAL, myDash);
			xmlBuilder.closeTag();
		}
		if(getExpressionDashOffset().getLiteral() != null){
			xmlBuilder.openTag(SLDTags.CSSPARAMETER,SLDTags.NAME_ATTR,SLDTags.STROKE_DASHOFFSET_ATTR);
			xmlBuilder.writeTag(FilterTags.LITERAL, getExpressionDashOffset().getLiteral());
			xmlBuilder.closeTag();
		}
		xmlBuilder.closeTag();

		return xmlBuilder.getXML();
	}



}
