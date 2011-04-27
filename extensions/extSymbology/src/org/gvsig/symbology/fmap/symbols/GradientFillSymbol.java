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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.print.attribute.PrintRequestAttributeSet;

import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FPolygon2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.AbstractFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SymbolDrawingException;
import com.iver.utiles.StringUtilities;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.swing.threads.Cancellable;

/**
 *
 * Allows the user to fill a polygon with a gradient color.This gradient
 * can be painted with 4 different methods (linal, rectangular, circular and
 * buffered) and, for each one will be possible to modify its angle, percentage
 * and intervals.
 *
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class GradientFillSymbol  extends AbstractFillSymbol {

	private GradientFillSymbol gradsym;
	private SimpleFillSymbol sfs = new SimpleFillSymbol();

	private double angle;
	private double percentage = 100;
	private int intervals;
	private Color[] gradientColor=null;

	private int style;
	private PrintRequestAttributeSet properties;

	public ISymbol getSymbolForSelection() {

		if (gradsym==null)gradsym=new GradientFillSymbol();
		return gradsym;

	}


	public void draw(Graphics2D g, AffineTransform affineTransform, FShape shp, Cancellable cancel) {

		Rectangle2D bounds = shp.getBounds();
		double radius = Math.abs(Math.max(bounds.getHeight(), bounds.getWidth()));
		double centerX = bounds.getCenterX();
		double centerY = bounds.getCenterY();

		if(gradientColor != null)
			setFillColor(gradientColor[0]);
		g.fill(shp);

		g.setClip(shp);

		if (radius <= 1) {
			if(gradientColor != null)
				g.setColor(gradientColor[0]);
			g.drawLine((int) centerX, (int) centerY, (int) centerX, (int) centerY);
			return;
		}

		/*Creation of a new shape depending on the style of the gradient*/
		Shape myShape = null;
		if (style==0) {//Style=buffered
			myShape = shp;
		} else if (style==1) {//Style=lineal
			myShape = new Line2D.Double(bounds.getMinX(), bounds.getMaxY(), bounds.getMaxX(), bounds.getMaxY());
		} else	if (style==2) {//Style=circular
			myShape = new Ellipse2D.Double(bounds.getMinX(), bounds.getMinY(), radius, radius);
		} else if (style==3) {//Style=rectangular
			myShape = shp.getBounds();
		}

		if (intervals == 0) intervals = 1;

		/*The variable separation will be used to specify the width of the line*/
		int separation=(int) Math.abs(Math.min(bounds.getHeight(), bounds.getWidth()))/(int)intervals;
		/*If the style is linal the separation will be double*/
		if (style==1)separation*=2;

		/*If the user wants to apply a rotation*/
		boolean bRotate = angle != 0;

		/*All the intervals are painted*/
		for (int i = intervals; (cancel==null || !cancel.isCanceled()) && i>0 ; i--) {
			BasicStroke stroke = new BasicStroke((float) (separation*i*percentage*0.01)+1);

			FShape fShape = new FPolygon2D(
					new GeneralPathX(stroke.createStrokedShape(myShape)));

			double cenX,cenY;
			cenX = fShape.getBounds().getCenterX();
			cenY = fShape.getBounds().getCenterY();

			if (bRotate) {

				g.translate(cenX, cenY);
				g.rotate(angle);

				fShape.transform(AffineTransform.getTranslateInstance(-cenX,-cenY));

			}
			if(gradientColor != null)
				sfs.setFillColor(gradientColor[i-1]);
			sfs.draw(g, affineTransform, fShape, cancel);


			if (bRotate) {

				g.rotate(-angle);
				g.translate(-cenX, -cenY);
			}
		}

		g.setClip(null);

		/*If an outline exists it is painted*/
		ILineSymbol outLineSymbol = getOutline();
		if (outLineSymbol != null && hasOutline())
			outLineSymbol.draw(g, affineTransform, shp, null);
	}

	public XMLEntity getXMLEntity() {

		XMLEntity xml = new XMLEntity();

		xml.putProperty("className", getClassName());
		xml.putProperty("desc", getDescription());
		xml.putProperty("isShapeVisible", isShapeVisible());
		xml.putProperty("angle", angle);
		xml.putProperty("intervals",intervals);
		xml.putProperty("percentage", percentage);
		xml.putProperty("style", style);
		xml.putProperty("referenceSystem", getReferenceSystem());
		xml.putProperty("unit", getUnit());

		if(gradientColor != null){
			String[] strColors = new String[gradientColor.length];
			for (int i = 0; i < strColors.length; i++) {
				strColors[i] = StringUtilities.color2String(gradientColor[i]);
			}
			xml.putProperty("gradientColor", strColors);
		}

		Color c2 = getFillColor();
		if (c2!=null)
			xml.putProperty("color", StringUtilities.color2String(getFillColor()));

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

		if (xml.contains("color"))
			setFillColor(StringUtilities.string2Color(xml.getStringProperty("color")));


		setDescription(xml.getStringProperty("desc"));
		setIsShapeVisible(xml.getBooleanProperty("isShapeVisible"));
		setAngle(xml.getDoubleProperty("angle"));
		setStyle(xml.getIntProperty("style"));
		setIntervals(xml.getIntProperty("intervals"));
		setPercentage(xml.getDoubleProperty("percentage"));
		if(xml.contains("gradientColor")){
			String[] strColors = xml.getStringArrayProperty("gradientColor");

			Color[] cc = new Color[strColors.length];
			for (int i = 0; i < cc.length; i++) {
				cc[i] = StringUtilities.string2Color(strColors[i]);
			}
			setGradientColor(cc);

		}

		XMLEntity xmlOutline = xml.firstChild("id", "outline");
		if (xmlOutline != null) {
			setOutline((ILineSymbol) SymbologyFactory.
					createSymbolFromXML(xmlOutline, "outline"));
		}

		if (xml.contains("unit")) { // remove this line when done

			// measure unit (for outline)
			setUnit(xml.getIntProperty("unit"));

			// reference system (for outline)
			setReferenceSystem(xml.getIntProperty("referenceSystem"));
		}
		//has Outline
		if(xml.contains("hasOutline"))
			setHasOutline(xml.getBooleanProperty("hasOutline"));
	}




	public int getSymbolType() {
		return FShape.POLYGON;
	}

	public void drawInsideRectangle(Graphics2D g,AffineTransform scaleInstance, Rectangle r, PrintRequestAttributeSet properties) throws SymbolDrawingException {
		if (properties==null)
			draw(g, null, new FPolygon2D(new GeneralPathX(r)), null);
		else
			print(g, new AffineTransform(), new FPolygon2D(new GeneralPathX(r)), properties);
	}


	public String getClassName() {
		return getClass().getName();
	}

	public void print(Graphics2D g, AffineTransform at, FShape shape, PrintRequestAttributeSet properties) {
		this.properties=properties;
        draw(g, at, shape, null);
        this.properties=null;
	}

	/**
	 * Defines the angle for the rotation of the image when it is added to create the
	 * padding
	 *
	 * @return angle
	 */
	public double getAngle() {
		return angle;
	}
	/**
	 * Sets the angle for the rotation of the image when it is added to create the padding
	 * @param angle
	 */
	public void setAngle(double angle) {
		this.angle = angle;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage= percentage;
	}

	public int getIntervals() {
		return intervals;
	}

	public void setIntervals(int intervals) {
		this.intervals= intervals;
	}

	public int getStyle(){

		return style;

	}
	public void setStyle(int style) {

		this.style=style;
	}

	public Color[] getGradientColor(){

		return gradientColor;
	}

	public void setGradientColor(Color[] gradientcolor) {
		this.gradientColor=gradientcolor;
	}

}
