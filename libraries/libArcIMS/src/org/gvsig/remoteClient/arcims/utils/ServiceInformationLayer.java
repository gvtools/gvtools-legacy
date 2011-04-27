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

/**
 *
 */
package org.gvsig.remoteClient.arcims.utils;

import org.gvsig.remoteClient.utils.BoundaryBox;


/**
 * Abstract class that stores the information of every layer that is
 * part of the Service
 *  @author jsanz
 */
public abstract class ServiceInformationLayer {
    private String id;
    private double maxscale;
    private double minscale;
    protected String name;
    protected String type;
    private String visible;
    private BoundaryBox envelope;

    // public String fclasstype;
    public ServiceInformationLayer() {
        id = new String();
        name = new String();
        type = new String();
        visible = new String();
        envelope = new BoundaryBox();
        maxscale = -1.0;
        minscale = -1.0;

        // fclasstype = new String();
    }

    /**
     * @return Returns the envelope.
     */
    public BoundaryBox getEnvelope() {
        return envelope;
    }

    /**
     * @param envelope The envelope to set.
     */
    public void setEnvelope(BoundaryBox envelope) {
        this.envelope = envelope;
    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Returns the maxscale.
     */
    public double getMaxscale() {
        return maxscale;
    }

    /**
     * @param maxscale The maxscale to set.
     */
    public void setMaxscale(double maxscale) {
        this.maxscale = maxscale;
    }

    /**
     * @return Returns the minscale.
     */
    public double getMinscale() {
        return minscale;
    }

    /**
     * @param minscale The minscale to set.
     */
    public void setMinscale(double minscale) {
        this.minscale = minscale;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return Returns the visible.
     */
    public String getVisible() {
        return visible;
    }

    /**
     * @param visible The visible to set.
     */
    public void setVisible(String visible) {
        this.visible = visible;
    }
}
