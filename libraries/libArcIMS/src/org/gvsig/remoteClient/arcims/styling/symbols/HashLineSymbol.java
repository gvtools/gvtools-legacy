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
package org.gvsig.remoteClient.arcims.styling.symbols;



import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;

import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;


/**
 * This class is not supported at this time by gvSIG, so it will
 * be transformed in a SiumpleLine.
 * @author jsanz
 *
 */
public class HashLineSymbol extends AbstractSymbol implements IArcIMSSymbol {
    public static final String TAG = ServiceInfoTags.tHASHLINESYMBOL;
    private String color;
    private String linethickness;

    public HashLineSymbol() {
        color = "0,0,0";
        linethickness = "1";
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
     * @return Returns the linethickness.
     */
    public String getLinethickness() {
        return linethickness;
    }

    /**
     * @param linethickness The linethickness to set.
     */
    public void setLinethickness(String linethickness) {
        this.linethickness = linethickness;
    }

    protected String getParam() {
        String param = "";

        if (SymbolUtils.isVoid(color)) {
            param += (" color = \"" + color + "\"");
        }

        if (SymbolUtils.isVoid(linethickness)) {
            param += (" linethickness = \"" + linethickness + "\"");
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
