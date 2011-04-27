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

import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.rendering.ILegend;

import org.apache.log4j.Logger;

import org.gvsig.remoteClient.RasterClient;
import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;
import org.gvsig.remoteClient.arcims.utils.ServiceInformation;
import org.gvsig.remoteClient.wms.ICancellable;

import java.net.URL;

import java.util.TreeMap;


/**
 * Abstract class the with the necessary logic
 * to connect to a ArcIms and interpretate the data.
 *
 * The P at the end of the name is to avoid conflict with existing empty
 * class at libRemoteServices package @seeorg.gvsig.remoteClient.arcims.ArcImsClient
 *
 */
public abstract class ArcImsClientP extends RasterClient {
    private static Logger logger = Logger.getLogger(ArcImsProtocolHandler.class.getName());

    /**
     * Handler of the service used by the client to contact with ArcIMS
     * @see ArcImsProtocolHandler#ArcImsProtocolHandler()
     */
    protected ArcImsProtocolHandler handler;

    /**
     * Layers of the service
     */
    private TreeMap layers = new TreeMap();

    //    /**
    //     * Version and build number of the ArcIMS Server. This data is only 
    //     * for information purposes as the ArcXML is (at this time) fully compatible with 
    //     * 4 and 9 versions of ArcIMS.
    //     */
    //    private String[] version = new String[2]; 

    /**
    * Constructor.
    * the parameter host, indicates the ArcIms host to connect.
    * */
    public ArcImsClientP(String host, String service, String serviceType) {
        //m_log = LogManager.getLogManager().getLogger(getClass().getName());
        setHost(host);
        setServiceName(service);

        try {
            handler = ArcImsProtocolHandlerFactory.negotiate(serviceType);

            //handler = new org.gvsig.remoteClient.arcims.
            handler.setHost(host);
            handler.setService(service);
            handler.serviceInfo.setType(serviceType);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * <p>One of the three interfaces that  ArcIms defines. Request a map.</p>
     * @throws ServerErrorException
     * @throws ArcImsException
     * @throws ServerErrorException
     */

    //public abstract File getMap(ArcImsStatus status) throws ArcImsException, ServerErrorException;

    /**
     * <p>One of the three interfaces defined by  ArcIms, it gets the service capabilities</p>
     *
     */
    public void getCapabilities(ArcImsStatus status) throws ArcImsException {
        handler.getCapabilities(status);

        layers = handler.layers;

        // rootLayer = handler.rootLayer;
    }

    /**
     * <p>One of the three interfaces defined by  ArcIms, it gets the service capabilities</p>
     *
     */

    /*
    public void getCapabilities(URL url) {
        handler.getCapabilities(url);
        layers = handler.layers;
        rootLayer = handler.rootLayer;
    }
    */

    /**
     * <p>One of the three interfaces defined by the  ArcIms,
     * it gets the information about a feature requested</p>
     * @return  Proper XML to build the Element Info GUI
     */
    public String getFeatureInfo(ArcImsStatus status, int x, int y,
        int featureCount) throws ArcImsException {
        return handler.getElementInfo(status, x, y, featureCount);
    }

    /**
     * <p> Reads from the ArcIms Capabilities, the layers available in the service</p>
     * @return a TreeMap with the available layers in the ArcIms
     */
    public TreeMap getLayers() {
        return layers;
    }

    /**
     * <p>Reads from the ArcIms Capabilities the number if layers available in the service</p>
     * @return number of layers available
     */
    public int getNumberOfLayers() {
        if (layers != null) {
            return layers.size();
        }

        return 0;
    }

    /**
     * <p>Gets the image formats available in the Service to retrieve the maps</p>
     * @return a vector with all the available formats
     */
    public boolean isQueryable() {
        return handler.getServiceInformation().isQueryable();
    }

    public void close() {
        // your code here
    }

    /**
     * Gets the Service information included in the Capabilities
     * */
    public ServiceInformation getServiceInformation() {
        return handler.getServiceInformation();
    }

    /**
     * <p>Checks the connection to de remote ArcIms and requests its capabilities.</p>
     *
     */
    public boolean connect(boolean override, ICancellable cancel) {
        try {
            if (handler == null) {
                if (getHost().trim().length() > 0) {
                    handler.setHost(getHost());
                } else {
                    //must to specify host first!!!!
                    return false;
                }
            }

            getCapabilities(null);

            return true;
        } catch (Exception e) {
            logger.error("While connecting", e);

            return false;
        }
    }

    public boolean connect(URL server, ICancellable cancel) {
        setHost(server.toString());

        return connect(false, cancel);
    }

    /**
     * Returns the handler.
     * @return The handler
     */
    public ArcImsProtocolHandler getHandler() {
        return handler;
    }

    /**
     * Method called to obtain the Legend for an ArcIMS layer ID
     * @see es.prodevelop.cit.gvsig.arcims.fmap.layers.FFeatureLyrArcIMS#setInitialLegend()
     * @param layerId
     * @return
     * @throws ArcImsException
     */
    public ILegend getLegend(String layerId, SelectableDataSource sds) throws ArcImsException {
        return handler.getLegend(layerId, sds);
    }
}
