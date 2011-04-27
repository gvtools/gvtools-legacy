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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * 
 */
public abstract class AbstractTriangulator implements Triangulator {

	protected TIN tin = new TIN();
	protected Rectangle2D.Double fullExtent = null;
	protected Vertex firstVertex = null;
//	protected List<Vertex> vertices = new ArrayList<Vertex>();
//	protected List<Triangle> triangles = new ArrayList<Triangle>();
	
	public void addVertex(Vertex v) {
		tin.addVertex(v);
		if (firstVertex != null)
			if (fullExtent == null) {
				fullExtent = new Rectangle2D.Double();
				fullExtent.setFrameFromDiagonal(v.getX(), v.getY(), 
					firstVertex.getX(), firstVertex.getY());
			}
			else
				fullExtent.add(v.getX(), v.getY());
		
		if (firstVertex == null) 
			firstVertex = v;
	}

	public List<Triangle> getTriangles() {
		return tin.getTriangles();
	}


}
