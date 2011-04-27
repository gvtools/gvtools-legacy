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
package org.gvsig.symbology.fmap.symbols;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.PrintQuality;

import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.CartographicSupportToolkit;
import com.iver.cit.gvsig.fmap.core.FPolygon2D;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.AbstractFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SymbolDrawingException;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.utiles.StringUtilities;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.swing.threads.Cancellable;
import com.vividsolutions.jts.geom.Geometry;

/**
 * LineFillSymbol allows to define a filling pattern composed by lines to complete the padding
 * of the polygon.
 * @autor jaume dominguez faus - jaume.dominguez@iver.es
 */
public class LineFillSymbol extends AbstractFillSymbol {
	private double angle;
	private ILineSymbol lineSymbol = SymbologyFactory.createDefaultLineSymbol();
	private double offset = 5, csOffset = offset;
	private double separation = 5, csSeparation = separation;
	private double symbolLineWidth, csLineWidth=symbolLineWidth;
	private LineFillSymbol symSel;
	private PrintRequestAttributeSet properties;
	/**
	 * Returns the rotation angle of the lines that compose the filling pattern
	 * @return angle
	 */
	public double getAngle() {
		return angle;
	}
	/**
	 * Sets the rotation angle of the lines that compose the filling pattern
	 * @param angle
	 */
	public void setAngle(double angle) {
		this.angle = angle;
	}
	/**
	 * Gets the line symbol that is used to create the filling pattern.
	 * @return lineSymbol, ILineSymbol
	 */
	public ILineSymbol getLineSymbol() {
		return lineSymbol;
	}
	/**
	 * Establishes the line symbol that is used to create the filling pattern.
	 * @param lineSymbol,ILineSymbol
	 */
	public void setLineSymbol(ILineSymbol lineSymbol) {
		if (lineSymbol != null)
			symbolLineWidth = lineSymbol.getLineWidth();
		else
			symbolLineWidth = 0;
		this.lineSymbol = lineSymbol;

	}
	/**
	 * Gets the offset of the lines inside the filling pattern
	 * @return offset
	 */
	public double getOffset() {
		return offset;
	}
	/**
	 * Sets the offset of the lines inside the filling pattern
	 * @param offset
	 */
	public void setOffset(double offset) {
		this.offset = offset;
		this.csOffset = offset;
	}
	/**
	 * Returns the separation between lines that is used to create the filling pattern
	 * @return
	 */
	public double getSeparation() {
		return separation;
	}
	/**
	 * Establishes the separation between lines that is used to create the filling pattern
	 * @param separation
	 */
	public void setSeparation(double separation) {
		if (separation == 0) separation = 1D;
		this.separation = separation;
		this.csSeparation = separation;
	}

	public void draw(Graphics2D g, AffineTransform affineTransform, FShape shp, Cancellable cancel) {
		Color fillColor = getFillColor();
		if (fillColor!=null && hasFill()) {
			g.setColor(getFillColor());
			g.fill(shp);
		}

		if (lineSymbol != null) {
			if (csSeparation < 1) csSeparation = 1;
			Rectangle rClip = null;
			if (g.getClipBounds()!=null){
				rClip=(Rectangle)g.getClipBounds().clone();
				g.setClip(rClip.x, rClip.y, rClip.width, rClip.height);
			}
			g.clip(shp);
			g.setColor(Color.black);

			Rectangle2D bounds = shp.getBounds();
			double radius = Math.abs(Math.max(bounds.getHeight(), bounds.getWidth()));
			double centerX = bounds.getCenterX();
			double centerY = bounds.getCenterY();
			double aux = -(radius+csOffset);

			g.translate(centerX, centerY);
			g.rotate(angle);
			while ((cancel==null || !cancel.isCanceled()) && aux <= radius) {
				double y = aux;
				double x1 = - radius;
				double x2 = radius;
				Line2D line;
				line = new Line2D.Double(x1, y, x2, y);
				lineSymbol.draw(g, null, new FPolyline2D(new GeneralPathX(line)), null);
				aux += csSeparation;
			}
			g.rotate(-angle);
			g.translate(-centerX, -centerY);
			g.setClip(rClip);
		}

		ILineSymbol outLineSymbol = getOutline();
		if (outLineSymbol != null && hasOutline())
			outLineSymbol.draw(g, affineTransform, shp, cancel);
	}

	public void drawInsideRectangle(Graphics2D g,
			AffineTransform scaleInstance, Rectangle r, PrintRequestAttributeSet properties) throws SymbolDrawingException {
		if (properties==null)
			draw(g, null, new FPolygon2D(new GeneralPathX(r)), null);
		else
			print(g,new AffineTransform(),new FPolygon2D(new GeneralPathX(r)), properties);
	}


	public ISymbol getSymbolForSelection() {
		if (symSel == null) symSel = new LineFillSymbol();
		symSel.setFillColor(ISymbol.SELECTION_COLOR);
		return symSel;
	}

	public int getSymbolType() {
		return FShape.POLYGON;
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("className", getClassName());
		xml.putProperty("desc", getDescription());
		xml.putProperty("isShapeVisible", isShapeVisible());
		xml.putProperty("angle", angle);
		xml.putProperty("offset", offset);
		xml.putProperty("separation", separation);
		xml.putProperty("symbolLineWidth", symbolLineWidth);
		xml.putProperty("referenceSystem", getReferenceSystem());
		xml.putProperty("unit", getUnit());

		if (getFillColor() !=null)
			xml.putProperty("fillColor", StringUtilities.color2String(getFillColor()));
		xml.putProperty("hasFill", hasFill());
		if (lineSymbol!=null) {
			XMLEntity xmlLine = lineSymbol.getXMLEntity();
			xmlLine.putProperty("id", "fillLine");
			xml.addChild(xmlLine);
		}

		ILineSymbol outline = getOutline();
		if (outline!=null) {
			XMLEntity xmlOutline = outline.getXMLEntity();
			xmlOutline.putProperty("id", "outline");
			xml.addChild(xmlOutline);
		}
		xml.putProperty("hasOutline", hasOutline());
		return xml;
	}

	public void setXMLEntity(XMLEntity xml) {
		setDescription(xml.getStringProperty("desc"));
		setIsShapeVisible(xml.getBooleanProperty("isShapeVisible"));
		setAngle(xml.getDoubleProperty("angle"));
		setOffset(xml.getDoubleProperty("offset"));
		setSeparation(xml.getDoubleProperty("separation"));

		if (xml.contains("fillColor"))
			setFillColor(StringUtilities.
					string2Color(xml.getStringProperty("fillColor")));

		if (xml.contains("hasFill"))
			setHasFill(xml.getBooleanProperty("hasFill"));

		XMLEntity lineSymbolXML = xml.firstChild("id", "fillLine");
		if (lineSymbolXML != null) {
			setLineSymbol((ILineSymbol) SymbologyFactory.
					createSymbolFromXML(lineSymbolXML, "fill symbol"));
		}

		XMLEntity outlineXML = xml.firstChild("id", "outline");
		if (outlineXML != null) {
			setOutline((ILineSymbol) SymbologyFactory.
					createSymbolFromXML(outlineXML, "outline symbol"));
		}
		if (xml.contains("hasOutline"))
			setHasOutline(xml.getBooleanProperty("hasOutline"));

		if (xml.contains("unit")) { // remove this line when done
		// measure unit (for outline)
		setUnit(xml.getIntProperty("unit"));

		// reference system (for outline)
		setReferenceSystem(xml.getIntProperty("referenceSystem"));
		}
	}

	public String getClassName() {
		return getClass().getName();
	}

	public void print(Graphics2D g, AffineTransform at, FShape shape,
			PrintRequestAttributeSet properties) {
		this.properties=properties;
        draw(g, at, shape, null);
        this.properties=null;
	}

	public void setCartographicSize(double cartographicSize, FShape shp) {
		super.setCartographicSize(cartographicSize, shp);
		if (getLineSymbol()!=null) {
			getLineSymbol().setCartographicSize(csLineWidth, shp);
		}
		csOffset=offset;
		csSeparation=separation;
		csLineWidth=symbolLineWidth;

	}

	public double toCartographicSize(ViewPort viewPort, double dpi, FShape shp) {
		double s = super.toCartographicSize(viewPort, dpi, shp);
		csOffset = CartographicSupportToolkit.
			getCartographicLength(this, offset, viewPort, dpi);

		csSeparation = CartographicSupportToolkit.
			getCartographicLength(this, separation, viewPort, dpi);
		double csLineWidth = CartographicSupportToolkit.
			getCartographicLength(this, symbolLineWidth, viewPort, dpi);
		if (getLineSymbol()!=null) {
			getLineSymbol().setCartographicSize(csLineWidth, shp);
		}
		return s;
	}


	@Override
	public void setUnit(int unitIndex) {
		super.setUnit(unitIndex);
		if (getLineSymbol()!=null) {
			getLineSymbol().setUnit(unitIndex);
		}
	}

	@Override
	public void setReferenceSystem(int system) {
		super.setReferenceSystem(system);
		if (getLineSymbol()!=null) {
			getLineSymbol().setReferenceSystem(system);
		}
	}
}
