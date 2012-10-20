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
package org.gvsig.remoteClient.sld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * 
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 * 
 */
public class SLDProtocolHandlerFactory {

	private static ArrayList supportedVersions = new ArrayList();

	static {
		supportedVersions.add("1.0.0");
	}

	/**
	 * Creates a protocol handler according of an specific version.
	 * 
	 * @param f
	 * @return
	 * @throws XmlPullParserException
	 *             , if file is corrupt or format could not be recognised
	 * @throws IOException
	 *             , if the file could not be accessed
	 * @throws UnsupportedSLDVersionException
	 *             , if there is no available handler for such SLD version.
	 */
	public static SLDProtocolHandler createVersionedProtocolHandler(File f)
			throws XmlPullParserException, IOException,
			UnsupportedSLDVersionException {

		XMLSchemaParser xmlSchemaParser = null;
		xmlSchemaParser = new XMLSchemaParser();

		xmlSchemaParser.setInput(f);
		xmlSchemaParser.nextTag();

		String version = xmlSchemaParser.getAttributeValue("",
				SLDTags.VERSION_ATTR);
		try {
			SLDProtocolHandler handler = (SLDProtocolHandler) Class.forName(
					"org.gvsig.remoteClient.sld.sld"
							+ version.replaceAll("\\.", "_")
							+ ".SLDProtocolHandler"
							+ version.replaceAll("\\.", "_")).newInstance();
			handler.setVersion(version);

			return handler;
		} catch (Exception e) {
			throw new UnsupportedSLDVersionException(version);
		}
	}

	public static ArrayList<String> getSupportedVersions() {

		return supportedVersions;

	}

}
