package org.gvsig.remoteClient.wfs;

import java.util.HashMap;
import java.util.Vector;

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
 * $Id$
 * $Log$
 *
 */
/**
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class WFSServiceInformation {
	public String online_resource = null;
	public String version;
	public String name;
	public String scope;
	public String title;
	public String abstr;
	public String keywords;
	public String fees;
	public String operationsInfo;
	public String personname;
	public String organization;
	public String function;
	public String addresstype;
	public String address;
	public String place;
	public String province;
	public String postcode;
	public String country;
	public String phone;
	public String fax;
	public String email;
	public Vector formats;
	private HashMap operationsGet;
	private HashMap operationsPost;
	private HashMap namespaces;

	public WFSServiceInformation() {
		clear();
	}

	public void clear() {
		version = new String();
		name = new String();
		scope = new String();
		title = new String();
		abstr = new String();
		keywords = new String();
		fees = new String();
		operationsInfo = new String();
		personname = new String();
		organization = new String();
		function = new String();
		addresstype = new String();
		address = new String();
		place = new String();
		province = new String();
		postcode = new String();
		country = new String();
		phone = new String();
		fax = new String();
		email = new String();
		formats = new Vector();
		operationsGet = new HashMap();
		operationsPost = new HashMap();
		namespaces = new HashMap();
	}

	/**
	 * @return Returns the online_resource.
	 */
	public String getOnline_resource() {
		return online_resource;
	}

	/**
	 * Add a new supported operation
	 * 
	 * @param operation
	 *            The operation to support
	 * @param protocol
	 *            The HTTP protocol (Get or Post)
	 */
	public void addOperation(int operation, int protocol) {
		if (protocol == WFSOperation.PROTOCOL_GET) {
			operationsGet.put(new Integer(operation), new WFSOperation(
					operation));
		} else if (protocol == WFSOperation.PROTOCOL_POST) {
			operationsPost.put(new Integer(operation), new WFSOperation(
					operation));
		}
	}

	/**
	 * Add a new supported operation
	 * 
	 * @param operation
	 *            The operation to support
	 * @param protocol
	 *            The HTTP protocol (Get or Post)
	 * @param onlineResource
	 *            The online resource
	 */
	public void addOperation(int operation, int protocol, String onlineResource) {
		if (protocol == WFSOperation.PROTOCOL_GET) {
			operationsGet.put(new Integer(operation), new WFSOperation(
					operation, onlineResource));
		} else if (protocol == WFSOperation.PROTOCOL_POST) {
			operationsPost.put(new Integer(operation), new WFSOperation(
					operation, onlineResource));
		}
	}

	/**
	 * Gest the online resource for a concrete operation
	 * 
	 * @param operation
	 *            The operation
	 * @param protocol
	 *            The HTTP protocol (Get or Post)
	 * @return The online resource
	 */
	public String getOnlineResource(int operation, int protocol) {
		WFSOperation op = null;
		if (protocol == WFSOperation.PROTOCOL_GET) {
			op = (WFSOperation) operationsGet.get(new Integer(operation));
		} else if (protocol == WFSOperation.PROTOCOL_POST) {
			op = (WFSOperation) operationsPost.get(new Integer(operation));
		}
		if ((op == null) || (op.getOnlineResource() == null)
				|| (op.getOnlineResource().equals(""))) {
			return null;
		}
		return op.getOnlineResource();
	}

	/**
	 * Gets the online resource for a concrete operation. The default protocol
	 * is GET
	 * 
	 * @param operation
	 *            The operation
	 * @return The online resource
	 */
	public String getOnlineResource(int operation) {
		return getOnlineResource(operation, WFSOperation.PROTOCOL_GET);
	}

	/**
	 * Adds a new namespace
	 * 
	 * @param namespacePrefix
	 *            Namespace prefix
	 * @param namespaceURI
	 *            Namespace URI
	 */
	public void addNamespace(String namespacePrefix, String namespaceURI) {
		namespaces.put(namespacePrefix, namespaceURI);
	}

	/**
	 * Gest a namespace URI
	 * 
	 * @param namespaceprefix
	 *            Namespace prefix
	 * @return The namespace URI
	 */
	public String getNamespace(String namespaceprefix) {
		if (namespaces.containsKey(namespaceprefix)) {
			return (String) namespaces.get(namespaceprefix);
		}
		return null;
	}
}
