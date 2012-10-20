package org.gvsig.tableImport.importfields.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import org.gvsig.tableImport.importfields.ImportFieldParams;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;

public class LinkDefinitionPanel extends JWizardPanel implements ItemListener {

	private static final long serialVersionUID = 1L;
	private JLabel lblTable = null;
	private JComboBox cbTable = null;
	private JLabel lblTableLinkField = null;
	private JComboBox cbTableLinkField = null;
	private JLabel lblTableToImport = null;
	private JComboBox cbTableToImport = null;
	private JLabel lblLinkFieldTableToImport = null;
	private JComboBox cbLinkFieldTableToImport = null;

	private JLabel lblMessage = null;
	private JLabel lblSpace = null;

	private ImportFieldParams params = null;
	private DefaultComboBoxModel cbTable_model;
	private DefaultComboBoxModel cbTableLinkField_model;
	private DefaultComboBoxModel cbTableToImport_model;
	private DefaultComboBoxModel cbLinkFieldTableToImport_model;
	private boolean updating;
	private JLabel lblSpace2;

	/**
	 * This is the default constructor
	 */
	public LinkDefinitionPanel(JWizardComponents wizardComponents,
			ImportFieldParams params) {
		super(wizardComponents);
		this.params = params;
		initialize();
		this.update();
	}

	public LinkDefinitionPanel() {
		super(null);
		initialize();
	}

	public void update() {
		if (this.updating) {
			return;
		}
		this.updating = true;
		try {
			if (this.params == null) {
				this.updating = false;
				return;
			}

			this.fillFieldsInComboModel(this.cbTableLinkField_model,
					this.params.getTableFieldList());
			this.cbTableLinkField_model.setSelectedItem(this.params
					.getTableField());

			this.fillFieldsInComboModel(this.cbLinkFieldTableToImport_model,
					this.params.getTableToImportFieldList());
			this.cbLinkFieldTableToImport_model.setSelectedItem(this.params
					.getTableToImportField());

			if (this.params.isValidLinkParams()) {
				this.lblMessage.setText(" ");
				this.setNextButtonEnabled(true);
				this.setFinishButtonEnabled(this.params.isValid());
			} else {
				this.lblMessage.setText(params.getValidationMsg());
				this.setNextButtonEnabled(false);
				this.setFinishButtonEnabled(false);
			}
			super.update();
		} finally {
			this.updating = false;
		}
	}

	private void fillFieldsInComboModel(DefaultComboBoxModel model,
			ArrayList fieldList) {
		model.removeAllElements();
		if (fieldList == null) {
			return;
		}
		Iterator iter = fieldList.iterator();
		while (iter.hasNext()) {
			model.addElement(iter.next());
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
		gridBagConstraints10.gridx = 0;
		gridBagConstraints10.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints10.anchor = GridBagConstraints.SOUTHWEST;
		gridBagConstraints10.gridy = 0;
		gridBagConstraints10.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints10.weighty = 1.0;

		GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
		gridBagConstraints9.gridx = 0;
		gridBagConstraints9.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints9.anchor = GridBagConstraints.SOUTHWEST;
		gridBagConstraints9.gridy = 5;
		gridBagConstraints9.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints9.weighty = 1.0;
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		gridBagConstraints8.gridx = 0;
		gridBagConstraints8.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints8.anchor = GridBagConstraints.SOUTHWEST;
		gridBagConstraints8.gridy = 6;
		gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints8.weightx = 1;
		gridBagConstraints8.gridwidth = 2;
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints7.gridy = 4;
		gridBagConstraints7.weightx = 1.0;
		gridBagConstraints7.insets = new Insets(5, 15, 5, 15);
		gridBagConstraints7.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints7.gridx = 1;
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 0;
		gridBagConstraints6.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints6.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints6.gridy = 4;
		lblLinkFieldTableToImport = new JLabel();
		lblLinkFieldTableToImport.setText(PluginServices.getText(null,
				"link_field_table_to_import"));
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints5.gridy = 3;
		gridBagConstraints5.weightx = 1.0;
		gridBagConstraints5.insets = new Insets(5, 15, 5, 15);
		gridBagConstraints5.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints5.gridx = 1;
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints4.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints4.gridy = 3;
		lblTableToImport = new JLabel();
		lblTableToImport.setText(PluginServices
				.getText(null, "table_to_import"));
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.gridy = 2;
		gridBagConstraints3.weightx = 1.0;
		gridBagConstraints3.insets = new Insets(5, 15, 5, 15);
		gridBagConstraints3.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints3.gridx = 1;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints2.gridy = 2;
		lblTableLinkField = new JLabel();
		lblTableLinkField.setText(PluginServices.getText(null,
				"link_field_of_table"));
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.insets = new Insets(5, 15, 5, 15);
		gridBagConstraints1.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints1.gridx = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints.gridy = 1;
		lblTable = new JLabel();
		lblTable.setText(PluginServices.getText(null, "table"));

		lblMessage = new JLabel();
		lblSpace = new JLabel();
		lblSpace2 = new JLabel();

		this.setSize(400, 300);
		this.setLayout(new GridBagLayout());
		this.setPreferredSize(new Dimension(400, 300));
		this.add(lblTable, gridBagConstraints);
		this.add(getCbTable(), gridBagConstraints1);
		this.add(lblTableLinkField, gridBagConstraints2);
		this.add(getCbTableLinkField(), gridBagConstraints3);
		this.add(lblTableToImport, gridBagConstraints4);
		this.add(getCbTableToImport(), gridBagConstraints5);
		this.add(lblLinkFieldTableToImport, gridBagConstraints6);
		this.add(getCbLinkFieldTableToImport(), gridBagConstraints7);
		this.add(lblSpace, gridBagConstraints9);
		this.add(lblMessage, gridBagConstraints8);
		this.add(lblSpace2, gridBagConstraints10);

	}

	/**
	 * This method initializes cbTable
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getCbTable() {
		if (cbTable == null) {
			cbTable = new JComboBox();
			this.cbTable_model = new DefaultComboBoxModel();
			if (this.params.isLockTable()) {
				this.cbTable_model.addElement(params.getTable().getName());
				this.cbTable.setEditable(false);
				cbTable.setModel(this.cbTable_model);
				this.cbTable.setSelectedIndex(0);
				this.cbTable.setEditable(false);
			} else {
				this.fillProjectTableComboModel(this.cbTable_model);
				cbTable.addItemListener(this);
				cbTable.setModel(this.cbTable_model);
				cbTable_model.setSelectedItem(null);
			}
		}
		return cbTable;
	}

	private void fillProjectTableComboModel(DefaultComboBoxModel model) {
		Project project = ((ProjectExtension) PluginServices
				.getExtension(ProjectExtension.class)).getProject();
		List ptables = project
				.getDocumentsByType(ProjectTableFactory.registerName);

		Iterator iter = ptables.iterator();
		ProjectTable ptable;
		while (iter.hasNext()) {
			ptable = (ProjectTable) iter.next();
			model.addElement(new ComboElement(ptable, ptable.getName()));
		}

	}

	/**
	 * This method initializes cbTableLinkField
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getCbTableLinkField() {
		if (cbTableLinkField == null) {
			cbTableLinkField = new JComboBox();
			this.cbTableLinkField_model = new DefaultComboBoxModel();
			cbTableLinkField.addItemListener(this);
			cbTableLinkField.setModel(cbTableLinkField_model);

		}
		return cbTableLinkField;
	}

	/**
	 * This method initializes cbTableToImport
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getCbTableToImport() {
		if (cbTableToImport == null) {
			cbTableToImport = new JComboBox();
			this.cbTableToImport_model = new DefaultComboBoxModel();
			this.fillProjectTableComboModel(this.cbTableToImport_model);
			this.cbTableToImport_model.setSelectedItem(null);
			cbTableToImport.setModel(cbTableToImport_model);
			cbTableToImport.addItemListener(this);
		}
		return cbTableToImport;
	}

	/**
	 * This method initializes cbLinkFieldTableToImport
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getCbLinkFieldTableToImport() {
		if (cbLinkFieldTableToImport == null) {
			cbLinkFieldTableToImport = new JComboBox();
			this.cbLinkFieldTableToImport_model = new DefaultComboBoxModel();
			cbLinkFieldTableToImport.setModel(cbLinkFieldTableToImport_model);
			cbLinkFieldTableToImport.addItemListener(this);
		}
		return cbLinkFieldTableToImport;
	}

	private class ComboElement {

		private String description;
		private Object value;

		public ComboElement(Object value, String description) {
			this.value = value;
			this.description = description;
		}

		public String toString() {
			return this.description;
		}

		public Object getValue() {
			return this.value;
		}

	}

	public void itemStateChanged(ItemEvent e) {
		if (this.updating) {
			return;
		}
		if (e.getStateChange() == ItemEvent.DESELECTED) {
			return;
		}
		Object src = e.getSource();

		ComboElement element = null;
		String strElement = null;
		try {
			element = (ComboElement) e.getItem();
		} catch (ClassCastException ex) {
			strElement = (String) e.getItem();
		}
		if (src == this.cbTable) {
			if (this.params.isLockTable()) {
				return;
			}
			try {
				this.params.setTable((ProjectTable) element.getValue());
			} catch (ReadDriverException e1) {
				NotificationManager.addError(e1);
			}
		} else if (src == this.cbTableLinkField) {
			this.params.setTableField((String) strElement);

		} else if (src == this.cbTableToImport) {
			try {
				this.params.setTableToImport((ProjectTable) element.getValue());
			} catch (ReadDriverException e1) {
				NotificationManager.addError(e1);
			}

		} else if (src == this.cbLinkFieldTableToImport) {
			this.params.setTableToImportField(strElement);
		}
		this.update();

	}

}
