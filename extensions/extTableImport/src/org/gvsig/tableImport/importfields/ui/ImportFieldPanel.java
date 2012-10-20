package org.gvsig.tableImport.importfields.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import org.gvsig.tableImport.importfields.ImportFieldParams;
import org.gvsig.tableImport.importfields.ImportFieldParams.FielToImport;

import com.iver.andami.PluginServices;

public class ImportFieldPanel extends JWizardPanel implements
		PropertyChangeListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private JPanel pLista = null;
	private JPanel pButtons = null;
	private JButton bAll = null;
	private JButton bNone = null;
	private JLabel lblSpace = null;
	private JScrollPane jScrollPane = null;
	private JLabel lblMessage = null;

	private boolean updating;
	private ImportFieldParams params = null;
	private GridBagConstraints pLista_gbContraints;
	private ArrayList fields = new ArrayList();

	/**
	 * This is the default constructor
	 */
	public ImportFieldPanel(JWizardComponents wizardComponents,
			ImportFieldParams params) {
		super(wizardComponents);
		initialize();
		this.params = params;
		initialize();
		this.update();
	}

	public ImportFieldPanel() {
		super(null);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
		gridBagConstraints31.gridx = 0;
		gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints31.gridy = 1;
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.fill = GridBagConstraints.BOTH;
		gridBagConstraints11.weighty = 1.0;
		gridBagConstraints11.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints11.weightx = 1.0;
		gridBagConstraints11.anchor = GridBagConstraints.FIRST_LINE_START;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 0.0D;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridx = 0;
		this.setSize(400, 300);
		this.setLayout(new GridBagLayout());
		this.setPreferredSize(new Dimension(400, 300));
		this.add(getJScrollPane(), gridBagConstraints11);
		this.add(getPButtons(), gridBagConstraints);
		this.add(lblMessage, gridBagConstraints31);
	}

	/**
	 * This method initializes lLista
	 * 
	 * @return javax.swing.JList
	 */
	private JPanel getLLista() {
		if (pLista == null) {
			pLista = new JPanel();
			pLista.setName("lista");
			pLista.setLayout(new GridBagLayout());
			pLista_gbContraints = new GridBagConstraints();
			pLista_gbContraints.anchor = GridBagConstraints.FIRST_LINE_START;
			pLista_gbContraints.fill = GridBagConstraints.HORIZONTAL;
			pLista_gbContraints.weightx = 1;
			pLista_gbContraints.gridx = 1;
			// pLista_gbContraints.gridy=GridBagConstraints.REMAINDER;

		}
		return pLista;
	}

	/**
	 * This method initializes pButtons
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPButtons() {
		if (pButtons == null) {
			lblMessage = new JLabel();
			if (this.params == null) {
				lblMessage.setText("Message");
			} else {
				lblMessage.setText(" ");
			}
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.weightx = 1.0D;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.gridx = 0;
			lblSpace = new JLabel();
			lblSpace.setText(" ");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.anchor = GridBagConstraints.EAST;
			gridBagConstraints3.ipady = 0;
			gridBagConstraints3.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.ipadx = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			gridBagConstraints2.ipady = 0;
			gridBagConstraints2.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.ipadx = 0;
			pButtons = new JPanel();
			pButtons.setLayout(new GridBagLayout());
			pButtons.add(lblSpace, gridBagConstraints4);
			pButtons.add(getBAll(), gridBagConstraints2);
			pButtons.add(getBNone(), gridBagConstraints3);
		}
		return pButtons;
	}

	/**
	 * This method initializes bAll
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBAll() {
		if (bAll == null) {
			bAll = new JButton();
			bAll.setText(PluginServices.getText(null, "select_all"));
			bAll.setActionCommand("all");
			bAll.setName("all");
			bAll.addActionListener(this);
		}
		return bAll;
	}

	/**
	 * This method initializes bNone
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBNone() {
		if (bNone == null) {
			bNone = new JButton();
			bNone.setText(PluginServices.getText(null, "clear_selection"));
			bNone.setActionCommand("none");
			bNone.setName("none");
			bNone.addActionListener(this);
		}
		return bNone;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getLLista());
		}
		return jScrollPane;
	}

	public void update() {
		try {
			if (this.updating) {
				return;
			}
			this.updating = true;
			if (this.params == null) {
				return;
			}
			ArrayList fieldsToImport = this.params.getFieldsToImport();
			if (fieldsToImport == null) {
				this.getLLista().removeAll();
				return;
			}
			this.fillList(fieldsToImport);

			if (this.params.isValid()) {
				this.setFinishButtonEnabled(true);
				this.lblMessage.setText(" ");
			} else {
				this.setFinishButtonEnabled(false);
				this.lblMessage.setText(params.getValidationMsg());
			}
			super.update();
		} finally {
			this.updating = false;
		}
	}

	private void fillList(ArrayList fieldsToImport) {
		FielToImport fieldDef;
		FieldPanel fieldElement;
		int i;
		for (i = 0; i < fieldsToImport.size(); i++) {
			fieldDef = (FielToImport) fieldsToImport.get(i);
			if (this.fields.size() > i) {
				fieldElement = (FieldPanel) this.fields.get(i);
			} else {
				fieldElement = this.addNewFieldPanel();
			}
			fieldElement.setToImpor(fieldDef.toImport);
			fieldElement.setSourceField(fieldDef.originalFieldName);
			fieldElement.setTargetFieldName(fieldDef.fieldNameToUse);
		}
		for (i = this.fields.size() - 1; i >= fieldsToImport.size(); i--) {
			fieldElement = (FieldPanel) this.fields.remove(i);
			;
			fieldElement.removeFrom(this.getLLista());
		}
	}

	private FieldPanel addNewFieldPanel() {
		FieldPanel fieldElement = new FieldPanel();
		fieldElement.addPropertyChangeListener(this);
		fieldElement.loadIn(this.getLLista());
		this.fields.add(fieldElement);
		this.doLayout();
		return fieldElement;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (this.updating) {
			return;
		}
		if (!(evt.getSource() instanceof FieldPanel)) {
			return;
		}
		String srcField = ((FieldPanel) evt.getSource()).getSourceField();
		Iterator iter = this.params.getFieldsToImport().iterator();
		while (iter.hasNext()) {
			FielToImport field = (FielToImport) iter.next();
			if (field.originalFieldName.equals(srcField)) {
				if (evt.getPropertyName().equals("toImport")) {
					field.toImport = ((Boolean) evt.getNewValue())
							.booleanValue();
					// System.out.println("set to " +field.toImport
					// +" toImpor of "+ srcField);
				} else if (evt.getPropertyName().equals("targetFieldName")) {
					field.fieldNameToUse = (String) evt.getNewValue();
					// System.out.println("set to " +field.fieldNameToUse
					// +" nameToUse of "+ srcField);
				}
				break;
			}

		}
		this.update();
	}

	public void actionPerformed(ActionEvent e) {
		boolean toImpor = false;
		if (e.getActionCommand().equals(this.getBAll().getActionCommand())) {
			toImpor = true;
		} else if (e.getActionCommand().equals(
				this.getBNone().getActionCommand())) {
			toImpor = false;
		}
		Iterator iter = this.params.getFieldsToImport().iterator();
		while (iter.hasNext()) {
			((FielToImport) iter.next()).toImport = toImpor;
		}
		this.update();
	}

}
