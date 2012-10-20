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
 * 2010 Software Colaborativo (www.scolab.es)   development
 */

package org.gvsig.graph.gui;

import javax.swing.JComboBox;

import com.iver.andami.ui.mdiManager.WindowInfo;

public class FormatSelectionPanelData {
	private JComboBox formats;
	private WindowInfo wi;
	private boolean isOkButtonPressed;

	public FormatSelectionPanelData(WindowInfo wi, boolean isOkButtonPressed) {
		this.wi = wi;
		this.isOkButtonPressed = isOkButtonPressed;
	}

	public JComboBox getFormats() {
		return formats;
	}

	public void setFormats(JComboBox formats) {
		this.formats = formats;
	}

	public WindowInfo getWi() {
		return wi;
	}

	public void setWi(WindowInfo wi) {
		this.wi = wi;
	}

	public boolean isOkButtonPressed() {
		return isOkButtonPressed;
	}

	public void setOkButtonPressed(boolean isOkButtonPressed) {
		this.isOkButtonPressed = isOkButtonPressed;
	}
}
