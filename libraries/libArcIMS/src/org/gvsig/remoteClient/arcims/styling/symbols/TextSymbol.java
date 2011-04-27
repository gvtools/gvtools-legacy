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


public class TextSymbol extends AbstractSymbol implements IArcIMSSymbol {
    public static final String TAG = ServiceInfoTags.tTEXTSYMBOL;
    protected String font;
    protected String fontColor;
    protected String fontSize;
    protected String fontStyle;

    public TextSymbol() {
        font = "Arial";
        fontColor = "0,0,0";
        fontSize = "12";
        fontStyle = SymbolUtils.TEXT_TYPE_REGULAR;
    }

    public String toString() {
        return "<" + TAG + getParam() + "/>\r\n";
    }

    /**
     * @return Returns the font.
     */
    public String getFont() {
        return font;
    }

    /**
     * @param font The font to set.
     */
    public void setFont(String font) {
        this.font = font;
    }

    /**
     * @return Returns the fontColor.
     */
    public String getFontColor() {
        return fontColor;
    }

    /**
     * @param fontColor The fontColor to set.
     */
    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * @return Returns the fontSize.
     */
    public String getFontSize() {
        return fontSize;
    }

    /**
     * @param fontSize The fontSize to set.
     */
    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * @return Returns the fontStyle.
     */
    public String getFontStyle() {
        return fontStyle;
    }

    /**
     * @param fontStyle The fontStyle to set.
     */
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    protected String getParam() {
        String param = "";

        if (SymbolUtils.isVoid(font)) {
            param += (" font=\"" + font + "\"");
        }

        if (SymbolUtils.isVoid(fontColor)) {
            param += (" fontcolor=\"" + fontColor + "\"");
        }

        if (SymbolUtils.isVoid(fontSize)) {
            param += (" fontsize=\"" + fontSize + "\"");
        }

        if (SymbolUtils.isVoid(fontStyle)) {
            param += (" fontstyle=\"" + fontStyle + "\"");
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
