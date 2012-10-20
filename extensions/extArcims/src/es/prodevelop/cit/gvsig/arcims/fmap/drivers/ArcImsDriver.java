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
 *   Generalitat Valenciana
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
package es.prodevelop.cit.gvsig.arcims.fmap.drivers;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.URL;

import org.gvsig.remoteClient.arcims.ArcImsStatus;
import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;
import org.gvsig.remoteClient.wms.ICancellable;

/**
 * This interface offers the functionality of an ArcIMS client.
 * 
 * @see es.prodevelop.cit.gvsig.arcims.fmap.drivers.FMapRasterArcImsDriver
 *      FMapRasterArcImsDriver
 * @author jldominguez
 */
public interface ArcImsDriver {
	/**
	 * Gets the layers available on the server (the class that implements this
	 * interface will get the service name from the user)
	 * 
	 * @param server
	 *            ArcIMS server's URL
	 */
	public void getCapabilities(URL server, ICancellable cancel)
			throws ArcImsException, IOException, ProtocolException;

	/**
	 * Gets the visual information from the layers of the required service (that
	 * is, the map itself) depending on the graphic context (coord. system,
	 * view's size, etc)
	 * 
	 * @param status
	 *            the graphic context in which the request is performed
	 * @return the graphic data to be viewed, that is, the map
	 */
	public Object getMap(ArcImsStatus status) throws ArcImsException,
			IOException, ProtocolException;

	/**
	 * Gets the layer's available information on a particular xy coordinates
	 * (usually derived from a mouse click). If it's a raster layer, a request
	 * will be sent to the server asking for the data associated to the elements
	 * (polygon, line or point) which cover the clicked pixel.
	 * 
	 * @param status
	 *            the graphic context in which the request is performed (ccord.
	 *            system, view's dimension etc.)
	 * @param i
	 *            x coordinate of the queried pixel
	 * @param j
	 *            y coordinate of the queried pixel
	 * @param max_value
	 *            maximun number of vector elements whose information will be
	 *            retrieved.
	 * @return the available information at the given coordinates
	 */
	public String getFeatureInfo(ArcImsStatus status, int i, int j,
			int max_value) throws ArcImsException, IOException,
			ProtocolException;
}
