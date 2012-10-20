/*
 * Created on 03-mar-2006
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
 * $Id: GeoProcessingConvexHullPanel.java 24989 2008-11-12 12:28:13Z jcampos $
 * $Log$
 * Revision 1.7  2007-03-06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.6  2006/08/29 07:56:30  cesar
 * Rename the *View* family of classes to *Window* (ie: SingletonView to SingletonWindow, ViewInfo to WindowInfo, etc)
 *
 * Revision 1.5  2006/08/29 07:13:56  cesar
 * Rename class com.iver.andami.ui.mdiManager.View to com.iver.andami.ui.mdiManager.IWindow
 *
 * Revision 1.4  2006/08/11 16:20:24  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/07/21 09:10:34  azabala
 * fixed bug 608: user doesnt enter any result file to the geoprocess panel
 *
 * Revision 1.2  2006/06/29 17:32:10  azabala
 * result layer selection is congruent with AbstractGeoprocessPanel
 *
 * Revision 1.1  2006/06/20 18:20:45  azabala
 * first version in cvs
 *
 * Revision 1.2  2006/05/24 23:19:10  azabala
 * arreglado bug que hacia que falle el geoprocessing cuando el fichero resultado no acaba en shp
 *
 * Revision 1.1  2006/05/24 21:13:44  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.5  2006/04/11 18:02:42  azabala
 * Intento de que el Panel salga bien en todas las resoluciones (y de dejarlos mas bonitos)
 *
 * Revision 1.4  2006/03/28 16:25:53  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/03/21 19:26:08  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/03/09 17:04:15  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/03/05 19:56:06  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.convexhull.gui;

import java.awt.Component;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessPanel;
import com.iver.utiles.GenericFileFilter;

public class GeoProcessingConvexHullPanel extends AbstractGeoprocessPanel
		implements IWindow, ConvexHullPanelIF {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel = null;
	private JCheckBox selectedOnlyCheckbox = null;
	private JButton openFileButton = null;
	private WindowInfo viewInfo;
	private JPanel resultLayerPanel = null;
	private JLabel jLabel1 = null;
	private JPanel inputLayerPanel = null;
	private JLabel jLabel2 = null;

	/**
	 * This constructor initializes the set of layers
	 */
	public GeoProcessingConvexHullPanel(FLayers layers) {
		super();
		this.layers = layers;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		jLabel = new JLabel();
		this.setLayout(null);
		this.setBounds(new java.awt.Rectangle(0, 0, 486, 377));
		jLabel.setText("Convex_Hull._Introduccion_de_datos");
		jLabel.setBounds(10, 26, 426, 21);
		this.add(jLabel, null);
		this.add(getLayersComboBox(), null);
		this.add(getResultLayerPanel(), null);
		this.add(getInputLayerPanel(), null);
		layersComboBox.setSelectedIndex(0);
		initSelectedItemsJCheckBox();
	}

	private void initSelectedItemsJCheckBox() {
		String selectedLayer = (String) layersComboBox.getSelectedItem();
		FLyrVect inputLayer = (FLyrVect) layers.getLayer(selectedLayer);
		FBitSet fBitSet = null;
		try {
			fBitSet = inputLayer.getRecordset().getSelection();
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (fBitSet.cardinality() == 0) {
			selectedOnlyCheckbox.setEnabled(false);
		} else {
			selectedOnlyCheckbox.setEnabled(true);
		}
		selectedOnlyCheckbox.setSelected(false);
	}

	private JCheckBox getSelectedOnlyCheckBox() {
		if (selectedOnlyCheckbox == null) {
			selectedOnlyCheckbox = new JCheckBox();
			selectedOnlyCheckbox.setText(PluginServices.getText(this,
					"Usar_solamente_los_elementos_seleccionados"));
			selectedOnlyCheckbox.setBounds(new java.awt.Rectangle(10, 72, 300,
					24));
		}
		return selectedOnlyCheckbox;
	}

	/**
	 * This method initializes layersComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getLayersComboBox() {
		if (layersComboBox == null) {
			layersComboBox = new JComboBox();
			DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(
					getLayerNames());
			layersComboBox.setModel(defaultModel);
			layersComboBox.setBounds(142, 26, 123, 23);
			layersComboBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					// Cambiar el estado del CheckBox
					initSelectedItemsJCheckBox();
				}
			});
		}
		return layersComboBox;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.geoprocess.impl.convexhull.gui.ConvexHullPanelIF#
	 * getFileNameResultTextField()
	 */
	public JTextField getFileNameResultTextField() {
		if (fileNameResultTextField == null) {
			super.getFileNameResultTextField().setBounds(
					new java.awt.Rectangle(135, 11, 169, 21));
		}
		return fileNameResultTextField;
	}

	/**
	 * This method initializes openFileButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOpenFileButton() {
		if (openFileButton == null) {
			openFileButton = new JButton();
			openFileButton.setText(PluginServices.getText(this, "Abrir"));
			openFileButton.setBounds(new java.awt.Rectangle(311, 12, 101, 21));
			openFileButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							openResultFile();
						}
					});
		}
		return openFileButton;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.geoprocess.impl.convexhull.gui.ConvexHullPanelIF#
	 * openResultFile()
	 */
	public void openResultFile() {
		JFileChooser jfc = new JFileChooser();
		jfc.addChoosableFileFilter(new GenericFileFilter("shp", "Ficheros SHP"));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.geoprocess.impl.convexhull.gui.ConvexHullPanelIF#
	 * getInputLayer()
	 */
	public FLyrVect getInputLayer() {
		FLyrVect solution = null;
		String selectedLayer = (String) layersComboBox.getSelectedItem();
		solution = (FLyrVect) layers.getLayer(selectedLayer);
		return solution;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.geoprocess.impl.convexhull.gui.ConvexHullPanelIF#
	 * isConvexHullOnlySelected()
	 */
	public boolean isConvexHullOnlySelected() {
		return selectedOnlyCheckbox.isSelected();
	}

	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.MODALDIALOG);
			viewInfo.setTitle(PluginServices.getText(this, "Convex_Hull"));
		}
		return viewInfo;
	}

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	/**
	 * This method initializes resultLayerPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getResultLayerPanel() {
		if (resultLayerPanel == null) {
			jLabel1 = new JLabel();
			jLabel1.setBounds(new java.awt.Rectangle(4, 13, 126, 17));
			jLabel1.setText(PluginServices.getText(this, "Cobertura_de_salida")
					+ ":");
			resultLayerPanel = new JPanel();
			resultLayerPanel.setLayout(null);
			resultLayerPanel
					.setBounds(new java.awt.Rectangle(11, 200, 426, 39));
			resultLayerPanel
					.setBorder(javax.swing.BorderFactory
							.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
			resultLayerPanel.add(getOpenFileButton(), null);
			resultLayerPanel.add(getFileNameResultTextField(), null);
			resultLayerPanel.add(jLabel1, null);
		}
		return resultLayerPanel;
	}

	/**
	 * This method initializes inputLayerPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getInputLayerPanel() {
		if (inputLayerPanel == null) {
			jLabel2 = new JLabel();
			jLabel2.setBounds(new java.awt.Rectangle(11, 26, 149, 23));
			jLabel2.setText(PluginServices
					.getText(this, "Cobertura_de_entrada") + ":");
			inputLayerPanel = new JPanel();
			inputLayerPanel.setLayout(null);
			inputLayerPanel.setBounds(new java.awt.Rectangle(10, 60, 428, 133));
			inputLayerPanel
					.setBorder(javax.swing.BorderFactory
							.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
			inputLayerPanel.add(getSelectedOnlyCheckBox(), null);
			inputLayerPanel.add(jLabel2, null);
			inputLayerPanel.add(getLayersComboBox(), null);
		}
		return inputLayerPanel;
	}

} // @jve:decl-index=0:visual-constraint="15,20"
