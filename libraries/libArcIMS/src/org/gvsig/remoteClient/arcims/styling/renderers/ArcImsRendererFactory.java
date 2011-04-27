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

import org.apache.log4j.Logger;

import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;
import org.gvsig.remoteClient.arcims.styling.symbols.ArcImsSymbolFactory;
import org.gvsig.remoteClient.arcims.styling.symbols.IArcIMSSymbol;
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;

import org.kxml2.io.KXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import java.util.ArrayList;


/**
 * @author jsanz
 *
 */
public class ArcImsRendererFactory {
    private static Logger logger = Logger.getLogger(ArcImsRendererFactory.class.getName());
    private int featType;

    /**
     * Constructor to set the type of features the instance has to suppose
     * @param mfeatType
     */
    public ArcImsRendererFactory(int mfeatType) {
        this.featType = mfeatType;
    }

    /**
     * @return Returns the featType.
     */
    public int getFeatType() {
        return featType;
    }

    /**
     * @param featType The featType to set.
     */
    public void setFeatType(int featType) {
        this.featType = featType;
    }

    public Renderer getRenderer(KXmlParser parser) throws ArcImsException {
        // logger.debug("Start GetRenderer with " + parser.getName());

        Renderer render = null;

        //  		int depth = parser.getDepth();
        //  		logger.debug("XML depth = "+depth);

        //			logger.debug("START =" + parser.getName());
        if (parser.getName().equals(ServiceInfoTags.tGROUPRENDERER)) {
            render = (GroupRenderer) parseGroupRenderer(parser);
        } else if (parser.getName().equals(ServiceInfoTags.tSIMPLERENDERER)) {
            render = (SimpleRenderer) parseSimpleRenderer(parser);
        } else if (parser.getName()
                             .equals(ServiceInfoTags.tSCALEDEPENDENTRENDERER)) {
            render = (ScaleDependentRenderer) parseScaleDependentRenderer(parser);
        } else if (parser.getName().equals(ServiceInfoTags.tVALUEMAPRENDERER)) {
            render = (ValueMapRenderer) parseValueMapRenderer(parser);
        } else if (parser.getName().equals(ServiceInfoTags.tSIMPLELABELRENDERER)) {
            render = (SimpleLabelRenderer) parseSimpleLabelRenderer(parser);
        } else if (parser.getName()
                             .equals(ServiceInfoTags.tVALUEMAPLABELRENDERER)) {
            render = (ValueMapLabelRenderer) parseValueMapLabelRenderer(parser);
        }

        return render;
    }

    /**
     * This method returns a SimpleRenderer
     * @param parser
     * @return
     * @throws ArcImsException
     */
    private SimpleRenderer parseSimpleRenderer(KXmlParser parser)
        throws ArcImsException {
        // logger.debug("Start parsing SimpleRenderer");

        IArcIMSSymbol symbol = null;
        String startTag = parser.getName();
        int depth = parser.getDepth();

        try {
            int currentTag;
            boolean end = false;
            currentTag = parser.next();

            while (!end) {
                switch (currentTag) {
                case KXmlParser.START_TAG:

                    //If parser starts a Renderer, call getRenderer method
                    if (ArcImsSymbolFactory.isSymbol(parser.getName())) {
                        symbol = ArcImsSymbolFactory.parseSymbol(parser,
                                featType);
                    }

                    break;

                case KXmlParser.END_TAG:

                    if (parser.getName().equals(startTag) &&
                            (parser.getDepth() == depth)) {
                        end = true;
                    }

                    break;

                case KXmlParser.TEXT:
                    break;
                }

                if (!end) {
                    currentTag = parser.next();
                }
            }
        } catch (XmlPullParserException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_symbology_error");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_symbology_error");
        }

        if (symbol != null) {
            return new SimpleRenderer(symbol);
        } else {
            return null;
        }
    }

    /**
     * This parser will return a GroupRenderer
     * @param parser
     * @return
     * @throws ArcImsException
     */
    private GroupRenderer parseGroupRenderer(KXmlParser parser)
        throws ArcImsException {
        // logger.debug("Start parsing GroupRenderer");

        GroupRenderer render = new GroupRenderer();
        String startTag = parser.getName();

        try {
            int currentTag;
            boolean end = false;
            currentTag = parser.next();

            while (!end) {
                switch (currentTag) {
                case KXmlParser.START_TAG:

                    //If parser starts a Renderer, call getRenderer method
                    if (isRenderer(parser.getName())) {
                        render.addRender(getRenderer(parser));
                    }

                    break;

                case KXmlParser.END_TAG:

                    if (parser.getName().equals(startTag)) {
                        ; //&& parser.getDepth()==depth);
                    }

                    end = true;

                    break;

                case KXmlParser.TEXT:
                    break;
                }

                if (!end) {
                    currentTag = parser.next();
                }
            }
        } catch (XmlPullParserException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_symbology_error");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_symbology_error");
        }

        return render;
    }

    /**
     * A ScaleDependentRenderer is a type of GroupRenderer with scale limits
     * @param parser
     * @return
     * @throws ArcImsException
     */
    private ScaleDependentRenderer parseScaleDependentRenderer(
        KXmlParser parser) throws ArcImsException {
        // logger.debug("Start parsing ScaleDependentRenderer");

        ScaleDependentRenderer render = null;
        String startTag = parser.getName();

        try {
            //We are at SCALEDEPENDENTRENDERER tag, so we have to get it attributes
            String lower = parser.getAttributeValue("", ServiceInfoTags.aLOWER);
            String upper = parser.getAttributeValue("", ServiceInfoTags.UPPER);

            //Now we can create the renderer and start parsing inside this tag
            render = new ScaleDependentRenderer(lower, upper);

            //Start parsing
            int currentTag;
            boolean end = false;
            currentTag = parser.next();

            while (!end) {
                switch (currentTag) {
                case KXmlParser.START_TAG:

                    //If parser starts a Renderer, call getRenderer method
                    if (isRenderer(parser.getName())) {
                        Renderer innerRender = getRenderer(parser);
                        render.addRender(innerRender);
                    }

                    break;

                case KXmlParser.END_TAG:

                    if (parser.getName().equals(startTag)) {
                        ; //&& parser.getDepth()==depth);
                    }

                    end = true;

                    break;

                case KXmlParser.TEXT:
                    break;
                }

                if (!end) {
                    currentTag = parser.next();
                }
            }
        } catch (XmlPullParserException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_symbology_error");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_symbology_error");
        }

        return render;
    }

    private ValueMapRenderer parseValueMapRenderer(KXmlParser parser)
        throws ArcImsException {
        // logger.debug("Start parsing ValueMapRenderer");

        ValueMapRenderer render = null;
        String startTag = parser.getName();
        int depth = parser.getDepth();

        try {
            //We are at VALUEMAPRENDER tag, so we have to get it attributes
            String lookupfield = parser.getAttributeValue("",
                    ServiceInfoTags.aLOOKUPFIELD);

            //Now we can create the renderer and start parsing inside this tag
            // logger.debug("Creating ValueMapRenderer (" + lookupfield + ")");
            render = new ValueMapRenderer(lookupfield);

            ArrayList values = render.getValues();
            TypeValueMap tvm = null;
            IArcIMSSymbol symb = null;

            //Start parsing inner tags (EXACT, RANGE or OTHER)
            int currentTag;
            boolean end = false;
            currentTag = parser.next();

            while (!end) {
                switch (currentTag) {
                case KXmlParser.START_TAG:

                    //
                    if (parser.getName().equals(ServiceInfoTags.tEXACT)) {
                        // Get the parameters
                        String label = parser.getAttributeValue("",
                                ServiceInfoTags.aLABEL);
                        String value = parser.getAttributeValue("",
                                ServiceInfoTags.VALUE);
                        String method = parser.getAttributeValue("",
                                ServiceInfoTags.aMETHOD);

                        // Create the ExactValueMap object
                        // logger.debug("Creating ExactValueMap");
                        tvm = new ExactValueMap();

                        if (label != null) {
                            tvm.setLabel(label);
                        }

                        if (value != null) {
                            ((ExactValueMap) tvm).setValue(value);
                        }

                        if (method != null) {
                            ((ExactValueMap) tvm).setMethod(method);
                        }
                    }

                    if (parser.getName().equals(ServiceInfoTags.tRANGE)) {
                        // Get the parameters
                        String lower = parser.getAttributeValue("",
                                ServiceInfoTags.aLOWER);
                        String upper = parser.getAttributeValue("",
                                ServiceInfoTags.UPPER);
                        String equality = parser.getAttributeValue("",
                                ServiceInfoTags.aEQUALITY);
                        String label = parser.getAttributeValue("",
                                ServiceInfoTags.aLABEL);

                        // Create the RangeValueMap object
                        if ((lower != null) && (upper != null)) {
                            logger.debug("Creating RangeValueMap");
                            tvm = new RangeValueMap(lower, upper);

                            if (equality != null) {
                                ((RangeValueMap) tvm).setEquality(equality);
                            }

                            if (label != null) {
                                tvm.setLabel(label);
                            }
                        }
                    }

                    if (parser.getName().equals(ServiceInfoTags.tOTHER)) {
                        // Get the parameters
                        String label = parser.getAttributeValue("",
                                ServiceInfoTags.aLABEL);

                        // Create the Other object
                        logger.debug("Creating OtherValueMap");
                        tvm = new OtherValueMap();

                        if (label != null) {
                            tvm.setLabel(label);
                        }
                    }

                    break;

                case KXmlParser.END_TAG:

                    if (parser.getName().equals(startTag) &&
                            (parser.getDepth() == depth)) {
                        end = true;
                    }

                    break;

                case KXmlParser.TEXT:
                    break;
                }

                /*
                 *  Once the exits the switch statement we can add
                 *  the TypeValueMap into the arraylist of values
                 */
                if (tvm != null) {
                    /*
                     * Create and assign the symbol definition passing the parser
                     */
                    parser.nextTag();

                    if (ArcImsSymbolFactory.isSymbol(parser.getName())) {
                        symb = ArcImsSymbolFactory.parseSymbol(parser, featType);
                    }

                    if (symb != null) {
                        tvm.setSymbol(symb);
                    }

                    // Pass the TypeValueMap to the arraylist of values
                    values.add(tvm);
                }

                // Erase the references
                symb = null;
                tvm = null;

                /*
                 * If end continue as false, get the next tag of the XML
                 */
                if (!end) {
                    currentTag = parser.next();
                }
            }
        } catch (XmlPullParserException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_symbology_error");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_symbology_error");
        }

        return render;
    }

    private SimpleLabelRenderer parseSimpleLabelRenderer(KXmlParser parser)
        throws ArcImsException {
        // logger.debug("Start parsing SimpleLabelRenderer");

        SimpleRenderer render = null;
        SimpleLabelRenderer render2 = null;

        String field = parser.getAttributeValue("", ServiceInfoTags.aSFIELD);
        String rotational = parser.getAttributeValue("",
                ServiceInfoTags.aROTATIONALANGLES);

        render = parseSimpleRenderer(parser);

        if ((render != null) && (field != null)) {
            render2 = new SimpleLabelRenderer(field, render.getSymbol());

            if (rotational != null) {
                render2.setRotationalangles(rotational);
            }
        }

        return render2;
    }

    private ValueMapLabelRenderer parseValueMapLabelRenderer(KXmlParser parser)
        throws ArcImsException {
        logger.debug("Start parsing ValueMapLabelRenderer");

        ValueMapRenderer render = null;
        ValueMapLabelRenderer render2 = null;

        String labelfield = parser.getAttributeValue("",
                ServiceInfoTags.aLABELFIELD);
        String rotational = parser.getAttributeValue("",
                ServiceInfoTags.aROTATIONALANGLES);

        /*
         * As this render is a type of a ValueMapRenderer, we can use this method and afterwards
         * get the parameters and values
         */
        render = parseValueMapRenderer(parser);

        if ((render != null) && (labelfield != null)) {
            render2 = new ValueMapLabelRenderer(labelfield,
                    render.getLookupfield());

            if (rotational != null) {
                render2.setRotationalangles(rotational);
            }

            if (render.getValues() != null) {
                render2.setValues(render.getValues());
            }
        }

        return render2;
    }

    /**
     * Tests if a TAG is one of the ArcIMS renderers
     * @param name
     * @return
     */
    public static boolean isRenderer(String name) {
        int l = ServiceInfoTags.RENDERERS.length;

        for (int i = 0; i < l; i++) {
            if (name.equals(ServiceInfoTags.RENDERERS[i])) {
                return true;
            }
        }

        return false;
    }
}
