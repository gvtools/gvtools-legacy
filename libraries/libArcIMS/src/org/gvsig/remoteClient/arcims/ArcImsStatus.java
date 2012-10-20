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

import java.awt.geom.Rectangle2D;
import java.util.Vector;

import org.gvsig.remoteClient.RemoteClientStatus;
import org.gvsig.remoteClient.arcims.utils.ServiceInformation;

/**
 * Describes the status of a ArcImsclient, so it adds to the Remote client
 * status a list of layers, a list of layer styles, the extent of the map.
 * Provides the functionality to modify these lists.
 * 
 * @see org.gvsig.remoteClient.RemoteClientStatus
 * @author jcarrasco
 * @author jsanz
 * 
 */
public class ArcImsStatus extends RemoteClientStatus implements Cloneable {
	/**
	 * List of layer to be retrieved by the ArcIms
	 */
	private Vector layers;

	/**
	 * List of layer styles
	 */
	private Vector styles;

	/**
	 * Extent required by the ArcIms client
	 */
	private Rectangle2D extent;

	/**
	 * The ArcIMS Service delivers images with transparency
	 */
	private boolean transparency;
	private String onlineResource;
	private String service;
	private Vector dimensions;
	private ServiceInformation serviceInfo;
	public boolean verbose;

	// Added by jldominguez to allow image identification with
	// the arcimsstatus variables
	private String server;

	/**
	 * Constructor
	 * 
	 */
	public ArcImsStatus() {
		layers = new Vector();
		styles = new Vector();
		verbose = false;
	}

	/**
	 * Retrieves a vector of Dimensions
	 * 
	 * @return Vector, the list of dimensions
	 */
	public Vector getDimensions() {
		return dimensions;
	}

	/**
	 * Sets the dimensions of the client
	 * 
	 * @param dimensions
	 *            A vector of dimensions
	 */
	public void setDimensions(Vector dimensions) {
		this.dimensions = dimensions;
	}

	/**
	 * <p>
	 * Retrieves the layer list required by the ArcIms client
	 * </p>
	 * 
	 * @return Vector, the list of layers
	 */
	public Vector getLayerIds() {
		return layers;
	}

	/**
	 * Sets the list of layers required by the ArcIms client
	 * 
	 * @param _layers
	 *            Vector, the list of layers to set
	 */
	public void setLayerIds(Vector _layers) {
		layers = _layers;
	}

	/**
	 * <p>
	 * Adds a layer to the list of layers required by the ArcIms client
	 * </p>
	 * 
	 * @param alayerName
	 *            name of the layer to be added.
	 */
	public void addLayerName(String alayerName) {
		layers.add(alayerName);
	}

	/**
	 * <p>
	 * Removes a layer from the layer list
	 * </p>
	 * 
	 * @param alayerName
	 * @return true if the layer name has been deleted from the list
	 */
	public boolean removeLayerName(String alayerName) {
		return layers.remove(alayerName);
	}

	/**
	 * <p>
	 * Gets the styles list required by the ArcIms client
	 * </p>
	 * 
	 * @return Vector with the list of layer styles
	 */
	public Vector getStyles() {
		return styles;
	}

	/**
	 * <p>
	 * sets the styles list required by the ArcIms client
	 * </p>
	 * 
	 * 
	 * @param astyles
	 *            list to be set as the required styles.
	 */
	public void setStyles(Vector astyles) {
		styles = astyles;
	}

	/**
	 * <p>
	 * Adds a style name to the styles list required by the ArcIms client
	 * </p>
	 * 
	 * 
	 * @param aname
	 *            style name to be added
	 */
	public void addStyleName(String aname) {
		styles.add(aname);
	}

	/**
	 * <p>
	 * Removes a style from the list of styles required by the ArcIms client
	 * </p>
	 * 
	 * 
	 * @param aname
	 *            style name to be removed
	 */
	public boolean removeStyleName(String aname) {
		return styles.remove(aname);
	}

	/**
	 * <p>
	 * Gets the extent defined by the map
	 * </p>
	 */
	public Rectangle2D getExtent() {
		return extent;
	}

	/**
	 * <p>
	 * Sets the extent defined by the map
	 * </p>
	 */
	public void setExtent(Rectangle2D extent) {
		this.extent = extent;
	}

	/**
	 * Gets transparency
	 * 
	 * @return boolean
	 */
	public boolean getTransparency() {
		return transparency;
	}

	/**
	 * Sets transparency
	 * 
	 * @param arcimsTransparency
	 */
	public void setTransparency(boolean arcimsTransparency) {
		transparency = arcimsTransparency;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof ArcImsStatus)) {
			return false;
		}

		ArcImsStatus s = (ArcImsStatus) obj;

		// Compare layer names
		if (!(((s.getLayerIds() == null) && (this.getLayerIds() == null)) || s
				.getLayerIds().equals(this.getLayerIds()))) {
			return false;
		}

		// Compare extent
		if (!(((s.getExtent() == null) && (this.getExtent() == null)) || s
				.getExtent().equals(this.getExtent()))) {
			return false;
		}

		// Compare height
		if (s.getHeight() != this.getHeight()) {
			return false;
		}

		// Compare width
		if (s.getWidth() != this.getWidth()) {
			return false;
		}

		// Compare styles
		if (!(((s.getStyles() == null) && (this.getStyles() == null)) || s
				.getStyles().equals(this.getStyles()))) {
			return false;
		}

		// Compare transparencies
		if (s.getTransparency() != this.getTransparency()) {
			return false;
		}

		// Compare srs
		if (!(((s.getSrs() == null) && (this.getSrs() == null)) || s.getSrs()
				.equals(this.getSrs()))) {
			return false;
		}

		// Compare exception formats
		if (!(((s.getExceptionFormat() == null) && (this.getExceptionFormat() == null)) || s
				.getExceptionFormat().equals(this.getExceptionFormat()))) {
			return false;
		}

		// Compare formats
		if (!(((s.getFormat() == null) && (this.getFormat() == null)) || s
				.getFormat().equals(this.getFormat()))) {
			return false;
		}

		// Compare online resources
		if (!(((s.getOnlineResource() == null) && (this.getOnlineResource() == null)) || s
				.getOnlineResource().equals(this.getOnlineResource()))) {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		ArcImsStatus newObject = new ArcImsStatus();
		Vector v = this.getLayerIds();

		if (v != null) {
			newObject.setLayerIds((Vector) v.clone());
		}

		Rectangle2D r = this.getExtent();

		if (r != null) {
			newObject.setExtent((Rectangle2D) r.clone());
		}

		newObject.setHeight(this.getHeight());
		newObject.setWidth(this.getWidth());
		v = this.getStyles();

		if (v != null) {
			newObject.setStyles((Vector) v.clone());
		}

		newObject.setTransparency(this.getTransparency());
		newObject.setSrs(this.getSrs());
		newObject.setFormat(this.getFormat());
		newObject.setOnlineResource(this.getOnlineResource());

		ServiceInformation si = this.getServiceInfo();

		if (si != null) {
			newObject.setServiceInformation((ServiceInformation) si.clone());
		}

		return newObject;
	}

	/**
	 * Returns the URL that the server specified for a ArcIms request if any was
	 * described in its capabilities document.
	 * 
	 * @return <b>String</b> containing the URL for this operationName or
	 *         <B>null</B> if none was specified.
	 */
	public String getOnlineResource() {
		return onlineResource;
	}

	/**
	 * Sets the string literal containing the URL of an online resource for a
	 * specific ArcIms request.
	 * 
	 * @param url
	 *            String containing the URL for the given ArcIms request
	 */
	public void setOnlineResource(String url) {
		onlineResource = url;
	}

	/**
	 * Gets service
	 * 
	 * @return service
	 */
	public String getService() {
		return service;
	}

	/**
	 * Sets service
	 * 
	 * @param service
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * Sets the Service Information object of the status
	 * 
	 * @param si
	 */
	public void setServiceInformation(ServiceInformation si) {
		this.serviceInfo = si;
	}

	/**
	 * Gets the Servcie Information
	 */
	public ServiceInformation getServiceInfo() {
		return serviceInfo;
	}

	/**
	 * Gets server
	 */
	public String getServer() {
		return server;
	}

	/**
	 * Sets server
	 * 
	 * @param server
	 */
	public void setServer(String server) {
		this.server = server;
	}
}
