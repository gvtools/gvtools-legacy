package com.iver.cit.gvsig;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.gui.Table;

/**
 * @author Fernando González Cortés
 */
public class RemoveTableUnion extends Extension {

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		Table t = (Table) PluginServices.getMDIManager().getActiveWindow();
		ProjectTable pt = t.getModel();
		try {
			FBitSet old = (FBitSet) pt.getModelo().getSelection().clone();
			pt.restoreDataSource();
			// Por si acaso teníamos seleccionado un campo
			// de los de la unión, no seleccionamos
			// ningún campo.
			t.clearSelectedFields();
			pt.getModelo().getRecordset().setSelection(old);

		} catch (ReadDriverException e) {
			NotificationManager.addError(e.getMessage(), e);
		} catch (DriverLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t.getModel().setModified(true);
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();

		if (v == null) {
			return false;
		}

		if (v.getClass() == Table.class) {
			Table t = (Table) v;
			if (t.getModel().getOriginal() != null) {
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

		if (v instanceof Table) {
			return true;
		} else {
			return false;
		}

	}

}
