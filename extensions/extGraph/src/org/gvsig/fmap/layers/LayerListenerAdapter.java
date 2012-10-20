/*
 * Created on 06-nov-2006
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
 * $Id: LayerListenerAdapter.java 24738 2008-11-04 12:32:27Z fpenarrubia $
 * $Log$
 * Revision 1.1  2006-11-07 19:49:55  azabala
 * *** empty log message ***
 *
 *
 */
package org.gvsig.fmap.layers;

import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionListener;
import com.iver.cit.gvsig.fmap.layers.LayerEvent;
import com.iver.cit.gvsig.fmap.layers.LayerListener;
import com.iver.cit.gvsig.fmap.layers.LayerPositionEvent;

/**
 * Empty implementation of LayerCollectionListener
 * 
 * (allows to extending classes to implements only interesting methods, for
 * example, only listen for layer selection events)
 * 
 * */
public class LayerListenerAdapter implements LayerCollectionListener,
		LayerListener {

	public void layerAdded(LayerCollectionEvent e) {
	}

	public void layerMoved(LayerPositionEvent e) {
	}

	public void layerRemoved(LayerCollectionEvent e) {
	}

	public void layerAdding(LayerCollectionEvent e) throws CancelationException {
	}

	public void layerMoving(LayerPositionEvent e) throws CancelationException {
	}

	public void layerRemoving(LayerCollectionEvent e)
			throws CancelationException {
	}

	public void activationChanged(LayerCollectionEvent e)
			throws CancelationException {
	}

	public void visibilityChanged(LayerCollectionEvent e)
			throws CancelationException {
	}

	public void visibilityChanged(LayerEvent e) {
	}

	public void activationChanged(LayerEvent e) {
	}

	public void nameChanged(LayerEvent e) {
	}

	public void editionChanged(LayerEvent e) {
	}

	public void drawValueChanged(LayerEvent e) {
		// TODO Auto-generated method stub

	}

}
