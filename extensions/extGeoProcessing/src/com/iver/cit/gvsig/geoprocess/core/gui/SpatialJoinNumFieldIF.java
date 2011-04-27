/*
 * Created on 01-mar-2006
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
* $Id: SpatialJoinNumFieldIF.java 10626 2007-03-06 16:55:54Z caballero $
* $Log$
* Revision 1.3  2007-03-06 16:47:58  caballero
* Exceptions
*
* Revision 1.2  2006/08/11 16:12:27  azabala
* *** empty log message ***
*
* Revision 1.1  2006/05/24 21:13:09  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.2  2006/03/23 21:02:37  azabala
* *** empty log message ***
*
* Revision 1.1  2006/03/05 19:56:06  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.geoprocess.core.gui;

/**
 * Base interface to model logic of
 * dialogs to select sumarize functions for numeric
 * fields of related layer of a spatial join relationship
 * @author azabala
 *
 */
public interface SpatialJoinNumFieldIF {
	/**
	 * will be called when user would pust ok button, 
	 * or when would close main dialog
	 */
	public void ok();
	public boolean isOk();
}

