/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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

package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.Annotation_Layer;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.MouseMovementBehavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.PointBehavior;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.project.documents.view.tool.Annotation_ModifyListener;
import com.iver.cit.gvsig.project.documents.view.toolListeners.StatusBarListener;


/**
 * Class that extends of the tool InfoToolExtension and
 * override the methods that interests us to change its behavior.
 *
 * @author Vicente Caballero Navarro
 */
public class Annotation_ToolExtension
    extends com.iver.cit.gvsig.InfoToolExtension {

	/* (non-Javadoc)
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String s) {
        IWindow window = PluginServices.getMDIManager().getActiveWindow();

        if (window instanceof View) {
            MapControl mapCtrl = ((View) window).getMapControl();

            if (s.compareTo("MODIFYANNOTATION") == 0) {
                if (mapCtrl.getMapTool("centerviewtopoint") == null) {
                    StatusBarListener sbl = new StatusBarListener(mapCtrl);
                    Annotation_ModifyListener chl = new Annotation_ModifyListener(mapCtrl);
                    mapCtrl.addMapTool("modifyannotation",
                        new Behavior[] {
                            new PointBehavior(chl),
                            new MouseMovementBehavior(sbl)
                        });
                }
            }

   			if (s.compareTo("MODIFYANNOTATION") == 0) {
   				mapCtrl.setTool("modifyannotation");
   			}
        }
    }

    /* (non-Javadoc)
     * @see com.iver.andami.plugins.IExtension#initialize()
     */
    public void initialize() {
        super.initialize();
        
        registerIcons();
    }

    private void registerIcons(){
    	
    	PluginServices.getIconTheme().registerDefault(
				"ext-annotation",
				this.getClass().getClassLoader().getResource("images/Annotation.png")
			);
    }
    
    /* (non-Javadoc)
     * @see com.iver.andami.plugins.IExtension#isEnabled()
     */
    public boolean isEnabled() {
        return super.isEnabled();
    }

    /* (non-Javadoc)
     * @see com.iver.andami.plugins.IExtension#isVisible()
     */
    public boolean isVisible() {
        IWindow window = PluginServices.getMDIManager().getActiveWindow();

        if (window instanceof View) {
            MapContext mapContext = ((View) window).getMapControl()
                                     .getMapContext();
            FLayer[] layers=mapContext.getLayers().getActives();
            for (int i=0;i<layers.length;i++){
            	if (layers[i] instanceof Annotation_Layer){
            		if (layers[i].isEditing())
            			return true;
            	}
            }
        }

        return false;
	}
}
