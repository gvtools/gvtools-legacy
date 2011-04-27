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
package com.iver.cit.gvsig.fmap.core;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Serializable;

import org.cresques.cts.ICoordTrans;

import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.v02.FLabel;
import com.iver.cit.gvsig.fmap.rendering.FStyledShapePainter;
import com.iver.utiles.swing.threads.Cancellable;
import com.vividsolutions.jts.geom.Geometry;


/**
 * Interfaz de Geometría.
 *
 * @author $author$
 */
public interface IGeometry extends Shape, Serializable {
	public static int BEST = 0;
	public static int N = 1;
	public static int NE = 2;
	public static int E = 3;
	public static int SE = 4;
	public static int S = 5;
	public static int SW = 6;
	public static int W = 7;
	public static int NW = 8;

	public static int SELECTHANDLER=0;
	public static int STRETCHINGHANDLER=1;
	public final static FStyledShapePainter shpPainter = new FStyledShapePainter();

	/**
	 * Dibujará esta Shape en el Graphics con el símbolo que se pasa como
	 * parámetro y despues de aplicarle la transformación que se pasa también
	 * como parámetro. El parametro image que recibe es la imagen de la cual
	 * se obtuvo el graphics que también se pasa como parámetro. Dibujará la
	 * geometria en caso de que la IGeometry intersecte o esté contenida en el
	 * rectángulo que se pasa como parámetro
	 *
	 * @param g DOCUMENT ME!
	 * @param vp TODO
	 * @param symbol DOCUMENT ME!
	 */
	void draw(Graphics2D g, ViewPort vp, ISymbol symbol);

	/**
	 * Dibuja la geometría sobre el Graphics2D que se pasa como parámetro.
	 *
	 * @param g Graphics2D.
	 * @param vp ViewPort.
	 * @param symbol Símbolo.
	 * @param cancel TODO
	 */
	void draw(Graphics2D g, ViewPort vp, ISymbol symbol, Cancellable cancel);

    /**
     * You can use this function if you are going to draw into
     * a bitmap. (With ints coords). It will do a decimation,
     * drawing a shape with less coords (faster draw)
     * @param g
     * @param vp
     * @param symbol
     * @param cancel TODO
     */
    void drawInts(Graphics2D g, ViewPort vp, ISymbol symbol, Cancellable cancel);
    
    
    void drawInts(Graphics2D g, ViewPort vp, ISymbol symbol);
	/**
	 * Transforma esta Shape en un Geometry de JTS
	 *
	 * @return Geometría.
	 */
	Geometry toJTSGeometry();

	/**
	 * Obtiene las posiciones donde se debe situar la etiqueta para esta
	 * IGeometry. Es un array porque si una geometria es un multipunto por
	 * ejemplo puede quererse etiquetar todos sus puntos. El parámetro que se
	 * pasa indica como debe de colocar la geometria la etiqueta
	 *
	 * @param position TODO: POR AHORA NO SE HACE CASO A ESTO
	 * @param duplicates TODO: POR AHORA NO SE HACE CASO A ESTO
	 *
	 * @return DOCUMENT ME!
	 */
	FLabel[] createLabels(int position, boolean duplicates);

	/**
	 * Obtiene el tipo de la geometría
	 *
	 * @return una de las constantes de FShape: POINT, LINE, POLIGON
	 */
	int getGeometryType();

	/**
	 * Clona la Geometría.
	 *
	 * @return Geometría clonada.
	 */
	IGeometry cloneGeometry();

	/**
	 * Devuelve true si la geometría intersecta con el rectángulo que se pasa
	 * como parámetro.
	 *
	 * @param r Rectángulo.
	 *
	 * @return True, si intersecta.
	 */
	boolean intersects(Rectangle2D r);
	/**
	 * Devuelve true si la geometría contiene al rectángulo que se pasa
	 * como parámetro.
	 *
	 * @param r Rectángulo.
	 *
	 * @return True, si intersecta.
	 */
	//boolean contains(IGeometry g);

	/**
	 * Se usa en las strategies de dibujo para comprobar de manera rápida
	 * si intersecta con el rectángulo visible
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public boolean fastIntersects(double x, double y, double w, double h);

	/**
	 * Devuelve el Rectángulo que ocupa la geometría.
	 *
	 * @return Rectángulo.
	 */
	Rectangle2D getBounds2D();

	/**
	 * Reproyecta la geometría a partir del transformador de coordenadas.
	 *
	 * @param ct Coordinate Transformer.
	 */
	void reProject(ICoordTrans ct);

	/**
	 * Devuelve el GeneralPathXIterator con la información relativa a la geometría.
	 * @param at TODO
	 *
	 * @return PathIterator.
	 */
	PathIterator getPathIterator(AffineTransform at);

    public byte[] toWKB() throws IOException;
    /**
	 * It returns the handlers of the geomety,
	 * these they can be of two types is straightening and of seleccion.
	 *
	 * @param type Type of handlers
	 *
	 * @return Handlers.
	 */
	public Handler[] getHandlers(int type);

	public void transform(AffineTransform at);

	PathIterator getPathIterator(AffineTransform at, double flatness);

	/**
	 * Useful to have the real shape behind the scenes.
	 * May be uses to edit it knowing it it is a Circle, Ellipse, etc
	 * @return
	 */
	Shape getInternalShape();

	void drawInts(Graphics2D graphics2D, ViewPort viewPort, double dpi, CartographicSupport cartographicSymbol, Cancellable cancel);

	//boolean intersects(IGeometry geom);
}
