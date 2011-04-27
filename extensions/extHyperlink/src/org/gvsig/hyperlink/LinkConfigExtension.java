/*
 * Created on 01-jun-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
package org.gvsig.hyperlink;

import org.apache.log4j.Logger;
import org.gvsig.hyperlink.config.gui.ConfigTab;
import org.gvsig.hyperlink.layers.ManagerRegistry;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.plugins.PluginClassLoader;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;


/**
 * Extensión para gestionar los hiperlinks.
 *
 * @author Vicente Caballero Navarro
 */
public class LinkConfigExtension extends Extension {
	private static Logger logger = Logger.getLogger(LinkConfigExtension.class.getName());
	ManagerRegistry layerManager;

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String s) {
		logger.debug("Command : " + s);

		if (s.compareTo("LINK_SETTINGS")==0) {
			View view = (View) PluginServices.getMDIManager().getActiveWindow();
			LinkControls ext = (LinkControls) PluginServices.getExtension(LinkControls.class);
			// init tool and load legacy config in case it has been not done
			ext.initTool(view);
			FLayer[] activas = view.getMapControl().getMapContext().getLayers().getActives();
			for (int i = 0; i < activas.length; i++) {
				if (!activas[i].isAvailable()) {
					return;
				}

				if (layerManager.hasManager(activas[i])) {
					ConfigTab configWindow = new ConfigTab();
					configWindow.setModel(activas[i]);
					PluginServices.getMDIManager().addCentredWindow(configWindow);
				}
			}
		
		}
	}

	/**
	 * @see com.iver.mdiApp.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
															 .getActiveWindow();

		if (f == null) {
			return false;
		}

		if (f instanceof View) {

		  MapContext mapa = ((View) f).getModel().getMapContext();

		  return mapa.getLayers().getLayersCount() > 0;
		} else {
			return false;
		}
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		// it will be enabled when there is only ONE active layer, and this layer
		// is available and has a valid ILayerLinkManager
		View f = (View) PluginServices.getMDIManager().getActiveWindow();

		if (f == null) {
			return false;
		}

		if (f instanceof com.iver.cit.gvsig.project.documents.view.gui.BaseView) {
			com.iver.cit.gvsig.project.documents.view.gui.BaseView view = (com.iver.cit.gvsig.project.documents.view.gui.BaseView) f;
			IProjectView model = view.getModel();

			FLayer[] activas = model.getMapContext().getLayers().getActives();

			if(activas.length==1) {
				if (activas[0].isAvailable()
						&& layerManager.hasManager(activas[0])) {
					return true;
				}
			}
		}
		return false;
	}

	public void initialize() {
		LinkControls ext = (LinkControls) PluginServices.getExtension(LinkControls.class);
		layerManager = ext.getLayerManager();
	}

}
