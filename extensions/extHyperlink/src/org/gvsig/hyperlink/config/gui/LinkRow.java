package org.gvsig.hyperlink.config.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import org.gvsig.gui.beans.swing.GridBagLayoutPanel;

import com.iver.andami.PluginServices;

public class LinkRow extends JPanel {
	JPanel pnlFieldAndExtension = null;
	JPanel pnlHyperLinkAction = null;
	JLabel lblLinkExtension = null;
	JLabel lblLinkField = null;
	JLabel lblDefaultAction = null;
	JTextField txtLinkExtension = null;
	JComboBox cmbLinkField = null;
	JComboBox cmbLinkType = null;

	public LinkRow() {
		super();
		initialize();
	}

	private void initialize() {
		GridBagLayoutPanel aux = new GridBagLayoutPanel();
		aux.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		aux.addComponent(getPnlFieldAndExtension(), getPnlHyperLinkAction());
		this.setLayout(new BorderLayout());
		this.add(aux, BorderLayout.CENTER);
	}

	public String getSelectedField() {
		return (String) getCmbLinkField().getSelectedItem();
	}

	public void setFields(String[] fields) {
		for (int i = 0; i < fields.length; i++) {
			getCmbLinkField().addItem(fields[i]);
		}
	}

	public void setSelectedField(String field) {
		getCmbLinkField().setSelectedItem(field);
	}

	public int getSelectedAction() {
		return getCmbLinkType().getSelectedIndex();
	}

	public String getSelectedActionName() {
		return (String) getCmbLinkType().getSelectedItem();
	}

	public void setActions(String[] actions) {
		for (int i = 0; i < actions.length; i++) {
			getCmbLinkType().addItem(actions[i]);
		}
	}

	public void setSelectedAction(String action) {
		getCmbLinkType().setSelectedItem(action);
	}

	public void setSelectedAction(int action) {
		getCmbLinkType().setSelectedIndex(action);
	}

	public String getExtension() {
		return getTxtLinkExtension().getText();
	}

	public void setExtension(String extension) {
		getTxtLinkExtension().setText(extension);
	}

	private JPanel getPnlFieldAndExtension() {
		if (pnlFieldAndExtension == null) {
			lblLinkExtension = new JLabel();
			lblLinkExtension.setText(" \t \t"
					+ PluginServices.getText(this, "Extension"));
			lblLinkField = new JLabel();
			lblLinkField.setText(PluginServices.getText(this, "Campo"));
			pnlFieldAndExtension = new JPanel();
			pnlFieldAndExtension.add(lblLinkField, null);
			pnlFieldAndExtension.add(getCmbLinkField(), null);
			pnlFieldAndExtension.add(lblLinkExtension, null);
			pnlFieldAndExtension.add(getTxtLinkExtension(), null);
		}
		return pnlFieldAndExtension;
	}

	/**
	 * This method initializes jPanel8. This panel contains the ComboBox to
	 * select the action, (type of HyperLink)
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPnlHyperLinkAction() {
		if (pnlHyperLinkAction == null) {
			lblDefaultAction = new JLabel();
			lblDefaultAction.setText(PluginServices.getText(this, "Action")
					+ "  ");
			pnlHyperLinkAction = new JPanel();
			pnlHyperLinkAction.add(lblDefaultAction, null);
			pnlHyperLinkAction.add(getCmbLinkType(), null);
		}
		return pnlHyperLinkAction;
	}

	/**
	 * This method initializes jComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getCmbLinkField() {
		if (cmbLinkField == null) {
			cmbLinkField = new JComboBox();
		}
		return cmbLinkField;
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtLinkExtension() {
		if (txtLinkExtension == null) {
			txtLinkExtension = new JTextField();
			txtLinkExtension.setPreferredSize(new Dimension(40, 20));
		}
		return txtLinkExtension;
	}

	/**
	 * This method initializes jComboBox1
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getCmbLinkType() {
		if (cmbLinkType == null) {
			cmbLinkType = new JComboBox();

		}
		return cmbLinkType;
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		getTxtLinkExtension().setEnabled(enabled);
		getCmbLinkField().setEnabled(enabled);
		getCmbLinkType().setEnabled(enabled);
		lblLinkField.setEnabled(enabled);
		lblDefaultAction.setEnabled(enabled);
		lblLinkExtension.setEnabled(enabled);
	}
}
