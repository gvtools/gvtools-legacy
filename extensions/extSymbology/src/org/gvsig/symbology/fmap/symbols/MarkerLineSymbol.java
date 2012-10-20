/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
package org.gvsig.symbology.fmap.symbols;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.print.attribute.PrintRequestAttributeSet;

import org.apache.batik.ext.awt.geom.PathLength;

import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.styles.ILineStyle;
import com.iver.cit.gvsig.fmap.core.symbols.AbstractLineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.swing.threads.Cancellable;

/**
 * MarkerLineSymbol allows to use any symbol defined as an image to draw a
 * lineal object. The line will be painted as a run of symbols through the path
 * defined by the line.
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class MarkerLineSymbol extends AbstractLineSymbol {
	private IMarkerSymbol marker;
	private MarkerLineSymbol symSelect;
	private double separation = 5;
	private double cartographicSeparation;
	private double width;
	private PrintRequestAttributeSet properties;

	{
		marker = SymbologyFactory.createDefaultMarkerSymbol();
		marker.setSize(5);
	}

	public void setLineWidth(double width) {
		this.width = width;
		getLineStyle().setLineWidth((float) width);
	}

	public double getLineWidth() {
		return width;
	}

	public void draw(Graphics2D g, AffineTransform affineTransform, FShape shp,
			Cancellable cancel) {
		PathLength pl = new PathLength(shp);
		float myLineLength = pl.lengthOfPath(); // length without the first and
												// last arrow
		float separation = (float) cartographicSeparation;

		FPoint2D p = new FPoint2D(pl.pointAtLength(0));
		double lineWidth = getLineStyle().getLineWidth();
		marker.setSize(lineWidth);
		marker.draw(g, affineTransform, p, null);

		float length = (float) (separation + lineWidth);
		while ((cancel == null || !cancel.isCanceled())
				&& length < myLineLength) {
			p = new FPoint2D(pl.pointAtLength(length));
			marker.draw(g, affineTransform, p, null);
			length += separation + lineWidth;
		}
	}

	/**
	 * Gets the separation between the marker symbols that are used to draw the
	 * line.
	 * 
	 * @return separation
	 */
	public double getSeparation() {
		return separation;
	}

	public ISymbol getSymbolForSelection() {
		if (symSelect == null) {
			symSelect = (MarkerLineSymbol) SymbologyFactory
					.createSymbolFromXML(getXMLEntity(),
							"selection derived symbol");
			symSelect.setMarker((IMarkerSymbol) symSelect.getMarker()
					.getSymbolForSelection());

		}

		return symSelect;
	}

	public Color getColor() {
		return marker.getColor();
	}

	public void setLineColor(Color color) {
		marker.setColor(color);
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("className", getClassName());
		xml.putProperty("isShapeVisible", isShapeVisible());
		xml.putProperty("desc", getDescription());
		xml.putProperty("unit", getUnit());
		xml.putProperty("referenceSystem", getReferenceSystem());
		xml.putProperty("separation", getSeparation());
		XMLEntity xmlMarker = marker.getXMLEntity();
		xmlMarker.putProperty("id", "marker");
		xml.addChild(xmlMarker);
		setLineWidth(getLineWidth()); // not a joke;
		XMLEntity xmlLineStyle = getLineStyle().getXMLEntity();
		xmlLineStyle.putProperty("id", "lineStyle");
		xml.addChild(xmlLineStyle);

		return xml;
	}

	public void setXMLEntity(XMLEntity xml) {
		setIsShapeVisible(xml.getBooleanProperty("isShapeVisible"));
		setDescription(xml.getStringProperty("desc"));
		setSeparation(xml.getDoubleProperty("separation"));
		setUnit(xml.getIntProperty("unit"));
		setReferenceSystem(xml.getIntProperty("referenceSystem"));
		marker = (IMarkerSymbol) SymbologyFactory.createSymbolFromXML(
				xml.firstChild("id", "marker"), "theMarker");
		setLineStyle((ILineStyle) SymbologyFactory.createStyleFromXML(
				xml.firstChild("id", "lineStyle"), "theLineStyle"));
		width = getLineStyle().getLineWidth();
	}

	/**
	 * Sets the separation between the marker symbols that compose the line
	 * 
	 * @param separation
	 */
	public void setSeparation(double separation) {
		this.separation = separation;
		this.cartographicSeparation = separation;
	}

	public String getClassName() {
		return getClass().getName();
	}

	public void print(Graphics2D g, AffineTransform at, FShape shape,
			PrintRequestAttributeSet properties) {
		this.properties = properties;
		draw(g, at, shape, null);
		this.properties = null;
	}

	/**
	 * Returns the marker symbol that compose the line
	 * 
	 * @return marker IMarkerSymbol
	 */
	public IMarkerSymbol getMarker() {
		return marker;
	}

	/**
	 * Sets the marker symbol that compose the line
	 * 
	 * @param marker
	 *            IMarkerSymbol
	 */
	public void setMarker(IMarkerSymbol marker) {
		this.marker = marker;
	}

	@Override
	public void setCartographicSize(double cartographicSize, FShape shp) {
		double oldLineWidth = getLineWidth();
		super.setCartographicSize(cartographicSize, shp);
		double newLineWidth = getLineWidth();
		double scale = newLineWidth / oldLineWidth;
		cartographicSeparation = separation * scale;
	}

	@Override
	public void setUnit(int unitIndex) {
		super.setUnit(unitIndex);
		marker.setUnit(unitIndex);
	}

	@Override
	public void setReferenceSystem(int system) {
		super.setReferenceSystem(system);
		marker.setReferenceSystem(system);
	}

}
