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

package org.gvsig.remoteClient.arcims.styling.symbols;



import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;

import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;


/**
 * @author jsanz
 *
 */
public class ArcImsSimpleMarkerSymbol extends AbstractSymbol implements IArcIMSSymbol {
    public static final String TAG = ServiceInfoTags.tSIMPLEMARKERSYMBOL;
    private String color;
    private String width;
    private String type;

    /**
     * @param color
     * @param width
     * @param type
     */
    public ArcImsSimpleMarkerSymbol() {
        color = "0,0,0";
        width = "3";
        type = SymbolUtils.POINT_TYPE_CIRCLE;
    }

    public String toString() {
        return "<" + TAG + getParam() + "/>\r\n";
    }

    /**
     * @return Returns the color.
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color The color to set.
     */
    public void setColor(String color) {
        this.color = color;
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
     * @return Returns the width.
     */
    public String getWidth() {
        return width;
    }

    /**
     * @param width The width to set.
     */
    public void setWidth(String width) {
        this.width = width;
    }

    protected String getParam() {
        String param = "";

        if (SymbolUtils.isVoid(color)) {
            param += (" color=\"" + color + "\"");
        }

        if (SymbolUtils.isVoid(width)) {
            param += (" width=\"" + width + "\"");
        }

        if (SymbolUtils.isVoid(type)) {
            param += (" type=\"" + type + "\"");
        }

        return param;
    }

    /* (non-Javadoc)
     * @see org.gvsig.remoteClient.arcims.styling.symbols.IArcIMSSymbol#getFSymbol()
     */
    public ISymbol getFSymbol() {
        return ArcImsFSymbolFactory.getFSymbol(this);
    }
}
