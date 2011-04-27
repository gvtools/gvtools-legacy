/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package es.prodevelop.cit.gvsig.arcims.gui.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.gvsig.remoteClient.arcims.ArcImsClientP;
import org.gvsig.remoteClient.arcims.ArcImsProtocolHandler;
import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;
import org.gvsig.remoteClient.arcims.utils.ServiceInformation;

import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.utiles.NotExistInXMLEntity;
import com.iver.utiles.XMLEntity;

import es.prodevelop.cit.gvsig.arcims.fmap.drivers.FMapFeatureArcImsDriver;
import es.prodevelop.cit.gvsig.arcims.fmap.drivers.FMapRasterArcImsDriver;
import es.prodevelop.cit.gvsig.arcims.gui.panels.utils.ServicesTableDataSource;
import es.prodevelop.cit.gvsig.arcims.gui.panels.utils.ServicesTableModel;
import es.prodevelop.cit.gvsig.arcims.gui.panels.utils.ServicesTableSelectionListener;
import es.prodevelop.cit.gvsig.arcims.gui.wizards.ArcImsWizard;


/**
 * This class implements the first panels of the ArcIMS wizard. It
 * asks for the server's URL and the service name (which can be either an
 * ImageService or a FeatureService). Depending on the type of service, one
 * panel (Image Service Panel) or the other (Feature Service Panel) will be enabled.
 * All three panels are owned by the ArcIMS wizard.
 *
 * @see es.prodevelop.cit.gvsig.arcims.gui.panels.FeatureServicePanel
 *
 *
 * @author jldominguez
 */
public class ServiceNamesPanel extends JPanel implements ActionListener,
    MouseListener, KeyListener, ItemListener {
    private static Logger logger = Logger.getLogger(ServiceNamesPanel.class.getName());
    private static final long serialVersionUID = 0;
    private ArcImsWizard parentWizard = null;
    private JPanel northPanel = null;
    private JLabel serverLabel = null;
    private JComboBox serverComboBox = null;
    private JButton connectButton = null;
    private JScrollPane servicesTableScrollPane = null;
    private JButton nextButton = null;
    private JButton previousButton = null;
    private JTable servicesTable = new JTable();
    private String selectedServiceType = "";
    private String selectedServiceName = "";
    private Vector favoriteServers = new Vector();
    private ServicesTableSelectionListener tableSelectionListener;
    private JPanel userDefinedServiceNamePanel = null;

    // private JRadioButton serviceNameSelectionModeUserRadioButton = null;
    // private JRadioButton serviceNameSelectionModeListRadioButton = null;
    // private ButtonGroup serviceSelectionModeButtonGroup = null;
    private JLabel serviceNameLabel = null;
    private JTextField userDefinedServiceNameTextField = null;
    private String serviceType = "";
    private URL completeURL = null;
    private JLabel versionTitleLabel = null;
    private JLabel versionLabel = null;
    private JCheckBox cacheOverride = null;

    /**
     * The owning wizard is used as a parameter to allow easy <i>upwards</i>
     * references.
     *
     * @param parent
     *            the wizard that owns this panel
     */
    public ServiceNamesPanel(ArcImsWizard parent) {
        super();
        parentWizard = parent;
        initialize();
    }

    private void initialize() {
        this.setSize(501, 470);
        this.setLayout(null);
        this.add(getNorthPanel(), null);
        tableSelectionListener = new ServicesTableSelectionListener(this);
        this.add(getServicesTableScrollPane(), null);
        this.add(getNextButton(), null);
        this.add(getPreviousButton(), null);
        this.add(getUserDefinedServiceNamePanel(), null);
        this.nextButton.setEnabled(false);

        versionTitleLabel = new JLabel(PluginServices.getText(this,
                    "server_version") + ":");
        versionTitleLabel.setBounds(new java.awt.Rectangle(39, 443, 125, 21));
        this.add(versionTitleLabel, null);
        versionLabel = new JLabel("-");
        versionLabel.setBounds(new java.awt.Rectangle(170 - 15, 443, 160, 21));
        this.add(versionLabel, null);

        servicesTable.addMouseListener(this);
        servicesTable.setBounds(new java.awt.Rectangle(15, 45, 400, 181));
        servicesTable.setDragEnabled(false);
        servicesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        servicesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        fireServerUrlUnknown();
    }

    /**
     * Gets the service type of the selected service (ImageService or
     * FeatureService)
     *
     * @return service type of the service selected by the user
     */
    public String getSelectedServiceType() {
        return selectedServiceType;
    }

    /**
     * This method initializes northPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getNorthPanel() {
        if (northPanel == null) {
            serverLabel = new JLabel();
            serverLabel.setText("URL:");
            serverLabel.setBounds(new java.awt.Rectangle(10, 20, 71, 20));

            versionTitleLabel = new JLabel(PluginServices.getText(this,
                        "version") + ":");
            versionTitleLabel.setBounds(new java.awt.Rectangle(10, 40, 71, 20));
            versionLabel = new JLabel("-");
            versionLabel.setBounds(new java.awt.Rectangle(60, 40, 200, 20));

            cacheOverride = new JCheckBox(PluginServices.getText(this,
                        "update_catalog"), false);
            cacheOverride.setBounds(new java.awt.Rectangle(7, 60 - 9, 200, 20));

            northPanel = new JPanel();
            northPanel.setLayout(null);
            northPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, PluginServices.getText(this, "server"),
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            northPanel.setBounds(new java.awt.Rectangle(17, 10, 477, 85)); // hasta y = 96
                                                                           // northPanel.add(serverLabel, null);

            northPanel.add(getServerComboBox(), null);
            northPanel.add(getConnectButton(), null);

            // northPanel.add(versionTitleLabel);
            // northPanel.add(versionLabel);
            northPanel.add(cacheOverride);
        }

        return northPanel;
    }

    /**
     * Initializes the JComboBox that contains the URLs of the favorite servers,
     * read from a XML file located on the user's local directory.
     *
     * @return javax.swing.JComboBox
     */
    public JComboBox getServerComboBox() {
        if (serverComboBox == null) {
            serverComboBox = new JComboBox();
            serverComboBox.setEditable(true);

            serverComboBox.getEditor().getEditorComponent().addKeyListener(this);
            serverComboBox.addItemListener(this);

            loadFavoriteServers();
            addServersFromVector();

            serverComboBox.setBounds(new java.awt.Rectangle(11, 26, 380 + 74, 20));
        }

        return serverComboBox;
    }

    /**
     * Inicializes the connectButton
     *
     * @return javax.swing.JButton
     */
    public JButton getConnectButton() {
        if (connectButton == null) {
            connectButton = new JButton();
            connectButton.setText(PluginServices.getText(this, "conectar"));
            connectButton.setBounds(new java.awt.Rectangle(383 - 17, 33 + 17,
                    100, 20));

            connectButton.addActionListener(this);
        }

        return connectButton;
    }

    /**
     * Inicializes the servicesTableScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getServicesTableScrollPane() {
        if (servicesTableScrollPane == null) {
            servicesTableScrollPane = new JScrollPane();
            servicesTableScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, PluginServices.getText(this, "available_services"),
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));

            servicesTableScrollPane.setBounds(new java.awt.Rectangle(17, 152,
                    477, 261 + 12)); // hasta y = 413
        }

        return servicesTableScrollPane;
    }

    /**
     * Inicializes the nextButton, which will enable and open a new panel
     * depending on the user's selection
     *
     * @return javax.swing.JButton
     */
    public JButton getNextButton() {
        if (nextButton == null) {
            nextButton = new JButton(); // 
            nextButton.setText(PluginServices.getText(this, "siguiente"));
            nextButton.setBounds(new java.awt.Rectangle(395, 444, 100, 20));
            nextButton.addActionListener(this);
        }

        return nextButton;
    }

    public JButton getPreviousButton() {
        if (previousButton == null) {
            previousButton = new JButton(); // 
            previousButton.setText(PluginServices.getText(this, "anterior"));
            previousButton.setBounds(new java.awt.Rectangle(395 - 103, 444,
                    100, 20));
            previousButton.setEnabled(false);
            previousButton.addActionListener(this);
        }

        return previousButton;
    }

    public void setServerComboText(String s) {
        this.serverComboBox.addItem(s);
        this.serverComboBox.setSelectedItem(s);
    }

    /**
     * Writes the list of ArcIMS servers contained in the serverComboBox on a
     * XML local file. It should remove server URLs from unsuccessful
     * connections.
     *
     * @param v
     *            a vector of Strings (servers' URLs)
     */
    private void writeServerVectorToXML(Vector v) {
        String s = "";

        for (int i = 0; i < v.size(); i++)
            s = s + "," + ((String) v.get(i));

        s = s.substring(1);

        XMLEntity xml = PluginServices.getPluginServices(this).getPersistentXML();

        if (xml == null) {
            xml = new XMLEntity();
            xml.putProperty("arcims-servers", s);
        }
        else {
            xml.putProperty("arcims-servers", s);
        }
    }

    /**
     * Loads a local variable (favoriteServers) with a list of servers and the
     * ones found in the local persistent data XML file.
     *
     */
    private void loadFavoriteServers() {
        favoriteServers.add("http://www.geographynetwork.com");
        favoriteServers.add("http://massotti.carm.es");
        // favoriteServers.add("http://cartoweb.paeria.es"); // Ayunt. de Lerida
        // favoriteServers.add("http://geodaten.stadt-bottrop.de"); // Ayunt. de Bottrop (Alemania)
        favoriteServers.add("http://comercio.ideam.gov.co");
        favoriteServers.add("http://atl.cenapred.unam.mx");
        // favoriteServers.add("http://gdz1.leipzig.ifag.de");

        XMLEntity xml = PluginServices.getPluginServices(this).getPersistentXML();

        if (xml == null) {
            return;
        }

        try {
            String[] s = xml.getStringArrayProperty("arcims-servers");
            String[] servers = s[0].split(",");

            for (int i = 0; i < servers.length; i++)
                addIfNotRepeated(favoriteServers, servers[i]);
        }
        catch (NotExistInXMLEntity e) {
            logger.warn("Property 'arcims-servers' not found in XML file. ");
        }
    }

    private void addIfNotRepeated(Vector v, String s) {
        if (v.contains(s)) {
            return;
        }

        v.add(0, s);
    }

    private void removeServerFromVector(Vector v, String s) {
        if (!(v.contains(s))) {
            return;
        }

        v.remove(s);
    }

    /**
     * Utility method to load the server names from a 'favoriteServers' vector
     * into the server names combobox.
     */
    private void addServersFromVector() {
        for (int i = 0; i < favoriteServers.size(); i++)
            this.serverComboBox.addItem((String) favoriteServers.get(i));
    }

    /**
     * Listens to mouse events on the available services list. A double click
     * will have the same effect as a click on the nextButton.
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == this.servicesTable) {
            if (!this.servicesTable.isEnabled()) {
                return;
            }

            int selrow = servicesTable.getSelectedRow();

            if (selrow == -1) {
                return;
            }

            String servname = ServicesTableModel.getColumnValueOfRow(servicesTable,
                    PluginServices.getText(this, "name"), selrow);
            userDefinedServiceNameTextField.setText(servname);

            // double click event:
            if (e.getClickCount() == 2) {
                ActionEvent artificialEvent = new ActionEvent(nextButton,
                        ActionEvent.ACTION_PERFORMED, "");
                this.actionPerformed(artificialEvent);
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Listens to button events: connectButton will retrieve the server's
     * available services list (catalog). NextButton will be enabled if a valid
     * service (imageservice or featureservice) has been selected by the user.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connectButton) {
            // ---------------------- CONNECT BUTTON - START
            this.nextButton.setEnabled(false);

            boolean badURL = false;
            URL url = null;
            ServicesTableDataSource stds = null;
            url = null;

            // find out complete url (using libArcIMS):
            try {
                url = new URL(serverComboBox.getSelectedItem().toString());
                completeURL = ArcImsProtocolHandler.getUrlWithServlet(url);

                // this truly loads the service names, types and status:
                stds = new ServicesTableDataSource(completeURL,
                        cacheOverride.isSelected());
            }
            catch (MalformedURLException e1) {
                badURL = true;
            }
            catch (ArcImsException e1) {
                badURL = true;
            }

            if (badURL) {
                logger.info("Wrong url");
                JOptionPane.showMessageDialog(this,
                    PluginServices.getText(this, "wrong_url") + ": " +
                    serverComboBox.getSelectedItem().toString(),
                    PluginServices.getText(this, "wrong_url"),
                    JOptionPane.ERROR_MESSAGE);

                return;
            }
            else {
                // check that the list is non-empty:
                if (stds.getDataVector().size() == 0) {
                    JOptionPane.showMessageDialog(this,
                        PluginServices.getText(this, "server_has_no_services") +
                        ": " + url,
                        PluginServices.getText(this, "server_has_no_services"),
                        JOptionPane.ERROR_MESSAGE);

                    // remove useless url:
                    removeServerFromVector(favoriteServers, url.toString());
                    // writeServerVectorToXML(favoriteServers);

                    return;
                }
            }

            // enables certain controls if the url is ok and there are services:
            fireServerUrlOk();

            // save working url:
            favoriteServers.add(0, url.toString());
            writeServerVectorToXML(favoriteServers);

            parentWizard.setHostName(completeURL.toString());

            ServicesTableModel dtm = new ServicesTableModel(stds.getDataVector(),
                    stds.getColNamesVector());
            setModelAndWidths(servicesTable, dtm);

            // Ask to be notified of selection changes.
            ListSelectionModel rowSM = servicesTable.getSelectionModel();
            rowSM.addListSelectionListener(this.tableSelectionListener);

            servicesTableScrollPane.setViewportView(servicesTable);
            servicesTable.repaint();
            servicesTableScrollPane.repaint();

            // setEnableComponent(servicesTable, serviceNameSelectionModeListRadioButton.isSelected());
            // ---------------------- CONNECT BUTTON - END
        }

        // ---------------------- NEXT BUTTON - START
        if (e.getSource() == nextButton) {
            // if the 'user defined' option is set, local variables get values from
            // the combobox
            // if (serviceNameSelectionModeUserRadioButton.isSelected()) {
            String userServiceName = userDefinedServiceNameTextField.getText();
            int ind = -1;

            try {
                ind = ServicesTableModel.getFirstRowWithValueInColumnName(servicesTable,
                        PluginServices.getText(this, ServiceInfoTags.aNAME),
                        userServiceName);
            }
            catch (ArcImsException e1) {
                logger.error("While searching value in table ", e1);
                ind = -1;
            }

            if (ind == -1) {
                JOptionPane.showMessageDialog(this,
                    PluginServices.getText(this, "service_does_not_exist") +
                    ": " + userServiceName, "Error", JOptionPane.ERROR_MESSAGE);

                return;
            }

            selectedServiceType = ServicesTableModel.getColumnValueOfRowWithIndex(servicesTable,
                    1, ind);
            selectedServiceName = userServiceName;

            // }

            // check that service type is supported:
            if (!isSupportedServiceType(selectedServiceName)) {
                return;
            }

                // +++++++++++++  This line truly starts the connection:          ++++++++++++
                // +++++++++++++  creates driver, client and gets capabilities    ++++++++++++
                try {
					parentWizard.getDataSource()
					            .setHostService(completeURL, selectedServiceName,
					    selectedServiceType);
				} catch (DriverException de) {
	                logger.error("While setting host service ", de);
	                showErrorMessage(de.getMessage());
	                return;
				}

            // get service information:
            ArcImsClientP cli = null;

            if (selectedServiceType.compareToIgnoreCase(
                        ServiceInfoTags.vIMAGESERVICE) == 0) {
                FMapRasterArcImsDriver idrv = (FMapRasterArcImsDriver) parentWizard.getDataSource()
                                                                                   .getDriver();
                cli = (ArcImsClientP) idrv.getClient();
            }

            if (selectedServiceType.compareToIgnoreCase(
                        ServiceInfoTags.vFEATURESERVICE) == 0) {
                FMapFeatureArcImsDriver fdrv = (FMapFeatureArcImsDriver) parentWizard.getDataSource()
                                                                                     .getDriver();
                cli = (ArcImsClientP) fdrv.getClient();
            }

            ServiceInformation si = cli.getServiceInformation();

            // 
            if ((si.getLayers() == null) || (si.getLayers().size() == 0)) {
                logger.warn("No layers found ");
                JOptionPane.showMessageDialog(this,
                    PluginServices.getText(this, "no_layers_found") + ". " +
                    PluginServices.getText(this, "service") + ": " +
                    selectedServiceName,
                    PluginServices.getText(this, "no_layers_found"),
                    JOptionPane.ERROR_MESSAGE);

                return;
            }

            parentWizard.setServiceName(selectedServiceName);
            parentWizard.setServiceType(selectedServiceType);
            parentWizard.fillAndMoveTabbedPaneToEnabled();

            // ---------------------- NEXT BUTTON - END
        }
    }

    public void pseudoNextFired(FLayer lyr) {
        // if the 'user defined' option is set, local variables get values from
        // the combobox
        // if (serviceNameSelectionModeUserRadioButton.isSelected()) {
        String userServiceName = userDefinedServiceNameTextField.getText();
        int ind = -1;

        try {
            ind = ServicesTableModel.getFirstRowWithValueInColumnName(servicesTable,
                    PluginServices.getText(this, ServiceInfoTags.aNAME),
                    userServiceName);
        }
        catch (ArcImsException e1) {
            logger.error("While searching value in table ", e1);
            ind = -1;
        }

        if (ind == -1) {
            JOptionPane.showMessageDialog(this,
                PluginServices.getText(this, "service_does_not_exist") + ": " +
                userServiceName, "Error", JOptionPane.ERROR_MESSAGE);

            return;
        }

        selectedServiceType = ServicesTableModel.getColumnValueOfRowWithIndex(servicesTable,
                1, ind);
        selectedServiceName = userServiceName;

        // }

        // check that service type is supported:
        if (!isSupportedServiceType(selectedServiceName)) {
            return;
        }

        try {
//             +++++++++++++  This line truly starts the connection:          ++++++++++++
//             +++++++++++++  creates driver, client and gets capabilities    ++++++++++++
            parentWizard.getDataSource()
                        .setHostService(completeURL, selectedServiceName,
                selectedServiceType, lyr);
        }
        catch (DriverException de) {
            logger.error("While setting host service ", de);
            showErrorMessage(de.getMessage());

            return;
        }

        // get service information:
        ArcImsClientP cli = null;

        if (selectedServiceType.compareToIgnoreCase(
                    ServiceInfoTags.vIMAGESERVICE) == 0) {
            FMapRasterArcImsDriver idrv = (FMapRasterArcImsDriver) parentWizard.getDataSource()
                                                                               .getDriver();
            cli = (ArcImsClientP) idrv.getClient();
        }

        if (selectedServiceType.compareToIgnoreCase(
                    ServiceInfoTags.vFEATURESERVICE) == 0) {
            FMapFeatureArcImsDriver fdrv = (FMapFeatureArcImsDriver) parentWizard.getDataSource()
                                                                                 .getDriver();
            cli = (ArcImsClientP) fdrv.getClient();
        }

        ServiceInformation si = cli.getServiceInformation();

        // 
        if ((si.getLayers() == null) || (si.getLayers().size() == 0)) {
            logger.warn("No layers found ");
            JOptionPane.showMessageDialog(this,
                PluginServices.getText(this, "no_layers_found") + ". " +
                PluginServices.getText(this, "service") + ": " +
                selectedServiceName,
                PluginServices.getText(this, "no_layers_found"),
                JOptionPane.ERROR_MESSAGE);

            return;
        }

        parentWizard.setServiceName(selectedServiceName);
        parentWizard.setServiceType(selectedServiceType);
        parentWizard.fillAndMoveTabbedPaneToEnabled();
    }

    private void setModelAndWidths(JTable table, ServicesTableModel m) {
        table.setModel(m);

        TableColumnModel cm = table.getColumnModel();
        TableColumn col;
        int count = cm.getColumnCount();

        if (count == 3) {
            col = cm.getColumn(0);
            col.setPreferredWidth(230);
            col = cm.getColumn(1);
            col.setPreferredWidth(110);
            col = cm.getColumn(2);
            col.setPreferredWidth(110);
            table.repaint();
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this,
            PluginServices.getText(this, message),
            PluginServices.getText(this, "error"), JOptionPane.ERROR_MESSAGE);
    }

    private boolean servicesTableHasSelection() {
        return (servicesTable.getSelectedRow() != -1);
    }

    /**
     * Utility method to manage the services names table.
     *
     * @param sName service name
     * @return row that contains it
     */
    public int getRowNumberOfServiceName(String sName) {
        int l = servicesTable.getRowCount();
        String s;

        for (int i = 0; i < l; i++) {
            s = ServicesTableModel.getColumnValueOfRow(servicesTable,
                    PluginServices.getText(this, "name"), i);

            if (s.compareToIgnoreCase(sName) == 0) {
                return i;
            }
        }

        logger.error("Service name not found: " + sName);

        return -1;
    }

    public JTable getServicesTable() {
        return servicesTable;
    }

    public void setServicesTable(JTable servicesTable) {
        this.servicesTable = servicesTable;
    }

    public void setSelectedServiceType(String selectedServiceType) {
        this.selectedServiceType = selectedServiceType;
    }

    public String getSelectedServiceName() {
        return selectedServiceName;
    }

    public void setSelectedServiceName(String selectedServiceName) {
        this.selectedServiceName = selectedServiceName;
    }

    public ArcImsWizard getParentWizard() {
        return parentWizard;
    }

    public void setParentWizard(ArcImsWizard parentWizard) {
        this.parentWizard = parentWizard;
    }

    public ServicesTableSelectionListener getTableSelectionListener() {
        return tableSelectionListener;
    }

    public void setTableSelectionListener(
        ServicesTableSelectionListener tableSelectionListener) {
        this.tableSelectionListener = tableSelectionListener;
    }

    /**
     * This method initializes userDefinedServiceNamePanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getUserDefinedServiceNamePanel() {
        if (userDefinedServiceNamePanel == null) {
            userDefinedServiceNamePanel = new JPanel();
            userDefinedServiceNamePanel.setBounds(new java.awt.Rectangle(17,
                    97, 477, 56)); // hasta y = 151
            userDefinedServiceNamePanel.setLayout(null);
            userDefinedServiceNamePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, PluginServices.getText(this, "service_name"),
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            userDefinedServiceNameTextField = new JTextField();
            userDefinedServiceNameTextField.setBounds(11, 21, 380 + 74, 20);
            userDefinedServiceNameTextField.addKeyListener(this);

            serviceNameLabel = new JLabel(PluginServices.getText(this,
                        "service_name"));
            serviceNameLabel.setBounds(70, 21, 120, 20);

            // serviceSelectionModeButtonGroup = new ButtonGroup();
            // serviceSelectionModeButtonGroup.add(getServiceNameSelectionModeRadioButton());
            // serviceSelectionModeButtonGroup.add(getServiceNameSelectionModeListRadioButton());
            userDefinedServiceNamePanel.add(userDefinedServiceNameTextField);

            // userDefinedServiceNamePanel.add(serviceNameLabel);
        }

        return userDefinedServiceNamePanel;
    }

    /**
     * This method initializes serviceNameSelectionModeRadioButton
     *
     * @return javax.swing.JRadioButton
     */

    /*
    public JRadioButton getServiceNameSelectionModeRadioButton() {
            if (serviceNameSelectionModeUserRadioButton == null) {
                    serviceNameSelectionModeUserRadioButton = new JRadioButton();
                    serviceNameSelectionModeUserRadioButton.addActionListener(this);
                    serviceNameSelectionModeUserRadioButton
                                    .setBounds(new java.awt.Rectangle(170, 20, 160, 20));
                    serviceNameSelectionModeUserRadioButton.setText(PluginServices
                                    .getText(this, "defined_by_user") + ":");
            }
            return serviceNameSelectionModeUserRadioButton;
    }
    */

    /**
     * This method initializes serviceNameSelectionModeListRadioButton
     *
     * @return javax.swing.JRadioButton
     */

    /*
    private JRadioButton getServiceNameSelectionModeListRadioButton() {
            if (serviceNameSelectionModeListRadioButton == null) {
                    serviceNameSelectionModeListRadioButton = new JRadioButton();
                    serviceNameSelectionModeListRadioButton.addActionListener(this);
                    serviceNameSelectionModeListRadioButton
                                    .setBounds(new java.awt.Rectangle(10, 20, 160, 20));
                    serviceNameSelectionModeListRadioButton.setText(PluginServices
                                    .getText(this, "selected_from_list"));
    
            }
            return serviceNameSelectionModeListRadioButton;
    }
    */
    private void setEnableComponent(JComponent c, boolean v) {
        if (c == null) {
            return;
        }

        c.setEnabled(v);

        if (c instanceof JTable) {
            if (v) {
                c.setForeground(Color.BLACK);
            }
            else {
                c.setForeground(Color.LIGHT_GRAY);
            }
        }
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        if (e.getSource() == userDefinedServiceNameTextField) {
            boolean user = true;
            int length = userDefinedServiceNameTextField.getText().length();
            setServiceType("");
            nextButton.setEnabled(user && (length > 0));
        }

        if (e.getSource() == serverComboBox.getEditor().getEditorComponent()) {
            fireServerUrlUnknown();
        }
    }

    private void fireServerUrlUnknown() {
        // setEnableComponent(serviceNameSelectionModeListRadioButton, false);
        // setEnableComponent(serviceNameSelectionModeUserRadioButton, false);
        setEnableComponent(servicesTable, false);
        setEnableComponent(nextButton, false);
        setEnableComponent(userDefinedServiceNameTextField, false);

        if (versionLabel != null) {
            versionLabel.setText("-");
        }
    }

    private void fireServerUrlOk() {
        // setEnableComponent(serviceNameSelectionModeListRadioButton, true);
        // setEnableComponent(serviceNameSelectionModeUserRadioButton, true);
        setEnableComponent(servicesTable, true);
        setEnableComponent(nextButton, true);

        String str = "-";

        if (versionLabel != null) {
            str = getServerVersion(completeURL);
            versionLabel.setText(str);
            parentWizard.setServerVersionInPanels(str);
        }

        // serviceNameSelectionModeListRadioButton.setSelected(true);
        // ActionEvent ae = new ActionEvent(serviceNameSelectionModeListRadioButton, ActionEvent.ACTION_PERFORMED, "");
        // actionPerformed(ae);
        setUserDecisionTrue();
    }

    /**
     * This method calls the liArcIMS library to quickly find out
     * the server full url (with '/servlet/...')
     *
     * @param url server short url
     * @return full url
     */
    private String getServerVersion(URL url) {
        String[] vers;

        try {
            vers = ArcImsProtocolHandler.getVersion(url);
        }
        catch (ArcImsException e) {
            logger.error("Server version not found ", e);
            JOptionPane.showMessageDialog(this,
                PluginServices.getText(this, "unable_to_findout_server_version"),
                PluginServices.getText(this, "unable_to_findout_server_version"),
                JOptionPane.ERROR_MESSAGE);

            return "-";
        }

        return vers[0] + " (" + vers[1] + ")";
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setUserServiceName(String str) {
        userDefinedServiceNameTextField.setText(str);
    }

    public void itemStateChanged(ItemEvent e) {
        this.fireServerUrlUnknown();
    }

    /**
     *
     * Utility method to decide if a service type is supported
     * or not (ServiceInfoTags.IMAGESERVICE, etc)
     */
    private boolean isSupportedServiceType(String svcName) {
        int ind = -1;

        try {
            ind = ServicesTableModel.getFirstRowWithValueInColumnIndex(servicesTable,
                    0, svcName);
        }
        catch (ArcImsException e1) {
            logger.error("While searching value in table ", e1);
            ind = -1;
        }

        if (ind == -1) {
            // should never happen
            JOptionPane.showMessageDialog(this,
                PluginServices.getText(this, "service_does_not_exist") + ": " +
                svcName, "Error", JOptionPane.ERROR_MESSAGE);

            return false;
        }

        String svcType = ServicesTableModel.getColumnValueOfRowWithIndex(servicesTable,
                1, ind);

        if ((svcType.compareToIgnoreCase(ServiceInfoTags.vIMAGESERVICE) != 0) &&
                (svcType.compareToIgnoreCase(ServiceInfoTags.vFEATURESERVICE) != 0)) {
            showNotImplementedMessage();

            return false;
        }

        return true;
    }

    private void showNotImplementedMessage() {
        JOptionPane.showMessageDialog(this,
            PluginServices.getText(this, "service_type_not_supported"),
            PluginServices.getText(this, "error"), JOptionPane.ERROR_MESSAGE);
    }

    public void setUserDecisionTrue() {
        setEnableComponent(userDefinedServiceNameTextField, true);
        setEnableComponent(servicesTable, true);

        int length = userDefinedServiceNameTextField.getText().length();
        setEnableComponent(nextButton, (length > 0));
    }
} // @jve:decl-index=0:visual-constraint="10,8"
