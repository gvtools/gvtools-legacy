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

import java.awt.Color;
import java.net.URL;
import java.text.NumberFormat;

import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.GvNode;
import org.gvsig.graph.core.IGraph;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.NetworkUtils;
import org.gvsig.graph.core.NodesDriver;
import org.gvsig.graph.gui.ServiceAreaControlPanel;
import org.gvsig.graph.solvers.OneToManySolver;
import org.gvsig.graph.solvers.ServiceAreaExtractor2;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.core.v02.FLabel;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.GraphicLayer;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.fmap.rendering.FGraphic;
import com.iver.cit.gvsig.fmap.rendering.FGraphicLabel;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.save.BeforeSavingListener;
import com.iver.utiles.save.SaveEvent;

/**
 * @author fjp
 * 
 * Extension to add nodes as a layer. It will be useful to have a good view of
 * the network, and allows to edit and fix bugs.
 */
public class AddLayerNodesExtension extends Extension {


	public void initialize() {
	}

	public void execute(String actionCommand) {

		View v = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mapCtrl = v.getMapControl();
		MapContext map = mapCtrl.getMapContext();
		SingleLayerIterator it = new SingleLayerIterator(map.getLayers());		
		while (it.hasNext()) {
			FLayer aux = it.next();
			if (!aux.isActive())
				continue;
			Network net = (Network) aux.getProperty("network");

			if (net != null) {
				GvFlag[] flags = net.getFlags();
				try {
					if (actionCommand.equals("LOAD_NODES")) {
						add_nodes_layer(mapCtrl, net);
					}
				} catch (BaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return;
			}
		}

	}

	/**
	 * @param mapCtrl
	 * @param net
	 * @return
	 * @throws GraphException
	 */
	private void add_nodes_layer(MapControl mapCtrl, Network net) throws GraphException {
		NodesDriver driver = new NodesDriver(net);
		FLyrVect lyr = (FLyrVect) LayerFactory.createLayer("tmpNodes", driver, net.getLayer().getCrs());
		URL legendPath = this.getClass().getClassLoader().getResource("images/nodos.gvl");
		NetworkUtils.loadLegend(lyr, legendPath.getPath());
		mapCtrl.getMapContext().beginAtomicEvent();
		mapCtrl.getMapContext().getLayers().addLayer(lyr);
		BeforeSavingListener savingListener = new MyBeforeSavingListener(lyr, mapCtrl.getMapContext());
		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		ext.addListener(savingListener);
		mapCtrl.getMapContext().endAtomicEvent();
	
	}

	public boolean isEnabled() {
		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof View)
		{
			View v = (View) window;
	        MapControl mapCtrl = v.getMapControl();
			MapContext map = mapCtrl.getMapContext();
			
			SingleLayerIterator it = new SingleLayerIterator(map.getLayers());
			while (it.hasNext())
			{
				FLayer aux = it.next();
				if (!aux.isActive())
					continue;
				Network net = (Network) aux.getProperty("network");
				
				if ( net != null)
				{
//					if (net.getFlags().length > 0)
//					{
						return true;
//					}
				}
			}
		}
		return false;
	}

	public boolean isVisible() {
		IWindow f = PluginServices.getMDIManager()
		 .getActiveWindow();
		if (f == null) {
		    return false;
		}
		if (f instanceof View) {
			return true;
		}
		return false;

	}

}
