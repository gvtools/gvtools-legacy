package es.gva.cit.catalog;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.commons.httpclient.HttpConnection;

import sun.net.www.http.HttpClient;


import com.iver.utiles.swing.jcomboServer.ServerData;

import es.gva.cit.catalog.drivers.DiscoveryServiceCapabilities;
import es.gva.cit.catalog.drivers.IDiscoveryServiceDriver;
import es.gva.cit.catalog.exceptions.NotSupportedProtocolException;
import es.gva.cit.catalog.exceptions.NotSupportedVersionException;
import es.gva.cit.catalog.exceptions.ServerIsNotReadyException;
import es.gva.cit.catalog.querys.DiscoveryServiceQuery;
import es.gva.cit.catalog.ui.search.SearchAditionalPropertiesPanel;
import es.gva.cit.catalog.utils.URIUtils;

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
 * $Id$
 * $Log$
 *
 */
/**
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class DiscoveryServiceClient {
	/**
	 * The server URI
	 */
	private URI uri = null;
	/**
	 * The driver to make the querys
	 */
	private IDiscoveryServiceDriver driver;
	/**
	 * The service capabilities
	 */
	private DiscoveryServiceCapabilities capabilities;
	/**
	 * The server status message
	 */
	private String serverStatus = null;

	public DiscoveryServiceClient(String sUri,IDiscoveryServiceDriver driver) {
		setDriver(driver);
		if (driver == null){
			serverStatus = "errorServerNotFound";
		}else{
			try {
				this.uri = URIUtils.createUri(sUri,
						driver.getDefaultSchema(),
						driver.getDefaultPort());
			} catch (URISyntaxException e) {
				serverStatus = "errorServerNotFound";
			}
		}
	}

	/**
	 * It make a getCapabilities operation
	 * @return the service version
	 * @throws ServerIsNotReadyException 
	 */
	public DiscoveryServiceCapabilities getCapabilities() throws ServerIsNotReadyException {        
		if (serverIsReady()){
			try {
				if (getDriver().isProtocolSupported(getUri())) {
					capabilities = getDriver().getCapabilities(getUri());
					return capabilities;
				}
			} catch (NotSupportedProtocolException e) {
				capabilities = new DiscoveryServiceCapabilities();
				capabilities.setAvailable(false);
				capabilities.setServerMessage("notSupportedProtocol");
			} catch (NotSupportedVersionException e) {
				capabilities = new DiscoveryServiceCapabilities();
				capabilities.setAvailable(false);
				capabilities.setServerMessage("notSupportedVersion");
			} 
		}
		return capabilities;    
	} 

	/**
	 * It tries if the server is ready 
	 * @return boolean
	 * true --> server is ready
	 * false --> server is not ready
	 */
	public boolean serverIsReady() throws ServerIsNotReadyException {        
		Properties systemSettings = System.getProperties();


		Object isProxyEnabled = systemSettings.get("http.proxySet"); 
		if ((isProxyEnabled == null) || (isProxyEnabled.equals("false"))){
			Socket sock;
			try{				
				sock = new Socket(getUri().getHost(),
						getUri().getPort());
			} catch (UnknownHostException e) {
				throw new ServerIsNotReadyException(e);
			} catch (IOException e) {
				throw new ServerIsNotReadyException(e);
			}
			return (sock != null);
		}else{
			Object host = systemSettings.get("http.proxyHost"); 
			Object port = systemSettings.get("http.proxyPort");
			Object user = systemSettings.get("http.proxyUserName");
			Object password = systemSettings.get("http.proxyPassword");
			if ((host != null) && (port != null)){
				int iPort = 80;
				try{
					iPort = Integer.parseInt((String)port);
				}catch (Exception e) {
					//Use 80
				}
				HttpConnection connection = new HttpConnection(getUri().getHost(), 
						getUri().getPort());
				connection.setProxyHost((String)host);
				connection.setProxyPort(iPort);
				Authenticator.setDefault(new SimpleAuthenticator(
                        user,password));
				
				try {
					connection.open();
					connection.close();						
				} catch (IOException e) {
					throw new ServerIsNotReadyException(e);					
				}
			}			
		}		
		return true;
	} 

	/**
	 * @return the server URI
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * @return Return the server URI like a String
	 */
	public String getSUri() {
		return uri.toString();
	} 

	/**
	 * @return Returns the driver.
	 */
	protected IDiscoveryServiceDriver getDriver() {        
		return driver;
	}

	/**
	 * 
	 * @param driver the driver to set
	 */
	protected void setDriver(IDiscoveryServiceDriver driver) {
		this.driver = driver;
	} 

	/**
	 * @return the server protocol
	 */
	public String getProtocol() {        
		return driver.getServiceName();
	}

	/**
	 * Gets the aditional panel
	 * @return
	 */
	public SearchAditionalPropertiesPanel getAditionalSearchPanel(){
		return driver.getAditionalSearchPanel();
	}

	/**
	 * Gets a query
	 * @return
	 */
	public DiscoveryServiceQuery createQuery(){
		return driver.createQuery();
	}
	
	private class SimpleAuthenticator
	   extends Authenticator
	{
	   private String username,
	                  password;
	                     
	   public SimpleAuthenticator(Object username, Object password)
	   {
		   if (username != null){
			   this.username = (String)username;
		   }
		   if (password != null){
			   this.password = (String)password;
		   }
	   }
	   
	   protected PasswordAuthentication getPasswordAuthentication()
	   {
	      return new PasswordAuthentication(
	             username,password.toCharArray());
	   }
	}

}
