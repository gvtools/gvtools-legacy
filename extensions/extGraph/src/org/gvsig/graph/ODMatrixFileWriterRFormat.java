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

package org.gvsig.graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * 
 *         From R help: One common use of scan is to read in a large matrix.
 *         Suppose file matrix.dat just contains the numbers for a 200 x 2000
 *         matrix. Then we can use A <- matrix(scan("matrix.dat", n = 200*2000),
 *         200, 2000, byrow = TRUE) On one test this took 1 second (under Linux,
 *         3 seconds under Windows on the same machine) whereas A <-
 *         as.matrix(read.table("matrix.dat")) took 10 seconds (and more
 *         memory),
 */
public class ODMatrixFileWriterRFormat implements IODMatrixFileWriter {

	protected BufferedWriter output;

	public void closeFile() throws IOException {
		output.flush();
		output.close();
	}

	public void openFile(File f) throws IOException {
		output = new BufferedWriter(new FileWriter(f));
	}

	public void saveDist(int i, int j, double cost, double length)
			throws IOException {
		long secs = Math.round(cost);
		String strAux = secs + " ";
		output.write(strAux);
	}

	public String getFormatDescription() {
		return "R_format";
	}

	public String getFileExtension() {
		return ".dat";
	}
}
