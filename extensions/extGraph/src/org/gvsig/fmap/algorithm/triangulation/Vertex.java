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

package org.gvsig.fmap.algorithm.triangulation;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * 
 */
public class Vertex extends Coordinate {

	protected ArrayList<Integer> relatedTriIndexes;


	public Vertex(double x, double y) {
		super(x, y, 0.0);
		relatedTriIndexes = new ArrayList<Integer>();
	}

	public Vertex(double x, double y, double z) {
		super(x, y, z);
		relatedTriIndexes = new ArrayList<Integer>();
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public void addTriRef(int idTri) {
		relatedTriIndexes.add(new Integer(idTri));
		
	}

	public ArrayList<Integer> getRelatedTriIndexes() {
		return relatedTriIndexes;
	}

}
