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
package es.gva.cit.gvsig.gazetteer;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.iver.andami.PluginServices;
import com.iver.andami.persistence.serverData.ServerDataPersistence;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.swing.jcomboServer.ServerData;

import es.gva.cit.gazetteer.adl.drivers.ADLGazetteerServiceDriver;
import es.gva.cit.gazetteer.geonames.GeonamesServiceDriver;
import es.gva.cit.gazetteer.idec.drivers.IDECGazetteerServiceDriver;
import es.gva.cit.gazetteer.utils.GazetteerDriverRegister;
import es.gva.cit.gazetteer.wfs.drivers.WFSServiceDriver;
import es.gva.cit.gazetteer.wfsg.drivers.WFSGServiceDriver;
import es.gva.cit.gvsig.gazetteer.gui.ConnectDialog;

/**
 * @author Jorge Piera Llodra (piera_jor@gva.es)
 */
public class GazetteerExtension extends Extension {

	public static void main(String[] args) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.Extension#inicializar()
	 */
	public void initialize() {
		System.out.println("Añado GazetteerModule");
		registerIcons();
	}

	private void registerIcons() {
		PluginServices.getIconTheme().registerDefault(
				"gazetteer-search",
				this.getClass().getClassLoader()
						.getResource("images/GazzButton.png"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.Extension#postInitialize()
	 */
	public void postInitialize() {
		GazetteerDriverRegister register = GazetteerDriverRegister
				.getInstance();
		register.register(new ADLGazetteerServiceDriver());
		register.register(new WFSServiceDriver());
		register.register(new WFSGServiceDriver());
		register.register(new IDECGazetteerServiceDriver());
		register.register(new GeonamesServiceDriver());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.Extension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		// JToolBar toolBar =
		// (JToolBar)PluginServices.getMainFrame().getComponentByName("Catalogo");
		// System.out.println("gjkmgbghb");
		// for (int i=0 ; i<toolBar.getComponentCount() ; i++){
		// System.out.println(((JButton)toolBar.getComponentAtIndex(i)).getName());
		// }
		actionConnectDialogStart();
	}

	/**
	 * 
	 * 
	 */
	private void actionConnectDialogStart() {
		System.out.println("Botón de Gazeteer pulsado");
		restoreServerList();
		ConnectDialog connectDialog = new ConnectDialog();
		PluginServices.getMDIManager().addWindow(connectDialog);
	}

	private void restoreServerList() {
		ServerDataPersistence persistence = new ServerDataPersistence(this,
				ServerData.SERVER_TYPE_GAZETTEER);

		ServerData[] servers = persistence.getArrayOfServerData();

		boolean found = false;
		for (int i = 0; i < servers.length; i++) {
			if (servers[i].getServiceType().equals(
					ServerData.SERVER_TYPE_GAZETTEER)) {
				found = true;
			}
		}

		if (!found) {
			if (servers.length == 0) {
				servers = getDefaultServers();
			} else {
				ServerData[] newServers = new ServerData[servers.length
						+ getDefaultServers().length];
				System.arraycopy(servers, 0, newServers, 0, servers.length);
				System.arraycopy(getDefaultServers(), 0, newServers,
						servers.length, getDefaultServers().length);
				servers = newServers;
			}
			persistence.setArrayOfServerData(servers);
		}

		for (int i = 0; i < servers.length; i++) {
			if (servers[i].getServiceType().equals(
					ServerData.SERVER_TYPE_GAZETTEER)) {
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

		servers[0] = new ServerData(
				"http://middleware.alexandria.ucsb.edu/gaz/adlgaz/dispatch",
				date, date, ServerData.SERVER_TYPE_GAZETTEER,
				ServerData.SERVER_SUBTYPE_GAZETTEER_ADL);
		servers[1] = new ServerData(
				"http://demo.deegree.org:8080/gazetteer/wfs-g", date, date,
				ServerData.SERVER_TYPE_GAZETTEER,
				ServerData.SERVER_SUBTYPE_GAZETTEER_WFSG);
		servers[2] = new ServerData(
				"http://193.144.250.29/webservices/services/IDEC_GeoServeisPort",
				date, date, ServerData.SERVER_TYPE_GAZETTEER,
				ServerData.SERVER_SUBTYPE_GAZETTEER_IDEC);
		servers[3] = new ServerData("www.geonames.org:80", date, date,
				ServerData.SERVER_TYPE_GAZETTEER, "Geonames");

		return servers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.Extension#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.Extension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices
				.getMDIManager().getActiveWindow();

		if (f == null) {
			return false;
		}

		if (f instanceof View) {
			if (((View) f).getMapControl().getViewPort().getAdjustedExtent() != null) {
				return true;
			}
		} else if (f instanceof BaseView)
			return true;

		return false;
	}
}
