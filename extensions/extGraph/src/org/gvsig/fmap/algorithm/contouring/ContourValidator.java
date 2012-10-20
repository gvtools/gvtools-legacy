/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

/*
 * AUTHORS (In addition to CIT):
 * 2008 Software Colaborativo (www.scolab.es)   development
 */

package org.gvsig.fmap.algorithm.contouring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * 
 */
public class ContourValidator {
	private Collection<Geometry> insideGeoms;
	private Collection<Geometry> outsideGeoms;
	private Geometry border;

	public ContourValidator(Geometry border,
			Collection<Geometry> outsideGeometries,
			Collection<Geometry> insideGeometries) {
		this.outsideGeoms = outsideGeometries;
		this.insideGeoms = insideGeometries;
		this.border = border;
	}

	/**
	 * @return a List of geometries from insideGeoms that are not inside. It
	 *         does a clip also, so you can use this collection to adapt border
	 *         geometry and fix the error by expanding border. If the list is
	 *         empty, all insideGeoms are inside border.
	 */
	public List<Geometry> checkInsideGeoms() {
		ArrayList<Geometry> clippedGeoms = new ArrayList<Geometry>();
		for (Iterator<Geometry> it = insideGeoms.iterator(); it.hasNext();) {
			Geometry geom = it.next();
			Geometry clip = null;
			if (!border.covers(geom))
				clip = geom.difference(border);
			if (clip != null)
				clippedGeoms.add(clip);
		}
		return clippedGeoms;
	}

	/**
	 * @return a List of geometries from outsideGeoms that are not inside. It
	 *         does a clip also, so you can use this collection to adapt border
	 *         geometry and fix the error by "crunching" the border. If the list
	 *         is empty, all outsideGeoms are outside border.
	 */
	public List<Geometry> checkOutsideGeoms() {
		ArrayList<Geometry> clippedGeoms = new ArrayList<Geometry>();
		for (Iterator<Geometry> it = outsideGeoms.iterator(); it.hasNext();) {
			Geometry geom = it.next();
			Geometry clip = null;
			if (border.covers(geom))
				clip = geom.intersection(border);
			if (clip != null)
				clippedGeoms.add(clip);
		}
		return clippedGeoms;
	}

}
