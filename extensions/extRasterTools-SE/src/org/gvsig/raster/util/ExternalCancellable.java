/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2009 IVER T.I. and Generalitat Valenciana.
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 */
package org.gvsig.raster.util;

import org.gvsig.gui.beans.incrementabletask.IncrementableTask;
import org.gvsig.raster.dataset.io.IExternalCancellable;

public class ExternalCancellable implements IExternalCancellable {
	private IncrementableTask task = null;
	
	public ExternalCancellable(IncrementableTask task) {
		this.task = task;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gvsig.raster.dataset.io.IExternalCancellable#processFinalize()
	 */
	public void processFinalize() {
		if(task != null)
			task.processFinalize();
	}
}
