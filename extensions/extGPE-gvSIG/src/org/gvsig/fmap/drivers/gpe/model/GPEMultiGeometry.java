package org.gvsig.fmap.drivers.gpe.model;

import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;

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
 * $Id$
 * $Log$
 *
 */
/**
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class GPEMultiGeometry extends GPEGeometry {
	protected IGeometry geometry = null;
	protected ArrayList geometries = null;
	protected Rectangle2D bounds = null;

	public GPEMultiGeometry(String id, String srs) {
		super(id, null, srs);
		geometries = new ArrayList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.gpe.model.GPEGeometry#getGeometry()
	 */
	public IGeometry getIGeometry() {
		if (geometry == null) {
			geometry = ShapeFactory.createPolygon2D(createGeneralXPath());
		}
		return geometry;
	}

	/**
	 * Create the general X path
	 * 
	 * @return
	 */
	protected GeneralPathX createGeneralXPath() {
		GeneralPathX gp = new GeneralPathX();
		for (int i = 0; i < geometries.size(); i++) {
			IGeometry geom = ((GPEGeometry) geometries.get(i)).getIGeometry();
			FShape shape = (FShape) geom.getInternalShape();
			PathIterator it = shape.getPathIterator(null);
			int theType;
			double[] theData = new double[6];
			while (!it.isDone()) {
				theType = it.currentSegment(theData);
				switch (theType) {
				case PathIterator.SEG_MOVETO:
					gp.moveTo(theData[0], theData[1]);
					break;
				case PathIterator.SEG_LINETO:
					gp.lineTo(theData[0], theData[1]);
					break;
				case PathIterator.SEG_CLOSE:
					break;
				}
				it.next();
			}
		}
		return gp;
	}

	/**
	 * Gets one geometry
	 * 
	 * @param i
	 *            Geometry position
	 * @return A Geometry
	 */
	public GPEGeometry getGeometryAt(int i) {
		return (GPEGeometry) geometries.get(i);
	}

	/**
	 * @return the number of geometries
	 */
	public int getGeometriesSize() {
		return geometries.size();
	}

	/**
	 * Adds a new geometry
	 * 
	 * @param geometry
	 *            The geometry to add
	 */
	public void addGeometry(GPEGeometry geometry) {
		if (bounds == null) {
			bounds = geometry.getShapeBounds();
		} else {
			bounds.add(geometry.getShapeBounds());
		}
		geometries.add(geometry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.drivers.gpe.model.GPEGeometry#getShapeBounds()
	 */
	public Rectangle2D getShapeBounds() {
		return bounds;
	}
}
