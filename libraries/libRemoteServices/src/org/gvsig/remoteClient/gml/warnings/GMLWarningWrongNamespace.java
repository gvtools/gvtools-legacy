package org.gvsig.remoteClient.gml.warnings;

import java.util.Hashtable;
import java.util.Map;

import org.gvsig.remoteClient.gml.exceptions.GMLException;

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
 * $Id: GMLWarningWrongNamespace.java 18271 2008-01-24 09:06:43Z jpiera $
 * $Log$
 * Revision 1.1  2007-01-15 13:08:06  csanchez
 * Sistema de Warnings y Excepciones adaptado a BasicException
 *
 *
 */
/**
 * @author Carlos Sánchez Periñán (sanchez_carper@gva.es)
 */
public class GMLWarningWrongNamespace extends GMLException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5960588828358796702L;
	private String namespace;
	
	public GMLWarningWrongNamespace(String key){
		super();
		this.init();
		this.namespace = key;
	}
	
	protected Map values() {
		Hashtable params;
		params = new Hashtable();
		params.put("namespace",namespace);
		return params;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void init() {
		messageKey="gml_warning_wrong_Namespace";
		formatString="Invalid Tag Ignored, not found in %(namespace) namespace";
		code = serialVersionUID;
	}

}
