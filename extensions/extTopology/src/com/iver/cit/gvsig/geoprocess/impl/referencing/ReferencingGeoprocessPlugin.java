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
package com.iver.cit.gvsig.geoprocess.impl.referencing;

import java.net.URL;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.GeoprocessPluginAbstract;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessController;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;
import com.iver.cit.gvsig.geoprocess.impl.referencing.gui.ReferencingGeoprocessPanel;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class ReferencingGeoprocessPlugin extends GeoprocessPluginAbstract {

	private static String dataConvertPkg;
	private static String geoprocessName;
	
	static{
		dataConvertPkg = 
			PluginServices.getText(null, "Conversion_de_datos");
		geoprocessName =
			PluginServices.getText(null, "Spatial_Adjust");
	}
	
	
	public IGeoprocessUserEntries getGeoprocessPanel() {
		com.iver.andami.ui.mdiManager.IWindow view = PluginServices.getMDIManager().getActiveWindow();
		View vista = (View) view;
		FLayers layers = vista.getModel().getMapContext().getLayers();
		return (IGeoprocessUserEntries) new ReferencingGeoprocessPanel(layers);
	}

	public URL getImgDescription() {
		URL url = PluginServices.getIconTheme().getURL("spatialadjustdesc-icon");
		return url;
	}

	public IGeoprocessController getGpController() {
		return new ReferencingGeoprocessController();
	}

	
	public String getNamespace() {
		return dataConvertPkg + "/" + geoprocessName;
	}
	
	public String toString(){
		return geoprocessName;
	}


}
