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
package org.gvsig.topology.ui.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.gvsig.gui.beans.swing.JFileChooser;
import org.gvsig.topology.Topology;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiFrame.MDIFrame;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.utiles.GenericFileFilter;

/**
 * GUI utility class.
 * 
 * @author Alvaro Zabala
 *
 */
public class GUIUtil {
	
	public static final String DEFAULT_TOPO_FILES_DIRECTORY = "";
	
	private static GUIUtil _instance = new GUIUtil();
	
	private String topologyFilesDirectory;
	
	
	public static GUIUtil getInstance(){
		return _instance;
	}
	
	/**
	 * Shows an internationalized message to the user with a JOptionPanel
	 * dialog
	 * @param message key of the message in the language file
	 * @param title key of the title in the language file
	 */
	public  void messageBox(String message, String title){
		JFrame parentComponent = (JFrame)PluginServices.getMainFrame();
		JOptionPane.showMessageDialog(parentComponent, 
				PluginServices.getText(null,message), 
				PluginServices.getText(null, title),
				JOptionPane.ERROR_MESSAGE); 
	}
	
	public boolean optionMessage(String message, String title){
		JFrame parentComponent = (JFrame)PluginServices.getMainFrame();
		int userEntry = JOptionPane.showConfirmDialog(parentComponent, 
														message, 
														title, 
														JOptionPane.YES_NO_OPTION);
		return userEntry == JOptionPane.YES_OPTION;
	}
	
	/**
	 * Opens a JFileChooser to allows user to select a file from the filesystem.
	 * 
	 * @param extension of the desired files to show in the dialog
	 * @return string path of the selected file
	 */
	public  String selectFile(String extFilter, String description, boolean forOpen) {
		JFileChooser jfc = new JFileChooser("TOPOLOGY_FILES", getFilesDirectory());
		jfc.addChoosableFileFilter(new GenericFileFilter(extFilter, description));
		
		int userEntry = -1;
		if(forOpen){
			userEntry = jfc.showOpenDialog((Component) PluginServices.getMainFrame());
		}else{
			userEntry = jfc.showSaveDialog((Component) PluginServices.getMainFrame());
		}
		if (userEntry == JFileChooser.APPROVE_OPTION) {
			if (jfc.getSelectedFile() != null) {
				return jfc.getSelectedFile().getAbsolutePath();
			}
		}// if
		return null;
	}
	
	/**
	 * Ask the user to overwrite an existing file
	 * @param outputFile
	 * @return
	 */
	/*
	 * This method is copied from AbstractGeoprocessController class,
	 * from extGeoprocessing project.
	 * */
	
	public boolean askForOverwriteOutputFile(File outputFile) {
		String title = PluginServices.getText(this, "Sobreescribir_fichero");
		String confirmDialogText = PluginServices.getText(this,
				"Sobreescribir_fichero_Pregunta_1")
				+ "\n'"
				+ outputFile.getAbsolutePath()
				+ "'\n"
				+ PluginServices.getText(this,
						"Sobreescribir_fichero_Pregunta_2");
		return optionMessage(confirmDialogText, title);
	}
	
	/**
	 * Adds a Topology to the view TOC.
	 * @param mapContext
	 * @param lyrs
	 * @param newTopology
	 */
	public  void addTopologyToTOC(MapContext mapContext, List lyrs, Topology newTopology){
		mapContext.beginAtomicEvent();
		for(int i = 0; i < lyrs.size(); i++){
			FLyrVect lyr = (FLyrVect) lyrs.get(i);
			mapContext.getLayers().removeLayer(lyr);
		}
		mapContext.getLayers().addLayer(newTopology);
		mapContext.endAtomicEvent();
	}
	
	public void updateTopologyInToc(MapContext mapContext, Topology topology){
		mapContext.beginAtomicEvent();
		mapContext.getLayers().removeLayer(topology);
		List newTopoLyrs = topology.getLayers();
		addTopologyToTOC(mapContext, newTopoLyrs, topology);
		mapContext.endAtomicEvent();
	}
	
	private GUIUtil(){
		topologyFilesDirectory = DEFAULT_TOPO_FILES_DIRECTORY;
	}
	
	public void setFilesDirectory(String filesDirectory){
		this.topologyFilesDirectory = filesDirectory;
	}
	
	public String getFilesDirectory(){
		return topologyFilesDirectory;
	}
		
	public Window getParentWindow(Component component){
		return (Window) getParentOfType(component, Window.class);
	}
	
	public Container getParentOfType(Component component, Class<?> parentType){
		Container solution = null;
		Container parent = component.getParent();
		while(parent != null && !(parentType.isAssignableFrom(parent.getClass())))
			parent = parent.getParent();
		solution = parent;
		return solution;
	}
	
	public void centerDialog(Container d, MDIFrame mainFrame) {
        int offSetX = d.getWidth() / 2;
        int offSetY = d.getHeight() / 2;
        d.setLocation((mainFrame.getWidth() / 2) - offSetX, (mainFrame
                .getHeight() / 2)
                - offSetY);
    }
	
}
