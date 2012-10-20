/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
package com.iver.cit.gvsig.project.documents.view.legend.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.StringReader;
import java.sql.Types;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;

import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.symbology.fmap.labeling.parse.LabelExpressionParser;
import org.gvsig.symbology.fmap.labeling.parse.ParseException;
import org.gvsig.symbology.fmap.rendering.filter.operations.Expression;
import org.gvsig.symbology.fmap.rendering.filter.operations.ExpressionException;
import org.gvsig.symbology.fmap.rendering.filter.operations.OperationTags;
import org.gvsig.symbology.fmap.rendering.filter.operations.OperatorsUtils;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

/**
 * Implements the panel which is used to create or modify an specific filter
 * expression contained in a VectorialFilterExpressionLegend.
 * 
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 * 
 */
public class ExpressionCreator extends JPanel implements IWindow {

	private static final long serialVersionUID = 1L;
	private FLyrVect layer;
	private String[] fieldNames;
	private String[] operators;
	private int width = 750;
	private int height = 400;
	private DataSource dataSource;
	private Object[] fieldNamesExpression;
	private LabelExpressionParser parser;
	private ISymbol symbolForExpression;
	private String descriptionForExpression;
	private int shapeType;
	private JButton addExpressBut;
	private JButton addOperatorBut;
	private JButton butVerify;
	private JButton butClear;
	private AcceptCancelPanel acceptCancelPanel;

	private JTextArea expressionArea;
	private JList fieldNamesList;
	private JList operatorsList;
	private JTextArea fieldPatternText = new JTextArea();
	private JTextArea operatorPatternText = new JTextArea();
	private JSymbolPreviewButton symbol;
	private JTextArea description;

	private MyListener myBehavior = new MyListener();

	String expression = "";

	/**
	 * Constructor method
	 * 
	 * @param vect
	 */
	public ExpressionCreator(FLyrVect vect) {
		super();
		this.layer = vect;
		this.setOperators(operators);
		getShapeType();
		fieldNamesList = getFieldNames();
		operatorsList = getFieldOperators();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {

		GridBagLayoutPanel topPanel = new GridBagLayoutPanel();
		topPanel.add(getFieldsPanel());
		topPanel.add(getButtonsPanel());
		topPanel.add(getOperatorsPanel());

		expressionArea = new JTextArea();
		expressionArea.setColumns(60);
		expressionArea.setRows(5);
		JScrollPane expressionScroll = new JScrollPane(expressionArea);
		expressionScroll.setPreferredSize(new Dimension(this.width - 125,
				(this.height) / 5));

		GridBagLayoutPanel centPanel = new GridBagLayoutPanel();
		centPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				PluginServices.getText(this, "expression") + " ("
						+ PluginServices.getText(this, "SLD_grammar") + ")",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		centPanel.add(expressionScroll, null);

		JPanel buttPanel = new JPanel();
		buttPanel.setPreferredSize(new Dimension(100, this.height / 7));

		buttPanel.add(getButVerify());
		buttPanel.add(getButClear());

		centPanel.add(buttPanel);

		GridBagLayoutPanel symAndDescPanel = new GridBagLayoutPanel();
		symAndDescPanel.add(getSymbol());
		symAndDescPanel.add(getDescriptionPanel());

		JPanel bottomPanel = new JPanel();
		acceptCancelPanel = new AcceptCancelPanel(action, action);
		bottomPanel.add(acceptCancelPanel, BorderLayout.SOUTH);
		bottomPanel.setPreferredSize(new Dimension(200, 35));

		this.add(topPanel);
		this.add(centPanel);
		this.add(symAndDescPanel);
		this.add(bottomPanel);
	}

	private JPanel getOperatorsPanel() {
		JScrollPane operatorsScroll = new JScrollPane(operatorsList);
		operatorsScroll.setPreferredSize(new Dimension(this.width / 4,
				this.height / 5));

		JPanel operatorsPanel = new JPanel();
		operatorsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, PluginServices.getText(this, "operators"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		operatorsPanel.setPreferredSize(new Dimension((this.width) / 3,
				(this.height + 160) / 3));

		operatorPatternText.setEnabled(false);
		JScrollPane patternOperators = new JScrollPane(operatorPatternText);
		patternOperators.setBorder(javax.swing.BorderFactory
				.createTitledBorder(null,
						PluginServices.getText(this, "pattern"),
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
						null));
		patternOperators.setPreferredSize(new Dimension((this.width - 50) / 3,
				(this.height + 40) / 7));

		operatorsPanel.add(operatorsScroll, null);
		operatorsPanel.add(patternOperators, null);

		return operatorsPanel;
	}

	private JPanel getFieldsPanel() {

		JPanel fieldPanel = new JPanel();
		JScrollPane fieldsScroll = new JScrollPane(fieldNamesList);
		fieldsScroll.setPreferredSize(new Dimension(this.width / 4,
				this.height / 5));

		fieldPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				PluginServices.getText(this, "fields_exp"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		fieldPanel.setPreferredSize(new Dimension((this.width) / 3,
				(this.height + 160) / 3));

		fieldPatternText.setEnabled(false);
		JScrollPane patternFields = new JScrollPane(fieldPatternText);
		patternFields.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, PluginServices.getText(this, "pattern"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		patternFields.setPreferredSize(new Dimension((this.width - 50) / 3,
				(this.height + 40) / 7));

		fieldPanel.add(fieldsScroll, null);
		fieldPanel.add(patternFields, null);

		return fieldPanel;
	}

	private JPanel getButtonsPanel() {
		JPanel myPanelBut = new JPanel();

		addExpressBut = new JButton(PluginServices.getText(this,
				"add_expression"));
		addExpressBut.addActionListener(action);
		myPanelBut.add(addExpressBut);

		addOperatorBut = new JButton(PluginServices.getText(this,
				"add_operator"));
		addOperatorBut.addActionListener(action);

		myPanelBut.add(addOperatorBut);

		return myPanelBut;
	}

	private JButton getButClear() {
		if (butClear == null) {
			butClear = new JButton();
			butClear.setText(PluginServices.getText(this, "clear"));
			butClear.setPreferredSize(new Dimension(80, 20));
			butClear.addActionListener(action);
		}
		return butClear;
	}

	private JButton getButVerify() {
		if (butVerify == null) {
			butVerify = new JButton();
			butVerify.setText(PluginServices.getText(this, "verify"));
			butVerify.setPreferredSize(new Dimension(80, 20));
			butVerify.addActionListener(action);
		}
		return butVerify;
	}

	private JList getFieldNames() {

		try {
			// Para evitar los campos no pertenecientes a la fuente original de
			// la capa.
			dataSource = this.layer.getSource().getRecordset();
			dataSource.start();
			this.fieldNames = dataSource.getFieldNames();
			dataSource.stop();
		} catch (ReadDriverException e) {
			NotificationManager.addError(PluginServices.getText(this,
					"error_trying_to_access_to_the_layer"), e);
		}

		JList myList = new JList(fieldNames);
		myList.addMouseListener(myBehavior);
		myList.addMouseMotionListener(myBehavior);
		myList.addKeyListener(myBehavior);

		return myList;
	}

	private JList getFieldOperators() {

		String[] myOperators = new String[OperatorsUtils.getOperatorNames().length];
		for (int i = 0; i < OperatorsUtils.getOperatorNames().length; i++) {
			if (OperatorsUtils.getOperatorNames()[i].toString().compareTo(
					OperationTags.REPLACE_OP) != 0)
				myOperators[i] = OperatorsUtils.getOperatorNames()[i]
						.toString();
		}
		JList myFieldOperators = new JList(myOperators);
		myFieldOperators.addMouseListener(myBehavior);
		myFieldOperators.addMouseMotionListener(myBehavior);
		myFieldOperators.addKeyListener(myBehavior);

		return myFieldOperators;
	}

	private JScrollPane getDescriptionPanel() {
		if (description == null) {
			description = new JTextArea();
			description.setColumns(55);
			description.setRows(3);

			JScrollPane scroll = new JScrollPane(description);
			scroll.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
					PluginServices.getText(this, "description"),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					null));
			return scroll;
		}
		return null;
	}

	private JPanel getSymbol() {
		if (symbol == null) {
			JPanel panel = new JPanel();
			symbol = new JSymbolPreviewButton(getShapeType());
			symbol.setSymbol(SymbologyFactory
					.createDefaultSymbolByShapeType(shapeType));
			symbol.setPreferredSize(new Dimension(100, 35));
			panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
					PluginServices.getText(this, "symbol"),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					null));
			panel.add(symbol);
			return panel;
		}
		return null;
	}

	/**
	 * Obtains the shapetype of the layer
	 * 
	 * @return int shapetype of the layer
	 */
	private int getShapeType() {

		try {
			shapeType = layer.getShapeType();
		} catch (ReadDriverException e) {
			NotificationManager.addError(PluginServices.getText(this,
					"error_trying_to_access_to_the_layer"), e);
		}
		return shapeType;
	}

	public WindowInfo getWindowInfo() {
		WindowInfo wi = new WindowInfo(WindowInfo.MODALDIALOG
				| WindowInfo.RESIZABLE);
		wi.setTitle(PluginServices.getText(this, "expression_creator"));
		wi.setWidth(this.width);
		wi.setHeight(this.height);

		return wi;
	}

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	private ActionListener action = new ActionListener() {

		Object[] options = { PluginServices.getText(this, "yes"),
				PluginServices.getText(this, "no"),
				PluginServices.getText(this, "cancel") };

		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();
			if ("OK".equals(actionCommand)) {
				if (verifyExpression() && symbol.getSymbol() != null) {
					setExpression(expressionArea.getText());
					setFieldNamesExpression(parser.getClassNames().toArray());
					setDescriptionForExpression(description.getText());
					setSymbolForExpression(symbol.getSymbol());
					PluginServices.getMDIManager().closeWindow(
							ExpressionCreator.this);
				}
				int answer = 0;

				if (!verifyExpression()) {
					answer = JOptionPane
							.showOptionDialog(
									(Component) PluginServices.getMainFrame(),
									PluginServices.getText(this,
											"malformed_or_empty_expression")
											+ "\n"
											+ PluginServices.getText(this,
													"it_will_not_be_added"),
									PluginServices.getText(this,
											"confirmation_dialog"),
									JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE, null,
									options, options[1]);

					if (answer == JOptionPane.OK_OPTION) {
						setExpression(null);
						PluginServices.getMDIManager().closeWindow(
								ExpressionCreator.this);
					}

				} else if (symbol.getSymbol() == null) {
					answer = JOptionPane
							.showOptionDialog(
									(Component) PluginServices.getMainFrame(),
									PluginServices.getText(this,
											"no_symbol_especified")
											+ "\n"
											+ PluginServices
													.getText(this,
															"a_default_symbol_will_be_used"),
									PluginServices.getText(this,
											"confirmation_dialog"),
									JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE, null,
									options, options[1]);

					if (answer == JOptionPane.OK_OPTION) {
						setExpression(expressionArea.getText());
						setFieldNamesExpression(parser.getClassNames()
								.toArray());
						setDescriptionForExpression(description.getText());
						setSymbolForExpression(SymbologyFactory
								.createDefaultSymbolByShapeType(shapeType));
						PluginServices.getMDIManager().closeWindow(
								ExpressionCreator.this);
					}

				}
			} else if ("CANCEL".equals(actionCommand)) {
				setExpression(null);
				PluginServices.getMDIManager().closeWindow(
						ExpressionCreator.this);
			} else if (e.getSource().equals(addExpressBut)) {
				if (fieldNamesList.getSelectedValue() != null) {
					expressionArea.append(" ["
							+ fieldNamesList.getSelectedValue().toString()
							+ "]");
				}
			} else if (e.getSource().equals(addOperatorBut)) {
				if (operatorsList.getSelectedValue() != null) {
					expressionArea.append(" "
							+ operatorsList.getSelectedValue().toString());
				}
			} else if (e.getSource().equals(butClear)) {
				expressionArea.setText("");
				expression = "";
			} else if (e.getSource().equals(butVerify)) {

				if (expressionArea.getText().compareTo("") == 0) {
					JOptionPane.showMessageDialog(
							butVerify,
							PluginServices.getText(this,
									"no_expression_defined") + "\n");
				} else {
					Expression exp = null;
					Hashtable<String, Value> symbols = getSymbolsTable(fieldNames);

					try {
						parser = new LabelExpressionParser(new StringReader(
								expressionArea.getText() + ";"), symbols);
						parser.LabelExpression();
						exp = (Expression) parser.getStack().pop();
						if (exp.evaluate().toString().compareTo("true") == 0
								|| exp.evaluate().toString().compareTo("false") == 0)
							JOptionPane.showMessageDialog(
									butVerify,
									PluginServices.getText(this,
											"correct_expression") + "\n");
						else
							JOptionPane
									.showMessageDialog(
											butVerify,
											PluginServices.getText(this,
													"incorrect_expression")
													+ "\n-"
													+ PluginServices
															.getText(this,
																	"the_result_is_not_a_boolean_value")
													+ "\n");

					} catch (ExpressionException e2) {
						int type = e2.getType();
						String message = "";
						message += PluginServices.getText(this,
								"incorrect_expression") + ":";

						if ((type & ExpressionException.CLASS_CASTING_EXCEPTION) != 0) {
							type = type
									& ~ExpressionException.CLASS_CASTING_EXCEPTION;
							message += "\n-"
									+ PluginServices.getText(this,
											"some_arguments_are_incorrect")
									+ ".\n";
						}
						if ((type & ExpressionException.INCORRECT_NUMBER_OF_ARGUMENTS) != 0) {
							type = type
									& ~ExpressionException.INCORRECT_NUMBER_OF_ARGUMENTS;
							message += "\n-"
									+ PluginServices
											.getText(this,
													"the_number_or_arguments_is_incorrect")
									+ ".\n";
						}
						if ((type & ExpressionException.ARGUMENT_ADDED_TO_CONSTANT) != 0) {
							type = type
									& ~ExpressionException.ARGUMENT_ADDED_TO_CONSTANT;
							message += "\n-"
									+ PluginServices.getText(this,
											"arguments_added_to_a_costant")
									+ ".\n";
						}
						if ((type & ExpressionException.DIVIDED_BY_CERO) != 0) {
							type = type & ~ExpressionException.DIVIDED_BY_CERO;
							message += "\n-"
									+ PluginServices.getText(this,
											"divided_by_0") + ".\n";
						}
						if ((type & ExpressionException.NO_CLASSIF_NAME) != 0) {
							type = type & ~ExpressionException.NO_CLASSIF_NAME;
							message += "\n-"
									+ PluginServices.getText(this,
											"classifying_field_name_not_found")
									+ ".\n";
						}

						JOptionPane.showMessageDialog(butVerify, message);
					} catch (ParseException e2) {

						String message = PluginServices.getText(this,
								"incorrect_expression");
						JOptionPane.showMessageDialog(butVerify, message);
					}

				}
			}
		}

	};

	private Hashtable<String, Value> getSymbolsTable(String[] fNames) {
		Hashtable<String, Value> parser_symbol_table = new Hashtable<String, Value>();

		for (int j = 0; j < fNames.length; j++) {
			try {
				parser_symbol_table.put(fNames[j],
						dataSource.getFieldValue(0, j));
			} catch (ReadDriverException e) {
				e.printStackTrace();
			}
		}
		return parser_symbol_table;
	}

	private String getFieldType(Object selectedValue) {
		String cad = null;
		int type;

		try {
			type = dataSource.getFieldType(dataSource
					.getFieldIndexByName(selectedValue.toString()));

			switch (type) {
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.DECIMAL:
			case Types.DOUBLE:
			case Types.FLOAT:
			case Types.NUMERIC:
				cad = PluginServices.getText(this, "numeric_value");
				break;
			case Types.LONGVARCHAR:
			case Types.VARCHAR:
				cad = PluginServices.getText(this, "string_value");
				break;
			case Types.BOOLEAN:
				cad = PluginServices.getText(this, "boolean_value");
				break;
			case Types.DATE:
				cad = PluginServices.getText(this, "date_value");
				break;
			}
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}
		return cad;
	}

	private class MyListener implements MouseListener, MouseMotionListener,
			KeyListener {

		public void mouseEntered(MouseEvent e) {/* Nothing */
		}

		public void mouseExited(MouseEvent e) {/* Nothing */
		}

		public void mouseReleased(MouseEvent e) {/* Nothing */
		}

		public void mouseDragged(MouseEvent e) {/* Nothing */
		}

		public void mouseMoved(MouseEvent e) {/* Nothing */
		}

		public void keyTyped(KeyEvent e) {/* Nothing */
		}

		public void keyPressed(KeyEvent e) {/* Nothing */
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				if (e.getSource().equals(operatorsList)) {
					int index = operatorsList.locationToIndex(e.getPoint());
					ListModel dlm = operatorsList.getModel();
					Object item = dlm.getElementAt(index);
					;
					operatorsList.ensureIndexIsVisible(index);
					expressionArea.append(" " + item);
				} else if (e.getSource().equals(fieldNamesList)) {
					int index = fieldNamesList.locationToIndex(e.getPoint());
					ListModel dlm = fieldNamesList.getModel();
					Object item = dlm.getElementAt(index);
					;
					fieldNamesList.ensureIndexIsVisible(index);
					expressionArea.append(" [" + item + "]");
				}
			}
		}

		public void keyReleased(KeyEvent e) {
			if (operatorsList.getSelectedValue() != null)
				operatorPatternText.setText(OperatorsUtils
						.getPatternByName(operatorsList.getSelectedValue()
								.toString()));
			if (fieldNamesList.getSelectedValue() != null)
				fieldPatternText.setText(getFieldType(fieldNamesList
						.getSelectedValue()));

			repaint();
		}

		public void mousePressed(MouseEvent e) {
			if (operatorsList.getSelectedValue() != null)
				operatorPatternText.setText(OperatorsUtils
						.getPatternByName(operatorsList.getSelectedValue()
								.toString()));
			if (fieldNamesList.getSelectedValue() != null)
				fieldPatternText.setText(getFieldType(fieldNamesList
						.getSelectedValue()));

			repaint();
		}
	}

	private boolean verifyExpression() {
		Hashtable<String, Value> symbols = getSymbolsTable(fieldNames);

		try {
			parser = new LabelExpressionParser(new StringReader(
					expressionArea.getText()), symbols);

			parser.LabelExpression();

			Expression expr = ((Expression) parser.getStack().pop());

			if (expr.evaluate().toString().compareTo("true") == 0
					|| expr.evaluate().toString().compareTo("false") == 0)

				return true;
			else
				return false;

		} catch (ExpressionException e2) {
			return false;
		} catch (ParseException e) {
			return false;
		}

	}

	public String[] getOperators() {
		return operators;
	}

	public void setOperators(String[] operators) {
		this.operators = operators;
	}

	public String getExpression() {
		return expression;
	}

	public Object[] getFieldNamesExpression() {
		return fieldNamesExpression;
	}

	public void setFieldNamesExpression(Object[] objects) {
		this.fieldNamesExpression = objects;
	}

	public void setDescriptionForExpression(String descriptionForExpression) {
		this.descriptionForExpression = descriptionForExpression;
		description.setText(this.descriptionForExpression);
	}

	public void setSymbolForExpression(ISymbol symbolForExpression) {
		this.symbolForExpression = symbolForExpression;
		this.symbol.setSymbol(this.symbolForExpression);
	}

	public void setExpression(String text) {
		this.expression = text;
		expressionArea.setText(text);

	}

	public String getDescriptionForExpression() {

		if (descriptionForExpression == null
				|| descriptionForExpression.compareTo("") == 0)
			descriptionForExpression = this.expressionArea.getText();

		return descriptionForExpression;
	}

	public ISymbol getSymbolForExpression() {

		if (symbol.getSymbol() != null)
			symbolForExpression = symbol.getSymbol();

		if (symbolForExpression == null)
			symbolForExpression = SymbologyFactory
					.createDefaultSymbolByShapeType(shapeType);

		symbolForExpression.setDescription(getDescriptionForExpression());
		return symbolForExpression;
	}
}
