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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Types;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JBlank;
import org.gvsig.gui.beans.swing.JIncrementalNumberField;
import org.gvsig.symbology.fmap.rendering.ProportionalSymbolsLegend;
import org.gvsig.symbology.fmap.symbols.MarkerFillSymbol;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.styles.IMarkerFillPropertiesStyle;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleLineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleMarkerSymbol;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.fmap.layers.layerOperations.ClassifiableVectorial;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ILegendPanel;
import com.iver.cit.gvsig.project.documents.view.legend.gui.JSymbolPreviewButton;
import com.iver.cit.gvsig.project.documents.view.legend.gui.Quantities;

/**
 * Implements the panel for the legend of proportional symbols.In the interface
 * will be options to select the value field, the normalization field (if the
 * user wants to use it) and options to select the symbol an its minimum and
 * maximum size.
 * 
 * Also there will be possible to select a background symbol (only when the
 * shapetype of the layer is polygonal).
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class ProportionalSymbols extends JPanel implements ILegendPanel {
	private static final long serialVersionUID = 7394720230276170902L;
	private JPanel symbolPanel;
	private JPanel backgroundPanel;
	private JSymbolPreviewButton tmplateSymbol;
	private JSymbolPreviewButton backSymbol;
	private JComboBox cmbValue;
	private JComboBox cmbNormalization;
	private JIncrementalNumberField txtMinSize;
	private JIncrementalNumberField txtMaxSize;
	private static Logger logger = Logger.getLogger(ProportionalSymbols.class
			.getName());
	private ClassifiableVectorial myLayer;
	private ProportionalSymbolsLegend auxLegend;
	// private ProportionalSymbolsLegend theLegend;
	private String[] fieldNames;
	private int templateShapeType = 0;
	private String noNormalization = PluginServices.getText(this, "none");
	private boolean useNormalization = true;

	/**
	 * Default constructor
	 */
	public ProportionalSymbols() {
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {

		setLayout(new BorderLayout());
		JPanel aux = new JPanel(new BorderLayout());

		JPanel fieldsPanel = new JPanel(new FlowLayout());
		fieldsPanel.setBorder(BorderFactory.createTitledBorder(null,
				PluginServices.getText(this, "fields")));
		fieldsPanel.setPreferredSize(new Dimension(300, 60));

		cmbValue = new JComboBox();
		cmbValue.setActionCommand("VALUE_SELECTED");

		cmbNormalization = new JComboBox();
		cmbNormalization.setActionCommand("NORMALIZATION_SELECTED");

		fieldsPanel
				.add(new JLabel(PluginServices.getText(this, "value") + ":"));
		fieldsPanel.add(cmbValue);

		fieldsPanel.add(new JLabel(PluginServices
				.getText(this, "normalization") + ":"));
		fieldsPanel.add(cmbNormalization);

		symbolPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 2));
		symbolPanel.setBorder(BorderFactory.createTitledBorder(null,
				PluginServices.getText(this, "symbol")));

		GridBagLayoutPanel aux2 = new GridBagLayoutPanel();
		aux2.addComponent(new JLabel(PluginServices.getText(this, "size")));
		aux2.addComponent(new JBlank(10, 10));
		aux2.addComponent(PluginServices.getText(this, "from") + ":",
				getTxtMinSize());
		aux2.addComponent(PluginServices.getText(this, "to") + ":",
				getTxtMaxSize());

		JPanel templatePanel = new JPanel();
		templatePanel.setBorder(BorderFactory.createTitledBorder(null,
				PluginServices.getText(this, "template")));
		templatePanel.add(getTemplSymbol());
		symbolPanel.add(new JBlank(10, 10));
		symbolPanel.add(aux2);
		symbolPanel.add(new JBlank(10, 10));
		symbolPanel.add(templatePanel);

		aux.add(fieldsPanel, BorderLayout.NORTH);
		aux.add(symbolPanel, BorderLayout.CENTER);

		this.add(aux, BorderLayout.CENTER);

		cmbValue.addActionListener(action);
		cmbNormalization.addActionListener(action);
		tmplateSymbol.addActionListener(action);
	}

	/**
	 * Creates the panel where the button for the background symbol will be
	 * placed
	 * 
	 * @return JPanel panel
	 */

	private JPanel getBackgroundPanel() {
		backgroundPanel = new JPanel();
		backgroundPanel.setBorder(BorderFactory.createTitledBorder(null,
				PluginServices.getText(this, "background")));
		backgroundPanel.add(getBtnBackground());
		return backgroundPanel;
	}

	/**
	 * Creates a JIncrementalNumberField which is used to select the maximum
	 * size for the symbol
	 * 
	 * @return JIncrementalNumberField
	 */
	private JIncrementalNumberField getTxtMaxSize() {
		if (txtMaxSize == null) {
			txtMaxSize = new JIncrementalNumberField(String.valueOf(25), 7, 0,
					100, 1);
			txtMaxSize.addActionListener(action);
		}
		return txtMaxSize;
	}

	/**
	 * Creates a JIncrementalNumberField which is used to select the minimum
	 * size for the symbol
	 * 
	 * @return JIncrementalNumberField
	 */
	private JIncrementalNumberField getTxtMinSize() {
		if (txtMinSize == null) {
			txtMinSize = new JIncrementalNumberField(String.valueOf(3), 7, 0,
					100, 1);
			txtMinSize.addActionListener(action);
		}
		return txtMinSize;
	}

	/**
	 * Creates a JSymbolPreviewButton which is used to select the template
	 * symbol
	 * 
	 * @return JSymbolPreviewButton
	 */
	private JSymbolPreviewButton getTemplSymbol() {

		if (tmplateSymbol == null) {
			int templateShapeType = ((this.templateShapeType % FShape.Z) == FShape.POLYGON) ? FShape.POINT
					: this.templateShapeType;
			tmplateSymbol = new JSymbolPreviewButton(templateShapeType);
		}
		tmplateSymbol.setPreferredSize(new Dimension(100, 45));
		return tmplateSymbol;
	}

	/**
	 * Creates a JSymbolPreviewButton which is used to select the background
	 * symbol
	 * 
	 * @return JSymbolPreviewButton
	 */
	private JSymbolPreviewButton getBtnBackground() {
		if (backSymbol == null) {
			backSymbol = new JSymbolPreviewButton(FShape.POLYGON);
			backSymbol.setPreferredSize(new Dimension(100, 45));
		}
		return backSymbol;
	}

	/**
	 * Creates a new symbol of an specific shapetype with a concrete size
	 * 
	 * @param shapeType
	 * @param size
	 * 
	 * @return ISymbol symbol created
	 */
	private ISymbol newSymbol(int shapeType, double size) {
		if (getTemplSymbol().getSymbol() == null) {
			ISymbol templateSymbol;
			switch (shapeType) {
			case FShape.POINT:
			case FShape.POLYGON:
			case FShape.POINT | FShape.Z:
			case FShape.POINT | FShape.M:
			case FShape.POLYGON | FShape.Z:
			case FShape.POLYGON | FShape.M:
				templateSymbol = new SimpleMarkerSymbol();
				((SimpleMarkerSymbol) templateSymbol).setSize(size);
				((SimpleMarkerSymbol) templateSymbol).setColor(Color.DARK_GRAY);
				break;

			case FShape.LINE:
			case FShape.LINE | FShape.Z:
			case FShape.LINE | FShape.M:
				templateSymbol = new SimpleLineSymbol();
				((SimpleLineSymbol) templateSymbol).setLineWidth(size);
				((SimpleLineSymbol) templateSymbol)
						.setLineColor(Color.DARK_GRAY);
				break;
			default:
				throw new Error("Unknown symbol type");
			}
			getTemplSymbol().setSymbol(templateSymbol);
			return newSymbol(shapeType, size);
		} else {
			// clone symbol
			ISymbol mySymbol = SymbologyFactory.createSymbolFromXML(
					getTemplSymbol().getSymbol().getXMLEntity(), null);

			if (mySymbol instanceof ILineSymbol) {
				ILineSymbol lSym = (ILineSymbol) mySymbol;
				lSym.setLineWidth(size);

			}
			if (mySymbol instanceof IMarkerSymbol) {
				IMarkerSymbol mSym = (IMarkerSymbol) mySymbol;
				mSym.setSize(size);
				if (shapeType == FShape.POLYGON) {
					// this is to allow using in Polygon layers
					MarkerFillSymbol fillSymbol = new MarkerFillSymbol();
					fillSymbol.setOutline(null);
					fillSymbol.setFillColor(null);
					fillSymbol.getMarkerFillProperties().setFillStyle(
							IMarkerFillPropertiesStyle.SINGLE_CENTERED_SYMBOL);
					fillSymbol.setMarker(mSym);
				}
			}
			return mySymbol;
		}
	}

	/**
	 * Fills the comboboxes that are placed in the panel with the classifying
	 * field names of the layer which contain numerical information
	 * 
	 */
	private void fillFieldNames() {
		SelectableDataSource rs;

		try {
			rs = ((FLyrVect) myLayer).getRecordset();
			logger.debug("rs.start()");
			rs.start();

			int cont = 0;
			for (int i = 0; i < rs.getFieldCount(); i++) {
				if (isNumericField(rs.getFieldType(i)))
					cont++;
			}
			String[] nomFields = new String[cont];

			cont = 0;
			for (int i = 0; i < rs.getFieldCount(); i++) {
				if (isNumericField(rs.getFieldType(i))) {
					nomFields[cont] = rs.getFieldAlias(i).trim();
					cont++;
				}
			}

			rs.stop();
			this.fieldNames = nomFields;

			DefaultComboBoxModel cMValue = new DefaultComboBoxModel(
					this.fieldNames);
			DefaultComboBoxModel cMNormalization = new DefaultComboBoxModel(
					this.fieldNames);
			cmbValue.setModel(cMValue);
			cmbNormalization.setModel(cMNormalization);
			cmbNormalization.addItem(noNormalization);

		} catch (ReadDriverException e) {
			NotificationManager.addError(
					PluginServices.getText(this, "recovering_recordset"), e);
		}
	}

	public void setData(FLayer lyr, ILegend legend) {

		this.myLayer = (ClassifiableVectorial) lyr;

		fillFieldNames();

		try {
			templateShapeType = ((this.myLayer.getShapeType() % FShape.Z) == FShape.POLYGON) ? FShape.POINT
					: this.myLayer.getShapeType();
			getTemplSymbol().setShapeType(templateShapeType);

			if ((myLayer.getShapeType() % FShape.Z) == FShape.POLYGON
					&& backgroundPanel == null)
				symbolPanel.add(getBackgroundPanel());

		} catch (ReadDriverException e) {
			NotificationManager.addError(PluginServices.getText(this,
					"error accessing to the layer"), e);
		}

		if (ProportionalSymbolsLegend.class.equals(legend.getClass())) {

			try {
				auxLegend = (ProportionalSymbolsLegend) legend.cloneLegend();
			} catch (XMLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cmbValue.setSelectedItem(auxLegend.getClassifyingFieldNames()[0]);
			ISymbol defSymbol = auxLegend.getDefaultSymbol();

			if (((ProportionalSymbolsLegend) auxLegend).getUseNormalization())
				cmbNormalization.setSelectedItem(auxLegend
						.getClassifyingFieldNames()[1]);
			else
				cmbNormalization.setSelectedItem(noNormalization);

			txtMaxSize.setDouble(auxLegend.getMaxSize());
			txtMinSize.setDouble(auxLegend.getMinSize());

			if (templateShapeType == FShape.LINE) {
				((ILineSymbol) defSymbol).setLineWidth(2);
			} else {
				((IMarkerSymbol) defSymbol).setSize(15);
			}

			getTemplSymbol().setSymbol(defSymbol);

			if (((ProportionalSymbolsLegend) auxLegend).getBackgroundSymbol() != null)
				getBtnBackground().setSymbol(
						((ProportionalSymbolsLegend) auxLegend)
								.getBackgroundSymbol());

		} else {

			auxLegend = new ProportionalSymbolsLegend();
			auxLegend.setTemplateShapeType(templateShapeType);
			auxLegend.setDefaultSymbol(newSymbol(templateShapeType,
					templateShapeType == FShape.LINE ? 2 : 15));
			getTemplSymbol().setSymbol(auxLegend.getDefaultSymbol());
			if (templateShapeType == FShape.LINE) {
				txtMinSize.setDouble(3);
				txtMaxSize.setDouble(3);
			} else {
				txtMinSize.setDouble(10);
				txtMaxSize.setDouble(10);
			}
		}

	}

	public ILegend getLegend() {
		ProportionalSymbolsLegend theLegend = new ProportionalSymbolsLegend();

		String[] fieldNames = new String[2];
		fieldNames[0] = cmbValue.getSelectedItem().toString();

		if (!useNormalization)
			fieldNames[1] = fieldNames[0];
		else
			fieldNames[1] = cmbNormalization.getSelectedItem().toString();

		auxLegend.setTemplateShapeType(templateShapeType);
		auxLegend.setValueField(cmbValue.getSelectedItem().toString());
		auxLegend.setNormalizationField(cmbNormalization.getSelectedItem()
				.toString());
		auxLegend.setUseNormalization(useNormalization);

		auxLegend.setMinSize(txtMinSize.getDouble());
		auxLegend.setMaxSize(txtMaxSize.getDouble());

		ISymbol symbol = getTemplSymbol().getSymbol();
		symbol.setDescription(getSymbolDescription());
		auxLegend.setDefaultSymbol(symbol);
		auxLegend.addSymbol(ValueFactory.createValue("defaultSymbol"), symbol);

		auxLegend.setBackgroundSymbol(getBtnBackground().getSymbol());
		auxLegend.setClassifyingFieldNames(fieldNames);

		DataSource recordSet = null;

		try {
			recordSet = ((AlphanumericData) myLayer).getRecordset();

			for (int i = 0; i < fieldNames.length; i++) {
				double min = 0, max = 0;

				for (int j = 0; j < recordSet.getRowCount(); j++) {
					Value val = recordSet.getFieldValue(j,
							recordSet.getFieldIndexByName(fieldNames[i]));
					double dob = Double.valueOf(val.toString());
					if (dob < min)
						min = dob;
					if (dob > max)
						max = dob;
				}
				if (i == 0) {
					auxLegend.setMinFeature(min);
					auxLegend.setMaxFeature(max);
				}
			}

		} catch (ReadDriverException e) {
			NotificationManager.addError("error_accessing_to_the_layer", e);
		}

		try {
			theLegend = (ProportionalSymbolsLegend) auxLegend.cloneLegend();
			theLegend.addSymbol(ValueFactory.createValue("defaultSymbol"),
					symbol);
		} catch (XMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return theLegend;
	}

	/**
	 * Creates the String which will be the description of the symbol. If the
	 * selected normalization field is not "None", then the String created will
	 * have this structure ValueField/NormalizationField. Else, if the user
	 * select "None" for the normalization field,the string will show the
	 * ValueField only.
	 * 
	 * @return String description for the symbol
	 */

	private String getSymbolDescription() {
		String description = "";

		if (cmbValue.getSelectedItem() != null)
			description += cmbValue.getSelectedItem().toString();
		if (cmbNormalization.getSelectedItem().toString()
				.compareTo(noNormalization) != 0)
			description += " / "
					+ cmbNormalization.getSelectedItem().toString();

		return description;
	}

	public String getDescription() {
		return PluginServices.getText(this,
				"draw_quantities_using_symbol_size_to_show_exact_values");
	}

	public ImageIcon getIcon() {
		return new ImageIcon(this.getClass().getClassLoader()
				.getResource("images/ProportionalSymbols.PNG"));
	}

	public Class getParentClass() {
		return Quantities.class;
	}

	public String getTitle() {
		return PluginServices.getText(this, "proportional_symbols");
	}

	public Class getLegendClass() {
		return ProportionalSymbolsLegend.class;
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

			FLyrVect lyr = (FLyrVect) layer;
			try {
				if (lyr.getShapeType() == FShape.MULTI)
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

	public JPanel getPanel() {
		return this;
	}

	private ActionListener action = new ActionListener() {

		public void actionPerformed(ActionEvent e) {

			if (e.getSource().equals(cmbValue)) {
				JComboBox cb = (JComboBox) e.getSource();
				auxLegend.setValueField(cb.getSelectedItem().toString());
			}
			if (e.getSource().equals(cmbNormalization)) {
				JComboBox cb = (JComboBox) e.getSource();
				if (cb.getSelectedItem().toString().compareTo(noNormalization) == 0) {
					useNormalization = false;
					auxLegend.setNormalizationField(cmbValue.getSelectedItem()
							.toString());
				} else {
					useNormalization = true;
					auxLegend.setNormalizationField(cb.getSelectedItem()
							.toString());
				}
				auxLegend.setUseNormalization(useNormalization);
			}
			if (e.getSource().equals(txtMinSize)) {
				// ISymbol sym = tmplateSymbol.getSymbol();
				// if(sym != null) {
				// if(sym instanceof ILineSymbol) {
				// ILineSymbol line = (ILineSymbol)sym;
				// line.setLineWidth(txtMinSize.getDouble());
				// tmplateSymbol.setSymbol(line);
				// }
				//
				// if(sym instanceof IMarkerSymbol) {
				// IMarkerSymbol point = (IMarkerSymbol)sym;
				// point.setSize(txtMinSize.getDouble());
				// tmplateSymbol.setSymbol(point);
				// }
				// tmplateSymbol.repaint();
				// }

				if (txtMaxSize.getDouble() < txtMinSize.getDouble())
					txtMaxSize.setDouble(txtMinSize.getDouble());
			}
			if (e.getSource().equals(txtMaxSize)) {
				if (txtMaxSize.getDouble() < txtMinSize.getDouble())
					txtMinSize.setDouble(txtMaxSize.getDouble());
			}
			// if(e.getSource().equals(tmplateSymbol)) {
			// ISymbol sym = tmplateSymbol.getSymbol();
			// if(sym != null) {
			// if(sym instanceof ILineSymbol) {
			// ILineSymbol line = (ILineSymbol)sym;
			// txtMinSize.setDouble(((int)line.getLineWidth()));
			// txtMaxSize.setDouble(((int)line.getLineWidth()));
			// }
			//
			// if(sym instanceof IMarkerSymbol) {
			// IMarkerSymbol point = (IMarkerSymbol)sym;
			// txtMinSize.setDouble(((int)point.getSize()));
			// txtMaxSize.setDouble(((int)point.getSize()));
			//
			// }
			// }
			// }
		}
	};

}
