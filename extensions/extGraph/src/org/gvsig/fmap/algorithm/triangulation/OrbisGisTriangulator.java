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
import java.util.HashMap;

import org.gvsig.fmap.algorithm.triangulation.orbisgis.Triangulation;

import com.vividsolutions.jts.geom.Coordinate;


/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * 
 * 
 * Adapter to OrbisGIS algorithm 
 * 
 * This algorithm is suitable to use breakLines (very useful for professional use).
 * The algorithm removes duplicated points and may insert others to deal with breakLines.
 * This is the reason to clean initial points and create a new TIN from the result.
 *
 */
public class OrbisGisTriangulator extends AbstractTriangulator {

	public TIN calculateTriangulation() {
		int[][] breakLineList = new int[0][0];

	    try {
	    	final Triangulation t = new Triangulation(tin.vertices.toArray(new Vertex[0]),
	    			breakLineList);
	    	t.triangulate();
			
	    	
	    	HashMap<Coordinate, Vertex> hash = new HashMap<Coordinate, Vertex>();
	    	tin.vertices = new ArrayList<Vertex>();
	    	Coordinate[] points = t.getUniqueTriangulatedPoints();
	    	for (int i=0; i < points.length; i++) {
	    		Vertex v = new Vertex(points[i].x, points[i].y, points[i].z);
	    		hash.put(points[i], v);
	    		tin.addVertex(v);
	    	}
	    	
	    	final Coordinate[] cc = t.getTriangulatedPoints();
	    	long index = 0;
	    	for (int i = 0; i < cc.length; i += 3, index++) {
	    		Vertex v1 = hash.get(cc[i+0]);
	    		Vertex v2 = hash.get(cc[i+1]);
	    		Vertex v3 = hash.get(cc[i+2]);
	    		
	    		tin.addTriangle(v1, v2, v3);
		    }
			return tin;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	

}

