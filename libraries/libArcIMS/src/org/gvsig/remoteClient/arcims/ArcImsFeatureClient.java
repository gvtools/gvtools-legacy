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

import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;
import org.gvsig.remoteClient.exceptions.ServerErrorException;

import java.awt.geom.Rectangle2D;

import java.util.ArrayList;


/**
 * Class that connects with ArcIMS and retrieves a Feature Layer
 * @author jsanz
 *
 */
public class ArcImsFeatureClient extends ArcImsClientP {
    public ArcImsFeatureClient(String host, String service, String serviceType) {
        super(host, service, serviceType);
    }

    /**
     * Requests a map
     * @param status
     * @return
     * @throws ArcImsException
     * @throws ServerErrorException
     */
    public ArrayList getMap(ArcImsVectStatus status)
        throws ArcImsException, ServerErrorException {
        return ((ArcImsProtFeatureHandler) handler).getMap(status);
    }

    /**
     * Requests service extent in view coordinates
     * @param status
     * @param subfields
     * @return
     * @throws ArcImsException
     * @throws ServerErrorException
     */
    public Rectangle2D getLayerExtent(ArcImsVectStatus status)
        throws ArcImsException, ServerErrorException {
        return ((ArcImsProtFeatureHandler) handler).getLayerExtent(status);
    }

    /**
     * @param status
     * @param subfields
     * @param where
     * @param envelope
     * @return
     * @throws ArcImsException
     */
    public ArrayList getAttributes(ArcImsVectStatus status, String[] subfields,
        String where, Rectangle2D envelope) throws ArcImsException {
        return ((ArcImsProtFeatureHandler) handler).getAttributes(status,
            subfields, where, envelope);
    }

    /**
     * @param status
     * @param subfields
     * @param where
     * @param envelope
     * @return
     * @throws ArcImsException
     */
    public ArrayList getAttributesWithEnvelope(ArcImsVectStatus status,
        String[] subfields, String where, Rectangle2D envelope)
        throws ArcImsException {
        return ((ArcImsProtFeatureHandler) handler).getAttributesWithEnvelope(status,
            subfields, where, envelope);
    }
}
