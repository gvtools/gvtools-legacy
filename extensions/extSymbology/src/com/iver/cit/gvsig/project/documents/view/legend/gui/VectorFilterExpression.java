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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JBlank;
import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.symbology.fmap.rendering.VectorFilterExpressionLegend;
import org.gvsig.symbology.fmap.symbols.PictureFillSymbol;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.styles.IMarkerFillPropertiesStyle;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.layers.layerOperations.ClassifiableVectorial;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.NullIntervalValue;

/**
 * Implements the JPanel that shows the properties of a
 * VectorialFilterExpressionLegend in order to allows the user to modify its
 * characteristics
 * 
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 * 
 */
public class VectorFilterExpression extends JPanel implements ILegendPanel,
		ActionListener {
	private static final long serialVersionUID = -7187473609965942511L;
	private VectorFilterExpressionLegend theLegend;
	VectorFilterExpressionLegend auxLegend;
	private ClassifiableVectorial layer;
	private PictureFillSymbol previewSymbol;
	private JPanel pnlCenter;
	private JPanel pnlMovBut;
	private SymbolTable symbolTable;
	private JButton btnAddExpression;
	private JButton btnModExpression;
	private JButton btnRemoveExpression;
	private JButton moveUp;
	private JButton moveDown;
	private int shapeType;
	protected JCheckBox chkdefaultvalues = null;
	protected JSymbolPreviewButton defaultSymbolPrev;
	private GridBagLayoutPanel defaultSymbolPanel = new GridBagLayoutPanel();

	/**
	 * This is the default constructor
	 */
	public VectorFilterExpression() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {

		pnlCenter = new JPanel();
		pnlCenter.setLayout(new BorderLayout());

		JPanel pnlButtons = new JPanel();
		btnAddExpression = new JButton(PluginServices.getText(this,
				"new_filter_expression"));
		btnAddExpression.setActionCommand("NEW_EXPRESSION");
		btnAddExpression.addActionListener(this);
		pnlButtons.add(btnAddExpression);

		btnModExpression = new JButton(PluginServices.getText(this,
				"modify_filter_expression"));
		btnModExpression.setActionCommand("MODIFY_EXPRESSION");
		btnModExpression.addActionListener(this);
		pnlButtons.add(btnModExpression);

		btnRemoveExpression = new JButton(PluginServices.getText(this,
				"delete_filter_expression"));
		btnRemoveExpression.setActionCommand("REMOVE");
		btnRemoveExpression.addActionListener(this);
		pnlButtons.add(btnRemoveExpression);
		defaultSymbolPanel.add(getChkDefaultvalues());
		pnlCenter.add(defaultSymbolPanel, BorderLayout.NORTH);

		this.setLayout(new BorderLayout());
		this.add(pnlCenter, BorderLayout.CENTER);
		this.add(pnlButtons, BorderLayout.SOUTH);

	}

	public void getDefaultSymbolPrev(int shapeType) {
		if (defaultSymbolPrev == null) {
			defaultSymbolPrev = new JSymbolPreviewButton(shapeType);
			defaultSymbolPrev.setPreferredSize(new Dimension(110, 20));
			defaultSymbolPanel.add(defaultSymbolPrev, null);
		}
	}

	public String getDescription() {
		return PluginServices
				.getText(this,
						"shows_the_elements_of_the_layer_depending_on_the_value_of_a_filter_expression")
				+ ".";
	}

	public ISymbol getIconSymbol() {
		if (previewSymbol == null) {
			try {
				previewSymbol = new PictureFillSymbol();
				previewSymbol.setImage(new File(this.getClass()
						.getClassLoader()
						.getResource("images/ValoresUnicos.png").getFile())
						.toURL());
				previewSymbol.getMarkerFillProperties().setFillStyle(
						IMarkerFillPropertiesStyle.SINGLE_CENTERED_SYMBOL);
			} catch (IOException e) {
				return null;
			}
		}
		return previewSymbol;
	}

	public ILegend getLegend() {
		auxLegend.clear();
		fillSymbolListFromTable();

		try {
			theLegend = (VectorFilterExpressionLegend) auxLegend.cloneLegend();

		} catch (XMLException e) {
			e.printStackTrace();
		}
		if (defaultSymbolPrev.getSymbol() != null)
			theLegend.setDefaultSymbol(defaultSymbolPrev.getSymbol());

		theLegend.useDefaultSymbol(chkdefaultvalues.isSelected());
		return theLegend;
	}

	/**
	 * Fills the list of symbols of the legend
	 * 
	 */
	private void fillSymbolListFromTable() {
		Object clave;
		ISymbol theSymbol;
		boolean bRestoValores = false;
		int hasta;

		FLyrVect m = (FLyrVect) layer;
		try {

			if (auxLegend.getClassifyingFieldNames() != null) {
				String[] fNames = auxLegend.getClassifyingFieldNames();
				int[] fieldTypes = new int[auxLegend.getClassifyingFieldNames().length];

				for (int i = 0; i < auxLegend.getClassifyingFieldNames().length; i++) {
					int fieldIndex = m.getSource().getRecordset()
							.getFieldIndexByName(fNames[i]);
					fieldTypes[i] = m.getSource().getRecordset()
							.getFieldType(fieldIndex);
				}

				auxLegend.setClassifyingFieldTypes(fieldTypes);
			}
		} catch (ReadDriverException e) {
			NotificationManager.addError(
					PluginServices.getText(this, "could_not_setup_legend"), e);
		}

		auxLegend.useDefaultSymbol(chkdefaultvalues.isSelected());
		if (bRestoValores) {
			hasta = symbolTable.getRowCount() - 1;
		} else {
			hasta = symbolTable.getRowCount();
		}

		for (int row = 0; row < symbolTable.getRowCount(); row++) {
			clave = symbolTable.getFieldValue(row, 1);
			theSymbol = (ISymbol) symbolTable.getFieldValue(row, 0);
			theSymbol
					.setDescription((String) symbolTable.getFieldValue(row, 2));
			auxLegend.addSymbol(clave, theSymbol);
		}
		if (chkdefaultvalues.isSelected()) {
			if (defaultSymbolPrev.getSymbol() != null) {
				String description = PluginServices.getText(this, "default");
				defaultSymbolPrev.getSymbol().setDescription(description);
				auxLegend.addSymbol(new NullIntervalValue(),
						defaultSymbolPrev.getSymbol());
			}
		}

	}

	public Class getLegendClass() {
		return VectorFilterExpressionLegend.class;
	}

	public JPanel getPanel() {
		return this;
	}

	public Class getParentClass() {
		return Categories.class;
	}

	public String getTitle() {
		return PluginServices.getText(this, "expressions");
	}

	public boolean isSuitableFor(FLayer layer) {
		FLyrVect lVect = (FLyrVect) layer;
		try {
			return (lVect.getShapeType() % FShape.Z) != FShape.MULTI;
		} catch (ReadDriverException e) {
			return false;
		}

	}

	public void setData(FLayer lyr, ILegend legend) {
		this.layer = (ClassifiableVectorial) lyr;
		shapeType = 0;

		try {
			shapeType = this.layer.getShapeType();
		} catch (ReadDriverException e) {
			NotificationManager.addError(
					PluginServices.getText(this, "generating_intervals"), e);
		}

		if (symbolTable != null)
			pnlCenter.remove(symbolTable);
		if (pnlMovBut != null)
			pnlCenter.remove(pnlMovBut);

		getDefaultSymbolPrev(shapeType);

		symbolTable = new SymbolTable(this, "expressions", shapeType);
		pnlCenter.add(symbolTable, BorderLayout.CENTER);
		pnlCenter.add(getPnlMovBut(), BorderLayout.EAST);

		if (legend instanceof VectorFilterExpressionLegend) {
			try {
				auxLegend = (VectorFilterExpressionLegend) legend.cloneLegend();
			} catch (XMLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			symbolTable.fillTableFromSymbolList(auxLegend.getSymbols(),
					auxLegend.getValues(), auxLegend.getDescriptions());
		} else {
			auxLegend = new VectorFilterExpressionLegend();
			auxLegend.setShapeType(shapeType);
		}
		defaultSymbolPrev.setSymbol(auxLegend.getDefaultSymbol());
		getChkDefaultvalues().setSelected(auxLegend.isUseDefaultSymbol());
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

	public void actionPerformed(ActionEvent e) {
		int[] indices = null;

		if (e.getActionCommand() == "MOVE-UP"
				|| e.getActionCommand() == "MOVE-DOWN") {
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
		}
		if (e.getActionCommand() == "NEW_EXPRESSION") {
			ExpressionCreator newExpression = new ExpressionCreator(
					(FLyrVect) this.layer);
			PluginServices.getMDIManager().addWindow((IWindow) newExpression);
			String expression = ((ExpressionCreator) newExpression)
					.getExpression();
			if (newExpression.getFieldNamesExpression() != null)
				addClassFieldNames(newExpression.getFieldNamesExpression());

			if (expression != null)
				if (expression.compareTo("") != 0) {
					auxLegend.addSymbol(expression,
							newExpression.getSymbolForExpression());
					symbolTable.removeAllItems();
					symbolTable.fillTableFromSymbolList(auxLegend.getSymbols(),
							auxLegend.getValues(), auxLegend.getDescriptions());
				}
			repaint();
		} else if (e.getActionCommand() == "MODIFY_EXPRESSION") {

			if (symbolTable.getSelectedRowElements() == null) {
				JOptionPane.showMessageDialog(this,
						PluginServices.getText(this, "select_one_row") + ".\n");
			}

			else {
				ISymbol mySymbol = (ISymbol) symbolTable
						.getSelectedRowElements()[0];
				String expression = (String) symbolTable
						.getSelectedRowElements()[1];
				String myDesc = (String) symbolTable.getSelectedRowElements()[2];

				ExpressionCreator newExpression = new ExpressionCreator(
						(FLyrVect) this.layer);
				newExpression.setExpression(expression);
				newExpression.setDescriptionForExpression(myDesc);
				newExpression.setSymbolForExpression(mySymbol);
				PluginServices.getMDIManager().addWindow(
						(IWindow) newExpression);

				if (expression != null)
					if (expression.compareTo("") != 0
							&& newExpression.getExpression() != null) {
						auxLegend.delSymbol(mySymbol);
						auxLegend.addSymbol(((ExpressionCreator) newExpression)
								.getExpression(), newExpression
								.getSymbolForExpression());
						symbolTable.removeAllItems();
						symbolTable.fillTableFromSymbolList(
								auxLegend.getSymbols(), auxLegend.getValues(),
								auxLegend.getDescriptions());

						repaint();
					}
			}
		}

		else if (e.getActionCommand() == "REMOVE") {
			if (symbolTable.getSelectedRowElements() == null) {
				JOptionPane.showMessageDialog(this,
						PluginServices.getText(this, "select_one_row") + ".\n");
			} else {
				ISymbol mySymbol = (ISymbol) symbolTable
						.getSelectedRowElements()[0];
				auxLegend.delSymbol(mySymbol);
				symbolTable.removeAllItems();
				symbolTable.fillTableFromSymbolList(auxLegend.getSymbols(),
						auxLegend.getValues(), auxLegend.getDescriptions());

				repaint();
			}
		}
	}

	/**
	 * Adds new classifying field names to the legend when a new expression is
	 * created or an existing one is modified
	 * 
	 * @param fieldNamesExpression
	 */
	private void addClassFieldNames(Object[] fieldNamesExpression) {
		boolean appears = false;
		ArrayList<String> myFieldNames = new ArrayList<String>();

		if (auxLegend.getClassifyingFieldNames() != null) {

			for (int i = 0; i < auxLegend.getClassifyingFieldNames().length; i++) {
				myFieldNames.add(auxLegend.getClassifyingFieldNames()[i]);
			}
			for (int i = 0; i < fieldNamesExpression.length; i++) {
				appears = false;
				for (int j = 0; j < auxLegend.getClassifyingFieldNames().length; j++) {
					if (auxLegend.getClassifyingFieldNames()[j]
							.compareTo((String) fieldNamesExpression[i]) == 0)
						appears = true;
				}
				if (!appears) {
					myFieldNames.add((String) fieldNamesExpression[i]);
				}
			}

			auxLegend.setClassifyingFieldNames((String[]) myFieldNames
					.toArray(new String[myFieldNames.size()]));
		}

		else {
			for (int i = 0; i < fieldNamesExpression.length; i++) {
				myFieldNames.add((String) fieldNamesExpression[i]);
			}
			auxLegend.setClassifyingFieldNames((String[]) myFieldNames
					.toArray(new String[myFieldNames.size()]));
		}
	}

	public ImageIcon getIcon() {
		return new ImageIcon(this.getClass().getClassLoader()
				.getResource("images/FilterExpressions.png"));
	}

	/**
	 * This method initializes chkdefaultvalues
	 * 
	 * @return javax.swing.JCheckBox
	 */
	protected JCheckBox getChkDefaultvalues() {
		if (chkdefaultvalues == null) {
			chkdefaultvalues = new JCheckBox();
			chkdefaultvalues.setText(PluginServices.getText(this,
					"resto_valores") + ": ");
			chkdefaultvalues
					.setBounds(new java.awt.Rectangle(342, 26, 141, 20));
			chkdefaultvalues.setSelected(false);
			chkdefaultvalues
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							if (chkdefaultvalues.isSelected()) {
								auxLegend.useDefaultSymbol(true);
							} else {
								auxLegend.useDefaultSymbol(false);
							}
						}
					});
		}

		return chkdefaultvalues;
	}

}
