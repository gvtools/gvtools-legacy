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

package org.gvsig.remoteClient.arcims.styling.symbols;

import org.apache.log4j.Logger;

import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;

import org.kxml2.io.KXmlParser;


public class ArcImsSymbolFactory {
    private static Logger logger = Logger.getLogger(ArcImsSymbolFactory.class.getName());

    /**
     * Tests if a TAG is one of the ArcIMS symbols
     * @param name
     * @return
     */
    public static boolean isSymbol(String name) {
        for (int i = 0; i < ServiceInfoTags.SYMBOLS.length; i++) {
            if (name.equals(ServiceInfoTags.SYMBOLS[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * Tests if a TAG is one of the ArcIMS symbols
     * @param name
     * @return
     */
    public static boolean isSupported(String name) {
        for (int i = 0; i < ServiceInfoTags.NOT_SUPP_SYMBOLS.length; i++) {
            if (name.equals(ServiceInfoTags.NOT_SUPP_SYMBOLS[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method will return valid IArcIMSSymbol from a ArcXML symbol definition
     * @see com.iver.cit.gvsig.fmap.core.v02.FConstant
     * @see org.gvsig.remoteClient.arcims.styling.symbols.IArcIMSSymbol
     * @param parser
     * @param featType Type of feature to get the symbol, useful when a default symbol has to be created
     * @return
     */
    public static IArcIMSSymbol parseSymbol(KXmlParser parser, int featType) {
        //TODO Parse symbols
        // logger.debug("Start parsing Symbol with " + parser.getName());

        IArcIMSSymbol symb = null;

        //  		int depth = parser.getDepth();
        //  		logger.debug("XML depth = "+depth);

        //  		logger.debug("START =" + parser.getName());

        //SIMPLELINESYMBOL;
        if (parser.getName().equals(ServiceInfoTags.tSIMPLELINESYMBOL)) {
            symb = new ArcImsSimpleLineSymbol();

            ArcImsSimpleLineSymbol esymb = (ArcImsSimpleLineSymbol) symb;

            String color = parser.getAttributeValue("", ServiceInfoTags.aCOLOR);

            if (color != null) {
                esymb.setColor(color);
            }

            String width = parser.getAttributeValue("", ServiceInfoTags.aWIDTH);

            if (width != null) {
                esymb.setWidth(width);
            }

            String type = parser.getAttributeValue("", ServiceInfoTags.aTYPE);

            if (type != null) {
                esymb.setType(type);
            }

            String captype = parser.getAttributeValue("",
                    ServiceInfoTags.aCAPTYPE);

            if (captype != null) {
                esymb.setCaptype(captype);
            }

            String jointype = parser.getAttributeValue("",
                    ServiceInfoTags.aJOINTYPE);

            if (jointype != null) {
                esymb.setJointype(jointype);
            }

            String transparency = parser.getAttributeValue("",
                    ServiceInfoTags.aTRANSPARENCY);

            if (transparency != null) {
                esymb.setTransparency(transparency);
            }
        }
        //SIMPLEMARKERSYMBOL;
        else if (parser.getName().equals(ServiceInfoTags.tSIMPLEMARKERSYMBOL)) {
            symb = new ArcImsSimpleMarkerSymbol();

            ArcImsSimpleMarkerSymbol esymb = (ArcImsSimpleMarkerSymbol) symb;

            String color = parser.getAttributeValue("", ServiceInfoTags.aCOLOR);

            if (color != null) {
                esymb.setColor(color);
            }

            String width = parser.getAttributeValue("", ServiceInfoTags.aWIDTH);

            if (width != null) {
                esymb.setWidth(width);
            }

            String type = parser.getAttributeValue("", ServiceInfoTags.aTYPE);

            if (type != null) {
                esymb.setType(type);
            }
        }
        //SIMPLEPOLYGONSYMBOL;
        else if (parser.getName().equals(ServiceInfoTags.tSIMPLEPOLYGONSYMBOL)) {
            symb = new ArcImsSimplePolygonSymbol();

            ArcImsSimplePolygonSymbol esymb = (ArcImsSimplePolygonSymbol) symb;

            String hasBoundary = parser.getAttributeValue("",
                    ServiceInfoTags.aBOUNDARY);

            if ((hasBoundary != null) && hasBoundary.equals("false")) {
                esymb.setHasBoundary(false);
            } else {
                ArcImsSimpleLineSymbol lsymb = esymb.getBoundary();
                String strBound = "boundary";
                String color = parser.getAttributeValue("",
                        strBound + ServiceInfoTags.aCOLOR);

                if (color != null) {
                    lsymb.setColor(color);
                }

                String width = parser.getAttributeValue("",
                        strBound + ServiceInfoTags.aWIDTH);

                if (width != null) {
                    lsymb.setWidth(width);
                }

                String type = parser.getAttributeValue("",
                        strBound + ServiceInfoTags.aTYPE);

                if (type != null) {
                    lsymb.setType(type);
                }

                String captype = parser.getAttributeValue("",
                        strBound + ServiceInfoTags.aCAPTYPE);

                if (captype != null) {
                    lsymb.setCaptype(captype);
                }

                String jointype = parser.getAttributeValue("",
                        strBound + ServiceInfoTags.aJOINTYPE);

                if (jointype != null) {
                    lsymb.setJointype(jointype);
                }

                String transparency = parser.getAttributeValue("",
                        strBound + ServiceInfoTags.aTRANSPARENCY);

                if (transparency != null) {
                    lsymb.setTransparency(transparency);
                }
            }

            String strFill = "fill";
            String color = parser.getAttributeValue("",
                    strFill + ServiceInfoTags.aCOLOR);

            if (color != null) {
                esymb.setFillcolor(color);
            }

            String trans = parser.getAttributeValue("",
                    strFill + ServiceInfoTags.aTRANSPARENCY);

            if (trans != null) {
                esymb.setFilltransparency(trans);
            }

            String type = parser.getAttributeValue("",
                    strFill + ServiceInfoTags.aTYPE);

            if (type != null) {
                esymb.setFilltype(type);
            }
        }
        //TEXTSYMBOL;
        else if (parser.getName().equals(ServiceInfoTags.tTEXTSYMBOL)) {
            symb = new TextSymbol();

            TextSymbol esymb = (TextSymbol) symb;

            String font = parser.getAttributeValue("", ServiceInfoTags.aFONT);

            if (font != null) {
                esymb.setFont(font);
            }

            String fontcolor = parser.getAttributeValue("",
                    ServiceInfoTags.aFONT + ServiceInfoTags.aCOLOR);

            if (fontcolor != null) {
                esymb.setFontColor(fontcolor);
            }

            String fontsize = parser.getAttributeValue("",
                    ServiceInfoTags.aFONT + ServiceInfoTags.aSIZE);

            if (fontsize != null) {
                esymb.setFontSize(fontsize);
            }

            String fontstyle = parser.getAttributeValue("",
                    ServiceInfoTags.aFONT + ServiceInfoTags.aSTYLE);

            if (fontstyle != null) {
                esymb.setFontStyle(fontstyle);
            }
        }
        //RASTERMARKERSYMBOL;
        else if (parser.getName().equals(ServiceInfoTags.tRASTERMARKERSYMBOL)) {
            String url = parser.getAttributeValue("", ServiceInfoTags.aURL);

            if (url != null) {
                symb = new RasterMarkerSymbol(url);

                RasterMarkerSymbol esymb = (RasterMarkerSymbol) symb;
                String size = parser.getAttributeValue("", ServiceInfoTags.aSIZE);

                if (size != null) {
                    esymb.setSize(size);
                }

                String trans = parser.getAttributeValue("",
                        ServiceInfoTags.aTRANSPARENCY);

                if (trans != null) {
                    esymb.setTransparency(trans);
                }
            }
        }
        // RASTERFILLSYMBOL;
        else if (parser.getName().equals(ServiceInfoTags.tRASTERFILLSYMBOL)) {
            String url = parser.getAttributeValue("", ServiceInfoTags.aURL);

            if (url != null) {
                symb = new RasterFillSymbol(url);

                RasterFillSymbol esymb = (RasterFillSymbol) symb;
                String trans = parser.getAttributeValue("",
                        ServiceInfoTags.aTRANSPARENCY);

                if (trans != null) {
                    esymb.setTransparency(trans);
                }
            }
        }
        //Not supported
        //GRADIENTFILLSYMBOL;
        else if (parser.getName().equals(ServiceInfoTags.tGRADIENTFILLSYMBOL)) {
            symb = new ArcImsGradientFillSymbol();

            ArcImsGradientFillSymbol esymb = (ArcImsGradientFillSymbol) symb;

            String finishcolor = parser.getAttributeValue("",
                    "finish" + ServiceInfoTags.aCOLOR);

            if (finishcolor != null) {
                esymb.setFinishcolor(finishcolor);
            }

            String startcolor = parser.getAttributeValue("",
                    "start" + ServiceInfoTags.aCOLOR);

            if (startcolor != null) {
                esymb.setStartcolor(startcolor);
            }

            String transparency = parser.getAttributeValue("",
                    ServiceInfoTags.aTRANSPARENCY);

            if (transparency != null) {
                esymb.setTransparency(transparency);
            }

            String type = parser.getAttributeValue("", ServiceInfoTags.aTYPE);

            if (type != null) {
                esymb.setType(type);
            }
        }
        //HASHLINESYMBOL;
        else if (parser.getName().equals(ServiceInfoTags.tHASHLINESYMBOL)) {
            symb = new HashLineSymbol();

            HashLineSymbol esymb = (HashLineSymbol) symb;

            String color = parser.getAttributeValue("", ServiceInfoTags.aCOLOR);

            if (color != null) {
                esymb.setColor(color);
            }

            String linethickness = parser.getAttributeValue("",
                    ServiceInfoTags.aLINETHICKNESS);

            if (linethickness != null) {
                esymb.setLinethickness(linethickness);
            }
        }
        // TRUETYPEMARKERSYMBOL;
        else if (parser.getName().equals(ServiceInfoTags.tTRUETYPEMARKERSYMBOL)) {
            symb = new TrueTypeMarkerSymbol();
        }
        // CALLOUTMARKERSYMBOL
        else if (parser.getName().equals(ServiceInfoTags.tCALLOUTMARKERSYMBOL)) {
            symb = new CallOutMarkerSymbol();
        }
        // CHARTSYMBOL;
        else if (parser.getName().equals(ServiceInfoTags.tCHARTSYMBOL)) {
            symb = new ChartSymbol();
        }
        // RASTERSHIELDSYMBOL;
        else if (parser.getName().equals(ServiceInfoTags.tRASTERSHIELDSYMBOL)) {
            String url = parser.getAttributeValue("", ServiceInfoTags.aURL);

            if (url != null) {
                symb = new RasterShieldSymbol(url);
            }
        }
        // SHIELDSYMBOL;
        else if (parser.getName().equals(ServiceInfoTags.tSHIELDSYMBOL)) {
            symb = new ShieldSymbol();
        }
        // TEXTMARKERSYMBOL;
        else if (parser.getName().equals(ServiceInfoTags.tTEXTMARKERSYMBOL)) {
            symb = new TextMarkerSymbol();
        }

        //  		//Return allways a simplePolygon to test
        //		symb = new SimplePolygonSymbol();
        return symb;
    }
}
