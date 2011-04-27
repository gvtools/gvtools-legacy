package com.iver.cit.gvsig;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.BitSet;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.selection.SelectionFieldIterator;
import com.iver.cit.gvsig.project.documents.table.Statistics.NonNumericFieldException;
import com.iver.cit.gvsig.project.documents.table.gui.Statistics;
import com.iver.cit.gvsig.project.documents.table.gui.Table;

/**
 * @author Fernando González Cortés
 */
public class TableNumericFieldOperations extends Extension{

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		registerIcons();
	}

	private void registerIcons(){
    	PluginServices.getIconTheme().registerDefault(
				"table-statistics",
				this.getClass().getClassLoader().getResource("images/statistics.png")
			);
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();

		if (v != null) {
			if (v.getClass() == Table.class) {
				Table table = (Table) v;
				doExecute(table);
			}
		}
	}
	
	/**
	 * "execute" method acction
	 * @param actionCommand
	 * The acction command that executes this method
	 * @param table
	 * Table to operate
	 */

	protected void doExecute(Table table){
		int fieldIndex = table.getSelectedFieldIndices().nextSetBit(0);
		try {
			SelectableDataSource ds = table.getModel().getModelo().getRecordset();
			BitSet selectedRows = (BitSet)ds.getSelection().clone();
			// If selection is empty, calculate on the full datasource
			if (selectedRows.cardinality() == 0){
				selectedRows.set(0, (int) ds.getRowCount());
			}
			SelectionFieldIterator iterator = new SelectionFieldIterator(ds, selectedRows, fieldIndex);
			com.iver.cit.gvsig.project.documents.table.Statistics statsCalculator = new com.iver.cit.gvsig.project.documents.table.Statistics(iterator);
			BigDecimal sum = statsCalculator.sum();
			BigDecimal min = statsCalculator.min();
			BigDecimal max = statsCalculator.max();
			BigDecimal mean = statsCalculator.mean();
			BigDecimal variance = statsCalculator.variance();
			BigDecimal stdDeviation = statsCalculator.stdDeviation();
			
			Statistics st = new Statistics();
			st.setStatistics(mean.doubleValue(),
					max.doubleValue(),
					min.doubleValue(),
					variance.doubleValue(),
					stdDeviation.doubleValue(),
					(int) statsCalculator.count(),
					new BigDecimal(max.doubleValue()).subtract(min).doubleValue(),
					sum.doubleValue());
			PluginServices.getMDIManager().addWindow(st);
		} catch (ReadDriverException e) {
			NotificationManager.showMessageError(
					PluginServices.getText(this, "Statistics__Error_accessing_the_data"), e);
		} catch (NonNumericFieldException e) {
			NotificationManager.showMessageError("Statistics__Selected_field_is_not_numeric", e);
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
			Table table = (Table) v;
			return doIsEnabled(table);
		}
		return false;
	}

	protected boolean doIsEnabled(Table table){
		BitSet indices = table.getSelectedFieldIndices();
		// System.out.println("TableNumericFieldOperations.isEnabled: Tabla: " + table.getModel().getModelo().getRecordset().getName() );
		if (indices.cardinality() == 1){
			try {
				int index=indices.nextSetBit(0);
				if (table.getModel().getModelo().getRecordset().getFieldCount()<index+1)
					return false;
				int type = table.getModel().getModelo().getRecordset().getFieldType(index);
				if ((type == Types.BIGINT) ||
						(type == Types.DECIMAL) ||
						(type == Types.DOUBLE) ||
						(type == Types.FLOAT) ||
						(type == Types.INTEGER) ||
						(type == Types.SMALLINT) ||
						(type == Types.TINYINT) ||
						(type == Types.REAL) ||
						(type == Types.NUMERIC)){
					return true;
				}
			} catch (ReadDriverException e) {
				return false;
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
