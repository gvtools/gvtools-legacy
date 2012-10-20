/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
package es.gva.cit.catalog.ui.serverconnect;

import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.gvsig.i18n.Messages;

import com.iver.utiles.swing.jcomboServer.JComboServer;
import com.iver.utiles.swing.jcomboServer.ServerData;

import es.gva.cit.catalog.drivers.ICatalogServiceDriver;
import es.gva.cit.catalog.drivers.IDiscoveryServiceDriver;
import es.gva.cit.catalog.utils.CatalogConstants;
import es.gva.cit.catalog.utils.CatalogDriverRegister;

/**
 * Panel de conexi�n con los servers de cat�logo.
 * 
 * @author Jorge Piera Llodra (piera_jor@gva.es)
 */
public class ServerConnectPanel extends JPanel {
	private javax.swing.JPanel centerPanel;
	private javax.swing.JButton closeButton;
	private javax.swing.JButton connectButton;
	private javax.swing.JLabel dataBaseLabel;
	private javax.swing.JPanel dataBasePanel;
	private javax.swing.JTextField dataBaseText;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JPanel lowerPanel;
	protected javax.swing.JComboBox protocolCombo;
	private javax.swing.JLabel protocolLabel;
	private javax.swing.JPanel protocolPanel;
	private javax.swing.JLabel replyLabel;
	private javax.swing.JEditorPane replyText;
	private javax.swing.JButton searchButton;
	private JComboServer serverCombo;
	private javax.swing.JLabel serverLabel;
	private javax.swing.JPanel serverPanel;
	private javax.swing.JButton serverPropertiesButton;
	private javax.swing.JPanel upperPanel;

	/** Creates new form ServerConnectPanel */
	public ServerConnectPanel() {
		initComponents();
		initLabels();
		initButtonSize();
		initDefaultValues();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" C�digo Generado  ">
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		upperPanel = new javax.swing.JPanel();
		serverPanel = new javax.swing.JPanel();
		serverLabel = new javax.swing.JLabel();
		serverCombo = new JComboServer();
		protocolPanel = new javax.swing.JPanel();
		protocolLabel = new javax.swing.JLabel();
		protocolCombo = new javax.swing.JComboBox();
		serverPropertiesButton = new javax.swing.JButton();
		dataBasePanel = new javax.swing.JPanel();
		dataBaseLabel = new javax.swing.JLabel();
		dataBaseText = new javax.swing.JTextField();
		centerPanel = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		replyText = new javax.swing.JEditorPane();
		replyLabel = new javax.swing.JLabel();
		lowerPanel = new javax.swing.JPanel();
		connectButton = new javax.swing.JButton();
		searchButton = new javax.swing.JButton();
		closeButton = new javax.swing.JButton();

		setLayout(new java.awt.BorderLayout(0, 5));

		upperPanel.setLayout(new java.awt.GridBagLayout());

		serverPanel.setLayout(new java.awt.GridBagLayout());

		serverLabel.setText("jLabel1");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 2, 2);
		serverPanel.add(serverLabel, gridBagConstraints);

		serverCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"Art\u00edculo 1", "Art\u00edculo 2", "Art\u00edculo 3",
				"Art\u00edculo 4" }));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
		serverPanel.add(serverCombo, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 23;
		gridBagConstraints.ipady = 3;
		gridBagConstraints.weightx = 0.3;
		upperPanel.add(serverPanel, gridBagConstraints);

		protocolPanel.setLayout(new java.awt.GridBagLayout());

		protocolLabel.setText("jLabel1");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 2, 2);
		protocolPanel.add(protocolLabel, gridBagConstraints);

		protocolCombo.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Art\u00edculo 1", "Art\u00edculo 2",
						"Art\u00edculo 3", "Art\u00edculo 4" }));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 4);
		protocolPanel.add(protocolCombo, gridBagConstraints);

		serverPropertiesButton.setPreferredSize(new java.awt.Dimension(25, 25));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 3);
		protocolPanel.add(serverPropertiesButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 0.3;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
		upperPanel.add(protocolPanel, gridBagConstraints);

		dataBasePanel.setLayout(new java.awt.GridBagLayout());

		dataBaseLabel.setText("jLabel1");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 2, 2);
		dataBasePanel.add(dataBaseLabel, gridBagConstraints);

		dataBaseText.setText("jTextField1");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
		dataBasePanel.add(dataBaseText, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 0.3;
		upperPanel.add(dataBasePanel, gridBagConstraints);

		add(upperPanel, java.awt.BorderLayout.NORTH);

		centerPanel.setLayout(new java.awt.BorderLayout(2, 4));

		centerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5,
				2, 5));
		jScrollPane1.setViewportView(replyText);

		centerPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

		replyLabel.setText("jLabel1");
		centerPanel.add(replyLabel, java.awt.BorderLayout.NORTH);

		add(centerPanel, java.awt.BorderLayout.CENTER);

		lowerPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT,
				5, 0));

		lowerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5,
				5, 0));
		connectButton.setText("jButton1");
		lowerPanel.add(connectButton);

		searchButton.setText("jButton2");
		lowerPanel.add(searchButton);

		closeButton.setText("jButton3");
		lowerPanel.add(closeButton);

		add(lowerPanel, java.awt.BorderLayout.SOUTH);
	}// </editor-fold>

	/**
	 * Rewrite the labels
	 */
	private void initLabels() {
		serverLabel.setText(Messages.getText("serverURL"));
		protocolLabel.setText(Messages.getText("protocols"));
		dataBaseLabel.setText(Messages.getText("database"));
		replyLabel.setText(Messages.getText("serverReply"));
		connectButton.setText(Messages.getText("connectButton"));
		searchButton.setText(Messages.getText("searchButton"));
		closeButton.setText(Messages.getText("close"));
		serverPropertiesButton
				.setIcon(new ImageIcon(
						"./gvSIG/extensiones/es.gva.cit.gvsig.catalogClient/images/serverProperties.png"));
	}

	/**
	 * Initialize the default values
	 */
	private void initDefaultValues() {
		dataBaseText.setText("");
		protocolCombo.removeAllItems();
		serverCombo.removeAllItems();
		serverCombo.setEditable(true);
		replyText.setEditable(false);
	}

	/**
	 * Initialize the buttons size
	 */
	private void initButtonSize() {
		connectButton.setPreferredSize(CatalogConstants.BUTTON_SIZE);
		searchButton.setPreferredSize(CatalogConstants.BUTTON_SIZE);
		closeButton.setPreferredSize(CatalogConstants.BUTTON_SIZE);
	}

	/**
	 * Set the database panel visible
	 * 
	 * @param isVisible
	 */
	protected void setDatabaseVisible(boolean isVisible) {
		dataBaseLabel.setVisible(false);
		dataBaseText.setVisible(false);
		dataBasePanel.setVisible(false);
	}

	/**
	 * Adds a new server
	 * 
	 * @param serverData
	 *            Server to add
	 */
	public void addServer(ServerData serverData) {
		serverCombo.addServer(serverData);
	}

	/**
	 * Load the drivers
	 * 
	 * @param drivers
	 */
	public void loadDrivers(Object[] drivers) {
		for (int i = 0; i < drivers.length; i++) {
			protocolCombo.addItem(drivers[i]);
		}
	}

	/**
	 * Select a concrete protocol
	 * 
	 * @param protocol
	 *            Protocol to select
	 */
	public void setProtocol(String protocol) {
		ICatalogServiceDriver driver = CatalogDriverRegister.getInstance()
				.getDriver(protocol);
		if (driver != null) {
			for (int i = 0; i < protocolCombo.getItemCount(); i++) {
				IDiscoveryServiceDriver auxDriver = (IDiscoveryServiceDriver) protocolCombo
						.getItemAt(i);
				if (auxDriver.getServiceName().toLowerCase()
						.compareTo(driver.getServiceName().toLowerCase()) == 0) {
					protocolCombo.setSelectedItem(auxDriver);
				}
			}
		}
	}

	/**
	 * Updates the protocl combo
	 */
	public void updateProtocol() {
		ServerData server = getServer();
		if (server != null) {
			setProtocol(server.getServiceSubType());
		}
	}

	/**
	 * @return the selected server
	 */
	public ServerData getServer() {
		return serverCombo.getSelectedServer();
	}

	public ServerData[] getAllServers() {
		return serverCombo.getAllServers();
	}

	/**
	 * @return the selected driver by protocol
	 */
	public Object getDriver() {
		return protocolCombo.getSelectedItem();
	}

	/**
	 * @return the server address
	 */
	public String getServerAddress() {
		return (String) serverCombo.getSelectedServer().getServerAddress();
	}

	/**
	 * @return the server address
	 */
	public String getDatabase() {
		return dataBaseText.getText();
	}

	/**
	 * Set the server reply
	 * 
	 * @param text
	 *            Text to write
	 */
	public void setServerReply(String text) {
		replyText.setText(text);
	}

	/**
	 * Adds a listener to manage the panel events
	 * 
	 * @param listener
	 *            Listener to add
	 */
	public void addActionListener(ActionListener listener) {
		serverCombo.addActionListener(listener);
		serverCombo
				.setActionCommand(CatalogConstants.SERVER_COMBO_ACTION_COMMAND);
		protocolCombo.addActionListener(listener);
		protocolCombo
				.setActionCommand(CatalogConstants.PROTOCOL_COMBO_ACTION_COMMAND);
		connectButton.addActionListener(listener);
		connectButton
				.setActionCommand(CatalogConstants.CONNECT_BUTTON_ACTION_COMMAND);
		searchButton.addActionListener(listener);
		searchButton
				.setActionCommand(CatalogConstants.SEARCH_BUTTON_ACTION_COMMAND);
		closeButton.addActionListener(listener);
		closeButton
				.setActionCommand(CatalogConstants.CLOSE_BUTTON_ACTION_COMMAND);
		serverPropertiesButton.addActionListener(listener);
		serverPropertiesButton
				.setActionCommand(CatalogConstants.SERVERPROPERTIES_BUTTON_ACTION_COMMAND);

	}

	/**
	 * Enable or disable the search button
	 * 
	 * @param isEnabled
	 *            If the search button has to be enabled
	 */
	public void enableSearchButton(boolean isEnabled) {
		searchButton.setEnabled(isEnabled);
	}

	/**
	 * Enable or disable the server properties button
	 * 
	 * @param isEnabled
	 *            If the search button has to be enabled
	 */
	public void enableServerPropertiesButton(boolean isEnabled) {
		serverPropertiesButton.setEnabled(isEnabled);
	}

	/**
	 * Set visible the properties button
	 * 
	 * @param isVisible
	 *            If the properties button has to be visible
	 */
	public void setServerPropertiesButtonVisible(boolean isVisible) {
		serverPropertiesButton.setVisible(isVisible);
	}

	public void setServerTextEnabled(boolean isEnabled) {
		serverCombo.setEnabled(isEnabled);
	}
}