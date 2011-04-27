/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
/* CVS MESSAGES:
*
* $Id: 
* $Log: 
*/
package org.gvsig.topology;

import java.util.List;

import org.gvsig.fmap.core.FLyrUtil;
import org.gvsig.fmap.tools.GeometryInfoListener;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.MouseMovementBehavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.PointBehavior;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.project.documents.view.toolListeners.StatusBarListener;

/**
 * Shows information about the geometry of the selected feature.
 * @author Alvaro Zabala
 *
 */
public class GeometryInfoExtension extends Extension{

	public void execute(String actionCommand) {
		IWindow f = PluginServices.getMDIManager().getActiveWindow();
		if (f == null) {
		    return;
		}
		if (f instanceof View) {
		    View vista = (View) f;
		    MapControl mapControl = vista.getMapControl();
		    if(!mapControl.hasTool("geometryinfo")){
		    		GeometryInfoListener il = new GeometryInfoListener(mapControl);
		    	 	mapControl.addMapTool("geometryinfo", 
		    	 						new Behavior[]{new PointBehavior(il), 
		    	 						new MouseMovementBehavior(new StatusBarListener(mapControl))}); 	
		    }//if
		    mapControl.setTool("geometryinfo");
		}//if
	}

	
	public void initialize() {
		registerIcons();
	}
	
	protected void registerIcons(){
		PluginServices.getIconTheme().registerDefault("geometry-info",
													   this.getClass().
													   getClassLoader().
													   getResource("images/geometry.gif")
		);
	}
	
	protected String getName(){
		return PluginServices.getText(this, "GEOMETRY_INFO");
	}

	/**
	 * Returns if this Edit CAD tool is visible. 
	 * For this, there must be an active vectorial editing lyr in the TOC, which geometries'
	 * dimension would must be linear or polygonal, and with at least one selected geometry.
	 *  
	 */
	public boolean isEnabled() {
		IWindow f = PluginServices.getMDIManager().getActiveWindow();
		if (f == null) {
		    return false;
		}
		if (f instanceof View) {
		    View vista = (View) f;
		    IProjectView model = vista.getModel();
		    MapContext mapContext =  model.getMapContext();
			List<FLyrVect> activeLyrs = FLyrUtil.getActiveVectorialLyrs(mapContext);
			if(activeLyrs.size() == 0)
				return false;
			return true;
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
		    MapContext mapContext =  model.getMapContext();
		    List<FLyrVect> vectorialLyrs = FLyrUtil.getVectorialLayers(mapContext);
		    return vectorialLyrs.size() > 0;
		}
		return false;
	}

}
