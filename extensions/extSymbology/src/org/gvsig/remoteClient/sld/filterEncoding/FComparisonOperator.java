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
 * Implements the main functionalities to parse a comparison operator
 * of a Filter Encoding expression.<br>
 * 
 * A comparison operator is used to form expressions that evaluate the 
 * mathematical comparison between two arguments. If the arguments satisfy
 * the comparison then the expression evaluates to TRUE.Otherwise the 
 * expression evaluates to FALSE.
 *
 * @see http://www.opengeospatial.org/standards/filter
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class FComparisonOperator  {


	String comparisonType;
	FExpression insideExpression;
	FExpression insideExpression2;
	Set <String> fieldNames = new HashSet <String>();
	boolean value = true;
	int hasIntervals = 0;
	protected String opExpressionStr ="";


	public void parse(XMLSchemaParser parser, String tag) throws XmlPullParserException, IOException, LegendDriverException {

		int currentTag;
		boolean end = false;

		this.comparisonType = tag;

		parser.require(XMLSchemaParser.START_TAG, null, tag);
		currentTag = parser.next();

		while (!end)
		{
			switch(currentTag)
			{
			case XMLSchemaParser.START_TAG:
				insideExpression =new FExpression();
				insideExpression.parse(parser,currentTag,parser.getName());	
				opExpressionStr += insideExpression.getExpressionStr();

				opExpressionStr += FilterUtils.getSymbol4Expression("ogc:"+this.comparisonType) + " ";

				fieldNames.addAll(insideExpression.getFieldNames());
				currentTag = parser.nextTag();
				insideExpression2 =new FExpression();
				insideExpression2.parse(parser, currentTag,parser.getName());
				opExpressionStr += insideExpression2.getExpressionStr();

				fieldNames.addAll(insideExpression2.getFieldNames());
				parser.nextTag();
				if (insideExpression == null || insideExpression2 == null)
					throw new LegendDriverException(LegendDriverException.PARSE_LEGEND_FILE_ERROR);

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

	public String getComparisonType() { return comparisonType; }
	public void setComparisonType(String comparisonType) { this.comparisonType = comparisonType; }
	public boolean evaluate() { return value; }
	public Set<String> getFieldNames() { return fieldNames; }
	public int getHasIntervals() { return hasIntervals; }
	public FExpression getInsideExpression() { return insideExpression; }
	public FExpression getInsideExpression2() {return insideExpression2; }
	public String getOpExpressionStr() {return opExpressionStr;}

}
