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

/*
 * AUTHORS (In addition to CIT):
 * 2008 Prodevelop S.L. main development
 */

package org.gvsig.normalization.extensions;

import java.util.BitSet;

import org.gvsig.normalization.gui.INormPanelModel;
import org.gvsig.normalization.gui.NormPanelModel;
import org.gvsig.normalization.gui.NormalizationPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.gui.Table;

/**
 * Normalization Extension. This extension normalizes strings from a table. This
 * extension uses as a GUI a button and a menu item.
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicent Sanjaime Calvet</a>
 */

public class NormalizationTableExtension extends Extension {

	private static final String NORMALIZE_TABLE = "NORMALIZE_TABLE";

	/**
	 * This method executes the normalization table extension
	 * 
	 * @param actionCommand
	 */
	public void execute(String actionCommand) {

		IWindow iw = PluginServices.getMDIManager().getActiveWindow();
		Table tab = (Table) iw;
		boolean isFile = false;

		// NORMALIZATION TABLE
		if (actionCommand.equalsIgnoreCase(NORMALIZE_TABLE)) {
			// New Model Panel
			boolean isEdit = tab.isEditing();
			INormPanelModel model = NormPanelModel.getInstance();
			model.isFile(false);
			model.setInNewTable(false);
			model.setTab(tab);

			// Get name of the main field
			String nameMainField = getNameMainField(tab);
			model.setFieldToNormalize(nameMainField);

			/* Create the panel */
			NormalizationPanel normPanel = new NormalizationPanel(model,
					isFile, isEdit);
			normPanel.setPs(PluginServices.getPluginServices(this));
			model.registerListener(normPanel);
			// Show panel
			PluginServices.getMDIManager().addWindow(normPanel);

			normPanel.setVisible(true);
		}
	}

	/**
	 * This method initializes some parameters of the extension
	 */
	public void initialize() {

	}

	/**
	 * This method puts available the extension when the table active is being
	 * edited
	 * 
	 * @return enable
	 */
	public boolean isEnabled() {

		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof Table) {
			Table table = (Table) window;
			BitSet columnSelected = table.getSelectedFieldIndices();
			if (!columnSelected.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method puts visible the extension when the active window is a Table
	 * 
	 * @return visible
	 */
	public boolean isVisible() {
		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		// Visible when there are tables in the gvSIG window
		if (window instanceof Table) {
			return true;
		}
		return false;
	}

	/**
	 * This method gets the name of the main field
	 * 
	 * @param _tab
	 * @return name of the selected field
	 */
	private static String getNameMainField(Table _tab) {

		BitSet _columnSelected = _tab.getSelectedFieldIndices();
		int po = _columnSelected.nextSetBit(0);
		ProjectTable oriPT = _tab.getModel();
		IEditableSource ies = oriPT.getModelo();
		FieldDescription[] fd = ies.getFieldsDescription();
		String name = fd[po].getFieldName();
		return name.toUpperCase();
	}

}
