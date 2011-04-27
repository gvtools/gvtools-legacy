/*
 * Created on 23-jun-2006
 *
 * gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
* $Id: GeoprocessManagerExtension.java 24907 2008-11-10 21:36:24Z azabala $
* $Log$
* Revision 1.9  2006-09-20 13:40:47  caballero
* IProjectView
*
* Revision 1.8  2006/09/15 10:42:54  caballero
* extensibilidad de documentos
*
* Revision 1.7  2006/09/07 19:02:27  azabala
* cached Geoprocess Manager, to remember folder status
*
* Revision 1.6  2006/09/07 18:54:11  azabala
* added comments
*
* Revision 1.5  2006/08/29 07:56:30  cesar
* Rename the *View* family of classes to *Window* (ie: SingletonView to SingletonWindow, ViewInfo to WindowInfo, etc)
*
* Revision 1.4  2006/08/29 07:13:56  cesar
* Rename class com.iver.andami.ui.mdiManager.View to com.iver.andami.ui.mdiManager.IWindow
*
* Revision 1.3  2006/08/18 08:40:05  jmvivo
* Actualizado para que el isEnabled tenga en cuenta que las capas esten 'avialable'
*
* Revision 1.2  2006/06/27 16:12:38  azabala
* registration of all core geoprocesses (wizard's)
*
* Revision 1.1  2006/06/23 19:03:52  azabala
* first version in cvs
*
*
*/
package com.iver.cit.gvsig.geoprocess.manager;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.geoprocess.impl.buffer.BufferGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.clip.ClipGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.convexhull.ConvexHullGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.difference.DifferenceGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.dissolve.DissolveGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.intersection.IntersectionGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.merge.MergeGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.spatialjoin.SpatialJoinGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.union.UnionGeoprocessPlugin;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;
/**
 * This andami extension shows a GUI component (GeoprocessManager)
 * that allows user to launch geoprocesses, and developers to register
 * new geoprocesses in a dynamic linkage manner.
 * <br>
 * In its initialize() method it registers the basis geoprocesses
 * (buffer, clip, etc.)
 *
 * @author azabala
 *
 */
public class GeoprocessManagerExtension extends Extension {

	/**
	 * Cached instance of geoprocess manager
	 */
	private GeoprocessManager gpManagerDialog = null;
	/**
	 * Register basis geoprocesses during extension initialization
	 */
	public void initialize() {
		ExtensionPoints extensionPoints =
			ExtensionPointsSingleton.getInstance();
		extensionPoints.add("GeoprocessManager",
				"BUFFER",
				BufferGeoprocessPlugin.class);
		extensionPoints.add("GeoprocessManager",
				"CLIP",
				ClipGeoprocessPlugin.class);
		extensionPoints.add("GeoprocessManager",
				"CONVEX HULL",
				ConvexHullGeoprocessPlugin.class);
		extensionPoints.add("GeoprocessManager",
				"DIFFERENCE",
				DifferenceGeoprocessPlugin.class);
		extensionPoints.add("GeoprocessManager",
				"DISSOLVE",
				DissolveGeoprocessPlugin.class);
		extensionPoints.add("GeoprocessManager",
				"INTERSECTION",
				IntersectionGeoprocessPlugin.class);
		extensionPoints.add("GeoprocessManager",
				"MERGE",
				MergeGeoprocessPlugin.class);
		extensionPoints.add("GeoprocessManager",
				"SPATIAL JOIN",
				SpatialJoinGeoprocessPlugin.class);
		extensionPoints.add("GeoprocessManager",
				"UNION",
				UnionGeoprocessPlugin.class);
		
		registerIcons();

	}
	
	private void registerIcons(){
		PluginServices.getIconTheme().registerDefault(
				"gp-manager",
				this.getClass().getClassLoader().getResource("images/gpmanager.png")
			);
	}

	public void execute(String actionCommand) {
		if(actionCommand.equalsIgnoreCase("GEOPROCESSING_MANAGER")){
			if(gpManagerDialog == null)
				gpManagerDialog = new GeoprocessManager();
			PluginServices.getMDIManager().
				addWindow(gpManagerDialog);
		}

	}

	public boolean isEnabled() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
		.getActiveWindow();
		if (f == null) {
			return false;
		}
		if (f instanceof View) {
			View vista = (View) f;
			IProjectView model = vista.getModel();
			FLayers layers =  model.getMapContext().getLayers();
			 SingleLayerIterator iterator = 
			    	new SingleLayerIterator(layers);
		    while(iterator.hasNext()){
		    	FLayer layer = iterator.next();
		    	if(layer instanceof FLyrVect)
		    		return true;
		    }//while
		    return false;
		}
		return false;
	}

	public boolean isVisible() {
		//Meter en un geoprocessutil para todas las extensiones
		//de geoprocessing
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
		 .getActiveWindow();
		if (f == null) {
		    return false;
		}
		if (f instanceof View) {
		    View vista = (View) f;
		    IProjectView model = vista.getModel();
		    FLayers layers =  model.getMapContext().getLayers();
		    SingleLayerIterator iterator = 
		    	new SingleLayerIterator(layers);
		    while(iterator.hasNext()){
		    	FLayer layer = iterator.next();
		    	if(layer instanceof FLyrVect)
		    		return true;
		    }//while
		    return false;
		}//if
		return false;
	}

}

