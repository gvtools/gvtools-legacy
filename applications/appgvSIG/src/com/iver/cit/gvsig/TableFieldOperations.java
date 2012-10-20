package com.iver.cit.gvsig;

import java.io.IOException;
import java.util.BitSet;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.instruction.EvaluationException;
import com.hardcode.gdbms.engine.instruction.SemanticException;
import com.hardcode.gdbms.parser.ParseException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.project.documents.table.gui.Table;

/**
 * @author Fernando González Cortés
 */
public class TableFieldOperations extends Extension {

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		registerIcons();
	}

	private void registerIcons() {
		PluginServices.getIconTheme().registerDefault(
				"table-order-asc",
				this.getClass().getClassLoader()
						.getResource("images/orderasc.png"));

		PluginServices.getIconTheme().registerDefault(
				"table-order-desc",
				this.getClass().getClassLoader()
						.getResource("images/orderdesc.png"));
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();

		if (v != null) {
			if (v instanceof Table) {

				Table table = (Table) v;

				doExecute(actionCommand, table);
				table.getModel().setModified(true);
			}
		}
	}

	/**
	 * "execute" method acction
	 * 
	 * @param actionCommand
	 *            The acction command that executes this method
	 * @param table
	 *            Table to operate
	 */
	protected void doExecute(String actionCommand, Table table) {
		int fieldIndex = table.getSelectedFieldIndices().nextSetBit(0);

		DataSource sds = null;
		try {
			sds = table.getModel().getModelo().getRecordset();
			String dsName = sds.getName();

			String fieldName = sds.getFieldName(fieldIndex);
			String sql = "select * from '" + dsName + "' order by " + fieldName;
			if ("ORDERASC".equals(actionCommand)) {
				sql += " asc;";
			} else {
				sql += " desc;";
			}
			DataSource ds = sds.getDataSourceFactory().executeSQL(sql,
					DataSourceFactory.MANUAL_OPENING);
			table.setOrder(ds.getWhereFilter());
		} catch (ReadDriverException e) {
			NotificationManager.addError("No se pudo ordenar", e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		} catch (DriverLoadException e) {
			NotificationManager.addError("Error con la carga de drivers", e);
		} catch (SemanticException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (EvaluationException e) {
			throw new RuntimeException(e);
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
			BitSet indices = t.getSelectedFieldIndices();
			if (indices.cardinality() == 1) {
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
		}
		return false;
	}

}
