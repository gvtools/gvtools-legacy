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
package org.gvsig.remoteClient.sld;

import java.net.URL;
import java.util.ArrayList;

/**
 * Implements the ExternalGraphic element of an SLD implementation specification
 * .
 * <p>
 * The ExternalGraphic element allows a reference to be made to an external
 * graphic file with a Web URL. The onlineResource sub-element gives the URL and
 * the format sub-element identifies the expected document MIME type of a
 * succesful fetch. Knowing the MIME type in advance allows the styler to select
 * the best-supported format from the list of URLs with the equivalent content.
 * Users should avoid referencing external graphics that may change at arbitrary
 * times. Graphic should be static when al all possible.
 * 
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public abstract class SLDExternalGraphic implements ISLDFeatures {

	protected ArrayList<URL> onlineResource = new ArrayList<URL>();
	protected String format;

	public ArrayList<URL> getOnlineResource() {
		return onlineResource;
	}

	public void setOnlineResource(ArrayList<URL> onlineResource) {
		this.onlineResource = onlineResource;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void addOnlineResource(URL myURL) {
		this.onlineResource.add(myURL);
	}

}
