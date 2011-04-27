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
package com.iver.cit.gvsig.fmap.operations.strategies;

import java.awt.geom.Rectangle2D;

import org.geotools.resources.geometry.XRectangle2D;

import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.IntersectionMatrix;


public class QueryByGeometryVisitor implements FeatureVisitor {

	private Geometry geom = null;
    private IGeometry geom_gvSIG = null;
    private Rectangle2D geomBounds;
	private int relation;
    private FBitSet bitset = null;
	public static final int EQUALS = 0;
	public static final int DISJOINT = 1;
	public static final int INTERSECTS = 2;
	public static final int TOUCHES = 3;
	public static final int CROSSES = 4;
	public static final int WITHIN = 5;
	public static final int CONTAINS = 6;
	public static final int OVERLAPS = 7;

    private Geometry getJTSgeom()
    {
        if (geom == null)
            geom = geom_gvSIG.toJTSGeometry();
        return geom;
    }

	/**
	 *
	 */
	public QueryByGeometryVisitor(IGeometry geom, int relation) {
        this.geomBounds = geom.getBounds2D();
		// this.geom = geom.toJTSGeometry();
        this.geom_gvSIG = geom;
		this.relation = relation;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor#visit(com.iver.cit.gvsig.fmap.core.IGeometry, int)
	 */
	public void visit(IGeometry g, int index) throws VisitorException, ProcessVisitorException {
        Geometry g1;
        IntersectionMatrix m;
        switch (relation) {
        case CONTAINS:
        	if (XRectangle2D.intersectInclusive(geomBounds, g.getBounds2D())) {
        		g1 = g.toJTSGeometry();
        		m = getJTSgeom().relate(g1);

        		if (m.isContains()) {
        			bitset.set(index, true);
        		}
        	}
        	break;

        case CROSSES:
        	if (XRectangle2D.intersectInclusive(geomBounds, g.getBounds2D()))
        	{
        		g1 = g.toJTSGeometry();
        		m = getJTSgeom().relate(g1);

        		if (m.isCrosses(g1.getDimension(), geom.getDimension())) {
        			bitset.set(index, true);
        		}
        	}
        	break;

        case DISJOINT:
        	// TODO: POR OPTIMIZAR
        	g1 = g.toJTSGeometry();
        	m = getJTSgeom().relate(g1);
        	if (m.isDisjoint()) {
        		bitset.set(index, true);
        	}
        	break;

        case EQUALS:
        	// TODO: REVISAR ESTO PARA QUE COMPRUEBE SI SON IGUALES LOS RECTANGULOS
        	// Y DE PASO, COMPROBAR SI HACE FALTA USAR ESTA FUNCIÓN DONDE
        	// SE USE EL Rectangle2D.contains
        	if (XRectangle2D.containsInclusive(geomBounds, g.getBounds2D()))
        	{
        		g1 = g.toJTSGeometry();
        		m = getJTSgeom().relate(g1);
        		if (m.isEquals(g1.getDimension(), getJTSgeom().getDimension())) {
        			bitset.set(index, true);
        		}
        	}
        	break;

        case INTERSECTS:
        	if (XRectangle2D.intersectInclusive(geomBounds, g.getBounds2D()))
        	{
        		g1 = g.toJTSGeometry();
        		m = getJTSgeom().relate(g1);

        		if (m.isIntersects()) {
        			bitset.set(index, true);
        		}
        	}
        	break;

        case OVERLAPS:
        	if (XRectangle2D.intersectInclusive(geomBounds, g.getBounds2D()))
        	{
        		g1 = g.toJTSGeometry();
        		m = getJTSgeom().relate(g1);
        		if (m.isOverlaps(g1.getDimension(),
        				geom.getDimension())) {
        			bitset.set(index, true);
        		}
        	}

        	break;

        case TOUCHES:
        	if (XRectangle2D.intersectInclusive(geomBounds, g.getBounds2D()))
        	{
        		g1 = g.toJTSGeometry();
        		m = getJTSgeom().relate(g1);
        		if (m.isTouches(g1.getDimension(), geom.getDimension())) {
        			bitset.set(index, true);
        		}
        	}

        	break;

        case WITHIN:
        	if (XRectangle2D.intersectInclusive(geomBounds, g.getBounds2D())) {
        		g1 = g.toJTSGeometry();
        		m = getJTSgeom().relate(g1);

        		if (m.isWithin()) {
        			bitset.set(index, true);
        		}
        	}

        	break;
        }
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor#stop(com.iver.cit.gvsig.fmap.layers.FLayer)
	 */
	public void stop(FLayer layer) throws VisitorException {
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor#start(com.iver.cit.gvsig.fmap.layers.FLayer)
	 */
	public boolean start(FLayer layer) throws StartVisitorException {
        bitset = new FBitSet();
		return true;
	}

	public FBitSet getBitSet() {
		return bitset;
	}

	public String getProcessDescription() {
		return "Looking for features by geometric criteria";
	}
}
