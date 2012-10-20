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

import java.util.StringTokenizer;

import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;

import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;

/**
 * @author jsanz
 * 
 */
public class ArcImsSimpleLineSymbol extends AbstractSymbol implements
		IArcIMSSymbol {
	public static final String TAG = ServiceInfoTags.tSIMPLELINESYMBOL;
	private String color;
	private String width;
	private String type;
	private String captype;
	private String jointype;
	private String transparency;

	/**
	 * The constructor sets default values
	 */
	public ArcImsSimpleLineSymbol() {
		color = "0,0,0";
		width = "0";
		type = SymbolUtils.LINE_TYPE_SOLID;
		captype = SymbolUtils.CAP_TYPE_ROUND;
		jointype = SymbolUtils.JOIN_TYPE_BEVEL;
		transparency = "1";
	}

	public String toString() {
		return "<" + TAG + getParam() + "/>\r\n";
	}

	protected String getParam() {
		String param = "";

		if (SymbolUtils.isVoid(color)) {
			param += (" color=\"" + color + "\"");
		}

		if (SymbolUtils.isVoid(width)) {
			param += (" width=\"" + width + "\"");
		}

		if (SymbolUtils.isVoid(type)) {
			param += (" type=\"" + type + "\"");
		}

		if (SymbolUtils.isVoid(captype)) {
			param += (" captype=\"" + captype + "\"");
		}

		if (SymbolUtils.isVoid(jointype)) {
			param += (" jointype=\"" + jointype + "\"");
		}

		if (SymbolUtils.isVoid(transparency)) {
			param += (" transparency=\"" + transparency + "\"");
		}

		return param;
	}

	/**
	 * @return Returns the color.
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color
	 *            The color to set.
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return Returns the width.
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            The width to set.
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * @return Returns the captype.
	 */
	public String getCaptype() {
		return captype;
	}

	/**
	 * @param captype
	 *            The captype to set.
	 */
	public void setCaptype(String captype) {
		this.captype = captype;
	}

	/**
	 * @return Returns the jointype.
	 */
	public String getJointype() {
		return jointype;
	}

	/**
	 * @param jointype
	 *            The jointype to set.
	 */
	public void setJointype(String jointype) {
		this.jointype = jointype;
	}

	/**
	 * @return Returns the transparency.
	 */
	public String getTransparency() {
		return transparency;
	}

	/**
	 * @param transparency
	 *            The transparency to set.
	 */
	public void setTransparency(String transparency) {
		this.transparency = transparency;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.remoteClient.arcims.styling.symbols.IArcIMSSymbol#getFSymbol()
	 */
	public ISymbol getFSymbol() {
		return ArcImsFSymbolFactory.getFSymbol(this);
	}

	/**
	 * Converts the comma-delimited string into a List of trimmed strings.
	 * 
	 * @param linePattern
	 *            a String with comma-delimited values
	 * @param lineWidth
	 *            DOCUMENT ME!
	 * 
	 * @return a List of the Strings that were delimited by commas
	 * 
	 * @throws IllegalArgumentException
	 *             DOCUMENT ME!
	 */
	public static float[] toArray(String linePattern, float lineWidth) {
		StringTokenizer st = new StringTokenizer(linePattern, ",");
		int numTokens = st.countTokens();

		float[] array = new float[numTokens];

		for (int i = 0; i < numTokens; i++) {
			String string = st.nextToken();
			array[i] = Float.parseFloat(string) * lineWidth;

			if (array[i] <= 0) {
				return null;
			}
		}

		return array;
	}
}
