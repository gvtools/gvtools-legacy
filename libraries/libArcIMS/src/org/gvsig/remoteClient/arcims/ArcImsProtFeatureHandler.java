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

package org.gvsig.remoteClient.arcims;

import com.hardcode.gdbms.engine.values.Value;

import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FMultiPoint2D;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;

import org.apache.log4j.Logger;

import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;
import org.gvsig.remoteClient.arcims.utils.ArcImsDownloadUtils;
import org.gvsig.remoteClient.arcims.utils.ArcImsValueFactory;
import org.gvsig.remoteClient.arcims.utils.FieldInformation;
import org.gvsig.remoteClient.arcims.utils.GetFeaturesTags;
import org.gvsig.remoteClient.arcims.utils.GetImageTags;
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;
import org.gvsig.remoteClient.arcims.utils.ServiceInformation;
import org.gvsig.remoteClient.arcims.utils.ServiceInformationLayerFeatures;
import org.gvsig.remoteClient.utils.BoundaryBox;

import org.kxml2.io.KXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import java.net.MalformedURLException;
import java.net.URL;

import java.sql.Types;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * @author jsanz
 *
 */
public class ArcImsProtFeatureHandler extends ArcImsProtocolHandler {
    private static boolean hasMore;
    private static int featCount;
    private static Logger logger = Logger.getLogger(ArcImsProtFeatureHandler.class.getName());

    /*
    private ArcImsCache aic;
    
    public ArrayList getCacheMap(ArcImsVectStatus status) throws ArcImsException{
            if (aic==null)
                    aic = new ArcImsCache(this.getLayerExtent(status),1);
    
            aic.addStatus(status);
    
            return aic.getGeometries(status);
    }
    */

    /**
     * Method to retrieve an ArrayList of features from an ArcIMS FeatureService
     *
     * @param status
     * @return ArrayList of IFeatures with geometries and Id's to attribute
     *         table
     * @throws ArcImsException
     */
    public ArrayList getMap(ArcImsVectStatus status) throws ArcImsException {
        logger.info("Start getMap");

        ArrayList array = new ArrayList();

        try {
            /*
             * Build the request
             */
            URL url = new URL(buildCapabilitiesRequest(status));

            hasMore = true;
            featCount = 1;

            while (hasMore) {
                /*
                 * Build the proper ArcXML
                 */
                String request = ArcXMLFeatures.getFeatureLayerRequest(status,
                        featCount);

                if (status.verbose) {
                    logger.debug(request);
                }

                /*
                 * Request the reader
                 */
                logger.info("Start features downloading");

                File f = ArcImsDownloadUtils.doRequestPost(url, request,
                        "getFeatures.xml");
                logger.info("End features downloading");

                /*
                 * Parse response and add geometries to the general Array
                 */
                String layerId = (String) status.getLayerIds().get(0);

                //Deal with UTF-8
                Reader reader = null;
                FileInputStream fis = new FileInputStream(f);
                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                reader = br;

                logger.debug("Start features with attributes parsing");
                array.addAll(getFeatures(reader, status.getServiceInfo(),
                        layerId, status.getSubfields(), 1));
                logger.debug("End features with attributes parsing");
            }

            hasMore = true;
            featCount = 1;
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_server_error");
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_server_error");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }

        logger.debug("End getMap");

        return array;
    }

    /**
     * Parse the XML data retrieved from ArcIMS GET_FEATURES request
     * to get a set of Features if withGeometries is true or an ArrayList of
     * Value[] if this boolean is false
     * @param Reader Stream to parse
     * @param si ServiceInformation with metadata
     * @param layerId Layer retrieved
     * @param subfields Subfields retrieved
     * @param withGeometries Will geometries be parsed?: 0=no 1=yes 2=evelopes
     * @return ArrayList of IFeatures or Value[]
     * @throws ArcImsException
     */
    private ArrayList getFeatures(Reader lector, ServiceInformation si,
        String layerId, String[] subfields, int withGeometries)
        throws ArcImsException {
        FieldInformation fi = null;
        ServiceInformationLayerFeatures sil = (ServiceInformationLayerFeatures) si.getLayerById(layerId);

        /*
         * Prepare a Value array to store every FEATURE tag information
         */
        Vector fieldsInfo = null;
        fieldsInfo = sil.getFieldsInfo();

        //If flag == true, throw an Exception (no layerId found)
        if (fieldsInfo == null) {
            throw new ArcImsException("arcims_no_features");
        }

        //We will store names and types in a hashmap
        ArrayList fieldsInfoA = new ArrayList();

        /*
         * If subfields[0] is equal to #ALL we add the entire vector
         * into the Hasmap
         */
        if (subfields[0].equals("#ALL#")) {
            for (int i = 0; i < fieldsInfo.size(); i++) {
                fi = ((FieldInformation) fieldsInfo.get(i));
                fieldsInfoA.add(fi);
            }
        } else {
            for (int i = 0; i < subfields.length; i++) {
                fi = null;
                fi = sil.getFieldInformation(subfields[i]);

                if (fi != null) {
                    fieldsInfoA.add(fi);
                }
            }
        }

        /*
             * Get the separators
             */
        String delCoords = si.getSeparators().getCs();
        String delTuple = si.getSeparators().getTs();
        char delDec = si.getSeparators().getDs();

        /*
         * ArrayList with features
         */
        ArrayList features = new ArrayList();

        /*
         * Start parsing
         */
        int tag;
        KXmlParser kxmlParser = null;
        kxmlParser = new KXmlParser();

        //FileReader fr = null;
        IGeometry geom = null;
        Value[] values = new Value[fieldsInfoA.size()];
        // int[] pos_ition = new int[fieldsInfoA.size()];

        //Initialize position array
        // for (int i = 0; i < position.length; i++) position[i] = -1;

        //		long timeFeat = 0;
        //		long timeGeom = 0;
        //		long timeTemp = 0;
        try {
            kxmlParser.setInput(lector);
            kxmlParser.nextTag();

            if (kxmlParser.getEventType() != KXmlParser.END_DOCUMENT) {
                kxmlParser.require(KXmlParser.START_TAG, null,
                    ServiceInfoTags.tARCXML);
                tag = kxmlParser.nextTag();

                while (tag != KXmlParser.END_DOCUMENT) {
                    switch (tag) {
                    case KXmlParser.START_TAG:

                        if (kxmlParser.getName()
                                          .compareTo(GetFeaturesTags.FEATURE) == 0) {
                            /*
                             * parse FEATURES' tag
                             */

                            //FIRST ENVELOPE TAG (We hope that ENVELOPE will ALLWAYS be returned at first...)
                            if (withGeometries == 2) {
                                //timeTemp = System.currentTimeMillis();
                                geom = parseEnvelopeFromFeatureTag(kxmlParser,
                                        delDec);
                            }

                            //SECOND FIELDS TAG
                            //timeTemp = System.currentTimeMillis();
                            values = parseValuesFromFeatureTag(kxmlParser, //the kxml parser
                                    fieldsInfoA, //Hashmap with names and FieldInformation objects)
                                    delDec // , //the separators
                                    // position //the relative positions of returned values
                                );

                            //timeFeat += System.currentTimeMillis() - timeTemp;

                            //If geometries are neede we add to the ArrayList the Ifeature
                            if (withGeometries == 1) {
                                //THIRD GEOMETRIES
                                //								timeTemp = System.currentTimeMillis();
                                geom = parseGeomFromFeatureTag(kxmlParser, //the kxml parser
                                        delTuple, delCoords,
                                        delDec //the separators
                                    );

                                //timeGeom += System.currentTimeMillis() - timeTemp;
                            }

                            //If some geometry is created, we add a IFeature into features ArrayList
                            if (withGeometries > 0) {
                                features.add(new DefaultFeature(geom, values));
                            }
                            //Else we only need the Value[] array
                            else {
                                features.add(values);
                            }
                        } else if (kxmlParser.getName()
                                                 .compareTo(GetFeaturesTags.FEATURECOUNT) == 0) {
                            String value = new String();

                            //Get the feature count
                            value = kxmlParser.getAttributeValue("",
                                    GetFeaturesTags.COUNT);

                            Integer intFeatCount = new Integer(value);
                            featCount += intFeatCount.intValue();

                            //Get the hasmore boolean
                            value = kxmlParser.getAttributeValue("",
                                    GetFeaturesTags.HASMORE);

                            Boolean boHasMore = new Boolean(value);
                            hasMore = boHasMore.booleanValue();
                        } else if (kxmlParser.getName()
                                                 .compareTo(ServiceInfoTags.tERROR) == 0) {
                            logger.error("Error parsing GET_FEATURES:\r\n" +
                                kxmlParser.nextText());
                            throw new ArcImsException("arcims_server_error");
                        }

                        break;

                    case KXmlParser.END_TAG:
                        break;

                    case KXmlParser.TEXT:
                        break;
                    }

                    tag = kxmlParser.next();
                }

                kxmlParser.require(KXmlParser.END_DOCUMENT, null, null);
            }
        } catch (ParseException pe) {
            logger.error(pe.getMessage(), pe);
            throw new ArcImsException("arcims_no_features");
        } catch (XmlPullParserException parser_ex) {
            logger.error(parser_ex.getMessage(), parser_ex);
            throw new ArcImsException("arcims_no_features");
        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            throw new ArcImsException("arcims_no_features");
        }

        //logger.debug("Time for parsing features " + timeFeat + " msecs");
        //logger.debug("Time for parsing geometries " + timeGeom + " msecs");
        return features;
    }

    /**
     * Returns an IGeometry with a line between corners of an ENVELOPE
     * @param parser
     * @param delDec
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private IGeometry parseEnvelopeFromFeatureTag(KXmlParser parser, char delDec)
        throws XmlPullParserException, IOException {
        int currentTag;
        boolean end = false;
        int f = 0;
        String att;
        String val;
        int type;

        BoundaryBox bb = null;

        //		parser.require(KXmlParser.START_TAG,null,GetFeaturesTags.FIELDS);
        currentTag = parser.next();

        while (!end) {
            switch (currentTag) {
            case KXmlParser.START_TAG:

                if (parser.getName().equals(ServiceInfoTags.tENVELOPE)) {
                    bb = super.parseEnvelope(parser, delDec);
                    end = true;
                }

                break;

            case KXmlParser.END_TAG:

                if (parser.getName().equals(GetFeaturesTags.FIELDS)) {
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

        //If BoundaryBox object has been created, we can convert it into a IGeometry (polyline).
        if (bb != null) {
            GeneralPathX polyline = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD);

            polyline.moveTo(bb.getXmin(), bb.getYmin());
            polyline.lineTo(bb.getXmax(), bb.getYmax());

            return ShapeFactory.createPolyline2D(polyline);
        } else {
            return null;
        }
    }

    /**
     * @param kxmlParser
     * @param fieldsInfoH
     * @param delDec
     * @param position
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     * @throws ArcImsException
     * @throws ParseException
     */
    private Value[] parseValuesFromFeatureTag(KXmlParser parser,
        ArrayList fieldsInfoA, char delDec) //, int[] position, boolean b)
        throws XmlPullParserException, IOException, ArcImsException, 
            ParseException {
        int currentTag;
        boolean end = false;
        // int f = 0;
        String att;
        String val;
        int type;

        Value[] values = new Value[fieldsInfoA.size()];
        ArrayList position = null;

        //		parser.require(KXmlParser.START_TAG,null,GetFeaturesTags.FIELDS);
        currentTag = parser.next();

        while (!end) {
            switch (currentTag) {
            case KXmlParser.START_TAG:

                if (parser.getName().equals(GetFeaturesTags.FIELD)) {
                    att = parser.getAttributeValue("", GetFeaturesTags.NAME);
                    val = parser.getAttributeValue("", GetFeaturesTags.VALUE);

                    /*
                     * As NAME is known, we need to get the position of that
                     * attribute into the ArrayList of fieldInformation objects
                     */
                    
                    position = getPosition(fieldsInfoA.iterator(), att);
                    
                    if (position == null) {
                        logger.error("Attribute not found at Metadata");
                        throw new ArcImsException(
                            "Attribute not found at Metadata");
                    }

                    // This way we can create a NullValue
                    if (val.equals("")) {
                        val = null;
                    }

                    /*
                     * At this point we know wat FieldInfo of the ArrayList we
                     * have to retrieve
                     */
                    int aux = ((Integer) position.get(0)).intValue(); 
                    type = ((FieldInformation) fieldsInfoA.get(aux)).getType();
                    
                    // avoid null String issue
                    if ((type == Types.VARCHAR) && (val == null)) {
                    	val = "";
                    }

                    // Add the Value into the correct position
                    for (int v=0; v<position.size(); v++) {
                    	aux = ((Integer) position.get(v)).intValue();
                        values[aux] =
                        	ArcImsValueFactory.createValueByType(val, type, delDec);
                    }

                    // logger.info(att + " ("+type+")\t" +
                    // values[f].toString());
                    // f++;
                }

                break;

            case KXmlParser.END_TAG:

                if (parser.getName().equals(GetFeaturesTags.FIELDS)) {
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

        return values;
    }

    /**
     * Method to get the position of an attribute into the subfields array
     *
     * @param fieldsInfoA
     * @param att
     * @return
     */
    private ArrayList getPosition(Iterator it, String att) {
    	
    	ArrayList resp = new ArrayList();
        int res = 0;
        FieldInformation fi;

        while (it.hasNext()) {
            fi = (FieldInformation) it.next();

            if (fi.getName().equals(att)) {
            	resp.add(new Integer(res)); //return res;
            }
            res++;
        }

        //If no integer has returned at this point we return a -1
        if (resp.size() == 0) {
        	return null;
        } else {
        	return resp;
        }
        
    }

    /**
     * Parses the Feature Tag to get a IFeature
     * @param KxmlParser with the FEATURE tag
     * @param Value array with the correct subtypes to store FIELDS data
     * @param String array with the correct fields names to retrieve
     * @param String with the tuple separator
     * @param String with the coordinates separator
     * @param Char with the decimal separator
     * @return @see com.hardcode.gdbms.engine.values.Value.IFeature
     * @throws IOException
     * @throws XmlPullParserException
     * @throws ArcImsException
     */
    private IGeometry parseGeomFromFeatureTag(KXmlParser parser,
        String delTuple, String delCoords, char delDec)
        throws IOException, XmlPullParserException, ArcImsException {
        int currentTag;
        boolean end = false;

        IGeometry geom = null;

        currentTag = parser.next();

        while (!end) {
            String featType = parser.getName();

            switch (currentTag) {
            case KXmlParser.START_TAG:

                if (featType.equals(GetFeaturesTags.MULTIPOINT)) {
                    // Go to COORDS tag
                    parser.nextTag();

                    if (parser.getName().equals(GetFeaturesTags.COORDS)) {
                        // Create a tokenizer with the tuples
                        String strPoints = parser.nextText();

                        // Get the points from COORDS string
                        ArrayList points = parseCoords(strPoints, delTuple,
                                delCoords, delDec);

                        //Convert these collections to array of doubles (primitive type)
                        double[] xD = new double[points.size()];
                        double[] yD = new double[points.size()];

                        int size = xD.length;

                        for (int i = 0; i < size; i++) {
                            xD[0] = ((Point2D) points.get(i)).getX();
                            yD[0] = ((Point2D) points.get(i)).getY();
                        }

                        //Get the geometry

                        //BUG in 1.0 FMultiPoint2D(double[] x, double[] y)
                        //						geom = ShapeFactory.createMultipoint2D(xD,yD);

                        //We will use another way to construct the geom object
                        FPoint2D[] fpoints = new FPoint2D[size];

                        for (int i = 0; i < size; i++) {
                            fpoints[i] = new FPoint2D(xD[i], yD[i]);
                        }

                        geom = new FMultiPoint2D(fpoints);
                    } else {
                        logger.error("Error parsing MULTIPOINT tag");
                        throw new ArcImsException("arcims_features_error");
                    }
                } else if (featType.equals(GetFeaturesTags.POLYLINE) ||
                        featType.equals(GetFeaturesTags.POLYGON)) {
                    //Parse while parser doesn't found </FEATURES> tag
                    //The GeneralPath to store the different paths
                    GeneralPathX polyline = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD);
                    boolean endpol = false;

                    while (!endpol) {
                        switch (currentTag) {
                        case KXmlParser.START_TAG:

                            if (parser.getName().equals(GetFeaturesTags.COORDS)) {
                                // Create a tokenizer with the tuples
                                String strPoints = parser.nextText();

                                // Get the points (Point2D) from COORDS string
                                ArrayList points = parseCoords(strPoints,
                                        delTuple, delCoords, delDec);

                                Iterator it = points.iterator();
                                Point2D point;

                                //First we MOVE to the first point of the path
                                point = (Point2D) it.next();
                                polyline.moveTo(point.getX(), point.getY());

                                //And now we can LINE to the rest of the points of the path
                                while (it.hasNext()) {
                                    point = (Point2D) it.next();
                                    polyline.lineTo(point.getX(), point.getY());
                                }
                            }

                            break;

                        case KXmlParser.END_TAG:

                            if (parser.getName().equals(featType)) {
                                endpol = true;
                            }

                            break;

                        case KXmlParser.TEXT:
                            break;
                        }

                        if (!endpol) {
                            currentTag = parser.next();
                        }
                    }

                    if (featType.equals(GetFeaturesTags.POLYLINE)) {
                        geom = ShapeFactory.createPolyline2D(polyline);
                    } else if (featType.equals(GetFeaturesTags.POLYGON)) {
                        geom = ShapeFactory.createPolygon2D(polyline);
                    }
                }

                break;

            case KXmlParser.END_TAG:

                if (parser.getName().compareTo(GetFeaturesTags.FEATURE) == 0) {
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

        return geom;
    }

    /**
     * Private method that parses a COORDS tag to return an ArrayList of Point2D.Double objects
     * @param strCoords
     * @param delTuple
     * @param delCoords
     * @param delDec
     * @return
     */
    private ArrayList parseCoords(String strCoords, String delTuple,
        String delCoords, char delDec) {
        String tupla = new String();
        ArrayList points = new ArrayList();
        String[] tuplaPart;
        Double x;
        Double y;

        /*
         * Replaces Decimal Separator to convert coords to suitable doubles.
         *
         * Maybe it will have a better performance if we insert this replace into
         * the while
         */
        if (delDec != '.') {
            strCoords = strCoords.replace(delDec, '.');
        }

        //Creates a tokenizer to run over the string
        StringTokenizer tuplas = new StringTokenizer(strCoords, delTuple, false);

        //Get ArrayLists collections with X's and Y's
        while (tuplas.hasMoreTokens()) {
            //			tupla = tuplas.nextToken().replace(delDec,'.');
            tupla = tuplas.nextToken();
            tuplaPart = tupla.split(delCoords);
            x = new Double(tuplaPart[0]);
            y = new Double(tuplaPart[1]);

            //Version with in-loop replacing
            /*
            if (delDec!='.'){
            x = new Double(tuplaPart[0].replace(delDec,'.'));
            y = new Double(tuplaPart[1].replace(delDec,'.'));
            } else{
            x = new Double(tuplaPart[0]);
            y = new Double(tuplaPart[1]);
            }
             */
            points.add(new Point2D.Double(x.doubleValue(), y.doubleValue()));
        }

        //Return an array op Point2D objects
        return points;
    }

    /**
     * @param status
     * @return
     * @throws ArcImsException
     */
    public Rectangle2D getLayerExtent(ArcImsVectStatus status)
        throws ArcImsException {
        Rectangle2D envelope = null;
        String layerId = (String) status.getLayerIds().get(0);

        //		logger.info("Getting Vectorial Layer Extent (" + status.getl)
        ServiceInformation si = status.getServiceInfo();
        String ini_srs = ServiceInfoTags.vINI_SRS;

        String srsView = status.getSrs().substring(ini_srs.length()).trim();
        String srsServ = si.getFeaturecoordsys();
        boolean srsAssumed = si.isSrsAssumed();

        if (srsAssumed || (srsView.equals(srsServ))) {
            BoundaryBox bb = si.getLayerById(layerId).getEnvelope();

            /*
             * At the end, we convert the BoundaryBox to a Rectangle2D
             */
            envelope = new Rectangle2D.Double();

            envelope.setFrameFromDiagonal(bb.getXmin(), bb.getYmin(),
                bb.getXmax(), bb.getYmax());

            return envelope;
        }

        /*
         * If SRS of View and Service are different, we do a custom request to retrieve the
         * global envelope of the layer
         */
        try {
            /*
             * Build the request
             */
            URL url;
            url = new URL(buildCapabilitiesRequest(status));

            /*
             * Build the proper ArcXML
             */
            String request = ArcXMLFeatures.getLayerExtentRequest(status);

            if (status.verbose) {
            	logger.debug(request);
            }

            /*
             * Request the envelope
             */
            File response;
            response = ArcImsDownloadUtils.doRequestPost(url, request,
                    "getLayerExtent.xml");

            /*
             * Parse response and return a Rectangle2D
             */
            char ds = si.getSeparators().getDs();
            envelope = parseEnvelopeTag(new FileReader(response), ds);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_server_error");
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_server_error");
        }

        return envelope;
    }

    /**
     * Retrieves an ArrayList of Value arrays. The query can be filtered by a WHERE clause or an
     * envelope. Only subfields passed will be retrieved
     * @param status
     * @param subfields
     * @param where
     * @param envelope
     * @return
     * @throws ArcImsException
     */
    public ArrayList getAttributes(ArcImsVectStatus status, String[] subfields,
        String where, Rectangle2D envelope) throws ArcImsException {
        logger.info("Start getAttributes");

        ArrayList valuesArray = new ArrayList();

        //Clone the status and set temporal subfields and where clause
        ArcImsVectStatus statusCloned = status;

        String[] subTemp = status.getSubfields();
        String whereTemp = status.getWhere();
        Rectangle2D rectTemp = status.getExtent();

        statusCloned.setSubfields(subfields);
        statusCloned.setWhere(where);
        statusCloned.setExtent(envelope);

        try {
            /*
             * Build the request
             */
            URL url;
            url = new URL(buildCapabilitiesRequest(statusCloned));

            hasMore = true;
            featCount = 0;

            while (hasMore) {
                /*
                 * Build the proper ArcXML
                 */
                String request = ArcXMLFeatures.getAttributesRequest(statusCloned,
                        featCount);

                if (status.verbose) {
                    logger.debug(request);
                }

                //The attributes come from ArcIMS in ASCII/cp1252 encoding
                boolean withUTF = false;

                //Don't download the file, pass the data directly from the connection
                boolean withFile = false;

                /*
                 * Get an InputStream from File or directly from the connection
                 */
                InputStream is = null;

                if (withFile) {
                    File f = ArcImsDownloadUtils.doRequestPost(url, request,
                            "getAttributes.xml");

                    try {
                        is = new FileInputStream(f);
                    } catch (FileNotFoundException e) {
                        logger.error(e.getMessage(), e);
                    }
                } else {
                    is = ArcImsDownloadUtils.getRemoteIS(url, request);
                }

                InputStreamReader isr = null;

                if (withUTF) {
                    //Deal with UTF-8
                    try {
                        isr = new InputStreamReader(is, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e.getMessage(), e);
                    }
                } else {
                    //No deal with UTF-8
                    isr = new InputStreamReader(is);
                }

                /*
                     * Finally, we can create a BufferedReader from the InputStreamReader
                     */
                BufferedReader br = new BufferedReader(isr);

                /*
                 * Parse response and return a Rectangle2D
                 */
                String layerId = (String) statusCloned.getLayerIds().get(0);
                logger.debug("Start attributes downloading and parsing (" +
                    featCount + ") ids retrieved");
                valuesArray.addAll(getFeatures(br,
                        statusCloned.getServiceInfo(), layerId,
                        statusCloned.getSubfields(), 0));
                logger.debug("End attributes downloading and parsing");
            }

            hasMore = true;
            featCount = 0;
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_server_error");
        } catch (ArcImsException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_server_error");
        }
        finally {
            status.setSubfields(subTemp);
            status.setWhere(whereTemp);
            status.setExtent(rectTemp);
        }

        logger.debug("End attributes retrieving");

        return valuesArray;
    }

    /**
     * Retrieves an ArrayList of I by some IDs. The geometry returned is the envelope
     * of the feature requested. The query can be filtered by a WHERE clause or an
     * envelope. Only subfields passed will be retrieved
     * @param status
     * @param subfields
     * @param where
     * @param envelope
     * @return
     * @throws ArcImsException
     */
    public ArrayList getAttributesWithEnvelope(ArcImsVectStatus status,
        String[] subfields, String where, Rectangle2D envelope)
        throws ArcImsException {
        //		//TEST
        //		try {
        //			boolean flag = true;
        //			IntValue iv;
        //			IdsReader ids = new IdsReader(status, "#ID#");
        //			while (flag) {
        //				iv = ids.readId();
        //				if (iv!=null)
        //					logger.info("ID = " + iv.intValue());
        //				else
        //					flag = false;
        //			}
        //		} catch (IOException e) {
        //			e.printStackTrace();
        //		} catch (ArcImsException e) {
        //			e.printStackTrace();
        //		}
        logger.info("Start getAttributesWihtEnvelope");

        ArrayList valuesArray = new ArrayList();

        //Clone the status and set temporal subfields and where clause
        ArcImsVectStatus statusCloned = status;

        String[] subTemp = status.getSubfields();
        String whereTemp = status.getWhere();
        Rectangle2D rectTemp = status.getExtent();

        statusCloned.setSubfields(subfields);
        statusCloned.setWhere(where);
        statusCloned.setExtent(envelope);

        try {
            /*
             * Build the request
             */
            URL url;
            url = new URL(buildCapabilitiesRequest(statusCloned));

            hasMore = true;
            featCount = 0;

            while (hasMore) {
                /*
                 * Build the proper ArcXML
                 */
                String request = ArcXMLFeatures.getAttributesRequest(statusCloned,
                        featCount);

                if (status.verbose) {
                	logger.debug(request);
                }

                //The attributes come from ArcIMS in ASCII/cp1252 encoding
                boolean withUTF = false;

                //Don't download the file, pass the data directly from the connection
                boolean withFile = false;

                /*
                 * Get an InputStream from File or directly from the connection
                 */
                InputStream is = null;

                if (withFile) {
                    File f = ArcImsDownloadUtils.doRequestPost(url, request,
                            "getAttributes.xml");

                    try {
                        is = new FileInputStream(f);
                    } catch (FileNotFoundException e) {
                        logger.error(e.getMessage(), e);
                    }
                } else {
                    is = ArcImsDownloadUtils.getRemoteIS(url, request);
                }

                InputStreamReader isr = null;

                if (withUTF) {
                    //Deal with UTF-8
                    try {
                        isr = new InputStreamReader(is, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e.getMessage(), e);
                    }
                } else {
                    //No deal with UTF-8
                    isr = new InputStreamReader(is);
                }

                /*
                     * Finally, we can create a BufferedReader from the InputStreamReader
                     */
                BufferedReader br = new BufferedReader(isr);

                /*
                 * Parse response and return a Rectangle2D
                 */
                String layerId = (String) statusCloned.getLayerIds().get(0);
                logger.debug("Start attributes downloading and parsing (" +
                    featCount + ") ids retrieved");
                valuesArray.addAll(getFeatures(br,
                        statusCloned.getServiceInfo(), layerId,
                        statusCloned.getSubfields(), 2)); //Here we request envelopes instead of geometries
                logger.debug("End attributes downloading and parsing");
            }

            hasMore = true;
            featCount = 0;
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_server_error");
        } catch (ArcImsException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_server_error");
        }
        finally {
            status.setSubfields(subTemp);
            status.setWhere(whereTemp);
            status.setExtent(rectTemp);
        }

        logger.debug("End attributes retrieving");

        return valuesArray;
    }

    /* (non-Javadoc)
     * @see org.gvsig.remoteClient.arcims.ArcImsProtocolHandler#getElementInfo(org.gvsig.remoteClient.arcims.ArcImsStatus, int, int, int)
     */
    public String getElementInfo(ArcImsStatus status, int x, int y,
        int featureCount) throws ArcImsException {
        throw new ClassCastException(
            "Invalid request, this method is only valid for Image Services");
    }
}
