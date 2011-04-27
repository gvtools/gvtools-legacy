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
package com.iver.cit.gvsig.gui.filter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;
import org.gvsig.gui.beans.filterPanel.tableFilterQueryPanel.TableFilterQueryJPanel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.instruction.IncompatibleTypesException;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.DateValue;
import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.StringValue;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.IWindowListener;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.utiles.DefaultCharSet;
import com.iver.utiles.StringUtilities;
import com.iver.utiles.exceptionHandling.ExceptionHandlingSupport;
import com.iver.utiles.exceptionHandling.ExceptionListener;

/**
 * This class substitutes the old "FilterDialog" class made by "Fernando González Cortés"
 * The functionality is the same, but now the class is made from separately (and reusable) components
 *
 * @author Pablo Piqueras Bartolomé (p_queras@hotmail.com)
 */
public class FilterDialog extends TableFilterQueryJPanel implements IWindow, IWindowListener {
	private static Logger logger = Logger.getLogger(Table.class.getName());
	private ExpressionDataSource model = null;
	private ArrayList expressionListeners = new ArrayList();
	private ExceptionHandlingSupport exceptionHandlingSupport = new ExceptionHandlingSupport();
	private NumberFormat nf = NumberFormat.getNumberInstance();

	private String title;

	private final int filterDialog_Width = 500;
	private final int filterDialog_Height = 362;
	private final int widthIncrementForAndami = 20; // This is necessary because when the panel is sent to Andami, that needs a bit more width-space to show that panel.


	/**
	 * This is the default constructor
	 */
	public FilterDialog(String _title) {
		super();
		title = _title;
		defaultTreeModel = (DefaultTreeModel)fieldsJTree.getModel();
	}
	/**
	 * This is the default constructor
	 */
	public FilterDialog() {
		super();
		defaultTreeModel = (DefaultTreeModel)fieldsJTree.getModel();
	}

	/*
	 *  (non-Javadoc)
	 * @see org.gvsig.gui.beans.filterPanel.AbstractFilterQueryJPanel#initialize()
	 */
	protected void initialize() {
		super.initialize();

		super.resizeHeight(filterDialog_Height);
		super.resizeWidth(filterDialog_Width - widthIncrementForAndami);

		this.addNewListeners();
	}

	/**
	 * Adds some listeners
	 */
	private void addNewListeners() {
		// Listener for "btnAdd"
		// Adds more elements to the current set
		getBtnAddToCurrentSet().addActionListener(new java.awt.event.ActionListener() {
			/*
			 *  (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(java.awt.event.ActionEvent e) {
				final String expr = "select * from '" +
					model.getDataSourceName() + "' where " +
					getTxtExpression().getText() + ";";

				logger.debug(expr);

				PluginServices.backgroundExecution(new Runnable() {
						public void run() {
							for (int i = 0;
									i < expressionListeners.size();
									i++) {
								ExpressionListener l = (ExpressionListener) expressionListeners.get(i);
								l.addToSet(expr);
							}
						}
					});
			}
		});

		// Listener for "btnNuevo"
		// Adds a new set
		getBtnNewSet().addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				final String expr = "select * from '" +
					model.getDataSourceName() + "' where " +
					getTxtExpression().getText() + ";";

				logger.debug(expr);

				PluginServices.backgroundExecution(new Runnable() {
					public void run() {
						for (int i = 0; i < expressionListeners.size(); i++) {
							ExpressionListener l = (ExpressionListener) expressionListeners.get(i);
							l.newSet(expr);
						}
					}
				});
			}
		});

		// Listener for "btnFromSet"
		// Selects elements in the table that are according the current filter condition
		getBtnFromSet().addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				final String expr = "select * from '" +
					model.getDataSourceName() + "' where " +
					getTxtExpression().getText() + ";";

				logger.debug(expr);

				PluginServices.backgroundExecution(new Runnable() {
					public void run() {
						for (int i = 0; i < expressionListeners.size(); i++) {
							ExpressionListener l = (ExpressionListener) expressionListeners.get(i);
							l.fromSet(expr);
						}
					}
				});
			}
		});

		// Listener for "fieldsJTree"
		getFieldsJTree().addMouseListener(new MouseAdapter() {
			/*
			 *  (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e) {
				int row = fieldsJTree.getRowForLocation(e.getX(), e.getY());

				if (row > -1) {
					switch (e.getClickCount()) {
						case 1:
							fillValues(row);
							break;
						case 2:
							String alias = jtreeRoot.getChildAt(row).toString();
							String name;
							try {
								name = model.getFieldName(row);
								putSymbol(name);
							} catch (FilterException e1) {
								e1.printStackTrace();
							}
							
							break;
					}
				}
			}
		});

		// Listener for "valuesJList"
		getValuesJList().addMouseListener(new MouseAdapter() {
			/*
			 *  (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					Value valor = (Value) valuesListModel.getElementAt(getValuesJList().getSelectedIndex());

					if (valor instanceof DateValue) {
						putSymbol("date('" + valor + "')");
					} else if (valor instanceof BooleanValue) {
						putSymbol("boolean('" + valor.toString() + "')");
					} else if (valor instanceof StringValue) {
						putSymbol("'" + valor.toString().replaceAll("'","''") + "'");
					} else {
						putSymbol(valor.toString());
					}
				}
			}
		});
	}

	/**
	 * Rellena la lista con los valores del campo seleccionado
	 */
	private void fillValues(int row) {
		//int index = lstCampos.getSelectedIndex();

		//Index es ahora el índice del campo seleccionado
		//Se eliminan los duplicados
		TreeSet conjunto = new TreeSet(new Comparator() {
			public int compare(Object o1, Object o2) {
				if ((o1 != null) && (o2 != null)) {
					Value v2 = (Value) o2;
					Value v1 = (Value) o1;
					BooleanValue boolVal;

					try {
						boolVal = (BooleanValue) (v1.greater(v2));

						if (boolVal.getValue()) {
							return 1;
						}

						boolVal = (BooleanValue) (v1.less(v2));

						if (boolVal.getValue()) {
							return -1;
						}
					} catch (IncompatibleTypesException e) {
						throw new RuntimeException(e);
					}
				}

				return 0;
			}
		}); // Para poder ordenar


		valuesListModel.clear();
		try {
			for (int i = 0; i < model.getRowCount(); i++) {
				Value value = model.getFieldValue(i, row);

				if (value instanceof NullValue)
				    continue;

				if (!conjunto.contains(value)) {
				    conjunto.add(value);
				}
			}

			Iterator it = conjunto.iterator();

			while (it.hasNext())
				valuesListModel.addElement(it.next());
		} catch (FilterException e) {
			throwException(e);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param t DOCUMENT ME!
	 */
	public void setModel(ExpressionDataSource t) {
		try {
			model = t;
            model.start();
        } catch (ReadDriverException e1) {
            NotificationManager.addError(e1.getMessage(), e1);
        }

        jtreeRoot.removeAllChildren();

        try {
			for (int i = 0; i < model.getFieldCount(); i++) {
				Object field = model.getFieldAlias(i);

				if (field != null) {
					jtreeRoot.add(new DefaultMutableTreeNode(field.toString()));
				}
			}

			defaultTreeModel.setRoot(jtreeRoot);
		} catch (FilterException e) {
			throwException(e);
		}
	}

		/**
		 * DOCUMENT ME!
		 *
		 * @return DOCUMENT ME!
		 *
		 * @throws ParseException DOCUMENT ME!
		 */
		private String validateExpression() throws ParseException {
			String expression = txtExpression.getText();
	//		HashSet variablesIndexes = new HashSet();
	//
	//		StringBuffer traducida = new StringBuffer();

			//Se transforman los nombres de los campos en las variables xix que analizarán
			//Se quitan los Date(fecha) y se mete la fecha correspondiente
			expression = translateDates(expression);
			expression = translateNumber(expression);
			expression = translateWord(expression, "true", "1");
			expression = translateWord(expression, "false", "0");

			String replacement;
			Pattern patron = Pattern.compile("[^<>!]=");
			Matcher m = patron.matcher(expression);
			int index = 0;

			while (m.find(index)) {
				index = m.start();
				replacement = expression.charAt(index) + "==";
				m.replaceFirst(replacement);
				index++;
			}

			expression = expression.replaceAll("[^<>!]=", "==");

			logger.debug(expression);

			return expression;
		}
	/**
	 * Redefinition of the 'putSymbol' method of AbstractFilterQueryJPanel
	 *   (I've made this redefinition for write the same code as the 'putSymbol'
	 *    code of the original class (FilterDialog) that was in this project
	 *    (appgvSIG) and didn't has path troubles to find 'StringUtilities').
	 *
	 * Sets a symbol on the filter expression (JTextArea that stores and shows
	 *   the current filter expression)
	 *
	 * @param symbol A symbol: character, characters, number, ...
	 */
	protected void putSymbol(String symbol) {
		int position = txtExpression.getCaretPosition();
		txtExpression.setText(StringUtilities.insert(txtExpression.getText(),
				position, symbol));

		if (symbol.equals(" () ")) {
			position = position + 2;
		} else {
			position = position + symbol.length();
		}

		txtExpression.setCaretPosition(position);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param expresion DOCUMENT ME!
	 * @param substring DOCUMENT ME!
	 * @param startingPos DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	private int getIndex(String expresion, String substring, int startingPos) {
		int index = startingPos;

		do {
			index = expresion.indexOf(substring, index);
		} while ((StringUtilities.isBetweenSymbols(expresion, index, "\"")) &&
				(index != -1));

		return index;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param expresion DOCUMENT ME!
	 * @param word DOCUMENT ME!
	 * @param translation DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws ParseException DOCUMENT ME!
	 */
	private String translateWord(String expresion, String word,
		String translation) throws ParseException {
		int booleanIndex = 0;
		int endIndex = 0;
		StringBuffer res = new StringBuffer();

		while ((booleanIndex = getIndex(expresion, word, booleanIndex)) != -1) {
			res.append(expresion.substring(endIndex, booleanIndex));
			endIndex = booleanIndex + word.length();
			booleanIndex++;
			res.append(translation);
		}

		if (endIndex < expresion.length()) {
			res.append(expresion.substring(endIndex));
		}

		return res.toString();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param expresion DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws ParseException DOCUMENT ME!
	 */
	private String translateDates(String expresion) throws ParseException {
		//Se obtiene el valor de la fecha
		String date = StringUtilities.substringDelimited(expresion, "Date(",
				")", 0);

		if (date == null) {
			return expresion;
		}

		//Se comprueba que no esté entre comillas
		int startIndex = expresion.indexOf(date);

		while (startIndex != -1) {
			if (!StringUtilities.isBetweenSymbols(expresion, startIndex, "\"")) {
				//Se sustituye por el valor ordinal de la fecha
				expresion = expresion.substring(0, startIndex - 5) +
					expresion.substring(startIndex).replaceFirst(date + "\\)",
						new Long((filterButtonsJPanel.getDateFormat().parse(date)).getTime()).toString());
				;
			} else {
				startIndex += date.length();
			}

			//Se obtiene el valor de la fecha

			/*            date = StringUtilities.substringDelimited(expresion, "Date(", ")",
			   startIndex);
			 */
			if (date == null) {
				return expresion;
			}

			startIndex = expresion.indexOf(date, startIndex);
		}

		return expresion;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param expresion DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws ParseException DOCUMENT ME!
	 */
	public String translateNumber(String expresion) throws ParseException {
		DefaultCharSet ss = new DefaultCharSet();
		ss.addInterval('0', '9');
		ss.addCharacter(',');
		ss.addCharacter('.');

		String number = StringUtilities.substringWithSymbols(expresion, ss, 0);

		if (number == null) {
			return expresion;
		}

		int startIndex = expresion.indexOf(number);

		while (startIndex != -1) {
			Number n = nf.parse(number);

			if (!StringUtilities.isBetweenSymbols(expresion, startIndex, "\"")) {
				//Se sustituye por el valor ordinal de la fecha
				expresion = expresion.substring(0, startIndex) +
					expresion.substring(startIndex).replaceFirst(number,
						n.toString());
			} else {
				startIndex += n.toString().length();
			}

			number = StringUtilities.substringWithSymbols(expresion, ss,
					startIndex);

			if (number == null) {
				return expresion;
			}

			startIndex = expresion.indexOf(number, startIndex);
		}

		return expresion;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param arg0
	 *
	 * @return
	 */
	public boolean addExpressionListener(ExpressionListener arg0) {
		return expressionListeners.add(arg0);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param arg0
	 *
	 * @return
	 */
	public boolean removeExpressionListener(ExpressionListener arg0) {
		return expressionListeners.remove(arg0);
	}

	/**
	 * @see com.iver.mdiApp.ui.MDIManager.IWindow#getWindowInfo()
	 */
	public WindowInfo getWindowInfo() {
		WindowInfo vi = new WindowInfo(WindowInfo.ICONIFIABLE);

		//if (System.getProperty("os.name")co.compareTo(arg0))
		vi.setHeight(this.filterDialog_Height);
		vi.setWidth(this.filterDialog_Width);

		// Old instructions
//		vi.setWidth(480);
//		vi.setHeight(362);
		vi.setTitle(PluginServices.getText( this, "filtro") + " (" + title + ")");
		return vi;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param o DOCUMENT ME!
	 */
	public void addExceptionListener(ExceptionListener o) {
		exceptionHandlingSupport.addExceptionListener(o);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param o DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean removeExceptionListener(ExceptionListener o) {
		return exceptionHandlingSupport.removeExceptionListener(o);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param t DOCUMENT ME!
	 */
	private void throwException(Throwable t) {
		exceptionHandlingSupport.throwException(t);
	}

    /* (non-Javadoc)
     * @see com.iver.andami.ui.mdiManager.ViewListener#viewActivated()
     */
    public void windowActivated() {
    }

    /* (non-Javadoc)
     * @see com.iver.andami.ui.mdiManager.ViewListener#viewClosed()
     */
    public void windowClosed() {
        try {
            model.stop();
        } catch (ReadDriverException e) {
            NotificationManager.addError(e.getMessage(), e);
        }
    }
    public Object getWindowProfile() {
		return WindowInfo.TOOL_PROFILE;
	}
}