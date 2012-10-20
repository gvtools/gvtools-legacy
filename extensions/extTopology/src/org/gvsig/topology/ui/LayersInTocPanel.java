/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
/* CVS MESSAGES:
 *
 * $Id: 
 * $Log: 
 */
package org.gvsig.topology.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.gvsig.topology.Topology;
import org.gvsig.topology.ui.util.BoxLayoutPanel;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayersIterator;

/**
 * Panel which shows toc layers with a check box to select or unselect many of
 * them.
 * 
 * @author Alvaro Zabala
 * 
 */
public class LayersInTocPanel extends BoxLayoutPanel {

	private static final long serialVersionUID = 4381919048902679753L;

	/**
	 * Root layer of the toc
	 */
	FLayers rootOfToc;

	/**
	 * Contains all layers that must be checked in the control (because they
	 * have been selected yet)
	 */
	List<FLayer> alreadySelectedLyrs;

	/**
	 * Relates a layer with its checkbox in the component
	 */
	Map<FLayer, JCheckBox> lyr_checkbox;

	/**
	 * Constructor
	 * 
	 * @param rootOfToc
	 *            root of toc
	 */
	public LayersInTocPanel(FLayers rootOfToc, List<FLayer> alreadySelectedLyrs) {
		super();
		this.rootOfToc = rootOfToc;
		this.alreadySelectedLyrs = alreadySelectedLyrs;
		this.lyr_checkbox = new HashMap<FLayer, JCheckBox>();
		initialize();
	}

	public LayersInTocPanel(FLayers rootOfToc) {
		this(rootOfToc, null);
	}

	/**
	 * Processes al layers with the specified visitor
	 * 
	 * @param visitor
	 */
	private void iterate(ILayerVisitor visitor) {
		LayersIterator lyrIt = new LayersIterator(rootOfToc);
		while (lyrIt.hasNext()) {
			FLayer lyr = lyrIt.nextLayer();
			if (!lyr.isInTOC())
				continue;
			visitor.visit(lyr);
		}// while
	}

	private void initialize() {
		iterate(new ILayerVisitor() {
			public void visit(FLayer visitedLayer) {
				if (visitedLayer instanceof FLyrVect) {
					FLyrVect lyrVect = (FLyrVect) visitedLayer;
					JCheckBox checkBoxLyr = new JCheckBox(lyrVect.getName());
					if (alreadySelectedLyrs != null) {
						if (alreadySelectedLyrs.contains(lyrVect)) {
							checkBoxLyr.setSelected(true);
						}
					}

					// if a layer is already contained in a topology
					// it cant be selected
					if (lyrVect.getParentLayer() instanceof Topology) {
						checkBoxLyr.setEnabled(false);
						checkBoxLyr.setToolTipText(PluginServices.getText(this,
								"capa_ya_pertenece_a_topologia"));
					}

					lyr_checkbox.put(lyrVect, checkBoxLyr);
					addRow(new JComponent[] { checkBoxLyr }, 350, 20);
				}// if
			}
		});
	}

	/**
	 * Returns the layes which checkbox has been checked
	 * 
	 * @return
	 */
	public List<FLyrVect> getSelectedLyrs() {
		final ArrayList<FLyrVect> solution = new ArrayList<FLyrVect>();
		iterate(new ILayerVisitor() {
			public void visit(FLayer visitedLayer) {
				if (visitedLayer instanceof FLyrVect) {
					FLyrVect lyrVect = (FLyrVect) visitedLayer;
					JCheckBox checkBox = lyr_checkbox.get(lyrVect);
					if (checkBox.isSelected())
						solution.add(lyrVect);
				}
			}
		});
		return solution;
	}

	interface ILayerVisitor {
		public void visit(FLayer visitedLayer);
	}

}
