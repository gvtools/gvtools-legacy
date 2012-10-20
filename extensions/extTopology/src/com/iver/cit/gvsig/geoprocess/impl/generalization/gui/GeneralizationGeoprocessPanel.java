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
package com.iver.cit.gvsig.geoprocess.impl.generalization.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessGridbagPanel;
import com.iver.cit.gvsig.geoprocess.impl.generalization.IGeneralizationGeoprocessUserEntries;

public class GeneralizationGeoprocessPanel extends
		AbstractGeoprocessGridbagPanel implements
		IGeneralizationGeoprocessUserEntries {

	private static final long serialVersionUID = 4103406609252667142L;

	private JRadioButton douglasPeuckerRadioButton;

	private JRadioButton topologyPreservingRadioButton;

	private JTextField distToleranceTextField;

	public GeneralizationGeoprocessPanel(FLayers arg0) {
		super(arg0, PluginServices.getText(null, "Generalization"));
	}

	protected void addSpecificDesign() {
		douglasPeuckerRadioButton = getDouglasPeuckerRadioButton();

		addComponent(douglasPeuckerRadioButton);

		topologyPreservingRadioButton = getTopologyPreservingRadioButton();
		addComponent(topologyPreservingRadioButton);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(douglasPeuckerRadioButton);
		buttonGroup.add(topologyPreservingRadioButton);
		douglasPeuckerRadioButton.setSelected(true);

		JPanel aux = new JPanel(new BorderLayout());
		String text = PluginServices.getText(this, "distTolerance") + ":";
		distToleranceTextField = getDistToleranceTextField();
		aux.add(distToleranceTextField, BorderLayout.WEST);

		addComponent(text, aux, GridBagConstraints.HORIZONTAL, new Insets(5, 5,
				5, 5));

		initSelectedItemsJCheckBox();
		updateNumSelectedFeaturesLabel();
	}

	private JTextField getDistToleranceTextField() {
		if (this.distToleranceTextField == null) {
			this.distToleranceTextField = new JTextField(15);
		}
		return distToleranceTextField;
	}

	protected void processLayerComboBoxStateChange(ItemEvent arg0) {
	}

	private JRadioButton getDouglasPeuckerRadioButton() {
		if (douglasPeuckerRadioButton == null) {
			douglasPeuckerRadioButton = new JRadioButton();
			douglasPeuckerRadioButton.setText(PluginServices.getText(this,
					"Metodo_Douglas_Peucker") + ":");
		}
		return douglasPeuckerRadioButton;
	}

	private JRadioButton getTopologyPreservingRadioButton() {
		if (topologyPreservingRadioButton == null) {
			topologyPreservingRadioButton = new JRadioButton();
			topologyPreservingRadioButton.setText(PluginServices.getText(this,
					"Metodo_topology_preserving") + ":");
		}
		return topologyPreservingRadioButton;
	}

	public boolean computeDouglasPeucker() {
		return douglasPeuckerRadioButton.isSelected();
	}

	public boolean computeTopologyPreservingSimplify() {
		return topologyPreservingRadioButton.isSelected();
	}

	public double getDistTolerance() throws GeoprocessException {
		try {
			String strDist = this.distToleranceTextField.getText();
			return Double.parseDouble(strDist);
		} catch (NumberFormatException ex) {
			throw new GeoprocessException(
					"Distancia de tolerancia introducida no numérica");
		}
	}

	public boolean onlyFirstLayerSelected() {
		return isFirstOnlySelected();
	}
}
