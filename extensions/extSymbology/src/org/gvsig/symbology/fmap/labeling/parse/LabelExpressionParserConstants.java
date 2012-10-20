/* Generated By:JavaCC: Do not edit this line. LabelExpressionParserConstants.java */
/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 *   Av. Blasco Ibáñez, 50
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
package org.gvsig.symbology.fmap.labeling.parse;

public interface LabelExpressionParserConstants {

	int EOF = 0;
	int PLUS = 5;
	int MINUS = 6;
	int MULTIPLY = 7;
	int DIVIDE = 8;
	int GT = 9;
	int LT = 10;
	int NOT = 11;
	int EQ = 12;
	int LE = 13;
	int GE = 14;
	int NE = 15;
	int OR = 16;
	int AND = 17;
	int DOT = 18;
	int OPEN_SQ_BRACKETS = 19;
	int CLOSE_SQ_BRACKETS = 20;
	int OPEN_PARENTHESIS = 21;
	int CLOSE_PARENTHESIS = 22;
	int DOUBLE_QUOTE = 23;
	int COMMA = 24;
	int EOFIELD = 25;
	int EOEXPR = 26;
	int BOOLEAN = 27;
	int NULL = 28;
	int IDENTIFIER = 29;
	int LETTER = 30;
	int DIGIT = 31;
	int INTEGER = 32;
	int FLOATING_POINT = 33;
	int EXPONENT = 34;
	int STRING = 35;
	int CHAR_STRING = 36;

	int DEFAULT = 0;

	String[] tokenImage = { "<EOF>", "\" \"", "\"\\r\"", "\"\\t\"", "\"\\n\"",
			"\"+\"", "\"-\"", "\"*\"", "\"/\"", "\">\"", "\"<\"", "\"!\"",
			"\"==\"", "\"<=\"", "\">=\"", "\"!=\"", "\"||\"", "\"&&\"",
			"\".\"", "\"[\"", "\"]\"", "\"(\"", "\")\"", "\"\\\"\"", "\",\"",
			"\":\"", "\";\"", "<BOOLEAN>", "\"null\"", "<IDENTIFIER>",
			"<LETTER>", "<DIGIT>", "<INTEGER>", "<FLOATING_POINT>",
			"<EXPONENT>", "<STRING>", "<CHAR_STRING>", };

}
