/*
 * Created on 01-feb-2006
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
* $Id: GeoprocessException.java 5412 2006-05-24 21:15:07Z azabala $
* $Log$
* Revision 1.1  2006-05-24 21:12:16  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.1  2006/02/12 21:03:25  azabala
* *** empty log message ***
*
* Revision 1.1  2006/02/01 19:40:59  azabala
* First version in CVS
*
*
*/
package com.iver.cit.gvsig.geoprocess.core.fmap;
/**
 * 	Exceptions throwed by geoprocesses.
 * @author azabala
 *
 */
public class GeoprocessException extends Exception {

	private static final long serialVersionUID = -1215904736755267813L;

	public GeoprocessException() {
		super();
	}

	public GeoprocessException(String message) {
		super(message);
	}

	public GeoprocessException(String message, Throwable cause) {
		super(message, cause);
	}

	public GeoprocessException(Throwable cause) {
		super(cause);

	}
}

