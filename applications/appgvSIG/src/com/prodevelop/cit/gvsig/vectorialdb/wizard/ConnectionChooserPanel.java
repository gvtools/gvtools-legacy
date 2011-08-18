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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.db.utils.ConnectionWithParams;
import com.iver.cit.gvsig.fmap.drivers.db.utils.SingleDBConnectionManager;
import com.iver.utiles.swing.JPasswordDlg;



/**
 * This dialog lets the user choose an available connection.
 *
 * @author jldominguez
 *
 */
public class ConnectionChooserPanel extends JPanel implements IWindow,
    ActionListener, KeyListener {

	private static Logger logger = Logger.getLogger(ConnectionChooserPanel.class.getName());

    private JComboBox availableConnectionsComboBox = null;
    private JButton okButton = null;
    private JButton cancelButton = null;
    private JButton jdbcButton = null;

    private boolean okPressed = false;
    WindowInfo winfo = null;

    private String drvName = "";
    /**
     * This method initializes the dialog.
     * @param drv_name dialog will show this type of connections, null for all
     *
     */
    public ConnectionChooserPanel(String drv_name) {
        super();
        drvName = drv_name;
        initialize();
    }

    private void reloadCombo() {

    	getAvailableConnectionsComboBox().removeAllItems();
        getAvailableConnectionsComboBox().addItem(new ConnectionWithParams());

        ConnectionWithParams[] conn = SingleDBConnectionManager.instance()
                                                                 .getAllConnections();

        if (conn == null) {
            return;
        }

        for (int i = 0; i < conn.length; i++) {
            ConnectionWithParams cwp = conn[i];

            if (cwp.getDrvName().compareToIgnoreCase(drvName) == 0) {
                getAvailableConnectionsComboBox().addItem(cwp);
            }
        }
    }

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        winfo = new WindowInfo(WindowInfo.MODALDIALOG);
        winfo.setHeight(119 - 70);
        winfo.setWidth(395);
        winfo.setTitle(PluginServices.getText(this, "choose_connection"));

        this.setLayout(null);
        this.setSize(new java.awt.Dimension(395, 89));
        this.add(getAvailableConnectionsComboBox(), null);
        this.add(getJdbcButton(), null);
        this.add(getOkButton(), null);
        this.add(getCancelButton(), null);

        reloadCombo();
    }

    public WindowInfo getWindowInfo() {
        return winfo;
    }

    /**
     * This method initializes availableConnectionsComboBox
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox getAvailableConnectionsComboBox() {
        if (availableConnectionsComboBox == null) {
            availableConnectionsComboBox = new JComboBox();
            availableConnectionsComboBox.addActionListener(this);
            availableConnectionsComboBox.setBounds(new java.awt.Rectangle(10,
                    15, 371 - 31, 21));
        }

        return availableConnectionsComboBox;
    }

    /**
     * This method initializes okButton
     *
     * @return javax.swing.JButton
     */
    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton(PluginServices.getText(this, "ok"));
            okButton.addActionListener(this);
            okButton.addKeyListener(this);
            okButton.setBounds(new java.awt.Rectangle(90, 50, 106, 26));
        }

        return okButton;
    }

    /**
     * This method initializes cancelButton
     *
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton(PluginServices.getText(this, "cancel"));
            cancelButton.addActionListener(this);
            cancelButton.addKeyListener(this);
            cancelButton.setBounds(new java.awt.Rectangle(200, 50, 106, 26));
        }

        return cancelButton;
    }

    public void actionPerformed(ActionEvent arg0) {
        Object src = arg0.getSource();

        if (src == okButton) {
            okPressed = true;
            PluginServices.getMDIManager().closeWindow(this);
        }

        if (src == cancelButton) {
            okPressed = false;
            PluginServices.getMDIManager().closeWindow(this);
        }

        if (src == availableConnectionsComboBox) {
            ConnectionWithParams sel = getSelectedCWP();

            if ((sel == null) || (sel.isNull())) {
            	okButton.setEnabled(false);
            	return;
            }

            if (!sel.isConnected()) {
                if (!tryToConnect(sel)) {
                    okButton.setEnabled(false);
                    availableConnectionsComboBox.setSelectedIndex(0);

                    return;
                }
            }

            okButton.setEnabled(sel.isConnected());
            okButton.requestFocus();
        }

        if (src == jdbcButton) {
            ConnectionWithParams sel = addNewConnection();

            if (sel != null) {
            	reloadCombo();
            	getAvailableConnectionsComboBox().setSelectedItem(sel);
            }
        }

    }

    public ConnectionWithParams getSelectedCWP() {
        Object sel = availableConnectionsComboBox.getSelectedItem();

        logger.debug("sel = availableConnectionsComboBox.getSelectedItem();");
        logger.debug("sel = " + sel);

        if (sel == null) {
            return null;
        }

        ConnectionWithParams resp = (ConnectionWithParams) sel;

        return resp;
    }

    public boolean isOkPressed() {
        return okPressed;
    }

    private boolean tryToConnect(ConnectionWithParams _cwp) {
        JPasswordDlg dlg = new JPasswordDlg();
        String strMessage = PluginServices.getText(this, "conectar_jdbc");
        String strPassword = PluginServices.getText(this, "password");
        dlg.setMessage(strMessage + " [" + _cwp.getDrvName() + ", " +
            _cwp.getHost() + ", " + _cwp.getPort() + ", " + _cwp.getDb() +
            ", " + _cwp.getUser() + "]. " + strPassword + "?");

        dlg.show();

        String clave = dlg.getPassword();

        if (clave == null) {
            return false;
        }

        try {
            _cwp.connect(clave);
        } catch (DBException e) {
        	showConnectionErrorMessage(e.getMessage());

            return false;
		}

        return true;
    }

    private void showConnectionErrorMessage(String _msg) {
        String msg = (_msg.length() > 300) ? "" : (": " + _msg);
        String title = PluginServices.getText(this, "connection_error");
        JOptionPane.showMessageDialog(this, title + msg, title,
            JOptionPane.ERROR_MESSAGE);
    }

    public void keyPressed(KeyEvent e) {
        Object src = e.getSource();

        if ((src == okButton) || (src == cancelButton)) {
            ActionEvent aux = new ActionEvent(src,
                    ActionEvent.ACTION_PERFORMED, "");
            actionPerformed(aux);
        }
    }

    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
    }

    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }

    private ConnectionWithParams addNewConnection() {
        ConnectionWithParams resp = null;

        DBConnectionParamsDialog newco = new DBConnectionParamsDialog();
        newco.showDialog();

        if (newco.isOkPressed()) {
            String _drvname = newco.getConnectionDriverName();
            String _host = newco.getConnectionServerUrl();
            String _port = newco.getConnectionPort();
            String _dbname = newco.getConnectionDBName();
            String _sche = newco.getConnectionSchema();
            String _user = newco.getConnectionUser();
            String _pw = newco.getConnectionPassword();
            String _conn_usr_name = newco.getConnectionName();

            boolean hasToBeCon = newco.hasToBeConnected();

            try {
                resp = SingleDBConnectionManager.instance()
                                                  .getConnection(_drvname,
                        _user, _pw, _conn_usr_name, _host, _port, _dbname,
                        _sche,
                        hasToBeCon);
            } catch (DBException e) {
            	  showConnectionErrorMessage(e.getMessage(), newco);
                  return null;
			}

            return resp;
        }
        else {
            return null;
        }
    }

    private void showConnectionErrorMessage(String _msg, Component parent) {
        String msg = (_msg.length() > 300) ? "" : (": " + _msg);
        String title = PluginServices.getText(this, "connection_error");
        JOptionPane.showMessageDialog(parent, title + msg, title,
            JOptionPane.ERROR_MESSAGE);
    }

    private JButton getJdbcButton() {
        if (jdbcButton == null) {
            jdbcButton = new JButton();
            jdbcButton.addActionListener(this);
            jdbcButton.setToolTipText(PluginServices.getText(this,
                    "add_connection"));
            jdbcButton.setBounds(new java.awt.Rectangle(381 - 26, 15, 26, 21));
            String _file = createResourceUrl("images/jdbc.png").getFile();
            jdbcButton.setIcon(new ImageIcon(_file));
        }

        return jdbcButton;
    }

    private java.net.URL createResourceUrl(String path) {
        return getClass().getClassLoader().getResource(path);
    }

	public Object getWindowProfile() {
		// TODO Auto-generated method stub
		return WindowInfo.DIALOG_PROFILE;
	}



} //  @jve:decl-index=0:visual-constraint="10,10"

// [eiel-gestion-conexiones]
