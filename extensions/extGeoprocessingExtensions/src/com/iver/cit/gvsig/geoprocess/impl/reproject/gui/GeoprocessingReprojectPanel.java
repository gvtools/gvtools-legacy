/*
 * Created on 30-jun-2006
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
* $Id: GeoprocessingReprojectPanel.java 13881 2007-09-19 16:22:04Z jaume $
* $Log$
* Revision 1.9  2007-09-19 16:09:14  jaume
* removed unnecessary imports
*
* Revision 1.8  2007/02/28 13:32:12  azabala
* added changes of V10 to allow compatibility of jcrs with other modules (geoprocessing, etc.)
*
* Revision 1.7  2006/10/17 08:22:19  jmvivo
* Sincronizacion de los cambios de Luis hechos en el branch v10 (basado en informe de historico de cvs con fecha >= 19/09/2006).
*
* Revision 1.6  2006/09/21 18:14:42  azabala
* changes of appGvSig packages (document extensibility)
*
* Revision 1.5  2006/08/29 08:46:36  cesar
* Rename the remaining method calls (extGeoprocessingExtensions was not in my workspace)
*
* Revision 1.4  2006/08/11 17:20:32  azabala
* *** empty log message ***
*
* Revision 1.2  2006/07/04 16:43:18  azabala
* *** empty log message ***
*
* Revision 1.1  2006/07/03 20:28:56  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.reproject.gui;

import java.awt.Insets;
import java.awt.event.ItemEvent;

import javax.swing.JLabel;

import org.cresques.cts.IProjection;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessGridbagPanel;
import com.iver.cit.gvsig.gui.panels.CRSSelectPanel;

public class GeoprocessingReprojectPanel extends AbstractGeoprocessGridbagPanel {
	/**
	 * projection of the input layer
	 */
	private IProjection inputLayerProjection;
	/**
	 * projection of the target layer
	 */
	private IProjection targetLayerProjection;
	
	/**
	 * label to show input projection name
	 */
	private JLabel inputProjectionLabel;
	
	/**
	 * label to show target projection name
	 */
	/**
	 * label to show target projection name
	 */
	private CRSSelectPanel targetPanelProj;
	
	/**
	 * Constructor.
	 * 
	 */
	public GeoprocessingReprojectPanel(FLayers layers) {
		super(layers, 
			PluginServices.getText(null, 
					"Reproyeccion._Introduccion_de_datos"));
	}

	protected void addSpecificDesign() {
		Insets insets = new Insets(5, 5, 5, 5);
		inputLayerProjection = getInputLayer().getProjection();
		targetLayerProjection = CRSFactory.getCRS("EPSG:23030");
		String inputProjectionText = PluginServices.
								getText(this, "Proyeccion_Actual")+
								":  "+
								inputLayerProjection.getAbrev();
		inputProjectionLabel = new JLabel(inputProjectionText);
		addComponent(inputProjectionLabel,  insets);
		
		
//		String targetProjectionText = PluginServices.
//						getText(this, "Proyeccion_Destino")+
//										":  " +
//									targetLayerProjection.getAbrev();
//		targetProjectionLabel = new JLabel(targetProjectionText);
//		addComponent(targetProjectionLabel, 
//				getOpenProjectionDialogButton(),
//				GridBagConstraints.REMAINDER,
//				insets);
		addComponent(getTargetPanelProj());
		initSelectedItemsJCheckBox();
		updateNumSelectedFeaturesLabel();
	}
	
	private CRSSelectPanel getTargetPanelProj() {
		if (targetPanelProj == null) {
			targetPanelProj = CRSSelectPanel.getPanel(targetLayerProjection);
			targetPanelProj.getJLabel().setText(PluginServices.
						getText(this, "Proyeccion_Destino"));
			targetPanelProj.setPreferredSize(new java.awt.Dimension(330,35));
			targetPanelProj.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (targetPanelProj.isOkPressed()) {
						targetLayerProjection = targetPanelProj.getCurProj();
					}
				}
			});
		}
		//azabala: added this for compability with JCRS
		targetPanelProj.setTransPanelActive(true);
		return targetPanelProj;
	}
	
//	private JButton getOpenProjectionDialogButton(){
//		JButton solution = new JButton("...");
//		solution.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent arg0) {
//				CSSelectionDialog csSelect = new CSSelectionDialog();
//				csSelect.setProjection(targetLayerProjection);
//		        PluginServices.getMDIManager().addWindow(csSelect);
//		        if (csSelect.isOkPressed()) {
//		        	targetLayerProjection = 
//		        		csSelect.getProjection();
//		        	String targetProjectionText = PluginServices.
//					getText(this, "Proyeccion_Destino")+
//									":  " +
//								targetLayerProjection.getAbrev();
//		        	targetProjectionLabel.
//		        		setText(targetProjectionText);
//		        }
//			}});
//		return solution;
//	}

	/**
	 * This method processes selection events in layer combo box
	 * (in this case, modify the projection to show the projection
	 * of the selected layer)
	 */
	protected void processLayerComboBoxStateChange(ItemEvent e) {
		FLyrVect inputLyr = getInputLayer();
		inputLayerProjection = inputLyr.getProjection();
		String inputProjectionText = PluginServices.
		getText(this, "Proyeccion_Actual")+
		":  "+
		inputLayerProjection.getAbrev();
		
		inputProjectionLabel.setText(inputProjectionText);
	}
	
	public IProjection getTargetProjection(){
		return targetLayerProjection;
	}


}  //  @jve:decl-index=0:visual-constraint="10,10"

