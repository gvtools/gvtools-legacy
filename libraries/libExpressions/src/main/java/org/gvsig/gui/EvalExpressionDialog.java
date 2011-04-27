package org.gvsig.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.GraphicOperator;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.baseclasses.Index;
import org.gvsig.expresions.EvalOperatorsTask;
import org.gvsig.expresions.ExpressionFieldExtension;
import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.operators.Field;

import bsh.EvalError;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.utiles.GenericFileFilter;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class EvalExpressionDialog extends JPanel implements IWindow {
    private JPanel pNorth = null;
    private JPanel pCentral = null;
    private JScrollPane jScrollPane = null;
    private JTextArea txtExp = null;
    private AcceptCancelPanel acceptCancel;
    private Table table;
    private FLyrVect lv;
    private JLabel lblColumn = null;
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
    private BSFManager interpreter = null; // Construct an interpreter
    private Index indexRow = null;
    private SelectableDataSource sds = null;
    private EvalExpression evalExpression=null;
    private IEditableSource ies=null;
	private JPanel pMessage;
	//private JTextArea txtMessage;
    private static ArrayList operators=new ArrayList();
//    public EvalExpressionDialog(Table table,BSFManager interpreter, ArrayList operators) {
//        super();
//        this.operators=operators;
//        this.interpreter=interpreter;
//        this.table = table;
//        initialize();
//
//    }
    
    public EvalExpressionDialog(FLyrVect lv,BSFManager interpreter, ArrayList operators) {
        super();
        this.operators=operators;
        this.interpreter=interpreter;
        this.lv = lv;
        initialize();

    }
    /**
     * This method initializes this
     */
    private void initialize() {
    	try {
	        evalExpressions();
        } catch (BSFException e) {
        	NotificationManager.addError(e);
		} catch (ReadDriverException e) {
			NotificationManager.addError(e);
		}
	    evalExpression=new EvalExpression();
//	    evalExpression.setTable(table);
	    evalExpression.setFLyrVect(lv);
//	    lv = (FLyrVect) table.getModel().getAssociatedTable();
        ButtonGroup bg = new ButtonGroup();
        bg.add(getRbNumber());
        bg.add(getRbString());
        bg.add(getRbDate());
        this.setLayout(new BorderLayout());
        this.setSize(549, 480);
        this.add(getTabPrincipal(), java.awt.BorderLayout.CENTER);
        this.add(getPMessage(), java.awt.BorderLayout.NORTH);
        this.add(getAcceptCancel(), java.awt.BorderLayout.SOUTH);
    }
    /**
     * This method initializes pCentral
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPNorth() {
        if (pNorth == null) {
            pNorth = new JPanel();
            pNorth.setLayout(new BorderLayout());
            pNorth.add(getPNorthEast(), java.awt.BorderLayout.EAST);
            pNorth.add(getPNorthCenter(), java.awt.BorderLayout.CENTER);
            pNorth.add(getPNorthWest(), java.awt.BorderLayout.WEST);
        }

        return pNorth;
    }

    /**
     * This method initializes pNorth
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPCentral() {
        if (pCentral == null) {
            pCentral = new JPanel();
            pCentral.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, PluginServices.getText(this,"expression"),
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            lblColumn = new JLabel();
            pCentral.add(lblColumn, null);

            pCentral.add(getJScrollPane(), null);
            lblColumn.setText(PluginServices.getText(this, "column") + " : " +
                evalExpression.getFieldDescriptorSelected().getFieldAlias());
            pCentral.add(getBClear(), null);
        }

        return pCentral;
    }

    /**
     * This method initializes pSouth
     *
     * @return javax.swing.JPanel
     */
    private AcceptCancelPanel getAcceptCancel() {
		if (this.acceptCancel == null) {
			this.acceptCancel = new AcceptCancelPanel(
					new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							boolean isAccepted=true;
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
								long t1 = System.currentTimeMillis();
								isAccepted=evalExpression();
								long t2 = System.currentTimeMillis();
								System.out
										.println("Tiempo evaluar expresiones = "
												+ (t2 - t1));
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
    /**
	 * Evaluate the expression.
     * @throws ReadDriverException
     * @throws BSFException
	 *
	 * @throws EvalError
	 * @throws DriverException
	 * @throws DriverIOException
	 * @throws IOException
	 * @throws DriverIOException
	 * @throws IOException
	 * @throws BSFException
	 */
    private boolean evalExpression() throws ReadDriverException, BSFException{
        long rowCount = sds.getRowCount();
        String expression=getTxtExp().getText()
        	.replaceAll("\\[","field(\"")
        	.replaceAll("\\]","\")");
        interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def expression():\n" +
        		"  return " +expression+ "");
        if (rowCount > 0) {
            try {
                interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"expression()");
            } catch (BSFException ee) {
                int option=JOptionPane.showConfirmDialog((Component) PluginServices.getMainFrame(),
                    PluginServices.getText(this,
                        "error_expression")+"\n"+ee.getMessage()+"\n"+PluginServices.getText(this,"continue?"));
                if (option!=JOptionPane.OK_OPTION) {
                	return false;
                }
            }
        }
        ies.startComplexRow();
        interpreter.declareBean("ee",evalExpression,EvalExpression.class);
        ArrayList exceptions=new ArrayList();
        interpreter.declareBean("exceptions",exceptions,ArrayList.class);
        FBitSet selection=sds.getSelection();
        if (selection.cardinality() > 0) {
			interpreter.declareBean("selection", selection, FBitSet.class);
			interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def p():\n" +
					"  i=selection.nextSetBit(0)\n" +
					"  while i >=0:\n" +
					"    indexRow.set(i)\n" +
					"    obj=expression()\n" +
					"    ee.setValue(obj,i)\n" +
					"    ee.saveEdits(i)\n" +
					"    i=selection.nextSetBit(i+1)\n");
		} else {
			interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,"def p():\n" +
					"  for i in xrange("+rowCount +"):\n" +
					"    indexRow.set(i)\n" +
//					"    print i , expression() , repr (expression())\n" +
					"    ee.setValue(expression(),i)\n" +
					"    ee.saveEdits(i)\n");
		}
        try {
        	interpreter.eval(EvalOperatorsTask.JYTHON,null,-1,-1,"p()");
//        	interpreter.exec(ExpressionFieldExtension.JYTHON,null,-1,-1,"p()");
        } catch (BSFException ee) {

        	 JOptionPane.showMessageDialog((Component) PluginServices.getMainFrame(),
                     PluginServices.getText(this, "evaluate_expression_with_errors")+" "+(rowCount-indexRow.get())+"\n"+ee.getMessage());
        }

        ies.endComplexRow(PluginServices.getText(this, "expression"));
        table.refresh();
        return true;
    }
    /**
	 * This method initializes pMessage
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPMessage() {
		if (pMessage == null) {

			pMessage = new JPanel();
			pMessage.setBorder(javax.swing.BorderFactory.createTitledBorder(null, PluginServices.getText(this,"information"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			pMessage.setPreferredSize(new java.awt.Dimension(550,120));
			pMessage.add(getJScrollPane3(), null);
		}
		return pMessage;
	}
    private void evalExpressions() throws BSFException, ReadDriverException {
    	if (lv ==null)
            ies=table.getModel().getModelo();
        else
        	ies = null;
//            ies = (VectorialEditableAdapter) lv.getSource().getRecordset();
//        ies = table.getModel().getModelo();
        sds = lv.getRecordset();
        interpreter.declareBean("sds", sds,SelectableDataSource.class);
        indexRow=new Index();
        interpreter.declareBean("indexRow", indexRow,Index.class);
    }
    /**
     * Evaluate the fields.
     *
     * @param interpreter
     *
     * @throws EvalError
     */
    int lastType=-1;
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
	private void refreshOperators(int type) {
        if (lastType!=-1 && lastType==type)
        	return;
        lastType=type;
    	ListOperatorsModel lom=(ListOperatorsModel)getListCommand().getModel();
        lom.clear();
           for (int i=0;i<operators.size();i++) {
            IOperator operator = (IOperator)operators.get(i);
            operator.setType(type);
            //Comprobar si tiene una capa asociada y pasarsela al GraphicOperator.
            if ((lv != null) && operator instanceof GraphicOperator) {
                GraphicOperator igo = (GraphicOperator) operator;
                igo.setLayer(lv);
            }
            if (operator.isEnable()) {
                   lom.addOperator(operator);
                   //System.out.println("Operator = "+operator.toString());
            }
        }
        getListCommand().repaint();

    }
    /**
     * This method initializes jScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setPreferredSize(new java.awt.Dimension(480, 80));
            jScrollPane.setViewportView(getTxtExp());
        }

        return jScrollPane;
    }

    /**
     * This method initializes txtExp
     *
     * @return javax.swing.JTextArea
     */
    private JTextArea getTxtExp() {
        if (txtExp == null) {
            txtExp = new JTextArea();
            txtExp.addCaretListener(new CaretListener(){
				public void caretUpdate(CaretEvent e) {
					if (txtExp.getText().length()>0)
						getAcceptCancel().setOkButtonEnabled(true);
					else
						getAcceptCancel().setOkButtonEnabled(false);
				}




			});
        }

        return txtExp;
    }

    public WindowInfo getWindowInfo() {
         WindowInfo wi = new WindowInfo(WindowInfo.MODALDIALOG);
        wi.setTitle(PluginServices.getText(this, "calculate_expression"));

        return wi;
    }

    /**
     * This method initializes pNorthEast
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPNorthEast() {
        if (pNorthEast == null) {
            pNorthEast = new JPanel();
            pNorthEast.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, PluginServices.getText(this,"commands"),
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            pNorthEast.add(getJScrollPane2(), null);
        }

        return pNorthEast;
    }

    /**
     * This method initializes pNorthCenter
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPNorthCenter() {
        if (pNorthCenter == null) {
            pNorthCenter = new JPanel();
            pNorthCenter.setLayout(new BoxLayout(getPNorthCenter(),
                    BoxLayout.Y_AXIS));
            pNorthCenter.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, PluginServices.getText(this,"type"),
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            pNorthCenter.add(getRbNumber(), null);
            pNorthCenter.add(getRbString(), null);
            pNorthCenter.add(getRbDate(), null);
        }

        return pNorthCenter;
    }

    /**
     * This method initializes pNorthWest
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPNorthWest() {
        if (pNorthWest == null) {
            pNorthWest = new JPanel();
            pNorthWest.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, PluginServices.getText(this,"field"),
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            pNorthWest.add(getJScrollPane1(), null);
        }

        return pNorthWest;
    }

    /**
     * This method initializes jScrollPane1
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane1() {
        if (jScrollPane1 == null) {
            jScrollPane1 = new JScrollPane();
            jScrollPane1.setPreferredSize(new java.awt.Dimension(175, 100));
            jScrollPane1.setViewportView(getListFields());
        }

        return jScrollPane1;
    }

    /**
     * This method initializes listFields
     *
     * @return javax.swing.JList
     */
    private JList getListFields() {
        if (listFields == null) {
            listFields = new JList();
            listFields.setModel(new ListOperatorsModel());

            ListOperatorsModel lm = (ListOperatorsModel) listFields.getModel();
            FieldDescription[] fds=evalExpression.getFieldDescriptors();
            for (int i = 0; i < fds.length; i++) {
                Field field=new Field();
                field.setFieldDescription(fds[i]);
                try {
                	field.eval(interpreter);
                } catch (BSFException e) {
					e.printStackTrace();
				}
                lm.addOperator(field);
            }

            listFields.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                    	IOperator operator=((IOperator) listFields.getSelectedValue());
                    	getTxtMessage2().setText(operator.getTooltip());
                    	if (e.getClickCount() == 2) {
                            getTxtExp().setText(operator.addText(
                                    getTxtExp().getText()));
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

    /**
     * This method initializes rbNumber
     *
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getRbNumber() {
        if (rbNumber == null) {
            rbNumber = new JRadioButton();
            rbNumber.setText(PluginServices.getText(this,"numeric"));
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

    /**
     * This method initializes rbString
     *
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getRbString() {
        if (rbString == null) {
            rbString = new JRadioButton();
            rbString.setText(PluginServices.getText(this,"string"));
            rbString.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                     if (rbString.isSelected())
                         refreshCommands();
                }
            });
        }

        return rbString;
    }

    /**
     * This method initializes rbData
     *
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getRbDate() {
        if (rbDate == null) {
            rbDate = new JRadioButton();
            rbDate.setText(PluginServices.getText(this,"date"));
            rbDate.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                    if (rbDate.isSelected())
                         refreshCommands();
                }
            });
        }

        return rbDate;
    }

    /**
     * This method initializes jScrollPane2
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane2() {
        if (jScrollPane2 == null) {
            jScrollPane2 = new JScrollPane();
            jScrollPane2.setPreferredSize(new java.awt.Dimension(175, 100));
            jScrollPane2.setViewportView(getListCommand());
        }

        return jScrollPane2;
    }

    /**
     * Refresh the commands.
     */
    private void refreshCommands() {
        int type=IOperator.NUMBER;
        if (getRbNumber().isSelected()) {
            type=IOperator.NUMBER;
        } else if (getRbString().isSelected()) {
            type=IOperator.STRING;
        } else if (getRbDate().isSelected()) {
            type=IOperator.DATE;
        }
        refreshOperators(type);

    }

    /**
     * This method initializes ListCommand
     *
     * @return javax.swing.JList
     */
    private JList getListCommand() {
        if (listCommand == null) {
            listCommand = new JList();
            listCommand.setModel(new ListOperatorsModel());
            listCommand.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                    	IOperator operator=((IOperator) listCommand.getSelectedValue());
                    	getTxtMessage2().setText(operator.getTooltip());
                    	if (e.getClickCount() == 2) {
                        	if (listCommand.getSelectedValue()==null)
                        		return;
                            getTxtExp().setText(operator.addText(
                                    getTxtExp().getText()));
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

    /**
	 * This method initializes bClear
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBClear() {
		if (bClear == null) {
			bClear = new JButton();
			bClear.setText(PluginServices.getText(this,"clear_expression"));
			bClear.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getTxtExp().setText("");
				}
			});
		}
		return bClear;
	}
	/**
	 * This method initializes tabPrincipal
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getTabPrincipal() {
		if (tabPrincipal == null) {
			tabPrincipal = new JTabbedPane();
			tabPrincipal.addTab(PluginServices.getText(this,"general"), null, getPPrincipal(), null);
			tabPrincipal.addTab(PluginServices.getText(this,"advanced"), null, getPAdvanced(), null);
		}
		return tabPrincipal;
	}
	/**
	 * This method initializes pPrincipal
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPPrincipal() {
		if (pPrincipal == null) {
			pPrincipal = new JPanel();
			pPrincipal.setLayout(new BorderLayout());
			pPrincipal.setPreferredSize(new java.awt.Dimension(540,252));
			pPrincipal.add(getPNorth(), java.awt.BorderLayout.NORTH);
			pPrincipal.add(getPCentral(), java.awt.BorderLayout.CENTER);

		}
		return pPrincipal;
	}
	/**
	 * This method initializes pAdvanced
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPAdvanced() {
		if (pAdvanced == null) {
			pAdvanced = new JPanel();
			pAdvanced.setLayout(new BorderLayout());
			pAdvanced.add(getPAdvancedNorth(), java.awt.BorderLayout.NORTH);
			pAdvanced.add(getPAdvancedCenter(), java.awt.BorderLayout.CENTER);
		}
		return pAdvanced;
	}
	/**
	 * This method initializes pAdvancedNorth
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPAdvancedNorth() {
		if (pAdvancedNorth == null) {
			pAdvancedNorth = new JPanel();
			pAdvancedNorth.setPreferredSize(new java.awt.Dimension(873,100));
			pAdvancedNorth.setBorder(javax.swing.BorderFactory.createTitledBorder(null, PluginServices.getText(this,"expressions_from_file"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			pAdvancedNorth.add(getJTextField(), null);
			pAdvancedNorth.add(getBFile(), null);
			pAdvancedNorth.add(getBEval(), null);
		}
		return pAdvancedNorth;
	}
	/**
	 * This method initializes jTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setPreferredSize(new java.awt.Dimension(250,20));
		}
		return jTextField;
	}
	/**
	 * This method initializes bFile
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBFile() {
		if (bFile == null) {
			bFile = new JButton();
			bFile.setText(PluginServices.getText(this,"explorer"));
			bFile.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser jfc = new JFileChooser();
					jfc.addChoosableFileFilter(new GenericFileFilter("py",
							PluginServices.getText(this, "python")));

					if (jfc.showOpenDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
						File fileExpression = jfc.getSelectedFile();
						getJTextField().setText(fileExpression.getAbsolutePath());

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
			fileContents.append((char)c);
		}
		fileReader.close();
		return fileContents.toString();
	}
	/**
	 * This method initializes pAdvancedCenter
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPAdvancedCenter() {
		if (pAdvancedCenter == null) {
			lblLeng = new JLabel();
			lblLeng.setText("");
			pAdvancedCenter = new JPanel();
			pAdvancedCenter.add(lblLeng, null);
		}
		return pAdvancedCenter;
	}

	/**
	 * This method initializes bEval
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBEval() {
		if (bEval == null) {
			bEval = new JButton();
			bEval.setText(PluginServices.getText(this,"evaluate"));
			bEval.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					File file=new File(getJTextField().getText());
					if (!file.exists()) {
						JOptionPane.showMessageDialog((Component)PluginServices.getMainFrame(),PluginServices.getText(this,"incorrect_file"));
						return;
					}
					try {
						interpreter.exec(EvalOperatorsTask.JYTHON,null,-1,-1,readFile(file));
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
	/**
	 * This method initializes jScrollPane3
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane3() {
		if (jScrollPane3 == null) {
			jScrollPane3 = new JScrollPane();
			jScrollPane3.setPreferredSize(new java.awt.Dimension(530,80));
			jScrollPane3.setViewportView(getTxtMessage2());
		}
		return jScrollPane3;
	}
	/**
	 * This method initializes txtMessage2
	 *
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getTxtMessage2() {
		if (txtMessage2 == null) {
			txtMessage2 = new JTextArea();
			txtMessage2.setText(PluginServices.getText(this,"eval_expression_will_be_carried_out_right_now_with_current_values_in_table"));
			//txtMessage2.setSize(new java.awt.Dimension(550,100));
			txtMessage2.setEditable(false);
			txtMessage2.setBackground(UIManager.getColor(this));
		}
		return txtMessage2;
	}
	public Object getWindowProfile() {
		// TODO Auto-generated method stub
		return WindowInfo.TOOL_PROFILE;
		}
	
} //  @jve:decl-index=0:visual-constraint="10,10"
