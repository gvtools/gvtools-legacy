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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.gvsig.remoteClient.sld.symbolizers.ISLDSymbolizer;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;

/**
 * Implements the main class expresion where the parsing of a Filter Encoding
 * expression starts<br>
 * 
 * A filter is any valid predicate expression that can be formed using the
 * elements defined in the Filter Encoding specification. The root element
 * <Filter> contains the expression whichis created by combining the elements
 * defined in this specification
 * 
 * @see http://www.opengeospatial.org/standards/filter
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class Filter {

	FComparisonOperator compOperator = null;
	FLogicOperator logicOperators = null;
	FLogicOperatorNot notLogicOperator = null;
	FIsBetweenOperator isBetweenOperator = null;
	FIsNullOperator isNullOperator = null;
	FIsLikeOperator isLikeOperator = null;
	// FBinSpatialOperator binSpatialOperator = null;
	String expression = "";

	Set<String> fieldNames = new HashSet<String>();
	ArrayList<ISLDSymbolizer> symbolizers = new ArrayList<ISLDSymbolizer>();

	public void parse(XMLSchemaParser parser) throws XmlPullParserException,
			IOException, LegendDriverException {

		int currentTag;
		boolean end = false;
		currentTag = parser.next();

		expression = "( ";

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
					expression += "( ";
					compOperator.parse(parser, parser.getName());
					expression += compOperator.getOpExpressionStr();

					expression += ") ";

					fieldNames.addAll(compOperator.getFieldNames());

				} else if (parser.getName().compareTo(
						FilterUtils.remNameSpace(FilterTags.AND)) == 0
						|| parser.getName().compareTo(
								FilterUtils.remNameSpace(FilterTags.OR)) == 0) {
					logicOperators = new FLogicOperator();

					expression += "( ";
					logicOperators.parse(parser, parser.getName());

					if (logicOperators.getNumOperators() < 2)
						throw new LegendDriverException(
								LegendDriverException.PARSE_LEGEND_FILE_ERROR);

					expression += logicOperators.getOpExpressionStr();
					expression += ") ";
					fieldNames.addAll(logicOperators.getFieldNames());

				} else if (parser.getName().compareTo(
						FilterUtils.remNameSpace(FilterTags.NOT)) == 0) {
					notLogicOperator = new FLogicOperatorNot();

					expression += "( " + "! ";
					notLogicOperator.parse(parser, parser.getName());

					if (notLogicOperator.getNumOperators() > 1)
						throw new LegendDriverException(
								LegendDriverException.PARSE_LEGEND_FILE_ERROR);

					expression += notLogicOperator.getOpExpressionStr();
					expression += ") ";
					fieldNames.addAll(notLogicOperator.getFieldNames());

				} else if (parser.getName().compareTo(
						FilterUtils.remNameSpace(FilterTags.PROPERTYISBETWEEN)) == 0) {
					isBetweenOperator = new FIsBetweenOperator();
					expression += "( Ib ( ";
					isBetweenOperator.parse(parser, currentTag,
							parser.getName());

					if (isBetweenOperator.getInsideExpression() == null
							|| isBetweenOperator.getLowerBoundary() == null
							|| isBetweenOperator.getUpperBoundary() == null)
						throw new LegendDriverException(
								LegendDriverException.PARSE_LEGEND_FILE_ERROR);

					expression += isBetweenOperator.getInExpressionStr() + ","
							+ isBetweenOperator.getLoExpressionStr() + ","
							+ isBetweenOperator.getUpExpressionStr();
					expression += ") ) ";
				} else if (parser.getName().compareTo(
						FilterUtils.remNameSpace(FilterTags.PROPERTYISNULL)) == 0) {
					isNullOperator = new FIsNullOperator();
					expression += "( INull ";
					isNullOperator.parse(parser, currentTag, parser.getName());

					if (isNullOperator.getPropName() == null)
						throw new LegendDriverException(
								LegendDriverException.PARSE_LEGEND_FILE_ERROR);

					expression += isNullOperator.getOpExpressionStr();
					expression += ") ";
				} else if (parser.getName().compareTo(
						FilterUtils.remNameSpace(FilterTags.PROPERTYISLIKE)) == 0) {
					isLikeOperator = new FIsLikeOperator();
					expression += "( IL ( ";
					String wild = parser.getAttributeValue("",
							FilterTags.WILDCHAR);
					String single = parser.getAttributeValue("",
							FilterTags.SINGLECHAR);
					String scape = parser.getAttributeValue("",
							FilterTags.ESCAPECHAR);
					expression += "( " + wild + " , " + single + " , " + scape
							+ " ) , ";
					isLikeOperator.parse(parser, currentTag, parser.getName());

					if (isLikeOperator.getLiteral() == null
							|| isLikeOperator.getPropName() == null)
						throw new LegendDriverException(
								LegendDriverException.PARSE_LEGEND_FILE_ERROR);

					expression += "( " + isLikeOperator.getPropName() + " , "
							+ isLikeOperator.getLiteral() + " ) ";
					expression += ") ) ";
				}
				// else if
				// (parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.EQUALS))
				// ==0 ||
				// parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.DISJOINT))
				// == 0 ||
				// parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.TOUCHES))
				// == 0 ||
				// parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.WITHIN))
				// == 0 ||
				// parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.OVERLAPS))
				// == 0 ||
				// parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.CROSSES))
				// == 0 ||
				// parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.INTERSECTS))
				// == 0 ||
				// parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.CONTAINS))
				// == 0) {

				// String func = parser.getName();

				// binSpatialOperator = new FBinSpatialOperator();
				// expression += "( "+func+" ";
				// binSpatialOperator.parse(parser, currentTag,
				// parser.getName());

				// if(binSpatialOperator.getPropertyName()==null)
				// throw new Error ("binSpatialOperator mal!");

				// expression += binSpatialOperator.getOpExpressionStr();
				// expression += ") ";

				// }
				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(
						FilterUtils.remNameSpace(FilterTags.FILTER)) == 0)
					end = true;
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

		expression += ") ";
	}

	public Set<String> getFieldNames() {
		return fieldNames;
	}

	public void addSymbolizer2Filter(ISLDSymbolizer symbol) {
		symbolizers.add(symbol);
	}

	public ArrayList<ISLDSymbolizer> getSymbolizers() {
		return symbolizers;
	}

	public FComparisonOperator getCompOperator() {
		return compOperator;
	}

	public FLogicOperator getLogicOperator() {
		return logicOperators;
	}

	public FLogicOperatorNot getNotLogicOperator() {
		return notLogicOperator;
	}

	public String getExpression() {
		return this.expression;
	}

}
