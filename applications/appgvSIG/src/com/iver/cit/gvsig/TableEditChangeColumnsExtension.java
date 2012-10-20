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

import java.sql.Types;

import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.project.documents.table.gui.Table;

/**
 * DOCUMENT ME!
 * 
 * @author Vicente Caballero Navarro
 */
public class TableEditChangeColumnsExtension extends Extension {
	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();
		Table t = (Table) v;
		if ("REMOVECOLUMN".equals(actionCommand)) {
			t.removeColumn();

		}
		if ("ADDCOLUMN".equals(actionCommand)) {
			FieldDescription newField = new FieldDescription();
			newField.setDefaultValue(ValueFactory.createValue("default"));
			newField.setFieldName("prueba");
			newField.setFieldType(Types.VARCHAR);
			newField.setFieldLength(20);

			t.addColumn(newField);
		}
		if ("RENAMECOLUMN".equals(actionCommand)) {
			// TODO RENAME
			// t.addColumn();
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
			return (((Table) v).isEditing())
					&& ((Table) v).getSelectedFieldIndices().cardinality() > 0;
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
		} else if (v instanceof Table && ((Table) v).isEditing()) {
			return true;
		} else {
			return false;
		}
	}
}
