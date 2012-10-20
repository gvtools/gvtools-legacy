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

import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;

import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;

/**
 * @author jsanz
 * 
 */
public class ArcImsSimplePolygonSymbol extends AbstractSymbol implements
		IArcIMSSymbol {
	public static final String TAG = ServiceInfoTags.tSIMPLEPOLYGONSYMBOL;
	private ArcImsSimpleLineSymbol boundary;
	private boolean hasBoundary;
	private String fillcolor;
	private String filltype;
	private String filltransparency;

	/**
	 * @param hasBoundary
	 *            Set true if the symboll will have a boundary
	 * @param fillcolor
	 *            String presentation of a color
	 */
	public ArcImsSimplePolygonSymbol() {
		setHasBoundary(true);
		fillcolor = "0,200,0";
		filltransparency = "1";
		filltype = SymbolUtils.FILL_TYPE_SOLID;
	}

	public String toString() {
		return "<" + TAG + getParam() + "/>\r\n";
	}

	/**
	 * @return Returns the boundary.
	 */
	public ArcImsSimpleLineSymbol getBoundary() {
		return boundary;
	}

	/**
	 * @param boundary
	 *            The boundary to set.
	 */
	public void setBoundary(ArcImsSimpleLineSymbol boundary) {
		this.boundary = boundary;
	}

	/**
	 * @return Returns the fillcolor.
	 */
	public String getFillcolor() {
		return fillcolor;
	}

	/**
	 * @param fillcolor
	 *            The fillcolor to set.
	 */
	public void setFillcolor(String fillcolor) {
		this.fillcolor = fillcolor;
	}

	/**
	 * @return Returns the filltransparency.
	 */
	public String getFilltransparency() {
		return filltransparency;
	}

	/**
	 * @param filltransparency
	 *            The filltransparency to set.
	 */
	public void setFilltransparency(String filltransparency) {
		this.filltransparency = filltransparency;
	}

	/**
	 * @return Returns the filltype.
	 */
	public String getFilltype() {
		return filltype;
	}

	/**
	 * @param filltype
	 *            The filltype to set.
	 */
	public void setFilltype(String filltype) {
		this.filltype = filltype;
	}

	/**
	 * @return Returns the hasBoundary.
	 */
	public boolean isHasBoundary() {
		return hasBoundary;
	}

	/**
	 * If hasBoundary is set true, a
	 * 
	 * @param hasBoundary
	 *            The hasBoundary to set.
	 */
	public void setHasBoundary(boolean hasBoundary) {
		this.hasBoundary = hasBoundary;

		if (!hasBoundary) {
			boundary = null;
		} else {
			boundary = new ArcImsSimpleLineSymbol();
			boundary.setColor("0,0,0");
			boundary.setWidth("1");
			boundary.setType(SymbolUtils.LINE_TYPE_SOLID);
			boundary.setCaptype(SymbolUtils.CAP_TYPE_BUTT);
			boundary.setJointype(SymbolUtils.JOIN_TYPE_ROUND);
			boundary.setTransparency("1");
		}
	}

	protected String getParam() {
		String param = "";

		// Checks for every parameter
		if (hasBoundary) {
			param += " boundary=\"true\"";

			String bColor = boundary.getColor();
			String bType = boundary.getType();
			String bCapType = boundary.getCaptype();
			String bJoinType = boundary.getJointype();

			if (SymbolUtils.isVoid(bColor)) {
				param += (" boundarycolor=\"" + bColor + "\"");
			}

			if (SymbolUtils.isVoid(bType)) {
				param += (" boundarytype=\"" + bType + "\"");
			}

			if (SymbolUtils.isVoid(bCapType)) {
				param += (" boundarycaptype=\"" + bCapType + "\"");
			}

			if (SymbolUtils.isVoid(bJoinType)) {
				param += (" boundarytype=\"" + bJoinType + "\"");
			}
		}

		if (SymbolUtils.isVoid(fillcolor)) {
			param += (" fillcolor=\"" + fillcolor + "\"");
		}

		if (SymbolUtils.isVoid(filltype)) {
			param += (" filltype=\"" + filltype + "\"");
		}

		if (SymbolUtils.isVoid(filltransparency)) {
			param += (" filltransparency=\"" + filltransparency + "\"");
		}

		return param;
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
}
