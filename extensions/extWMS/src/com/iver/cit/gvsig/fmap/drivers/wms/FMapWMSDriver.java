/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package com.iver.cit.gvsig.fmap.drivers.wms;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.StringWriter;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;


import org.apache.commons.io.IOUtils;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.StyleImpl;
import org.geotools.data.wms.WMSUtils;
import org.geotools.data.wms.WebMapServer;
import org.geotools.data.wms.request.GetFeatureInfoRequest;
import org.geotools.data.wms.request.GetLegendGraphicRequest;
import org.geotools.data.wms.request.GetMapRequest;
import org.geotools.data.wms.response.GetFeatureInfoResponse;
import org.geotools.data.wms.response.GetLegendGraphicResponse;
import org.geotools.data.wms.response.GetMapResponse;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.ows.ServiceException;
import org.geotools.referencing.CRS;
import org.gvsig.remoteClient.exceptions.ServerErrorException;

import org.gvsig.remoteClient.utils.Utilities;
import org.gvsig.remoteClient.wms.ICancellable;
import org.gvsig.remoteClient.wms.WMSClient;
import org.gvsig.remoteClient.wms.WMSLayer;
import org.gvsig.remoteClient.wms.WMSStatus;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


import com.iver.cit.gvsig.fmap.layers.GTLayerAdaptareToWMSLayerNode;
import com.iver.cit.gvsig.fmap.layers.WMSLayerNode;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
/**
 * Driver WMS.
 *
 * @author Jaume Dominguez Faus
 */
public class FMapWMSDriver  {
    private WMSLayerNode fmapRootLayer;
    private TreeMap layers = new TreeMap();
    private WebMapServer wms;

    protected FMapWMSDriver(URL url) throws ConnectException, IOException {
	try {
	    wms = new WebMapServer(url);
	} catch (ServiceException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public String[] getLayerNames(){
	List<Layer> layerList = wms.getCapabilities().getLayerList();
	String[] layerNames = new String[layerList.size()];

	for (int i = 0; i < layerList.size(); i++) {
	    layerNames[i] = layerList.get(i).getName();
	}
	return layerNames;
    }

    public String[] getLayerTitles(){
	List<Layer> layerList = wms.getCapabilities().getLayerList();
	String[] layerTitles = new String[layerList.size()];

	for (int i = 0; i < layerList.size(); i++) {
	    layerTitles[i] = layerList.get(i).getTitle();
	}
	return layerTitles;
    }
   
    @Deprecated
	public void getCapabilities(URL server)
		throws WMSException {
	}


    public File getMap(WMSStatus status, ICancellable cancel) throws WMSException {
	GetMapRequest mapRequest = createMapRequest(status);

	try {
	    GetMapResponse response = (GetMapResponse) wms
		    .issueRequest(mapRequest);

	    return getMyResponse(mapRequest.getFinalURL(), cancel, "wmsGetMap");
	} catch (ServerErrorException e) {
	    throw new WMSException("WMS Unexpected server error."
		    + e.getMessage());
	} catch (ServiceException e) {
	    throw new WMSException("WMS Unexpected server error."
		    + e.getMessage());
	} catch (IOException e) {
	    throw new WMSException("WMS Unexpected server error."
		    + e.getMessage());
	}
    }

    private GetMapRequest createMapRequest(WMSStatus status) {
	GetMapRequest mapRequest = wms.createGetMapRequest();

	mapRequest.setSRS(status.getSrs());

	String bbox = status.getExtent().getMinX() + ","
		+ status.getExtent().getMinY() + ","
		+ status.getExtent().getMaxX() + ","
		+ status.getExtent().getMaxY();
	mapRequest.setBBox(bbox);
	mapRequest.setDimensions(status.getWidth(), status.getHeight());
	mapRequest.setFormat(status.getFormat());
	mapRequest.setTransparent(status.getTransparency());
	
	Vector<String> stylesVector = status.getStyles();
	if (stylesVector != null && stylesVector.size() > 0) {
	    Vector<String> layerNamesVector = status.getLayerNames();
	    for (int i=0; i < stylesVector.size(); i++) {
		mapRequest.addLayer(layerNamesVector.get(i), stylesVector.get(i));
	    }
	} else {
	    for (Layer layer : WMSUtils.getNamedLayers(wms.getCapabilities())) {
		mapRequest.addLayer(layer);
	    }
	}
	return mapRequest;
    }
    
    public File getMyResponse(URL request, ICancellable cancel, String id)
	    throws ServerErrorException {
	File f;
	try {
	    f = Utilities.downloadFile(request, id, cancel);

	    if (f == null) {
		return null;
	    }
	    // TODO: fpuga: Exception must be parsed here
	    return f;

	} catch (IOException e) {
	    e.printStackTrace();
	    throw new ServerErrorException();
	}

    }

    /**
     * Gets the legend graphic of one layer
     */
    public File getLegendGraphic(WMSStatus status, String layerName, ICancellable cancel) throws WMSException {
	GetLegendGraphicRequest request = wms.createGetLegendGraphicRequest();
	request.setLayer(layerName);
	request.setFormat("image/png");
	request.setStyle((String) status.getStyles().get(0));

	try {

	    GetLegendGraphicResponse response = wms.issueRequest(request);
	    return getMyResponse(request.getFinalURL(), cancel,
		    "wmsGetLegendGraphic");
	} catch (ServerErrorException e) {
	    throw new WMSException("WMS Unexpected server error."
		    + e.getMessage());
	} catch (ServiceException e) {
	    throw new WMSException("WMS Unexpected server error."
		    + e.getMessage());
	} catch (IOException e) {
	    throw new WMSException("WMS Unexpected server error."
		    + e.getMessage());
	}
    }


    /**
     * Establishes the connection.
     * @param override, if true the previous downloaded data will be overridden
     * @return <b>true</b> if the connection was successful, or <b>false</b> if it was no possible
     * to establish the connection for any reason such as version negotiation.
     */
    @Deprecated
    public boolean connect(boolean override, ICancellable cancel) {
    	if (override) {
    		fmapRootLayer = null;
    		layers.clear();
    	}
	return true;
    }

    @Deprecated
    public boolean connect(ICancellable cancel) {
	return true;
    }

    public String getVersion() {
	return wms.getCapabilities().getVersion();
    }

    public String getServiceTitle() {
	return wms.getInfo().getTitle();
    }

    /**
     * Returns a Hash table containing the values for each online resource.
     * Using as key a String with name of the WMS request and the value returned
     * by the hash is another string containing the corresponding Url
     * @return HashTable
     */
    public Hashtable getOnlineResources() {
	// fpuga TODO

	return new Hashtable<String, String>();
    }

    // TODO: fpuga: This should return a List not a Vector. I let it no maintain
    // the API
    public Vector getFormats() {
	List<String> formatsList = wms.getCapabilities().getRequest()
		.getGetMap()
		.getFormats();
	Vector<String> formats = new Vector<String>();
	for (String f : formatsList) {
	    formats.add(f);
	}
	return formats;
    }

    
    public boolean isQueryable() {
	return wms.getCapabilities().getRequest().getGetFeatureInfo() != null;
    }
    
    public boolean hasLegendGraphic() {
	return wms.getCapabilities().getRequest().getGetLegendGraphic() != null;
    }
    
    /**
     * @return A tree containing the info of all layers available on this server.
     */
    public WMSLayerNode getLayersTree() {
        if (fmapRootLayer == null){
	    GTLayerAdaptareToWMSLayerNode adapter = new GTLayerAdaptareToWMSLayerNode();
	    fmapRootLayer = adapter.adapter(wms.getCapabilities().getLayer(),
		    null, layers);
        }
        return fmapRootLayer;
    }



    public String getAbstract() {
	return wms.getInfo().getDescription();
    }

    /**
     * @param layerName
     * @param srs
     * @return
     */
    public Rectangle2D getLayersExtent(String[] layerNames, String srs) {

	try {
	    CoordinateReferenceSystem crs = CRS.decode(srs);
	    Set<Layer> gtLayers = getGTLayersFromLayerTitles(Arrays
		    .asList(layerNames));
	    GeneralEnvelope envelope = new GeneralEnvelope(crs);
	    for (Layer l : gtLayers) {
		envelope.add(l.getEnvelope(crs));
	    }

	    return envelope.toRectangle2D();
	} catch (NoSuchAuthorityCodeException e) {
	    e.printStackTrace();
	} catch (FactoryException e) {
	    e.printStackTrace();
	}
	
	return null;
    }


    public WMSLayerNode getLayer(String layerName) {
        if (getLayers().get(layerName) != null)
        {
            return (WMSLayerNode)layers.get(layerName);
        }
        return null;
    }


    private TreeMap getLayers() {
        if (fmapRootLayer == null){
            fmapRootLayer = getLayersTree();
        }
        return layers;
    }

    /**
     * @param wmsStatus
     * @param i
     * @param j
     * @param max_value
     * @return
     * @throws WMSException
     */
    public String getFeatureInfo(WMSStatus _wmsStatus, int i, int j, int max_value, ICancellable cancellable) throws WMSException {
	GetFeatureInfoRequest request = wms
		.createGetFeatureInfoRequest(createMapRequest(_wmsStatus));
	request.setQueryPoint(i, j);
	request.setFeatureCount(max_value);
	request.setInfoFormat("application/vnd.ogc.gml");

	Set<Layer> layerSet = getGTLayers(_wmsStatus);

	request.setQueryLayers(layerSet);

	try {
	    GetFeatureInfoResponse response = wms.issueRequest(request);

	    StringWriter writer = new StringWriter();
	    IOUtils.copy(response.getInputStream(), writer, "UTF-8");
	    return writer.toString();
	} catch (ServiceException e1) {
	    e1.printStackTrace();
	    throw new WMSException();
	} catch (IOException e1) {
	    e1.printStackTrace();
	    throw new WMSException();
	}
    }

    private Set<Layer> getGTLayers(WMSStatus wmsStatus) {
	Vector<String> layerNames = wmsStatus.getLayerNames();
	return getGTLayersFromLayerTitles(layerNames);
    }

    private Set<Layer> getGTLayersFromLayerTitles(List<String> layerNames) {
	List<Layer> layerList = wms.getCapabilities().getLayerList();
	Set<Layer> layerSet = new HashSet<Layer>();

	for (String layerName : layerNames) {
	    for (Layer l : layerList) {
		if (l.getTitle().equalsIgnoreCase(layerName)) {
		    layerSet.add(l);
		    break;
		}
	    }
	}
	return layerSet;
    }

    public String getHost(){
	// fpuga: this is not the original host, maybe we should return the
	// original
	String host = wms.getInfo().getSource().toString();
	return host;
    }

    /**
     * TODO: fpuga: I don't understand what is doing this method, y adapt it
     * from WMSProtocolHandler only
     * 
     * @return true if the layer has legendurl (layer-->style object in
     *         capabilities) returns false when more than one layer is selected
     * 
     */
    public boolean hasLegendUrl(WMSStatus wmsStatus, String layerQuery) {
	String statusStyle = (String) wmsStatus.getStyles().get(0);

	for (Layer layer:wms.getCapabilities().getLayerList()) {
	    if (layer.getName().equalsIgnoreCase(layerQuery)) {
		for (StyleImpl style:layer.getStyles()) {
		    if (style.getName().equalsIgnoreCase(statusStyle)) {
			return true;
		    }
		}
	    }
	}
	return false;
    }
}