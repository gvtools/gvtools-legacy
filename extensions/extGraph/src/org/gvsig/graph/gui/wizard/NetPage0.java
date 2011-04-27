/*
 * Created on 09-nov-2006
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
 * $Id: NetPage0.java 31524 2009-11-06 18:31:08Z fpenarrubia $
 * $Log$
 * Revision 1.9  2006-12-04 17:13:39  fjp
 * *** empty log message ***
 *
 * Revision 1.8  2006/11/13 20:41:08  azabala
 * *** empty log message ***
 *
 * Revision 1.7  2006/11/09 21:08:32  azabala
 * *** empty log message ***
 *
 *
 */
package org.gvsig.graph.gui.wizard;

import javax.swing.ButtonGroup;
import javax.swing.JEditorPane;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jwizardcomponent.JWizardPanel;

import org.gvsig.graph.gui.HTMLEditorKit;
import org.gvsig.gui.beans.swing.GridBagLayoutPanel;

import com.iver.andami.PluginServices;

/**
 * First page of the wizard.
 * It prevents user of pseudonodes in a linear layer, and offers
 * them to launch clean geoprocess to remove pseudonodes.
 * 
 * Also, it offers users to work with a snap tolerance in node definition.
 * 
 * @author alzabord
 *
 */
public class NetPage0 extends JWizardPanel  {
	private NetWizard owner;
	
	JRadioButton originalLayerBtn;
	JRadioButton cleanedLayerBtn;
	JRadioButton originalCoordinatesBtn;
	JRadioButton snappedCoordinatesBtn;
	JSpinner snapTolerance;
	
	NetPage0(NetWizard wizard) {
		super(wizard.getWizardComponents());
		this.owner = wizard;
		initialize();
	}
	
	
	public boolean cleanOriginalLayer(){
		return cleanedLayerBtn.isSelected();
	}
	
	public boolean applySnapTolerance(){
		return snappedCoordinatesBtn.isSelected();
	}
	
	public double getSnapTolerance(){
		if(applySnapTolerance())
			return ((Number) snapTolerance.getValue()).doubleValue();
		else
			return 0d;
	}
	
	
	 public void next() {
        super.next();
        owner.setCleanOriginalLayer(cleanOriginalLayer());
        boolean applySnap = applySnapTolerance();
        if(applySnap){
        	owner.setSnapTolerance(getSnapTolerance());
        }
	 	owner.setApplySnapTolerance(applySnap);  
    }
	

	private void initialize() {
		// this.setSize(new java.awt.Dimension(319,234));
		GridBagLayoutPanel contentPane = new GridBagLayoutPanel();
		
		JEditorPane htmlPanel = new JEditorPane();
		htmlPanel.setEditable(false);
		htmlPanel.setEditorKit(new HTMLEditorKit());
		String layerName = owner.getLayer().getName();
		String htmlText = PluginServices.getText(null, "netpage0_htmltext1") + 
		layerName + PluginServices.getText(null, "netpage0_htmltext2");
		htmlPanel.setText(htmlText);
		contentPane.addComponent(htmlPanel);
		
		
		contentPane.addBlank();
		contentPane.addBlank();
		contentPane.addBlank();
		
		originalLayerBtn = new JRadioButton(PluginServices.getText(this, "Calcular_la_red_sobre_la_capa_original"));
		originalLayerBtn.setSelected(true);
		
		cleanedLayerBtn = new JRadioButton(PluginServices.getText(this, "Aplicar_un_CLEAN_sobre_la_capa_original"));
		
		ButtonGroup cleanGroup = new ButtonGroup();
		cleanGroup.add(originalLayerBtn);
		cleanGroup.add(cleanedLayerBtn);
		
		contentPane.addComponent(originalLayerBtn);
		contentPane.addComponent(cleanedLayerBtn);
		
		contentPane.addBlank();
		contentPane.addBlank();
		contentPane.addBlank();
		
		originalCoordinatesBtn = new JRadioButton(PluginServices.getText(this, "Trabajar_con_las_coordenadas_originales"));
		originalCoordinatesBtn.setSelected(true);
		
		snappedCoordinatesBtn = new JRadioButton(PluginServices.getText(this, "Aplicar_tolerancia_de_snap"));
		SpinnerNumberModel spinnerModel =
			new SpinnerNumberModel(0.01, 0d, 100d, 0.01 );
		snapTolerance = new JSpinner(spinnerModel);
//		 Disable keyboard edits in the spinner
//		JFormattedTextField tf = ((JSpinner.DefaultEditor) snapTolerance
//				.getEditor()).getTextField();
//		tf.setEditable(false);
//		tf.setBackground(Color.white);
		               
		ButtonGroup snapGroup = new ButtonGroup();
		snapGroup.add(originalCoordinatesBtn);
		snapGroup.add(snappedCoordinatesBtn);
		
		contentPane.addComponent(originalCoordinatesBtn);
		contentPane.addComponent(snappedCoordinatesBtn, snapTolerance);
		
		
		this.add(contentPane);
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
