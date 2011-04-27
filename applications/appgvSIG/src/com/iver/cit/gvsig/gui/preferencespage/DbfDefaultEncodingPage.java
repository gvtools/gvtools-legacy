/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2008 Software Colaborativo.
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
 */

/* CVS MESSAGES:
*
*/
package com.iver.cit.gvsig.gui.preferencespage;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.iver.andami.PluginServices;
import com.iver.andami.preferences.AbstractPreferencePage;
import com.iver.andami.preferences.StoreException;
import com.iver.cit.gvsig.fmap.drivers.dbf.DbaseFile;
import com.iver.cit.gvsig.fmap.drivers.dbf.DbfEncodings;
import com.iver.cit.gvsig.fmap.drivers.shp.DbaseFileNIO;
import com.iver.utiles.swing.JComboBox;

/**
 * @author Fjp
 * TODO: To be developed in future releases.
 *
 */
public class DbfDefaultEncodingPage extends AbstractPreferencePage {
	private static Preferences prefs = Preferences.userRoot().node( "gvSIG.encoding.dbf" );
	private JTextArea jTextArea = null;
	private JComboBox cmbDefaultDbfCharset;
	private DbfEncodings dbfEncodings = DbfEncodings.getInstance();
	private ImageIcon icon;

	public DbfDefaultEncodingPage() {
		super();
		icon = new ImageIcon(this.getClass().getClassLoader().getResource("images/icu_logo_encoding.gif"));
		addComponent(getJTextArea());
		int[] ids=dbfEncodings.getSupportedDbfLanguageIDs();
		String[] encodings=new String[ids.length-1];
		for (int i=1;i<ids.length;i++){
			encodings[i-1]=dbfEncodings.getCharsetForDbfId(ids[i]);
		}

		addComponent(PluginServices.getText(this, "dbf_default_encoding") + ":",
			cmbDefaultDbfCharset = new JComboBox(encodings));
	}

	public void initializeValues() {
		if (prefs.get("dbf_encoding", null) != null) {
			String charsetName=prefs.get("dbf_encoding", DbaseFile.getDefaultCharset().toString());
			Charset newDefaultCharset=null;
			try{
				newDefaultCharset = Charset.forName(charsetName);
			}catch (UnsupportedCharsetException e) {
				JOptionPane.showMessageDialog(null, "Unsupported CharSet for the System");
				Logger.getLogger(this.getClass()).warn(e.getLocalizedMessage(), e);
				newDefaultCharset = Charset.defaultCharset();
			}
	//		Charset newDefaultCharset = Charset.forName(charsetName);
			cmbDefaultDbfCharset.setSelectedItem(charsetName);
			DbaseFile.setDefaultCharset(newDefaultCharset);
			DbaseFileNIO.setDefaultCharset(newDefaultCharset);
		}
	}

	public String getID() {
		return this.getClass().getName();
	}

	public String getTitle() {
		return PluginServices.getText(this, "dbf_default_encoding");
	}

	public JPanel getPanel() {
		return this;
	}

	/* (non-Javadoc)
	 * @see com.iver.andami.preferences.AbstractPreferencePage#storeValues()
	 */
	public void storeValues() throws StoreException {
		String charsetName = (String)cmbDefaultDbfCharset.getSelectedItem();
		if (Charset.isSupported(charsetName))
		{
			prefs.put("dbf_encoding", charsetName);
			Charset newDefaultCharset = Charset.forName(charsetName);
			DbaseFile.setDefaultCharset(newDefaultCharset);
			DbaseFileNIO.setDefaultCharset(newDefaultCharset);
		}
		else
		{
			if (cmbDefaultDbfCharset.getSelectedIndex() == 0) {
				prefs.remove("dbf_encoding");
				DbaseFile.setDefaultCharset(null);
				DbaseFileNIO.setDefaultCharset(null);
			}
			else
				throw new StoreException("Bad charsetName");
		}


		// ¿Guardar en alguna clase el charset?
	}

	public void initializeDefaults() {
		cmbDefaultDbfCharset.setSelectedItem(dbfEncodings.getCharsetForDbfId(0));
	}

	public ImageIcon getIcon() {
		return icon;
	}
	/**
	 * This method initializes jTextArea
	 *
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setBounds(new java.awt.Rectangle(13,7,285,57));
			jTextArea.setForeground(java.awt.Color.black);
			jTextArea.setBackground(java.awt.SystemColor.control);
			jTextArea.setRows(3);
			jTextArea.setWrapStyleWord(true);
			jTextArea.setLineWrap(true);
			jTextArea.setEditable(false);
			jTextArea.setText(PluginServices.getText(this,"default_charset_name_for_dbf"));
		}
		return jTextArea;
	}

	public boolean isValueChanged() {
		return super.hasChanged();
	}

	public void setChangesApplied() {
		setChanged(false);
	}
}
