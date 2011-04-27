/*
 * Created on 09-feb-2006
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
* $Id: BufferPanelIF.java 6747 2006-08-11 16:18:48Z azabala $
* $Log$
* Revision 1.3  2006-08-11 16:14:02  azabala
* refactoring to make logic independent from UI
*
* Revision 1.2  2006/07/21 09:10:34  azabala
* fixed bug 608: user doesnt enter any result file to the geoprocess panel
*
* Revision 1.1  2006/06/20 18:20:45  azabala
* first version in cvs
*
* Revision 1.1  2006/05/24 21:14:55  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.3  2006/04/11 18:01:45  azabala
* añadido metodo getFLayers
*
* Revision 1.2  2006/02/17 15:56:48  azabala
* *** empty log message ***
*
* Revision 1.1  2006/02/12 21:01:40  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.buffer.gui;

import java.io.File;
import java.io.FileNotFoundException;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;

/**
 * Models  funtionality of GeoProcessingBufferPanel
 * GUI component.
 * 
 * @author azabala
 *
 */
public interface BufferPanelIF {
	
	/*
	 * Constants to indicate how to create buffers on polygonal geometries
	 */
	public final String BUFFER_INSIDE = PluginServices.getText(null, "Dentro");

	public final String BUFFER_INSIDE_OUTSIDE = PluginServices.getText(null,
			"Dentro_y_fuera");
	public final String BUFFER_OUTSIDE = PluginServices.getText(null, "Fuera");
	
	public void openResultFile();
	public void constantDistanceSelected();
	public void attributeDistanceSelected();
	
	public File getOutputFile() throws FileNotFoundException;
	public FLyrVect getInputLayer();
	public FLayers getFLayers();
	public boolean isConstantDistanceSelected();
	public boolean isAttributeDistanceSelected();
	public double getConstantDistance() throws GeoprocessException;
	public String getAttributeDistanceField()throws GeoprocessException;
	public boolean isBufferOnlySelected();
	public boolean isDissolveBuffersSelected();
	
	public void error(String errorDescription, String error);
	public boolean askForOverwriteOutputFile(File file);
	public int getNumberOfRadialBuffers();
	public String getTypePolygonBuffer();
	public boolean isSquareCap();
	
}

