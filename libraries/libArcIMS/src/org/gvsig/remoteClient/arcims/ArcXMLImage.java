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

import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;
import org.gvsig.remoteClient.arcims.utils.ServiceInformation;

/**
 * Class that provides static methods to generate ImageServer related ArcXML
 * requests
 * 
 * @author jsanz
 * @author jcarrasco
 */
public class ArcXMLImage extends ArcXML {
	/**
	 * Creates a complete request in ArcXML for an ImageService from an
	 * ArcImsStatus; including extent, format and so on
	 * 
	 * @see org.gvsig.remoteClient.arcims.ArcImsStatus
	 * @param status
	 * @return String
	 */
	public static String getMapRequest(ArcImsStatus status) {
		/**
		 * Layers that we want to request
		 */
		Vector layers = status.getLayerIds();

		/**
		 * The EPSG code that image requested will have, the ArcIMS server will
		 * reproject data into this code, see <a
		 * href="http://www.epsg.org">EPSG</a>
		 */
		String srs = new String();

		/**
		 * We suppose that status.getSrs() allways will give a string started by
		 * this string
		 */
		String ini_srs = ServiceInfoTags.vINI_SRS;

		/**
		 * Gets de Decimal Separator from the status.ServiceInfo
		 */
		ServiceInformation si = status.getServiceInfo();
		char ds = si.getSeparators().getDs();

		/**
		 * Is the ServiceInfo FeatureCoordsys assumed?
		 */
		boolean srsAssumed = si.isSrsAssumed();

		/**
		 * Assign the srs from the status
		 * 
		 * @see org.gvsig.remoteClient.RemoteClientStatus#getSrs()
		 */
		if (!srsAssumed && status.getSrs().startsWith(ini_srs)) {
			srs = status.getSrs().substring(ini_srs.length()).trim();
		} else {
			srs = "";
		}

		return getImageRequest(ArcXML.getEnvelope(status.getExtent(), ds), // envelope
				ArcXML.getFilterCoordsys(srs), // filter
				ArcXML.getFeatureCoordsys(srs), // feature
				status.getFormat(), // image format
				new Dimension(status.getWidth(), status.getHeight()), // image
																		// size
				layers // layers to draw
		);
	}

	/**
	 * Creates a custom ArcXML request to obtain a request projected by the
	 * ArcIms server
	 * 
	 * @param srsInput
	 *            The SRS of the coordinates provided
	 * @param srsOutput
	 *            The SRS of the coordinates needed
	 * @param envelope
	 *            Rectangle2D with the envelope provided
	 * @param ds
	 *            Char with the Decimal Separator
	 * @param imageSize
	 *            Dimension object
	 * @param oneLayerId
	 *            A dumb layer identificator (but valid) of the service
	 * @return String
	 */
	public static String getCustomExtentRequest(String srsInput,
			Rectangle2D envelope, String srsOutput, char ds,
			Dimension imageSize, String format, String oneLayerId) {
		/**
		 * The returned request
		 */
		String request = new String();

		/**
		 * To this request we only need a layer, i.e. the first of the service
		 */
		Vector layer = new Vector(1);
		layer.add(oneLayerId);

		request = getImageRequest(ArcXML.getEnvelope(envelope, ds), // envelope
				ArcXML.getFilterCoordsys(srsInput), // filter
				ArcXML.getFeatureCoordsys(srsOutput), // feature
				format, // image format
				imageSize, // image size
				layer // layers to draw
		);

		return request;
	}

	/**
	 * Creates a complete request in ArcXML for an ImageService. This private
	 * method is used for every request that needs a GET_IMAGE.
	 * 
	 * @see ArcImsStatus
	 * @see org.gvsig.remoteClient.arcims.ArcImsProtImageHandler#getServiceExtent
	 * @param envelope
	 *            Envelope of the request
	 * @param filterCoordsys
	 *            SRS of input data
	 * @param featureCoordsys
	 *            SRS of output data
	 * @param format
	 *            Image format (PNG,..)
	 * @param imageSize
	 *            Dimension object with image size to be requested
	 * @param layerIds
	 *            Vector with layer Ids to retrieve
	 * @return String
	 */
	private static String getImageRequest(String envelope,
			String filterCoordsys, String featureCoordsys, String format,
			Dimension imageSize, Vector layerIds) {
		/**
		 * Building the heading of the request
		 */
		String request = new String();
		request = "<?xml version = '1.0' encoding = 'UTF-8'?>\r\n"
				+ ArcXML.startRequest("1.1")
				+ "\t\t<GET_IMAGE autoresize=\"false\" show=\"layers\">\r\n"
				+ "\t\t\t<PROPERTIES>\r\n";

		if (!envelope.equals("")) {
			request += ("\t\t\t\t" + envelope + "\r\n");
		}

		if (imageSize != null) {
			request += ("\t\t\t\t" + ArcXML.getImageSize(imageSize) + "\r\n");
		}

		if (!featureCoordsys.equals("")) {
			request += ("\t\t\t\t" + featureCoordsys + "\r\n");
		}

		if (!filterCoordsys.equals("")) {
			request += ("\t\t\t\t" + filterCoordsys + "\r\n");
		}

		request += ("\t\t\t\t" + ArcXML.getBackground(Color.WHITE, Color.WHITE) + "\r\n");

		if (!format.equals("")) {
			request += ("\t\t\t\t" + "<OUTPUT type=\"" + format + "\"/>\r\n");
		}

		// Build the layerlist
		request += ("\t\t\t\t" + "<LAYERLIST order=\"true\">\r\n");

		// "\t\t\t\t\t"+"<LAYERDEF id=\""+status.getLayerNames()+"\" visible=\"true\"/>\r\n"+

		/**
		 * Building the string that specifies what layers will be requested
		 */
		for (int i = 0; i < layerIds.size(); i++) {
			request += ("\t\t\t\t\t<LAYERDEF id=\""
					+ layerIds.elementAt(i).toString() + "\" visible=\"true\" />\r\n");
		}

		/**
		 * Building the end of the request
		 */
		request += ("\t\t\t\t" + "</LAYERLIST>\r\n" + "\t\t\t</PROPERTIES>\r\n"
				+ "\t\t</GET_IMAGE>\r\n" + ArcXML.endRequest());
		return request;
	}

	/**
	 * Creates the ArcXML retrieve INFO of a specific location dependig if the
	 * layer is a FEATURECLASS or a IMAGE
	 * 
	 * @see org.gvsig.remoteClient.arcims.ArcImsProtImageHandler#getElementInfo(ArcImsStatus,
	 *      int, int, int)
	 * @param layerType
	 *            A String with the layer type @see
	 *            org.gvsig.remoteClient.arcims.utils.ServiceInfoTags
	 * @param id
	 *            The layer id to request
	 * @param coords
	 *            A pair of coordinates with the center of the request
	 * @param dists
	 *            A pair of distances to extend the point to a BoundaryBox
	 * @param coordsys
	 *            A valid EPSG code
	 * @return String with the ArcXML
	 */
	public static String getInfoRequest(String layerType, String id,
			double[] coords, double[] dists, String coordsys, char ds) {
		StringBuffer sb = new StringBuffer();
		Rectangle2D rect = new Rectangle2D.Double();

		rect.setFrameFromDiagonal(coords[0] - dists[0], coords[1] - dists[1],
				coords[0] + dists[0], coords[1] + dists[1]);

		if (layerType.equals(ServiceInfoTags.vLAYERTYPE_F)) {
			sb.append(ArcXML.startRequest("1.1"));
			sb.append("\t\t<GET_FEATURES outputmode=\"xml\" checkesc=\"true\" geometry=\"false\" envelope=\"false\">\r\n");
			sb.append("\t\t\t<LAYER id=\"" + id + "\" />\r\n");
			sb.append("\t\t\t<SPATIALQUERY subfields=\"#ALL#\" >\r\n");

			if (!coordsys.equals("")) {
				sb.append("\t\t\t\t" + ArcXML.getFeatureCoordsys(coordsys)
						+ "\r\n");
				sb.append("\t\t\t\t" + ArcXML.getFilterCoordsys(coordsys)
						+ "\r\n");
			}

			sb.append("\t\t\t\t<SPATIALFILTER relation=\"area_intersection\">\r\n");
			sb.append(getEnvelope(rect, ds));
			sb.append("\t\t\t\t</SPATIALFILTER>\r\n");
			sb.append("\t\t\t</SPATIALQUERY>\r\n");
			sb.append("\t\t</GET_FEATURES>\r\n");
			sb.append(ArcXML.endRequest());
		} else if (layerType.equals(ServiceInfoTags.vLAYERTYPE_I)) {
			double xCenter = rect.getCenterX();
			double yCenter = rect.getCenterY();

			// We need to format these values into the correct encoding
			DecimalFormatSymbols dfs = new DecimalFormatSymbols();
			dfs.setDecimalSeparator(ds);

			DecimalFormat formatter = new DecimalFormat(PATRON, dfs);

			String xF = formatter.format(xCenter);
			String yF = formatter.format(yCenter);

			sb.append(ArcXML.startRequest("1.1"));
			sb.append("\t\t<GET_RASTER_INFO x=\"" + xF + "\" y=\"" + yF
					+ "\" layerid=\"" + id + "\">\r\n");
			sb.append("\t\t\t<COORDSYS id=\"" + coordsys + "\"/>\r\n");
			sb.append("\t\t</GET_RASTER_INFO>\r\n");
			sb.append(ArcXML.endRequest());
		}

		return sb.toString();
	}
}
