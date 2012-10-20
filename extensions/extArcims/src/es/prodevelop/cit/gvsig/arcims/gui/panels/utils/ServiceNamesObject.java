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
package es.prodevelop.cit.gvsig.arcims.gui.panels.utils;

import com.iver.andami.PluginServices;

/**
 * This class simply stores the name, type and status of one services that is
 * available in an ArcIMS server. It is used to model the services list in the
 * wizard's services JTable.
 * 
 * @author jldominguez
 */
public class ServiceNamesObject {
	private String serviceName = "";
	private String serviceType = "";
	private String serviceStatus = "";

	public ServiceNamesObject(String name, String type, String status) {
		serviceName = name;
		serviceType = type;
		serviceStatus = status;
	}

	/**
	 * Gets the service name
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Gets the service type (ImageService or FeatureService)
	 */
	public String getServiceType() {
		return serviceType;
	}

	/**
	 * Gets the service's status (should be ENABLED)
	 */
	public String getServiceStatus() {
		return serviceStatus;
	}

	public String toString() {
		String r = " " + PluginServices.getText(this, "service") + ": "
				+ serviceName + " ";

		return r;
	}

	public String toolTipText() {
		String r = " " + PluginServices.getText(this, "service") + ": "
				+ serviceName + " \n";
		r = r + " " + PluginServices.getText(this, "service_type") + ": "
				+ serviceType + " \n";
		r = r + " " + PluginServices.getText(this, "status") + ": "
				+ serviceStatus + " ";

		return r;
	}
}
