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
package org.gvsig.remoteClient.sld.filterEncoding;

import java.io.IOException;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Implements the main functionalities to parse a PropertyIsNull element of a
 * Filter Encoding expression<br>
 * 
 * The PropertyIsNull element encodes an operator that checks to see if the
 * value of its content is NULL. A NULL is equivalent to no value present. The
 * value 0 is valid value and is not considered NULL.
 * 
 * @see http://www.opengeospatial.org/standards/filter
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class FIsNullOperator {

	protected String propName;
	protected String opExpressionStr = "";

	public void parse(XMLSchemaParser parser, int Tag2, String expressionType)
			throws XmlPullParserException, IOException {
		int currentTag;
		boolean end = false;
		currentTag = Tag2;

		parser.require(XMLSchemaParser.START_TAG, null,
				FilterTags.PROPERTYISNULL);

		while (!end) {
			switch (currentTag) {
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(
						FilterUtils.remNameSpace(FilterTags.PROPERTYNAME)) == 0) {
					this.propName = parser.nextText();
					opExpressionStr += "( [" + this.propName + "] ) ";
				}
				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(
						FilterUtils.remNameSpace(FilterTags.PROPERTYISNULL)) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}
	}

	public String getPropName() {
		return propName;
	}

	public String getOpExpressionStr() {
		return opExpressionStr;
	}
}
