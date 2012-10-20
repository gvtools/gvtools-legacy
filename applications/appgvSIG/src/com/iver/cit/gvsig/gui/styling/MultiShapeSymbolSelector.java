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
package com.iver.cit.gvsig.gui.styling;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.gvsig.gui.beans.AcceptCancelPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.symbols.IFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.MultiShapeSymbol;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ISymbolSelector;

/**
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class MultiShapeSymbolSelector extends JPanel implements ISymbolSelector {
	private SymbolSelector markerSelector;
	private SymbolSelector lineSelector;
	private SymbolSelector fillSelector;
	private WindowInfo wi;
	private JTabbedPane tabbedPane;
	private MultiShapeSymbol sym;
	protected AcceptCancelPanel okCancelPanel;
	protected boolean accepted = false;

	MultiShapeSymbolSelector(Object currSymbol) {
		sym = (MultiShapeSymbol) currSymbol;
		markerSelector = (SymbolSelector) SymbolSelector.createSymbolSelector(
				sym.getMarkerSymbol(), FShape.POINT, false);

		lineSelector = (SymbolSelector) SymbolSelector.createSymbolSelector(
				sym.getLineSymbol(), FShape.LINE, false);
		fillSelector = (SymbolSelector) SymbolSelector.createSymbolSelector(
				sym.getFillSymbol(), FShape.POLYGON, false);
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		add(getJTabbedPane(), BorderLayout.CENTER);

		ActionListener okAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accepted = true;
				PluginServices.getMDIManager().closeWindow(
						MultiShapeSymbolSelector.this);
			}
		}, cancelAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accepted = false;
				// ((SymbolPreviewer) jPanelPreview).setSymbol(null);
				PluginServices.getMDIManager().closeWindow(
						MultiShapeSymbolSelector.this);

			}
		};
		okCancelPanel = new AcceptCancelPanel();
		okCancelPanel.setOkButtonActionListener(okAction);
		okCancelPanel.setCancelButtonActionListener(cancelAction);

		this.add(okCancelPanel, BorderLayout.SOUTH);
	}

	private JTabbedPane getJTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.addTab(PluginServices.getText(this, "marker"),
					markerSelector);
			tabbedPane.addTab(PluginServices.getText(this, "line"),
					lineSelector);
			tabbedPane.addTab(PluginServices.getText(this, "fill"),
					fillSelector);
			tabbedPane.setPreferredSize(getWindowInfo().getMinimumSize());
		}

		return tabbedPane;
	}

	public Object getSelectedObject() {
		MultiShapeSymbol symSel = new MultiShapeSymbol();
		if (accepted) {
			symSel.setMarkerSymbol((IMarkerSymbol) markerSelector
					.getSelectedObject());
			symSel.setLineSymbol((ILineSymbol) lineSelector.getSelectedObject());
			symSel.setFillSymbol((IFillSymbol) fillSelector.getSelectedObject());
		} else {
			symSel.setMarkerSymbol(sym.getMarkerSymbol());
			symSel.setLineSymbol(sym.getLineSymbol());
			symSel.setFillSymbol(sym.getFillSymbol());
		}
		return symSel;
	}

	public void setSymbol(Object symbol) {
		MultiShapeSymbol sym = (MultiShapeSymbol) symbol;
		markerSelector.setSymbol(sym.getMarkerSymbol());
		lineSelector.setSymbol(sym.getLineSymbol());
		fillSelector.setSymbol(sym.getFillSymbol());
	}

	public WindowInfo getWindowInfo() {
		if (wi == null) {
			wi = new WindowInfo(WindowInfo.MODALDIALOG | WindowInfo.RESIZABLE);
			wi.setWidth(706);
			wi.setHeight(500);
			wi.setTitle(PluginServices.getText(this, "symbol_selector"));
		}
		return wi;
	}

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

}
