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
import java.awt.geom.Rectangle2D;

import javax.print.attribute.PrintRequestAttributeSet;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class PointAdapter extends PolyLineAdapter {

	public void paint(Graphics2D g, AffineTransform at, boolean andLastPoint) {
	}
	public void obtainShape(Point2D p) {
        GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD, 1);
        elShape.moveTo(p.getX(), p.getY());
        setGPX(elShape);
    }


	/**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     * @param at DOCUMENT ME!
     * @param symbol DOCUMENT ME!
     */
    public void draw(Graphics2D g, AffineTransform at, ISymbol symbol) {
    	symbol.draw(g,at,getShape(at), null);
    }
    public void print(Graphics2D g, AffineTransform at, ISymbol symbol,PrintRequestAttributeSet properties) {
        symbol.print(g,at,getShape(at), properties);
    }
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public FShape getShape(AffineTransform at) {
    	FPoint2D point=new FPoint2D(getGPX().getCurrentPoint());
    	point.transform(at);
        return point;
    }
    public Rectangle2D getBounds2D(){
        Rectangle2D r=getShape(new AffineTransform()).getBounds2D();
        double w=r.getWidth();
        double h=r.getHeight();
        double x=r.getX();
        double y=r.getY();
        boolean modified=false;
        if (r.getWidth()<0.5) {
         modified=true;
         w=1;
         x=x-0.25;
        }
        if(r.getHeight()<0.5) {
         modified=true;
         h=1;
//         y=y-0.5;
        }
        if (modified)
         return new Rectangle2D.Double(x,y,w,h);
        return r;
   }
}
