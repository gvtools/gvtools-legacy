package org.gvsig.remoteClient.gml.schemas;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import org.gvsig.remoteClient.gml.GMLTags;
import org.gvsig.remoteClient.gml.exceptions.GMLException;
import org.gvsig.remoteClient.gml.factories.XMLSchemasFactory;
import org.gvsig.remoteClient.gml.warnings.GMLWarningInfo;
import org.gvsig.remoteClient.gml.warnings.GMLWarningMalformed;
import org.gvsig.remoteClient.gml.warnings.GMLWarningNoSchema;
import org.gvsig.remoteClient.gml.warnings.GMLWarningNotFound;
import org.gvsig.remoteClient.utils.Utilities;
import org.xmlpull.v1.XmlPullParserException;

/* gvSIG. Sistema de Informaciï¿½n Geogrï¿½fica de la Generalitat Valenciana
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
 *   Av. Blasco Ibï¿½ï¿½ez, 50
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
 * $Id: XMLSchemaManager.java 18271 2008-01-24 09:06:43Z jpiera $
 * $Log$
 * Revision 1.2  2007-01-15 13:11:00  csanchez
 * Sistema de Warnings y Excepciones adaptado a BasicException
 *
 * Revision 1.1  2006/12/22 11:25:04  csanchez
 * Nuevo parser GML 2.x para gml's sin esquema
 *
 * 
 * 
 * Revision 1.2  2006/08/30 10:47:36  jorpiell
 * AÃ±adido un File.separator en lugar de unas barras que impedia que funcioanra en linux
 *
 * Revision 1.1  2006/08/10 12:00:49  jorpiell
 * Primer commit del driver de Gml
 *
 * 
 */
/**
 * This class represents a GML file header. It has methods to parses the GML
 * file header and retrieve the namespaces and the attributes. If there is an
 * schema on the GML file, it has to manage it. It has to retrieve the GML file
 * version
 * 
 * @author Jorge Piera Llodrï¿½ (piera_jor@gva.es)
 * @author Carlos Sánchez Periñán (sanchez_carper@gva.es)
 * 
 */
public class XMLSchemaManager {

	private File file = null;
	private String schema = null;
	private XMLSchemaParser nameSpaceParser = new XMLSchemaParser();
	private String mainTag;
	private int no_schema = 1;

	public GMLWarningInfo warnings = null;

	public XMLSchemaManager(File file) {
		super();
		this.file = file;
		this.warnings = new GMLWarningInfo();
	}

	/**
	 * Reader for the GML file It parses the GML header and returns the
	 * attributes
	 * 
	 * @param parser
	 * 
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public void parse(XMLSchemaParser parser) throws XmlPullParserException,
			IOException {
		// nextTag() --> Method from KXML library to get next full tag
		parser.nextTag();
		// It keeps the name of the start tag to compare with the close tag
		// later
		mainTag = parser.getName();
		// If it has namespace before the ":" we keep the maintag without
		// namespace
		int pos = mainTag.indexOf(":");
		if (pos > 0) {
			mainTag = mainTag.substring(mainTag.indexOf(":") + 1,
					mainTag.length());
		}
		// It start to get all the attributes and values from the header tag
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			String attName = parser.getAttributeName(i);
			String attValue = parser.getAttributeValue(i);

			// it splits the attributes names at the both sides from ":"
			String[] ns = attName.split(":");

			// If it founds the 'xmlns' is a new namespace declaration and it
			// has to parse it
			if ((ns.length > 1)
					&& (ns[0].compareTo(GMLTags.XML_NAMESPACE) == 0)) {
				parseNameSpace(ns[1], attValue);
			}
			// If its the SCHEMA LOCATION attribute, it means that there are
			// schema and it tries to parse it
			if ((ns.length > 1)
					&& (ns[1].compareTo(GMLTags.XML_SCHEMA_LOCATION) == 0)) {
				no_schema = 0;
				parseSchemaLocation(ns[0], attValue);
			}
		}
		if (no_schema == 1) {
			// Alert that th GML File hasn't schema but it tries to parse
			warnings.setElement(new GMLWarningNoSchema());
		}
	}

	/**
	 * It adds an XML namespace tag to the hashtable
	 * 
	 * @param xmlnsName
	 *            : Namespace
	 * @param xmlnsValue
	 *            : URI
	 */
	private void parseNameSpace(String xmlnsName, String xmlnsValue) {
		XMLSchemasFactory.addType(xmlnsName, xmlnsValue);
	}

	/**
	 * Parses the schema location attribute XML attribute that contains the
	 * schema location info
	 * 
	 * @param namespace
	 * @param schemas
	 **/
	private void parseSchemaLocation(String namespace, String schemas) {
		// It take the name of the schemas file to open or downlad
		StringTokenizer tokenizer = new StringTokenizer(schemas, " \t");
		while (tokenizer.hasMoreTokens()) {
			String URI = tokenizer.nextToken();
			if (!tokenizer.hasMoreTokens()) {
				// If it hasn't the name of the schemas file or dont find it,
				// it exits, and tries to parse without schema
				warnings.setElement(new GMLWarningNotFound(null, null));
			} else {
				schema = tokenizer.nextToken();
				// It add the schemaLocation to the hashtable
				String name = null;
				try {
					name = XMLSchemasFactory.addSchemaLocation(namespace, URI,
							schema);
				} catch (GMLException e) {
					// TODO Auto-generated catch block
					warnings.setElement(new GMLWarningMalformed());
				}
				// It parses the schema.
				parseSchema(schema, name);
			}
		}
	}

	/**
	 * Schema to parse It Downloads the schemas and parses them
	 * 
	 * @param urlString
	 * @param nameSpace
	 */
	private void parseSchema(String urlString, String nameSpace) {
		// If it is a local file, it has to construct the absolute route
		if (urlString.indexOf("http://") != 0) {
			File f = new File(urlString);
			if (!(f.isAbsolute())) {
				urlString = file.getParentFile().getAbsolutePath()
						+ File.separator + urlString;
				f = new File(urlString);
			}
			/****************************************
			 * CALL TO THE SCHEMA PARSER .XSD *
			 ****************************************/
			try {
				nameSpaceParser.parse(f, nameSpace);
			} catch (IOException e) {
				// We can't open the file of the schema
				warnings.setElement(new GMLWarningNotFound(f.getName(), e));
			} catch (XmlPullParserException e) {
				// We can't open the file of the schema
				warnings.setElement(new GMLWarningNotFound(f.getName(), e));
			}

			// Else it is an URL direction and it has to download it.
		} else {
			URL url;
			try {
				url = new URL(urlString);
				// Download the schema without cancel option.
				File f = Utilities.downloadFile(url, "gml_schmema.xsd", null);
				/****************************************
				 * CALL TO THE SCHEMA PARSER .XSD *
				 ****************************************/
				nameSpaceParser.parse(f, nameSpace);
			} catch (MalformedURLException e) {
				// We can't open the file of the schema
				warnings.setElement(new GMLWarningNotFound(urlString, e));
			} catch (ConnectException e) {
				// We can't open the file of the schema
				warnings.setElement(new GMLWarningNotFound(urlString, e));
			} catch (UnknownHostException e) {
				// We can't open the file of the schema
				warnings.setElement(new GMLWarningNotFound(urlString, e));
			} catch (IOException e) {
				// We can't open the file of the schema
				warnings.setElement(new GMLWarningNotFound(urlString, e));
			} catch (XmlPullParserException e) {
				// We can't open the file of the schema
				warnings.setElement(new GMLWarningNotFound(urlString, e));
			}
		}
	}

	/**
	 * @return Returns if there are schema.
	 */
	public boolean Schema() {
		if (schema == null)
			return false;
		else
			return true;
	}

	/**
	 * @return Returns the targetNameSpace.
	 */
	public String getTargetNamespace() {
		return nameSpaceParser.getTargetNamespace();
	}

	/**
	 * @return Returns the version. TODO: Manage the different versions
	 */
	public String getVersion() {
		return nameSpaceParser.getversion();
	}
}
