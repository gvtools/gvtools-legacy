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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.gui.beans.swing.JIncrementalNumberField;
import org.gvsig.symbology.fmap.symbols.MarkerLineSymbol;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.gui.styling.AbstractTypeSymbolEditor;
import com.iver.cit.gvsig.gui.styling.EditorTool;
import com.iver.cit.gvsig.gui.styling.SymbolEditor;
import com.iver.cit.gvsig.gui.styling.SymbolSelector;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ISymbolSelector;

/**
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class MarkerLine extends AbstractTypeSymbolEditor implements
		ActionListener {

	private ArrayList<JPanel> tabs = new ArrayList<JPanel>();
	private IMarkerSymbol marker = SymbologyFactory.createDefaultMarkerSymbol();
	private JButton btnChooseMarker;
	private JIncrementalNumberField incrSeparation;
	private JIncrementalNumberField incrWidth;

	public MarkerLine(SymbolEditor owner) {
		super(owner);
		initialize();
	}

	private void initialize() {
		// Marker line tab
		JPanel myTab = new JPanel();
		myTab.setName(PluginServices.getText(this, "marker_line"));

		GridBagLayoutPanel aux = new GridBagLayoutPanel();

		// width
		aux.addComponent(PluginServices.getText(this, "width") + ":",
				incrWidth = new JIncrementalNumberField(String.valueOf(1), 5,
						0.01, Double.MAX_VALUE, 1));
		incrWidth.setDouble(1);
		incrWidth.addActionListener(this);

		// separation
		aux.addComponent(PluginServices.getText(this, "separation") + ":",
				incrSeparation = new JIncrementalNumberField(String.valueOf(1),
						5, 0.01, Double.MAX_VALUE, 1));
		incrSeparation.setDouble(1);
		incrSeparation.addActionListener(this);
		btnChooseMarker = new JButton(PluginServices.getText(this,
				"choose_marker"));
		btnChooseMarker.addActionListener(this);
		aux.addComponent("", btnChooseMarker);
		myTab.add(aux);
		tabs.add(myTab);
	}

	public EditorTool[] getEditorTools() {
		// TODO Auto-generated method stub
		throw new Error("Not yet implemented!");
	}

	public ISymbol getLayer() {
		MarkerLineSymbol sym = new MarkerLineSymbol();
		sym.setMarker(marker);
		sym.setSeparation(incrSeparation.getDouble());
		sym.setLineWidth(incrWidth.getDouble());
		return sym;
	}

	public String getName() {
		return PluginServices.getText(this, "marker_line_symbol");
	}

	public Class getSymbolClass() {
		return MarkerLineSymbol.class;
	}

	public JPanel[] getTabs() {
		return (JPanel[]) tabs.toArray(new JPanel[tabs.size()]);
	}

	public void refreshControls(ISymbol layer) {
		MarkerLineSymbol sym = (MarkerLineSymbol) layer;
		marker = sym.getMarker();
		incrSeparation.setDouble(sym.getSeparation());
		incrWidth.setDouble(sym.getLineWidth());
	}

	public void actionPerformed(ActionEvent e) {
		JComponent comp = (JComponent) e.getSource();
		if (comp.equals(btnChooseMarker)) {
			ISymbolSelector symSelect = SymbolSelector.createSymbolSelector(
					marker, FShape.POINT);
			PluginServices.getMDIManager().addWindow(symSelect);
			IMarkerSymbol myMarker = (IMarkerSymbol) symSelect
					.getSelectedObject();

			if (myMarker == null)
				return;

			this.marker = myMarker;
		}
		fireSymbolChangedEvent();
	}

}
