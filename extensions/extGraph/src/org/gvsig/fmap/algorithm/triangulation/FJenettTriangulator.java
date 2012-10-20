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
 * 2008 {Software Colaborativo (www.scolab.es)}   {development}
 */

package org.gvsig.fmap.algorithm.triangulation;

import java.util.Iterator;

import org.gvsig.fmap.algorithm.triangulation.fjenett.ITRIANGLE;
import org.gvsig.fmap.algorithm.triangulation.fjenett.Triangulate;
import org.gvsig.fmap.algorithm.triangulation.fjenett.XYZ;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * 
 *         Adapter to F Jenett algorithm
 * 
 */
public class FJenettTriangulator extends AbstractTriangulator {

	// class XYZWrapper extends XYZ {
	// private Vertex v;
	//
	// public XYZWrapper(Vertex v) {
	// super(v.getX(), v.getY(), v.getZ());
	// this.v = v;
	// }
	//
	// public Vertex getVertex() {
	// return v;
	// }
	// }

	@Override
	public void addVertex(Vertex v) {
		// TODO Auto-generated method stub
		super.addVertex(v);
	}

	public TIN calculateTriangulation() {
		int nv = tin.vertices.size();
		Iterator<Vertex> it = tin.vertices.iterator();

		XYZ[] points = new XYZ[nv + 3];

		for (int i = 0; i < nv; i++) {
			Vertex v = it.next();

			XYZ site = new XYZ(v.getX(), v.getY(), v.getZ());

			points[i] = site; // revisar la coordenada z si no funciona
		}

		// Para el big triangle
		for (int i = nv; i < nv + 3; i++)
			points[i] = new XYZ(0, 0, 0);

		ITRIANGLE[] triangles = new ITRIANGLE[nv * 3];

		for (int i = 0; i < triangles.length; i++)
			triangles[i] = new ITRIANGLE();

		int ntri = Triangulate.Triangulate(nv, points, triangles);

		for (int tt = 0; tt < ntri; tt++) {
			createTriangle(triangles[tt]);
		}

		return tin;

	}

	private void createTriangle(ITRIANGLE t) {

		Vertex v1 = tin.vertices.get(t.getP1());
		Vertex v2 = tin.vertices.get(t.getP2());
		Vertex v3 = tin.vertices.get(t.getP3());

		tin.addTriangle(v1, v2, v3);
	}

}
