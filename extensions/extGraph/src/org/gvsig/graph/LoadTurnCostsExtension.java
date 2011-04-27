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
package org.gvsig.graph;

import java.awt.geom.Point2D;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.gvsig.graph.core.GvNode;
import org.gvsig.graph.core.GvTurn;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.gui.RouteControlPanel;
import org.gvsig.graph.gui.TurnCostsTableChooser;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.IntValue;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.GraphicLayer;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.fmap.rendering.FGraphic;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.IView;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class LoadTurnCostsExtension extends Extension {
	/**
	 * Component to control flags and routes
	 */
	private RouteControlPanel controlPanel;
	private int idSymbolTurnCost = -1;

	// private String fieldType;
	// private String fieldDist;
	// private String fieldSense;

	public void initialize() {

	}

	public void execute(String actionCommand) {
		IView view = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mapControl = view.getMapControl();
		MapContext map = mapControl.getMapContext();
		SingleLayerIterator lyrIterator = new SingleLayerIterator(map
				.getLayers());
		while (lyrIterator.hasNext()) {
			FLayer lyr = lyrIterator.next();
			if ((lyr.isActive()) && (lyr instanceof FLyrVect)) {
				FLyrVect lyrVect = (FLyrVect) lyr;
				Network net = (Network) lyr.getProperty("network");

				if (net != null) {
					try {
						load_turncosts(lyrVect, mapControl);
					} catch (ReadDriverException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	private void addGraphicTurn(MapControl mapCtrl, GvTurn turnCost) {
		GraphicLayer graphicLayer = mapCtrl.getMapContext().getGraphicsLayer();
		if (idSymbolTurnCost  == -1) {
			FSymbol simFlag = new FSymbol(FConstant.SYMBOL_TYPE_ICON);
			simFlag.setStyle(FConstant.SYMBOL_STYLE_MARKER_IMAGEN);
			simFlag.setSizeInPixels(true);
			simFlag.setSize(16);
			ImageIcon icon = new ImageIcon(this.getClass().getClassLoader()
					.getResource("images/turncost_16.png"));
			simFlag.setIcon(icon.getImage());

			idSymbolTurnCost = graphicLayer.addSymbol(simFlag);
		}
		IGeometry gAux = ShapeFactory.createPoint2D(turnCost.getNode().getX(), turnCost.getNode().getY());
		FGraphic graphic = new FGraphic(gAux, idSymbolTurnCost);
		graphic.setTag("BARRIER");
		graphic.setObjectTag(turnCost);
		graphicLayer.addGraphic(graphic);		

	}

	private void load_turncosts(FLyrVect lyrVect, MapControl mapControl)
			throws ReadDriverException {
		Network net = (Network) lyrVect.getProperty("network");

		if (net != null) {
			ProjectExtension pe = (ProjectExtension) PluginServices
					.getExtension(ProjectExtension.class);
			Project project = pe.getProject();
			ProjectTable[] pts = (ProjectTable[]) project.getDocumentsByType(
					ProjectTableFactory.registerName).toArray(
					new ProjectTable[0]);
			TurnCostsTableChooser chooser = new TurnCostsTableChooser();
			chooser.setModel(pts);
			PluginServices.getMDIManager().addWindow(chooser);
			if (chooser.isOkSelected()) {
				ProjectTable t = chooser.getSelectedTable();
				String fieldFromId = chooser.getFieldFromId();
				String fieldToId = chooser.getFieldToId();
				String fieldTurnCost = chooser.getFieldTurnCost();

				SelectableDataSource rs = t.getModelo().getRecordset();
				int iF_from = rs.getFieldIndexByName(fieldFromId);
				int iF_to = rs.getFieldIndexByName(fieldToId);
				int iF_turncost = rs.getFieldIndexByName(fieldTurnCost);

				for (int i = 0; i < rs.getRowCount(); i++) {
					NumericValue from = (NumericValue) rs.getFieldValue(i,
							iF_from);
					NumericValue to = (NumericValue) rs.getFieldValue(i, iF_to);
					NumericValue turn_cost = (NumericValue) rs.getFieldValue(i,
							iF_turncost);
					GvTurn n = net.addTurnCost(from.intValue(), to.intValue(), turn_cost
							.doubleValue());
					if (n != null)
						addGraphicTurn(mapControl, n);
				}
				mapControl.drawGraphics();

			}
		} else {
			JOptionPane.showMessageDialog((JComponent) PluginServices
					.getMDIManager().getActiveWindow(), PluginServices.getText(
					this, "la_capa_no_tiene_red_asociada"));
		}
	}

	public boolean isEnabled() {
		IWindow f = PluginServices.getMDIManager().getActiveWindow();
		if (f == null) {
			return false;
		}
		if (f instanceof View) {
			View v = (View) f;
			MapContext map = v.getMapControl().getMapContext();
			SingleLayerIterator it = new SingleLayerIterator(map.getLayers());
			while (it.hasNext()) {
				FLayer aux = it.next();
				if (!aux.isActive())
					continue;
				Network net = (Network) aux.getProperty("network");

				if (net != null) {
					return true;
				}
			}
			return false;

		}
		return false;

	}

	public boolean isVisible() {
		IWindow f = PluginServices.getMDIManager().getActiveWindow();

		if (f == null) {
			return false;
		}

		if (f instanceof View) {
			View vista = (View) f;
			IProjectView model = vista.getModel();
			MapContext mapa = model.getMapContext();
			FLayer[] activeLayers = mapa.getLayers().getActives();
			if (activeLayers.length > 0)
				if (activeLayers[0] instanceof FLyrVect) {
					FLyrVect lyrVect = (FLyrVect) activeLayers[0];
					if (!lyrVect.isAvailable())
						return false;

					int shapeType;
					try {
						shapeType = lyrVect.getShapeType();
						// if (shapeType == FShape.LINE)
						if ((shapeType & FShape.LINE) == FShape.LINE)
							return true;
					} catch (ReadDriverException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		return false;

	}

}
