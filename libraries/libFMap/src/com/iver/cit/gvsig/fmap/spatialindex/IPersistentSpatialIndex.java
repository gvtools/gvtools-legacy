/*
 * Created on 28-abr-2006
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
 * $Id: IPersistentSpatialIndex.java 5416 2006-05-24 21:57:42Z azabala $
 * $Log$
 * Revision 1.2  2006-05-24 21:57:42  azabala
 * añadidos comentarios
 *
 * Revision 1.1  2006/05/01 18:38:41  azabala
 * primera version en cvs del api de indices espaciales
 *
 *
 */
package com.iver.cit.gvsig.fmap.spatialindex;

/**
 * An spatial index based in persistent data stores.
 * 
 * @author azabala
 * 
 */
public interface IPersistentSpatialIndex extends ISpatialIndex {
	/**
	 * It makes persistent all changes applied to Spatial Index
	 * 
	 */
	public void flush();

	/**
	 * Checks if the persistent store of this spatial index exists
	 * 
	 * @return
	 */
	public boolean exists();

	public void load() throws SpatialIndexException;

	/**
	 * Frees resources of persistent data store (closes files, etc)
	 * 
	 */
	public void close();
}
