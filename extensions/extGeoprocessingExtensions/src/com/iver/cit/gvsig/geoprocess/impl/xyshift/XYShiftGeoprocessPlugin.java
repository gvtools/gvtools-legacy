/*
 * Created on 28-jun-2006
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
 * $Id: XYShiftGeoprocessPlugin.java 21235 2008-06-05 14:08:38Z azabala $
 * $Log$
 * Revision 1.7  2007-09-19 16:09:14  jaume
 * removed unnecessary imports
 *
 * Revision 1.6  2007/06/20 10:50:31  jmvivo
 * Modificación para estandarizar la busqueda de los html de descripciones.
 * También se controla que, si no existe la descripción en el idioma corriente se usará el inglés.
 *
 * Revision 1.5  2006/09/21 18:14:42  azabala
 * changes of appGvSig packages (document extensibility)
 *
 * Revision 1.4  2006/08/29 08:46:36  cesar
 * Rename the remaining method calls (extGeoprocessingExtensions was not in my workspace)
 *
 * Revision 1.3  2006/08/11 17:17:55  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/06/29 17:58:31  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/06/28 18:17:21  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.xyshift;

import java.net.URL;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.GeoprocessPluginAbstract;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessController;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;
import com.iver.cit.gvsig.geoprocess.impl.xyshift.gui.GeoprocessingXYShiftPanel2;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class XYShiftGeoprocessPlugin extends GeoprocessPluginAbstract implements
		IGeoprocessPlugin {

	private static String dataConvertPkg;
	private static String geoprocessName;

	static {
		dataConvertPkg = PluginServices.getText(null, "Conversion_de_datos");
		geoprocessName = PluginServices.getText(null, "XYShift");
	}

	public IGeoprocessUserEntries getGeoprocessPanel() {
		View vista = (View) PluginServices.getMDIManager().getActiveWindow();
		FLayers layers = vista.getModel().getMapContext().getLayers();

		return new GeoprocessingXYShiftPanel2(layers);
	}

	public URL getImgDescription() {
		URL url = PluginServices.getIconTheme().getURL("xyshiftdesc-icon");
		return url;
	}

	public IGeoprocessController getGpController() {
		return new XYShiftGeoprocessController();
	}

	public String getNamespace() {
		return dataConvertPkg + "/" + geoprocessName;
	}

	public String toString() {
		return geoprocessName;
	}

}
