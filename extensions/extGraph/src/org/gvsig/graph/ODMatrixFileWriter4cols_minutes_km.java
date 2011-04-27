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

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

public class ODMatrixFileWriter4cols_minutes_km extends ODMatrixFileWriter4cols {
	
	private NumberFormat nf = NumberFormat.getInstance();
	
	public void openFile(File f) throws IOException {
		super.openFile(f);
		nf.setMaximumFractionDigits(1);
		
	}

	public void saveDist(int i, int j, double cost, double length) throws IOException {
		double minutes = cost / 60.0;
		double km = length / 1000.0;
		String m = nf.format(minutes);
		String k = nf.format(km);
		String strAux = i + "\t" + j + "\t" + m + "\t" + k;
		output.write(strAux);
		output.newLine();

		
	}

	public String getFormatDescription() {
		return "i_j_minutes_kilometers";
	}

	public String getFileExtension() {
		return ".txt";
	}
}

