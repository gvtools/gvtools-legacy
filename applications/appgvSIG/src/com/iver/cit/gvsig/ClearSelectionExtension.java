/*
 * Created on 31-may-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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
package com.iver.cit.gvsig;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;

/**
 * Extensión encargada de limpiar la selección.
 * 
 * @author Vicente Caballero Navarro
 */
public class ClearSelectionExtension extends Extension {
	/**
	 * @see com.iver.mdiApp.plugins.IExtension#updateUI(java.lang.String)
	 */
	public void execute(String s) {
		if (s.compareTo("DEL_SELECTION") == 0) {
			boolean refresh = false;
			com.iver.andami.ui.mdiManager.IWindow view = PluginServices
					.getMDIManager().getActiveWindow();

			if (view instanceof BaseView) {
				BaseView vista = (BaseView) view;
				IProjectView model = vista.getModel();
				MapContext mapa = model.getMapContext();
				MapControl mapCtrl = vista.getMapControl();
				FLayers layers = mapa.getLayers();
				refresh = clearSelectionOfView(layers);

				if (refresh) {
					mapCtrl.drawMap(false);
				}
				((ProjectDocument) vista.getModel()).setModified(true);
			} else if (view instanceof Table) {
				Table table = (Table) view;
				ProjectTable model = table.getModel();
				SelectableDataSource dataSource = null;
				try {
					dataSource = model.getModelo().getRecordset();
				} catch (ReadDriverException e) {
					e.printStackTrace();
				}
				if (dataSource.getSelection().cardinality() != 0) {
					refresh = true;
				}
				dataSource.clearSelection();
				if (refresh) {
					table.refresh();
				}
				table.getModel().setModified(true);
			}
		}
	}

	private boolean clearSelectionOfView(FLayers layers) {
		boolean refresh = false;

		for (int i = 0; i < layers.getLayersCount(); i++) {
			FLayer lyr = layers.getLayer(i);
			if (lyr instanceof FLayers) {
				refresh = refresh || clearSelectionOfView((FLayers) lyr);
			} else if (lyr instanceof FLyrVect) {
				FLyrVect lyrVect = (FLyrVect) lyr;
				if (lyrVect.isActive()) {
					try {
						SelectableDataSource dataSource;

						dataSource = ((FLyrVect) lyr).getRecordset();
						if (dataSource.getSelection().cardinality() != 0) {
							refresh = true;
						}
						dataSource.clearSelection();
					} catch (ReadDriverException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return refresh;
	}

	/**
	 * @see com.iver.mdiApp.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow view = PluginServices
				.getMDIManager().getActiveWindow();

		if (view == null) {
			return false;
		}
		if (view instanceof BaseView) {
			MapContext mapa = ((BaseView) view).getModel().getMapContext();
			return mapa.getLayers().getLayersCount() > 0;
		}
		if (view instanceof Table) {
			return true;
		}

		return false;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		com.iver.andami.ui.mdiManager.IWindow view = PluginServices
				.getMDIManager().getActiveWindow();
		if (view == null) {
			return false;
		}
		if (view instanceof BaseView) {
			MapContext mapa = ((BaseView) view).getMapControl().getMapContext();
			return hasVectorLayersWithSelection(mapa.getLayers());
		}
		if (view instanceof Table) {
			return ((Table) view).getSelectedRowIndices().length > 0;
		}
		return false;
	}

	private boolean hasVectorLayersWithSelection(FLayers layers) {
		for (int i = 0; i < layers.getLayersCount(); i++) {
			FLayer lyr = layers.getLayer(i);
			if (lyr instanceof FLayers) {
				if (hasVectorLayersWithSelection((FLayers) lyr)) {
					return true;
				}
			} else if (lyr instanceof FLyrVect) {
				FLyrVect lyrVect = (FLyrVect) lyr;
				if (lyrVect.isActive()) {
					if (lyrVect.isAvailable()) {
						try {
							if (lyrVect.getRecordset().getSelection()
									.cardinality() > 0)
								return true;
						} catch (ReadDriverException e) {
							e.printStackTrace();
							NotificationManager.addWarning(
									"Capa " + lyrVect.getName()
											+ " sin recordset correcto", e);
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		registerIcons();
	}

	private void registerIcons() {
		PluginServices.getIconTheme().registerDefault(
				"view-clear-selection",
				this.getClass().getClassLoader()
						.getResource("images/delselection.png"));
	}
}
