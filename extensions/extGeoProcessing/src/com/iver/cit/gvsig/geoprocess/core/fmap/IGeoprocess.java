/*
 * Created on 01-feb-2006
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
* $Id: IGeoprocess.java 5795 2006-06-12 19:15:38Z azabala $
* $Log$
* Revision 1.2  2006-06-12 19:15:38  azabala
* cambios para poder trabajar en geoprocessing con capas MULTI (dxf, jdbc, etc)
*
* Revision 1.1  2006/05/24 21:12:16  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.4  2006/03/15 18:34:50  azabala
* *** empty log message ***
*
* Revision 1.3  2006/03/14 18:32:46  fjp
* Cambio con LayerDefinition para que sea compatible con la definición de tablas también.
*
* Revision 1.2  2006/02/13 17:55:25  azabala
* *** empty log message ***
*
* Revision 1.1  2006/02/12 21:03:25  azabala
* *** empty log message ***
*
* Revision 1.3  2006/02/06 19:05:26  azabala
* Changes in creation of layer result
*
* Revision 1.2  2006/02/02 19:47:57  azabala
* Added method setResult
*
* Revision 1.1  2006/02/01 19:40:59  azabala
* First version in CVS
*
*
*/
package com.iver.cit.gvsig.geoprocess.core.fmap;

import java.util.Map;

import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.edition.ISchemaManager;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.utiles.swing.threads.IBackgroundExecution;

/**
 * 
 * Spatial process which operates with one or more
 * vectorial layers to produce a new vectorial layer.
 * So geoprocesses could be costly task, extends IBackgroundExecution
 * interface that offers a method to run in background.
 * 
 * @author azabala
 * 
 * TODO Migrate to libGeoprocess all geoprocessing logic,
 * and GUIS in extGeoprocessing
 * 
 * 
 *
 */
public interface IGeoprocess extends IBackgroundExecution{
	
	/**
	 * It sets geoprocess parameters
	 * @param params
	 * @throws geoprocess exception for invalid params
	 */
	public void setParameters(Map params) throws GeoprocessException;
	
	/**
	 * Many geoprocess has to verify some preconditions
	 * to run. If these checks doesnt pass, the geoprocess
	 * wont run.
	 * @return if entry params verify preconditions to run geoprocess
	 */
	public void checkPreconditions() throws GeoprocessException;
	
	/**
	 * process spatial layers to generate a new layer
	 */
	public void process() throws GeoprocessException;
	
	/**
	 * Allows to cancel large and computatinal costly proccesses.
	 */
	public void cancel();
	
	/**
	 * Creates a new layer from geoprocess results
	 * @return a new vectorial layer
	 */
	public FLayer getResult() throws GeoprocessException;
	
	/**
	 * Allows "clients" of this geoprocess to set persistent datastore
	 * properties to save results
	 * @param adapter editable adapter to manage geometries' operations
	 * @param writer has the responsability to save final results
	 * @param schemaManager knows how to create datastore schema
	 */
	public void setResultLayerProperties( IWriter writer,
									ISchemaManager schemaManager);
	
	/**
	 * Geoprocess' result layer will be a function of geoprocess
	 * inputs layer, user selection (params) and geoprocess itself
	 * (data transforms that geoprocess will do). This method
	 * offers a LayerDefinition that is function of all of this
	 * causes.
	 * 
	 * @return LayerDefinition that describes result layer.
	 */

	public ILayerDefinition createLayerDefinition();
}

