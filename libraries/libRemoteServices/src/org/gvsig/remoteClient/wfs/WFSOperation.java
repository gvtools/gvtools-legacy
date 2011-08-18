package org.gvsig.remoteClient.wfs;

import java.util.Hashtable;

import org.gvsig.remoteClient.utils.CapabilitiesTags;

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
 * $Id: WFSOperation.java 18271 2008-01-24 09:06:43Z jpiera $
 * $Log$
 * Revision 1.1  2007-02-09 14:11:01  jorpiell
 * Primer piloto del soporte para WFS 1.1 y para WFS-T
 *
 *
 */
/**
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 */
public class WFSOperation {
	public static final int GETCAPABILITIES = 0;
	public static final int DESCRIBEFEATURETYPE = 1;
	public static final int GETFEATURE = 2;
	public static final int TRANSACTION = 3;
	public static final int LOCKFEATURE = 4;
	public static final int PROTOCOL_GET = 0;
	public static final int PROTOCOL_POST = 1;
	private static Hashtable operations;	
	//
	private int operationName;
	private String onlineResource;
	
	static{
		operations = new Hashtable();
		operations.put(CapabilitiesTags.GETCAPABILITIES,new Integer(GETCAPABILITIES));
		operations.put(CapabilitiesTags.WFS_DESCRIBEFEATURETYPE,new Integer(DESCRIBEFEATURETYPE));
		operations.put(CapabilitiesTags.WFS_GETFEATURE,new Integer(GETFEATURE));
		operations.put(CapabilitiesTags.WFS_TRANSACTION,new Integer(TRANSACTION));
		operations.put(CapabilitiesTags.WFS_LOCKFEATURE,new Integer(LOCKFEATURE));
	}
	
	/**
	 * Return if the operation is registered
	 * @param op
	 * The operation name
	 * @return
	 * The operation code
	 */
	public static int getOperation(String op){
		if (operations.get(op)  != null){
			return ((Integer)operations.get(op)).intValue();
		}
		return -1;
	}
	
	public WFSOperation(int operationName) {
		super();
		this.operationName = operationName;		
	}		

	public WFSOperation(int operationName, String onlineResource) {
		this.onlineResource = onlineResource;
	}	
	
	/**
	 * @return Returns the onlineResource.
	 */
	public String getOnlineResource() {
		return onlineResource;
	}
	/**
	 * @param onlineResource The onlineResource to set.
	 */
	public void setOnlineResource(String onlineResource) {
		this.onlineResource = onlineResource;
	}	

	/**
	 * @return Returns the operationName.
	 */
	public int getOperationName() {
		return operationName;
	}
	
	/**
	 * @param operationName The operationName to set.
	 */
	public void setOperationName(int operationName) {
		this.operationName = operationName;
	}		
}

