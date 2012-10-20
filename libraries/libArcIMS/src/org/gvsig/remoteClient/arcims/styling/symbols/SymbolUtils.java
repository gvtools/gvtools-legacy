/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */

/**
 *
 */
package org.gvsig.remoteClient.arcims.styling.symbols;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;

import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;

/**
 * @author jsanz
 * 
 */
public class SymbolUtils {
	/*
	 * Point types
	 */
	public static final String POINT_TYPE_CIRCLE = "circle";
	public static final String POINT_TYPE_TRIANGLE = "triangle";
	public static final String POINT_TYPE_SQUARE = "square";
	public static final String POINT_TYPE_CROSS = "cross";

	/*
	 * Line types
	 */
	public static final String LINE_TYPE_SOLID = "solid";
	public static final String LINE_TYPE_DASH = "dash";
	public static final String LINE_TYPE_DOT = "dot";
	public static final String LINE_TYPE_DASH_DOT = "dash_dot";
	public static final String LINE_TYPE_DASH_DOT_DOT = "dash_dot_dot";
	public static final HashMap LINE_TYPES = new HashMap();

	static {
		LINE_TYPES.put(LINE_TYPE_SOLID, "0");
		LINE_TYPES.put(LINE_TYPE_DASH, "2");
		LINE_TYPES.put(LINE_TYPE_DOT, "0.05,2");
		LINE_TYPES.put(LINE_TYPE_DASH_DOT, "2,2,0.01,2");
		LINE_TYPES.put(LINE_TYPE_DASH_DOT_DOT, "2,2,0.01,2,0.01,2");
	}

	/*
	 * End line types
	 */
	public static final String CAP_TYPE_BUTT = "butt";
	public static final String CAP_TYPE_ROUND = "round";
	public static final String CAP_TYPE_SQUARE = "square";

	/*
	 * Joining line types
	 */
	public static final String JOIN_TYPE_ROUND = "round";
	public static final String JOIN_TYPE_MITER = "miter";
	public static final String JOIN_TYPE_BEVEL = "bevel";

	/*
	 * Polygon fill types
	 */
	public static final String FILL_TYPE_SOLID = "solid";
	public static final String FILL_TYPE_BDIAG = "bdiagonal";
	public static final String FILL_TYPE_FDIAG = "fdiagonal";
	public static final String FILL_TYPE_CROSS = "cross";
	public static final String FILL_TYPE_DIAGC = "diagcross";
	public static final String FILL_TYPE_HORIZ = "horizontal";
	public static final String FILL_TYPE_VERT = "vertical";
	public static final String FILL_TYPE_GRAYFILL = "gray";
	public static final String FILL_TYPE_LIGHTGRAYFILL = "lightgray";
	public static final String FILL_TYPE_DARKGRAYFILL = "darkgray";

	/*
	 * Text types
	 */
	public static final String TEXT_TYPE_REGULAR = "regular";
	public static final String TEXT_TYPE_BOLD = "bold";
	public static final String TEXT_TYPE_ITALIC = "italic";
	public static final String TEXT_TYPE_UNDERLINE = "underline";
	public static final String TEXT_TYPE_OUTLINE = "outline";
	public static final String TEXT_TYPE_BOLDITALIC = "bolditalic";

	/**
	 * Parses a <em>RR,GG,BB</em> string into a correct Color object
	 * 
	 * @see java.awt.Color#Color(int, int, int)
	 * @param str
	 * @return
	 */
	public static Color getColor(String str, float alpha) {
		String[] strA = str.split(",");

		if (strA.length != 3) {
			return null;
		}

		int red = Integer.parseInt(strA[0]);
		int green = Integer.parseInt(strA[1]);
		int blue = Integer.parseInt(strA[2]);
		int ialpha = (int) alpha * 255;

		return new Color(red, green, blue, ialpha);
	}

	/**
	 * Generates a String representations on the form <em>RR,GG,BB</em>
	 * 
	 * @see java.awt.Color
	 * @param color
	 * @return
	 */
	public static String getStringColor(Color color) {
		return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
	}

	/**
	 * Returns a correct Shape type from an ArcIMS Symbol definition
	 * 
	 * @see IArcIMSSymbol
	 * @see FConstant
	 * @param isimb
	 * @return
	 */
	public static int getShapeType(IArcIMSSymbol isimb) {
		ISymbol simb = isimb.getFSymbol();
		int fsimbType = simb.getSymbolType();
		int type = FConstant.SHAPE_TYPE_NULL;

		if (fsimbType == FConstant.SYMBOL_TYPE_FILL) {
			type = FConstant.SHAPE_TYPE_POLYGON;
		} else if (fsimbType == FConstant.SYMBOL_TYPE_LINE) {
			type = FConstant.SHAPE_TYPE_POLYLINE;
		} else if (fsimbType == FConstant.SYMBOL_TYPE_MULTIPOINT) {
			type = FConstant.SHAPE_TYPE_MULTIPOINT;
		}

		return type;
	}

	public static int getFontStyle(String strStyle) {
		if (strStyle.equals(TEXT_TYPE_BOLD)) {
			return Font.BOLD;
		} else if (strStyle.equals(TEXT_TYPE_ITALIC)) {
			return Font.ITALIC;
		}
		// else if (strStyle.equals(TEXT_TYPE_UNDERLINE))
		// return Font.HANGING_BASELINE;
		// else if (strStyle.equals(TEXT_TYPE_OUTLINE))
		// return Font.PLAIN;
		else if (strStyle.equals(TEXT_TYPE_BOLDITALIC)) {
			return Font.BOLD + Font.ITALIC;
		}

		// If no type has found, return a plain type
		return Font.PLAIN;
	}

	public static boolean isVoid(String param) {
		boolean flag = true;

		if (param == null) {
			flag = false;
		} else if (param.equals("")) {
			flag = false;
		}

		return flag;
	}
}
