/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2008 Software Colaborativo S.L.
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
 * www.scolab.es
 */
package com.iver.cit.gvsig;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.preferences.IPreference;
import com.iver.andami.preferences.IPreferenceExtension;
import com.iver.cit.gvsig.fmap.drivers.dbf.DbfEncodings;
import com.iver.cit.gvsig.gui.preferencespage.DbfDefaultEncodingPage;

/**
 * @author Francisco José Peñarrubia
 * 
 *         Set encoding to dbf files. To install it, you need to copy
 *         (overwrite) dbf.jar, fmap.jar, iver-utiles.jar and of course, install
 *         this plugin to gvSIG/extensiones.
 * 
 */
public class ShalomExtension extends Extension implements IPreferenceExtension {
	static File lastDir = null;
	private static DbfDefaultEncodingPage dbfDefaultEncodingPage = new DbfDefaultEncodingPage();
	private DbfEncodings dbfEncodings = DbfEncodings.getInstance();

	private class MyOption extends Object {
		public int dbfLanguageId;
		public String charSetName;

		public MyOption(int aux) {
			dbfLanguageId = aux;
			charSetName = dbfEncodings.getCharsetForDbfId(aux);
		}

		@Override
		public String toString() {
			return charSetName;
		}

	}

	/**
	 * @see com.iver.andami.plugins.Extension#inicializar()
	 */
	public void initialize() {
		About classAbout = (About) PluginServices
				.getExtension(com.iver.cit.gvsig.About.class);
		java.net.URL aboutURL2 = ShalomExtension.class
				.getResource("/about.htm");

		classAbout.getAboutPanel().addAboutUrl("Shalom", aboutURL2);

		// TODO: To use in future releases.
		// ExtensionPoints extensionPoints =
		// ExtensionPointsSingleton.getInstance();
		//
		// extensionPoints.add("AplicationPreferences","EncodingsPage", new
		// DbfDefaultEncodingPage());

	}

	/**
	 * @see com.iver.andami.plugins.Extension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				if (f.canWrite())
					if (f.getName().toLowerCase().endsWith(".dbf"))
						return true;
				return false;
			}

			@Override
			public String getDescription() {
				return (PluginServices.getText(this, "Ficheros_dbf"));
			}

		});
		if (lastDir != null)
			fc.setCurrentDirectory(lastDir);
		fc.setMultiSelectionEnabled(true);

		int res = fc.showOpenDialog((Component) PluginServices.getMainFrame());
		if (res == JFileChooser.APPROVE_OPTION) {
			lastDir = fc.getCurrentDirectory();
			int dbfEncoding = getSelectedDbfEncoding();
			if (dbfEncoding == -1)
				return;
			File[] files = fc.getSelectedFiles();
			for (int i = 0; i < files.length; i++) {
				try {
					setSelectedDbfEncoding(files[i], dbfEncoding);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private int getSelectedDbfEncoding() {

		MyOption[] myOptions = new MyOption[dbfEncodings
				.getSupportedDbfLanguageIDs().length];
		for (int i = 0; i < dbfEncodings.getSupportedDbfLanguageIDs().length; i++) {
			int aux = dbfEncodings.getSupportedDbfLanguageIDs()[i];
			myOptions[i] = new MyOption(aux);
		}
		MyOption selectedOption = (MyOption) JOptionPane.showInputDialog(
				(Component) PluginServices.getMainFrame(),
				PluginServices.getText(this, "select_encoding"),
				PluginServices.getText(this, "select_encoding"),
				JOptionPane.OK_CANCEL_OPTION, null, myOptions, 2);
		if (selectedOption == null)
			return -1;

		return selectedOption.dbfLanguageId;
	}

	private void setSelectedDbfEncoding(File file, int dbfEncoding)
			throws IOException {
		RandomAccessFile fo = new RandomAccessFile(file, "rw");
		fo.seek(29);
		fo.writeByte(dbfEncoding);
		fo.close();

	}

	/**
	 * @see com.iver.andami.plugins.Extension#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * @see com.iver.andami.plugins.Extension#isVisible()
	 */
	public boolean isVisible() {
		return true;
	}

	public IPreference[] getPreferencesPages() {
		IPreference[] preferences = new IPreference[1];
		preferences[0] = dbfDefaultEncodingPage;
		return preferences;
	}
}
