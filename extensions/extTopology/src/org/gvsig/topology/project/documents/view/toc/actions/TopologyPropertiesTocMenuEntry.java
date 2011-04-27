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
package org.gvsig.topology.project.documents.view.toc.actions;

import javax.swing.JDialog;

import org.gvsig.topology.Topology;
import org.gvsig.topology.ui.TopologyPropertiesPanel;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.project.documents.view.toc.AbstractTocContextMenuAction;
import com.iver.cit.gvsig.project.documents.view.toc.ITocItem;
import com.iver.cit.gvsig.project.documents.view.toc.TocItemBranch;

/**
 * TOC menu entry to show a panel with the properties of a Topology.
 * 
 * @author Alvaro Zabala
 *
 */
public class TopologyPropertiesTocMenuEntry extends
		AbstractTocContextMenuAction {

	
	public int getGroupOrder() {
		return 30;
	}

	public int getOrder() {
		return 0;
	}
	
	
	public void execute(ITocItem item, FLayer[] selectedItems) {
	    FLayer selectedLyr =((TocItemBranch)item).getLayer();
	    Topology topology = (Topology) selectedLyr;
	    TopologyPropertiesPanel propsPanel = new TopologyPropertiesPanel(topology);
	    if (PluginServices.getMainFrame() == null) {
			JDialog dlg = new JDialog();
			dlg.getContentPane().add(propsPanel);
			dlg.setModal(false);
			dlg.pack();
			dlg.setVisible(true);
		} else {
			PluginServices.getMDIManager().addWindow(propsPanel);
		}

	}

	public String getText() {
		return PluginServices.getText(this, "Topology_properties_toc");
	}
	
	/**
	 * This entry only will be visible if one only topology is 
	 * selected in the TOC.
	 */
	public boolean isEnabled(ITocItem item, FLayer[] selectedItems) {
		if (!isTocItemBranch(item)) 
			return false;
		FLayer selectedLyr =((TocItemBranch)item).getLayer();
		if(selectedLyr instanceof Topology)
			return true;
		else
			return false;
	}

}
