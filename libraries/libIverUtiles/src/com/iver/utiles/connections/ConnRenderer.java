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

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;



/**
 * Renderer of the JTree.
 *
 * @author Vicente Caballero Navarro
 */
public class ConnRenderer extends DefaultTreeCellRenderer {
	//private static final ImageIcon bdConnectIcon= new ImageIcon(ConnRenderer.class.getClassLoader()
	//		  .getResource("images/gtk-ok.png"));
	
	//private String directory=PluginServices.getPluginServices(this)
	//  .getPluginDirectory().getPath();
private String directory=null;
	private static ImageIcon bdConnectIcon = null;
	private static ImageIcon bdConnectkeyIcon = null;
    private static ImageIcon bdNoConnectIcon = null;
    private static ImageIcon bdNoConnectkeyIcon = null;
    private static ImageIcon tableIcon = null;
	
    public ConnRenderer(){
    directory=".";
    	
	bdConnectIcon = new ImageIcon(ConnRenderer.class.getResource(
	"images/gtk-ok.png"));
	bdConnectkeyIcon = new ImageIcon(ConnRenderer.class.getResource(
	"images/gtk-ok-key.png"));
    bdNoConnectIcon = new ImageIcon(ConnRenderer.class.getResource(
	"images/gtk-no.png"));
    bdNoConnectkeyIcon = new ImageIcon(ConnRenderer.class.getResource(
	"images/gtk-no-key.png"));
    tableIcon = new ImageIcon(ConnRenderer.class.getResource(
	"images/gtk-properties.png"));
   }
    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
            row, hasFocus);

        if (isConnection(value)) {
            if (isConnected(value)) {
            	if (havePassword(value))
            		setIcon(bdConnectIcon); //Conectado
            	else
            		setIcon(bdConnectkeyIcon); //Conectado sin clave
            } else {
            	if (havePassword(value))
            		setIcon(bdNoConnectIcon); //Desconectado
            	else
            		setIcon(bdNoConnectkeyIcon); //Desconectado sin clave
            }
        } else {
            setIcon(tableIcon);
        }

        return this;
    }

    /**
     * Returns true if is a connection.
     *
     * @param value Tree´s node.
     *
     * @return True if is a connection.
     */
    private boolean isConnection(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getUserObject() instanceof ConnectionTrans) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the connection that is passed for parameter is connected.
     *
     * @param value Tree´s node.
     *
     * @return True if is connected.
     */
    private boolean isConnected(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getUserObject() instanceof ConnectionTrans) {
        	ConnectionTrans connTrans=(ConnectionTrans) node.getUserObject();
        	return connTrans.isConnected();
        }
        return false;
    }
    
    /**
     * Returns true if the connection has kept the password.
     *
     * @param value Tree´s node.
     *
     * @return True if is connected.
     */
    private boolean havePassword(Object value){
    	DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getUserObject() instanceof ConnectionTrans) {
        	ConnectionTrans connTrans=(ConnectionTrans) node.getUserObject();
        	return connTrans.isSavePassword();
        }
        return false;
    }
}
