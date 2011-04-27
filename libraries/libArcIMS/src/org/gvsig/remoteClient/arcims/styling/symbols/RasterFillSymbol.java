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
package org.gvsig.remoteClient.arcims.styling.symbols;



import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;

import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;


/**
 * This class is not supported at this time by gvSIG.
 * @author jsanz
 *
 */
public class RasterFillSymbol extends AbstractSymbol implements IArcIMSSymbol {
    public static final String TAG = ServiceInfoTags.tRASTERFILLSYMBOL;
    private String url;
    private String transparency;

    public RasterFillSymbol(String murl) {
        this.url = murl;
        transparency = "1.0";
    }

    public String toString() {
        return "<" + TAG + getParam() + " />\r\n";
    }

    protected String getParam() {
        String param = "";

        if (SymbolUtils.isVoid(url)) {
            param += (" url=\"" + url + "\"");
        }

        if (SymbolUtils.isVoid(transparency)) {
            param += (" transparency=\"" + transparency + "\"");
        }

        return param;
    }

    /* (non-Javadoc)
     * @see org.gvsig.remoteClient.arcims.styling.symbols.IArcIMSSymbol#getFSymbol()
     */
    public ISymbol getFSymbol() {
        return ArcImsFSymbolFactory.getFSymbol(this);
    }

    public String getTransparency() {
        return transparency;
    }

    public void setTransparency(String transparency) {
        this.transparency = transparency;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
