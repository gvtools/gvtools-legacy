/*
 * Created on 02-mar-2006
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
 * $Id: MergePanelIF.java 6749 2006-08-11 16:30:38Z azabala $
 * $Log$
 * Revision 1.3  2006-08-11 16:30:38  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/07/21 09:10:34  azabala
 * fixed bug 608: user doesnt enter any result file to the geoprocess panel
 *
 * Revision 1.1  2006/06/20 18:20:45  azabala
 * first version in cvs
 *
 * Revision 1.1  2006/05/24 21:10:15  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.2  2006/03/17 19:52:07  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/03/05 19:56:06  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.merge.gui;

import java.io.File;
import java.io.FileNotFoundException;

import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

/**
 * Base interfaz to model MergePanel component
 * 
 * @author azabala
 * 
 */
public interface MergePanelIF {
	/**
	 * Returns all selected layer, in TOC list and directory list
	 * 
	 * @return
	 */
	public FLyrVect[] getSelectedLayers();

	/**
	 * Looks for vectorial files in a directory, and creates FLyrVect
	 * 
	 * @param directory
	 * @param exts
	 * @return
	 */
	public FLyrVect[] loadLayersInDirectory(File directory, String[] exts);

	/**
	 * @param layers
	 */
	public void addLayersToMergeList(FLyrVect[] layers);

	/**
	 * Devuelve la capa cuyo esquema tendra el resultado del merge
	 * 
	 * @return
	 */
	public FLyrVect getSelectedSchema();

	public FLayers getFLayers();

	/**
	 * Event of layer selection in one of two list: TOC and directory list
	 * 
	 */
	public void layersSelected();

	public void openResultFileDialog();

	public void openDirectoryLayersDialog();

	public File getOutputFile() throws FileNotFoundException;

	public void error(String error, String errorDescription);

	public boolean askForOverwriteOutputFile(File file);
}
