/**
 *
 */
package com.iver.cit.gvsig.project.documents.table.gui;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;

import jwizardcomponent.FinishAction;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.TableOperations;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.gui.simpleWizard.SimpleWizard;
import com.iver.cit.gvsig.project.documents.table.FieldSelectionModel;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;

public class JoinWizardController {
	private final TableOperations tableOperations;
	private final String defaultSourceTable;

	/**
	 * @param tableOperations
	 */
	public JoinWizardController(TableOperations tableOperations) {
		defaultSourceTable = null;
		this.tableOperations = tableOperations;
	}

	/* initialize with a default source table name; this is useful for calling
	   JOIN from the layer context menu in the TOC */
	public JoinWizardController(TableOperations tableOperations, String defaultTableName) {
		defaultSourceTable = defaultTableName;
		this.tableOperations = tableOperations;
	}	
	

	public void runWizard(ProjectTable[] pts) {
		// create wizard
		ImageIcon logo = PluginServices.getIconTheme().get("table-join");
		final SimpleWizard wizard = new SimpleWizard(logo);
		wizard.getWindowInfo().setTitle(PluginServices.getText(this, "Table_Join"));

		// create first step (source table)
		final TableWizardStep srcTableWzrd = new TableWizardStep(wizard.getWizardComponents(), "Title" );
		srcTableWzrd.getHeaderLbl().setText(PluginServices.getText(this,"Source_table_options"));
		srcTableWzrd.getTableNameLbl().setText(PluginServices.getText(this,"Source_table_"));
		srcTableWzrd.getFieldNameLbl().setText(PluginServices.getText(this,"Field_to_use_for_JOIN_"));
		srcTableWzrd.getFieldPrefixLbl().setText(PluginServices.getText(this,"Field_prefix_"));
		srcTableWzrd.getTableNameCmb().addItemListener(
				new ItemListener() {

					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange()==e.SELECTED) {
							ProjectTable pt = (ProjectTable) srcTableWzrd.getTableNameCmb().getSelectedItem();

							try {
								srcTableWzrd.setFieldModel(new FieldSelectionModel(
										pt.getModelo().getRecordset(),
										PluginServices.getText(this, "seleccione_campo_enlace"),
										-1));
								srcTableWzrd.getFieldPrefixTxt().setText(tableOperations.sanitizeFieldName(pt.getName()));
							} catch (ReadDriverException e1) {
								NotificationManager.addError(
										PluginServices.getText(this, "Error_getting_table_fields"),
										e1);
							}
						}

					}
				}
		);
		
		/* if the class has been initialized with the name of a specific table,
		   then add that one to the top of the list and deactivate the drop-down list */
		if ( defaultSourceTable != null) {
			for (int i=0; i<pts.length; i++) {
				if (pts[i].getName() == defaultSourceTable ) {
					if (!pts[i].getModelo().isEditing()) {
						srcTableWzrd.getTableNameCmb().addItem(pts[i]);
						srcTableWzrd.getTableNameCmb().setEditable(false);
						srcTableWzrd.getTableNameCmb().setEnabled(false);
					}
				}
			}
		} else {
			/* not initialized with a default source table name: add all tables to list */
			for (int i=0; i<pts.length; i++) {
				if (!pts[i].getModelo().isEditing())
					srcTableWzrd.getTableNameCmb().addItem(pts[i]);
			}
		}

		// create second step (target table)
		final TableWizardStep targTableWzrd = new TableWizardStep(wizard.getWizardComponents(), "Title" );
		targTableWzrd.getHeaderLbl().setText(PluginServices.getText(this,"Target_table_options"));
		targTableWzrd.getTableNameLbl().setText(PluginServices.getText(this,"Target_table_"));
		targTableWzrd.getFieldNameLbl().setText(PluginServices.getText(this,"Field_to_use_for_JOIN_"));
		targTableWzrd.getFieldPrefixLbl().setText(PluginServices.getText(this,"Field_prefix_"));
		targTableWzrd.getTableNameCmb().addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange()==e.SELECTED)
						setTrgtWzrdFieldType(srcTableWzrd,targTableWzrd);
				}
			}
		);
		
		srcTableWzrd.getFieldNameCmb().addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==e.SELECTED)
					setTrgtWzrdFieldType(srcTableWzrd,targTableWzrd);
			}
		});
		
		/* if running with a default table, we will simplify the target selection 
		   list by excluding the default table */
		if ( defaultSourceTable != null) { 
			for (int i=0; i<pts.length; i++) {
				if (pts[i].getName() != defaultSourceTable)
					if (!pts[i].getModelo().isEditing())
						targTableWzrd.getTableNameCmb().addItem(pts[i]);
			}
		} else {
			/* no default table: user has full flexibility */
			for (int i=0; i<pts.length; i++) {
				if (!pts[i].getModelo().isEditing())
					targTableWzrd.getTableNameCmb().addItem(pts[i]);
			}
		}

		// add steps and configure wizard
		wizard.getWizardComponents().addWizardPanel(srcTableWzrd);
		wizard.getWizardComponents().addWizardPanel(targTableWzrd);
		wizard.getWizardComponents().updateComponents();
		wizard.setSize(new Dimension(450, 230));
		wizard.getWizardComponents().setFinishAction(new FinishAction(wizard.getWizardComponents()) {
			public void performAction() {
				ProjectTable sourceProjectTable = (ProjectTable) srcTableWzrd.getTableNameCmb().getSelectedItem();
				String field1 = (String) srcTableWzrd.getFieldNameCmb().getSelectedItem();
				String prefix1 = srcTableWzrd.getFieldPrefixTxt().getText();
				if (sourceProjectTable==null || field1==null || prefix1 == null) {
					NotificationManager.showMessageError(
							PluginServices.getText(this, "Join_parameters_are_incomplete"), new InvalidParameterException());
					return;
				}
				ProjectTable targetProjectTable = (ProjectTable) targTableWzrd.getTableNameCmb().getSelectedItem();
				String field2 = (String) targTableWzrd.getFieldNameCmb().getSelectedItem();
				String prefix2 = targTableWzrd.getFieldPrefixTxt().getText();
				if (targetProjectTable==null || field2==null || prefix2 == null) {
					NotificationManager.showMessageError(
							PluginServices.getText(this, "Join_parameters_are_incomplete"), new InvalidParameterException());
					return;
				}
				tableOperations.execJoin(sourceProjectTable, field1, prefix1, targetProjectTable, field2, prefix2);

				PluginServices.getMDIManager().closeWindow(wizard);
			}
		}
		);

		// show the wizard
		PluginServices.getMDIManager().addWindow(wizard);
	}
	
	private void setTrgtWzrdFieldType(TableWizardStep srcTableWzrd,TableWizardStep targTableWzrd) {
		try {
			
			ProjectTable sourcePt = (ProjectTable) srcTableWzrd.getTableNameCmb().getSelectedItem();
			ProjectTable targetPt = (ProjectTable) targTableWzrd.getTableNameCmb().getSelectedItem();
			targTableWzrd.getFieldPrefixTxt().setText(tableOperations.sanitizeFieldName(targetPt.getName()));
			
			//índice del campo
			SelectableDataSource sds = sourcePt.getModelo().getRecordset();
			String fieldName = (String) srcTableWzrd.getFieldNameCmb().getSelectedItem();
			int fieldIndex = sds.getFieldIndexByName(fieldName);
			if (fieldIndex!=-1) {
				int type = sds.getFieldType(fieldIndex);
				targTableWzrd.setFieldModel(new FieldSelectionModel(
						targetPt.getModelo().getRecordset(),
						PluginServices.getText(this, "seleccione_campo_enlace"),
						type));
			}
			else {
				NotificationManager.addError(PluginServices.getText(this, "Error_getting_table_fields")
						, new Exception());
			}
		} catch (ReadDriverException e2) {
			NotificationManager.addError(PluginServices.getText(this, "Error_getting_table_fields"),
					e2);
		}
	}

	private class InvalidParameterException extends Exception {
	}
}