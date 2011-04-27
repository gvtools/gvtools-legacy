package es.iver.derivedGeom;

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

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;

import es.iver.derivedGeom.gui.LayerAndProcessSelectionPanel;

/**
 * <p>Extension that allows user create a shape layer with derivative geometry.</p>
 * <p>Only works with vector layers with line or point geometries.</p>
 * 
 * <p>User will define the output layer, with geometries will use, in which order, and the kind of geometry
 *  type of the new shape: line, polygon, or multi.</p>
 *  
 * <p>There are 3 kinds of conversions:
 *  <ul>
 *   <li><b>Points</b> to <b>Multi-Lines</b>: the selected points will be converted in multi-lines in the specified order.</li>
 *   <li><b>Points</b> to <b>Polygons</b>: the selected points will be converted in polygons in the specified order, (the last point selected
 *    will be the start point of the last line of the polygon, the first point will be also the last point of the polygon).</li>
 *   or
 *   <li><b>Lines</b> to <b>Polygons</b>: the selected multi-line geometries (only for composed lines).</li>
 *  </ul>
 * </p>
 * 
 * <p>After the selection, that information will be added, if the layer can be edited, as new columns of
 *  the associated data table.</p>
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class AddDerivedGeometrySHPExtension extends Extension {
//	private DerivativeGeometryControlPanel controlPanel;
//	private boolean isControlPlanelShown = false;
//	public static final String ToolID = "dGeomsSnappingTool";
//	public static NonEditedLayerSelectionCADTool nonEditedLayerSelectionTool;
//	public static FLyrVect nonEditedLayer = null;
	
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		if (actionCommand.equals("ADD_DERIVATIVE_GEOMETRY_SHP")) {
			IWindow view = PluginServices.getMDIManager().getActiveWindow();
			if (view instanceof View) {
		        new LayerAndProcessSelectionPanel((View)view);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
//		nonEditedLayerSelectionTool = new NonEditedLayerSelectionCADTool(null); // By default any layer
		registerIcons();
	}

	private void registerIcons() {
		PluginServices.getIconTheme().registerDefault(
			"add_v2-icon",
			this.getClass().getClassLoader().getResource("images/add_v2-icon.png")
		);

		PluginServices.getIconTheme().registerDefault(
			"add-all-icon",
			this.getClass().getClassLoader().getResource("images/add-all-icon.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"button-cancel-icon",
			this.getClass().getClassLoader().getResource("images/button-cancel-icon.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"button-ok-icon",
			this.getClass().getClassLoader().getResource("images/button-ok-icon.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"double-left-arrow-icon",
			this.getClass().getClassLoader().getResource("images/double-left-arrow-icon.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"double-right-arrow-icon",
			this.getClass().getClassLoader().getResource("images/double-right-arrow-icon.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"down-arrow-icon",
			this.getClass().getClassLoader().getResource("images/down-arrow-icon.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"layerGroup",
			this.getClass().getClassLoader().getResource("images/layerGroup.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"left-arrow-icon",
			this.getClass().getClassLoader().getResource("images/left-arrow-icon.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"multi-icon",
			this.getClass().getClassLoader().getResource("images/multi-icon.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"MultiPoint",
			this.getClass().getClassLoader().getResource("images/MultiPoint.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"Point",
			this.getClass().getClassLoader().getResource("images/Point.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"Polygon",
			this.getClass().getClassLoader().getResource("images/Polygon.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"Rect",
			this.getClass().getClassLoader().getResource("images/Rect.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"reload-icon",
			this.getClass().getClassLoader().getResource("images/reload-icon.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"remove-all-icon",
			this.getClass().getClassLoader().getResource("images/remove-all-icon.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"remove-icon",
			this.getClass().getClassLoader().getResource("images/remove-icon.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"right-arrow-icon",
			this.getClass().getClassLoader().getResource("images/right-arrow-icon.png")
		);
	
		PluginServices.getIconTheme().registerDefault(
			"up-arrow-icon",
			this.getClass().getClassLoader().getResource("images/up-arrow-icon.png")
		);
		
		PluginServices.getIconTheme().registerDefault(
			"create-derivative-geometry-shape-icon",
			this.getClass().getClassLoader().getResource("images/create-derivative-geometry-shape-icon.png")
		);
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager().getActiveWindow();

		if (f == null) {
			return false;
		}
		
		if (f instanceof View) {
			View view = (View) f;
		
			IProjectView model = view.getModel();
			MapContext map = model.getMapContext();
			
			return hasVectorVisiblePointOrLineLayers(map.getLayers());
		} 
		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager().getActiveWindow();

		if (f == null) {
			return false;
		}
		
		if (f instanceof View) {
			View view = (View) f;
		
			IProjectView model = view.getModel();
			MapContext map = model.getMapContext();
		
			return map.getLayers().getLayersCount() > 0;
		} else {
			return false;
		}
	}

	/**
	 * <p>Finds recursively if there is any visible non being edited vector layer of lines of point geometries.</p>
	 * 
	 * @param root the root node
	 * @return <code>true</code> if there is any visible vector layer of lines of point geometries; otherwise <code>false</code>
	 */
    private boolean hasVectorVisiblePointOrLineLayers(FLayers root) {
		if (root != null) {
			FLayer node;
			FLyrVect vectNode;

			for (int i = 0; i < root.getLayersCount(); i++) {
				node = root.getLayer(i);

				if (node instanceof FLyrVect) {
					vectNode = (FLyrVect)node;

					try {
						if ((!node.isEditing()) && (node.isVisible()) && ((vectNode.getShapeType() == FShape.LINE) || (vectNode.getShapeType() == FShape.POINT)) )
							return true;
					}
					catch(Exception ex) {
						PluginServices.getLogger().error(ex);
						continue;
					}
				}
				else {
					if (node instanceof FLayers) {
						if (hasVectorVisiblePointOrLineLayers((FLayers) node))
							return true;
					}
				}
			}
		}
		
		return false;
    }
    
//    public void showControlPanel(DerivativeGeometryProcessParameters parameters) {
//    	if ((controlPanel != null) && (! isControlPlanelShown)) {
//    		
//    		if (parameters != null)
//    			controlPanel.setParameters(parameters);
//
//    		PluginServices.getMDIManager().addWindow(controlPanel);
//    		isControlPlanelShown = true;
//    	}
//    }
//    
//    public void hideControlPanel() {
//    	if ((controlPanel != null) && (isControlPlanelShown)) {
//	    	PluginServices.getMDIManager().closeWindow(controlPanel);
//	    	isControlPlanelShown = false;
//    	}
//    }
}
