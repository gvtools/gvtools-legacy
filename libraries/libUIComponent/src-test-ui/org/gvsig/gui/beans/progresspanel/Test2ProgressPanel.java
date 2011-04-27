package org.gvsig.gui.beans.progresspanel;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import org.apache.log4j.Logger;
import org.gvsig.gui.beans.Messages;
import org.gvsig.gui.beans.incrementabletask.IncrementableTask;


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

/**
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class Test2ProgressPanel {

	public static void main(String[] args) {
		SampleProcess iprocess = new SampleProcess("Proceso de ejemplo",
				Messages.getText("Ongoing_process_please_wait"));

		IncrementableTask iTask = new IncrementableTask(iprocess, new ProgressPanel(false));
		iTask.addIncrementableListener(iprocess);
		iprocess.setIncrementableTask(iTask);

		final SampleProcess f_iprocess = iprocess;
		final IncrementableTask f_iTask = iTask;
		
		iTask.getProgressPanel().addComponentListener(new ComponentAdapter() {
			/*
			 * (non-Javadoc)
			 * @see java.awt.event.ComponentAdapter#componentHidden(java.awt.event.ComponentEvent)
			 */
			public void componentHidden(ComponentEvent e) {
				f_iTask.getProgressPanel().dispose();

				/* Writes in the gvSIG log the results of the process */
				String text = "\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n" +
					"Resumen del proceso:\n" +
					f_iprocess.getLog() +
					"\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n";
				Logger.getLogger(getClass().getName()).debug(text);
			}
		});

		/* Starts the process */
		iprocess.start();
		iTask.start();
	}
}
