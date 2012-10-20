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

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.JOptionPane;

import com.hardcode.driverManager.Driver;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.exceptions.table.StartEditingTableException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.project.documents.table.gui.Table;

/**
 * DOCUMENT ME!
 * 
 * @author Vicente Caballero Navarro
 */
public class TableEditStartExtension extends Extension {
	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		if ("STARTEDIT".equals(actionCommand)) {
			IWindow v = PluginServices.getMDIManager().getActiveWindow();

			try {
				Table table = (Table) v;
				Driver drv = table.getModel().getModelo().getOriginalDriver();
				if (drv instanceof IWriteable) {
					IWriter writer = ((IWriteable) drv).getWriter();
					if (!writer.canSaveEdits()) {
						JOptionPane.showMessageDialog(
								(Component) PluginServices.getMDIManager()
										.getActiveWindow(),
								PluginServices.getText(this,
										"this_table_is_not_self_editable"),
								PluginServices.getText(this, "warning"),
								JOptionPane.WARNING_MESSAGE);
						return;
					}
				} else {
					JOptionPane.showMessageDialog((Component) PluginServices
							.getMDIManager().getActiveWindow(), PluginServices
							.getText(this, "this_table_is_not_self_editable"),
							PluginServices.getText(this, "warning"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				table.startEditing();
				/*
				 * IEditableSource edTable = (IEditableSource)
				 * table.getModel().getAssociatedTable();
				 * edTable.getCommandRecord().addCommandListener(table);
				 */

			} catch (StartEditingTableException e) {
				e.printStackTrace();
			} catch (HeadlessException e) {
				e.printStackTrace();
			} catch (VisitorException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();

		if (v == null) {
			return false;
		}
		if (v instanceof Table) {
			Table t = (Table) v;
			IEditableSource ies = t.getModel().getModelo();
			// FJP:
			// Si está linkada, por ahora no dejamos editar
			// TODO: Esto evita la edición en un sentido, pero no en el otro
			// Hay que permitir la edición, pero evitar que toquen el/los
			// campos de unión. Para eso tendremos que añadir alguna función
			// que indique si un campo está involucrado en alguna unión, o
			// quizás algo más genérico, algo que permita bloquear campos
			// para que no se puedan editar.
			if (t.getModel().getLinkTable() != null)
				return false;
			if (ies.getOriginalDriver() instanceof IWriteable) {
				return true;
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

		if (v instanceof Table && !((Table) v).isEditing()
				&& ((Table) v).getModel().getAssociatedTable() == null) {
			return true;
		}

		return false;
	}
}
