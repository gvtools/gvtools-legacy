package com.iver.cit.gvsig.geoprocess.impl.clean;

import java.net.URL;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.GeoprocessPluginAbstract;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessController;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;
import com.iver.cit.gvsig.geoprocess.impl.clean.gui.CleanGeoprocessPanel;
import com.iver.cit.gvsig.geoprocess.manager.GeoprocessManager;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class CleanGeoprocessPlugin extends GeoprocessPluginAbstract implements
		IGeoprocessPlugin {

	private static String topologyPkg;
	private static String geoprocessName;
	private static String topologyPkgDesc;

	static {
		topologyPkg = PluginServices.getText(null, "Topology");
		geoprocessName = PluginServices.getText(null, "LineClean");
		topologyPkgDesc = PluginServices.getText(null, "Topology_Desc");
		GeoprocessManager.registerPackageDescription(topologyPkg,
				topologyPkgDesc);
	}

	public IGeoprocessUserEntries getGeoprocessPanel() {
		com.iver.andami.ui.mdiManager.IWindow view = PluginServices
				.getMDIManager().getActiveWindow();
		View vista = (View) view;
		FLayers layers = vista.getModel().getMapContext().getLayers();
		return (IGeoprocessUserEntries) new CleanGeoprocessPanel(layers);
	}

	public IGeoprocessController getGpController() {
		return new CleanGeoprocessController();
	}

	public URL getImgDescription() {
		URL url = PluginServices.getIconTheme().getURL("clean-icon");
		return url;
	}

	public String getNamespace() {
		return topologyPkg + "/" + geoprocessName;
	}

	public String toString() {
		return geoprocessName;
	}

}
