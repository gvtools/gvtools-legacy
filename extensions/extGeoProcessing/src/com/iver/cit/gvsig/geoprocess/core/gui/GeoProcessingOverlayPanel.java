/*
 * Created on 04-jul-2005
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
package com.iver.cit.gvsig.geoprocess.core.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.layerOperations.SingleLayer;
import com.iver.utiles.GenericFileFilter;
/**
 * Panel that allows user to enter clip geoprocess params
 * (input layers, etc).
 *
 * @author jmorell, azabala
 */
public class GeoProcessingOverlayPanel extends AbstractGeoprocessPanel
							implements OverlayPanelIF, IWindow{

	private static final long serialVersionUID = 1L;
	private JButton openFileDialogButton = null;
	private JCheckBox selectedFirstLayerCheckBox = null;
	private JComboBox secondLayerComboBox = null;
	private JCheckBox secondLayerSelectedCheckBox = null;
	private JLabel jLabel = null;
	/**
     * Text that describes which type of overlay
     * operation will launch this panel
     */
	private String titleText;

	private WindowInfo viewInfo;
	private JPanel resultLayerPanel = null;
	private JLabel jLabel7 = null;
	private JPanel clipLayerjPanel = null;
	private JLabel jLabel5 = null;
	private JLabel jLabel6 = null;
	private JLabel jLabel4 = null;
	private JPanel inputLayerPanel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;

	/**
	 * This constructor initializes the set of layers
	 */
	public GeoProcessingOverlayPanel(FLayers layers, String titleText) {
		super();
		this.layers = layers;
		this.titleText = titleText;
		initialize();
	}
	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private  void initialize() {
		jLabel = new JLabel();
		this.setLayout(null);
		this.setBounds(new java.awt.Rectangle(0,0,486,377));
		jLabel.setText(titleText);
		//this.add(getFirstLayerComboBox(), null);
		jLabel.setBounds(new java.awt.Rectangle(8,20,423,26));
		this.add(getInputLayerPanel(), null);
		this.add(jLabel, null);
		this.add(getResultLayerPanel(), null);
		this.add(getClipLayerjPanel(), null);
        changeSelectedItemsJCheckBox();
        changeSelectedItemsJCheckBox1();
        changeSelectedItemsNumberJLabel();
        changeSelectedItemsNumberJLabel1();
	}

    private void changeSelectedItemsJCheckBox() {
        FLyrVect inputSelectable = (FLyrVect)(layers.getLayer((String)layersComboBox.getSelectedItem()));
        FBitSet fBitSet = null;
		try {
			fBitSet = inputSelectable.getRecordset().getSelection();
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (fBitSet.cardinality()==0) {
            selectedFirstLayerCheckBox.setEnabled(false);
            selectedFirstLayerCheckBox.setSelected(false);
        } else {
            selectedFirstLayerCheckBox.setEnabled(true);
            selectedFirstLayerCheckBox.setSelected(true);
        }
    }
    private void changeSelectedItemsJCheckBox1() {
        FLyrVect inputSelectable = (FLyrVect)(layers.getLayer((String)secondLayerComboBox.getSelectedItem()));
        FBitSet fBitSet = null;
		try {
			fBitSet = inputSelectable.getRecordset().getSelection();
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (fBitSet.cardinality()==0) {
            secondLayerSelectedCheckBox.setEnabled(false);
            secondLayerSelectedCheckBox.setSelected(false);
        } else {
            secondLayerSelectedCheckBox.setEnabled(true);
            secondLayerSelectedCheckBox.setSelected(true);
        }
    }
    private void changeSelectedItemsNumberJLabel() {
        if (getOnlyFirstLayerSelectedCheckBox().isSelected()) {
            FLyrVect inputSelectable = (FLyrVect)(layers.getLayer((String)layersComboBox.getSelectedItem()));
            FBitSet fBitSet = null;
			try {
				fBitSet = inputSelectable.getRecordset().getSelection();
			} catch (ReadDriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            jLabel3.setText(new Integer(fBitSet.cardinality()).toString());
        } else {
        	ReadableVectorial va = ((SingleLayer)(layers.
        			getLayer((String)layersComboBox.
        					getSelectedItem()))).
        							getSource();
            try {
                jLabel3.setText(new Integer(va.getShapeCount()).toString());
            } catch (ReadDriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    private void changeSelectedItemsNumberJLabel1() {
        if (getOnlySecondLayerSelectedCheckBox().isSelected()) {
            FLyrVect inputSelectable = (FLyrVect)(layers.getLayer((String)secondLayerComboBox.getSelectedItem()));
            FBitSet fBitSet = null;
			try {
				fBitSet = inputSelectable.getRecordset().getSelection();
			} catch (ReadDriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            jLabel6.setText(new Integer(fBitSet.cardinality()).toString());
        } else {
        	ReadableVectorial va = ((SingleLayer)(layers.getLayer(
        			(String)secondLayerComboBox.getSelectedItem())))
        			.getSource();
            try {
                jLabel6.setText(new Integer(va.getShapeCount()).toString());
            } catch (ReadDriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

	/**
	 *
	 * TODO
	 * El panel de seleccion de escritura esta metido a pelo en todas
	 * las pantallas. Hacer que sea generico, y vinculado a los IWriters
	 * cargados en el sistema.
	 *
	 *
	 */
	private JButton getOpenFileDialogButton() {
		if (openFileDialogButton == null) {
			openFileDialogButton = new JButton();
			openFileDialogButton.setText(PluginServices.getText(this,"Abrir"));
			openFileDialogButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
                    openResultFileDialog();
				}
			});
		}
		return openFileDialogButton;
	}

	/**
	 * This method initializes selectedFirstLayerCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getOnlyFirstLayerSelectedCheckBox() {
		if (selectedFirstLayerCheckBox == null) {
			selectedFirstLayerCheckBox = new JCheckBox();
			selectedFirstLayerCheckBox.setText(PluginServices.getText(this,"Usar_solamente_los_elementos_seleccionados"));
			selectedFirstLayerCheckBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
                    changeSelectedItemsNumberJLabel();
				}
			});
		}
		return selectedFirstLayerCheckBox;
	}
	/**
	 * This method initializes secondLayerComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getSecondLayerComboBox() {
		if (secondLayerComboBox == null) {
			secondLayerComboBox = new JComboBox();
            DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(getLayerNames());
            secondLayerComboBox.setModel(defaultModel);
			secondLayerComboBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
                    changeSelectedItemsJCheckBox1();
                    changeSelectedItemsNumberJLabel1();
				}
			});
		}
		return secondLayerComboBox;
	}
	/**
	 * This method initializes secondLayerSelectedCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getOnlySecondLayerSelectedCheckBox() {
		if (secondLayerSelectedCheckBox == null) {
			secondLayerSelectedCheckBox = new JCheckBox();
			secondLayerSelectedCheckBox.setText(PluginServices.getText(this,"Usar_solamente_los_elementos_seleccionados"));
			secondLayerSelectedCheckBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
                    changeSelectedItemsNumberJLabel1();
				}
			});
		}
		return secondLayerSelectedCheckBox;
	}

	public FLyrVect getSecondLayer() {
		String clippingLayer =
			(String) secondLayerComboBox.getSelectedItem();
		try {
			return (FLyrVect) layers.getLayer(clippingLayer);
		} catch (Exception e) {
			return null;
		}
	}

	public boolean onlyFirstLayerSelected() {
		return selectedFirstLayerCheckBox.isSelected();
	}

	public boolean onlySecondLayerSelected() {
		return secondLayerSelectedCheckBox.isSelected();
	}


	//TODO Sustituir esto para que pueda trabajar
	//con los IWriter que estén registrados en el sistema
	public void openResultFileDialog() {
		JFileChooser jfc = new JFileChooser();
        jfc.addChoosableFileFilter(
        		new GenericFileFilter("shp", PluginServices.getText(this, "Ficheros_SHP")));
        if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            if (!(file.getPath().endsWith(".shp") || file.getPath().endsWith(".SHP"))){
                file = new File(file.getPath()+".shp");
            }
        }
        if (jfc.getSelectedFile()!=null)
        	getFileNameResultTextField().setText(jfc.getSelectedFile().getAbsolutePath());
	}

	//TODO Rehacer esto
	public void firstLayerSelectionChecked(boolean checked) {
		changeSelectedItemsJCheckBox();
        changeSelectedItemsNumberJLabel();
	}
	public void secondLayerSelectionChecked(boolean checked) {
		changeSelectedItemsJCheckBox1();
		changeSelectedItemsNumberJLabel1();
	}

	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.MODALDIALOG);
			viewInfo.setTitle(PluginServices.getText(this,
					"Operacion_de_overlay"));
		}
		return viewInfo;
	}
	public Object getWindowProfile(){
		return WindowInfo.DIALOG_PROFILE;
	}
	/**
	 * This method initializes resultLayerPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getResultLayerPanel() {
		if (resultLayerPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new java.awt.Insets(9,7,7,34);
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.ipadx = 12;
			gridBagConstraints2.gridx = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.ipadx = 177;
			gridBagConstraints1.ipady = 1;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.insets = new java.awt.Insets(12,6,9,7);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new java.awt.Insets(14,6,11,6);
			gridBagConstraints.gridy = 0;
			gridBagConstraints.ipadx = 2;
			gridBagConstraints.ipady = 1;
			gridBagConstraints.gridx = 0;
			jLabel7 = new JLabel();
			jLabel7.setText(PluginServices.getText(this, "Cobertura_de_salida") + ":");
			resultLayerPanel = new JPanel();
			resultLayerPanel.setLayout(new GridBagLayout());
			resultLayerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
			resultLayerPanel.add(jLabel7, gridBagConstraints);
			resultLayerPanel.add(getFileNameResultTextField(), gridBagConstraints1);
			resultLayerPanel.add(getOpenFileDialogButton(), gridBagConstraints2);
			resultLayerPanel.setBounds(new java.awt.Rectangle(9,295,445,42));
		}
		return resultLayerPanel;
	}
	/**
	 * This method initializes clipLayerjPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getClipLayerjPanel() {
		if (clipLayerjPanel == null) {
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.insets = new java.awt.Insets(17,10,5,5);
			gridBagConstraints12.gridy = 0;
			gridBagConstraints12.ipadx = 24;
			gridBagConstraints12.ipady = 7;
			gridBagConstraints12.gridx = 0;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridwidth = 2;
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.gridy = 0;
			gridBagConstraints11.ipadx = 216;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.insets = new java.awt.Insets(16,5,4,24);
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.insets = new java.awt.Insets(4,5,3,104);
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridy = 1;
			gridBagConstraints10.ipadx = 36;
			gridBagConstraints10.gridwidth = 3;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.insets = new java.awt.Insets(4,7,19,178);
			gridBagConstraints9.gridy = 2;
			gridBagConstraints9.ipadx = 1;
			gridBagConstraints9.gridx = 2;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.insets = new java.awt.Insets(3,11,19,6);
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridy = 2;
			gridBagConstraints8.ipadx = 2;
			gridBagConstraints8.ipady = 1;
			gridBagConstraints8.gridwidth = 2;
			jLabel4 = new JLabel();
			jLabel4.setText(PluginServices.getText(this, "Cobertura_de_recorte") + ":");
			jLabel6 = new JLabel();
			jLabel6.setText("00");
			jLabel5 = new JLabel();
			jLabel5.setText(PluginServices.getText(this, "Numero_de_elementos_seleccionados") + ":");
			clipLayerjPanel = new JPanel();
			clipLayerjPanel.setLayout(new GridBagLayout());
			clipLayerjPanel.setBounds(new java.awt.Rectangle(9,174,445,115));
			clipLayerjPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
			clipLayerjPanel.add(jLabel5, gridBagConstraints8);
			clipLayerjPanel.add(jLabel6, gridBagConstraints9);
			clipLayerjPanel.add(getOnlySecondLayerSelectedCheckBox(), gridBagConstraints10);
			clipLayerjPanel.add(getSecondLayerComboBox(), gridBagConstraints11);
			clipLayerjPanel.add(jLabel4, gridBagConstraints12);
		}
		return clipLayerjPanel;
	}
	/**
	 * This method initializes inputLayerPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getInputLayerPanel() {
		if (inputLayerPanel == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridwidth = 2;
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.ipadx = 223;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.insets = new java.awt.Insets(17,7,0,28);
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.insets = new java.awt.Insets(7,4,7,184);
			gridBagConstraints6.gridy = 2;
			gridBagConstraints6.ipadx = 1;
			gridBagConstraints6.ipady = 9;
			gridBagConstraints6.gridx = 2;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.insets = new java.awt.Insets(6,7,9,3);
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.ipadx = 7;
			gridBagConstraints5.ipady = 8;
			gridBagConstraints5.gridwidth = 2;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.insets = new java.awt.Insets(1,5,5,108);
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.ipadx = 33;
			gridBagConstraints4.gridwidth = 3;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new java.awt.Insets(21,12,5,6);
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.ipadx = 7;
			gridBagConstraints3.gridx = 0;
			jLabel3 = new JLabel();
			jLabel3.setText("00");
			jLabel2 = new JLabel();
			jLabel2.setText(PluginServices.getText(this, "Numero_de_elementos_seleccionados") + ":");
			jLabel1 = new JLabel();
			jLabel1.setText(PluginServices.getText(this, "Cobertura_de_entrada") + ":");
			inputLayerPanel = new JPanel();
			inputLayerPanel.setLayout(new GridBagLayout());
			inputLayerPanel.setBounds(new java.awt.Rectangle(8,56,446,111));
			inputLayerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
			inputLayerPanel.add(jLabel1, gridBagConstraints3);
			inputLayerPanel.add(getOnlyFirstLayerSelectedCheckBox(), gridBagConstraints4);
			inputLayerPanel.add(jLabel2, gridBagConstraints5);
			inputLayerPanel.add(jLabel3, gridBagConstraints6);
			inputLayerPanel.add(getInputLayerComboBox(), gridBagConstraints7);
		}
		return inputLayerPanel;
	}

	/**
	 * This method initializes inputLayerComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getInputLayerComboBox() {
		if (layersComboBox == null) {
			layersComboBox = new JComboBox();
			DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(getLayerNames());
			layersComboBox.setModel(defaultModel);
			layersComboBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
	                changeSelectedItemsJCheckBox();
	                changeSelectedItemsNumberJLabel();
				}
			});
		}
		return layersComboBox;
	}
}  //  @jve:decl-index=0:visual-constraint="57,37"
