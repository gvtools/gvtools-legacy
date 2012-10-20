package org.gvsig.selectionTools;

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

import org.gvsig.selectionTools.tools.CircleSelectListener;
import org.gvsig.selectionTools.tools.behavior.CircleSelectionBehavior;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.MouseMovementBehavior;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.project.documents.view.toolListeners.StatusBarListener;

/**
 * <p>
 * Extension to add support for selecting the geometries of the active vector
 * layers that intersect with a circle defined by the user.
 * </p>
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class SelectByCircleExtension extends Extension {
	public static final String CIRCLE_SELECTION_TOOL_NAME = "circleSelection";

	/*
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		registerIcons();
	}

	private void registerIcons() {
		PluginServices.getIconTheme().registerDefault(
				"circle-cursor-icon",
				this.getClass().getClassLoader()
						.getResource("images/circle-cursor-icon.png"));

		PluginServices.getIconTheme().registerDefault(
				"select-by-circle-icon",
				this.getClass().getClassLoader()
						.getResource("images/select-by-circle-icon.png"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		if (actionCommand.equals("SELCIRCLE")) {
			IWindow view = PluginServices.getMDIManager().getActiveWindow();
			if (view instanceof View) {
				// Selection by circle
				MapControl mc = ((View) view).getMapControl();

				// If current's view MapControl doesn't have the
				// "CircleSelection" tool, adds it
				if (!mc.getNamesMapTools().containsKey(
						CIRCLE_SELECTION_TOOL_NAME)) {
					CircleSelectListener circleSelListener = new CircleSelectListener(
							mc);
					mc.addMapTool(CIRCLE_SELECTION_TOOL_NAME,
							new Behavior[] {
									new CircleSelectionBehavior(
											circleSelListener),
									new MouseMovementBehavior(
											new StatusBarListener(mc)) });
				}

				mc.setTool(CIRCLE_SELECTION_TOOL_NAME);
			}
		}
	}

	/*
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices
				.getMDIManager().getActiveWindow();

		if (f == null) {
			return false;
		}

		if (f instanceof View) {
			View vista = (View) f;
			IProjectView model = vista.getModel();
			MapContext mapa = model.getMapContext();

			return mapa.getLayers().getLayersCount() > 0;
		}

		return false;
	}

	/*
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices
				.getMDIManager().getActiveWindow();

		if (f == null) {
			return false;
		}

		if (f instanceof View) {
			View vista = (View) f;
			IProjectView model = vista.getModel();
			MapContext mapa = model.getMapContext();

			FLayer layers[] = mapa.getLayers().getActives();
			FLayer layer;

			for (int i = 0; i < layers.length; i++) {
				layer = layers[i];

				if ((layer instanceof FLyrVect) && (layer.isAvailable())
						&& (layer.isActive()))
					return true;
			}
		}

		return false;
	}
}
