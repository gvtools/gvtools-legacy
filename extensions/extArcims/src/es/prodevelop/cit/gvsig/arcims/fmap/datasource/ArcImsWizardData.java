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
package es.prodevelop.cit.gvsig.arcims.fmap.datasource;

import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.cresques.cts.ProjectionUtils;
import org.gvsig.remoteClient.arcims.ArcImsFeatureClient;
import org.gvsig.remoteClient.arcims.ArcImsProtImageHandler;
import org.gvsig.remoteClient.arcims.ArcImsStatus;
import org.gvsig.remoteClient.arcims.utils.MyCancellable;
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;
import org.gvsig.remoteClient.arcims.utils.ServiceInformation;
import org.gvsig.remoteClient.arcims.utils.ServiceInformationLayerFeatures;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.utiles.swing.threads.DefaultCancellableMonitorable;

import es.prodevelop.cit.gvsig.arcims.fmap.drivers.ArcImsDriver;
import es.prodevelop.cit.gvsig.arcims.fmap.drivers.ArcImsVectorialAdapter;
import es.prodevelop.cit.gvsig.arcims.fmap.drivers.ArcImsVectorialEditableAdapter;
import es.prodevelop.cit.gvsig.arcims.fmap.drivers.FMapFeatureArcImsDriver;
import es.prodevelop.cit.gvsig.arcims.fmap.drivers.FMapRasterArcImsDriver;
import es.prodevelop.cit.gvsig.arcims.fmap.layers.FFeatureLyrArcIMS;
import es.prodevelop.cit.gvsig.arcims.fmap.layers.FFeatureLyrArcIMSCollection;
import es.prodevelop.cit.gvsig.arcims.fmap.layers.FRasterLyrArcIMS;
import es.prodevelop.cit.gvsig.arcims.fmap.layers.LayerScaleData;

/**
 * This class implements a data source object, owned by the ArcImsWizard object.
 * It will create the ArcIMS layer and load some basic data.
 * 
 * @author jldominguez
 * 
 */
public class ArcImsWizardData {
	private static Logger logger = Logger.getLogger(ArcImsWizardData.class
			.getName());

	/**
	 * The graphic context in which a request is performed (ccord. system,
	 * view's dimension etc.)
	 */
	private ArcImsStatus status;
	private String testInfo;
	private String serviceType;
	private FMapRasterArcImsDriver arcImsImageDriver = null;
	private FMapFeatureArcImsDriver arcImsFeatureDriver = null;
	private boolean missingSrs = false;
	private MapControl mapControl;
	private MyCancellable myCanc;

	public ArcImsWizardData() {
		myCanc = new MyCancellable(new DefaultCancellableMonitorable());
	}

	public void setHostService(URL host, String service, String svcType)
			throws DriverException {
		serviceType = svcType;

		boolean nogood = true;

		if (serviceType.compareToIgnoreCase(ServiceInfoTags.vIMAGESERVICE) == 0) {
			nogood = false;
			arcImsImageDriver = new FMapRasterArcImsDriver(host.toString(),
					service, svcType);

			// Send a getCapabilities request;
			if (!arcImsImageDriver.connect(myCanc)) {
				DriverException de = new DriverException("cant_connect");
				logger.error("While trying to connect ", de);
				throw de;
			}

			testInfo = "Connected successfully.";
		}

		if (serviceType.compareToIgnoreCase(ServiceInfoTags.vFEATURESERVICE) == 0) {
			nogood = false;
			arcImsFeatureDriver = new FMapFeatureArcImsDriver(host.toString(),
					service, svcType);

			// Send a getCapabilities request;
			if (!arcImsFeatureDriver.connect(myCanc)) {
				DriverException de = new DriverException("cant_connect");
				logger.error("While trying to connect ", de);
				throw de;
			}

			testInfo = "Connected successfully.";
		}

		if (nogood) {
			logger.error("Nothing done. Unable to find out serviceType ");
		}
	}

	public void setHostService(URL host, String service, String svcType,
			FLayer lyr) throws DriverException {
		serviceType = svcType;

		boolean nogood = true;

		if (serviceType.compareToIgnoreCase(ServiceInfoTags.vIMAGESERVICE) == 0) {
			nogood = false;

			arcImsImageDriver = (FMapRasterArcImsDriver) ((FRasterLyrArcIMS) lyr)
					.getDriver();

			// arcImsImageDriver = new FMapRasterArcImsDriver(host.toString(),
			// service, svcType);
			// Send a getCapabilities request;
			if (!arcImsImageDriver.connect(myCanc)) {
				DriverException de = new DriverException("cant_connect");
				logger.error("While trying to connect ", de);
				throw de;
			}

			testInfo = "Connected successfully.";
		}

		if (serviceType.compareToIgnoreCase(ServiceInfoTags.vFEATURESERVICE) == 0) {
			nogood = false;

			// arcImsFeatureDriver = new
			// FMapFeatureArcImsDriver(host.toString(), service, svcType);
			arcImsFeatureDriver = (FMapFeatureArcImsDriver) ((FFeatureLyrArcIMS) lyr)
					.getSource().getDriver();

			// Send a getCapabilities request;
			if (!arcImsFeatureDriver.connect(myCanc)) {
				DriverException de = new DriverException("cant_connect");
				logger.error("While trying to connect ", de);
				throw de;
			}

			testInfo = "Connected successfully.";
		}

		if (nogood) {
			logger.error("Nothing done. Unable to find out serviceType ");
		}
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String t) {
		serviceType = t;
	}

	public ArcImsDriver getDriver() {
		if (serviceType.compareToIgnoreCase(ServiceInfoTags.vFEATURESERVICE) == 0) {
			return arcImsFeatureDriver;
		}

		if (serviceType.compareToIgnoreCase(ServiceInfoTags.vIMAGESERVICE) == 0) {
			return arcImsImageDriver;
		}

		logger.error("Null value returned. Unable to find out serviceType ");

		return null;
	}

	public void setDriver(ArcImsDriver d) {
		boolean nogood = true;

		if (d instanceof FMapRasterArcImsDriver) {
			arcImsImageDriver = (FMapRasterArcImsDriver) d;
			nogood = false;
		}

		if (d instanceof FMapFeatureArcImsDriver) {
			arcImsFeatureDriver = (FMapFeatureArcImsDriver) d;
			nogood = false;
		}

		if (nogood) {
			logger.error("Nothing done. Unable to find out serviceType ");
		}
	}

	public ArcImsStatus getStatus() {
		return status;
	}

	public void setStatus(ArcImsStatus s) {
		status = s;
	}

	/**
	 * Gets available layers from the current server and service
	 * 
	 * @return a TreeMap with available layers
	 */
	public TreeMap getLayers() {
		if (serviceType.compareToIgnoreCase(ServiceInfoTags.vFEATURESERVICE) == 0) {
			return arcImsFeatureDriver.getLayers();
		}

		if (serviceType.compareToIgnoreCase(ServiceInfoTags.vIMAGESERVICE) == 0) {
			return arcImsImageDriver.getLayers();
		}

		logger.error("Null value returned. Unable to find out serviceType in method "
				+ "TreeMap getLayers()"
				+ ", class: "
				+ this.getClass().toString());

		return null;
	}

	/**
	 * This method is invoqued by the wizard's <tt>getLayer()</tt> method to
	 * create an ArcIMS raster layer.
	 * 
	 * @param host
	 *            server's URL
	 * @param service
	 *            service name
	 * @param sLayer
	 *            comma-separated list of selected layers
	 * @param name
	 *            new gvSIG layer's name
	 * @param srs
	 *            project's coordinate system
	 * @return the new layer (of class FRasterLyrArcIMS, which is a subclass of
	 *         _FLyrArcIMS which is a subclass of FLayer)
	 */
	public FRasterLyrArcIMS createArcImsRasterLayer(String host,
			String service, String sLayer, String name,
			CoordinateReferenceSystem crs, String imgFormat) throws Exception {
		FMapRasterArcImsDriver drv = new FMapRasterArcImsDriver(host, service,
				this.serviceType);

		if (!drv.connect(myCanc)) {
			throw new Exception();
		}

		FRasterLyrArcIMS flyr = new FRasterLyrArcIMS();
		flyr.setDriver(drv);
		flyr.setHost(new URL(host));
		flyr.setService(service);
		flyr.setServiceType(this.serviceType);
		flyr.setServiceInformationInStatus(drv.getClient()
				.getServiceInformation());

		ServiceInformation si = flyr.getArcimsStatus().getServiceInfo();

		if ((si.getFeaturecoordsys() == null)
				|| (si.getFeaturecoordsys().equals(""))) {
			missingSrs = true;
			si.setFeaturecoordsys(ProjectionUtils.getAbrev(crs)
					.substring(ServiceInfoTags.vINI_SRS.length()).trim());
			logger.warn("Server provides no SRS. ");
		} else {
			missingSrs = false;
		}

		flyr.setFullExtent(((ArcImsProtImageHandler) drv.getClient()
				.getHandler()).getServiceExtent(crs, flyr.getArcimsStatus()));

		// we *dont* use PNG always
		// flyr.setFormat("image/png");
		flyr.setFormat(imgFormat);
		flyr.setTransparency(0);
		flyr.setLayerQuery(sLayer);
		flyr.setCrs(crs);
		flyr.setName(name);

		// ----------- service info ---------------

		// flyr.getSource().getDriver();
		// ----------------- 1.0
		return flyr;
	}

	public String getTestInfo() {
		return testInfo;
	}

	/**
	 * This method is invoqued by the wizard's <tt>getLayer()</tt> method to
	 * create an ArcIMS feature layer.
	 * 
	 * @param host
	 *            server's URL
	 * @param service
	 *            service name
	 * @param sLayer
	 *            comma-separated list of selected layers
	 * @param name
	 *            new gvSIG layer's name
	 * @param crs
	 *            project's coordinate system
	 * @param sep
	 *            whether it'a grouped set of layers (false) or not (true)
	 * @return the new layer (of class FRasterLyrArcIMS, which is a subclass of
	 *         FRasterLyrArcIMS, which is a subclass of FLayer)
	 */
	public FFeatureLyrArcIMSCollection createArcImsFeatureLayer(String host,
			String service, String sLayer, String name,
			CoordinateReferenceSystem crs, boolean sep) throws Exception {
		String[] selectedLayerIds = sLayer.split(",");
		int count = selectedLayerIds.length;

		FFeatureLyrArcIMS[] individualLayers = new FFeatureLyrArcIMS[count];

		String item;

		for (int i = 0; i < count; i++) {
			item = selectedLayerIds[i];

			FMapFeatureArcImsDriver drv = new FMapFeatureArcImsDriver(host,
					service, item);

			if (!(drv.connect(myCanc))) {
				throw new Exception();
			}

			ArcImsVectorialAdapter oldadapter = new ArcImsVectorialAdapter(drv);
			ArcImsVectorialEditableAdapter adapter = new ArcImsVectorialEditableAdapter();

			/* 1 */individualLayers[i] = new FFeatureLyrArcIMS(adapter);

			/* 2 */drv.setLayer(individualLayers[i]);

			ServiceInformation si = drv.getClient().getServiceInformation();
			ServiceInformationLayerFeatures silf = (ServiceInformationLayerFeatures) si
					.getLayerById(item);
			String lyrname = silf.getName();

			individualLayers[i].setProjectionInStatus(ProjectionUtils
					.getAbrev(crs));
			individualLayers[i].setHostInStatus(new URL(host));
			individualLayers[i].setServiceInStatus(service);

			String units = si.getMapunits();
			int theDpi = si.getScreen_dpi();
			long scale;

			if (silf.getMaxscale() != -1) {
				scale = LayerScaleData
						.getTrueScaleFromRelativeScaleAndMapUnits(
								silf.getMaxscale(), units, theDpi);
				individualLayers[i].setMaxScale((double) scale);
			}

			if (silf.getMinscale() != -1) {
				scale = LayerScaleData
						.getTrueScaleFromRelativeScaleAndMapUnits(
								silf.getMinscale(), units, theDpi);
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
			individualLayers[i].setCrs(crs);
			individualLayers[i].setName(lyrname);

			Rectangle2D fext = ((ArcImsFeatureClient) drv.getClient())
					.getLayerExtent(individualLayers[i].getArcimsStatus());
			drv.setFullExtent(fext);

			// individualLayers[i].setF. setFullExtent(((ArcImsProtImageHandler)
			// drv.getClient().getHandler()).getServiceExtent(srs,
			// individualLayers[i].getArcimsStatus()));

			// ------ -------------
			drv.setAdapter(adapter);

			// adapter.setRecordSet(drv.getRecordSet());
			adapter.setOriginalDataSource(drv.getRecordSet());
			adapter.setOriginalVectorialAdapter(oldadapter);
			drv.declareTable(individualLayers[i]);
			individualLayers[i].setInitialLegend();
			individualLayers[i].setShapeType(adapter.getShapeType());
			individualLayers[i].setRecordset(drv.getRecordSet());

			// ------ -------------
			if ((si.getFeaturecoordsys() == null)
					|| (si.getFeaturecoordsys().equals(""))) {
				missingSrs = true;
				si.setFeaturecoordsys(ProjectionUtils.getAbrev(crs)
						.substring(ServiceInfoTags.vINI_SRS.length()).trim());
				logger.warn("Server provides no SRS. ");
			} else {
				missingSrs = false;
			}
		}

		FFeatureLyrArcIMSCollection collection = new FFeatureLyrArcIMSCollection(
				mapControl.getMapContext(), null, sep);
		collection.setName(name);
		collection.setCrs(crs);

		for (int i = 0; i < count; i++) {
			collection.addLayer(individualLayers[i]);
		}

		return collection;
	}

	/**
	 * Tells whether the SRS was missing on the server.
	 * 
	 * @return <tt>true</tt> if and only if the Srs was not provided by the
	 *         server.
	 */
	public boolean isMissingSrs() {
		return missingSrs;
	}

	public void setMissingSrs(boolean missingSrs) {
		this.missingSrs = missingSrs;
	}

	public void setMapControl(MapControl mapControl) {
		this.mapControl = mapControl;
	}
}
