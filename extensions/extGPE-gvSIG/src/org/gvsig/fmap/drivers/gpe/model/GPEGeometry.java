package org.gvsig.fmap.drivers.gpe.model;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.core.IGeometry;

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
public class GPEGeometry {
	protected IGeometry geometry = null;
	protected String srs = null;
	protected String id = null;
	// This geometri is reprojected from the original
	// CRS to the layer CRS.
	private IGeometry reprojectedGeometry = null;
	protected ArrayList geometries = null;
	protected Rectangle2D bounds = null;

	public GPEGeometry(String id, IGeometry geometry, String srs) {
		super();
		this.geometry = geometry;
		this.srs = srs;
		this.id = id;
		this.geometries = new ArrayList();
	}

	/**
	 * @return the geometry
	 */
	public IGeometry getIGeometry() {
		return geometry;
	}

	/**
	 * @return the srs
	 */
	public String getSrs() {
		return srs;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the reprojectedGeometry
	 */
	public IGeometry getReprojectedGeometry() {
		return reprojectedGeometry;
	}

	/**
	 * @param reprojectedGeometry
	 *            the reprojectedGeometry to set
	 */
	public void setReprojectedGeometry(IGeometry reprojectedGeometry) {
		this.reprojectedGeometry = reprojectedGeometry;
	}

	/**
	 * @return the bounding box
	 */
	public Rectangle2D getShapeBounds() {
		bounds = getIGeometry().getBounds2D();
		return bounds;
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

	public boolean isMultiGeometry() {
		if (geometries.size() == 0)
			return false;
		return true;
	}

}
