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
package com.iver.cit.gvsig.jdbc_spatial;

import java.security.KeyException;

import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.layers.FLayerVectorialDB;
import com.iver.cit.gvsig.project.documents.view.legend.gui.General;
import com.iver.cit.gvsig.project.documents.view.legend.gui.LabelingManager;
import com.iver.cit.gvsig.project.documents.view.legend.gui.LegendManager;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ThemeManagerWindow;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

public class ExtJDBC_Spatial extends Extension {

    public void initialize() {
        System.out.println("Añado WizardJDBC.");
//        AddLayer.addWizard(WizardJDBC.class);
        ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
    	extensionPoints.add("CatalogLayers","POSTGIS",new JDBCLayerBuilder());
//    	extensionPoints.add("Layers",FLayerVectorialDB.class.getName(), FLayerVectorialDB.class);
		try {
			((ExtensionPoint)extensionPoints.get("Layers")).addAlias(FLayerVectorialDB.class.getName(), "VectorialDB");
		} catch (KeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ThemeManagerWindow.setTabEnabledForLayer(General.class, FLayerVectorialDB.class, true);
		ThemeManagerWindow.setTabEnabledForLayer(LegendManager.class, FLayerVectorialDB.class, true);
		ThemeManagerWindow.setTabEnabledForLayer(LabelingManager.class, FLayerVectorialDB.class, true);
    }

    public void execute(String actionCommand) {
        // TODO Auto-generated method stub

    }

    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isVisible() {
        // TODO Auto-generated method stub
        return false;
    }

}
