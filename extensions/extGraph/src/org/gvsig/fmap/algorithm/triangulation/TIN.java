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
import java.util.List;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es) Maybe we should check if
 *         there are vertex with same x, y.
 */
public class TIN {
	ArrayList<Vertex> vertices;
	ArrayList<Triangle> triangles;

	public TIN() {
		vertices = new ArrayList<Vertex>();
		triangles = new ArrayList<Triangle>();
	}

	public void addTriangle(Vertex v1, Vertex v2, Vertex v3) {
		// TODO: Use Factory?
		Triangle t = new DefaultTriangle(v1, v2, v3);
		if (triangles.add(t)) {
			v1.addTriRef(triangles.size() - 1);
			v2.addTriRef(triangles.size() - 1);
			v3.addTriRef(triangles.size() - 1);
		}
	}

	public List<Triangle> getTrianglesRelatedTo(Vertex v) {
		ArrayList<Triangle> relatedTris = new ArrayList<Triangle>();
		for (Integer i : v.getRelatedTriIndexes()) {
			Triangle t = triangles.get(i);
			relatedTris.add(t);
		}

		return relatedTris;

	}

	public void addVertex(Vertex v) {
		vertices.add(v);
	}

	public List<Triangle> getTriangles() {
		return triangles;
	}

}
