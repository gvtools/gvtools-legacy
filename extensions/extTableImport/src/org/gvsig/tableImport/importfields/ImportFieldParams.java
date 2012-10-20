package org.gvsig.tableImport.importfields;

import java.util.ArrayList;
import java.util.Iterator;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;

public class ImportFieldParams {
	private boolean lockTable = false;
	private ArrayList tableFieldList = null;
	private ArrayList tableFieldList_types = null;
	private ProjectTable table = null;
	private ProjectTable tableToImport = null;
	private ArrayList tableToImportFieldList;
	private ArrayList tableToImportFieldList_types;
	private ArrayList fieldsToImport = null;
	private String tableField = null;
	private String tableToImportField = null;

	public String getTableToImportField() {
		return tableToImportField;
	}

	public void setTableToImportField(String tableToImportField) {
		this.tableToImportField = tableToImportField;
	}

	private String validationMsg = "";

	public boolean isValidLinkParams() {
		this.clearValidationMsg();
		if (this.table == null) {
			this.validationMsg = PluginServices.getText(null, "missing_table");
			return false;
		}
		if (this.tableFieldList == null) {
			this.validationMsg = PluginServices.getText(null,
					"cant_load_table_fields");
			return false;
		}
		if (this.tableToImport == null) {
			this.validationMsg = PluginServices.getText(null,
					"missing_table_to_import");
			return false;
		}
		if (this.tableToImportFieldList == null) {
			this.validationMsg = PluginServices.getText(null,
					"cant_load_table_to_import_fields");
			return false;
		}
		if (this.tableField == null || this.tableField.length() == 0) {
			this.validationMsg = PluginServices.getText(null,
					"missing_table_field_for_link");
			return false;
		}
		if (this.tableToImportField == null
				|| this.tableToImportField.length() == 0) {
			this.validationMsg = PluginServices.getText(null,
					"missing_table_to_import_field_for_link");
			return false;
		}
		if (!this.tableFieldList.contains(this.tableField)) {
			this.validationMsg = PluginServices.getText(null,
					"field_for_link_not_found_in_table");
			return false;
		}
		if (!this.tableToImportFieldList.contains(this.tableToImportField)) {
			this.validationMsg = PluginServices.getText(null,
					"field_for_link_not_found_in_table_to_import");
			return false;
		}
		if (!this.isCompatibleFieldTypes((Integer) this.tableFieldList_types
				.get(this.tableFieldList.indexOf(this.tableField)),
				(Integer) this.tableToImportFieldList_types
						.get(this.tableToImportFieldList
								.indexOf(this.tableToImportField)))) {
			this.validationMsg = PluginServices.getText(null,
					"incompatible_types_of_link_fields");
			return false;

		}

		return true;
	}

	private boolean isCompatibleFieldTypes(Integer object, Integer object2) {
		// TODO implementar mejor
		return object.intValue() == object2.intValue();
	}

	public boolean isValid() {
		this.clearValidationMsg();
		if (!this.isValidLinkParams())
			return false;

		if (this.fieldsToImport == null) {
			this.loadFieldsToImport();
			// this.validationMsg=PluginServices.getText(null,"missing_fields_to_import_list");
			return false;
		}

		boolean isSelectedAField = false;
		boolean haveFieldNameConflict = false;
		Iterator iter = this.fieldsToImport.iterator();
		Iterator iterTable;
		while (iter.hasNext()) {
			FielToImport field = (FielToImport) iter.next();
			if (field.toImport) {
				isSelectedAField = true;
				iterTable = this.tableFieldList.iterator();
				while (iterTable.hasNext()) {
					String next = (String) iterTable.next();
					if (next != null)
						if (next.equals(field.fieldNameToUse)) {
							this.validationMsg = PluginServices.getText(null,
									"field_name_conflict")
									+ ": "
									+ field.fieldNameToUse;
							haveFieldNameConflict = true;
							break;
						}
				}
			}

		}
		if (!isSelectedAField) {
			this.validationMsg = PluginServices.getText(null,
					"missing_field_to_import");
			return false;
		}
		return !haveFieldNameConflict;

	}

	private void loadFieldsToImport() {
		Iterator iter = this.tableToImportFieldList.iterator();
		if (this.fieldsToImport == null) {
			this.fieldsToImport = new ArrayList();
		}
		FielToImport field;
		while (iter.hasNext()) {
			field = new FielToImport();
			field.originalFieldName = (String) iter.next();
			field.fieldNameToUse = field.originalFieldName;
			this.addFieldsToImportContains(field);
		}
	}

	private void addFieldsToImportContains(FielToImport field) {
		Iterator iter = this.fieldsToImport.iterator();
		FielToImport old;
		boolean found = false;
		while (iter.hasNext()) {
			old = (FielToImport) iter.next();
			if (old.originalFieldName.equals(field.originalFieldName)) {
				found = true;
				break;
			}
		}
		if (!found) {
			this.fieldsToImport.add(field);
		}

	}

	public class FielToImport {
		public boolean toImport = false;
		public String originalFieldName = null;
		public String fieldNameToUse = null;
	}

	public ProjectTable getTable() {
		return table;
	}

	public void setTable(ProjectTable table) throws ReadDriverException {
		if (this.table == table)
			return;
		this.table = table;
		this.fieldsToImport = null;
		this.tableFieldList = null;
		this.tableFieldList_types = null;
		this.tableField = null;

		if (this.table == null) {
			return;
		}

		try {
			SelectableDataSource ds = this.table.getModelo().getRecordset();
			ds.start();

			String[] names = ds.getFieldNames();
			this.tableFieldList = new ArrayList(names.length);
			this.tableFieldList_types = new ArrayList(names.length);
			for (int i = 0; i < names.length; i++) {
				this.tableFieldList.add(names[i]);
				this.tableFieldList_types.add(new Integer(ds.getFieldType(i)));
			}

			ds.stop();
		} catch (ReadDriverException e) {
			this.tableFieldList = null;
			this.tableFieldList_types = null;
			throw e;
		}

	}

	public ProjectTable getTableToImport() {
		return tableToImport;
	}

	public void setTableToImport(ProjectTable tableToImport)
			throws ReadDriverException {

		if (this.tableToImport == tableToImport) {
			return;
		}
		this.fieldsToImport = null;
		this.tableToImportFieldList = null;
		this.tableToImportFieldList_types = null;
		this.tableToImportField = null;

		this.tableToImport = tableToImport;

		if (this.tableToImport == null) {
			return;
		}

		try {
			SelectableDataSource ds = this.tableToImport.getModelo()
					.getRecordset();
			ds.start();

			String[] names = ds.getFieldNames();
			this.tableToImportFieldList = new ArrayList(names.length);
			this.tableToImportFieldList_types = new ArrayList(names.length);
			for (int i = 0; i < names.length; i++) {
				this.tableToImportFieldList.add(names[i]);
				this.tableToImportFieldList_types.add(new Integer(ds
						.getFieldType(i)));
			}

			ds.stop();
		} catch (ReadDriverException e) {
			this.tableToImportFieldList = null;
			this.tableToImportFieldList_types = null;
			throw e;
		}

	}

	public ArrayList getFieldsToImport() {
		return fieldsToImport;
	}

	public boolean isLockTable() {
		return lockTable;
	}

	public void setLockTable(boolean lockTable) {
		this.lockTable = lockTable;
	}

	public void clearValidationMsg() {
		this.validationMsg = "";
	}

	public String getValidationMsg() {
		return validationMsg;
	}

	public String getTableField() {
		return tableField;
	}

	public void setTableField(String tableField) {
		this.tableField = tableField;
	}

	public ArrayList getTableFieldList() {
		return tableFieldList;
	}

	public ArrayList getTableToImportFieldList() {
		return tableToImportFieldList;
	}
}
