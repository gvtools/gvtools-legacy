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

import org.apache.log4j.Logger;

import org.cresques.cts.IProjection;

import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;
import org.gvsig.remoteClient.arcims.utils.ArcImsDownloadUtils;
import org.gvsig.remoteClient.arcims.utils.GetImageTags;
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;
import org.gvsig.remoteClient.arcims.utils.ServiceInformation;
import org.gvsig.remoteClient.arcims.utils.ServiceInformationLayer;
import org.gvsig.remoteClient.exceptions.ServerErrorException;
import org.gvsig.remoteClient.utils.BoundaryBox;

import org.kxml2.io.KXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.Vector;


/**
 * <p>
 * Describes the handler to communicate to a ArcIMS ImageServer
 * </p>
 */
public class ArcImsProtImageHandler extends ArcImsProtocolHandler {
    private static Logger logger = Logger.getLogger(ArcImsProtImageHandler.class.getName());

    /**
     * Method to obtain the correct extent of the Service in a specific SRS
     * @param srsFlyr The SRS of the View
     * @param status
     * @return Rectangle2D The boundary box of the Service
     * @throws ArcImsException
     */
    public Rectangle2D getServiceExtent(IProjection srsFlyr, ArcImsStatus status)
        throws ArcImsException {
        Rectangle2D rect = new Rectangle();

        try {
            URL url = new URL(this.buildCapabilitiesRequest(status));

            /**
             * The seriveInfo
             */
            ServiceInformation si = status.getServiceInfo();

            /**
            * Gets de Decimal Separator and SRS from the status.ServiceInfo
            */
            char ds = si.getSeparators().getDs();
            String srsSI = si.getFeaturecoordsys();

            /**
                 * The EPSG code that image requested will have,
                 * the ArcIMS server will reproject data into this
                 * code, see <a href="http://www.epsg.org">EPSG</a>
                 */
            String srsView = srsFlyr.getAbrev();

            /**
                 * We suppose that status.getSrs() allways will give a
                 * string started by this string
                 */
            String ini_srs = ServiceInfoTags.vINI_SRS;

            /**
                 * Assign the srs from the status
                 * @see org.gvsig.remoteClient.RemoteClientStatus#getSrs()
                 */
            if (srsView.startsWith(ini_srs)) {
                srsView = srsView.substring(ini_srs.length()).trim();
            }

            /**
                 * If both SRS are the same or status we pass empty strings
                 */
            if (srsView.equalsIgnoreCase(srsSI)) {
                srsView = "";
                srsSI = "";
            }

            /**
             * Build the custom request for this extent
             */
            String request = ArcXMLImage.getCustomExtentRequest(srsSI, 
                //ServiceInfo SRS
                null, //envelope 
                    srsView, //SRS of the view
                    ds, //Decimal separator
                    null, //Image size
                    "", si.getLayer(0).getId() //A valid Layer Id
                );

            if (status.verbose) {
            	logger.debug(request);
            }

            logger.info("Requesting Service Extent XML");

            File response = ArcImsDownloadUtils.doRequestPost(url, request,
                    "getServiceExtent.xml");
            rect = parseEnvelopeTag(new FileReader(response), ds);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_server_error");
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_server_error");
        }

        return rect;
    }

    /**
    * <p>It will send a GetFeatureInfo request to the ArcIms
    * Parsing the response and redirecting the info to the ArcIms client</p>
     * @throws ArcImsException
    */
    public String getElementInfo(ArcImsStatus status, int x, int y,
        int featureCount) throws ArcImsException {
        double iViewX;
        double iViewY;
        double iServiceX;
        double iServiceY;
        double xReal;
        double yReal;

        // double dist;
        int ratio;
        double currentScale;

        /**
         * ServiceInformation of the current Service
         */
        ServiceInformation si = status.getServiceInfo();
        char ds = si.getSeparators().getDs();

        /**
         * Determine the SRS to do all the requests, if the ServiceInformation
         * doesn't provide a valid SRS, then we use the status srs
         */
        String srs = status.getSrs().substring(ServiceInfoTags.vINI_SRS.length())
                           .trim();

        /**
         * The geographic extent of the current window
         */
        Rectangle2D geoServiceExtent = new Rectangle();
        Rectangle2D geoViewExtent = status.getExtent();

        /**
         * If the SRS's are different we need a reprojection, using a properly
         * formed ArcIms GETIMAGE request
         */
        if (!si.getFeaturecoordsys().equalsIgnoreCase(srs)) {
            try {
                logger.info("Requesting the Extent of the Image");

                URL url = new URL(this.buildCapabilitiesRequest(status));
                String request = ArcXMLImage.getCustomExtentRequest(srs,
                        status.getExtent(), si.getFeaturecoordsys(), ds, null,
                        "", si.getLayer(0).getId());

                if (status.verbose) {
                	logger.debug(request);
                }

                File response = ArcImsDownloadUtils.doRequestPost(url, request,
                        "getInfoImageExtent.xml");
                geoServiceExtent = parseEnvelopeTag(new FileReader(response), ds);
            } catch (MalformedURLException e) {
                logger.error(e.getMessage(), e);
                throw new ArcImsException("arcims_server_error");
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
                throw new ArcImsException("arcims_server_error");
            }
        } else {
            geoServiceExtent = status.getExtent();
        }

        /**
         * URL for the requests of info
         */
        URL requestF;
        URL requestI;
        URL request;

        /**
         * StringBuffer to store the POST info to pass to the server
         */
        StringBuffer sb = new StringBuffer();

        /**
         * Vector of Layers IDs to request
         */
        Vector idLayers = status.getLayerIds();

        /**
         * String with the ArcXML request (GET_FEATURES)
         */
        String sArcXML = new String();

        /**
         * First at all, we build the request for this service
         */
        try {
            requestF = new URL(buildGetFeatureInfoRequest(status,
                        ServiceInfoTags.vLAYERTYPE_F));
            requestI = new URL(buildGetFeatureInfoRequest(status,
                        ServiceInfoTags.vLAYERTYPE_I));
        } catch (Exception e) {
            e.printStackTrace();

            return "";
        }

        sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        sb.append("<FeatureInfoResponse>");

        /**
         * We need to obtain the real (geographic) x,y not the pixels of the
         * image, a BoundaryBox of the area we will query, the envelope and
         * scale of the current view
         *
         */
        ratio = 3;
        iViewX = geoViewExtent.getWidth() / status.getWidth();
        iViewY = geoViewExtent.getHeight() / status.getHeight();
        iServiceX = geoServiceExtent.getWidth() / status.getWidth();
        iServiceY = geoServiceExtent.getHeight() / status.getHeight();
        xReal = geoViewExtent.getMinX() + (x * iViewX);
        yReal = geoViewExtent.getMaxY() - (y * iViewY);

        if (serviceInfo.getScreen_dpi() == -1) {
            serviceInfo.setScreen_dpi(96);
        }

        // currentScale = serviceInfo.screen_dpi/INCHES*((iX+iY)/2);
        currentScale = 1.0 * ((iServiceX + iServiceY) / 2.0);

        /**
         * Now we are ready to run over the layers to request the info for every
         * one
         */
        Vector ids = status.getLayerIds();
        int n = ids.size();
        String fields;

        ServiceInformationLayer sil;
        ServiceInformationLayer silTemp;

        /*
         * If ServiceInfo is Assumed we don't pass a valid SRS to
         * avoid projection problems.
         */
        if (si.isSrsAssumed()) {
            srs = "";
        }

        logger.info("Searching candidate layers");

        for (int i = 0; i < n; i++) {
            /**
             * Before we do any request to the server, we will ensure that the
             * layer is viewed in the current envelope and scale
             */

            /**
             * Retrieve the proper ServiceInformationLayer
             */
            sil = silTemp = (ServiceInformationLayer) si.getLayers().get(0);

            for (int j = 0; j < si.getLayers().size(); j++) {
                silTemp = (ServiceInformationLayer) si.getLayers().get(j);

                if (silTemp.getId().equals(ids.get(i))) {
                    sil = silTemp;

                    break;
                }
            }

            /**
             * Querying about the scale
             */
            boolean inScale = false;

            if (sil.getMinscale() == -1) {
                if (sil.getMaxscale() == -1) {
                    inScale = true;
                } else {
                    inScale = (currentScale < sil.getMaxscale());
                }
            } else {
                if (sil.getMaxscale() == -1) {
                    inScale = (currentScale > sil.getMinscale());
                } else {
                    inScale = (currentScale > sil.getMinscale()) &&
                        (currentScale < sil.getMaxscale());
                }
            }

            if (inScale) {
                /**
                 * Querying about the envelope, we have to ask if geoExtent has
                 * the Envelope of the layer inside it.
                 */
                boolean inEnvelope = false;
                BoundaryBox mEnv = sil.getEnvelope();

                Area mVentana = new Area((java.awt.Shape) geoServiceExtent);
                Rectangle2D rectCapa = new Rectangle2D.Double(mEnv.getXmin(),
                        mEnv.getYmin(), mEnv.getXmax() - mEnv.getXmin(),
                        mEnv.getYmax() - mEnv.getYmin());

                inEnvelope = mVentana.intersects(rectCapa);

                /**
                 * If two conditions are true, we do the request
                 */

                if (inEnvelope) {
                    fields = "";

                    /**
                     * Get the Info request for a featureclass or raster layer
                     */
                    double[] coords = { xReal, yReal };
                    double[] dists = { ratio * iServiceX, ratio * iServiceY };
                    sArcXML = ArcXMLImage.getInfoRequest(sil.getType(),
                            idLayers.get(i).toString(), coords, dists, srs,
                            si.getSeparators().getDs());

                    if (status.verbose) {
                        logger.debug("sArcXML = " + sArcXML);
                    }

                    /**
                     * File with a layer info response
                     */
                    if (sil.getType().equals(ServiceInfoTags.vLAYERTYPE_F)) {
                        request = requestF;
                    } else {
                        request = requestI;
                    }

                    logger.info("Requesting layer \"" + sil.getName() +
                        "\" information");

                    File response = ArcImsDownloadUtils.doRequestPost(request,
                            sArcXML, "getElementInfo.xml");

                    /**
                     * Now we can parse the file to get the FIELDS element
                     */
                    fields = parseFieldsInfoResponse(response);

                    if (!fields.equals("")) {
                        sb.append(ArcXML.getLayerHeaderInfoResponse(
                                ids.get(i).toString(), status.getServiceInfo()));

                        /**
                         * FIELDS element
                         */
                        sb.append(fields.replaceAll("#", "_"));

                        // sb.append("\t\t"+"<FIELDS #IY_PRE=\"1000\"
                        // #SHAPE#=\"[Geometry]\" #ID#=\"11\" />\r\n");
                        sb.append(ArcXML.getLayerFooterInfoResponse());
                    }

                } // Fin del if de ventana
                else {
                    logger.info("Layer \"" + sil.getName() +
                        "\" is not shown at this envelope");
                }
            }
            // Fin del if de escala
            else {
                logger.info("Layer \"" + sil.getName() +
                    "\" is not shown at this scale");
            }
        }

        // Fin del for
        sb.append("</FeatureInfoResponse>");
        return sb.toString();
    }

    /**
            * Parse the xml data retrieved from the ArcIms, it will parse the ArcIms
            * Features
            * @see #getElementInfo(ArcImsStatus, int, int, int)
            * @param f
            *            The XML file to parse to obtain the FIELDS element
            * @return XML with FIELDS or BAND tags as a response to a getElementInfo
            */
    private String parseFieldsInfoResponse(File f) {
        BufferedReader fi;
        String buf;
        StringBuffer fields = new StringBuffer();
        String token = null;
        int l = 0;

        try {
            fi = new BufferedReader(new FileReader(f));

            while ((buf = fi.readLine()) != null) {
                l++;
                if ((buf.length() > 2) &&
                        (buf.substring(0, 1).compareTo("<") == 0)) {
                    token = buf.substring(1); //,l.indexOf(GT));

                    if (token.startsWith("FIELDS")) {
                        fields.append(buf + "");
                    }

                    if (token.startsWith("BAND ")) {
                        fields.append(buf + "");
                    }
                }
            }

            fi.close();
            logger.debug("Parsed finidhed: " + l + " lines.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ie) {
            logger.error("ERROR. (" + l + "lines reeded)");
            ie.printStackTrace();
        }

        return fields.toString();
    }

    /**
     * <p>Builds a GetMap request that is sent to the ArcIms
     * the response (image) will be redirect to the
     * ArcIms client</p>
     * @param status An  @see ArcImsStatus object
     * @return File The image requested (a map)
    * @throws ArcImsException
    * @throws ServerErrorException
     */
    public File getMap(ArcImsStatus status)
        throws ArcImsException, ServerErrorException {
        File img = null;
        String currentImageUrl = "";

        try {
            URL url = new URL(buildCapabilitiesRequest(status));

            String request = ArcXMLImage.getMapRequest(status);

            if (status.verbose) {
            	logger.debug(request);
            }

            logger.info("Requesting ArcIMS GetImage XML");

            File response = ArcImsDownloadUtils.doRequestPost(url, request,
                    "getImage.xml");

            try {
                currentImageUrl = parseGetImageRequest(new FileReader(response));

                //Control if not /output/blahblah has been passed
                if (!currentImageUrl.startsWith("http://")) {
                    String servlet = status.getServer();
                    int ind1 = servlet.indexOf("//");
                    int ind = servlet.indexOf("/", ind1 + 2);
                    currentImageUrl = servlet.substring(0, ind) +
                        currentImageUrl;
                }
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
                throw new ArcImsException("Fichero remoto no encontrado");
            }

            URL virtualURL = ArcImsDownloadUtils.getVirtualUrlFromStatus(status);
            logger.info("Downloading ArcIMS image");

            String[] urlSplitted = currentImageUrl.split("\\.");
            String extension = urlSplitted[urlSplitted.length - 1];
            img = ArcImsDownloadUtils.downloadFile(new URL(currentImageUrl),
                    virtualURL, "getImage." + extension);

            // img =com.iver.andami.Utilities.downloadFile(new URL(currentImageUrl),"getImage");
            if (img.length() == 0) {
                return null;
            }

            // response.delete();
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_server_timeout");
        } catch (ConnectException ce) {
            logger.error("Timed out error", ce);
            throw new ArcImsException("arcims_server_timeout");
        } catch (FileNotFoundException fe) {
            logger.error("FileNotFound Error", fe);
            throw new ArcImsException("arcims_server_error");
        } catch (IOException e) {
            logger.error("IO Error", e);
            throw new ArcImsException("arcims_server_error");
        }

        return img;
    }

    /**
     * Method that parses a GET_IMAGE request, sets the
     * url to download the rendered image.
     * @param fr
    * @return @see  org.gvsig.remoteClient.arcims.ArcImsProtImageHandler#getMap(ArcImsStatus)
    * @throws ArcImsException
    * @throws ServerErrorException
     */
    private String parseGetImageRequest(Reader fr)
        throws ArcImsException, ServerErrorException {
        String imageUrl = "";
        int tag;
        KXmlParser kxmlParser = new KXmlParser();
        String value = null;

        try {
            kxmlParser.setInput(fr);
            kxmlParser.nextTag();

            if (kxmlParser.getEventType() != KXmlParser.END_DOCUMENT) {
                kxmlParser.require(KXmlParser.START_TAG, null,
                    ServiceInfoTags.tARCXML);
                tag = kxmlParser.nextTag();

                while (tag != KXmlParser.END_DOCUMENT) {
                    switch (tag) {
                    case KXmlParser.START_TAG:

                        if (kxmlParser.getName().compareTo(GetImageTags.OUTPUT) == 0) {
                            value = kxmlParser.getAttributeValue("",
                                    GetImageTags.URL);

                            if (value != null) {
                                imageUrl = value;
                            }
                        } else if (kxmlParser.getName()
                                                 .compareTo(ServiceInfoTags.tERROR) == 0) {
                            throw new ServerErrorException(
                                "Error parsing GET_IMAGE:\r\n" +
                                kxmlParser.nextText());
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
        } catch (XmlPullParserException parser_ex) {
            logger.error(parser_ex.getMessage(), parser_ex);
            throw new ArcImsException("arcims_server_error");
        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            throw new ArcImsException("arcims_server_error");
        }

        return imageUrl;
    }

    /**
     * Simple method to test if an ArcIMS server responses with an image
     * of an specified format
     * @param status
     * @return
     * @throws ArcImsException
     */
    public boolean testFormat(ArcImsStatus status, String format)
        throws ArcImsException {
        boolean resp = true;
        URL url;

        try {
            ServiceInformation si = status.getServiceInfo();
            url = new URL(this.buildCapabilitiesRequest(status));

            /**
             * Build the custom request for this extent
             */
            String request = ArcXMLImage.getCustomExtentRequest("", // ServiceInfo
                                                                    // SRS
                    null, // envelope
                    "", // SRS of the view
                    si.getSeparators().getDs(), // Decimal separator
                    null, // Image size
                    format, si.getLayer(0).getId() // A valid Layer Id
                );

            if (status.verbose) {
            	logger.debug(request);
            }

            logger.info("Querying for a specific format");

            //			BufferedReader lector = ArcImsDownloadUtils.getRemoteReader(url,request);
            File f = ArcImsDownloadUtils.doRequestPost(url, request,
                    "testFormat.xml");
            BufferedReader lector = new BufferedReader(new FileReader(f));

            /**
             * Loop over the reader until it ends or an ERROR is found
             */
            String lin = lector.readLine();

            //The regular expression to match
            String regex = ".*<" + ServiceInfoTags.tERROR + ".*</" +
                ServiceInfoTags.tERROR + ">.*";

            do {
                if (lin.matches(regex)) {
                    return false;
                }

                lin = lector.readLine();
            } while (lin != null);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_server_eror");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new ArcImsException("arcims_server_eror");
        }

        return resp;
    }
}
