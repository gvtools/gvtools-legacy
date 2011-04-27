/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
/* CVS MESSAGES:
*
* $Id: 
* $Log: 
*/
package org.gvsig.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gvsig.gui.beans.swing.GridBagLayoutPanel;

import com.iver.andami.PluginServices;
import com.iver.utiles.GenericFileFilter;

public class ResultFLyrVectPanel extends GridBagLayoutPanel{

	
	private JPanel createFilePanel(){
		Insets insets = new Insets(5, 5, 5, 5);
		JPanel aux = new JPanel(new BorderLayout());
		String resultLayerText = PluginServices.getText(this, "Cobertura_de_salida") + ":";
		JTextField resultTf = getFileNameResultTextField();
		JButton openButton = getOpenResultButton();
	    aux.add(resultTf, BorderLayout.WEST);
	    aux.add(new JLabel(" "), BorderLayout.CENTER);
	    aux.add(openButton, BorderLayout.EAST);
	    addComponent(resultLayerText, aux, GridBagConstraints.HORIZONTAL, insets );
		setBounds(0, 0, 520, 410);
		return aux;
	}
	
	
	
	protected JTextField getFileNameResultTextField() {
		return new JTextField(25);
	}
	
	private JButton getOpenResultButton() {
		JButton	openResultButton = new JButton();
		openResultButton.setText(PluginServices.getText(this, "Abrir"));
		openResultButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					openResultFile();
				}
			}
		);
		return openResultButton;
	}
	
	/**
	 * Opens a dialog to select where (file, database, etc)
	 * to save the result layer.
	 *
	 */
	public void openResultFile() {
		JFileChooser jfc = new JFileChooser();
		jfc
				.addChoosableFileFilter(new GenericFileFilter("shp",
						PluginServices.getText (this,"Ficheros_SHP")));
		if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			if (!(file.getPath().endsWith(".shp") || file.getPath().endsWith(
					".SHP"))) {
				file = new File(file.getPath() + ".shp");
			}
		}// if
		if (jfc.getSelectedFile() != null) {
			getFileNameResultTextField().setText(
					jfc.getSelectedFile().getAbsolutePath());
		}

	}
}
