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
package org.gvsig.rastertools.selectrasterlayer;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JScrollBar;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;

/**
 * Extensión de la clase SelectImageListenerImple de FMap. Esta clase permite
 * capturar el evento de la selección de un punto sobre la vista . Controlara
 * que capa de la pila que esté visible cae dentro del punto seleccionado
 * poniendo esta capa como activa. En caso de que haya varias capas visibles
 * sobre ese punto, pondrá como activa la capa superior.
 * 
 * @author Nacho Brodin (nachobrodin@gmail.com)
 */
public class SelectImageListener extends SelectImageListImpl {

	Rectangle2D extentLayer = null;

	/**
	 * Contructor
	 * 
	 * @param mapCtrl
	 */
	public SelectImageListener(MapControl mapCtrl) {
		super(mapCtrl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.tools.Listeners.PointListener#point(com.iver.
	 * cit.gvsig.fmap.tools.Events.PointEvent)
	 */
	public void point(PointEvent event) {
		super.point(event);

		Point2D pointSelect = event.getPoint();

		if (PluginServices.getMainFrame() != null)
			PluginServices.getMainFrame().enableControls();

		ViewPort vp = mapCtrl.getMapContext().getViewPort();

		wcPoint = vp.toMapPoint((int) pointSelect.getX(),
				(int) pointSelect.getY());

		FLayers layers = mapCtrl.getMapContext().getLayers();
		for (int i = 0; i < layers.getLayersCount(); i++)
			layers.getLayer(i).setActive(false);

		for (int i = layers.getLayersCount() - 1; i >= 0; i--) {
			if (select(layers.getLayer(i), i))
				break;
		}
	}

	private boolean select(FLayer layer, int pos) {
		if (layer instanceof FLayers) {
			FLayers laux = (FLayers) layer;
			for (int j = laux.getLayersCount() - 1; j >= 0; j--) {
				if (select(laux.getLayer(j), j)) {
					return true;
				}
			}
		} else {
			try {
				extentLayer = layer.getFullExtent();
			} catch (ReadDriverException exc) {
				NotificationManager.addError("Error al obtener el extent", exc);
			}

			if (extentLayer.getMaxX() >= wcPoint.getX()
					&& extentLayer.getMinX() <= wcPoint.getX()
					&& extentLayer.getMaxY() >= wcPoint.getY()
					&& extentLayer.getMinY() <= wcPoint.getY()
					&& layer.isVisible() && layer instanceof FLyrRasterSE) {
				layer.setActive(true);
				BaseView view = (BaseView) PluginServices.getMDIManager()
						.getActiveWindow();
				JScrollBar verticalBar = view.getTOC().getJScrollPane()
						.getVerticalScrollBar();
				double widthPerEntry = verticalBar.getMaximum()
						/ layer.getMapContext().getLayers().getLayersCount();
				verticalBar.setValue((int) widthPerEntry
						* (layer.getMapContext().getLayers().getLayersCount()
								- pos - 1));
				return true;
			}
		}
		return false;
	}
}