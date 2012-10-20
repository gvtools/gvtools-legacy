/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */

/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package org.gvsig.remoteClient.arcims;

import java.io.IOException;

import org.gvsig.remoteClient.utils.CapabilitiesTags;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * <p>
 * Defines a style. Theme that describes the appeareance of certain layer.
 * </p>
 * 
 */
public abstract class ArcImsStyle {
	/**
	 * style name, defined in the ArcIms capabilities
	 */
	private String name;

	/**
	 * style title, defined in the ArcIms capabilities
	 */
	private String title;

	/**
	 * style abstract, defined in the ArcIms capabilities
	 */
	private String styleAbstract;
	private LegendURL legendURL;

	/**
	 * <p>
	 * Parses the STYLE tag in the ArcIms capabilities, filling the ArcImsStyle
	 * object loading the data in memory to be easily accesed
	 * </p>
	 * 
	 */
	public abstract void parse(KXmlParser parser) throws IOException,
			XmlPullParserException;

	/**
	 * Parses the legendURL tag.
	 * 
	 * @param parser
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	protected void parseLegendURL(KXmlParser parser) throws IOException,
			XmlPullParserException {
		int currentTag;
		boolean end = false;

		parser.require(KXmlParser.START_TAG, null, CapabilitiesTags.LEGENDURL);
		currentTag = parser.nextTag();

		String value = new String();
		LegendURL legend = new LegendURL();

		// First of all set whether the layer is Queryable reading the
		// attribute.
		value = parser.getAttributeValue("", CapabilitiesTags.WIDTH);

		if (value != null) {
			legend.width = Integer.parseInt(value);
		}

		value = parser.getAttributeValue("", CapabilitiesTags.HEIGHT);

		if (value != null) {
			legend.height = Integer.parseInt(value);
		}

		while (!end) {
			switch (currentTag) {
			case KXmlParser.START_TAG:

				if (parser.getName().compareTo(CapabilitiesTags.FORMAT) == 0) {
					legend.format = parser.nextText();
				} else if (parser.getName().compareTo(
						CapabilitiesTags.ONLINERESOURCE) == 0) {
					value = parser.getAttributeValue("",
							CapabilitiesTags.XLINK_TYPE);

					if (value != null) {
						legend.onlineResource_type = value;
					}

					value = parser.getAttributeValue("",
							CapabilitiesTags.XLINK_HREF);

					if (value != null) {
						legend.onlineResource_href = value;
					}
				}

				break;

			case KXmlParser.END_TAG:

				if (parser.getName().compareTo(CapabilitiesTags.LEGENDURL) == 0) {
					end = true;
				}

				break;

			case KXmlParser.TEXT:
				break;
			}

			if (!end) {
				currentTag = parser.next();
			}
		}

		parser.require(KXmlParser.END_TAG, null, CapabilitiesTags.LEGENDURL);
	}

	/**
	 * gets the LegendURL OnlineResource type
	 */
	public String getLegendURLOnlineResourceType() {
		if (legendURL != null) {
			return legendURL.onlineResource_type;
		} else {
			return null;
		}
	}

	/**
	 * gets the LegendURL OnlineResource href
	 */
	public String getLegendURLOnlineResourceHRef() {
		if (legendURL != null) {
			return legendURL.onlineResource_href;
		} else {
			return null;
		}
	}

	public String getLegendURLFormat() {
		if (legendURL != null) {
			return legendURL.format;
		} else {
			return null;
		}
	}

	public int getLegendURLWidth() {
		if (legendURL != null) {
			return legendURL.width;
		}

		return 0;
	}

	public int getLegendURLHeight() {
		if (legendURL != null) {
			return legendURL.height;
		}

		return 0;
	}

	/**
	 * sets LegendURL
	 */
	protected void setLegendURL(LegendURL legendURL) {
		this.legendURL = legendURL;
	}

	/**
	 * <p>
	 * gets the style name
	 * </p>
	 * 
	 * @return style name
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>
	 * sets the style name.
	 * </p>
	 * 
	 * @param _name
	 */
	public void setName(String _name) {
		name = _name;
	}

	/**
	 * <p>
	 * gets the style title
	 * </p>
	 * 
	 * 
	 * @return style title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * <p>
	 * Sets style title
	 * </p>
	 * 
	 * 
	 * @param _title
	 */
	public void setTitle(String _title) {
		title = _title.trim();
	}

	/**
	 * <p>
	 * gets style abstract
	 * </p>
	 * 
	 * 
	 * @return style abstract
	 */
	public String getAbstract() {
		return styleAbstract;
	}

	/**
	 * <p>
	 * sets style abstract
	 * </p>
	 * 
	 * 
	 * @param aabstract
	 *            style abstract
	 */
	public void setAbstract(String aabstract) {
		styleAbstract = aabstract;
	}

	/**
	 * <p>
	 * Inner class describing the Legend URL defined for styles in the
	 * specifications in ArcIms
	 * </p>
	 * 
	 */
	protected class LegendURL {
		public int width;
		public int height;
		public String format;
		public String onlineResource_type;
		public String onlineResource_href;

		public LegendURL() {
			width = 0;
			height = 0;
			format = new String();
			onlineResource_type = new String();
			onlineResource_href = new String();
		}
	}
}
