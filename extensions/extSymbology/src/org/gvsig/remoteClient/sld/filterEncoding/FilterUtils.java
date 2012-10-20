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

/**
 * 
 * Useful methods which are used during the parse or the storage of a Filter
 * Encoding expression
 * 
 * @see http://www.opengeospatial.org/standards/filter
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 * 
 */
public class FilterUtils {

	public static String getSymbol4Expression(String type) {

		if (type.compareTo(FilterTags.PROPERTYISEQUALTO) == 0)
			return "==";
		else if (type.compareTo(FilterTags.PROPERTYISNOTEQUALTO) == 0)
			return "!=";
		else if (type.compareTo(FilterTags.PROPERTYISLESSTHAN) == 0)
			return "<";
		else if (type.compareTo(FilterTags.PROPERTYISGREATERTHAN) == 0)
			return ">";
		else if (type.compareTo(FilterTags.PROPERTYISLESSOREQUALTHAN) == 0)
			return "<=";
		else if (type.compareTo(FilterTags.PROPERTYISGREATEROREQUALTHAN) == 0)
			return ">=";
		else if (type.compareTo(FilterTags.ADD) == 0)
			return "+";
		else if (type.compareTo(FilterTags.DIV) == 0)
			return "/";
		else if (type.compareTo(FilterTags.MULT) == 0)
			return "*";
		else if (type.compareTo(FilterTags.SUB) == 0)
			return "-";
		else if (type.compareTo(FilterTags.AND) == 0)
			return "&&";
		else if (type.compareTo(FilterTags.OR) == 0)
			return "||";
		else if (type.compareTo(FilterTags.NOT) == 0)
			return "!";

		return null;
	}

	public static String remNameSpace(String literal) {

		return literal.substring(literal.indexOf(":") + 1, literal.length());

	}

}
