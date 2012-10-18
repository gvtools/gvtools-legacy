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
package com.iver.cit.gvsig.fmap.layers;

import java.util.Map;

import org.gvsig.exceptions.BaseException;

/**
 * <p>Exception produced persisting objects.
 * 
 * <p>The processes used to execute it are named <i>Marshall</i> (object to XML representation)
 *  and <i>Unmarshall</i> (XML representation to object). If one of this processes fails, then a <code>XMLException</code> will be
 *  produced.</p>
 *
 * @author Vicente Caballero Navarro
 */
public class XMLException extends BaseException {
	private static final long serialVersionUID = 1L;

	/**
 	 * <p>Constructs an XML exception with the specified cause.</p>
 	 *   
	 * @param e an exception with the cause
	 */
	public XMLException(Throwable e) {
		init();
		initCause(e);
	}

	/**
	 * <p>Prepares the messages to display.</p>
	 */
	private void init() {
		messageKey = "exception_loading_or_creating_a_xml";
		formatString = "Exception loading or creating a xml";
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.exceptions.BaseException#values()
	 */
	protected Map values() {
		return null;
	}
}
