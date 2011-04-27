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
 * 2008 Prodevelop S.L. main development
 */

package org.gvsig.normalization.extensions;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.gvsig.normalization.gui.INormPanelModel;
import org.gvsig.normalization.gui.NormPanelModel;
import org.gvsig.normalization.gui.NormalizationPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.GenericFileFilter;

/**
 * Normalization Extension. This extension normalizes strings from a text file.
 * The GUI of the extension is a menu item and a button on the gvSIG toolbar. As
 * there aren't any initial conditions to run the extension, it will be always
 * visible.
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicent Sanjaime Calvet</a>
 */

public class NormalizationFileExtension extends Extension {

	private static final String NORMALIZE_FILE = "NORMALIZE_FILE";

	private static final Logger log = PluginServices.getLogger();

	/**
	 * This method executes the normalization file extension
	 * 
	 * @param actionCommand
	 */
	public void execute(String actionCommand) {

		if (actionCommand.equalsIgnoreCase(NORMALIZE_FILE)) {

			/* Load and read the normalization file */
			File file = null;
			List<String> arr = new ArrayList<String>();
			boolean isFile = true;

			try {
				/* load file */
				file = loadFile();
			} catch (Exception e) {
				file = null;
				String mes = PluginServices.getText(this,
						"normerrorloadingfile");
				String tit = PluginServices.getText(this, "normalization");
				JOptionPane.showMessageDialog(null, mes, tit,
						JOptionPane.ERROR_MESSAGE);
				log.error("ERROR, Loading the strings file ");
			}

			if (file != null) {
				try {
					/* read file */
					arr = readTextFile(file);
				} catch (Exception e) {
					arr = null;
					String mes = PluginServices.getText(this,
							"normerrorreadingfile");
					String tit = PluginServices.getText(this, "normalization");
					JOptionPane.showMessageDialog(null, mes, tit,
							JOptionPane.ERROR_MESSAGE);
					log.error("ERROR, Reading the strings file");
				}

				if (arr != null && arr.size() > 0) {

					INormPanelModel model = NormPanelModel.getInstance();
					model.setNameFile(file.getName());
					model.setFileChains(arr);
					model.setInNewTable(isFile);

					// Create the panel
					NormalizationPanel normPanel = new NormalizationPanel(
							model, isFile, false);
					model.registerListener(normPanel);
					normPanel.setPs(PluginServices.getPluginServices(this));

					// Show panel
					PluginServices.getMDIManager().addWindow(normPanel);
					normPanel.setVisible(true);
				}
			}
		}
	}

	/**
	 * This method initializes some parameters of the extension
	 */

	public void initialize() {

	}

	/**
	 * This method puts available the extension
	 * 
	 * @return enable
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * This method puts visible the extension
	 * 
	 * @return visible
	 */
	public boolean isVisible() {

		IWindow win = PluginServices.getMDIManager().getActiveWindow();
		if (win instanceof View || win instanceof Table) {
			return true;
		}
		return false;
	}

	/**
	 * This method reads the file and return a list with strings
	 * 
	 * @param file
	 * @return list with the strings from file
	 */
	private List<String> readTextFile(File file) {

		List<String> arr = new ArrayList<String>();
		if (file != null) {
			String str = "";
			try {
				FileInputStream fis = new FileInputStream(file);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fis, "UTF-8"));

				// fr = new FileReader(file);
				// BufferedReader br = new BufferedReader(fr);
				while ((str = br.readLine()) != null) {
					arr.add(str);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return arr;
	}

	/**
	 * This method loads a external file with chains to normalize
	 * 
	 * @return text file (txt or csv)
	 */
	private File loadFile() {

		File thefile = null;
		JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle(PluginServices.getText(this, "load_text_file"));
		String[] extensions = { "txt", "csv" };
		jfc.addChoosableFileFilter(new GenericFileFilter(extensions,
				PluginServices.getText(this, "txt_file")));
		int returnval = jfc.showOpenDialog((Component) PluginServices
				.getMainFrame());

		if (returnval == JFileChooser.APPROVE_OPTION) {
			thefile = jfc.getSelectedFile();
		} else {
			return null;
		}
		return thefile;
	}

}
