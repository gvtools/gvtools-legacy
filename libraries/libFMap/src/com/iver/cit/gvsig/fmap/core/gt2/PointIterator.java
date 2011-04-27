/*
 * Created on 12-may-2005
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
package com.iver.cit.gvsig.fmap.core.gt2;

import java.awt.geom.AffineTransform;

import com.vividsolutions.jts.geom.Point;


/**
 * A path iterator for the LiteShape class, specialized to iterate over Point objects.
 *
 * @author Andrea Aime
 */
public class PointIterator extends AbstractLiteIterator {
    /** Transform applied on the coordinates during iteration */
    private AffineTransform at;
    
    /** The point we are going to provide when asked for coordinates */
    private Point point;
    
    /** True when the point has been read once */
    private boolean done;
    

    /**
     * Creates a new PointIterator object.
     *
     * @param p The polygon
     * @param at The affine transform applied to coordinates during iteration
     */
    public PointIterator(Point point, AffineTransform at) {
        if (at == null) {
            at = new AffineTransform();
        }
        
        this.at = at;
        this.point = point;
        done = false;
    }

    /**
     * Return the winding rule for determining the interior of the path.
     *
     * @return <code>WIND_EVEN_ODD</code> by default.
     */
    public int getWindingRule() {
        return WIND_EVEN_ODD;
    }

    /**
     * @see java.awt.geom.PathIterator#next()
     */
    public void next() {
        done = true;
    }

    /**
     * @see java.awt.geom.PathIterator#isDone()
     */
    public boolean isDone() {
        return done;
    }

    /**
     * @see java.awt.geom.PathIterator#currentSegment(double[])
     */
    public int currentSegment(double[] coords) {
        coords[0] = point.getX();
        coords[1] = point.getY();
        at.transform(coords, 0, coords, 0, 1);

        return SEG_MOVETO;
    }

}
