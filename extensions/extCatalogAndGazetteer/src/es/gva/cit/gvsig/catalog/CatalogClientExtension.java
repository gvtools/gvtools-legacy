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
package es.gva.cit.gvsig.catalog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.iver.andami.PluginServices;
import com.iver.andami.persistence.serverData.ServerDataPersistence;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;
import com.iver.utiles.swing.jcomboServer.ServerData;

import es.gva.cit.catalog.csw.drivers.CSWISO19115CatalogServiceDriver;
import es.gva.cit.catalog.csw.drivers.CSWebRIMCatalogServiceDriver;
import es.gva.cit.catalog.srw.drivers.SRWCatalogServiceDriver;
import es.gva.cit.catalog.utils.CatalogDriverRegister;
import es.gva.cit.catalog.z3950.drivers.Z3950CatalogServiceDriver;
import es.gva.cit.gvsig.catalog.gui.ConnectDialog;


/**
 * DOCUMENT ME!
 *
 * @author Luis W. Sevilla
 */
public class CatalogClientExtension extends Extension {
    /* (non-Javadoc)
     * @see com.iver.andami.plugins.Extension#inicializar()
     */
    public void initialize() {
        System.out.println("Añado CatalogClientModule");
        ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
        ExtensionPoint extensionPoint = new ExtensionPoint("CatalogLayers","Lista de capas que se pueden cargar desde el catalogo.");
        extensionPoints.put(extensionPoint); 
        registerIcons();
    }
    
    private void registerIcons(){
    	PluginServices.getIconTheme().registerDefault(
				"catalog-search",
				this.getClass().getClassLoader().getResource("images/SearchButton.png")
			);
    }
    
    /*
     * (non-Javadoc)
     * @see com.iver.andami.plugins.Extension#postInitialize()
     */
    public void postInitialize(){
    	CatalogDriverRegister register = CatalogDriverRegister.getInstance();
		register.register(new Z3950CatalogServiceDriver());
		register.register(new SRWCatalogServiceDriver());
		register.register(new CSWISO19115CatalogServiceDriver());
		register.register(new CSWebRIMCatalogServiceDriver());
    }

    /* (non-Javadoc)
     * @see com.iver.andami.plugins.Extension#execute(java.lang.String)
     */
    public void execute(String actionCommand) {
    	actionConnectDialogStart();
    }

    /**
     * DOCUMENT ME!
     */
    private void actionConnectDialogStart() {
        System.out.println("Botón Cliente de metadatos pulsado");
        restoreServerList();

        ConnectDialog connectDialog = new ConnectDialog();
        PluginServices.getMDIManager().addWindow(connectDialog);
    }

    /**
     * It restores a server list. If this list does't exist it create  a server
     * list by default.
     */
    private void restoreServerList() {
        ServerDataPersistence persistence = new ServerDataPersistence(this,ServerData.SERVER_TYPE_CATALOG);
        
        ServerData[] servers = persistence.getArrayOfServerData();
        
        boolean found = false;
        for (int i=0 ; i<servers.length ; i++){
            if (servers[i].getServiceType().equals(ServerData.SERVER_TYPE_CATALOG)){
                found = true;
            }
        }       
        
        if (!found){
             if (servers.length == 0){
                servers = getDefaultServers();
            }else{
                ServerData[] newServers = new ServerData[servers.length + getDefaultServers().length ];
                System.arraycopy(servers, 0, newServers, 0, servers.length);
                System.arraycopy(getDefaultServers(), 0, newServers, servers.length, getDefaultServers().length);
                servers = newServers;
            }
            persistence.setArrayOfServerData(servers);
        }
         
        
        for (int i = 0; i < servers.length; i++) {
            if (servers[i].getServiceType().equals(ServerData.SERVER_TYPE_CATALOG)){
                ConnectDialog.addServer(servers[i]);
            }
        }
        
        
    }

    /**
     * It creates a server list by default
     *
     * @return
     */
    private ServerData[] getDefaultServers() {
    	ServerData[] servers = new ServerData[4];
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();

        
        servers[0] = new ServerData("http://delta.icc.es/indicio/csw", date, date, ServerData.SERVER_TYPE_CATALOG, ServerData.SERVER_SUBTYPE_CATALOG_CSW);
        servers[1] = new ServerData("mapas.euitto.upm.es:2100", date, date, ServerData.SERVER_TYPE_CATALOG, ServerData.SERVER_SUBTYPE_CATALOG_Z3950);
        servers[2] = new ServerData("193.43.36.137:2100", date, date, ServerData.SERVER_TYPE_CATALOG, ServerData.SERVER_SUBTYPE_CATALOG_Z3950);
        servers[3] = new ServerData("http://idee.unizar.es/SRW/servlet/search/ExplainSOAP",date,date,ServerData.SERVER_TYPE_CATALOG,ServerData.SERVER_SUBTYPE_CATALOG_SRW);
        return servers;
    }

    /* (non-Javadoc)
     * @see com.iver.andami.plugins.Extension#isEnabled()
     */
    public boolean isEnabled() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.iver.andami.plugins.Extension#isVisible()
     */
    public boolean isVisible() {
        com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
                                                             .getActiveWindow();

        if (f == null) {
            return false;
        }

        return (f instanceof BaseView);
    }
}
