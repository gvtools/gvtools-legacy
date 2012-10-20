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
package com.iver.cit.gvsig.fmap.tools;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;

/**
 * <p>
 * Listener that selects all features of the active, and vector layers of the
 * associated <code>MapControl</code> that their area intersects with the point
 * selected by a single click of any button of the mouse.
 * </p>
 * 
 * @author Vicente Caballero Navarro
 */
public class PointSelectionListener implements PointListener {
	/**
	 * The image to display when the cursor is active.
	 */
	private final Image img = new ImageIcon(
			MapControl.class.getResource("images/PointSelectCursor.gif"))
			.getImage();

	/**
	 * The cursor used to work with this tool listener.
	 * 
	 * @see #getCursor()
	 */
	private Cursor cur = Toolkit.getDefaultToolkit().createCustomCursor(img,
			new Point(16, 16), "");

	/**
	 * Reference to the <code>MapControl</code> object that uses.
	 */
	protected MapControl mapCtrl;

	/**
	 * <p>
	 * Creates a new <code>PointSelectionListener</code> object.
	 * </p>
	 * 
	 * @param mc
	 *            the <code>MapControl</code> where will be applied the changes
	 */
	public PointSelectionListener(MapControl mc) {
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
		try {
			// mapCtrl.getMapContext().selectByPoint(event.getPoint(), 1);
			Point2D p = event.getPoint();
			Point2D mapPoint = mapCtrl.getViewPort().toMapPoint((int) p.getX(),
					(int) p.getY());

			// Tolerancia de 3 pixels
			double tol = mapCtrl.getViewPort().toMapDistance(3);
			FLayer[] actives = mapCtrl.getMapContext().getLayers().getActives();
			for (int i = 0; i < actives.length; i++) {
				if (actives[i] instanceof FLyrVect) {
					FLyrVect lyrVect = (FLyrVect) actives[i];
					FBitSet oldBitSet = lyrVect.getSource().getRecordset()
							.getSelection();
					FBitSet newBitSet = lyrVect.queryByPoint(mapPoint, tol);
					if (event.getEvent().isControlDown())
						newBitSet.xor(oldBitSet);
					lyrVect.getRecordset().setSelection(newBitSet);
				}
			}

		} catch (ReadDriverException e) {
			throw new BehaviorException("No se pudo hacer la selección");
		} catch (VisitorException e) {
			throw new BehaviorException("No se pudo hacer la selección");
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

	}

}
