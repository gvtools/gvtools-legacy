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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Vector;

import org.gvsig.remoteClient.arcims.utils.ServiceInformation;
import org.gvsig.remoteClient.arcims.utils.ServiceInformationLayer;

/**
 * Class that provides static methods to generate general ArcXML requests
 * 
 * @author jsanz
 * @author jcarrasco
 */
public class ArcXML {
	public static final String PATRON = new String("0.0########");

	/**
	 * Starting string for an ArcXML request
	 * 
	 * @param version
	 * @return String
	 */
	protected static String startRequest(String version) {
		return "<ARCXML version=\"" + version + "\">\r\n" + "\t<REQUEST>\r\n";
	}

	/**
	 * Ending string for an ArcXML request
	 * 
	 * @return string
	 */
	protected static String endRequest() {
		return "\t</REQUEST>\r\n" + "</ARCXML>\r\n";
	}

	/**
	 * Creates the ArcXML request for a Service_Info
	 * 
	 * @param version
	 * @return String
	 */
	public static String getServiceInfoRequest(String version) {
		/**
		 * We will request all the information the server can expose of its
		 * services
		 */
		String envelope = "true";
		String fields = "true";
		String renderer = "true";
		String extensions = "true";

		return startRequest(version) + "\t\t<GET_SERVICE_INFO envelope=\""
				+ envelope + "\" fields=\"" + fields + "\" renderer=\""
				+ renderer + "\"\r\n" + "\t\t\textensions=\"" + extensions
				+ "\" />\r\n" + endRequest();
	}

	/**
	 * Builds a start LAYER element for a XML response of a GetElementInfo
	 * 
	 * @see ArcImsProtocolHandler#getElementInfo(ArcImsStatus, int, int, int)
	 * @param id
	 * @param si
	 * @return String, the start tag for the LAYER element
	 */
	public static String getLayerHeaderInfoResponse(String id,
			ServiceInformation si) {
		String name = null;
		Vector layers = si.getLayers();

		int i = 0;
		ServiceInformationLayer sil;

		for (i = 0; i < layers.size(); i++) {
			sil = (ServiceInformationLayer) layers.get(i);

			if (sil.getId().equals(id)) {
				name = sil.getName();

				break;
			}
		}

		return "<LAYER ID=\"" + id + "\" NAME=\"" + name + "\">";
	}

	/**
	 * Builds a end LAYER element for a XML response of a GetElementInfo
	 * 
	 * @see ArcImsProtocolHandler#getElementInfo(ArcImsStatus, int, int, int)
	 * @return String, the end tag for the LAYER element
	 */
	protected static String getLayerFooterInfoResponse() {
		return "\t</LAYER>\r\n";
	}

	/**
	 * Creates a ENVELOPE element from a Rectangle2D
	 * 
	 * @param r
	 *            Rectangle2D to parse
	 * @param ds
	 *            with the Decimal separator
	 * @return String with proper ArcXML tags
	 * @see java.awt.geom.Rectangle2D
	 */
	protected static String getEnvelope(Rectangle2D r, char ds) {
		if (r == null) {
			return "";
		} else {
			DecimalFormatSymbols dfs = new DecimalFormatSymbols();
			dfs.setDecimalSeparator(ds);

			DecimalFormat formatter = new DecimalFormat(PATRON, dfs);

			String envelope = new String();
			envelope = "<ENVELOPE " + "minx=\"" + formatter.format(r.getMinX())
					+ "\" miny=\"" + formatter.format(r.getMinY())
					+ "\" maxx=\"" + formatter.format(r.getMaxX())
					+ "\" maxy=\"" + formatter.format(r.getMaxY()) + "\"/>";

			return envelope;
		}
	}

	/**
	 * Creates a IMAGESIZE element from a Dimension object
	 * 
	 * @param sz
	 *            Dimension to parse
	 * @return String with proper ArcXML tags
	 * @see java.awt.Dimension
	 */
	protected static String getImageSize(Dimension sz) {
		return "<IMAGESIZE width=\"" + sz.width + "\" height=\"" + sz.height
				+ "\" />";
	}

	/**
	 * Creates the FILTERCOORDSYS element
	 * 
	 * @param srs
	 *            The EPSG code
	 * @return String with proper ArcXML tags
	 */
	protected static String getFilterCoordsys(String srs) {
		if (!srs.equals("")) {
			return "<FILTERCOORDSYS id=\"" + srs + "\" />";
		} else {
			return "";
		}
	}

	/**
	 * Creates the FEATURECOORDSYS element based on a EPSG code
	 * 
	 * @param srs
	 *            The EPSG code
	 * @return String with proper ArcXML tags
	 */
	protected static String getFeatureCoordsys(String srs) {
		if (!srs.equals("")) {
			return "<FEATURECOORDSYS id=\"" + srs + "\" />";
		} else {
			return "";
		}
	}

	/**
	 * Creates a BACKGROUND color for the ArcIMS service based on two colors
	 * 
	 * @param backColor
	 *            Color for the background
	 * @param transColor
	 *            Transparent color in the output image
	 * @return String with proper ArcXML tags
	 */
	protected static String getBackground(java.awt.Color backColor,
			Color transColor) {
		String strBackColor = new String(backColor.getRed() + ","
				+ backColor.getGreen() + "," + backColor.getBlue());
		String strTransColor = new String(transColor.getRed() + ","
				+ transColor.getGreen() + "," + transColor.getBlue());

		return "<BACKGROUND color=\"" + strBackColor + "\" transcolor=\""
				+ strTransColor + "\"/>";
	}

	/**
	 * Method that creates a GETCLIENTSERVICES ArcXML to request a complete list
	 * of Services available in an ArcIMS Server
	 * 
	 * @return String with proper ArcXML tags
	 */
	public static String getClientServices() {
		return "<GETCLIENTSERVICES/>";
	}

	/**
	 * Simple method to get string representations of double numbers according
	 * to a decimal separator
	 * 
	 * @param number
	 * @param ds
	 * @return
	 */
	public static String parseNumber(double number, char ds) {
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator(ds);

		DecimalFormat formatter = new DecimalFormat(PATRON, dfs);

		return formatter.format(number);
	}

	public static String replaceUnwantedCharacters(String str) {
		String resp = str;
		resp = resp.replace('.', '_');
		resp = resp.replace('#', 'z');

		return resp;
	}
}
