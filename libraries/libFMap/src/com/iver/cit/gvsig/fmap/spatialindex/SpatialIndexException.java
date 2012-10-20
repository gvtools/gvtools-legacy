/*
 * Created on 28-abr-2006
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
 * $Id: SpatialIndexException.java 12380 2007-06-27 20:17:30Z azabala $
 * $Log$
 * Revision 1.3  2007-06-27 20:17:30  azabala
 * new spatial index (rix)
 *
 * Revision 1.2  2007/03/06 17:08:59  caballero
 * Exceptions
 *
 * Revision 1.1  2006/05/01 18:38:41  azabala
 * primera version en cvs del api de indices espaciales
 *
 *
 */
package com.iver.cit.gvsig.fmap.spatialindex;

import java.util.Map;

import org.gvsig.exceptions.BaseException;

public class SpatialIndexException extends BaseException {

	Exception e;

	public SpatialIndexException(Exception e) {
		this.e = e;
	}

	public SpatialIndexException() {

	}

	protected Map values() {
		// TODO Auto-generated method stub
		return null;
	}

	public void printStackTrace() {
		e.printStackTrace();
	}
}
