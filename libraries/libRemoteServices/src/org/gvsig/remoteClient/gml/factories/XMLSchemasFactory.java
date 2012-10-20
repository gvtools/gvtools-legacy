package org.gvsig.remoteClient.gml.factories;

import java.util.Hashtable;

import org.gvsig.exceptions.BaseException;
import org.gvsig.remoteClient.gml.exceptions.GMLException;
import org.gvsig.remoteClient.gml.schemas.XMLNameSpace;
import org.gvsig.remoteClient.gml.warnings.GMLWarningMalformed;

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
 * $Id: XMLSchemasFactory.java 18271 2008-01-24 09:06:43Z jpiera $
 * $Log$
 * Revision 1.3  2007-01-15 13:11:00  csanchez
 * Sistema de Warnings y Excepciones adaptado a BasicException
 *
 * Revision 1.2  2006/12/22 11:25:44  csanchez
 * Nuevo parser GML 2.x para gml's sin esquema
 *
 * Revision 1.1  2006/08/10 12:00:49  jorpiell
 * Primer commit del driver de Gml
 *
 *
 */
/**
 * Factory to create Schemas. The XML schema parser adds a new schema every time
 * it find one when is parsing a xml (gml) file
 * 
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 * @author Carlos Sánchez Periñán (sanchez_carper@gva.es)
 * 
 */
public class XMLSchemasFactory {
	private static Hashtable schemas = new Hashtable();

	/**
	 * It gets the schema type by the key "name"
	 **/
	public static XMLNameSpace getType(String name) {
		return (XMLNameSpace) schemas.get(name);
	}

	/**
	 * Adds a new Schema
	 * 
	 * @param xmlnsName
	 *            XML nameSpace name
	 * @param xmlnsValue
	 *            XML nameSpace value or URI
	 */
	public static void addType(String xmlnsName, String xmlnsValue) {
		// Add a new namespace instance named "schema"...
		XMLNameSpace schema = new XMLNameSpace(xmlnsName, xmlnsValue);
		// Now adds a new hash map for import new schemas
		schemas.put(xmlnsName, schema);
	}

	/**
	 * It find a nameSpace by location.
	 * 
	 * @param namespace
	 *            Parent nameSpace
	 * @param location
	 *            URL or file where the nameSpace is located
	 * @param schema
	 *            Schema
	 * @return String
	 * @throws BaseException
	 */
	public static String addSchemaLocation(String namespace, String location,
			String schema) throws GMLException {
		XMLNameSpace GMLSchema;
		// it gets the schema "default namespace" by the URI, but the W3C
		// Schemas URI must be declared previously
		if ((GMLSchema = getSchemaByLocation(location)) == null) {
			// GML is badly shaped
			// correct form:
			// xmlns:namespace="http://www.w3.org/2001/XMLSchema-instance"
			// namespace:schemaLocation="www.URI.com file.xsd"
			throw new GMLWarningMalformed();
		} else {
			// set the correct location (URI) to the new main namespace
			GMLSchema.setLocation(schema);
			return GMLSchema.getName();
		}
	}

	/**
	 * It find a nameSpace by location.
	 * 
	 * @param location
	 *            URL or file where the nameSpace is located
	 * @return
	 */
	public static XMLNameSpace getSchemaByLocation(String location) {
		Object[] keys = schemas.keySet().toArray();
		for (int i = 0; i < schemas.size(); i++) {
			XMLNameSpace schema = (XMLNameSpace) schemas.get(keys[i]);
			if (schema.getLocation().equals(location)) {
				return schema;
			}
		}
		return null;
	}

	/**
	 * Just for degug. It prints all the registred components.
	 */
	public static void printSchemas() {
		System.out.println("*** SCHEMAS ***");
		Object[] keys = schemas.keySet().toArray();
		for (int i = 0; i < schemas.size(); i++) {
			XMLNameSpace schema = (XMLNameSpace) schemas.get(keys[i]);
			System.out.print("NAME: " + schema.getName());
			System.out.print("LOCATION: " + schema.getLocation());
			System.out.print("\n");
		}
	}
}
