/*
 * Created on 22-jun-2005
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
package com.iver.gvsig.addeventtheme;

import java.util.ArrayList;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.AddLayer;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.gvsig.addeventtheme.gui.AddEventThemePanel;

/**
 * The AddEventThemeExtension class allows to create a new point Layer in gvSIG
 * from an existing gvSIG Table.
 * 
 * @author jmorell
 */
public class AddEventThemeExtension extends Extension {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.Extension#inicializar()
	 */
	public void initialize() {
		// TODO Auto-generated method stub
		registerIcons();
	}

	private void registerIcons() {
		PluginServices.getIconTheme().registerDefault(
				"view-addevent-layer",
				AddLayer.class.getClassLoader().getResource(
						"images/addeventtheme.png"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.Extension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		// De la vista
		View vista = (View) PluginServices.getMDIManager().getActiveWindow();
		MapContext mapContext = vista.getModel().getMapContext();
		// Del proyecto (las tablas)
		Project project = ((ProjectExtension) PluginServices
				.getExtension(ProjectExtension.class)).getProject();
		ArrayList tableList = project
				.getDocumentsByType(ProjectTableFactory.registerName);
		AddEventThemePanel addEventThemePanel = new AddEventThemePanel(
				mapContext, tableList);
		PluginServices.getMDIManager().addWindow(addEventThemePanel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.Extension#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.Extension#isVisible()
	 */
	public boolean isVisible() {
		Project project = ((ProjectExtension) PluginServices
				.getExtension(ProjectExtension.class)).getProject();
		if (project != null) {
			ArrayList tableList = project
					.getDocumentsByType(ProjectTableFactory.registerName);
			com.iver.andami.ui.mdiManager.IWindow f = PluginServices
					.getMDIManager().getActiveWindow();
			if (!tableList.isEmpty() && f != null && f instanceof View)
				return true;
		}
		return false;
	}

}
