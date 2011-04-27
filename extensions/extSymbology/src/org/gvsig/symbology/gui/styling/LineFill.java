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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JIncrementalNumberField;
import org.gvsig.symbology.fmap.symbols.LineFillSymbol;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.gui.panels.ColorChooserPanel;
import com.iver.cit.gvsig.gui.styling.AbstractTypeSymbolEditor;
import com.iver.cit.gvsig.gui.styling.EditorTool;
import com.iver.cit.gvsig.gui.styling.SymbolEditor;
import com.iver.cit.gvsig.project.documents.view.legend.gui.JSymbolPreviewButton;

/**
* <b>LineFill</b> allows to store and modify the properties that fills a
* polygon with a padding composed of lines<p>
* <p>
* This functionality is carried out thanks to a tab (line fill)which is included
* in the panel to edit the properities of a symbol (SymbolEditor)how is explained
* in AbstractTypeSymbolEditor.<p>
* <p>
* This tab permits the user to change the line that composes the padding
* (<b>btnLineSymbol</b>),the outline of the polygon (<b>btnOutlineSymbol</b>),
* the color between the lines (<b>jcc</b>),the angle of the line(<b>incrAngle</b>),
* the offset between paralel lines(<b>incrOffset</b>) and the separation
* (<b>incrSeparation</b>).
*
*
*@see AbstractTypeSymbolEditor
*@author jaume dominguez faus - jaume.dominguez@iver.es
*/
public class LineFill extends AbstractTypeSymbolEditor implements ActionListener {
	private ArrayList<JPanel> tabs = new ArrayList<JPanel>();
	private ColorChooserPanel jcc;
	private JIncrementalNumberField incrAngle;
	private JIncrementalNumberField incrOffset;
	private JIncrementalNumberField incrSeparation;
	private JSymbolPreviewButton btnOutlineSymbol;
	private JSymbolPreviewButton btnLineSymbol;
	private ILineSymbol outline;
	private JCheckBox useBorder;
	private ILineSymbol line = SymbologyFactory.createDefaultLineSymbol();
	public LineFill(SymbolEditor owner) {
		super(owner);
		initialize();
	}

	/**
	 * Initializes the parameters that allows the user to fill the padding of
	 * a polygon with a simpleline style.To do it, a tab (line fill) is created inside the
	 * SymbolEditor panel with default values for the different attributes.
	 */

	private void initialize() {

		GridBagLayoutPanel content = new GridBagLayoutPanel();
		content.setName(PluginServices.getText(this, "line_fill"));
		JPanel aux;
		aux = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		aux.add(jcc = new ColorChooserPanel(true,true));
		jcc.setAlpha(255);
		content.addComponent(PluginServices.getText(this, "color")+":",
				aux);

		aux = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		aux.add(incrAngle = new JIncrementalNumberField("0", 5));
		content.addComponent(PluginServices.getText(this, "angle")+":",
				aux);

		aux = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		aux.add(incrOffset = new JIncrementalNumberField("0", 5));
		content.addComponent(PluginServices.getText(this, "offset")+":",
				aux);

		aux = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		aux.add(incrSeparation = new JIncrementalNumberField(
				"5",
				5,
				1,
				Double.POSITIVE_INFINITY,
				1));
		incrSeparation.setDouble(5);
		content.addComponent(PluginServices.getText(this, "separation")+":",
				aux);

		aux = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		aux.add(btnLineSymbol = new JSymbolPreviewButton(FShape.LINE));
		btnLineSymbol.setPreferredSize(new Dimension(100, 35));
		content.addComponent(PluginServices.getText(this, "line")+":", aux);




		aux = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		aux.add(btnOutlineSymbol = new JSymbolPreviewButton(FShape.LINE));
		btnOutlineSymbol.setPreferredSize(new Dimension(100, 35));
		useBorder = new JCheckBox(PluginServices.getText(this, "use_outline"));
		content.addComponent(useBorder);
		content.addComponent(PluginServices.getText(this, "outline")+":", aux);

		jcc.addActionListener(this);
		incrAngle.addActionListener(this);
		incrOffset.addActionListener(this);
		incrSeparation.addActionListener(this);
		btnOutlineSymbol.addActionListener(this);
		btnLineSymbol.addActionListener(this);
		useBorder.addActionListener(this);

		tabs.add(content);

	}

	public EditorTool[] getEditorTools() {
		return null;
	}

	public ISymbol getLayer() {
		LineFillSymbol sym = new LineFillSymbol();
		sym.setAngle(incrAngle.getDouble()*FConstant.DEGREE_TO_RADIANS);
		sym.setOffset(incrOffset.getDouble());
		sym.setSeparation(incrSeparation.getDouble());

		sym.setHasFill(jcc.getUseColorisSelected());
		Color c = jcc.getColor();
		if (c != null)
			c = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());

		sym.setFillColor(c);


		sym.setHasOutline(useBorder.isSelected());
		outline = (ILineSymbol) btnOutlineSymbol.getSymbol();
		sym.setOutline(outline);


		sym.setLineSymbol(line);

		return sym;
	}

	public String getName() {
		return PluginServices.getText(this, "line_fill_symbol");
	}

	public Class<? extends ISymbol> getSymbolClass() {
		return LineFillSymbol.class;
	}

	public JPanel[] getTabs() {
		return (JPanel[]) tabs.toArray(new JPanel[tabs.size()]);
	}

	public void refreshControls(ISymbol layer) {
		LineFillSymbol lfs = (LineFillSymbol) layer;
		incrAngle.setDouble(lfs.getAngle()/FConstant.DEGREE_TO_RADIANS);
		incrOffset.setDouble(lfs.getOffset());
		incrSeparation.setDouble(lfs.getSeparation());
		jcc.setUseColorIsSelected(lfs.hasFill());
		jcc.setColor(lfs.getFillColor());

		outline=lfs.getOutline();
		btnOutlineSymbol.setSymbol(outline);
		useBorder.setSelected(lfs.hasOutline());

		line = lfs.getLineSymbol();
		btnLineSymbol.setSymbol(line);

	}

	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();

		line = (ILineSymbol) btnLineSymbol.getSymbol();
		outline = (ILineSymbol) btnOutlineSymbol.getSymbol();
		fireSymbolChangedEvent();
	}

}