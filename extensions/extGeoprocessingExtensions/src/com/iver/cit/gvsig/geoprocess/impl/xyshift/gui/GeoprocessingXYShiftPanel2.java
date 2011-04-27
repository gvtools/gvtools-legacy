/*
 * Created on 31-jul-2006
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
* $Id: GeoprocessingXYShiftPanel2.java 6752 2006-08-11 17:21:45Z azabala $
* $Log$
* Revision 1.1  2006-08-11 17:17:55  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.xyshift.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessGridbagPanel;

public class GeoprocessingXYShiftPanel2 extends AbstractGeoprocessGridbagPanel {
	
	
	private JLabel offsetLabel;
	private JTextField xoffsetTextField;
	private JTextField yoffsetTextField;

	/**
	 * Constructor.
	 * 
	 */
	public GeoprocessingXYShiftPanel2(FLayers layers) {
		super(layers, PluginServices.
				getText(null, "XYShift._Introduccion_de_datos") + ":");
	}

	protected void addSpecificDesign() {
		Insets insets = new Insets(5, 5, 5, 5);
	    
		offsetLabel = new JLabel();
        offsetLabel.setText(PluginServices.
        		getText(this,"Introducir_valores_desplazamiento"));
        addComponent(offsetLabel, insets);
        
        xoffsetTextField = new JTextField(20);
		addComponent(PluginServices.getText(this,"xOffset")+":", 
				xoffsetTextField, GridBagConstraints.BOTH, insets);
		
		 yoffsetTextField = new JTextField(20);
			addComponent(PluginServices.getText(this,"yOffset")+":", 
					yoffsetTextField, GridBagConstraints.BOTH, insets);
			
		initSelectedItemsJCheckBox();
		updateNumSelectedFeaturesLabel();		
		
	}
	
	public double getXOffset() throws GeoprocessException{
		try {
			String strDist = xoffsetTextField.getText();
			return Double.parseDouble(strDist);
		} catch (NumberFormatException ex) {
			throw new GeoprocessException(
					"Offset en x introducido no numerico");
		}
	}
	
	public double getYOffset() throws GeoprocessException{
		try {
			String strDist = yoffsetTextField.getText();
			return Double.parseDouble(strDist);
		} catch (NumberFormatException ex) {
			throw new GeoprocessException(
					"Distancia de buffer introducida no numerica");
		}
	}

	protected void processLayerComboBoxStateChange(ItemEvent e) {
	}
	
	

}

