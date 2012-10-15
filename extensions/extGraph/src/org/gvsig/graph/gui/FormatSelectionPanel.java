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
 
/**
 * 
 */
package org.gvsig.graph.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JButton;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;

class FormatSelectionPanel extends JPanel implements IWindow {

	/**
	 * 
	 */
	FormatSelectionPanelData data = new FormatSelectionPanelData(null,
			false);

	public FormatSelectionPanel(String introductoryText) {
		super(new BorderLayout());
		String[] formatos = { "SHP", "POSTGIS" }; //, "GML" };
		data.setFormats(new JComboBox(formatos));
		GridBagLayoutPanel contentPanel =
			new GridBagLayoutPanel();
		contentPanel.addComponent(new JLabel(introductoryText));
		contentPanel.addComponent(data.getFormats());
		JButton okButton = new JButton(PluginServices.getText(null,
				"Aceptar"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setIsOkButtonPressed(true);
				close();
			}
		});
		contentPanel.addComponent(okButton);

		add(contentPanel);
	}

	protected void setIsOkButtonPressed(boolean b) {
		this.data.setOkButtonPressed(b);
		
	}

	void close() {
		PluginServices.getMDIManager().closeWindow(this);
	}

	public String getSelectedFormat() {
		return (String) data.getFormats().getSelectedItem();
	}

	public WindowInfo getWindowInfo() {
		if (data.getWi() == null) {
			data.setWi(new WindowInfo(WindowInfo.MODALDIALOG | WindowInfo.PALETTE));
			data.getWi().setTitle(PluginServices
					.getText(null, "Format_selection"));// Internacionalizar
															// esto
			data.getWi().setWidth(400);
			data.getWi().setHeight(55);
		}
		return data.getWi();
	}

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	public boolean isOkButtonPressed() {
		return data.isOkButtonPressed();
	}

}
