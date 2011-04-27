/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integraci�n de Tecnolog�as SL
 *   Conde Salvatierra de �lava , 34-10
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
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;


/**
 * @author jsanz
 *
 */
public class SimpleLabelRenderer extends SimpleRenderer
    implements ILabelRenderer {
    public static final String TAG = ServiceInfoTags.tSIMPLELABELRENDERER;
    private String rotationalangles;
    private String field;

    public SimpleLabelRenderer(String mfield, IArcIMSSymbol symbol) {
        super(symbol);
        this.field = mfield;
        rotationalangles = "";
    }

    /**
     * Generates an XML representation of the Renderer
     */
    public String toString() {
        return "<" + SimpleLabelRenderer.TAG + getParam() + ">\r\n" +
        symbol.toString() + "</" + SimpleLabelRenderer.TAG + ">\r\n";
    }

    private String getParam() {
        String param = "";

        if (SymbolUtils.isVoid(rotationalangles)) {
            param += (" rotationalangles=\"" + rotationalangles + "\"");
        }

        if (SymbolUtils.isVoid(field)) {
            param += (" field=\"" + field + "\"");
        }

        return param;
    }

    /**
     * @return Returns the textSymbol.
     */
    public IArcIMSSymbol getTextSymbol() {
        return symbol;
    }

    /**
     * @param textSymbol The textSymbol to set.
     */
    public void setTextSymbol(IArcIMSSymbol textSymbol) {
        this.symbol = textSymbol;
    }

    /**
     * @return Returns the labelField.
     */
    public String getField() {
        return field;
    }

    /**
     * @param labelField The labelField to set.
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return Returns the rotationalAngles.
     */
    public String getRotationalangles() {
        return rotationalangles;
    }

    /**
     * @param rotationalAngles The rotationalAngles to set.
     */
    public void setRotationalangles(String rotationalAngles) {
        this.rotationalangles = rotationalAngles;
    }
}
