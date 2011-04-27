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

import java.net.URL;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.gvsig.remoteClient.arcims.ArcImsClientP;
import org.gvsig.remoteClient.arcims.ArcImsImageClient;
import org.gvsig.remoteClient.arcims.ArcImsStatus;
import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;
import org.gvsig.remoteClient.exceptions.ServerErrorException;
import org.gvsig.remoteClient.wms.ICancellable;

import es.prodevelop.cit.gvsig.arcims.fmap.layers.ArcImsLayerNode;


/**
* ArcIMS driver implementation. Requests are passed to the <tt>ArcImsClientP</tt> object.
*
* @see org.gvsig.remoteClient.arcims.ArcImsImageClient ArcImsImageClient
* @author jldominguez
*/
public class FMapRasterArcImsDriver implements ArcImsDriver {
    private static Logger logger = Logger.getLogger(FMapRasterArcImsDriver.class.getName());
    private ArcImsImageClient client;
    private String server;
    private String service;
    private String serviceType;
    private boolean connectionDone = false;

    public FMapRasterArcImsDriver() {
    }

    /**
    * The constructor needs the server's URL and the name of the service to be used.
    *
    * @param host server's URL
    * @param service name of the service, chosen among the ones retrieved after a
    * request with the parameter <tt>ServiceName=Catalog</tt>
    */
    public FMapRasterArcImsDriver(String host, String service, String svcType) {
        serviceType = svcType;
        init(host, service);
    }

    /**
     * This method is called by the constructor and creates the
     * <tt>client</tt> oject.
     * @param host
     * @param service
     */
    public void init(String host, String service) {
        server = host;
        this.service = service;
        client = new ArcImsImageClient(host, service, this.serviceType);
    }

    /**
    * Gets available layers from the current server and service
    *
    * @return a TreeMap with available layers
     */
    public TreeMap getLayers() {
        return client.getLayers();
    }

    /**
    * Gets the layers available on the server (the class that implements
    * this interface will get the service name from the user)
    *
    * @param server ArcIMS server's URL
    */
    public void getCapabilities(URL server, ICancellable cancel)
        throws ArcImsException {
        if (connectionDone) {
            return;
        }
        else {
            connectionDone = getClient().connect(server, cancel);
        }

        if (!connectionDone) {
            throw new ArcImsException("connect_error");
        }
    }

    /**
    * Gets the visual information from the layers of
    * the required service (that is, the map itself) depending
    * on the graphic context (coord. system, view's size, etc)
    *
    * @param status the graphic context in which the request is performed
    * @return the graphic data to be viewed, that is, the map. For the imageservice,
    * it will be a <tt>File</tt> object.
    */
    public Object getMap(ArcImsStatus status) throws ArcImsException {
        try {
            return client.getMap(status);
        }
        catch (ServerErrorException e) {
            logger.error("While getting map ", e);
            throw new ArcImsException("ArcIMS Unexpected server error." +
                e.getMessage());
        }
    }

    /**
    * Gets the layer's available information on a particular xy coordinates
    * (usually derived from a mouse click). If it's a raster layer,
    * a request will be sent to the server asking for the data associated
    * to the elements (polygon, line or point) which cover the clicked
    * pixel.
    *
    * @param status the graphic context in which the request is performed
    * (ccord. system, view's dimension etc.)
    * @param i x coordinate of the queried pixel
    * @param j y coordinate of the queried pixel
    * @param  max_value maximun number of vector elements whose information
    * will be retrieved.
    * @return the available information at the given coordinates
    */
    public String getFeatureInfo(ArcImsStatus status, int i, int j,
        int max_value) throws ArcImsException {
        String r = "No info available.";

        try {
            r = client.getFeatureInfo(status, i, j, max_value);
        }
        catch (ArcImsException e) {
            logger.error("ArcImsException. ", e);
            throw e;
        }

        return r;
    }

    /**
    * Given a layer name, gets a node that contains relevant information
    * about the layer.
    *
    * @param layerName the name of the layer
    * @return a node with layer's information
    */
    public ArcImsLayerNode getLayer(String layerName) {
        ArcImsLayerNode node = new ArcImsLayerNode();
        node.setName(layerName);

        return node;
    }

    /**
     * This method starts a connection with the server and sends
     * a <tt>getCapabilities</tt> request.
     * @return <b>true</b> if the connection was successful, <b>false</b>
     * otherwise (bad or no server URL, for example)
     */
    public boolean connect(ICancellable cancel) {
        if (connectionDone) {
            return true;
        }
        else {
            connectionDone = client.connect(false, cancel);
            ;

            return connectionDone;
        }
    }

    /**
     * Gets the <tt>ArcImsClientP</tt> object, onto which requests
     * are passed.
     *
     * @return the object that actually performs requests.
     */
    public ArcImsClientP getClient() {
        if (client == null) {
            init(server, service);
        }

        return client;
    }

    /**
     * This method tells whether this layer is queriable with a call to
     * <tt>getFeatureInfo(...)</tt>
     *
     * @return <b>true</b> if method <tt>getFeatureInfo(...)</tt> can be invoqued,
     * <b>false</b> otherwise
     */
    public boolean isQueryable() {
        return client.isQueryable();
    }

    public boolean testFormat(ArcImsStatus status, String imgFormat) {
        try {
            return client.testFromat(status, imgFormat);
        }
        catch (ArcImsException e) {
            logger.error("While testing omage format ", e);
        }

        return false;
    }
}
