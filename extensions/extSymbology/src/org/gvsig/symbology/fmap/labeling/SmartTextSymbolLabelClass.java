/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
package org.gvsig.symbology.fmap.labeling;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.PathIterator;

import org.gvsig.symbology.fmap.symbols.SmartTextSymbol;

import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.styles.ILabelStyle;
import com.iver.cit.gvsig.fmap.core.symbols.ITextSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SymbolDrawingException;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelClass;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelLocationMetrics;
import com.iver.utiles.XMLEntity;

/**
 * <p>
 * SmartTextSymbolLabelClass.java<br>
 * </p>
 *
 * <p>
 *
 *   This is a <b>wrapper</b> to be able to use a SmartTextSymbol as a
 * LabelClass. A SmartTextSymbol uses a line instead of a point.
 * Being in fact a Line and not a Marker, it does not make any sense
 * to have label styles since the label styles are well-defined rectangle
 * areas where texts are placed in fields backgrounded by an image. In this
 * contexts, the area is defined dinamically for each line, and there is no
 * sense to have texts fields. They will be rendered as a single string
 * along a line.<br>
 * </p>
 * <p> The label itself is the SmartTextSymbol,
 * the geometry (the line), the label expression,
 * and the text applied to the symbol.<br>
 * </p>
 * <p>
 *   Most of the operations performed by this LabelClass are in fact
 * delegated to the symbol.<br>
 * </p>
 *
 *
 * @author jaume dominguez faus - jaume.dominguez@iver.es Mar 6, 2008
 *
 */
public class SmartTextSymbolLabelClass extends LabelClass {
	private SmartTextSymbol smartTextSymbol;

	@Override
	public void draw(Graphics2D graphics, LabelLocationMetrics llm, FShape shp) {

		getTextSymbol().draw(graphics, null, shp, null);


	}

	@Override
	public void drawInsideRectangle(Graphics2D graphics, Rectangle bounds)
			throws SymbolDrawingException {
		getTextSymbol().drawInsideRectangle(graphics, null, bounds, null);
	}


	@Override
	public String getClassName() {
		return getClass().getName();
	}

	@Override
	public ILabelStyle getLabelStyle() {
		// label style not allowed in this context
		return null;
	}


//	@Override
//	public FShape getShape(LabelLocationMetrics llm) {
//		// TODO Auto-generated method stub
//		throw new Error("Not yet implemented!");
//	}


	@Override
	public ITextSymbol getTextSymbol() {
		if (smartTextSymbol == null) {
			smartTextSymbol = new SmartTextSymbol();
		}
		return smartTextSymbol;
	}


	@Override
	public XMLEntity getXMLEntity() {
		XMLEntity xml = super.getXMLEntity();
		xml.putProperty("className", getClassName());
		return xml;
	}

	@Override
	public void setLabelStyle(ILabelStyle labelStyle) {
		// operation don't supported in this context
	}


	@Override
	public void setTextSymbol(ITextSymbol textSymbol) {
////		if (! (textSymbol instanceof SmartTextSymbol)) throw new IllegalArgumentException("Only SmartTextSymbol allowed in this context");
//		if (! (textSymbol instanceof SmartTextSymbol)) {
//			// transform it into a SmartTextSymbol
//			SmartTextSymbol aux = new SmartTextSymbol();
//			if (textSymbol != null) {
//				aux.setDescription(textSymbol.getDescription());
//				aux.setText(textSymbol.getText());
//				aux.setFont(textSymbol.getFont());
//				aux.setTextColor(textSymbol.getTextColor());
//				aux.setFontSize(textSymbol.getFont().getSize());
//
//			}
//			textSymbol = aux;
//		}
		this.smartTextSymbol = (SmartTextSymbol) textSymbol;
		super.setTextSymbol(textSymbol);
	}


}
