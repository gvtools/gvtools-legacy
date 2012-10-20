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

/* CVS MESSAGES:
 *
 * $Id: DotDensityFillSymbol.java 13953 2007-09-21 12:26:04Z jaume $
 * $Log$
 * Revision 1.8  2007-09-21 12:25:32  jaume
 * cancellation support extended down to the IGeometry and ISymbol level
 *
 * Revision 1.7  2007/09/18 14:50:31  caballero
 * Leyendas sobre el Layout
 *
 * Revision 1.6  2007/03/26 14:24:24  jaume
 * IPrintable refactored
 *
 * Revision 1.5  2007/03/09 11:20:56  jaume
 * Advanced symbology (start committing)
 *
 * Revision 1.3.2.4  2007/02/21 16:09:02  jaume
 * *** empty log message ***
 *
 * Revision 1.3.2.3  2007/02/16 10:54:12  jaume
 * multilayer splitted to multilayerline, multilayermarker,and  multilayerfill
 *
 * Revision 1.3.2.2  2007/02/15 16:23:44  jaume
 * *** empty log message ***
 *
 * Revision 1.3.2.1  2007/02/09 07:47:04  jaume
 * Isymbol moved
 *
 * Revision 1.3  2007/01/12 10:08:26  jaume
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/10 16:39:41  jaume
 * ISymbol now belongs to com.iver.cit.gvsig.fmap.core.symbols package
 *
 * Revision 1.1  2007/01/10 16:31:36  jaume
 * *** empty log message ***
 *
 * Revision 1.4  2006/11/14 11:10:27  jaume
 * *** empty log message ***
 *
 * Revision 1.3  2006/11/13 09:15:23  jaume
 * javadoc and some clean-up
 *
 * Revision 1.2  2006/11/09 18:39:05  jaume
 * *** empty log message ***
 *
 * Revision 1.1  2006/11/09 10:22:50  jaume
 * *** empty log message ***
 *
 *
 */
package org.gvsig.symbology.fmap.symbols;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.Random;

import javax.print.attribute.PrintRequestAttributeSet;

import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.symbols.AbstractFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SymbolDrawingException;
import com.iver.utiles.StringUtilities;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.swing.threads.Cancellable;

/**
 * <p>
 * Symbol that draws a set of points within a polygon. The amount of points is
 * defined by the field dotCount.<br>
 * </p>
 * <p>
 * This symbol only draws the points. The outline and the fill of the polygon is
 * handled by a SimpleFillSymboll where a DotDensityFillSymbol should be
 * embedded.<br>
 * </p>
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 * 
 */
public class DotDensityFillSymbol extends AbstractFillSymbol {
	private int dotCount;
	private double dotSize;
	private double dotSpacing;
	private Color dotColor;
	private boolean fixedPlacement;
	private PrintRequestAttributeSet properties;

	public DotDensityFillSymbol() {
		super();
	}

	public ISymbol getSymbolForSelection() {
		return this; // the selection color is applied in the SimpleFillSymbol
	}

	public void draw(Graphics2D g, AffineTransform affineTransform, FShape shp,
			Cancellable cancel) {
		int maxIntentos = 35;
		int width = shp.getBounds().width;
		int height = shp.getBounds().height;
		int minx = shp.getBounds().x;
		int miny = shp.getBounds().y;
		Random random = new Random();
		g.setClip(shp);
		g.setColor(getDotColor());
		g.setBackground(null);
		int size = (int) dotSize;
		for (int i = 0; (cancel == null || !cancel.isCanceled())
				&& i < dotCount; i++) {
			int x, y;
			int intentos = 0;
			/*
			 * Introducimos este bucle para procurar que los puntos queden
			 * dentro del shape. Le ponemos además un numero máximo de intentos
			 * para evitar las posibilidad de un bucle infinito o excesivamente
			 * reiterativo.
			 */
			do {
				x = (int) Math.abs(random.nextDouble() * width);
				y = (int) Math.abs(random.nextDouble() * height);
				x = x + minx;
				y = y + miny;
				intentos++;
			} while (intentos < maxIntentos && !shp.contains(x, y));
			g.fillRect(x, y, size, size);
		}
		g.setClip(null);
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("className", getClassName());

		// color
		Color c = getDotColor();
		if (c != null)
			xml.putProperty("color",
					StringUtilities.color2String(getDotColor()));

		// description
		xml.putProperty("desc", getDescription());

		// is shape visible
		xml.putProperty("isShapeVisible", isShapeVisible());

		// dot count
		xml.putProperty("dotCount", dotCount);

		// dot size
		xml.putProperty("dotSize", dotSize);

		// dot spacing
		xml.putProperty("dotSpacing", dotSpacing);

		xml.putProperty("referenceSystem", getReferenceSystem());
		xml.putProperty("unit", getUnit());

		return xml;
	}

	public int getSymbolType() {
		return FShape.POLYGON;
	}

	public void drawInsideRectangle(Graphics2D g,
			AffineTransform scaleInstance, Rectangle r,
			PrintRequestAttributeSet properties) throws SymbolDrawingException {
		int x = r.x;
		int y = r.y;
		int width = r.width;
		int height = r.height;
		int size = height / 5;
		g.setColor(getDotColor());
		g.setBackground(null);
		g.fillRect((int) (x + width * 0.2), (int) (y + height * 0.2), size,
				size);
		g.fillRect((int) (x + width * 0.25), (int) (y + height * 0.7), size,
				size);
		g.fillRect((int) (x + width * 0.35), (int) (y + height * 0.5), size,
				size);
		g.fillRect((int) (x + width * 0.6), (int) (y + height * 0.1), size,
				size);
		g.fillRect((int) (x + width * 0.7), (int) (y + height * 0.8), size,
				size);
		g.fillRect((int) (x + width * 0.8), (int) (y + height * 0.3), size,
				size);
		g.fillRect((int) (x + width * 0.9), (int) (y + height * 0.6), size,
				size);
	}

	public String getClassName() {
		return getClass().getName();
	}

	public void setXMLEntity(XMLEntity xml) {
		// color
		if (xml.contains("color"))
			setDotColor(StringUtilities.string2Color(xml
					.getStringProperty("color")));

		// description
		setDescription(xml.getStringProperty("desc"));

		// is shape visible
		setIsShapeVisible(xml.getBooleanProperty("isShapeVisible"));

		// dot count
		dotCount = xml.getIntProperty("dotCount");

		// dot size
		dotSize = xml.getDoubleProperty("dotSize");

		// dot spacing
		dotSpacing = xml.getDoubleProperty("dotSpacing");
	}

	/**
	 * @return
	 * @uml.property name="dotCount"
	 */
	public int getDotCount() {
		return dotCount;
	}

	/**
	 * @param dotCount
	 * @uml.property name="dotCount"
	 */
	public void setDotCount(int dotCount) {
		this.dotCount = dotCount;
	}

	/**
	 * @return
	 * @uml.property name="dotSize"
	 */
	public double getDotSize() {
		return dotSize;
	}

	/**
	 * @param dotSize
	 * @uml.property name="dotSize"
	 */
	public void setDotSize(double dotSize) {
		this.dotSize = dotSize;
	}

	/**
	 * @return
	 * @uml.property name="dotSpacing"
	 */
	public double getDotSpacing() {
		return dotSpacing;
	}

	/**
	 * @param dotSpacing
	 * @uml.property name="dotSpacing"
	 */
	public void setDotSpacing(double dotSpacing) {
		this.dotSpacing = dotSpacing;
	}

	public Color getDotColor() {
		return dotColor;
	}

	public void setDotColor(Color dotColor) {
		this.dotColor = dotColor;
	}

	public void print(Graphics2D g, AffineTransform at, FShape shape,
			PrintRequestAttributeSet properties) {
		this.properties = properties;
		draw(g, at, shape, null);
		this.properties = null;

	}

}
