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


/**
 * @author jsanz
 *
 */
public class ExactValueMap extends TypeValueMap {
    public static final String TAG = ServiceInfoTags.tEXACT;
    public static final String IS_EXACT = "IsExact";
    public static final String IS_CONTAINED = "isContained";
    private String method;
    private String value;

    /**
     * @param value
     */
    public ExactValueMap() {
        super("");
        method = ExactValueMap.IS_EXACT;
    }

    /**
     * Generates an XML representation of the ValueMap type
     */
    public String toString() {
        String param = "";

        if (SymbolUtils.isVoid(value)) {
            param += (" value=\"" + value + "\"");
        }

        if (SymbolUtils.isVoid(method)) {
            param += (" method=\"" + method + "\"");
        }

        if (SymbolUtils.isVoid(label)) {
            param += (" label=\"" + label + "\"");
        }

        return "<" + ExactValueMap.TAG + param + "\">\r\n" + symbol.toString() +
        "</" + ExactValueMap.TAG + ">\r\n";
    }

    /**
     * @return Returns the method.
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method The method to set.
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }
}
