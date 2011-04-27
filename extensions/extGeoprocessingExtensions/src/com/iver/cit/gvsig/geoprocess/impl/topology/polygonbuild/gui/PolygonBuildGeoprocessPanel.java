/*
 * Created on 15-dic-2006
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
* $Id: PolygonBuildGeoprocessPanel.java 21235 2008-06-05 14:08:38Z azabala $
* $Log$
* Revision 1.1  2006-12-21 17:23:27  azabala
* *** empty log message ***
*
* Revision 1.2  2006/12/19 19:29:50  azabala
* *** empty log message ***
*
* Revision 1.1  2006/12/15 19:06:29  azabala
* scheleton of polygon build
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.topology.polygonbuild.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessGridbagPanel;
import com.iver.cit.gvsig.geoprocess.impl.topology.polygonbuild.IPolygonBuildGeoprocessUserEntries;

public class PolygonBuildGeoprocessPanel extends AbstractGeoprocessGridbagPanel
	implements IPolygonBuildGeoprocessUserEntries{

	private JCheckBox snapToleranceCb;
	private JTextField snapToleranceTf;
	
	private JCheckBox dangleToleranceCb;
	private JTextField dangleToleranceTf;
	
	
	private JCheckBox previousCleanCb;
	
	
	private JCheckBox addGroupOfLyrsCb;
	
	public PolygonBuildGeoprocessPanel(FLayers layers) {
		super(layers, PluginServices.getText(null, "Build_de_poligonos"));
	}

	public boolean applySnapTolerance(){
		return snapToleranceCb.isSelected();
	}
	
	public double getSnapTolerance() throws GeoprocessException{
		try {
			String strDist = this.snapToleranceTf.getText();
			return Double.parseDouble(strDist);
		} catch (NumberFormatException ex) {
			throw new GeoprocessException(
					"Tolerancia de snap introducida no numerica");
		}
	}
	
	public boolean applyDangleTolerance(){
		return dangleToleranceCb.isSelected();
	}
	
	public double getDangleTolerance() throws GeoprocessException{
		try {
			String strDist = this.dangleToleranceTf.getText();
			return Double.parseDouble(strDist);
		} catch (NumberFormatException ex) {
			throw new GeoprocessException(
					"Tolerancia de dangle introducida no numerica");
		}
	}
	
	public boolean computeCleanBefore(){
		return previousCleanCb.isSelected();
	}
	
	public boolean createLyrsWithErrorGeometries(){
		return addGroupOfLyrsCb.isSelected();
	}
	
	
	protected void addSpecificDesign() {
		Insets insets = new Insets(5, 5, 5, 5);
		
		//snap tolerance
		this.snapToleranceCb = new JCheckBox();
		this.snapToleranceCb.setText(PluginServices.getText(this,
				"Aplicar_tolerancia_de_snap"));
		this.snapToleranceTf = new JTextField(20);
		this.snapToleranceTf.setEnabled(false);
		this.snapToleranceCb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				snapToleranceTf.setEnabled(snapToleranceCb.isSelected());
				
			}});
		addComponent(snapToleranceCb, 
				snapToleranceTf,
				GridBagConstraints.NONE, 
				insets);
		
		//dangle tolerance
		this.dangleToleranceCb = new JCheckBox();
		this.dangleToleranceCb.setText(PluginServices.getText(this,
				"Aplicar_tolerancia_de_dangles"));
		this.dangleToleranceTf = new JTextField(20);
		this.dangleToleranceTf.setEnabled(false);
		this.dangleToleranceCb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				dangleToleranceTf.setEnabled(dangleToleranceCb.isSelected());
				
			}});
		addComponent(dangleToleranceCb, 
				dangleToleranceTf,
				GridBagConstraints.NONE, 
				insets);
		//clean previous
		this.previousCleanCb = new JCheckBox();
		this.previousCleanCb.setText(PluginServices.getText(this,
		"Limpiar_topologicamente_la_capa_de_entrada"));
		addComponent(previousCleanCb, 
				GridBagConstraints.NONE, 
				insets);
		
		//add dangles to toc
		this.addGroupOfLyrsCb = new JCheckBox();
		this.addGroupOfLyrsCb.setText(PluginServices.getText(this,
				"Añadir_al_TOC_geometrias_erroneas"));
		addComponent(addGroupOfLyrsCb, 
				GridBagConstraints.NONE, 
				insets);
		//FORCE THIS IN ALL GRIDBAGPANEL IMPLEMENTATIONS
		initSelectedItemsJCheckBox();
		updateNumSelectedFeaturesLabel();
	}

	protected void processLayerComboBoxStateChange(ItemEvent e) {
	}

}

