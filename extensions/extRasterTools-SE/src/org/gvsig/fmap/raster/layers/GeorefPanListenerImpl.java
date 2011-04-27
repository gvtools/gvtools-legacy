/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 IVER T.I. and Generalitat Valenciana.
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
package org.gvsig.fmap.raster.layers;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.tools.Events.MoveEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PanListener;


/**
 * Implementación de la interfaz PanListener como herramienta para realizar el
 * Pan.
 *
 * @author Nacho Brodin (nachobrodin@gmail.com)
 */
public class GeorefPanListenerImpl implements PanListener {
//	private final Image ipan = new ImageIcon(MapControl.class.getResource(
//				"images/CruxCursor.png")).getImage();
	private final Image ipan = PluginServices.getIconTheme().get("crux-cursor").getImage();
	private Cursor cur = Toolkit.getDefaultToolkit().createCustomCursor(ipan,
			new Point(16, 16), "");
	private MapControl mapControl;
	private FLyrRasterSE lyrRaster = null;
	private String	pathToFile = null;


	/**
	 * Crea un nuevo RectangleListenerImpl.
	 *
	 * @param mapControl MapControl.
	 */
	public GeorefPanListenerImpl(MapControl mapControl) {
		this.mapControl = mapControl;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.PanListener#move(java.awt.geom.Point2D,
	 * 		java.awt.geom.Point2D)
	 */
	public void move(MoveEvent event) {
		ViewPort vp = mapControl.getMapContext().getViewPort();

		Point2D from = vp.toMapPoint(event.getFrom());
		Point2D to = vp.toMapPoint(event.getTo());

		Rectangle2D.Double r = new Rectangle2D.Double();
		Rectangle2D extent = vp.getExtent();
		r.x = extent.getX() - (to.getX() - from.getX());
		r.y = extent.getY() - (to.getY() - from.getY());
		r.width = extent.getWidth();
		r.height = extent.getHeight();
		vp.setExtent(r);


		// mapControl.drawMap();
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
		return true;
	}

	/**
	 * @return Returns the lyrRaster.
	 */
	public FLyrRasterSE getLyrRaster() {
		return lyrRaster;
	}

	/**
	 * @param lyrRaster The lyrRaster to set.
	 */
	public void setLyrRaster(FLyrRasterSE lyrRaster) {
		this.lyrRaster = lyrRaster;
	}

	/**
	 * @return Returns the pathToFile.
	 */
	public String getPathToFile() {
		return pathToFile;
	}

	/**
	 * @param pathToFile The pathToFile to set.
	 */
	public void setPathToFile(String pathToFile) {
		this.pathToFile = pathToFile;
	}
}
