/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
package org.gvsig.remoteClient;

import java.io.File;

public abstract class OGCProtocolHandler {
	/**
	 * procotol handler name
	 */
	protected String name;
	/**
	 * protocol handler version
	 */
	protected String version;
	/**
	 * host of the WMS to connect
	 */
	protected String host;
	/**
	 * port number of the comunication channel of the WMS to connect
	 */
	protected String port;

	/**
	 * @return Returns the host.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            The host to set.
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the port.
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port
	 *            The port to set.
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @return Returns the version.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            The version to set.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	// /**
	// * @deprectated
	// * (temporarily)
	// */
	// protected File downloadFile(URL url, String fileName) throws
	// IOException,ConnectException, UnknownHostException{
	// File f = null;
	//
	// try{
	// f = TempFileManager.createTempFile(fileName, "tmp");
	// System.out.println("downloading '"+url.toString()+"' to: "+f.getAbsolutePath());
	//
	// f.deleteOnExit();
	//
	// } catch (IOException io) {
	// io.printStackTrace();
	// }
	// DataOutputStream dos = new DataOutputStream( new BufferedOutputStream(new
	// FileOutputStream(f)));
	// byte[] buffer = new byte[1024*256];
	// InputStream is = url.openStream();
	// long readed = 0;
	// for (int i = is.read(buffer); i>0; i = is.read(buffer)){
	// dos.write(buffer, 0, i);
	// readed += i;
	// }
	// dos.close();
	// /*if (!isNotAnException(f))
	// // SI que es una excepción
	// throw new ServerErrorResponseException();*/
	// return f;
	// }

	/**
	 * parses the data retrieved by the Capabilities XML document
	 * 
	 */
	public abstract boolean parseCapabilities(File f);

}
