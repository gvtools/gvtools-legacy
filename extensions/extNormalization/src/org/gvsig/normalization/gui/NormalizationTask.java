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
 * 2008 Prodevelop S.L. main development
 */

package org.gvsig.normalization.gui;

import org.apache.log4j.Logger;
import org.gvsig.normalization.operations.Normalization;

import com.iver.andami.PluginServices;
import com.iver.utiles.swing.threads.AbstractMonitorableTask;

/**
 * Normalization task
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicent Sanjaime Calvet</a>
 */
public class NormalizationTask extends AbstractMonitorableTask {

	private Logger logger = PluginServices.getLogger();
	private Normalization normalization;
	private long finalStep;

	/**
	 * @param na
	 * @param normPanelModel
	 *            panel model
	 * 
	 */
	public NormalizationTask(Normalization na, INormPanelModel normPanelModel) {

		this.normalization = na;

		// configure task
		setInitialStep(0);
		setStatusMessage(PluginServices.getText(this, "Normalizating"));
		setDeterminatedProcess(true);
		finalStep = normalization.getEstimatedRowCount();
		setFinalStep((int) finalStep);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.utiles.swing.threads.ITask#run()
	 */
	public void run() throws Exception {
		String log = PluginServices.getText(this, "chains_normalizated");
		int firstRow = normalization.getPattern().getNofirstrows() == 0 ? 1
				: normalization.getPattern().getNofirstrows() + 1;
		for (int i = firstRow; i < finalStep + 1; i++) {
			if (!isCanceled()) {
				setCurrentStep(i);
				setNote(i + " " + log);
				logger.debug("Position: " + (i - 1));
				normalization.fillRow(i - 1);
			} else {
				return;
			}
		}
		normalization.postProcess();
		normalization.removeAllListeners();
	}
}
