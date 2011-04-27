/*
 * Created on 09-ago-2006
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
* $Id: GeoProcessingSpatialJoinPanel2.java 12953 2007-08-07 16:09:50Z azabala $
* $Log$
* Revision 1.3  2007-08-07 16:09:50  azabala
* bug solved when a layer vect hasnt numeric fields
*
* Revision 1.2  2006/09/19 19:24:09  azabala
* fixed bug (sumarization function dialog showed always the same fields, first selected layer fields in combo box)
*
* Revision 1.1  2006/08/11 16:30:38  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.spatialjoin.gui;

import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.util.Map;

import javax.swing.JCheckBox;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.geoprocess.core.gui.GeoProcessingOverlayPanel2;
import com.iver.cit.gvsig.geoprocess.core.gui.SpatialJoinNumericFieldSelection;

public class GeoProcessingSpatialJoinPanel2 extends
		GeoProcessingOverlayPanel2 implements SpatialJoinPanelIF{

	
	private JCheckBox nearestCheckbox;
	
	private SpatialJoinNumericFieldSelection fieldDialog;
	
	private Map sumarizeFunctions;
	
	
	public GeoProcessingSpatialJoinPanel2(FLayers layers) {
		super(layers, PluginServices.
							getText(null,
							"Enlace_espacial._Introduccion_de_datos") +
							":");
	}
	protected void addSpecificDesign() {
		super.addSpecificDesign();
		Insets insets = new Insets(5, 5, 5, 5);
		nearestCheckbox = new JCheckBox();
		nearestCheckbox.setText(PluginServices.
				getText(this, "Obtener_mas_proximo"));
		addComponent(nearestCheckbox, insets);
	}

	
	public boolean isNearestSelected() {
		return nearestCheckbox.isSelected();
	}
	
	public FLyrVect getFirstLayer(){
		return this.getInputLayer();
	}

	public boolean openSumarizeFunction() {
		//if(fieldDialog == null){
			fieldDialog = new SpatialJoinNumericFieldSelection(getSecondLayer());
			fieldDialog.pack();
		//}
		fieldDialog.setSize(560,300);
		fieldDialog.setVisible(true);
		sumarizeFunctions = fieldDialog.getSumarizationFunctions();
		return fieldDialog.isOk();
	}
	/**
	 * Subclasses those want to overwrite logic of this component
	 * must overwrites this method
	 * @param e
	 */
	protected void processLayer2ComboBoxStateChange(ItemEvent e) {
		if(XTypes.getNumericFieldsNames(getSecondLayer()).length == 0){
			nearestCheckbox.setSelected(true);
			nearestCheckbox.setEnabled(false);
		}else{
			nearestCheckbox.setEnabled(true);
		}
	}

	

	public Map getSumarizeFunctions() {
		return sumarizeFunctions;
	}

}

