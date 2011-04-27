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

import org.gvsig.fmap.algorithm.contouring.TriEdge;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * I think it would be better to speak with people from JTS and pay them to implement
 * contour and delaunay, and put them on JTS library.
 *
 */
public interface Triangle {
	public Vertex getV1();
	public Vertex getV2();
	public Vertex getV3();
	public boolean isVisited();
	public void setVisited(boolean visited);
	public TriEdge getEdgeContainingZ(double height);
	public Coordinate getNextPoint(Coordinate lastPoint, double height);
	public boolean contains(Coordinate pointOut);
	public boolean containsZ(double height) throws Exception;
	public Coordinate[] getSegmentZ(double z);

}

