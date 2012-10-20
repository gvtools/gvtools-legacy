/*
 * Created on 27-abr-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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
package com.iver.cit.gvsig.project.documents.view.legend.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Hashtable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import com.hardcode.gdbms.engine.values.NullValue;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.rendering.NullIntervalValue;
import com.iver.cit.gvsig.fmap.rendering.NullUniqueValue;
import com.iver.cit.gvsig.project.documents.gui.SymbolCellEditor;
import com.iver.cit.gvsig.project.documents.gui.TableSymbolCellRenderer;
import com.iver.cit.gvsig.project.documents.view.legend.edition.gui.IntervalCellEditor;
import com.iver.cit.gvsig.project.documents.view.legend.edition.gui.ValueCellEditor;
import com.iver.utiles.swing.jtable.JTable;
import com.iver.utiles.swing.jtable.TextFieldCellEditor;

/**
 * JPanel que contiene la tabla con los símbolos intervalos o valores y
 * etiquetado de estos valores.
 * 
 * @author Vicente Caballero Navarro
 */
public class SymbolTable extends JPanel {
	private static final long serialVersionUID = -8694846716328735113L;
	private static Hashtable<String, TableCellEditor> cellEditors = new Hashtable<String, TableCellEditor>();

	public static final String VALUES_TYPE = "values";
	public static final String INTERVALS_TYPE = "intervals";
	private JTable table;
	private String type;
	private int shapeType;

	/**
	 * Crea un nuevo FSymbolTable.
	 * 
	 * @param type
	 *            tipo de valor si es intervalo: "intervals" y si es por
	 *            valores: "values".
	 */
	public SymbolTable(Component ownerComponent, String type, int shapeType) {
		super(new GridLayout(1, 0));
		this.type = type;
		this.shapeType = shapeType;

		table = new JTable();
		table.setModel(new MyTableModel());
		table.setPreferredScrollableViewportSize(new Dimension(480, 110));

		initializeCellEditors();

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

		// Set up column sizes.
		// initColumnSizes(table);
		setUpSymbolColumn(table, table.getColumnModel().getColumn(0));

		if (cellEditors.get(type) == null)
			throw new Error("Symbol table type not set!");

		setUpValueColumn(table, table.getColumnModel().getColumn(1),
				cellEditors.get(this.type));
		setUpLabelColumn(table, table.getColumnModel().getColumn(2));

		// Add the scroll pane to this panel.
		add(scrollPane);
		table.setRowSelectionAllowed(true);
	}

	/**
	 * Inicializa los valores de los CellEditors que la SymbolTable poseerá por
	 * defecto
	 */
	private void initializeCellEditors() {
		this.cellEditors.put(this.INTERVALS_TYPE, new IntervalCellEditor());
		this.cellEditors.put(this.VALUES_TYPE, new ValueCellEditor());
	}

	/**
	 * Añade un nuevo CellEditor a la lista de disponibles
	 * 
	 * @param key
	 *            String con el nombre identificativo del CellEditor
	 * @param cellEditor
	 *            CellEditor que va a ser añadido
	 */
	public static void addCellEditor(String key, TableCellEditor cellEditor) {
		cellEditors.put(key, cellEditor);
	}

	/**
	 * Obtiene el valor de los elementos de una fila seleccionada
	 * 
	 * @return Object[] Array con los objetos de cada una de las columnas de la
	 *         fila seleccionada
	 */
	public Object[] getSelectedRowElements() {
		Object[] values = new Object[3];

		MyTableModel m = (MyTableModel) table.getModel();
		int[] selectedRows = table.getSelectedRows();

		if (selectedRows.length != 1)
			return null;

		for (int i = 0; i < 3; i++) {
			values[i] = m.getValueAt(selectedRows[0], i);
		}

		return values;
	}

	/**
	 * Añade una fila al modelo.
	 * 
	 * @param vector
	 *            Fila en forma de vector de Object para añadir al modelo.
	 */
	public void addRow(Object[] vector) {
		MyTableModel m = (MyTableModel) table.getModel();
		m.addRow(vector);

	}

	/**
	 * Elimina la fila que tiene como clave el objeto que se pasa como
	 * parámetro.
	 * 
	 * @param obj
	 *            clave del objeto a eliminar.
	 */
	public void removeRow(Object obj) {
		MyTableModel m = (MyTableModel) table.getModel();

		for (int i = 0; i < m.getRowCount(); i++) {
			if (m.getValueAt(i, 1) instanceof NullUniqueValue
					|| m.getValueAt(i, 1) instanceof NullIntervalValue) {
				m.removeRow(i);
			}
		}
	}

	/**
	 * Elimina las filas que están seleccionadas.
	 */
	public void removeSelectedRows() {
		if (table.getCellEditor() != null) {
			table.getCellEditor().cancelCellEditing();
		}

		MyTableModel m = (MyTableModel) table.getModel();
		int[] selectedRows = table.getSelectedRows();

		for (int i = selectedRows.length - 1; i >= 0; i--) {
			m.removeRow(selectedRows[i]);
		}
	}

	/**
	 * Rellena la tabla con los símbolos valores y descripciones que se pasan
	 * como parámetro.
	 * 
	 * @param symbols
	 *            Array de símbolos
	 * @param values
	 *            Array de valores.
	 * @param descriptions
	 *            Array de descripciones.
	 */
	public void fillTableFromSymbolList(ISymbol[] symbols, Object[] values,
			String[] descriptions) {
		ISymbol theSymbol;

		for (int i = 0; i < symbols.length; i++) {
			theSymbol = symbols[i];
			if (!(values[i] instanceof NullIntervalValue)
					&& !(values[i] instanceof NullUniqueValue))
				addTableRecord(theSymbol, values[i], descriptions[i]);
		}
	}

	/**
	 * Añade una fila con los objetos que se pasan como parámetros.
	 * 
	 * @param symbol
	 *            símbolo de la fila.
	 * @param value
	 *            Valor de la fila.
	 * @param description
	 *            Descripción.
	 */
	public void addTableRecord(ISymbol symbol, Object value, String description) {
		Object[] theRow = new Object[3];
		theRow[0] = symbol;
		theRow[1] = value;
		theRow[2] = description;
		addRow(theRow);
	}

	/**
	 * Devuelve el valor a partie del número de fila y columna.
	 * 
	 * @param row
	 *            número de fila.
	 * @param col
	 *            número de columna.
	 * 
	 * @return Objeto.
	 */
	public Object getFieldValue(int row, int col) {
		MyTableModel m = (MyTableModel) table.getModel();

		return m.getValueAt(row, col);
	}

	/**
	 * Devuelve el número total de filas que contiene el modelo.
	 * 
	 * @return Número de filas.
	 */
	public int getRowCount() {
		MyTableModel m = (MyTableModel) table.getModel();

		return m.getRowCount();
	}

	/**
	 * Elimina todas las filas del modelo.
	 */
	public void removeAllItems() {
		table.setModel(new MyTableModel());
		setUpSymbolColumn(table, table.getColumnModel().getColumn(0));
		setUpValueColumn(table, table.getColumnModel().getColumn(1),
				cellEditors.get(this.type));
		setUpLabelColumn(table, table.getColumnModel().getColumn(2));
	}

	/**
	 * Inicializa el cell editor de tipo descripción de la columna que se pasa
	 * como parámetro.
	 * 
	 * @param table2
	 *            Tabla.
	 * @param column
	 *            Columna.
	 */
	public void setUpLabelColumn(JTable table2, TableColumn column) {
		TextFieldCellEditor labeleditor = new TextFieldCellEditor();
		column.setCellEditor(labeleditor);
	}

	/**
	 * Inicializa el cell editor de tipo valor de la columna que se pasa como
	 * parámetro.
	 * 
	 * @param table2
	 *            Tabla.
	 * @param column
	 *            Columna.
	 * @param tableCellEditor
	 */
	public void setUpValueColumn(JTable table2, TableColumn column,
			TableCellEditor tableCellEditor) {
		column.setCellEditor(tableCellEditor);
	}

	/**
	 * Inicializa el cell editor de tipo símbolo de la columna que se pasa como
	 * parámetro.
	 * 
	 * @param table2
	 *            Tabla.
	 * @param column
	 *            Columna.
	 */
	public void setUpSymbolColumn(JTable table2, TableColumn column) {
		// Set up the editor
		column.setMaxWidth(100);
		column.setWidth(60);
		column.setPreferredWidth(60);
		column.setMinWidth(50);

		// FSymbolCellEditor symboleditor = new FSymbolCellEditor();
		SymbolCellEditor symboleditor = new SymbolCellEditor();
		column.setCellEditor(symboleditor);

		TableSymbolCellRenderer renderer = new TableSymbolCellRenderer(true);
		column.setCellRenderer(renderer);
	}

	public int[] getSelectedRows() {
		return table.getSelectedRows();
	}

	public void moveDownRows(int startPos, int endPos, int numOfElements) {
		if (startPos > endPos)
			return;
		if (startPos >= getRowCount() - 1)
			return;
		if (startPos == getRowCount() - 1)
			return;

		Object[][] values = new Object[getRowCount()][3];
		for (int i = 0; i < getRowCount(); i++) {
			values[i][0] = table.getModel().getValueAt(i, 0);
			values[i][1] = table.getModel().getValueAt(i, 1);
			values[i][2] = table.getModel().getValueAt(i, 2);
		}

		Object[][] aux = new Object[numOfElements][3];
		for (int i = 0; i < numOfElements; i++) {

			aux[numOfElements - i - 1][0] = values[startPos - i][0];
			aux[numOfElements - i - 1][1] = values[startPos - i][1];
			aux[numOfElements - i - 1][2] = values[startPos - i][2];
		}

		Object[][] targetVal = { { values[endPos][0], values[endPos][1],
				values[endPos][2] } };

		values[startPos - numOfElements + 1][0] = targetVal[0][0];
		values[startPos - numOfElements + 1][1] = targetVal[0][1];
		values[startPos - numOfElements + 1][2] = targetVal[0][2];

		for (int i = 0; i < numOfElements; i++) {
			values[endPos - i][0] = aux[numOfElements - i - 1][0];
			values[endPos - i][1] = aux[numOfElements - i - 1][1];
			values[endPos - i][2] = aux[numOfElements - i - 1][2];
		}

		ISymbol[] symbols = new ISymbol[getRowCount()];
		Object[] objects = new Object[getRowCount()];
		String[] cads = new String[getRowCount()];

		for (int i = 0; i < getRowCount(); i++) {
			symbols[i] = (ISymbol) values[i][0];
			objects[i] = values[i][1];
			cads[i] = (String) values[i][2];
		}

		removeAllItems();
		fillTableFromSymbolList(symbols, objects, cads);
		table.addRowSelectionInterval(endPos - numOfElements + 1, endPos);
	}

	public void moveUpRows(int startPos, int endPos, int numOfElements) {

		if (startPos == 0)
			return;
		if (endPos > startPos)
			return;

		Object[][] values = new Object[getRowCount()][3];
		for (int i = 0; i < getRowCount(); i++) {
			values[i][0] = table.getModel().getValueAt(i, 0);
			values[i][1] = table.getModel().getValueAt(i, 1);
			values[i][2] = table.getModel().getValueAt(i, 2);
		}

		Object[][] aux = new Object[numOfElements][3];
		for (int i = 0; i < numOfElements; i++) {

			aux[i][0] = values[startPos + i][0];
			aux[i][1] = values[startPos + i][1];
			aux[i][2] = values[startPos + i][2];
		}

		Object[][] targetVal = { { values[endPos][0], values[endPos][1],
				values[endPos][2] } };

		values[startPos + numOfElements - 1][0] = targetVal[0][0];
		values[startPos + numOfElements - 1][1] = targetVal[0][1];
		values[startPos + numOfElements - 1][2] = targetVal[0][2];

		for (int i = 0; i < numOfElements; i++) {

			values[endPos + i][0] = aux[i][0];
			values[endPos + i][1] = aux[i][1];
			values[endPos + i][2] = aux[i][2];
		}

		ISymbol[] symbols = new ISymbol[getRowCount()];
		Object[] objects = new Object[getRowCount()];
		String[] cads = new String[getRowCount()];

		for (int i = 0; i < getRowCount(); i++) {
			symbols[i] = (ISymbol) values[i][0];
			objects[i] = values[i][1];
			cads[i] = (String) values[i][2];
		}

		removeAllItems();
		fillTableFromSymbolList(symbols, objects, cads);
		table.addRowSelectionInterval(endPos, endPos + numOfElements - 1);
	}

	/**
	 * Modelo que propio que se aplica a la tabla.
	 * 
	 * @author Vicente Caballero Navarro
	 */
	class MyTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;

		// AbstractTableModel {
		private String[] columnNames = {
				PluginServices.getText(this, "Simbolo"),
				PluginServices.getText(this, "Valor"),
				PluginServices.getText(this, "Etiqueta") };

		/**
		 * Devuelve el número de columnas.
		 * 
		 * @return Número de columnas.
		 */
		public int getColumnCount() {
			return columnNames.length;
		}

		/**
		 * Devuelve el String del valor de la columna.
		 * 
		 * @param col
		 *            Número de columna.
		 * 
		 * @return Nombre de la columna.
		 */
		public String getColumnName(int col) {
			return columnNames[col];
		}

		/**
		 * JTable uses this method to determine the default renderer/ editor for
		 * each cell. If we didn't implement this method, then the last column
		 * would contain text ("true"/"false"), rather than a check box.
		 */
		public Class getColumnClass(int c) {
			if (getValueAt(0, c) == null) {
				return NullValue.class;
			}
			return getValueAt(0, c).getClass();
		}

		/*
		 * Don't need to implement this method unless your table's editable.
		 */
		public boolean isCellEditable(int row, int col) {
			// Note that the data/cell address is constant,
			// no matter where the cell appears onscreen.
			// if (col > 0) {
			return true;
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (column == 2)
				return ((ISymbol) getValueAt(row, 0)).getDescription();

			return super.getValueAt(row, column);
		}

		@Override
		public void setValueAt(Object aValue, int row, int column) {

			if (column == 0) {
				ISymbol oldSymbol = (ISymbol) getValueAt(row, 0);
				ISymbol newSymbol = (ISymbol) aValue;
				newSymbol.setDescription(oldSymbol.getDescription());
				// TODO: Habría que enviar a la leyenda un replace(oldSymbol,
				// newSymbol)
				super.setValueAt(newSymbol, row, column);
			} else {
				if (column == 2) {
					ISymbol symbol = (ISymbol) getValueAt(row, 0);
					symbol.setDescription((String) aValue);
					setValueAt(symbol, row, 0);
				}

				super.setValueAt(aValue, row, column);
			}
		}

	}
}
