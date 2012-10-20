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
package com.iver.cit.gvsig.geoprocess.impl.smooth.gui;

import java.awt.event.ItemEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessGridbagPanel;
import com.iver.cit.gvsig.geoprocess.impl.smooth.ISmoothGeoprocessUserEntries;

public class SmoothGeoprocessPanel extends AbstractGeoprocessGridbagPanel
		implements ISmoothGeoprocessUserEntries {

	/*
	 * copiado de SmoothGeometry. TODO REDISEÑAR
	 */
	static String[] curveTypesNames = new String[] {
			PluginServices.getText(null, "BEZIER"),
			PluginServices.getText(null, "B_SPLINE"),
			PluginServices.getText(null, "CARDINAL_SPLINE"),
			PluginServices.getText(null, "CATMULLROM_SPLINE"),
			PluginServices.getText(null, "CUBIC_BSPLINE"),
			PluginServices.getText(null, "LAGRANGE_CURVE"),
			PluginServices.getText(null, "NATURAL_CUBIC_SPLINE"),
			PluginServices.getText(null, "NURB_SPLINE") };;

	private static final long serialVersionUID = 4103406609252667142L;

	private JComboBox curveOptionJb;

	public SmoothGeoprocessPanel(FLayers arg0) {
		super(arg0, PluginServices.getText(null, "Smooth_Lines"));
	}

	JComboBox getCurveTypesCb() {
		if (curveOptionJb == null) {
			curveOptionJb = new JComboBox();
			DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(
					curveTypesNames);
			curveOptionJb.setModel(defaultModel);
		}
		return curveOptionJb;
	}

	protected void addSpecificDesign() {
		curveOptionJb = getCurveTypesCb();

		addComponent(PluginServices.getText(this, "CURVE_TYPE"), curveOptionJb);

		initSelectedItemsJCheckBox();
		updateNumSelectedFeaturesLabel();
	}

	protected void processLayerComboBoxStateChange(ItemEvent arg0) {
	}

	public boolean onlyFirstLayerSelected() {
		return isFirstOnlySelected();
	}

	public int getCurveOption() {
		return getCurveTypesCb().getSelectedIndex();
	}
}
