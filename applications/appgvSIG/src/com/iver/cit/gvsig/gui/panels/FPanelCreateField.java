/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package com.iver.cit.gvsig.gui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Types;
import java.text.ParseException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gvsig.gui.beans.AcceptCancelPanel;

import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;

public class FPanelCreateField extends JPanel implements IWindow {

	private static final String DEFAULT_FIELD_LENGTH = "50";
	private JLabel jLblFieldName = null;
	private JTextField jTxtFieldName = null;
	private JLabel jLblFieldType = null;
	private JComboBox jCboFieldType = null;
	private JLabel jLblFieldLength = null;
	private JTextField jTxtFieldLength = null;
	private JLabel jLblFieldPrecision = null;
	private JTextField jTxtFieldPrecision = null;
	private JLabel jLblDefaultValue = null;
	private JTextField jTxtDefaultValue = null;
	private WindowInfo viewInfo;
	private JPanel jPanel = null;  //  @jve:decl-index=0:visual-constraint="299,27"
	private AcceptCancelPanel jPanelOkCancel = null;
	private JPanel jPnlFields = null;
	private KeyListener checkInt = new KeyListener() {
		public void keyPressed(KeyEvent e)  { }
		public void keyReleased(KeyEvent e) {
			JTextField component = (JTextField) e.getComponent();

			try {
				component.setText(
						String.valueOf(
								Integer.parseInt(component.getText())
								)
						);

			} catch (Exception ex) {
				String text = component.getText();
				text = (text.length() <= 1)? "0" : text.substring(0, text.length()-1);
				component.setText(text);
			}}
		public void keyTyped(KeyEvent e)    { }
	};
	private String[] currentFieldNames;

	public FPanelCreateField() {
		super();
		// TODO Auto-generated constructor stub
		initialize();
	}

	public FPanelCreateField(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
		initialize();
	}

	public FPanelCreateField(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
		initialize();
	}

	public FPanelCreateField(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
		initialize();
	}

	public WindowInfo getWindowInfo() {
		if (viewInfo == null)
		{
			viewInfo = new WindowInfo(WindowInfo.MODALDIALOG);
			viewInfo.setWidth(this.getWidth()+8);
			viewInfo.setHeight(this.getHeight());
			viewInfo.setTitle(PluginServices.getText(this, "new_field_properties"));
		}
		return viewInfo;
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {

		this.setLayout(new BorderLayout());
		this.setSize(300, 210);
		this.setPreferredSize(new java.awt.Dimension(300,210));
		this.add(getJPanel(), java.awt.BorderLayout.CENTER);
		this.add(getJPanelOkCancel(), java.awt.BorderLayout.SOUTH);

	}

	/**
	 * This method initializes jTxtFieldName
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTxtFieldName() {
		if (jTxtFieldName == null) {
			jTxtFieldName = new JTextField();
			jTxtFieldName.setBounds(new java.awt.Rectangle(147,15,138,22));

		}
		return jTxtFieldName;
	}

	/**
	 * This method initializes jCboFieldType
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJCboFieldType() {
		if (jCboFieldType == null) {
			jCboFieldType = new JComboBox();
			jCboFieldType.setBounds(new java.awt.Rectangle(147,52,138,22));
			jCboFieldType.addItem("Boolean");
			jCboFieldType.addItem("Date");
			jCboFieldType.addItem("Integer");
			jCboFieldType.addItem("Double");
			jCboFieldType.addItem("String");

			jCboFieldType.setSelectedIndex(4);
			jCboFieldType.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// System.out.println("actionPerformed()" + e.getActionCommand()); // TODO Auto-generated Event stub actionPerformed()
					String strType = (String) getJCboFieldType().getModel().getSelectedItem();
					int fieldType = FieldDescription.stringToType(strType);
					if (fieldType == Types.DOUBLE) {
						getJTxtFieldPrecision().setEnabled(true);
						if (getJTxtFieldPrecision().getText().equals("")){
							getJTxtFieldPrecision().setText("3");
						} else {
							try {
								Integer.parseInt(getJTxtFieldPrecision().getText());
							} catch (NumberFormatException e1){
								getJTxtFieldPrecision().setText("3");
							}
						}
					}else{
						getJTxtFieldPrecision().setEnabled(false);
					}
					if (fieldType == Types.INTEGER) {
						getJTxtFieldLength().setText("9");
					}
					if (fieldType == Types.BOOLEAN)
					{
						getJTxtFieldLength().setText("0");
						getJTxtFieldLength().setEnabled(false);
					}
					else
						getJTxtFieldLength().setEnabled(true);

				}
			});

		}
		return jCboFieldType;
	}

	/**
	 * This method initializes jTxtFieldLength
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTxtFieldLength() {
		if (jTxtFieldLength == null) {
			jTxtFieldLength = new JTextField();
			jTxtFieldLength.setBounds(new java.awt.Rectangle(147,89,138,22));
			jTxtFieldLength.setText(DEFAULT_FIELD_LENGTH);
			jTxtFieldLength.addKeyListener(checkInt);
		}
		return jTxtFieldLength;
	}

	/**
	 * This method initializes jTxtFieldPrecision
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTxtFieldPrecision() {
		if (jTxtFieldPrecision == null) {
			jTxtFieldPrecision = new JTextField();
			jTxtFieldPrecision.setBounds(new java.awt.Rectangle(147,126,138,22));
			jTxtFieldPrecision.setEnabled(false);
			jTxtFieldPrecision.addKeyListener(checkInt );
		}
		return jTxtFieldPrecision;
	}

	/**
	 * This method initializes jTxtDefaultValue
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTxtDefaultValue() {
		if (jTxtDefaultValue == null) {
			jTxtDefaultValue = new JTextField();
			jTxtDefaultValue.setBounds(new java.awt.Rectangle(147,163,138,22));
		}
		return jTxtDefaultValue;
	}

	public FieldDescription getFieldDescription() throws ParseException
	{
		FieldDescription newField = new FieldDescription();
		newField.setFieldName(getJTxtFieldName().getText());
		String strType = (String) getJCboFieldType().getModel().getSelectedItem();
		int fieldType = FieldDescription.stringToType(strType);
		newField.setFieldType(fieldType);
		try {
			int fieldLength = Integer.parseInt(getJTxtFieldLength().getText());
			newField.setFieldLength(fieldLength);
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), 0);
		}

		if (fieldType == Types.DOUBLE)
		{
			try {
			newField.setFieldDecimalCount(
					Integer.parseInt(
							getJTxtFieldPrecision().getText()));
			} catch (NumberFormatException e){
				newField.setFieldDecimalCount(3);
		}
		}
		else
			newField.setFieldDecimalCount(0);
		String defaultValue = getJTxtDefaultValue().getText();
		if (defaultValue != null)
		{

			if (defaultValue.compareTo("")==0)
				newField.setDefaultValue(ValueFactory.createNullValue());
			else
				newField.setDefaultValue(ValueFactory.createValueByType(defaultValue, fieldType));
		}

		return newField;
	}

	public void setOkAction(ActionListener okAction) {
		getJPanelOkCancel().setOkButtonActionListener(okAction);

	}

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(null);

			jPanel.add(getJPnlFields(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanelOkCancel
	 *
	 * @return javax.swing.JPanel
	 */
	private AcceptCancelPanel getJPanelOkCancel() {
		if (jPanelOkCancel == null) {
			jPanelOkCancel = new AcceptCancelPanel();
			jPanelOkCancel.setCancelButtonActionListener(new ActionListener(){
				public void actionPerformed(java.awt.event.ActionEvent e) {
					PluginServices.getMDIManager().closeWindow(FPanelCreateField.this);
				};
			});
			jPanelOkCancel.setPreferredSize(new java.awt.Dimension(10,50));
		}
		return jPanelOkCancel;
	}

	/**
	 * This method initializes jPnlFields
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPnlFields() {
		if (jPnlFields == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(6);
			gridLayout.setVgap(3);
			gridLayout.setHgap(5);
			gridLayout.setColumns(2);
			jPnlFields = new JPanel();
			jPnlFields.setLayout(gridLayout);
			jPnlFields.setBounds(new java.awt.Rectangle(5,12,290,142));
			jLblDefaultValue = new JLabel();
			jLblDefaultValue.setBounds(new java.awt.Rectangle(14,163,125,22));
			jLblDefaultValue.setText(PluginServices.getText(this, "default_value"));
			jLblFieldPrecision = new JLabel();
			jLblFieldPrecision.setBounds(new java.awt.Rectangle(14,126,112,22));
			jLblFieldPrecision.setText(PluginServices.getText(this, "precision"));
			jLblFieldLength = new JLabel();
			jLblFieldLength.setBounds(new java.awt.Rectangle(14,89,99,22));
			jLblFieldLength.setText(PluginServices.getText(this, "field_length"));
			jLblFieldType = new JLabel();
			jLblFieldType.setBounds(new java.awt.Rectangle(14,52,94,22));
			jLblFieldType.setText(PluginServices.getText(this, "field_type"));
			jLblFieldName = new JLabel();
			jLblFieldName.setText(PluginServices.getText(this, "field_name"));
			jLblFieldName.setBounds(new java.awt.Rectangle(14,15,99,22));
			jPnlFields.add(jLblFieldName, null);
			jPnlFields.add(getJTxtFieldName(), null);
			jPnlFields.add(jLblFieldType, null);
			jPnlFields.add(getJCboFieldType(), null);
			jPnlFields.add(jLblFieldLength, null);
			jPnlFields.add(getJTxtFieldLength(), null);
			jPnlFields.add(jLblFieldPrecision, null);
			jPnlFields.add(getJTxtFieldPrecision(), null);
			jPnlFields.add(jLblDefaultValue, null);
			jPnlFields.add(getJTxtDefaultValue(), null);
		}
		return jPnlFields;
	}

	public void setCurrentFieldNames(String[] fieldNames) {
		currentFieldNames = fieldNames;
		String newField = PluginServices.getText(this, "field").replaceAll(" +", "_");
		int index=0;
		for (int i = 0; i < currentFieldNames.length; i++) {
			if (currentFieldNames[i].startsWith(newField)) {
				try {
					index = Integer.parseInt(currentFieldNames[i].replaceAll(newField,""));
				} catch (Exception e) { /* we don't care */}
			}
		}
		jTxtFieldName.setText(newField+(++index));
	}

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
}  //  @jve:decl-index=0:visual-constraint="9,10"
