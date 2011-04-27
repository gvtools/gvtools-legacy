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
 
package org.gvsig.graph.gui;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.gvsig.graph.IODMatrixFileWriter;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.solvers.OneToManySolver;

import com.iver.andami.PluginServices;
import com.iver.utiles.swing.threads.AbstractMonitorableTask;

public class ODMatrixTask extends AbstractMonitorableTask{

	private Network net;
	private GvFlag[] originFlags;
	private GvFlag[] destinationFlags;
	private File generatedFile;
	private IODMatrixFileWriter selectedWriter;

	public ODMatrixTask(Network net, GvFlag[] originFlags,
			GvFlag[] destinationFlags, File generatedFile, 
			IODMatrixFileWriter writer) {
		this.net = net;
		this.originFlags = originFlags;
		this.destinationFlags = destinationFlags;
		this.generatedFile = generatedFile;
		this.selectedWriter = writer;
		
		setInitialStep(0);
		setDeterminatedProcess(true);
		setStatusMessage(PluginServices.getText(this, "calculating odmatrix"));

		setFinalStep(originFlags.length); // Importante: Fijarlo en el constructor, para que esté establecido antes del run() <br>
		
	}
	
	public void run() throws Exception {
		try {				
			selectedWriter.openFile(generatedFile);

			OneToManySolver solver = new OneToManySolver();
			solver.setNetwork(net);
			solver.putDestinationsOnNetwork(destinationFlags);
			solver.setExploreAllNetwork(true);
			for (int i=0; i < originFlags.length; i++)
			{				
				if (isCanceled())
					break;
				
				solver.setSourceFlag(originFlags[i]);
				long t1 = System.currentTimeMillis();
				
				solver.calculate();
				long t2 = System.currentTimeMillis();
				System.out.println("Punto " + i + " de " + originFlags.length + ". " + (t2-t1) + " msecs.");
				
				for (int j=0; j < destinationFlags.length; j++)
				{
					selectedWriter.saveDist(i, j, destinationFlags[j].getCost()
							, destinationFlags[j].getAccumulatedLength());
				}
				long t3 = System.currentTimeMillis();
				System.out.println("T. de escritura: " + (t3-t2) + " msecs.");
				setNote(PluginServices.getText(this, "origin_odmatrix") 
						+ " " + (i +1) + 
						": " + destinationFlags.length + " " + 
						PluginServices.getText(this, "destinations_odmatrix_written") +
						" " + (t3-t1) + " msecs"
						) ; 
				reportStep();	
			}
			solver.removeDestinationsFromNetwork(destinationFlags);
			solver.removeDestinationsFromNetwork(originFlags);
			selectedWriter.closeFile();
			net.removeFlags();
			if (!isCanceled())
				JOptionPane.showMessageDialog((Component) PluginServices.getMainFrame(),
					PluginServices.getText(this,"file_generated"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}

