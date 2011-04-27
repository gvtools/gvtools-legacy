/*
 * Created on 09-mar-2006
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
* $Id: AbstractGeoprocessPanel.java 21232 2008-06-05 14:03:49Z azabala $
* $Log$
* Revision 1.8  2007-03-06 16:47:58  caballero
* Exceptions
*
* Revision 1.7  2006/09/15 10:42:54  caballero
* extensibilidad de documentos
*
* Revision 1.6  2006/07/21 09:10:34  azabala
* fixed bug 608: user doesnt enter any result file to the geoprocess panel
*
* Revision 1.5  2006/06/29 17:29:34  azabala
* Added common functionality to all geoprocess panels (result layer dialog)
*
* Revision 1.4  2006/06/08 18:22:02  azabala
* Arreglado pete cuando abríamos un dialogo de geoprocessing habiendo agrupaciones en el TOC
*
* Revision 1.3  2006/06/02 18:21:28  azabala
* *** empty log message ***
*
* Revision 1.2  2006/05/25 08:21:14  jmvivo
* Añadido metodo de peticion de confirmacion para sobreescribir el fichero de salida
*
* Revision 1.1  2006/05/24 21:13:09  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.2  2006/05/08 15:36:33  azabala
* check and ask for spatial index creation
*
* Revision 1.1  2006/04/11 18:01:23  azabala
* primera version en cvs
*
* Revision 1.5  2006/04/07 19:00:58  azabala
* *** empty log message ***
*
* Revision 1.4  2006/03/23 21:02:37  azabala
* *** empty log message ***
*
* Revision 1.3  2006/03/21 19:26:08  azabala
* *** empty log message ***
*
* Revision 1.2  2006/03/14 19:32:22  azabala
* *** empty log message ***
*
* Revision 1.1  2006/03/09 17:03:59  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.geoprocess.core.gui;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.view.legend.CreateSpatialIndexMonitorableTask;
import com.iver.utiles.GenericFileFilter;
import com.iver.utiles.swing.threads.IMonitorableTask;

/**
 * Abstract base class with utility methods
 * commons to all GeoprocessXXXPanel
 * @author azabala
 *
 */
public abstract class AbstractGeoprocessPanel extends JPanel 
				implements IGeoprocessPanel, IGeoprocessUserEntries{
	protected FLayers layers = null;
	protected JComboBox layersComboBox = null;
	protected JTextField fileNameResultTextField = null;

	public FLyrVect getInputLayer() {
		FLyrVect solution = null;
		String selectedLayer = (String)layersComboBox.getSelectedItem();
        solution = (FLyrVect)layers.getLayer(selectedLayer);
        return solution;
	}

	public void setFLayers(FLayers layers){
		this.layers = layers;
	}

	public FLayers getFLayers(){
		return layers;
	}

	protected FLyrVect[] getVectorialLayers(FLayers layers){
		FLyrVect[] solution = null;
		ArrayList list = new ArrayList();
		int numLayers = layers.getLayersCount();
		for(int i = 0; i < numLayers; i++){
			FLayer layer = layers.getLayer(i);
			if(layer instanceof FLyrVect)
				list.add(layer);
			else if(layer instanceof FLayers)
				list.addAll(Arrays.asList(getVectorialLayers((FLayers)layer)));
		}
		solution = new FLyrVect[list.size()];
		list.toArray(solution);
		return solution;

	}

	protected String[] getLayerNames() {
		String[] solution = null;
		int numLayers = layers.getLayersCount();
		if(layers != null &&  numLayers > 0){
			ArrayList list = new ArrayList();
			for(int i = 0; i < numLayers; i++){
				FLayer layer = layers.getLayer(i);
				if(layer instanceof FLyrVect)
					list.add(layer.getName());
				if(layer instanceof FLayers){
					FLayers layers = (FLayers) layer;
					FLyrVect[] vectorials = getVectorialLayers(layers);
					for(int j = 0; j < vectorials.length; j++){
						list.add(vectorials[j].getName());
					}
				}
			}//for
			solution = new String[list.size()];
			list.toArray(solution);
		}
		return solution;
	}

	/**
	 * Shows to the user a dialog error, whith the title and message body
	 * specified as parameters.
	 *
	 * @param message body of the error message
	 * @param title title of the error message
	 */
	public void error(String message, String title) {
		JFrame parentComponent = (JFrame)PluginServices.
											getMainFrame();
		JOptionPane.showMessageDialog(parentComponent, message, title,
				JOptionPane.ERROR_MESSAGE);
	}


	/**
	 * Asks to the user for the creation of a spatial index for the
	 * specified layer.
	 * It will be of help for certain geoprocesses.
	 *
	 * FLyrVect default spatial index is MapServer quadtree, that hasnt
	 * the ability to do nearest neighbour searches.
	 *
	 * For those geoprocesses that needs it (NN searches) overwrite this
	 * method and use JSI RTree, or SIL RTree.
	 *
	 * It returns an IMonitorableTask, a task to build the spatial
	 * index in background.
	 *
	 * @param layer
	 * @return an ITask
	 */
	public IMonitorableTask askForSpatialIndexCreation(FLyrVect layer){
		String title = PluginServices.getText(this, "Crear_Indice");
		String confirmDialogText =
			PluginServices.getText(this, "Crear_Indice_Pregunta_1")+
					" " +
					layer.getName()+
					" " +
			PluginServices.getText(this, "Crear_Indice_Pregunta_2");
		int option = JOptionPane.showConfirmDialog(this,
					confirmDialogText,
					title,
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null);
		if(option == JOptionPane.YES_OPTION){
			//Usually we want task like spatial index creation dont block
			//GUI responses. Now, we dont want to start geoprocess execution
			//until spatial index creation would finish, to have advantage
			//of the spatial index
			try {
				CreateSpatialIndexMonitorableTask task
					= new CreateSpatialIndexMonitorableTask(layer);
				return task;
			} catch (ReadDriverException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;

	}
	/**
	 * Confirm overwrite the output file
	 * if it allready exist.
	 *
	 *
	 * @param outputFile
	 * @return answer
	 */
	public boolean askForOverwriteOutputFile(File outputFile){
		String title = PluginServices.getText(this, "Sobreescribir_fichero");
		String confirmDialogText =
			PluginServices.getText(this, "Sobreescribir_fichero_Pregunta_1")+
					"\n'" +
					outputFile.getAbsolutePath()+
					"'\n" +
			PluginServices.getText(this, "Sobreescribir_fichero_Pregunta_2");
		int option = JOptionPane.showConfirmDialog(this,
					confirmDialogText,
					title,
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null);
		if(option == JOptionPane.YES_OPTION){
			return true;
		}
		return false;
	}

	 public void openResultFile() {
			JFileChooser jfc = new JFileChooser();
	        jfc.addChoosableFileFilter(new GenericFileFilter("shp",
	        		PluginServices.getText(this, "Ficheros_SHP")));
	        if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) ==
	        										JFileChooser.APPROVE_OPTION) {
	            File file = jfc.getSelectedFile();
	            if (!(file.getPath().endsWith(".shp") || file.getPath().endsWith(".SHP"))){
	                file = new File(file.getPath()+".shp");
	            }
	        }//if
	        if (jfc.getSelectedFile()!=null) {
	        	getFileNameResultTextField().setText(
	        			jfc.getSelectedFile().getAbsolutePath());
	        }

	  }

		 /**
		  * Gives access to the text field where user could enter the result
		  * file path.
		  * If this method is called during the concrete gui subclasses building,
		  * caller must sets bounds of this component.
		  * @return
		  */
		public JTextField getFileNameResultTextField() {
			if (fileNameResultTextField == null) {
				fileNameResultTextField = new JTextField();
			}
			return fileNameResultTextField;
		}


		public File getOutputFile() throws FileNotFoundException{
			String fileName = getFileNameResultTextField().getText();
			if(fileName.length() == 0){
				throw new FileNotFoundException("No se ha seleccionado ningun fichero de salida");
			}
			if(! fileName.endsWith(".shp")){
				if(! fileName.endsWith("."))
					fileName += ".";
				fileName += "shp";
			}
			return new File(fileName);
		}

}



