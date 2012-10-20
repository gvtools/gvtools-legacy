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

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.gvsig.selectionTools.tools.buffer.gui.BufferConfigurationPanel;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * <p>
 * Extension to add support for selecting the geometries of the active vector
 * layers that intersect with a buffer around their previously selected
 * geometries.
 * </p>
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class SelectByBufferExtension extends Extension {
	public static final String BUFFER_SELECTION_TOOL_NAME = "bufferSelection";

	/*
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		registerIcons();
	}

	private void registerIcons() {
		PluginServices.getIconTheme().registerDefault(
				"select-by-buffer-icon",
				this.getClass().getClassLoader()
						.getResource("images/select-by-buffer-icon.png"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		if (actionCommand.equals("SELBUFFER")) {
			IWindow window = PluginServices.getMDIManager().getActiveWindow();

			if (window instanceof View) {
				View view = (View) window;
				IProjectView model = ((View) window).getModel();

				/*
				 * Unavaliable tool with views in geographic projections
				 */
				CoordinateReferenceSystem crs = view.getMapControl().getCrs();
				if (!(crs instanceof ProjectedCRS)) {
					JOptionPane
							.showMessageDialog(
									null,
									PluginServices
											.getText(null,
													"Tool_unavaliable_with_view_in_geographic_projection"),
									PluginServices.getText(this, "Warning"),
									JOptionPane.ERROR_MESSAGE);
					return;
				}

				MapContext mapContext = model.getMapContext();

				// If there is at least one active vector layer that has
				// geometries selected -> can use this tool, otherwise notifies
				// the
				// limitation in a JOptionPane
				FLayer layers[] = mapContext.getLayers().getActives();
				FLayer layer;
				ArrayList usefulLayers = new ArrayList();
				int emptySelectionLayers = 0;

				for (int i = 0; i < layers.length; i++) {
					layer = layers[i];

					if ((layer instanceof FLyrVect) && (layer.isAvailable())
							&& (layer.isActive())) {
						try {
							usefulLayers.add((FLyrVect) layer);
							if (((FLyrVect) layer).getSource().getRecordset()
									.getSelection().cardinality() == 0) {
								emptySelectionLayers++;
							}
						} catch (ReadDriverException rde) {
							JOptionPane.showMessageDialog(
									null,
									PluginServices.getText(null,
											"Failed_selecting_layer")
											+ ": "
											+ layer.getName(), PluginServices
											.getText(null, "Warning"),
									JOptionPane.WARNING_MESSAGE);
						}
					}
				}

				if (usefulLayers.size() == 0
						|| emptySelectionLayers == usefulLayers.size()) {
					JOptionPane.showMessageDialog(null, PluginServices.getText(
							null, "There_are_no_geometries_selected"),
							PluginServices.getText(null, "Warning"),
							JOptionPane.WARNING_MESSAGE);

					return;
				}

				// Creates and displays the configuration panel
				PluginServices.getMDIManager().addWindow(
						new BufferConfigurationPanel((FLyrVect[]) usefulLayers
								.toArray(new FLyrVect[0]), (View) window));
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

			/*
			 * Unavaliable tool with views in geographic projections
			 */
			if (!(vista.getMapControl().getCrs() instanceof ProjectedCRS)) {
				return false;
			}

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
