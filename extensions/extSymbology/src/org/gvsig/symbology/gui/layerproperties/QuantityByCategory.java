package org.gvsig.symbology.gui.layerproperties;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Types;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.symbology.fmap.rendering.GraduatedSymbolLegend;
import org.gvsig.symbology.fmap.rendering.QuantityByCategoryLegend;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.IFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.fmap.rendering.IInterval;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.NullIntervalValue;
import com.iver.cit.gvsig.fmap.rendering.VectorialIntervalLegend;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ILegendPanel;
import com.iver.cit.gvsig.project.documents.view.legend.gui.MultipleAttributes;
import com.iver.cit.gvsig.project.documents.view.legend.gui.SymbolTable;
import com.iver.cit.gvsig.project.documents.view.legend.gui.VectorialInterval;
import com.iver.utiles.swing.JComboBox;

/**
 * Implements the panel of a legend where the user can compare two different
 * characteristics of a region in the map. These two "fields" will be compared,
 * on one side, using a color for the region and , on the other side, using a
 * graduated symbol. Both methods will change (the color or the size of the
 * symbol) depending on the value of the fields.
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class QuantityByCategory extends JPanel implements ILegendPanel,
		ActionListener {
	private static final long serialVersionUID = 5098346573350040756L;
	private JPanel pnlNorth;
	private JPanel pnlSouth;
	private JPanel pnlButtons;
	private GridBagLayoutPanel pnlFields;
	private JPanel pnlColorAndSymbol;
	private JComboBox cmbColorField;
	private JComboBox cmbGraduatedSymbolField;
	private JButton btnColor;
	private JButton btnSymbol;
	// private JButton btnDelete;
	// private JButton btnDeleteAll;
	private QuantityByCategoryLegend legend;
	private QuantityByCategoryLegend oldLegend;
	private FLayer layer;
	private SymbolTable symbolTable;

	/**
	 * Constructor method
	 */
	public QuantityByCategory() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(490, 300);
		this.add(getPnlNorth(), BorderLayout.NORTH);
		this.add(getPnlSouth(), BorderLayout.CENTER);
		// this.add(getPnlButtons(),BorderLayout.SOUTH);
	}

	/**
	 * Obtains the south panel
	 * 
	 * @return JPanel
	 */
	private JPanel getPnlSouth() {
		if (pnlSouth == null) {
			pnlSouth = new JPanel();
			pnlSouth.setLayout(new BorderLayout());
		}
		return pnlSouth;
	}

	/**
	 * Obtains the panel where the buttons will be placed
	 * 
	 * @return JPanel
	 */
	// private JPanel getPnlButtons() {
	// if(pnlButtons == null) {
	// pnlButtons = new JPanel();
	// pnlButtons.add(getButDel());
	// pnlButtons.add(getButDelAll());
	// }
	// return pnlButtons;
	// }
	/**
	 * Obtains the button that is used to delete all the rows of the symbolTable
	 * 
	 * @return JButton
	 */
	// private JButton getButDelAll() {
	// if (btnDeleteAll == null) {
	// btnDeleteAll = new JButton();
	// btnDeleteAll.addActionListener(this);
	// btnDeleteAll.setText(PluginServices.getText(this, "Quitar_todos"));
	// }
	// return btnDeleteAll;
	// }
	/**
	 * Obtains the button that is used to delete a row of the symbolTable
	 * 
	 * @return JButton
	 */
	// private JButton getButDel() {
	// if (btnDelete == null) {
	// btnDelete = new JButton();
	// btnDelete.addActionListener(this);
	// btnDelete.setText(PluginServices.getText(this, "Quitar"));
	// }
	//
	// return btnDelete;
	// }
	/**
	 * Obtains the north panel
	 * 
	 * @return JPanel
	 */
	private JPanel getPnlNorth() {
		if (pnlNorth == null) {
			pnlNorth = new JPanel(new GridLayout(1, 2));
			pnlNorth.add(getPnlFields());
			pnlNorth.add(getPnlColorAndSymbol());
		}

		return pnlNorth;
	}

	/**
	 * Obtains the panel where the user has the options to select the variation
	 * to be applied
	 * 
	 * @return JPanel
	 */
	private JPanel getPnlColorAndSymbol() {
		if (pnlColorAndSymbol == null) {
			pnlColorAndSymbol = new JPanel();
			pnlColorAndSymbol.setBorder(BorderFactory.createTitledBorder(null,
					PluginServices.getText(this, "variation_by")));
			pnlColorAndSymbol.add(getBtnColor());
			pnlColorAndSymbol.add(getBtnSymbol());
		}

		return pnlColorAndSymbol;
	}

	/**
	 * Creates the button which is used to selecte the variation by symbol
	 * 
	 * @return JButton
	 */
	private JButton getBtnSymbol() {
		if (btnSymbol == null) {
			btnSymbol = new JButton(PluginServices.getText(this, "symbol"));
			btnSymbol.addActionListener(this);
		}
		return btnSymbol;
	}

	/**
	 * Creates the button which is used to selecte the variation by color ramp
	 * 
	 * @return JButton
	 */
	private JButton getBtnColor() {
		if (btnColor == null) {
			btnColor = new JButton(PluginServices.getText(this, "color_ramp"));
			btnColor.addActionListener(this);
		}

		return btnColor;
	}

	/**
	 * Creates the panel where the JComboBoxes to select the fields for the
	 * variation will be placed
	 * 
	 * @return GridBagLayoutPanel
	 */
	private GridBagLayoutPanel getPnlFields() {
		if (pnlFields == null) {
			pnlFields = new GridBagLayoutPanel();
			pnlFields.setBorder(BorderFactory.createTitledBorder(null,
					PluginServices.getText(this, "value_fields")));
			pnlFields.addComponent(PluginServices.getText(this, "color_field"),
					getCmbColorField());
			pnlFields.addComponent(
					PluginServices.getText(this, "symbol_field"),
					getCmbGraduatedField());
		}

		return pnlFields;
	}

	/**
	 * Creates a JComboBox where the user will select the field for the symbol
	 * variation
	 * 
	 * @return JComboBox
	 */
	private JComboBox getCmbGraduatedField() {
		if (cmbGraduatedSymbolField == null) {
			cmbGraduatedSymbolField = new JComboBox();
			cmbGraduatedSymbolField.addActionListener(this);
		}
		return cmbGraduatedSymbolField;
	}

	/**
	 * Creates a JComboBox where the user will select the field for the color
	 * ramp variation
	 * 
	 * @return JComboBox
	 */
	private JComboBox getCmbColorField() {
		if (cmbColorField == null) {
			cmbColorField = new JComboBox();
			cmbColorField.addActionListener(this);
		}
		return cmbColorField;
	}

	public void setData(FLayer lyr, ILegend legend) {
		this.layer = lyr;
		this.oldLegend = null;

		if (symbolTable != null)
			pnlSouth.remove(symbolTable);

		symbolTable = new SymbolTable(this, SymbolTable.INTERVALS_TYPE,
				FShape.MULTI);
		pnlSouth.add(symbolTable, BorderLayout.CENTER);
		fillFieldNames();

		if (legend instanceof QuantityByCategoryLegend) {
			try {
				this.oldLegend = (QuantityByCategoryLegend) legend
						.cloneLegend();
			} catch (XMLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cmbColorField.setSelectedItem(this.oldLegend.getColorRampLegend()
					.getClassifyingFieldNames()[0]);
			cmbGraduatedSymbolField.setSelectedItem(this.oldLegend
					.getGraduatedSymbolLegend().getClassifyingFieldNames()[0]);
			symbolTable.removeAllItems();
			symbolTable.fillTableFromSymbolList(this.oldLegend.getSymbols(),
					this.oldLegend.getValues(),
					this.oldLegend.getDescriptions());

		} else {
			this.oldLegend = new QuantityByCategoryLegend();
			this.oldLegend.setClassifyingFieldNames(new String[] {
					(String) getCmbColorField().getSelectedItem(),
					(String) getCmbGraduatedField().getSelectedItem() });
			try {
				this.oldLegend.setShapeType(((FLyrVect) lyr).getShapeType());
			} catch (ReadDriverException e) {
				NotificationManager.addError(PluginServices.getText(this,
						"getting_layer_shape_type"), e);
			}
		}
	}

	private void fillSymbolListFromTable() {
		ISymbol theSymbol;
		IInterval theInterval = null;
		// Borramos las anteriores listas:
		this.oldLegend.clear();

		FLyrVect m = (FLyrVect) layer;
		try {

			if (this.oldLegend.getClassifyingFieldNames() != null) {
				String[] fNames = this.oldLegend.getClassifyingFieldNames();
				int[] fieldTypes = new int[this.oldLegend
						.getClassifyingFieldNames().length];

				for (int i = 0; i < this.oldLegend.getClassifyingFieldNames().length; i++) {
					int fieldIndex = m.getSource().getRecordset()
							.getFieldIndexByName(fNames[i]);
					fieldTypes[i] = m.getSource().getRecordset()
							.getFieldType(fieldIndex);
				}

				this.oldLegend.setClassifyingFieldTypes(fieldTypes);
			}
		} catch (ReadDriverException e) {
			NotificationManager.addError(
					PluginServices.getText(this, "could_not_setup_legend"), e);
		}

		for (int row = 0; row < symbolTable.getRowCount(); row++) {

			theInterval = (IInterval) symbolTable.getFieldValue(row, 1);
			theSymbol = (ISymbol) symbolTable.getFieldValue(row, 0);
			theSymbol
					.setDescription((String) symbolTable.getFieldValue(row, 2));

			if (theSymbol instanceof IFillSymbol) {
				this.oldLegend.getColorRampLegend().addSymbol(theInterval,
						theSymbol);
			} else if (theSymbol instanceof IMarkerSymbol) {
				this.oldLegend.getGraduatedSymbolLegend().addSymbol(
						theInterval, theSymbol);
			}

		}

		if (oldLegend.getColorRampLegend().isUseDefaultSymbol())
			this.oldLegend.getColorRampLegend().addSymbol(
					new NullIntervalValue(),
					oldLegend.getColorRampLegend().getDefaultSymbol());
		if (oldLegend.getGraduatedSymbolLegend().isUseDefaultSymbol())
			this.oldLegend.getGraduatedSymbolLegend().addSymbol(
					new NullIntervalValue(),
					oldLegend.getGraduatedSymbolLegend().getDefaultSymbol());

	}

	public ILegend getLegend() {
		fillSymbolListFromTable();
		try {
			this.legend = (QuantityByCategoryLegend) this.oldLegend
					.cloneLegend();
		} catch (XMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return this.legend;
	}

	public String getDescription() {
		return PluginServices
				.getText(this, "draw_quantities_for_each_category");
	}

	public ImageIcon getIcon() {
		return new ImageIcon(this.getClass().getClassLoader()
				.getResource("images/QuantitiesByCategory.png"));
	}

	public Class getParentClass() {
		return MultipleAttributes.class;
	}

	public String getTitle() {
		return PluginServices.getText(this, "quantity_by_category");
	}

	public JPanel getPanel() {
		return this;
	}

	public Class getLegendClass() {
		return QuantityByCategoryLegend.class;
	}

	public void actionPerformed(ActionEvent e) {
		JComponent c = (JComponent) e.getSource();

		if (c.equals(getBtnColor())) {
			VectorialIntervalLegend colorRamp = this.oldLegend
					.getColorRampLegend();
			String fieldName = (String) getCmbColorField().getSelectedItem();
			if (!fieldName.equals(colorRamp.getClassifyingFieldNames()[0])) {
				// if classification field has changed, clear the legend
				colorRamp.setClassifyingFieldNames(new String[] { fieldName });
				colorRamp.clear();
			}
			// create a new modal window to edit the color ramp legend
			VectorialInterval legPanel = new VectorialInterval();
			legPanel.setData(layer, colorRamp);
			InnerWindow window = new InnerWindow(legPanel);
			PluginServices.getMDIManager().addWindow(window);
			cmbColorField
					.setSelectedItem(colorRamp.getClassifyingFieldNames()[0]
							.toString());
			ILegend newLegend = window.getLegend();
			if (newLegend != null) {
				this.oldLegend.getColorRampLegend().clear();

				this.oldLegend.setColorRampLegend(newLegend);
			}
			symbolTable.removeAllItems();
			symbolTable.fillTableFromSymbolList(this.oldLegend.getSymbols(),
					this.oldLegend.getValues(),
					this.oldLegend.getDescriptions());
		} else if (c.equals(getBtnSymbol())) {
			GraduatedSymbolLegend graduatedSymbol = this.oldLegend
					.getGraduatedSymbolLegend();
			String fieldName = (String) getCmbGraduatedField()
					.getSelectedItem();
			if (!fieldName
					.equals(graduatedSymbol.getClassifyingFieldNames()[0])) {
				// if classification field has changed, clear the legend
				graduatedSymbol
						.setClassifyingFieldNames(new String[] { fieldName });
				graduatedSymbol.setDefaultSymbol(SymbologyFactory
						.createDefaultSymbolByShapeType(FShape.POINT));
				graduatedSymbol.setMinSymbolSize(1);
				graduatedSymbol.setMaxSymbolSize(14);
				graduatedSymbol.clear();
			}
			// create a new modal window to edit the graduated symbol legend
			GraduatedSymbols legPanel = new GraduatedSymbols(false);
			legPanel.setData(layer, graduatedSymbol);
			InnerWindow window = new InnerWindow(legPanel);
			PluginServices.getMDIManager().addWindow(window);
			cmbGraduatedSymbolField.setSelectedItem(graduatedSymbol
					.getClassifyingFieldNames()[0].toString());
			ILegend newLegend = window.getLegend();
			if (newLegend != null) {
				this.oldLegend.getGraduatedSymbolLegend().clear();
				this.oldLegend.setGraduateSymbolLegend(newLegend);
			}
			symbolTable.removeAllItems();
			symbolTable.fillTableFromSymbolList(this.oldLegend.getSymbols(),
					this.oldLegend.getValues(),
					this.oldLegend.getDescriptions());
		}

		else if (c.equals(getCmbColorField())) {
			symbolTable.removeAllItems();
			symbolTable.fillTableFromSymbolList(this.oldLegend
					.getGraduatedSymbolLegend().getSymbols(), this.oldLegend
					.getGraduatedSymbolLegend().getValues(), this.oldLegend
					.getDescriptions());
		}

		else if (c.equals(getCmbGraduatedField())) {
			symbolTable.removeAllItems();
			symbolTable.fillTableFromSymbolList(this.oldLegend
					.getColorRampLegend().getSymbols(), this.oldLegend
					.getColorRampLegend().getValues(), this.oldLegend
					.getDescriptions());
		}

		// if (c.equals(getButDel())) {
		// symbolTable.removeSelectedRows();
		// }
		//
		// else if(c.equals(getButDelAll())) {
		// symbolTable.removeAllItems();
		// }
	}

	private class InnerWindow extends JPanel implements IWindow {
		private ActionListener okAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				legend = panel.getLegend();
				PluginServices.getMDIManager().closeWindow(InnerWindow.this);
			}

		}, cancelAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PluginServices.getMDIManager().closeWindow(InnerWindow.this);
			}
		};
		private ILegend legend;
		private ILegendPanel panel;
		private WindowInfo wi;

		public InnerWindow(ILegendPanel panel) {
			this.panel = panel;
			this.setLayout(new BorderLayout());
			add((JComponent) panel, BorderLayout.NORTH);
			add(new AcceptCancelPanel(okAction, cancelAction),
					BorderLayout.SOUTH);
		}

		public ILegend getLegend() {
			return legend;
		}

		public WindowInfo getWindowInfo() {
			if (wi == null) {
				wi = new WindowInfo(WindowInfo.MODALDIALOG
						| WindowInfo.RESIZABLE);
				JComponent c = (JComponent) panel;
				wi.setWidth(c.getWidth());
				wi.setHeight(c.getHeight());
				wi.setTitle(panel.getDescription());
			}
			return wi;
		}

		public Object getWindowProfile() {
			return WindowInfo.DIALOG_PROFILE;
		}

	}

	/**
	 * Fills the comboboxes that are placed in the panel with the classifying
	 * field names of the layer
	 * 
	 */
	protected void fillFieldNames() {
		SelectableDataSource rs = null;
		ArrayList<String> nomFields = null;

		try {
			if (layer instanceof FLyrVect) {
				rs = ((FLyrVect) layer).getRecordset();
			} else {
				rs = ((AlphanumericData) layer).getRecordset();
			}
			rs.start();

			nomFields = new ArrayList<String>();

			int type;
			for (int i = 0; i < rs.getFieldCount(); i++) {
				type = rs.getFieldType(i);

				if (type == Types.NULL) {
					continue;
				}

				if ((type == Types.INTEGER) || (type == Types.DOUBLE)
						|| (type == Types.FLOAT) || (type == Types.BIGINT)) {
					nomFields.add(rs.getFieldAlias(i).trim());
				}
			}

			rs.stop();
		} catch (ReadDriverException e) {
			NotificationManager.addError(
					PluginServices.getText(this, "recovering_recordset"), e);
		}

		DefaultComboBoxModel cM = new DefaultComboBoxModel(nomFields.toArray());
		cmbColorField.setModel(cM);
		cM = new DefaultComboBoxModel(nomFields.toArray());
		cmbGraduatedSymbolField.setModel(cM);

		symbolTable.removeAllItems();
	}

	/**
	 * Checks if an specific field contains numerical data
	 * 
	 * @param fieldType
	 *            index of the field
	 * 
	 * @return boolean true or false depending on the type of data (numerical or
	 *         not)
	 */
	private boolean isNumericField(int fieldType) {
		switch (fieldType) {
		case Types.BIGINT:
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.INTEGER:
		case Types.NUMERIC:
		case Types.REAL:
		case Types.SMALLINT:
		case Types.TINYINT:
			return true;
		default:
			return false;
		}

	}

	public boolean isSuitableFor(FLayer layer) {
		if (layer instanceof FLyrVect) {
			try {
				FLyrVect lyr = (FLyrVect) layer;

				if ((lyr.getShapeType() % FShape.Z) != FShape.POLYGON)
					return false;

				SelectableDataSource sds;
				sds = ((FLyrVect) layer).getRecordset();
				String[] fNames = sds.getFieldNames();
				for (int i = 0; i < fNames.length; i++) {
					if (isNumericField(sds.getFieldType(i))) {
						return true;
					}
				}
			} catch (ReadDriverException e) {
				return false;
			}
		}
		return false;
	}
}
