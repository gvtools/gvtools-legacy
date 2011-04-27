package com.iver.cit.gvsig.geoprocess.impl.build;

import java.net.URL;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.GeoprocessPluginAbstract;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessController;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;
import com.iver.cit.gvsig.geoprocess.impl.build.gui.BuildGeoprocessPanel;
import com.iver.cit.gvsig.geoprocess.manager.GeoprocessManager;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class BuildGeoprocessPlugin extends GeoprocessPluginAbstract implements
		IGeoprocessPlugin {
	
	private static String topologyPkg;
	private static String geoprocessName;
	private static String topologyPkgDesc;
	
	static{
		topologyPkg = PluginServices.getText(null, "Topology");
		geoprocessName = PluginServices.getText(null, "Build");
		topologyPkgDesc = PluginServices.getText(null, "Topology_Desc");		
		GeoprocessManager.
		registerPackageDescription(topologyPkg, topologyPkgDesc);		
	}	
		

	public IGeoprocessUserEntries getGeoprocessPanel() {
		com.iver.andami.ui.mdiManager.IWindow view = PluginServices.getMDIManager().getActiveWindow();
		View vista = (View) view;
		FLayers layers = vista.getModel().getMapContext().getLayers();
		return (IGeoprocessUserEntries) new BuildGeoprocessPanel(layers);
	}

	public IGeoprocessController getGpController() {
		return new BuildGeoprocessController();
	}

	public URL getImgDescription() {
		URL url = PluginServices.getIconTheme().getURL("build-icon");
		return url;
	}

	public String getNamespace() {
		return topologyPkg + "/" + geoprocessName;
	}
	
	public String toString() {
		return geoprocessName;
	}

}
