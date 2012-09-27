/*
 * Created on 22-nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
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
package com.iver.cit.gvsig.fmap.rendering.styling;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import org.geotools.renderer.lite.StyledShapePainter;
import org.geotools.renderer.style.LineStyle2D;
import org.geotools.renderer.style.PolygonStyle2D;
import org.geotools.renderer.style.Style2D;

import com.iver.utiles.StringUtilities;
import com.iver.utiles.XMLEntity;

/**
 * @author   FJP  TODO To change the template for this generated type comment go to  Window - Preferences - Java - Code Generation - Code and Comments
 */
public class FStyle2D {
	public static final int POINT = 1;
    public static final int LINE = 2;
    public static final int POLYGON = 4;
    public static final int TEXT = 8;
        
	public final static int SYMBOL_TYPE_POINT = 1;
	public final static int SYMBOL_TYPE_LINE = 3;
	public final static int SYMBOL_TYPE_FILL = 5;
	public final static int SYMBOL_TYPE_TEXT = 7;
	public final static int SYMBOL_TYPE_ICON = 9;
		
	public final static int SYMBOL_TYPE_POINTZ = 11;
	public final static int SYMBOL_TYPE_MULTIPOINT = 8;
	public final static int SYMBOL_STYLE_POINTZ = 0;
	public final static int SYMBOL_TYPE_POLYLINEZ = 13;
	public final static int SYMBOL_TYPE_POLYGONZ = 15;
	// Para símbolos de tipo polígono.
	public final static int SYMBOL_STYLE_FILL_SOLID = 1;
	public final static int SYMBOL_STYLE_FILL_TRANSPARENT = 2;
	public final static int SYMBOL_STYLE_FILL_HORIZONTAL = 3;
	public final static int SYMBOL_STYLE_FILL_VERTICAL = 4;
	public final static int SYMBOL_STYLE_FILL_CROSS = 5;
	public final static int SYMBOL_STYLE_FILL_UPWARD_DIAGONAL = 6;
	public final static int SYMBOL_STYLE_FILL_DOWNWARD_DIAGONAL = 7;
	public final static int SYMBOL_STYLE_FILL_CROSS_DIAGONAL = 8;
	public final static int SYMBOL_STYLE_FILL_GRAYFILL = 9;
	public final static int SYMBOL_STYLE_FILL_LIGHTGRAYFILL = 10;
	public final static int SYMBOL_STYLE_FILL_DARKGRAYFILL = 11;

	//	Para símbolos de tipo Punto
	public final static int SYMBOL_STYLE_MARKER_CIRCLE = 30;
	public final static int SYMBOL_STYLE_MARKER_SQUARE = 31;
	public final static int SYMBOL_STYLE_MARKER_TRIANGLE = 32;
	public final static int SYMBOL_STYLE_MARKER_CROSS = 33;
	public final static int SYMBOL_STYLE_MARKER_TRUETYPE = 34;
	public final static int SYMBOL_STYLE_MARKER_IMAGEN = 35;		      

	// Para símbolos de líneas
	public final static int SYMBOL_STYLE_LINE_SOLID = 60;
	public final static int SYMBOL_STYLE_LINE_DASH = 61;
	public final static int SYMBOL_STYLE_LINE_DOT = 62;
	public final static int SYMBOL_STYLE_LINE_DASHDOT = 63;
	public final static int SYMBOL_STYLE_LINE_DASHDOTDOT = 64;
	public final static int SYMBOL_STYLE_LINE_RAIL = 65;
	public final static int SYMBOL_STYLE_LINE_ARROW = 66;
    
	// Para símbolos de tipo texto
	public final static int SYMBOL_STYLE_TEXT_NORMAL = 90;
	public final static int SYMBOL_STYLE_TEXT_CURSIVE = 91;
	public final static int SYMBOL_STYLE_TEXT_BOLD = 92;
	public final static int SYMBOL_STYLE_TEXT_BOLDCURSIVE = 93;
    
    /**
	 * RGB (incluye transparencia) que se utilizará para la aceleración gráfica
	 * @uml.property  name="rgbPoint"
	 */
	private int rgbPoint;
	/**
	 * @uml.property  name="rgbLine"
	 */
	private int rgbLine;
	/**
	 * @uml.property  name="rgbPolygon"
	 */
	private int rgbPolygon;
	/**
	 * @uml.property  name="rgbText"
	 */
	private int rgbText;
	/**
	 * @uml.property  name="pointStyle2D"
	 */
	private Style2D pointStyle2D;
	/**
	 * @uml.property  name="lineStyle2D"
	 */
	private LineStyle2D lineStyle2D;
	/**
	 * @uml.property  name="polygonStyle2D"
	 */
	private PolygonStyle2D polygonStyle2D;
	/**
	 * @uml.property  name="textStyle2D"
	 */
	private Style2D textStyle2D;
	private static BufferedImage img = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
	private static StyledShapePainter shpPainter = new StyledShapePainter();
	private static Rectangle rect = new Rectangle(0,0,1,1);
		
	/**
	 * @return  Returns the rgb (incluye transparencia) que se utilizará para la aceleración gráfica.
	 * @uml.property  name="rgbPoint"
	 */
	public int getRgbPoint() {
		return rgbPoint;
	}
	/**
	 * @return  Returns the rgbLine.
	 * @uml.property  name="rgbLine"
	 */
	public int getRgbLine() {
		return rgbLine;
	}
	/**
	 * @return  Returns the rgbPolygon.
	 * @uml.property  name="rgbPolygon"
	 */
	public int getRgbPolygon() {
		return rgbPolygon;
	}
	/**
	 * @return  Returns the rgbText.
	 * @uml.property  name="rgbText"
	 */
	public int getRgbText() {
		return rgbText;
	}
	
	/**
	 * @return  Returns the style2D.
	 * @uml.property  name="lineStyle2D"
	 */
	public synchronized LineStyle2D getLineStyle2D() {
		return lineStyle2D;
	}

	// TODO geotools refactoring
	// /**
	// * @param style2D The style2D to set.
	// * @uml.property name="lineStyle2D"
	// */
	// public synchronized void setLineStyle2D(LineStyle2D style2D) {
	// lineStyle2D = style2D;
	// // Recalculamos el RGB
	// Graphics2D g2 = img.createGraphics();
	//
	// shpPainter.paint(g2,rect,style2D,0);
	// rgbLine = img.getRGB(0,0);
	//
	// }
	//
	// /**
	// * @return Returns the style2D.
	// * @uml.property name="polygonStyle2D"
	// */
	// public synchronized PolygonStyle2D getPolygonStyle2D() {
	// return polygonStyle2D;
	// }
	// /**
	// * @param style2D The style2D to set.
	// * @uml.property name="polygonStyle2D"
	// */
	// public synchronized void setPolygonStyle2D(PolygonStyle2D style2D) {
	// polygonStyle2D = style2D;
	// // Recalculamos el RGB
	// Graphics2D g2 = img.createGraphics();
	//
	// shpPainter.paint(g2,rect,style2D,0);
	// rgbPolygon = img.getRGB(0,0);
	//
	// }
	//
	// /**
	// * @return Returns the style2D.
	// * @uml.property name="pointStyle2D"
	// */
	// public synchronized Style2D getPointStyle2D() {
	// return pointStyle2D;
	// }
	// /**
	// * @param style2D The style2D to set.
	// * @uml.property name="pointStyle2D"
	// */
	// public synchronized void setPointStyle2D(Style2D style2D) {
	// pointStyle2D = style2D;
	// // Recalculamos el RGB
	// Graphics2D g2 = img.createGraphics();
	//
	// shpPainter.paint(g2,rect,style2D,0);
	// rgbPoint = img.getRGB(0,0);
	//
	// }
	//
	// /**
	// * @return Returns the style2D.
	// * @uml.property name="textStyle2D"
	// */
	// public synchronized Style2D getTextStyle2D() {
	// return textStyle2D;
	// }
	// /**
	// * @param style2D The style2D to set.
	// * @uml.property name="textStyle2D"
	// */
	// public synchronized void setTextStyle2D(Style2D style2D) {
	// textStyle2D = style2D;
	// // Recalculamos el RGB
	// Graphics2D g2 = img.createGraphics();
	//
	// shpPainter.paint(g2,rect,style2D,0);
	// rgbText = img.getRGB(0,0);
	//
	// }
public XMLEntity getXMLEntity(){
	XMLEntity xml = new XMLEntity();
	xml.putProperty("className",this.getClass().getName());
	xml.putProperty("rgbLine",rgbLine);
	xml.putProperty("rgbPolygon",rgbPolygon);
	xml.putProperty("rbgText",rgbText);
	
	///////////////LINE
	xml.putProperty("lMaxScale",lineStyle2D.getMaxScale());
	xml.putProperty("lMinScale",lineStyle2D.getMinScale());
	if (lineStyle2D.getContour() instanceof Color){
		xml.putProperty("lContour",StringUtilities.color2String((Color)lineStyle2D.getContour()));
	}else if (lineStyle2D.getContour() instanceof GradientPaint){
		xml.putProperty("lTransparency",((GradientPaint)lineStyle2D.getContour()).getTransparency());
		xml.putProperty("lColor1",StringUtilities.color2String(((GradientPaint)lineStyle2D.getContour()).getColor1()));
		xml.putProperty("lColor2",StringUtilities.color2String(((GradientPaint)lineStyle2D.getContour()).getColor2()));
		xml.putProperty("lPoint1X",((GradientPaint)lineStyle2D.getContour()).getPoint1().getX());
		xml.putProperty("lPoint1Y",((GradientPaint)lineStyle2D.getContour()).getPoint1().getY());
		xml.putProperty("lPoint2X",((GradientPaint)lineStyle2D.getContour()).getPoint2().getX());
		xml.putProperty("lPoint2Y",((GradientPaint)lineStyle2D.getContour()).getPoint2().getY());
	}else if (lineStyle2D.getContour() instanceof TexturePaint){
		xml.putProperty("lTransparency",((TexturePaint)lineStyle2D.getContour()).getTransparency());
		xml.putProperty("lAnchorRectX",((TexturePaint)lineStyle2D.getContour()).getAnchorRect().getX());
		xml.putProperty("lAnchorRectY",((TexturePaint)lineStyle2D.getContour()).getAnchorRect().getY());
		xml.putProperty("lAnchorRectW",((TexturePaint)lineStyle2D.getContour()).getAnchorRect().getWidth());
		xml.putProperty("lAnchorRectH",((TexturePaint)lineStyle2D.getContour()).getAnchorRect().getHeight());
		//TODO falta que guardar la imagen
	}
	//TODO xml.putProperty("",lineStyle2D.getContourComposite());
	//TODO xml.putProperty("",lineStyle2D.getGraphicStroke());
	//TODO xml.putProperty("",lineStyle2D.getStroke());
	
	
	//////POINT
	xml.putProperty("pointMaxScale",pointStyle2D.getMaxScale());
	xml.putProperty("pointMinScale",pointStyle2D.getMinScale());
	
	////////////POLYGON
	xml.putProperty("pMaxScale",polygonStyle2D.getMaxScale());
	xml.putProperty("pMinScale",polygonStyle2D.getMinScale());
	if (polygonStyle2D.getContour() instanceof Color){
		xml.putProperty("pContour",StringUtilities.color2String((Color)polygonStyle2D.getContour()));
	}else if (polygonStyle2D.getContour() instanceof GradientPaint){
		xml.putProperty("pContourTransparency",((GradientPaint)polygonStyle2D.getContour()).getTransparency());
		xml.putProperty("pContourColor1",StringUtilities.color2String(((GradientPaint)polygonStyle2D.getContour()).getColor1()));
		xml.putProperty("pContourColor2",StringUtilities.color2String(((GradientPaint)polygonStyle2D.getContour()).getColor2()));
		xml.putProperty("pContourPoint1X",((GradientPaint)polygonStyle2D.getContour()).getPoint1().getX());
		xml.putProperty("pContourPoint1Y",((GradientPaint)polygonStyle2D.getContour()).getPoint1().getY());
		xml.putProperty("pContourPoint2X",((GradientPaint)polygonStyle2D.getContour()).getPoint2().getX());
		xml.putProperty("pContourPoint2Y",((GradientPaint)polygonStyle2D.getContour()).getPoint2().getY());
	}else if (polygonStyle2D.getContour() instanceof TexturePaint){
		xml.putProperty("pContourTransparency",((TexturePaint)polygonStyle2D.getContour()).getTransparency());
		xml.putProperty("pContourAnchorRectX",((TexturePaint)polygonStyle2D.getContour()).getAnchorRect().getX());
		xml.putProperty("pContourAnchorRectY",((TexturePaint)polygonStyle2D.getContour()).getAnchorRect().getY());
		xml.putProperty("pContourAnchorRectW",((TexturePaint)polygonStyle2D.getContour()).getAnchorRect().getWidth());
		xml.putProperty("pContourAnchorRectH",((TexturePaint)polygonStyle2D.getContour()).getAnchorRect().getHeight());
		//TODO falta que guardar la imagen
	}
	if (polygonStyle2D.getFill() instanceof Color){
		xml.putProperty("pFill",StringUtilities.color2String((Color)polygonStyle2D.getContour()));
	}else if (polygonStyle2D.getContour() instanceof GradientPaint){
		xml.putProperty("pFillTransparency",((GradientPaint)polygonStyle2D.getContour()).getTransparency());
		xml.putProperty("pFillColor1",StringUtilities.color2String(((GradientPaint)polygonStyle2D.getContour()).getColor1()));
		xml.putProperty("pFillColor2",StringUtilities.color2String(((GradientPaint)polygonStyle2D.getContour()).getColor2()));
		xml.putProperty("pFillPoint1X",((GradientPaint)polygonStyle2D.getContour()).getPoint1().getX());
		xml.putProperty("pFillPoint1Y",((GradientPaint)polygonStyle2D.getContour()).getPoint1().getY());
		xml.putProperty("pFillPoint2X",((GradientPaint)polygonStyle2D.getContour()).getPoint2().getX());
		xml.putProperty("pFillPoint2Y",((GradientPaint)polygonStyle2D.getContour()).getPoint2().getY());
	}else if (polygonStyle2D.getContour() instanceof TexturePaint){
		xml.putProperty("pFillTransparency",((TexturePaint)polygonStyle2D.getContour()).getTransparency());
		xml.putProperty("pFillAnchorRectX",((TexturePaint)polygonStyle2D.getContour()).getAnchorRect().getX());
		xml.putProperty("pFillAnchorRectY",((TexturePaint)polygonStyle2D.getContour()).getAnchorRect().getY());
		xml.putProperty("pFillAnchorRectW",((TexturePaint)polygonStyle2D.getContour()).getAnchorRect().getWidth());
		xml.putProperty("pFillAnchorRectH",((TexturePaint)polygonStyle2D.getContour()).getAnchorRect().getHeight());
		//TODO falta que guardar la imagen
	}
	//TODO xml.putProperty("",polygonStyle2D.getContourComposite());
	//TODO xml.putProperty("",polygonStyle2D.getGraphicStroke());
	//TODO xml.putProperty("",polygonStyle2D.getStroke());
	
	
	
		return xml;
}
}
