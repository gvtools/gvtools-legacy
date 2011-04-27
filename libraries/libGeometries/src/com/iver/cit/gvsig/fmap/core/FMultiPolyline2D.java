package com.iver.cit.gvsig.fmap.core;

import org.cresques.cts.IProjection;
import org.gvsig.fmap.geometries.iso.aggregate.MultiCurve;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

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
/* CVS MESSAGES:
 *
 * $Id: FMultiPolyline2D.java,v 1.2 2008/03/25 08:47:41 cvs Exp $
 * $Log: FMultiPolyline2D.java,v $
 * Revision 1.2  2008/03/25 08:47:41  cvs
 * Visitors removed
 *
 * Revision 1.1  2008/03/12 08:46:20  cvs
 * *** empty log message ***
 *
 *
 */
/**
 * @author Jorge Piera Llodrá (jorge.piera@iver.es)
 */
public class FMultiPolyline2D extends FGeometryCollection implements MultiCurve{
	
	public FMultiPolyline2D(String id, IProjection projection) {
		super(id, projection);
	}

	public FMultiPolyline2D(String id, IProjection projection, FPolyline2D[] lines) {
		super(id, projection, lines);		
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FGeometryCollection#cloneGeometry()
	 */
	public IGeometry cloneGeometry() {
		FPolyline2D[] aux = new FPolyline2D[getPrimitivesNumber()];
		for (int i=0; i < getPrimitivesNumber(); i++){
			aux[i] = (FPolyline2D) geometries[i].cloneGeometry();
		}
		return new FMultiPolyline2D(id, projection, aux);
	}
	
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FGeometryCollection#getGeometryType()
	 */
	public int getGeometryType() {
		return FShape.MULTIPOLYLINE;
	}
	
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FGeometryCollection#toJTSGeometry()
	 */
	public Geometry toJTSGeometry() {
		LineString[] lines = new LineString[geometries.length];
        for (int i = 0; i < lines.length; i++){
        	lines[i] = (LineString)((FPolyline2D)geometries[i]).toJTSGeometry();
        }
        return new GeometryFactory().createMultiLineString(lines);		
	}
}
