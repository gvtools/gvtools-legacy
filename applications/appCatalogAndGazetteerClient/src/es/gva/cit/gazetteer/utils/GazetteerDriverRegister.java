package es.gva.cit.gazetteer.utils;

import java.util.Iterator;

import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

import es.gva.cit.gazetteer.drivers.IGazetteerServiceDriver;

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
/* CVS MESSAGES:
 *
 * $Id: GazetteerDriverRegister.java 512 2007-07-24 11:25:43 +0000 (Tue, 24 Jul 2007) jorpiell $
 * $Log$
 * Revision 1.1.2.1  2007/07/24 11:25:42  jorpiell
 * The registers has been refactorized
 *
 * Revision 1.1.2.1  2007/07/10 11:18:04  jorpiell
 * Added the registers
 *
 *
 */
/**
 * This class is used to register the different gazetteer drivers and to
 * retrieve them. It uses the gvSIG extension points.
 * 
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class GazetteerDriverRegister {
	private static GazetteerDriverRegister instance = null;
	private static final String DRIVER_REGISTER_NAME = "GazetteerDrivers";

	/**
	 * This method cretaes the singleton instance
	 * 
	 */
	private synchronized static void createInstance() {
		if (instance == null) {
			instance = new GazetteerDriverRegister();
		}
	}

	/**
	 * @return the remote service instance instance
	 */
	public static GazetteerDriverRegister getInstance() {
		if (instance == null) {
			createInstance();
		}
		return instance;
	}

	/**
	 * This method is used to register a new gazetter driver that manage a
	 * concrete protocol
	 * 
	 * @param driver
	 *            Gazetteer driver to register
	 */
	public void register(IGazetteerServiceDriver driver) {
		ExtensionPoints extensionPoints = ExtensionPointsSingleton
				.getInstance();
		extensionPoints.add(DRIVER_REGISTER_NAME, driver.getServiceName(),
				driver);
	}

	/**
	 * It is used to retrieve a driver that supports a concrete protocol
	 * 
	 * @param protocol
	 *            Gazetteer protocol
	 * @return The concrete gazatteer service driver
	 */
	public IGazetteerServiceDriver getDriver(String protocol) {
		ExtensionPoint extensionPoint = (ExtensionPoint) ExtensionPointsSingleton
				.getInstance().get(DRIVER_REGISTER_NAME);
		Iterator keys = extensionPoint.keySet().iterator();
		while (keys.hasNext()) {
			Object driver = extensionPoint.get(keys.next());
			if (((IGazetteerServiceDriver) driver).getServiceName()
					.toUpperCase().compareTo(protocol.toUpperCase()) == 0) {
				return (IGazetteerServiceDriver) driver;
			}
		}
		return null;
	}

	/**
	 * @return a list with all the gazetteer drivers
	 */
	public IGazetteerServiceDriver[] getDrivers() {
		IGazetteerServiceDriver[] drivers = null;
		ExtensionPoint extensionPoint = (ExtensionPoint) ExtensionPointsSingleton
				.getInstance().get(DRIVER_REGISTER_NAME);
		drivers = new IGazetteerServiceDriver[extensionPoint.keySet().size()];
		Iterator keys = extensionPoint.keySet().iterator();
		int i = 0;
		while (keys.hasNext()) {
			drivers[i] = (IGazetteerServiceDriver) extensionPoint.get(keys
					.next());
			i++;
		}
		return drivers;
	}
}
