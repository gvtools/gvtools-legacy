/* gvSIG. Geographic Information System of the Valencian Government
*
* Copyright (C) 2007-2008 Infrastructures and Transports Department
* of the Valencian Government (CIT)
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
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
* MA  02110-1301, USA.
* 
*/

/*
* AUTHORS (In addition to CIT):
* 2009 Software Colaborativo (www.scolab.es)   development
*/
 
package org.gvsig.graph;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import com.iver.utiles.save.BeforeSavingListener;
import com.iver.utiles.save.SaveEvent;

public class MyBeforeSavingListener implements BeforeSavingListener {

	private FLyrVect lyr;
	private MapContext mapContext;

	public MyBeforeSavingListener(FLyrVect lyr, MapContext mapContext) {
		this.lyr = lyr;
		this.mapContext = mapContext;
	}

	/* (non-Javadoc)
	 * @see com.iver.utiles.save.BeforeSavingListener#beforeSaving(com.iver.utiles.save.SaveEvent)
	 */
	public void beforeSaving(SaveEvent e) {
		// We remove the layer before saving (maybe we should say something to the user)
		FLayer aux = mapContext.getLayers().getLayer(lyr.getName()); 
		if (aux != null) {
			JOptionPane.showMessageDialog((Component) PluginServices.getMDIManager().getActiveWindow(),
					PluginServices.getText(null, "tmp_nodes_will_be_deleted"));
			mapContext.beginAtomicEvent();
			mapContext.getLayers().removeLayer(aux);
            if (aux instanceof AlphanumericData){
                Project project = ((ProjectExtension)PluginServices.getExtension(ProjectExtension.class)).getProject();
                ProjectTable pt = project.getTable((AlphanumericData) aux);

                ArrayList tables = project.getDocumentsByType(ProjectTableFactory.registerName);
                for (int j = 0; j < tables.size(); j++) {
                    if (tables.get(j) == pt){
                    	project.delDocument((ProjectDocument)tables.get(j));
                        break;
                    }
                }

                PluginServices.getMDIManager().closeSingletonWindow(pt);
            }
        	mapContext.endAtomicEvent();
        	mapContext.invalidate();
        	Project project=((ProjectExtension)PluginServices.getExtension(ProjectExtension.class)).getProject();
    		project.setModified(true);
        	PluginServices.getMainFrame().enableControls();

		}
			
	}

}

