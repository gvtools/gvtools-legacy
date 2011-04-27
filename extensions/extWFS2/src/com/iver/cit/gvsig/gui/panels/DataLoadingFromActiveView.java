package com.iver.cit.gvsig.gui.panels;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.gui.filter.DefaultExpressionDataSource;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;

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

/**
 * This class has specific code for "WFSFilterPanel" extracted from "FiltroExension" that should be used by "WFSFilterPanel" but that has a conflict in the "DriverException" class
 * (because there are 2 classes with the same name "DriverException" but with different code: one in "libFMap" and another in "libGDBMS").
 *
 * @author Pablo Piqueras Bartolomé (p_queras@hotmail.com)
 */
public class DataLoadingFromActiveView {
	/**
	 * Gets the default expression data source of the active window (view or table).
	 * 
	 * @return data of the active view or <code>null</code>
	 */
	public static DefaultExpressionDataSource getDefaultExpressionDataSource() {
		DefaultExpressionDataSource ds = null;
		IWindow window = PluginServices.getMDIManager().getActiveWindow();

		try {
			if ((window == null) || (!(window instanceof BaseView))) {
				return null;
			}
			else {
				// Tries to access to the ActivedWindow and get the data from its selected layer
				SelectableDataSource dataSource = null;

				if (window instanceof Table) {
					Table vista = (Table) window;

					dataSource = (SelectableDataSource)vista.getModel().getModelo().getRecordset();
				} else if (window instanceof BaseView) {
					IProjectView pv = ((BaseView) window).getModel();
					FLayer layer = pv.getMapContext().getLayers().getActives()[0];

					if (layer == null)
						return null;

					dataSource = pv.getProject().getDataSourceByLayer(layer);
				}
	
				// Load values for return them
				ds = new DefaultExpressionDataSource();
				ds.setTable(dataSource);

			}
		} catch (ReadDriverException de) {
			NotificationManager.addError(PluginServices.getText(null, "error_filtering"), de);
			return null;
		}

		return ds;
	}
}
