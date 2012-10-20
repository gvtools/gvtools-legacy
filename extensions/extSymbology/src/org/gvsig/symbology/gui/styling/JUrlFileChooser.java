/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
package org.gvsig.symbology.gui.styling;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import org.gvsig.gui.beans.swing.JFileChooser;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;

public class JUrlFileChooser extends JFileChooser {

	private static final long serialVersionUID = 1844355534608274984L;

	public JUrlFileChooser(String fileChooserID) {
		super(fileChooserID, new SymbolFileSystemView());
		this.setFileFilter(ff);
		this.setFileSelectionMode(JFileChooser.FILES_ONLY);
		this.setMultiSelectionEnabled(false);
	}

	FileFilter ff = new FileFilter() {
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			String fName = f.getAbsolutePath();
			if (fName != null) {
				fName = fName.toLowerCase();
				return fName.endsWith(".png") || fName.endsWith(".gif")
						|| fName.endsWith(".jpg") || fName.endsWith(".jpeg")
						|| fName.endsWith(".bmp") || fName.endsWith(".svg");
			}
			return false;
		}

		public String getDescription() {
			return PluginServices.getText(this, "bitmap_and_svg_image_files")
					+ ", " + PluginServices.getText(this, "URL");
		}
	};

	public String getSelectedURLPath() {
		try {
			return new URL("file", null, getSelectedFile().getAbsolutePath())
					.toExternalForm();
		} catch (MalformedURLException e) {
			return null;
		}
	}

	private static class SymbolFileSystemView extends FileSystemView {
		private File root = new File(SymbologyFactory.SymbolLibraryPath);
		private File[] roots = new File[] { root };

		@Override
		public File createNewFolder(File containingDir) {
			File folder = new File(containingDir, "New Folder");
			folder.mkdir();
			return folder;
		}

		@Override
		public File getDefaultDirectory() {
			return root;
		}

		@Override
		public File getHomeDirectory() {
			return root;
		}

		@Override
		public File[] getRoots() {
			return roots;
		}
	}
}
