package com.iver.cit.gvsig.project.documents.view.tool.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gvsig.gui.beans.AcceptCancelPanel;

import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.layers.Annotation_Mapping;

public class Annotation_ModifyWindow extends JPanel implements IWindow{

	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JTextField jTextField = null;
	private AcceptCancelPanel accept;
	private boolean isAccepted=false;
	private JPanel jPanel1 = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private JPanel jPanel2 = null;
	private TextPropertiesPanel textPropPanel = null;
	private Annotation_Mapping mapping;
	/**
	 * This is the default constructor
	 */
	public Annotation_ModifyWindow() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
//		this.setSize(292, 197);
		this.add(getJPanel1(), java.awt.BorderLayout.CENTER);
	}

	public WindowInfo getWindowInfo() {
		WindowInfo wi=new WindowInfo(WindowInfo.MODALDIALOG | WindowInfo.RESIZABLE);
		wi.setTitle(PluginServices.getText(this,"modify_annotation"));
		wi.setWidth(350);
		wi.setHeight(250);
		return wi;
	}

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jLabel = new JLabel();
			jLabel.setText(PluginServices.getText(this,"fonttext"));
			jLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
			jLabel.setHorizontalTextPosition(javax.swing.SwingConstants.TRAILING);
			jLabel.setPreferredSize(new java.awt.Dimension(100,16));
			jLabel.setVerticalAlignment(javax.swing.SwingConstants.CENTER);

			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, PluginServices.getText(this,"options"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));

			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new Insets(8,4,4,4);
			jPanel.add(jLabel, gridBagConstraints);

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.insets = new Insets(4,4,4,8);
			jPanel.add(getTxtText(), gridBagConstraints1);

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints.insets = new Insets(8,4,4,8);
			jPanel.add(getTextPropPanel(), gridBagConstraints);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtText() {
		if (jTextField == null) {
			jTextField = new JTextField();
		}
		return jTextField;
	}

	private TextPropertiesPanel getTextPropPanel() {
		if (textPropPanel==null) {
			textPropPanel = new TextPropertiesPanel();
		}
		return textPropPanel;
	}

	private AcceptCancelPanel getAcceptCancelPanel() {
		if (accept == null) {
			ActionListener okAction, cancelAction;
			okAction = new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					isAccepted = true;
					PluginServices.getMDIManager().closeWindow(
							Annotation_ModifyWindow.this);
				}
			};
			cancelAction = new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					isAccepted = false;
					PluginServices.getMDIManager().closeWindow(
							Annotation_ModifyWindow.this);
				}
			};
			accept = new AcceptCancelPanel(okAction, cancelAction);
			accept.setPreferredSize(new java.awt.Dimension(200, 30));
			// accept.setBounds(new java.awt.Rectangle(243,387,160,28));
			accept.setEnabled(true);
			accept.setVisible(true);
		}
		return accept;
	}

	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BorderLayout());
			jPanel1.setSize(new java.awt.Dimension(244,142));
			jPanel1.add(getJPanel(), java.awt.BorderLayout.NORTH);
			jPanel1.add(getJPanel2(), java.awt.BorderLayout.SOUTH);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jPanel2
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.add(getAcceptCancelPanel(), null);
		}
		return jPanel2;
	}

	public void setValues(Value[] values, Annotation_Mapping am) {
		this.mapping=am;
		getTxtText().setText(values[am.getColumnText()].toString());
		getTextPropPanel().setFontType(values[am.getColumnTypeFont()].toString());
		getTextPropPanel().setFontStyle(((NumericValue)values[am.getColumnStyleFont()]).intValue());
		getTextPropPanel().setTextHeight(Double.parseDouble(values[am.getColumnHeight()].toString()));
		int intColor=((NumericValue)values[am.getColumnColor()]).intValue();
		Color color=new Color(intColor);
		getTextPropPanel().setColor(color);
		getTextPropPanel().setRotation(Double.parseDouble(values[am.getColumnRotate()].toString()));
	}

	public boolean isAccepted() {
		return isAccepted;
	}

	public Value[] getValues() {
		Value[] values=new Value[6];
		values[mapping.getColumnText()]=ValueFactory.createValue(getTxtText().getText());
		values[mapping.getColumnTypeFont()]=ValueFactory.createValue(getTextPropPanel().getFontType());
		values[mapping.getColumnStyleFont()]=ValueFactory.createValue(getTextPropPanel().getFontStyle());
		values[mapping.getColumnHeight()]=ValueFactory.createValue(getTextPropPanel().getTextHeight());
		values[mapping.getColumnColor()]=ValueFactory.createValue(getTextPropPanel().getColor().getRGB());
		values[mapping.getColumnRotate()]=ValueFactory.createValue(getTextPropPanel().getRotation());
		return values;
	}

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
