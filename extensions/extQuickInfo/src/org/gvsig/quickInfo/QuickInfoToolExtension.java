package org.gvsig.quickInfo;

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

import org.gvsig.quickInfo.gui.QuickInfoDataSelectionPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * <p><a href="http://www.gvsig.com/">gvSIG</a> extension.</p>
 * <p>Tool that displays the selected information that the cursor points on the active view.</p>
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class QuickInfoToolExtension extends Extension {
	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		if (actionCommand.equals("QINFO")) {
			View view = (View)PluginServices.getMDIManager().getActiveWindow();
	        new QuickInfoDataSelectionPanel(view.getMapControl());
//        PluginServices.getMDIManager().addWindow(dataSelectionPanel);
		}
 	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		registerIcons();
	}

	protected void registerIcons() {
		PluginServices.getIconTheme().registerDefault(
			"right-arrow-icon",
			this.getClass().getClassLoader().getResource("images/arrow_right.png")
		);

		PluginServices.getIconTheme().registerDefault(
			"layerGroup",
			this.getClass().getClassLoader().getResource("images/layerGroup.png")
		);

		PluginServices.getIconTheme().registerDefault(
			"field-leaf-icon",
			this.getClass().getClassLoader().getResource("images/field-leaf-icon.png")
		);

		PluginServices.getIconTheme().registerDefault(
			"field-complex-icon",
			this.getClass().getClassLoader().getResource("images/field-complex-icon.png")
		);

		PluginServices.getIconTheme().registerDefault(
			"field-child-icon",
			this.getClass().getClassLoader().getResource("images/field-child-icon.png")
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
			
			return hasVectorVisibleLayers(map.getLayers());
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
	 * <p>Finds recursively if there is any visible vector layer.</p>
	 * 
	 * @param root the root node
	 * @return <code>true</code> if the layer is found and is visible in the tree; otherwise <code>false</code>
	 */
    private boolean hasVectorVisibleLayers(FLayers root) {
		if (root != null) {
			FLayer node;

			for (int i = 0; i < root.getLayersCount(); i++) {
				node = root.getLayer(i);

				if (node instanceof FLyrVect) {
					if (node.isVisible())
						return true;
				}
				else {
					if (node instanceof FLayers) {
						if (hasVectorVisibleLayers((FLayers) node))
							return true;
					}
				}
			}
		}
		
		return false;
    }
}
