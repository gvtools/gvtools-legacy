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
package org.gvsig.remoteClient.arcims.styling.renderers;

import org.gvsig.remoteClient.arcims.styling.symbols.SymbolUtils;
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;

import java.util.Iterator;


/**
 * Class representing a scale dependent legend, it isn't supported at this time by gvSIG.
 * This renderer usually is formed by other GROUPRENDERER or VALUEMAPRENDERER. This way the
 * ArcIMS service can change its simbology according to the scale. In this case it will be
 * necessary to select one of the renderers.
 * @author jsanz
 *
 */
public class ScaleDependentRenderer extends GroupRenderer {
    public static final String TAG = ServiceInfoTags.tSCALEDEPENDENTRENDERER;
    private String lower;
    private String upper;

    /**
     * @param lower
     * @param upper
     */
    public ScaleDependentRenderer(String lower, String upper) {
        this.lower = lower;
        this.upper = upper;
    }

    /**
     * @return Returns the lower.
     */
    public String getLower() {
        return lower;
    }

    /**
     * @param lower The lower to set.
     */
    public void setLower(String lower) {
        this.lower = lower;
    }

    /**
     * @return Returns the upper.
     */
    public String getUpper() {
        return upper;
    }

    /**
     * @param upper The upper to set.
     */
    public void setUpper(String upper) {
        this.upper = upper;
    }

    /**
     * Generates an XML representation of the Renderer
     */
    public String toString() {
        String param = new String();

        if (SymbolUtils.isVoid(upper)) {
            param += (" upper =\"" + upper + "\"");
        }

        if (SymbolUtils.isVoid(lower)) {
            param += (" lower =\"" + lower + "\"");
        }

        StringBuffer sb = new StringBuffer();
        Iterator it = super.iterator();

        while (it.hasNext()) {
            sb.append(((Renderer) it.next()).toString());
        }

        return "<" + ScaleDependentRenderer.TAG + param + ">\r\n" +
        sb.toString() + "</" + ScaleDependentRenderer.TAG + ">\r\n";
    }
}
