/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
/* CVS MESSAGES:
 *
 * $Id: 
 * $Log: 
 */
package com.iver.cit.gvsig.geoprocess.impl.voronoi;

import java.net.URL;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.GeoprocessPluginAbstract;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessController;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;
import com.iver.cit.gvsig.geoprocess.impl.voronoi.gui.VoronoiGeoprocessPanel;
import com.iver.cit.gvsig.geoprocess.manager.GeoprocessManager;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class VoronoiGeoprocessPlugin extends GeoprocessPluginAbstract implements
		IGeoprocessPlugin {

	private static String analisisPkg;
	private static String geometriaComputacionalPkg;
	private static String geometriaCompPkgDesc;
	private static String geoprocessName;
	static {
		analisisPkg = PluginServices.getText(null, "Analisis");
		geometriaComputacionalPkg = PluginServices.getText(null, "GC");
		geometriaCompPkgDesc = PluginServices.getText(null, "GC_Desc");
		geoprocessName = PluginServices.getText(null, "Voronoi");

		GeoprocessManager.registerPackageDescription(analisisPkg + "/"
				+ geometriaComputacionalPkg, geometriaCompPkgDesc);
	}

	public IGeoprocessUserEntries getGeoprocessPanel() {
		View vista = (View) PluginServices.getMDIManager().getActiveWindow();
		FLayers layers = vista.getModel().getMapContext().getLayers();

		return new VoronoiGeoprocessPanel(layers);
	}

	public URL getImgDescription() {
		URL url = PluginServices.getIconTheme().getURL("voronoidesc-icon");
		return url;
	}

	public IGeoprocessController getGpController() {
		return new VoronoiGeoprocessController();
	}

	public String getNamespace() {
		return analisisPkg + "/" + geometriaComputacionalPkg + "/"
				+ geoprocessName;
	}

	public String toString() {
		return geoprocessName;
	}

}
