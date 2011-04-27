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
package com.iver.cit.gvsig.geoprocess.impl.voronoi.gui;

import java.awt.event.ItemEvent;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;

import org.gvsig.jts.voronoi.Voronoier;
import org.gvsig.jts.voronoi.Voronoier.VoronoiStrategy;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessGridbagPanel;
import com.iver.cit.gvsig.geoprocess.impl.voronoi.IVoronoiGeoprocessUserEntries;

public class VoronoiGeoprocessPanel extends AbstractGeoprocessGridbagPanel 
 								implements IVoronoiGeoprocessUserEntries{

	private static final long serialVersionUID = 6485409632799083097L;
	
	private JRadioButton tinRadioButton;
	
	private JRadioButton thiessenRadioButton;

	private JComboBox algorithmCb;
	

	public VoronoiGeoprocessPanel(FLayers arg0) {
		super(arg0, PluginServices.getText(null, "Voronoi"));
	}

	protected void addSpecificDesign() {
		tinRadioButton = getTinRadioButton();
		
		addComponent(tinRadioButton);
		thiessenRadioButton = getThiessenRadioButton();
		addComponent(thiessenRadioButton);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(tinRadioButton);
		buttonGroup.add(thiessenRadioButton);
		tinRadioButton.setSelected(true);
		
		algorithmCb = getAlgorithmCb();
		addComponent(algorithmCb);
		
		initSelectedItemsJCheckBox();
		updateNumSelectedFeaturesLabel();
	}

	protected void processLayerComboBoxStateChange(ItemEvent arg0) {
	}
	
	private JRadioButton getTinRadioButton() {
		if (tinRadioButton == null) {
			tinRadioButton = new JRadioButton();
			tinRadioButton.setText(PluginServices.getText(this,
					"Calcular_Tin")
					+ ":");
//			tinRadioButton.setBounds(new java.awt.Rectangle(2, 41,
//					287, 21));
		}
		return tinRadioButton;
	}
	
	private JRadioButton getThiessenRadioButton() {
		if (thiessenRadioButton == null) {
			thiessenRadioButton = new JRadioButton();
			thiessenRadioButton.setText(PluginServices.getText(this,
					"Calcular_Thiessen")
					+ ":");
//			thiessenRadioButton.setBounds(new java.awt.Rectangle(2, 10,
//					303, 24));
		}
		return thiessenRadioButton;
	}
	
	private JComboBox getAlgorithmCb(){
		if(algorithmCb == null){
			algorithmCb = new JComboBox();
			Iterator<VoronoiStrategy> it = Voronoier.getRegisteredAlgorithms().iterator();
			while(it.hasNext()){
				algorithmCb.addItem(it.next());
			}
		}
		return algorithmCb;
	}

	public boolean computeThiessen() {
		return getThiessenRadioButton().isSelected();
	}

	public boolean computeTin() {
		return getTinRadioButton().isSelected();
	}

	public boolean onlyFirstLayerSelected() {
		return isFirstOnlySelected();
	}

	public VoronoiStrategy getAlgorithm() {
		return (VoronoiStrategy) getAlgorithmCb().getSelectedItem();
	}

}

