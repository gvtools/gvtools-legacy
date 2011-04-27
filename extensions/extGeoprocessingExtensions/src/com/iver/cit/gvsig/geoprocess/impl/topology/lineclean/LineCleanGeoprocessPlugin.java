/*
 * Created on 10-oct-2006
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
* $Id: LineCleanGeoprocessPlugin.java 21235 2008-06-05 14:08:38Z azabala $
* $Log$
* Revision 1.1  2006-12-21 17:23:27  azabala
* *** empty log message ***
*
* Revision 1.1  2006/12/04 19:42:23  azabala
* *** empty log message ***
*
* Revision 1.2  2006/10/17 18:25:53  azabala
* *** empty log message ***
*
* Revision 1.1  2006/10/10 18:50:17  azabala
* First version in CVS
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.topology.lineclean;

import java.net.URL;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.GeoprocessPluginAbstract;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessController;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;
import com.iver.cit.gvsig.geoprocess.impl.topology.lineclean.gui.LineCleanGeoprocessPanel;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class LineCleanGeoprocessPlugin extends GeoprocessPluginAbstract implements IGeoprocessPlugin {

	private static String dataConvertPkg;
	private static String geoprocessName;

	static{
		dataConvertPkg =
			PluginServices.getText(null, "Conversion_de_datos");
		geoprocessName =
			PluginServices.getText(null, "LineClean");
	}



	public IGeoprocessUserEntries getGeoprocessPanel() {
		View vista = (View)PluginServices.
		getMDIManager().
		getActiveWindow();
		FLayers layers = vista.getModel().
			getMapContext().
			getLayers();

		return new LineCleanGeoprocessPanel(layers);
	}

	public URL getImgDescription() {
		URL url = PluginServices.getIconTheme().getURL("linecleandesc-icon");
		return url;
	}

	public IGeoprocessController getGpController() {
		return new LineCleanGeoprocessController();
	}

	public String getNamespace() {
		return dataConvertPkg + "/" + geoprocessName;
	}

	public String toString(){
		return geoprocessName;
	}

}

