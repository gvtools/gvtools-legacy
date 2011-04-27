package org.gvsig.fmap.geometries.operation;

import java.util.HashMap;
import java.util.Map;

import org.gvsig.exceptions.BaseException;

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
 * $Id: GeometryOperationException.java,v 1.1 2008/03/12 08:46:21 cvs Exp $
 * $Log: GeometryOperationException.java,v $
 * Revision 1.1  2008/03/12 08:46:21  cvs
 * *** empty log message ***
 *
 *
 */
/**
 * @author Jorge Piera Llodrá (jorge.piera@iver.es)
 */
public class GeometryOperationException extends BaseException{
	private static final long serialVersionUID = 1L;
	private int geometryType = -1;
	private int operationCode = -1;
	
	public GeometryOperationException(int geometryType, int operationCode){
		this.geometryType = geometryType;	
		this.operationCode = operationCode;
	}
	
	/**
	 * Initializes some values
	 */
	public void init() {
		messageKey="geometries_opeartion_exception";
		formatString = "Exception executing the operation with code %(operationCode) " +
			"for the geometry with type %(geometryType).";
		code = serialVersionUID;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gvsig.exceptions.BaseException#values()
	 */
	protected Map values() {
		HashMap map = new HashMap();
		map.put("geometryType", geometryType);
		map.put("operationCode", operationCode);
		return map;
	}

}

