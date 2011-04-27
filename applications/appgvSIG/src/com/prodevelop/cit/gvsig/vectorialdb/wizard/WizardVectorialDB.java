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
package com.prodevelop.cit.gvsig.vectorialdb.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.cresques.cts.IProjection;
import org.gvsig.gui.beans.swing.JButton;

import com.hardcode.driverManager.DriverLoadException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.SingleVectorialDBConnectionExtension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.ICanReproject;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver;
import com.iver.cit.gvsig.fmap.drivers.db.utils.ConnectionWithParams;
import com.iver.cit.gvsig.fmap.drivers.db.utils.SingleVectorialDBConnectionManager;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.gui.WizardPanel;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.swing.JPasswordDlg;


/**
 * Driver-independent GeoDB wizard. Queries the drivers to fill GUI controls.
 * Multi-table selection available.
 *
 * @author jldominguez
 *
 */
public class WizardVectorialDB extends WizardPanel implements ActionListener,
    ListSelectionListener {
    private static Logger logger = Logger.getLogger(WizardVectorialDB.class.getName());

    // private DefaultDBDriver driver;
    protected IConnection conex = null;
    private ConnectionWithParams selectedDataSource = null;
    private JPanel namePanel = null;
    private JPanel tablesPanel = null;
    private JScrollPane tablesScrollPane = null;
    private AvailableTablesCheckBoxList tablesList = null;
    private JComboBox datasourceComboBox = null;
    private UserTableSettingsPanel settingsPanel = null;
    private UserSelectedFieldsPanel fieldsPanel = null;
    private UserTableSettingsPanel emptySettingsPanel = null;
    private UserSelectedFieldsPanel emptyFieldsPanel = null;
    private JButton dbButton = null;
    private BaseView view = null;

    public WizardVectorialDB() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        setTabName(PluginServices.getText(this, "GeoDB"));
        setLayout(null);
        setSize(512, 478);

        IWindow iw = PluginServices.getMDIManager().getActiveWindow();

        if (iw == null) {
            return;
        }

        if (!(iw instanceof BaseView)) {
            return;
        }

        view = (BaseView) iw;
        setMapCtrl(view.getMapControl());

        emptySettingsPanel = new UserTableSettingsPanel(null, null, "",
        		getMapCtrl(), true, this, null);
        emptyFieldsPanel = new UserSelectedFieldsPanel(null, null, true, this);

        add(getNamePanel(), null);
        loadVectorialDBDatasourcesCombo();

        add(getTablesPanel(), null);
    }

    protected void loadVectorialDBDatasourcesCombo() {
        getDatasourceComboBox().removeAllItems();

        getDatasourceComboBox().addItem(new ConnectionWithParams());

        ConnectionWithParams[] conn = SingleVectorialDBConnectionManager.instance()
                                                                 .getAllConnections();

        if (conn == null) {
            return;
        }

        for (int i = 0; i < conn.length; i++) {
            getDatasourceComboBox().addItem(conn[i]);
        }
    }

    private String[] getDriverNames() {
        Class[] classes = new Class[] { IVectorialDatabaseDriver.class };

        ArrayList ret = new ArrayList();
        String[] driverNames = LayerFactory.getDM().getDriverNames();

        for (int i = 0; i < driverNames.length; i++) {
            boolean is = false;

            for (int j = 0; j < classes.length; j++) {
                if (LayerFactory.getDM().isA(driverNames[i], classes[j])) {
                    ret.add(driverNames[i]);
                }
            }
        }

        return (String[]) ret.toArray(new String[0]);
    }

    /**
     * Utility method to instantiate a IVectorialDataBaseDriver from its name.
     *
     * @param drvname driver's name
     * @return a IVectorialDataBaseDriver
     *
     * @throws DriverLoadException
     */
    public IVectorialDatabaseDriver getDriverFromName(String drvname)
        throws DriverLoadException {
        IVectorialDatabaseDriver resp =
        	(IVectorialDatabaseDriver) LayerFactory.getDM().getDriver(drvname);
        return resp;
    }

    public void initWizard() {
    }

    public void execute() {
    	SingleVectorialDBConnectionExtension.saveAllToPersistence();
    }

    /**
     * Return FLayers if user performs multi selection.
     */
    public FLayer getLayer() {
        try {
            IProjection proj = null;
            TablesListItem[] selected = getSelectedTables();
            int count = selected.length;
            String groupName = selectedDataSource.getDb() + " (" +
                conex.getNameServer() + ")";

            FLayer[] all_layers = new FLayer[count];
            String strEPSG = getMapCtrl().getViewPort().getProjection()
                                 .getAbrev();

            for (int i = 0; i < count; i++) {
                TablesListItem item = selected[i];

                IVectorialDatabaseDriver driver = null;
                driver = getDriverFromName(selectedDataSource.getDrvName());
                UserTableSettingsPanel userTableSettingsPanel=item.getUserTableSettingsPanel(getMapCtrl().getViewPort().getProjection()
    			       	.getAbrev());
                Rectangle2D _wa = userTableSettingsPanel.getWorkingArea();

                if (_wa != null) {
                    driver.setWorkingArea(_wa);
                }
                String layerName = userTableSettingsPanel.getUserLayerName();
                // Change: we parse tableName to extract schema (or owner in Oracle)
                String[] tokens = item.getTableName().split("\\u002E", 2);
                String tableName;
                String schema = null;
                if (tokens.length > 1)
                {
                	schema = tokens[0];
                	tableName = tokens[1];
                }
                else
                {
                	tableName = tokens[0];
                }

                String fidField = userTableSettingsPanel.getIdFieldName();
                String geomField = userTableSettingsPanel.getGeoFieldName();
                String[] fields = item.getUserSelectedFieldsPanel()
                .getUserSelectedFields(fidField, geomField);

                //        		fields = driver.manageGeometryField(fields, geomField);

                // driver.manageGeometryField(geomField);
                DBLayerDefinition lyrDef = new DBLayerDefinition();
                lyrDef.setName(layerName);
                lyrDef.setSchema(schema);
                lyrDef.setTableName(tableName);

                if (userTableSettingsPanel.isSqlActive()) {
                    String whereClause = userTableSettingsPanel.getWhereClause();
                    lyrDef.setWhereClause(whereClause);
                }
                else {
                    lyrDef.setWhereClause("");
                }

                lyrDef.setFieldGeometry(geomField);
                lyrDef.setFieldNames(fields);

                lyrDef.setFieldID(fidField);

                if (_wa != null) {
                    lyrDef.setWorkingArea(_wa);
                }

                lyrDef.setSRID_EPSG(strEPSG);

                if (driver instanceof ICanReproject) {
                    ((ICanReproject) driver).setDestProjection(strEPSG);
                }
                lyrDef.setHost(selectedDataSource.getHost());
                lyrDef.setPort(Integer.parseInt(selectedDataSource.getPort()));
                lyrDef.setDataBase(selectedDataSource.getDb());
                lyrDef.setUser(selectedDataSource.getUser());
                lyrDef.setPassword(selectedDataSource.getPw());

                driver.setData(conex, lyrDef);

                if (driver instanceof ICanReproject) {
                	 proj = userTableSettingsPanel.getProjection();
                }

                all_layers[i] = LayerFactory.createDBLayer(driver, layerName,
                        proj);
            }

            return layerArrayToGroup(all_layers, groupName);
        }
        catch (Exception e) {
            logger.error("While creating jdbc layer: " + e.getMessage(), e);
            NotificationManager.addError("Error al cargar la capa: " +
                e.getMessage(), e);
        }

        return null;
    }

    protected FLayer layerArrayToGroup(FLayer[] all_layers, String name) {
        if (all_layers.length == 1) {
            return all_layers[0];
        }

        MapContext mc = view.getMapControl().getMapContext();
        FLayers root = view.getMapControl().getMapContext().getLayers();

        FLayers group = new FLayers();//(mc,root);
		group.setMapContext(mc);
		group.setParentLayer(root);
        group.setName(name);

        for (int i = 0; i < all_layers.length; i++) {
            group.addLayer(all_layers[i]);
        }

        return group;
    }

    protected TablesListItem[] getSelectedTables() {
        int count = tablesList.getModel().getSize();
        ArrayList resp = new ArrayList();

        for (int i = 0; i < count; i++) {
            TablesListItem item = (TablesListItem) tablesList.getModel()
                                                             .getElementAt(i);

            if (item.isSelected()) {
                resp.add(item);
            }
        }

        return (TablesListItem[]) resp.toArray(new TablesListItem[0]);
    }

    /**
     * This method initializes namePanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getNamePanel() {
        if (namePanel == null) {
            namePanel = new JPanel();
            namePanel.setLayout(null);
            namePanel.setBounds(new java.awt.Rectangle(5, 5, 501, 51));
            namePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, PluginServices.getText(this, "choose_connection"),
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            namePanel.add(getDatasourceComboBox(), null);
            namePanel.add(getJdbcButton(), null);
        }

        return namePanel;
    }

    /**
     * This method initializes tablesPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getTablesPanel() {
        if (tablesPanel == null) {
            tablesPanel = new JPanel();
            tablesPanel.setLayout(new BorderLayout());
            tablesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, PluginServices.getText(this, "choose_table"),
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            tablesPanel.setBounds(new java.awt.Rectangle(5, 55, 246, 191));
            tablesPanel.add(getTablesScrollPane(), java.awt.BorderLayout.CENTER);
        }

        return tablesPanel;
    }

    /**
     * This method initializes settingsPanel
     *
     * @return javax.swing.JPanel
     */

    /**
     * This method initializes tablesScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getTablesScrollPane() {
        if (tablesScrollPane == null) {
            tablesScrollPane = new JScrollPane();
            tablesScrollPane.setViewportView(getTablesList());
        }

        return tablesScrollPane;
    }

    /**
     * This method initializes tablesList
     *
     * @return javax.swing.JList
     */
    private AvailableTablesCheckBoxList getTablesList() {
        if (tablesList == null) {
            tablesList = new AvailableTablesCheckBoxList(this);
            tablesList.addListSelectionListener(this);
            tablesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        return tablesList;
    }

    /**
     * This method initializes layerNameTextField
     *
     * @return javax.swing.JTextField
     */

    /**
     * This method initializes jComboBox
     *
     * @return javax.swing.JComboBox
     */
    protected JComboBox getDatasourceComboBox() {
        if (datasourceComboBox == null) {
            datasourceComboBox = new JComboBox();
            datasourceComboBox.setBounds(new java.awt.Rectangle(10, 20, 446, 21));
            datasourceComboBox.addActionListener(this);
        }

        return datasourceComboBox;
    }

    public void actionPerformed(ActionEvent arg0) {
        Object src = arg0.getSource();

        if (src == datasourceComboBox) {
            Object sel_obj = datasourceComboBox.getSelectedItem();

            if (sel_obj == null) {
                return;
            }

            if (!(sel_obj instanceof ConnectionWithParams)) {
                return;
            }

            selectedDataSource = (ConnectionWithParams) sel_obj;

            if (selectedDataSource.isNull()) {
                updateTableList(selectedDataSource);
                setEmptyPanels();

                return;
            }

            if (!selectedDataSource.isConnected()) {
                if (!tryToConnect(selectedDataSource)) {
                    datasourceComboBox.setSelectedIndex(0);

                    return;
                }
            }

            getDatasourceComboBox().repaint();
            updateTableList(selectedDataSource);

            // setEmptyPanels();
            return;
        }

        if (src == dbButton) {
            ConnectionWithParams sel = addNewConnection();

            if (sel != null) {
                loadVectorialDBDatasourcesCombo();
                getDatasourceComboBox().setSelectedItem(sel);
            }
        }
    }

    private boolean tryToConnect(ConnectionWithParams _cwp) {
        JPasswordDlg dlg = new JPasswordDlg();
        dlg.setLocationRelativeTo((Component)PluginServices.getMainFrame());
        String strMessage = PluginServices.getText(this, "conectar_jdbc");
        String strPassword = PluginServices.getText(this, "password");
        dlg.setMessage(strMessage + " [" + _cwp.getDrvName() + ", " +
            _cwp.getHost() + ", " + _cwp.getPort() + ", " + _cwp.getDb() +
            ", " + _cwp.getUser() + "]. " + strPassword + "?");

        dlg.setVisible(true);

        String clave = dlg.getPassword();

        if (clave == null) {
            return false;
        }

        try {
            _cwp.connect(clave);
        }
        catch (DBException e) {
            showConnectionErrorMessage(e.getMessage());

            return false;
        }

        return true;
    }

    private void updateTableList(ConnectionWithParams src) {
        if (src.isNull()) {
            getTablesList().setModel(new DefaultListModel());
            getTablesScrollPane().setViewportView(tablesList);
            tablesScrollPane.updateUI();

            return;
        }

        conex = src.getConnection();

        String drvName = src.getDrvName();
        String dbName = "";

        IVectorialDatabaseDriver drv = null;

        try {
            dbName = src.getConnection().getCatalogName();
            drv = (IVectorialDatabaseDriver) LayerFactory.getDM().getDriver(drvName);
        }
        catch (Exception e) {
            logger.error("While getting driver instance: " + e.getMessage(), e);
        }

        if (!(drv instanceof IVectorialDatabaseDriver)) {
            logger.error(
                "Unexpected driver type (not a DefaultDBDriver driver)");

            return;
        }

        String[] tablnames = null;

        try {
            tablnames = drv.getTableNames(conex, dbName);
        }
        catch (DBException e) {
            logger.error("While getting table names: " + e.getMessage(), e);

            return;
        }

        DefaultListModel lmodel = new DefaultListModel();

        for (int i = 0; i < tablnames.length; i++) {
            lmodel.addElement(new TablesListItem(tablnames[i], drv, conex,
                    getMapCtrl(), this));
        }

        getTablesList().setModel(lmodel);
        getTablesScrollPane().setViewportView(tablesList);
        tablesScrollPane.updateUI();
    }

    public void valueChanged(ListSelectionEvent arg0) {
        Object src = arg0.getSource();

        if (src == tablesList) {
            TablesListItem selected = (TablesListItem) tablesList.getSelectedValue();

            try {
                setSettingsPanels(selected);
            }
            catch (DBException e) {
                showConnectionErrorMessage(e.getMessage());
                tablesList.clearSelection();
                setEmptyPanels();
            }

            checkFinishable();
        }
    }

    private boolean validFormSettings() {
        int count = tablesList.getModel().getSize();

        boolean at_least_one = false;
        boolean resp = true;

        for (int i = 0; i < count; i++) {
            TablesListItem item = (TablesListItem) tablesList.getModel()
                                                             .getElementAt(i);

            if (item.isSelected()) {
                at_least_one = true;
            }

            if (item.disturbsWizardValidity()) {
                resp = false;
            }
        }

        return (at_least_one && resp);
    }

    public void checkFinishable() {
        boolean finishable = validFormSettings();
        callStateChanged(finishable);
    }

    /**
     * This method initializes jdbcButton
     *
     * @return javax.swing.JButton
     */
    private JButton getJdbcButton() {
        if (dbButton == null) {
            dbButton = new JButton();
            dbButton.addActionListener(this);
            dbButton.setToolTipText(PluginServices.getText(this,
                    "add_connection"));
            dbButton.setBounds(new java.awt.Rectangle(465, 20, 26, 21));

            String _file = createResourceUrl("images/jdbc.png").getFile();
            dbButton.setIcon(new ImageIcon(_file));
        }

        return dbButton;
    }

    private ConnectionWithParams addNewConnection() {
        ConnectionWithParams resp = null;

        VectorialDBConnectionParamsDialog newco = new VectorialDBConnectionParamsDialog();
        newco.showDialog();

        if (newco.isOkPressed()) {
            String _drvname = newco.getConnectionDriverName();
            String _host = newco.getConnectionServerUrl();
            String _port = newco.getConnectionPort();
            String _dbname = newco.getConnectionDBName();
            String _user = newco.getConnectionUser();
            String _pw = newco.getConnectionPassword();
            String _conn_usr_name = newco.getConnectionName();

            boolean hasToBeCon = newco.hasToBeConnected();

            try {
                resp = SingleVectorialDBConnectionManager.instance()
                                                  .getConnection(_drvname,
                        _user, _pw, _conn_usr_name, _host, _port, _dbname,
                        hasToBeCon);
            }
            catch (DBException e) {
                showConnectionErrorMessage(e.getMessage());

                return null;
            }
            SingleVectorialDBConnectionExtension.saveAllToPersistence();
            return resp;
        }
        else {
            return null;
        }
    }

    private void showConnectionErrorMessage(String _msg) {
        String msg = (_msg.length() > 300) ? "" : (": " + _msg);
        String title = PluginServices.getText(this, "connection_error");
        JOptionPane.showMessageDialog(this, title + msg, title,
            JOptionPane.ERROR_MESSAGE);
    }

    private java.net.URL createResourceUrl(String path) {
        return getClass().getClassLoader().getResource(path);
    }

    public void setSettingsPanels(TablesListItem actTable)
        throws DBException {
        if (actTable == null) {
            setEmptyPanels();

            return;
        }

        settingsPanel = actTable.getUserTableSettingsPanel(getMapCtrl().getViewPort().getProjection()
		       	.getAbrev());
        fieldsPanel = actTable.getUserSelectedFieldsPanel();

        removeFieldPanels();
        add(fieldsPanel);
        fieldsPanel.repaint();

        removeSettingsPanels();
        add(settingsPanel);
        settingsPanel.repaint();

        repaint();
    }

    private void setEmptyPanels() {
        removeFieldPanels();
        add(emptyFieldsPanel);
        removeSettingsPanels();
        add(emptySettingsPanel);

        settingsPanel = emptySettingsPanel;
        fieldsPanel = emptyFieldsPanel;

        repaint();
    }

    private void removeFieldPanels() {
        for (int i = 0; i < getComponentCount(); i++) {
            if (getComponent(i) instanceof UserSelectedFieldsPanel) {
                remove(i);
            }
        }
    }

    private void removeSettingsPanels() {
        for (int i = 0; i < getComponentCount(); i++) {
            if (getComponent(i) instanceof UserTableSettingsPanel) {
                remove(i);
            }
        }
    }
} //  @jve:decl-index=0:visual-constraint="10,10"
