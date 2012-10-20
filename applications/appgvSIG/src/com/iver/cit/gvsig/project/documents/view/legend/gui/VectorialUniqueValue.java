/* gvSIG. Sistema de Informaciï¿½n Geogrï¿½fica de la Generalitat Valenciana
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
 *   Av. Blasco Ibï¿½ï¿½ez, 50
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JBlank;
import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.raster.datastruct.ColorItem;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.layers.layerOperations.ClassifiableVectorial;
import com.iver.cit.gvsig.fmap.rendering.AbstractClassifiedVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;
import com.iver.cit.gvsig.fmap.rendering.NullUniqueValue;
import com.iver.cit.gvsig.fmap.rendering.VectorialUniqueValueLegend;
import com.iver.cit.gvsig.fmap.rendering.ZSort;
import com.iver.cit.gvsig.gui.styling.JComboBoxColorScheme;
import com.iver.cit.gvsig.gui.styling.SymbolLevelsWindow;

/**
 * DOCUMENT ME!
 * 
 * @author fjp To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class VectorialUniqueValue extends JPanel implements ILegendPanel,
		ActionListener {
	private static Logger logger = Logger.getLogger(VectorialUniqueValue.class
			.getName());

	protected VectorialUniqueValueLegend theLegend;
	private ClassifiableVectorial layer;
	private SymbolTable symbolTable;
	protected JComboBox cmbFields;
	private JButton btnRemoveAll;
	private JButton btnRemove;
	private JButton moveUp;
	private JButton moveDown;
	private JCheckBox chbUseDefault = null;
	private JSymbolPreviewButton defaultSymbolPrev;
	private VectorialUniqueValueLegend auxLegend;
	private JPanel pnlCenter;
	private JPanel pnlMovBut;
	private JButton btnOpenSymbolLevelsEditor;
	private GridBagLayoutPanel defaultSymbolPanel = new GridBagLayoutPanel();

	private JComboBoxColorScheme cmbColorScheme;

	/**
	 *
	 */
	public VectorialUniqueValue() {
		super();
		initComponents();
	}

	/**
	 * DOCUMENT ME!
	 */
	protected void initComponents() {

		JPanel pnlButtons = new JPanel();

		JButton btnAddAll = new JButton(PluginServices.getText(this,
				"Anadir_todos"));
		btnAddAll.setActionCommand("ADD_ALL_VALUES");
		btnAddAll.addActionListener(this);
		pnlButtons.add(btnAddAll);

		JButton btnAdd = new JButton(PluginServices.getText(this, "Anadir"));
		btnAdd.setActionCommand("ADD_VALUE");
		btnAdd.addActionListener(this);
		pnlButtons.add(btnAdd);

		btnRemoveAll = new JButton(PluginServices.getText(this, "Quitar_todos"));
		btnRemoveAll.setActionCommand("REMOVE_ALL");
		btnRemoveAll.addActionListener(this);
		pnlButtons.add(btnRemoveAll);

		btnRemove = new JButton(PluginServices.getText(this, "Quitar"));
		btnRemove.setActionCommand("REMOVE");
		btnRemove.addActionListener(this);
		pnlButtons.add(btnRemove);

		btnOpenSymbolLevelsEditor = new JButton(PluginServices.getText(this,
				"symbol_levels"));
		btnOpenSymbolLevelsEditor.addActionListener(this);
		btnOpenSymbolLevelsEditor.setActionCommand("OPEN_SYMBOL_LEVEL_EDITOR");
		pnlButtons.add(btnOpenSymbolLevelsEditor);
		btnOpenSymbolLevelsEditor.setEnabled(symbolTable != null
				&& symbolTable.getRowCount() > 0);

		pnlCenter = new JPanel();
		pnlCenter.setLayout(new BorderLayout());

		cmbFields = new JComboBox();
		cmbFields.setActionCommand("FIELD_SELECTED");
		cmbFields.addActionListener(this);
		cmbFields.setVisible(true);

		JPanel pnlNorth = new JPanel();
		pnlNorth.setLayout(new GridLayout(0, 2));

		GridBagLayoutPanel auxPanel = new GridBagLayoutPanel();
		JLabel lblFieldClassification = new JLabel(PluginServices.getText(this,
				"Campo_de_clasificacion") + ": ");
		auxPanel.add(lblFieldClassification);
		auxPanel.add(cmbFields);
		pnlNorth.add(auxPanel);

		auxPanel = new GridBagLayoutPanel();
		auxPanel.add(new JLabel(PluginServices.getText(this, "color_scheme")
				+ ": "));
		cmbColorScheme = new JComboBoxColorScheme(false);
		cmbColorScheme.addActionListener(this);
		auxPanel.add(cmbColorScheme);
		pnlNorth.add(auxPanel);

		defaultSymbolPanel.add(getChbUseDefault(), null);
		pnlNorth.add(defaultSymbolPanel);
		pnlNorth.add(new JBlank(0, 30));

		this.setLayout(new BorderLayout());
		this.add(pnlNorth, BorderLayout.NORTH);
		this.add(pnlCenter, BorderLayout.CENTER);
		this.add(pnlButtons, BorderLayout.SOUTH);

	}

	/**
	 * DOCUMENT ME!
	 */
	private void fillTableValues() {
		DataSource elRs;

		try {
			elRs = ((FLyrVect) layer).getRecordset();
			logger.debug("elRs.start()");
			elRs.start();

			int idField = -1;
			String fieldName = (String) cmbFields.getSelectedItem();
			if (fieldName == null) {
				JOptionPane.showMessageDialog((Component) PluginServices
						.getMainFrame(), PluginServices.getText(this,
						"no_hay_campo_seleccionado"));
				return;
			}

			idField = elRs.getFieldIndexByName(fieldName);
			auxLegend = LegendFactory.createVectorialUniqueValueLegend(layer
					.getShapeType());
			auxLegend.setDefaultSymbol(defaultSymbolPrev.getSymbol());
			auxLegend.setClassifyingFieldNames(new String[] { fieldName });

			// long numReg = elRs.getRowCount();
			if (idField == -1) {
				NotificationManager.addWarning(
						PluginServices.getText(this, "unrecognized_field_name")
								+ " " + fieldName, null);

				return;
			}

			symbolTable.removeAllItems();

			int numSymbols = 0;
			ISymbol theSymbol = null;

			// auxLegend=(VectorialUniqueValueLegend)m_lyr.getLegend();
			// auxLegend =
			// LegendFactory.createVectorialUniqueValueLegend(layer.getShapeType());

			Value clave;

			ColorItem[] colorScheme = cmbColorScheme.getSelectedColors();

			Color[] colors = new Color[colorScheme.length];
			for (int i = 0; i < colorScheme.length; i++) {
				colors[i] = colorScheme[i].getColor();
			}
			auxLegend.setColorScheme(colors);

			Random rand = new Random(System.currentTimeMillis());

			for (int j = 0; j < elRs.getRowCount(); j++) {
				clave = elRs.getFieldValue(j, idField);

				if (clave instanceof NullValue) {
					continue;
				}

				// //Comprobar que no esta repetido y no hace falta introducir
				// en el hashtable el campo junto con el simbolo.
				if (auxLegend.getSymbolByValue(clave) == null) {
					// si no esta creado el simbolo se crea
					// jaume (moved to ISymbol); theSymbol = new
					// FSymbol(layer.getShapeType());
					theSymbol = SymbologyFactory
							.createDefaultSymbolByShapeType(layer
									.getShapeType(), colorScheme[rand
									.nextInt(colorScheme.length)].getColor());
					theSymbol.setDescription(clave.toString());
					auxLegend.addSymbol(clave, theSymbol);

					numSymbols++;

					if (numSymbols == 100) {
						int resp = JOptionPane.showConfirmDialog(this,
								PluginServices.getText(this,
										"mas_de_100_simbolos"), PluginServices
										.getText(this, "quiere_continuar"),
								JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE);

						if ((resp == JOptionPane.NO_OPTION)
								|| (resp == JOptionPane.DEFAULT_OPTION)) {
							return;
						}
					}
				}
			} // for

			symbolTable.fillTableFromSymbolList(auxLegend.getSymbols(),
					auxLegend.getValues(), auxLegend.getDescriptions());
			elRs.stop();
		} catch (ReadDriverException e) {
			NotificationManager.addError(
					PluginServices.getText(this, "recovering_recordset"), e);
		}

		btnRemoveAll.setEnabled(true);
		btnRemove.setEnabled(true);

		// m_bCacheDirty = false;
	}

	private boolean compareClassifyingFieldNames(String[] a, String[] b) {
		if (a == b)
			return true;
		if (a == null || b == null)
			return false;
		if (a.length != b.length)
			return false;
		for (int i = 0; i < a.length; i++) {
			if (!a[i].equals(b[i]))
				return false;
		}
		return true;
	}

	private boolean compareClassifyingFieldTypes(int[] a, int[] b) {
		if (a == b)
			return true;
		if (a == null || b == null)
			return false;
		if (a.length != b.length)
			return false;
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i])
				return false;
		}
		return true;
	}

	/**
	 * A partir de los registros de la tabla, regenera el FRenderer. (No solo el
	 * symbolList, si no tambiï¿½n el arrayKeys y el defaultRenderer
	 */
	private void fillSymbolListFromTable() {
		Value clave;
		ISymbol theSymbol;
		ArrayList<Value> visitedKeys = new ArrayList();
		boolean changedLegend = false;

		String fieldName = (String) cmbFields.getSelectedItem();
		String[] classifyingFieldNames = new String[] { fieldName };
		if (auxLegend != null) {
			if (!compareClassifyingFieldNames(classifyingFieldNames,
					auxLegend.getClassifyingFieldNames())) {
				auxLegend.setClassifyingFieldNames(classifyingFieldNames);
				changedLegend = true;
			}
		} else {
			auxLegend.setClassifyingFieldNames(classifyingFieldNames);
			changedLegend = true;
		}

		FLyrVect m = (FLyrVect) layer;
		try {
			// int fieldType =
			// m.getSource().getRecordset().getFieldType((int)cmbFields.getSelectedIndex());
			int fieldType = m.getRecordset().getFieldType(
					(int) cmbFields.getSelectedIndex());
			int[] classifyingFieldTypes = new int[] { fieldType };
			if (auxLegend != null) {
				if (!compareClassifyingFieldTypes(classifyingFieldTypes,
						auxLegend.getClassifyingFieldTypes())) {
					auxLegend.setClassifyingFieldTypes(classifyingFieldTypes);
					changedLegend = true;
				}
			} else {
				auxLegend.setClassifyingFieldTypes(classifyingFieldTypes);
				changedLegend = true;
			}
		} catch (ReadDriverException e) {
			NotificationManager.addError(
					PluginServices.getText(this, "could_not_setup_legend"), e);
		} catch (Exception e) {
			NotificationManager.showMessageWarning(
					PluginServices.getText(this, "could_not_setup_legend"), e);
		}

		if (changedLegend) {
			auxLegend.clear();
		}

		for (int row = 0; row < symbolTable.getRowCount(); row++) {
			clave = (Value) symbolTable.getFieldValue(row, 1);
			theSymbol = (ISymbol) symbolTable.getFieldValue(row, 0);
			String description = (String) symbolTable.getFieldValue(row, 2);
			theSymbol.setDescription(description);
			ISymbol legendSymbol = null;
			if (auxLegend != null) {
				legendSymbol = auxLegend.getSymbolByValue(clave);
			}
			if (legendSymbol == null
					|| (auxLegend.isUseDefaultSymbol() && legendSymbol == auxLegend
							.getDefaultSymbol())) {
				if (auxLegend != null) {
					auxLegend.addSymbol(clave, theSymbol);
				}
			} else {
				/*
				 * FIXME: Se optimizaría descomentarizando el if, pero el metodo
				 * equals del AbstractSymbol no tiene en cuenta determinadas
				 * propiedades del simbolo, como, por ejemplo, el tamaño.
				 * Descomentarizar al arreglar el metodo equals del
				 * AbstractSymbol.
				 */
				// if(!legendSymbol.equals(theSymbol)){
				auxLegend.replace(legendSymbol, theSymbol);
				// }
			}
			visitedKeys.add(clave);
		}
		if (auxLegend != null) {
			Object[] keys = auxLegend.getValues();
			for (int i = 0; i < keys.length; i++) {
				Object key = keys[i];
				if (!visitedKeys.contains(key)) {
					auxLegend.delSymbol(key);
				}
			}
		}

		clave = new NullUniqueValue();
		if (chbUseDefault.isSelected()) {
			theSymbol = defaultSymbolPrev.getSymbol();
			if (theSymbol != null) {
				String description = PluginServices.getText(this, "default");
				theSymbol.setDescription(description);
				ISymbol legendSymbol = null;
				if (auxLegend != null) {
					legendSymbol = auxLegend.getSymbolByValue(clave);
				}
				if (legendSymbol == null) {
					auxLegend.addSymbol(clave, theSymbol);
				} else {
					// if(!legendSymbol.equals(theSymbol)){
					if (legendSymbol != theSymbol) {
						auxLegend.replace(legendSymbol, theSymbol);
					}
				}
			}
		} else {
			if (auxLegend != null) {
				ISymbol legendSymbol = auxLegend.getSymbolByValue(clave);
				if (legendSymbol != null) {
					auxLegend.replace(legendSymbol, null);
				}
			}
		}
	}

	/**
	 * DOCUMENT ME!
	 */
	private void fillFieldNames() {
		SelectableDataSource rs;

		try {
			// rs = ((FLyrVect) layer).getSource().getRecordset();
			rs = ((FLyrVect) layer).getRecordset(); // Todos los campos, también
													// los de uniones
			logger.debug("rs.start()");
			rs.start();
			int fieldCount = rs.getFieldCount();
			String[] nomFields = new String[fieldCount];

			for (int i = 0; i < fieldCount; i++) {
				nomFields[i] = rs.getFieldAlias(i);
			}

			rs.stop();

			DefaultComboBoxModel cM = new DefaultComboBoxModel(nomFields);
			cmbFields.setModel(cM);

			// fieldsListValor.setSelectedIndex(0);
		} catch (ReadDriverException e) {
			NotificationManager.addError(
					PluginServices.getText(this, "recovering_recordset"), e);
		}
	}

	public void setData(FLayer layer, ILegend legend) {
		this.layer = (ClassifiableVectorial) layer;
		int shapeType = 0;
		try {
			shapeType = this.layer.getShapeType();
		} catch (ReadDriverException e) {
			NotificationManager.addError(
					PluginServices.getText(this, "accessing_to_the_layer"), e);
		}

		getDefaultSymbolPrev(shapeType);

		if (symbolTable != null)
			pnlCenter.remove(symbolTable);
		if (pnlMovBut != null)
			pnlCenter.remove(pnlMovBut);

		symbolTable = new SymbolTable(this, SymbolTable.VALUES_TYPE, shapeType);
		pnlCenter.add(symbolTable, BorderLayout.CENTER);
		pnlCenter.add(getPnlMovBut(), BorderLayout.EAST);

		fillFieldNames();

		symbolTable.removeAllItems();

		if (VectorialUniqueValueLegend.class.equals(legend.getClass())) {
			try {
				auxLegend = (VectorialUniqueValueLegend) legend.cloneLegend();
				// FIXME: parche
				ZSort legendZSort = ((VectorialUniqueValueLegend) legend)
						.getZSort();
				if (legendZSort != null) {
					ZSort auxZSort = new ZSort(auxLegend);
					auxZSort.copyLevels(legendZSort);
					auxZSort.setUsingZSort(legendZSort.isUsingZSort());
					auxLegend.setZSort(auxZSort);
				}
				// Fin del parche
			} catch (XMLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getChbUseDefault().setSelected(auxLegend.isUseDefaultSymbol());
			cmbFields.getModel().setSelectedItem(
					auxLegend.getClassifyingFieldNames()[0]);
			setColorScheme();
			symbolTable.fillTableFromSymbolList(auxLegend.getSymbols(),
					auxLegend.getValues(), auxLegend.getDescriptions());
			chbUseDefault.setSelected(auxLegend.isUseDefaultSymbol());
		} else {
			auxLegend = new VectorialUniqueValueLegend(shapeType);
		}
		defaultSymbolPrev.setSymbol(auxLegend.getDefaultSymbol());
		btnOpenSymbolLevelsEditor.setEnabled(symbolTable != null
				&& symbolTable.getRowCount() > 0);
	}

	private JPanel getPnlMovBut() {
		if (pnlMovBut == null) {
			pnlMovBut = new JPanel();
			pnlMovBut.setLayout(new BoxLayout(pnlMovBut, BoxLayout.Y_AXIS));
			pnlMovBut.add(new JBlank(1, 70));
			pnlMovBut.add(moveUp = new JButton(PluginServices.getIconTheme()
					.get("up-arrow")));
			moveUp.setSize(new Dimension(15, 15));
			pnlMovBut.add(new JBlank(1, 10));
			pnlMovBut.add(moveDown = new JButton(PluginServices.getIconTheme()
					.get("down-arrow")));
			pnlMovBut.add(new JBlank(1, 70));
			moveDown.setActionCommand("MOVE-DOWN");
			moveUp.setActionCommand("MOVE-UP");
			moveDown.addActionListener(this);
			moveUp.addActionListener(this);
		}
		return pnlMovBut;
	}

	private void setColorScheme() {

		if (auxLegend.getColorScheme() != null) {
			ColorItem[] colors = new ColorItem[auxLegend.getColorScheme().length];
			for (int i = 0; i < auxLegend.getColorScheme().length; i++) {
				colors[i] = new ColorItem();
				colors[i].setColor(auxLegend.getColorScheme()[i]);
			}
			cmbColorScheme.setSelectedColors(colors);
		}
	}

	private void getDefaultSymbolPrev(int shapeType) {
		if (defaultSymbolPrev == null) {
			defaultSymbolPrev = new JSymbolPreviewButton(shapeType);
			defaultSymbolPrev.setPreferredSize(new Dimension(110, 20));
			defaultSymbolPrev.addActionListener(this);
			defaultSymbolPanel.add(defaultSymbolPrev, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.gui.legendmanager.panels.ILegendPanel#getLegend()
	 */
	public ILegend getLegend() {
		fillSymbolListFromTable();

		if (auxLegend != null) {
			// your settings that are not the set of symbols must be located
			// here
			// auxLegend.setClassifyingFieldNames(
			// new String[] {(String) cmbFields.getSelectedItem()});

			ISymbol defaultSymbolLegend = auxLegend.getDefaultSymbol();
			ISymbol symbol = defaultSymbolPrev.getSymbol();
			if (symbol != null) {
				if (symbol != defaultSymbolLegend) {
					auxLegend.setDefaultSymbol(symbol);
				}
			}
			auxLegend.useDefaultSymbol(chbUseDefault.isSelected());

			try {
				theLegend = (VectorialUniqueValueLegend) auxLegend
						.cloneLegend();
			} catch (XMLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// FIXME: parche
			ZSort auxZSort = ((VectorialUniqueValueLegend) auxLegend)
					.getZSort();
			if (auxZSort != null) {
				ZSort legendZSort = new ZSort(theLegend);
				legendZSort.copyLevels(auxZSort);
				legendZSort.setUsingZSort(auxZSort.isUsingZSort());
				theLegend.setZSort(legendZSort);
			}
			// Fin del parche
		}

		return theLegend;
	}

	private JCheckBox getChbUseDefault() {
		if (chbUseDefault == null) {
			chbUseDefault = new JCheckBox();
			chbUseDefault.setSelected(false);
			chbUseDefault
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							if (chbUseDefault.isSelected()) {
								auxLegend.useDefaultSymbol(true);
							} else {
								auxLegend.useDefaultSymbol(false);
							}
						}
					});
			chbUseDefault.setText(PluginServices.getText(this, "resto_valores")
					+ ": ");
		}

		return chbUseDefault;
	}

	public void actionPerformed(ActionEvent e) {
		int[] indices = null;

		if (e.getActionCommand() == "MOVE-UP"
				|| e.getActionCommand() == "MOVE-DOWN") {
			if (!auxLegend.isOwnOrder()) {
				auxLegend.setOwnOrder(true);
			}
			indices = symbolTable.getSelectedRows();
		}

		if (e.getActionCommand() == "MOVE-UP") {
			if (indices.length > 0) {
				int classIndex = indices[0];
				int targetPos = Math.max(0, classIndex - 1);
				symbolTable.moveUpRows(classIndex, targetPos, indices.length);
			}
		}

		if (e.getActionCommand() == "MOVE-DOWN") {
			if (indices.length > 0) {
				int classIndex = indices[indices.length - 1];
				int targetPos = Math.min(symbolTable.getRowCount() - 1,
						classIndex + 1);
				symbolTable.moveDownRows(classIndex, targetPos, indices.length);
			}
		}

		if (e.getActionCommand() == "MOVE-UP"
				|| e.getActionCommand() == "MOVE-DOWN") {
			ArrayList orders = new ArrayList();

			for (int i = 0; i < symbolTable.getRowCount(); i++) {
				orders.add(symbolTable.getFieldValue(i, 1).toString());
			}
			auxLegend.setOrders(orders);

		}

		// modificar el combobox de valor
		if (e.getActionCommand() == "FIELD_SELECTED") {
			JComboBox cb = (JComboBox) e.getSource();
			String fieldName = (String) cb.getSelectedItem();
			symbolTable.removeAllItems();
			btnOpenSymbolLevelsEditor.setEnabled(false);
		}

		// add all elements by value
		if (e.getActionCommand() == "ADD_ALL_VALUES") {
			fillTableValues();
			btnOpenSymbolLevelsEditor.setEnabled(symbolTable != null
					&& symbolTable.getRowCount() > 0);
		}

		// add only one value
		if (e.getActionCommand() == "ADD_VALUE") {
			try {
				ISymbol symbol = SymbologyFactory
						.createDefaultSymbolByShapeType(layer.getShapeType());
				Value clave = ValueFactory.createValue(0.0);
				symbolTable.addTableRecord(symbol, clave, "0 - 0");
				btnOpenSymbolLevelsEditor.setEnabled(true);
			} catch (ReadDriverException ex) {
				NotificationManager.addError(
						PluginServices.getText(this, "getting_shape_type"), ex);
			}
		}

		// Vacia la tabla
		if (e.getActionCommand() == "REMOVE_ALL") {
			symbolTable.removeAllItems();
			auxLegend.setZSort(null);
			btnOpenSymbolLevelsEditor.setEnabled(false);
		}

		// Quitar solo el elemento seleccionado
		if (e.getActionCommand() == "REMOVE") {
			symbolTable.removeSelectedRows();
			btnOpenSymbolLevelsEditor.setEnabled(symbolTable.getRowCount() > 0);
		}

		if (e.getActionCommand() == "OPEN_SYMBOL_LEVEL_EDITOR") {
			ZSort myZSort = null;
			if (auxLegend != null) {
				myZSort = ((AbstractClassifiedVectorLegend) getLegend())
						.getZSort();
				if (myZSort == null) {
					myZSort = new ZSort(auxLegend);
				}
			}
			if (myZSort == null && theLegend != null) {
				myZSort = new ZSort(theLegend);
			}
			SymbolLevelsWindow sl = new SymbolLevelsWindow(myZSort);
			PluginServices.getMDIManager().addWindow(sl);
			auxLegend.setZSort(sl.getZSort());
		}
	}

	public String getDescription() {
		return PluginServices.getText(this, "Dado_un_campo_de_atributos")
				+ ", "
				+ PluginServices
						.getText(this,
								"muestra_los_elementos_de_la_capa_usando_un_simbolo_por_cada_valor_unico")
						.toLowerCase() + ".";
	}

	public ImageIcon getIcon() {
		return new ImageIcon(this.getClass().getClassLoader()
				.getResource("images/ValoresUnicos.png"));
	}

	public Class getParentClass() {
		return Categories.class;
	}

	public String getTitle() {
		return PluginServices.getText(this, "Valores_unicos");
	}

	public JPanel getPanel() {
		return this;
	}

	public Class getLegendClass() {
		return VectorialUniqueValueLegend.class;
	}

	public boolean isSuitableFor(FLayer layer) {
		return (layer instanceof FLyrVect);
	}

}
