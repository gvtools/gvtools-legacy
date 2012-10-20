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
 * $Id: SimpleLabelStyle.java 13953 2007-09-21 12:26:04Z jaume $
 * $Log$
 * Revision 1.18  2007-09-21 12:22:36  jaume
 * cancellation support extended down to the IGeometry and ISymbol level
 *
 * Revision 1.17  2007/09/17 09:32:52  jaume
 * removed system.out....
 *
 * Revision 1.16  2007/08/21 09:30:42  jvidal
 * javadoc
 *
 * Revision 1.15  2007/05/08 08:47:39  jaume
 * *** empty log message ***
 *
 * Revision 1.14  2007/04/19 14:21:30  jaume
 * *** empty log message ***
 *
 * Revision 1.12  2007/04/18 15:35:11  jaume
 * *** empty log message ***
 *
 * Revision 1.11  2007/04/12 14:28:43  jaume
 * basic labeling support for lines
 *
 * Revision 1.10  2007/04/11 16:01:34  jaume
 * try to fit the text in the correct place
 *
 * Revision 1.9  2007/04/10 16:34:01  jaume
 * towards a styled labeling
 *
 * Revision 1.8  2007/04/05 16:07:14  jaume
 * Styled labeling stuff
 *
 * Revision 1.7  2007/04/04 15:42:03  jaume
 * *** empty log message ***
 *
 * Revision 1.6  2007/04/04 15:41:05  jaume
 * *** empty log message ***
 *
 * Revision 1.5  2007/04/02 16:34:56  jaume
 * Styled labeling (start commiting)
 *
 * Revision 1.4  2007/04/02 00:10:04  jaume
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/29 16:02:01  jaume
 * *** empty log message ***
 *
 * Revision 1.2  2007/03/09 11:20:56  jaume
 * Advanced symbology (start committing)
 *
 * Revision 1.1.2.2  2007/02/15 16:23:44  jaume
 * *** empty log message ***
 *
 * Revision 1.1.2.1  2007/02/09 07:47:04  jaume
 * Isymbol moved
 *
 *
 */
package org.gvsig.symbology.fmap.styles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.Messages;
import com.iver.cit.gvsig.fmap.core.FPolygon2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.styles.AbstractStyle;
import com.iver.cit.gvsig.fmap.core.styles.ILabelStyle;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleLineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SymbolDrawingException;
import com.iver.utiles.XMLEntity;

/**
 * Implements a style for the creation of simple labels.
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class SimpleLabelStyle extends AbstractStyle implements ILabelStyle {
	private Point2D markerPoint = new Point2D.Double();
	// conertir açò a Rectangle2D[] ja que pot arribar a gastar-se massivament
	// en el pintat
	private ArrayList<Rectangle2D> textFieldAreas = new ArrayList<Rectangle2D>();
	private BackgroundFileStyle background;
	private Dimension defaultSize = new Dimension(32, 32);
	private Dimension sz;

	public int getFieldCount() {
		return textFieldAreas.size();
	}

	public void setTextFields(String[] texts) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; texts != null && i < texts.length; i++) {
			sb.append(texts[i]);
			if (i < texts.length)
				sb.append(" ");
		}
	}

	public boolean isSuitableFor(ISymbol symbol) {
		return true;
	}

	public String getClassName() {
		return getClass().getName();
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("className", getClassName());
		xml.putProperty("desc", getDescription());
		xml.putProperty("markerPointX", markerPoint.getX());
		xml.putProperty("markerPointY", markerPoint.getY());

		int size = getFieldCount();
		String[] minx = new String[size];
		String[] miny = new String[size];
		String[] widths = new String[size];
		String[] heights = new String[size];

		Rectangle2D[] rects = getTextBounds();
		for (int i = 0; i < rects.length; i++) {
			minx[i] = String.valueOf(rects[i].getMinX());
			miny[i] = String.valueOf(rects[i].getMinY());
			widths[i] = String.valueOf(rects[i].getWidth());
			heights[i] = String.valueOf(rects[i].getHeight());
		}

		xml.putProperty("minXArray", minx);
		xml.putProperty("minYArray", miny);
		xml.putProperty("widthArray", widths);
		xml.putProperty("heightArray", heights);
		if (getBackgroundFileStyle() != null) {
			XMLEntity bgXML = getBackgroundFileStyle().getXMLEntity();
			bgXML.putProperty("id", "LabelStyle");
			xml.addChild(bgXML);
		}
		if (sz != null) {
			xml.putProperty("sizeW", (int) sz.getWidth());
			xml.putProperty("sizeH", (int) sz.getHeight());
		}
		return xml;
	}

	public void setXMLEntity(XMLEntity xml) {
		setDescription(xml.getStringProperty("desc"));

		double x = xml.getDoubleProperty("markerPointX");
		double y = xml.getDoubleProperty("markerPointY");

		double[] minx = xml.getDoubleArrayProperty("minXArray");
		double[] miny = xml.getDoubleArrayProperty("minYArray");
		double[] widths = xml.getDoubleArrayProperty("widthArray");
		double[] heights = xml.getDoubleArrayProperty("heightArray");

		textFieldAreas.clear();
		for (int i = 0; i < minx.length; i++) {
			addTextFieldArea(new Rectangle2D.Double(minx[i], miny[i],
					widths[i], heights[i]));
		}
		markerPoint.setLocation(x, y);
		XMLEntity bgXML = xml.firstChild("id", "LabelStyle");
		if (bgXML != null) {
			background = (BackgroundFileStyle) SymbologyFactory
					.createStyleFromXML(xml.getChild(0), null);
		}
		if (xml.contains("sizeW"))
			sz = new Dimension(xml.getIntProperty("sizeW"),
					xml.getIntProperty("sizeH"));
	}

	public Rectangle2D[] getTextBounds() {
		return (Rectangle2D[]) textFieldAreas
				.toArray(new Rectangle2D[textFieldAreas.size()]);
	}

	public void drawInsideRectangle(Graphics2D g, Rectangle r)
			throws SymbolDrawingException {
		if (getBackgroundFileStyle() != null)
			getBackgroundFileStyle().drawInsideRectangle(g, r);
	}

	public Dimension getSize() {
		if (sz == null && getBackgroundFileStyle() != null) {
			Rectangle bgBounds = getBackgroundFileStyle().getBounds();
			setSize(bgBounds.getWidth(), bgBounds.getHeight());
		} else if (sz == null)
			sz = defaultSize;
		return sz;
	}

	public Point2D getMarkerPoint() {
		return markerPoint;
	}

	public void setMarkerPoint(Point2D p) throws IllegalArgumentException {
		if (p.getX() < 0 || p.getX() > 1)
			throw new IllegalArgumentException("X must be >=0 and <=1 ("
					+ p.getX() + ")");
		if (p.getY() < 0 || p.getY() > 1)
			throw new IllegalArgumentException("Y must be >=0 and <=1 ("
					+ p.getY() + ")");
		// the marker represents the point labeled in relative percent units
		this.markerPoint = p;
	}

	public void drawOutline(Graphics2D g, Rectangle r)
			throws SymbolDrawingException {
		if (getBackgroundFileStyle() != null)
			getBackgroundFileStyle().drawOutline(g, r);

		final double[] xy = new double[2];
		// draw the pointer
		{
			xy[0] = markerPoint.getX();
			xy[1] = markerPoint.getY();

			int x = (int) Math.round(r.width * xy[0]);
			int y = (int) Math.round(r.height * xy[1]);

			int size = 7;
			g.setColor(Color.ORANGE.darker());
			g.fillOval(x, y, size, size);
			g.setColor(Color.BLACK.brighter());
			g.drawString(Messages.getString("labeled_point"), x + size + 10, y
					+ size);
			g.setColor(Color.BLACK);
			g.drawLine(x - size, (int) (y + (size * 0.5)), x + 2 * size - 1,
					(int) (y + (size * 0.5)));
			g.drawLine((int) (x + (size * 0.5)), y - size,
					(int) (x + (size * 0.5)), y + 2 * size - 1);
		}

		// draw the text fields
		if (textFieldAreas.size() > 0) {
			SimpleFillSymbol sym = new SimpleFillSymbol();
			Color c = Color.blue.brighter().brighter();

			sym.setFillColor(new Color(c.getRed(), c.getGreen(), c.getBlue(),
					100));
			SimpleLineSymbol outline = new SimpleLineSymbol();
			c = Color.BLACK;
			outline.setLineColor(new Color(c.getRed(), c.getGreen(), c
					.getBlue(), 100));
			sym.setOutline(outline);
			for (int i = 0; i < textFieldAreas.size(); i++) {
				// FIXME: Esto es un parche, habría que cambiar el API de los
				// simbolos y/o estilos
				// pero mientras tanto
				if (getBackgroundFileStyle() == null) {
					Rectangle2D textFieldArea = (Rectangle2D) textFieldAreas
							.get(i);
					xy[0] = textFieldArea.getX();
					xy[1] = textFieldArea.getY();

					int x = (int) Math.round(((r.width) * xy[0]));
					int y = (int) Math.round((r.height) * xy[1]);

					xy[0] = textFieldArea.getMaxX();
					xy[1] = textFieldArea.getMaxY();

					int width = (int) Math.round((r.width * xy[0]) - x);
					int height = (int) Math.round((r.height * xy[1]) - y);

					Rectangle aux = new Rectangle(x, y, width, height);
					FShape shp = new FPolygon2D(new GeneralPathX(aux));
					sym.draw(g, null, shp, null);
					g.setColor(Color.BLACK);
					g.drawString(String.valueOf(i + 1), x + 5, y + 10); // start
																		// with
																		// 1
				} else {
					double xOffset = 0;
					double yOffset = 0;
					double scale = 1;
					Dimension backgroundBounds = getSize();
					if (backgroundBounds.getWidth() > backgroundBounds
							.getHeight()) {
						scale = r.getWidth() / backgroundBounds.getWidth();
						yOffset = 0.5 * (r.getHeight() - backgroundBounds
								.getHeight() * scale);
					} else {
						scale = r.getHeight() / backgroundBounds.getHeight();
						xOffset = 0.5 * (r.getWidth() - backgroundBounds
								.getWidth() * scale);
					}

					Rectangle2D textFieldArea = (Rectangle2D) textFieldAreas
							.get(i);
					xy[0] = textFieldArea.getX();
					xy[1] = textFieldArea.getY();

					int x = (int) Math.round(xy[0]
							* backgroundBounds.getWidth() * scale + xOffset);
					int y = (int) Math.round(xy[1]
							* backgroundBounds.getHeight() * scale + yOffset);

					xy[0] = textFieldArea.getMaxX();
					xy[1] = textFieldArea.getMaxY();

					int width = (int) Math.round((xy[0]
							* backgroundBounds.getWidth() * scale + xOffset)
							- x);
					int height = (int) Math.round((xy[1]
							* backgroundBounds.getHeight() * scale + yOffset)
							- y);

					Rectangle aux = new Rectangle(x, y, width, height);
					FShape shp = new FPolygon2D(new GeneralPathX(aux));
					sym.draw(g, null, shp, null);
					g.setColor(Color.BLACK);
					g.drawString(String.valueOf(i + 1), x + 5, y + 10); // start
																		// with
																		// 1

				}
			}
		}
	}

	public void setTextFieldArea(int index, Rectangle2D rect) {
		textFieldAreas.set(index, rect);
	}

	public void addTextFieldArea(Rectangle2D rect) {
		textFieldAreas.add(rect);
	}

	public void deleteTextFieldArea(int index) {
		textFieldAreas.remove(index);
	}

	public void setSize(double width, double height) {
		sz = new Dimension((int) Math.round(width), (int) Math.round(height));
	}

	public BackgroundFileStyle getBackgroundFileStyle() {
		return background;
	}

	public void setBackgroundFileStyle(BackgroundFileStyle background) {
		this.background = background;
	}

	public void setBackground(XMLEntity background) {
		XMLEntity xml = getXMLEntity();

		if (xml.firstChild("id", "LabelStyle") != null) {
			xml.removeChild(xml.firstIndexOfChild("id", "LabelStyle"));
		}
		XMLEntity xmlBG = background;
		xmlBG.putProperty("id", "LabelStyle");
		xml.addChild(xmlBG);
		setXMLEntity(xml);
	}

	public XMLEntity getBackground() {
		return this.background.getXMLEntity();
	}
}
