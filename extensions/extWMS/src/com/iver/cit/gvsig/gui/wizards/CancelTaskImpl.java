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
package com.iver.cit.gvsig.gui.wizards;

import org.gvsig.remoteClient.wms.ICancellable;

/**
 * All tasks which use libRemoteServices need an object that implements ICancellable
 * to cancel processes.
 * @author Nacho Brodin (nachobrodin@gmail.com)
 */
public class CancelTaskImpl implements ICancellable {
	private boolean       cancel = false;
	
	public CancelTaskImpl() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gvsig.compat.net.ICancellable#getID()
	 */
	public Object getID() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.compat.net.ICancellable#isCanceled()
	 */
	public boolean isCanceled() {
		return cancel;
	}
	
	/**
	 * Cancel the task
	 * @param cancel
	 */
	public void setCanceled(boolean cancel) {
		this.cancel = cancel;
	}
}
