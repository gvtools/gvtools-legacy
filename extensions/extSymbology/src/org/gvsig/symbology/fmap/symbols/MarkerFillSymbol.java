/* gvSIG. Sistema de Informaciï¿½n Geogrï¿½fica de la Generalitat Valenciana
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
 *   Av. Blasco Ibï¿½ï¿½ez, 50
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

/* CVS MESSAGES:
 *
 * $Id: MarkerFillSymbol.java 16176 2007-11-08 16:07:26Z jdominguez $
 * $Log$
 * Revision 1.19  2007-09-21 12:25:32  jaume
 * cancellation support extended down to the IGeometry and ISymbol level
 *
 * Revision 1.18  2007/09/20 11:53:11  jvidal
 * bug solved
 *
 * Revision 1.16  2007/08/09 10:39:41  jaume
 * first round of found bugs fixed
 *
 * Revision 1.15  2007/08/08 12:04:05  jvidal
 * javadoc
 *
 * Revision 1.14  2007/08/03 09:22:09  jaume
 * refactored class names
 *
 * Revision 1.13  2007/08/02 11:13:50  jaume
 * char encoding fix
 *
 * Revision 1.12  2007/08/01 11:45:59  jaume
 * passing general tests (drawing test yet missing)
 *
 * Revision 1.11  2007/07/23 06:52:25  jaume
 * default selection color refactored, moved to MapContext
 *
 * Revision 1.10  2007/05/28 15:36:42  jaume
 * *** empty log message ***
 *
 * Revision 1.9  2007/05/08 08:47:40  jaume
 * *** empty log message ***
 *
 * Revision 1.8  2007/03/28 16:48:14  jaume
 * *** empty log message ***
 *
 * Revision 1.7  2007/03/26 14:25:17  jaume
 * implements IPrintable
 *
 * Revision 1.6  2007/03/21 17:36:22  jaume
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/13 16:58:36  jaume
 * Added QuantityByCategory (Multivariable legend) and some bugfixes in symbols
 *
 * Revision 1.4  2007/03/09 11:20:57  jaume
 * Advanced symbology (start committing)
 *
 * Revision 1.2.2.4  2007/02/16 10:54:12  jaume
 * multilayer splitted to multilayerline, multilayermarker,and  multilayerfill
 *
 * Revision 1.2.2.3  2007/02/15 16:23:44  jaume
 * *** empty log message ***
 *
 * Revision 1.2.2.2  2007/02/12 15:15:20  jaume
 * refactored interval legend and added graduated symbol legend
 *
 * Revision 1.2.2.1  2007/02/09 07:47:05  jaume
 * Isymbol moved
 *
 * Revision 1.2  2007/01/10 16:39:41  jaume
 * ISymbol now belongs to com.iver.cit.gvsig.fmap.core.symbols package
 *
 * Revision 1.1  2007/01/10 16:31:36  jaume
 * *** empty log message ***
 *
 * Revision 1.10  2006/11/09 18:39:05  jaume
 * *** empty log message ***
 *
 * Revision 1.9  2006/11/09 10:22:50  jaume
 * *** empty log message ***
 *
 * Revision 1.8  2006/11/08 13:05:51  jaume
 * *** empty log message ***
 *
 * Revision 1.7  2006/11/08 10:56:47  jaume
 * *** empty log message ***
 *
 * Revision 1.6  2006/11/07 08:52:30  jaume
 * *** empty log message ***
 *
 * Revision 1.5  2006/11/06 17:08:45  jaume
 * *** empty log message ***
 *
 * Revision 1.4  2006/11/06 16:06:52  jaume
 * *** empty log message ***
 *
 * Revision 1.3  2006/11/06 07:33:54  jaume
 * javadoc, source style
 *
 * Revision 1.2  2006/10/31 16:16:34  jaume
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/30 19:30:35  jaume
 * *** empty log message ***
 *
 *
 */
package org.gvsig.symbology.fmap.symbols;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.print.attribute.PrintRequestAttributeSet;

import org.gvsig.symbology.fmap.styles.SimpleMarkerFillPropertiesStyle;

import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.Messages;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.CartographicSupportToolkit;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.styles.IMarkerFillPropertiesStyle;
import com.iver.cit.gvsig.fmap.core.symbols.AbstractFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.AbstractMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SymbolDrawingException;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.utiles.StringUtilities;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.swing.threads.Cancellable;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Allows to define a marker symbol of any type as a path image to be used for a
 * filled of a polygon's padding
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class MarkerFillSymbol extends AbstractFillSymbol {
	public static final int RANDOM_FILL = 3;
	public static final int GRID_FILL = 1;
	public static final int SINGLE_CENTERED_SYMBOL = 2;
	public static int DefaultFillStyle = GRID_FILL;
	private MarkerFillSymbol selectionSymbol;
	private IMarkerFillPropertiesStyle markerFillProperties = new SimpleMarkerFillPropertiesStyle();
	private IMarkerSymbol markerSymbol = SymbologyFactory
			.createDefaultMarkerSymbol();
	private double previousMarkerSize = markerSymbol.getSize();
	private PrintRequestAttributeSet properties;

	public ISymbol getSymbolForSelection() {
		if (selectionSymbol == null) {
			selectionSymbol = (MarkerFillSymbol) SymbologyFactory
					.createSymbolFromXML(getXMLEntity(), null);
			selectionSymbol.setFillColor(MapContext.getSelectionColor());
		}

		return selectionSymbol;
	}

	public void draw(Graphics2D g, AffineTransform affineTransform, FShape shp,
			Cancellable cancel) {
		switch (markerFillProperties.getFillStyle()) {
		case SINGLE_CENTERED_SYMBOL:
			// case a single marker is used into a polygon shapetype
			Geometry geom = FConverter.java2d_to_jts(shp);
			com.vividsolutions.jts.geom.Point centroid = geom.getCentroid();
			/*
			 * Hay ocasiones en que jts no puede calcular un centroide y
			 * devuelve NaN (por ejemplo con geometrías poligonales cuyos puntos
			 * tienen todos la misma abscisa y distinta ordenada con tan solo
			 * una diferencia de 1 ó 2 unidades) entonces, en lugar de utilizar
			 * este centroide tomamos el centro del bounds del shp (la geometría
			 * es tan pequeña que consideramos que deben coincidir).
			 */
			if (!(Double.isNaN(centroid.getX()) || Double
					.isNaN(centroid.getY()))) {
				double centroidX = centroid.getX()
						+ markerFillProperties.getXOffset();
				double centroidY = centroid.getY()
						+ markerFillProperties.getYOffset();
				FPoint2D p = new FPoint2D(new Point2D.Double(centroidX,
						centroidY));
				markerSymbol.draw(g, affineTransform, p, null);
			} else {
				double centroidX = shp.getBounds().getCenterX();
				double centroidY = shp.getBounds().getCenterY();
				FPoint2D p = new FPoint2D(new Point2D.Double(centroidX,
						centroidY));
				markerSymbol.draw(g, affineTransform, p, null);
			}
			break;
		case GRID_FILL:
		// case a grid fill is used
		{
			Rectangle rClip = null;
			if (g.getClipBounds() != null) {
				rClip = (Rectangle) g.getClipBounds().clone();
				g.setClip(rClip.x, rClip.y, rClip.width, rClip.height);
			}
			g.clip(shp);

			int size = (int) markerSymbol.getSize();
			Rectangle rProv = new Rectangle();
			rProv.setFrame(0, 0, size, size);
			Paint resulPatternFill = null;

			double xSeparation = markerFillProperties.getXSeparation(); // TODO
																		// apply
																		// CartographicSupport
			double ySeparation = markerFillProperties.getYSeparation(); // TODO
																		// apply
																		// CartographicSupport
			double xOffset = markerFillProperties.getXOffset();
			double yOffset = markerFillProperties.getYOffset();

			BufferedImage sample = null;
			sample = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D gAux = sample.createGraphics();

			try {
				markerSymbol.drawInsideRectangle(gAux, gAux.getTransform(),
						rProv, null);
			} catch (SymbolDrawingException e) {
				if (e.getType() == SymbolDrawingException.UNSUPPORTED_SET_OF_SETTINGS) {
					try {
						SymbologyFactory
								.getWarningSymbol(
										SymbolDrawingException.STR_UNSUPPORTED_SET_OF_SETTINGS,
										"",
										SymbolDrawingException.UNSUPPORTED_SET_OF_SETTINGS)
								.drawInsideRectangle(gAux, gAux.getTransform(),
										rProv, null);
					} catch (SymbolDrawingException e1) {
						// IMPOSSIBLE TO REACH THIS
					}
				} else {
					// should be unreachable code
					throw new Error(
							Messages.getString("symbol_shapetype_mismatch"));
				}
			}
			rProv.setRect(0, 0, rProv.getWidth() + xSeparation,
					rProv.getHeight() + ySeparation);

			BufferedImage bi = new BufferedImage(rProv.width, rProv.height,
					BufferedImage.TYPE_INT_ARGB);
			gAux = bi.createGraphics();
			gAux.drawImage(sample, null, (int) (xSeparation * 0.5),
					(int) (ySeparation * 0.5));

			resulPatternFill = new TexturePaint(bi, rProv);
			sample = null;
			gAux.dispose();

			g.setColor(null);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			g.translate(xOffset, -yOffset);
			g.setPaint(resulPatternFill);
			g.fill(shp);
			g.translate(-xOffset, +yOffset);
			g.setClip(rClip);
			bi = null;
		}
			break;
		case RANDOM_FILL: {

			double s = markerSymbol.getSize();
			Rectangle r = shp.getBounds();
			int drawCount = (int) (Math.min(r.getWidth(), r.getHeight()) / s);
			Random random = new Random();

			int minx = r.x;
			int miny = r.y;
			int width = r.width;
			int height = r.height;

			r = new Rectangle();
			g.setClip(shp);

			for (int i = 0; (cancel == null || !cancel.isCanceled())
					&& i < drawCount; i++) {
				int x = (int) Math.abs(random.nextDouble() * width);
				int y = (int) Math.abs(random.nextDouble() * height);
				x = x + minx;
				y = y + miny;
				markerSymbol.draw(g, new AffineTransform(), new FPoint2D(x, y),
						cancel);

			}
			g.setClip(null);
		}
			break;
		}
		if (getOutline() != null) {
			getOutline().draw(g, affineTransform, shp, cancel);
		}

	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("className", getClassName());
		xml.putProperty("isShapeVisible", isShapeVisible());
		// color (necessite)
		if (getFillColor() != null)
			xml.putProperty("color",
					StringUtilities.color2String(getFillColor()));
		xml.putProperty("desc", getDescription());
		xml.putProperty("referenceSystem", getReferenceSystem());
		xml.putProperty("unit", getUnit());
		xml.addChild(markerSymbol.getXMLEntity());
		xml.addChild(markerFillProperties.getXMLEntity());

		if (getOutline() != null) {
			XMLEntity outlineXML = getOutline().getXMLEntity();
			outlineXML.putProperty("id", "outline symbol");
			xml.addChild(outlineXML);
		}
		xml.putProperty("hasOutline", hasOutline());
		return xml;
	}

	public int getSymbolType() {
		return FShape.POLYGON;
	}

	public void drawInsideRectangle(Graphics2D g,
			AffineTransform scaleInstance, Rectangle r,
			PrintRequestAttributeSet properties) throws SymbolDrawingException {
		markerFillProperties.setSampleSymbol(markerSymbol);
		switch (markerFillProperties.getFillStyle()) {
		case SINGLE_CENTERED_SYMBOL:
			FPoint2D p = new FPoint2D(r.getCenterX(), r.getCenterY());
			markerSymbol.draw(g, null, p, null);
			break;
		case GRID_FILL: {
			g.setClip(r);
			int size = (int) markerSymbol.getSize();
			if (size <= 0)
				size = 1;
			Rectangle rProv = new Rectangle();
			rProv.setFrame(0, 0, size, size);
			Paint resulPatternFill = null;

			BufferedImage sample = null;
			sample = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D gAux = sample.createGraphics();

			double xSeparation = markerFillProperties.getXSeparation(); // TODO
																		// apply
																		// CartographicSupport
			double ySeparation = markerFillProperties.getYSeparation(); // TODO
																		// apply
																		// CartographicSupport
			double xOffset = markerFillProperties.getXOffset();
			double yOffset = markerFillProperties.getYOffset();

			markerSymbol.drawInsideRectangle(gAux, new AffineTransform(),
					rProv, properties);

			rProv.setRect(0, 0, rProv.getWidth() + xSeparation,
					rProv.getHeight() + ySeparation);

			BufferedImage bi = new BufferedImage(rProv.width, rProv.height,
					BufferedImage.TYPE_INT_ARGB);
			gAux = bi.createGraphics();
			gAux.drawImage(sample, null, (int) (xSeparation * 0.5),
					(int) (ySeparation * 0.5));

			resulPatternFill = new TexturePaint(bi, rProv);
			g.setColor(null);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			// g.translate(xOffset, rProv.getHeight()-yOffset);
			g.translate(xOffset, -yOffset);
			g.setPaint(resulPatternFill);
			g.fill(r);
			// g.translate(-xOffset, -rProv.getHeight()+yOffset);
			g.translate(-xOffset, yOffset);
			g.setClip(null);
		}
			break;
		case RANDOM_FILL:
			g.setClip(r);
			int x = r.x;
			int y = r.y;
			int width = r.width;
			int height = r.height;
			g.setBackground(null);

			markerSymbol.draw(g, null, new FPoint2D((x + width * 0.2),
					(y + height * 0.8)), null);
			markerSymbol.draw(g, null, new FPoint2D((x + width * 0.634),
					(y + height * 0.3)), null);
			markerSymbol.draw(g, null, new FPoint2D((x + width * 0.26),
					(y + height * 0.35)), null);
			markerSymbol.draw(g, null, new FPoint2D((x + width * 0.45),
					(y + height * 0.98)), null);
			markerSymbol.draw(g, null, new FPoint2D((x + width * 0.9),
					(y + height * 0.54)), null);
			markerSymbol.draw(g, null, new FPoint2D((x + width * 1.1),
					(y + height * 0.7)), null);
			g.setClip(null);
			break;
		}
		if (getOutline() != null && hasOutline()) {
			if (properties == null)
				getOutline().draw(g, scaleInstance,
						new FPolyline2D(new GeneralPathX(r)), null);
			else
				getOutline().print(g, scaleInstance,
						new FPolyline2D(new GeneralPathX(r)), properties);
		}
	}

	public String getClassName() {
		return getClass().getName();
	}

	public void setXMLEntity(XMLEntity xml) {
		setDescription(xml.getStringProperty("desc"));
		setIsShapeVisible(xml.getBooleanProperty("isShapeVisible"));

		markerSymbol = (AbstractMarkerSymbol) SymbologyFactory
				.createSymbolFromXML(xml.getChild(0), null);
		markerFillProperties = (SimpleMarkerFillPropertiesStyle) SymbologyFactory
				.createStyleFromXML(xml.getChild(1), null);

		if (xml.contains("unit")) { // remove this line when done
			// measure unit (for outline)
			setUnit(xml.getIntProperty("unit"));

			// reference system (for outline)
			setReferenceSystem(xml.getIntProperty("referenceSystem"));
		}

		if (xml.contains("hasOutline")) {
			XMLEntity outlineXML = xml.firstChild("id", "outline symbol");
			if (outlineXML != null) {
				setOutline((ILineSymbol) SymbologyFactory.createSymbolFromXML(
						outlineXML, "outline"));
			}
		}
	}

	public void setMarker(IMarkerSymbol marker) {
		this.markerSymbol = marker;
	}

	public IMarkerSymbol getMarker() {
		return markerSymbol;
	}

	public Color getFillColor() {
		return markerSymbol.getColor();
	}

	public void setFillColor(Color color) {
		markerSymbol.setColor(color);
	}

	public void print(Graphics2D g, AffineTransform at, FShape shape,
			PrintRequestAttributeSet properties) {
		this.properties = properties;
		draw(g, at, shape, null);
		this.properties = null;

	}

	/**
	 * Sets the markerfillproperties to be used by the class
	 * 
	 * @param markerFillStyle
	 *            ,IMarkerFillPropertiesStyle
	 */
	public void setMarkerFillProperties(
			IMarkerFillPropertiesStyle markerFillStyle) {
		this.markerFillProperties = markerFillStyle;
	}

	/**
	 * Returns the markerfillproperties that are used by the class
	 * 
	 * @return markerFillProperties,IMarkerFillPropertiesStyle
	 */
	public IMarkerFillPropertiesStyle getMarkerFillProperties() {
		return markerFillProperties;
	}

	@Override
	public void setUnit(int unitIndex) {
		super.setUnit(unitIndex);
		if (getMarker() != null) {
			getMarker().setUnit(unitIndex);
		}
	}

	@Override
	public void setReferenceSystem(int system) {
		super.setReferenceSystem(system);
		if (getMarker() != null) {
			getMarker().setReferenceSystem(system);
		}
	}

	public void setCartographicSize(double cartographicSize, FShape shp) {

		super.setCartographicSize(cartographicSize, shp);
		IMarkerSymbol marker = getMarker();
		if (marker != null) {
			marker.setCartographicSize(previousMarkerSize, shp);
		}

		super.setCartographicSize(cartographicSize, shp);

	}

	public double toCartographicSize(ViewPort viewPort, double dpi, FShape shp) {
		IMarkerSymbol marker = getMarker();
		if (marker != null) {
			previousMarkerSize = marker.getSize();
			double size = CartographicSupportToolkit.getCartographicLength(
					this, previousMarkerSize, viewPort, dpi);
			marker.setSize(size);
		}
		double s = super.toCartographicSize(viewPort, dpi, shp);
		return s;

	}
}
