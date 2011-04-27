/*
 * Created on 21-jun-2006
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
* $Id: IGeoprocessTree.java 6057 2006-06-27 16:14:29Z azabala $
* $Log$
* Revision 1.3  2006-06-27 16:14:06  azabala
* added geoprocess panel opening with user mouse interaction
*
* Revision 1.2  2006/06/23 19:04:29  azabala
* *** empty log message ***
*
* Revision 1.1  2006/06/22 17:46:30  azabala
* first version in cvs
*
*
*/
package com.iver.cit.gvsig.geoprocess.manager;

import java.awt.event.MouseListener;

import javax.swing.event.TreeSelectionListener;

import com.iver.cit.gvsig.geoprocess.core.IGeoprocessPlugin;
/**
 * It models the logic of GeoprocessTree component.
 * @author azabala
 *
 */
public interface IGeoprocessTree {
	/**
	 * This string separates packages (nodes) and
	 * geoprocesses (leafs) in the textual representation
	 * of a geoprocess
	 */
	public static final String PATH_SEPARATOR = "/";
	/**
	 * Returns the selected geoprocess in the tree
	 * (or null if the selected node is a directory)
	 * @return
	 */
	public IGeoprocessPlugin getGeoprocess();
	/**
	 * Adds a geoprocess to the specified path
	 * @param path
	 * @param metadata
	 * @return
	 */
	public void register(IGeoprocessPlugin metadata);
	
	/**
	 * Adds a tree selection listener to the geoprocess tree
	 * @param l
	 */
	public void addTreeSelectionListener(TreeSelectionListener l);
	
	/**
	 * Adds a mouse listener to the associated tree
	 * @param l
	 */
	public void addMouseListener(MouseListener l);
	
	
	
	
}

