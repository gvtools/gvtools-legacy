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

import org.gvsig.remoteClient.arcims.styling.symbols.IArcIMSSymbol;
import org.gvsig.remoteClient.arcims.styling.symbols.SymbolUtils;


/**
 * @author jsanz
 *
 */
public class RangeValueMap extends TypeValueMap {
    public static final String TAG = "RANGE";
    public static final String EQ_ALL = "all";
    public static final String EQ_UPPER = "upper";
    public static final String EQ_LOWER = "lower";
    public static final String EQ_NONE = "none";
    private String lower;
    private String upper;
    private String equality;

    /**
     * @param lower
     * @param upper
     * @param equality
     * @param label
     */
    public RangeValueMap(String lower, String upper) {
        super("");
        this.lower = lower;
        this.upper = upper;
        this.equality = EQ_LOWER;
    }

    /**
     * Generates an XML representation of the ValueMap type
     */
    public String toString() {
        String param = "";

        if (SymbolUtils.isVoid(lower)) {
            param += (" lower=\"" + lower + "\"");
        }

        if (SymbolUtils.isVoid(upper)) {
            param += (" upper=\"" + upper + "\"");
        }

        if (SymbolUtils.isVoid(equality)) {
            param += (" equality=\"" + equality + "\"");
        }

        if (SymbolUtils.isVoid(label)) {
            param += (" label=\"" + label + "\"");
        }

        return "<" + RangeValueMap.TAG + param + "\">\r\n" + symbol.toString() +
        "</" + RangeValueMap.TAG + ">\r\n";
    }

    /**
     * @return Returns the equality.
     */
    public String getEquality() {
        return equality;
    }

    /**
     * @param equality The equality to set.
     */
    public void setEquality(String equality) {
        this.equality = equality;
    }

    /**
     * @return Returns the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label The label to set.
     */
    public void setLabel(String label) {
        this.label = label;
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
     * @return Returns the symbol.
     */
    public IArcIMSSymbol getSymbol() {
        return symbol;
    }

    /**
     * @param symbol The symbol to set.
     */
    public void setSymbol(IArcIMSSymbol symbol) {
        this.symbol = symbol;
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
}
