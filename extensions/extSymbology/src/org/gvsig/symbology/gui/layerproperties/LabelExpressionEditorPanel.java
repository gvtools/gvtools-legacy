package org.gvsig.symbology.gui.layerproperties;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.gvsig.gui.beans.swing.JButton;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;

public class LabelExpressionEditorPanel extends JPanel implements IWindow,
		ActionListener, MouseListener {
	private static final String CANCEL_ACTION = "CANCEL";
	private static final String ACCEPT_ACTION = "ACCEPT";
	private static final String ADDFIELD_ACTION = "ADD_FIELD";
	private String originalValue;
	private String value;
	private WindowInfo wInfo = null;
	private String[] fieldNames;
	private int[] fieldTypes;
	private JButton botCancel;
	private JButton botAccept;
	private JButton botAddField;
	private JScrollPane spExpression;
	private JTextArea txtExpression;
	private JScrollPane spFieldList;
	private JList lstFields;

	public LabelExpressionEditorPanel(String[] fieldNames, int[] fieldTypes) {
		super();
		this.fieldNames = fieldNames;
		this.fieldTypes = fieldTypes;
		initialize();
	}

	private void initialize() {
		this.setLayout(new GridBagLayout());

		GridBagConstraints baseConst = new GridBagConstraints();
		baseConst.ipadx = 2;
		baseConst.ipady = 2;
		baseConst.anchor = baseConst.CENTER;
		baseConst.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints con;

		con = (GridBagConstraints) baseConst.clone();

		con.gridx = 0;
		con.gridy = 0;
		con.gridheight = 3;
		con.gridwidth = 2;
		con.fill = con.BOTH;
		con.weightx = 1;
		con.weighty = 1;
		this.add(getFieldListComposition(), con);

		con = (GridBagConstraints) baseConst.clone();
		con.gridx = 2;
		con.gridy = 2;
		con.fill = con.NONE;
		this.add(getBotAddField(), con);

		con.gridx = 0;
		con.gridy = 3;
		con.gridheight = 4;
		con.gridwidth = 5;
		con.fill = con.BOTH;
		con.weightx = 1;
		con.weighty = 1;
		this.add(getExpressionComposition(), con);

		con = (GridBagConstraints) baseConst.clone();
		con.gridx = 3;
		con.gridy = 8;
		con.fill = con.NONE;
		this.add(getBotAccept(), con);

		con = (GridBagConstraints) baseConst.clone();
		con.gridx = 4;
		con.gridy = 8;
		con.fill = con.NONE;
		this.add(getBotCancel(), con);

	}

	private JButton getBotCancel() {
		if (botCancel == null) {
			botCancel = new JButton(PluginServices.getText(this, "cancel"));
			botCancel.setActionCommand(CANCEL_ACTION);
			botCancel.addActionListener(this);
		}
		return botCancel;
	}

	private JButton getBotAccept() {
		if (botAccept == null) {
			botAccept = new JButton(PluginServices.getText(this, "accept"));
			botAccept.setActionCommand(ACCEPT_ACTION);
			botAccept.addActionListener(this);
		}
		return botAccept;

	}

	private Component getExpressionComposition() {
		if (spExpression == null) {
			spExpression = new JScrollPane();
			spExpression.setViewportView(getTxtExpression());
			spExpression
					.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			spExpression
					.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			spExpression.setBorder(BorderFactory
					.createTitledBorder(PluginServices.getText(this,
							"expression")));
		}
		return spExpression;
	}

	private JTextArea getTxtExpression() {
		if (txtExpression == null) {
			txtExpression = new JTextArea();
		}
		return txtExpression;
	}

	public void setExpression(String expr) {
		this.getTxtExpression().setText(expr);
	}

	public String getExpression() {
		return this.getTxtExpression().getText();
	}

	private JButton getBotAddField() {
		if (botAddField == null) {
			botAddField = new JButton(PluginServices.getText(this, "add_field"));
			botAddField.setActionCommand(ADDFIELD_ACTION);
			botAddField.addActionListener(this);
		}
		return botAddField;

	}

	private Component getFieldListComposition() {
		if (spFieldList == null) {
			spFieldList = new JScrollPane();
			spFieldList.setViewportView(getLstFields());
			spFieldList
					.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			spFieldList
					.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			spFieldList
					.setBorder(BorderFactory.createTitledBorder(PluginServices
							.getText(this, "fields")));

		}
		return spFieldList;
	}

	private JList getLstFields() {
		if (lstFields == null) {
			lstFields = new JList(this.fieldNames);
			lstFields.addMouseListener(this);
		}
		return lstFields;
	}

	public WindowInfo getWindowInfo() {
		if (wInfo == null) {
			wInfo = new WindowInfo(WindowInfo.RESIZABLE
					+ WindowInfo.MODALDIALOG);
			wInfo.setWidth(500);
			wInfo.setHeight(400);
			wInfo.setTitle(PluginServices.getText(this, "expression"));
		}
		return wInfo;
	}

	public Object getWindowProfile() {
		return WindowInfo.EDITOR_PROFILE;
	}

	public void setValue(String value) {
		this.originalValue = value;
		this.value = value;
		this.setExpression(value);
	}

	public String getValue() {
		return this.value;
	}

	public void actionPerformed(ActionEvent e) {
		doAction(e.getActionCommand());
	}

	private void doAction(String action) {
		if (action.equals(ADDFIELD_ACTION)) {
			JTextArea txt = getTxtExpression();
			StringBuffer strb = new StringBuffer();
			String str = txt.getText();
			int sini = txt.getSelectionStart();
			int send = txt.getSelectionEnd();
			if (sini > 0) {
				strb.append(str.substring(0, sini));
				str = str.substring(sini);
			}
			strb.append('[');
			strb.append((String) getLstFields().getSelectedValue());
			strb.append(']');
			send = send - sini;
			if (send > 0) {
				str = str.substring(send);
			}
			strb.append(str);

			getTxtExpression().setText(strb.toString());

		} else {
			// Accept or cancel
			if (action.equals(ACCEPT_ACTION)) {
				this.value = getExpression();
			} else {
				this.value = this.originalValue;
			}
			PluginServices.getMDIManager().closeWindow(this);
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == getLstFields()) {
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() > 1) {
				doAction(ADDFIELD_ACTION);
			}
		}

	}

	public void mouseEntered(MouseEvent e) {
		// Do nothing

	}

	public void mouseExited(MouseEvent e) {
		// Do nothing
	}

	public void mousePressed(MouseEvent e) {
		// Do nothing

	}

	public void mouseReleased(MouseEvent e) {
		// Do nothing

	}
}
