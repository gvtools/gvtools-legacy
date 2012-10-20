package com.iver.cit.gvsig.gui.wizards;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;

import com.iver.cit.gvsig.exceptions.layers.ConnectionErrorLayerException;
import com.iver.cit.gvsig.fmap.drivers.wfs.FMapWFSDriver;
import com.iver.cit.gvsig.fmap.drivers.wfs.FMapWFSDriverFactory;
import com.iver.cit.gvsig.fmap.layers.WFSLayerNode;

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
 * $Id: WFSWizardData.java 24753 2008-11-04 15:21:32Z jpiera $
 * $Log$
 * Revision 1.13  2007-09-19 16:14:50  jaume
 * removed unnecessary imports
 *
 * Revision 1.12  2007/03/06 17:06:25  caballero
 * Exceptions
 *
 * Revision 1.11  2007/02/09 14:12:38  jorpiell
 * Soporte para WFS 1.1 y WFS-T
 *
 * Revision 1.10  2006/12/18 08:48:38  jorpiell
 * The props panel uses the same layer driver insteaf of create a nes driver each time
 *
 * Revision 1.9  2006/11/16 16:57:30  jorpiell
 * Un mensaje de error que no se mostraba
 *
 * Revision 1.8  2006/11/16 13:28:50  jorpiell
 * Se usa la factoria de layers para crear la capa
 *
 * Revision 1.7  2006/07/11 07:26:43  caballero
 * traducción cant_connect_wfs
 *
 * Revision 1.6  2006/06/14 08:46:24  jorpiell
 * Se tiene en cuanta la opcion para refrescar las capabilities
 *
 * Revision 1.5  2006/05/23 13:21:59  jorpiell
 * Si hay algún problema en la carga se muestra un mensaje de error
 *
 * Revision 1.4  2006/05/23 08:09:53  jorpiell
 * Se ha cambiado la forma en la que se leian los valores seleccionados en los paneles y se ha cambiado el comportamiento de los botones
 *
 * Revision 1.3  2006/05/19 12:58:03  jorpiell
 * Modificados algunos paneles
 *
 * Revision 1.2  2006/04/20 16:38:24  jorpiell
 * Ahora mismo ya se puede hacer un getCapabilities y un getDescribeType de la capa seleccionada para ver los atributos a dibujar. Queda implementar el panel de opciones y hacer el getFeature().
 *
 * Revision 1.1  2006/04/19 12:50:16  jorpiell
 * Primer commit de la aplicación. Se puede hacer un getCapabilities y ver el mensaje de vienvenida del servidor
 *
 *
 */

/**
 * <p>
 * Model with the information used to add or load a WFS layer using a WFS
 * wizard.
 * </p>
 * 
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 */
public class WFSWizardData {
	private FMapWFSDriver driver = null;
	private String title = null;
	private String _abstract = null;
	private String serverVersion = null;
	private int buffer = 0;
	private int timeOut = 0;
	private String UserName = null;

	/**
	 * Gets the online resource of the WFS driver.
	 * 
	 * @return the online resource
	 */
	public String getOnlineResource() {
		return driver.getOnlineResource();
	}

	/**
	 * Gets the host of the WFS driver.
	 * 
	 * @return the host name
	 */
	public String getHost() {
		return driver.getHost();
	}

	/**
	 * Create the WFSClient and try to connect
	 * 
	 * @param host
	 *            server name
	 * @throws Exception
	 */
	public void setHost(URL host, boolean override) throws Exception {
		try {
			driver = FMapWFSDriverFactory.getFMapDriverForURL(host);
			try {
				driver.createClient(host);
			} catch (ConnectException e) {
				throw new ConnectionErrorLayerException(host.toString(), e);
			} catch (IOException e) {
				throw new ConnectionErrorLayerException(host.toString(), e);
			}

			if (!driver.connect(override, null)) {
				throw new ConnectionErrorLayerException(host.toString(), null);
			}

		} catch (Exception e1) {
			throw e1;
		}
	}

	/**
	 * The server's title (not used in gvSIG).
	 * 
	 * @return server's title
	 */
	public String getTitle() {
		title = driver.getServiceTitle();

		if (title == null) {
			return "None";
		}

		return title;
	}

	/**
	 * The server's description.
	 * 
	 * @return server's description
	 */
	public String getAbstract() {
		_abstract = driver.getServiceAbstract();

		if (_abstract == null) {
			return "None";
		}

		return _abstract;
	}

	/**
	 * Gets the driver used with adding the WFS layer.
	 * 
	 * @return Returns the driver.
	 */
	public FMapWFSDriver getDriver() {
		return driver;
	}

	/**
	 * @see FMapWFSDriver#getLayerList()
	 */
	public WFSLayerNode[] getFeatures() {
		return driver.getLayerList();
	}

	/**
	 * @see FMapWFSDriver#getLayerInfo(String, String)
	 */
	public Object getFeatureInfo(String featureName, String nameSpace) {
		return driver.getLayerInfo(featureName, nameSpace);
	}

	/**
	 * Gets the server type.
	 * 
	 * @return server type
	 */
	public String getServerType() {
		serverVersion = driver.getVersion();

		if (serverVersion == null) {
			return "WFS";
		}

		return "WFS " + serverVersion;
	}

	/**
	 * Gets the maximum number of features that can load.
	 * 
	 * @return the maximum number of features that can load
	 */
	public int getBuffer() {
		return buffer;
	}

	/**
	 * Sets the maximum number of features that can load.
	 * 
	 * @param buffer
	 *            the maximum number of features that can load
	 */
	public void setBuffer(int buffer) {
		this.buffer = buffer;
	}

	/**
	 * Gets the time out used to add or load a layer.
	 * 
	 * @return the time out used to add or load a layer
	 */
	public int getTimeOut() {
		return timeOut;
	}

	/**
	 * Sets the time out used to add or load a layer.
	 * 
	 * @param timeOut
	 *            the time out used to add or load a layer
	 */
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	/**
	 * Gets the user name.
	 * 
	 * @return the user name
	 */
	public String getUserName() {
		return UserName;
	}

	/**
	 * Sets the user name.
	 * 
	 * @param userName
	 *            the user name
	 */
	public void setUserName(String userName) {
		UserName = userName;
	}

	/**
	 * Sets the driver used to add or load a layer.
	 * 
	 * @param driver
	 *            the driver used to add or load a layer
	 */
	public void setDriver(FMapWFSDriver driver) {
		this.driver = driver;
	}
}
