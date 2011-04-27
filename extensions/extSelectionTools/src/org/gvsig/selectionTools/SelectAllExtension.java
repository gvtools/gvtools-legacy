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

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;

import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * <p>Extension to add support for selecting all the features of a vector layer.</p>
 *
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class SelectAllExtension extends Extension {
	/*
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		if (actionCommand.equals("SELALL") ) {
			IWindow view = PluginServices.getMDIManager().getActiveWindow();
			if (view instanceof View) {
				MapControl mc = ((View)view).getMapControl();
				FLayer[] activeLayers = mc.getMapContext().getLayers().getActives();

				FLayer layer;
				SelectableDataSource sds;
				FBitSet selectedRows;

				for (int i = 0; i < activeLayers.length; i++) {
					layer = activeLayers[i];

					if ((layer.isAvailable()) && (layer instanceof FLyrVect)) {
						FLyrVect lyrVect = (FLyrVect) layer;

						try {
							sds = lyrVect.getRecordset();
							selectedRows = sds.getSelection();
							selectedRows.set(0, (int)sds.getRowCount()); // Selects all features
							sds.setSelection(selectedRows);
						} catch (ReadDriverException e) {
							NotificationManager.showMessageError("Driver exception", e);
						}
					}
				}
			}
		}
	}

	/*
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager().getActiveWindow();

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
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager().getActiveWindow();

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
				
				if ((layer instanceof FLyrVect) && (layer.isAvailable()) && (layer.isActive()))
					return true;
			}
		}

		return false;
	}
}
