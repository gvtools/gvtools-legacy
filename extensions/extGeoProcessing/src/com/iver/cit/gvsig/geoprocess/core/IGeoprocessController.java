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
 * $Id: IGeoprocessController.java 21230 2008-06-05 14:01:14Z azabala $
 * $Log$
 * Revision 1.2  2006-10-23 10:27:13  caballero
 * ancho y alto del panel
 *
 * Revision 1.1  2006/05/24 21:12:48  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.1  2006/04/11 17:55:51  azabala
 * primera version en cvs
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core;

import com.iver.cit.gvsig.geoprocess.core.fmap.IGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;

/**
 * This interface receives user inputs from a GeoprocessPanel (GUI component
 * associated to a geoprocess) and prepare the execution of the geoprocess. <br>
 * In a model-view-controller architecture, IGeoprocessController would be the
 * Controller, IGeoprocessPanel the view, and IGeoprocess the model.
 * 
 * @author azabala
 * 
 *         TODO Maybe we could add an intermediate interface, IUserEntry, to
 *         allow to work with GUI environments like this (IGeoprocessPanel) and
 *         with shell environments.
 * 
 */
public interface IGeoprocessController {
	/**
	 * Sets "view" part, a panel where user has entered input data
	 * 
	 * @param viewPanel
	 */
	public void setView(IGeoprocessUserEntries userEntries);

	/**
	 * Returns model part, the geoprocess that we are going to launch, if user
	 * entries are correct. It has the responsability to create it
	 * 
	 * @param geoprocess
	 */
	public IGeoprocess getGeoprocess();

	/**
	 * Launch geoprocess. Return true if execution was possible, and false if
	 * any precondition wasnt verified, or if there were any problem in
	 * execution
	 */
	public boolean launchGeoprocess();

	/**
	 * Return the width of geoprocess panel.
	 */
	public int getWidth();

	/**
	 * Return the height of geoprocess panel.
	 */
	public int getHeight();
}
