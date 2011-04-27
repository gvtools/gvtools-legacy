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
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.operations.strategies.DefaultStrategy;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.MeasureEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.CircleListener;


/**
 * <p>Listener that selects all features of the active, available and vector layers which intersect with the defined
 *  circle area in the associated {@link MapControl MapControl} object.</p>
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class CircleSelectionListener implements CircleListener {
	/**
	 * The image to display when the cursor is active.
	 */
	private final Image img = new ImageIcon(this.getClass().getResource("images/circle-cursor-icon.png")).getImage(); 

	/**
	 * The cursor used to work with this tool listener.
	 * 
	 * @see #getCursor()
	 */
	private Cursor cur = Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(16, 16), "");

	/**
	 * Reference to the <code>MapControl</code> object that uses.
	 */
	private MapControl mapCtrl;

	/**
	 * <p>Creates a new listener for selecting circular areas.</p>
	 *
	 * @param mc the <code>MapControl</code> object where the measures are made 
	 */
	public CircleSelectionListener(MapControl mc) {
		this.mapCtrl = mc;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.CircleListener#circle(com.iver.cit.gvsig.fmap.tools.Events.MeasureEvent)
	 */
	public void circle(MeasureEvent event) throws BehaviorException {
		if (event.getEvent().getID() == MouseEvent.MOUSE_RELEASED) {
			FLayer[] activeLayers = mapCtrl.getMapContext().getLayers().getActives();
	
			FLayer layer;
			FLyrVect lyrVect;
	
			Point2D.Double center = new Point2D.Double(event.getXs()[0].doubleValue(), event.getYs()[0].doubleValue());
			Point2D.Double point2 = new Point2D.Double(event.getXs()[1].doubleValue(), event.getYs()[1].doubleValue());

			double radius = center.distance(point2);
			IGeometry geom = ShapeFactory.createCircle(center, radius);

			double flatness;

			// Creates the geometry
			// If the scale is < 500 -> approximates the circle with a polyline with more points, as many as
			// smaller would be the scale
			if (mapCtrl.getMapContext().getScaleView() < 500) {
				GeneralPathX gP = new GeneralPathX();
				flatness = mapCtrl.getMapContext().getScaleView() * FConverter.FLATNESS / (500 * 2); // The number 2 forces to create the double of points 
				gP.append(geom.getPathIterator(null, flatness), true);
				geom = ShapeFactory.createPolygon2D(gP);
			}
			else {
				// Bigger scale -> Smaller flatness
				GeneralPathX gP = new GeneralPathX();
				flatness = FConverter.FLATNESS / (mapCtrl.getMapContext().getScaleView() * 2); // *2 to reduce the number of lines of the polygon
				gP.append(geom.getPathIterator(null, flatness), true);
				geom = ShapeFactory.createPolygon2D(gP);
			}

	        FBitSet newBitSet, oldBitSet;

			for (int i = 0; i < activeLayers.length; i++) {
				layer = activeLayers[i];

				if ((layer.isAvailable()) && (layer instanceof FLyrVect)) {
					lyrVect = (FLyrVect) layer;

					try {
						oldBitSet = lyrVect.getSource().getRecordset().getSelection();

						newBitSet = lyrVect.queryByShape(geom, DefaultStrategy.INTERSECTS);

		                if (event.getEvent().isControlDown())
		                    newBitSet.xor(oldBitSet);

		                lyrVect.getRecordset().setSelection(newBitSet);
					} catch (com.vividsolutions.jts.geom.TopologyException topEx) {
						NotificationManager.showMessageError(PluginServices.getText(null, "Failed_selecting_geometries_by_circle_topology_exception_explanation"), topEx);
					} catch (Exception ex) {
						NotificationManager.showMessageError(PluginServices.getText(null, "Failed_selecting_geometries"), ex);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#getCursor()
	 */
	public Cursor getCursor() {
		return cur;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#cancelDrawing()
	 */
	public boolean cancelDrawing() {
		return false;
	}
}
