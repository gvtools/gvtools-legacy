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

import org.gvsig.fmap.algorithm.triangulation.visad.DelaunayWatson;


/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * 
 * Adapter to visad Watson algorithm
 *
 */
public class WatsonTriangulator extends AbstractTriangulator {

	public TIN calculateTriangulation() {
		int numPoints = tin.vertices.size();
	    double[][] samples = new double[2][numPoints];
	    double[] samp0 = samples[0];
	    double[] samp1 = samples[1];
//	    double[] samp2 = samples[2];
	    Iterator<Vertex> it = tin.vertices.iterator();
	    for (int i=0; i<numPoints; i++) {
	    	Vertex v = it.next();
	    	samp0[i] = v.getX();
	    	samp1[i] = v.getY();
//	    	samp2[i] = v.getZ();
	    }

	    try {
			DelaunayWatson tri2 = new DelaunayWatson(samples);
			tri2.improve(samples, 10);
			
//			System.out.println("Fin de trayecto. Num. triángulos=" + tri2.Tri.length);
			for (int i=0; i< tri2.Tri.length; i++) {
			      createTriangle(tri2.Tri[i], samples);
		    }
			return tin;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	private void createTriangle(int[] vertex, double[][] points) {
		
		// TODO: Crear los Vertex con un Factory.
		Vertex v1 = tin.vertices.get(vertex[0]); //new Vertex(points[0][vertex[0]], points[1][vertex[0]]);
		Vertex v2 = tin.vertices.get(vertex[1]); //new Vertex(points[0][vertex[1]], points[1][vertex[1]]);
		Vertex v3 = tin.vertices.get(vertex[2]); //new Vertex(points[0][vertex[2]], points[1][vertex[2]]);
		
		tin.addTriangle(v1, v2, v3);
	}

}

