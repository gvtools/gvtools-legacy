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
package org.gvsig.symbology.fmap.symbols;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.print.attribute.PrintRequestAttributeSet;

import org.apache.batik.ext.awt.geom.PathLength;
import org.apache.log4j.Logger;
import org.gvsig.symbology.fmap.styles.BackgroundFileStyle;
import org.gvsig.tools.file.PathGenerator;

import com.iver.cit.gvsig.fmap.Messages;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.CartographicSupportToolkit;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.AbstractLineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SymbolDrawingException;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.swing.threads.Cancellable;

/**
 * PictureLineSymbol allows to use any symbol defined as an image (by an image file)
 * supported  by gvSIG.This symbol will be used as an initial object.The line will be
 * painted as a succession of puntual symbols through the path defined by it(the line).
 *
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class PictureLineSymbol extends AbstractLineSymbol  {
//	transient private BufferedImage img;
//	transient private BufferedImage selImg;
	transient private PictureLineSymbol selectionSym;
	private String selImagePath;
	private double width;
	private String imagePath;
	private boolean selected;
	private double xScale = 1, csXScale = xScale;
	private double yScale = 1, csYScale = yScale;

	private BackgroundFileStyle bgImage;
	private BackgroundFileStyle bgSelImage;
	private PrintRequestAttributeSet properties;
	private PathGenerator pathGenerator=PathGenerator.getInstance();

	/**
	 * Constructor method
	 *
	 */
	public PictureLineSymbol() {
		super();
	}
	/**
	 * Constructor method
	 * @param imageURL, URL of the normal image
	 * @param selImageURL, URL of the image when it is selected in the map
	 * @throws IOException
	 */

	public PictureLineSymbol(URL imageURL, URL selImageURL) throws IOException {
		setImage(imageURL);
		if (selImageURL!=null)
			setSelImage(selImageURL);
		else setSelImage(imageURL);
	}
	/**
	 * Sets the URL for the image to be used as a picture line symbol
	 * @param imageFile, File
	 * @throws IOException
	 */
	public void setImage(URL imageUrl) throws IOException{

		bgImage= BackgroundFileStyle.createStyleByURL(imageUrl);
		imagePath = imageUrl.toString();
	}
	/**
	 * Sets the URL for the image to be used as a picture line symbol (when it is selected in the map)
	 * @param imageFile, File
	 * @throws IOException
	 */
	public void setSelImage(URL selImageUrl) throws IOException{

		bgSelImage= BackgroundFileStyle.createStyleByURL(selImageUrl);
		selImagePath = selImageUrl.toString();
	}


	public void setLineWidth(double width) {
		this.width = width;
		getLineStyle().setLineWidth((float) width);
	}

	public double getLineWidth() {
		return width;
	}

	public ISymbol getSymbolForSelection() {
		if (selectionSym == null) {
			selectionSym = (PictureLineSymbol) SymbologyFactory.createSymbolFromXML(getXMLEntity(), getDescription());
			selectionSym.selected=true;
			selectionSym.selectionSym = selectionSym; // avoid too much lazy creations

		}
		return selectionSym;

	}

	public void draw(Graphics2D g, AffineTransform affineTransform, FShape shp, Cancellable cancel) {
		float csWidth = getLineStyle().getLineWidth();
		g.setClip(new BasicStroke((float) csWidth, BasicStroke.CAP_ROUND,BasicStroke.CAP_ROUND).createStrokedShape(shp));
		BackgroundFileStyle bg = (!selected) ? bgImage : bgSelImage ;

		if (csXScale<=0 &&  csYScale<=0)
			return;

		Rectangle bounds = bg.getBounds();
		final double imageWidth  = bounds.getWidth()  * csXScale;
		final double imageHeight = bounds.getHeight() * csYScale;

		if (imageWidth==0 || imageHeight==0) return;
		int height = (int) csWidth;

		PathLength pl = new PathLength(shp);
		PathIterator iterator = ((FPolyline2D) shp).getPathIterator(null, 0.8);
		double[] theData = new double[6];
		Point2D firstPoint = null, startPoint = null, endPoint = null;
		if (!iterator.isDone()) {
			if ( iterator.currentSegment(theData) != PathIterator.SEG_CLOSE) {
				firstPoint = new Point2D.Double(theData[0], theData[1]);
			}
		}
		float currentPathLength = 1;

		Rectangle rect = new Rectangle();

		while ((cancel==null || !cancel.isCanceled()) && !iterator.isDone()) {

			int theType = iterator.currentSegment(theData);
			switch (theType) {
			case PathIterator.SEG_MOVETO:
				startPoint = new Point2D.Double(theData[0], theData[1]);

				endPoint = null;
				iterator.next();

				continue;

			case PathIterator.SEG_LINETO:
			case PathIterator.SEG_QUADTO:
			case PathIterator.SEG_CUBICTO:
				endPoint = new Point2D.Double(theData[0], theData[1]);

				break;
			case PathIterator.SEG_CLOSE:
				endPoint = startPoint;
				startPoint = firstPoint;
				break;
			}

			double a = endPoint.getX() - startPoint.getX();
			double b = endPoint.getY() - startPoint.getY();
			double theta = pl.angleAtLength(currentPathLength);

			double x = startPoint.getX();
			double y = startPoint.getY();

			// Theorem of Pythagoras
			float segmentLength = (float) Math.sqrt(a*a + b*b);

			// compute how many times the image has to be drawn
			// to completely cover this segment's length
			int count = (int) Math.ceil(segmentLength/imageWidth);

			for (int i = 0; (cancel==null || !cancel.isCanceled()) && i < count; i++) {
				g.translate(x, y);
				g.rotate(theta);

				double xOffsetTranslation = imageWidth*i;


				g.translate(xOffsetTranslation, -csWidth);

				rect.setBounds(0, (int) Math.round(height*.5), (int) Math.ceil(imageWidth), height);
				try {
					bg.drawInsideRectangle(g, rect, false);
				} catch (SymbolDrawingException e) {
					Logger.getLogger(getClass()).warn(Messages.getString("label_style_could_not_be_painted"), e);
				}
				g.setColor(Color.red);
				g.translate(-xOffsetTranslation, csWidth);

				g.rotate(-theta);
				g.translate(-x, -y);
			}

			startPoint = endPoint;
			currentPathLength += segmentLength;
			iterator.next();
		}

		g.setClip(null);
		g.setColor(Color.red);

	}


	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("className", getClassName());
		xml.putProperty("isShapeVisible", isShapeVisible());
		xml.putProperty("desc", getDescription());
		xml.putProperty("imagePath", pathGenerator.getURLPath(imagePath));
		xml.putProperty("selImagePath", pathGenerator.getURLPath(selImagePath));
		xml.putProperty("lineWidth", getLineWidth());
		xml.putProperty("xScale", getXScale());
		xml.putProperty("yScale", getYScale());

		// measure unit
		xml.putProperty("unit", getUnit());

		// reference system
		xml.putProperty("referenceSystem", getReferenceSystem());

		return xml;

	}

	public void setXMLEntity(XMLEntity xml) {
		System.out.println(this.getClass().getClassLoader());
		setDescription(xml.getStringProperty("desc"));
		setIsShapeVisible(xml.getBooleanProperty("isShapeVisible"));
		imagePath = pathGenerator.getAbsoluteURLPath(xml.getStringProperty("imagePath"));
		selImagePath = pathGenerator.getAbsoluteURLPath(xml.getStringProperty("selImagePath"));
		setLineWidth(xml.getDoubleProperty("lineWidth"));
		setXScale(xml.getDoubleProperty("xScale"));
		setYScale(xml.getDoubleProperty("yScale"));
		setReferenceSystem(xml.getIntProperty("referenceSystem"));
		setUnit(xml.getIntProperty("unit"));

		File rootDir = new File(SymbologyFactory.SymbolLibraryPath);
		try {
			try{
				setImage(new URL(imagePath));
			} catch (MalformedURLException e) {
				try{
					setImage(new URL(pathGenerator.getAbsoluteURLPath(imagePath)));
				} catch (MalformedURLException e1) {
					setImage(new URL("file://"+ rootDir.getAbsolutePath() + File.separator +imagePath));
				}
			}
			try{
				setSelImage(new URL(selImagePath));
			} catch (MalformedURLException e) {
				try{
					setSelImage(new URL(pathGenerator.getAbsoluteURLPath(selImagePath)));
				} catch (MalformedURLException e1) {
					setSelImage(new URL("file://"+ rootDir.getAbsolutePath() + File.separator +selImagePath));
				}
			}
		} catch (MalformedURLException e) {
			Logger.getLogger(getClass()).error(Messages.getString("invalid_url"));
		} catch (IOException e) {
			Logger.getLogger(getClass()).error(Messages.getString("invalid_url"));

		}

	}
	/**
	 * Sets the yscale for the picture line symbol
	 * @param yScale
	 */
	public void setYScale(double yScale) {
		this.yScale = yScale;
		this.csYScale = yScale;
	}
	/**
	 * Sets the xscale for the picture line symbol
	 * @param xScale
	 */
	public void setXScale(double xScale) {
		this.xScale = xScale;
		this.csXScale = xScale;
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
	/**
	 * Returns the path of the image that is used as a picture line symbol (when it
	 * is selected in the map)
	 * @return selimagePath,String
	 */
	public String getSelImagePath() {
		return selImagePath;
	}
	/**
	 * Returns the path of the image that is used as a picture line symbol
	 * @return imagePath,String
	 */
	public String getImagePath() {
		return imagePath;
	}
	/**
	 * Returns the xscale for the picture line symbol
	 * @param xScale
	 */
	public double getXScale() {
		return xScale;
	}
	/**
	 * Returns the yscale for the picture line symbol
	 * @param yScale
	 */
	public double getYScale() {
		return yScale;
	}

	@Override
	public void setCartographicSize(double cartographicSize, FShape shp) {
		getLineStyle().setLineWidth((float) cartographicSize);
		double scale = cartographicSize/width;
		csXScale = xScale * scale;
		csYScale = yScale * scale;
	}

	@Override
	public double toCartographicSize(ViewPort viewPort, double dpi, FShape shp) {
		double s = super.toCartographicSize(viewPort, dpi, shp);
		setCartographicSize(CartographicSupportToolkit.
				getCartographicLength(this, width, viewPort, dpi), shp);
		return s;
	}
}
