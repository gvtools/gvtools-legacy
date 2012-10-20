/*
 * Created on 13-oct-2005
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
package com.iver.cit.gvsig.fmap.core;

import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.IConnection;

/**
 * @author fjp If a driver can reproject, it must implement this interface.
 */
public interface ICanReproject {
	/**
	 * @return a EPSG string defining the original projection. It means that the
	 *         original data are in this proj.
	 */
	String getSourceProjection(IConnection conn, DBLayerDefinition lyrDef);

	/**
	 * @return a EPSG string. You set this variable with
	 *         setDestProjection(String epsg)
	 */
	String getDestProjection();

	/**
	 * Set this variable to tell the driver in which projection do you want your
	 * data. If the driver can reproject to this new EPSG, it will return true
	 * in canReproject. NOTE: use String strEPSG = mapCtrl.getViewPort()
	 * .getProjection().getAbrev() .substring(5); Otherwise, it will return
	 * false.
	 * 
	 * @param toEPSG
	 */
	void setDestProjection(String toEPSG);

	/**
	 * @return true if the driver will be able to deliver the entities in the
	 *         destiny projection. False otherwise.
	 */
	boolean canReproject(String toEPSGdestinyProjection);

}
