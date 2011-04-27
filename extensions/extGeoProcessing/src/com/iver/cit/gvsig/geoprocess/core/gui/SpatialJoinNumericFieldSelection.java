/*
 * Created on 01-mar-2006
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
 * $Id: SpatialJoinNumericFieldSelection.java 10626 2007-03-06 16:55:54Z caballero $
 * $Log$
 * Revision 1.4  2007-03-06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.3  2006/08/11 16:12:27  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/06/20 18:19:43  azabala
 * refactorización para que todos los nuevos geoprocesos cuelguen del paquete impl
 *
 * Revision 1.1  2006/05/24 21:13:09  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.4  2006/04/07 19:00:58  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/03/26 20:02:08  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/03/23 21:02:37  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/03/05 19:56:06  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.gvsig.gui.beans.AcceptCancelPanel;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

public class SpatialJoinNumericFieldSelection 
	extends JDialog implements SpatialJoinNumFieldIF {

	private static final long serialVersionUID = -9031708821779458450L;
	
	private FLyrVect inputLayer;
	private JPanel jContentPane;
	private JPanel acceptCancelPanel;
	private boolean ok = false;
	/**
	 * This is the default constructor
	 */
	public SpatialJoinNumericFieldSelection(FLyrVect inputLayer) {
		super((JFrame)PluginServices.getMainFrame(), true);
		this.inputLayer =  inputLayer; 
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle(PluginServices.getText(this,"Funciones_Sumarizacion"));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getJContentPane(), BorderLayout.CENTER);
		getContentPane().add(getAcceptCancelPanel(), BorderLayout.SOUTH);
		
	}
	
	private JPanel getAcceptCancelPanel() {
		if (acceptCancelPanel == null) {
			acceptCancelPanel = new AcceptCancelPanel();
			((AcceptCancelPanel) acceptCancelPanel).setOkButtonActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					ok();

				}});
			((AcceptCancelPanel) acceptCancelPanel).setCancelButtonActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent arg0) {
					setVisible(false);
				}});
		}
		return acceptCancelPanel;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new NumericFieldFunctionsControl(inputLayer);
		}
		return jContentPane;
	}

	public void ok() {
		ok = true;
		setVisible(false);
	}

	public Map getSumarizationFunctions() {
		return ((NumericFieldFunctionsControl)jContentPane).getFieldFunctionMap();
	}

	public boolean isOk() {
		return ok;
	}


}  //  @jve:decl-index=0:visual-constraint="10,10"
