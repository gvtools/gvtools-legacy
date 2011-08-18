/*
 * Created on 22-jun-2005
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
package com.iver.cit.gvsig.sde;

import java.io.File;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.About;
import com.iver.cit.gvsig.SingleVectorialDBConnectionExtension;
import com.iver.cit.gvsig.fmap.drivers.sde.ArcSdeDriver;
import com.iver.cit.gvsig.fmap.drivers.sde.ConnectionSDE;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.gui.panels.FPanelAbout;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;


/**
 * SDE Extension.
 *
 * @author Vicente Caballero Navarro
 */
public class ExtSDE extends Extension {
    public void execute(String actionCommand) {
    }

    public boolean isEnabled() {
        return false;
    }

    public boolean isVisible() {
        return false;
    }

    public void initialize() {
    	ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
    	extensionPoints.add("databaseconnections",ConnectionSDE.class.toString(),ConnectionSDE.class);
//		System.out.println("Añado Wizard SDE.");
//        AddLayer.addWizard(com.iver.cit.gvsig.sde.gui.sdewizard2.WizardSDE.class);
    }

	public void postInitialize() {
		About about=(About)PluginServices.getExtension(About.class);
		FPanelAbout panelAbout=about.getAboutPanel();
		java.net.URL aboutURL = this.getClass().getResource(
	        "/about.htm");
	    panelAbout.addAboutUrl(PluginServices.getText(this,"ArcSDE"),aboutURL);
	    try{
	    	LayerFactory.getDM().addDriver(new File(this.getClass().getResource(
	    	"/lib").getFile()),
	    	ArcSdeDriver.NAME,
	    	ArcSdeDriver.class);
	    }catch (Error e) {
	    	//FIXME: Se nos ha pedido por parte de la CIT que no se informe
	    	//al usuario de que le falta esta librería.
//	    	NotificationManager.addWarning("SDE lib not found!!!",e);
	    }
	    SingleVectorialDBConnectionExtension svdbc=(SingleVectorialDBConnectionExtension)PluginServices.getExtension(SingleVectorialDBConnectionExtension.class);
	    svdbc.initialize();
	}

}

// [eiel-gestion-conexiones]
