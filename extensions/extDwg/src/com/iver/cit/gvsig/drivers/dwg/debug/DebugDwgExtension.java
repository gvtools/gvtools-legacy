/*
 * Created on 11-ene-2007
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
* $Id: DebugDwgExtension.java 23943 2008-10-15 07:12:20Z vcaballero $
* $Log$
* Revision 1.1.2.1  2007-02-28 07:35:06  jmvivo
* Actualizado desde el HEAD.
*
* Revision 1.1  2007/01/11 20:31:05  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.drivers.dwg.debug;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.tools.Behavior.PointBehavior;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class DebugDwgExtension extends Extension {		
    DwgEntityListener entityListener = null;
	public void initialize() {
	}

	public void execute(String actionCommand) {
		View v = (View) PluginServices.getMDIManager().getActiveWindow();
        MapControl mapCtrl = v.getMapControl();

        if (!mapCtrl.hasTool("dwgDebug")) // We create it for the first time.
        {
        	entityListener = new DwgEntityListener(mapCtrl);
            mapCtrl.addMapTool("dwgDebug", new PointBehavior(entityListener));
        }
        mapCtrl.setTool("dwgDebug");

	}

	public boolean isEnabled() {
		return true;

	}

	public boolean isVisible() {
		return true;

	}

}


