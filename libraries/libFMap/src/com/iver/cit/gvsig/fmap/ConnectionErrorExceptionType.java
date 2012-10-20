/*
 * Created on 06-sep-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * $Id: ConnectionErrorExceptionType.java 7491 2006-09-22 08:09:01Z ldiaz $
 * $Log$
 * Revision 1.2  2006-09-22 08:09:01  ldiaz
 * set/get de layerName y driverName pasan a ExceptionDescription en iverUtiles
 *
 * Revision 1.1  2006/09/21 17:18:31  azabala
 * First version in cvs
 *
 *
 */
package com.iver.cit.gvsig.fmap;

import java.net.URL;

import com.iver.utiles.ExceptionDescription;

public class ConnectionErrorExceptionType extends ExceptionDescription {

	URL host;

	// private String layerName;
	// private String driverName;

	public ConnectionErrorExceptionType() {
		super(40, "Error de conexion a servidor remoto");
	}

	public String getHtmlErrorMessage() {
		String message = "<p><b>Error de conexión a servidor remoto:</b><br>";
		message += "Se ha producido un error al tratar de conectar al servicio "
				+ host.toString();
		message += "<br>Información adicional:";
		message += "Capa:" + getLayerName() + "<br>";
		message += "Driver:" + getDriverName() + "<br>";
		return message;

	}

	// private String getDriverName() {
	// return driverName;
	// }
	//
	// public void setDriverName(String driverName){
	// this.driverName = driverName;
	// }
	// private String getLayerName() {
	// return layerName;
	// }
	//
	// public void setLayerName(String layerName){
	// this.layerName = layerName;
	// }

	public URL getHost() {
		return host;
	}

	public void setHost(URL host) {
		this.host = host;
	}

}
