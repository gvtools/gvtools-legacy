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
package com.iver.cit.gvsig.exceptions.visitors;

import java.util.Hashtable;
import java.util.Map;

import org.gvsig.exceptions.BaseException;

/**
 * <p>The information of a layer or group of layers is accessed using the *strategy* and *visitor* software patterns. If
 *  any problem is produced accessing that information in that way, a <code>VisitException</code> will be produced.</p>
 *
 * @author Vicente Caballero Navarro
 */
public class VisitorException extends BaseException {
	private String layer = null;
	/**
 	 * <p>Constructs an visitor exception with the specified cause, and the layer where this exception was produced.</p>
 	 * 
 	 * @param layer the layer affected
	 * @param exception an exception with the cause
	 */
	public VisitorException(String layer,Throwable exception) {
		this.layer = layer;
		init();
		initCause(exception);
	}

	/**
	 * <p>Prepares the messages to display.</p>
	 */
	private void init() {
		messageKey = "error_visitor";
		formatString = "Cannot visit the layer: %(layer). ";
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.exceptions.BaseException#values()
	 */
	protected Map values() {
		Hashtable params = new Hashtable();
		params.put("layer",layer);
		return params;
	}
}
