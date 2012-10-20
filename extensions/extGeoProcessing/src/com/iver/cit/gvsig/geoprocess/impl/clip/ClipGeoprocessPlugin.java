/*
 * Created on 22-jun-2006
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
 * $Id: ClipGeoprocessPlugin.java 21232 2008-06-05 14:03:49Z azabala $
 * $Log$
 * Revision 1.8  2007-09-19 16:02:53  jaume
 * removed unnecessary imports
 *
 * Revision 1.7  2007/06/20 10:50:32  jmvivo
 * Modificación para estandarizar la busqueda de los html de descripciones.
 * También se controla que, si no existe la descripción en el idioma corriente se usará el inglés.
 *
 * Revision 1.6  2007/03/06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.5  2006/09/15 10:42:54  caballero
 * extensibilidad de documentos
 *
 * Revision 1.4  2006/08/29 07:56:30  cesar
 * Rename the *View* family of classes to *Window* (ie: SingletonView to SingletonWindow, ViewInfo to WindowInfo, etc)
 *
 * Revision 1.3  2006/08/11 16:19:01  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/06/27 16:11:41  azabala
 * toString() added to Plugin interface to force textual representation of geoprocess plugins
 *
 * Revision 1.1  2006/06/23 19:02:35  azabala
 * first version in cvs
 *
 * Revision 1.1  2006/06/22 17:46:30  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.clip;

import java.net.URL;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.GeoprocessPluginAbstract;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessController;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.core.gui.GeoProcessingOverlayPanel2;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;
import com.iver.cit.gvsig.geoprocess.manager.GeoprocessManager;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class ClipGeoprocessPlugin extends GeoprocessPluginAbstract implements
		IGeoprocessPlugin {
	private static String analisisPkg;
	private static String overlayPkg;
	private static String overlayPkgDesc;
	private static String geoprocessName;

	static {
		analisisPkg = PluginServices.getText(null, "Analisis");
		overlayPkg = PluginServices.getText(null, "Overlay");
		overlayPkgDesc = PluginServices.getText(null, "Overlay_Desc");
		geoprocessName = PluginServices.getText(null, "Recortar");

		// GeoprocessManager.registerPackageDescription(
		// "Analisis/Overlay",
		// "Geoprocesos que extraen "+
		// "información basandose en "+
		// "la superposición de dos capas");
		GeoprocessManager.registerPackageDescription(analisisPkg + "/"
				+ overlayPkg, overlayPkgDesc);

	}

	public IGeoprocessUserEntries getGeoprocessPanel() {
		View vista = (View) PluginServices.getMDIManager().getActiveWindow();
		FLayers layers = vista.getModel().getMapContext().getLayers();
		String titleText = PluginServices.getText(this,
				"Recortar._Introduccion_de_datos") + ":";
		return new GeoProcessingOverlayPanel2(layers, titleText);
	}

	public URL getImgDescription() {
		return PluginServices.getIconTheme().getURL("clipdesc-resource");
	}

	public IGeoprocessController getGpController() {
		return new ClipGeoprocessController();
	}

	public String getNamespace() {
		return analisisPkg + "/" + overlayPkg + "/" + geoprocessName;
	}

	public String toString() {
		return geoprocessName;
	}

}
