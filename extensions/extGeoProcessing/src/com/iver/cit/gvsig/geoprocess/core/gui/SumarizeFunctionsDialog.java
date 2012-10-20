/*
 * Created on 24-feb-2006
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
 * $Id: SumarizeFunctionsDialog.java 10626 2007-03-06 16:55:54Z caballero $
 * $Log$
 * Revision 1.2  2007-03-06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.1  2006/05/24 21:13:09  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.5  2006/04/11 18:02:42  azabala
 * Intento de que el Panel salga bien en todas las resoluciones (y de dejarlos mas bonitos)
 *
 * Revision 1.4  2006/04/07 19:00:58  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/03/26 20:02:08  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/03/05 19:53:25  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/26 20:52:13  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.geoprocess.core.fmap.AverageFunction;
import com.iver.cit.gvsig.geoprocess.core.fmap.MaxFunction;
import com.iver.cit.gvsig.geoprocess.core.fmap.MinFunction;
import com.iver.cit.gvsig.geoprocess.core.fmap.SumFunction;
import com.iver.cit.gvsig.geoprocess.core.fmap.SummarizationFunction;

/**
 * Component to select sumarization functions to numeric fields.
 * 
 * @author azabala
 * 
 *         FIXME Internationalize texts
 * 
 */
public class SumarizeFunctionsDialog extends JDialog implements
		SumarizeFuntDialogIF {
	private static final long serialVersionUID = 6085468284091604644L;
	private JPanel jContentPane = null;
	private JPanel maxJPanel = null;
	private JLabel maxLabel = null;
	private JCheckBox maxCheckBox = null;
	private JPanel jPanel = null;
	private JLabel minLabel = null;
	private JCheckBox minCheckBox = null;
	private JPanel jPanel1 = null;
	private JLabel avgLabel = null;
	private JCheckBox avgCheckBox = null;
	private JPanel jPanel2 = null;
	private JLabel sumLabel = null;
	private JCheckBox sumCheckBox = null;
	private JButton okButton = null;

	private List selectedFunctions;

	/**
	 * This is the default constructor
	 */
	public SumarizeFunctionsDialog() {
		super((JFrame) PluginServices.getMainFrame(), true);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(241, 161);
		String title = PluginServices.getText(this, "Funciones_Sumarizacion");
		this.setTitle(title);
		this.setContentPane(getJContentPane());
		selectedFunctions = new ArrayList();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.insets = new java.awt.Insets(7, 79, 21, 73);
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.ipadx = 2;
			gridBagConstraints5.gridwidth = 2;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.insets = new java.awt.Insets(7, 14, 7, 27);
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.ipadx = 8;
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new java.awt.Insets(18, 13, 6, 29);
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.ipadx = -1;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new java.awt.Insets(6, 31, 8, 12);
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.ipadx = 2;
			gridBagConstraints2.gridx = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new java.awt.Insets(18, 29, 6, 12);
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.gridx = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getMaxJPanel(), gridBagConstraints1);
			jContentPane.add(getJPanel(), gridBagConstraints2);
			jContentPane.add(getJPanel1(), gridBagConstraints3);
			jContentPane.add(getJPanel2(), gridBagConstraints4);
			jContentPane.add(getOkButton(), gridBagConstraints5);
		}
		return jContentPane;
	}

	/**
	 * This method initializes maxJPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMaxJPanel() {
		if (maxJPanel == null) {
			maxLabel = new JLabel();
			String max = PluginServices.getText(this, "Maximo");
			maxLabel.setText(max);
			maxJPanel = new JPanel();
			maxJPanel.add(maxLabel, null);
			maxJPanel.add(getMaxCheckBox(), null);
		}
		return maxJPanel;
	}

	/**
	 * This method initializes maxCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getMaxCheckBox() {
		if (maxCheckBox == null) {
			maxCheckBox = new JCheckBox();
		}
		return maxCheckBox;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			minLabel = new JLabel();
			String min = PluginServices.getText(this, "Minimo");
			minLabel.setText(min);
			jPanel = new JPanel();
			jPanel.add(minLabel, null);
			jPanel.add(getMinCheckBox(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes minCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getMinCheckBox() {
		if (minCheckBox == null) {
			minCheckBox = new JCheckBox();
		}
		return minCheckBox;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			avgLabel = new JLabel();
			String media = PluginServices.getText(this, "Media");
			avgLabel.setText(media);
			jPanel1 = new JPanel();
			jPanel1.add(avgLabel, null);
			jPanel1.add(getJCheckBox(), null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBox() {
		if (avgCheckBox == null) {
			avgCheckBox = new JCheckBox();
		}
		return avgCheckBox;
	}

	/**
	 * This method initializes jPanel2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			sumLabel = new JLabel();
			String sum = PluginServices.getText(this, "Sum");
			sumLabel.setText(sum);
			jPanel2 = new JPanel();
			jPanel2.add(sumLabel, null);
			jPanel2.add(getJCheckBox2(), null);
		}
		return jPanel2;
	}

	/**
	 * This method initializes jCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBox2() {
		if (sumCheckBox == null) {
			sumCheckBox = new JCheckBox();
		}
		return sumCheckBox;
	}

	/**
	 * This method initializes okButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			String ok = PluginServices.getText(this, "Aceptar");
			okButton.setText(ok);
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					ok();
				}
			});
		}
		return okButton;
	}

	public void ok() {

		if (minCheckBox.isSelected()) {
			selectedFunctions.add(new MinFunction());
		}
		if (maxCheckBox.isSelected()) {
			selectedFunctions.add(new MaxFunction());
		}
		if (avgCheckBox.isSelected()) {
			selectedFunctions.add(new AverageFunction());
		}
		if (sumCheckBox.isSelected()) {
			selectedFunctions.add(new SumFunction());
		}
		this.dispose();

	}

	public SummarizationFunction[] getFunctions() {
		SummarizationFunction[] solution = new SummarizationFunction[selectedFunctions
				.size()];
		selectedFunctions.toArray(solution);
		return solution;

	}

	public void resetCheckbox() {
		minCheckBox.setSelected(false);
		maxCheckBox.setSelected(false);
		avgCheckBox.setSelected(false);
		sumCheckBox.setSelected(false);
		selectedFunctions.clear();

	}

} // @jve:decl-index=0:visual-constraint="76,15"
