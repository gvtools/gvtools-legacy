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
import org.gvsig.remoteClient.sld.SLDRule;
import org.gvsig.remoteClient.sld.SLDTags;
import org.gvsig.remoteClient.sld.filterEncoding.Filter;
import org.gvsig.remoteClient.sld.filterEncoding.FilterTags;
import org.gvsig.remoteClient.sld.filterEncoding.FilterUtils;
import org.gvsig.remoteClient.sld.sld1_0_0.symbolizers.SLDLineSymbolizer1_0_0;
import org.gvsig.remoteClient.sld.sld1_0_0.symbolizers.SLDPointSymbolizer1_0_0;
import org.gvsig.remoteClient.sld.sld1_0_0.symbolizers.SLDPolygonSymbolizer1_0_0;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;

/**
 * Implements the Rule element of an SLD specification.<br>
 * 
 * Rules are used to group rendering instructions by feature-property
 * conditions and map scales.Rule definitions are placed inmediatelly inside
 * of feature-style definitions.
 * 
 * 
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class SLDRule1_0_0 extends SLDRule {


	public void parse(XMLSchemaParser parser,int cuTag, String expressionType)throws IOException, XmlPullParserException, LegendDriverException  {
		int currentTag;
		boolean end = false;

		parser.require(XMLSchemaParser.START_TAG, null, SLDTags.RULE);
		currentTag = parser.next();

		while (!end)
		{
			switch(currentTag)
			{
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(SLDTags.NAME)==0) {
					setName(parser.nextText());
				}
				else if(parser.getName().compareTo(SLDTags.TITLE)==0) {
					setTitle(parser.nextText());
				}
				else if(parser.getName().compareTo(SLDTags.ABSTRACT)==0) {
					setRuleAbstract(parser.nextText());
				}
				else if(parser.getName().compareTo(SLDTags.LEGENDGRAPHIC)==0) {
					SLDLegendGraphic1_0_0 legend = new SLDLegendGraphic1_0_0();
					legend.parse(parser,currentTag,null);
					setLegendGraphic(legend);
				}
				else if(parser.getName().compareTo(SLDTags.MINSCALEDENOMINATOR)==0) {
					setMinScaleDenominator(Double.valueOf(parser.nextText()));
				}
				else if(parser.getName().compareTo(SLDTags.MAXSCALEDENOMINATOR)==0) {
					setMaxScaleDenominator(Double.valueOf(parser.nextText()));
				}
				else if(parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.FILTER))==0) {
					Filter filter = new Filter();
					filter.parse(parser);
					setFilter(filter);
				}
//				else if(parser.getName().compareTo(SLDTags.ELSEFILTER)==0) {
//					this.elseFilter = new Filter();
//					elseFilter.parse(parser);
//				}
//				else if(parser.getName().compareTo(SLDTags.ELSEFILTER)==0) {
//					throw new Error ("ElseFilter not yet implemented->Rule\n");
//				}
				else if(parser.getName().compareTo(SLDTags.LINESYMBOLIZER)==0) {
					SLDLineSymbolizer1_0_0 sldLine = new SLDLineSymbolizer1_0_0();
					sldLine.parse(parser);
					addLineSymbolizer(sldLine);
				}
				else if(parser.getName().compareTo(SLDTags.POLYGONSYMBOLIZER)==0) {
					SLDPolygonSymbolizer1_0_0 sldPolygon = new SLDPolygonSymbolizer1_0_0();
					sldPolygon.parse(parser);
					addPolygonSymbolizer(sldPolygon);
				}
				else if(parser.getName().compareTo(SLDTags.POINTSYMBOLIZER)==0) {
					SLDPointSymbolizer1_0_0 sldPoint = new SLDPointSymbolizer1_0_0();
					sldPoint.parse(parser);
					addPointSymbolizer(sldPoint);
				}
//				else if(parser.getName().compareTo(SLDTags.TEXTSYMBOLIZER)==0) {
//					throw new Error ("TextSymbolizer not yet implemented->Rule\n");
//				}
//				else if(parser.getName().compareTo(SLDTags.RASTERSYMBOLIZER)==0) {
//					throw new Error ("RasterSymbolizer not yet implemented->Rule\n");
//				}

				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(SLDTags.RULE) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		parser.require(XMLSchemaParser.END_TAG, null, SLDTags.RULE);

	}


	


	public void applyScale2Symbolizers(double minScaleDenominator,double maxScaleDenominator) {

		for (int i = 0; i < getLineSymbolizers().size(); i++) {
			getLineSymbolizers().get(i).setMinScaleDenominator(minScaleDenominator);
			getLineSymbolizers().get(i).setMaxScaleDenominator(maxScaleDenominator);
		}
		for (int i = 0; i < getPolygonSymbolizers().size(); i++) {
			getPolygonSymbolizers().get(i).setMinScaleDenominator(minScaleDenominator);
			getPolygonSymbolizers().get(i).setMaxScaleDenominator(maxScaleDenominator);
		}
		for (int i = 0; i < getPointSymbolizers().size(); i++) {
			getPointSymbolizers().get(i).setMinScaleDenominator(minScaleDenominator);
			getPointSymbolizers().get(i).setMaxScaleDenominator(maxScaleDenominator);
		}
	}

	public boolean hasFilter() {
		if(this.filter != null) return true;
		else return false;
	}

	public void addSymbolizers2Filter() {
		if(getFilter() != null) {
			for (int i = 0; i < getLineSymbolizers().size(); i++) {
				getFilter().addSymbolizer2Filter(getLineSymbolizers().get(i));
			}
			for (int i = 0; i < getPolygonSymbolizers().size(); i++) {
				getFilter().addSymbolizer2Filter(getPolygonSymbolizers().get(i));
			}
			for (int i = 0; i < getPointSymbolizers().size(); i++) {
				getFilter().addSymbolizer2Filter(getPointSymbolizers().get(i));
			}
		}
	}

	public String toXML() {
		// TODO Auto-generated method stub
		throw new Error ("Not yet implemented");
	}
	
	

}
