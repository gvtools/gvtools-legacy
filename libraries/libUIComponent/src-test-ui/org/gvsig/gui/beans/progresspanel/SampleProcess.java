package org.gvsig.gui.beans.progresspanel;

import org.gvsig.gui.beans.Messages;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanel;
import org.gvsig.gui.beans.incrementabletask.IncrementableProcess;

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
 * Process that adds a layer with derivative geometries, according the
 * configuration.
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class SampleProcess extends IncrementableProcess {
	public SampleProcess(String title, String label) {
		super(title);

		this.label = label;
		this.isPausable = true;
	}

	/**
	 * Sample process. <br>
	 * "cancelProcess" is a shared object that, if you want allow to cancel the
	 * process, you should read it once in a while and check if it's canceled,
	 * if true, throw an InterrupedException
	 * 
	 * @throws InterruptedException
	 *             if fails the process
	 */
	public void process() throws InterruptedException {
		try {
			percentage = 0;

			for (int i = 1; i <= 20; i++) {
				percentage = i * 5;

				if (cancelProcess.isCanceled()) {
					throw new InterruptedException();
				}

				log.addLine("Paso: " + i);
				Thread.sleep(1000);
			}

			percentage = 100;
			log.addLine(Messages.getText("Process_finished_successfully"));
			return;
		} catch (Exception ex) {
			if (!cancelProcess.isCanceled()) {
				// Next line must be uncommented
				// NotificationManager.showMessageError(Messages.getText("Failed_the_process"),
				// ex);
				log.addLine(Messages.getText("Failed_the_process"));
			}

			/* CANCELLATION PROCESS */

			// Must restore the changes
			// ....

			throw new InterruptedException();
		} finally {
			/* Summary of the process */
			log.addLine("    Proceso de ejemplo con barra de progreso durante 20 segundos");

			// Ends the progress panel
			iTask.getButtonsPanel().getButton(ButtonsPanel.BUTTON_ACCEPT)
					.doClick();
		}
	}
}
