package org.gvsig.selectionTools.tools;

/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.operations.strategies.DefaultStrategy;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.MeasureEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PolylineListener;

/**
 * <p>
 * Listener that selects all features of the active and vector layers which
 * intersect with the defined polyline in the associated {@link MapControl
 * MapControl} object.
 * </p>
 * 
 * <p>
 * The selection will be produced after user finishes the creation of the
 * polyline.
 * </p>
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class PolyLineSelectionListener implements PolylineListener {
	/**
	 * The image to display when the cursor is active.
	 */
	private final Image img = new ImageIcon(this.getClass().getResource(
			"images/polyline-cursor-icon.png")).getImage();

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
	private MapControl mapCtrl;

	/**
	 * <p>
	 * Creates a new <code>PolygonSelectionListener</code> object.
	 * </p>
	 * 
	 * @param mc
	 *            the <code>MapControl</code> where is drawn the polyline
	 */
	public PolyLineSelectionListener(MapControl mc) {
		this.mapCtrl = mc;
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
	 * com.iver.cit.gvsig.fmap.tools.Listeners.PolylineListener#points(com.iver
	 * .cit.gvsig.fmap.tools.Events.MeasureEvent)
	 */
	public void points(MeasureEvent event) throws BehaviorException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.tools.Listeners.PolylineListener#pointFixed(com
	 * .iver.cit.gvsig.fmap.tools.Events.MeasureEvent)
	 */
	public void pointFixed(MeasureEvent event) throws BehaviorException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.tools.Listeners.PolylineListener#polylineFinished
	 * (com.iver.cit.gvsig.fmap.tools.Events.MeasureEvent)
	 */
	public void polylineFinished(MeasureEvent event) throws BehaviorException {
		try {
			GeneralPathX gp = event.getGP();
			IGeometry geom = ShapeFactory.createPolyline2D(gp);
			FLayer[] actives = mapCtrl.getMapContext().getLayers().getActives();

			for (int i = 0; i < actives.length; i++) {
				if (actives[i] instanceof FLyrVect) {
					FLyrVect lyrVect = (FLyrVect) actives[i];
					FBitSet oldBitSet = lyrVect.getSource().getRecordset()
							.getSelection();
					FBitSet newBitSet = lyrVect.queryByShape(geom,
							DefaultStrategy.INTERSECTS);
					if (event.getEvent().isControlDown())
						newBitSet.xor(oldBitSet);
					lyrVect.getRecordset().setSelection(newBitSet);
				}
			}
		} catch (com.vividsolutions.jts.geom.TopologyException topEx) {
			NotificationManager
					.showMessageError(
							PluginServices
									.getText(null,
											"Failed_selecting_geometries_by_polyline_topology_exception_explanation"),
							topEx);
		} catch (Exception ex) {
			NotificationManager
					.showMessageError(PluginServices.getText(null,
							"Failed_selecting_geometries"), ex);
		}
	}
}