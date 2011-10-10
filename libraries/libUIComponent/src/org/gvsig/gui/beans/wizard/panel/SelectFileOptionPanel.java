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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.gvsig.gui.beans.openfile.FileTextField;

/**
 * <p>
 * This panel implements a panel to select a file that can be added to a wizard.
 * </p>
 * @author <a href="mailto:jpiera@gvsig.org">Jorge Piera Llodr&aacute;</a>
 */
public class SelectFileOptionPanel extends JPanel implements DocumentListener{
	private JLabel fileLabel;
	private FileTextField fileTextField;
	private javax.swing.JPanel northPanel;

	public SelectFileOptionPanel(String fileText) {
		super();		
		initComponents();
		if (fileText != null){
		    fileLabel.setText(fileText + ":");
		}
		initListeners();
	}
	
    private void initListeners() {	
		Object obj = fileTextField.getComponent(0);
		if ((obj != null) && (obj instanceof JTextField)) {
			((JTextField)obj).getDocument().addDocumentListener(this);	
		}
	}

	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		northPanel = new JPanel();
		fileLabel = new JLabel();
		fileTextField = new FileTextField();

		setLayout(new BorderLayout());

		northPanel.setLayout(new GridBagLayout());

		northPanel.add(fileLabel, new GridBagConstraints());

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		northPanel.add(fileTextField, gridBagConstraints);

		add(northPanel, BorderLayout.NORTH);
	}

	public File getSelectedFile(){
		return fileTextField.getSelectedFile();
	}

	public String getSelectedFileName(){
		return fileTextField.getSelectedFile().getAbsolutePath();
	}

	protected void checkNextButtonEnabled(){

	}

	public void changedUpdate(DocumentEvent e) {
		checkNextButtonEnabled();		
	}

	public void insertUpdate(DocumentEvent e) {
		checkNextButtonEnabled();			
	}

	public void removeUpdate(DocumentEvent e) {
		checkNextButtonEnabled();			
	}   
}

