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
package com.iver.utiles.connections;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

//import com.iver.andami.messages.Messages;


public class JDBCManager extends JPanel{

	private JTabbedPane tabbedPanel = null;
	private JPanel pparameters = null;
	private JPanel popciones = null;
	private JComboBox cbDrivers = null;
	private JTextField txtName = null;
	private JPanel pServer = null;
	private JLabel lblDrivers = null;
	private JLabel lblName = null;
	private JPanel phost = null;
	private JTextField txtHost = null;
	private JLabel lblHost = null;
	private JLabel lblPort = null;
	private JTextField txtPort = null;
	private JPanel pUser = null;
	private JLabel lblUser = null;
	private JTextField txtUser = null;
	private JLabel lblPassword = null;
	private JPasswordField txtPassword = null;
	private JPanel pDatabase = null;
	private JLabel lblDB = null;
	private JTextField txtDB = null;
	private JButton bTestConnection = null;
	private JPanel pConnectionsTree = null;
	private JButton bAcept = null;
	private JButton bCancel = null;
	private JTree treeConnection = null;
	private ConnectionDB connectionDB=null;
	private ConnectionTrans connectionTrans=null;
	private DefaultMutableTreeNode root=null;
	private DefaultTreeModel treeModel=null;
	private JScrollPane scrollTree = null;
	private JRadioButton rbPassword = null;
	private JPopupMenu popupMenu = null;
	private JMenuItem menuConnect = null;
	private JMenuItem menuDisConnect = null;
	private JMenuItem menuDel = null;
	private JMenuItem menuRefresh = null;
	private ConnectionTrans ctTree;
	private DefaultMutableTreeNode nodeSelected=null;
	private static PropertyResourceBundle resourceBundle;
	/**
	 * This is the default constructor
	 */
	public JDBCManager(ConnectionTrans[] ct) {
		super();

        /* try {
            Class.forName("com.mysql.jdbc.Driver", true,
                    JDBCManager.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } */



		connectionDB=ConnectionDB.getInstance();
		connectionDB.setConnTrans(ct);
		initialize();
	}

/**
	 * This method initializes scrollTree
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getScrollTree() {
		if (scrollTree == null) {
			scrollTree = new JScrollPane();
			scrollTree.setPreferredSize(new java.awt.Dimension(175,255));
			scrollTree.setAutoscrolls(true);
			scrollTree.setViewportView(getTreeConnection());
		}
		return scrollTree;
	}
/**
 * This method initializes rbPassword
 *
 * @return javax.swing.JRadioButton
 */
private JRadioButton getRbPassword() {
	if (rbPassword == null) {
		rbPassword = new JRadioButton();
		rbPassword.setText(getTranslation("guardar_clave"));
		rbPassword.setSelected(true);
		rbPassword.setPreferredSize(new java.awt.Dimension(160,24));
	}
	return rbPassword;
}
/**
 * This method initializes popupMenu
 *
 * @return javax.swing.JPopupMenu
 */
private JPopupMenu getPopupMenu() {
	if (popupMenu == null) {
		popupMenu = new JPopupMenu();
		popupMenu.add(getMenuConectar());
		popupMenu.add(getMenuDisConnect());
		popupMenu.add(getMenuDel());
		popupMenu.add(getMenuRefresh());
	}
	return popupMenu;
}

/**
 * This method initializes menuConectar
 *
 * @return javax.swing.JMenuItem
 */
private JMenuItem getMenuConectar() {
	if (menuConnect == null) {
		menuConnect = new JMenuItem(getTranslation("conectar"));
		menuConnect.setEnabled(true);
		menuConnect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {

				try {
					connectionTrans=PaneltoConnectTrans();
					if (connectionDB.testDB(connectionTrans)){
						connectionTrans.setConnected(true);
						registerConnection(connectionTrans);
					}

				} catch (ConnectionException e1) {
					JOptionPane.showMessageDialog((Component)JDBCManager.this,e1);
				}
				treeModel.reload();
			}
		});
	}
	return menuConnect;
}

/**
 * This method initializes menuDisConnect
 *
 * @return javax.swing.JMenuItem
 */
private JMenuItem getMenuDisConnect() {
	if (menuDisConnect == null) {
		menuDisConnect = new JMenuItem();
		menuDisConnect.setText(getTranslation("desconectar"));
		menuDisConnect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				ctTree.setConnected(false);
				for (int i=0;i<root.getChildCount();i++){
					DefaultMutableTreeNode node=(DefaultMutableTreeNode)root.getChildAt(i);
					if (node.getUserObject().equals(ctTree)){
						node.removeAllChildren();
					}
				}
				treeModel.reload();
			}
		});
	}
	return menuDisConnect;
}

/**
 * This method initializes menuDel
 *
 * @return javax.swing.JMenuItem
 */
private JMenuItem getMenuDel() {
	if (menuDel == null) {
		menuDel = new JMenuItem();
		menuDel.setText(getTranslation("eliminar"));
		menuDel.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(java.awt.event.ActionEvent e) {
				for (int i=0;i<root.getChildCount();i++){
					DefaultMutableTreeNode node=(DefaultMutableTreeNode)root.getChildAt(i);
					if (node.getUserObject().equals(ctTree)){
						treeModel.removeNodeFromParent(node);
						treeConnection.repaint();
					}
				}
				connectionDB.delPersistence(ctTree.getHost()+"_"+ctTree.getName());
			}
		});
	}
	return menuDel;
}

/**
 * This method initializes menuRefresh
 *
 * @return javax.swing.JMenuItem
 */
private JMenuItem getMenuRefresh() {
	if (menuRefresh == null) {
		menuRefresh = new JMenuItem();
		menuRefresh.setText(getTranslation("refrescar"));
		menuRefresh.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				init(ctTree);
			}
		});
	}
	return menuRefresh;
}

public static void main(String[] args) {
	JDialog dialog=new JDialog();

	ConnectionTransInit[] connDrivers=new ConnectionTransInit[2];
	connDrivers[0]=new ConnectionTransInit();
	connDrivers[0].setHost("localhost");
	connDrivers[0].setName("MYSQL DataBase");
	connDrivers[0].setConnBegining("jdbc:mysql:");
	connDrivers[0].setPort("3306");
    connDrivers[0].setDb("test");
    connDrivers[0].setUser("root");
    connDrivers[0].setPassword("aquilina");
	try {
		Class.forName("com.mysql.jdbc.Driver");
        Class.forName("org.postgresql.Driver");
	} catch (ClassNotFoundException e) {
		throw new RuntimeException(e);
	}

	connDrivers[1]=new ConnectionTransInit();
	connDrivers[1].setHost("localhost");
	connDrivers[1].setName("POSTGRES DataBase");
	connDrivers[1].setConnBegining("jdbc:postgresql:");
	connDrivers[1].setPort("5432");
    connDrivers[1].setDb("latin1");
    connDrivers[1].setUser("postgres");
    connDrivers[1].setPassword("aquilina");


	JDBCManager sm=new JDBCManager(connDrivers);
	dialog.getContentPane().add(sm);
	dialog.setSize(600,350);
	dialog.setVisible(true);
}
	/**
	 * This method initializes this
	 *
	 * @return void
	 * @throws ConectadoException
	 * @throws SQLException
	 */
	private void initialize(){
		this.setSize(604, 305);
		setResourceBundle();
		this.add(getPConnectionsTree(), null);
		this.add(getParameters(), null);
		this.add(getBAcept(), null);
		this.add(getBCancel(), null);
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method initializes parameters
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getParameters() {
		if (tabbedPanel == null) {
			tabbedPanel = new JTabbedPane();
			tabbedPanel.addTab(getTranslation("parametros"), null, getPparameters(), null);
			//tabbedPanel.addTab(getTraduction("opciones"), null, getPopciones(), null);
		}
		return tabbedPanel;
	}

	/**
	 * This method initializes pparameters
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPparameters() {
		if (pparameters == null) {
			pparameters = new JPanel();
			pparameters.setLayout(null);
			pparameters.setPreferredSize(new java.awt.Dimension(400,230));
			pparameters.add(getPServer(), null);
			pparameters.add(getPhost(), null);
			pparameters.add(getPUser(), null);
			pparameters.add(getPDatabase(), null);
			pparameters.add(getBTestConnection(), null);
		}
		return pparameters;
	}

	/**
	 * This method initializes popciones
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPopciones() {
		if (popciones == null) {
			popciones = new JPanel();
		}
		return popciones;
	}

	/**
	 * This method initializes cbDrivers
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getCbDrivers() {
		if (cbDrivers == null) {
			cbDrivers = new JComboBox();
			/*Driver[] drivers=null;
			try {
				drivers = getDrivers();
			} catch (DriverLoadException e) {
				NotificationManager.addError(PluginServices.getText(this,"Error obteniendo los drivers JDBC"),e);
			}
			if (drivers!=null)
			*/
			for (int i=0;i<connectionDB.getDefaultTrans().length;i++){
				cbDrivers.addItem(connectionDB.getDefaultTrans()[i]);
			}
			//cbDrivers.addItem("mysql");
			cbDrivers.setPreferredSize(new java.awt.Dimension(150,25));
			cbDrivers.setBounds(new java.awt.Rectangle(24,35,179,20));
			cbDrivers.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					initPort(((ConnectionTrans)cbDrivers.getSelectedItem()).getPort());
				}
			});
		}
		return cbDrivers;
	}

	/**
	 * This method initializes txtName
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtName() {
		if (txtName == null) {
			txtName = new JTextField();
			txtName.setBounds(new java.awt.Rectangle(212,35,162,20));
		}
		return txtName;
	}

	/**
	 * This method initializes pServer
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPServer() {
		if (pServer == null) {
			lblName = new JLabel();
			lblName.setBounds(new java.awt.Rectangle(212,13,81,18));
			lblName.setText(getTranslation("nombre"));
			lblDrivers = new JLabel();
			lblDrivers.setBounds(new java.awt.Rectangle(25,18,105,16));
			lblDrivers.setText(getTranslation("drivers"));
			pServer = new JPanel();
			pServer.setLayout(null);
			pServer.setBounds(new java.awt.Rectangle(2,2,400,69));
			pServer.setBorder(javax.swing.BorderFactory.createTitledBorder(null, getTranslation("servidor"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			pServer.add(getCbDrivers(), null);
			pServer.add(getTxtName(), null);
			pServer.add(lblDrivers, null);
			pServer.add(lblName, null);
		}
		return pServer;
	}

	/**
	 * This method initializes phost
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPhost() {
		if (phost == null) {
			lblPort = new JLabel();
			lblPort.setText(getTranslation("puerto"));
			lblPort.setPreferredSize(new java.awt.Dimension(60,16));
			lblHost = new JLabel();
			lblHost.setText(getTranslation("hostname"));
			lblHost.setPreferredSize(new java.awt.Dimension(60,16));
			phost = new JPanel();
			phost.setLayout(new FlowLayout());
			phost.setBounds(new java.awt.Rectangle(3,73,195,85));
			phost.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray,1));
			phost.add(lblHost, null);
			phost.add(getTxtHost(), null);
			phost.add(lblPort, null);
			phost.add(getTxtPort(), null);
		}
		return phost;
	}

	/**
	 * This method initializes txtHost
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtHost() {
		if (txtHost == null) {
			txtHost = new JTextField();
			txtHost.setPreferredSize(new java.awt.Dimension(110,20));
		}
		return txtHost;
	}

	/**
	 * This method initializes txtPort
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtPort() {
		if (txtPort == null) {
			txtPort = new JTextField();
			ConnectionTrans ct=(ConnectionTrans)getCbDrivers().getSelectedItem();
			txtPort.setText(ct.getPort());
			txtPort.setPreferredSize(new java.awt.Dimension(110,20));
		}
		return txtPort;
	}

	/**
	 * This method initializes pUser
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPUser() {
		if (pUser == null) {
			lblPassword = new JLabel();
			lblPassword.setText(getTranslation("clave"));
			lblPassword.setPreferredSize(new java.awt.Dimension(60,16));
			lblUser = new JLabel();
			lblUser.setText(getTranslation("usuario"));
			lblUser.setPreferredSize(new java.awt.Dimension(60,16));
			pUser = new JPanel();
			pUser.setBounds(new java.awt.Rectangle(201,73,195,85));
			pUser.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray,1));
			pUser.add(lblUser, null);
			pUser.add(getTxtUser(), null);
			pUser.add(lblPassword, null);
			pUser.add(getTxtPassword(), null);
			pUser.add(getRbPassword(), null);
		}
		return pUser;
	}

	/**
	 * This method initializes txtUser
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtUser() {
		if (txtUser == null) {
			txtUser = new JTextField();
			txtUser.setPreferredSize(new java.awt.Dimension(100,20));
		}
		return txtUser;
	}

	/**
	 * This method initializes txtPassword
	 *
	 * @return javax.swing.JTextField
	 */
	private JPasswordField getTxtPassword() {
		if (txtPassword == null) {
			txtPassword = new JPasswordField();
			txtPassword.setPreferredSize(new java.awt.Dimension(100,20));
		}
		return txtPassword;
	}

	/**
	 * This method initializes pDatabase
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPDatabase() {
		if (pDatabase == null) {
			lblDB = new JLabel();
			lblDB.setText(getTranslation("base_datos"));
			pDatabase = new JPanel();
			pDatabase.setBounds(new java.awt.Rectangle(4,160,391,34));
			pDatabase.add(lblDB, null);
			pDatabase.add(getTxtDB(), null);
		}
		return pDatabase;
	}

	/**
	 * This method initializes txtDB
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtDB() {
		if (txtDB == null) {
			txtDB = new JTextField();
			txtDB.setPreferredSize(new java.awt.Dimension(200,20));
		}
		return txtDB;
	}

	/**
	 * This method initializes bTestConnection
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBTestConnection() {
		if (bTestConnection == null) {
			bTestConnection = new JButton();
			bTestConnection.setBounds(new java.awt.Rectangle(4,197,392,26));
			bTestConnection.setText(getTranslation("prueba_conexion"));
			bTestConnection.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ConnectionTrans ct=new ConnectionTrans();
					ct.setDriver(getCbDrivers().getSelectedItem().toString());
					ct.setName(getTxtName().getText());
					ct.setHost(getTxtHost().getText());
					ct.setPort(getTxtPort().getText());
					ct.setUser(getTxtUser().getText());
					ct.setPassword(getTxtPassword().getText());
					ct.setDb(getTxtDB().getText());
					ct.setConnBegining(((ConnectionTrans)getCbDrivers().getSelectedItem()).getConnBeginning());
					//System.out.println("Botón prueba conexión");
					boolean correct=true;
					try {
						connectionDB.testDB(ct);
					} catch (ConnectionException e1) {
						JOptionPane.showMessageDialog((Component)JDBCManager.this,e1);
						correct=false;
					}
					if (correct)
					JOptionPane.showMessageDialog((Component)JDBCManager.this,getTranslation("conexion_correcta"));
				}
			});
		}
		return bTestConnection;
	}

	/**
	 * This method initializes pConnectionsTree
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPConnectionsTree() {
		if (pConnectionsTree == null) {
			pConnectionsTree = new JPanel();
			pConnectionsTree.setPreferredSize(new java.awt.Dimension(170,260));
			pConnectionsTree.setBackground(java.awt.SystemColor.control);
			pConnectionsTree.add(getScrollTree(), null);
		}
		return pConnectionsTree;
	}
	private ConnectionTrans PaneltoConnectTrans(){
		connectionTrans=new ConnectionTrans();
		connectionTrans.setDriver(((ConnectionTrans)getCbDrivers().getSelectedItem()).getName());
		connectionTrans.setName(getTxtName().getText());
		connectionTrans.setHost(getTxtHost().getText());
		connectionTrans.setPort(getTxtPort().getText());
		connectionTrans.setUser(getTxtUser().getText());
		connectionTrans.setSavePassword(getRbPassword().isSelected());
		connectionTrans.setPassword(getTxtPassword().getText());
		connectionTrans.setDb(getTxtDB().getText());
		connectionTrans.setConnBegining(((ConnectionTrans)getCbDrivers().getSelectedItem()).getConnBeginning());
		return connectionTrans;
	}
	//public Connection getConnection(String host,String name) throws SQLException{
	//	return connectionDB.getConnectionByName(host+"_"+name);
	//}
	/**
	 * This method initializes bAcept
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBAcept() {
		if (bAcept == null) {
			bAcept = new JButton();
			bAcept.setText(getTranslation("aceptar"));
			bAcept.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						connectionTrans=PaneltoConnectTrans();
						if (connectionDB.testDB(connectionTrans)){
							connectionTrans.setConnected(true);
							registerConnection(connectionTrans);
						}

					} catch (ConnectionException e1) {
						JOptionPane.showMessageDialog((Component)JDBCManager.this,e1);
					}
					treeModel.reload();

				}


			});
		}
		return bAcept;
	}

	private void registerConnection(ConnectionTrans connectionTrans) {
		boolean same=false;
		boolean samewithChange=false;
		DefaultMutableTreeNode node=null;
		for (int i=0;i<root.getChildCount();i++){
			//Object obj=((DefaultMutableTreeNode)root.getChildAt(i)).getUserObject();

			if (connectionTrans.equals(ctTree)){
				same=true;
				if (connectionTrans.isConnected()!=ctTree.isConnected()|| ctTree.isConnected()==false || connectionTrans.isSavePassword()!=ctTree.isSavePassword() || connectionTrans.isSavePassword()==false){
					node=(DefaultMutableTreeNode)root.getChildAt(i);
					samewithChange=true;
					ctTree.setConnected(true);
				}
			}else{
				//((ConnectionTrans)obj).setConnected(true);
			}
		}

		try{
			if (!same){
				try {
					connectionDB.setupDriver(connectionTrans);
					connectionDB.setPersistence(connectionTrans);
				} catch (Exception e) {
					e.printStackTrace();
				}
				initTree(connectionTrans);
			}
			if (samewithChange){
				try {
					connectionDB.setupDriver(connectionTrans);
					connectionDB.setPersistence(connectionTrans);
				} catch (Exception e) {
					e.printStackTrace();
				}
				modifyTreeNode(connectionTrans,node);

			}
		}catch (Exception e1) {
			// TODO: handle exception
		}
		treeConnection.repaint();
		//PluginServices.getMDIManager().closeView(JDBCManager.this);


	}

	/**
	 * This method initializes bCancel
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBCancel() {
		if (bCancel == null) {
			bCancel = new JButton();
			bCancel.setText(getTranslation("cancelar"));
			bCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

				}
			});
		}
		return bCancel;
	}

	/**
	 * This method initializes treeConnection
	 *
	 * @return javax.swing.JTree
	 */
	private JTree getTreeConnection() {
		if (treeConnection == null) {
			treeConnection = new JTree();
			root = new DefaultMutableTreeNode(java.lang.Object.class);
			treeModel = new DefaultTreeModel(root);
			//TOCRenderer m_TocRenderer = new TOCRenderer();
		    //treeConnection.setCellRenderer(m_TocRenderer);
			treeConnection.setRootVisible(false);
			treeConnection.setSize(new java.awt.Dimension(170,252));
			treeConnection.setCellRenderer(new ConnRenderer());
			//treeConnection.setShowsRootHandles(true);

		        //Posibilidad de seleccionar de forma aleatoria nodos de la leyenda.
		    treeConnection.setModel(treeModel);
			treeConnection.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					 int x = e.getX();
			         int y = e.getY();
			         int row = treeConnection.getRowForLocation(x, y);
			         TreePath path = treeConnection.getPathForRow(row);
			         if (path != null) {
			        	 nodeSelected = (DefaultMutableTreeNode) path.getLastPathComponent();
			         }
					if (e.getButton()==MouseEvent.BUTTON1){
					System.out.println("mouseClicked()"); // TODO Auto-generated Event stub mousePressed()
					 ///int x = e.getX();
			         ///int y = e.getY();
			         ///int row = treeConnection.getRowForLocation(x, y);
			         ///TreePath path = treeConnection.getPathForRow(row);
			         ///if (path != null) {
			        ///	 nodeSelected = (DefaultMutableTreeNode) path.getLastPathComponent();

			        	 if (nodeSelected.getUserObject() instanceof ConnectionTrans){
			            	ctTree=(ConnectionTrans)nodeSelected.getUserObject();
			            	init(ctTree);
			        	 }
			        /// }
					}else if (e.getButton()==MouseEvent.BUTTON3){
						if (nodeSelected.getUserObject() instanceof ConnectionTrans){
			            	ctTree=(ConnectionTrans)nodeSelected.getUserObject();
						}
						if (!ctTree.isConnected()){
							getMenuDisConnect().setEnabled(false);
							getMenuConectar().setEnabled(true);
						}else{
							getMenuConectar().setEnabled(false);
							getMenuDisConnect().setEnabled(true);
						}
						JPopupMenu popup=getPopupMenu();
						treeConnection.add(popup);

                        //System.out.println("boton derecho");
                        popup.show(e.getComponent(), e.getX(), e.getY());
					}
				}


			});
		}
		return treeConnection;
	}
	/*public ViewInfo getViewInfo() {
		ViewInfo m_viewinfo = new ViewInfo(ViewInfo.MODALDIALOG);
		m_viewinfo.setTitle(PluginServices.getText(this,
				"DBManager"));

		return m_viewinfo;
	}*/
	private void init(ConnectionTrans ct){

 		try {
			initDriver(ct.getDriver());
		} catch (ConnectionException e) {
		}
 		initName(ct.getName());
 		initHost(ct.getHost());
 		initPort(ct.getPort());
 		initUser(ct.getUser());
 		initSavePassword(ct.isSavePassword());
 		initPassword(ct.getPassword());
 		initDB(ct.getDb());
	}
	private void init() throws Exception{
		ConnectionTrans[] conns=null;
			conns=connectionDB.getPersistence();
        if (conns == null)
            conns = connectionDB.getDefaultTrans();
		int j=-1;
		for (j=0;j<conns.length;j++){
			initTree(conns[j]);
		}
		if (j>=0){
			j=j-1;
		init(conns[j]);
		}

	}
	private void modifyTreeNode(ConnectionTrans cs,DefaultMutableTreeNode node) throws ConnectionException{

			String[] s=new String[0];
			s=connectionDB.getTableNames(connectionDB.getConnection(cs));
			if (cs.isSavePassword()){
			((ConnectionTrans)node.getUserObject()).setSavePassword(true);

			}else{
				((ConnectionTrans)node.getUserObject()).setSavePassword(false);

			}
			((ConnectionTrans)node.getUserObject()).setPassword(cs.getPassword());
			node.removeAllChildren();
			for(int i=0;i<s.length;i++){
				DefaultMutableTreeNode table=new DefaultMutableTreeNode(s[i]);
				treeModel.insertNodeInto(table,node,node.getChildCount());
			}

			treeModel.reload();
	}

	private void initTreeWithoutPassword(ConnectionTrans cs){
		DefaultMutableTreeNode aux=new DefaultMutableTreeNode(cs);
		treeModel.insertNodeInto(aux,root,root.getChildCount());
	}
	private void initTree(ConnectionTrans cs) throws ConnectionException{
		if (cs.isSavePassword()){
		String[] s=new String[0];
		s=connectionDB.getTableNames(connectionDB.getConnection(cs));
		DefaultMutableTreeNode aux=new DefaultMutableTreeNode(cs);
		treeModel.insertNodeInto(aux,root,root.getChildCount());
		for(int i=0;i<s.length;i++){
			DefaultMutableTreeNode table=new DefaultMutableTreeNode(s[i]);
			treeModel.insertNodeInto(table,aux,aux.getChildCount());
		}
		}else{
			initTreeWithoutPassword(cs);
		}
		treeModel.reload();

	}
	private void initDriver(String s) throws ConnectionException{
		//VectorialJDBCDriver driver=connectionDB.getDriver(s);
		for (int i=0;i<getCbDrivers().getItemCount();i++){
			ConnectionTrans ct=(ConnectionTrans)getCbDrivers().getItemAt(i);
			if (ct.getName().equals(s)){
				getCbDrivers().setSelectedItem(ct);
			}
		}

	}
	private void initName(String s){
		getTxtName().setText(s);
	}
	private void initHost(String s){
		getTxtHost().setText(s);
	}
	private void initPort(String s){
		getTxtPort().setText(s);
	}
	private void initUser(String s){
		getTxtUser().setText(s);
	}
	private boolean initSavePassword(boolean b){
		getRbPassword().setSelected(b);
		return b;
	}
private void initPassword(String s){
	getTxtPassword().setText(s);
}
private void initDB(String s){
	getTxtDB().setText(s);
}

void setResourceBundle() {
	try {
		resourceBundle = (PropertyResourceBundle) ResourceBundle.getBundle("com.iver.utiles.connections.text");
	} catch (MissingResourceException e) {
	System.out.println("Exception = "+e);
		//	logger.error(Messages.getString("PluginServices.No_se_encontro_el_recurso_de_traducciones") + name, e);
	}
}
public static String getTranslation(String key) {
	if (resourceBundle == null) {
		return key;
	}

	if (key == null) {
		return null;
	}

	try {
		return resourceBundle.getString(key);
	} catch (MissingResourceException e) {
		//logger.warn(Messages.getString("PluginServices.No_se_encontro_la_traduccion_para") + " " + key);

		return key;
	}
}

/*private Driver[] getDrivers() throws DriverLoadException{
	Class[] classes = new Class[] { VectorialJDBCDriver.class };

	ArrayList ret = new ArrayList();
	String[] driverNames = LayerFactory.getDM().getDriverNames();

	for (int i = 0; i < driverNames.length; i++) {
		for (int j = 0; j < classes.length; j++) {
			if (LayerFactory.getDM().isA(driverNames[i], classes[j])) {
				ret.add(LayerFactory.getDM().getDriver(driverNames[i]));
			}
		}
	}

	return (Driver[]) ret.toArray(new Driver[0]);

}
*/
}  //  @jve:decl-index=0:visual-constraint="10,10"
