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

import java.io.IOException;

import org.gvsig.gpe.parser.ICoordinateIterator;
import org.gvsig.gpe.writer.ICoordinateSequence;

import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FPoint3D;

public class CoordinatesSequencePoint implements ICoordinateSequence,
		ICoordinateIterator {
	double[] points = null;
	int index = 0;

	public CoordinatesSequencePoint(FPoint2D point) {
		this.points = new double[2];
		points[0] = point.getX();
		points[1] = point.getY();
		index = 2;
	}

	public CoordinatesSequencePoint(FPoint3D point) {
		this.points = new double[3];
		points[0] = point.getX();
		points[1] = point.getY();
		points[2] = point.getZs()[0];
		index = 3;
	}

	public int getSize() {
		return 1;
	}

	public ICoordinateIterator iterator() {
		return this;
	}

	public int getDimension() {
		return points.length;
	}

	public boolean hasNext() throws IOException {
		return (index > 0);
	}

	public void next(double[] buffer) throws IOException {
		buffer[0] = points[0];
		buffer[1] = points[1];
		if ((buffer.length == 3) && (points.length == 3)) {
			buffer[2] = points[2];
		}
		index--;
	}

}
