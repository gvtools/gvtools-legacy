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
package org.gvsig.symbology.fmap.rendering.filter.operations;

public class OperationTags {

	// Useful to create the pattern
	public final static String OPERAND = "operand";
	public final static String NUMERIC_VALUE = "numeric_value";
	public final static String BOOLEAN_VALUE = "boolean_value";
	public final static String CONSTANT = "constant";
	public final static String NUMERIC_OR_BOOLEAN_CONSTANT = "numeric_or_boolean_value";
	public static final String NULL_CONSTANT = "null";
	public static final String STRING_CONSTANT = "String";

	// Operators
	public final static String ADD_OP = "+";
	public final static String DIV_OP = "/";
	public final static String MINUS_OP = "-";
	public final static String MULT_OP = "*";
	public final static String AND_OP = "&&";
	public final static String OR_OP = "||";
	public final static String NOT_OP = "Not";

	public final static String EQ_OP = "==";
	public final static String NEQ_OP = "!=";
	public final static String GREATER_THAN_OP = ">";
	public final static String GREATER_THAN_OR_EQ_OP = ">=";
	public final static String LESS_THAN_OP = "<";
	public final static String LESS_THAN_OR_EQ_OP = "<=";

	public final static String ISBETWEEN_OP = "IsBetween";
	public final static String ISNULL_OP = "IsNull";
	public final static String REPLACE_OP = "Replace";

}
