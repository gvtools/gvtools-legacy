package org.gvsig.tableImport.importfields;

import jwizardcomponent.FinishAction;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.wizard.WizardAndami;
import com.iver.cit.gvsig.project.documents.table.gui.Table;

public class ImportFieldsAction extends FinishAction {

	private WizardAndami wizard;
	private ImportFieldParams params;

	public ImportFieldsAction(WizardAndami wizard, ImportFieldParams params) {
		super(wizard.getWizardComponents());

		this.wizard = wizard;
		this.params = params;
	}

	public void performAction() {
		if (params.isValid()) {
			ImportFieldsExtension ext = (ImportFieldsExtension) PluginServices
					.getExtension(ImportFieldsExtension.class);
			try {
				ext.doImportField(this.params);
			} catch (Exception e) {
				NotificationManager.addError(e);
				return;
			}

		}
		PluginServices.getMDIManager().closeWindow(this.wizard);

		// TODO: Cuando la tabla es de una capa y no esta en edición no se
		// refresca
		IWindow[] windows = PluginServices.getMDIManager().getAllWindows();
		Table tableWindow;
		for (int i = 0; i < windows.length; i++) {
			if (windows[i] instanceof Table) {
				tableWindow = (Table) windows[i];
				if (tableWindow.getModel().equals(params.getTable())) {
					try {
						params.getTable().createAlias();
					} catch (ReadDriverException e) {
						e.printStackTrace();
					}
					tableWindow.setModel(params.getTable());
					break;
				}
			}
		}
	}

}
