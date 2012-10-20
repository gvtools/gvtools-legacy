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

import java.util.ArrayList;
import java.util.Iterator;

import org.gvsig.fmap.algorithm.triangulation.pirol.DelaunayCalculator;
import org.gvsig.fmap.algorithm.triangulation.pirol.DelaunayPunkt;
import org.gvsig.fmap.algorithm.triangulation.pirol.PolygonCreator;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * 
 *         Adapter to F Jenett algorithm
 * 
 */
public class PirolTriangulator extends AbstractTriangulator {

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

		DelaunayPunkt[] points = new DelaunayPunkt[nv];

		for (int i = 0; i < nv; i++) {
			Vertex v = it.next();
			double[] cAux = new double[2];
			cAux[0] = v.getX();
			cAux[1] = v.getY();
			// cAux[2] = v.getZ();
			DelaunayPunkt site = new DelaunayPunkt(cAux, i);

			points[i] = site; // revisar la coordenada z si no funciona
		}

		DelaunayCalculator pirolDelaunayCalculator = new DelaunayCalculator(
				points, null);
		try {
			pirolDelaunayCalculator.run();
			// pirolDelaunayCalculator.createDelaunayNet();
			// pirolDelaunayCalculator.compilePoints();

			ArrayList<DelaunayPunkt[]> triangles = PolygonCreator
					.createTrianglesList(pirolDelaunayCalculator);

			for (int tt = 0; tt < triangles.size(); tt++) {
				createTriangle(triangles.get(tt));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tin;

	}

	private void createTriangle(DelaunayPunkt[] t) {

		Vertex v1 = tin.vertices.get(t[0].getIndex());
		Vertex v2 = tin.vertices.get(t[1].getIndex());
		Vertex v3 = tin.vertices.get(t[2].getIndex());
		System.out.println("T[" + t[0].getIndex() + " - " + t[1].getIndex()
				+ " - " + t[2].getIndex());

		tin.addTriangle(v1, v2, v3);
	}

}
