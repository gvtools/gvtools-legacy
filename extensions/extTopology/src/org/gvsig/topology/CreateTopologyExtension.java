package org.gvsig.topology;
import org.gvsig.topology.topologyrules.FMapGeometryMustBeClosed;
import org.gvsig.topology.topologyrules.JtsValidRule;
import org.gvsig.topology.topologyrules.LineMustNotHaveDangles2;
import org.gvsig.topology.topologyrules.LineMustNotHavePseudonodes;
import org.gvsig.topology.topologyrules.LineMustNotSelfIntersect;
import org.gvsig.topology.topologyrules.LyrMustBeContainedByOneGeometry;
import org.gvsig.topology.topologyrules.LyrMustBeCoveredByLyr;
import org.gvsig.topology.topologyrules.LyrMustBeCoveredByOneGeometry;
import org.gvsig.topology.topologyrules.LyrMustBeEqual;
import org.gvsig.topology.topologyrules.LyrMustContainsOneGeometry;
import org.gvsig.topology.topologyrules.LyrMustCoversOneGeometry;
import org.gvsig.topology.topologyrules.LyrMustCrossWith;
import org.gvsig.topology.topologyrules.LyrMustDisjoint;
import org.gvsig.topology.topologyrules.LyrMustNotHaveDuplicated;
import org.gvsig.topology.topologyrules.LyrMustTouch;
import org.gvsig.topology.topologyrules.MustNotHaveRepeatedPoints;
import org.gvsig.topology.topologyrules.PointsMustNotOverlap;
import org.gvsig.topology.topologyrules.PolygonMustNotHaveGaps;
import org.gvsig.topology.topologyrules.PolygonMustNotOverlap;
import org.gvsig.topology.topologyrules.PolygonMustNotOverlapWith;
import org.gvsig.topology.topologyrules.PolygonMustNotSelfIntersect;
import org.gvsig.topology.ui.NewTopologyWizard;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayerGenericVectorial;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.project.documents.view.legend.gui.General;
import com.iver.cit.gvsig.project.documents.view.legend.gui.LabelingManager;
import com.iver.cit.gvsig.project.documents.view.legend.gui.LegendManager;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ThemeManagerWindow;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

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
/**
 * ANDAMI's extension to create new topologies, from toc's layers and 
 * topology rules specified by user.
 */
public class CreateTopologyExtension extends Extension {

	public void execute(String actionCommand) {
		if(actionCommand.equalsIgnoreCase("CREATE_TOPOLOGY")){
			com.iver.andami.ui.mdiManager.IWindow f = 
				PluginServices.getMDIManager().getActiveWindow();
				if (f == null) {
					return;
				}
				if (f instanceof View) {
					View vista = (View) f;
					IProjectView model = vista.getModel();
					MapContext mapContext = model.getMapContext();
					NewTopologyWizard wizard = new NewTopologyWizard(mapContext);
					wizard.getWindowInfo().setWidth(640);
					wizard.getWindowInfo().setHeight(350);
					wizard.getWindowInfo().setTitle(PluginServices.getText(this, "Create_Topology"));
					PluginServices.getMDIManager().addWindow(wizard);
				}
		}
	}

	public void initialize() {
		registerIcons();
		registerTopologyRules();
		
		ThemeManagerWindow.setTabEnabledForLayer(General.class, FLayerGenericVectorial.class, true);
		ThemeManagerWindow.setTabEnabledForLayer(LegendManager.class, FLayerGenericVectorial.class, true);
		ThemeManagerWindow.setTabEnabledForLayer(LabelingManager.class, FLayerGenericVectorial.class, true);

	}
	
	private void registerIcons(){
		PluginServices.getIconTheme().registerDefault(
				"create-topology",
				this.getClass().getClassLoader().getResource("images/create-topology.png")
		);
		
		
		PluginServices.getIconTheme().registerDefault(
				"introductory-step-wizard",
				this.getClass().getClassLoader().getResource("images/introductory-step-wizard.png")
		);
	}
	
	private void registerTopologyRules(){
		
		//TODO Implementar un mecanismo que no requiera registro,
		//sino descubrimiento
		ExtensionPoints extensionPoints = 
			ExtensionPointsSingleton.getInstance();
		
		extensionPoints.add("TopologyRules",
								"JTSVALID", //usar una entrada en el text para esto
							JtsValidRule.class);
		
		extensionPoints.add("TopologyRules",
				"MUSTNOTHAVEREPEATED",
			MustNotHaveRepeatedPoints.class);
		
		extensionPoints.add("TopologyRules",
				"FMAPMUSTBECLOSED",
			FMapGeometryMustBeClosed.class);
		
		extensionPoints.add("TopologyRules",
				"LINENOTSELFINTERSECT",
				LineMustNotSelfIntersect.class);
		
		extensionPoints.add("TopologyRules",
				"LINEMUSTNOTHAVEDANGLES",
				LineMustNotHaveDangles2.class);
		
		extensionPoints.add("TopologyRules",
				"LINEMUSTNOTHAVEPSEUDONODES",
				LineMustNotHavePseudonodes.class);
		
		extensionPoints.add("TopologyRules",
				"POLYGONMUSTNOTSELFINTERSECT",
				PolygonMustNotSelfIntersect.class);
		
		extensionPoints.add("TopologyRules",
				"POLYGONMUSTNOTOVERLAP",
				PolygonMustNotOverlap.class);
		
		extensionPoints.add("TopologyRules",
				"POLYGONMUSTNOTOVERLAPWITH",
				PolygonMustNotOverlapWith.class);
		
		extensionPoints.add("TopologyRules",
				"POINTSMUSTNOTOVERLAP",
				PointsMustNotOverlap.class);
		
		extensionPoints.add("TopologyRules",
				"POLYGONSMUSTNOTHAVEGAPS",
				PolygonMustNotHaveGaps.class);
		
		extensionPoints.add("TopologyRules",
				"LYRMUSTBECONTAINED",
				LyrMustBeContainedByOneGeometry.class);
		
		extensionPoints.add("TopologyRules",
				"LYRMUSTBECOVERED",
				LyrMustBeCoveredByOneGeometry.class);
		
		extensionPoints.add("TopologyRules",
				"LYRMUSTBEEQUAL",
				LyrMustBeEqual.class);
		
		extensionPoints.add("TopologyRules",
				"LYRMUSTCONTAINS",
				LyrMustContainsOneGeometry.class);
		
		extensionPoints.add("TopologyRules",
				"LYRMUSTCOVERS",
				LyrMustCoversOneGeometry.class);
		
		extensionPoints.add("TopologyRules",
				"LYRMUSTCROSSWITH",
				LyrMustCrossWith.class);
		
		extensionPoints.add("TopologyRules",
				"LYRMUSTDISJOINTS",
				LyrMustDisjoint.class);
		
		extensionPoints.add("TopologyRules",
				"LYRMUSTNOTHAVEDUPLICATES",
				LyrMustNotHaveDuplicated.class);
		
		extensionPoints.add("TopologyRules",
				"LYRMUSTTOUCH",
				LyrMustTouch.class);
		
		extensionPoints.add("TopologyRules",
				"LYRMUSTBECOVEREDBYLAYER",
				LyrMustBeCoveredByLyr.class);
		
		
	}

	public boolean isEnabled() {
		return true;
	}

	/**
	 * If the active view has any vectorial layer, the plugin will be visible
	 * 
	 * @return visibility of plugin's gui components.
	 */
	public boolean isVisible() {
		IWindow f = PluginServices.getMDIManager().getActiveWindow();
		if (f == null) {
		    return false;
		}
		if (f instanceof View) {
		    View vista = (View) f;
		    IProjectView model = vista.getModel();
		    FLayers layers =  model.getMapContext().getLayers();
		    SingleLayerIterator lyrIterator = new SingleLayerIterator(layers);
			 while(lyrIterator.hasNext()){
				 FLayer lyr = lyrIterator.next();
				 if(lyr instanceof FLyrVect){
					 if(! (lyr.getParentLayer() instanceof Topology))
						 return true;
				 }//if
			 }//while
		    return false;
		}
		return false;
	}
}
