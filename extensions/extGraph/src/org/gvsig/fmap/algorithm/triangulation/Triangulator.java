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

import java.util.List;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * 
 *         Interface to allow the use of several algorithms to triangulate. It
 *         will be mainly Delaunay Triangulations, but maybe in the future we
 *         can use Constrained Delaunay Triangulations (CDT), or maybe better
 *         algorithms.
 * 
 */
public interface Triangulator {
	public void addVertex(Vertex v);

	public TIN calculateTriangulation();

	public List<Triangle> getTriangles();
}
