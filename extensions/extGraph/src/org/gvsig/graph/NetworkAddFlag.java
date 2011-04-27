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

import org.gvsig.graph.core.Network;
import org.gvsig.graph.tools.BarrierListener;
import org.gvsig.graph.tools.FlagListener;
import org.gvsig.graph.tools.TurnCostListener;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.fmap.tools.Behavior.PointBehavior;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class NetworkAddFlag extends Extension {		
    FlagListener flagListener = null;
    BarrierListener barrierListener = null;
	private TurnCostListener turnCostListener;
	public void initialize() {
		PluginServices.getIconTheme().registerDefault(
				"add_flag_on_node",
				this.getClass().getClassLoader().getResource("images/add_flag_on_node.png")
			);		
		PluginServices.getIconTheme().registerDefault(
				"add_flag_on_arc",
				this.getClass().getClassLoader().getResource("images/add_flag_on_arc.png")
			);		
		PluginServices.getIconTheme().registerDefault(
				"no_way",
				this.getClass().getClassLoader().getResource("images/no_way.png")
			);		
		PluginServices.getIconTheme().registerDefault(
				"turncost",
				this.getClass().getClassLoader().getResource("images/turncost_16.png")
			);		
		
	}

	public void execute(String actionCommand) {
		View v = (View) PluginServices.getMDIManager().getActiveWindow();
        MapControl mapCtrl = v.getMapControl();

        if (!mapCtrl.hasTool("addFlag")) // We create it for the first time.
        {
        	flagListener = new FlagListener(mapCtrl);
            mapCtrl.addMapTool("addFlag", new PointBehavior(flagListener));
        }

        if (!mapCtrl.hasTool("addBarrier")) // We create it for the first time.
        {
        	barrierListener = new BarrierListener(mapCtrl);
            mapCtrl.addMapTool("addBarrier", new PointBehavior(barrierListener));
        }

        if (!mapCtrl.hasTool("addTurnCost")) // We create it for the first time.
        {
        	turnCostListener = new TurnCostListener(mapCtrl);
            mapCtrl.addMapTool("addTurnCost", new PointBehavior(turnCostListener));
        }

        if (actionCommand.compareTo("ADD_FLAG_TO_NETWORK") == 0)
        {
        	flagListener.setMode(FlagListener.TO_ARC);
            mapCtrl.setTool("addFlag");
        }
        if (actionCommand.compareTo("ADD_FLAG_TO_NODE") == 0)
        {
        	flagListener.setMode(FlagListener.TO_NODE);
            mapCtrl.setTool("addFlag");
        }
        if (actionCommand.compareTo("ADD_BARRIER") == 0)
        {
            mapCtrl.setTool("addBarrier");
        }
        if (actionCommand.compareTo("ADD_TURNCOST") == 0)
        {
            mapCtrl.setTool("addTurnCost");
        }        
      
//        else
//        {
//        	JOptionPane.showMessageDialog((JComponent) PluginServices.getMDIManager().getActiveWindow(), 
//        			"Not implemented yet");
//        }

		
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
					return true;
				}
			}
		}
		return false;

	}

	public boolean isVisible() {
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
					return true;
				}
			}
		}
		return false;

	}

}


