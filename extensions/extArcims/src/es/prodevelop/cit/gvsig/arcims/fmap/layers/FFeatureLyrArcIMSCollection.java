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
 *   Generalitat Valenciana
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
package es.prodevelop.cit.gvsig.arcims.fmap.layers;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.cresques.cts.ProjectionUtils;
import org.gvsig.remoteClient.arcims.ArcImsFeatureClient;
import org.gvsig.remoteClient.arcims.ArcImsProtocolHandler;
import org.gvsig.remoteClient.arcims.utils.MyCancellable;
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;
import org.gvsig.remoteClient.arcims.utils.ServiceInformation;
import org.gvsig.remoteClient.arcims.utils.ServiceInformationLayer;
import org.gvsig.remoteClient.arcims.utils.ServiceInformationLayerFeatures;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionListener;
import com.iver.cit.gvsig.fmap.layers.LayerDrawEvent;
import com.iver.cit.gvsig.fmap.layers.LayerPositionEvent;
import com.iver.cit.gvsig.fmap.layers.SelectionEvent;
import com.iver.cit.gvsig.fmap.layers.SelectionListener;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.layers.layerOperations.LayerCollection;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.connections.ConnectionException;
import com.iver.utiles.swing.threads.Cancellable;
import com.iver.utiles.swing.threads.DefaultCancellableMonitorable;

import es.prodevelop.cit.gvsig.arcims.fmap.drivers.ArcImsVectorialAdapter;
import es.prodevelop.cit.gvsig.arcims.fmap.drivers.ArcImsVectorialEditableAdapter;
import es.prodevelop.cit.gvsig.arcims.fmap.drivers.FMapFeatureArcImsDriver;


public class FFeatureLyrArcIMSCollection extends FLayers
    implements SelectionListener, LayerCollectionListener {
    private static Logger logger = Logger.getLogger(FFeatureLyrArcIMSCollection.class.getName());
    private ArrayList layerlist = new ArrayList();
    private boolean mustBeSeparated = false;
    private MapContext myFMap;

    public FFeatureLyrArcIMSCollection(MapContext fmap, FLayers parent,
        boolean willbesep) {
        super();
        setParentLayer(parent);
        setMapContext(fmap);
        myFMap = fmap;
        mustBeSeparated = willbesep;
    }

    public FFeatureLyrArcIMSCollection() {
        // super(null, null);
    	super();
    }

    /**
     * The extCatalogYNomenclator needs this creator.
     *
     * @param p a Map object with the following keys:
     *
     * (key, object type returned)
     * ---------------------------
     * "host", String (with or without the servlet path)
     * "service_name", String (remote service name)
     * "srs", String (coordinate system)
     * "layer_name", String (local layer name)
     *
     * @return a FFeatureLyrArcIMSCollection layer
     * @throws ConnectionException
     */
    public FFeatureLyrArcIMSCollection(Map m) throws ConnectionException {
        super();

        try {
            String _host = (String) m.get("host");
            String host = ArcImsProtocolHandler.getUrlWithServlet(new URL(_host))
                                               .toString();
            String service = (String) m.get("service_name");
            String _srs = (String) m.get("srs");
            String name = (String) m.get("layer_name");

            // in case layer_name is missing or equals the empty string:
            if ((name == null) || (name.length() == 0)) {
                name = service;
            }

            // --------------------------------------------
            CoordinateReferenceSystem srs = ProjectionUtils.getCRS(_srs);

            MyCancellable myCanc = new MyCancellable(new DefaultCancellableMonitorable());
            FMapFeatureArcImsDriver drv = new FMapFeatureArcImsDriver(host,
                    service);

            if (!drv.connect(myCanc)) {
                throw new Exception("Unable to connect to server. ");
            }

            ServiceInformation si = drv.getClient().getServiceInformation();
            int layercount = si.getLayers().size();
            String layerQuery = "";

            for (int i = 0; i < layercount; i++) {
                if (isTrueString(
                            ((ServiceInformationLayer) si.getLayer(i)).getVisible())) {
                    layerQuery = layerQuery + "," +
                        ((ServiceInformationLayer) si.getLayer(i)).getId();
                }
            }

            if (layerQuery.length() == 0) {
                throw new Exception("No layers are visible by default ");
            }
            else {
                layerQuery = layerQuery.substring(1);
            }

            String[] selectedLayerIds = layerQuery.split(",");
            int count = selectedLayerIds.length;

            FFeatureLyrArcIMS[] individualLayers = new FFeatureLyrArcIMS[count];

            String item;

            for (int i = 0; i < count; i++) {
                item = selectedLayerIds[i];

                drv = new FMapFeatureArcImsDriver(host, service, item);

                if (!(drv.connect(myCanc))) {
                    throw new Exception();
                }

                ArcImsVectorialAdapter oldadapter = new ArcImsVectorialAdapter(drv);
                ArcImsVectorialEditableAdapter adapter = new ArcImsVectorialEditableAdapter();

                /* 1 */ individualLayers[i] = new FFeatureLyrArcIMS(adapter);

                /* 2 */ drv.setLayer(individualLayers[i]);

                si = drv.getClient().getServiceInformation();

                ServiceInformationLayerFeatures silf = (ServiceInformationLayerFeatures) si.getLayerById(item);
                String lyrname = silf.getName();

				individualLayers[i].setProjectionInStatus(ProjectionUtils
						.getAbrev(srs));
                individualLayers[i].setHostInStatus(new URL(host));
                individualLayers[i].setServiceInStatus(service);

                String units = si.getMapunits();
                int theDpi = si.getScreen_dpi();
                long scale;

                if (silf.getMaxscale() != -1) {
                    scale = LayerScaleData.getTrueScaleFromRelativeScaleAndMapUnits(silf.getMaxscale(),
                            units, theDpi);
                    individualLayers[i].setMaxScale((double) scale);
                }

                if (silf.getMinscale() != -1) {
                    scale = LayerScaleData.getTrueScaleFromRelativeScaleAndMapUnits(silf.getMinscale(),
                            units, theDpi);
                    individualLayers[i].setMinScale((double) scale);
                }

                individualLayers[i].setServiceInformationInStatus(si);

                Vector ids = new Vector();
                ids.add(item);
                individualLayers[i].setLayerIdsInStatus((Vector) ids.clone());
                individualLayers[i].setSubfieldsInStatus();

                /* 3 */
                // individualLayers[i].setLegend(new VectorialUniqueValueLegend());
                individualLayers[i].setHost(new URL(host));
                individualLayers[i].setService(service);
                individualLayers[i].setServiceType(ServiceInfoTags.vFEATURESERVICE);
                individualLayers[i].setTransparency(0);
                individualLayers[i].setLayerQuery(item);
                individualLayers[i].setCrs(srs);
                individualLayers[i].setName(lyrname);

                Rectangle2D fext = ((ArcImsFeatureClient) drv.getClient()).getLayerExtent(individualLayers[i].getArcimsStatus());
                drv.setFullExtent(fext);

                // individualLayers[i].setF. setFullExtent(((ArcImsProtImageHandler) drv.getClient().getHandler()).getServiceExtent(srs, individualLayers[i].getArcimsStatus()));

                // ------ -------------
                drv.setAdapter(adapter);

                // adapter.setRecordSet(drv.getRecordSet());
                adapter.setOriginalDataSource(drv.getRecordSet());
                adapter.setOriginalVectorialAdapter(oldadapter);
                drv.declareTable( individualLayers[i] );
                individualLayers[i].setInitialLegend();
                individualLayers[i].setShapeType(adapter.getShapeType());
                individualLayers[i].setRecordset(drv.getRecordSet());

                // ------ -------------
                if ((si.getFeaturecoordsys() == null) ||
                        (si.getFeaturecoordsys().equals(""))) {
					si.setFeaturecoordsys(ProjectionUtils.getAbrev(srs)
							.substring(ServiceInfoTags.vINI_SRS.length())
							.trim());
                    logger.warn("Server provides no SRS. ");
                }
            }

            setName(name);
            setCrs(srs);

            for (int i = 0; i < count; i++) {
                addLayer(individualLayers[i]);
            }
        }
        catch (Exception e) {
            throw new ConnectionException("Unable to create ArcIMS feature layer collection ",
                e);
        }
    }

    public MapContext getMapContext() {
        return myFMap;
    }

    public boolean mustBeSeparated() {
        return mustBeSeparated;
    }

    public void hasBeenSeparated() {
        mustBeSeparated = false;
    }

    public void setParentLayer(FLayers lyr) {
        super.setParentLayer(lyr);

        if (lyr == null) {
            return;
        }

        myFMap = lyr.getMapContext();

        if (lyr instanceof LayerCollection) {
            LayerCollection lyrcol = (LayerCollection) lyr;
            lyrcol.addLayerCollectionListener(this);
        }
    }

    private List myGetList() {
        List resp = Collections.synchronizedList(new ArrayList());

        int count = getLayersCount();

        for (int i = 0; i < count; i++) {
            resp.add(getLayer(i));
        }

        return resp;
    }

    // we need to rewrite this method because we dont have access to fmap
    // and list
    public void draw(BufferedImage image, Graphics2D g, ViewPort viewPort,
        Cancellable cancel, double scale) throws ReadDriverException {
        List myList = myGetList();

        Iterator iter = myList.iterator();

        try {
            while (iter.hasNext()) {
                if (cancel.isCanceled()) {
                    break; // Más que nada porque las capas raster no son interrumpibles por ahora.
                }

                FLayer lyr = (FLayer) iter.next();
                LayerDrawEvent beforeEvent = new LayerDrawEvent(lyr, g,
                        viewPort, LayerDrawEvent.LAYER_BEFORE_DRAW);

                myFMap.fireLayerDrawingEvent(beforeEvent);

                if (lyr.isVisible()) {
                    long t1 = System.currentTimeMillis();

                    try {
                        lyr.draw(image, g, viewPort, cancel, scale);
                    }
                    catch (ReadDriverException e) {
                        myFMap.addLayerError("La capa " + lyr.getName() +
                            " es errónea.");
                        e.printStackTrace();
                    }

                    long t2 = System.currentTimeMillis();
                    System.out.println("Layer " + lyr.getName() + " " +
                        (t2 - t1) + " milisecs.");
                }

                LayerDrawEvent afterEvent = new LayerDrawEvent(lyr, g,
                        viewPort, LayerDrawEvent.LAYER_AFTER_DRAW);
                myFMap.fireLayerDrawingEvent(afterEvent);
            }

            if (getVirtualLayers() != null) {
                getVirtualLayers().draw(image, g, viewPort, cancel, scale);
            }
        }
        catch (ConcurrentModificationException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addLayer(FLayer layer) throws CancelationException {
        if (!(layer instanceof FFeatureLyrArcIMS)) {
            logger.error(
                "Only FFeatureLyrArcIMS layers allowed in this collection. ");

            return;
        }

        FFeatureLyrArcIMS lyr = (FFeatureLyrArcIMS) layer;

        // lyr.addSelectionListener(this);
        super.addLayer(layer);
        layerlist.add(layer);
    }

    public void selectionChanged(SelectionEvent e) {
        // e.get
        getMapContext().invalidate();

        // System.out.println("==========> Selection changed somewhere....");
        // TODO Auto-generated method stub
    }

    public void setXMLEntity(XMLEntity xml) throws XMLException {
        setActive(xml.getBooleanProperty("active"));
        setName(xml.getStringProperty("name"));
        setMinScale(xml.getDoubleProperty("minScale"));
        setMaxScale(xml.getDoubleProperty("maxScale"));
        setVisible(xml.getBooleanProperty("visible"));

        if (xml.contains("proj")) {
            setCrs(ProjectionUtils.getCRS(xml.getStringProperty("proj")));
        }

        if (xml.contains("transparency")) {
            setTransparency(xml.getIntProperty("transparency"));
        }

        // ------------
        int numLayers = xml.getIntProperty("numLayers");
        String[] s = xml.getStringArrayProperty("LayerNames");

        for (int i = 0; i < numLayers; i++) {
            FLayer layer = null;

            try {
                String className = xml.getChild(i).getStringProperty("className");
                Class clase = Class.forName(className);
                layer = (FLayer) clase.newInstance();
                layer.setName(s[i]);
                layer.setXMLEntity(xml.getChild(i));
                layer.load();
                logger.debug("Layer: " + layer.getName() + " has been loaded.");
            }
            catch (Exception e) {
                logger.error("While loading layer: " + layer.getName() +
                    " (visible = false)", e);
                layer.setVisible(false);
            }

            addLayer(layer);
        }
    }

    public XMLEntity getXMLEntity() throws XMLException {
        XMLEntity xml = new XMLEntity();
        xml.putProperty("className", this.getClass().getName());
        xml.putProperty("active", isActive());
        xml.putProperty("name", getName());
        xml.putProperty("minScale", getMinScale());
        xml.putProperty("maxScale", getMaxScale());
        xml.putProperty("visible", isVisible());

        if (getCrs() != null) {
			xml.putProperty("proj", ProjectionUtils.getAbrev(getCrs()));
        }

        xml.putProperty("transparency", getTransparency());

        // --------------------
        xml.putProperty("numLayers", layerlist.size());

        String[] s = new String[layerlist.size()];

        for (int i = 0; i < layerlist.size(); i++) {
            s[i] = ((FFeatureLyrArcIMS) layerlist.get(i)).getName();
        }

        xml.putProperty("LayerNames", s);

        for (int i = 0; i < layerlist.size(); i++) {
            xml.addChild(((FFeatureLyrArcIMS) layerlist.get(i)).getXMLEntity());
        }

        return xml;
    }

    public void layerAdded(LayerCollectionEvent e) {
        // this layer has been added to a collection
        // if it has to be separated, it will be separated now:
        if (!mustBeSeparated) {
            return;
        }

        hasBeenSeparated();

        FLayers lyr = getParentLayer();

        if (lyr == null) {
            return;
        }

        FLayer item;
        int count;

        if (lyr instanceof LayerCollection) {
            LayerCollection lyrcol = (LayerCollection) lyr;
            count = getLayersCount();

            while (count > 0) {
                item = getLayer(count - 1);
                removeLayer(item);
                try {
					lyrcol.addLayer(item);
				} catch (Exception ex) {
					logger.error("While addgin layer: " + ex.getMessage());
				}
                count = getLayersCount();
            }

            lyrcol.removeLayer(this);
        }
    }

    public void layerMoved(LayerPositionEvent e) {
        // TODO Auto-generated method stub
    }

    public void layerRemoved(LayerCollectionEvent e) {
        // TODO Auto-generated method stub
    }

    public void layerAdding(LayerCollectionEvent e) throws CancelationException {
        // TODO Auto-generated method stub
    }

    public void layerMoving(LayerPositionEvent e) throws CancelationException {
        // TODO Auto-generated method stub
    }

    public void layerRemoving(LayerCollectionEvent e)
        throws CancelationException {
        // TODO Auto-generated method stub
    }

    public void activationChanged(LayerCollectionEvent e)
        throws CancelationException {
        // TODO Auto-generated method stub
    }

    public void visibilityChanged(LayerCollectionEvent e)
        throws CancelationException {
        // TODO Auto-generated method stub
    }

    public void setXMLEntity03(XMLEntity xml) throws XMLException {
        // TODO
    }

    public void replaceLayer(String layerName, FLayer layer) {
        // TODO
    }

    private boolean isTrueString(String visible) {
        if (visible.compareToIgnoreCase("true") == 0) {
            return true;
        }

        return false;
    }
}
