package com.iver.cit.gvsig.fmap.core;

import java.awt.Shape;
import java.io.IOException;

import org.cresques.cts.IProjection;
import org.geotools.data.postgis.attributeio.WKBEncoder;
import org.gvsig.fmap.geometries.iso.primitive.AbstractGeometryPrimitive;

import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.vividsolutions.jts.geom.Geometry;
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
 * $Id: FGeometry.java,v 1.2 2008/03/25 08:47:41 cvs Exp $
 * $Log: FGeometry.java,v $
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
public abstract class FGeometry extends AbstractGeometry implements FShape, AbstractGeometryPrimitive {
	
	public FGeometry(String id, IProjection projection) {
		super(id, projection);		
	}
	
	public FGeometry(IProjection projection) {
		this(null, projection);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#toWKB()
	 */
	public byte[] toWKB() throws IOException {
		return WKBEncoder.encodeGeometry(toJTSGeometry());
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getInternalShape()
	 */
	public Shape getInternalShape() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#fastIntersects(double, double, double, double)
	 */
	public boolean fastIntersects(double x, double y, double w, double h) {
		return intersects(x,y,w,h);
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry3D#getZs()
	 */
	public double[] getZs() {		
		return null;
	}	

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#cloneGeometry()
	 */
	public IGeometry cloneGeometry() {
		return (IGeometry)cloneFShape();
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getGeometryType()
	 */
	public int getGeometryType() {
		return getShapeType();
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getHandlers(int)
	 */
	public Handler[] getHandlers(int type) {
		if (type==STRETCHINGHANDLER){
			return getStretchingHandlers();
		}else if (type==SELECTHANDLER){
			return getSelectHandlers();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#isSimple()
	 */
	public boolean isSimple() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#toJTSGeometry()
	 */
	public Geometry toJTSGeometry() {
		return FConverter.java2d_to_jts(this);
	}	
	
	
}
