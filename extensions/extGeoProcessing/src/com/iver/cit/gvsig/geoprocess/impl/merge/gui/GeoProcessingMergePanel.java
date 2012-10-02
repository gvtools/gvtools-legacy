/*
 * Created on 14-jul-2005
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
package com.iver.cit.gvsig.geoprocess.impl.merge.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;

import org.gvsig.gui.beans.swing.JButton;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.drivers.dgn.DgnMemoryDriver;
import com.iver.cit.gvsig.fmap.drivers.dxf.DXFMemoryDriver;
import com.iver.cit.gvsig.fmap.drivers.shp.IndexedShpDriver;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessPanel;
import com.iver.utiles.GenericFileFilter;
/**
 * GUI component to that allows user entrys to launch
 * a Merge geoprocess.
 *
 * @author azabala
 *
 */
public class GeoProcessingMergePanel
			extends AbstractGeoprocessPanel implements MergePanelIF,
			IWindow{

    private static final long serialVersionUID = 1L;
    private JLabel titleJLabel = null;
    private JScrollPane inputJScrollPane = null;
    /**
     * It has a list with all FLyrVect of TOC
     */
    private JList inputJList = null;
    /**
     * Combo box to select which layer of the user selection
     * will preserve its schema
     */
    private JComboBox fieldsJComboBox = null;
    private JButton outputJButton = null;
    private JButton directoryButton = null;

    //layers selected to merge
    private FLyrVect[] layersSelected = null;
    private File outputFile = null;

	//user could specify a directory where look for new layers
    //files to add
	private FLyrVect[] aditionalLayers;
	private String[] inputLayersToAdd;
	private JScrollPane directoryLayerScrollPane = null;
	/**
	 * It has a list with all the FLyrVect in the directory
	 * selected by the user
	 */
	private JList directoryLayerList = null;

	private FLyrVect[] aditionalLayersSelection = null;

	private WindowInfo viewInfo;
	private JPanel resultLayerPanel = null;
	private JLabel jLabel = null;
	private JPanel resultSchemaPanel = null;
	private JLabel jLabel1 = null;
	private JPanel directoryPanel = null;
	private JLabel directoryLabel = null;
	private JPanel inputLyrPanel = null;
	private JLabel jLabel2 = null;
    /**
     * This constructor initializes the set of layers
     */
    public GeoProcessingMergePanel(FLayers layers) {
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
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.insets = new java.awt.Insets(4,7,0,33);
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.ipadx = 445;
        gridBagConstraints4.ipady = 91;
        gridBagConstraints4.gridx = 0;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.insets = new java.awt.Insets(1,7,3,33);
        gridBagConstraints3.gridy = 2;
        gridBagConstraints3.ipadx = 445;
        gridBagConstraints3.ipady = 122;
        gridBagConstraints3.gridx = 0;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.insets = new java.awt.Insets(3,9,1,31);
        gridBagConstraints2.gridy = 3;
        gridBagConstraints2.ipadx = 445;
        gridBagConstraints2.ipady = 35;
        gridBagConstraints2.gridx = 0;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.insets = new java.awt.Insets(1,9,42,31);
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.ipadx = 445;
        gridBagConstraints1.ipady = 35;
        gridBagConstraints1.gridx = 0;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10,8,3,31);
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 266;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.gridx = 0;
        titleJLabel = new JLabel();
        titleJLabel.setText(PluginServices.getText(this,"Juntar._Introduccion_de_datos") + ":");
        this.setLayout(new GridBagLayout());
        this.setSize(486,377);
//        this.setBounds(new java.awt.Rectangle(0,0,486,377));
        this.add(titleJLabel, gridBagConstraints);
        this.add(getResultLayerPanel(), gridBagConstraints1);
        this.add(getResultSchemaPanel(), gridBagConstraints2);
        this.add(getDirectoryPanel(), gridBagConstraints3);
        this.add(getInputLyrPanel(), gridBagConstraints4);
    }

    private void setDirectoryLabelText(String text){
    	directoryLabel.setText(text);
    }

    /**
     * Returns an array with names of all vectorial layers in TOC
     */
    protected String[] getLayerNames() {
    	String[] solution = null;
        ArrayList layerNames = new ArrayList();
        for (int i=0;i<layers.getLayersCount();i++) {
        	FLayer layer = layers.getLayer(i);
        	if(! (layer instanceof FLyrVect))
        		continue;
            layerNames.add(layer.getName());
        }
        solution = new String[layerNames.size()];
        layerNames.toArray(solution);
        return solution;
    }


    protected String[] getDirectoryLayerNames(){
    	String[] solution = null;
        ArrayList layerNames = new ArrayList();
        if(aditionalLayers != null){
        	for(int i = 0; i < aditionalLayers.length; i++){
        		layerNames.add(aditionalLayers[i].getName());
        	}//for
        }
        solution = new String[layerNames.size()];
        layerNames.toArray(solution);
        return solution;
    }

    private String[] getLayersSelectedNames() {
    	if(layersSelected == null && aditionalLayersSelection == null)
    		return null;
    	ArrayList solution = new ArrayList();
    	if(layersSelected != null){
    		for(int i = 0; i < layersSelected.length; i++){
    			solution.add(layersSelected[i].getName());
    		}
    	}
    	if(aditionalLayersSelection != null){
    		for(int i = 0; i < aditionalLayersSelection.length; i++){
    			solution.add(aditionalLayersSelection[i].getName());
    		}
    	}
        String[] layersSelectedNames = new String[solution.size()];
        solution.toArray(layersSelectedNames);
        return layersSelectedNames;
    }

    /**
     * This method initializes inputJScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getInputJScrollPane() {
    	if (inputJScrollPane == null) {
    		inputJScrollPane = new JScrollPane();
    		inputJScrollPane.setBounds(new java.awt.Rectangle(10,27,427,50));
    		inputJScrollPane.setViewportView(getInputJList());
    	}
    	return inputJScrollPane;
    }

    /**
     * This method initializes inputJList
     *
     * @return javax.swing.JList
     */
    private JList getInputJList() {
        if (inputJList == null) {
            inputJList = new JList(getLayerNames());
            LayerSelectorListener layerSelectorListener = new LayerSelectorListener(layers);
            inputJList.addListSelectionListener(layerSelectorListener);
        }
        return inputJList;
    }

    class AditionalLayersListModel extends AbstractListModel{
		private static final long serialVersionUID = 6123487908892908100L;
		private FLyrVect[] layers;

		AditionalLayersListModel(FLyrVect[] layers){
			this.layers = layers;
		}
		public int getSize() {
			if(layers != null){
				return layers.length;
			}
			return 0;
		}

		public Object getElementAt(int position) {
			if(layers != null){
				return layers[position].getName();
			}
			return null;
		}
    }
    /**
     * ListModel for input layer list (which has layers of the TOC)
     * @author azabala
     *
     */
    class LayersListModel extends AbstractListModel{
		private static final long serialVersionUID = 1L;
		private FLayers layers;
		LayersListModel(FLayers layers){
			this.layers = layers;
		}

		public int getSize() {
			return layers.getLayersCount();
		}

		public Object getElementAt(int arg0) {
			if(arg0 < layers.getLayersCount())
				return layers.getLayer(arg0).getName();
			else{
				if(aditionalLayers == null)
					return null;
				else{
					FLyrVect layer = aditionalLayers[arg0 - layers.getLayersCount()];
					if(layer == null)
						return "";
					else
						return layer.getName();
				}//else
			}//else
		}//getElementAt

    }

    /**
     * Listens selection of layers in input layers list, and
     * refresh selected layers list
     * @author azabala
     *
     */
    private class LayerSelectorListener implements ListSelectionListener {
        private FLayers layers;
        public LayerSelectorListener(FLayers layers) {
            this.layers = layers;
        }
        public void valueChanged(javax.swing.event.ListSelectionEvent e) {
        	Object[] layersSelectedNames = inputJList.getSelectedValues();
        	if(layersSelectedNames == null)
        		return;
            layersSelected = new FLyrVect[layersSelectedNames.length];
            for (int i=0;i<layersSelectedNames.length;i++) {
                String layerString = (String) layersSelectedNames[i];
                FLyrVect lyr = (FLyrVect) layers.getLayer(layerString);
                if(lyr != null)
                	layersSelected[i] = lyr;
            }
            //refresh combo box of schema preserve of layers
            DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(getLayersSelectedNames());
            fieldsJComboBox.setModel(defaultModel);
        }//valueChanged
    }


    private class AditionalLayerSelectorListener implements ListSelectionListener {
        private FLyrVect[] aditionalLayers;
        public AditionalLayerSelectorListener(FLyrVect[] aditionalLayers) {
            this.aditionalLayers = aditionalLayers;
        }
        public void valueChanged(javax.swing.event.ListSelectionEvent e) {
        	Object[] layersSelectedNames = directoryLayerList.getSelectedValues();
        	if(layersSelectedNames == null)
        		return;
        	if(this.aditionalLayers == null)
        		return;
            aditionalLayersSelection = new FLyrVect[layersSelectedNames.length];
            for (int i=0;i<layersSelectedNames.length;i++) {
            	String selection = (String) layersSelectedNames[i];
            	for(int j = 0; j < this.aditionalLayers.length; j++){
            		if(this.aditionalLayers[j].getName().equals(selection)){
            			aditionalLayersSelection[i] = this.aditionalLayers[j];
            			break;
            		}//if
            	}//for
            }//for
            //Any time user add a layer to aditional layers list,
            //we create a new comboboxmodel, to add this layers to the
            //schema presevation combo box
            DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(getLayersSelectedNames());
            fieldsJComboBox.setModel(defaultModel);
        }//valueChanged
    }

    /**
     * This method initializes fieldsJComboBox
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox getFieldsJComboBox() {
    	if (fieldsJComboBox == null) {
    		fieldsJComboBox = new JComboBox();
    		fieldsJComboBox.setBounds(new java.awt.Rectangle(225,6,191,23));
    	}
    	return fieldsJComboBox;
    }

    /**
     * This method initializes fileNameResultTextField
     *
     * @return javax.swing.JTextField
     */
    public JTextField getFileNameResultTextField() {
    	if (fileNameResultTextField == null) {
    		super.getFileNameResultTextField().
    			setBounds(new java.awt.Rectangle(134,7,
    									194,22));
    	}
    	return fileNameResultTextField;
    }

    /**
     * This method initializes outputJButton
     *
     * @return javax.swing.JButton
     */
    private JButton getOutputJButton() {
    	if (outputJButton == null) {
    		outputJButton = new JButton();
    		outputJButton.setText(PluginServices.getText(this,"Abrir"));
    		outputJButton.setBounds(new java.awt.Rectangle(334,5,104,26));
            outputJButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                   openResultFileDialog();
                }
            });
    	}
    	return outputJButton;
    }

	/**
	 * This method initializes directoryButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDirectoryButton() {
		if (directoryButton == null) {
			directoryButton = new JButton();
			directoryButton.setText(PluginServices.getText(this,"Seleccionar_Directorio"));
			directoryButton.setBounds(new java.awt.Rectangle(136,91,183,26));
			directoryButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					openDirectoryLayersDialog();
				}});
		}
		return directoryButton;
	}

	public FLyrVect[] getSelectedLayers() {
		FLyrVect[] solution = null;
		String[] selectedNames = this.getLayersSelectedNames();
		if(selectedNames == null){
			return null;
		}
		ArrayList loadedSelectedLayers = new ArrayList();
		for(int i = 0; i < selectedNames.length; i++){
			String name = selectedNames[i];
			FLyrVect layer = (FLyrVect) layers.getLayer(name);
			if(layer == null){
				//it is a directory layer
				for(int j = 0; j < aditionalLayers.length; j++){
					FLyrVect aditional = aditionalLayers[j];
					if(name.equals(aditional.getName())){
						layer = aditional;
					}//
				}//fir
			}//if
			if(layer != null)
				loadedSelectedLayers.add(layer);
		}//for
		solution = new FLyrVect[loadedSelectedLayers.size()];
		loadedSelectedLayers.toArray(solution);
		return solution;
	}


	public void addLayersToMergeList(FLyrVect[] layers) {
		// TODO Auto-generated method stub

	}

	public FLyrVect getSelectedSchema() {
		FLyrVect solution = null;
		String layerName = (String) fieldsJComboBox.getSelectedItem();
		if(layerName == null)
			return null;
		solution = (FLyrVect) layers.getLayer(layerName);
		if (solution == null){
			//see if it is an aditionallayer
			for(int i = 0; i < aditionalLayers.length; i++){
				FLyrVect layer = aditionalLayers[i];
				if(layer.getName() == layerName)
					solution = layer;
			}
		}
		return solution;
	}
   /**
    * This method is called every time user add new layers to directory layers
    * list
    */

	public void layersSelected() {
		 LayerSelectorListener layerSelectorListener = new LayerSelectorListener(layers);
         inputJList.addListSelectionListener(layerSelectorListener);
         LayersListModel model = new LayersListModel(layers);
         inputJList.setModel(model);
         AditionalLayerSelectorListener aditionalListener = new
         	AditionalLayerSelectorListener(aditionalLayers);
         directoryLayerList.addListSelectionListener(aditionalListener);
         AditionalLayersListModel aditionalListModel =
        	 new AditionalLayersListModel(aditionalLayers);
         directoryLayerList.setModel(aditionalListModel);
	}

	public void openResultFileDialog() {
		 JFileChooser jfc = new JFileChooser();
         jfc.addChoosableFileFilter(
        		 new GenericFileFilter("shp",
        				 	PluginServices.getText(this,
        				 			"Ficheros_SHP")));
         if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
             File file = jfc.getSelectedFile();
             if (!(file.getPath().endsWith(".shp") || file.getPath().endsWith(".SHP"))){
                 file = new File(file.getPath()+".shp");
             }
             outputFile = file;
         }
         if (jfc.getSelectedFile()!=null) getFileNameResultTextField().setText(jfc.getSelectedFile().getAbsolutePath());
	}

	public void openDirectoryLayersDialog() {
		 JFileChooser jfc = new JFileChooser();
		 jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		 final String[] extensions = {"shp", "dxf", "dwg"};
         jfc.addChoosableFileFilter(
        		 new GenericFileFilter(extensions,
        				 PluginServices.getText(this, "Ficheros_de_cartografia")));
         if (jfc.showOpenDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
             File file = jfc.getSelectedFile();
             FLyrVect[] newlayers = loadLayersInDirectory(file, extensions);
             setDirectoryLabelText("Capas del directorio: " + file.getName());
            this.aditionalLayers = newlayers;
         }
         layersSelected();
	}

	public FLyrVect[] loadLayersInDirectory(File file, final String[] extensions) {
		if(file.isDirectory()){
       	 inputLayersToAdd = file.list(
					new FilenameFilter() {
						public boolean accept(File dir, String name) {
							for(int i = 0; i < extensions.length; i++){
								if(name.endsWith(extensions[i]))
									return true;
							}//for
							return false;
						}//acept
					});
        }else{
       	 	inputLayersToAdd = new String[]{file.getAbsolutePath()};
        }

		FLyrVect[] solution = null;
		if(inputLayersToAdd == null)
			return solution;
		else
			solution = new FLyrVect[inputLayersToAdd.length];
		//sin florituras. cogemos la primera que haya
		CoordinateReferenceSystem crs = layers.getLayer(0).getCrs();
		DgnMemoryDriver dgnDriver = null;
		IndexedShpDriver shpDriver = null;
		DXFMemoryDriver dxfDriver = null;
		VectorialFileDriver driver = null;
		//if we found layers with errors in the directory,
		//we'll save error descriptions in errors
		ArrayList errors = new ArrayList();
		for(int i = 0; i < inputLayersToAdd.length; i++){
			String fileName = inputLayersToAdd[i];
			String fullPath = file.getAbsolutePath() + "/" + fileName;
			File afile = new File(fullPath);
			try {
				if(fileName.endsWith("dxf") || fileName.endsWith("DXF")){
					dxfDriver = new DXFMemoryDriver();
					dxfDriver.open(afile);
					dxfDriver.initialize();
					driver = dxfDriver;
				}else if (fileName.endsWith("shp") || fileName.endsWith("SHP")){
					shpDriver = new IndexedShpDriver();
					shpDriver.open(afile);
					shpDriver.initialize();
					driver = shpDriver;
				}else if (fileName.endsWith("dgn") || fileName.endsWith("DGN")){
					dgnDriver = new DgnMemoryDriver();
					dgnDriver.open(afile);
					dgnDriver.initialize();
					driver = dxfDriver;
				}
				solution[i] = (FLyrVect) LayerFactory.createLayer(fileName,
						driver, afile, crs);
			}  catch (ReadDriverException e) {
				String errorText =  fullPath + "\n";
				errors.add(errorText);
				e.printStackTrace();
			}
		}//for
		if(errors.size() > 0){
			String errorMsg = PluginServices.getText(this, "Se_han_encontrado_capas_erroneas_en");
			errorMsg += " "+file.getAbsolutePath() + "\n";
			for(int i = 0; i < errors.size(); i++){
				errorMsg +=  (String) errors.get(i) + "\n";
			}
			JOptionPane.showMessageDialog(this, errorMsg);
		}
		return solution;
	}

	/**
	 * This method initializes directoryLayerScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getDirectoryLayerScrollPane() {
		if (directoryLayerScrollPane == null) {
			directoryLayerScrollPane = new JScrollPane();
			directoryLayerScrollPane.setBounds(new java.awt.Rectangle(10,29,428,59));
			directoryLayerScrollPane.setViewportView(getDirectoryLayerList());
		}
		return directoryLayerScrollPane;
	}

	/**
	 * This method initializes directoryLayerList
	 *
	 * @return javax.swing.JList
	 */
	private JList getDirectoryLayerList() {
		if (directoryLayerList == null) {
			directoryLayerList = new JList(getDirectoryLayerNames());
			AditionalLayerSelectorListener listener =
				new AditionalLayerSelectorListener(aditionalLayers);
			directoryLayerList.addListSelectionListener(listener);
		}
		return directoryLayerList;
	}

	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.MODALDIALOG);
			viewInfo.setTitle(PluginServices.getText(this,
					"Juntar"));
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
			jLabel = new JLabel();
			jLabel.setText(PluginServices.getText(this, "Cobertura_de_salida") + ":");
			jLabel.setBounds(new java.awt.Rectangle(2,5,128,26));
			resultLayerPanel = new JPanel();
			resultLayerPanel.setLayout(null);
			resultLayerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
			resultLayerPanel.add(getOutputJButton(), null);
			resultLayerPanel.add(getFileNameResultTextField(), null);
			resultLayerPanel.add(jLabel, null);
		}
		return resultLayerPanel;
	}

	/**
	 * This method initializes resultSchemaPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getResultSchemaPanel() {
		if (resultSchemaPanel == null) {
			jLabel1 = new JLabel();
			jLabel1.setText(PluginServices.getText(this, "Usar_los_campos_de_la_capa") + ":");
			jLabel1.setBounds(new java.awt.Rectangle(11,5,203,25));
			resultSchemaPanel = new JPanel();
			resultSchemaPanel.setLayout(null);
			resultSchemaPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
			resultSchemaPanel.add(getFieldsJComboBox(), null);
			resultSchemaPanel.add(jLabel1, null);
		}
		return resultSchemaPanel;
	}

	/**
	 * This method initializes directoryPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getDirectoryPanel() {
		if (directoryPanel == null) {
			directoryLabel = new JLabel();
			directoryLabel.setText(PluginServices.getText(this, "Capas_del_directorio") + ":");
			directoryLabel.setBounds(new java.awt.Rectangle(9,5,429,19));
			directoryPanel = new JPanel();
			directoryPanel.setLayout(null);
			directoryPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
			directoryPanel.add(getDirectoryButton(), null);
			directoryPanel.add(getDirectoryLayerScrollPane(), null);
			directoryPanel.add(directoryLabel, null);
		}
		return directoryPanel;
	}

	/**
	 * This method initializes inputLyrPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getInputLyrPanel() {
		if (inputLyrPanel == null) {
			jLabel2 = new JLabel();
			jLabel2.setBounds(new java.awt.Rectangle(13,4,422,16));
			jLabel2.setText(PluginServices.getText(this, "Coberturas_de_entrada") + ":");
			inputLyrPanel = new JPanel();
			inputLyrPanel.setLayout(null);
			inputLyrPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
			inputLyrPanel.add(getInputJScrollPane(), null);
			inputLyrPanel.add(jLabel2, null);
		}
		return inputLyrPanel;
	}

}  //  @jve:decl-index=0:visual-constraint="18,18"
