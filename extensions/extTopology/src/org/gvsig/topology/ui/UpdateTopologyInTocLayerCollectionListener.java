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
package org.gvsig.topology.ui;

import org.gvsig.topology.Topology;

import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionListener;
import com.iver.cit.gvsig.fmap.layers.LayerPositionEvent;
/**
 * LayerCollectionListener which updates the toc.
 * 
 * @author Alvaro Zabala
 *
 */
public class UpdateTopologyInTocLayerCollectionListener implements LayerCollectionListener {
	
	/**
	 * MapContext associated to the TOC
	 */
	MapContext mapContext;
	
	/**
	 * topology to update
	 */
	Topology topology;
	
	
	public UpdateTopologyInTocLayerCollectionListener(MapContext mapContext,
			Topology topology) {
		this.mapContext = mapContext;
		this.topology = topology;
	}

	public void layerAdded(
			LayerCollectionEvent e) {
		mapContext.endAtomicEvent();
	}
	
	//workaround to avoid bug "automatic error correction crashes
	//when layers has been edited"
//	private void checkNoEditioManagerRegistered(){
		//Esto valdria si FLayers tuviese un metodo getLayerCollectionListeners
//		LayerListener[] listeners = topology.getParentLayer().getLayerListeners()();
//		for(int i = 0; i < listeners.length; i++){
//			if(listeners[i] instanceof EditionManager)
//				topology.getParentLayer().removeLayerListener(listeners[i]);
//		}
//	}

	public void layerAdding(
			LayerCollectionEvent e)
			throws CancelationException {
		
		try {
			mapContext.beginAtomicEvent();
			FLayers rootLyrs = mapContext.getLayers();
//			checkNoEditioManagerRegistered();	
			rootLyrs.replaceLayer(topology.getName(),topology);
		} catch (LoadLayerException e1) {
			e1.printStackTrace();
		}
	}

	public void layerMoved(LayerPositionEvent e) {
		mapContext.endAtomicEvent();
	}

	public void layerMoving(LayerPositionEvent e)
			throws CancelationException {
		try {
			mapContext.beginAtomicEvent();
//			checkNoEditioManagerRegistered();
			mapContext.getLayers().replaceLayer(topology.getName(),topology);
		} catch (LoadLayerException e1) {
			e1.printStackTrace();
		}
	}

	public void layerRemoved(
			LayerCollectionEvent e) {
		mapContext.endAtomicEvent();
	}

	public void layerRemoving(
			LayerCollectionEvent e)
			throws CancelationException {
		try {
			mapContext.beginAtomicEvent();
//			checkNoEditioManagerRegistered();
			mapContext.getLayers().replaceLayer(topology.getName(),topology);
		} catch (LoadLayerException e1) {
			e1.printStackTrace();
		}
	}

	public void visibilityChanged(
			LayerCollectionEvent e)
			throws CancelationException {
	}

	public void setTopology(Topology topology) {
		this.topology = topology;
	}
}
