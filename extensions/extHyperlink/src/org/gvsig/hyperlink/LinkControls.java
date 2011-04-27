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

import java.util.Map;

import org.apache.log4j.Logger;
import org.gvsig.hyperlink.actions.ImgFormat;
import org.gvsig.hyperlink.actions.LoadRasterLayer;
import org.gvsig.hyperlink.actions.LoadVectorLayer;
import org.gvsig.hyperlink.actions.PdfFormat;
import org.gvsig.hyperlink.actions.SvgFormat;
import org.gvsig.hyperlink.actions.TxtFormat;
import org.gvsig.hyperlink.config.LayerLinkConfig;
import org.gvsig.hyperlink.config.gui.ConfigTab;
import org.gvsig.hyperlink.layers.ManagerRegistry;
import org.gvsig.hyperlink.layers.VectLayerManager;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayersIterator;
import com.iver.cit.gvsig.fmap.tools.Behavior.PointBehavior;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.project.documents.view.legend.gui.General;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ThemeManagerWindow;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;


/**
 * Extensión para gestionar los hiperlinks.
 *
 * @author Cesar Martinez Izquierdo
 * @author Vicente Caballero Navarro
 */
public class LinkControls extends Extension {
	private static Logger logger = Logger.getLogger(LinkControls.class.getName());
	ManagerRegistry layerManager;
	public static final String LAYERPROPERTYNAME = "org.gvsig.hyperlink.config";
	public static final String TOOLNAME = "org.gvsig.hyperlink.tool";
	public static final String ACTIONSEXTENSIONPOINT = "HyperLinkAction";
	
	private static final int LEGACY_IMAGE_TYPE = 0;
	private static final int LEGACY_HTML_TYPE = 1;
	private static final int LEGACY_PDF_TYPE = 2;
	private static final int LEGACY_SVG_TYPE = 3;

	/*
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String s) {
		View view = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mapCtrl = view.getMapControl();
		logger.debug("Comand : " + s);

		if (s.compareTo("HYPERLINK") == 0) {
			initTool(view);
			mapCtrl.setTool(TOOLNAME);
		}
	}

	/**
	 * Inits the tool for the provided view.
	 * @param view
	 */
	public void initTool(View view) {
		MapControl mapCtrl = view.getMapControl();
		if (view.getMapControl().getNamesMapTools().get(TOOLNAME) == null) {
			mapCtrl.addMapTool(TOOLNAME, new PointBehavior(new LinkListener(mapCtrl, layerManager)));
			loadLegacyConfig(view);
		}
	}

	public void loadLegacyConfig(View view) {
		LayersIterator iterator = new LayersIterator(view.getMapControl().getMapContext().getLayers());
		while (iterator.hasNext()) {
			FLayer layer = iterator.nextLayer();
			loadLegacyConfig(layer);
		}	
	}

	/**
	 * Returns a LayerLinkConfig object if an old-style or new-style hyperlink was found and configured,
	 * or null otherwise.
	 * @param layer
	 */
	public LayerLinkConfig loadLegacyConfig(FLayer layer) {
		LayerLinkConfig layerConfig = (LayerLinkConfig) layer.getProperty(LinkControls.LAYERPROPERTYNAME);
		if (layerConfig!=null) { // don't apply compatibility if the layer already has new 1.9.0 configuration
			return layerConfig;
		}
		Object fName = layer.getProperty("legacy.hyperlink.selectedField");
		if (fName!=null && fName instanceof String) {
			Map properties = layer.getExtendedProperties();
			properties.remove("legacy.hyperlink.selectedField"); //remove it from layer to don't keep legacy properties for ever in the project
			String fieldName = (String) fName;
			Object extObj = layer.getProperty("legacy.hyperlink.extension");
			String extension = null;
			if (extObj!=null && extObj instanceof String) {
				properties.remove("legacy.hyperlink.extension"); //remove it from layer to don't keep legacy properties for ever in the project
				extension = (String) extObj;
			}
			Object typeObj = layer.getProperty("legacy.hyperlink.type");
			int type = -1;
			if (typeObj!=null && typeObj instanceof Integer) {
				properties.remove("legacy.hyperlink.type"); //remove it from layer to don't keep legacy properties for ever in the project
				type = ((Integer) typeObj).intValue();
			}
			LayerLinkConfig config = new LayerLinkConfig();
			config.setEnabled(true);
			config.addLink(getLegacyActionCode(type), fieldName, extension);
			layer.setProperty(LinkControls.LAYERPROPERTYNAME, config);
			return config;
		}
		return null;
	}

	private String getLegacyActionCode(int type) {
		switch (type) {
		case LEGACY_IMAGE_TYPE:
			return ImgFormat.actionCode;
		case LEGACY_PDF_TYPE:
			return PdfFormat.actionCode;
		case LEGACY_SVG_TYPE:
			return SvgFormat.actionCode;
		case LEGACY_HTML_TYPE:
		default:
			return TxtFormat.actionCode;
		}
	}

	/*
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

	/*
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		View f = (View) PluginServices.getMDIManager().getActiveWindow();

		if (f == null) {
			return false;
		}

		if (f instanceof com.iver.cit.gvsig.project.documents.view.gui.BaseView) {
			com.iver.cit.gvsig.project.documents.view.gui.BaseView view = (com.iver.cit.gvsig.project.documents.view.gui.BaseView) f;
			IProjectView model = view.getModel();

			if (model != null &&
					model.getMapContext()!=null &&
					model.getMapContext().getLayers().getVisibles().length>0)
				return true;
			else
				return false;

		}
		return true;
	}

	/*
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		registerLayers();
		registerActions();
		registerConfigPanel();
	}

	private void registerLayers() {
		layerManager = new ManagerRegistry();
		layerManager.put(FLyrVect.class, VectLayerManager.class);
	}

	public ManagerRegistry getLayerManager() {
		return layerManager;
	}

	private void registerActions(){
		ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
		ILinkActionManager tmpAction = new TxtFormat();
		extensionPoints.add(ACTIONSEXTENSIONPOINT, tmpAction.getActionCode(), tmpAction);
		tmpAction = new ImgFormat();
		extensionPoints.add(ACTIONSEXTENSIONPOINT, tmpAction.getActionCode(), tmpAction);
		tmpAction = new PdfFormat();
		extensionPoints.add(ACTIONSEXTENSIONPOINT, tmpAction.getActionCode(), tmpAction);
		tmpAction = new LoadRasterLayer();
		extensionPoints.add(ACTIONSEXTENSIONPOINT, tmpAction.getActionCode(), tmpAction);
		tmpAction = new LoadVectorLayer();
		extensionPoints.add(ACTIONSEXTENSIONPOINT, tmpAction.getActionCode(), tmpAction);
		tmpAction = new SvgFormat();
		extensionPoints.add(ACTIONSEXTENSIONPOINT, tmpAction.getActionCode(), tmpAction);
	}

	private void registerConfigPanel() {
		// pages
		ThemeManagerWindow.addPage(ConfigTab.class);

		ThemeManagerWindow.setTabEnabledForLayer(ConfigTab.class, FLyrVect.class, true);
	}
}
