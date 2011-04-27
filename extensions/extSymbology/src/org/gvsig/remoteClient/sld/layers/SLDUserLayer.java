/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
package org.gvsig.remoteClient.sld.layers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;

/**
 * Implements the User-Defined Layer element of an SLD implementation specification
 * .<p>
 * 
 * In addition to using named layers, it is also useful to be able to define custom
 * user-defined layers for rendering.<p>
 * Since a layer is defined as a collection of potentially mixed-type features, the
 * UserLayer element must provide the means to identify the features to be used. All
 * features to be rendered are assumed to be fetched from a Web Feature Server (WFS) or a
 * Web Coverage Service (WCS, in which case the term �features� is used loosely).
 * 
 * The remote server to be used is identified by RemoteOWS (OGC Web Service) element.
 * 
 * @see SLDLayer
 * @see SLDNamedLayer
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public abstract class SLDUserLayer extends AbstractSLDLayer{


	protected ArrayList<URL> remoteOWSOnlineResource= new ArrayList<URL>() ;
	protected String remoteOWSService;

	
	public abstract void parse(XMLSchemaParser parser)throws IOException, XmlPullParserException, LegendDriverException;
	
	public abstract String toXML();

	public ArrayList<URL> getRemoteOWSOnlineResource() {
		return remoteOWSOnlineResource;
	}

	public void setRemoteOWSOnlineResource(ArrayList<URL> remoteOWSOnlineResource) {
		this.remoteOWSOnlineResource = remoteOWSOnlineResource;
	}
	
	public void addRemoteOWSOnlineResource(URL myURL) {
		this.remoteOWSOnlineResource.add(myURL);
		
	}
	
	public String getRemoteOWSService() {
		return remoteOWSService;
	}

	public void setRemoteOWSService(String remoteOWSService) {
		this.remoteOWSService = remoteOWSService;
	}
	
	
}
