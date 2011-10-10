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
* 2010 {Prodevelop}   {Task}
*/
 
package org.gvsig.gui.beans.wizard.panel;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * <p>
 * This panel implements a progress bar that can be added to a wizard.
 * <p>
 * @author <a href="mailto:jpiera@gvsig.org">Jorge Piera Llodr&aacute;</a>
 */
public class ProgressOptionPanel extends JPanel {
	private JLabel pluginLabel;
	private JProgressBar progressBar;
	private JLabel progressLabel;

	public ProgressOptionPanel() {
		super();		
		initComponents();		
	}

	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new GridBagLayout());
		
		progressBar = new JProgressBar();
		progressLabel = new JLabel();
		pluginLabel = new JLabel();

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		northPanel.add(progressBar, gridBagConstraints);

		progressLabel.setFont(new Font("DejaVu Sans", 0, 11)); // NOI18N
		progressLabel.setText("task");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(5, 2, 2, 2);
		northPanel.add(progressLabel, gridBagConstraints);

		pluginLabel.setText("plugin");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(5, 2, 2, 2);
		northPanel.add(pluginLabel, gridBagConstraints);
		
	    setLayout(new BorderLayout());
		add(northPanel, BorderLayout.NORTH);
	}

	/**
	 * @param plugin the plugin to set
	 */
	public void setMainText(String plugin) {
		this.pluginLabel.setText(plugin);
	}	
	
	public void setSecondaryText(String plugin) {
		this.progressLabel.setText(plugin);
	}		
	
	public void setProgress(int progress){
		progressBar.setValue(progress);
	}	
	
	public void setExceptionText(String text, Exception e) {
		progressLabel.setText(text);		
	}
}


