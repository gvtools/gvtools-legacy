/* gvSIG. Sistema de Informaci?n Geogr?fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib??ez, 50
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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.print.attribute.PrintRequestAttributeSet;

import org.apache.log4j.Logger;
import org.gvsig.symbology.fmap.styles.BackgroundFileStyle;
import org.gvsig.symbology.fmap.styles.SimpleMarkerFillPropertiesStyle;

import com.iver.cit.gvsig.fmap.Messages;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FPolygon2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.styles.IMarkerFillPropertiesStyle;
import com.iver.cit.gvsig.fmap.core.symbols.AbstractFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SymbolDrawingException;
import com.iver.utiles.StringUtilities;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.swing.threads.Cancellable;

/**
 * PictureFillSymbol allows to use an image file suported by gvSIG as a padding
 * for the polygons.This image can be modified using methods to scale or rotate
 * it.
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class PictureFillSymbol extends AbstractFillSymbol {
	private double angle = 0;
	private double xScale = 1;
	private double yScale = 1;
	transient private PictureFillSymbol selectionSym;
	private boolean selected;
	private IMarkerFillPropertiesStyle markerFillProperties = new SimpleMarkerFillPropertiesStyle();
	private String imagePath;
	private String selImagePath;
	private BackgroundFileStyle bgImage;
	private BackgroundFileStyle bgSelImage;
	private PrintRequestAttributeSet properties;

	public void draw(Graphics2D g, AffineTransform affineTransform, FShape shp,
			Cancellable cancel) {
		Color fillColor = getFillColor();
		if (fillColor != null) {
			g.setColor(fillColor);
			g.fill(shp);
		}

		g.setClip(shp);
		BackgroundFileStyle bg = (!selected) ? bgImage : bgSelImage;
		if (bg != null && bg.getBounds() != null) {
			int sizeW = (int) ((int) bg.getBounds().getWidth() * xScale);
			int sizeH = (int) ((int) bg.getBounds().getHeight() * yScale);
			Rectangle rProv = new Rectangle();
			rProv.setFrame(0, 0, sizeW, sizeH);
			Paint resulPatternFill = null;
			BufferedImage sample = null;

			sample = new BufferedImage(sizeW, sizeH,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D gAux = sample.createGraphics();

			double xSeparation = markerFillProperties.getXSeparation(); // TODO
																		// apply
																		// CartographicSupport
			double ySeparation = markerFillProperties.getYSeparation(); // TODO
																		// apply
																		// CartographicSupport
			double xOffset = markerFillProperties.getXOffset();
			double yOffset = markerFillProperties.getYOffset();

			try {
				bg.drawInsideRectangle(gAux, rProv);
			} catch (SymbolDrawingException e) {
				Logger.getLogger(getClass()).warn(
						Messages.getString("label_style_could_not_be_painted"),
						e);
			}

			Dimension sz = rProv.getSize();
			sz = new Dimension((int) Math.round(sz.getWidth() + (xSeparation)),
					(int) Math.round(sz.getHeight() + (ySeparation)));
			rProv.setSize(sz);

			BufferedImage bi = new BufferedImage(rProv.width, rProv.height,
					BufferedImage.TYPE_INT_ARGB);
			gAux = bi.createGraphics();
			gAux.drawImage(sample, null, (int) (xSeparation * 0.5),
					(int) (ySeparation * 0.5));

			rProv.x = rProv.x + (int) xOffset;
			rProv.y = rProv.y + (int) yOffset;
			resulPatternFill = new TexturePaint(bi, rProv);

			g.setColor(null);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			g.setPaint(resulPatternFill);
		}
		g.rotate(-angle);
		AffineTransform at = new AffineTransform();
		at.rotate(angle);
		shp.transform(at);
		g.fill(shp);
		at.rotate(-angle * 2);
		shp.transform(at);
		g.rotate(angle);
		g.setClip(null);
		if (getOutline() != null) {
			getOutline().draw(g, affineTransform, shp, cancel);
		}
	}

	/**
	 * Constructor method
	 * 
	 */
	public PictureFillSymbol() {
		super();
	}

	/**
	 * Constructor method
	 * 
	 * @param imageURL
	 *            , URL of the normal image
	 * @param selImageURL
	 *            , URL of the image when it is selected in the map
	 * @throws IOException
	 */

	public PictureFillSymbol(URL imageURL, URL selImageURL) throws IOException {
		setImage(imageURL);
		if (selImageURL != null)
			setSelImage(selImageURL);
		else
			setSelImage(imageURL);
	}

	/**
	 * Sets the URL for the image to be used as a picture fill symbol (when it
	 * is selected in the map)
	 * 
	 * @param imageFile
	 *            , File
	 * @throws IOException
	 */
	public void setSelImage(URL selImageUrl) throws IOException {

		bgSelImage = BackgroundFileStyle.createStyleByURL(selImageUrl);
		selImagePath = selImageUrl.toString();
	}

	/**
	 * Defines the URL from where the picture to fill the polygon is taken.
	 * 
	 * @param imageFile
	 * @throws IOException
	 */
	public void setImage(URL imageUrl) throws IOException {

		bgImage = BackgroundFileStyle.createStyleByURL(imageUrl);
		imagePath = imageUrl.toString();
	}

	public void drawInsideRectangle(Graphics2D g,
			AffineTransform scaleInstance, Rectangle r,
			PrintRequestAttributeSet properties) throws SymbolDrawingException {
		if (properties == null)
			draw(g, null, new FPolygon2D(new GeneralPathX(r)), null);
		else
			print(g, new AffineTransform(),
					new FPolygon2D(new GeneralPathX(r)), properties);
	}

	public ISymbol getSymbolForSelection() {
		if (selectionSym == null) {
			selectionSym = (PictureFillSymbol) SymbologyFactory
					.createSymbolFromXML(getXMLEntity(), getDescription());
			selectionSym.selected = true;
			selectionSym.selectionSym = selectionSym; // avoid too much lazy
														// creations
		}
		return selectionSym;

	}

	public int getSymbolType() {
		return FShape.POLYGON;
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("desc", getDescription());
		xml.putProperty("className", getClassName());
		xml.putProperty("isShapeVisible", isShapeVisible());
		xml.putProperty("referenceSystem", getReferenceSystem());
		xml.putProperty("unit", getUnit());

		xml.putProperty("angle", angle);
		xml.putProperty("scaleX", xScale);
		xml.putProperty("scaleY", yScale);
		xml.putProperty("imagePath",
				PicturePathGeneratorUtils.getURLPath(imagePath));
		xml.putProperty("selImagePath",
				PicturePathGeneratorUtils.getURLPath(selImagePath));
		if (getFillColor() != null)
			xml.putProperty("fillColor",
					StringUtilities.color2String(getFillColor()));

		xml.putProperty("hasFill", hasFill());
		XMLEntity fillPropertiesXML = markerFillProperties.getXMLEntity();
		fillPropertiesXML.putProperty("id", "fillProperties");
		xml.addChild(fillPropertiesXML);
		if (getOutline() != null) {
			XMLEntity outlineXML = getOutline().getXMLEntity();
			outlineXML.putProperty("id", "outline symbol");
			xml.addChild(outlineXML);
		}
		xml.putProperty("hasOutline", hasOutline());
		return xml;
	}

	public void setXMLEntity(XMLEntity xml) {

		imagePath = PicturePathGeneratorUtils.getAbsoluteURLPath(xml
				.getStringProperty("imagePath"));
		selImagePath = PicturePathGeneratorUtils.getAbsoluteURLPath(xml
				.getStringProperty("selImagePath"));
		setDescription(xml.getStringProperty("desc"));
		setIsShapeVisible(xml.getBooleanProperty("isShapeVisible"));
		setAngle(xml.getDoubleProperty("angle"));
		setXScale(xml.getDoubleProperty("scaleX"));
		setYScale(xml.getDoubleProperty("scaleY"));
		if (xml.contains("fillColor"))
			setFillColor(StringUtilities.string2Color(xml
					.getStringProperty("fillColor")));
		markerFillProperties = (IMarkerFillPropertiesStyle) SymbologyFactory
				.createStyleFromXML(xml.firstChild("id", "fillProperties"),
						"fill properties");

		File rootDir = new File(SymbologyFactory.SymbolLibraryPath);
		try {
			try {
				setImage(new URL(imagePath));
			} catch (MalformedURLException e) {
				try {
					setImage(new URL(
							PicturePathGeneratorUtils
									.getAbsoluteURLPath(imagePath)));
				} catch (MalformedURLException e1) {
					setImage(new URL("file://" + rootDir.getAbsolutePath()
							+ File.separator + imagePath));
				}
			}
			try {
				setSelImage(new URL(selImagePath));
			} catch (MalformedURLException e) {
				try {
					setSelImage(new URL(
							PicturePathGeneratorUtils
									.getAbsoluteURLPath(selImagePath)));
				} catch (MalformedURLException e1) {
					setSelImage(new URL("file://" + rootDir.getAbsolutePath()
							+ File.separator + selImagePath));
				}
			}
		} catch (MalformedURLException e) {
			Logger.getLogger(getClass()).error(
					Messages.getString("invalid_url"));
		} catch (IOException e) {
			Logger.getLogger(getClass()).error(
					Messages.getString("invalid_url"));

		}

		if (xml.contains("hasFill"))
			setHasFill(xml.getBooleanProperty("hasFill"));

		XMLEntity outlineXML = xml.firstChild("id", "outline symbol");
		if (outlineXML != null) {
			setOutline((ILineSymbol) SymbologyFactory.createSymbolFromXML(
					outlineXML, "outline"));
		}

		if (xml.contains("unit")) { // remove this line when done
			// measure unit (for outline)
			setUnit(xml.getIntProperty("unit"));

			// reference system (for outline)
			setReferenceSystem(xml.getIntProperty("referenceSystem"));
		}

		if (xml.contains("hasOutline"))
			setHasOutline(xml.getBooleanProperty("hasOutline"));

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
	 * Returns the IMarkerFillProperties that allows this class to treat the
	 * picture as a marker in order to scale it, rotate it and so on.
	 * 
	 * @return markerFillProperties,IMarkerFillPropertiesStyle
	 */
	public IMarkerFillPropertiesStyle getMarkerFillProperties() {
		return markerFillProperties;
	}

	/**
	 * Sets the MarkerFillProperties in order to allow the user to modify the
	 * picture as a marker (it is possible to scale it, rotate it and so on)
	 * 
	 * @param prop
	 */
	public void setMarkerFillProperties(IMarkerFillPropertiesStyle prop) {
		this.markerFillProperties = prop;
	}

	/**
	 * Defines the angle for the rotation of the image when it is added to
	 * create the padding
	 * 
	 * @return angle
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * Sets the angle for the rotation of the image when it is added to create
	 * the padding
	 * 
	 * @param angle
	 */
	public void setAngle(double angle) {
		this.angle = angle;
	}

	/**
	 * Defines the scale for the x axis of the image when it is added to create
	 * the padding
	 * 
	 * @return xScale
	 */
	public double getXScale() {
		return xScale;
	}

	/**
	 * Returns the scale for the x axis of the image when it is added to create
	 * the padding
	 * 
	 * @param xScale
	 */
	public void setXScale(double xScale) {
		this.xScale = xScale;
	}

	/**
	 * Defines the scale for the y axis of the image when it is added to create
	 * the padding
	 * 
	 * @return yScale
	 */
	public double getYScale() {
		return yScale;
	}

	/**
	 * Returns the scale for the y axis of the image when it is added to create
	 * the padding
	 * 
	 * @param yScale
	 */
	public void setYScale(double yScale) {
		this.yScale = yScale;
	}

	/**
	 * Returns the path of the image that is used to create the padding to fill
	 * the polygon
	 * 
	 * @return imagePath
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * Returns the path of the image used when the polygon is selected
	 * 
	 * @return
	 */
	public String getSelImagePath() {
		return selImagePath;
	}

	@Override
	public double toCartographicSize(ViewPort viewPort, double dpi, FShape shp) {
		return super.toCartographicSize(viewPort, dpi, shp);
		// this symbol cannot apply any cartographic transfomation to its
		// filling
	}

}
