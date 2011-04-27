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

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.gvsig.graph.core.Network;
import org.gvsig.graph.gui.RouteControlPanel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.IView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.util.GvSession;

public class ManageFlagsExtension extends Extension {
	/**
	 * Component to control flags and routes
	 * */
	private RouteControlPanel controlPanel;
	
//	private String fieldType;
//	private String fieldDist;
//	private String fieldSense;

	public void initialize() {
		PluginServices.getIconTheme().registerDefault(
				"manage_flags",
				this.getClass().getClassLoader().getResource("images/manage_flags.png")
			);		

	}

	public void execute(String actionCommand) {
		IView view = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mapControl = view.getMapControl();
		MapContext map = mapControl.getMapContext();
		SingleLayerIterator lyrIterator = new SingleLayerIterator(map
				.getLayers());
		while (lyrIterator.hasNext()) {
			FLayer lyr = lyrIterator.next();
			if ((lyr.isActive()) && (lyr instanceof FLyrVect))
			{
				FLyrVect lyrVect = (FLyrVect) lyr;
				Network net = (Network) lyr.getProperty("network");

				if ( net != null)
				{
					manageFlags(lyrVect, mapControl);
				}
			}
		}


	}

	private void manageFlags(FLyrVect lyrVect, MapControl mapControl) {
		Network net = (Network) lyrVect.getProperty("network");

		if ( net != null)
		{
				if(controlPanel == null)
				{
				
					controlPanel = new RouteControlPanel(net);
				}
				controlPanel.setMapControl(mapControl, net);
				//lo guardamos en una variable de sesion para poder recuperarlo
				//(ejemplo: cuando borremos los flags de la red de la capa activa
				//borraremos a su vez el contenido del control panel		
				controlPanel = (RouteControlPanel) PluginServices.getMDIManager().addWindow(controlPanel);
				GvSession.getInstance().put(mapControl, "RouteControlPanel", controlPanel);
		}
		else
		{
			JOptionPane.showMessageDialog((JComponent) PluginServices.getMDIManager().getActiveWindow(),
						PluginServices.getText(this, "la_capa_no_tiene_red_asociada"));
		}
	}

	public boolean isEnabled() {
		IWindow f = PluginServices.getMDIManager()
		 .getActiveWindow();
		if (f == null) {
		    return false;
		}
		if (f instanceof View) {
		    View v = (View) f;
			MapContext map = v.getMapControl().getMapContext();
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
				if (activeLayers[0] instanceof FLyrVect){
					FLyrVect lyrVect = (FLyrVect) activeLayers[0];
					int shapeType ;
					try {
						if (!lyrVect.isAvailable())
							return false;
						
						shapeType = lyrVect.getShapeType();
//						if (shapeType == FShape.LINE)
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
