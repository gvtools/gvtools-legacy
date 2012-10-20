package com.iver.cit.gvsig.gui.panels;

import java.awt.Dimension;

import org.gvsig.gui.beans.panelGroup.panels.AbstractPanel;

import com.iver.cit.gvsig.fmap.layers.FLyrWFS;
import com.iver.cit.gvsig.fmap.layers.WFSLayerNode;
import com.iver.cit.gvsig.gui.wizards.WFSWizardData;

/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * $Id$
 * $Log$
 *
 */

/**
 * <p>
 * Default panel used to create a WFS group's panel.
 * </p>
 * 
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public abstract class AbstractWFSPanel extends AbstractPanel implements
		IWFSPanel {
	// Default dimensions of all WFS panels
	static final int PANEL_WIDTH = 475;
	static final int PANEL_HEIGHT = 365;

	/**
	 * Initializes an WFS panel.
	 */
	public AbstractWFSPanel() {
		super();
		setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gui.beans.panelGroup.panels.AbstractPanel#initialize()
	 */
	protected void initialize() {
		// By default all panels will be at the GUI
		setVisible(true);
	}

	/**
	 * <p>
	 * Gets the information of the wizard used to add or load panels.
	 * </p>
	 * 
	 * @return the wizard data
	 */
	public WFSWizardData getWizardData() {
		return (getPanelGroup() == null) ? null
				: ((WFSParamsPanel) getPanelGroup()).getWizardData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gui.beans.panelGroup.panels.AbstractPanel#setReference(java
	 * .lang.Object)
	 */
	public void setReference(Object ref) {
		super.setReference(ref);

		if (ref instanceof FLyrWFS) {
			refresh(((FLyrWFS) ref).getWfsLayerNode());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.gui.panels.IWFSPanel#refresh(com.iver.cit.gvsig.fmap
	 * .layers.WFSLayerNode)
	 */
	public void refresh(WFSLayerNode layer) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gui.beans.panelGroup.panels.IPanel#accept()
	 */
	public void accept() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gui.beans.panelGroup.panels.IPanel#apply()
	 */
	public void apply() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gui.beans.panelGroup.panels.IPanel#cancel()
	 */
	public void cancel() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gui.beans.panelGroup.panels.IPanel#selected()
	 */
	public void selected() {
	}
}
