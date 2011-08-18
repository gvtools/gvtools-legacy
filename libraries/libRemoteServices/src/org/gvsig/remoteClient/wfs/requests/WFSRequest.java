package org.gvsig.remoteClient.wfs.requests;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.gvsig.remoteClient.utils.Utilities;
import org.gvsig.remoteClient.wfs.WFSOperation;
import org.gvsig.remoteClient.wfs.WFSProtocolHandler;
import org.gvsig.remoteClient.wfs.WFSServiceInformation;
import org.gvsig.remoteClient.wfs.WFSStatus;
import org.gvsig.remoteClient.wfs.exceptions.WFSGetFeatureException;

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
public abstract class WFSRequest {
	protected WFSStatus status = null;
	protected WFSProtocolHandler protocolHandler = null;
			
	public WFSRequest(WFSStatus status, WFSProtocolHandler protocolHandler) {
		super();
		this.status = status;
		this.protocolHandler = protocolHandler;
	}

	/**
	 * Send a request to the server.
	 * @return
	 * The server reply
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws ConnectException 
	 */
	public File sendRequest() throws ConnectException, UnknownHostException, IOException{
		//if exists an online resource for the GET operation
		String onlineResource = protocolHandler.getServiceInformation().getOnlineResource(getOperationCode(), WFSOperation.PROTOCOL_GET);
		if (onlineResource != null){
			String symbol = getSymbol(onlineResource);
			onlineResource = onlineResource + symbol;
			return sendHttpGetRequest(onlineResource);
		}
		//if exists an online resource for the POST operation
		onlineResource =  protocolHandler.getServiceInformation().getOnlineResource(getOperationCode(), WFSOperation.PROTOCOL_POST);
		if (onlineResource != null){
			return sendHttpPostRequest(onlineResource);
		}
		//If the online resource doesn't exist, it tries with the server URL and GET
		onlineResource = protocolHandler.getHost();
		String symbol = getSymbol(onlineResource);
		onlineResource = onlineResource + symbol;
		return sendHttpGetRequest(onlineResource);
	}
	
	protected abstract String getHttpGetRequest(String onlineResource);
	
	protected abstract String getHttpPostRequest(String onlineResource);
	
	protected abstract String getTempFilePrefix();
	
	protected abstract int getOperationCode();
	
	protected abstract String getSchemaLocation();
	
	protected abstract boolean isDeleted();
	
	/**
	 * Send a Http request using the get protocol
	 * @param onlineResource
	 * @return
	 * @throws ConnectException
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private File sendHttpGetRequest(String onlineResource) throws ConnectException, UnknownHostException, IOException{
		URL url = new URL(getHttpGetRequest(onlineResource));
		if (isDeleted()){
			Utilities.removeURL(url);
		}
		return Utilities.downloadFile(url, getTempFilePrefix(), null);		
	}
	
	/**
	 * Send a Http request using the post protocol
	 * @param onlineResource
	 * @return
	 * @throws ConnectException
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private File sendHttpPostRequest(String onlineResource) throws ConnectException, UnknownHostException, IOException{
		URL url = new URL(onlineResource);
		String data = getHttpPostRequest(onlineResource);
		if (isDeleted()){
			Utilities.removeURL(url+data);
		}
		return Utilities.downloadFile(url, data, getTempFilePrefix(), null);		
	}
	
	/**
	 * Just for not repeat code. Gets the correct separator according 
	 * to the server URL
	 * @param h
	 * @return
	 */
	protected static String getSymbol(String h) {
		String symbol;
		if (h.indexOf("?")==-1) 
			symbol = "?";
		else if (h.indexOf("?")!=h.length()-1)
			symbol = "&";
		else
			symbol = "";
		return symbol;
	}  
	
}
