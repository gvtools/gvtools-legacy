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

import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.gvsig.gpe.parser.ICoordinateIterator;
import org.gvsig.gpe.writer.ICoordinateSequence;

public class CoordinatesSequenceBbox implements ICoordinateSequence,
		ICoordinateIterator {
	Rectangle2D bbox = null;
	int index = 0;

	public CoordinatesSequenceBbox(Rectangle2D bbox) {
		this.bbox = bbox;
	}

	public int getSize() {
		return 2;
	}

	public ICoordinateIterator iterator() {
		return this;
	}

	public int getDimension() {
		return 2;
	}

	public boolean hasNext() throws IOException {
		if (index <= 1) {
			return true;
		}
		return false;
	}

	public void next(double[] buffer) throws IOException {
		if (index == 0) {
			buffer[0] = bbox.getMinX();
			buffer[1] = bbox.getMinY();
		} else if (index == 1) {
			buffer[0] = bbox.getMaxX();
			buffer[1] = bbox.getMaxY();
		}
		index++;
	}

}
