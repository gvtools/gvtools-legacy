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
package org.gvsig.symbology.gui.styling;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JBlank;
import org.gvsig.gui.beans.swing.JIncrementalNumberField;
import org.gvsig.gui.beans.swing.ValidatingTextField;
import org.gvsig.raster.datastruct.ColorItem;
import org.gvsig.symbology.fmap.symbols.GradientFillSymbol;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.gui.styling.AbstractTypeSymbolEditor;
import com.iver.cit.gvsig.gui.styling.EditorTool;
import com.iver.cit.gvsig.gui.styling.JComboBoxColorScheme;
import com.iver.cit.gvsig.gui.styling.SymbolEditor;
import com.iver.cit.gvsig.project.documents.view.legend.gui.JSymbolPreviewButton;
import com.iver.utiles.swing.JComboBox;

/**
 * <b>GradientFill</b> allows to store and modify the properties that fills a
 * polygon with a padding with a gradient
 * <p>
 * <p>
 * This functionality is carried out thanks to a tab (gradient fill)which is
 * included in the panel to edit the properities of a symbol (SymbolEditor)how
 * is explained in AbstractTypeSymbolEditor.
 * <p>
 * <p>
 * This tab permits the user to change the different properties of the gradient
 * such as its style <b>gradientStyle</b>, angle of rotation
 * <b>gradientAngle</b>, percentage <b>gradientPercentage</b>, intervals
 * <b>gradientIntervals</b> and the outline <b>outline</b> of the polygon.Also
 * the user can select the colors for the gradient <b>gradientColor</b>.
 * 
 * 
 * 
 * @see AbstractTypeSymbolEditor
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class GradientFill extends AbstractTypeSymbolEditor implements
		ActionListener {

	private JIncrementalNumberField gradientIntervals;
	private JIncrementalNumberField gradientPercentage;
	private JIncrementalNumberField gradientAngle;
	private JComboBoxColorScheme gradientColor;
	private JComboBox gradientStyle;
	private JSymbolPreviewButton btnOutline;
	private ILineSymbol outline;
	private ArrayList<JPanel> tabs = new ArrayList<JPanel>();
	private JCheckBox useBorder;

	/**
	 * Constructor method
	 * 
	 * @param owner
	 */
	public GradientFill(SymbolEditor owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {

		JPanel myTab = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 5));
		myTab.setName(PluginServices.getText(this, "gradient_fill"));

		JPanel aux = new JPanel(new GridLayout(1, 2, 20, 5));

		GridBagLayoutPanel aux2;

		aux2 = new GridBagLayoutPanel();

		aux2.addComponent(new JBlank(5, 20));
		gradientIntervals = new JIncrementalNumberField("", 10,
				ValidatingTextField.INTEGER_VALIDATOR,
				ValidatingTextField.NUMBER_CLEANER, 1, 100, 1);
		aux2.addComponent(PluginServices.getText(this, "intervals") + ":",
				gradientIntervals);
		aux2.addComponent(new JBlank(5, 5));

		gradientPercentage = new JIncrementalNumberField("", 10, 0, 100, 1);
		aux2.addComponent(PluginServices.getText(this, "percentage") + ":",
				gradientPercentage);
		gradientPercentage.setDouble(100);
		aux2.addComponent(new JBlank(5, 5));

		gradientAngle = new JIncrementalNumberField("", 10, 0, 360, 1);
		aux2.addComponent(PluginServices.getText(this, "angle") + ":",
				gradientAngle);

		aux.add(aux2);

		aux2 = new GridBagLayoutPanel();

		aux2.addComponent(new JBlank(5, 20));
		gradientStyle = getCmbgradientStyle();
		aux2.addComponent(PluginServices.getText(this, "style") + ":",
				gradientStyle);

		aux2.addComponent(new JBlank(5, 5));
		useBorder = new JCheckBox(PluginServices.getText(this, "use_outline"));
		aux2.addComponent(useBorder);
		aux2.addComponent(PluginServices.getText(this, "outline") + ":",
				btnOutline = new JSymbolPreviewButton(FShape.LINE));
		aux2.addComponent(new JBlank(5, 5));

		JPanel aux3 = new JPanel(new GridLayout(1, 2, 20, 5));

		GridBagLayoutPanel aux4 = new GridBagLayoutPanel();

		aux4.addComponent(new JBlank(5, 5));
		aux4.addComponent(PluginServices.getText(this, "gradient_color") + ":",
				gradientColor = new JComboBoxColorScheme(false));

		int colorCount = gradientColor.getSelectedColors().length;
		gradientIntervals.setMaxValue(colorCount);

		aux3.add(aux4);
		aux.add(aux2);
		myTab.add(aux);
		myTab.add(aux3);

		gradientIntervals.addActionListener(this);
		gradientPercentage.addActionListener(this);
		gradientAngle.addActionListener(this);
		gradientColor.addActionListener(this);
		gradientStyle.addActionListener(this);
		btnOutline.addActionListener(this);
		useBorder.addActionListener(this);

		tabs.add(myTab);
	}

	public EditorTool[] getEditorTools() {
		return null;
	}

	public ISymbol getLayer() {

		GradientFillSymbol layer = new GradientFillSymbol();

		layer.setHasOutline(useBorder.isSelected());
		outline = (ILineSymbol) btnOutline.getSymbol();
		layer.setOutline(outline);

		layer.setStyle(gradientStyle.getSelectedIndex());
		layer.setIntervals(gradientIntervals.getInteger());

		if (gradientStyle.getSelectedIndex() == 2) {
			gradientAngle.setEnabled(false);
		}
		layer.setAngle(gradientAngle.getDouble() * FConstant.DEGREE_TO_RADIANS);
		layer.setPercentage(gradientPercentage.getDouble());

		// ColorItem[] array=new ColorItem
		// [gradientColor.getSelectedColors().length];
		// array=gradientColor.getSelectedColors();
		// ColorItem[] selcolors = new ColorItem
		// [gradientColor.getSelectedColors().length];;
		//
		// for(int i=0;i<array.length;i++) {
		// selcolors[i].setColor(array[i].getColor());
		// }

		if (gradientColor.getSelectedColors() != null) {
			Color[] colors = new Color[gradientColor.getSelectedColors().length];
			for (int i = 0; i < colors.length; i++) {
				colors[i] = gradientColor.getSelectedColors()[i].getColor();
			}
			layer.setGradientColor(colors);
		}

		return layer;
	}

	public String getName() {
		return PluginServices.getText(GradientFill.class,
				"gradient_fill_symbol");
	}

	public Class getSymbolClass() {
		return GradientFillSymbol.class;
	}

	public JPanel[] getTabs() {
		return (JPanel[]) tabs.toArray(new JPanel[tabs.size()]);
	}

	public void refreshControls(ISymbol layer) {

		gradientStyle.removeActionListener(this);
		GradientFillSymbol sym = (GradientFillSymbol) layer;
		gradientStyle.setSelectedIndex(sym.getStyle());

		outline = sym.getOutline();
		btnOutline.setSymbol(outline);
		useBorder.setSelected(sym.hasOutline());

		int colorCount = (sym.getGradientColor().length);
		gradientIntervals.setMaxValue(colorCount);
		gradientIntervals.setInteger(sym.getIntervals());
		gradientAngle.setDouble(sym.getAngle() / FConstant.DEGREE_TO_RADIANS);
		gradientPercentage.setDouble(sym.getPercentage());

		if (sym.getGradientColor() != null) {
			ColorItem[] colors = new ColorItem[sym.getGradientColor().length];
			for (int i = 0; i < sym.getGradientColor().length; i++) {
				colors[i] = new ColorItem();
				colors[i].setColor(sym.getGradientColor()[i]);
			}
			gradientColor.setSelectedColors(colors);
		}

		gradientStyle.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		JComponent c = (JComponent) e.getSource();
		if (c.equals(gradientColor)) {

			gradientIntervals
					.setMaxValue(gradientColor.getSelectedColors().length);

			if (gradientIntervals.getInteger() > gradientColor
					.getSelectedColors().length)
				gradientIntervals
						.setInteger(gradientColor.getSelectedColors().length);
			else
				gradientIntervals.setInteger(gradientIntervals.getInteger());

		}
		if (c.equals(gradientStyle)) {

			if (gradientStyle.getSelectedIndex() == 2) {
				gradientAngle.setInteger(0);
				gradientAngle.setEnabled(false);
			} else
				gradientAngle.setEnabled(true);
		}

		outline = (ILineSymbol) btnOutline.getSymbol();
		fireSymbolChangedEvent();
	}

	/**
	 * Establishes the values for the JCombobox where the user can select the
	 * different styles for the gradient
	 * 
	 * @return
	 */
	private JComboBox getCmbgradientStyle() {
		if (gradientStyle == null) {
			gradientStyle = new JComboBox(new String[] {
					PluginServices.getText(this, "buffered"),
					PluginServices.getText(this, "lineal"),
					PluginServices.getText(this, "circular"),
					PluginServices.getText(this, "rectangular") });

		}

		return gradientStyle;
	}

}
