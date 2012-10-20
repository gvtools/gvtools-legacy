/*
 * Created on 26-oct-2005
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
package com.iver.cit.gvsig.vectorialdb;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.gvsig.gui.beans.AcceptCancelPanel;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.utiles.XMLEntity;

public class DlgConnection extends JDialog {

	private String[] driverNames = null;
	private JPanel jContentPane = null;
	private ConnectionPanel jConnPanel = null;
	private JButton jBtnOK = null;
	private JPanel jPanel1 = null;
	private JButton jBtnCancel = null;
	private ConnectionSettings connSettings = null;

	/**
	 * This is the default constructor
	 */
	public DlgConnection() {
		this(null);
	}

	public DlgConnection(String[] driverNames) {
		super();
		this.driverNames = driverNames;
		initialize();
	}

	// private void setPreferences()
	// {
	// XMLEntity xml =
	// PluginServices.getPluginServices(this).getPersistentXML();
	//
	// if (xml == null) {
	// xml = new XMLEntity();
	// }
	//
	// if (!xml.contains("db-connections")) {
	// String[] servers = new String[0];
	// xml.putProperty("db-connections", servers);
	// }
	//
	// try {
	// String[] servers = xml.getStringArrayProperty("db-connections");
	// HashMap settings = new HashMap();
	// for (int i = 0; i < servers.length; i++) {
	// ConnectionSettings cs = new ConnectionSettings();
	// cs.setFromString(servers[i]);
	// settings.put(cs.getName(), cs);
	// }
	// getJConnPanel().setSettings(settings);
	// } catch (NotExistInXMLEntity e) {
	// }
	//
	// }
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(380, 332);
		this.setTitle(PluginServices.getText(this, "database_connection"));
		this.setContentPane(getJContentPane());
		// setPreferences();
	}

	private String[] getDriverNames() {
		if (this.driverNames == null) {

			Class[] classes = new Class[] { IVectorialDatabaseDriver.class };

			ArrayList ret = new ArrayList();
			String[] myDriverNames = LayerFactory.getDM().getDriverNames();

			for (int i = 0; i < myDriverNames.length; i++) {
				boolean is = false;

				for (int j = 0; j < classes.length; j++) {
					if (LayerFactory.getDM().isA(myDriverNames[i], classes[j])) {
						ret.add(myDriverNames[i]);
					}
				}
			}

			this.driverNames = (String[]) ret.toArray(new String[0]);
		}
		return this.driverNames;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJConnPanel(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getJPanel1(), java.awt.BorderLayout.SOUTH);

		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private ConnectionPanel getJConnPanel() {
		if (jConnPanel == null) {
			jConnPanel = new ConnectionPanel();
			String[] drvAux = this.getDriverNames();
			jConnPanel.setDrivers(drvAux);

			XMLEntity xml = PluginServices.getPluginServices(
					"com.iver.cit.gvsig").getPersistentXML();

			if (xml == null) {
				xml = new XMLEntity();
			}

			if (!xml.contains("db-connections")) {
				String[] servers = new String[0];
				xml.putProperty("db-connections", servers);
			}

			String[] servers = xml.getStringArrayProperty("db-connections");
			HashMap settings = new HashMap();
			for (int i = 0; i < servers.length; i++) {
				ConnectionSettings cs = new ConnectionSettings();
				cs.setFromString(servers[i]);
				for (int j = 0; j < drvAux.length; j++) {
					if (cs.getDriver().equals(drvAux[j])) {
						settings.put(cs.getName(), cs);
						break;
					}
				}
			}
			jConnPanel.setSettings(settings);
			jConnPanel.setPreferredSize(new java.awt.Dimension(400, 300));

		}
		return jConnPanel;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			ActionListener okAction = new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (!jConnPanel.done()) {
						JOptionPane.showMessageDialog(DlgConnection.this,
								"No estan todos los datos rellenos", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					jConnPanel.saveConnectionSettings();
					connSettings = jConnPanel.getConnectionSettings();
					dispose();
				}
			};

			ActionListener cancelAction = new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					connSettings = null;
					dispose();
				}
			};
			jPanel1 = new AcceptCancelPanel(okAction, cancelAction);

		}
		return jPanel1;
	}

	public ConnectionSettings getConnSettings() {
		return connSettings;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
