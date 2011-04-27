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
* $Id: UnknownResponseFormatExceptionType.java 7491 2006-09-22 08:09:01Z ldiaz $
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

public class UnknownResponseFormatExceptionType extends ExceptionDescription {

	URL host;
	String protocol;
	String format;
	String layerName;
	
	public UnknownResponseFormatExceptionType(){
		super(20, "Formato de respuesta desconocido");
	}
	public String getHtmlErrorMessage() {
		String message = "<p><b>Formato de respuesto desconocido</b></p>";
		message += "<br>Información adicional:</b>";
		message += "<ul>";
		message += "<li>Capa: " + getLayerName()+"</li>";
		message += "<li>Driver: " + getLayerName()+"</li>";
		message += "<li>Formato: " + getFormat()+"</li>";
		message += "<li>Protocolo: " + getProtocol()+"</li>";
		message += "<li>Host: " + getHost().toString()+"</li>";
		message += "</ul>";
		return message;
	
	}

	public String getLayerName() {
		return layerName;
	}
	
	public void setLayerName(String layerName){
		this.layerName = layerName;
	}
	
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public URL getHost() {
		return host;
	}
	public void setHost(URL host) {
		this.host = host;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

}

