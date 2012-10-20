/* gvSIG. Sistema de Información Geogràfica de la Generalitat Valenciana
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

/* CVS MESSAGES:
 *
 * $Id: CharacterMarkerSymbol.java 14501 2007-10-08 15:47:31Z jdominguez $
 * $Log$
 * Revision 1.27  2007-09-21 12:25:32  jaume
 * cancellation support extended down to the IGeometry and ISymbol level
 *
 * Revision 1.26  2007/08/09 07:20:03  jvidal
 * javadoc
 *
 * Revision 1.25  2007/07/23 06:52:25  jaume
 * default selection color refactored, moved to MapContext
 *
 * Revision 1.24  2007/07/18 06:54:34  jaume
 * continuing with cartographic support
 *
 * Revision 1.23  2007/07/03 10:58:29  jaume
 * first refactor on CartographicSupport
 *
 * Revision 1.22  2007/06/29 13:07:01  jaume
 * +PictureLineSymbol
 *
 * Revision 1.21  2007/06/07 06:50:40  jaume
 * *** empty log message ***
 *
 * Revision 1.20  2007/05/29 15:46:37  jaume
 * *** empty log message ***
 *
 * Revision 1.19  2007/05/28 15:36:42  jaume
 * *** empty log message ***
 *
 * Revision 1.18  2007/05/17 09:32:06  jaume
 * *** empty log message ***
 *
 * Revision 1.17  2007/05/09 16:07:26  jaume
 * *** empty log message ***
 *
 * Revision 1.16  2007/05/09 11:05:28  jaume
 * *** empty log message ***
 *
 * Revision 1.15  2007/05/08 08:47:40  jaume
 * *** empty log message ***
 *
 * Revision 1.14  2007/04/26 11:41:00  jaume
 * attempting to let defining size in world units
 *
 * Revision 1.13  2007/04/20 07:11:11  jaume
 * *** empty log message ***
 *
 * Revision 1.12  2007/04/19 16:01:27  jaume
 * *** empty log message ***
 *
 * Revision 1.11  2007/04/19 14:21:30  jaume
 * *** empty log message ***
 *
 * Revision 1.10  2007/03/26 14:24:13  jaume
 * implemented Print
 *
 * Revision 1.9  2007/03/21 11:37:00  jaume
 * *** empty log message ***
 *
 * Revision 1.8  2007/03/21 11:02:17  jaume
 * *** empty log message ***
 *
 * Revision 1.7  2007/03/09 11:20:56  jaume
 * Advanced symbology (start committing)
 *
 * Revision 1.5.2.8  2007/02/21 07:34:09  jaume
 * labeling starts working
 *
 * Revision 1.5.2.7  2007/02/16 10:54:12  jaume
 * multilayer splitted to multilayerline, multilayermarker,and  multilayerfill
 *
 * Revision 1.5.2.6  2007/02/15 16:23:44  jaume
 * *** empty log message ***
 *
 * Revision 1.5.2.5  2007/02/14 09:58:37  jaume
 * *** empty log message ***
 *
 * Revision 1.5.2.4  2007/02/12 15:15:20  jaume
 * refactored interval legend and added graduated symbol legend
 *
 * Revision 1.5.2.3  2007/02/09 07:47:04  jaume
 * Isymbol moved
 *
 * Revision 1.5.2.2  2007/02/05 14:59:04  jaume
 * *** empty log message ***
 *
 * Revision 1.5.2.1  2007/01/30 18:10:45  jaume
 * start commiting labeling stuff
 *
 * Revision 1.5  2007/01/25 16:25:23  jaume
 * *** empty log message ***
 *
 * Revision 1.4  2007/01/24 17:58:22  jaume
 * new features and architecture error fixes
 *
 * Revision 1.3  2007/01/16 11:50:44  jaume
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/10 16:39:41  jaume
 * ISymbol now belongs to com.iver.cit.gvsig.fmap.core.symbols package
 *
 * Revision 1.1  2007/01/10 16:31:36  jaume
 * *** empty log message ***
 *
 * Revision 1.6  2006/12/04 17:13:39  fjp
 * *** empty log message ***
 *
 * Revision 1.5  2006/11/14 11:10:27  jaume
 * *** empty log message ***
 *
 * Revision 1.4  2006/11/09 18:39:05  jaume
 * *** empty log message ***
 *
 * Revision 1.3  2006/11/08 10:56:47  jaume
 * *** empty log message ***
 *
 * Revision 1.2  2006/11/06 17:08:45  jaume
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/31 16:16:34  jaume
 * *** empty log message ***
 *
 * Revision 1.4  2006/10/30 19:30:35  jaume
 * *** empty log message ***
 *
 * Revision 1.3  2006/10/29 23:53:49  jaume
 * *** empty log message ***
 *
 * Revision 1.2  2006/10/26 16:27:33  jaume
 * support for composite marker symbols (not tested)
 *
 * Revision 1.1  2006/10/25 10:50:41  jaume
 * movement of classes and gui stuff
 *
 * Revision 1.3  2006/10/24 19:54:16  jaume
 * added IPersistence
 *
 * Revision 1.2  2006/10/24 08:02:51  jaume
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/18 07:54:06  jaume
 * *** empty log message ***
 *
 *
 */
package org.gvsig.symbology.fmap.symbols;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.styles.IMask;
import com.iver.cit.gvsig.fmap.core.symbols.AbstractMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.utiles.StringUtilities;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.swing.threads.Cancellable;

/**
 * Allows to use a source of TrueType characters to define the marker that will
 * substitute the symbol.If the picture is defined in a source, the performance
 * is more agile.
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class CharacterMarkerSymbol extends AbstractMarkerSymbol {
	private Font font = new Font("Arial", Font.PLAIN, 20);
	private int unicode;
	private ISymbol selectionSymbol;
	private VisualCorrection visualCorrection;
	private double size;

	/**
	 * Creates a new instance of CharacterMarker with default values
	 * 
	 */
	public CharacterMarkerSymbol() {
		super();
	}

	/**
	 * Creates a new instance of CharacterMarker specifying the marker source
	 * font, the character code corresponding to the symbol, and the color that
	 * will be used in rendering time.
	 * 
	 * @param font
	 *            - src Font
	 * @param charCode
	 *            - character code of the symbol for this font
	 * @param color
	 *            - color to be used in when rendering.
	 */
	public CharacterMarkerSymbol(Font font, int charCode, Color color) {
		super();
		this.font = font;
		unicode = charCode;
		setColor(color);
	}

	/**
	 * Returns the font that will be used to define the symbol
	 * 
	 * @return font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Sets the font that will be used to define the symbol
	 * 
	 * @return font
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	public ISymbol getSymbolForSelection() {
		if (selectionSymbol == null) {
			XMLEntity xml = getXMLEntity();
			xml.putProperty("color", StringUtilities.color2String(MapContext
					.getSelectionColor()));
			selectionSymbol = SymbologyFactory.createSymbolFromXML(xml,
					getDescription() + " version for selection.");
		}
		return selectionSymbol;
	}

	public void draw(Graphics2D g, AffineTransform affineTransform, FShape shp,
			Cancellable cancel) {
		g.setColor(getColor());
		double theta = getRotation();

		int xOffset = (int) getOffset().getX(); // * multiplicador d'unitats;
		int yOffset = (int) getOffset().getY(); // * multiplicador d'unitats

		double size = getSize();
		if (size < 0.0001) {
			return;
		}
		Point2D p = new Point2D.Double(((FPoint2D) shp).getX(),
				((FPoint2D) shp).getY());

		if (isVisuallyCorrected()) {
			size *= visualCorrection.sizeScale;
			p.setLocation(p.getX() - xOffset * size
					* visualCorrection.xOffsetScale, p.getY() - yOffset * size
					* visualCorrection.yOffsetScale);

		}
		g.setFont(getFont().deriveFont((float) size));

		g.translate((int) (p.getX() + xOffset), (int) (p.getY() + yOffset));
		if (theta != 0)
			g.rotate(theta);

		char[] text = new char[] { (char) unicode };

		IMask mask = getMask();
		if (mask != null) {
			FontRenderContext frc = g.getFontRenderContext();

			GlyphVector gv = font.createGlyphVector(frc, text);

			Shape markerShape = gv.getOutline(0, 0);
			mask.getFillSymbol().draw(g, null, mask.getHaloShape(markerShape),
					cancel);

		}
		g.drawChars(text, 0, text.length, -(int) (size * 0.4),
				(int) (size * 0.4));

		if (theta != 0)
			g.rotate(-theta);
		g.translate(-(int) (p.getX() + xOffset), -(int) (p.getY() + yOffset));
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();

		// the class name
		xml.putProperty("className", getClassName());

		// color
		xml.putProperty("color", StringUtilities.color2String(getColor()));

		// font
		xml.putProperty("font", font.getFontName());

		// font style
		xml.putProperty("fontStyle", font.getStyle());

		// marker size
		xml.putProperty("size", size);

		// symbol code
		xml.putProperty("symbolCode", unicode);

		// description
		xml.putProperty("desc", getDescription());

		// is shape visible
		xml.putProperty("isShapeVisible", isShapeVisible());

		// x offset
		xml.putProperty("xOffset", getOffset().getX());

		// y offset
		xml.putProperty("yOffset", getOffset().getY());

		// rotation
		xml.putProperty("rotation", getRotation());

		// measure unit
		xml.putProperty("unit", getUnit());

		// reference system
		xml.putProperty("referenceSystem", getReferenceSystem());

		return xml;
	}

	/**
	 * Sets the unicode for a symbol represented by a character
	 * 
	 * @param symbol
	 *            , int
	 */
	public void setUnicode(int symbol) {
		this.unicode = symbol;
	}

	/**
	 * Obtains the unicode for a symbol
	 * 
	 * @return unicode, int
	 */
	public int getUnicode() {
		return unicode;
	}

	public String getClassName() {
		return this.getClass().getName();
	}

	public void setXMLEntity(XMLEntity xml) {
		setColor(StringUtilities.string2Color(xml.getStringProperty("color")));
		Point p = new Point();
		p.setLocation(xml.getDoubleProperty("xOffset"),
				xml.getDoubleProperty("yOffset"));

		setDescription(xml.getStringProperty("desc"));
		size = xml.getDoubleProperty("size");
		font = new Font(xml.getStringProperty("font"),
				xml.getIntProperty("fontStyle"), (int) size);
		setIsShapeVisible(xml.getBooleanProperty("isShapeVisible"));
		unicode = xml.getIntProperty("symbolCode");
		setOffset(p);
		setRotation(xml.getDoubleProperty("rotation"));
		setReferenceSystem(xml.getIntProperty("referenceSystem"));
		setUnit(xml.getIntProperty("unit"));
	}

	public void print(Graphics2D g, AffineTransform at, FShape shape)
			throws ReadDriverException {
		// TODO Implement it
		throw new Error("Not yet implemented!");

	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
		font = new Font(font.getName(), font.getStyle(), (int) Math.round(size));
	}

	/**
	 * Returns true of false depending if the character marker symbol selected
	 * is visually corrected.That is, if the character has been properly scaled
	 * to use a specific number of pixels to be represented.
	 * 
	 * @return boolean
	 */
	public boolean isVisuallyCorrected() {
		return visualCorrection != null;
	}

	/**
	 * Sets the visual correction for a character in order to scale it if it is
	 * necessary.
	 * 
	 * @return boolean
	 */
	public void setVisuallyCorrected(boolean visuallyCorrected) {
		if (visuallyCorrected && visualCorrection == null) {
			CharacterMarkerSymbol clone = (CharacterMarkerSymbol) SymbologyFactory
					.createSymbolFromXML(getXMLEntity(), getDescription());
			int frameSize = 200;
			double symbolSize = 100;
			BufferedImage bi = new BufferedImage(frameSize, frameSize,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D aGraphics = bi.createGraphics();
			clone.setOffset(new Point2D.Double(0, 0));
			clone.setSize(symbolSize);
			clone.setColor(Color.PINK);

			// draw it at center of the image
			FPoint2D pCenter = new FPoint2D(frameSize / 2, frameSize / 2);
			clone.draw(aGraphics, new AffineTransform(), pCenter, null);

			int realTop = -1;
			int realBottom = -1;
			int realLeft = -1;
			int realRight = -1;

			// let's see where the highest pixel is in Y-axis
			boolean done = false;
			for (int j = 0; !done && j < bi.getHeight(); j++) {
				for (int i = 0; !done && i < bi.getWidth(); i++) {
					if (bi.getRGB(i, j) != 0) {
						realTop = j;
						done = true;
					}
				}
			}

			// let's see where the lowest pixel is in Y-axis
			done = false;
			for (int j = bi.getHeight() - 1; !done && j >= 0; j--) {
				for (int i = 0; !done && i < bi.getWidth(); i++) {
					if (bi.getRGB(i, j) != 0) {
						realBottom = i;
						done = true;
					}
				}
			}

			// let's see where the first pixel at left is in X-axis
			done = false;
			for (int i = 0; !done && i < bi.getWidth(); i++) {
				for (int j = 0; !done && j < bi.getHeight(); j++) {
					if (bi.getRGB(i, j) != 0) {
						realLeft = i;
						done = true;
					}
				}
			}

			// let's see where the first pixel at right is in X-axis
			done = false;
			for (int i = bi.getWidth() - 1; !done && i >= 0; i--) {
				for (int j = 0; !done && j < bi.getHeight(); j++) {
					if (bi.getRGB(i, j) != 0) {
						realRight = i;
						done = true;
					}
				}
			}

			int realWidth = bi.getWidth() - realRight - realLeft;
			int realHeight = bi.getHeight() - realBottom - realTop;

			visualCorrection = new VisualCorrection();

			if (realBottom != -1 && realTop != -1 && realLeft != -1
					&& realRight != -1) {
				double correctingSize = Math.max(realHeight, realWidth);
				visualCorrection.sizeScale = clone.getSize() / correctingSize;
			}

			if (realLeft != -1 && realRight != -1) {
				double correctingCenterX = (((realWidth) * 0.5) + realLeft);
				double correctingCenterY = (((realHeight) * 0.5) + realBottom);
				visualCorrection.xOffsetScale = (pCenter.getX() - correctingCenterX)
						/ frameSize;
				visualCorrection.yOffsetScale = (pCenter.getY() - correctingCenterY)
						/ frameSize;
			}
		} else {
			visualCorrection = null;
		}
	}

	/**
	 * Class to be used for the methods that control the visual correction of a
	 * character. This visual correction has the responsibility of modify the
	 * dimensions of the character to be used as a symbol(in case that the user
	 * wants to use more or less pixels to represent it )
	 * 
	 */
	private class VisualCorrection {
		double xOffsetScale = 1;
		double yOffsetScale = 1;
		double sizeScale = 1;
	}
}
