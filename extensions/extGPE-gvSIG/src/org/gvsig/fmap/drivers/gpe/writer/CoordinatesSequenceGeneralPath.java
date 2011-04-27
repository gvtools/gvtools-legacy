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
* 2009 Iver T.I.  {{Task}}
*/
 
package org.gvsig.fmap.drivers.gpe.writer;

import java.awt.geom.PathIterator;
import java.io.IOException;

import org.gvsig.gpe.parser.ICoordinateIterator;
import org.gvsig.gpe.writer.ICoordinateSequence;

public class CoordinatesSequenceGeneralPath implements ICoordinateSequence, ICoordinateIterator{
	private PathIterator it = null;
	int size = 0;
	boolean hasMoreGeoemtries = true;
	
	public CoordinatesSequenceGeneralPath(PathIterator it) {
		super();
		this.it = it;
	}

	public int getSize() {
		return size;
	}
	
	public void initialize(){
		size = 0;
	}
	
	public boolean hasMoreGeometries(){
		return hasMoreGeoemtries;
	}
	
	public ICoordinateIterator iterator() {
		return this;
	}
	
	public int getDimension() {
		return 2;
	}
	
	public boolean hasNext() throws IOException {
		if (it.isDone()){
			hasMoreGeoemtries = false;
			return false;
		}
		double[] coords = new double[2];
		int type = it.currentSegment(coords);		
		if (type == PathIterator.SEG_CLOSE){
			hasMoreGeoemtries = false;
		}
		return ((type != PathIterator.SEG_MOVETO) || (size == 0));		
	}
	
	public void next(double[] buffer) throws IOException {
		it.currentSegment(buffer);		
		it.next();	
		size++;
	}
	
}

