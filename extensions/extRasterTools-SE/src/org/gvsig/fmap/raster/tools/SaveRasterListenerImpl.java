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
package org.gvsig.fmap.raster.tools;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.tools.Events.RectangleEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.RectangleListener;

/**
 * Implementación de la interfaz RectangleListener como herramienta para
 * realizar un Salvado a Raster.
 * 
 * @author Nacho Brodin (nachobrodin@gmail.com)
 */
public class SaveRasterListenerImpl implements RectangleListener {
	private final Image isaveraster = PluginServices.getIconTheme()
			.get("rect-select-cursor").getImage();
	private Cursor cur = Toolkit.getDefaultToolkit().createCustomCursor(
			isaveraster, new Point(16, 16), "");
	protected MapControl mapCtrl;

	protected Rectangle2D pixelRect = null;
	protected Rectangle2D rect = null;

	/**
	 * Crea un nuevo RectangleListenerImpl.
	 * 
	 * @param mapCtrl
	 *            MapControl.
	 */
	public SaveRasterListenerImpl(MapControl mapCtrl) {
		this.mapCtrl = mapCtrl;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.RectangleListener#rectangle(com.iver.cit.gvsig.fmap.tools.Events.RectangleEvent)
	 */
	public void rectangle(RectangleEvent event) {
		rect = event.getWorldCoordRect();
		pixelRect = event.getPixelCoordRect();
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#getCursor()
	 */
	public Cursor getCursor() {
		return cur;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#cancelDrawing()
	 */
	public boolean cancelDrawing() {
		System.out.println("cancelDrawing del SaveRasterListenerImpl");
		return true;
	}
}
