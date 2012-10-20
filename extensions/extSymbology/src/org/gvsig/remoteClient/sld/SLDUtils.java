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
package org.gvsig.remoteClient.sld;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.StringTokenizer;

import com.iver.cit.gvsig.fmap.core.symbols.SimpleMarkerSymbol;
import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;

/**
 * Implements an utility class for SLD functionality
 * 
 * @author Pepe Vidal Salvador jose.vidal.salvador@iver.es
 * 
 */
public class SLDUtils {

	public static Color convertHexStringToColor(String str)
			throws NumberFormatException, LegendDriverException {
		int multiplier = 1;
		StringTokenizer tokenizer = new StringTokenizer(str, " \t\r\n\b:;[]()+");
		while (tokenizer.hasMoreTokens()) {
			multiplier = 1;
			String token = tokenizer.nextToken();
			if (null == token) {
				throw new NumberFormatException(str);
			}
			if (token.startsWith("-")) {
				multiplier = -1;
				token = token.substring(1);
			}
			int point_index = token.indexOf(".");
			if (point_index > 0) {
				token = token.substring(0, point_index);
			} else if (point_index == 0) {
				return new Color(0);
			}
			try {
				if (token.startsWith("0x")) {
					return new Color(multiplier
							* Integer.parseInt(token.substring(2), 16));
				} else if (token.startsWith("#")) {
					return new Color(multiplier
							* Integer.parseInt(token.substring(1), 16));
				} else if (token.startsWith("0") && !token.equals("0")) {
					return new Color(multiplier
							* Integer.parseInt(token.substring(1), 8));
				} else {
					return new Color(multiplier * Integer.parseInt(token));
				}
			} catch (NumberFormatException e) {
				continue;
			}
		}
		throw new LegendDriverException(
				LegendDriverException.PARSE_LEGEND_FILE_ERROR);

	}

	public static String convertOpacityToString(float alpha) {
		return String.valueOf((float) (alpha / 255));
	}

	public static String convertLineJoinToString(int lineJoin) {
		if (lineJoin == BasicStroke.JOIN_BEVEL)
			return "bevel";
		else if (lineJoin == BasicStroke.JOIN_MITER)
			return "miter";
		else if (lineJoin == BasicStroke.JOIN_ROUND)
			return "round";
		return null;
	}

	public static String convertLineCapToString(int endCap) {
		if (endCap == BasicStroke.CAP_BUTT)
			return "butt";
		else if (endCap == BasicStroke.CAP_ROUND)
			return "round";
		else if (endCap == BasicStroke.CAP_SQUARE)
			return "square";

		return null;
	}

	public static int setMarkerStyle(String name) {
		if (name == null)
			return SimpleMarkerSymbol.SQUARE_STYLE;
		else if (name.compareTo(SLDTags.CIRCLE) == 0)
			return SimpleMarkerSymbol.CIRCLE_STYLE;
		else if (name.compareTo(SLDTags.TRIANGLE) == 0)
			return SimpleMarkerSymbol.TRIANGLE_STYLE;
		else if (name.compareTo(SLDTags.STAR) == 0)
			return SimpleMarkerSymbol.STAR_STYLE;
		else if (name.compareTo(SLDTags.CROSS) == 0)
			return SimpleMarkerSymbol.CROSS_STYLE;

		return SimpleMarkerSymbol.SQUARE_STYLE;
	}

	public static String convertColorToHexString(java.awt.Color c) {
		String str = Integer.toHexString(c.getRGB() & 0xFFFFFF);
		return ("#" + "000000".substring(str.length()) + str.toUpperCase());
	}

	public static boolean isANumber(String s) {
		final String digit = "([0-9])+" + ".?" + "([0-9])*";
		if (s.matches(digit))
			return true;
		return false;
	}

	public static boolean isColor(String s) {
		final String alpha = "[0-9a-fA-F]";
		final String color = "#" + alpha + "{6}";
		if (s.matches(color))
			return true;
		return false;
	}

	public static String getMarkWellKnownName(int style) {

		if (style == SimpleMarkerSymbol.CIRCLE_STYLE)
			return "circle";
		else if (style == SimpleMarkerSymbol.CROSS_STYLE)
			return "cross";
		else if (style == SimpleMarkerSymbol.SQUARE_STYLE)
			return "square";
		else if (style == SimpleMarkerSymbol.TRIANGLE_STYLE)
			return "triangle";
		else if (style == SimpleMarkerSymbol.STAR_STYLE)
			return "star";

		return "square";
	}

	public static boolean isLineJoin(String literal) {
		if (literal.compareTo("bevel") == 0 || literal.compareTo("miter") == 0
				|| literal.compareTo("round") == 0)
			return true;
		return false;
	}

	public static boolean isLineCap(String literal) {
		if (literal.compareTo("butt") == 0 || literal.compareTo("round") == 0
				|| literal.compareTo("square") == 0)
			return true;
		return false;
	}
}
