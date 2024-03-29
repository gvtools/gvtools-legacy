package org.gvsig.remoteClient.wfs.wfs_1_1_0;

import java.io.IOException;

import org.gvsig.remoteClient.gml.GMLTags;
import org.gvsig.remoteClient.gml.schemas.XMLNameSpace;
import org.gvsig.remoteClient.utils.BoundaryBox;
import org.gvsig.remoteClient.utils.CapabilitiesTags;
import org.gvsig.remoteClient.utils.Utilities;
import org.gvsig.remoteClient.wfs.WFSFeature;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParserException;

/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
 * $Id: WFSFeature1_1_0.java 18271 2008-01-24 09:06:43Z jpiera $
 * $Log$
 * Revision 1.1  2007-02-09 14:11:01  jorpiell
 * Primer piloto del soporte para WFS 1.1 y para WFS-T
 *
 *
 */
/**
 * @author Jorge Piera Llodr� (piera_jor@gva.es)
 */
public class WFSFeature1_1_0 extends WFSFeature {

	public WFSFeature1_1_0() {

	}

	public WFSFeature1_1_0(String name) {
		super(name);
	}

	/**
	 * <p>
	 * Parses the contents of the parser(WMSCapabilities) to extract the
	 * information about an WMSLayer
	 * </p>
	 * 
	 * @throws IOException
	 * @throws XmlPullParserException
	 * 
	 */
	public void parse(KXmlParser parser) throws XmlPullParserException,
			IOException {
		int currentTag;
		boolean end = false;

		parser.require(KXmlParser.START_TAG, null,
				CapabilitiesTags.WFS_FEATURETYPE);

		for (int i = 0; i < parser.getAttributeCount(); i++) {
			String[] attName = parser.getAttributeName(i).split(":");
			if (attName.length == 2) {
				if (attName[0].compareTo(GMLTags.XML_NAMESPACE) == 0) {
					XMLNameSpace nameSpace = new XMLNameSpace(attName[1],
							parser.getAttributeValue(i));
					this.setNamespace(nameSpace);
				}
			}
		}

		currentTag = parser.next();
		while (!end) {
			switch (currentTag) {
			case KXmlParser.START_TAG:
				if (parser.getName().compareToIgnoreCase(CapabilitiesTags.NAME) == 0) {
					this.setName(parser.nextText());
				} else if (parser.getName().compareToIgnoreCase(
						CapabilitiesTags.TITLE) == 0) {
					this.setTitle(parser.nextText());
				} else if (parser.getName().compareToIgnoreCase(
						CapabilitiesTags.WFS_KEYWORDS) == 0) {
					parser.nextTag();
					while (parser.getName().compareTo(CapabilitiesTags.KEYWORD) == 0) {
						parser.next();
						String keyword = parser.getText();
						if ((keyword != null) || (!(keyword.equals("")))) {
							this.addKeyword(keyword);
						}
						// Keyword end tag
						parser.nextTag();
						// Keyword start tag?
						parser.nextTag();
					}

				} else if (parser.getName().compareToIgnoreCase(
						CapabilitiesTags.KEYWORD) == 0) {
					this.addKeyword(parser.nextText());
				} else if (parser.getName().compareToIgnoreCase(
						CapabilitiesTags.SRS) == 0) {
					String value = parser.nextText();
					if (value != null) {
						String[] mySRSs = value.split(" ");
						for (int i = 0; i < mySRSs.length; i++) {
							this.addSRS(mySRSs[i]);
						}

					}
				} else if (parser.getName().compareToIgnoreCase(
						CapabilitiesTags.DEAFAULTSRS) == 0) {
					String value = parser.nextText();
					addSRS(value.trim());
				} else if (parser.getName().compareToIgnoreCase(
						CapabilitiesTags.LATLONGBOUNDINGBOX) == 0) {
					BoundaryBox bbox = new BoundaryBox();
					bbox.setSrs(CapabilitiesTags.EPSG_4326);
					String value = parser.getAttributeValue("",
							CapabilitiesTags.MINX);
					if ((value != null) && (Utilities.isNumber(value)))
						bbox.setXmin(Double.parseDouble(value));
					value = parser.getAttributeValue("", CapabilitiesTags.MINY);
					if ((value != null) && (Utilities.isNumber(value)))
						bbox.setYmin(Double.parseDouble(value));
					value = parser.getAttributeValue("", CapabilitiesTags.MAXX);
					if ((value != null) && (Utilities.isNumber(value)))
						bbox.setXmax(Double.parseDouble(value));
					value = parser.getAttributeValue("", CapabilitiesTags.MAXY);
					if ((value != null) && (Utilities.isNumber(value)))
						bbox.setYmax(Double.parseDouble(value));
					this.addBBox(bbox);
					this.setLatLonBbox(bbox);
				} else if (parser.getName().compareTo(
						CapabilitiesTags.BOUNDINGBOX) == 0) {
					BoundaryBox bbox = new BoundaryBox();
					String value = parser.getAttributeValue("",
							CapabilitiesTags.SRS);
					if (value != null)
						bbox.setSrs(value);
					value = parser.getAttributeValue("", CapabilitiesTags.MINX);
					if ((value != null) && (Utilities.isNumber(value)))
						bbox.setXmin(Double.parseDouble(value));
					value = parser.getAttributeValue("", CapabilitiesTags.MINY);
					if ((value != null) && (Utilities.isNumber(value)))
						bbox.setYmin(Double.parseDouble(value));
					value = parser.getAttributeValue("", CapabilitiesTags.MAXX);
					if ((value != null) && (Utilities.isNumber(value)))
						bbox.setXmax(Double.parseDouble(value));
					value = parser.getAttributeValue("", CapabilitiesTags.MAXY);
					if ((value != null) && (Utilities.isNumber(value)))
						bbox.setYmax(Double.parseDouble(value));
					if (bbox.getSrs() != null) {
						this.addBBox(bbox);
						this.addSRS(bbox.getSrs());
					}
				}

				break;
			case KXmlParser.END_TAG:
				if (parser.getName()
						.compareTo(CapabilitiesTags.WFS_FEATURETYPE) == 0)
					end = true;
				break;
			case KXmlParser.TEXT:
				break;
			}
			if (!end) {
				currentTag = parser.next();
			}
		}
	}
}
