/* gvSIG. Sistema de Informaciï¿½n Geogrï¿½fica de la Generalitat Valenciana
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
 *   Av. Blasco Ibï¿½ï¿½ez, 50
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
import org.gvsig.remoteClient.sld.SLDUtils;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;


/**
 * Implements the main functionalities to parse an Filter Encoding
 * expression<br>
 * The expression element is an abstrac element which means that it
 * does not really exist and its only purpose is to act as a placeholder
 * for the elements and combinations of elements that can be used o form
 * expressions.
 *
 * @see http://www.opengeospatial.org/standards/filter
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class FExpression  {

	protected String literal;
	protected String propertyName;
	protected String function;
	protected String expressionType;
	protected FExpression insideExpression;
	protected FExpression insideExpression2;
	boolean isPropertyName = false;

	String expressionStr = "";

	Set <String> fieldNames = new HashSet <String>();

	double value;


	public void parse(XMLSchemaParser parser, int Tag2, String expressionType) throws XmlPullParserException, IOException, LegendDriverException {

		int currentTag;
		boolean end = false;
		currentTag = Tag2;
		this.expressionType = expressionType;

		while (!end)
		{
			switch(currentTag)
			{
			case XMLSchemaParser.START_TAG:

				if (parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.PROPERTYNAME))==0) {
					this.propertyName = parser.nextText();
					fieldNames.add(this.propertyName);
					isPropertyName = true;
					end = true;
					expressionStr +="["+this.propertyName +"] ";
				}
				else if (parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.LITERAL))==0) {
					this.literal = parser.nextText();
					String myLiteral = this.literal;
					if (!this.literal.startsWith("#"))
						if (SLDUtils.isANumber(this.literal)) {
							setValue(Double.valueOf(this.literal));
						} else {
							/*
							 * Parche para leer las leyendas generadas con versiones anteriores que
							 * no ponían comillas para identificar los literales
							 */
							if (!(this.literal.startsWith("\"") && this.literal.endsWith("\""))){
								myLiteral = "\""+this.literal +"\"";
							}
							/* Fin del parche */
						}
					end = true;
					expressionStr += myLiteral +" ";
				}
				else if (parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.FUNCTION))==0) {
					this.function = parser.nextText();
					end = true;
				}
				else if ((parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.ADD))==0) ||
				(parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.DIV))==0) ||
				(parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.MULT))==0) ||
				(parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.SUB))==0) ){

					String operation = parser.getName();
					currentTag = parser.nextTag();
					this.insideExpression = new FExpression();
					expressionStr += "( ";

					this.insideExpression.parse(parser,currentTag,parser.getName());
					expressionStr += insideExpression.getExpressionStr();

					expressionStr += FilterUtils.getSymbol4Expression("ogc:"+operation)+" ";

					fieldNames.addAll(insideExpression.getFieldNames());
					currentTag = parser.nextTag();
					this.insideExpression2 = new FExpression();

					this.insideExpression2.parse(parser,currentTag,parser.getName());
					expressionStr += insideExpression2.getExpressionStr();

					expressionStr += ") ";

					fieldNames.addAll(insideExpression2.getFieldNames());
					parser.nextTag();
					if (insideExpression == null && insideExpression2 == null)
						throw new LegendDriverException (LegendDriverException.PARSE_LEGEND_FILE_ERROR);
					end = true;


				}

				break;
			case XMLSchemaParser.END_TAG:
				if (parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.PROPERTYNAME))!=0 &&
						parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.LITERAL))!=0 &&
						parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.FUNCTION))!=0 &&
						parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.ADD))!=0 &&
						parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.DIV))!=0 &&
						parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.MULT))!=0 &&
						parser.getName().compareTo(FilterUtils.remNameSpace(FilterTags.SUB))!=0) {
					end = true;
				}
				break;
			case XMLSchemaParser.TEXT:
				break;
			}
			if (!end)
				currentTag = parser.next();
		}

	}


	public String getLiteral() { return literal; }
	public void setLiteral(String literal) {
		this.literal = literal; }
	public String getPropertyName() { return propertyName; }
	public void setPropertyName(String propertyName) { this.propertyName = propertyName; }
	public double evaluate() { return this.value; }
	public void setValue(double value) { this.value = value; }
	public Set<String> getFieldNames() { return fieldNames; }
	public boolean isPropertyName() {return isPropertyName;}
	public String getExpressionType() {return expressionType;}

	public FExpression getInsideExpression() {return insideExpression;}
	public FExpression getInsideExpression2() {return insideExpression2;}
	public String getExpressionStr() {return expressionStr;}

}
