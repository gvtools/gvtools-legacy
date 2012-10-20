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
package org.gvsig.graph.tools;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.gvsig.graph.core.GvTurn;
import org.gvsig.graph.core.Network;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.GraphicLayer;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.fmap.rendering.FGraphic;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;

public class TurnCostListener implements PointListener {
	private MapControl mapCtrl;
	private int idFrom = -1;

	private int idSymbolTurnCost = -1;

	private Cursor cur = java.awt.Cursor
			.getPredefinedCursor(Cursor.HAND_CURSOR);
	private int idTo;

	public TurnCostListener(MapControl mc) {
		this.mapCtrl = mc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.fmap.tools.Listeners.PointListener#point(org.gvsig.fmap.tools
	 * .Events.PointEvent) The PointEvent method bring you a point in pixel
	 * coordinates. You need to transform it to world coordinates. The class to
	 * do conversions is ViewPort, obtained thru the MapContext of mapCtrl.
	 */
	public void point(PointEvent event) throws BehaviorException {

		Point2D pReal = mapCtrl.getMapContext().getViewPort()
				.toMapPoint(event.getPoint());

		SingleLayerIterator it = new SingleLayerIterator(mapCtrl
				.getMapContext().getLayers());
		while (it.hasNext()) {
			FLayer aux = it.next();
			if (!aux.isActive())
				continue;
			Network net = (Network) aux.getProperty("network");

			if (net != null) {
				Point2D nearestPoint = new Point2D.Double();
				double realTol = mapCtrl.getViewPort().toMapDistance(
						FlagListener.pixelTolerance);
				int idArc = net.findClosestArc(pReal.getX(), pReal.getY(),
						realTol, nearestPoint);
				if (idArc == -1) {
					JOptionPane.showMessageDialog((Component) PluginServices
							.getMDIManager().getActiveWindow(), PluginServices
							.getText(null, "point_not_on_the_network"));
					return;
				}
				if (idFrom == -1) {
					idFrom = idArc;
					return;
				}
				if (idArc == idFrom)
					return;
				idTo = idArc;
				String auxCost = JOptionPane.showInputDialog(
						PluginServices.getText(null, "New_turncost")
								+ ":"
								+ PluginServices.getText(null,
										"turn_prohibited_minus_1"), "-1");
				if (auxCost == null) {
					idFrom = -1;
					return;
				}
				double cost = Double.parseDouble(auxCost);
				GvTurn turnCost = net.addTurnCost(idFrom, idTo, cost);
				if (turnCost == null) {
					JOptionPane.showMessageDialog((Component) PluginServices
							.getMDIManager().getActiveWindow(), PluginServices
							.getText(null, "no_conection_between_arcs"));

					return;
				}

				GraphicLayer graphicLayer = mapCtrl.getMapContext()
						.getGraphicsLayer();
				if (idSymbolTurnCost == -1) {
					FSymbol simFlag = new FSymbol(FConstant.SYMBOL_TYPE_ICON);
					simFlag.setStyle(FConstant.SYMBOL_STYLE_MARKER_IMAGEN);
					simFlag.setSizeInPixels(true);
					simFlag.setSize(16);
					ImageIcon icon = new ImageIcon(this.getClass()
							.getClassLoader()
							.getResource("images/turncost_16.png"));
					simFlag.setIcon(icon.getImage());

					idSymbolTurnCost = graphicLayer.addSymbol(simFlag);
				}
				IGeometry gAux = ShapeFactory.createPoint2D(
						nearestPoint.getX(), nearestPoint.getY());
				FGraphic graphic = new FGraphic(gAux, idSymbolTurnCost);
				graphic.setTag("BARRIER");
				graphic.setObjectTag(turnCost);
				graphicLayer.addGraphic(graphic);
				mapCtrl.drawGraphics();
				idFrom = -1;
				PluginServices.getMainFrame().enableControls();

			}
		}

	}

	public Cursor getCursor() {
		return cur;
	}

	public boolean cancelDrawing() {
		return false;
	}

	public void pointDoubleClick(PointEvent event) throws BehaviorException {

	}

}
