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
import org.gvsig.remoteClient.sld.SLDFeatureTypeStyle;
import org.gvsig.remoteClient.sld.SLDTags;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;

/**
 * Implements the FeatureTypeStyle element of an SLD implementation specification 
 * (version 1.0.0).<p>
 * The FeatureTypeStyle defines the styling that is to be applied to a single feature
 * type of a layer. This element may also be externally re-used outside of the WMSes
 * and layers.<p>
 * 
 * The FeatureTypeStyle element identifies that explicit separation between the
 * handling of features of specific feature types. The 'layer' concept is unique 
 * to WMS and SLD, but features are used more generally, such as in WFS and GML,
 * so this explicit separation is important.<p>
 * 
 * Like a UserStyle, a FeatureTypeStyle can have a Name, Title, and Abstract.
 * The Name element does not have an explicit use at present, though it conceivably
 * might be used to reference a feature style in some feature-style library. The Title
 * and Abstract are for human-readable information.<p>
 * 
 * The FeatureTypeName identifies the specific feature type that the feature-type 
 * style is for. It is allowed to be optional, but only if one feature type is 
 * in-context (in-layer) and that feature type must match the syntax and semantics 
 * of all feature-property references inside of the FeatureTypeStyle. Note that 
 * there is no restriction against a single UserStyle from including multiple 
 * FeatureTypeStyles that reference the same FeatureTypeName. This case does not 
 * create an exception in the rendering semantics,however, since a map styler is 
 * expected to process all FeatureTypeStyles in the order that they appear, 
 * regardless, plotting one instance over top of another.<p>
 * 
 * The SemanticTypeIdentifier is experimental and is intended to be used to identify
 * what the feature style is suitable to be used for using community-controlled 
 * name(s). For example, a single style may be suitable to use with many different
 * feature types. The syntax of the SemanticTypeIdentifier string is undefined, but
 * the strings �generic:line�, �generic:polygon�, �generic:point�, �generic:text�,
 * �generic:raster�, and �generic:any� are reserved to indicate that a FeatureTypeStyle
 * may be used with any feature type with the corresponding default geometry type
 * (i.e., no feature properties are referenced in the feature-type style).<p>
 * 
 * The FeatureTypeStyle contains one or more Rule elements that allow conditional
 * rendering. Rules are discussed in Section 10.
 * 
 * @see SLDRule1_0_0 
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
*/
public class SLDFeatureTypeStyle1_0_0 extends SLDFeatureTypeStyle {

	
	public void parse(XMLSchemaParser parser,int cuTag, String expressionType)throws IOException, XmlPullParserException, LegendDriverException  {
		int currentTag;
		boolean end = false;

		parser.require(XMLSchemaParser.START_TAG, null, SLDTags.FEATURETYPESTYLE);
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
					setFtsAbstract(parser.nextText());
				}
				else if(parser.getName().compareTo(SLDTags.FEATURETYPENAME)==0) {
					setFeatureTypeName(parser.nextText());
				}
				else if (parser.getName().compareTo(SLDTags.SEMANTICTYPEIDENTIFIER)==0){
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						addSemanticTypeIdentifier((parser.getAttributeValue(i)));
					}
				}
				else if(parser.getName().compareTo(SLDTags.RULE)==0){
					SLDRule1_0_0 rule = new SLDRule1_0_0();
					rule.parse(parser,currentTag,null);
					rule.applyScale2Symbolizers(rule.getMinScaleDenominator(),rule.getMaxScaleDenominator());
					rule.addSymbolizers2Filter();
					addRule(rule);
				}
				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(SLDTags.FEATURETYPESTYLE) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		parser.require(XMLSchemaParser.END_TAG, null, SLDTags.FEATURETYPESTYLE);

	}

	
	public String toXML() {
		// TODO Auto-generated method stub
		throw new Error ("Not yet implemented");
	}
	
	
}
