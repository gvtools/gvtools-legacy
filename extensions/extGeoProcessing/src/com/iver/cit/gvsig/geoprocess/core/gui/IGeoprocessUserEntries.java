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
package com.iver.cit.gvsig.geoprocess.core.gui;

import java.io.File;
import java.io.FileNotFoundException;

import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

/**
 * All classes that provides user entries for geoprocesses must implement this
 * interface.
 * 
 * @author Alvaro Zabala
 *
 */
public interface IGeoprocessUserEntries {
	/**
	 * Provides de input layer for the geoprocess
	 * @return
	 */
	public FLyrVect getInputLayer();
	/**
	 * Provides the FLayers where we're going to add the results.
	 * @param layers
	 */
	public void setFLayers(FLayers layers);
	public FLayers getFLayers();
	
	/**
	 * Notify an error to the user
	 * @param message
	 * @param title
	 */
	public void error(String message, String title);
	/**
	 * Ask the user for overwrite the output file
	 * TODO By now geoprocesses only store their results in SHP. Extend this.
	 * @param outputFile
	 * @return
	 */
	public boolean askForOverwriteOutputFile(File outputFile);
	
	/**
	 * Provides the output shp file to the geoprocess
	 * @return
	 * @throws FileNotFoundException
	 */
	public File getOutputFile() throws FileNotFoundException;
}
