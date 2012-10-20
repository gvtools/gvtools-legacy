/*
 * Created on 23-feb-2006
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
 * $Id: DissolvePanelIF.java 6749 2006-08-11 16:30:38Z azabala $
 * $Log$
 * Revision 1.3  2006-08-11 16:28:25  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/07/21 09:13:55  azabala
 * fixed bug 608: user doesnt enter any result file to the geoprocess panel
 *
 * Revision 1.1  2006/06/20 18:20:45  azabala
 * first version in cvs
 *
 * Revision 1.1  2006/05/24 21:11:14  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.2  2006/03/05 19:53:25  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/26 20:51:21  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.dissolve.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.fmap.SummarizationFunction;
import com.iver.utiles.swing.threads.IMonitorableTask;

/**
 * Models funtionality of GeoProcessingDissolvePanel GUI component.
 * 
 * @author azabala
 */

public interface DissolvePanelIF {

	public File getOutputFile() throws FileNotFoundException;

	public void openResultFile();

	public FLyrVect getInputLayer();

	public FLayers getFLayers();

	/**
	 * Allows user to choose one or many sumarize functions to a numeric field
	 * 
	 */
	public void openSumarizeFunction();

	/**
	 * tells if we are goint to dissolve only selected features, or wal features
	 * 
	 * @return
	 */
	public boolean isDissolveOnlySelected();

	public boolean onlyAdjacentSelected();

	/**
	 * Notify to gui that input layer combo content has change, to change
	 * content of fields combobox
	 * 
	 */
	public void inputLayerSelectedChange();

	/**
	 * returns dissolve attribute name
	 * 
	 * @return
	 */
	public String getDissolveFieldName();

	/**
	 * Sets numerical fields, to allow users to select group by sumarization
	 * function.
	 * 
	 * @return TODO
	 */

	public String[] getInputLayerNumericFields();

	/**
	 * Returns input layer numeric fields for which user has selected one or
	 * more than one sumarization functions
	 * 
	 * @return
	 */
	public String[] getFieldsToSummarize();

	/**
	 * Returns sumarization function for a numeric field name
	 * 
	 * @param numericFieldName
	 * @return
	 */
	public SummarizationFunction[] getSumarizationFunctinFor(
			String numericFieldName);

	public Map getFieldFunctionMap();

	public void error(String error, String errorDescription);

	public boolean askForOverwriteOutputFile(File file);

	public IMonitorableTask askForSpatialIndexCreation(FLyrVect layer);

}
