/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
package com.iver.cit.gvsig.gui.toolListeners;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.apache.log4j.Logger;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrWCS;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;

/**
 * <p>
 * Listener that executes a <i>zoom in</i> operation in the associated
 * <code>MapControl</code>, having as center the point selected.
 * </p>
 * 
 * <p>
 * Listens a single click of any mouse's button.
 * </p>
 * 
 * <p>
 * The <i>zoom in</i> factor will depend on the maximum resolution of the
 * <i>WCS</i> layers in the <code>MapControl</code>.
 * </p>
 * 
 * @author Nacho Brodin (brodin_ign@gva.es)
 */
public class WCSZoomPixelCursorListener implements PointListener {
	/**
	 * Object used to log messages for this listener.
	 */
	private static Logger logger = Logger
			.getLogger(WCSZoomPixelCursorListener.class.getName());

	/**
	 * The image to display when the cursor is active.
	 */
	private final Image img = PluginServices.getIconTheme()
			.get("view-previsualize-area").getImage();

	/**
	 * The cursor used to work with this tool listener.
	 * 
	 * @see #getCursor()
	 */
	private Cursor cur = Toolkit.getDefaultToolkit().createCustomCursor(img,

	/**
	 * Reference to the <code>MapControl</code> object that uses.
	 */
	new Point(16, 16), "");
	private MapControl mapCtrl;

	/**
	 * <p>
	 * Creates a new <code>WCSZoomPixelCursorListener</code> object.
	 * </p>
	 * 
	 * @param mc
	 *            the <code>MapControl</code> where be applied the changes
	 */
	public WCSZoomPixelCursorListener(MapControl mc) {
		this.mapCtrl = mc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.tools.Listeners.PointListener#point(com.iver.
	 * cit.gvsig.fmap.tools.Events.PointEvent)
	 */
	public void point(PointEvent event) throws BehaviorException {
		Point2D pReal = mapCtrl.getMapContext().getViewPort()
				.toMapPoint(event.getPoint());
		Point imagePoint = new Point((int) event.getPoint().getX(), (int) event
				.getPoint().getY());
		ViewPort v = mapCtrl.getMapContext().getViewPort();

		FLayer[] actives = mapCtrl.getMapContext().getLayers().getActives();
		Rectangle2D ext = null;
		try {
			ext = actives[0].getFullExtent();
		} catch (ExpansionFileReadException e) {
			throw new BehaviorException(e.getMessage());
		} catch (ReadDriverException e) {
			throw new BehaviorException(e.getMessage());
		}

		if (ext != null) {

			double w2 = v.getImageWidth() / 2D;
			double h2 = v.getImageHeight() / 2D;
			double wcOriginX = pReal.getX();
			double wcOriginY = pReal.getY();

			Point2D maxRes = ((FLyrWCS) actives[0]).getMaxResolution();
			double wcDstMinX = wcOriginX - w2 * maxRes.getX();
			double wcDstMinY = wcOriginY - h2 * maxRes.getY();
			double wcDstWidth = w2 * maxRes.getX() * 2D;
			double wcDstHeight = h2 * maxRes.getY() * 2D;

			ext = new Rectangle2D.Double(wcDstMinX, wcDstMinY, wcDstWidth,
					wcDstHeight);
			mapCtrl.getMapContext().getViewPort().setExtent(ext);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#getCursor()
	 */
	public Cursor getCursor() {
		return cur;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#cancelDrawing()
	 */
	public boolean cancelDrawing() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.tools.Listeners.PointListener#pointDoubleClick
	 * (com.iver.cit.gvsig.fmap.tools.Events.PointEvent)
	 */
	public void pointDoubleClick(PointEvent event) throws BehaviorException {
		// TODO Auto-generated method stub
	}
}