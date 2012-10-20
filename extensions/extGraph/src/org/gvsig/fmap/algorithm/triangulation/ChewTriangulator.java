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

import org.gvsig.fmap.algorithm.triangulation.paul_chew.Pnt;
import org.gvsig.fmap.algorithm.triangulation.paul_chew.Triangle;
import org.gvsig.fmap.algorithm.triangulation.paul_chew.Triangulation;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * 
 *         Adapter to Chew algorithm FIXME: Doesn't work with my data, I don't
 *         know why :-( It seems that there are points that return no cavity. =>
 *         DON'T USE UNTIL THIS IS FIXED.
 * 
 */
public class ChewTriangulator extends AbstractTriangulator {

	class PntWrapper extends Pnt {
		private Vertex v;

		public PntWrapper(Vertex v) {
			super(v.getX(), v.getY());
			this.v = v;
		}

		public PntWrapper(double x, double y, double z) {
			super(x, y);
			this.v = new Vertex(x, y, z);
		}

		/**
		 * Use this for Big Triangle creation
		 * 
		 * @param p
		 */
		public PntWrapper(Pnt p) {
			super(p.coord(0), p.coord(1));
			this.v = null;
		}

		public Vertex getVertex() {
			return v;
		}
	}

	@Override
	public void addVertex(Vertex v) {
		// TODO Auto-generated method stub
		super.addVertex(v);
	}

	public TIN calculateTriangulation() {
		int numPoints = tin.vertices.size();
		Iterator<Vertex> it = tin.vertices.iterator();
		double height = fullExtent.getHeight();
		double h = 3.0 * height;
		double centerX = fullExtent.getCenterX();
		Pnt upPnt = new Pnt(centerX, fullExtent.getMinY() + h);
		Pnt leftPnt = new Pnt(centerX - 2.0 * fullExtent.getWidth(),
				fullExtent.getMinY());
		Pnt rightPnt = new Pnt(centerX + 2.0 * fullExtent.getWidth(),
				fullExtent.getMinY());
		PntWrapper p1 = new PntWrapper(leftPnt);
		PntWrapper p2 = new PntWrapper(upPnt);
		PntWrapper p3 = new PntWrapper(rightPnt);
		Triangle bigTriangle = new Triangle(p1, p3, p2);
		System.out.println("BigTriangle = " + bigTriangle.get(0) + " - "
				+ bigTriangle.get(1));
		Triangulation tri2 = new Triangulation(bigTriangle);
		for (int i = 0; i < numPoints; i++) {
			Vertex v = it.next();

			PntWrapper site = new PntWrapper(v);
			System.out.println("i=" + i + " " + site.toString());
			tri2.delaunayPlace(site);
		}
		Iterator<Triangle> itTri = tri2.iterator();
		while (itTri.hasNext()) {
			Triangle t = itTri.next();
			createTriangle(t);
		}
		return tin;

	}

	private void createTriangle(Triangle t) {

		Vertex v1 = ((PntWrapper) t.get(0)).getVertex();
		Vertex v2 = ((PntWrapper) t.get(1)).getVertex();
		Vertex v3 = ((PntWrapper) t.get(2)).getVertex();
		if ((v1 != null) && (v2 != null) && (v3 != null))
			tin.addTriangle(v1, v2, v3);
	}

}
