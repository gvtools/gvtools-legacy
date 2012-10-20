package com.iver.cit.gvsig.project.documents.view.toc.actions;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.TableOperations;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrAnnotation;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.fmap.layers.layerOperations.ClassifiableVectorial;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.ProjectFactory;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.cit.gvsig.project.documents.view.toc.AbstractTocContextMenuAction;
import com.iver.cit.gvsig.project.documents.view.toc.ITocItem;

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
 * Join external table fields to attribute table (1:1).
 * 
 * @author Benjamin Ducke
 */
public class AttJoinTocMenuEntry extends AbstractTocContextMenuAction {
	protected SelectableDataSource dataSource = null;
	private String filterTitle;
	private String tableName = null;

	public String getGroup() {
		return "attTableTOCtools";
	}

	public int getGroupOrder() {
		return 1;
	}

	public int getOrder() {
		return 3;
	}

	public String getText() {
		return PluginServices.getText(this, "join");
	}

	public boolean isEnabled(ITocItem item, FLayer[] selectedItems) {
		if (isTocItemBranch(item)) {
			FLayer lyr = getNodeLayer(item);
			if (lyr.isAvailable()) {
				return selectedItems.length == 1;
			}
		}
		return false;
	}

	public boolean isVisible(ITocItem item, FLayer[] selectedItems) {
		if (isTocItemBranch(item)) {
			FLayer lyr = getNodeLayer(item);
			if ((lyr instanceof ClassifiableVectorial)) {
				if (!((lyr instanceof FLyrVect) && !((FLyrVect) lyr)
						.isPropertiesMenuVisible())) {
					if (!(lyr instanceof FLyrAnnotation))
						return true;
				}

			}
		}
		return false;

	}

	public void execute(ITocItem item, FLayer[] selectedItems) {

		/* add current layer's attribute table to project */
		FLayer lyr = getNodeLayer(item);
		if (!lyr.isAvailable())
			return;

		try {
			if (lyr instanceof AlphanumericData) {
				AlphanumericData co = (AlphanumericData) lyr;
				ProjectExtension ext = (ProjectExtension) PluginServices
						.getExtension(ProjectExtension.class);

				ProjectTable projectTable = ext.getProject().getTable(co);
				EditableAdapter ea = null;
				ReadableVectorial rv = ((FLyrVect) lyr).getSource();
				if (rv instanceof VectorialEditableAdapter) {
					ea = (EditableAdapter) ((FLyrVect) lyr).getSource();
				} else {
					ea = new EditableAdapter();
					SelectableDataSource sds = ((FLyrVect) lyr).getRecordset();
					ea.setOriginalDataSource(sds);
				}

				if (projectTable == null) {
					projectTable = ProjectFactory.createTable(
							PluginServices.getText(this, "Tabla_de_Atributos")
									+ ": " + lyr.getName(), ea);
					projectTable
							.setProjectDocumentFactory(new ProjectTableFactory());
					projectTable.setAssociatedTable(co);
					ext.getProject().addDocument(projectTable);
					projectTable.setModel(ea);
				}
				Table t = new Table();
				t.setModel(projectTable);
				if (ea.isEditing())
					ea.getCommandRecord().addCommandListener(t);
				t.getModel().setModified(true);
				tableName = t.getModel().getName();
			}
		} catch (ReadDriverException e) {
			NotificationManager.addError(PluginServices.getText(this,
					"No_se_pudo_obtener_la_tabla_de_la_capa"), e);
		}

		Project project = ((ProjectExtension) PluginServices
				.getExtension(ProjectExtension.class)).getProject();
		project.setModified(true);

		/* call the Join extension with the right action and the table name */
		TableOperations TO = null;
		TO = new TableOperations();
		TO.execute("JOIN", tableName);
	}
}
