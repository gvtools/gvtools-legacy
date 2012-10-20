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
import java.util.HashSet;
import java.util.Set;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;

/**
 * Implements the main functionalities to parse a logical negation operator of a
 * Filter Encoding expression<br>
 * 
 * The logical negation operator (NOT) reverses the logical value of an
 * expression
 * 
 * @see http://www.opengeospatial.org/standards/filter
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class FLogicOperatorNot {

	FComparisonOperator compOperator = null;
	FLogicOperator logicOperators = null;
	Set<String> fieldNames = new HashSet<String>();
	String propertyName;
	int numOperators = 0;
	String opExpressionStr = "";

	public void parse(XMLSchemaParser parser, String tag)
			throws XmlPullParserException, IOException, LegendDriverException {

		int currentTag;
		boolean end = false;
		currentTag = parser.next();

		while (!end) {
			switch (currentTag) {
			case XMLSchemaParser.START_TAG:
				if (parser.getName().compareTo(
						FilterUtils.remNameSpace(FilterTags.PROPERTYISEQUALTO)) == 0
						|| parser
								.getName()
								.compareTo(
										FilterUtils
												.remNameSpace(FilterTags.PROPERTYISNOTEQUALTO)) == 0
						|| parser
								.getName()
								.compareTo(
										FilterUtils
												.remNameSpace(FilterTags.PROPERTYISLESSTHAN)) == 0
						|| parser
								.getName()
								.compareTo(
										FilterUtils
												.remNameSpace(FilterTags.PROPERTYISGREATERTHAN)) == 0
						|| parser
								.getName()
								.compareTo(
										FilterUtils
												.remNameSpace(FilterTags.PROPERTYISLESSOREQUALTHAN)) == 0
						|| parser
								.getName()
								.compareTo(
										FilterUtils
												.remNameSpace(FilterTags.PROPERTYISGREATEROREQUALTHAN)) == 0) {

					if (parser
							.getName()
							.compareTo(
									FilterUtils
											.remNameSpace(FilterTags.PROPERTYISEQUALTO)) != 0
							&& parser
									.getName()
									.compareTo(
											FilterUtils
													.remNameSpace(FilterTags.PROPERTYISNOTEQUALTO)) != 0) {
					}

					compOperator = new FComparisonOperator();
					opExpressionStr += "( ";
					compOperator.parse(parser, parser.getName());
					opExpressionStr += compOperator.getOpExpressionStr();
					opExpressionStr += ") ";

					fieldNames.addAll(compOperator.getFieldNames());
					numOperators++;

				} else if (parser.getName().compareTo(
						FilterUtils.remNameSpace(FilterTags.AND)) == 0
						|| parser.getName().compareTo(
								FilterUtils.remNameSpace(FilterTags.OR)) == 0) {
					logicOperators = new FLogicOperator();
					opExpressionStr += "( ";
					logicOperators.parse(parser, parser.getName());
					opExpressionStr += logicOperators.getOpExpressionStr();
					opExpressionStr += ") ";

					fieldNames.addAll(logicOperators.getFieldNames());
					numOperators++;

					if (logicOperators.getNumOperators() < 2)
						throw new LegendDriverException(
								LegendDriverException.PARSE_LEGEND_FILE_ERROR);

				} else if (parser.getName().compareTo(
						FilterUtils.remNameSpace(FilterTags.NOT)) == 0) {
					FLogicOperatorNot notlogicOperators = new FLogicOperatorNot();
					opExpressionStr += "( " + "Not ";
					notlogicOperators.parse(parser, parser.getName());
					opExpressionStr += notlogicOperators.getOpExpressionStr();
					opExpressionStr += ") ";

					fieldNames.addAll(notlogicOperators.getFieldNames());
					numOperators++;
				}

				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(
						FilterUtils.remNameSpace(FilterTags.NOT)) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		parser.require(XMLSchemaParser.END_TAG, null, FilterTags.NOT);

	}

	public int getNumOperators() {
		return numOperators;
	}

	public Set<String> getFieldNames() {
		return fieldNames;
	}

	public String getOpExpressionStr() {
		return opExpressionStr;
	}

}
