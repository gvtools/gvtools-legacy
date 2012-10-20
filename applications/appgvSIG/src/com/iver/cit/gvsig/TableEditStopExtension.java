package com.iver.cit.gvsig;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.andami.plugins.IExtension;
import com.iver.andami.plugins.status.IExtensionStatus;
import com.iver.andami.plugins.status.IUnsavedData;
import com.iver.andami.plugins.status.UnsavedData;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.exceptions.table.CancelEditingTableException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.utiles.swing.threads.IMonitorableTask;

/**
 * DOCUMENT ME!
 * 
 * @author Vicente Caballero Navarro
 */
public class TableEditStopExtension extends Extension {
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
		Table table = (Table) v;
		if ("STOPEDITING".equals(actionCommand)) {
			stopEditing(table);
		}
		/*
		 * IEditableSource edTable = (IEditableSource)
		 * table.getModel().getAssociatedTable();
		 * edTable.getCommandRecord().removeCommandListener(table);
		 */

	}

	/**
	 * DOCUMENT ME!
	 */
	public void stopEditing(Table table) {
		int resp = JOptionPane.showConfirmDialog(null,
				PluginServices.getText(this, "realmente_desea_guardar") + " : "
						+ table.getModel().getName(), "Guardar",
				JOptionPane.YES_NO_OPTION);
		if (resp == JOptionPane.NO_OPTION) { // CANCEL EDITING
			try {
				table.cancelEditing();
			} catch (CancelEditingTableException e) {
				e.printStackTrace();
			}
		} else { // GUARDAMOS LA TABLA
			table.stopEditing();
		}
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();

		if (v == null) {
			return false;
		} else if (v instanceof Table && ((Table) v).isEditing()
				&& ((Table) v).getModel().getAssociatedTable() == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <p>
	 * This class provides the status of extensions. If this extension has some
	 * unsaved editing table (and save them), and methods to check if the
	 * extension has some associated background tasks.
	 * 
	 * @author Vicente Caballero Navarro
	 * 
	 */
	private class StopEditingStatus implements IExtensionStatus {
		/**
		 * This method is used to check if this extension has some unsaved
		 * editing tables.
		 * 
		 * @return true if the extension has some unsaved editing tables, false
		 *         otherwise.
		 */
		public boolean hasUnsavedData() {
			ProjectExtension pe = (ProjectExtension) PluginServices
					.getExtension(ProjectExtension.class);
			ProjectTable[] tables = (ProjectTable[]) pe.getProject()
					.getDocumentsByType(ProjectTableFactory.registerName)
					.toArray(new ProjectTable[0]);
			for (int i = 0; i < tables.length; i++) {
				if (tables[i].getModelo() == null) {
					continue;
				}
				if (tables[i].getModelo().isEditing())
					return true;
			}
			return false;
		}

		/**
		 * This method is used to check if the extension has some associated
		 * background process which is currently running.
		 * 
		 * @return true if the extension has some associated background process,
		 *         false otherwise.
		 */
		public boolean hasRunningProcesses() {
			return false;
		}

		/**
		 * <p>
		 * Gets an array of the traceable background tasks associated with this
		 * extension. These tasks may be tracked, canceled, etc.
		 * </p>
		 * 
		 * @return An array of the associated background tasks, or null in case
		 *         there is no associated background tasks.
		 */
		public IMonitorableTask[] getRunningProcesses() {
			return null;
		}

		/**
		 * <p>
		 * Gets an array of the UnsavedData objects, which contain information
		 * about the unsaved editing tables and allows to save it.
		 * </p>
		 * 
		 * @return An array of the associated unsaved editing layers, or null in
		 *         case the extension has not unsaved editing tables.
		 */
		public IUnsavedData[] getUnsavedData() {
			ProjectExtension pe = (ProjectExtension) PluginServices
					.getExtension(ProjectExtension.class);
			ProjectTable[] tables = (ProjectTable[]) pe.getProject()
					.getDocumentsByType(ProjectTableFactory.registerName)
					.toArray(new ProjectTable[0]);
			ArrayList unsavedTables = new ArrayList();
			for (int i = 0; i < tables.length; i++) {
				if (tables[i].getModelo() == null) {
					continue;
				}
				if (tables[i].getModelo().isEditing()) {
					UnsavedTable ul = new UnsavedTable(
							TableEditStopExtension.this);
					ul.setTable(tables[i]);
					unsavedTables.add(ul);
				}
			}
			return (IUnsavedData[]) unsavedTables.toArray(new IUnsavedData[0]);
		}
	}

	private class UnsavedTable extends UnsavedData {

		private ProjectTable table;

		public UnsavedTable(IExtension extension) {
			super(extension);
		}

		public String getDescription() {
			return PluginServices.getText(this, "editing_table_unsaved");
		}

		public String getResourceName() {
			return table.getName();
		}

		public boolean saveData() {
			return executeSaveTable(table);
		}

		public void setTable(ProjectTable table) {
			this.table = table;

		}
	}

	// TODO Este código está duplicado, también está en la clase Table en el
	// método "public void stopEditing()"
	private boolean executeSaveTable(ProjectTable table2) {
		IEditableSource ies = table2.getModelo();
		if (ies instanceof IWriteable) {
			IWriteable w = (IWriteable) ies;
			IWriter writer = w.getWriter();
			if (writer == null) {
				return false;
			}
			try {
				ITableDefinition tableDef = ies.getTableDefinition();
				writer.initialize(tableDef);
				ies.stopEdition(writer, EditionEvent.ALPHANUMERIC);
				ies.getSelection().clear();
			} catch (InitializeWriterException e) {
				NotificationManager.addError(
						PluginServices.getText(this, "error_saving_table"), e);
				return false;
			} catch (StopWriterVisitorException e) {
				NotificationManager.addError(
						PluginServices.getText(this, "error_saving_table"), e);
				return false;
			} catch (ReadDriverException e) {
				NotificationManager.addError(
						PluginServices.getText(this, "error_saving_table"), e);
				return false;
			}
		}
		return true;
	}

	public IExtensionStatus getStatus() {
		return new StopEditingStatus();
	}
}
