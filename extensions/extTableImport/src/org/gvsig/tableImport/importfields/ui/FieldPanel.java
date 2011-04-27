package org.gvsig.tableImport.importfields.ui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class FieldPanel extends JComponent implements DocumentListener, ItemListener,KeyListener, FocusListener {

	private static final long serialVersionUID = 1L;
	private JCheckBox chkImport = null;
	private JTextField txtSourceField = null;
	private JTextField txtTargetFieldName = null;
	private boolean txtTargetFieldName_changed = false;

	private boolean updating=false;
	/**
	 * This is the default constructor
	 */


	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	protected void loadIn(JPanel panel) {
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.weightx = 1.0;
		gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints2.gridx = 2;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.weightx = 1.0D;
		gridBagConstraints1.anchor = GridBagConstraints.CENTER;
		gridBagConstraints1.gridwidth = 1;
		gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints1.gridx = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		//gridBagConstraints.gridy = 0;
		//this.setSize(350, 28);
		//this.setLayout(new GridBagLayout());
		//this.setPreferredSize(new Dimension(350, 28));
		panel.add(getChkImport(), gridBagConstraints);
		panel.add(getTxtSourceField(), gridBagConstraints1);
		panel.add(getTxtTargetFieldName(), gridBagConstraints2);
	}

	protected void removeFrom(JPanel panel) {
		panel.remove(getChkImport());
		panel.remove(getTxtSourceField());
		panel.remove(getTxtTargetFieldName());
	}

	/**
	 * This method initializes chkImport
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getChkImport() {
		if (chkImport == null) {
			chkImport = new JCheckBox();
			chkImport.setName("import");
			chkImport.addItemListener(this);
		}
		return chkImport;
	}

	/**
	 * This method initializes txtSourceField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtSourceField() {
		if (txtSourceField == null) {
			txtSourceField = new JTextField();
			txtSourceField.setName("sourceField");
			txtSourceField.setEditable(false);
		}
		return txtSourceField;
	}

	/**
	 * This method initializes txtTargetFieldName
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtTargetFieldName() {
		if (txtTargetFieldName == null) {
			txtTargetFieldName = new JTextField();
			txtTargetFieldName.getDocument().addDocumentListener(this);
			txtTargetFieldName.addFocusListener(this);
			txtTargetFieldName.addKeyListener(this);
		}
		return txtTargetFieldName;
	}

	public boolean isToImport(){
		return this.getChkImport().isSelected();
	}

	public void setToImpor(boolean value){
		this.updating=true;
		this.getChkImport().setSelected(value);
		this.updating=false;
	}

	public String getSourceField(){
		return this.getTxtSourceField().getText();
	}

	public void setSourceField(String fieldName){
		this.updating=true;
		this.getTxtSourceField().setText(fieldName);
		this.updating=false;
	}

	public String getTargetFieldName(){
		return this.getTxtTargetFieldName().getText();
	}

	public void setTargetFieldName(String fieldName){
		this.updating=true;
		this.getTxtTargetFieldName().setText(fieldName);
		this.updating=false;
	}
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource().equals(this.getTxtTargetFieldName())){
			firePropertyChange("targetFieldName", evt.getOldValue(), evt.getNewValue());
		}
	}

	public void itemStateChanged(ItemEvent e) {
		boolean isSelected =e.getStateChange() == ItemEvent.SELECTED;

		if (e.getSource().equals(this.getChkImport())){
			firePropertyChange("toImport", !isSelected, isSelected);
		}

	}
	public void changedUpdate(DocumentEvent e) {
		// FIXME: Peta porque antes de que termine el evento se hace un setText
//		firePropertyChange("targetFieldName", null, this.getTargetFieldName());
		if (this.updating) return;
		this.txtTargetFieldName_changed=true;
	}

	public void insertUpdate(DocumentEvent e) {
		// FIXME: Peta porque antes de que termine el evento se hace un setText
//		firePropertyChange("targetFieldName", null, this.getTargetFieldName());
		if (this.updating) return;
		this.txtTargetFieldName_changed=true;

	}
	public void removeUpdate(DocumentEvent e) {
		// FIXME: Peta porque antes de que termine el evento se hace un setText
//		firePropertyChange("targetFieldName", null, this.getTargetFieldName());
		if (this.updating) return;
		this.txtTargetFieldName_changed=true;
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	}
	public void keyReleased(KeyEvent e) {
		if (this.txtTargetFieldName_changed){
			firePropertyChange("targetFieldName", null, this.getTargetFieldName());
			this.txtTargetFieldName_changed=false;
		}
	}
	public void keyTyped(KeyEvent e) {
		if (this.txtTargetFieldName_changed){
			firePropertyChange("targetFieldName", null, this.getTargetFieldName());
			this.txtTargetFieldName_changed=false;
		}

	}
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub

	}
	public void focusLost(FocusEvent e) {
		if (this.txtTargetFieldName_changed){
			firePropertyChange("targetFieldName", null, this.getTargetFieldName());
			this.txtTargetFieldName_changed=false;
		}
	}

}
