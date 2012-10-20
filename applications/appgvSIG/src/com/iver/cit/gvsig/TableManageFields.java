/*
 * Created on 02-mar-2004
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

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.gui.DlgFieldManager;
import com.iver.cit.gvsig.project.documents.table.gui.Table;

/**
 * Extensión que abre las tablas asociadas a las vistas.
 * 
 * @author Vicente Caballero Navarro
 */
public class TableManageFields extends Extension {

	/**
	 * @see com.iver.mdiApp.plugins.IExtension#updateUI(java.lang.String)
	 */
	public void execute(String s) {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();

		Table t = (Table) v;
		ProjectTable pt = t.getModel();
		IEditableSource ies = pt.getModelo();
		if (ies.getOriginalDriver() instanceof IWriteable) {
			IWriteable aux = (IWriteable) ies.getOriginalDriver();
			IWriter writer = aux.getWriter();
			// No todos los writer pueden cambiar los campos
			if (writer.canAlterTable()) {
				// IFieldManager fieldManager = (IFieldManager) writer;
				DlgFieldManager dlg = new DlgFieldManager(ies);
				PluginServices.getMDIManager().addWindow(dlg);
			}
		}

	}

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
	}

	public boolean isEnabled() {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();

		if (v == null) {
			return false;
		}

		if (v instanceof Table) {
			Table t = (Table) v;
			IEditableSource ies = t.getModel().getModelo();
			// ProjectTable pt = t.getModel();
			if (ies.getOriginalDriver() instanceof IWriteable) {
				IWriter writer = ((IWriteable) ies.getOriginalDriver())
						.getWriter();
				if ((writer != null) && (writer.canAlterTable())) {
					if (ies.isEditing()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();

		if (v == null) {
			return false;
		}

		if (v instanceof Table) {
			return true;
		}
		return false;
	}

}
