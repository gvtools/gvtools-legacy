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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import org.gvsig.gui.beans.swing.GridBagLayoutPanel;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IPlacementConstraints;

public class DuplicateLayersMode extends GridBagLayoutPanel {
	private static final long serialVersionUID = 1091963463412809652L;
	private JRadioButton rdBtnRemoveDuplicates;
	private JRadioButton rdBtnOnePerFeature;
	private JRadioButton rdBtnOnePerFeaturePart;
	
	public DuplicateLayersMode() {
		super();
		initialize();
	}
	private void initialize() {
		setBorder(BorderFactory.
				createTitledBorder(null,
						PluginServices.getText(this, "duplicate_labels")));
		addComponent(getRdBtnRemoveDuplicates());
		addComponent(getRdBtnOnePerFeature());
		addComponent(getRdBtnOnePerFeaturePart());

		ButtonGroup group = new ButtonGroup();
		group.add(getRdBtnOnePerFeature());
		group.add(getRdBtnOnePerFeaturePart());
		group.add(getRdBtnRemoveDuplicates());
	}
	

	private JRadioButton getRdBtnOnePerFeaturePart() {
		if (rdBtnOnePerFeaturePart == null) {
			rdBtnOnePerFeaturePart = new JRadioButton(
				PluginServices.getText(this, "place_one_label_per_feature_part"));
		}
		return rdBtnOnePerFeaturePart;
	}

	private JRadioButton getRdBtnOnePerFeature() {
		if (rdBtnOnePerFeature == null) {
			rdBtnOnePerFeature = new JRadioButton(
				PluginServices.getText(this, "place_one_label_per_feature"));
		}
		return rdBtnOnePerFeature;
	}

	private JRadioButton getRdBtnRemoveDuplicates() {
		if (rdBtnRemoveDuplicates == null) {
			rdBtnRemoveDuplicates = new JRadioButton(
				PluginServices.getText(this, "remove_duplicate_labels"));

		}
		return rdBtnRemoveDuplicates;
	}
	
	public void setMode(int dupMode) {
		rdBtnRemoveDuplicates.setSelected(dupMode == IPlacementConstraints.REMOVE_DUPLICATE_LABELS);
		rdBtnOnePerFeature.setSelected(dupMode == IPlacementConstraints.ONE_LABEL_PER_FEATURE);
		rdBtnOnePerFeaturePart.setSelected(dupMode == IPlacementConstraints.ONE_LABEL_PER_FEATURE_PART);
	}
	
	public int getMode() {
		if (rdBtnRemoveDuplicates.isSelected()) {
			return IPlacementConstraints.REMOVE_DUPLICATE_LABELS;
		}
		if (rdBtnOnePerFeature.isSelected()) {
			return IPlacementConstraints.ONE_LABEL_PER_FEATURE;
		}
		if (rdBtnOnePerFeaturePart.isSelected()) {
			return IPlacementConstraints.ONE_LABEL_PER_FEATURE_PART;
		}
		
		throw new Error("Unsupported layer duplicates mode");
	}
}
