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
package com.iver.cit.gvsig.project.documents.table.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.addlayer.AddLayerDialog;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class DataBaseOpenDialog extends JPanel implements KeyListener, FocusListener {
    private JLabel jLabel = null;
    private JTextField txtHost = null;
    private JLabel jLabel1 = null;
    private JTextField txtUser = null;
    private JComboBox cmbDriver = null;
    private JLabel jLabel2 = null;

	private JLabel jLabel3 = null;
	private JTextField txtTable = null;
	private JLabel jLabel4 = null;
	private JTextField txtPort = null;
	private JLabel jLabel5 = null;
	private JPasswordField txtPassword = null;
	private JLabel jLabel6 = null;
	private JTextField txtDB = null;
	private AddLayerDialog dialog = null;
	
    /**
     * This is the default constructor
     */
    public DataBaseOpenDialog(AddLayerDialog dialog) {
        super();
        this.dialog = dialog;
        initialize();
    }

    /**
     * This is the default constructor
     */
    public DataBaseOpenDialog() {
        super();
        initialize();
    }
    
    private boolean checkFields() {
    	if(txtHost.getText().compareTo("") != 0 &&
    		txtTable.getText().compareTo("") != 0 &&
    		txtPort.getText().compareTo("") != 0 &&
    		txtDB.getText().compareTo("") != 0)
    		return true;
    	return false;
    }
    
    /**
     * This method initializes this
     */
    private void initialize() {
        jLabel6 = new JLabel();
        jLabel5 = new JLabel();
        jLabel4 = new JLabel();
        jLabel3 = new JLabel();
        jLabel2 = new JLabel();
        jLabel1 = new JLabel();
        jLabel = new JLabel();
        this.setLayout(null);
        this.setSize(348, 243);
        jLabel.setBounds(5, 12, 99, 23);
        jLabel.setText(PluginServices.getText(this,"Servidor")+":");
        jLabel1.setBounds(5, 140, 99, 23);
        jLabel1.setText(PluginServices.getText(this,"usuario")+":");
        jLabel2.setBounds(5, 204, 99, 23);
        jLabel2.setText(PluginServices.getText(this,"driver")+":");
        jLabel3.setBounds(5, 108, 99, 23);
        jLabel3.setText(PluginServices.getText(this,"Tabla")+":");
        jLabel4.setBounds(5, 44, 99, 23);
        jLabel4.setText(PluginServices.getText(this,"puerto")+":");
        jLabel5.setBounds(5, 172, 99, 23);
        jLabel5.setText(PluginServices.getText(this,"clave")+":");
        jLabel6.setBounds(5, 76, 99, 23);
        jLabel6.setText(PluginServices.getText(this,"base_datos")+":");
        this.add(jLabel, null);
        this.add(getTxtHost(), null);
        this.add(jLabel1, null);
        this.add(getTxtUser(), null);
        this.add(getCmbDriver(), null);
        this.add(jLabel2, null);
        this.add(jLabel3, null);
        this.add(getTxtTable(), null);
        this.add(jLabel4, null);
        this.add(getTxtPort(), null);
        this.add(jLabel5, null);
        this.add(getTxtPassword(), null);
        this.add(jLabel6, null);
        this.add(getTxtDB(), null);
        
        getTxtHost().addKeyListener(this);
        getTxtUser().addKeyListener(this);
        getTxtTable().addKeyListener(this);
        getTxtPort().addKeyListener(this);
        getTxtDB().addKeyListener(this);
        getTxtPassword().addKeyListener(this);
        
        getTxtHost().addFocusListener(this);
        getTxtUser().addFocusListener(this);
        getTxtTable().addFocusListener(this);
        getTxtPort().addFocusListener(this);
        getTxtDB().addFocusListener(this);
        getTxtPassword().addFocusListener(this);
    }

    /**
     * This method initializes txtHost
     *
     * @return javax.swing.JTextField
     */
    private JTextField getTxtHost() {
        if (txtHost == null) {
            txtHost = new JTextField();
            txtHost.setBounds(118, 12, 200, 23);
        }

        return txtHost;
    }

    /**
     * This method initializes txtUser
     *
     * @return javax.swing.JTextField
     */
    private JTextField getTxtUser() {
        if (txtUser == null) {
            txtUser = new JTextField();
            txtUser.setBounds(118, 140, 130, 23);
        }

        return txtUser;
    }

    /**
     * This method initializes cmbDriver
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox getCmbDriver() {
        if (cmbDriver == null) {
            cmbDriver = new JComboBox();
            cmbDriver.setBounds(118, 204, 200, 23);
        }

        return cmbDriver;
    }

    /**
     * @see com.iver.cit.gvsig.gui.OpenLayerPanel#getDriverNames()
     */
    public String getDriverName() {
        return cmbDriver.getSelectedItem().toString();
    }

    public void setClasses(Class[] classes){
        	DefaultComboBoxModel model = new DefaultComboBoxModel();
			String[] driverNames = LayerFactory.getDM().getDriverNames();
			for (int i = 0; i < driverNames.length; i++) {
				//boolean is = false;
				for (int j = 0; j < classes.length; j++) {
					if (LayerFactory.getDM().isA(driverNames[i], classes[j])){
						model.addElement(driverNames[i]);
					}
				}
			}
			cmbDriver.setModel(model);
    }

    public String getHost(){
    	return txtHost.getText();
    }

    public String getUser(){
    	return txtUser.getText();
    }

    public String getPassword(){
    	char[] buffer = txtPassword.getPassword();

    	String str = new String(buffer);

    	for (int i = 0; i < buffer.length; i++) {
    		buffer[i] = '\0';
    	}

    	return str;
    }

    public String getTable(){
    	return txtTable.getText();
    }

    public String getDataBase(){
    	return txtDB.getText();
    }

    public String getPort(){
    	return txtPort.getText();
    }

    /**
	 * This method initializes txtTable
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtTable() {
		if (txtTable == null) {
			txtTable = new JTextField();
			txtTable.setBounds(118, 108, 200, 23);
		}
		return txtTable;
	}
	/**
	 * This method initializes txtPort
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtPort() {
		if (txtPort == null) {
			txtPort = new JTextField();
			txtPort.setBounds(118, 44, 46, 23);
		}
		return txtPort;
	}
	/**
	 * This method initializes txtPassword
	 *
	 * @return javax.swing.JPasswordField
	 */
	private JPasswordField getTxtPassword() {
		if (txtPassword == null) {
			txtPassword = new JPasswordField();
			txtPassword.setBounds(118, 172, 130, 23);
		}
		return txtPassword;
	}
	/**
	 * This method initializes txtDB
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtDB() {
		if (txtDB == null) {
			txtDB = new JTextField();
			txtDB.setBounds(118, 76, 200, 23);
		}
		return txtDB;
	}

	public void keyPressed(KeyEvent e) {
		
	}

	public void keyReleased(KeyEvent e) {
		if(dialog != null) {
			if(checkFields())
				dialog.getJPanel().setOkButtonEnabled(true);
			else
				dialog.getJPanel().setOkButtonEnabled(false);
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void focusGained(FocusEvent e) {
	}

	public void focusLost(FocusEvent e) {
		if(dialog != null) {
			if(checkFields())
				dialog.getJPanel().setOkButtonEnabled(true);
			else
				dialog.getJPanel().setOkButtonEnabled(false);
		}
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
