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
package com.iver.cit.gvsig.fmap.core.adapter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.print.attribute.PrintRequestAttributeSet;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class PolyLineAdapter extends GeometryAdapter {
    private Point2D pointPosition = new Point2D.Double();
    private AffineTransform identity = new AffineTransform();

    /**
     * DOCUMENT ME!
     *
     * @param p DOCUMENT ME!
     */
    public void obtainShape(Point2D p) {
        Point2D[] points = getPoints();
        GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
                points.length);

        if (points.length > 0) {
            elShape.moveTo(((Point2D) points[0]).getX(),
                ((Point2D) points[0]).getY());
        }

        for (int i = 0; i < points.length; i++) {
            elShape.lineTo(((Point2D) points[i]).getX(),
                ((Point2D) points[i]).getY());
        }

        if (points.length > 0) {
            elShape.lineTo(p.getX(), p.getY());
        }

        setGPX(elShape);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public FShape getShape(AffineTransform at) {
    	 GeneralPathX polyLine = new GeneralPathX(new FPolyline2D(getGPX()));
         polyLine.transform(at);

        return new FPolyline2D(polyLine);
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     * @param at DOCUMENT ME!
     * @param symbol DOCUMENT ME!
     */
    public void draw(Graphics2D g, AffineTransform at, ISymbol symbol) {
    	FShape shapeAux =getShape(at);
    	symbol.draw(g,at,shapeAux, null);
    	// FGraphicUtilities.DrawShape(g, at, shapeAux, symbol);
    }
    public void print(Graphics2D g, AffineTransform at, ISymbol symbol,PrintRequestAttributeSet properties) {
    	FShape shapeAux =getShape(at);
    	symbol.print(g,at,shapeAux, properties);
		// FGraphicUtilities.DrawShape(g, at, shapeAux, symbol);
    }
    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     * @param at DOCUMENT ME!
     * @param andLastPoint DOCUMENT ME!
     */
    public void paint(Graphics2D g, AffineTransform at, boolean andLastPoint) {
        if (andLastPoint) {
            obtainShape(pointPosition);
        }

        FShape shapeAux=getShape(at);
        //ISymbol symbol = new FSymbol(FConstant.SYMBOL_TYPE_LINE, Color.red);
        ILineSymbol symbol = SymbologyFactory.createDefaultLineSymbol();
        symbol.setLineColor(Color.RED);
        symbol.draw(g, identity, shapeAux, null);
        // FGraphicUtilities.DrawShape(g, identity, shapeAux,
        //     new FSymbol(FConstant.SYMBOL_TYPE_LINE, Color.red));
    }

    /**
     * DOCUMENT ME!
     *
     * @param p DOCUMENT ME!
     */
    public void pointPosition(Point2D p) {
        pointPosition = p;
    }
}
