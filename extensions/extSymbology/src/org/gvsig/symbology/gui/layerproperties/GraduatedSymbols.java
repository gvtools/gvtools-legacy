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

/* CVS MESSAGES:
 *
 * $Id: GraduatedSymbols.java 16232 2007-11-09 13:36:35Z jdominguez $
 * $Log$
 * Revision 1.8  2007-09-19 15:44:44  jaume
 * new signal catched (legend error)
 *
 * Revision 1.7  2007/05/21 10:38:27  jaume
 * *** empty log message ***
 *
 * Revision 1.6  2007/05/17 09:32:37  jaume
 * *** empty log message ***
 *
 * Revision 1.5  2007/05/10 09:46:45  jaume
 * Refactored legend interface names
 *
 * Revision 1.4  2007/05/08 15:45:31  jaume
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/21 08:03:03  jaume
 * refactored to use ISymbol instead of FSymbol
 *
 * Revision 1.2  2007/03/09 11:25:00  jaume
 * Advanced symbology (start committing)
 *
 * Revision 1.1.2.4  2007/02/21 07:35:14  jaume
 * *** empty log message ***
 *
 * Revision 1.1.2.3  2007/02/14 09:59:17  jaume
 * *** empty log message ***
 *
 * Revision 1.1.2.2  2007/02/13 16:19:19  jaume
 * graduated symbol legends (start commiting)
 *
 * Revision 1.1.2.1  2007/02/12 15:14:41  jaume
 * refactored interval legend and added graduated symbol legend
 *
 *
 */
package org.gvsig.symbology.gui.layerproperties;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JIncrementalNumberField;
import org.gvsig.symbology.fmap.rendering.GraduatedSymbolLegend;
import org.gvsig.symbology.fmap.symbols.MarkerFillSymbol;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
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
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.rendering.FInterval;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;
import com.iver.cit.gvsig.fmap.rendering.NullIntervalValue;
import com.iver.cit.gvsig.fmap.rendering.VectorialIntervalLegend;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ILegendPanel;
import com.iver.cit.gvsig.project.documents.view.legend.gui.JSymbolPreviewButton;
import com.iver.cit.gvsig.project.documents.view.legend.gui.Quantities;
import com.iver.cit.gvsig.project.documents.view.legend.gui.SymbolTable;
import com.iver.cit.gvsig.project.documents.view.legend.gui.VectorialInterval;

/**
 * Implements the interface that shows the information of a legend which draws
 * quantities using symbol size to show relative values.
 * 
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 * 
 */

public class GraduatedSymbols extends VectorialInterval implements ILegendPanel {
	private static final long serialVersionUID = -263195536343912694L;
	static final double DEFAULT_SYMBOL_MAX_SIZE = 7;
	private static final double DEFAULT_SYMBOL_MIN_SIZE = 1;
	private JIncrementalNumberField txtMinSize;
	private JIncrementalNumberField txtMaxSize;
	private JSymbolPreviewButton btnTemplate;
	private JSymbolPreviewButton btnBackground;
	private int shapeType;
	private GridBagLayoutPanel aux;
	private int templateShapeType = 0;
	private boolean showBackground = true;

	public GraduatedSymbols() {
		super();
		this.showBackground = true;
	}

	public GraduatedSymbols(boolean showBackground) {
		super();
		this.showBackground = showBackground;
	}

	@Override
	public JPanel getOptionPanel() {

		if (optionPanel == null) {
			optionPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
			optionPanel.setBorder(BorderFactory.createTitledBorder(null,
					PluginServices.getText(this, "symbol")));

			aux = new GridBagLayoutPanel();
			aux.addComponent(new JLabel(PluginServices.getText(this, "size")));
			aux.addComponent(PluginServices.getText(this, "from") + ":",
					getTxtMinSize());
			aux.addComponent(PluginServices.getText(this, "to") + ":",
					getTxtMaxSize());
			aux.addComponent(PluginServices.getText(this, "template"),
					getBtnTemplate());
			optionPanel.add(aux);

		}
		return optionPanel;
	}

	/**
	 * Adds the button that allows the user to select a background for the
	 * graduated symbol.
	 */
	private void getBackgroundPanel() {
		aux.addComponent(PluginServices.getText(this, "background"),
				getBtnBackground());
	}

	/**
	 * Creates the JSymbolPreviewButton for the background of the graduated
	 * symbol.
	 */
	private JSymbolPreviewButton getBtnBackground() {
		if (btnBackground == null) {
			btnBackground = new JSymbolPreviewButton(FShape.POLYGON);
			btnBackground.setPreferredSize(new Dimension(100, 35));
			btnBackground.setSymbol(SymbologyFactory.createDefaultFillSymbol());
		}
		return btnBackground;
	}

	/**
	 * Creates the JSymbolPreviewButton for the graduated symbol.
	 */
	private JSymbolPreviewButton getBtnTemplate() {
		if (btnTemplate == null) {
			templateShapeType = ((shapeType % FShape.Z) == FShape.POLYGON) ? FShape.POINT
					: shapeType;
			btnTemplate = new JSymbolPreviewButton(templateShapeType);
			btnTemplate.setPreferredSize(new Dimension(100, 35));

		}
		return btnTemplate;
	}

	/**
	 * Creates the JIncrementalNumberField which is used to specify the maximum
	 * size of the graduated symbol
	 */
	private JIncrementalNumberField getTxtMaxSize() {
		if (txtMaxSize == null) {
			txtMaxSize = new JIncrementalNumberField(String.valueOf(25), 7, 0,
					100, 1);

		}
		return txtMaxSize;
	}

	/**
	 * Creates the JIncrementalNumberField which is used to specify the minimum
	 * size of the graduated symbol
	 */
	private JIncrementalNumberField getTxtMinSize() {
		if (txtMinSize == null) {
			txtMinSize = new JIncrementalNumberField(String.valueOf(3), 7, 0,
					100, 1);
		}
		return txtMinSize;
	}

	@Override
	public void setData(FLayer lyr, ILegend legend) {
		this.layer = (FLyrVect) lyr;

		try {
			shapeType = (this.layer.getShapeType());
			templateShapeType = ((shapeType % FShape.Z) == FShape.POLYGON) ? FShape.POINT
					: shapeType;
			getBtnTemplate().setShapeType(templateShapeType);
			if (showBackground) {
				if ((shapeType % FShape.Z) == FShape.POLYGON
						&& btnBackground == null) {
					getBackgroundPanel();
					getBtnBackground().setShapeType(FShape.POLYGON);
				}
			}
		} catch (ReadDriverException e) {
			NotificationManager.addError(
					PluginServices.getText(this, "generating_intervals"), e);
		}

		if (symbolTable != null)
			pnlCenter.remove(symbolTable);

		getDefaultSymbolPrev(templateShapeType);

		symbolTable = new SymbolTable(this, SymbolTable.INTERVALS_TYPE,
				templateShapeType);
		pnlCenter.add(symbolTable);

		fillFieldNames();

		if (legend instanceof GraduatedSymbolLegend) {
			try {
				auxLegend = (GraduatedSymbolLegend) legend.cloneLegend();
			} catch (XMLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			getChkDefaultvalues().setSelected(auxLegend.isUseDefaultSymbol());
			cmbField.setSelectedItem(auxLegend.getClassifyingFieldNames()[0]);
			symbolTable.fillTableFromSymbolList(auxLegend.getSymbols(),
					auxLegend.getValues(), auxLegend.getDescriptions());
			getTxtMaxSize().setDouble(
					((GraduatedSymbolLegend) auxLegend).getMaxSymbolSize());
			getTxtMinSize().setDouble(
					((GraduatedSymbolLegend) auxLegend).getMinSymbolSize());
			if (auxLegend.getSymbols().length > 0) {
				getBtnTemplate()
						.setSymbol(
								((GraduatedSymbolLegend) auxLegend)
										.getSymbols()[auxLegend.getSymbols().length - 1]);
			} else {
				getBtnTemplate()
						.setSymbol(
								newSymbol(shapeType,
										shapeType == FShape.LINE ? 3 : 10));
			}
			if (showBackground) {
				if ((shapeType % FShape.Z) == FShape.POLYGON)
					getBtnBackground().setSymbol(
							((GraduatedSymbolLegend) auxLegend)
									.getBackgroundSymbol());
			}
			if (auxLegend.isUseDefaultSymbol())
				txtNumIntervals
						.setText(String.valueOf(auxLegend.getSymbols().length - 1));
			else
				txtNumIntervals
						.setText(String.valueOf(auxLegend.getSymbols().length));

		} else {
			// Si la capa viene con otro tipo de leyenda, creamos
			// una nueva del tipo que maneja este panel
			auxLegend = new GraduatedSymbolLegend();
			getTxtMaxSize().setDouble(DEFAULT_SYMBOL_MAX_SIZE);
			getTxtMinSize().setDouble(DEFAULT_SYMBOL_MIN_SIZE);
			auxLegend.setDefaultSymbol(newSymbol(shapeType,
					shapeType == FShape.LINE ? 3 : 10));
			getBtnTemplate().setSymbol(auxLegend.getDefaultSymbol());
			((GraduatedSymbolLegend) auxLegend)
					.setTemplateShapeType(templateShapeType);

		}
		cmbFieldType.setSelectedIndex(auxLegend.getIntervalType());
		defaultSymbolPrev.setSymbol(auxLegend.getDefaultSymbol());
	}

	@Override
	public String getDescription() {
		return PluginServices.getText(this,
				"draw_quantities_using_symbol_size_to_show_relative_values");
	}

	@Override
	public ILegend getLegend() {

		GraduatedSymbolLegend gsl = new GraduatedSymbolLegend(
				(VectorialIntervalLegend) super.getLegend());

		double minSize = getTxtMinSize().getDouble();
		gsl.setMinSymbolSize(minSize);
		gsl.setMaxSymbolSize(getTxtMaxSize().getDouble());

		try {
			gsl.setShapeType(layer.getShapeType());
		} catch (ReadDriverException e) {
			NotificationManager.addWarning(
					"Reached what should be unreachable code", e);
		}

		ISymbol sym = getBtnTemplate().getSymbol();

		if (sym != null) {
			gsl.setTemplateShapeType(sym.getSymbolType());
			gsl.setDefaultSymbol(sym);
		} else {
			gsl.setTemplateShapeType(templateShapeType);
		}

		gsl.useDefaultSymbol(getChkDefaultvalues().isSelected());
		if (defaultSymbolPrev.getSymbol() != null)
			gsl.setDefaultSymbol(defaultSymbolPrev.getSymbol());

		if (this.showBackground) {
			if ((shapeType % FShape.Z) == FShape.POLYGON)
				gsl.setBackgroundSymbol(btnBackground.getSymbol());
		}

		return gsl;
	}

	@Override
	public ImageIcon getIcon() {
		return new ImageIcon(this.getClass().getClassLoader()
				.getResource("images/GraduatedSymbols.PNG"));
	}

	@Override
	public Class getParentClass() {
		return Quantities.class;
	}

	@Override
	public String getTitle() {
		return PluginServices.getText(this, "graduated_symbols");
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

	@Override
	public Class getLegendClass() {
		return GraduatedSymbolLegend.class;
	}

	@Override
	public boolean isSuitableFor(FLayer layer) {
		if (super.isSuitableFor(layer)) {
			FLyrVect lVect = (FLyrVect) layer;
			try {
				return lVect.getShapeType() != FShape.MULTI;
			} catch (ReadDriverException e) {
				return false;
			}
		}
		return false;
	}

	private ISymbol newSymbol(int shapeType, double size) {
		if (getBtnTemplate().getSymbol() == null) {
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
			getBtnTemplate().setSymbol(templateSymbol);
			return newSymbol(shapeType, size);
		} else {
			// clone symbol
			ISymbol mySymbol = SymbologyFactory.createSymbolFromXML(
					getBtnTemplate().getSymbol().getXMLEntity(), null);

			if (mySymbol instanceof ILineSymbol) {
				ILineSymbol lSym = (ILineSymbol) mySymbol;
				lSym.setLineWidth(size);

			}
			if (mySymbol instanceof IMarkerSymbol) {
				IMarkerSymbol mSym = (IMarkerSymbol) mySymbol;
				mSym.setSize(size);
				if ((shapeType % FShape.Z) == FShape.POLYGON) {
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

	@Override
	protected void fillTableValues() {

		symbolTable.removeAllItems();

		try {
			FInterval[] arrayIntervalos = calculateIntervals();
			if (arrayIntervalos == null)
				return;

			FInterval interval;
			NumberFormat.getInstance().setMaximumFractionDigits(2);
			auxLegend.clear();

			float minSize = (float) getTxtMinSize().getDouble();
			float maxSize = (float) getTxtMaxSize().getDouble();
			ISymbol theSymbol;

			this.shapeType = layer.getShapeType();

			if ((layer.getShapeType() % FShape.Z) == FShape.POLYGON)
				this.templateShapeType = FShape.POINT;

			auxLegend = LegendFactory
					.createVectorialIntervalLegend(this.shapeType);
			auxLegend.setIntervalType(getCmbIntervalTypes().getSelectedIndex());
			if (chkdefaultvalues.isSelected()) {
				auxLegend.getDefaultSymbol().setDescription("Default");
				auxLegend
						.addSymbol(
								new NullIntervalValue(),
								SymbologyFactory
										.createDefaultSymbolByShapeType(this.templateShapeType));
			}

			int symbolType;
			symbolType = layer.getShapeType();
			int numSymbols = 0;

			double step = (maxSize - minSize) / arrayIntervalos.length;
			double size = minSize;
			for (int k = 0; k < arrayIntervalos.length; k++) {
				interval = arrayIntervalos[k];
				theSymbol = newSymbol(symbolType, size);

				auxLegend.addSymbol(interval, theSymbol);
				System.out.println("addSymbol = " + interval + " theSymbol = "
						+ theSymbol.getDescription());
				numSymbols++;

				if (numSymbols > 100) {
					int resp = JOptionPane
							.showConfirmDialog(this, PluginServices.getText(
									this, "mas_de_100_simbolos"),
									PluginServices.getText(this,
											"quiere_continuar"),
									JOptionPane.YES_NO_OPTION,
									JOptionPane.WARNING_MESSAGE);

					if ((resp == JOptionPane.NO_OPTION)
							|| (resp == JOptionPane.DEFAULT_OPTION)) {
						return;
					}
				}

				theSymbol.setDescription(NumberFormat.getInstance().format(
						interval.getMin())
						+ " - "
						+ NumberFormat.getInstance().format(interval.getMax()));

				size = size + step;
			} // for

			symbolTable.fillTableFromSymbolList(auxLegend.getSymbols(),
					auxLegend.getValues(), auxLegend.getDescriptions());

		} catch (ReadDriverException e) {
			NotificationManager
					.addError(PluginServices.getText(this,
							"could_not_get_shape_type"), e);
		} catch (LegendLayerException e) {
			NotificationManager.addError(
					PluginServices.getText(this, "failed_computing_intervals"),
					e);
		}

		bDelAll.setEnabled(true);
		bDel.setEnabled(true);

	}

	public void setShowBackground(boolean showBackground) {
		this.showBackground = showBackground;
	}

	public boolean getShowBackground() {
		return this.showBackground;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
