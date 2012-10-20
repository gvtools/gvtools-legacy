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
import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.project.documents.table.gui.Table;

/**
 * Extensión encargada de gestionar el rehacer un comando anteriormente
 * deshecho.
 * 
 * @author Vicente Caballero Navarro
 */
public class RedoTableExtension extends Extension {
	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		registerIcons();
	}

	private void registerIcons() {

		PluginServices.getIconTheme()
				.registerDefault(
						"table-redo",
						this.getClass().getClassLoader()
								.getResource("images/Redo.png"));
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String s) {
		Table tabla = (Table) PluginServices.getMDIManager().getActiveWindow();

		if (s.compareTo("REDO") == 0) {
			if (tabla.isEditing()) {
				IEditableSource vea = tabla.getModel().getModelo();
				try {
					vea.redo();
				} catch (EditionCommandException e) {
					NotificationManager
							.addError(
									"Error accediendo a los Drivers para rehacer un comando",
									e);
				}
				try {
					vea.getSelection().clear();
				} catch (ReadDriverException e) {
					e.printStackTrace();
				}
				// tabla.refresh();
			}
			tabla.getModel().setModified(true);
		}
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		Table tabla = (Table) PluginServices.getMDIManager().getActiveWindow();
		// MapControl mapControl = (MapControl) vista.getMapControl();
		// FLayers layers=mapControl.getMapContext().getLayers();
		// for (int i=0;i<layers.getLayersCount();i++){
		if (tabla.getModel().getModelo() instanceof IEditableSource
				&& tabla.isEditing()) {
			IEditableSource vea = tabla.getModel().getModelo();
			if (vea == null)
				return false;
			return vea.getCommandRecord().moreRedoCommands();
		}

		// }
		return false;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices
				.getMDIManager().getActiveWindow();

		if (f == null) {
			return false;
		}

		if (f instanceof Table) {
			return true;
		} else {
			return false;
		}
	}
}
