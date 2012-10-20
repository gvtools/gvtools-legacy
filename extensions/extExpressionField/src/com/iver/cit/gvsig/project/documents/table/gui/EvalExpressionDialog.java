package com.iver.cit.gvsig.project.documents.table.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.apache.bsf.BSFException;
import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.swing.JButton;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ExpressionFieldExtension;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.project.documents.table.GraphicOperator;
import com.iver.cit.gvsig.project.documents.table.IOperator;
import com.iver.cit.gvsig.project.documents.table.operators.Field;
import com.iver.utiles.GenericFileFilter;

/**
 * This dialog allows the user create expressions to fill a field of the table
 * 
 * @author Vicente Caballero Navarro
 */
public class EvalExpressionDialog extends JPanel implements IWindow {
	private JPanel pNorth = null;
	private JPanel pCentral = null;
	private JScrollPane jScrollPane = null;
	private JTextArea txtExp = null;
	private AcceptCancelPanel acceptCancel;
	private JPanel pNorthEast = null;
	private JPanel pNorthCenter = null;
	private JPanel pNorthWest = null;
	private JScrollPane jScrollPane1 = null;
	private JList listFields = null;
	private JRadioButton rbNumber = null;
	private JRadioButton rbString = null;
	private JRadioButton rbDate = null;
	private JScrollPane jScrollPane2 = null;
	private JList listCommand = null;
	private JPanel pMessage;
	private EvalExpression evalExpression;
	int lastType = -1;
	private JButton bClear = null;
	private JTabbedPane tabPrincipal = null;
	private JPanel pPrincipal = null;
	private JPanel pAdvanced = null;
	private JPanel pAdvancedNorth = null;
	private JTextField jTextField = null;
	private JButton bFile = null;
	private JPanel pAdvancedCenter = null;
	private JLabel lblLeng = null;
	private JButton bEval = null;
	private JScrollPane jScrollPane3 = null;
	private JTextArea txtMessage2 = null;

	public EvalExpressionDialog(EvalExpression ee) {
		this.evalExpression = ee;
		initialize();
	}

	private void initialize() {
		ButtonGroup bg = new ButtonGroup();
		bg.add(getRbNumber());
		bg.add(getRbString());
		bg.add(getRbDate());
		this.setLayout(new GridBagLayout());
		this.setSize(549, 480);
		GridBagConstraints constr = new GridBagConstraints();
		constr.gridwidth = GridBagConstraints.REMAINDER;
		constr.gridheight = 1;
		constr.fill = GridBagConstraints.BOTH;
		constr.ipadx = 5;
		constr.ipady = 5;
		constr.weightx = 1;
		constr.weighty = 0.3;

		this.add(getPMessage(), constr);
		constr.gridheight = 5;
		constr.weighty = 1;
		this.add(getTabPrincipal(), constr);
		GridBagConstraints constr2 = new GridBagConstraints();
		constr2.gridwidth = GridBagConstraints.REMAINDER;
		constr2.gridheight = 1;
		constr2.fill = GridBagConstraints.HORIZONTAL;
		constr2.anchor = GridBagConstraints.LAST_LINE_END;
		constr2.weightx = 1;
		constr2.weighty = 0;

		this.add(getAcceptCancel(), constr2);

	}

	private JPanel getPNorth() {
		if (pNorth == null) {
			pNorth = new JPanel();
			pNorth.setLayout(new GridBagLayout());
			GridBagConstraints contr = new GridBagConstraints();
			contr.ipadx = 5;
			contr.ipady = 5;
			contr.fill = GridBagConstraints.BOTH;
			contr.weightx = 1;
			contr.weighty = 1;
			pNorth.add(getPNorthWest(), contr);

			contr.fill = GridBagConstraints.VERTICAL;
			contr.weightx = 0;
			contr.weighty = 1;

			pNorth.add(getPNorthCenter(), contr);

			contr.fill = GridBagConstraints.BOTH;
			contr.weightx = 0.5;
			contr.weighty = 1;

			pNorth.add(getPNorthEast(), contr);

		}

		return pNorth;
	}

	private JPanel getPCentral() {
		if (pCentral == null) {
			StringBuilder tit = new StringBuilder();
			tit.append(PluginServices.getText(this, "expression"));
			tit.append(" ");
			tit.append(PluginServices.getText(this, "column"));
			tit.append(" : ");
			tit.append(evalExpression.getFieldDescriptorSelected()
					.getFieldAlias());
			pCentral = new JPanel();
			pCentral.setLayout(new GridBagLayout());
			pCentral.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, tit.toString(),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					null));

			GridBagConstraints contr = new GridBagConstraints();
			contr.gridwidth = GridBagConstraints.REMAINDER;
			contr.gridheight = 1;
			contr.fill = GridBagConstraints.BOTH;
			contr.ipadx = 5;
			contr.ipady = 5;
			contr.weightx = 1;
			contr.weighty = 1;
			pCentral.add(getJScrollPane(), contr);

			GridBagConstraints contr1 = new GridBagConstraints();
			contr1.gridwidth = 1;
			contr1.gridheight = 1;
			contr1.fill = GridBagConstraints.NONE;
			contr1.ipadx = 5;
			contr1.ipady = 5;
			contr1.anchor = GridBagConstraints.CENTER;
			pCentral.add(getBClear(), contr1);
		}

		return pCentral;
	}

	private AcceptCancelPanel getAcceptCancel() {
		if (this.acceptCancel == null) {
			this.acceptCancel = new AcceptCancelPanel(
					new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							boolean isAccepted = true;
							Preferences prefs = Preferences.userRoot().node(
									"fieldExpressionOptions");
							int limit;
							limit = prefs.getInt("limit_rows_in_memory", -1);
							if (limit != -1) {
								int option = JOptionPane
										.showConfirmDialog(
												(Component) PluginServices
														.getMainFrame(),
												PluginServices
														.getText(
																this,
																"it_has_established_a_limit_of_rows_will_lose_the_possibility_to_undo_wants_to_continue"));
								if (option != JOptionPane.OK_OPTION) {
									return;
								}
							}
							try {
								isAccepted = evalExpression
										.evalExpression(getTxtExp().getText());
								if (evalExpression.getTable() != null) {
									evalExpression.getTable().refresh();
								}
							} catch (BSFException e1) {
								NotificationManager.addError(e1);
							} catch (ReadDriverException e1) {
								NotificationManager.addError(e1);
							}
							if (isAccepted)
								PluginServices.getMDIManager().closeWindow(
										EvalExpressionDialog.this);
						}
					}, new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							PluginServices.getMDIManager().closeWindow(
									EvalExpressionDialog.this);
						}
					});
			acceptCancel.setOkButtonEnabled(false);
		}

		return this.acceptCancel;
	}

	private JPanel getPMessage() {
		if (pMessage == null) {

			pMessage = new JPanel();
			pMessage.setLayout(new GridLayout());
			pMessage.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, PluginServices.getText(this, "information"),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					null));
			pMessage.add(getJScrollPane3(), null);
		}
		return pMessage;
	}

	private void refreshOperators(int type) {
		if (lastType != -1 && lastType == type)
			return;
		lastType = type;
		ListOperatorsModel lom = (ListOperatorsModel) getListCommand()
				.getModel();
		lom.clear();

		for (IOperator operator : evalExpression.getOperators()) {
			operator.setType(type);
			if ((evalExpression.getLayer() != null)
					&& operator instanceof GraphicOperator) {
				GraphicOperator igo = (GraphicOperator) operator;
				igo.setLayer(evalExpression.getLayer());
			}
			if (operator.isEnable()) {
				lom.addOperator(operator);
			}

		}

		getListCommand().repaint();
		getJScrollPane2().repaint();
		getJScrollPane2().doLayout();
		this.doLayout();

	}

	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setPreferredSize(new java.awt.Dimension(480, 80));
			jScrollPane.setViewportView(getTxtExp());
		}

		return jScrollPane;
	}

	private JTextArea getTxtExp() {
		if (txtExp == null) {
			txtExp = new JTextArea();
			txtExp.addCaretListener(new CaretListener() {
				public void caretUpdate(CaretEvent e) {
					if (txtExp.getText().length() > 0)
						getAcceptCancel().setOkButtonEnabled(true);
					else
						getAcceptCancel().setOkButtonEnabled(false);
				}
			});
		}

		return txtExp;
	}

	public WindowInfo getWindowInfo() {
		WindowInfo wi = new WindowInfo(WindowInfo.MODALDIALOG
				+ WindowInfo.RESIZABLE);
		wi.setTitle(PluginServices.getText(this, "calculate_expression"));

		return wi;
	}

	private JPanel getPNorthEast() {
		if (pNorthEast == null) {
			pNorthEast = new JPanel(new GridLayout());
			pNorthEast.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, PluginServices.getText(this, "commands"),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					null));
			pNorthEast.add(getJScrollPane2(), null);
		}

		return pNorthEast;
	}

	private JPanel getPNorthCenter() {
		if (pNorthCenter == null) {
			pNorthCenter = new JPanel();
			pNorthCenter.setLayout(new BoxLayout(getPNorthCenter(),
					BoxLayout.Y_AXIS));
			pNorthCenter
					.setBorder(javax.swing.BorderFactory.createTitledBorder(
							null,
							PluginServices.getText(this, "type"),
							javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
							javax.swing.border.TitledBorder.DEFAULT_POSITION,
							null, null));
			pNorthCenter.add(getRbNumber(), null);
			pNorthCenter.add(getRbString(), null);
			pNorthCenter.add(getRbDate(), null);
		}

		return pNorthCenter;
	}

	private JPanel getPNorthWest() {
		if (pNorthWest == null) {
			pNorthWest = new JPanel(new GridLayout());
			pNorthWest.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, PluginServices.getText(this, "field"),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					null));
			pNorthWest.add(getJScrollPane1(), null);
		}

		return pNorthWest;
	}

	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setPreferredSize(new java.awt.Dimension(175, 100));
			jScrollPane1.setViewportView(getListFields());
		}

		return jScrollPane1;
	}

	private JList getListFields() {
		if (listFields == null) {
			listFields = new JList();
			listFields.setModel(new ListOperatorsModel());

			ListOperatorsModel lm = (ListOperatorsModel) listFields.getModel();
			FieldDescription[] fds = evalExpression.getFieldDescriptors();
			for (int i = 0; i < fds.length; i++) {
				Field field = new Field();
				field.setFieldDescription(fds[i]);
				try {
					field.eval(evalExpression.getInterpreter());
				} catch (BSFException e) {
					e.printStackTrace();
				}
				lm.addOperator(field);
			}

			listFields.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					IOperator operator = ((IOperator) listFields
							.getSelectedValue());
					if (operator != null) {
						getTxtMessage2().setText(operator.getTooltip());
						if (e.getClickCount() == 2) {
							String text = getTxtExp().getText();
							int selStart = getTxtExp().getSelectionStart();
							int selEnd = getTxtExp().getSelectionEnd();

							// int caretPos = getTxtExp().getCaretPosition();
							// if (caretPos == text.length()){
							// getTxtExp().setText(operator.addText(text));
							// } else {
							// getTxtExp().setText(operator.addText(text.substring(0,
							// caretPos))+
							// text.substring(caretPos));
							// }
							getTxtExp().setText(
									operator.addText(text
											.substring(0, selStart))
											+ text.substring(selEnd));

						}
					}
				}

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {
				}

				public void mousePressed(MouseEvent e) {
				}

				public void mouseReleased(MouseEvent e) {
				}
			});
		}

		return listFields;
	}

	private JRadioButton getRbNumber() {
		if (rbNumber == null) {
			rbNumber = new JRadioButton();
			rbNumber.setText(PluginServices.getText(this, "numeric"));
			rbNumber.setSelected(true);
			rbNumber.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					if (rbNumber.isSelected())
						refreshCommands();
				}
			});
		}

		return rbNumber;
	}

	private JRadioButton getRbString() {
		if (rbString == null) {
			rbString = new JRadioButton();
			rbString.setText(PluginServices.getText(this, "string"));
			rbString.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					if (rbString.isSelected())
						refreshCommands();
				}
			});
		}

		return rbString;
	}

	private JRadioButton getRbDate() {
		if (rbDate == null) {
			rbDate = new JRadioButton();
			rbDate.setText(PluginServices.getText(this, "date"));
			rbDate.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					if (rbDate.isSelected())
						refreshCommands();
				}
			});
		}

		return rbDate;
	}

	private JScrollPane getJScrollPane2() {
		if (jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();
			jScrollPane2.setPreferredSize(new java.awt.Dimension(175, 100));
			jScrollPane2.setViewportView(getListCommand());
		}

		return jScrollPane2;
	}

	private void refreshCommands() {
		int type = IOperator.NUMBER;
		if (getRbNumber().isSelected()) {
			type = IOperator.NUMBER;
		} else if (getRbString().isSelected()) {
			type = IOperator.STRING;
		} else if (getRbDate().isSelected()) {
			type = IOperator.DATE;
		}
		refreshOperators(type);

	}

	private JList getListCommand() {
		if (listCommand == null) {
			listCommand = new JList();
			listCommand.setModel(new ListOperatorsModel());
			listCommand.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					IOperator operator = ((IOperator) listCommand
							.getSelectedValue());
					if (operator != null) {
						getTxtMessage2().setText(operator.getTooltip());
						if (e.getClickCount() == 2) {
							if (listCommand.getSelectedValue() == null)
								return;

							String text = getTxtExp().getText();
							int caretPos = getTxtExp().getCaretPosition();
							int selStart = getTxtExp().getSelectionStart();
							int selEnd = getTxtExp().getSelectionEnd();
							if (caretPos == text.length()) {
								getTxtExp().setText(operator.addText(text));
							} else {
								getTxtExp().setText(
										text.substring(0, selStart)
												+ operator.addText(text
														.substring(selStart,
																selEnd))
												+ text.substring(selEnd));
							}

						}

					}
				}

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {
				}

				public void mousePressed(MouseEvent e) {
				}

				public void mouseReleased(MouseEvent e) {
				}
			});
			refreshOperators(IOperator.NUMBER);
		}

		return listCommand;
	}

	private JButton getBClear() {
		if (bClear == null) {
			bClear = new JButton();
			bClear.setText(PluginServices.getText(this, "clear_expression"));
			bClear.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getTxtExp().setText("");
				}
			});
		}
		return bClear;
	}

	private JTabbedPane getTabPrincipal() {
		if (tabPrincipal == null) {
			tabPrincipal = new JTabbedPane();
			tabPrincipal.addTab(PluginServices.getText(this, "general"), null,
					getPPrincipal(), null);
			tabPrincipal.addTab(PluginServices.getText(this, "advanced"), null,
					getPAdvanced(), null);
		}
		return tabPrincipal;
	}

	private JPanel getPPrincipal() {
		if (pPrincipal == null) {
			pPrincipal = new JPanel();
			pPrincipal.setLayout(new BorderLayout());
			pPrincipal.add(getPNorth(), java.awt.BorderLayout.NORTH);
			pPrincipal.add(getPCentral(), java.awt.BorderLayout.CENTER);

		}
		return pPrincipal;
	}

	private JPanel getPAdvanced() {
		if (pAdvanced == null) {
			pAdvanced = new JPanel();
			pAdvanced.setLayout(new BorderLayout());
			pAdvanced.add(getPAdvancedNorth(), java.awt.BorderLayout.NORTH);
			pAdvanced.add(getPAdvancedCenter(), java.awt.BorderLayout.CENTER);
		}
		return pAdvanced;
	}

	private JPanel getPAdvancedNorth() {
		if (pAdvancedNorth == null) {
			pAdvancedNorth = new JPanel(new GridBagLayout());
			pAdvancedNorth
					.setBorder(javax.swing.BorderFactory.createTitledBorder(
							null,
							PluginServices.getText(this,
									"expressions_from_file"),
							javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
							javax.swing.border.TitledBorder.DEFAULT_POSITION,
							null, null));
			GridBagConstraints contr = new GridBagConstraints();
			contr.anchor = GridBagConstraints.FIRST_LINE_START;
			contr.fill = GridBagConstraints.HORIZONTAL;
			contr.weighty = 0;
			contr.weightx = 1;
			contr.insets = new Insets(3, 3, 3, 3);
			contr.ipadx = 5;
			contr.ipady = 5;

			pAdvancedNorth.add(getJTextField(), contr);
			contr.fill = GridBagConstraints.NONE;
			contr.weighty = 0;
			contr.weightx = 0;
			pAdvancedNorth.add(getBFile(), null);
			pAdvancedNorth.add(getBEval(), null);
		}
		return pAdvancedNorth;
	}

	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setPreferredSize(new java.awt.Dimension(250, 20));
		}
		return jTextField;
	}

	private JButton getBFile() {
		if (bFile == null) {
			bFile = new JButton();
			bFile.setText(PluginServices.getText(this, "explorer"));
			bFile.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser jfc = new JFileChooser();
					jfc.addChoosableFileFilter(new GenericFileFilter("py",
							PluginServices.getText(this, "python")));

					if (jfc.showOpenDialog((Component) PluginServices
							.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
						File fileExpression = jfc.getSelectedFile();
						getJTextField().setText(
								fileExpression.getAbsolutePath());

					}
				}
			});
		}
		return bFile;
	}

	private String readFile(File aFile) throws IOException {
		StringBuffer fileContents = new StringBuffer();
		FileReader fileReader = new FileReader(aFile);
		int c;
		while ((c = fileReader.read()) > -1) {
			fileContents.append((char) c);
		}
		fileReader.close();
		return fileContents.toString();
	}

	private JPanel getPAdvancedCenter() {
		if (pAdvancedCenter == null) {
			lblLeng = new JLabel();
			lblLeng.setText("");
			pAdvancedCenter = new JPanel();
			pAdvancedCenter.add(lblLeng, null);
		}
		return pAdvancedCenter;
	}

	private JButton getBEval() {
		if (bEval == null) {
			bEval = new JButton();
			bEval.setText(PluginServices.getText(this, "evaluate"));
			bEval.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					File file = new File(getJTextField().getText());
					if (!file.exists()) {
						JOptionPane.showMessageDialog(
								(Component) PluginServices.getMainFrame(),
								PluginServices.getText(this, "incorrect_file"));
						return;
					}
					try {
						evalExpression.getInterpreter().exec(
								ExpressionFieldExtension.JYTHON, null, -1, -1,
								readFile(file));
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (BSFException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		return bEval;
	}

	private JScrollPane getJScrollPane3() {
		if (jScrollPane3 == null) {
			jScrollPane3 = new JScrollPane();
			jScrollPane3.setPreferredSize(new java.awt.Dimension(530, 80));
			jScrollPane3.setViewportView(getTxtMessage2());
		}
		return jScrollPane3;
	}

	private JTextArea getTxtMessage2() {
		if (txtMessage2 == null) {
			txtMessage2 = new JTextArea();
			txtMessage2
					.setText(PluginServices
							.getText(this,
									"eval_expression_will_be_carried_out_right_now_with_current_values_in_table"));
			txtMessage2.setEditable(false);
			txtMessage2.setBackground(UIManager.getColor(this));
			txtMessage2.setLineWrap(true);
			txtMessage2.setWrapStyleWord(true);
		}
		return txtMessage2;
	}

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
}