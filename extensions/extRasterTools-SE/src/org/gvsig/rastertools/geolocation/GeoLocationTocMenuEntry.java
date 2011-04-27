/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2007 IVER T.I. and Generalitat Valenciana.
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
 */
package org.gvsig.rastertools.geolocation;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.fmap.raster.layers.IRasterLayerActions;
import org.gvsig.raster.gui.IGenericToolBarMenuItem;
import org.gvsig.raster.util.RasterToolsUtil;
import org.gvsig.rastertools.geolocation.behavior.GeoRasterBehavior;
import org.gvsig.rastertools.geolocation.listener.GeorefPanListener;
import org.gvsig.rastertools.geolocation.ui.GeoLocationDialog;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.tools.CompoundBehavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.MouseMovementBehavior;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;
import com.iver.cit.gvsig.project.documents.view.toc.AbstractTocContextMenuAction;
import com.iver.cit.gvsig.project.documents.view.toc.ITocItem;
import com.iver.cit.gvsig.project.documents.view.toolListeners.StatusBarListener;

/**
 * Herramienta del menú contextual que carga el raster en el localizador para tener una visión general de
 * esta y carga el zoom del cursor para tener una selección de precisión.
 *
 * 16-jun-2007
 * @author Nacho Brodin (nachobrodin@gmail.com)
 */
public class GeoLocationTocMenuEntry extends AbstractTocContextMenuAction implements PropertyChangeListener, IGenericToolBarMenuItem {
	static private GeoLocationTocMenuEntry singleton  = null;
	private GeoRasterBehavior mb = null;

	/**
	 * Nadie puede crear una instancia a esta clase única, hay que usar el
	 * getSingleton()
	 */
	private GeoLocationTocMenuEntry() {}

	/**
	 * Devuelve un objeto unico a dicha clase
	 * @return
	 */
	static public GeoLocationTocMenuEntry getSingleton() {
		if (singleton == null)
			singleton = new GeoLocationTocMenuEntry();
		return singleton;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.project.documents.contextMenu.AbstractContextMenuAction#getGroup()
	 */
	public String getGroup() {
		return "GeoRaster";
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.project.documents.contextMenu.AbstractContextMenuAction#getGroupOrder()
	 */
	public int getGroupOrder() {
		return 55;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.project.documents.contextMenu.AbstractContextMenuAction#getOrder()
	 */
	public int getOrder() {
		return 4;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.project.documents.IContextMenuAction#getText()
	 */
	public String getText() {
		return RasterToolsUtil.getText(this, "geolocation");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.project.documents.view.toc.AbstractTocContextMenuAction#isEnabled(com.iver.cit.gvsig.project.documents.view.toc.ITocItem, com.iver.cit.gvsig.fmap.layers.FLayer[])
	 */
	public boolean isEnabled(ITocItem item, FLayer[] selectedItems) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.project.documents.view.toc.AbstractTocContextMenuAction#isVisible(com.iver.cit.gvsig.project.documents.view.toc.ITocItem, com.iver.cit.gvsig.fmap.layers.FLayer[])
	 */
	public boolean isVisible(ITocItem item, FLayer[] selectedItems) {
		if ((selectedItems == null) || (selectedItems.length != 1))
			return false;

		if (!(selectedItems[0] instanceof FLyrRasterSE))
			return false;

		return ((FLyrRasterSE) selectedItems[0]).isActionEnabled(IRasterLayerActions.GEOLOCATION);
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.project.documents.view.toc.AbstractTocContextMenuAction#execute(com.iver.cit.gvsig.project.documents.view.toc.ITocItem, com.iver.cit.gvsig.fmap.layers.FLayer[])
	 */
	public void execute(ITocItem item, FLayer[] selectedItems) {
		if (selectedItems == null || selectedItems.length != 1 || !(selectedItems[0] instanceof FLyrRasterSE)) {
			RasterToolsUtil.messageBoxError(PluginServices.getText(this,"layers_not_selected"), null);
			return;
		}
		
		BaseView theView = null;
		GeoLocationDialog dialog = null;
		
		IWindow[] win = PluginServices.getMDIManager().getAllWindows();
		for (int i = 0; i < win.length; i++) {
			if (win[i] instanceof BaseView) {
				FLayers lyrs = ((BaseView) win[i]).getMapControl().getMapContext().getLayers();
				for (int j = 0; j < lyrs.getLayersCount(); j++) 
					if(lyrs.getLayer(j).equals(selectedItems[0])) 
						theView = (BaseView)win[i];
			}
			if (win[i] instanceof GeoLocationDialog)
				RasterToolsUtil.closeWindow(win[i]);
		}
		
		if(theView == null) {
			RasterToolsUtil.messageBoxError(PluginServices.getText(this,"view_not_found"), null);
			return;
		}
		
		MapControl mapCtrl = theView.getMapControl();
		
		// Listener de eventos de movimiento que pone las coordenadas del ratón en
		// la barra de estado
		StatusBarListener sbl = new StatusBarListener(mapCtrl);
		
		FLyrRasterSE lyr = (FLyrRasterSE)selectedItems[0];
		dialog = new GeoLocationDialog(lyr, mapCtrl.getViewPort(), theView);
		Point posit = RasterToolsUtil.iwindowPosition((int)dialog.getSizeWindow().getWidth(), (int)dialog.getSizeWindow().getHeight());
		dialog.setPosition((int)posit.getX(), (int)posit.getY());
		RasterToolsUtil.addWindow(dialog);
		
		dialog.init(mapCtrl);
		loadGeoPanListener(mapCtrl, sbl, dialog, lyr);
		mapCtrl.setTool("geoPan");
	}

	/**
	 * Carga el listener de selección de raster en el MapControl.
	 */
	private void loadGeoPanListener(MapControl mapCtrl, StatusBarListener sbl, GeoLocationDialog gld, FLyrRasterSE lyr) {
		if (mapCtrl.getNamesMapTools().get("geoPan") == null) {
			GeorefPanListener pl = new GeorefPanListener(mapCtrl);
			mb = new GeoRasterBehavior(pl, gld, lyr);
			mapCtrl.addMapTool("geoPan", new Behavior[]{mb, new MouseMovementBehavior(sbl)});
		} else {
			Behavior b = mapCtrl.getMapTool("geoPan");
			if(	b instanceof CompoundBehavior &&
				((CompoundBehavior)b).getBehavior(0) instanceof GeoRasterBehavior) {
				GeoRasterBehavior beh = (GeoRasterBehavior)((CompoundBehavior)b).getBehavior(0);
				beh.setLayer(lyr);
				beh.setITransformIO(gld);	
			}				
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gvsig.rastertools.generictoolbar.IGenericToolBarMenuItem#getIcon()
	 */
	public Icon getIcon() {
		return RasterToolsUtil.getIcon("geolocalization-icon");
	}

	public void propertyChange(PropertyChangeEvent evt) {}
}