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
package org.gvsig.topology;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.layers.StartEditionLayerException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * Utility class to make edition operations for topology error correction.
 * 
 * @author Alvaro Zabala
 *
 */

public class EditionUtils {
	
	public static void startEdition(FLyrVect[] flyrVects) throws StartEditionLayerException{
		/*
		 * FIXME FLyrVect.setEditing(true) changes the TOC's active layer, to
		 * ensure a layer which is editing is the active in TOC. Until we could
		 * change this behavior, we must save the previous selected layer
		 */
		View view = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mapCtrl = view.getMapControl();
		FLayer[] activeLyrs = mapCtrl.getMapContext().getLayers().getActives();
		// Ponemos el resto de temas desactivados
		if (mapCtrl != null) {
			mapCtrl.getMapContext().getLayers().setActive(false);
		}
		
		for(int i = 0; i < flyrVects.length; i++)
			flyrVects[i].setEditing(true);
		
		if (mapCtrl != null)
			mapCtrl.getMapContext().getLayers().setActive(false);
		for (int i = 0; i < activeLyrs.length; i++) {
			activeLyrs[i].setActive(true);
		}
	}
	
	public static void startEdition(FLyrVect lyr) throws StartEditionLayerException {
		EditionUtils.startEdition(new FLyrVect[]{lyr});
	}

}
