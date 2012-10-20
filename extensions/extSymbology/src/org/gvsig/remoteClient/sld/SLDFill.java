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

import java.awt.Color;

import org.gvsig.remoteClient.sld.filterEncoding.FExpression;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;

/**
 * Implements the Fill element of an SLD implementation specification.
 * <p>
 * The Fill element specifies how the area of the geometry will be filled. There
 * are two types of fills, solid-color and repeated GraphicFill. The repeated
 * graphic fill is selected only if the GraphicFill element is present. If the
 * Fill element is omitted from its parent element, then no fill will be
 * rendered. The GraphicFill and CssParameter elements are discussed in
 * conjunction with the Stroke element in Section 11.1.3. Here, the CssParameter
 * names are �fill� instead of �stroke� and �fill-opacity� instead
 * of �stroke-opacity�. None of the other CssParameters in Stroke are
 * available for filling and the default value for the fill color in this
 * context is 50% gray (value �#808080�).
 * 
 * @see SLDFill
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public abstract class SLDFill implements ISLDFeatures {

	protected SLDGraphic fillGraphic;
	protected FExpression expressionColor = new FExpression();
	protected FExpression expressionOpacity = new FExpression();
	String cad;

	public SLDGraphic getFillGraphic() {
		return fillGraphic;
	}

	public void setFillGraphic(SLDGraphic fillGraphic) {
		this.fillGraphic = fillGraphic;
	}

	public FExpression getExpressionColor() {
		return expressionColor;
	}

	public void setExpressionColor(FExpression expressionColor) {
		this.expressionColor = expressionColor;
	}

	public FExpression getExpressionOpacity() {
		return expressionOpacity;
	}

	public void setExpressionOpacity(FExpression expressionOpacity) {
		this.expressionOpacity = expressionOpacity;
	}

	public String getCad() {
		return cad;
	}

	public void setCad(String cad) {
		this.cad = cad;
	}

	public float getFillOpacity() {
		return Float.valueOf(expressionOpacity.getLiteral());
	}

	public Color getFillColor() throws NumberFormatException,
			LegendDriverException {
		if (this.getExpressionColor().getLiteral() != null)
			return SLDUtils.convertHexStringToColor(this.expressionColor
					.getLiteral());
		return null;
	}
}
