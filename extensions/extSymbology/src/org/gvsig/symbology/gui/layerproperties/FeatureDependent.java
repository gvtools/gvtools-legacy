/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
package org.gvsig.symbology.gui.layerproperties;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JBlank;
import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.gui.beans.swing.celleditors.BooleanTableCellEditor;
import org.gvsig.gui.beans.swing.cellrenderers.BooleanTableCellRenderer;
import org.gvsig.symbology.fmap.labeling.FeatureDependentLabeled;
import org.gvsig.symbology.fmap.labeling.parse.LabelExpressionParser;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingMethod;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelClass;

public class FeatureDependent extends AbstractLabelingMethodPanel implements
		ActionListener {
	private static final long serialVersionUID = 5493451803343695650L;
	private static int NAME_FIELD_INDEX = 0;
	private static int PREVIEW_FIELD_INDEX = 1;
	private static int FILTER_FIELD_INDEX = 2;
	private static int LABEL_EXPRESSION_FIELD_INDEX = 3;
	private static int VISIBLE_FIELD_INDEX = 4;
	JTable tblClasses = null;
	private JButton btnMoveUpClass;
	private JButton btnAddClass;
	private JCheckBox chkLabel;
	private JCheckBox chkDefinePriorities;
	private JScrollPane scrlPan;
	private boolean openEditor = false;
	private JButton btnMoveDownClass;
	private JButton btnDelClass;
	private String[] fieldNames;
	private int[] fieldTypes;
	private GridBagLayoutPanel buttonPanel;

	@Override
	public String getName() {
		return PluginServices.getText(this,
				"define_classes_of_features_and_label_each_differently") + ".";
	}

	@Override
	public Class<? extends ILabelingMethod> getLabelingMethodClass() {
		return FeatureDependentLabeled.class;
	}

	private JCheckBox getChkDefinePriorities() {
		if (chkDefinePriorities == null) {
			chkDefinePriorities = new JCheckBox(PluginServices.getText(this,
					"label_priority"));
			chkDefinePriorities.addActionListener(this);
			chkDefinePriorities.setName("CHK_DEFINE_PRIORITIES");
		}
		return chkDefinePriorities;
	}

	private JButton getBtnDelClass() {
		if (btnDelClass == null) {
			btnDelClass = new JButton(PluginServices.getText(this, "delete"));
			btnDelClass.setName("BTNDELCLASS");
			btnDelClass.addActionListener(this);
		}
		return btnDelClass;
	}

	private JButton getBtnAddClass() {
		if (btnAddClass == null) {
			btnAddClass = new JButton(PluginServices.getText(this, "add"));
			btnAddClass.setName("BTNADDCLASS");
			btnAddClass.addActionListener(this);
		}
		return btnAddClass;
	}

	private JButton getBtnMoveUpClass() {
		if (btnMoveUpClass == null) {
			btnMoveUpClass = new JButton(
					PluginServices.getText(this, "move_up"));
			btnMoveUpClass.setName("BTNMOVEUPCLASS");
			btnMoveUpClass.addActionListener(this);
		}
		return btnMoveUpClass;
	}

	private JButton getBtnMoveDownClass() {
		if (btnMoveDownClass == null) {
			btnMoveDownClass = new JButton(PluginServices.getText(this,
					"move_down"));
			btnMoveDownClass.setName("BTNMOVEDOWNCLASS");
			btnMoveDownClass.addActionListener(this);
		}
		return btnMoveDownClass;
	}

	private JScrollPane getCenterScrl() {

		scrlPan = new JScrollPane(getTblClasses());
		scrlPan.setPreferredSize(new Dimension(180, 300));

		return scrlPan;
	}

	private JTable getTblClasses() {

		tblClasses = new JTable(new LabelClassTableModel());
		tblClasses.setRowHeight(50);
		tblClasses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblClasses.addMouseListener(new MouseAdapter() {
			int prevRow = -1;

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!tblClasses.isEnabled()) {
					return;
				}
				int col = tblClasses.getColumnModel().getColumnIndexAtX(
						e.getX());
				int row = (int) ((e.getY() - tblClasses.getLocation().getY()) / tblClasses
						.getRowHeight());
				if (!(row > tblClasses.getRowCount() - 1 || col > tblClasses
						.getColumnCount() - 1)) {
					openEditor = (row == prevRow
							&& e.getButton() == MouseEvent.BUTTON1 && e
							.getClickCount() == 2);
					prevRow = row;
					if (openEditor)
						tblClasses.editCellAt(row, col);
				}
			}
		});

		tblClasses.getModel().addTableModelListener(new TableModelListener() {

			public void tableChanged(TableModelEvent e) {
				if (!tblClasses.isEnabled()) {
					return;
				}

				if (e.getColumn() == VISIBLE_FIELD_INDEX) {
					System.err.println("ahora toca cambiar la visibilidad");
					LabelClass oldLc = (LabelClass) tblClasses.getValueAt(
							e.getFirstRow(), PREVIEW_FIELD_INDEX);
					oldLc.setVisible(Boolean.valueOf(tblClasses.getValueAt(
							e.getFirstRow(), VISIBLE_FIELD_INDEX).toString()));
				}
			}

		});

		TableColumnModel cm = tblClasses.getColumnModel();

		tblClasses.getColumnModel().getColumn(PREVIEW_FIELD_INDEX)
				.setCellRenderer(new TableCellRenderer() {

					public Component getTableCellRendererComponent(
							JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						LabelClassPreview lcPr = new LabelClassPreview();
						lcPr.setLabelClass((LabelClass) value);
						return lcPr;
					}

				});
		tblClasses.getColumnModel().getColumn(VISIBLE_FIELD_INDEX)
				.setCellRenderer(new BooleanTableCellRenderer(false));
		tblClasses.getColumnModel().getColumn(LABEL_EXPRESSION_FIELD_INDEX)
				.setCellRenderer(new TableCellRenderer() {
					public Component getTableCellRendererComponent(
							JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						String expr = null;
						if (value != null)
							expr = (String) value;
						if (expr == null)
							expr = LabelExpressionParser
									.tokenFor(LabelExpressionParser.EOEXPR);

						// expr =
						// expr.replaceAll(LabelExpressionParser.tokenFor(LabelExpressionParser.EOFIELD),
						// " | ");
						expr = expr.substring(0, expr.length() - 1);
						return new JLabel("<html><p>" + expr + "</p></html>",
								JLabel.CENTER);
					}
				});

		// the editors

		for (int i = 0; i < tblClasses.getColumnModel().getColumnCount(); i++) {
			if (i != VISIBLE_FIELD_INDEX) {
				tblClasses.getColumnModel().getColumn(i)
						.setCellEditor(new LabelClassCellEditor());
			} else {
				tblClasses.getColumnModel().getColumn(VISIBLE_FIELD_INDEX)
						.setCellEditor(new BooleanTableCellEditor(tblClasses));
			}
		}
		((DefaultTableModel) tblClasses.getModel()).fireTableDataChanged();
		repaint();

		return tblClasses;
	}

	private class LabelClassCellEditor extends AbstractCellEditor implements
			TableCellEditor {
		private static final long serialVersionUID = 6399823783851437400L;

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			if (openEditor) {
				LabelClass oldLc = (LabelClass) tblClasses.getValueAt(row,
						PREVIEW_FIELD_INDEX);
				oldLc.setVisible(Boolean.valueOf(tblClasses.getValueAt(row,
						VISIBLE_FIELD_INDEX).toString()));
				LabelClassProperties lcProp = new LabelClassProperties(
						fieldNames, fieldTypes);
				oldLc.setTexts(new String[] { oldLc.getName() });
				lcProp.setLabelClass(oldLc);
				PluginServices.getMDIManager().addWindow(lcProp);
				LabelClass newLc = lcProp.getLabelClass();

				LabelClassTableModel m = (LabelClassTableModel) tblClasses
						.getModel();
				Boolean changeDone = false;

				if (!(oldLc.getName().equals(newLc.getName())))
					if (!checSameLablClassName(m, newLc.getName())) {

						m.setValueAt(newLc.getStringLabelExpression(), row,
								LABEL_EXPRESSION_FIELD_INDEX);
						m.setValueAt(newLc.getName(), row, NAME_FIELD_INDEX);
						m.setValueAt(newLc, row, PREVIEW_FIELD_INDEX);
						m.setValueAt(newLc.getSQLQuery(), row,
								FILTER_FIELD_INDEX);
						m.setValueAt(newLc.isVisible(), row,
								VISIBLE_FIELD_INDEX);
						fireEditingStopped(); // Make the renderer reappear.
						changeDone = true;
					} else {
						JOptionPane
								.showMessageDialog(
										tblClasses,
										PluginServices
												.getText(this,
														"cannot_exist_two_label_classes_with_the_same_name")
												+ "\n", PluginServices.getText(
												this, "error"),
										JOptionPane.ERROR_MESSAGE);
						changeDone = true;
					}
				if (!changeDone) {
					m.setValueAt(newLc.getStringLabelExpression(), row,
							LABEL_EXPRESSION_FIELD_INDEX);
					m.setValueAt(newLc.getName(), row, NAME_FIELD_INDEX);
					m.setValueAt(newLc, row, PREVIEW_FIELD_INDEX);
					m.setValueAt(newLc.getSQLQuery(), row, FILTER_FIELD_INDEX);
					m.setValueAt(newLc.isVisible(), row, VISIBLE_FIELD_INDEX);
					fireEditingStopped(); // Make the renderer reappear.
					changeDone = true;
				}
			}

			method.clearAllClasses();
			LabelClass[] classes = ((LabelClassTableModel) tblClasses
					.getModel()).toLabelClassArray();
			for (int i = 0; i < classes.length; i++) {
				method.addLabelClass(classes[i]);
			}

			openEditor = false;
			return null;
		}

		public Object getCellEditorValue() {
			return null;
		}

	}

	private boolean checSameLablClassName(LabelClassTableModel mod, String name) {
		for (int i = 0; i < mod.getRowCount(); i++) {
			if (name.equals(mod.getLabelAtRow(i).getName()))
				return true;
		}
		return false;
	}

	private class LabelClassTableModel extends DefaultTableModel {
		private static final long serialVersionUID = -9152998982339430209L;
		Object[][] values;

		private String[] classesTableFieldNames = new String[] {
				PluginServices.getText(this, "name"),
				PluginServices.getText(this, "preview"),
				PluginServices.getText(this, "filter"),
				PluginServices.getText(this, "label_expression"),
				PluginServices.getText(this, "visible"), };

		public LabelClassTableModel() {
			super();
			LabelClass[] labelClasses = method.getLabelClasses();

			values = new Object[labelClasses.length][classesTableFieldNames.length];
			for (int i = 0; i < values.length; i++) {
				values[i][PREVIEW_FIELD_INDEX] = labelClasses[i];
				values[i][NAME_FIELD_INDEX] = labelClasses[i].getName();
				values[i][FILTER_FIELD_INDEX] = labelClasses[i].getSQLQuery();
				values[i][LABEL_EXPRESSION_FIELD_INDEX] = labelClasses[i]
						.getStringLabelExpression();
				values[i][VISIBLE_FIELD_INDEX] = labelClasses[i].isVisible();
			}
			setDataVector(values, classesTableFieldNames);
		}

		public String getColumnName(int col) {
			return classesTableFieldNames[col];
		}

		public int getColumnCount() {
			return classesTableFieldNames.length;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return true;
		}

		public LabelClass getLabelAtRow(int row) {
			return (LabelClass) getValueAt(row, PREVIEW_FIELD_INDEX);
		}

		public LabelClass[] toLabelClassArray() {
			LabelClass[] classes = new LabelClass[getRowCount()];
			for (int i = 0; i < classes.length; i++) {
				classes[i] = getLabelAtRow(i);
				if (getChkDefinePriorities().isSelected()) {
					classes[i].setPriority(i);
				}
			}
			return classes;
		}

		public void setClassArray(LabelClass[] classes) {
			for (int i = 0; i < classes.length; i++) {
				setValueAt(classes[i], i, PREVIEW_FIELD_INDEX);
				setValueAt(classes[i].getName(), i, NAME_FIELD_INDEX);
				setValueAt(classes[i].getSQLQuery(), i, FILTER_FIELD_INDEX);
				setValueAt(classes[i].getStringLabelExpression(), i,
						LABEL_EXPRESSION_FIELD_INDEX);
				setValueAt(classes[i].isVisible(), i, VISIBLE_FIELD_INDEX);
			}

		}

	}

	@Override
	protected void initializePanel() {

		setLayout(new BorderLayout());
		JPanel panel = new JPanel(new BorderLayout());
		buttonPanel = new GridBagLayoutPanel();
		buttonPanel.addComponent(getBtnAddClass());
		buttonPanel.addComponent(getBtnDelClass());
		buttonPanel.addComponent(new JBlank(10, 10));
		buttonPanel.addComponent(getBtnMoveUpClass());
		buttonPanel.addComponent(getBtnMoveDownClass());
		panel.add(buttonPanel, BorderLayout.EAST);
		panel.add(getChkDefinePriorities(), BorderLayout.NORTH);
		panel.add(getCenterScrl(), BorderLayout.CENTER);
		add(panel, BorderLayout.CENTER);
	}

	@Override
	public void fillPanel(ILabelingMethod method,
			SelectableDataSource dataSource) throws ReadDriverException {
		fieldNames = dataSource.getFieldNames();
		fieldTypes = new int[fieldNames.length];
		for (int i = 0; i < fieldTypes.length; i++) {
			fieldTypes[i] = dataSource.getFieldType(i);
		}
		if (method == null) {
			this.method = new FeatureDependentLabeled();
		}
		chkDefinePriorities.setSelected(this.method.definesPriorities());
		repaint();
	}

	private void swapClass(int classIndex, int targetPos, int numOfElements) {

		LabelClass[] classes = ((LabelClassTableModel) tblClasses.getModel())
				.toLabelClassArray();
		LabelClass aux = classes[targetPos];
		classes[targetPos] = classes[classIndex];
		classes[classIndex] = aux;
		((LabelClassTableModel) tblClasses.getModel()).setClassArray(classes);
	}

	public void actionPerformed(ActionEvent e) {
		if (method == null)
			return;

		JComponent c = (JComponent) e.getSource();
		LabelClassTableModel mod = ((LabelClassTableModel) tblClasses
				.getModel());

		if (c.equals(btnAddClass)) {

			LabelClass newClass = new LabelClass();
			String name = PluginServices.getText(this, "labeling")
					+ String.valueOf(1);

			int count = 0;
			while (checSameLablClassName(mod, name)) {
				count++;
				name = PluginServices.getText(this, "labeling")
						+ String.valueOf(count);
			}
			newClass.setName(name);
			mod.addRow(new Object[] { newClass.getName(), newClass,
					newClass.getSQLQuery(),
					newClass.getStringLabelExpression(), newClass.isVisible() });
		} else if (c.equals(btnDelClass)) {
			if (mod.getRowCount() >= 1) {
				int[] sRows = tblClasses.getSelectedRows();

				for (int i = sRows.length - 1; i >= 0; i--) {
					mod.removeRow(sRows[i]);
				}
			}
		} else if (c.equals(chkDefinePriorities)) {

			method.setDefinesPriorities(chkDefinePriorities.isSelected());

		} else if (c.equals(chkLabel)) {
			int[] sRows = tblClasses.getSelectedRows();
			for (int i = sRows.length - 1; i >= 0; i--) {
				LabelClass lc = mod.getLabelAtRow(i);
				lc.setVisible(chkLabel.isSelected());
			}

		} else if (c.equals(btnMoveUpClass)) {
			int[] indices = tblClasses.getSelectedRows();
			if (indices.length > 0) {
				int classIndex = indices[0];
				int targetPos = Math.max(0, classIndex - 1);
				swapClass(classIndex, targetPos, indices.length);
			}

		} else if (c.equals(btnMoveDownClass)) {
			int[] indices = tblClasses.getSelectedRows();
			if (indices.length > 0) {
				int classIndex = indices[indices.length - 1];
				int targetPos = Math.min(tblClasses.getRowCount() - 1,
						classIndex + 1);
				swapClass(classIndex, targetPos, indices.length);
			}

		} else if (c.equals(btnDelClass)) {
			int[] indices = tblClasses.getSelectedRows();
			if (indices.length > 0) {
				int classIndex = indices[0];
				int targetPos = Math.min(tblClasses.getRowCount(), classIndex);
				swapClass(classIndex, targetPos, indices.length);
			}
		}

		mod.fireTableDataChanged();
		method.clearAllClasses();
		LabelClass[] classes = ((LabelClassTableModel) tblClasses.getModel())
				.toLabelClassArray();
		for (int i = 0; i < classes.length; i++) {
			method.addLabelClass(classes[i]);
		}
		repaint();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub

	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		getBtnAddClass().setEnabled(enabled);
		getBtnDelClass().setEnabled(enabled);
		getBtnMoveDownClass().setEnabled(enabled);
		getBtnMoveUpClass().setEnabled(enabled);
		getChkDefinePriorities().setEnabled(enabled);
		scrlPan.setEnabled(enabled);
		tblClasses.setEnabled(enabled);
	}

}
